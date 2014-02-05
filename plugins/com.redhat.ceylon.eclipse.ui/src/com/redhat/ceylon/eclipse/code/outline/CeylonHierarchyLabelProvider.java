package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.MULTIPLE_TYPES_IMAGE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.TYPE_ID_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getStyledDescriptionFor;
import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;

abstract class CeylonHierarchyLabelProvider extends
		StyledCellLabelProvider {
		
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

	StyledString getStyledText(CeylonHierarchyNode n) {
	    Declaration d = getDisplayedDeclaration(n);
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
	    if (n.isNonUnique()) {
	    	result.append(" - and other supertypes");
	    	result.append(getViewInterfacesShortcut());
	    }
	    return result;
	}

    String getViewInterfacesShortcut() {
        return "";
    }
    
    abstract IProject getProject();
    abstract boolean isShowingRefinements();

	Declaration getDisplayedDeclaration(CeylonHierarchyNode n) {
	    Declaration d = n.getDeclaration(getProject());
	    if (isShowingRefinements() && d.isClassOrInterfaceMember()) {
	        d = (ClassOrInterface) d.getContainer();
	    }
	    return d;
	}

    
	@Override
	public void update(ViewerCell cell) {
		CeylonHierarchyNode n = (CeylonHierarchyNode) cell.getElement();
		if (n.isMultiple()) {
			cell.setText("multiple supertypes" + getViewInterfacesShortcut());
			cell.setStyleRanges(new StyleRange[0]);
            cell.setImage(MULTIPLE_TYPES_IMAGE);
		}
		else {
			StyledString styledText = getStyledText(n);
			cell.setText(styledText.toString());
			cell.setStyleRanges(styledText.getStyleRanges());
			cell.setImage(getImageForDeclaration(getDisplayedDeclaration(n)));
		}
		super.update(cell);
	}
}