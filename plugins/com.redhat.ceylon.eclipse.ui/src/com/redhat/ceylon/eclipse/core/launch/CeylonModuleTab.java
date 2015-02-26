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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.ControlAccessibleListener;
import org.eclipse.jdt.internal.debug.ui.launcher.AbstractJavaMainTab;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
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
    private Button verboseCheck;
    private Button fStopInMainCheckButton;

    
    public void createControl(Composite parent) {
        Composite comp = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);
        ((GridLayout)comp.getLayout()).verticalSpacing = 0;
        createProjectEditor(comp);
        createVerticalSpacer(comp, 1);
        createModuleEditor(comp, "Module:");
        createVerticalSpacer(comp, 1);
        createDeclarationEditor(comp, "Runnable function or class (must be toplevel and shared, with no parameters):");
        setControl(comp);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
    }

    public Image getImage() {
        return CeylonLabelProvider.MODULE;
    }
    
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
        Module mod = LaunchHelper.chooseModule(LaunchHelper.getProjectFromName(this.fProjText.getText()), true);
        if (mod != null) {
            if (mod.isDefault()) {
                fModuleText.setText(Module.DEFAULT_MODULE_NAME);
            } else {
                fModuleText.setText(LaunchHelper.getFullModuleName(mod));
            }
            
            Declaration topLevel = LaunchHelper.getDefaultRunnableForModule(mod);

            // fill the field, else leave blank
            if (topLevel != null) {
                this.fTopLevelText.setText(LaunchHelper.getTopLevelDisplayName(topLevel));
            }
        }
    }
    
 
    /**
     * Show a dialog that lists all runnable types
     */
    protected void handleSearchButtonSelected() {
        
        Declaration d = LaunchHelper.chooseDeclaration(
                LaunchHelper.getDeclarationsForModule(
                        fProjText.getText(), fModuleText.getText()));
        
        if (d != null) {
            fTopLevelText.setText(LaunchHelper.getTopLevelDisplayName(d));
            
            // unique situation in which default module was selected but that the type belongs to a module
            Module mod = LaunchHelper.getModule(d);
            if (mod != null && !mod.isDefault()) {
                fModuleText.setText(LaunchHelper.getModuleFullName(d));
            }
        }
    }   

    public void initializeFrom(ILaunchConfiguration config) {
        super.initializeFrom(config); // sets project

        try { // exception in any value renders existing config useless, so combining
            fModuleText.setText(
                config.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, EMPTY_STRING));
            fTopLevelText.setText(
                config.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME, EMPTY_STRING));
            verboseCheck.setSelection(
                config.getAttribute(ICeylonLaunchConfigurationConstants.ATTR_LAUNCH_VERBOSE, false));

            updateStopInMainFromConfig(config);
        } catch (CoreException ce) {
            JDIDebugUIPlugin.log(ce);
        }
    }   

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
            
            try {
                ILaunchConfigurationType launchType = getCurrentLaunchConfiguration().getType();
                
                boolean javaEnabled = LaunchHelper.isBuilderEnabled(project,
                        ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_JAVA_MODULE);
                
                boolean jsEnabled = LaunchHelper.isBuilderEnabled(project,
                        ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_JAVASCIPT_MODULE);
                
                if (launchType.equals(DebugPlugin.getDefault().getLaunchManager()
                            .getLaunchConfigurationType(ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVA_MODULE))
                        && !javaEnabled) {
                    setErrorMessage("The project " + projectName + " is not enabled to run as a Java module"); 
                    return false;
                }
                
                if (launchType.equals(DebugPlugin.getDefault().getLaunchManager()
                            .getLaunchConfigurationType(ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVASCRIPT_MODULE))
                        && !jsEnabled) {
                    setErrorMessage("The project " + projectName + " is not enabled to run as a JavaScript module"); 
                    return false;
                }
            } catch (CoreException e) {
                e.printStackTrace(); // TODO logger
            }
            
        } else {
            return false;
        }

        String moduleName = fModuleText.getText().trim();
        if (moduleName == null || moduleName.length() == 0) {
            setErrorMessage("The Ceylon module is not specified"); 
            return false;
        }
        
        if (!LaunchHelper.isModuleInProject(project, moduleName)) {
            setErrorMessage("Ceylon module not found in project");
            return false;
        }
        
        String topLevelName = fTopLevelText.getText().trim();
        if (topLevelName == null || topLevelName.length() == 0) {
            setErrorMessage("The top level class or function is not specified"); 
            return false;
        }
        
        if (!LaunchHelper.isModuleContainsTopLevel(project, moduleName, 
                LaunchHelper.getTopLevelNormalName(moduleName, topLevelName))) {
            setErrorMessage("The top level class not found in module or is not runnable");
            return false;
        }
        // can't think of anything else
        return true;
    }

    public void performApply(ILaunchConfigurationWorkingCopy config) {

        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText().trim());
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, fModuleText.getText().trim());
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME, fTopLevelText.getText().trim());
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_LAUNCH_VERBOSE, verboseCheck.getSelection());
        mapResources(config);
        
        // attribute added in 2.1, so null must be used instead of false for backwards compatibility
        if (fStopInMainCheckButton.getSelection()) {
            config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, true);
        }
        else {
            config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, (String)null);
        }
    }
    
    public void setDefaults(ILaunchConfigurationWorkingCopy config) {
        IJavaElement javaElement = getContext();
        if (javaElement != null) {
            initializeJavaProject(javaElement, config);
        }
        else {
            config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
        }
        
        String projectName = "";
        String moduleName = null;
        String topLevelName = null;
        
        if (javaElement == null) {
            return;
        }
        
        Module module = null;
        if (getContext().getJavaProject().exists()) {
            module = LaunchHelper.getDefaultOrOnlyModule(getContext().getJavaProject().getProject(), true);
            projectName = getContext().getJavaProject().getProject().getName();
        }
        
        if (module != null) {
            moduleName = LaunchHelper.getFullModuleName(module);
        }

        if (moduleName == null) {
            moduleName = EMPTY_STRING;
        }
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, moduleName);
        if (moduleName.length() > 0) {   
            moduleName = getLaunchConfigurationDialog().generateName(moduleName);
            
        }

        Declaration topLevel = null;
        if (module != null) {
            topLevel = LaunchHelper.getDefaultRunnableForModule(module);
        }
        if (topLevel != null) {
            topLevelName = LaunchHelper.getRunnableName(topLevel);
        }

        if (topLevelName == null) {
            topLevelName = EMPTY_STRING;
        }
        config.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME, topLevelName);
        if (topLevelName.length() > 0) {   
            topLevelName = getLaunchConfigurationDialog().generateName(topLevelName);
 
        }

        config.rename(projectName + " - " + moduleName + " - " + topLevelName);
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
        
        Composite checks = SWTFactory.createComposite(group, 2, 2, GridData.BEGINNING);
        verboseCheck = createCheckButton(checks, "Verbose output"); // creating here just for visual alignment
        verboseCheck.addSelectionListener(getDefaultListener());
        
        fStopInMainCheckButton = SWTFactory.createCheckButton(checks, "St&op inside", null, false, 1);
        fStopInMainCheckButton.addSelectionListener(getDefaultListener());
    }
    
    /**
     * updates the stop in main attribute from the specified launch config
     * @param config the config to load the stop in main attribute from
     */
    private void updateStopInMainFromConfig(ILaunchConfiguration config) {
        boolean stop = false;
        try {
            stop = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, false);
        }
        catch (CoreException e) {JDIDebugUIPlugin.log(e);}
        fStopInMainCheckButton.setSelection(stop);
    }
}
