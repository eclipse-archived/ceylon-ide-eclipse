package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.ModuleCategoryNode.getCategoryNodes;
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
            ModuleNode moduleNode = (ModuleNode) element;
            return moduleNode.getName();
        }
        else if (element instanceof ModuleVersionNode) {
            ModuleVersionNode moduleVersionNode = 
                    (ModuleVersionNode) element;
            return moduleVersionNode.getModule().getName();
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
        IStructuredSelection selection = 
                (IStructuredSelection) 
                    getTreeViewer().getSelection();
        Object selectedElement = selection
                .getFirstElement();
        if (selectedElement instanceof ModuleNode) {
            ModuleNode moduleNode = 
                    (ModuleNode) selectedElement;
            versionNode = moduleNode.getLastVersion();
        }
        else if (selectedElement instanceof ModuleVersionNode) {
            versionNode = 
                    (ModuleVersionNode) selectedElement;
        }
        else {
            return "";
        }
        return ModuleSearchViewPart.getModuleDoc(versionNode);
    }
    
    static Map<String, String> getAddedModulesWithVersions(
            Object[] results) {
        Map<String,String> added = 
                new HashMap<String,String>();
        for (Object result: results) {
            String name; String version;
            if (result instanceof ModuleNode) {
                ModuleNode moduleNode = (ModuleNode) result;
                name = moduleNode.getName();
                version = 
                        moduleNode.getLastVersion()
                            .getVersion();
            }
            else if (result instanceof ModuleVersionNode) {
                ModuleVersionNode moduleVersionNode = 
                        (ModuleVersionNode) result;
                name = 
                        moduleVersionNode.getModule()
                            .getName();
                version = moduleVersionNode.getVersion();
            }
            else {
                continue;
            }
            added.put(name, version);
        }
        return added;
    }
    
    public static Map<String,String> selectModules(
            ModuleImportSelectionDialog dialog, 
            IProject project) {
        dialog.setInput(getCategoryNodes(project));
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