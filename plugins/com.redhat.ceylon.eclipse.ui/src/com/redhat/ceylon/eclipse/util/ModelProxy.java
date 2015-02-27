package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getTypeCheckers;
import static java.util.Collections.singletonList;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;

public class ModelProxy {
    
    private final String qualifiedName;
    private final String unitName;
    private final String packageName;
    private final String moduleName;
    private final String moduleVersion;
    private final String name;
    
    private final SoftReference<Declaration> declaration;
    
    public ModelProxy(Declaration declaration) {
        this.name = declaration.getName();
        this.qualifiedName = declaration.getQualifiedNameString();
        //TODO: persist the signature somehow, to handle overloads
        Unit unit = declaration.getUnit();
        this.unitName = unit.getFilename();
        Package pack = unit.getPackage();
        this.packageName = pack.getNameAsString();
        this.moduleName = pack.getModule().getNameAsString();
        this.moduleVersion = pack.getModule().getVersion();
        this.declaration = new SoftReference<Declaration>(declaration);
    }
    
    public Declaration getDeclaration(IProject project) {
        Declaration dec = this.declaration.get();
        if (dec!=null) return dec;
        //first handle the case of new declarations 
        //defined in a dirty editor, and local declarations
        //in an external source file
        IEditorPart part = EditorUtil.getCurrentEditor();
        if (part instanceof CeylonEditor /*&& part.isDirty()*/) {
            final CeylonParseController controller =
                    ((CeylonEditor) part).getParseController();
            Tree.CompilationUnit rootNode = controller.getRootNode();
            if (rootNode!=null) {
                Unit unit = rootNode.getUnit();
                if (unit!=null) {
                    String pname = unit.getPackage().getNameAsString();
                    if (pname.equals(packageName)) {
                        Declaration result = 
                                getDeclarationInUnit(qualifiedName, unit);
                        if (result!=null) {
                            return result;
                        }
                    }
                }
            }
        }
        TypeChecker typeChecker = 
                getTypeChecker(moduleName, moduleVersion, project).get(0);
        Package pack = getModelLoader(typeChecker)
                .getLoadedModule(moduleName)
                .getPackage(packageName);
        for (Unit unit: pack.getUnits()) {
            if (unit.getFilename().equals(unitName)) {
                Declaration result = 
                        getDeclarationInUnit(qualifiedName, unit);
                if (result!=null) {
                    return result;
                }
            }
        }
        //the above approach doesn't work for binary modules 
        //because the filenames are wrong for the iterated 
        //units (.class instead of .ceylon), nor for Java
        //modules, apparently
        for (Declaration d: pack.getMembers()) {
            String qn = d.getQualifiedNameString();
            if (qn.equals(qualifiedName)) {
                return d;
            }
            else if (qualifiedName.startsWith(qn)) {
                for (Declaration m: d.getMembers()) {
                    if (m.getQualifiedNameString().equals(qualifiedName)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
    
    public String getQualifiedName() {
        return qualifiedName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this==obj) {
            return true;
        }
        else if (obj==null) {
            return false;
        }
        else if (obj instanceof ModelProxy) {
            ModelProxy proxy = (ModelProxy) obj;
            return qualifiedName.equals(proxy.qualifiedName);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return qualifiedName.hashCode();
    }
    
    @Override
    public String toString() {
        return qualifiedName;
    }
    
    public static List<TypeChecker> getTypeChecker(Module module,
            /*optional*/ IProject project) {
        return getTypeChecker(module.getNameAsString(), 
                module.getVersion(), 
                project);
    }
    
    static List<TypeChecker> getTypeChecker(String moduleName, String moduleVersion, 
            /*optional*/ IProject project) {
        if (project==null) {
            List<TypeChecker> tcs = new ArrayList<TypeChecker>();
            for (TypeChecker typeChecker: getTypeCheckers()) {
                JDTModelLoader modelLoader = getModelLoader(typeChecker);
                Module module = modelLoader.getLoadedModule(moduleName);
                if (module!=null && module.getVersion().equals(moduleVersion)) {
                    tcs.add(typeChecker);
                }
            }
            return tcs;
        }
        else {
            return singletonList(getProjectTypeChecker(project));
        }
    }
    
    public static Declaration getDeclarationInUnit(String qualifiedName, Unit unit) {
        for (Declaration d: unit.getDeclarations()) {
            String qn = d.getQualifiedNameString();
            if (qn.equals(qualifiedName)) {
                return d;
            }
            else if (qualifiedName.startsWith(qn)) {
                //TODO: I have to do this because of the
                //      shortcut refinement syntax, but
                //      I guess that's really a bug in
                //      the typechecker!
                for (Declaration m: d.getMembers()) {
                    if (m.getQualifiedNameString().equals(qualifiedName)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    
}
