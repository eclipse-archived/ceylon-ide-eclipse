/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_SEARCH_RESULTS;
import static java.util.Arrays.asList;
import static org.eclipse.jdt.core.IJavaElement.CLASS_FILE;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;

import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.ide.eclipse.code.editor.SourceArchiveEditorInput;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;

public class CeylonSearchResult 
        extends AbstractTextSearchResult
        implements IEditorMatchAdapter, IFileMatchAdapter {
    
    private static final ImageDescriptor IMAGE = 
            CeylonPlugin.imageRegistry()
                .getDescriptor(CEYLON_SEARCH_RESULTS);
    
    ISearchQuery query;
    
    CeylonSearchResult(ISearchQuery query) {
        this.query = query;
    }
    
    @Override
    public String getTooltip() {
        return getLabel();
    }

    @Override
    public ISearchQuery getQuery() {
        return query;
    }

    @Override
    public String getLabel() {
        return query.getLabel();
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return IMAGE;
    }

    @Override
    public IFileMatchAdapter getFileMatchAdapter() {
        return this;
    }

    @Override
    public IEditorMatchAdapter getEditorMatchAdapter() {
        return this;
    }

    @Override
    public Match[] computeContainedMatches(
            AbstractTextSearchResult atsr, IFile file) {
        return getMatchesForFile(file);
    }

    public Match[] getMatchesForFile(IFile file) {
        List<Match> matches = new ArrayList<Match>();
        for (Object element: this.getElements()) {
            IFile elementFile = getFile(element);
            if (elementFile!=null && elementFile.equals(file)) {
                matches.addAll(asList(getMatches(element)));
            }
        }
        return matches.toArray(new Match[matches.size()]);
    }
    
    public Match[] getMatchesForSourceArchive(
            SourceArchiveEditorInput input) {
        List<Match> matches = new ArrayList<Match>();
        IPath inputPath = input.getPath();
        if (inputPath!=null) { //the file could have been deleted
            String path = inputPath.toOSString();
            for (Object element: this.getElements()) {
                if (element instanceof CeylonElement) {
                    CeylonElement ce = (CeylonElement) element;
                    VirtualFile file = ce.getVirtualFile();
                    if (path.equals(file.getPath())) {
                        matches.addAll(asList(getMatches(element)));
                    }
                }
            }
        }
        return matches.toArray(new Match[matches.size()]);
    }
    
    private Match[] getMatchesForClassFile(IClassFile classFile) {
        List<Match> matches = new ArrayList<Match>();
        for (Object element: this.getElements()) {
            if (element instanceof IJavaElement) {
                IJavaElement je = (IJavaElement) element;
                IJavaElement elementClassFile = 
                        je.getAncestor(CLASS_FILE);
                if (elementClassFile!=null && 
                        elementClassFile.equals(classFile)) {
                    matches.addAll(asList(getMatches(element)));
                }
            }
        }
        return matches.toArray(new Match[matches.size()]);
    }
    
    private Match[] getMatchesForURI(URI uri) {
        List<Match> matches = new ArrayList<Match>();
        for (Object element: this.getElements()) {
            if (element instanceof CeylonElement) {
                CeylonElement ce = (CeylonElement) element;
                VirtualFile file = ce.getVirtualFile();
                if (uri.toString().endsWith(file.getPath())) {
                    matches.addAll(asList(getMatches(element)));
                }
            }
            else if (element instanceof IJavaElement) {
                IJavaElement je = (IJavaElement) element;
                IResource resource = je.getResource();
                if (resource!=null) {
                    URI path = resource.getLocationURI();
                    if (uri.toString().endsWith(path.toString())) {
                        matches.addAll(asList(getMatches(element)));
                    }
                }
            }
        }
        return matches.toArray(new Match[matches.size()]);
    }

    @Override
    public IFile getFile(Object element) {
        if (element instanceof IFile) {
            return (IFile) element;
        }
        else if (element instanceof CeylonElement) {
            CeylonElement ce = (CeylonElement) element;
            return ce.getFile();
        }
        else if (element instanceof IJavaElement) {
            IJavaElement je = (IJavaElement) element;
            return (IFile) je.getResource();
        }
        else { 
            return null;
        }
    }

    @Override
    public Match[] computeContainedMatches(
            AbstractTextSearchResult atsr, 
            IEditorPart editor) {
        IEditorInput ei = editor.getEditorInput();
        if (ei instanceof SourceArchiveEditorInput) {
            SourceArchiveEditorInput saei = 
                    (SourceArchiveEditorInput) ei;
            return getMatchesForSourceArchive(saei);
        }
        else if (ei instanceof IFileEditorInput) {
            return getMatchesForFile(EditorUtil.getFile(ei));
        }
        else if (ei instanceof FileStoreEditorInput) {
            FileStoreEditorInput fsei = 
                    (FileStoreEditorInput) ei;
            return getMatchesForURI(fsei.getURI());
        }
        else if (ei instanceof IClassFileEditorInput) {
            IClassFileEditorInput cfei = 
                    (IClassFileEditorInput) ei;
            return getMatchesForClassFile(cfei.getClassFile());
        }
        else {
            return new Match[0];
        }
    }

    @Override
    public boolean isShownInEditor(Match match, IEditorPart editor) {
        IEditorInput ei = editor.getEditorInput();
        Object element = match.getElement();
        if (ei instanceof IFileEditorInput) {
            IFile file = getFile(element);
            return file!=null && 
                    file.equals(EditorUtil.getFile(ei));
        }
        else if (ei instanceof FileStoreEditorInput) {
            FileStoreEditorInput fsei = 
                    (FileStoreEditorInput) ei;
            String uri = fsei.getURI().toString();
            if (element instanceof CeylonElement) {
                CeylonElement ce = (CeylonElement) element;
                VirtualFile file = ce.getVirtualFile();
                return uri.endsWith(file.getPath());
            }
            else if (element instanceof IJavaElement) {
                IJavaElement je = (IJavaElement) element;
                URI path = je.getResource().getLocationURI();
                return uri.endsWith(path.toString());
            }
            else {
                return false;
            }
        }
        else if (ei instanceof IClassFileEditorInput) {
            if (element instanceof IJavaElement) {
                IClassFileEditorInput cfei = 
                        (IClassFileEditorInput) ei;
                IJavaElement je = (IJavaElement) element;
                return je.getAncestor(IJavaElement.CLASS_FILE)
                            == cfei.getClassFile();
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
}