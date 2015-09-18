package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.DEFAULT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.IMPORT_LIST_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.PACKAGE_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.ROOT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.UNIT_CATEGORY;

import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class CeylonOutlineBuilder extends Visitor {
    
    @Override
    public void visit(Tree.Parameter that) {
        //don't make a node
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        if (!(that instanceof Tree.TypeParameterDeclaration) &&
                !(that instanceof Tree.TypeConstraint) &&
                !(that instanceof Tree.Variable/* && 
                        ((Tree.Variable) that).getType() instanceof SyntheticVariable*/)) {
            if (that instanceof Tree.AnyAttribute) {
                Tree.AnyAttribute a = 
                        (Tree.AnyAttribute) that;
                TypedDeclaration att = 
                        a.getDeclarationModel();
                if (att==null || 
                        !att.isShared() && 
                        !att.isToplevel() &&
                        !att.isClassOrInterfaceMember()) {
                    return;
                }
            }
            pushSubItem(that);
            super.visitAny(that);
            popSubItem();
        }
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        Tree.Term bme = that.getBaseMemberExpression();
        if (that.getRefinement() &&
                (bme instanceof Tree.BaseMemberExpression ||
                bme instanceof Tree.ParameterizedExpression &&
                    ((Tree.ParameterizedExpression) bme).getPrimary() 
                            instanceof Tree.BaseMemberExpression)) {
            pushSubItem(that);
            super.visitAny(that);
            popSubItem();
        }
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
        pushSubItem(that);
        super.visitAny(that);
        popSubItem();
    }
    
    @Override
    public void visit(Tree.ModuleDescriptor that) {
        pushSubItem(that);
        super.visitAny(that);
        popSubItem();
    }
    
    @Override
    public void visit(Tree.Import that) {
        pushSubItem(that);
        super.visitAny(that);
        popSubItem();
    }
    
    @Override
    public void visit(Tree.ImportModule that) {
        pushSubItem(that);
        super.visitAny(that);
        popSubItem();
    }
    
    @Override
    public void visit(Tree.ImportList that) {
        Tree.ImportList il = (Tree.ImportList) that;
        if (!il.getImports().isEmpty()) {
            pushSubItem(that, IMPORT_LIST_CATEGORY);
            super.visitAny(that);
            popSubItem();
        }
    }
    
    private Stack<CeylonOutlineNode> itemStack = 
            new Stack<CeylonOutlineNode>();
    
    public final CeylonOutlineNode buildTree(CeylonParseController cpc) {
        if (cpc==null) return null;
        Tree.CompilationUnit rootNode = cpc.getLastCompilationUnit();
        if (rootNode==null) return null;
        TypecheckerUnit u = rootNode.getUnit();
        if (u==null) return null;
        if (rootNode.getStartIndex()==null) return null;
        
        IProject project = cpc.getProject();
        IPath path = cpc.getPath();
        IFile file = 
                project==null || path==null ? null :
                    project.getFile(path);
        if (u instanceof CeylonUnit) {
            CeylonUnit unit = (CeylonUnit) u;
            PhasedUnit phasedUnit = unit.getPhasedUnit();
            if (phasedUnit == null || 
                    !phasedUnit.isFullyTyped()) {
                return null;
            }
        } 
        CeylonOutlineNode modelRoot = 
                createTopItem(rootNode, file);
        itemStack.push(modelRoot);
        try {
            Unit unit = rootNode.getUnit();
            if (unit!=null && 
                !unit.getFilename()
                    .equals("module.ceylon")) { //it looks a bit funny to have two nodes representing the module
                ModuleNode moduleNode = new ModuleNode();
                Module module = unit.getPackage().getModule();
                String mname = module.getNameAsString();
                moduleNode.setModuleName(mname);
                moduleNode.setVersion(module.getVersion());
                createSubItem(moduleNode, PACKAGE_CATEGORY, 
                        file==null ? null : file.getParent());
            }
            if (unit!=null && 
                !unit.getFilename()
                    .equals("module.ceylon") &&
                !unit.getFilename()
                    .equals("package.ceylon")) { //it looks a bit funny to have two nodes representing the package
                PackageNode packageNode = new PackageNode();
                String pname = 
                        unit.getPackage()
                            .getQualifiedNameString();
                packageNode.setPackageName(pname);
                createSubItem(packageNode, PACKAGE_CATEGORY, 
                        file==null ? null : file.getParent());
            }
            createSubItem(rootNode, UNIT_CATEGORY, file);
            rootNode.visit(this);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        itemStack.pop();
        return modelRoot;
    }
    
    private CeylonOutlineNode createTopItem(Node node, IFile file) {
        return new CeylonOutlineNode(node, ROOT_CATEGORY, file);
    }

    /*private CeylonOutlineNode createSubItem(Node n) {
        return createSubItem(n, DEFAULT_CATEGORY);
    }*/

    private CeylonOutlineNode createSubItem(Node n, int category) {
        return createSubItem(n, category, null);
    }

    private CeylonOutlineNode createSubItem(Node n, int category, 
            IResource file) {
        CeylonOutlineNode parent = itemStack.peek();
        CeylonOutlineNode treeNode = 
                new CeylonOutlineNode(n, parent, category, file);
        parent.addChild(treeNode);
        return treeNode;
    }

    private CeylonOutlineNode pushSubItem(Node n) {
        return pushSubItem(n, DEFAULT_CATEGORY);
    }

    private CeylonOutlineNode pushSubItem(Node n, int category) {
        return itemStack.push(createSubItem(n, category));
    }

    private void popSubItem() {
        itemStack.pop();
    }
}
