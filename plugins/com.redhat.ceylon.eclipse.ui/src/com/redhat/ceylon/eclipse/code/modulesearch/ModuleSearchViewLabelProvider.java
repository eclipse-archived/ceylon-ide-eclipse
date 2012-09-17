package com.redhat.ceylon.eclipse.code.modulesearch;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class ModuleSearchViewLabelProvider extends StyledCellLabelProvider {
    
    @Override
    public void update(ViewerCell cell) {
        if (cell.getElement() instanceof ModuleNode) {
            updateModuleNode(cell, (ModuleNode) cell.getElement());
        } else if (cell.getElement() instanceof ModuleVersionNode) {
            updateVersionNode(cell, (ModuleVersionNode) cell.getElement());
        }
        super.update(cell);
    }

    private void updateModuleNode(ViewerCell cell, ModuleNode moduleNode) {
        ModuleVersionNode lastVersion = moduleNode.getLastVersion();
    
        StyledString styledText = new StyledString();
        styledText.append(moduleNode.getName());
        styledText.append(" : ", StyledString.QUALIFIER_STYLER);
        styledText.append(lastVersion.getVersion(), StyledString.QUALIFIER_STYLER);
    
        if (lastVersion.getAuthors() != null && !lastVersion.getAuthors().isEmpty()) {
            styledText.append(" (by: ", StyledString.QUALIFIER_STYLER);
            styledText.append(lastVersion.getAuthorsCommaSeparated(), StyledString.QUALIFIER_STYLER);
            styledText.append(")", StyledString.QUALIFIER_STYLER);
        }
    
        cell.setText(styledText.toString());
        cell.setStyleRanges(styledText.getStyleRanges());
        cell.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.CEYLON_ARCHIVE));
    }

    private void updateVersionNode(ViewerCell cell, ModuleVersionNode versionNode) {
        StyledString styledText = new StyledString();
        styledText.append(versionNode.getVersion());
        cell.setText(styledText.toString());
        cell.setStyleRanges(styledText.getStyleRanges());
        cell.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.MODULE_VERSION));
    }

}