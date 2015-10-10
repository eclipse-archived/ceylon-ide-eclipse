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

import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.DEFAULT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.IMPORT_LIST_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.ROOT_CATEGORY;

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

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

/**
 * @author rfuhrer
 */
public class CeylonStructureCreator extends StructureCreator {
    
    @Override
    public String getName() {
        return "Ceylon Structure Compare";
    }

    @Override
    protected IStructureComparator createStructureComparator(Object input, 
            IDocument document, ISharedDocumentAdapter sharedDocumentAdapter, 
            IProgressMonitor monitor) 
                    throws CoreException {
        
        if (input instanceof CeylonDocumentRangeNode) {
            return (CeylonDocumentRangeNode) input;
        }
        
        final boolean isEditable;
        if (input instanceof IEditableContent) {
            IEditableContent ec = (IEditableContent) input;
            isEditable = ec.isEditable();
        }
        else {
            isEditable = false;
        }
        
        StructureRootNode structureRootNode = 
                new StructureRootNode(document, 
                        input, this, sharedDocumentAdapter) {
            @Override
            public boolean isEditable() {
                return isEditable;
            }
        };
        
        CeylonParseController pc = new CeylonParseController();
        if (input instanceof ResourceNode) {
            ResourceNode node = (ResourceNode) input;
            IResource file = node.getResource();
            pc.initialize(file.getProjectRelativePath(), 
                    file.getProject(), null);
        }
        else {
            pc.initialize(null, null, null);
        }
        
        if (pc.parseAndTypecheck(document, 10, monitor, null) != null) {
            // now visit the model, creating TreeCompareNodes for each ModelTreeNode
            CeylonOutlineNode tree = 
                    new CeylonOutlineBuilder() {
                        //don't create nodes for shortcut refinement
                        //because we can't distinguish them w/o a
                        //full typecheck
                        public void visit(Tree.SpecifierStatement that) {}
                    }.buildTree(pc);
            if (tree!=null) {
                buildCompareTree(tree, structureRootNode, document);
            }
        }
        
        return structureRootNode;

    }

    private void buildCompareTree(CeylonOutlineNode outlineNode, 
            DocumentRangeNode parent, IDocument document) {
        CeylonDocumentRangeNode compareNode;
        switch (outlineNode.getCategory()) {
        case ROOT_CATEGORY: //attach children of the unit node directly to our root
            compareNode = new CeylonDocumentRangeNode(parent, 
                    "@root", outlineNode, document) {
                @Override
                public String getName() {
                    return "Ceylon Source File";
                }
            };
            break;
        case IMPORT_LIST_CATEGORY:
            compareNode = new CeylonDocumentRangeNode(parent, 
                    "@importlist", outlineNode, document);
            break;
        case DEFAULT_CATEGORY:
            compareNode = new CeylonDocumentRangeNode(parent, 
                    outlineNode.getIdentifier(), outlineNode, document);
            break;
        default:
            // The outline view has some extra nodes 
            // we don't care about so just do nothing
            return;
        }
        for (CeylonOutlineNode treeChild: outlineNode.getChildren()) {            
            buildCompareTree(treeChild, compareNode, document);
        }
        parent.addChild(compareNode);
    }
    
    @Override
    public String getContents(Object node, boolean ignoreWhitespace) {
        if (node instanceof IStreamContentAccessor) {
            IStreamContentAccessor sca = (IStreamContentAccessor) node;
            try {
                String contents = readString(sca);
                return ignoreWhitespace ? 
                        contents.replaceAll("\\p{javaWhitespace}+", " ") : 
                        contents;
            } 
            catch (CoreException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static String readString(InputStream is, String encoding) {
        if (is == null) {
            return null;
        }
        BufferedReader reader = null;
        try {
            StringBuffer buffer= new StringBuffer();
            char[] part = new char[2048];
            int read = 0;
            reader = new BufferedReader(new InputStreamReader(is, encoding));
            while ((read= reader.read(part)) != -1) {
                buffer.append(part, 0, read);
            }
            return buffer.toString();

        }
        catch (IOException ex) {
            // NeedWork
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {
                    // silently ignored
                }
            }
        }
        return null;
    }

    public static String readString(IStreamContentAccessor sa) 
            throws CoreException {
        InputStream is= sa.getContents();
        if (is != null) {
            String encoding = null;
            if (sa instanceof IEncodedStreamContentAccessor) {
                try {
                    encoding = ((IEncodedStreamContentAccessor) sa).getCharset();
                }
                catch (Exception e) {}
            }
            if (encoding == null)
                encoding = ResourcesPlugin.getEncoding();
            return readString(is, encoding);
        }
        return null;
    }
    
    @Override
    protected String[] getPath(Object element, Object input) {
        return super.getPath(element, input);
    }

    @Override
    protected String getDocumentPartitioning() {
        return IDocument.DEFAULT_CONTENT_TYPE;
    }

}
