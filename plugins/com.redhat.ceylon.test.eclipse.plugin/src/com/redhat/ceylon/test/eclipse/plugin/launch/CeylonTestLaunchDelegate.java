package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModulesInProject;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_PORT;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getProject;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getShell;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.launch.CeylonLaunchDelegate;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.ui.TestRunViewPart;
import com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil;

public class CeylonTestLaunchDelegate extends CeylonLaunchDelegate {
    
    // hack for propagate port parameter into method getProgramArguments
    private final ThreadLocal<String> portThreadLocal = new ThreadLocal<String>();

    @Override
    public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        String port = getFreePort();
        launch.setAttribute(LAUNCH_CONFIG_PORT, port);
        
        try {
            portThreadLocal.set(port);
            super.launch(config, mode, launch, monitor);
        } finally {
            portThreadLocal.remove();
        }
        
        TestRunViewPart.showPageAsync();
    }
    
    @Override
    public boolean preLaunchCheck(ILaunchConfiguration config, String mode, IProgressMonitor monitor) throws CoreException {
        if( !validateConfig(config) )
            return false;
        
        if( !validateCeylonTestDependency(config) )
            return false;
    
        return super.preLaunchCheck(config, mode, monitor);
    }

    @Override
    public String[] getClasspath(ILaunchConfiguration config) throws CoreException {
        List<String> classpathList = new ArrayList<String>();

        IJavaProject project = getJavaProject(config);
        
        String[] javaClasspath = getJavaClasspath(config);
        classpathList.addAll(asList(javaClasspath));
        
        String[] ceylonClasspath = getCeylonProjectClasspath(project);
        classpathList.addAll(asList(ceylonClasspath));

        // at runtime, we need the compiler/common/typechecker/cmr jars to be present for the runtime module system
        classpathList.addAll(CeylonPlugin.getRuntimeRequiredJars());

        List<String> pluginClasspath = getPluginClasspath();
        classpathList.addAll(pluginClasspath);

        return classpathList.toArray(new String[classpathList.size()]);
    }

    @Override
    public String getProgramArguments(ILaunchConfiguration config) throws CoreException {
        StringBuilder programArguments = new StringBuilder();
        programArguments.append(super.getProgramArguments(config));
        
        programArguments.append(" -port ");
        programArguments.append(portThreadLocal.get());
        
        List<CeylonTestLaunchConfigEntry> entries = CeylonTestLaunchConfigEntry.buildFromLaunchConfig(config);
        for (CeylonTestLaunchConfigEntry entry : entries) {
            IProject project = getProject(entry.getProjectName());
            switch (entry.getType()) {
            case PROJECT:
                List<Module> modules = getModulesInProject(project);
                for (Module module : modules) {
                    if (CeylonTestUtil.containsCeylonTestImport(module)) {
                        programArguments.append(" -test \"module ").append(module.getNameAsString()).append("\"");
                    }
                }
                break;
            case MODULE:
                programArguments.append(" -test \"module ").append(entry.getModPkgDeclName()).append("\"");
                break;
            case PACKAGE:
                programArguments.append(" -test \"package ").append(entry.getModPkgDeclName()).append("\"");
                break;
            case CLASS:
            case CLASS_LOCAL:
                programArguments.append(" -test \"class ").append(entry.getModPkgDeclName()).append("\"");
                break;
            case METHOD:
            case METHOD_LOCAL:
                programArguments.append(" -test \"function ").append(entry.getModPkgDeclName()).append("\"");
                break;
            }
        }

        return programArguments.toString();
    };

    private boolean validateCeylonTestDependency(ILaunchConfiguration config) throws CoreException {
        IStatusHandler prompter = DebugPlugin.getDefault().getStatusHandler(promptStatus);
        if (prompter != null) {
            if (!((Boolean) prompter.handleStatus(CeylonTestDependencyStatusHandler.CODE, config)).booleanValue()) {
                return false;
            }
        }
        return true;
    }

    private boolean validateConfig(ILaunchConfiguration config) throws CoreException {
        String errorMessage = null;

        List<CeylonTestLaunchConfigEntry> entries = CeylonTestLaunchConfigEntry.buildFromLaunchConfig(config);
        for (CeylonTestLaunchConfigEntry entry : entries) {
            entry.validate();
            if (!entry.isValid()) {
                errorMessage = entry.getErrorMessage();
                break;
            }
        }

        if (errorMessage != null) {
            final String errorMessageFinal = errorMessage;
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    MessageDialog.openInformation(getShell(),
                            CeylonTestMessages.launchDialogInfoTitle,
                            CeylonTestMessages.launchConfigIsNotValid + "\n" + errorMessageFinal);
                }
            });
            return false;
        }

        return true;
    }

    private List<String> getPluginClasspath() {
        List<String> pluginClasspath = new ArrayList<String>();

        Bundle bundle = CeylonTestPlugin.getDefault().getBundle();

        try {
            URL url = bundle.getEntry("");
            if (url != null) {
                pluginClasspath.add(FileLocator.toFileURL(url).getFile());
            }
        } catch (IOException e) {
            CeylonTestPlugin.logInfo("", e);
        }

        try {
            URL url = bundle.getEntry("bin");
            if (url != null) {
                pluginClasspath.add(FileLocator.toFileURL(url).getFile());
            }
        } catch (IOException e) {
            CeylonTestPlugin.logInfo("", e);
        }

        return pluginClasspath;
    }
    
    private String getFreePort() throws CoreException {
        int port = SocketUtil.findFreePort();
        if (port == -1) {
            throw new CoreException(new Status(IStatus.ERROR, 
                    CeylonTestPlugin.PLUGIN_ID,
                    CeylonTestMessages.errorNoSocket));
        }
        return String.valueOf(port);
    }

}