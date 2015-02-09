package com.redhat.ceylon.eclipse.code.preferences;
import org.eclipse.jdt.internal.debug.ui.Filter;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for Filter model objects
 */
public class FilterLabelProvider 
        extends LabelProvider 
        implements ITableLabelProvider {
    
	private static final Image IMG_CLASS =
		JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
    private static final Image IMG_INTERFACE =
            JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_INTERFACE);
    private static final Image IMG_FUNCTION =
            JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PUBLIC);
    private static final Image IMG_VALUE =
            JavaUI.getSharedImages().getImage(ISharedImages.IMG_FIELD_PUBLIC);
	private static final Image IMG_PKG =
		JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKAGE);
	
	public String getColumnText(Object object, int column) {
		if (column == 0) {
			return getText(object);
		}
		return "";
	}
	
	@Override
	public String getText(Object element) {
		String name = ((Filter) element).getName();
		int loc = name.lastIndexOf('(');
		if (loc>0) {
		    name = name.substring(0, loc);
		}
        return name;
	}
	
	public Image getColumnImage(Object object, int column) {
		String name = ((Filter) object).getName();
		String type = "";
        int loc = name.lastIndexOf('(');
        if (loc>0) {
            type = name.substring(loc);
            switch (type) {
            case "(Class)": return IMG_CLASS;
            case "(Interface)": return IMG_INTERFACE;
            case "(Function)": return IMG_FUNCTION;
            case "(Value)": return IMG_VALUE;
            default: return null; 
            }
        }
		if (name.endsWith("*") || 
		    name.equals("(default package)")) {
			return IMG_PKG;
		}
		return null;
	}
}