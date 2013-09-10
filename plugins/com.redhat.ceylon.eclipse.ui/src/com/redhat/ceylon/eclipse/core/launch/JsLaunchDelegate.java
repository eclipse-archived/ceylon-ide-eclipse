package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputDirectory;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonRepositories;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getInterpolatedCeylonSystemRepo;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getReferencedProjectsOutputRepositories;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;

import java.io.PrintStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import com.redhat.ceylon.compiler.js.CeylonRunJsException;
import com.redhat.ceylon.compiler.js.CeylonRunJsTool;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class JsLaunchDelegate extends LaunchConfigurationDelegate {

    private MessageConsole findConsole() {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conman = plugin.getConsoleManager();
        MessageConsole cons = null;
        for (IConsole ccons : conman.getConsoles()) {
            if ("com.redhat.ceylon".equals(ccons.getName())) {
                cons = (MessageConsole)ccons;
                break;
            }
        }
        if (cons == null) {
            cons = new MessageConsole("com.redhat.ceylon", IConsoleConstants.MESSAGE_CONSOLE_TYPE,
                    null, "UTF-8", true);
            conman.addConsoles(new IConsole[]{cons});
        }
        cons.clearConsole();
        return cons;
    }

    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
            ILaunch launch, IProgressMonitor monitor) throws CoreException {

        final IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(
                configuration.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null));
        if (!CeylonBuilder.compileToJs(proj)) {
            throw new CoreException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                    "JavaScript compilation is disabled for this project."));
        }
        //Check that JS is enabled for the project
        final String qname = configuration.getAttribute(ATTR_MAIN_TYPE_NAME, "::run");
        final int tipple = qname.indexOf("::");
        final String methname = tipple >= 0 ? qname.substring(tipple+2) : qname;
        final String modname = configuration.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_CEYLON_MODULE, "default");
        final ArrayList<String> repos = new ArrayList<>();
        //Add output repo
        repos.add(getCeylonModulesOutputDirectory(proj).getAbsolutePath());
        //Add referenced project repos
        repos.addAll(getReferencedProjectsOutputRepositories(proj));
        //Add project repos
        repos.addAll(getCeylonRepositories(proj));
        PrintStream pout = new PrintStream(findConsole().newOutputStream());
        try {
            CeylonRunJsTool runner = new CeylonRunJsTool();
            //Set system repo
            runner.setSystemRepository(getInterpolatedCeylonSystemRepo(proj));
            runner.setRepositoryAsStrings(repos);
            runner.setRun(methname);
            runner.setModuleVersion(modname);
            runner.setOutput(pout);
            runner.setOffline(CeylonProjectConfig.get(proj).isOffline());
            runner.setDebug(true);
            runner.setCwd(proj.getLocation().toFile());
            runner.setNodeExe(configuration.getAttribute(
                    ICeylonLaunchConfigurationConstants.ATTR_JS_NODEPATH, (String)null));
            runner.run();
        } catch (CeylonRunJsException ex) {
            //Install node.js
            throw new CoreException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                    ex.getMessage()));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            if (proj.getReferencedProjects() != null && proj.getReferencedProjects().length > 0) {
                boolean all = true;
                for (IProject dep : proj.getReferencedProjects()) {
                    all &= CeylonBuilder.compileToJs(dep);
                }
                if (!all) {
                    throw new CoreException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                            "Not all the referenced projects compile to JavaScript, which may be causing this error:"
                            + ex.getClass().getName() + " - " + ex.getMessage()));
                }
            }
            throw new CoreException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                    "Node.js exited abnormally: " + 
                    ex.getClass().getName() + " - " + ex.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CoreException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                    e.getClass().getName() + " - " + e.getMessage()));
        } finally {
            pout.close();
        }
    }

}
