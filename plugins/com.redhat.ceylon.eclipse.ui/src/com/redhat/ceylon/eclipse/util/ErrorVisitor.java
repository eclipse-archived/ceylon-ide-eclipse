package com.redhat.ceylon.eclipse.util;

import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.AnalysisMessage;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.StatementOrArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public abstract class ErrorVisitor extends Visitor {
    
    protected boolean warnForErrors = false;
    
    protected int getSeverity(Message error, boolean expected) {
        return expected || error instanceof UsageWarning ? 
            SEVERITY_WARNING : SEVERITY_ERROR;
    }
    
    @Override
    public void visitAny(Node node) {
        super.visitAny(node);
        for (Message error: node.getErrors()) {
            if (!include(error)) continue;
            
            int startOffset = 0;
            int endOffset = 0;
            int startCol = 0;
            int startLine = 0;

            if (error instanceof RecognitionError) {
                RecognitionError recognitionError = (RecognitionError) error;
                RecognitionException re = recognitionError
                        .getRecognitionException();
                if (error instanceof LexError) {
                    startLine = re.line;
                    startCol = re.charPositionInLine;
                    startOffset = re.index;
                    endOffset = startOffset;
                }
                CommonToken token = (CommonToken) re.token;
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
//                if (error instanceof UnsupportedError &&
//                            node.getUnit().getPackage().getQualifiedNameString()
//                                    .startsWith(Module.LANGUAGE_MODULE_NAME)) {
//                    continue;
//                }
                AnalysisMessage analysisMessage = (AnalysisMessage) error;
                Node errorNode = Nodes.getIdentifyingNode(analysisMessage.getTreeNode());
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
            
            handleMessage(startOffset, endOffset, startCol, startLine, error);
        }
    }
    
    protected abstract void handleMessage(int startOffset, int endOffset, 
            int startCol, int startLine, Message error);

    protected boolean include(Message msg) {
        if (msg instanceof UsageWarning) {
            return !((UsageWarning) msg).isSuppressed();
        }
        else {
            return true;
        }
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

}