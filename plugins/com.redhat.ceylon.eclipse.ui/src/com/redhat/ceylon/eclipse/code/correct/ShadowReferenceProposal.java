package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CreateProposal.getDocument;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring.guessName;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
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
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.util.FindUtils;
import com.redhat.ceylon.eclipse.util.Indents;

class ShadowReferenceProposal extends CorrectionProposal {
    
    private final IFile file;
    private final int offset;
    private final int length;
    
    ShadowReferenceProposal(int offset, int length, IFile file, 
            TextChange change) {
        super("Shadow reference inside control structure", change);
        this.file=file;
        this.offset=offset;
        this.length=length;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        EditorUtil.gotoLocation(file, offset, length);
    }
    
    static void addShadowSwitchReferenceProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.Term) {
            Tree.Statement statement = FindUtils.findStatement(cu, node);
            if (statement instanceof Tree.SwitchStatement) {
                String name = guessName(node);
                TextFileChange change = new TextFileChange("Shadow Reference", file);
                change.setEdit(new MultiTextEdit());
                Integer offset = statement.getStartIndex();
                change.addEdit(new ReplaceEdit(offset, 
                        node.getStartIndex()-offset,
                        "value " + name + " = "));
                IDocument doc = getDocument(change);
                change.addEdit(new InsertEdit(node.getStopIndex()+1, ";" + 
                        Indents.getDefaultLineDelimiter(doc) + getIndent(statement, doc) +
                        "switch (" + name));
                if (node instanceof BaseMemberExpression) {
                    Declaration d = ((BaseMemberExpression) node).getDeclaration();
                    if (d!=null) {
                        FindReferenceVisitor frv = new FindReferenceVisitor(d);
                        frv.visit(((Tree.SwitchStatement) statement).getSwitchCaseList());
                        for (Node n: frv.getNodes()) {
                            Node identifyingNode = getIdentifyingNode(n);
                            Integer start = identifyingNode.getStartIndex();
                            if (start!=node.getStartIndex()) {
                                change.addEdit(new ReplaceEdit(start, 
                                        identifyingNode.getText().length(), name));
                            }
                        }
                    }
                }
                proposals.add(new ShadowReferenceProposal(offset+6, name.length(), 
                        file, change));
            }
        }
    }
    
    static void addShadowReferenceProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.Variable) {
            Tree.Variable var = (Tree.Variable) node;
            int offset = var.getIdentifier().getStartIndex();
            String name = guessName(var.getSpecifierExpression().getExpression().getTerm());
            TextChange change = new TextFileChange("Shadow Reference", file);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new InsertEdit(offset, name + " = "));
            Tree.Statement statement = FindUtils.findStatement(cu, node);
            FindReferenceVisitor frv = new FindReferenceVisitor(var.getDeclarationModel());
            frv.visit(statement);
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
        else if (node instanceof Tree.Term) {
            String name = guessName(node);
            TextChange change = new TextFileChange("Shadow Reference", file);
//            change.setEdit(new MultiTextEdit());
            Integer offset = node.getStartIndex();
            change.setEdit(new InsertEdit(offset, name + " = "));
            proposals.add(new ShadowReferenceProposal(offset, 1, file, change));
        }
    }
}