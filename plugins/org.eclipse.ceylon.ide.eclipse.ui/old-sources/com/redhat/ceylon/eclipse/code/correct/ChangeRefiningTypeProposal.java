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

import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.appendParameterText;
import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findDeclaration;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findStatement;
import static org.eclipse.ceylon.ide.eclipse.util.Types.getRefinedDeclaration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.platform.platformJ2C;
import org.eclipse.ceylon.ide.common.platform.TextChange;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Functional;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.ParameterList;
import org.eclipse.ceylon.model.typechecker.model.Reference;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

@Deprecated
public class ChangeRefiningTypeProposal {

    static void addChangeRefiningTypeProposal(IFile file,
            Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node) {
        Tree.Declaration decNode = findDeclaration(cu, node);
        if (decNode instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td =
                    (Tree.TypedDeclaration) decNode;
            TypedDeclaration dec = td.getDeclarationModel();
            //TODO: this can return the wrong member when
            //      there are multiple ... better to look
            //      at what RefinementVisitor does
            Declaration rd = getRefinedDeclaration(dec);
            if (rd instanceof TypedDeclaration) {
                TypeDeclaration decContainer =
                        (TypeDeclaration)
                            dec.getContainer();
                TypeDeclaration rdContainer =
                        (TypeDeclaration)
                            rd.getContainer();
                Type supertype =
                        decContainer.getType()
                            .getSupertype(rdContainer);
                Reference ref =
                        rd.appliedReference(supertype, 
                                Collections.<Type>emptyList());
                Type t = ref.getType();
                String type =
                        t.asSourceCodeString(
                                decNode.getUnit());
                Set<Declaration> declarations =
                        new HashSet<Declaration>();
                importProposals().importType(declarations, t, cu);
                TextFileChange change = 
                        new TextFileChange("Change Type",
                                file);
                int offset = node.getStartIndex();
                int length = node.getDistance();
                change.setEdit(new MultiTextEdit());
                TextChange chg2 = new platformJ2C().newChange(change.getName(), change);
                importProposals().applyImports(chg2, declarations, cu,
                        chg2.getDocument());
                change.addEdit(new ReplaceEdit(
                        offset, length, type));
                Region selection =
                        new Region(offset, type.length());
                proposals.add(new CorrectionProposal(
                        "Change type to '" + type + "'",
                        change, selection));
            }
        }
    }

    static void addChangeRefiningParametersProposal(
            IFile file, Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals,
            Node node) {
        Tree.Statement decNode =
                (Tree.Statement)
                    findStatement(cu, node);
        Tree.ParameterList list;
        Declaration dec; 
        if (decNode instanceof Tree.AnyMethod) {
            Tree.AnyMethod am = (Tree.AnyMethod) decNode;
            list = am.getParameterLists().get(0);
            dec = am.getDeclarationModel();
        }
        else if (decNode instanceof Tree.AnyClass) {
            Tree.AnyClass ac = (Tree.AnyClass) decNode;
            list = ac.getParameterList();
            dec = ac.getDeclarationModel();
        }
        else if (decNode instanceof Tree.SpecifierStatement) {
            Tree.SpecifierStatement ss = 
                    (Tree.SpecifierStatement) decNode;
            Tree.Term lhs = ss.getBaseMemberExpression();
            if (lhs instanceof Tree.ParameterizedExpression) {
                Tree.ParameterizedExpression pe = 
                        (Tree.ParameterizedExpression) lhs;
                list = pe.getParameterLists().get(0);
                dec = ss.getDeclaration();
            }
            else {
                return;
            }
        }
        else {
            return;
        }
        Declaration rd = dec.getRefinedDeclaration();
        if (dec==rd) {
            rd = dec.getContainer()
                    .getDirectMember(dec.getName(), 
                            null, false);
        }
        if (rd instanceof Functional &&
            dec instanceof Functional) {
            Functional rf = (Functional) rd;
            Functional f = (Functional) dec;
            List<ParameterList> rdPls = 
                    rf.getParameterLists();
            List<ParameterList> decPls = 
                    f.getParameterLists();
            if (rdPls.isEmpty() || decPls.isEmpty()) {
                return;
            }
            List<Parameter> rdpl =
                    rdPls.get(0)
                        .getParameters();
            List<Parameter> dpl =
                    decPls.get(0)
                        .getParameters();
            Scope decContainer = dec.getContainer();
            Scope rdContainer = rd.getContainer();
            Type supertype;
            if (decContainer instanceof TypeDeclaration &&
                rdContainer instanceof TypeDeclaration) {
                TypeDeclaration dctd = 
                        (TypeDeclaration)
                            decContainer;
                TypeDeclaration rdctd = 
                        (TypeDeclaration)
                            rdContainer;
                supertype =
                        dctd.getType()
                            .getSupertype(rdctd);
            }
            else {
                supertype = null;
            }
            Reference pr =
                    rd.appliedReference(supertype, 
                            Collections.<Type>emptyList());
            List<Tree.Parameter> params =
                    list.getParameters();
            TextFileChange change =
                    new TextFileChange(
                            "Fix Refining Parameter List",
                            file);
            change.setEdit(new MultiTextEdit());
            Unit unit = decNode.getUnit();
            Set<Declaration> declarations = 
                    new HashSet<Declaration>();
            for (int i=0; i<params.size(); i++) {
                Tree.Parameter p = params.get(i);
                if (rdpl.size()<=i) {
                    int start = i==0 ? 
                            list.getStartIndex()+1 : 
                            params.get(i-1)
                                .getEndIndex();
                    int stop =
                            params.get(params.size()-1)
                                .getEndIndex();
                    change.addEdit(new DeleteEdit(start, stop-start));
                    break;
                }
                else {
                    Parameter rdp = rdpl.get(i);
                    Type pt = 
                            pr.getTypedParameter(rdp)
                                .getFullType();
                    Type dt = 
                            dpl.get(i).getModel()
                                .getTypedReference()
                                    .getFullType();
                    if (!dt.isExactly(pt)) {
                        change.addEdit(new ReplaceEdit(
                                p.getStartIndex(),
                                p.getDistance(),
                                //TODO: better handling for callable parameters
                                pt.asSourceCodeString(unit) + " " + rdp.getName()));
                        importProposals().importType(declarations, pt, cu);
                    }
                }
            }
            if (rdpl.size()>params.size()) {
                StringBuilder buf = new StringBuilder();
                for (int i=params.size();
                        i<rdpl.size();
                        i++) {
                    Parameter rdp = rdpl.get(i);
                    if (i>0) {
                        buf.append(", ");
                    }
                    appendParameterText(buf, pr, rdp, unit);
                    Type pt = 
                            pr.getTypedParameter(rdp).getFullType();
                    importProposals().importType(declarations, pt, cu);
                }
                Integer offset =
                        params.isEmpty() ?
                            list.getStartIndex()+1 :
                            params.get(params.size()-1)
                                .getEndIndex();
                change.addEdit(new InsertEdit(offset,
                        buf.toString()));
            }
            TextChange chg2 = new platformJ2C().newChange(change.getName(), change);
            importProposals().applyImports(chg2, declarations, cu, chg2.getDocument());
            if (change.getEdit().hasChildren()) {
                Region selection =
                        new Region(list.getStartIndex()+1,
                                0);
                proposals.add(new CorrectionProposal(
                        "Fix refining parameter list",
                        change, selection));
            }
        }
    }

}
