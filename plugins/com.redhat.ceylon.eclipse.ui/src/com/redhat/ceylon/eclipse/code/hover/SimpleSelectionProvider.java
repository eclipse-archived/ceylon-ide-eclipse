package com.redhat.ceylon.eclipse.code.hover;

/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.core.runtime.ListenerList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import org.eclipse.jdt.ui.actions.SelectionDispatchAction;

/**
 * A simple default implementation of a {@link ISelectionProvider}. It stores
 * the selection and notifies all selection change listeners when the selection
 * is set.
 *
 * Instances of this class can be used as special selection provider
 * for {@link SelectionDispatchAction}s
 *
 * @since 3.4
 */
public class SimpleSelectionProvider implements ISelectionProvider {

    private final ListenerList<ISelectionChangedListener> fSelectionChangedListeners;
    private ISelection fSelection;

    /**
     * Create a new SimpleSelectionProvider
     */
    public SimpleSelectionProvider() {
        fSelectionChangedListeners= new ListenerList<ISelectionChangedListener>();
    }

    @Override
    public ISelection getSelection() {
        return fSelection;
    }

    @Override
    public void setSelection(ISelection selection) {
        fSelection= selection;

        for (ISelectionChangedListener l: fSelectionChangedListeners) {
            l.selectionChanged(new SelectionChangedEvent(this, selection));
        }
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        fSelectionChangedListeners.remove(listener);
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        fSelectionChangedListeners.add(listener);
    }
}