package com.redhat.ceylon.eclipse.code.outline;

/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

 *******************************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CeylonOutlineLabelProvider implements ILabelProvider, IColorProvider {
	boolean fShowDefiningType;

	private boolean fIsShowingInheritedMembers;
	protected ListenerList fListeners= new ListenerList();
	private final CeylonLabelProvider fLanguageLabelProvider;
	private ArrayList<ILabelDecorator> fLabelDecorators;
	protected final StorageLabelProvider fStorageLabelProvider;
	protected final IElementImageProvider fImageLabelProvider;
	private Color fForegroundColor;

	public interface IElementImageProvider {
		Image getImageLabel(Object element, long imageFlags);
	}

	public CeylonOutlineLabelProvider(CeylonLabelProvider langLabelProvider, 
			IElementImageProvider elemImageProvider, boolean showInheritedMembers, 
			boolean showStorage, Color foregroundColor) {
		fLanguageLabelProvider= langLabelProvider;
		fImageLabelProvider= elemImageProvider; // new JikesPGElementImageProvider();
		fIsShowingInheritedMembers= showInheritedMembers;
		fForegroundColor= foregroundColor;
		fStorageLabelProvider= 	showStorage ? new StorageLabelProvider() : null;
	}

	/**
	 * Adds a decorator to the label provider
	 */
	public void addLabelDecorator(ILabelDecorator decorator) {
		if (fLabelDecorators == null) {
			fLabelDecorators= new ArrayList<ILabelDecorator>(2);
		}
		fLabelDecorators.add(decorator);
	}

	/*
	 * @see ILabelProvider#getText
	 */
	public String getText(Object element) {
		String result= fLanguageLabelProvider.getText(element); // JikesPGElementLabels.getTextLabel(element, getTextFlags(element));
		if (result.length() == 0 && (element instanceof IStorage)) {
			result= fStorageLabelProvider.getText(element);
		}
		result= decorateText(result, element);
		if (fShowDefiningType) {
			//		IType type= getDefiningType(element);
			//		if (type != null) {
			//		    StringBuffer buf= new StringBuffer(super.getText(type));
			//		    buf.append(JavaElementLabels.CONCAT_STRING);
			//		    buf.append(result);
			//		    return buf.toString();
			//		}
		}
		return result;
	}

	protected String decorateText(String text, Object element) {
		if (fLabelDecorators != null && text.length() > 0) {
			for(int i= 0; i < fLabelDecorators.size(); i++) {
				ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
				text= decorator.decorateText(text, element);
			}
		}
		return text;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		if (fIsShowingInheritedMembers) {
			//		if (element instanceof ASTNode) {
			//		    ASTNode node= (ASTNode) element;
			//
			//		    if (fInput.getElementType() == IJavaElement.CLASS_FILE)
			//			je= je.getAncestor(IJavaElement.CLASS_FILE);
			//		    else
			//			je= je.getAncestor(IJavaElement.COMPILATION_UNIT);
			//		    if (fInput.equals(je)) {
			//			return null;
			//		    }
			//		}
			return fForegroundColor;
		}
		return null;
	}

	public Color getBackground(Object element) {
		return null;
	}

	public void setShowDefiningType(boolean showDefiningType) {
		fShowDefiningType= showDefiningType;
	}

	public boolean isShowDefiningType() {
		return fShowDefiningType;
	}

	//	private IType getDefiningType(Object element) {
	//	    int kind= ((IJavaElement) element).getElementType();
	//	    if (kind != IJavaElement.METHOD && kind != IJavaElement.FIELD && kind != IJavaElement.INITIALIZER) {
	//		return null;
	//	    }
	//	    IType declaringType= ((IMember) element).getDeclaringType();
	//	    if (kind != IJavaElement.METHOD) {
	//		return declaringType;
	//	    }
	//	    ITypeHierarchy hierarchy= getSuperTypeHierarchy(declaringType);
	//	    if (hierarchy == null) {
	//		return declaringType;
	//	    }
	//	    IMethod method= (IMethod) element;
	//	    int flags= method.getFlags();
	//	    if (Flags.isPrivate(flags) || Flags.isStatic(flags) || method.isConstructor()) {
	//		return declaringType;
	//	    }
	//	    IMethod res= JavaModelUtil.findMethodDeclarationInHierarchy(hierarchy, declaringType, method.getElementName(), method.getParameterTypes(), false);
	//	    if (res == null || method.equals(res)) {
	//		return declaringType;
	//	    }
	//	    return res.getDeclaringType();
	//	}

	public Image getImage(Object element) {
		Image result= null;
		if (fImageLabelProvider != null)
			result= fImageLabelProvider.getImageLabel(element, 0);
		else if (fLanguageLabelProvider != null)
			result= fLanguageLabelProvider.getImage(element);
		if (result == null && (element instanceof IStorage)) {
			result= fStorageLabelProvider.getImage(element);
		}
		return decorateImage(result, element);
	}

	protected Image decorateImage(Image image, Object element) {
		if (fLabelDecorators != null && image != null) {
			for(int i= 0; i < fLabelDecorators.size(); i++) {
				ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
				image= decorator.decorateImage(image, element);
			}
		}
		return image;
	}

	public void addListener(ILabelProviderListener listener) {
		fListeners.add(listener);
	}

	public void dispose() {
		if (fLabelDecorators != null) {
			for(int i= 0; i < fLabelDecorators.size(); i++) {
				ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
				decorator.dispose();
			}
			fLabelDecorators= null;
		}
		if (fStorageLabelProvider != null)
			fStorageLabelProvider.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
		if (fLabelDecorators != null) {
			for(int i= 0; i < fLabelDecorators.size(); i++) {
				ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
				decorator.removeListener(listener);
			}
		}
		fListeners.remove(listener);
	}
}

class StorageLabelProvider extends LabelProvider {

	private IEditorRegistry fEditorRegistry= null;
	private Map<String,Image> fJarImageMap= new HashMap<String,Image>(10);
	private Image fDefaultImage;

	private IEditorRegistry getEditorRegistry() {
		if (fEditorRegistry == null)
			fEditorRegistry= PlatformUI.getWorkbench().getEditorRegistry();
		return fEditorRegistry;
	}
	
	public Image getImage(Object element) {
		if (element instanceof IStorage) 
			return getImageForJarEntry((IStorage)element);

		return super.getImage(element);
	}

	public String getText(Object element) {
		if (element instanceof IStorage)
			return ((IStorage)element).getName();

		return super.getText(element);
	}

	public void dispose() {
		if (fJarImageMap != null) {
			Iterator<Image> each= fJarImageMap.values().iterator();
			while (each.hasNext()) {
				Image image= each.next();
				image.dispose();
			}
			fJarImageMap= null;
		}
		fDefaultImage= null;
	}
	
	/*
	 * Gets and caches an image for a JarEntryFile.
	 * The image for a JarEntryFile is retrieved from the EditorRegistry.
	 */ 
	private Image getImageForJarEntry(IStorage element) {
		if (fJarImageMap == null)
			return getDefaultImage();

		if (element == null || element.getName() == null)
			return getDefaultImage();

		// Try to find icon for full name
		String name= element.getName();
		Image image= fJarImageMap.get(name);
		if (image != null) 
			return image;
		IFileEditorMapping[] mappings= getEditorRegistry().getFileEditorMappings();
		int i= 0;
		while (i < mappings.length) {
			if (mappings[i].getLabel().equals(name))
				break;
			i++;
		}
		String key= name;
		if (i == mappings.length) {
			// Try to find icon for extension
			IPath path= element.getFullPath();
			if (path == null)
				return getDefaultImage();
			key= path.getFileExtension();
			if (key == null)
				return getDefaultImage();
			image= (Image)fJarImageMap.get(key);
			if (image != null) 
				return image;
		}

		// Get the image from the editor registry	
		ImageDescriptor desc= getEditorRegistry().getImageDescriptor(name);
		image= desc.createImage();

		fJarImageMap.put(key, image);

		return image;
	}
	
	private Image getDefaultImage() {
		if (fDefaultImage == null)
			fDefaultImage= PlatformUI.getWorkbench().getSharedImages()
			        .getImage(ISharedImages.IMG_OBJ_FILE);
		return fDefaultImage;
	}
}

