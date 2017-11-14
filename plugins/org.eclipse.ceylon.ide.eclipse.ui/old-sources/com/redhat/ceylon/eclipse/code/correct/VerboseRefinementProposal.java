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

import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;
import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Unit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;

@Deprecated
class VerboseRefinementProposal extends CorrectionProposal {

    private VerboseRefinementProposal(Change change) {
        super("Convert to verbose refinement", change, null);
    }

    static void addVerboseRefinementProposal(
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.Statement statement, Tree.CompilationUnit cu) {
        if (statement instanceof Tree.SpecifierStatement) {
            Tree.SpecifierStatement ss = (Tree.SpecifierStatement) statement;
            if (ss.getRefinement()) {
                TextFileChange change = new TextFileChange("Convert to Verbose Refinement", file);
                change.setEdit(new MultiTextEdit());
                Tree.Expression e = ss.getSpecifierExpression().getExpression();
                if (e!=null && !isTypeUnknown(e.getTypeModel())) {
                    Unit unit = ss.getUnit();
                    Type t = unit.denotableType(e.getTypeModel());
                    HashSet<Declaration> decs = new HashSet<Declaration>();
                    importProposals().importType(decs, t, cu);
                    importProposals().applyImports(change, decs, cu, EditorUtil.getDocument(change));
                    String type = t.asSourceCodeString(unit);
                    change.addEdit(new InsertEdit(statement.getStartIndex(), 
                            "shared actual " + type + " "));
                    proposals.add(new VerboseRefinementProposal(change));
                }
            }
        }
    }

}