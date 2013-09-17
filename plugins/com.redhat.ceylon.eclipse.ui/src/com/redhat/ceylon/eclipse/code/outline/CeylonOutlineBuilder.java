package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.DEFAULT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.IMPORT_LIST_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.PACKAGE_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.ROOT_CATEGORY;

import java.util.Stack;

import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SyntheticVariable;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class CeylonOutlineBuilder {
	
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
		String filename = rootNode.getUnit().getFilename();
		if (!filename.equals("module.ceylon") &&
		    !filename.equals("package.ceylon")) {
			createSubItem(pn, PACKAGE_CATEGORY);
		}
		//createSubItem(rootNode);
		/*pushSubItem(rootNode.getImportList());
		for (Tree.Import i: rootNode.getImportList().getImports()) {
			createSubItem(i);
		}
		popSubItem();*/
 		rootNode.visit(new CeylonModelVisitor());
	}

	public class CeylonModelVisitor extends Visitor {
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

	protected CeylonOutlineNode fModelRoot;

	private Stack<CeylonOutlineNode> fItemStack= new Stack<CeylonOutlineNode>();

	public final CeylonOutlineNode buildTree(Node rootASTNode) {
		fItemStack.push(fModelRoot=createTopItem(rootASTNode, ROOT_CATEGORY));
		try {
			visitTree(rootASTNode);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		fItemStack.pop();
		return fModelRoot;
	}


	protected CeylonOutlineNode createTopItem(Node n) {
		return createTopItem(n, DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode createTopItem(Node n, int category) {
		return new CeylonOutlineNode(n, category);
	}

	protected CeylonOutlineNode createSubItem(Node n) {
		return createSubItem(n, DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode createSubItem(Node n, int category) {
		final CeylonOutlineNode parent= fItemStack.peek();
		CeylonOutlineNode treeNode= new CeylonOutlineNode(n, parent, category);
		parent.addChild(treeNode);
		return treeNode;
	}

	protected CeylonOutlineNode pushSubItem(Node n) {
		return pushSubItem(n, DEFAULT_CATEGORY);
	}

	protected CeylonOutlineNode pushSubItem(Node n, int category) {
		return fItemStack.push(createSubItem(n, category));
	}

	protected void popSubItem() {
		fItemStack.pop();
	}
}
