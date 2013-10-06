package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.JDK_MODULE_VERSION;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getModuleSearchResults;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleNode;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchManager;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchViewContentProvider;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchViewPart;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleVersionNode;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.EditedSourceFile;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;

public final class ModuleImportSelectionDialog extends FilteredElementTreeSelectionDialog {
    
    ModuleImportSelectionDialog(Shell parent, final IProject project, final Module module) {
        super(parent, new ModuleSelectionLabelProvider(), 
                new ModuleSearchViewContentProvider() {
            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof ModuleCategoryNode) {
                    ModuleCategoryNode cat = (ModuleCategoryNode) parentElement;
                    List<ModuleNode> result = cat.getModules();
                    if (result==null) {
                        result = getImportableModuleNodes(project, module, cat.getName());
                        cat.setModules(result);
                    }
                    return result.toArray();
                }
                return super.getChildren(parentElement);
            }
            @Override
                    public boolean hasChildren(Object element) {
                        return super.hasChildren(element)|| 
                                element instanceof ModuleCategoryNode;
                    }
        });
        setTitle("Module Selection");
        setMessage("Select modules to import:");
    }
    
    @Override
    protected String getElementName(Object element) {
        if (element instanceof ModuleNode) {
            return ((ModuleNode) element).getName();
        }
        else if (element instanceof ModuleVersionNode) {
            return ((ModuleVersionNode) element).getModule().getName();
        }
        return "";
    }
    
    @Override
    protected boolean isCategory(Object element) {
        return element instanceof ModuleCategoryNode;
    }
    
    @Override
    protected String getDoc() {
        ModuleVersionNode versionNode;
        Object selectedElement = ((IStructuredSelection) getTreeViewer().getSelection())
                .getFirstElement();
        if (selectedElement instanceof ModuleNode) {
            versionNode = ((ModuleNode) selectedElement).getLastVersion();
        }
        else if (selectedElement instanceof ModuleVersionNode) {
            versionNode = (ModuleVersionNode) selectedElement;
        }
        else {
            return "";
        }
        return ModuleSearchViewPart.getModuleDoc(versionNode);
    }
    
    static List<ModuleNode> getImportableModuleNodes(IProject project, Module module, String prefix) {
        if (prefix.equals(".")) {
            List<ModuleNode> list = new ArrayList<ModuleNode>();
            for (IProject p: CeylonBuilder.getProjects()) {
                for (Module m: CeylonBuilder.getProjectModules(p).getListOfModules()) {
                    if (m.getUnit() instanceof ProjectSourceFile ||
                        m.getUnit() instanceof EditedSourceFile) {
                        if (!excluded(module, m.getNameAsString())) {
                            ModuleNode moduleNode = new ModuleNode(m.getNameAsString(), new ArrayList<ModuleVersionNode>(1));
                            moduleNode.getVersions().add(new ModuleVersionNode(moduleNode, m.getVersion()));
                            list.add(moduleNode);
                        }
                    }
                }
            }
            return list;
        }
        else if (prefix.startsWith("java.")) {
            List<ModuleNode> list = new ArrayList<ModuleNode>();
            for (String name: JDKUtils.getJDKModuleNames()) {
                if (name.startsWith(prefix) && !excluded(module, name)) {
                    ModuleNode moduleNode = new ModuleNode(name, new ArrayList<ModuleVersionNode>(1));
                    moduleNode.getVersions().add(new ModuleVersionNode(moduleNode, JDK_MODULE_VERSION));
                    list.add(moduleNode);
                }
            }
            return list;
        }
        else {
            ModuleSearchResult searchResults = getModuleSearchResults(prefix, 
                    getProjectTypeChecker(project), project);
            List<ModuleDetails> list = new ArrayList<ModuleDetails>(searchResults.getResults());
            for (Iterator<ModuleDetails> it = list.iterator(); it.hasNext();) {
                String name = it.next().getName();
                if (excluded(module, name)) {
                    it.remove();
                }
            }
            return ModuleSearchManager.convertResult(list);
        }
    }
        
    private static boolean excluded(Module module, String name) {
        if (module == null) {
            //i.e. New Ceylon Module wizard
            if (name.equals(Module.LANGUAGE_MODULE_NAME)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            //i.e. Ceylon Module properties page
            if (module.getNameAsString().equals(name)) {
                return true;
            }
            else {
                for (ModuleImport mi: module.getImports()) {
                    if (mi.getModule().getNameAsString().equals(name)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
    
    static Map<String, String> getAddedModulesWithVersions(Object[] results) {
        Map<String,String> added = new HashMap<String,String>();
        for (Object result: results) {
            String name; String version;
            if (result instanceof ModuleNode) {
                name = ((ModuleNode) result).getName();
                version = ((ModuleNode) result).getLastVersion().getVersion();
            }
            else if (result instanceof ModuleVersionNode) {
                name = ((ModuleVersionNode) result).getModule().getName();
                version = ((ModuleVersionNode) result).getVersion();
            }
            else {
                continue;
            }
            added.put(name, version);
        }
        return added;
    }
    
    public static Map<String,String> selectModules(Shell shell, IProject project, Module module) {
        ElementTreeSelectionDialog dialog = new ModuleImportSelectionDialog(shell, project, module);
        dialog.setInput(ModuleCategoryNode.getCategoryNodes());
        dialog.open();
        Object[] results = dialog.getResult();
        if (results==null) {
            return emptyMap();
        }
        else {
            return getAddedModulesWithVersions(results);
        }
    }

}