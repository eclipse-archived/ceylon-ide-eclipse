package com.redhat.ceylon.eclipse.imp.treeModelBuilder;

import org.eclipse.imp.services.base.TreeModelBuilderBase;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SyntheticVariable;

public class CeylonTreeModelBuilder extends TreeModelBuilderBase {
	
	@Override
	public void visitTree(Object root) {
		if (root == null)
			return;
		Node rootNode = (Node) root;
		PackageNode pn = new PackageNode(null);
		pn.setPackageName(rootNode.getUnit().getPackage().getQualifiedNameString());
		createSubItem(pn);
		//createSubItem(rootNode);
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
				if (that instanceof Tree.Block) {
					
				}
				else {
					pushSubItem(that);
					super.visitAny(that);
					popSubItem();
				}
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
