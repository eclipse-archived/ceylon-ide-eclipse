package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Indents;

class ConvertStringProposal extends CorrectionProposal {

    private ConvertStringProposal(String name, Change change) {
        super(name, change, null);
    }

    static void addConvertToVerbatimProposal(Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, Node node, IDocument doc) {
        if (node instanceof Tree.StringLiteral) {
            Tree.StringLiteral literal = (Tree.StringLiteral) node;
            Token token = node.getToken();
            if (token.getType()==CeylonLexer.ASTRING_LITERAL ||
                    token.getType()==CeylonLexer.STRING_LITERAL) {
                String text = "\"\"\"" + literal.getText() + "\"\"\"";
                int offset = node.getStartIndex();
                int length = node.getDistance(); 
                String reindented = getConvertedText(text, token.getCharPositionInLine()+3, doc);
                TextFileChange change = new TextFileChange("Convert to Verbatim String", file);
                change.setEdit(new ReplaceEdit(offset, length, reindented));
                proposals.add(new ConvertStringProposal("Convert to verbatim string", change));
            }
        }
    }

    static void addConvertFromVerbatimProposal(Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, Node node, IDocument doc) {
        if (node instanceof Tree.StringLiteral) {
            Tree.StringLiteral literal = (Tree.StringLiteral) node;
            Token token = node.getToken();
            if (token.getType()==CeylonLexer.AVERBATIM_STRING ||
                token.getType()==CeylonLexer.VERBATIM_STRING) {
                String text = "\"" +
                        literal.getText()
                            .replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("`", "\\`") +
                        "\"";
                int offset = node.getStartIndex();
                int length = node.getDistance(); 
                String reindented = getConvertedText(text, token.getCharPositionInLine()+1, doc);
                TextFileChange change = new TextFileChange("Convert to Ordinary String", file);
                change.setEdit(new ReplaceEdit(offset, length, reindented));
                proposals.add(new ConvertStringProposal("Convert to ordinary string", change));
            }
        }
    }

    private static String getConvertedText(String text, int indentation,
            IDocument doc) {
        StringBuilder result = new StringBuilder();
        for (String line: text.split("\n|\r\n?")) {
            if (result.length() == 0) {
                //the first line of the string
                result.append(line);
            }
            else {
                for (int i = 0; i<indentation; i++) {
                    result.append(" ");
                }
                result.append(line);
            }
            result.append(Indents.getDefaultLineDelimiter(doc));
        }
        result.setLength(result.length()-1);
        return result.toString();
    }
    
}