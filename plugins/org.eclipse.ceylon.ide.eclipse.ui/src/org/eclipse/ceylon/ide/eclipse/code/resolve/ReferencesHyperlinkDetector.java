/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.resolve;

import static org.eclipse.ceylon.ide.eclipse.code.editor.CeylonSourceViewer.SHOW_HIERARCHY;
import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoDeclaration;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getPopupStyle;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedModel;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.code.search.ReferencesPopup;
import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.ClassOrInterface;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Functional;
import org.eclipse.ceylon.model.typechecker.model.ModelUtil;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;

public class ReferencesHyperlinkDetector implements IHyperlinkDetector {
    private static final IHyperlink[] NO_HYPERLINKS = new IHyperlink[0];
    
    private CeylonEditor editor;
    private CeylonParseController controller;
    
    public ReferencesHyperlinkDetector(CeylonEditor editor,
            CeylonParseController controller) {
        this.editor = editor;
        this.controller = controller;
    }
    
    private final class CeylonQuickReferencesLink implements IHyperlink {
        private final Node id;

        private CeylonQuickReferencesLink(Node id) {
            this.id = id;
        }

        @Override
        public void open() {
            //TODO: if there is just one reference,
            //      navigate straight to it?
//            editor.getCeylonSourceViewer()
//                .doOperation(SHOW_REFERENCES);
            ReferencesPopup popup = 
                    new ReferencesPopup(
                            editor.getSite().getShell(), 
                            getPopupStyle(), editor);
            popup.show(false);
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "References" + 
                    CorrectionUtil.shortcut(
                            "org.eclipse.ceylon.ide.eclipse.ui.editor.findReferences");
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }

    private final class CeylonQuickRefinementsLink implements IHyperlink {
        private final Node id;
        private Declaration dec;

        private CeylonQuickRefinementsLink(Node id, Declaration dec) {
            this.id = id;
            this.dec = dec;
        }

        @Override
        public void open() {
            //TODO: if there is just one refinement,
            //      navigate straight to it?
            ReferencesPopup popup = 
                    new ReferencesPopup(
                            editor.getSite().getShell(), 
                            getPopupStyle(), editor);
            popup.show(true);
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            String hint = 
                    CorrectionUtil.shortcut(
                            "org.eclipse.ceylon.ide.eclipse.ui.editor.findReferences");
            if (!hint.isEmpty()) {
                hint = hint + hint.substring(2);
            }
            String action = 
                    dec instanceof TypeDeclaration ? 
                            "Subtypes" : "Refinements";
            return action + hint;
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }

    private final class CeylonQuickHierarchyLink implements IHyperlink {
        private final Node id;

        private CeylonQuickHierarchyLink(Node id) {
            this.id = id;
        }

        @Override
        public void open() {
            editor.getCeylonSourceViewer()
                .doOperation(SHOW_HIERARCHY);
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "Hierarchy" + 
                    CorrectionUtil.shortcut(
                            "org.eclipse.ceylon.ide.eclipse.ui.editor.hierarchy");
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }

    /*private final class CeylonReferencesLink implements IHyperlink {
        private final Referenceable dec;
        private final Node id;

        private CeylonReferencesLink(Referenceable dec, Node id) {
            this.dec = dec;
            this.id = id;
        }

        @Override
        public void open() {
            new FindReferencesAction(editor, dec).run();
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "Find References";
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }*/

    private final class CeylonRefinementLink implements IHyperlink {
        private final Referenceable dec;
        private final Node id;

        private CeylonRefinementLink(Referenceable dec, Node id) {
            this.dec = dec;
            this.id = id;
        }

        @Override
        public void open() {
            gotoDeclaration(dec);
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "Refined Declaration" + 
                    CorrectionUtil.shortcut(
                            "org.eclipse.ceylon.ide.eclipse.ui.action.openRefinedDeclaration");
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }

    private final class CeylonSuperclassLink implements IHyperlink {
        private final Referenceable dec;
        private final Node id;

        private CeylonSuperclassLink(Referenceable dec, Node id) {
            this.dec = dec;
            this.id = id;
        }

        @Override
        public void open() {
            gotoDeclaration(dec);
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "Superclass";
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }

    private final class CeylonTypeLink implements IHyperlink {
        private final Type type;
        private final Node id;

        private CeylonTypeLink(Type type, Node id) {
            this.type = type;
            this.id = id;
        }

        @Override
        public void open() {
            gotoDeclaration(type.getDeclaration());
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "Declared Type";
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }

    /*private final class CeylonHierarchyLink implements IHyperlink {
        private final Declaration dec;
        private final Node id;

        private CeylonHierarchyLink(Declaration dec, Node id) {
            this.dec = dec;
            this.id = id;
        }

        @Override
        public void open() {
            try {
                showHierarchyView().focusOn(dec);
            }
            catch (PartInitException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "Type Hierarchy View";
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }*/

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, 
            IRegion region, boolean canShowMultipleHyperlinks) {
        if (controller==null ||
                controller.getLastCompilationUnit()==null) {
            return null;
        }
        else {
            Node node = 
                    findNode(controller.getLastCompilationUnit(), 
                            controller.getTokens(), 
                            region.getOffset(), 
                            region.getOffset() +
                            region.getLength());
            if (node==null) {
                return null;
            }
            else {
                Node id = getIdentifyingNode(node);
                if (id==null) {
                    return null;
                }
                else {
                    Referenceable referenceable = 
                            getReferencedModel(node);
                    if (referenceable!=null) {
                        if (referenceable instanceof Declaration) {
                            Declaration dec = 
                                    (Declaration) 
                                        referenceable;
                            List<IHyperlink> links = new ArrayList<>();
                            links.add(new CeylonQuickReferencesLink(id));
                            if (dec.isFormal() || dec.isDefault() 
                                    || dec instanceof ClassOrInterface) {
                                links.add(new CeylonQuickRefinementsLink(id, dec));
                            }
                            if (dec.isActual()) {
                                Declaration refined = 
                                        dec.getRefinedDeclaration();
                                if (refined!=null) {
                                    links.add(new CeylonRefinementLink(refined, id));
                                }
                            }
                            if (dec instanceof TypedDeclaration) {
                                boolean isVoid = 
                                        dec instanceof Functional &&
                                        ((Functional) dec).isDeclaredVoid();
                                TypedDeclaration td = 
                                        (TypedDeclaration) dec;
                                Type type = td.getType();
                                if (!isVoid &&
                                        !isTypeUnknown(type) 
                                        && !type.isUnion() 
                                        && !type.isIntersection()) {
                                    links.add(new CeylonTypeLink(type, id));
                                }
                            }
                            if (dec instanceof Class) {
                                Class c = (Class) dec;
                                Type extendedType = c.getExtendedType();
                                if (!ModelUtil.isTypeUnknown(extendedType)) {
                                    links.add(new CeylonSuperclassLink(extendedType.getDeclaration(), id));
                                }
                            }
                            if (dec instanceof ClassOrInterface ||
                                    dec.isActual() || dec.isFormal() || dec.isDefault()) {
                                links.add(new CeylonQuickHierarchyLink(id));
                            }
                            return links.toArray(NO_HYPERLINKS);
                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        return null;
                    }
                }
            }
        }
    }

}
