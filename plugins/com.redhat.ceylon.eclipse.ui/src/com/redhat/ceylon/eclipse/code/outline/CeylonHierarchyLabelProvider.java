package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getQualifiedDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MULTIPLE_TYPES_IMAGE;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;

import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;

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

    Font getFont() {
        return null;
    }

    String getPrefix() {
        return null;
    }

    StyledString getStyledText(CeylonHierarchyNode n) {
        Declaration d = getDisplayedDeclaration(n);
        if (d==null) {
        	return new StyledString();
        }
        IPreferenceStore prefs = EditorUtil.getPreferences();
        StyledString result = getQualifiedDescriptionFor(d, 
                prefs.getBoolean(TYPE_PARAMS_IN_OUTLINES),
                prefs.getBoolean(PARAMS_IN_OUTLINES),
                prefs.getBoolean(PARAM_TYPES_IN_OUTLINES),
                prefs.getBoolean(RETURN_TYPES_IN_OUTLINES),
                getPrefix(), getFont());
        /*if (d.isClassOrInterfaceMember()) {
            Declaration container = (Declaration) d.getContainer();
            result.append(" in ")
                  .append(container.getName(), Highlights.TYPE_ID_STYLER);
        }*/
        result.append(" - ", Highlights.PACKAGE_STYLER)
              .append(getPackageLabel(d), Highlights.PACKAGE_STYLER);
        if (n.isNonUnique()) {
            result.append(" - and other supertypes")
                  .append(getViewInterfacesShortcut());
        }
        return result;
    }

    String getViewInterfacesShortcut() {
        return "";
    }
    
    abstract boolean isShowingRefinements();

    Declaration getDisplayedDeclaration(CeylonHierarchyNode node) {
        Declaration declaration = node.getDeclaration();
        if (declaration!=null && 
                isShowingRefinements() && 
                declaration.isClassOrInterfaceMember()) {
            declaration = (ClassOrInterface) declaration.getContainer();
        }
        return declaration;
    }
    
    @Override
    public void update(ViewerCell cell) {
        CeylonHierarchyNode n = 
                (CeylonHierarchyNode) cell.getElement();
        if (n.isMultiple()) {
            cell.setText("multiple supertypes" + 
                    getViewInterfacesShortcut());
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