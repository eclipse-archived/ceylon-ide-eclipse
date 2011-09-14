package com.redhat.ceylon.eclipse.imp.editor;

import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.imp.services.base.FolderBase;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

/**
 * This file provides a skeletal implementation of the language-dependent aspects
 * of a source-text folder.  This implementation is generated from a template that
 * is parameterized with respect to the name of the language, the package containing
 * the language-specific types for AST nodes and AbstractVisitors, and the name of
 * the folder package and class.
 */
public class CeylonFoldingUpdater extends FolderBase {

    @Override
	public void sendVisitorToAST(HashMap<Annotation,Position> newAnnotations, 
	        List<Annotation> annotations, Object ast) {
        //TODO: we should also allow multiline comments 
        //      to be folded, but there is no treenode 
        //      for them!
        for (CommonToken token: getTokens()) {
            if (token.getType()==CeylonLexer.MULTI_COMMENT ||
                    token.getType()==CeylonLexer.STRING_LITERAL) {
                if (isMultilineToken(token)) {
                    makeAnnotation(token, token);
                }
            }
        }
		Tree.CompilationUnit cu = (Tree.CompilationUnit) ast;
        new Visitor() {
            @Override 
            public void visit(Tree.ImportList importList) {
                super.visit(importList);
                if (!importList.getImports().isEmpty()) {
                    foldIfNecessary(importList);
                }
            }
            /*@Override 
            public void visit(Tree.Import that) {
                super.visit(that);
                foldIfNecessary(that);
            }*/
			@Override 
			public void visit(Tree.Body that) {
                super.visit(that);
                if (that.getToken()!=null) { //for "else if"
                    foldIfNecessary(that);
                }
			}
            @Override 
            public void visit(Tree.NamedArgumentList that) {
                super.visit(that);
                foldIfNecessary(that);
            }
            private void foldIfNecessary(Node node) {
                CommonToken token = (CommonToken) node.getToken();
                CommonToken endToken = (CommonToken) node.getEndToken();
                if (endToken.getLine()-token.getLine()>1) {
                    makeAnnotation(token, endToken);
                }
            }
		}.visit(cu);
	}

    private boolean isMultilineToken(CommonToken token) {
        return token.getText().indexOf('\n')>0 ||
                token.getText().indexOf('\r')>0;
    }

    private void makeAnnotation(CommonToken token, CommonToken endToken) {
        makeAnnotation(token.getStartIndex(), 
                endToken.getStopIndex()-token.getStartIndex());
    }

    private List<CommonToken> getTokens() {
        return ((CeylonParseController) parseController)
                .getTokenStream().getTokens();
    }

}
