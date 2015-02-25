package com.redhat.ceylon.eclipse.code.modulesearch;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.util.Highlights;

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
        styledText.append(moduleNode.getName()); //really should be: CeylonLabelProvider.PACKAGE_STYLER
        styledText.append(" \"", Highlights.VERSION_STYLER);
        styledText.append(lastVersion.getVersion(), Highlights.VERSION_STYLER);
        styledText.append("\"", Highlights.VERSION_STYLER);
    
//        if (lastVersion.getAuthors() != null && !lastVersion.getAuthors().isEmpty()) {
//            styledText.append(" (by ", StyledString.QUALIFIER_STYLER);
//            styledText.append(lastVersion.getAuthorsCommaSeparated(), StyledString.QUALIFIER_STYLER);
//            styledText.append(")", StyledString.QUALIFIER_STYLER);
//        }
//    
        cell.setText(styledText.toString());
        cell.setStyleRanges(styledText.getStyleRanges());
        cell.setImage(CeylonLabelProvider.MODULE);
    }

    private void updateVersionNode(ViewerCell cell, ModuleVersionNode versionNode) {
        StyledString styledText = new StyledString();
        styledText.append(" \"", Highlights.VERSION_STYLER);
        styledText.append(versionNode.getVersion(), Highlights.VERSION_STYLER);
        styledText.append("\"", Highlights.VERSION_STYLER);
        cell.setText(styledText.toString());
        cell.setStyleRanges(styledText.getStyleRanges());
        cell.setImage(CeylonLabelProvider.VERSION);
    }

}