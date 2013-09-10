package com.redhat.ceylon.eclipse.code.quickfix;

/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;


class EnterAliasLinkedMode extends AbstractRenameLinkedMode {

	private final class LinkedPositionsVisitor 
	        extends Visitor implements NaturalVisitor {
		private final int adjust;
		private final IDocument document;
		private final LinkedPositionGroup linkedPositionGroup;
		int i=1;

		private LinkedPositionsVisitor(int adjust, IDocument document,
				LinkedPositionGroup linkedPositionGroup) {
			this.adjust = adjust;
			this.document = document;
			this.linkedPositionGroup = linkedPositionGroup;
		}

		@Override
		public void visit(BaseMemberOrTypeExpression that) {
		    super.visit(that);
		    addLinkedPosition(document, that.getIdentifier(), 
		            that.getDeclaration());
		}

		@Override
		public void visit(BaseType that) {
		    super.visit(that);
		    addLinkedPosition(document, that.getIdentifier(), 
		            that.getDeclarationModel());
		}

		protected void addLinkedPosition(final IDocument document,
		        Identifier id, Declaration d) {
		    if (id!=null && d!=null && dec.equals(d)) {
		        try {
		            int pos = id.getStartIndex()+adjust;
					int len = id.getText().length();
					linkedPositionGroup.addPosition(new LinkedPosition(document, 
		                    pos, len, i++));
		        }
		        catch (BadLocationException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}

	public EnterAliasLinkedMode(ImportMemberOrType element, 
			Declaration dec, CeylonEditor editor) {
		super(element, dec, editor);
	}

	@Override
	protected String getName(Node node) {
		Tree.Alias alias = ((Tree.ImportMemberOrType) node).getAlias();
		if (alias==null) {
			return super.getName(node);
		}
		else {
			return alias.getIdentifier().getText();
		}
	}

	@Override
	protected String getHintTemplate() {
		return "Enter alias for '" + dec.getName() + "' {0}";
	}

	@Override
	protected int init(Node node, IDocument document) {
		Tree.Alias alias = ((Tree.ImportMemberOrType) node).getAlias();
		if (alias==null) {
			try {
		        int start = node.getStartIndex();
				document.set(document.get(0,start) + dec.getName() + "=" + 
						document.get(start, document.getLength()-start));
				return dec.getName().length()+1;
			}
			catch (BadLocationException e) {
				e.printStackTrace();
				return -1;
			}
		}
		else {
			return 0;
		}
	}
	
	@Override
	protected int getIdentifyingOffset(Node node) {
		Tree.Alias alias = ((Tree.ImportMemberOrType) node).getAlias();
		if (alias!=null) {
			return alias.getStartIndex();
		}
		else {
			return super.getIdentifyingOffset(node);
		}
	}
	
	@Override
	public void addLinkedPositions(final IDocument document, Tree.CompilationUnit rootNode, 
			final int adjust, final LinkedPositionGroup linkedPositionGroup) {
		rootNode.visit(new LinkedPositionsVisitor(adjust, document, linkedPositionGroup));
	}
}
