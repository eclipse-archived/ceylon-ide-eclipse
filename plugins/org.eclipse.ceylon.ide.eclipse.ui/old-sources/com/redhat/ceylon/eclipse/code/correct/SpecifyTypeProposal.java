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
import static org.eclipse.ceylon.ide.eclipse.code.correct.SpecifyTypeArgumentsProposal.addSpecifyTypeArgumentsProposal;
import static org.eclipse.ceylon.ide.eclipse.code.correct.TypeProposal.getTypeProposals;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.REVEAL;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.platform.platformJ2C;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.eclipse.util.LinkedMode;
import org.eclipse.ceylon.ide.common.platform.ReplaceEdit;
import org.eclipse.ceylon.ide.common.platform.TextChange;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Type;

@Deprecated
public class SpecifyTypeProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final Type infType;
    private final String desc;
    private final Tree.Type typeNode;
    private CeylonEditor editor;
    private final Tree.CompilationUnit rootNode;
    private Point selection;
    
    private SpecifyTypeProposal(String desc, Tree.Type type,
            Tree.CompilationUnit cu, Type infType, 
            CeylonEditor editor) {
        this.desc = desc;
        this.typeNode = type;
        this.rootNode = cu;
        this.infType = rootNode.getUnit().denotableType(infType);
        this.editor = editor;
    }
    
    @Override
    public void apply(IDocument document) {
        int offset = typeNode.getStartIndex();
        int length = typeNode.getDistance();
        if (editor==null) {
            IEditorPart ed = getCurrentEditor();
            if (ed instanceof CeylonEditor) {
                editor = (CeylonEditor) ed;
            }
        }
        if (editor==null) {
            if (typeNode instanceof Tree.LocalModifier) {
                TextChange change = new platformJ2C().newChange("Specify Type", document);
                change.initMultiEdit();
                HashSet<Declaration> decs = new HashSet<Declaration>();
                importProposals().importType(decs, infType, rootNode);
                int il = (int) importProposals().applyImports(change, decs, rootNode, change.getDocument());
                String typeName = 
                        infType.asSourceCodeString(rootNode.getUnit());
                change.addEdit(new ReplaceEdit(offset, length, typeName));
                change.apply();
                offset += il;
                length = typeName.length();
                selection = new Point(offset, length);
            }
        }
        else {
            LinkedModeModel linkedModeModel = new LinkedModeModel();
            ProposalPosition linkedPosition = 
                    getTypeProposals(document, offset, length, 
                            infType, rootNode, null);
            try {
                LinkedMode.addLinkedPosition(linkedModeModel, linkedPosition);
                LinkedMode.installLinkedMode(editor, document, linkedModeModel, 
                        this, new DeleteBlockingExitPolicy(document), NO_STOP, -1);
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    static void addSpecifyTypeProposal(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, CeylonEditor editor) {
        for (SpecifyTypeProposal proposal: createProposals(cu, node, editor)) {
            proposals.add(proposal);
        }
    }
    
    public static SpecifyTypeProposal createProposal(Tree.CompilationUnit cu, 
            Node node, CeylonEditor editor) {
        Tree.Type type = (Tree.Type) node;
        return new SpecifyTypeProposal("Declare explicit type", 
                type, cu, type.getTypeModel(), editor);
    }

    public static List<SpecifyTypeProposal> createProposals(Tree.CompilationUnit cu, 
            Node node, CeylonEditor editor) {
        final Tree.Type type = (Tree.Type) node;
        InferredType result = inferType(cu, type);
        List<SpecifyTypeProposal> list = 
                new ArrayList<SpecifyTypeProposal>(2);
        Type declaredType = type.getTypeModel();
        if (!isTypeUnknown(declaredType)) {
            if (!isTypeUnknown(result.generalizedType) &&
                    (isTypeUnknown(result.inferredType) || 
                            !result.generalizedType.isSubtypeOf(result.inferredType)) &&
                            !result.generalizedType.isSubtypeOf(declaredType)) {
                list.add(new SpecifyTypeProposal("Widen type to", 
                        type, cu, result.generalizedType, editor));
            }
            if (!isTypeUnknown(result.inferredType)) {
                if (!result.inferredType.isSubtypeOf(declaredType)) {
                    list.add(new SpecifyTypeProposal("Change type to", type, cu,
                            result.inferredType, editor));
                }
                else if (!declaredType.isSubtypeOf(result.inferredType)) {
                    list.add(new SpecifyTypeProposal("Narrow type to", type, cu,
                            result.inferredType, editor));
                }
            }
            if (type instanceof Tree.LocalModifier) {
                list.add(new SpecifyTypeProposal("Declare explicit type", 
                        type, cu, declaredType, editor));
            }
        }
        else {
            if (!isTypeUnknown(result.inferredType)) {
                list.add(new SpecifyTypeProposal("Declare type", type, cu,
                        result.inferredType, editor));
            }
            if (!isTypeUnknown(result.generalizedType) && 
                    (isTypeUnknown(result.inferredType) ||
                            !result.generalizedType.isSubtypeOf(result.inferredType))) {
                list.add(new SpecifyTypeProposal("Declare type", type, cu,
                        result.generalizedType, editor));
            }
        }
        return list;
    }

    static InferredType inferType(Tree.CompilationUnit cu,
            final Tree.Type type) {
        InferTypeVisitor itv = new InferTypeVisitor(type.getUnit()) {
            @Override 
            public void visit(Tree.TypedDeclaration that) {
                if (that.getType()==type) {
                    dec = that.getDeclarationModel();
//                    union(that.getType().getTypeModel());
                }
                super.visit(that);
            }            
        };
        itv.visit(cu);
        return itv.result;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), false);
    }

    @Override
    public Point getSelection(IDocument document) {
        return selection;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        String type = infType.asString(rootNode.getUnit());
        return desc + " '" + type + "'";
    }

    @Override
    public Image getImage() {
        return REVEAL;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    static void addTypingProposals(Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, Node node,
            Tree.Declaration decNode, CeylonEditor editor) {
        if (decNode instanceof Tree.TypedDeclaration && 
                !(decNode instanceof Tree.ObjectDefinition) &&
                !(decNode instanceof Tree.Variable)) {
            Tree.Type type = ((Tree.TypedDeclaration) decNode).getType();
            if (type instanceof Tree.LocalModifier || 
                    type instanceof Tree.StaticType) {
                addSpecifyTypeProposal(cu, type, proposals, editor);
            }
        }
        else if (node instanceof Tree.LocalModifier || 
                node instanceof Tree.StaticType) {
            addSpecifyTypeProposal(cu, node, proposals, editor);
        }
        if (node instanceof Tree.MemberOrTypeExpression) {
            addSpecifyTypeArgumentsProposal(cu, node, proposals, file);
        }
    }
    
}
