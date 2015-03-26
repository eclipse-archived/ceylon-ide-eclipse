package com.redhat.ceylon.eclipse.code.correct;

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

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;

public class ChangeInitialCaseOfIdentifierInDeclaration 
        extends CorrectionProposal {

    public static void addChangeIdentifierCaseProposal(Node node, 
            Collection<ICompletionProposal> proposals, IFile file) {
        Tree.Identifier identifier = null;
        
        if (node instanceof Tree.TypeDeclaration) {
            identifier = ((Tree.TypeDeclaration) node).getIdentifier();
        }
        else if (node instanceof Tree.TypeParameterDeclaration) {
            identifier = ((Tree.TypeParameterDeclaration) node).getIdentifier();
        }
        else if (node instanceof Tree.TypedDeclaration) {
            identifier = ((Tree.TypedDeclaration) node).getIdentifier();
        }
        else if (node instanceof Tree.ImportPath) {
            List<Identifier> importIdentifiers = ((Tree.ImportPath) node).getIdentifiers();
            for (Identifier importIdentifier : importIdentifiers) {
                if (importIdentifier.getText() != null && 
                        !importIdentifier.getText().isEmpty() && 
                        Character.isUpperCase(importIdentifier.getText().charAt(0))) {
                    identifier = importIdentifier;
                    break;
                }
            }
        }
        
        if (identifier != null && !identifier.getText().isEmpty()) {
            addProposal(identifier, proposals, file);
        }
    }
    
    private static void addProposal(Identifier identifier, 
            Collection<ICompletionProposal> proposals, IFile file) {
        String oldIdentifier = identifier.getText();
        int first = oldIdentifier.codePointAt(0);
        int newFirst = isUpperCase(first) ? 
                toLowerCase(first) : toUpperCase(first);
        String newFirstLetter = new String(toChars(newFirst));
        String newIdentifier = newFirstLetter + 
                oldIdentifier.substring(charCount(first));
        
        TextFileChange change = new TextFileChange("Change initial case of identifier", file);
        change.setEdit(new ReplaceEdit(identifier.getStartIndex(), 1, newFirstLetter));

        ChangeInitialCaseOfIdentifierInDeclaration proposal = 
                new ChangeInitialCaseOfIdentifierInDeclaration(newIdentifier, change);
        if (!proposals.contains(proposal)) {
            proposals.add(proposal);
        }
    }

    public ChangeInitialCaseOfIdentifierInDeclaration(String newIdentifier, Change change) {
        super("Change initial case of identifier to '" + newIdentifier + "'", change, null);
    }

}