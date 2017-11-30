/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getActivePage;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getDirtyEditors;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getFile;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.viewsupport.StorageLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;

public class RecentFilesPopup extends PopupDialog {
    
    public static List<IFile> recents = new ArrayList<IFile>();
    
    private Text filterText;
    private TableViewer list;
    
    RecentFilesPopup(Shell shell) {
        super(shell, SWT.NONE, true, false, false, false, false, "", null);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.marginTop = 0;
        layout.marginLeft = 2;
        layout.marginRight = 2;
        layout.marginBottom = 2;
        parent.setLayout(layout);
        list = new TableViewer(parent, 
                SWT.NO_TRIM|SWT.SINGLE|SWT.FULL_SELECTION);
        list.setFilters(new ViewerFilter[] {new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                return ((IFile) element).getName().toLowerCase()
                        .startsWith(filterText.getText().toLowerCase());
            }
        }});
        list.setLabelProvider(new StorageLabelProvider() {
            @Override
            public String getText(Object element) {
                for (IEditorPart part: EditorUtil.getDirtyEditors()) {
                    if (getFile(part.getEditorInput())==element) {
                        return "*" + super.getText(element);
                    }
                }
                return super.getText(element);
            }
        });
        list.setContentProvider(ArrayContentProvider.getInstance());
        list.getTable().setCursor(new Cursor(getShell().getDisplay(), SWT.CURSOR_HAND));
        list.getTable().addListener(SWT.MouseMove, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle bounds = event.getBounds();
                TableItem item = list.getTable().getItem(new Point(bounds.x, bounds.y));
                if (item!=null) {
                    list.setSelection(new StructuredSelection(item.getData()));
                }
            }
        });
        list.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 0x0D || e.keyCode == SWT.KEYPAD_CR) { // Enter key
                    go();
                }
            }
        });
        list.getTable().addMouseListener(new MouseListener() {
            @Override
            public void mouseUp(MouseEvent e) {
                go();
            }
            @Override
            public void mouseDown(MouseEvent e) {}
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                go();
            }
        });
        List<IFile> files = new ArrayList<IFile>(recents);
        for (IEditorPart part: getDirtyEditors()) {
            IFile file = getFile(part.getEditorInput());
            if (file!=null) {
                files.remove(file);
                files.add(0, file);
            }
        }
        list.setInput(files);
        if (files.isEmpty()) {
            filterText.setMessage("no files");
        }
        else {
            list.setSelection(new StructuredSelection(files.get(0)));
        }
        return list.getControl();
    }
    
    void go() {
        StructuredSelection selection = (StructuredSelection) list.getSelection();
        IFile file = (IFile) selection.getFirstElement();
        try {
            IDE.openEditor(getActivePage(), file);
        }
        catch (PartInitException e) {
            e.printStackTrace();
        }
        close();
    }
    
    @Override
    protected Control createTitleControl(Composite parent) {
        filterText= createFilterText(parent);
        return filterText;
    }

    protected Text getFilterText() {
        return filterText;
    }

    protected Text createFilterText(Composite parent) {
        filterText= new Text(parent, SWT.NONE);
        filterText.setMessage("type filter text");
        Dialog.applyDialogFont(filterText);

        GridData data= new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalAlignment= GridData.FILL;
        data.verticalAlignment= GridData.CENTER;
        filterText.setLayoutData(data);

        filterText.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 0x0D || e.keyCode == SWT.KEYPAD_CR) // Enter key
                    go();
                if (e.keyCode == SWT.ARROW_DOWN)
                    list.getTable().setFocus();
                if (e.keyCode == SWT.ARROW_UP)
                    list.getTable().setFocus();
                if (e.character == 0x1B) // ESC
                    dispose();
            }
            public void keyReleased(KeyEvent e) {
                // do nothing
            }
        });
        filterText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                list.refresh();
                Object elem = list.getElementAt(0);
                if (elem!=null) {
                    list.setSelection(new StructuredSelection(elem));
                }
            }
        });

        return filterText;
    }

    public final void dispose() {
        close();
    }
    
    public void widgetDisposed(DisposeEvent event) {
        list = null;
        filterText = null;
    }

    public void setFocus() {
        getShell().forceFocus();
        filterText.setFocus();
    }

    public static void addToHistory(IFile file) {
        if (file!=null) {
            if (!recents.contains(file)) {
                recents.add(file);
                if (recents.size()>10) {
                    recents.remove(0);
                }
            }
        }
    }

}
