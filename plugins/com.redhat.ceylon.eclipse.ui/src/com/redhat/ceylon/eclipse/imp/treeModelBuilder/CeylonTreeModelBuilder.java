package com.redhat.ceylon.eclipse.imp.treeModelBuilder;

import org.eclipse.imp.services.base.TreeModelBuilderBase;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Annotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Block;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class CeylonTreeModelBuilder extends TreeModelBuilderBase {
	@Override
	public void visitTree(Object root) {
		if (root == null)
			return;
		Node rootNode = (Node) root;
 		CeylonModelVisitor visitor = new CeylonModelVisitor();
 		
		rootNode.visitChildren(visitor);
	}

	public class CeylonModelVisitor extends Visitor {
		// set to true to get nodes for everything in the outline
		private static final boolean INCLUDEALL = false;

		@Override
		public void visitAny(Node that) {
			if(that instanceof Annotation || that instanceof Declaration && 
					!(that instanceof ParameterList)) {
				if(that instanceof Block) {
					
				} else {
					pushSubItem(that);
					super.visitAny(that);
					popSubItem();
				}
			} else {
				if(INCLUDEALL) {
					pushSubItem(that,-1);
					super.visitAny(that);
					popSubItem();
				} else {
					super.visitAny(that);
				}
			}
			
		}
		
	}
}
