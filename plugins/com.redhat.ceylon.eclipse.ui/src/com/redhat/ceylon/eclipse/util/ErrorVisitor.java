package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.parse.MessageHandler.ERROR_CODE_KEY;
import static com.redhat.ceylon.eclipse.code.parse.MessageHandler.SEVERITY_KEY;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import com.redhat.ceylon.eclipse.code.parse.MessageHandler;

import com.redhat.ceylon.compiler.typechecker.analyzer.AnalysisWarning;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.AnalysisMessage;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.StatementOrArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public abstract class ErrorVisitor extends Visitor {
	
    private final MessageHandler handler;
    private boolean warnForErrors = false;
    
    public ErrorVisitor(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void visitAny(Node node) {
        super.visitAny(node);
        for (Message error: node.getErrors()) {
        	if (!include(error)) continue;
        	
            String errorMessage = error.getMessage();
            int startOffset = 0;
            int endOffset = 0;
            int startCol = 0;
            int startLine = 0;

            Map<String, Object> attributes = new HashMap<String, Object>();
            if (error instanceof RecognitionError) {
                RecognitionError recognitionError = (RecognitionError) error;
                CommonToken token = (CommonToken) recognitionError
                        .getRecognitionException().token;
                if (token!=null) {
                    startOffset = token.getStartIndex();
                    endOffset = token.getStopIndex();
                    startCol = token.getCharPositionInLine();
                    startLine = token.getLine();
                    if (token.getType()==CeylonParser.EOF) {
                        startOffset--;
                        endOffset--;
                    }
                }
            }
            if (error instanceof AnalysisMessage) {
                if (error instanceof AnalysisWarning &&
                            node.getUnit().getPackage().getQualifiedNameString()
                                    .startsWith("ceylon.language")) {
                    continue;
                }
                AnalysisMessage analysisMessage = (AnalysisMessage) error;
                Node errorNode = getIdentifyingNode(analysisMessage.getTreeNode());
                if (errorNode == null) {
                    errorNode = analysisMessage.getTreeNode();
                }
                Token token = errorNode.getToken();
                if (token!=null) {
                    startOffset = errorNode.getStartIndex();
                    endOffset = errorNode.getStopIndex();
                    startCol = token.getCharPositionInLine();
                    startLine = token.getLine();
                }
            }
            attributes.put("CeylonMessageClass", error.getClass().getSimpleName());
            attributes.put(SEVERITY_KEY, getSeverity(error, warnForErrors));
            attributes.put(ERROR_CODE_KEY, error.getCode());

            handler.handleSimpleMessage(errorMessage, startOffset, adjust(endOffset),
                    startCol, startCol, startLine, startLine, attributes);
        }
    }
    
    protected boolean include(Message msg) {
    	return true;
    }
    
    protected int adjust(int stopIndex) {
        return stopIndex;
    }
    
    @Override
    public void visit(StatementOrArgument that) {
        boolean owe = warnForErrors;
        warnForErrors = false;
        for (Tree.CompilerAnnotation c: that.getCompilerAnnotations()) {
            if (c.getIdentifier().getText().equals("error")) {
                warnForErrors = true;
            }
        }
        super.visit(that);
        warnForErrors = owe;
    }

    public abstract int getSeverity(Message error, boolean expected);
}