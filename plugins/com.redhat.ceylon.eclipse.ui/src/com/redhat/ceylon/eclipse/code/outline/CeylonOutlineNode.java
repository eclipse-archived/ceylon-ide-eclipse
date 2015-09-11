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

import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageKeyForNode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getNodeDecorationAttributes;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getStyledLabelForNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJava;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJs;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.core.model.SourceFile;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class CeylonOutlineNode implements IAdaptable {
    
    private static final class ActionFilter implements IActionFilter {
        @Override
        public boolean testAttribute(Object object, String name, String value) {
            CeylonOutlineNode target = (CeylonOutlineNode) object;
            boolean result = false;
            if (target.runnable) {
                if (name.equals("javaRunnable")) {
                    IResource resource = target.getResource();
                    result = resource!=null &&
                            compileToJava(resource.getProject());
                }
                else if (name.equals("jsRunnable")) {
                    IResource resource = target.getResource();
                    result = resource!=null &&
                            compileToJs(resource.getProject());
                }
                else {
                    return false;
                }
            }
            if (value.equals("true")) {
                return result;
            }
            else if (value.equals("false")) {
                return !result;
            }
            else {
                return false;
            }
        }
    }

    public static final int ROOT_CATEGORY = -4;
    public static final int PACKAGE_CATEGORY = -3;
    public static final int UNIT_CATEGORY = -2;
    public static final int IMPORT_LIST_CATEGORY = -1;
    public static final int DEFAULT_CATEGORY = 0;

    private final List<CeylonOutlineNode> children = 
            new ArrayList<CeylonOutlineNode>();

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
    private boolean runnable;

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
            Tree.Declaration treeDeclaration = 
                    (Tree.Declaration) treeNode;
            Declaration model = 
                    treeDeclaration.getDeclarationModel();
            if (model!=null) {
                shared = model.isShared();
            }
            Tree.Identifier identifier = 
                    treeDeclaration.getIdentifier();
            name = identifier==null ? null : identifier.getText();
        }
        else if (treeNode instanceof Tree.SpecifierStatement) {
            Tree.SpecifierStatement treeSpecifier = 
                    (Tree.SpecifierStatement) treeNode;
            Tree.Identifier id = 
                    getIdentifier(treeSpecifier);
            name = id==null ? null : id.getText();
            shared = false;
            declaration = true;
        }
        else {
            shared = true;
        }
        if (category==DEFAULT_CATEGORY) {
            //span of the "identifying" node
            Node identifyingNode = getIdentifyingNode(treeNode);
            startOffset = identifyingNode.getStartIndex();
            endOffset = identifyingNode.getEndIndex();
        }
        if (treeNode!=null && 
                !(treeNode instanceof PackageNode) &&
                !(treeNode instanceof ModuleNode)) {
            //whole span of the complete construct
            realStartOffset = treeNode.getStartIndex();
            realEndOffset = treeNode.getEndIndex();
        }
        label = getStyledLabelForNode(treeNode);
        imageKey = getImageKeyForNode(treeNode);
        decorations = getNodeDecorationAttributes(treeNode);
        if (shared && treeNode instanceof Tree.AnyMethod) {
            Tree.AnyMethod am = (Tree.AnyMethod) treeNode;
            List<Tree.ParameterList> lists = 
                    am.getParameterLists();
            runnable = 
                    lists.size()==1 && 
                    lists.get(0).getParameters().isEmpty();
        }
        else if (shared && treeNode instanceof Tree.AnyClass) {
            Tree.AnyClass ac = (Tree.AnyClass) treeNode;
            Tree.ParameterList list = ac.getParameterList();
            runnable = 
                    list!=null && 
                    list.getParameters().isEmpty();
        }
        else {
            runnable = false;
        }
    }

    private Tree.Identifier getIdentifier(Tree.SpecifierStatement treeNode) {
        Tree.Term t = treeNode.getBaseMemberExpression();
        if (t instanceof Tree.BaseMemberExpression) { 
            Tree.BaseMemberExpression bme = 
                    (Tree.BaseMemberExpression) t;
            return bme.getIdentifier();
        }
        else if (t instanceof Tree.ParameterizedExpression) {
            Tree.ParameterizedExpression pe = 
                    (Tree.ParameterizedExpression) t;
            Tree.BaseMemberExpression bme = 
                    (Tree.BaseMemberExpression) 
                        pe.getPrimary();
            return bme.getIdentifier();
        }
        else {
             throw new RuntimeException("unexpected node type");
        }
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
    
    String getImageKey() {
        return imageKey;
    }
    
    StyledString getLabel() {
        return getLabel(null, null);
    }

    StyledString getLabel(String prefix, Font font) {
        if (category==DEFAULT_CATEGORY && declaration) {
            IEditorPart currentEditor = getCurrentEditor();
            if (currentEditor instanceof CeylonEditor) {
                CeylonEditor ce = 
                        (CeylonEditor) currentEditor;
                Tree.CompilationUnit rootNode = 
                        ce.getParseController()
                            .getRootNode();
                if (rootNode!=null) {
                    Node node = 
                            findNode(rootNode, 
                                    startOffset, endOffset);
                    if (!(node instanceof Tree.Declaration)) {
                        node = findStatement(rootNode, node);
                    }
                    if (node!=null && 
                            node.getStartIndex()==realStartOffset && 
                            node.getEndIndex()==realEndOffset) {
                        return getStyledLabelForNode(node, prefix, font);
                    }
                }
            }
        }
        return label;
    }
    
    int getDecorations() {
        if (category==DEFAULT_CATEGORY && declaration) {
            IEditorPart currentEditor = getCurrentEditor();
            if (currentEditor instanceof CeylonEditor) {
                CeylonEditor ce = 
                        (CeylonEditor) currentEditor;
                Tree.CompilationUnit rootNode = 
                        ce.getParseController()
                            .getRootNode();
                if (rootNode!=null) {
                    Node node = 
                            findNode(rootNode, 
                                    startOffset, endOffset);
                    if (!(node instanceof Tree.Declaration)) {
                        node = findStatement(rootNode, node);
                    }
                    if (node!=null) {
                        return getNodeDecorationAttributes(node);
                    }
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
                    SourceFile sf = (SourceFile) unit;
                    path = sf.getRelativePath();
                }
                else {
                    path = unit.getFilename();
                }
                return "@root:" + path; 
            case PACKAGE_CATEGORY:
                if (treeNode instanceof PackageNode) {
                    PackageNode pn = (PackageNode) treeNode;
                    String packageName = pn.getPackageName();
                    return "@package:" + packageName;
                }
                else {
                    ModuleNode mn = (ModuleNode) treeNode;
                    String moduleName = mn.getModuleName();
                    return "@module:" + moduleName;
                }
            case UNIT_CATEGORY:
                return "@unit:" + 
                    treeNode.getUnit().getFilename();
            case IMPORT_LIST_CATEGORY:
                return "@importlist:" + 
                    treeNode.getUnit().getFilename();
            case DEFAULT_CATEGORY:
            default:
                if (treeNode instanceof Tree.Import) {
                    Tree.Import imp = (Tree.Import) treeNode;
                    Tree.ImportPath packageName = 
                            imp.getImportPath();
                    return "@import:" + 
                        pathToName(packageName, treeNode);
                }
                else if (treeNode instanceof Tree.Declaration) {
                    Tree.Declaration dec = 
                            (Tree.Declaration) treeNode;
                    Tree.Identifier id = 
                            dec.getIdentifier();
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
                    Tree.ImportModule im = 
                            (Tree.ImportModule) treeNode;
                    Tree.QuotedLiteral ql = im.getQuotedLiteral();
                    if (ql!=null) {
                        return "@importmodule:" + ql.getText();
                    }
                    else {
                        Tree.ImportPath moduleName = im.getImportPath();
                        return "@importmodule:" + 
                            pathToName(moduleName, treeNode);
                    }
                }
                else if (treeNode instanceof Tree.ModuleDescriptor) {
                    Tree.ModuleDescriptor md = 
                            (Tree.ModuleDescriptor) treeNode;
                    Tree.ImportPath moduleName = md.getImportPath();
                    return "@moduledescriptor:" + 
                        pathToName(moduleName, treeNode);
                }
                else if (treeNode instanceof Tree.PackageDescriptor) {
                    Tree.PackageDescriptor pd = 
                            (Tree.PackageDescriptor) treeNode;
                    Tree.ImportPath packageName = pd.getImportPath();
                    return "@packagedescriptor:" + 
                        pathToName(packageName, treeNode);
                }
                else if (treeNode instanceof Tree.SpecifierStatement) {
                    Tree.SpecifierStatement ss = 
                            (Tree.SpecifierStatement) treeNode;
                    Tree.Identifier id = getIdentifier(ss);
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
    
    private static String pathToName(Tree.ImportPath importPath, 
            Node treeNode) {
        return importPath==null ? 
                String.valueOf(identityHashCode(treeNode)) : 
                    formatPath(importPath.getIdentifiers());
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }
    
    private IResource getResource() {
        if (resource!=null) {
            return resource;
        }
        else if (parent!=null) {
            return parent.getResource();
        }
        else {
            return null;
        }
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
        else if (adapter.equals(IActionFilter.class)) {
            return new ActionFilter();
        }
        else {
            return null;
        }
    }
    
}
