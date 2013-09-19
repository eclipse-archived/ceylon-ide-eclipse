package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.DEFAULT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.IMPORT_LIST_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.PACKAGE_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.ROOT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.UNIT_CATEGORY;

import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SyntheticVariable;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CeylonOutlineBuilder {
	
	void visitTree(Object root) {
		if (root==null) return;
		Tree.CompilationUnit rootNode = (Tree.CompilationUnit) root;
		Unit unit = rootNode.getUnit();
		if (unit==null) {
			//This was necessary because sometimes IMP
			//was sending us the AST before we had
			//finished properly parsing it ... but I
			//think I've fixed that by changes to
			//CeylonParseController
			return;
		}
		if (!unit.getFilename().equals("module.ceylon") &&
		    !unit.getFilename().equals("package.ceylon")) { //it looks a bit funny to have two nodes representing the package
	        PackageNode packageNode = new PackageNode();
	        packageNode.setPackageName(unit.getPackage().getQualifiedNameString());
		    createSubItem(packageNode, PACKAGE_CATEGORY, 
		            file==null ? null : file.getParent());
		}
		createSubItem(rootNode, UNIT_CATEGORY, file);
 		rootNode.visit(new CeylonModelVisitor());
	}

	private class CeylonModelVisitor extends Visitor {
		// set to true to get nodes for everything in the outline
		private static final boolean INCLUDEALL = false;
		
		@Override
		public void visitAny(Node that) {
		    if (that instanceof Tree.Parameter) {
		        //don't make a node
		    }
		    else if (that instanceof Tree.Declaration && 
					!(that instanceof Tree.TypeParameterDeclaration) &&
					!(that instanceof Tree.TypeConstraint) &&
					!(that instanceof Tree.Variable && 
							((Tree.Variable) that).getType() instanceof SyntheticVariable)) {
				pushSubItem(that);
				super.visitAny(that);
				popSubItem();
			}
            else if (that instanceof Tree.PackageDescriptor ||
            		that instanceof Tree.ModuleDescriptor) {
                pushSubItem(that);
                super.visitAny(that);
                popSubItem();
            }
			else if (that instanceof Tree.ImportList) {
			    if (!((Tree.ImportList) that).getImports().isEmpty()) {
                    pushSubItem(that, IMPORT_LIST_CATEGORY);
                    super.visitAny(that);
                    popSubItem();
			    }
			}
            else if (that instanceof Tree.Import) {
                pushSubItem(that);
                super.visitAny(that);
                popSubItem();
            }
            else if (that instanceof Tree.ImportModule) {
                pushSubItem(that);
                super.visitAny(that);
                popSubItem();
            }
			else {
				if (INCLUDEALL) {
					pushSubItem(that,-1);
					super.visitAny(that);
					popSubItem();
				} 
				else {
					super.visitAny(that);
				}
			}
			
		}
		
	}

	private CeylonOutlineNode modelRoot;
	private IFile file;

	private Stack<CeylonOutlineNode> itemStack= new Stack<CeylonOutlineNode>();

	public final CeylonOutlineNode buildTree(CeylonParseController cpc) {
	    file = cpc.getProject()==null || cpc.getPath()==null ? null :
	            cpc.getProject().getFile(cpc.getPath());
	    itemStack.push(modelRoot=createTopItem(cpc.getRootNode()));
		try {
			visitTree(cpc.getRootNode());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		itemStack.pop();
		return modelRoot;
	}
	
	protected CeylonOutlineNode createTopItem(Node node) {
        return new CeylonOutlineNode(node, ROOT_CATEGORY);
	}

	protected CeylonOutlineNode createSubItem(Node n) {
		return createSubItem(n, DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode createSubItem(Node n, int category) {
		return createSubItem(n, category, null);
	}

    protected CeylonOutlineNode createSubItem(Node n, int category, IResource file) {
        final CeylonOutlineNode parent= itemStack.peek();
        CeylonOutlineNode treeNode= new CeylonOutlineNode(n, parent, category, file);
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
