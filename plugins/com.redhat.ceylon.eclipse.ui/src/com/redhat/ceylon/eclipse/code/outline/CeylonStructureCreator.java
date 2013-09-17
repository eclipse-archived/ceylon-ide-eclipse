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

import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ISharedDocumentAdapter;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.compare.structuremergeviewer.StructureCreator;
import org.eclipse.compare.structuremergeviewer.StructureRootNode;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ILabelProvider;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

/**
 * @author rfuhrer
 */
public class CeylonStructureCreator extends StructureCreator {
	
    private final ILabelProvider labelProvider = new CeylonLabelProvider(true);
    
    @Override
    public String getName() {
        return "Ceylon Structure Compare";
    }

    @Override
    protected IStructureComparator createStructureComparator(Object input, 
    		IDocument document, ISharedDocumentAdapter sharedDocumentAdapter, 
    		IProgressMonitor monitor) 
    				throws CoreException {

        final boolean isEditable = input instanceof IEditableContent ?
                ((IEditableContent) input).isEditable() : false;
        
        StructureRootNode structureRootNode = new StructureRootNode(document, 
                    input, this, sharedDocumentAdapter) {
            @Override
            public boolean isEditable() {
                return isEditable;
            }
        };
        
    	CeylonParseController pc= new CeylonParseController();
    	CeylonOutlineBuilder builder= new CeylonOutlineBuilder();
    	
    	if (input instanceof ResourceNode) {
    	    IResource file = ((ResourceNode) input).getResource();
    	    pc.initialize(file.getProjectRelativePath(), file.getProject(), null);
    	}
    	else {
    	    pc.initialize(null, null, null);
    	}
    	
    	pc.parse(document, monitor, null);
    	
    	Node rootNode = pc.getRootNode();
    	
    	if (rootNode!=null) {
    		// now visit the model, creating TreeCompareNodes for each ModelTreeNode
    		buildCompareTree(builder.buildTree(rootNode), structureRootNode, document);
    	}
    	
        return structureRootNode;

    }

    private CeylonDocumentRangeNode buildCompareTree(CeylonOutlineNode outlineNode, 
    		DocumentRangeNode parent, IDocument document) {
        CeylonDocumentRangeNode compareNode = new CeylonDocumentRangeNode(labelProvider, 
                parent, outlineNode, document);
        parent.addChild(compareNode);
        for (CeylonOutlineNode treeChild: outlineNode.getChildren()) {
            buildCompareTree(treeChild, compareNode, document);
        }
        return compareNode;
    }

    @Override
    public String getContents(Object node, boolean ignoreWhitespace) {
        if (node instanceof IStreamContentAccessor) {
            IStreamContentAccessor sca = (IStreamContentAccessor) node;
            try {
                String contents = readString(sca);
                return ignoreWhitespace ? contents.replaceAll("\\w+", " ") : contents;
            } 
            catch (CoreException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static String readString(InputStream is, String encoding) {
        if (is == null)
            return null;
        BufferedReader reader= null;
        try {
            StringBuffer buffer= new StringBuffer();
            char[] part= new char[2048];
            int read= 0;
            reader= new BufferedReader(new InputStreamReader(is, encoding));

            while ((read= reader.read(part)) != -1)
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
        InputStream is= sa.getContents();
        if (is != null) {
            String encoding= null;
            if (sa instanceof IEncodedStreamContentAccessor) {
                try {
                    encoding= ((IEncodedStreamContentAccessor) sa).getCharset();
                } catch (Exception e) {
                }
            }
            if (encoding == null)
                encoding= ResourcesPlugin.getEncoding();
            return readString(is, encoding);
        }
        return null;
    }

}
