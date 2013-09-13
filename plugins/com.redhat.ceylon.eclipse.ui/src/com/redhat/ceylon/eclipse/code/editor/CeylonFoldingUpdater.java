package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;

import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

/**
 * This file provides a skeletal implementation of the language-dependent aspects
 * of a source-text folder.  This implementation is generated from a template that
 * is parameterized with respect to the name of the language, the package containing
 * the language-specific types for AST nodes and AbstractVisitors, and the name of
 * the folder package and class.
 */
public class CeylonFoldingUpdater extends FolderBase {

	public CeylonFoldingUpdater(CeylonSourceViewer sourceViewer) {
		super(sourceViewer);
	}

	@Override
	public void sendVisitorToAST(HashMap<Annotation,Position> newAnnotations, 
	        final List<Annotation> annotations, Object ast) {
        for (CommonToken token: getTokens()) {
            int type = token.getType();
			if (type==MULTI_COMMENT ||
                type==STRING_LITERAL ||
                type==ASTRING_LITERAL||
                type==VERBATIM_STRING||
                type==AVERBATIM_STRING) {
                if (isMultilineToken(token)) {
                    makeAnnotation(token, token);
                    //TODO: initially collapse copyright notice
                }
            }
        }
		Tree.CompilationUnit cu = (Tree.CompilationUnit) ast;
        new Visitor() {
            @Override 
            public void visit(Tree.ImportList importList) {
                super.visit(importList);
                if (!importList.getImports().isEmpty()) {
                    if (foldIfNecessary(importList)) {
                        //TODO: initially collapse the import list
                        //collapseLast(annotations);
                    }
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
		}.visit(cu);
	}

    protected void collapseLast(List<Annotation> annotations) {
        ((ProjectionAnnotation) annotations.get(annotations.size()-1)).markCollapsed();
    }

    private boolean foldIfNecessary(Node node) {
        CommonToken token = (CommonToken) node.getToken();
        CommonToken endToken = (CommonToken) node.getEndToken();
        if (endToken.getLine()-token.getLine()>1) {
            makeAnnotation(token, endToken);
            return true;
        }
        return false;
    }
    
    private boolean isMultilineToken(CommonToken token) {
        return token.getText().indexOf('\n')>0 ||
                token.getText().indexOf('\r')>0;
    }

    private void makeAnnotation(CommonToken token, CommonToken endToken) {
        makeAnnotation(token.getStartIndex(), 
                endToken.getStopIndex()-token.getStartIndex()+1);
    }

    private List<CommonToken> getTokens() {
        return ((CeylonParseController) parseController)
                .getTokens();
    }

}
