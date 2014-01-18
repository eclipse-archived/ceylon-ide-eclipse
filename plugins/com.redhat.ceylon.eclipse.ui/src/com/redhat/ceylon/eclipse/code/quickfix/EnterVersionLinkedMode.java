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

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModule;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRenameLinkedMode;
import com.redhat.ceylon.eclipse.code.refactor.RenameVersionRefactoring;


class EnterVersionLinkedMode extends AbstractRenameLinkedMode {

	private final Tree.ImportPath module;
	private final Tree.QuotedLiteral version;
	
	private final RenameVersionRefactoring refactoring;

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
		public void visit(ImportModule that) {
		    super.visit(that);
		    addLinkedPosition(document, that.getVersion(), 
		            that.getImportPath());
		}
		
		boolean eq(Tree.ImportPath x, Tree.ImportPath y) {
			List<Identifier> xids = x.getIdentifiers();
			List<Identifier> yids = y.getIdentifiers();
			if (xids.size()!=yids.size()) {
				return false;
			}
			for (int i=0; i<xids.size(); i++) {
				if (!xids.get(0).equals(yids.get(i))) {
					return false;
				}
			}
			return true;
		}
		
		protected void addLinkedPosition(final IDocument document,
		        Tree.QuotedLiteral version, Tree.ImportPath path) {
		    if (version!=null && path!=null && eq(module, path)) {
		        try {
		            int pos = version.getStartIndex()+adjust;
					int len = version.getText().length();
					linkedPositionGroup.addPosition(new LinkedPosition(document, 
		                    pos, len, i++));
		        }
		        catch (BadLocationException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}

	public EnterVersionLinkedMode(Tree.QuotedLiteral version, 
			Tree.ImportPath module, CeylonEditor editor) {
		super(editor);
		this.module = module;
		this.version = version;
		this.refactoring = new RenameVersionRefactoring(editor);
	}

	@Override
	protected String getName() {
		return version.getText();
	}

	@Override
	public String getHintTemplate() {
		return "Enter new version for " + linkedPositionGroup.getPositions().length + 
		        " occurrences of '" + getName() + "' {0}";
	}
	
	@Override
	protected int getIdentifyingOffset() {
		return version.getStartIndex();
	}
	
	@Override
	public void addLinkedPositions(final IDocument document, Tree.CompilationUnit rootNode, 
			final int adjust, final LinkedPositionGroup linkedPositionGroup) {
		rootNode.visit(new LinkedPositionsVisitor(adjust, document, linkedPositionGroup));
	}
	
	public boolean isEnabled() {
		return !getNewName().isEmpty();
	}
	
	public void done() {
		if (isEnabled()) {
		    try {
		        editor.doSave(new NullProgressMonitor());
//		        hideEditorActivity();
		        refactoring.setNewName(getNewName());
		        /*revertChanges();
		        if (showPreview) {
		            openPreview();
		        }
		        else {*/
		            new RefactoringExecutionHelper(refactoring,
		                    RefactoringStatus.WARNING,
		                    RefactoringSaveHelper.SAVE_ALL,
		                    editor.getSite().getShell(),
		                    editor.getSite().getWorkbenchWindow())
		                .perform(false, true);
//		        }
		    } 
		    catch (Exception e) {
		        e.printStackTrace();
		    }
		    finally {
//		        unhideEditorActivity();
		    }
		    super.done();
		}
		else {
		    super.cancel();
		}
	}


	
}
