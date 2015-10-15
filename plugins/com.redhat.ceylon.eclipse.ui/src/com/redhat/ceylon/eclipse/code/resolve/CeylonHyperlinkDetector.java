package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoNode;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedModel;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getNativeDeclaration;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getNativeHeader;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Unit;


public class CeylonHyperlinkDetector implements IHyperlinkDetector {
    private CeylonEditor editor;
    private CeylonParseController controller;
    
    public CeylonHyperlinkDetector(CeylonEditor editor,
            CeylonParseController controller) {
        this.editor = editor;
        this.controller = controller;
    }

    private final class CeylonNodeLink implements IHyperlink {
        private final Node node;
        private final Node id;

        private CeylonNodeLink(Node node, Node id) {
            this.node = node;
            this.id = id;
        }

        @Override
        public void open() {
            gotoNode(node, editor);
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            Backend supportedBackend = supportedBackend();
            return "Ceylon Declaration" + 
                    (supportedBackend == null ? 
                            "" : 
                            " \u2014 " +
                            (Backend.None.equals(supportedBackend) ? 
                                    "native header" :
                                        supportedBackend.name + 
                                        " backend implementation"));
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), id.getDistance());
        }
    }

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
                    if (referenceable instanceof Declaration) {
                        Declaration dec = 
                                (Declaration) 
                                    referenceable;
                        Backend supportedBackend = supportedBackend();
                        if (dec.isNative()) {
                            if (supportedBackend==null) {
                                return null;
                            }
                            else {
                                referenceable = 
                                        resolveNative(referenceable, 
                                                dec, supportedBackend);
                            }
                        }
                        else {
                            if (supportedBackend!=null) {
                                return null;
                            }
                        }
                    }
                    Node r = getReferencedNode(referenceable);
                    if (r==null) {
                        return null;
                    }
                    else {
                        return new IHyperlink[] { new CeylonNodeLink(r, id) };
                    }
                }
            }
        }
    }

    private Referenceable resolveNative(
            Referenceable referenceable, 
            Declaration dec, Backend backend) {
        Unit unit = dec.getUnit();
        Scope containerToSearchHeaderIn = null;
        if (unit instanceof CeylonBinaryUnit) {
            CeylonBinaryUnit binaryUnit = 
                    (CeylonBinaryUnit) unit;
            ExternalPhasedUnit phasedUnit = 
                    binaryUnit.getPhasedUnit();
            if (phasedUnit != null) {
                ExternalSourceFile sourceFile = 
                        phasedUnit.getUnit();
                if (sourceFile != null) {
                    String sourceRelativePath = 
                            binaryUnit.getModule()
                                .toSourceUnitRelativePath(
                                        unit.getRelativePath());
                    if (sourceRelativePath != null && 
                            sourceRelativePath.endsWith(".ceylon")) {
                        for (Declaration sourceDecl: 
                                sourceFile.getDeclarations()) {
                            if (sourceDecl.equals(dec)) {
                                containerToSearchHeaderIn = 
                                        sourceDecl.getContainer();
                                break;
                            }
                        }
                    } else {
                        for (Declaration sourceDecl: 
                                sourceFile.getDeclarations()) {
                            if (sourceDecl.getQualifiedNameString()
                                    .equals(dec.getQualifiedNameString())) {
                                containerToSearchHeaderIn = 
                                        sourceDecl.getContainer();
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            containerToSearchHeaderIn = dec.getContainer();
        }

        if (containerToSearchHeaderIn != null) {
            Declaration headerDeclaration = 
                    getNativeHeader(containerToSearchHeaderIn, 
                            dec.getName());
            if (headerDeclaration == null 
                    || ! headerDeclaration.isNative()) return null;
            if (Backend.None.equals(backend)) {
                referenceable = headerDeclaration;
            } else {
                if (headerDeclaration != null) {
                    referenceable = 
                            getNativeDeclaration(headerDeclaration, 
                                    supportedBackend());
                }
            }
        }
        return referenceable;
    }

    public Backend supportedBackend() {
        return null;
    }
}
