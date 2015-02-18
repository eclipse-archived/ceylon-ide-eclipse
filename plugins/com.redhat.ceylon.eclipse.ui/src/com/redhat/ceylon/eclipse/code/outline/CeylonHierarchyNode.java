package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoNode;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getTypeCheckers;
import static com.redhat.ceylon.eclipse.util.Nodes.getCompilationUnit;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static java.util.Collections.singletonList;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonHierarchyNode implements Comparable<CeylonHierarchyNode>{
    
    private final String qualifiedName;
    private final String unitName;
    private final String packageName;
    private final String moduleName;
    private final String name;
    
    private final SoftReference<Declaration> declaration;
    
    private boolean nonUnique;
    private boolean multiple;
    
    //private final CeylonHierarchyNode parent;
    private final List<CeylonHierarchyNode> children = 
            new ArrayList<CeylonHierarchyNode>();
    
    public CeylonHierarchyNode(Declaration declaration) {
        this.name = declaration.getName();
        this.qualifiedName = declaration.getQualifiedNameString();
        //TODO: persist the signature somehow, to handle overloads
        Unit unit = declaration.getUnit();
        this.unitName = unit.getFilename();
        this.packageName = unit.getPackage().getNameAsString();
        this.moduleName = unit.getPackage().getModule().getNameAsString();
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
            CompilationUnit rootNode = 
                    ((CeylonEditor) part).getParseController()
                            .getRootNode();
            if (rootNode!=null && rootNode.getUnit()!=null) {
                Unit unit = rootNode.getUnit();
                if (unit.getPackage().getNameAsString().equals(packageName)) {
                    Declaration result = 
                            getDeclarationInUnit(qualifiedName, unit);
                    if (result!=null) {
                        return result;
                    }
                }
            }
        }
        TypeChecker tc = getTypeChecker(project, moduleName).get(0);
        Package pack = getModelLoader(tc)
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

    static Declaration getDeclarationInUnit(String qualifiedName, Unit unit) {
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

    //TODO: review this code with David:
    void gotoHierarchyDeclaration(IProject project,
            //optional:
            CeylonParseController cpc) {
        Declaration dec = getDeclaration(project);
        if (dec!=null) {
            //TODO: this is broken for Java declarations
            Tree.CompilationUnit cu = getCompilationUnit(dec, cpc);
            if (cu!=null) {
                Node refNode = getReferencedNode(dec, cu);
                if (refNode!=null) {
                    gotoNode(refNode, project);
                }
            }
            else {
                gotoJavaNode(dec);
            }
        }
    }
    
}
