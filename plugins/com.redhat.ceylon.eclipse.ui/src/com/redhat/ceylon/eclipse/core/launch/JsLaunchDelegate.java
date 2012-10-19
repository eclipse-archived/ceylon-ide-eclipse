package com.redhat.ceylon.eclipse.core.launch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import com.redhat.ceylon.compiler.js.Runner;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;

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
            cons = new MessageConsole("com.redhat.ceylon", null);
            conman.addConsoles(new IConsole[]{cons});
        }
        cons.clearConsole();
        return cons;
    }

    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
            ILaunch launch, IProgressMonitor monitor) throws CoreException {

        //Check that JS is enabled for the project
        String qname = configuration.getAttribute(ATTR_MAIN_TYPE_NAME, "::run");
        String methname = qname.substring(qname.indexOf("::")+2);
        String modname = configuration.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_CEYLON_MODULE, "default");
        System.out.println("Launching " + methname + " from " + modname);
        ArrayList<String> repos = new ArrayList<String>(4);
        //Add workspace repo
        //Add project repo?
        //Add user repo
        repos.add("modules");
        PrintStream pout = new PrintStream(findConsole().newOutputStream());
        try {
            Runner.run(repos, modname, methname, pout);
        } catch (FileNotFoundException ex) {
            //Install node.js
            System.err.println(ex.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            pout.close();
        }
    }

}
