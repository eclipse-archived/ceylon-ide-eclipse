package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;

class ConvertToGetterProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    ConvertToGetterProposal(Declaration dec, int offset, IFile file, TextChange change) {
        super("Convert '" + dec.getName() + "' to getter", change, 10, CORRECTION);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }

    static void addConvertToGetterProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.AttributeDeclaration decNode) {
        Value dec = decNode.getDeclarationModel();
        if (!dec.isVariable()) { //TODO: temp restriction!
            TextChange change = new DocumentChange("Convert To Getter", doc);
            change.setEdit(new MultiTextEdit());
            Integer offset = decNode.getSpecifierOrInitializerExpression().getStartIndex();
            String space;
            String spaceAfter;
            try {
                space = doc.getChar(offset-1)==' ' ? "" : " ";
                spaceAfter = doc.getChar(offset+1)==' ' ? "" : " ";
            }
            catch (BadLocationException e) {
                e.printStackTrace();
                return;
            }
            change.addEdit(new ReplaceEdit(offset, 1, space + "{ return" + spaceAfter));
            change.addEdit(new InsertEdit(decNode.getStopIndex()+1, " }"));
            proposals.add(new ConvertToGetterProposal(dec, offset + space.length() + 2 , file, change));
        }
    }
    
}