/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil.defaultValue;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.MINOR_CHANGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;

@Deprecated
class AddInitializerProposal extends InitializerProposal {
    
	AddInitializerProposal(String desc, TypedDeclaration dec, int offset, int length,
	        TextChange change) {
        super(desc, 
        		change, dec, dec.getType(), 
        		new Region(offset, length),
        		MINOR_CHANGE, -1);
    }
    
	@Deprecated
    private static void addInitializerProposal(Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.TypedDeclaration decNode, Tree.SpecifierOrInitializerExpression sie) {
        FunctionOrValue dec = (FunctionOrValue) decNode.getDeclarationModel();
        if (dec==null) return;
        if (dec.getInitializerParameter()==null && !dec.isFormal()) {
            TextChange change = new TextFileChange("Add Initializer", file);
            int offset = decNode.getEndIndex()-1;
        	String defaultValue = defaultValue(cu.getUnit(), dec.getType());
            String def;
            int selectionOffset;
            if (decNode instanceof Tree.MethodDeclaration) {
				def = " => " + defaultValue;
				selectionOffset = offset + 4;
            }
            else {
                def = " = " + defaultValue;
                selectionOffset = offset + 3;
            }
            
            change.setEdit(new InsertEdit(offset, def));
            proposals.add(new AddInitializerProposal("", dec, 
                    selectionOffset, defaultValue.length(), 
                    change));
        }
    }

   @Deprecated
	static void addInitializerProposals(Collection<ICompletionProposal> proposals,
			IFile file, Tree.CompilationUnit cu, Node node) {
		if (node instanceof Tree.AttributeDeclaration) {
	        Tree.AttributeDeclaration attDecNode = (Tree.AttributeDeclaration) node;
	        Tree.SpecifierOrInitializerExpression sie = 
	                attDecNode.getSpecifierOrInitializerExpression();
	        if (!(sie instanceof Tree.LazySpecifierExpression)) {
	            addInitializerProposal(cu, proposals, file, attDecNode, sie);
	        }
	    }
	    if (node instanceof Tree.MethodDeclaration) {
	        Tree.MethodDeclaration methDecNode = (Tree.MethodDeclaration) node;
	        Tree.SpecifierExpression sie = methDecNode.getSpecifierExpression();
	        addInitializerProposal(cu, proposals, file, methDecNode, sie);
	    }
	}
    
}