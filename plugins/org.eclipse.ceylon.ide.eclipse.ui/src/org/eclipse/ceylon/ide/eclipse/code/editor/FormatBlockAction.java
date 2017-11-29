/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getSelection;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findDeclarationWithBody;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;

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
        Tree.CompilationUnit rootNode = pc.getParsedRootNode();
        if (rootNode==null) return;
        
        class FindBodyVisitor extends Visitor {
            Node result;
            private void handle(Node that) {
                if (ts.getOffset()>=that.getStartIndex() &&
                    ts.getOffset()+ts.getLength()<=that.getEndIndex()) {
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
            Tree.Declaration bodyDec = 
                    findDeclarationWithBody(rootNode, body);
            String bodyIndent = 
                    utilJ2C().indents().getIndent(bodyDec, document);
            String indent = bodyIndent + utilJ2C().indents().getDefaultIndent();
            String delim = utilJ2C().indents().getDefaultLineDelimiter(document);
            if (!body.getStatements().isEmpty()) {
                builder.append(delim);
                for (Tree.Statement st: body.getStatements()) {
                    builder.append(indent)
                        .append(Nodes.text(st, pc.getTokens()))
                        .append(delim);
                }
                builder.append(bodyIndent);
            }
        }
        else if (bodyNode instanceof Tree.NamedArgumentList) {
            Tree.NamedArgumentList body = 
                    (Tree.NamedArgumentList) bodyNode;
            String bodyIndent = utilJ2C().indents().getIndent(body, document);
            String indent = bodyIndent + utilJ2C().indents().getDefaultIndent();
            String delim = utilJ2C().indents().getDefaultLineDelimiter(document);
            if (!body.getNamedArguments().isEmpty()) {
                for (Tree.NamedArgument st: 
                        body.getNamedArguments()) {
                    builder.append(indent)
                        .append(Nodes.text(st, pc.getTokens()))
                        .append(delim);
                }
            }
            Tree.SequencedArgument sequencedArg = 
                    body.getSequencedArgument();
            if (sequencedArg!=null) {
                builder.append(indent)
                    .append(Nodes.text(sequencedArg, 
                            pc.getTokens()))
                    .append(delim);
            }
            if (builder.length()!=0) {
                builder.insert(0, delim);
                builder.append(bodyIndent);
            }
        }
        else if (bodyNode instanceof Tree.ImportMemberOrTypeList) {
            Tree.ImportMemberOrTypeList body = 
                    (Tree.ImportMemberOrTypeList) bodyNode;
            String bodyIndent = utilJ2C().indents().getIndent(body, document);
            String indent = bodyIndent + utilJ2C().indents().getDefaultIndent();
            String delim = utilJ2C().indents().getDefaultLineDelimiter(document);
            if (!body.getImportMemberOrTypes().isEmpty()) {
                for (Tree.ImportMemberOrType st: 
                        body.getImportMemberOrTypes()) {
                    builder.append(indent)
                        .append(Nodes.text(st, pc.getTokens()))
                        .append(",")
                        .append(delim);
                }
            }
            Tree.ImportWildcard wildcard = 
                    body.getImportWildcard();
            if (wildcard!=null) {
                builder.append(indent)
                    .append(Nodes.text(wildcard, 
                            pc.getTokens()))
                    .append(delim);
            }
            if (builder.toString().endsWith(","+delim)) {
                builder.setLength(builder.length()-1-delim.length());
                builder.append(delim);
            }
            if (builder.length()!=0) {
                builder.insert(0, delim);
                builder.append(bodyIndent);
            }
        }
        else {
            return;
        }
        String text = builder.toString();
        int start = bodyNode.getStartIndex()+1;
        int end = bodyNode.getEndIndex()-1;
        int len = end-start;
        try {
            if (!document.get(start, len).equals(text)) {
                DocumentChange change = 
                        new DocumentChange("Format Block", document);
                change.setEdit(new ReplaceEdit(start, len, text));
                EditorUtil.performChange(change);
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}