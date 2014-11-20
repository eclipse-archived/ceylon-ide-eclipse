package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectDeclaredSourceModules;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_PORT;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE_JS;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getShell;

import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.core.launch.ModuleLaunchDelegate;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.ui.TestRunViewPart;
import com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil;

public class CeylonTestLaunchDelegate extends ModuleLaunchDelegate {

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
    protected void prepareArguments(List<String> args, List<IPath> workingRepos, IProject p, 
            ILaunchConfiguration configuration, boolean runAsJs) throws CoreException {
        runAsJs = DebugPlugin.getDefault().getLaunchManager()
                .getLaunchConfigurationType(LAUNCH_CONFIG_TYPE_JS).equals(configuration.getType());
        
        LinkedHashSet<String> moduleArgs = new LinkedHashSet<String>();
        LinkedHashSet<String> testArgs = new LinkedHashSet<String>();
        LinkedHashSet<IProject> projects = new LinkedHashSet<IProject>();

        List<CeylonTestLaunchConfigEntry> entries = CeylonTestLaunchConfigEntry.buildFromLaunchConfig(configuration);
        for (CeylonTestLaunchConfigEntry entry : entries) {
            IProject project = null;
            Module module = null;
            switch (entry.getType()) {
            case PROJECT:
                project = CeylonTestUtil.getProject(entry.getProjectName());
                projects.add(project);
                for (Module m : getProjectDeclaredSourceModules(project)) {
                    if (CeylonTestUtil.containsCeylonTestImport(m)) {
                        moduleArgs.add(m.getNameAsString() + "/" + m.getVersion());
                        testArgs.add("module " + m.getNameAsString());
                    }
                }
                break;
            case MODULE:
                project = CeylonTestUtil.getProject(entry.getProjectName());
                projects.add(project);
                module = CeylonTestUtil.getModule(project, entry.getModPkgDeclName());
                moduleArgs.add(module.getNameAsString() + "/" + module.getVersion());
                testArgs.add("module " + module.getNameAsString());
                break;
            case PACKAGE:
                project = CeylonTestUtil.getProject(entry.getProjectName());
                projects.add(project);
                module = CeylonTestUtil.getModule(project, entry.getModuleName());
                moduleArgs.add(module.getNameAsString() + "/" + module.getVersion());
                testArgs.add("package " + entry.getModPkgDeclName());
                break;
            case CLASS:
            case CLASS_LOCAL:
                project = CeylonTestUtil.getProject(entry.getProjectName());
                projects.add(project);
                module = CeylonTestUtil.getModule(project, entry.getModuleName());
                moduleArgs.add(module.getNameAsString() + "/" + module.getVersion());
                testArgs.add("class " + entry.getModPkgDeclName());
                break;
            case METHOD:
            case METHOD_LOCAL:
                project = CeylonTestUtil.getProject(entry.getProjectName());
                projects.add(project);
                module = CeylonTestUtil.getModule(project, entry.getModuleName());
                moduleArgs.add(module.getNameAsString() + "/" + module.getVersion());
                testArgs.add("function " + entry.getModPkgDeclName());
                break;
            }
        }

        
        if(runAsJs) {
            args.add("test-js");
        }
        else {
            args.add("test");
        }
        args.add("--port="+portThreadLocal.get());

        
        for (IPath workingRepo : workingRepos) {
            args.add("--rep");
            args.add(workingRepo.toOSString());
        }
        for (IProject project : projects) {
            IPath outputFolder = getCeylonModulesOutputFolder(project).getLocation();
            if (!workingRepos.contains(outputFolder)) {
                args.add("--rep");
                args.add(outputFolder.toOSString());
            }
            for (String repo : CeylonProjectConfig.get(project).getProjectLocalRepos()) {
                args.add("--rep");
                args.add(repo);
            }
        }
        

        for (String testArg : testArgs) {
            args.add("--test");
            args.add(testArg);

        }
        args.addAll(moduleArgs);
    }

    @Override
    public boolean preLaunchCheck(ILaunchConfiguration config, String mode, IProgressMonitor monitor) throws CoreException {
        if( !validateConfig(config) )
            return false;

        if( !validateCeylonTestDependency(config) )
            return false;

        return super.preLaunchCheck(config, mode, monitor);
    }

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