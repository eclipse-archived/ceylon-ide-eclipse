/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.resolve;

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoNode;
import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.resolveNative;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedModel;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedNode;

import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import org.eclipse.ceylon.common.Backends;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;

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
                    "org.eclipse.ceylon.ide.eclipse.ui.action.openSelectedDeclaration");
            return "Declaration" +
                    (supportedBackends.none() ?
                            hint :
                            " \u2014 " +
                            (supportedBackends.header() ?
                                    "native header" + hint :
                                    supportedBackends +
                                    " implementation"));
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
            boolean syntheticVar = 
                    node instanceof Tree.Variable &&
                    ((Tree.Variable) node).getType() 
                        instanceof Tree.SyntheticVariable;
            if (!syntheticVar) {
                Tree.Declaration decNode = 
                        (Tree.Declaration) node;
                if (decNode.getDeclarationModel()
                        .getNativeBackends()
                        .equals(supportedBackends)) {
                    //we're already at the declaration itself
                    return null;
                }
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
                //we're already at the descriptor for
                //the module or package
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
            //look for the "original" declaration, 
            //ignoring narrowing synthetic declarations
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
                //for native declarations, each subclass of 
                //this hyperlink detector resolves to a 
                //different native header or impl
                referenceable = 
                            resolveNative(dec, 
                                    supportedBackends);
            }
            else {
                //for other declarations, the subclasses of
                //this hyperlink detector are disabled
                if (!supportedBackends.none()) {
                    return null;
                }
            }
        }
        else {
            //for module or package descriptors, the 
            //subclasses of this hyperlink detector are 
            //disabled
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

    public Backends supportedBackends() {
        return Backends.ANY;
    }
}
