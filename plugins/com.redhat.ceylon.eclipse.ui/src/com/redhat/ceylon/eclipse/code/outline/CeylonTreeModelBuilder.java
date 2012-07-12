package com.redhat.ceylon.eclipse.code.outline;

import java.util.Stack;

import org.eclipse.imp.core.ErrorHandler;

import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SyntheticVariable;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class CeylonTreeModelBuilder {
	
	public void visitTree(Object root) {
		if (root==null) return;
		Tree.CompilationUnit rootNode = (Tree.CompilationUnit) root;
		PackageNode pn = new PackageNode(null);
		Unit unit = rootNode.getUnit();
		if (unit==null) {
			//This was necessary because sometimes IMP
			//was sending us the AST before we had
			//finished properly parsing it ... but I
			//think I've fixed that by changes to
			//CeylonParseController
			return;
		}
		else {
			Package pkg = unit.getPackage();
			pn.setPackageName(pkg.getQualifiedNameString());
		}
		createSubItem(pn);
		//createSubItem(rootNode);
		/*pushSubItem(rootNode.getImportList());
		for (Tree.Import i: rootNode.getImportList().getImports()) {
			createSubItem(i);
		}
		popSubItem();*/
 		CeylonModelVisitor visitor = new CeylonModelVisitor(); 		
		rootNode.visit(visitor);
	}

	public class CeylonModelVisitor extends Visitor {
		// set to true to get nodes for everything in the outline
		private static final boolean INCLUDEALL = false;
		
		@Override
		public void visitAny(Node that) {
			if (that instanceof Tree.Declaration && 
					!(that instanceof Tree.Parameter) &&
					!(that instanceof Tree.TypeParameterDeclaration) &&
					!(that instanceof Tree.TypeConstraint) &&
					!(that instanceof Tree.Variable && 
							((Tree.Variable) that).getType() instanceof SyntheticVariable)) {
				pushSubItem(that);
				super.visitAny(that);
				popSubItem();
			}
			else if (that instanceof Tree.ImportList) {
			    if (!((Tree.ImportList) that).getImports().isEmpty()) {
                    pushSubItem(that);
                    super.visitAny(that);
                    popSubItem();
			    }
			}
            else if (that instanceof Tree.Import) {
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

	protected CeylonOutlineNode fModelRoot;

	private Stack<CeylonOutlineNode> fItemStack= new Stack<CeylonOutlineNode>();

	public final CeylonOutlineNode buildTree(Object rootASTNode) {
		fItemStack.push(fModelRoot= createTopItem(new CeylonOutlineNode(rootASTNode)));
		try {
			visitTree(rootASTNode);
		} catch (Exception e) {
			ErrorHandler.reportError("Exception caught from invocation of language-specific tree model builder implementation", e);
		}
		fItemStack.pop();
		return fModelRoot;
	}


	protected CeylonOutlineNode createTopItem(Object n) {
		return createTopItem(n, CeylonOutlineNode.DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode createTopItem(Object n, int category) {
		CeylonOutlineNode treeNode= new CeylonOutlineNode(n, category);
		return treeNode;
	}

	protected CeylonOutlineNode createSubItem(Object n) {
		return createSubItem(n, CeylonOutlineNode.DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode createSubItem(Object n, int category) {
		final CeylonOutlineNode parent= fItemStack.peek();
		CeylonOutlineNode treeNode= new CeylonOutlineNode(n, parent, category);

		parent.addChild(treeNode);
		return treeNode;
	}

	protected CeylonOutlineNode pushSubItem(Object n) {
		return pushSubItem(n, CeylonOutlineNode.DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode pushSubItem(Object n, int category) {
		return fItemStack.push(createSubItem(n, category));
	}

	protected void popSubItem() {
		fItemStack.pop();
	}
}
