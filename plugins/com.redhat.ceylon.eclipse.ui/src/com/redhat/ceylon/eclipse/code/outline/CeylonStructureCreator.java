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
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;

/**
 * @author rfuhrer
 */
public class CeylonStructureCreator extends StructureCreator {
    public class TreeCompareNode extends DocumentRangeNode implements ITypedElement {
        private final Node fASTNode;

        /**
         * @param treeNode
         * @param document
         */
        public TreeCompareNode(CeylonOutlineNode treeNode, IDocument document) {
            this(null, treeNode, document);
        }

        /**
         * @param parent
         * @param treeNode
         * @param document
         */
        public TreeCompareNode(DocumentRangeNode parent, CeylonOutlineNode treeNode, 
        		IDocument document) {
            super(parent, fCompareNodeIdentifier.getTypeCode(treeNode.getASTNode()), 
                    fCompareNodeIdentifier.getID(treeNode.getASTNode()), document,
                    fSrcPositionLocator.getStartOffset(treeNode.getASTNode()), 
                    fSrcPositionLocator.getLength(treeNode.getASTNode()));
            fASTNode= treeNode.getASTNode();
        }

        @Override
        public String toString() {
            return getTypeCode() + ":" + getId();
        }

        public Image getImage() {
            return fLabelProvider!=null ? 
            		fLabelProvider.getImage(fASTNode) : null;
        }

        public String getName() {
            return fLabelProvider!=null ? 
            		fLabelProvider.getText(fASTNode) : toString();
        }

        public String getType() {
            return "?type?";
        }
    }

    private CeylonCompareNodeIdentifier fCompareNodeIdentifier;
    private CeylonLabelProvider fLabelProvider;
    private CeylonSourcePositionLocator fSrcPositionLocator;

    public String getName() {
        return "Structural Comparison";
    }

    @Override
    protected IStructureComparator createStructureComparator(Object input, 
    		IDocument document, ISharedDocumentAdapter sharedDocumentAdapter, 
    		IProgressMonitor monitor) 
    				throws CoreException {

    	//ServiceFactory svcFactory= ServiceFactory.getInstance();
    	CeylonParseController pc= new CeylonParseController();
    	fCompareNodeIdentifier= new CeylonCompareNodeIdentifier();
    	fLabelProvider= new CeylonLabelProvider();
    	CeylonTreeModelBuilder builder= new CeylonTreeModelBuilder();
    	fSrcPositionLocator= pc.getSourcePositionLocator();
        
    	//TODO: pass some more info in here!
    	pc.initialize(null, null, null);

    	Node astRoot= (Node) pc.parse(document.get(), monitor);
    	DocumentRangeNode compareRoot;

    	if (astRoot!=null) {
    		// now visit the model, creating TreeCompareNodes for each ModelTreeNode
    		compareRoot= buildCompareTree(builder.buildTree(astRoot),
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
        for(CeylonOutlineNode treeChild: treeNode.getChildren()) {
            compareNode.addChild(buildCompareTree(treeChild, compareNode, document));
        }
        return compareNode;
    }

    public String getContents(Object node, boolean ignoreWhitespace) {
        if (node instanceof IStreamContentAccessor) {
            IStreamContentAccessor sca = (IStreamContentAccessor) node;
            try {
                return readString(sca);
            } catch (CoreException ex) {
            }
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

        } catch (IOException ex) {
            // NeedWork
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // silently ignored
                }
            }
        }
        return null;
    }

    public static String readString(IStreamContentAccessor sa) throws CoreException {
        InputStream is = sa.getContents();
        if (is != null) {
            String encoding = null;
            if (sa instanceof IEncodedStreamContentAccessor) {
                try {
                    encoding = ((IEncodedStreamContentAccessor) sa).getCharset();
                } catch (Exception e) {
                }
            }
            if (encoding == null)
                encoding = ResourcesPlugin.getEncoding();
            return readString(is, encoding);
        }
        return null;
    }
}
