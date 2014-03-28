package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class ConvertToSpecifierProposal extends CorrectionProposal {
    
    ConvertToSpecifierProposal(int offset, TextChange change) {
        super("Convert block to =>", change, new Point(offset, 0));
    }
    
    static void addConvertToSpecifierProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.Block block) {
        if (block.getStatements().size()==1) {
            Tree.Statement s = block.getStatements().get(0);
            Node end = null;
            Node start = null;
            if (s instanceof Tree.Return) {
                start = ((Tree.Return) s).getExpression();
                end = start;
            }
            else if (s instanceof Tree.ExpressionStatement) {
                start = ((Tree.ExpressionStatement) s).getExpression();
                end = start;
            }
            else if (s instanceof Tree.SpecifierStatement) {
                start = ((Tree.SpecifierStatement) s).getBaseMemberExpression();
                end = ((Tree.SpecifierStatement) s).getSpecifierExpression();
            }
            if (end!=null) {
                TextChange change = new TextFileChange("Convert to Specifier", file);
                change.setEdit(new MultiTextEdit());
                Integer offset = block.getStartIndex();
                String es;
                try {
                    es = doc.get(start.getStartIndex(), end.getStopIndex()-start.getStartIndex()+1);
                } 
                catch (BadLocationException ex) {
                    ex.printStackTrace();
                    return;
                }
                change.addEdit(new ReplaceEdit(offset, block.getStopIndex()-offset+1, 
                        "=> " + es + ";"));
                proposals.add(new ConvertToSpecifierProposal(offset+2 , change));
            }
        }
    }
    
}