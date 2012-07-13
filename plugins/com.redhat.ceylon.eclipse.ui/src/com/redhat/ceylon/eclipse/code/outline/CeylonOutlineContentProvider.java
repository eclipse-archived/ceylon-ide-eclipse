package com.redhat.ceylon.eclipse.code.outline;

public final class CeylonOutlineContentProvider 
        extends OutlineContentProviderBase {
	
    public CeylonOutlineContentProvider(OutlineInformationControl oic) {
        super(oic);
    }

    public Object[] getChildren(Object element) {
        return ((CeylonOutlineNode) element).getChildren().toArray();
    }

    public Object getParent(Object element) {
    	return ((CeylonOutlineNode) element).getParent();
    	
    }
}