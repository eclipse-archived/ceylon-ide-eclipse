package com.redhat.ceylon.eclipse.core.debug.actions;

public class CeylonOpenVariableDeclaredTypeHierarchyAction extends
        CeylonOpenVariableDeclaredTypeAction {
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction#isHierarchy()
     */
    @Override
    protected boolean isHierarchy() {
        return true;
    }   
}
