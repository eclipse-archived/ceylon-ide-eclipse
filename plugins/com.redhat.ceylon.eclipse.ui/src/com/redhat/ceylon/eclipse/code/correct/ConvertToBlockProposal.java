package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class ConvertToBlockProposal extends CorrectionProposal {
    
    ConvertToBlockProposal(int offset, TextChange change) {
        super("Convert => to block", change, new Region(offset, 0));
    }
    
    static void addConvertToBlockProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.LazySpecifierExpression spec, Node decNode) {
        TextChange change = new TextFileChange("Convert to Block", file);
        change.setEdit(new MultiTextEdit());
        Integer offset = spec.getStartIndex();
        String space;
        String spaceAfter;
        try {
            space = doc.getChar(offset-1)==' ' ? "" : " ";
            spaceAfter = doc.getChar(offset+2)==' ' ? "" : " ";
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return;
        }
        boolean isVoid;
        String addedKeyword = null;
        if (decNode instanceof Tree.Declaration) {
            Declaration dm = ((Tree.Declaration) decNode).getDeclarationModel();
            if (dm==null || dm.isParameter()) return;
            isVoid = dm instanceof Setter ||
                    dm instanceof Method && ((Method) dm).isDeclaredVoid();
        }
        else if (decNode instanceof Tree.MethodArgument) {
            Tree.MethodArgument ma = (Tree.MethodArgument) decNode;
            Method dm = ma.getDeclarationModel();
            if (dm==null) return;
            isVoid = dm.isDeclaredVoid();
            if (ma.getType().getToken()==null) {
                addedKeyword = "function ";
            }
        }
        else if (decNode instanceof Tree.AttributeArgument) {
            Tree.AttributeArgument aa = (Tree.AttributeArgument) decNode;
            isVoid = false;            
            if (aa.getType().getToken()==null) {
                addedKeyword = "value ";
            }
        }
        else if (decNode instanceof Tree.FunctionArgument) {
            Method dm = ((Tree.FunctionArgument) decNode).getDeclarationModel();
            if (dm==null) return;
            isVoid = dm.isDeclaredVoid();
        }
        else {
            return;
        }
        if (addedKeyword!=null) {
            change.addEdit(new InsertEdit(decNode.getStartIndex(), addedKeyword));
        }
        change.addEdit(new ReplaceEdit(offset, 2, space + (isVoid?"{":"{ return") + spaceAfter));
        change.addEdit(new InsertEdit(decNode.getStopIndex()+1, " }"));
        proposals.add(new ConvertToBlockProposal(offset + space.length() + 2 , change));
    }

}