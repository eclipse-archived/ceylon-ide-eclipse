package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoNode;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedModel;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static com.redhat.ceylon.ide.common.util.toCeylonString_.toCeylonString;
import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getNativeDeclaration;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getNativeHeader;

import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.redhat.ceylon.common.Backends;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.correct.CorrectionUtil;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.ide.common.model.CeylonBinaryUnit;
import com.redhat.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
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
            Backends supportedBackends = supportedBackends();
            String hint = CorrectionUtil.shortcut(
                    "com.redhat.ceylon.eclipse.ui.action.openSelectedDeclaration");
            return "Ceylon Declaration" +
                    (supportedBackends.none() ?
                            hint :
                            " \u2014 " +
                            (supportedBackends.header() ?
                                    "native header" + hint :
                                    supportedBackends +
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
        if (controller==null) {
            return null;
        }
        
        Tree.CompilationUnit rootNode = 
                controller.getLastCompilationUnit();
        if (rootNode==null) {
            return null;
        }
        
        Backends supportedBackends = supportedBackends();
        
        Node node = 
                findNode(rootNode, 
                        controller.getTokens(), 
                        region.getOffset(), 
                        region.getOffset() +
                        region.getLength());
        if (node==null) {
            return null;
        }
        else if (node instanceof Tree.Declaration) {
            Tree.Declaration decNode = 
                    (Tree.Declaration) node;
            if (decNode.getDeclarationModel()
                    .getNativeBackends()
                    .equals(supportedBackends)) {
                return null;
            }
        }
        else if (node instanceof Tree.ImportPath) {
            List<Tree.PackageDescriptor> packageDescriptors = 
                    rootNode.getPackageDescriptors();
            List<Tree.ModuleDescriptor> moduleDescriptors = 
                    rootNode.getModuleDescriptors();
            if (!packageDescriptors.isEmpty() &&
                    packageDescriptors.get(0)
                        .getImportPath()
                            == node 
             || !moduleDescriptors.isEmpty() &&
                    moduleDescriptors.get(0)
                        .getImportPath()
                            == node) {
                return null;
            }
        }

        Node id = getIdentifyingNode(node);
        if (id==null) {
            return null;
        }
        
        Referenceable referenceable = 
                getReferencedModel(node);
        if (referenceable==null) {
            return null;
        }
        if (referenceable instanceof Declaration) {
            Declaration dec = 
                    (Declaration) 
                        referenceable;
            if (dec instanceof TypedDeclaration) {
                Declaration od = dec;
                while (od!=null) {
                    referenceable = dec = od;
                    TypedDeclaration td = 
                            (TypedDeclaration) od;
                    od = td.getOriginalDeclaration();
                }
            }
            if (dec.isNative()) {
                if (supportedBackends.none()) {
                    return null;
                }
                else {
                    referenceable = 
                            resolveNative(referenceable, 
                                    dec, supportedBackends);
                }
            }
            else {
                if (!supportedBackends.none()) {
                    return null;
                }
            }
        }
        else { // Module or package descriptors
            if (!supportedBackends.none()) {
                return null;
            }
        }
        
        Node r = getReferencedNode(referenceable);
        if (r==null) {
            return null;
        }
        else {
            return new IHyperlink[] {
                new CeylonNodeLink(r, id)
            };
        }
        
    }

    private Referenceable resolveNative(
            Referenceable referenceable, 
            Declaration dec, Backends backends) {
        Unit unit = dec.getUnit();
        Scope containerToSearchHeaderIn = null;
        if (unit instanceof CeylonBinaryUnit) {
            CeylonBinaryUnit binaryUnit = 
                    (CeylonBinaryUnit) unit;
            ExternalPhasedUnit phasedUnit = 
                    binaryUnit.getPhasedUnit();
            if (phasedUnit != null) {
                Unit sourceFile = phasedUnit.getUnit();
                if (sourceFile != null) {
                    String sourceRelativePath = 
                            toJavaString(binaryUnit.getCeylonModule()
                                .toSourceUnitRelativePath(
                                        toCeylonString(unit.getRelativePath())));
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
            if (backends.header()) {
                referenceable = headerDeclaration;
            } else {
                if (headerDeclaration != null) {
                    referenceable = 
                            getNativeDeclaration(headerDeclaration, 
                                    supportedBackends());
                }
            }
        }
        return referenceable;
    }

    public Backends supportedBackends() {
        return Backends.ANY;
    }
}
