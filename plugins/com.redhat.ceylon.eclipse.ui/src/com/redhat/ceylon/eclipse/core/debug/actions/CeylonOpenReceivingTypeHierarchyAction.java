package org.eclipse.ceylon.ide.eclipse.core.debug.actions;

public class CeylonOpenReceivingTypeHierarchyAction extends
        CeylonOpenReceivingTypeAction {
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction#isHierarchy()
     */
    @Override
    protected boolean isHierarchy() {
        return true;
    }
}
