package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.util.FindStatementVisitor;

class ShadowReferenceProposal extends ChangeCorrectionProposal {
    
    final IFile file;
    final int offset;
    final int length;
    
    ShadowReferenceProposal(int offset, int length, IFile file, 
            TextChange change) {
        super("Shadow reference inside control structure", change, 10, CORRECTION);
        this.file=file;
        this.offset=offset;
        this.length=length;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addShadowReferenceProposal(IDocument doc, IFile file,
            Tree.CompilationUnit cu, Collection<ICompletionProposal> proposals, 
            Node node) {
        Tree.Variable var = (Tree.Variable) node;
        int offset = var.getIdentifier().getStartIndex();
        TextChange change = new DocumentChange("Shadow Reference", doc);
        change.setEdit(new MultiTextEdit());
        String name = Character.toString(var.getIdentifier().getText().charAt(0));
        change.addEdit(new InsertEdit(offset, name + " = "));
        FindStatementVisitor fsv = new FindStatementVisitor(var/*.getSpecifierExpression().getExpression().getTerm()*/, false);
        fsv.visit(cu);
        FindReferenceVisitor frv = new FindReferenceVisitor(var.getDeclarationModel());
        frv.visit(fsv.getStatement());
        for (Node n: frv.getNodes()) {
            Node identifyingNode = getIdentifyingNode(n);
            Integer start = identifyingNode.getStartIndex();
            if (start!=offset) {
                change.addEdit(new ReplaceEdit(start, 
                        identifyingNode.getText().length(), name));
            }
        }
        proposals.add(new ShadowReferenceProposal(offset, 1, file, change));
    }
}