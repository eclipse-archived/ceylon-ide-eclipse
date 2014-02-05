package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;

class ChangeDeclarationProposal extends ChangeCorrectionProposal {
    
    final IFile file;
    final int offset;
    final int length;
    
    ChangeDeclarationProposal(String kw, CommonToken token, 
            IFile file, TextFileChange change) {
        super("Change declaration to '" + kw + "'", change);
        this.file=file;
        this.offset=token.getStartIndex();
        this.length=kw.length();
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addChangeDeclarationProposal(ProblemLocation problem, IFile file,
            Collection<ICompletionProposal> proposals, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        CommonToken token = (CommonToken) decNode.getMainToken();
        String keyword;
        if (decNode instanceof Tree.AnyClass){
            keyword = "interface";
        }
        else if (decNode instanceof Tree.AnyMethod) {
            if (token.getText().equals("void")) return;
            keyword = "value";
        }
        else {
            return;
        }
        TextFileChange change = new TextFileChange("Change Declaration", file);
        change.setEdit(new ReplaceEdit(token.getStartIndex(), token.getText().length(), 
                keyword));
        proposals.add(new ChangeDeclarationProposal(keyword, token, file, change));
    }
}