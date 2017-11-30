/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findStatement;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Annotation;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Identifier;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Primary;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.TypedDeclaration;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

class PrintProposal implements ICompletionProposal, ICompletionProposalExtension6 {
    
    private final Node node;
    private final Tree.CompilationUnit rootNode;
    private final int currentOffset;
    
    public PrintProposal(Tree.CompilationUnit cu, 
    		Node node, int currentOffset) {
        this.rootNode = cu;
        this.node = node;
		this.currentOffset = currentOffset;
    }
    
    @Override
    public void apply(IDocument document) {
    	
        Tree.Statement st = findStatement(rootNode, node);
        Node expression;
        Node expanse;
        if (st instanceof Tree.ExpressionStatement) {
            Tree.ExpressionStatement es = 
                    (Tree.ExpressionStatement) st;
            Tree.Expression e = es.getExpression();
            expression = e;
            expanse = st;
            Tree.Term term = e.getTerm();
            if (term instanceof Tree.InvocationExpression) {
                Tree.InvocationExpression ie = 
                        (Tree.InvocationExpression) term;
                Primary primary = 
                        ie.getPrimary();
                if (primary instanceof Tree.QualifiedMemberExpression) {
                    Tree.QualifiedMemberExpression prim = 
                            (Tree.QualifiedMemberExpression) 
                                primary;
                    if (prim.getMemberOperator().getToken()==null) {
                        //an expression followed by two annotations 
                        //can look like a named operator expression
                        //even though that is disallowed as an
                        //expression statement
                        Tree.Primary p = prim.getPrimary();
                        expression = p;
                        expanse = expression;
                    }
                }
            }
        }
        else if (st instanceof Tree.Declaration) {
            Tree.Declaration dec = (Tree.Declaration) st;
			Declaration d = dec.getDeclarationModel();
            if (d==null || d.isToplevel()) {
                return;
            }
            //some expressions get interpreted as annotations
            Tree.AnnotationList al = dec.getAnnotationList();
            List<Tree.Annotation> annotations = 
                    al.getAnnotations();
            Tree.AnonymousAnnotation aa = 
            		al.getAnonymousAnnotation();
            if (aa!=null && currentOffset<=aa.getEndIndex()) {
            	expression = aa;
            	expanse = expression;
            }
            else if (!annotations.isEmpty() && 
            		currentOffset<=al.getEndIndex()) {
                Tree.Annotation a = annotations.get(0);
                expression = a;
                expanse = expression;
            }
            else if (st instanceof Tree.TypedDeclaration) {
                //some expressions look like a type declaration
                //when they appear right in front of an annotation
                //or function invocations
                Tree.TypedDeclaration td = 
                        (Tree.TypedDeclaration) st;
                Tree.Type type = td.getType();
                if (type instanceof Tree.SimpleType || 
                    type instanceof Tree.FunctionType) {
                    expression = type;
                    expanse = expression;
                }
                else {
                    return;
                }
            }
            else {
                return;
            }
        }
        else {
            return;
        }
//        
        int stopIndex = expanse.getEndIndex()-1;
//        if (currentOffset<expanse.getStartIndex() || 
//            currentOffset>stopIndex+1) {
//            return;
//        }
        int offset = expanse.getStartIndex();
        
        DocumentChange change = 
        		new DocumentChange("Print Expression", 
        		        document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(offset, "print("));
        
        String terminal = expanse.getEndToken().getText();
        String close = ")";
        if (!terminal.equals(";")) {
        	stopIndex++;
        	close = ");";
        }
        change.addEdit(new InsertEdit(stopIndex, close));
        
        EditorUtil.performChange(change);
        
    }
    

    @Override
    public Point getSelection(IDocument document) {
        return new Point(currentOffset+6,0);
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Print expression";
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.print");
        return new StyledString(getDisplayString())
                .append(hint, StyledString.QUALIFIER_STYLER);
    }
    
    @Override
    public Image getImage() {
        return MINOR_CHANGE;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
    static void addPrintProposal(Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, 
            Node node, int currentOffset) {
        PrintProposal prop = 
        		new PrintProposal(cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }
    
    private boolean isEnabled() {
        Tree.Statement st = findStatement(rootNode, node);
        if (st instanceof Tree.ExpressionStatement) {
            return true;
        }
        else if (st instanceof Tree.Declaration) {
            Tree.Declaration dec = (Tree.Declaration) st;
            Identifier id = dec.getIdentifier();
            if (id==null) {
                return false;
            }
            int line = id.getToken().getLine();
			Declaration d = dec.getDeclarationModel();
            if (d==null || d.isToplevel()) {
                return false;
            }
            //some expressions get interpreted as annotations
            Tree.AnnotationList al = dec.getAnnotationList();
            List<Annotation> annotations = 
                    al.getAnnotations();
            Tree.AnonymousAnnotation aa = 
            		al.getAnonymousAnnotation();
            if (aa!=null &&
            		currentOffset<=aa.getEndIndex()) {
                return aa.getEndToken().getLine()!=line;
            }
            else if (!annotations.isEmpty() &&
                    currentOffset<=al.getEndIndex()) {
                return al.getEndToken().getLine()!=line;
            }
            else if (st instanceof Tree.TypedDeclaration &&
                    !(st instanceof Tree.ObjectDefinition)) {
                //some expressions look like a type declaration
                //when they appear right in front of an annotation
                //or function invocations
                TypedDeclaration td = 
                        (Tree.TypedDeclaration) st;
                Tree.Type type = td.getType();
                if (currentOffset<=type.getEndIndex()) {
                	return (type instanceof Tree.SimpleType || 
                	        type instanceof Tree.FunctionType) && 
                            currentOffset<=type.getEndIndex() &&
                            currentOffset>=type.getStartIndex() &&
                	        type.getEndToken().getLine()!=line;
                }
            }
        }
        return false;
    }

}