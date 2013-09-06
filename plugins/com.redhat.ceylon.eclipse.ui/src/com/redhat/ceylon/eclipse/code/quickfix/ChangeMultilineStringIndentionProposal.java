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

class ChangeMultilineStringIndentationProposal extends ChangeCorrectionProposal {

    public static void addFixMultilineStringIndentation(Collection<ICompletionProposal> proposals, IFile file, Tree.CompilationUnit cu, Tree.StringLiteral literal) {
        int type = literal.getToken().getType();
        int startQuoteLength = getStartQuoteLength(type);
        int endQuoteLength = getEndQuoteLength(type);
        int offset = literal.getStartIndex() + startQuoteLength;
        int length = literal.getStopIndex() - literal.getStartIndex() - (startQuoteLength + endQuoteLength - 1);
        
        FindIndentationVisitor fiv = new FindIndentationVisitor(literal);
        fiv.visit(cu);
        
        if (fiv.getIndentation() != -1 && offset != -1 && length != -1) {
            String text = getFixedText(literal.getText(), fiv.getIndentation());
            TextFileChange change = new TextFileChange("Fix multiline string indentation", file);
            change.setEdit(new ReplaceEdit(offset, length, text));

            ChangeMultilineStringIndentationProposal proposal = new ChangeMultilineStringIndentationProposal(change);
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
    
    private static String getFixedText(String text, int indentation) {
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
                        if (i < indentation) {
                            for (int ii = i; ii < indentation; ii++) {
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

    private ChangeMultilineStringIndentationProposal(TextFileChange change) {
        super(change.getName(), change);
    }

    private static class FindIndentationVisitor extends Visitor {

        private int indentation;
        private int currentIndentation;
        private Tree.StringLiteral literal;

        private FindIndentationVisitor(Tree.StringLiteral literal) {
            this.literal = literal;
            this.indentation = -1;
        }

        public int getIndentation() {
            return indentation;
        }

        @Override
        public void visit(Tree.StringLiteral that) {
            int type = that.getToken().getType();
            if (type != STRING_MID && type != STRING_END) {
                currentIndentation = that.getToken().getCharPositionInLine() + getStartQuoteLength(type);
            }
            if (that == literal) {
                indentation = currentIndentation;
            }
        }

        @Override
        public void visit(Tree.StringTemplate that) {
            int oldIndentation = currentIndentation;
            currentIndentation = 0;
            super.visit(that);
            currentIndentation = oldIndentation;
        }

    }

}