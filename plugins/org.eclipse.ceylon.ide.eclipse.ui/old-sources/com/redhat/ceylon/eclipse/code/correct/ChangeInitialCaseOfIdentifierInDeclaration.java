/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static java.lang.Character.charCount;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toChars;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Identifier;

public class ChangeInitialCaseOfIdentifierInDeclaration 
        extends CorrectionProposal {

    public static void addChangeIdentifierCaseProposal(Node node, 
            Collection<ICompletionProposal> proposals, IFile file) {
        Tree.Identifier identifier = null;
        
        if (node instanceof Tree.Declaration) {
            Tree.Declaration td = 
                    (Tree.Declaration) node;
            Identifier id = td.getIdentifier();
            if (!id.getText().isEmpty()) {
                identifier = id;
            }
        }
        else if (node instanceof Tree.ImportPath) {
            Tree.ImportPath ip = (Tree.ImportPath) node;
            List<Identifier> id = ip.getIdentifiers();
            for (Identifier importIdentifier : id) {
                String text = importIdentifier.getText();
                if (text != null && !text.isEmpty() && 
                        Character.isUpperCase(text.charAt(0))) {
                    identifier = importIdentifier;
                    break;
                }
            }
        }
        
        if (identifier != null) {
            addProposal(identifier, proposals, file);
        }
    }
    
    private static void addProposal(Identifier identifier, 
            Collection<ICompletionProposal> proposals, 
            IFile file) {
        String oldIdentifier = identifier.getText();
        int first = oldIdentifier.codePointAt(0);
        int newFirst = 
                isUpperCase(first) ? 
                        toLowerCase(first) : 
                        toUpperCase(first);
        String newFirstLetter = new String(toChars(newFirst));
        String newIdentifier = newFirstLetter + 
                oldIdentifier.substring(charCount(first));
        
        TextFileChange change = 
                new TextFileChange(
                        "Change initial case of identifier", 
                        file);
        change.setEdit(new ReplaceEdit(
                identifier.getStartIndex(), 1, 
                newFirstLetter));

        ChangeInitialCaseOfIdentifierInDeclaration proposal = 
                new ChangeInitialCaseOfIdentifierInDeclaration(
                        newIdentifier, change);
        if (!proposals.contains(proposal)) {
            proposals.add(proposal);
        }
    }

    public ChangeInitialCaseOfIdentifierInDeclaration(
            String newIdentifier, Change change) {
        super("Change initial case of identifier to '" + 
            newIdentifier + "'", change, null);
    }

}