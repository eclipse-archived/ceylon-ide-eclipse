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

import static org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil.collectUninitializedMembers;
import static org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil.getDescription;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findDeclarationWithBody;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;

class AddParameterListProposal extends CorrectionProposal {
    
    AddParameterListProposal(int offset, 
            String desc, TextChange change) {
        super(desc, change, new Region(offset, 0));
    }

    @Deprecated
    static void addParameterListProposal(IFile file,
            Collection<ICompletionProposal> proposals, 
            Node node, Tree.CompilationUnit rootNode,
            boolean evenIfEmpty) {
        if (node instanceof Tree.TypedDeclaration) {
            node = findDeclarationWithBody(rootNode, node);
        }
        if (node instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition decNode = 
                    (Tree.ClassDefinition) node;
            Node n = CorrectionUtil.getBeforeParenthesisNode(decNode);
            if (n!=null && decNode.getParameterList()==null) {
                Declaration dec = decNode.getDeclarationModel();
                List<TypedDeclaration> uninitialized = 
                        collectUninitializedMembers(decNode.getClassBody());
                if (evenIfEmpty || !uninitialized.isEmpty()) {
                    StringBuilder params = new StringBuilder().append("(");
                    for (TypedDeclaration ud: uninitialized) {
                        if (params.length()>1) {
                            params.append(", ");
                        }
                        params.append(ud.getName());
                    }
                    params.append(")");
                    TextFileChange change = 
                            new TextFileChange("Add Parameter List", file);
                    int offset = n.getEndIndex();
                    change.setEdit(new InsertEdit(offset, params.toString()));
                    proposals.add(new AddParameterListProposal(offset+1, 
                            "Add initializer parameters '" + params + 
                                    "' to " + getDescription(dec), 
                            change));
                }
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.addParameterList");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }
    
}