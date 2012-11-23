package com.redhat.ceylon.eclipse.core.launch;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.redhat.ceylon.compiler.js.CeylonRunJsException;
import com.redhat.ceylon.compiler.js.CeylonRunJsTool;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;

/** Run Configuration page for JS launcher.
 * 
 * @author Enrique Zamudio
 */
public class LocalJsApplicationTabGroup extends AbstractLaunchConfigurationTabGroup {

    public void createTabs(final ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
            new AbstractLaunchConfigurationTab() {
                private Text nodePath;
                private Text target;
                private Button debug;
                private String module;
                private String projectName;

                @Override
                public void setDefaults(ILaunchConfigurationWorkingCopy conf) {
                    setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_DEBUG, conf, false, false);
                    try {
                        conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_NODEPATH, CeylonRunJsTool.findNode());
                    } catch (CeylonRunJsException ex) {
                        conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_NODEPATH, "");
                    }
                }
                
                @Override
                public void performApply(ILaunchConfigurationWorkingCopy conf) {
                    conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_DEBUG, debug.getSelection());
                    conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_NODEPATH, nodePath.getText());
                    if (module != null) {
                        //Target was changed
                        conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, target.getText());
                        conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_CEYLON_MODULE, module);
                        conf.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
                    }
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
                                CeylonRunJsTool.findNode()));
                        projectName = conf.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
                    } catch (CeylonRunJsException ex) {
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

                    pickTarget.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            //Choose a Ceylon project
                            IProject proj = chooseCeylonProject();
                            if (proj != null) {
                                //Get all top-level declarations from that project
                                TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(proj);
                                ArrayList<Declaration> decls = new ArrayList<Declaration>();
                                for (PhasedUnit pu : typeChecker.getPhasedUnits().getPhasedUnits()) {
                                    for (Declaration d : pu.getDeclarations()) {
                                        if (d.isShared() && (d instanceof com.redhat.ceylon.compiler.typechecker.model.Class || d instanceof Method)) {
                                            decls.add(d);
                                        }
                                    }
                                }
                                //Choose a declaration
                                if (decls.size() > 0) {
                                    Declaration dec = CeylonApplicationLaunchShortcut.chooseDeclaration(decls);
                                    if (dec != null) {
                                        target.setText(dec.getQualifiedNameString());
                                        Module mod = dec.getUnit().getPackage().getModule();
                                        module = mod.isDefault() ? "default" : mod.getNameAsString();
                                        if (!mod.isDefault()) {
                                            module = module + "/" + mod.getVersion();
                                        }
                                        projectName = proj.getName();
                                        scheduleUpdateJob();
                                    }
                                } else {
                                    showError("No shared declarations available in selected project.");
                                }
                            }
                        }
                    });
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

                private IProject chooseCeylonProject() {
                    ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
                    ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
                    dialog.setTitle(LauncherMessages.AbstractJavaMainTab_4); 
                    dialog.setMessage(LauncherMessages.AbstractJavaMainTab_3); 
                    IProject[] projs = ResourcesPlugin.getWorkspace().getRoot().getProjects();
                    ArrayList<IProject> pnames = new ArrayList<IProject>(projs.length);
                    IProject currentProj = null;
                    for (IProject p : projs) {
                        if (p.getName().equals(projectName)) {
                            currentProj = p;
                        }
                        if (CeylonNature.isEnabled(p) && CeylonBuilder.compileToJs(p)) {
                            pnames.add(p);
                        }
                    }
                    dialog.setElements(pnames.toArray());
                    if (currentProj != null) {
                        dialog.setInitialSelections(new Object[] { currentProj });
                    }
                    if (dialog.open() == Window.OK) {           
                        return (IProject) dialog.getFirstResult();
                    }       
                    return null;        
                }
            }
        };
        setTabs(tabs);
    }

}
