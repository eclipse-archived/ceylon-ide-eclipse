package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer.SHOW_HIERARCHY;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPopupStyle;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedModel;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.correct.CorrectionUtil;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.search.ReferencesPopup;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;

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
                            "com.redhat.ceylon.eclipse.ui.editor.findReferences");
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                              id.getDistance());
        }
    }

    private final class CeylonQuickRefinementsLink implements IHyperlink {
        private final Node id;

        private CeylonQuickRefinementsLink(Node id) {
            this.id = id;
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
                            "com.redhat.ceylon.eclipse.ui.editor.findReferences");
            return "Refinements" + hint + hint.substring(2);
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
                            "com.redhat.ceylon.eclipse.ui.editor.hierarchy");
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
                            "com.redhat.ceylon.eclipse.ui.action.openRefinedDeclaration");
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
                            if (dec.isFormal() || dec.isDefault()) {
                                links.add(new CeylonQuickRefinementsLink(id));
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
