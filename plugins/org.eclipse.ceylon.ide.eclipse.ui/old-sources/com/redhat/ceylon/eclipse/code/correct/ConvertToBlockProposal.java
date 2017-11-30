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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.Value;

@Deprecated
class ConvertToBlockProposal extends CorrectionProposal {
    
    private ConvertToBlockProposal(String desc, int offset, 
            TextChange change) {
        super(desc, change, new Region(offset, 0));
    }
    
    static void addConvertToBlockProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, 
            IFile file, Node decNode) {
        TextChange change = 
                new TextFileChange("Convert to Block", 
                        file);
        change.setEdit(new MultiTextEdit());
        int offset;
        int length;
        String semi;
        boolean isVoid;
        String addedKeyword = null;
        String desc = "Convert => to block";
        if (decNode instanceof Tree.MethodDeclaration) {
            Tree.MethodDeclaration md = 
                    (Tree.MethodDeclaration) decNode;
            Function dm = md.getDeclarationModel();
            if (dm==null || dm.isParameter()) {
                return;
            }
            isVoid = dm.isDeclaredVoid();
            List<Tree.ParameterList> pls = 
                    md.getParameterLists();
            if (pls.isEmpty()) return;
            offset = pls.get(pls.size()-1).getEndIndex();
            Tree.TypeConstraintList tcl = 
                    md.getTypeConstraintList();
            if (tcl!=null) {
                offset = tcl.getEndIndex();
            }
            length = 
                    md.getSpecifierExpression()
                        .getExpression()
                        .getStartIndex() 
                            - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.AttributeDeclaration) {
            Tree.AttributeDeclaration ad = 
                    (Tree.AttributeDeclaration) decNode;
            Value dm = ad.getDeclarationModel();
            if (dm==null || dm.isParameter()) {
                return;
            }
            isVoid = false;
            offset = ad.getIdentifier().getEndIndex();
            length = 
                    ad.getSpecifierOrInitializerExpression()
                        .getExpression()
                        .getStartIndex() 
                            - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.AttributeSetterDefinition) {
            Tree.AttributeSetterDefinition asd = 
                    (Tree.AttributeSetterDefinition) decNode;
            isVoid = true;
            offset = asd.getIdentifier().getEndIndex();
            length = 
                    asd.getSpecifierExpression()
                        .getExpression()
                        .getStartIndex() 
                            - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.MethodArgument) {
            Tree.MethodArgument ma = 
                    (Tree.MethodArgument) decNode;
            Function dm = ma.getDeclarationModel();
            if (dm==null) {
                return;
            }
            isVoid = dm.isDeclaredVoid();
            if (ma.getType().getToken()==null) {
                addedKeyword = "function ";
            }
            List<Tree.ParameterList> pls = 
                    ma.getParameterLists();
            if (pls.isEmpty()) {
                return;
            }
            offset = pls.get(pls.size()-1).getEndIndex();
            length = 
                    ma.getSpecifierExpression()
                        .getExpression()
                        .getStartIndex() 
                            - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.AttributeArgument) {
            Tree.AttributeArgument aa = 
                    (Tree.AttributeArgument) decNode;
            isVoid = false;            
            if (aa.getType().getToken()==null) {
                addedKeyword = "value ";
            }
            offset = aa.getIdentifier().getEndIndex();
            length = 
                    aa.getSpecifierExpression()
                        .getExpression()
                        .getStartIndex() 
                            - offset;
            semi = "";
        }
        else if (decNode instanceof Tree.FunctionArgument) {
            Tree.FunctionArgument fun = 
                    (Tree.FunctionArgument) decNode;
            Function dm = fun.getDeclarationModel();
            if (dm==null) {
                return;
            }
            isVoid = dm.isDeclaredVoid();
            List<Tree.ParameterList> pls = 
                    fun.getParameterLists();
            if (pls.isEmpty()) {
                return;
            }
            offset = pls.get(pls.size()-1).getEndIndex();
            Tree.TypeConstraintList tcl = 
                    fun.getTypeConstraintList();
            if (tcl!=null) {
                offset = tcl.getEndIndex();
            }
            length = 
                    fun.getExpression()
                        .getStartIndex() 
                            - offset;
            semi = ";";
            desc = "Convert anonymous function => to block";
        }
        else {
            return;
        }
        if (addedKeyword!=null) {
            int loc = decNode.getStartIndex();
            change.addEdit(new InsertEdit(loc, addedKeyword));
        }
        String text = " {" + (isVoid?"":" return") + " ";
        change.addEdit(new ReplaceEdit(offset, length, text));
        change.addEdit(new InsertEdit(decNode.getEndIndex(), semi + " }"));
        proposals.add(new ConvertToBlockProposal(desc, offset + 3, change));
    }

}