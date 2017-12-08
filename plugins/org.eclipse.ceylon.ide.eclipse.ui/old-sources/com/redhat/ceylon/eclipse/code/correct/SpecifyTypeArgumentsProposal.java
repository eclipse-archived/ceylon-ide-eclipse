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

import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.REVEAL;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;

@Deprecated
public class SpecifyTypeArgumentsProposal extends CorrectionProposal {

    SpecifyTypeArgumentsProposal(String type, TextFileChange change) {
        super("Specify explicit type arguments '" + type + "'", change, null, REVEAL);
    }
    
    static void addSpecifyTypeArgumentsProposal(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IFile file) {
        Tree.MemberOrTypeExpression ref = (Tree.MemberOrTypeExpression) node;
        Tree.Identifier identifier;
        Tree.TypeArguments typeArguments;
        if (ref instanceof Tree.BaseMemberOrTypeExpression) {
            identifier = ((Tree.BaseMemberOrTypeExpression) ref).getIdentifier();
            typeArguments = ((Tree.BaseMemberOrTypeExpression) ref).getTypeArguments();
        }
        else if (ref instanceof Tree.QualifiedMemberOrTypeExpression) {
            identifier = ((Tree.QualifiedMemberOrTypeExpression) ref).getIdentifier();
            typeArguments = ((Tree.QualifiedMemberOrTypeExpression) ref).getTypeArguments();
        }
        else {
            return;
        }
        if (typeArguments instanceof Tree.InferredTypeArguments &&
                typeArguments.getTypeModels()!=null &&
                !typeArguments.getTypeModels().isEmpty()) {
            StringBuilder builder = new StringBuilder("<");
            for (Type arg: typeArguments.getTypeModels()) {
                if (isTypeUnknown(arg)) {
                    return;
                }
                if (builder.length()!=1) {
                    builder.append(",");
                }
                builder.append(arg.asSourceCodeString(node.getUnit()));
            }
            builder.append(">");
            TextFileChange change = new TextFileChange("Specify Explicit Type Arguments", file);
            change.setEdit(new InsertEdit(identifier.getEndIndex(), builder.toString())); 
            proposals.add(new SpecifyTypeArgumentsProposal(builder.toString(), change));
        }
    }
    
}
