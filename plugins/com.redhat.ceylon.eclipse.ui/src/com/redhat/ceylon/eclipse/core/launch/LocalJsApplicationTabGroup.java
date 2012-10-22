package com.redhat.ceylon.eclipse.core.launch;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
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
                
                void showError(String msg) {
                    setErrorMessage(msg);
                    MessageDialog.openError(getShell(), "Ceylon JS Run Configuration", 
                            msg); 
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
                        showError(ex.getMessage());
                    } catch (CoreException ex) {
                        showError(ex.getMessage());
                    }
                }
                
                @Override
                public String getName() {
                    return "Ceylon JavaScript Launcher";
                }

                Group createGroup(Composite parent, int cols) {
                    Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
                    GridData gd= new GridData(SWT.FILL, SWT.FILL, true, false);
                    group.setLayoutData(gd);
                    GridLayout layout = new GridLayout();
                    layout.numColumns = cols;
                    group.setLayout(layout); 
                    return group;
                }

                @Override
                public void createControl(Composite parent) {
                    Composite main = new Composite(parent, SWT.FILL);
                    main.setLayout(new GridLayout(1, false));
                    main.setFont(parent.getFont());
                    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
                    gd.horizontalSpan = 1;
                    main.setLayoutData(gd);
                    main.setVisible(true);
                    setControl(main);

                    //No idea how this should be editable
                    new Label(main, SWT.LEFT).setText("Class/method to run:");
                    Group group = createGroup(main, 2);
                    target = new Text(group, SWT.SINGLE | SWT.BORDER | SWT.FILL);
                    target.setEditable(false);
                    target.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                    Button pickTarget = new Button(group, SWT.PUSH);
                    pickTarget.setText("Choose...");

                    new Label(main, SWT.LEFT).setText("Path to node.js executable:");
                    group = createGroup(main, 2);

                    //Select the node.js executable
                    nodePath = new Text(group, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
                    nodePath.setEditable(false);
                    nodePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                    Button pickNode = new Button(group, SWT.PUSH);
                    pickNode.setText("Browse...");
                    createVerticalSpacer(main, 10);
                    debug = createCheckButton(main, "Output full stack trace on errors");
                    debug.setEnabled(true);

                    pickNode.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            FileDialog fd = new FileDialog(getShell(), SWT.NULL);
                            String path = fd.open();
                            if (path != null) {
                                File np = new File(path);
                                if (np.isFile() && np.canExecute()) {
                                    nodePath.setText(path);
                                    scheduleUpdateJob();
                                } else {
                                    showError("You must select an executable file.");
                                }
                            }
                        }
                    });
                    debug.addSelectionListener(new SelectionListener() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            scheduleUpdateJob();
                        }
                        @Override
                        public void widgetDefaultSelected(SelectionEvent e) {
                            scheduleUpdateJob();
                        }
                    });
                }
                public String getId() {
                    return "com.redhat.ceylon.js.launcher.conf";
                };
            }
        };
        setTabs(tabs);
    }

}
