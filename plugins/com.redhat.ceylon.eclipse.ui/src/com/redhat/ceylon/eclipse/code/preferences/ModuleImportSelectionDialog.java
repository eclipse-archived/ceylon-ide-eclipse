package com.redhat.ceylon.eclipse.code.preferences;

import static java.util.Collections.emptyMap;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.eclipse.code.modulesearch.ModuleNode;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchViewPart;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleVersionNode;

public final class ModuleImportSelectionDialog extends FilteredElementTreeSelectionDialog {
    
    public ModuleImportSelectionDialog(Shell parent,
            ModuleImportContentProvider contentProvider) {
        super(parent, new ModuleSelectionLabelProvider(), 
                contentProvider);
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
    
    public static Map<String,String> selectModules(ModuleImportSelectionDialog dialog, IProject project) {
        dialog.setInput(ModuleCategoryNode.getCategoryNodes(project));
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