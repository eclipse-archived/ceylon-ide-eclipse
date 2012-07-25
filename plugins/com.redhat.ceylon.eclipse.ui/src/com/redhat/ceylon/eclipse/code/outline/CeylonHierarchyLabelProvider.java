package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.TYPE_ID_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImage;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getStyledDescriptionFor;
import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;

final class CeylonHierarchyLabelProvider extends
		StyledCellLabelProvider {
	
	private final CeylonHierarchyContentProvider contentProvider;
	
	public CeylonHierarchyLabelProvider(CeylonHierarchyContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {}

	@Override
	public boolean isLabelProperty(Object element, String property) {
	    return false;
	}

	@Override
	public void dispose() {}

	@Override
	public void addListener(ILabelProviderListener listener) {}

	StyledString getStyledText(Object element) {
	    Declaration d = getDisplayedDeclaration(element);
	    StyledString result = getStyledDescriptionFor(d);
	    /*if (d.getContainer() instanceof Declaration) {
	        result.append(" in ")
	                .append(getStyledDescriptionFor((Declaration) d.getContainer()));
	    }*/
	    if (d.isClassOrInterfaceMember()) {
	        result.append(" in ")
	                .append(((Declaration) d.getContainer()).getName(), TYPE_ID_STYLER);
	    }
	    result.append(" - ", QUALIFIER_STYLER)
	            .append(CeylonLabelProvider.getPackageLabel(d), QUALIFIER_STYLER);
	    return result;
	}

	Declaration getDisplayedDeclaration(Object element) {
		CeylonHierarchyNode n = (CeylonHierarchyNode) element;
	    Declaration d = n.getDeclaration();
	    if (contentProvider.isShowingRefinements() && 
	    		d.isClassOrInterfaceMember()) {
	        d = (ClassOrInterface) d.getContainer();
	    }
	    return d;
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString styledText = getStyledText(element);
		cell.setText(styledText.toString());
		cell.setStyleRanges(styledText.getStyleRanges());
		cell.setImage(getImage(getDisplayedDeclaration(element)));
		super.update(cell);
	}
}