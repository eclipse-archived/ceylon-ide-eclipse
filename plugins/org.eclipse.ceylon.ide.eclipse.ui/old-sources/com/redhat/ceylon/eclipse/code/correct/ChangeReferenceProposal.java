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

import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.completionJ2C;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getDocument;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getOccurrenceLocation;
import static org.eclipse.ceylon.ide.common.util.OccurrenceLocation.IMPORT;
import static java.lang.Character.isUpperCase;
import static java.util.Collections.singleton;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.util.NormalizedLevenshtein;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.common.util.OccurrenceLocation;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.NamedArgumentList;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.ParameterList;
import org.eclipse.ceylon.model.typechecker.model.Scope;

class ChangeReferenceProposal extends CorrectionProposal {
    
    @Deprecated
    private ChangeReferenceProposal(ProblemLocation problem,
            String name, String pkg, TextFileChange change) {
        super("Change reference to '" + name + "'" + pkg, 
                change, 
                new Region(problem.getOffset(), 
                        name.length()), 
                MINOR_CHANGE);
    }
    
    ChangeReferenceProposal(String desc, TextChange change, Region selection) {
        super(desc, change, selection, MINOR_CHANGE);
    }

    @Deprecated
    static void addChangeReferenceProposal(
            ProblemLocation problem,
            Collection<ICompletionProposal> proposals, 
            IFile file, 
            String brokenName, 
            Declaration dec,
            Tree.CompilationUnit rootNode) {
        TextFileChange change = 
                new TextFileChange("Change Reference",
                        file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        String pkg = "";
        int problemOffset = problem.getOffset();
        if (dec.isToplevel() && 
                !importProposals().isImported(dec, rootNode) &&
                isInPackage(rootNode, dec)) {
            String pn = 
                    dec.getContainer()
                        .getQualifiedNameString();
            pkg = " in '" + pn + "'";
            if (!pn.isEmpty() && 
                    !pn.equals(Module.LANGUAGE_MODULE_NAME)) {
                OccurrenceLocation ol = 
                        getOccurrenceLocation(rootNode,
                                findNode(rootNode, problemOffset),
                                problemOffset);
                if (ol!=IMPORT) {
                    List<InsertEdit> ies = 
                            importProposals().importEdits(rootNode,
                                    singleton(dec), 
                                    null, null, doc);
                    for (InsertEdit ie: ies) {
                        change.addEdit(ie);
                    }
                }
            }
        }
        change.addEdit(new ReplaceEdit(problemOffset, 
                brokenName.length(), dec.getName())); //Note: don't use problem.getLength() because it's wrong from the problem list
        proposals.add(new ChangeReferenceProposal(problem, 
                dec.getName(), pkg, change));
    }

    @Deprecated
    protected static boolean isInPackage(
            Tree.CompilationUnit cu, Declaration dec) {
        return !dec.getUnit().getPackage()
                .equals(cu.getUnit().getPackage());
    }

    static void addChangeReferenceProposals(
            Tree.CompilationUnit rootNode,
            Node node, ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IFile file) {
        Node id = getIdentifyingNode(node);
        if (id!=null) {
            String brokenName = id.getText();
            if (brokenName!=null && 
                    !brokenName.isEmpty()) {
                Scope scope = node.getScope();
                Collection<DeclarationWithProximity> dwps = 
                        completionJ2C().getProposals(node, scope, rootNode)
                            .values();
                for (DeclarationWithProximity dwp: dwps) {
                    processProposal(rootNode, problem,
                            proposals, file,
                            brokenName, 
                            dwp.getDeclaration());
                }
            }
        }
    }

    @Deprecated
    static void addChangeArgumentReferenceProposals(
            Tree.CompilationUnit rootNode,
            Node node, 
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IFile file) {
        String brokenName = 
                getIdentifyingNode(node)
                    .getText();
        if (brokenName!=null && 
                !brokenName.isEmpty()) {
            if (node instanceof Tree.NamedArgument) {
                Scope scope = node.getScope();
                if (!(scope instanceof NamedArgumentList)) {
                    scope = scope.getScope(); //for declaration-style named args
                }
                NamedArgumentList namedArgumentList = 
                        (NamedArgumentList) scope;
                ParameterList parameterList = 
                        namedArgumentList.getParameterList();
                if (parameterList!=null) {
                    for (Parameter parameter: 
                            parameterList.getParameters()) {
                        Declaration declaration = 
                                parameter.getModel();
                        if (declaration!=null) {
                            processProposal(rootNode, problem,
                                    proposals, file,
                                    brokenName, 
                                    declaration);
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    private static void processProposal(
            Tree.CompilationUnit rootNode,
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals,
            IFile file, 
            String brokenName, 
            Declaration declaration) {
        String name = declaration.getName();
        if (!brokenName.equals(name)) {
            boolean nuc = 
                    isUpperCase(name.codePointAt(0));
            boolean bnuc = 
                    isUpperCase(brokenName.codePointAt(0));
            if (nuc==bnuc) {
                double similarity = 
                        distance.similarity(brokenName, name);
                //TODO: would it be better to just sort by distance,
                //      and then select the 3 closest possibilities?
                if (similarity > 0.6) {
                    addChangeReferenceProposal(problem, 
                            proposals, file, 
                            brokenName, declaration, 
                            rootNode);
                }
            }
        }
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }
    
    static final NormalizedLevenshtein distance = new NormalizedLevenshtein();
    
}