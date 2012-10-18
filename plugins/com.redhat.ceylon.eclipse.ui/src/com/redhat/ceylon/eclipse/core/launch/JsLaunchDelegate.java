package com.redhat.ceylon.eclipse.core.launch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;

import com.redhat.ceylon.compiler.js.Runner;
import com.redhat.ceylon.eclipse.code.editor.Util;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;

public class JsLaunchDelegate extends LaunchConfigurationDelegate {

    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
            ILaunch launch, IProgressMonitor monitor) throws CoreException {

        String qname = configuration.getAttribute(ATTR_MAIN_TYPE_NAME, "::run");
        String methname = qname.substring(qname.indexOf("::")+2);
        String modname = configuration.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_CEYLON_MODULE, "default");
        System.out.println("Launching " + methname + " from " + modname);
        ArrayList<String> repos = new ArrayList<String>(4);
        //Add workspace repo
        //Add project repo?
        //Add user repo
        repos.add("modules");
        try {
            Runner.run(repos, modname, methname);
        } catch (FileNotFoundException ex) {
            //Install node.js
            System.err.println(ex.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
