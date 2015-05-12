package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJava;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.model.cmr.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleNode;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchManager;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchViewContentProvider;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleVersionNode;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public abstract class ModuleImportContentProvider extends ModuleSearchViewContentProvider {
    
    private final Module module;
    private IProject project;
    
    public ModuleImportContentProvider(Module module, IProject project) {
        this.module = module;
        this.project = project;
    }
    
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ModuleCategoryNode) {
            ModuleCategoryNode cat = (ModuleCategoryNode) parentElement;
            List<ModuleNode> result = cat.getModules();
            if (result==null) {
                result = getImportableModuleNodes(module, cat.getName());
                cat.setModules(result);
            }
            return result.toArray();
        }
        return super.getChildren(parentElement);
    }
    
    @Override
    public boolean hasChildren(Object element) {
        return super.hasChildren(element) || 
                element instanceof ModuleCategoryNode;
    }
    
    private List<ModuleNode> getImportableModuleNodes(Module module, String prefix) {
        if (prefix.equals(".")) {
            List<ModuleNode> list = new ArrayList<ModuleNode>();
            TreeMap<String, String> map = new TreeMap<String, String>();
            for (IProject p: CeylonBuilder.getProjects()) {
                if (project==null ||
                        compileToJava(p) && compileToJs(p) ||
                        compileToJava(p) && compileToJava(project) && !compileToJs(project) || 
                        compileToJs(p) && compileToJs(project) && !compileToJava(project)) {
                    for (Module m: CeylonBuilder.getProjectDeclaredSourceModules(p)) {
                        if (!excluded(module, m.getNameAsString())) {
                            map.put(m.getNameAsString(), m.getVersion());
                        }
                    }
                }
            }
            for (Map.Entry<String, String> entry: map.entrySet()) {
                ModuleNode moduleNode = new ModuleNode(entry.getKey(), new ArrayList<ModuleVersionNode>(1));
                moduleNode.getVersions().add(new ModuleVersionNode(moduleNode, entry.getValue()));
                list.add(moduleNode);
            }
            return list;
        }
        else if (prefix.startsWith("java.")||prefix.equals("java.|javax.")) {
            List<ModuleNode> list = new ArrayList<ModuleNode>();
            for (String name: new TreeSet<String>(JDKUtils.getJDKModuleNames())) {
                if ((prefix.equals("java.|javax.")||name.startsWith(prefix)) && !excluded(module, name)) {
                    ModuleNode moduleNode = new ModuleNode(name, new ArrayList<ModuleVersionNode>(1));
                    moduleNode.getVersions().add(new ModuleVersionNode(moduleNode, JDKUtils.jdk.version));
                    list.add(moduleNode);
                }
            }
            return list;
        }
        else {
            ModuleSearchResult searchResults = getModules(prefix);
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

    public abstract ModuleSearchResult getModules(String prefix);
        
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
    
}