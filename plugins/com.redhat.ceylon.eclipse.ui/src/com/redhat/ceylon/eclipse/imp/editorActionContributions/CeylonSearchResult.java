package com.redhat.ceylon.eclipse.imp.editorActionContributions;

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
	FindReferencesSearchQuery query;
	
	CeylonSearchResult(FindReferencesSearchQuery query) {
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
		return getMatches(file);
	}

	@Override
	public IFile getFile(Object element) {
		if (element instanceof IFile)
			return (IFile) element;
		throw new RuntimeException();
	}

	@Override
	public Match[] computeContainedMatches(AbstractTextSearchResult atsr,
			IEditorPart editor) {
		IEditorInput ei= editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			IFileEditorInput fi= (IFileEditorInput) ei;
			return getMatches(fi.getFile());
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
			return match.getElement().equals(fi.getFile());
		}
		else {
			return false;
		}
	}
	
}