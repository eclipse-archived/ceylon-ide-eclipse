package org.eclipse.ceylon.ide.eclipse.code.preferences;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.MODULE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.REPO;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.VERSION;
import static org.eclipse.ceylon.ide.eclipse.util.Highlights.STRING_STYLER;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import org.eclipse.ceylon.ide.common.modulesearch.ModuleNode;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleVersionNode;

final class ModuleSelectionLabelProvider
        extends StyledCellLabelProvider 
        implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, 
                   ILabelProvider {
    
    @Override
    public void update(ViewerCell cell) {
        cell.setImage(getImage(cell.getElement()));
        StyledString styledText = getStyledText(cell.getElement());
        cell.setText(styledText.getString());
        cell.setStyleRanges(styledText.getStyleRanges());
        super.update(cell);
    }
    
//    @Override
    public Image getImage(Object element) {
        if (element instanceof ModuleNode) {
            return MODULE;
        }
        else if (element instanceof ModuleVersionNode) {
            return VERSION;
        }
        else {
            return REPO;
        }
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ModuleNode) {
            ModuleNode md = (ModuleNode) element;
            return md.getName() + " \"" + md.getLastVersion().getVersion() + "\"";
        }
        else if (element instanceof ModuleVersionNode) {
            return "\"" + ((ModuleVersionNode) element).getVersion() + "\"";
        }
        else {
            return ((ModuleCategoryNode) element).getDescription();
        }
    }

//    @Override
    public StyledString getStyledText(Object element) {
        if (element instanceof ModuleNode) {
            ModuleNode md = (ModuleNode) element;
            return new StyledString(md.getName())
                .append(" \"" + md.getLastVersion().getVersion() + "\"", 
                    STRING_STYLER);
        }
        else if (element instanceof ModuleVersionNode) {
            ModuleVersionNode mvn = (ModuleVersionNode) element;
            return new StyledString("\"" + mvn.getVersion() + "\"",
                    STRING_STYLER);
        }
        else {
            ModuleCategoryNode mcn = (ModuleCategoryNode) element;
            return new StyledString(mcn.getDescription());
        }
    }
}