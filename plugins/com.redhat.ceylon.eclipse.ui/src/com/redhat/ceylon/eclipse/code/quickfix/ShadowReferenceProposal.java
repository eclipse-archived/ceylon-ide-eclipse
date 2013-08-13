package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;

import java.util.Collection;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;
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
        if (node instanceof Tree.Variable) { //TODO: handle expressions!
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
}