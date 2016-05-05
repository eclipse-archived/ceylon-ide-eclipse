package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME;
import static com.redhat.ceylon.eclipse.core.launch.LaunchHelper.getTopLevelDisplayName;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Module;

public abstract class CeylonModuleLaunchShortcut implements ILaunchShortcut2 {

    protected ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }
 
    protected abstract ILaunchConfigurationType getConfigurationType();
 
    private String getLaunchConfigurationName(String projectName, 
            String moduleName, Declaration declarationToRun) {
        String topLevelDisplayName = 
                LaunchHelper.getTopLevelDisplayName(declarationToRun);
        
        String configurationName = 
                projectName.trim() + " \u2014 " 
                + moduleName.trim() + " \u2014 "  
                + topLevelDisplayName.trim() + 
                " \u2192 " + launchType();
        
//        configurationName = configurationName
//                .replaceAll("[\u00c0-\ufffe]", "_");
        
        return getLaunchManager()
                .generateLaunchConfigurationName(configurationName);
    }
    
    /**
     * Creates a <b>new</b> configuration if none was found or chosen.
     * 
     * @return the configuration created.
     */
    protected ILaunchConfiguration createConfiguration(Declaration declarationToRun, 
            IResource resource) {
        try {
            Module mod = LaunchHelper.getModule(declarationToRun);      
            String moduleName = mod.getNameAsString();
            String projectName = resource.getProject().getName();
            
            String lcn = 
                    getLaunchConfigurationName(projectName, 
                            moduleName, declarationToRun);
            ILaunchConfigurationWorkingCopy wc = 
                    getConfigurationType()
                        .newInstance(null, lcn);
            
            wc.setAttribute(ATTR_PROJECT_NAME, projectName);
            wc.setAttribute(ATTR_MODULE_NAME, LaunchHelper.getFullModuleName(mod));
            
            // save the runnable display name, which may be exact name or 'run - default'
            wc.setAttribute(ATTR_TOPLEVEL_NAME, 
                    LaunchHelper.getTopLevelDisplayName(declarationToRun));
            
            wc.setMappedResources(new IResource[] {resource});
            return wc.doSave();
        } catch (CoreException exception) {
            MessageDialog.openError(EditorUtil.getShell(), "Ceylon Module Launcher Error", 
                    exception.getStatus().getMessage());
            return null;
        }
    }
    
    abstract String launchType();
    
    /**
     * Finds and returns an <b>existing</b> configuration to re-launch for the given type,
     * or <code>null</code> if there is no existing configuration.
     * 
     * @return a configuration to use for launching the given type or <code>null</code> if none
     */
    protected ILaunchConfiguration findLaunchConfiguration(Declaration declaration, 
            IResource resource, ILaunchConfigurationType configType) {
        
        List<ILaunchConfiguration> candidateConfigs = 
                getLaunchConfigurations(declaration, resource, configType);
        
        int candidateCount = candidateConfigs.size();
        if (candidateCount == 1) {
            return candidateConfigs.get(0);
        } 
        else if (candidateCount > 1) {
            return chooseConfiguration(candidateConfigs);
        }
        return null;
    }

    private List<ILaunchConfiguration> getLaunchConfigurations(Declaration declaration, 
            IResource resource, ILaunchConfigurationType configType) {
        List<ILaunchConfiguration> candidateConfigs = 
                Collections.<ILaunchConfiguration>emptyList();
        String projectName = resource.getProject().getName();
        
        try {
            ILaunchConfiguration[] configs = getLaunchManager()
                    .getLaunchConfigurations(configType);
            candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
 
            Module mod = LaunchHelper.getModule(declaration);
            String moduleName = LaunchHelper.getFullModuleName(mod);
            if (mod.isDefaultModule()) {
                moduleName = mod.getNameAsString();
            }
            
            String topLevelDisplayName = getTopLevelDisplayName(declaration);
            
            for (int i = 0; i < configs.length; i++) {
                ILaunchConfiguration config = configs[i];
                if (config.getAttribute(ATTR_TOPLEVEL_NAME, "").equals(topLevelDisplayName) && 
                        config.getAttribute(ATTR_PROJECT_NAME, "").equals(projectName) &&
                        config.getAttribute(ATTR_MODULE_NAME, "").equals(moduleName)) {
                    candidateConfigs.add(config);
                }
            }
        }
        catch (CoreException e) {
            e.printStackTrace(); // TODO : Use a logger
        }
        return candidateConfigs;
    }
    
    /**
     * Choose a pre-defined configuration if there is more than one defined configuration
     * for the same combination of project, module and runnable
     * @param configuration list
     * @return the chosen configuration
     */
    protected ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList) {
        IDebugModelPresentation labelProvider = 
                DebugUITools.newDebugModelPresentation();
        ElementListSelectionDialog dialog = 
                new ElementListSelectionDialog(EditorUtil.getShell(), labelProvider);
        dialog.setElements(configList.toArray());
        dialog.setTitle("Ceylon Launcher");  
        dialog.setMessage("Please choose a configuration to start the Ceylon application");
        dialog.setMultipleSelection(false);
        int result = dialog.open();
        labelProvider.dispose();
        if (result == Window.OK) {
            return (ILaunchConfiguration) dialog.getFirstResult();
        }
        return null;        
    }
 
    /**
     * Launch from a Navigation selection - right-click, run-as or debug-as
     */
    @Override
    public void launch(ISelection selection, String mode) {
        if (! (selection instanceof IStructuredSelection)) {
            return;
        }
        
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        List<IFile> files = new LinkedList<IFile>(); 
        for (Object object : structuredSelection.toList()) {
            if (object instanceof IAdaptable) {
                IResource resource = (IResource) 
                        ((IAdaptable)object).getAdapter(IResource.class);
                if (resource != null) {
                    if (resource instanceof IProject) {
                        final IProject project = (IProject)resource;
                        Module mod = LaunchHelper.getDefaultOrOnlyModule(project, true);
                        if (mod == null) {
                            mod = LaunchHelper.chooseModule(project, true);
                        }
                        if (mod != null) {
                            launchModule(mod, resource, mode);
                            return; // do not look at other parts of the selection
                        } else {
                            return;
                        }
                    }
                    else if (resource instanceof IFolder 
                            && LaunchHelper.getModule((IFolder)resource) != null) { //check for module
                        launchModule(LaunchHelper.getModule((IFolder)resource), resource, mode);
                        return;
                    } else {
                        LaunchHelper.addFiles(files, resource);
                    }
                }
            }
        }
        searchAndLaunch(files, mode);
    }

    /**
     * Launch from the current editor context 
     */
    @Override
    public void launch(IEditorPart editor, String mode) {
        IFile file = EditorUtil.getFile(editor.getEditorInput());
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            CeylonParseController cpc = ce.getParseController();
            if (cpc!=null) {
                Tree.CompilationUnit cu = cpc.getLastCompilationUnit();
                if (cu!=null) {
                    ISelection selection = ce.getSelectionProvider().getSelection();
                    if (selection instanceof ITextSelection) {
                        Node node = Nodes.findToplevelStatement(cu, 
                                Nodes.findNode(cu, cpc.getTokens(), (ITextSelection) selection));
                        if (node instanceof Tree.AnyMethod) {
                            Function method = 
                                    ((Tree.AnyMethod) node).getDeclarationModel();
                            if (method!=null && 
                                    method.isShared() &&
                                    method.isToplevel() && 
                                    !method.getParameterLists().isEmpty() &&
                                    method.getParameterLists().get(0).getParameters().isEmpty()) {
                                launch(method, file, mode);
                                return;
                            }
                        }
                        if (node instanceof Tree.AnyClass) {
                            Class clazz = 
                                    ((Tree.AnyClass) node).getDeclarationModel();
                            if (clazz!=null && 
                                    clazz.isShared() &&
                                    clazz.isToplevel() && 
                                    !clazz.isAbstract() &&
                                    clazz.getParameterList()!=null &&
                                    clazz.getParameterList().getParameters().isEmpty()) {
                                launch(clazz, file, mode);
                                return;
                            }
                        }
                    }
                }
            }
        }
        searchAndLaunch(Arrays.asList(file), mode);
    }
 
    /**
     * Launches a dialogue to help choose a declaration and its associated file
     * @param files - the files to search in
     * @param mode
     */
    private void searchAndLaunch(List<IFile> files, String mode) {
        
        Object[] ret = LaunchHelper.findDeclarationFromFiles(files);
        if (ret != null && ret[0] != null && ret[1] != null) {
            launch((Declaration)ret[0], (IFile)ret[1], mode);
        }
    }

    /**
     * Launches a module after giving an opportunity to select a runnable declaration
     * @param mod - module
     * @param resource - associate Eclipse resource
     * @param mode
     */
    private void launchModule(Module mod, IResource resource, String mode) {
        
        Declaration declarationToRun = 
                LaunchHelper.getDefaultRunnableForModule(mod);

        List<Declaration> decls = new LinkedList<Declaration>();
        
        if (declarationToRun != null) {
            decls.add(declarationToRun); // top
        }
        
        decls.addAll(LaunchHelper.getDeclarationsForModule(
                resource.getProject().getName(), 
                LaunchHelper.getFullModuleName(mod)));
        
        declarationToRun = LaunchHelper.chooseDeclaration(decls);
        if (declarationToRun != null) {
            launch(declarationToRun, resource, mode);
        }
    }
    
    /**
     * The actual launch after the declaration has been chosen
     * @param declarationToRun - the chosen declaration
     * @param resource - the associated Eclipse resource
     * @param mode
     */
    public void launch(Declaration declarationToRun, 
            IResource resource, String mode) {

        ILaunchConfiguration config = 
                findLaunchConfiguration(declarationToRun, 
                        resource, getConfigurationType());
        if (config == null) {
            config = createConfiguration(declarationToRun, 
                            resource);
        }
        if (config != null) {
            DebugUITools.launch(config, mode);
        }           
    }

    @Override
    public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editor) {
        IFile file = EditorUtil.getFile(editor.getEditorInput());
        ArrayList<ILaunchConfiguration> list = 
                new ArrayList<ILaunchConfiguration>();
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            CeylonParseController cpc = ce.getParseController();
            if (cpc!=null) {
                Tree.CompilationUnit cu = cpc.getLastCompilationUnit();
                if (cu!=null) {
                    ITextSelection selection = ce.getSelectionFromThread();
                    Node node = Nodes.findToplevelStatement(cu, 
                            Nodes.findNode(cu,cpc.getTokens(),selection));
                    if (node instanceof Tree.AnyMethod) {
                        Function method = 
                                ((Tree.AnyMethod) node).getDeclarationModel();
                        if (method!=null && 
                                method.isShared() &&
                                method.isToplevel() && 
                                !method.getParameterLists().isEmpty() &&
                                method.getParameterLists().get(0).getParameters().isEmpty()) {
                            list.addAll(getLaunchConfigurations(method, file, 
                                    getConfigurationType()));
                        }
                    }
                    if (node instanceof Tree.AnyClass) {
                        Class clazz = 
                                ((Tree.AnyClass) node).getDeclarationModel();
                        if (clazz!=null && 
                                clazz.isShared() &&
                                clazz.isToplevel() && 
                                !clazz.isAbstract() &&
                                clazz.getParameterList()!=null &&
                                clazz.getParameterList().getParameters().isEmpty()) {
                            list.addAll(getLaunchConfigurations(clazz, file, 
                                    getConfigurationType()));
                        }
                    }
                }
            }
        }
        return list.toArray(new ILaunchConfiguration[0]);
    }

    @Override
    public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IResource getLaunchableResource(ISelection selection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IResource getLaunchableResource(IEditorPart editor) {
        return EditorUtil.getFile(editor.getEditorInput());
    }
}
