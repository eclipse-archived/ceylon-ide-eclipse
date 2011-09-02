package com.redhat.ceylon.eclipse.imp.parser;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.imp.parser.IMessageHandler;

import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.AnalysisMessage;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class ErrorVisitor extends Visitor {
    private final IMessageHandler handler;

    public ErrorVisitor(IMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void visitAny(Node node) {
        super.visitAny(node);
        for (Message error : node.getErrors()) {
            String errorMessage = error.getMessage();
            int startOffset = 0;
            int endOffset = 0;
            int startCol = 0;
            int startLine = 0;

            // Map<String, Object> attributes = new HashMap<String, Object>();
            if (error instanceof RecognitionError) {
                RecognitionError recognitionError = (RecognitionError) error;
                CommonToken token = (CommonToken) recognitionError
                        .getRecognitionException().token;
                startOffset = token.getStartIndex();
                endOffset = token.getStopIndex();
                startCol = token.getCharPositionInLine();
                startLine = token.getLine();
                // attributes.put(SEVERITY_KEY, ERROR);
            }
            if (error instanceof AnalysisMessage) {
                AnalysisMessage analysisMessage = (AnalysisMessage) error;
                Node errorNode = CeylonSourcePositionLocator
                        .getIdentifyingNode(analysisMessage.getTreeNode());
                if (errorNode == null)
                    errorNode = analysisMessage.getTreeNode();
                Token token = errorNode.getToken();
                startOffset = errorNode.getStartIndex();
                endOffset = errorNode.getStopIndex();
                startCol = token.getCharPositionInLine();
                startLine = token.getLine();
                /*
                 * if (error instanceof AnalysisWarning) {
                 * attributes.put(SEVERITY_KEY, WARNING); } else {
                 * attributes.put(SEVERITY_KEY, ERROR); }
                 */
            }

            handler.handleSimpleMessage(errorMessage, startOffset, endOffset,
                    startCol, startCol, startLine, startLine/* , attributes */);
        }
    }
}