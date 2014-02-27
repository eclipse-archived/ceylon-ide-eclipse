package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;

class ConvertToGetterProposal extends CorrectionProposal {
    
    private final int offset; 
    private final IFile file;
    
    ConvertToGetterProposal(Declaration dec, int offset, 
            IFile file, TextChange change) {
        super("Convert '" + dec.getName() + "' to getter", change);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        EditorUtil.gotoLocation(file, offset);
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
                TextChange change = new TextFileChange("Convert To Getter", file);
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
                        offset + space.length() + 2 , file, change));
            }
        }
    }
    
}