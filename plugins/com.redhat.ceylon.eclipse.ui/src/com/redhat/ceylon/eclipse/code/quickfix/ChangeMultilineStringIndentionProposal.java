package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.quickfix.FindIndentationVisitor.fixIndent;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class ChangeMultilineStringIndentationProposal 
        extends ChangeCorrectionProposal {

    public static void addFixMultilineStringIndentation(Collection<ICompletionProposal> proposals, 
    		IFile file, Tree.CompilationUnit cu, Tree.StringLiteral literal) {
        int offset = literal.getStartIndex();
        int length = literal.getStopIndex() - literal.getStartIndex() + 1; 
        String text = fixIndent(cu, literal, literal.getToken().getText(), 
        		offset, length, "");
        if (text!=null) {
            TextFileChange change = 
            		new TextFileChange("Fix multiline string indentation", file);
            change.setEdit(new ReplaceEdit(offset, length, text));
            ChangeMultilineStringIndentationProposal proposal = 
            		new ChangeMultilineStringIndentationProposal(change);
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }
        }
    }
    
    private ChangeMultilineStringIndentationProposal(TextFileChange change) {
        super(change.getName(), change);
    }

}