package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.DEFAULT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.IMPORT_LIST_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.PACKAGE_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.ROOT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.UNIT_CATEGORY;

import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SyntheticVariable;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;

public class CeylonOutlineBuilder extends Visitor {
	
    @Override
    public void visit(Tree.Parameter that) {
        //don't make a node
    }

    @Override
    public void visit(Tree.Declaration that) {
        if (!(that instanceof Tree.TypeParameterDeclaration) &&
                !(that instanceof Tree.TypeConstraint) &&
                !(that instanceof Tree.Variable && 
                        ((Tree.Variable) that).getType() instanceof SyntheticVariable)) {
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
        if (!((Tree.ImportList) that).getImports().isEmpty()) {
            pushSubItem(that, IMPORT_LIST_CATEGORY);
            super.visitAny(that);
            popSubItem();
        }
    }

	private Stack<CeylonOutlineNode> itemStack = new Stack<CeylonOutlineNode>();

	public final CeylonOutlineNode buildTree(CeylonParseController cpc) {
	    IFile file = cpc.getProject()==null || cpc.getPath()==null ? null :
	            cpc.getProject().getFile(cpc.getPath());
	    Tree.CompilationUnit rootNode = cpc.getRootNode();
	    if (rootNode==null || 
	        rootNode.getUnit()==null || 
	        rootNode.getStartIndex()==null) {
	        return null;
	    }
	    if (rootNode.getUnit() instanceof CeylonUnit) {
	    	PhasedUnit phasedUnit = ((CeylonUnit) rootNode.getUnit()).getPhasedUnit();
	    	if (phasedUnit == null || ! phasedUnit.isFullyTyped()) {
	    	    return null;
	    	}
        } 
	    CeylonOutlineNode modelRoot = createTopItem(rootNode, file);
	    itemStack.push(modelRoot);
		try {
	        Unit unit = rootNode.getUnit();
	        if (unit!=null && 
	            !unit.getFilename().equals("module.ceylon") &&
	            !unit.getFilename().equals("package.ceylon")) { //it looks a bit funny to have two nodes representing the package
	            PackageNode packageNode = new PackageNode();
	            packageNode.setPackageName(unit.getPackage().getQualifiedNameString());
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
	
	protected CeylonOutlineNode createTopItem(Node node, IFile file) {
        return new CeylonOutlineNode(node, ROOT_CATEGORY, file);
	}

	protected CeylonOutlineNode createSubItem(Node n) {
		return createSubItem(n, DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode createSubItem(Node n, int category) {
		return createSubItem(n, category, null);
	}

    protected CeylonOutlineNode createSubItem(Node n, int category, IResource file) {
        final CeylonOutlineNode parent= itemStack.peek();
        CeylonOutlineNode treeNode = new CeylonOutlineNode(n, parent, category, file);
        parent.addChild(treeNode);
        return treeNode;
    }

	protected CeylonOutlineNode pushSubItem(Node n) {
		return pushSubItem(n, DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode pushSubItem(Node n, int category) {
		return itemStack.push(createSubItem(n, category));
	}

	protected void popSubItem() {
		itemStack.pop();
	}
}
