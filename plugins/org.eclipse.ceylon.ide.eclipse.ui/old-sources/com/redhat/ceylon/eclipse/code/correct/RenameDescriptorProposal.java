/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CHANGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.common.util.escaping_;

@Deprecated
class RenameDescriptorProposal {
    
    static void addRenameDescriptorProposal(Tree.CompilationUnit cu,
            IQuickAssistInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file) {
        String pn = escaping_.get_().escapePackageName(cu.getUnit().getPackage());
        //TODO: DocumentChange doesn't work for Problems View
        TextFileChange change = new TextFileChange("Rename", file);
//        DocumentChange change = new DocumentChange("Rename", context.getSourceViewer().getDocument());
        change.setEdit(new ReplaceEdit(problem.getOffset(), problem.getLength(), pn));
        proposals.add(new CorrectionProposal("Rename to '" + pn + "'", change, null, CHANGE) {
            @Override
            public StyledString getStyledDisplayString() {
                return Highlights.styleProposal(getDisplayString(), true);
            }
        });
    }
    
}