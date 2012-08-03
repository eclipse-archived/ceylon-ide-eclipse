/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/
package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ISharedDocumentAdapter;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.compare.structuremergeviewer.StructureCreator;
import org.eclipse.compare.structuremergeviewer.StructureRootNode;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

/**
 * @author rfuhrer
 */
public class CeylonStructureCreator extends StructureCreator {
	
    public class TreeCompareNode extends DocumentRangeNode 
            implements ITypedElement {
    	
        private final Node astNode;

        public TreeCompareNode(CeylonOutlineNode treeNode, IDocument document) {
            this(null, treeNode, document);
        }

        public TreeCompareNode(DocumentRangeNode parent, CeylonOutlineNode treeNode, 
        		IDocument document) {
            super(parent, CeylonStructureCreator.getTypeCode(treeNode.getTreeNode()), 
                    getID(treeNode.getTreeNode()), document,
                    getStartOffset(treeNode.getTreeNode()), 
                    getLength(treeNode.getTreeNode()));
            astNode= treeNode.getTreeNode();
        }

        @Override
        public String toString() {
            return getTypeCode() + ":" + getId();
        }

        @Override
        public Image getImage() {
            return fLabelProvider!=null ? 
            		fLabelProvider.getImage(astNode) : null;
        }

        @Override
        public String getName() {
            return fLabelProvider!=null ? 
            		fLabelProvider.getText(astNode) : toString();
        }

        @Override
        public String getType() {
            return "?type?";
        }
    }

    private CeylonLabelProvider fLabelProvider;
    
    static int getTypeCode(Object o) {
        if (o instanceof CeylonOutlineNode) {
            o = ((CeylonOutlineNode) o).getTreeNode();
        }
        if (o instanceof Tree.Declaration) {
            return ((Tree.Declaration) o).getDeclarationModel()
                    .getDeclarationKind().ordinal();
        }
        else if (o instanceof Tree.CompilationUnit) {
            return 100;
        }
        else {
            return -1;
        }
    }
    
    static String getID(Object o) {
        if (o instanceof CeylonOutlineNode) {
            o = ((CeylonOutlineNode) o).getTreeNode();
        }
        if (o instanceof Tree.Declaration) {
            return ((Tree.Declaration) o).getDeclarationModel().getQualifiedNameString();
        }
        else if (o instanceof Tree.CompilationUnit) {
            return ((Tree.CompilationUnit) o).getUnit().getFilename();
        }
        else {
            return o.toString();
        }
    }

    @Override
    public String getName() {
        return "Ceylon Structural Comparison";
    }

    @Override
    protected IStructureComparator createStructureComparator(Object input, 
    		IDocument document, ISharedDocumentAdapter sharedDocumentAdapter, 
    		IProgressMonitor monitor) 
    				throws CoreException {

    	CeylonParseController pc= new CeylonParseController();
    	fLabelProvider= new CeylonLabelProvider();
    	CeylonOutlineBuilder builder= new CeylonOutlineBuilder();
        
    	//TODO: pass some more info in here!
    	pc.initialize(null, null, null);

    	pc.parse(document.get(), monitor, null);
    	Node rootNode = pc.getRootNode();
    	DocumentRangeNode compareRoot;

    	if (rootNode!=null) {
    		// now visit the model, creating TreeCompareNodes for each ModelTreeNode
    		compareRoot= buildCompareTree(builder.buildTree(rootNode),
    				null, document);
    	} 
    	else {
    		compareRoot= new StructureRootNode(document, input, this, 
    				sharedDocumentAdapter);
    	}
    	return compareRoot;

    }

    private TreeCompareNode buildCompareTree(CeylonOutlineNode treeNode, 
    		DocumentRangeNode parent, IDocument document) {
        TreeCompareNode compareNode= new TreeCompareNode(parent, treeNode, document);
        for (CeylonOutlineNode treeChild: treeNode.getChildren()) {
            compareNode.addChild(buildCompareTree(treeChild, compareNode, document));
        }
        return compareNode;
    }

    @Override
    public String getContents(Object node, boolean ignoreWhitespace) {
        if (node instanceof IStreamContentAccessor) {
            IStreamContentAccessor sca = (IStreamContentAccessor) node;
            try {
                return readString(sca);
            } 
            catch (CoreException ex) {}
        }
        return null;
    }

    private static String readString(InputStream is, String encoding) {
        if (is == null)
            return null;
        BufferedReader reader = null;
        try {
            StringBuffer buffer = new StringBuffer();
            char[] part = new char[2048];
            int read = 0;
            reader = new BufferedReader(new InputStreamReader(is, encoding));
            while ((read = reader.read(part)) != -1)
                buffer.append(part, 0, read);
            return buffer.toString();

        } 
        catch (IOException ex) {
            // NeedWork
        } 
        finally {
            if (reader!=null) {
                try {
                    reader.close();
                } 
                catch (IOException ex) {}
            }
        }
        return null;
    }

    public static String readString(IStreamContentAccessor sa) throws CoreException {
        InputStream is = sa.getContents();
        if (is!=null) {
            String encoding = null;
            if (sa instanceof IEncodedStreamContentAccessor) {
                try {
                    encoding = ((IEncodedStreamContentAccessor) sa).getCharset();
                } 
                catch (Exception e) {}
            }
            if (encoding==null) {
                encoding = ResourcesPlugin.getEncoding();
            }
            return readString(is, encoding);
        }
        return null;
    }
}
