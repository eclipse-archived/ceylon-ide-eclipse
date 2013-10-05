package com.redhat.ceylon.eclipse.code.preferences;

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

import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleNode;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchManager;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchViewContentProvider;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchViewPart;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleVersionNode;

public final class ModuleImportSelectionDialog extends FilteredElementTreeSelectionDialog {
    
    ModuleImportSelectionDialog(Shell parent) {
        super(parent, new ModuleSelectionLabelProvider(), 
                new ModuleSearchViewContentProvider());
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
    
    static List<ModuleNode> getImportableModuleNodes(IProject project, Module module) {
        ModuleSearchResult searchResults = getModuleSearchResults("", 
                getProjectTypeChecker(project), project);
        List<ModuleDetails> list = new ArrayList<ModuleDetails>(searchResults.getResults());
        for (Iterator<ModuleDetails> it = list.iterator(); it.hasNext();) {
            String name = it.next().getName();
            if (module == null) {
                //i.e. New Ceylon Module wizard
                if (name.equals(Module.LANGUAGE_MODULE_NAME)) {
                    it.remove();
                }
            }
            else {
                //i.e. Ceylon Module properties page
                if (module.getNameAsString().equals(name)) {
                    it.remove();
                }
                else {
                    for (ModuleImport mi: module.getImports()) {
                        if (mi.getModule().getNameAsString().equals(name)) {
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }
        return ModuleSearchManager.convertResult(list);
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
        ElementTreeSelectionDialog dialog = new ModuleImportSelectionDialog(shell);
        dialog.setInput(getImportableModuleNodes(project, module));
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