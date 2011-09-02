package com.redhat.ceylon.eclipse.imp.treeModelBuilder;

import org.eclipse.imp.services.base.TreeModelBuilderBase;

import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SyntheticVariable;

public class CeylonTreeModelBuilder extends TreeModelBuilderBase {
	
	@Override
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
			else if (that instanceof Tree.ImportList || 
			        that instanceof Tree.Import) {
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
	
}
