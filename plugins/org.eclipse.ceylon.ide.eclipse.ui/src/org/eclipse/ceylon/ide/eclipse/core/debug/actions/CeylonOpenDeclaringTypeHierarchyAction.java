package org.eclipse.ceylon.ide.eclipse.core.debug.actions;

public class CeylonOpenDeclaringTypeHierarchyAction extends
        CeylonOpenDeclaringTypeAction {
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction#isHierarchy()
     */
    @Override
    protected boolean isHierarchy() {
        return true;
    }
}
