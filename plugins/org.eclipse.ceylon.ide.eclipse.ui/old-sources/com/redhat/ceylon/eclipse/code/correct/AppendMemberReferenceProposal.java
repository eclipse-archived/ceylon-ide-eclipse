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
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.common.util.RequiredType;
import org.eclipse.ceylon.ide.common.util.types_;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Value;

@Deprecated
class AppendMemberReferenceProposal extends CorrectionProposal  {
    
    private static final List<Type> NO_TYPES = 
            Collections.<Type>emptyList();
    
    private AppendMemberReferenceProposal(Node node, 
            String name, String type, TextFileChange change) {
        super("Append reference to member '" + name + 
                "' of type '" + type + "'", 
                change, 
                new Region(node.getEndIndex(), name.length()+1), 
                MINOR_CHANGE);
    }
    
    @Deprecated
    private static void addAppendMemberReferenceProposal(
            Node node,
            Collection<ICompletionProposal> proposals, 
            IFile file,
            TypedDeclaration dec, Type type,
            Tree.CompilationUnit rootNode) {
        TextFileChange change = 
                new TextFileChange("Append Member Reference", 
                        file);
        int problemOffset = node.getEndIndex();
        change.setEdit(new InsertEdit(problemOffset, 
                "." + dec.getName()));
        proposals.add(new AppendMemberReferenceProposal(
                node, dec.getName(), type.asString(), change));
    }
    
    @Deprecated
    static void addAppendMemberReferenceProposals(
            Tree.CompilationUnit rootNode, 
            Node node, ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IFile file) {
        Node id = getIdentifyingNode(node);
        if (id!=null) {
            if (node instanceof Tree.StaticMemberOrTypeExpression) {
                Tree.StaticMemberOrTypeExpression mte = 
                        (Tree.StaticMemberOrTypeExpression) 
                            node;
                Type t = mte.getTypeModel();
                if (t!=null) {
                    CommonToken token = 
                            (CommonToken) 
                                id.getToken();
                    RequiredType required = types_.get_()
                            .getRequiredType(rootNode, node, token);
                    Type requiredType = required.getType();
                    if (requiredType!=null) {
                        TypeDeclaration type = t.getDeclaration();
                        Collection<DeclarationWithProximity> dwps = 
                                type.getMatchingMemberDeclarations(
                                        node.getUnit(), 
                                        node.getScope(), 
                                        "", 0)
                                    .values();
                        for (DeclarationWithProximity dwp: dwps) {
                            Declaration dec = dwp.getDeclaration();
                            if (dec instanceof Value) {
                                Value value = (Value) dec;
                                Type vt = 
                                        value.appliedReference(t, NO_TYPES)
                                            .getType();
                                if (!isTypeUnknown(vt) 
                                        && vt.isSubtypeOf(requiredType)) {
                                    addAppendMemberReferenceProposal(
                                            id, proposals, file, 
                                            value, t, rootNode);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }
    
}