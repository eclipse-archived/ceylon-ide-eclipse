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

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;

/**
 * A launch configuration tab that displays and edits Ceylon project and
 * module name launch configuration attributes.
 */

public class CeylonModuleTab extends AbstractJavaMainTab  {
    
    private Text fModuleText;
    private Text fTopLevelText;
    private Button fModuleSearchButton;
    private Button fTopLevelSearchButton;
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite comp = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);
        ((GridLayout)comp.getLayout()).verticalSpacing = 0;
        createProjectEditor(comp);
        createVerticalSpacer(comp, 1);
        createModuleEditor(comp, "Module:");
        createVerticalSpacer(comp, 1);
        createDeclarationEditor(comp, "Top level method or class:");
        setControl(comp);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
    }


    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
     */
    public Image getImage() {
        return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_JAR);
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
     * Show a dialog that lists all modules in project
     */
    protected void handleModuleSearchButtonSelected() {
        LaunchHelper helper = new LaunchHelper(this.fProjText.getText());
        Set<Module> modules = helper.getModules(true);

        CeylonModuleSelectionDialog cmsd = new CeylonModuleSelectionDialog(getShell(), modules, "Choose Ceylon Module"); 
        if (cmsd.open() == Window.CANCEL) {
            return;
        }
        
        Object[] results = cmsd.getResult(); 
        if (results != null && results.length > 0) {
            if (results[0] instanceof Module) {
                Module mod = (Module)results[0];
                if (mod != null) {
                    if (mod.isDefault()) {
                        fModuleText.setText(Module.DEFAULT_MODULE_NAME);
                    } else {
                        fModuleText.setText(mod.getNameAsString() + "/" + mod.getVersion());
                    }
                    fProjText.setText(helper.getProject().getName());
                }
            }
        }
    }
    
 
    /**
     * Show a dialog that lists all runnable types
     */
    protected void handleSearchButtonSelected() {

        LaunchHelper helper = new LaunchHelper(this.fProjText.getText(), this.fModuleText.getText());
        FilteredItemsSelectionDialog sd = new CeylonTopLevelSelectionDialog(getShell(), false, 
            helper.getTopLevelDeclarations());
        if (sd.open() == Window.CANCEL) {
            return;
        }
        
        Object[] results = sd.getResult(); 
        if (results != null && results.length > 0) {
            if (results[0] instanceof Declaration) {
                Declaration decl = (Declaration)results[0];
                if (decl != null) {
                    fTopLevelText.setText(runnableDeclarationName(decl));
                    fProjText.setText(helper.getProject().getName());
                    fModuleText.setText(helper.getModuleFullName(decl));
                }
            }
        }
    }   

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.launcher.AbstractJavaMainTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration config) {
        super.initializeFrom(config); // sets project
        String moduleName = EMPTY_STRING;
            try {
                moduleName = config.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, EMPTY_STRING);
            }
            catch (CoreException ce) {JDIDebugUIPlugin.log(ce);}    
        fModuleText.setText(moduleName);        

        String topLevelName = EMPTY_STRING;
            try {
                topLevelName = config.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME, EMPTY_STRING);
            }
            catch (CoreException ce) {JDIDebugUIPlugin.log(ce);}    
        fTopLevelText.setText(topLevelName);
    }   

    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public boolean isValid(ILaunchConfiguration config) {
        setErrorMessage(null);
        setMessage(null);
        String projectName = fProjText.getText().trim();
        IProject project = LaunchHelper.getProjectFromName(projectName);
        
        if (project != null) {
            if (!project.exists()) {
                setErrorMessage("The project " + projectName + " does no exist."); 
                return false;
            }
            if (!project.isOpen()) {
                setErrorMessage("The project " + projectName + " is not opened"); 
                return false;
            }
            if (!CeylonNature.isEnabled(project)) {
                setErrorMessage("The project " + projectName + " is not a Ceylon project"); 
                return false;              
            }
        } else {
            return false;
        }

        LaunchHelper helper = new LaunchHelper(projectName);
        String moduleName = fModuleText.getText().trim();
        if (moduleName == null || moduleName.length() == 0) {
            setErrorMessage("The Ceylon module is not specified"); 
            return false;
        }
        
        helper = new LaunchHelper(projectName, moduleName);
        if (!helper.isProjectContainsModule(project, moduleName)) {
            setErrorMessage("Ceylon module not found in project");
            return false;
        }
        
        String topLevelName = fTopLevelText.getText().trim();
        if (topLevelName == null || topLevelName.length() == 0) {
            setErrorMessage("The top level class or function is not specified"); 
            return false;
        }
        
        if (!helper.isModuleContainsTopLevel(project, moduleName, topLevelName)) {
            setErrorMessage("The top level class not found in module");
            return false;
        }
        // can't think of anything else
        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText().trim());
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, fModuleText.getText().trim());
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME, fTopLevelText.getText().trim());
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
        
        String projectName = null;
        String moduleName = null;
        String topLevelName = null;
        
        if (javaElement != null) {
            projectName = getJavaProject().getProject().getName();
        } else {
            return;
        }
        
        LaunchHelper helper = new LaunchHelper(projectName);
        moduleName = helper.getModule().toString();

        if (moduleName == null) {
            moduleName = EMPTY_STRING;
        }
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, moduleName);
        if (moduleName.length() > 0) {   
            moduleName = getLaunchConfigurationDialog().generateName(moduleName);
            
        }

        topLevelName = runnableDeclarationName(helper.getTopLevel());

        if (topLevelName == null) {
            topLevelName = EMPTY_STRING;
        }
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME, topLevelName);
        if (topLevelName.length() > 0) {   
            topLevelName = getLaunchConfigurationDialog().generateName(topLevelName);
 
        }

        config.rename(getJavaProject().getProject().getName() + " - " + moduleName + " - " + topLevelName);
    }
    
    private String runnableDeclarationName(Declaration decl) {
        return decl.getQualifiedNameString().replace("::", ".");
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
        fModuleSearchButton = createPushButton(group, LauncherMessages.AbstractJavaMainTab_2, null); 
        fModuleSearchButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }
            public void widgetSelected(SelectionEvent e) {
                handleModuleSearchButtonSelected();
            }
            });
    }
  
    protected void createDeclarationEditor(Composite parent, String text) {
        Group group = SWTFactory.createGroup(parent, text, 2, 1, GridData.FILL_HORIZONTAL); 
        fTopLevelText = SWTFactory.createSingleText(group, 1);
        fTopLevelText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
            }
            });
        ControlAccessibleListener.addListener(fModuleText, group.getText());
        fTopLevelSearchButton = createPushButton(group, LauncherMessages.AbstractJavaMainTab_2, null); 
        fTopLevelSearchButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }
            public void widgetSelected(SelectionEvent e) {
                handleSearchButtonSelected();
            }
            });
    } 
}
