package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getActivePage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.viewsupport.StorageLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

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
        list = new TableViewer(parent, SWT.NO_TRIM|SWT.SINGLE|SWT.FULL_SELECTION);
        list.setLabelProvider(new StorageLabelProvider());
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
                if (e.character=='\r') {
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
        list.setInput(recents);
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
        Dialog.applyDialogFont(filterText);

        GridData data= new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalAlignment= GridData.FILL;
        data.verticalAlignment= GridData.CENTER;
        filterText.setLayoutData(data);

        filterText.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 0x0D || e.keyCode == SWT.KEYPAD_CR) // Enter key
//                    gotoSelectedElement();
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

}
