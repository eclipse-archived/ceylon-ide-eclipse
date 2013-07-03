package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_MID;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_START;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class ChangeMultilineStringIndentionProposal extends ChangeCorrectionProposal {

    public static void addFixMultilineStringIndention(Collection<ICompletionProposal> proposals, IFile file, Tree.CompilationUnit cu, Tree.StringLiteral literal) {
        int type = literal.getToken().getType();
        int startQuoteLength = getStartQuoteLength(type);
        int endQuoteLength = getEndQuoteLength(type);
        int offset = literal.getStartIndex() + startQuoteLength;
        int length = literal.getStopIndex() - literal.getStartIndex() - (startQuoteLength + endQuoteLength - 1);
        
        FindIndentionVisitor fiv = new FindIndentionVisitor(literal);
        fiv.visit(cu);
        
        if (fiv.getIndention() != -1 && offset != -1 && length != -1) {
            String text = getIndentedText(literal.getText(), fiv.getIndention());
            TextFileChange change = new TextFileChange("Fix multiline string indention", file);
            change.setEdit(new ReplaceEdit(offset, length, text));

            ChangeMultilineStringIndentionProposal proposal = new ChangeMultilineStringIndentionProposal(change);
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }
        }
    }
    
    private static int getStartQuoteLength(int type) {
        int startQuoteLength = -1;
        if (type == STRING_LITERAL || type == STRING_START) {
            startQuoteLength = 1;
        } else if (type == STRING_MID || type == STRING_END) {
            startQuoteLength = 2;
        } else if (type == VERBATIM_STRING || type == AVERBATIM_STRING) {
            startQuoteLength = 3;
        }
        return startQuoteLength;
    }

    private static int getEndQuoteLength(int type) {
        int endQuoteLength = -1;
        if (type == STRING_LITERAL || type == STRING_END) {
            endQuoteLength = 1;
        } else if (type == STRING_START || type == STRING_MID) {
            endQuoteLength = 2;
        } else if (type == VERBATIM_STRING || type == AVERBATIM_STRING) {
            endQuoteLength = 3;
        }
        return endQuoteLength;
    }
    
    private static String getIndentedText(String text, int indention) {
        StringBuilder result = new StringBuilder();
    
        for (String line : text.split("\n|\r\n?")) {
            if (result.length() == 0 /* first line */) {
                result.append(line);
            } else {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (Character.isWhitespace(c)) {
                        result.append(c);
                    } else {
                        if (i < indention) {
                            for (int ii = i; ii < indention; ii++) {
                                result.append(" ");
                            }
                        }
                        result.append(line.substring(i));
                        break;
                    }
                }
            }
            result.append("\n");
        }
        result.setLength(result.length() - 1);
    
        return result.toString();
    }

    private ChangeMultilineStringIndentionProposal(TextFileChange change) {
        super(change.getName(), change, 10, CORRECTION);
    }

    private static class FindIndentionVisitor extends Visitor {

        private int indention;
        private int currentIndention;
        private Tree.StringLiteral literal;

        private FindIndentionVisitor(Tree.StringLiteral literal) {
            this.literal = literal;
            this.indention = -1;
        }

        public int getIndention() {
            return indention;
        }

        @Override
        public void visit(Tree.StringLiteral that) {
            int type = that.getToken().getType();
            if (type != STRING_MID && type != STRING_END) {
                currentIndention = that.getToken().getCharPositionInLine() + getStartQuoteLength(type);
            }
            if (that == literal) {
                indention = currentIndention;
            }
        }

        @Override
        public void visit(Tree.StringTemplate that) {
            int oldIndention = currentIndention;
            currentIndention = 0;
            super.visit(that);
            currentIndention = oldIndention;
        }

    }

}