package com.redhat.ceylon.eclipse.code.quickfix;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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
import com.redhat.ceylon.eclipse.code.editor.Util;

class ConvertToBlockProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    ConvertToBlockProposal(int offset, IFile file, TextChange change) {
        super("Convert => to block", change);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }

    static void addConvertToBlockProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.LazySpecifierExpression spec, Node decNode) {
        TextChange change = new TextFileChange("Convert To Block", file);
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
            if (dm.isParameter()) return;
            isVoid = dm instanceof Setter ||
                    dm instanceof Method && ((Method) dm).isDeclaredVoid();
        }
        else if (decNode instanceof Tree.MethodArgument) {
            Tree.MethodArgument ma = (Tree.MethodArgument) decNode;
			isVoid = ma.getDeclarationModel().isDeclaredVoid();
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
            isVoid = ((Tree.FunctionArgument) decNode).getDeclarationModel().isDeclaredVoid();
        }
        else {
            return;
        }
        if (addedKeyword!=null) {
        	change.addEdit(new InsertEdit(decNode.getStartIndex(), addedKeyword));
        }
        change.addEdit(new ReplaceEdit(offset, 2, space + (isVoid?"{":"{ return") + spaceAfter));
        change.addEdit(new InsertEdit(decNode.getStopIndex()+1, " }"));
        proposals.add(new ConvertToBlockProposal(offset + space.length() + 2 , file, change));
    }

}