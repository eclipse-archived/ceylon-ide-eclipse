package com.redhat.ceylon.eclipse.core.debug;

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
