package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring;

final class FormatBlockAction extends Action {
    private final CeylonEditor editor;

    FormatBlockAction(CeylonEditor editor) {
        super(null);
        this.editor = editor;
    }
    
    @Override
    public void run() {
        IDocument document = editor.getCeylonSourceViewer().getDocument();
        final ITextSelection ts = getSelection(editor);
        CeylonParseController pc = editor.getParseController();
        Tree.CompilationUnit rootNode = pc.getRootNode();
        if (rootNode==null) return;
        
        class FindBodyVisitor extends Visitor {
            Node result;
            private void handle(Node that) {
                if (ts.getOffset()>=that.getStartIndex() &&
                        ts.getOffset()+ts.getLength()<=that.getStopIndex()+1) {
                    result = that;
                }
            }
            @Override
            public void visit(Tree.Body that) {
                handle(that);
                super.visit(that);
            }
            @Override
            public void visit(Tree.NamedArgumentList that) {
                handle(that);
                super.visit(that);
            }
            @Override
            public void visit(Tree.ImportMemberOrTypeList that) {
                handle(that);
                super.visit(that);
            }
        }
        FindBodyVisitor fbv = new FindBodyVisitor();
        fbv.visit(rootNode);
        StringBuilder builder = new StringBuilder();
        Node bodyNode = fbv.result;
        if (bodyNode instanceof Tree.Body) {
            Tree.Body body = (Tree.Body) bodyNode;
            String bodyIndent = getIndent(body, document);
            String indent = bodyIndent + getDefaultIndent();
            String delim = getDefaultLineDelimiter(document);
            if (!body.getStatements().isEmpty()) {
                builder.append(delim);
                for (Tree.Statement st: body.getStatements()) {
                    builder.append(indent)
                    .append(AbstractRefactoring.toString(st, pc.getTokens()))
                    .append(delim);
                }
                builder.append(bodyIndent);
            }
        }
        else if (bodyNode instanceof Tree.NamedArgumentList) {
            Tree.NamedArgumentList body = (Tree.NamedArgumentList) bodyNode;
            String bodyIndent = getIndent(body, document);
            String indent = bodyIndent + getDefaultIndent();
            String delim = getDefaultLineDelimiter(document);
            if (!body.getNamedArguments().isEmpty()) {
                for (Tree.NamedArgument st: body.getNamedArguments()) {
                    builder.append(indent)
                    .append(AbstractRefactoring.toString(st, pc.getTokens()))
                    .append(delim);
                }
            }
            if (body.getSequencedArgument()!=null) {
                builder.append(indent)
                .append(AbstractRefactoring.toString(body.getSequencedArgument(), 
                        pc.getTokens()))
                .append(delim);
            }
            if (builder.length()!=0) {
                builder.insert(0, delim);
                builder.append(bodyIndent);
            }
        }
        else if (bodyNode instanceof Tree.ImportMemberOrTypeList) {
            Tree.ImportMemberOrTypeList body = (Tree.ImportMemberOrTypeList) bodyNode;
            String bodyIndent = getIndent(body, document);
            String indent = bodyIndent + getDefaultIndent();
            String delim = getDefaultLineDelimiter(document);
            if (!body.getImportMemberOrTypes().isEmpty()) {
                for (Tree.ImportMemberOrType st: body.getImportMemberOrTypes()) {
                    builder.append(indent)
                    .append(AbstractRefactoring.toString(st, pc.getTokens()))
                    .append(",")
                    .append(delim);
                }
            }
            if (body.getImportWildcard()!=null) {
                builder.append(indent)
                .append(AbstractRefactoring.toString(body.getImportWildcard(), 
                        pc.getTokens()))
                .append(delim);
            }
            if (builder.toString().endsWith(",")) {
                builder.setLength(builder.length()-1);
            }
            if (builder.length()!=0) {
                builder.insert(0, delim);
                builder.append(bodyIndent);
            }
        }
        else {
            return;
        }
        DocumentChange change = new DocumentChange("Format Block", document);
        change.setEdit(new ReplaceEdit(bodyNode.getStartIndex()+1, 
                bodyNode.getStopIndex()-bodyNode.getStartIndex()-1, 
                builder.toString()));
        try {
            change.perform(new NullProgressMonitor());
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

}