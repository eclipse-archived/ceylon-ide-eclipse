package com.redhat.ceylon.eclipse.core.launch;

import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.compiler.js.Runner;

public class LocalJsApplicationTabGroup extends AbstractLaunchConfigurationTabGroup {

    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
            new AbstractLaunchConfigurationTab() {
                private Text nodePath;
                private Text target;
                private Button debug;

                @Override
                public void setDefaults(ILaunchConfigurationWorkingCopy conf) {
                    setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_DEBUG, conf, false, false);
                    try {
                        conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_NODEPATH, Runner.findNode());
                    } catch (FileNotFoundException ex) {
                        conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_NODEPATH, "");
                    }
                }
                
                @Override
                public void performApply(ILaunchConfigurationWorkingCopy conf) {
                    conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_DEBUG, debug.getSelection());
                    conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_NODEPATH, nodePath.getText());
                }
                
                @Override
                public void initializeFrom(ILaunchConfiguration conf) {
                    try {
                        target.setText(conf.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
                                "::run"));
                        debug.setSelection(conf.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_DEBUG, false));
                        nodePath.setText(conf.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_NODEPATH,
                                Runner.findNode()));
                    } catch (FileNotFoundException ex) {
                        nodePath.setText("[NOT FOUND]");
                        setErrorMessage(ex.getMessage());
                    } catch (CoreException ex) {
                        setErrorMessage(ex.getMessage());
                    }
                }
                
                @Override
                public String getName() {
                    return "Ceylon JavaScript Launcher";
                }
                
                @Override
                public void createControl(Composite parent) {
                    //No idea how this should be editable
                    new Label(parent, SWT.LEFT).setText("Class/method to run:");
                    target = new Text(parent, SWT.SINGLE | SWT.LEFT);
                    new Label(parent, SWT.LEFT).setText("Path to node.js:");
                    //Ideally we'd have a button next to the text, to open a file dialog and select the path
                    nodePath = new Text(parent, SWT.SINGLE | SWT.LEFT);
                    createVerticalSpacer(parent, 10);
                    debug = createCheckButton(parent, "Output full stack trace on errors");
                    debug.setEnabled(true);
                }
                public String getId() {
                    return "com.redhat.ceylon.js.launcher.conf";
                };
            }
        };
        setTabs(tabs);
    }
}
