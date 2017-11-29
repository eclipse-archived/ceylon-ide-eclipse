/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.SearchPattern;

import org.eclipse.ceylon.ide.eclipse.util.DocBrowser;

public abstract class FilteredElementTreeSelectionDialog extends
        ElementTreeSelectionDialog {
    
    public FilteredElementTreeSelectionDialog(Shell parent,
            ILabelProvider labelProvider,
            ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);
    }
    
    protected abstract String getElementName(Object element);
    
    protected boolean isCategory(Object element) {
        return false;
    }
    
    private Text createFilterText(Composite parent) {
        final Text text = new Text(parent, SWT.BORDER);
        final SearchPattern searchPattern = new SearchPattern();
        searchPattern.setPattern("");
        addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, 
                    Object parentElement, Object element) {
                if (searchPattern.matches(getElementName(element))) {
                    return true;
                }
//                if (isCategory(element) && 
//                        !getTreeViewer().getExpandedState(element)) {
//                    //don't go to the server and 
//                    //fetch children just to filter
//                    //out an unexpanded category
//                    return true;
//                }
                Object[] children = ((ITreeContentProvider)getTreeViewer().getContentProvider())
                        .getChildren(element);
                if (children==null) return false;
                for (Object child: children) {
                    if (select(viewer, element, child)) {
                        return true;
                    }
                }
                return false;
            }
        });
        GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        text.setLayoutData(data);
        text.setFont(parent.getFont());
        text.setText("");
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                searchPattern.setPattern(text.getText());
                getTreeViewer().refresh();
            }
        });
        return text;
    }

    @Override
    protected Label createMessageArea(Composite composite) {
        Label result = super.createMessageArea(composite);
        createFilterText(composite);
        return result;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
        Control result = super.createDialogArea(sashForm);
//        getTreeViewer().setSorter(new ViewerSorter());
        Composite composite = new Composite(sashForm, SWT.BORDER);
        composite.setLayoutData(gridData);
        GridLayout layout = new GridLayout(1, true);
        layout.marginWidth=0;
        layout.marginHeight=0;
        composite.setLayout(layout);
        final DocBrowser browser = new DocBrowser(composite, SWT.NONE);
        sashForm.setWeights(new int[] {3, 1});
        sashForm.setLayoutData(gridData);
        getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                browser.setText(getDoc());
            }
        });
        return result;
    }

    protected String getDoc() {
        return "";
    }
    
}