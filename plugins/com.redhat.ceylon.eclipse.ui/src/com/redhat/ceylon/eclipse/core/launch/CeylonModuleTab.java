/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.ControlAccessibleListener;
import org.eclipse.jdt.internal.debug.ui.launcher.AbstractJavaMainTab;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

/**
 * A launch configuration tab that displays and edits Ceylon project and
 * module name launch configuration attributes.
 */

public class CeylonModuleTab extends AbstractJavaMainTab  {
    
    private Text fModuleText;
    private Button fSearchButton;

    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite comp = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);
        ((GridLayout)comp.getLayout()).verticalSpacing = 0;
        createProjectEditor(comp);
        createVerticalSpacer(comp, 1);
        createModuleEditor(comp, "Module:");
        setControl(comp);
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
    }


    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
     */
    public Image getImage() {
        return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_JAR_WITH_SOURCE);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName() {
        return "Module";
    }
    
    /**
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getId()
     * 
     * @since 3.3
     */
    public String getId() {
        return PLUGIN_ID + ".ceylonModuleTab"; //$NON-NLS-1$
    }
    
    /**
     * Show a dialog that lists all main types
     */
    protected void handleSearchButtonSelected() {
                
        IJavaProject project = getJavaProject();

        IJavaElement[] elements = null;
        if ((project == null) || !project.exists()) {
            IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
            if (model != null) {
                try {
                    elements = model.getJavaProjects();
                }
                catch (JavaModelException e) {JDIDebugUIPlugin.log(e);}
            }
        }
        else {
            elements = new IJavaElement[]{project};
        }
        if (elements == null) {
            elements = new IJavaElement[]{};
        }

        Map<String, Module> modules = new HashMap<String, Module>();
        for (IJavaElement jElement : elements) {
            if (jElement instanceof IJavaProject) {
                IProject absProject = ((IJavaProject)jElement).getProject();
                for(Module module: CeylonBuilder.getProjectTypeChecker(absProject).getContext().getModules().getListOfModules()) {
                    modules.put(absProject.getName(), module);
                }
            }
        }

        CeylonModuleSelectionDialog cmsd = new CeylonModuleSelectionDialog(getShell(), modules, "Choose Ceylon Module"); 
        if (cmsd.open() == Window.CANCEL) {
            return;
        }
        
        Object[] results = cmsd.getResult(); 
        if (results != null && results.length > 0) {
            if (results[0] instanceof Map.Entry) {
                Map.Entry<String, Module> entry = (Map.Entry<String, Module>)results[0];
                if (entry != null) {
                    fModuleText.setText(entry.getValue().getNameAsString() + "/" + entry.getValue().getVersion());
                    fProjText.setText(entry.getKey());
                }
            }
        }
    }   
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.launcher.AbstractJavaMainTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration config) {
        super.initializeFrom(config);
        updateModuleNameFromConfig(config);
    }   

    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public boolean isValid(ILaunchConfiguration config) {
        setErrorMessage(null);
        setMessage(null);
        String name = fProjText.getText().trim();
        if (name.length() > 0) {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IStatus status = workspace.validateName(name, IResource.PROJECT);
            if (status.isOK()) {
                IProject project= ResourcesPlugin.getWorkspace().getRoot().getProject(name);
                if (!project.exists()) {
                    setErrorMessage("The project " + name + " does no exist."); 
                    return false;
                }
                if (!project.isOpen()) {
                    setErrorMessage("The project " + name + " is not opened"); 
                    return false;
                }
            }
            else {
                setErrorMessage("Error : " + status.getMessage()); 
                return false;
            }
        }
        name = fModuleText.getText().trim();
        if (name.length() == 0) {
            setErrorMessage("The Ceylon module name is not specified"); 
            return false;
        }
        
        // TODO check for run_ in typed-in module
        return true;
    }
            
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText().trim());
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, fModuleText.getText().trim());
        mapResources(config);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy config) {
        IJavaElement javaElement = getContext();
        if (javaElement != null) {
            initializeJavaProject(javaElement, config);
        }
        else {
            config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
        }
        initializeModuleName(javaElement, config);
    }

    protected void initializeModuleName(IJavaElement javaElement, ILaunchConfigurationWorkingCopy config) {
        String name = null;
        name = findModuleFromPackage(javaElement);

        if (name == null) {
            name = EMPTY_STRING;
        }
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, name);
        if (name.length() > 0) {   
            name = getLaunchConfigurationDialog().generateName(name);
            config.rename(name);
        }
    }


    private String findModuleFromPackage(IJavaElement javaElement) {
        String name =null;
        if (javaElement instanceof IPackageFragment) {
            IPackageFragment pkg = (IPackageFragment)javaElement;
            if (pkg.getClassFile("module_.class") != null) {
                Context context = CeylonBuilder.getProjectTypeChecker(pkg.getJavaProject().getProject()).getContext();
                for (Module module : context.getModules().getListOfModules()) {
                    if (module.getNameAsString().startsWith(pkg.getElementName())) {
                        name = module.getNameAsString() + "/" + module.getVersion();
                    }
                }
            }
        }
        return name;
    }
        
    protected void updateModuleNameFromConfig(ILaunchConfiguration config) {
        String moduleName = EMPTY_STRING;
            try {
                moduleName = config.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, EMPTY_STRING);
            }
            catch (CoreException ce) {JDIDebugUIPlugin.log(ce);}    
        fModuleText.setText(moduleName);        
    }
    
    protected void createModuleEditor(Composite parent, String text) {
        Group group = SWTFactory.createGroup(parent, text, 2, 1, GridData.FILL_HORIZONTAL); 
        fModuleText = SWTFactory.createSingleText(group, 1);
        fModuleText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
            }
            });
        ControlAccessibleListener.addListener(fModuleText, group.getText());
        fSearchButton = createPushButton(group, LauncherMessages.AbstractJavaMainTab_2, null); 
        fSearchButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }
            public void widgetSelected(SelectionEvent e) {
                handleSearchButtonSelected();
            }
            });
    }
}
