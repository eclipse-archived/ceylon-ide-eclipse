package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getTypeCheckers;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class CeylonHierarchyNode implements Comparable<CeylonHierarchyNode>{
    
    private final String qualifiedName;
    private final String unitName;
    private final String packageName;
    private final String moduleName;
    private final String name;
    
    private boolean nonUnique;
    private boolean multiple;
    
    //private final CeylonHierarchyNode parent;
    private final List<CeylonHierarchyNode> children = 
            new ArrayList<CeylonHierarchyNode>();
    
    CeylonHierarchyNode(Declaration declaration) {
        this.name = declaration.getName();
        this.qualifiedName = declaration.getQualifiedNameString();
        Unit unit = declaration.getUnit();
        this.unitName = unit.getFilename();
        this.packageName = unit.getPackage().getNameAsString();
        this.moduleName = unit.getPackage().getModule().getNameAsString();
    }
    
    public Declaration getDeclaration(IProject project) {
        TypeChecker tc = getTypeChecker(project, moduleName).get(0);
        Package pack = getModelLoader(tc)
                .getLoadedModule(moduleName)
                .getPackage(packageName);
        for (Unit unit: pack.getUnits()) {
            if (unit.getFilename().equals(unitName)) {
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
            }
        }
        //apparently the above approach doesn't work for Java modules
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

    public static List<TypeChecker> getTypeChecker(IProject project, String moduleName) {
        if (project==null) {
            List<TypeChecker> tcs = new ArrayList<TypeChecker>();
            for (TypeChecker tc: getTypeCheckers()) {
                if (getModelLoader(tc).getLoadedModule(moduleName)!=null) {
                    tcs.add(tc);
                }
            }
            return tcs;
        }
        else {
            return singletonList(getProjectTypeChecker(project));
        }
    }
    
    boolean isNonUnique() {
        return nonUnique;
    }
    
    void setNonUnique(boolean nonUnique) {
        this.nonUnique = nonUnique;
    }
    
    void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
    
    public boolean isMultiple() {
        return multiple;
    }
    
    public String getName() {
        return name;
    }
    
    /*CeylonHierarchyNode(Declaration declaration, CeylonHierarchyNode parent) {
        this.declaration = declaration;
        this.parent = parent;
    }*/
    
    void addChild(CeylonHierarchyNode child) {
        if (!children.contains(child)) children.add(child);
    }
    
    public List<CeylonHierarchyNode> getChildren() {
        return children;
    }
    
    /*public CeylonHierarchyNode getParent() {
        return parent;
    }*/
    
    @Override
    public boolean equals(Object obj) {
        if (this==obj) {
            return true;
        }
        else if (obj==null) {
            return false;
        }
        else if (obj instanceof CeylonHierarchyNode) {
            return qualifiedName.equals(((CeylonHierarchyNode) obj).qualifiedName);
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

    @Override
    public int compareTo(CeylonHierarchyNode node) {
        if (node.qualifiedName.equals(qualifiedName)) {
            return 0;
        }
        else {
            int ct = name.compareTo(node.name);
            if (ct!=0) {
                return ct;
            }
            else {
                return qualifiedName.compareTo(node.qualifiedName);
            }
        }
    }
}
