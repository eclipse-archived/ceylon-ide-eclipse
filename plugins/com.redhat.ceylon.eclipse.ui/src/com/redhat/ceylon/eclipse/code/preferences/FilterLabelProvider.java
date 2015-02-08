package com.redhat.ceylon.eclipse.code.preferences;
import org.eclipse.jdt.internal.debug.ui.Filter;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for Filter model objects
 */
public class FilterLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final Image IMG_TYPE =
		JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
    private static final Image IMG_TYPED =
            JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PUBLIC);
	private static final Image IMG_PKG =
		JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKAGE);

	/**
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	public String getColumnText(Object object, int column) {
		if (column == 0) {
			return ((Filter) object).getName();
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see ILabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		return ((Filter) element).getName();
	}

	/**
	 * @see ITableLabelProvider#getColumnImage(Object, int)
	 */
	public Image getColumnImage(Object object, int column) {
		String name = ((Filter) object).getName();
		if (name.endsWith("*") || name.equals("(default package)")) { //$NON-NLS-1$ //$NON-NLS-2$
			return IMG_PKG;
		}
        int loc = name.indexOf("::");
        if (loc>=0) name = name.substring(loc+2);
		return Character.isUpperCase(name.charAt(0)) ? 
		        IMG_TYPE : IMG_TYPED;
	}
}