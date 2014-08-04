package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.Indents;
import com.redhat.ceylon.eclipse.util.Nodes;

class ShadowReferenceProposal extends CorrectionProposal {
    
    ShadowReferenceProposal(int offset, int length, TextChange change) {
        super("Shadow reference inside control structure", change,
                new Region(offset, length));
    }
    
    static void addShadowSwitchReferenceProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.Term) {
            Tree.Statement statement = Nodes.findStatement(cu, node);
            if (statement instanceof Tree.SwitchStatement) {
                String name = Nodes.nameProposals(node)[0];
                TextFileChange change = new TextFileChange("Shadow Reference", file);
                change.setEdit(new MultiTextEdit());
                Integer offset = statement.getStartIndex();
                change.addEdit(new ReplaceEdit(offset, 
                        node.getStartIndex()-offset,
                        "value " + name + " = "));
                IDocument doc = EditorUtil.getDocument(change);
                change.addEdit(new InsertEdit(node.getStopIndex()+1, ";" + 
                        Indents.getDefaultLineDelimiter(doc) + 
                        getIndent(statement, doc) +
                        "switch (" + name));
                if (node instanceof BaseMemberExpression) {
                    Declaration d = ((BaseMemberExpression) node).getDeclaration();
                    if (d!=null) {
                        FindReferencesVisitor frv = new FindReferencesVisitor(d);
                        frv.visit(((Tree.SwitchStatement) statement).getSwitchCaseList());
                        for (Node n: frv.getNodes()) {
                            Node identifyingNode = Nodes.getIdentifyingNode(n);
                            Integer start = identifyingNode.getStartIndex();
                            if (start!=node.getStartIndex()) {
                                change.addEdit(new ReplaceEdit(start, 
                                        identifyingNode.getText().length(), name));
                            }
                        }
                    }
                }
                proposals.add(new ShadowReferenceProposal(offset+6, name.length(), change));
            }
        }
    }
    
    static void addShadowReferenceProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.Variable) {
            Tree.Variable var = (Tree.Variable) node;
            int offset = var.getIdentifier().getStartIndex();
            Tree.Term term = var.getSpecifierExpression().getExpression().getTerm();
			String name = Nodes.nameProposals(term)[0];
            TextChange change = new TextFileChange("Shadow Reference", file);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new InsertEdit(offset, name + " = "));
            Tree.Statement statement = Nodes.findStatement(cu, node);
            FindReferencesVisitor frv = 
            		new FindReferencesVisitor(var.getDeclarationModel());
            frv.visit(statement);
            for (Node n: frv.getNodes()) {
                Node identifyingNode = Nodes.getIdentifyingNode(n);
                Integer start = identifyingNode.getStartIndex();
                if (start!=offset) {
                    change.addEdit(new ReplaceEdit(start, 
                            identifyingNode.getText().length(), name));
                }
            }
            proposals.add(new ShadowReferenceProposal(offset, 1, change));
        }
        else if (node instanceof Tree.Term) {
            String name = Nodes.nameProposals(node)[0];
            TextChange change = new TextFileChange("Shadow Reference", file);
//            change.setEdit(new MultiTextEdit());
            Integer offset = node.getStartIndex();
            change.setEdit(new InsertEdit(offset, name + " = "));
            proposals.add(new ShadowReferenceProposal(offset, 1, change));
        }
    }
}