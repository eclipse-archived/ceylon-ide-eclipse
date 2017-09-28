package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findStatement;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.nameProposals;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;
import org.eclipse.ceylon.ide.common.util.FindReferencesVisitor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Value;

class ShadowReferenceProposal extends CorrectionProposal {
    
    ShadowReferenceProposal(int offset, int length, TextChange change) {
        super("Shadow reference inside control structure", change,
                new Region(offset, length));
    }
    
    static void addShadowSwitchReferenceProposal(IFile file,
            Node node, Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals) {
        if (node instanceof Tree.Term) {
            Tree.Statement statement =
                    findStatement(rootNode, node);
            if (statement instanceof Tree.SwitchStatement) {
                String name = nameProposals(node)[0];
                TextFileChange change =
                        new TextFileChange("Shadow Reference", file);
                change.setEdit(new MultiTextEdit());
//                Integer offset = statement.getStartIndex();
//                change.addEdit(new ReplaceEdit(offset,
//                        node.getStartIndex()-offset,
//                        "value " + name + " = "));
//                IDocument doc = getDocument(change);
//                change.addEdit(new InsertEdit(node.getEndIndex(),
//                        ";" +
//                        getDefaultLineDelimiter(doc) +
//                        getIndent(statement, doc) +
//                        "switch (" + name));
                Tree.SwitchStatement ss =
                        (Tree.SwitchStatement) statement;
                int loc = node.getStartIndex();
                change.addEdit(new InsertEdit(loc, name + " = "));
                if (node instanceof BaseMemberExpression) {
                    Tree.BaseMemberExpression bme =
                            (BaseMemberExpression) node;
                    Declaration d = bme.getDeclaration();
                    if (d!=null) {
                        FindReferencesVisitor frv =
                                new FindReferencesVisitor(d);
                        frv.visit(ss.getSwitchCaseList());
                        for (Node n: frv.getNodeSet()) {
                            Node identifyingNode =
                                    getIdentifyingNode(n);
                            Integer start =
                                    identifyingNode.getStartIndex();
                            if (start!=loc) {
                                int len = identifyingNode.getText().length();
                                change.addEdit(new ReplaceEdit(start, len, name));
                            }
                        }
                    }
                }
                proposals.add(new ShadowReferenceProposal(
                        loc, name.length(), change));
            }
        }
    }
    
    static void addShadowReferenceProposal(IFile file,
            Node node, Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals) {
        if (node instanceof Tree.Variable) {
            Tree.Variable var = (Tree.Variable) node;
            int offset =
                    var.getIdentifier()
                        .getStartIndex();
            Tree.Term term =
                    var.getSpecifierExpression()
                        .getExpression()
                        .getTerm();
			String name = nameProposals(term)[0];
            TextChange change =
                    new TextFileChange("Shadow Reference", file);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new InsertEdit(offset, name + " = "));
            Tree.Statement statement =
                    Nodes.findStatement(rootNode, node);
            Value dec = var.getDeclarationModel();
            FindReferencesVisitor frv = 
            		new FindReferencesVisitor(dec);
            frv.visit(statement);
            for (Node n: frv.getNodeSet()) {
                Node identifyingNode = getIdentifyingNode(n);
                Integer start =
                        identifyingNode.getStartIndex();
                if (start!=offset) {
                    int len = identifyingNode.getText().length();
                    change.addEdit(new ReplaceEdit(start, len, name));
                }
            }
            proposals.add(new ShadowReferenceProposal(
                    offset, name.length(), change));
        }
        else if (node instanceof Tree.Term) {
            String name = nameProposals(node)[0];
            TextChange change =
                    new TextFileChange("Shadow Reference", file);
//            change.setEdit(new MultiTextEdit());
            Integer offset = node.getStartIndex();
            change.setEdit(new InsertEdit(offset, name + " = "));
            proposals.add(new ShadowReferenceProposal(
                    offset, name.length(), change));
        }
    }
}