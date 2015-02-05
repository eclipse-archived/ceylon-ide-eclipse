/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageKeyForNode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getStyledLabelForNode;
import static java.lang.System.identityHashCode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.core.model.SourceFile;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Nodes;

public class CeylonOutlineNode implements IAdaptable {
    
    public static final int ROOT_CATEGORY = -4;
    public static final int PACKAGE_CATEGORY = -3;
    public static final int UNIT_CATEGORY = -2;
    public static final int IMPORT_LIST_CATEGORY = -1;
    public static final int DEFAULT_CATEGORY = 0;

    private final List<CeylonOutlineNode> children = new ArrayList<CeylonOutlineNode>();

    private CeylonOutlineNode parent;

    private final int category;
    private final String id;
    private IResource resource;
    private boolean declaration;
    private boolean shared;
    private int startOffset;
    private int endOffset;
    private String imageKey;
    private StyledString label;
    private int decorations;
    private String name;
    private int realStartOffset;
    private int realEndOffset;

    CeylonOutlineNode(Node treeNode) {
        this(treeNode, DEFAULT_CATEGORY);
    }

    CeylonOutlineNode(Node treeNode, int category) {
        this(treeNode, null, category);
    }
    
    CeylonOutlineNode(Node treeNode, CeylonOutlineNode parent) {
        this(treeNode, parent, DEFAULT_CATEGORY);
    }

    CeylonOutlineNode(Node treeNode, CeylonOutlineNode parent, 
            int category) {
        this(treeNode, parent, category, null);
    }
    
    CeylonOutlineNode(Node treeNode, int category, IResource resource) {
        this(treeNode, null, category, resource);
    }
    
    CeylonOutlineNode(Node treeNode, CeylonOutlineNode parent, 
            int category, IResource resource) {
        this.parent = parent;
        this.category = category;
        this.resource = resource;
        id = createIdentifier(treeNode);
        declaration = treeNode instanceof Tree.Declaration;
        if (declaration) {
            Declaration model = ((Tree.Declaration) treeNode).getDeclarationModel();
            if (model!=null) {
                shared = model.isShared();
            }
            Tree.Identifier identifier = ((Tree.Declaration) treeNode).getIdentifier();
            name = identifier==null ? null : identifier.getText();
        }
        else if (treeNode instanceof Tree.SpecifierStatement) {
            Tree.Term bme = ((Tree.SpecifierStatement) treeNode).getBaseMemberExpression();
            Tree.Identifier id;
            if (bme instanceof Tree.BaseMemberExpression) { 
                id = ((Tree.BaseMemberExpression) bme).getIdentifier();
            }
            else if (bme instanceof Tree.ParameterizedExpression) {
                id = ((Tree.BaseMemberExpression) ((Tree.ParameterizedExpression) bme).getPrimary()).getIdentifier();
            }
            else {
                 throw new RuntimeException("unexpected node type");
            }
            name = id==null ? null : id.getText();
            shared = false;
        }
        else {
            shared = true;
        }
        if (category==DEFAULT_CATEGORY) {
            //span of the "identifying" node
            startOffset = Nodes.getStartOffset(treeNode);
            endOffset = Nodes.getEndOffset(treeNode);
        }
        if (treeNode!=null && 
                !(treeNode instanceof PackageNode)) {
            //whole span of the complete construct
            realStartOffset = treeNode.getStartIndex();
            realEndOffset = treeNode.getStopIndex()+1;
        }
        label = getStyledLabelForNode(treeNode);
        imageKey = getImageKeyForNode(treeNode);
        decorations = CeylonLabelProvider.getNodeDecorationAttributes(treeNode);
    }

    void addChild(CeylonOutlineNode child) {   
        children.add(child);
    }

    public List<CeylonOutlineNode> getChildren() {
        return children;
    }

    public CeylonOutlineNode getParent() {
        return parent;
    }
    
    public boolean isShared() {
        return shared;
    }
    
    public String getName() {
        return name;
    }
    
    public int getCategory() {
        return category;
    }
    
    public boolean isDeclaration() {
        return declaration;
    }
    
    public int getStartOffset() {
        return startOffset;
    }
    
    public int getEndOffset() {
        return endOffset;
    }
    
    public int getRealStartOffset() {
        return realStartOffset;
    }
    
    public int getRealEndOffset() {
        return realEndOffset;
    }
    
    public String getImageKey() {
        return imageKey;
    }
    
    public StyledString getLabel() {
        if (category==DEFAULT_CATEGORY && declaration) {
            IEditorPart currentEditor = EditorUtil.getCurrentEditor();
            if (currentEditor instanceof CeylonEditor) {
                CeylonEditor ce = (CeylonEditor) currentEditor;
                CompilationUnit rootNode = 
                        ce.getParseController().getRootNode();
                if (rootNode!=null) {
                    Node node = Nodes.findNode(rootNode, startOffset);
                    if (node!=null && 
                            node.getStartIndex()==realStartOffset && 
                            node.getStopIndex()+1==realEndOffset) {
                        return getStyledLabelForNode(node);
                    }
                }
            }
        }
        return label;
    }

    public int getDecorations() {
        if (category==DEFAULT_CATEGORY && declaration) {
            IEditorPart currentEditor = EditorUtil.getCurrentEditor();
            if (currentEditor instanceof CeylonEditor) {
                CeylonEditor ce = (CeylonEditor) currentEditor;
                Node node = Nodes.findNode(ce.getParseController().getRootNode(), startOffset);
                if (node!=null) {
                    return CeylonLabelProvider.getNodeDecorationAttributes(node);
                }
            }
        }
        return decorations;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CeylonOutlineNode) {
            CeylonOutlineNode that = (CeylonOutlineNode) obj;
            return that.id.equals(id);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }
    
    public String getIdentifier() {
        return id;
    }
    
    public String createIdentifier(Node treeNode) {
        try {
            //note: we actually have two different outline
            //      nodes that both represent the same
            //      tree node, so we need to use the 
            //      category to distinguish them!
            switch (category) {
            case ROOT_CATEGORY:
                String path;
                Unit unit = treeNode.getUnit();
                if (unit instanceof SourceFile) {
                    path = ((SourceFile) unit).getRelativePath();
                }
                else {
                    path = unit.getFilename();
                }
                return "@root:" + path; 
            case PACKAGE_CATEGORY:
                return "@package:" + ((PackageNode)treeNode).getPackageName();
            case UNIT_CATEGORY:
                return "@unit:" + treeNode.getUnit().getFilename();
            case IMPORT_LIST_CATEGORY:
                return "@importlist:" + treeNode.getUnit().getFilename();
            case DEFAULT_CATEGORY:
            default:
                if (treeNode instanceof Tree.Import) {
                    return "@import:" + 
                            pathToName(((Tree.Import) treeNode).getImportPath(), treeNode);
                }
                else if (treeNode instanceof Tree.Declaration) {
                    Tree.Identifier id = ((Tree.Declaration) treeNode).getIdentifier();
                    String name = id==null ? 
                            String.valueOf(identityHashCode(treeNode)) : 
                            id.getText();
                    if (parent!=null && parent.isDeclaration()) {
                        return getParent().getIdentifier() + ":" + name;
                    }
                    else {
                        return "@declaration:" + name;
                    }
                }
                else if (treeNode instanceof Tree.ImportModule) {
                    return "@importmodule:" + 
                            pathToName(((Tree.ImportModule) treeNode).getImportPath(), treeNode);
                }
                else if (treeNode instanceof Tree.ModuleDescriptor) {
                    return "@moduledescriptor:" + 
                            pathToName(((Tree.ModuleDescriptor) treeNode).getImportPath(), treeNode);
                }
                else if (treeNode instanceof Tree.PackageDescriptor) {
                    return "@packagedescriptor:" + 
                            pathToName(((Tree.PackageDescriptor) treeNode).getImportPath(), treeNode);
                }
                else if (treeNode instanceof Tree.SpecifierStatement) {
                    Tree.Term bme = ((Tree.SpecifierStatement) treeNode).getBaseMemberExpression();
                    Tree.Identifier id;
                    if (bme instanceof Tree.BaseMemberExpression) { 
                        id = ((Tree.BaseMemberExpression) bme).getIdentifier();
                    }
                    else if (bme instanceof Tree.ParameterizedExpression) {
                        id = ((Tree.BaseMemberExpression) ((Tree.ParameterizedExpression) bme).getPrimary()).getIdentifier();
                    }
                    else {
                         throw new RuntimeException("unexpected node type");
                    }
                    String name = id==null ? 
                            String.valueOf(identityHashCode(treeNode)) : 
                            id.getText();
                    if (parent!=null && parent.isDeclaration()) {
                        return getParent().getIdentifier() + ":" + name;
                    }
                    else {
                        return "@declaration:" + name;
                    }
                }
                else {
                    throw new RuntimeException("unexpected node type");
                }
            }
        }
        catch (RuntimeException re) {
            re.printStackTrace();
            return "";
        }
    }
    
    private static String pathToName(Tree.ImportPath importPath, Node treeNode) {
        return importPath==null ? 
                String.valueOf(identityHashCode(treeNode)) : 
                    formatPath(importPath.getIdentifiers());
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if (adapter.equals(IFile.class) || 
            adapter.equals(IResource.class)) {
            return resource;
        }
        else if (adapter.equals(IJavaElement.class) && 
                resource instanceof IFolder) {
            return JavaCore.create(resource);
        }
        else {
            return null;
        }
    }
    
}
