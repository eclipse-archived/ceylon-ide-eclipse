package com.redhat.ceylon.eclipse.code.correct;

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

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.FindUtils.getAbstraction;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRenameLinkedMode;


//TODO: implement preview, like for other linked modes
class EnterAliasLinkedMode extends AbstractRenameLinkedMode {

	private final ImportMemberOrType element;
	private final Declaration dec;

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
		public void visit(Tree.StaticMemberOrTypeExpression that) {
		    super.visit(that);
		    addLinkedPosition(document, that.getIdentifier(), 
		            that.getDeclaration());
		}
		
		@Override
		public void visit(Tree.SimpleType that) {
		    super.visit(that);
		    addLinkedPosition(document, that.getIdentifier(), 
		            that.getDeclarationModel());
		}

        @Override
        public void visit(Tree.MemberLiteral that) {
            super.visit(that);
            addLinkedPosition(document, that.getIdentifier(), 
                    that.getDeclaration());
        }
        
		protected void addLinkedPosition(final IDocument document,
		        Identifier id, Declaration d) {
		    if (id!=null && d!=null && dec.equals(getAbstraction(d))) {
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
		super(editor);
		this.element = element;
		this.dec = dec;
	}

	@Override
	protected String getName() {
		Tree.Alias alias = element.getAlias();
		if (alias==null) {
			return dec.getName();
		}
		else {
			return alias.getIdentifier().getText();
		}
	}

	@Override
	public String getHintTemplate() {
		return "Enter alias for " + linkedPositionGroup.getPositions().length + 
		        " occurrences of '" + dec.getName() + "' {0}";
	}

	@Override
	protected int init(IDocument document) {
		Tree.Alias alias = ((Tree.ImportMemberOrType) element).getAlias();
		if (alias==null) {
			try {
		        int start = element.getStartIndex();
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
	protected int getIdentifyingOffset() {
		Tree.Alias alias = element.getAlias();
		if (alias!=null) {
			return alias.getStartIndex();
		}
		else {
			return getIdentifyingNode(element).getStartIndex();
		}
	}
	
	@Override
	public void addLinkedPositions(final IDocument document, Tree.CompilationUnit rootNode, 
			final int adjust, final LinkedPositionGroup linkedPositionGroup) {
		rootNode.visit(new LinkedPositionsVisitor(adjust, document, linkedPositionGroup));
	}
	
}
