package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SEARCH_RESULTS;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
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

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonSearchResult extends AbstractTextSearchResult
        implements IEditorMatchAdapter, IFileMatchAdapter {
    
    private static final ImageDescriptor IMAGE = CeylonPlugin.getInstance()
            .getImageRegistry().getDescriptor(CEYLON_SEARCH_RESULTS);
    
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
    public Match[] computeContainedMatches(AbstractTextSearchResult atsr,
            IFile file) {
        return getMatchesForFile(file);
    }

    public Match[] getMatchesForFile(IFile file) {
        List<Match> matches = new ArrayList<Match>();
        for (Object element: this.getElements()) {
            IFile elementFile = getFile(element);
            if ( elementFile!=null && elementFile.equals(file) ) {
                matches.addAll(Arrays.asList(getMatches(element)));
            }
        }
        return matches.toArray(new Match[matches.size()]);
    }

    public Match[] getMatchesForURI(URI uri) {
        List<Match> matches = new ArrayList<Match>();
        for (Object element: this.getElements()) {
            String path = ((CeylonElement) element).getVirtualFile().getPath();
            if (uri.toString().endsWith(path)) {
                matches.addAll(Arrays.asList(getMatches(element)));
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
            return ((CeylonElement) element).getFile();
        }
        else { 
            return null;
        }
    }

    @Override
    public Match[] computeContainedMatches(AbstractTextSearchResult atsr,
            IEditorPart editor) {
        IEditorInput ei = editor.getEditorInput();
        if (ei instanceof IFileEditorInput) {
            return getMatchesForFile(EditorUtil.getFile(ei));
        }
        else if (ei instanceof FileStoreEditorInput) {
            return getMatchesForURI(((FileStoreEditorInput)ei).getURI());
        }
        else {
            return new Match[0];
        }
    }

    @Override
    public boolean isShownInEditor(Match match, IEditorPart editor) {
        IEditorInput ei = editor.getEditorInput();
        if (ei instanceof IFileEditorInput) {
            IFile file = getFile(match.getElement());
            return file!=null && file.equals(EditorUtil.getFile(ei));
        }
        else if (ei instanceof FileStoreEditorInput) {
            String path = ((CeylonElement) match.getElement()).getVirtualFile().getPath();
            return ((FileStoreEditorInput)ei).getURI().toString().endsWith(path);
        }
        else {
            return false;
        }
    }
    
}