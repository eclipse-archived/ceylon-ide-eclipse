/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Value;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;

@Deprecated
class ConvertToGetterProposal extends CorrectionProposal {
    
    ConvertToGetterProposal(Declaration dec, int offset, 
            TextChange change) {
        super("Convert '" + dec.getName() + "' to getter", 
                change, new Region(offset, 0));
    }
    
    static void addConvertToGetterProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.AttributeDeclaration decNode) {
        Value dec = decNode.getDeclarationModel();
        final SpecifierOrInitializerExpression sie = 
                decNode.getSpecifierOrInitializerExpression();
        if (dec!=null && sie!=null) {
            if (dec.isParameter()) return;
            if (!dec.isVariable()) { //TODO: temp restriction, autocreate setter!
                TextChange change = new TextFileChange("Convert to Getter", file);
                change.setEdit(new MultiTextEdit());
                Integer offset = sie.getStartIndex();
                String space;
                try {
                    space = doc.getChar(offset-1)==' ' ? "" : " ";
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                    return;
                }
                change.addEdit(new ReplaceEdit(offset, 1, "=>"));
                // change.addEdit(new ReplaceEdit(offset, 1, space + "{ return" + spaceAfter));
                // change.addEdit(new InsertEdit(decNode.getStopIndex()+1, " }"));
                proposals.add(new ConvertToGetterProposal(dec, 
                        offset + space.length() + 2 , change));
            }
        }
    }
    
}