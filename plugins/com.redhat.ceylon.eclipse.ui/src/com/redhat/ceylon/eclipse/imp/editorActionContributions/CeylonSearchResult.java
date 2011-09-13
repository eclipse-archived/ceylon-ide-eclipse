package com.redhat.ceylon.eclipse.imp.editorActionContributions;

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

import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.ui.ICeylonResources;

public class CeylonSearchResult extends AbstractTextSearchResult
		implements IEditorMatchAdapter, IFileMatchAdapter {
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
		return CeylonLabelProvider.imageRegistry.getDescriptor(ICeylonResources.CEYLON_FILE);
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
	        if ( getFile(element).equals(file) ) {
	            matches.addAll(Arrays.asList(getMatches(element)));
	        }
	    }
		return matches.toArray(new Match[matches.size()]);
    }

	@Override
	public IFile getFile(Object element) {
		if (element instanceof IFile)
			return (IFile) element;
		else if (element instanceof CeylonElement)
			return ((CeylonElement) element).getFile();
		else 
			throw new RuntimeException();
	}

	@Override
	public Match[] computeContainedMatches(AbstractTextSearchResult atsr,
			IEditorPart editor) {
		IEditorInput ei= editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			IFileEditorInput fi= (IFileEditorInput) ei;
			return getMatchesForFile(fi.getFile());
		}
		else {
			return new Match[0];
		}
	}

	@Override
	public boolean isShownInEditor(Match match, IEditorPart editor) {
		IEditorInput ei= editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			IFileEditorInput fi= (IFileEditorInput) ei;
			return getFile(match.getElement()).equals(fi.getFile());
		}
		else {
			return false;
		}
	}
	
}