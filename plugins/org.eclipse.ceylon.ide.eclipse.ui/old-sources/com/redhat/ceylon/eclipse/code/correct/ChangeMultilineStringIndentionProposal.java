/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_MID;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_START;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;

import java.util.Collection;

import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;

@Deprecated
class FixMultilineStringIndentationProposal 
        extends CorrectionProposal {
    
    public static void addFixMultilineStringIndentation(
            Collection<ICompletionProposal> proposals, 
            IFile file, Tree.CompilationUnit cu, Node node) {
        if (node instanceof Tree.StringLiteral) {
            TextFileChange change = 
                    new TextFileChange("Fix Multiline String", file);
            IDocument doc = EditorUtil.getDocument(change);
            Tree.StringLiteral literal = (Tree.StringLiteral) node;
            int offset = literal.getStartIndex();
            int length = literal.getDistance(); 
            Token token = literal.getToken();
            int indentation = token.getCharPositionInLine() + 
                    getStartQuoteLength(token.getType());
            String text = getFixedText(token.getText(), indentation, doc);
            if (text!=null) {
                change.setEdit(new ReplaceEdit(offset, length, text));
                FixMultilineStringIndentationProposal proposal = 
                        new FixMultilineStringIndentationProposal(change);
                if (!proposals.contains(proposal)) {
                    proposals.add(proposal);
                }
            }
        }
    }
    
    private static String getFixedText(String text, 
            int indentation, IDocument doc) {
        StringBuilder result = new StringBuilder();
        for (String line: text.split("\n|\r\n?")) {
            if (result.length() == 0) {
                //the first line of the string
                result.append(line);
            }
            else {
                for (int i = 0; i<indentation; i++) {
                    //fix the indentation
                    result.append(" ");
                    if (line.startsWith(" ")) {
                        line = line.substring(1);
                    }
                }
                //the non-whitespace content
                result.append(line);
            }
            result.append(utilJ2C().indents().getDefaultLineDelimiter(doc));
        }
        result.setLength(result.length()-1);
        return result.toString();
    }

    private static int getStartQuoteLength(int type) {
        int startQuoteLength = -1;
        if (type == STRING_LITERAL || 
            type== ASTRING_LITERAL || 
            type == STRING_START) {
            startQuoteLength = 1;
        } 
        else if (type == STRING_MID || 
                type == STRING_END) {
            startQuoteLength = 2;
        } 
        else if (type == VERBATIM_STRING || 
                type == AVERBATIM_STRING) {
            startQuoteLength = 3;
        }
        return startQuoteLength;
    }
    
    private FixMultilineStringIndentationProposal(TextFileChange change) {
        super("Fix multiline string indentation", change, null);
    }

}