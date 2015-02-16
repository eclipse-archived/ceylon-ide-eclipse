package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isNameMatching;
import static com.redhat.ceylon.eclipse.code.open.OpenDeclarationDialog.isMatchingGlob;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Text;

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
        /*JavaElementPrefixPatternMatcher matcher= getMatcher();
        if (matcher == null || !(viewer instanceof TreeViewer))
            return true;
        TreeViewer treeViewer= (TreeViewer) viewer;

        String matchName= ((ILabelProvider) treeViewer.getLabelProvider()).getText(element);
        matchName= TextProcessor.deprocess(matchName);
        if (matchName != null && matcher.matches(matchName))
            return true;

        return hasUnfilteredChild(treeViewer, element);*/
        TreeViewer treeViewer= (TreeViewer) viewer;
        if (element instanceof CeylonOutlineNode) {
            String name = ((CeylonOutlineNode) element).getName();
            if (name==null) {
                return false;
            }
            else {
                String filter = filterText.getText();
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
        return true;
    }

    private boolean hasUnfilteredChild(TreeViewer viewer, Object element) {
        Object[] children=  ((ITreeContentProvider) viewer.getContentProvider()).getChildren(element);
        for (int i= 0; i < children.length; i++)
            if (select(viewer, element, children[i]))
                return true;
        return false;
    }
}