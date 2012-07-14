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

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class CeylonOutlineLabelProvider implements ILabelProvider, IColorProvider {
	boolean fShowDefiningType;

	protected ListenerList fListeners= new ListenerList();
	private final CeylonLabelProvider fLanguageLabelProvider;
	private ArrayList<ILabelDecorator> fLabelDecorators;

	public CeylonOutlineLabelProvider(CeylonLabelProvider langLabelProvider) {
		fLanguageLabelProvider= langLabelProvider;
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

	public String getText(Object element) {
		String result= fLanguageLabelProvider.getText(element);
		return decorateText(result, element);
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

	public Color getForeground(Object element) {
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

	public Image getImage(Object element) {
		Image result= null;
		if (fLanguageLabelProvider != null)
			result= fLanguageLabelProvider.getImage(element);
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

