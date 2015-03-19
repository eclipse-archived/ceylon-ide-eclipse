package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isNameMatching;
import static com.redhat.ceylon.eclipse.code.open.OpenDeclarationDialog.isMatchingGlob;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.DEFAULT_CATEGORY;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.IMPORT_LIST_CATEGORY;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

/**
 * The NamePatternFilter selects the elements which
 * match the given string patterns.
 *
 * @since 2.0
 */
class OutlineNamePatternFilter extends ViewerFilter {

    private final Text filterText;

    OutlineNamePatternFilter(Text filterText) {
        this.filterText = filterText;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        TreeViewer treeViewer = (TreeViewer) viewer;
        String filter = filterText.getText();
        String name = null;
        if (element instanceof CeylonOutlineNode) {
            CeylonOutlineNode on = 
                    (CeylonOutlineNode) element;
            int category = on.getCategory();
            if (category!=DEFAULT_CATEGORY && 
                category!=IMPORT_LIST_CATEGORY) {
                return false;
            }
            name = on.getName();
        }
        else if (element instanceof Declaration) {
            name = ((Declaration) element).getName();
        }
        else {
            return true;
        }
        if (name==null) {
            return filter.isEmpty() || filter.equals("*");
        }
        else {
            if (filter.contains("*")) {
                return isMatchingGlob(filter, name) ||
                        hasUnfilteredChild(treeViewer, element);
            }
            else {
                return isNameMatching(filter, name) ||
                        hasUnfilteredChild(treeViewer, element);
            }
        }
    }

    private boolean hasUnfilteredChild(TreeViewer viewer, Object element) {
        ITreeContentProvider cp = 
                (ITreeContentProvider) viewer.getContentProvider();
        Object[] children = cp.getChildren(element);
        if (children!=null) {
            for (int i=0; i<children.length; i++) {
                if (select(viewer, element, children[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}