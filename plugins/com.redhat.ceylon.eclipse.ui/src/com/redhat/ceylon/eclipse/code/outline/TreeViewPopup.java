/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.redhat.ceylon.eclipse.code.outline;

import static org.eclipse.ui.IWorkbenchCommandConstants.WINDOW_SHOW_VIEW_MENU;

import java.util.StringTokenizer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ActionHandler;
import org.eclipse.ui.commands.HandlerSubmission;
import org.eclipse.ui.commands.Priority;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;

/**
 * Abstract class for Show hierarchy in light-weight controls.
 *
 * @since 2.1
 */
@SuppressWarnings("deprecation")
public abstract class TreeViewPopup extends PopupDialog 
        implements IInformationControl, IInformationControlExtension, 
                   IInformationControlExtension2, IInformationControlExtension3,
                   DisposeListener {

    private static GridLayoutFactory popupLayoutFactory;
    protected static GridLayoutFactory getPopupLayout() {
        if (popupLayoutFactory == null) {
            popupLayoutFactory = GridLayoutFactory.fillDefaults()
                    .margins(POPUP_MARGINWIDTH, POPUP_MARGINHEIGHT)
                    .spacing(POPUP_HORIZONTALSPACING, POPUP_VERTICALSPACING);
        }
        return popupLayoutFactory;
    }
    
//    protected CeylonEditor editor;
    
    private Text filterText;
    
    private TreeViewer treeViewer;
    
    //protected JavaElementPrefixPatternMatcher fPatternMatcher;

    /**
     * Fields that support the dialog menu
     * @since 3.0
     * @since 3.2 - now appended to framework menu
     */
    private Composite viewMenuButtonComposite;
    
    private IAction showViewMenuAction;
    private HandlerSubmission showViewMenuHandlerSubmission;

    private StyledText titleLabel;

    private TriggerSequence commandBinding;
    
    protected TriggerSequence getCommandBinding() {
        return commandBinding;
    }
    
    private final CeylonEditor editor; //has to be defined here because it is used from create() called by super constructor!
    
    /**
     * Creates a tree information control with the given shell as parent. The given
     * styles are applied to the shell and the tree widget.
     *
     * @param parent the parent shell
     * @param shellStyle the additional styles for the shell
     * @param treeStyle the additional styles for the tree widget
     * @param invokingCommandId the id of the command that invoked this control or <code>null</code>
     * @param showStatusField <code>true</code> iff the control has a status field at the bottom
     */
    public TreeViewPopup(Shell parent, int shellStyle,
            String invokingCommandId, CeylonEditor editor) {
        super(parent, shellStyle, true, true, false, true, true, null, null);
        this.editor = editor; //has to be initialized here because it is used from create() called by super constructor!
        if (invokingCommandId != null) {
            commandBinding = EditorUtil.getCommandBinding(invokingCommandId);
        }
        // Title and status text must be set to get the title label created, so force empty values here.
        setInfoText("");

        // Create all controls early to preserve the life cycle of the original implementation.
        create();
        
        // Status field text can only be computed after widgets are created.
        setInfoText(getStatusFieldText());
    }
    
    protected Control createContents(Composite parent) {
        Composite composite = (Composite) super.createContents(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.verticalSpacing=8;
        layout.marginLeft=8;
        layout.marginRight=8;
        layout.marginTop=8;
        layout.marginBottom=8;
        Control[] children = composite.getChildren();
//        children[2].setVisible(false);
        children[children.length-2].setVisible(false);
        return composite;
    }
    
    /**
     * Create the main content for this information control.
     *
     * @param parent The parent composite
     * @return The control representing the main content.
     * @since 3.2
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        treeViewer = createTreeViewer(parent);
        treeViewer.setAutoExpandLevel(getDefaultLevel());
        //fTreeViewer.setUseHashlookup(true);

        //fCustomFiltersActionGroup= new CustomFiltersActionGroup(getId(), fTreeViewer);

        final Tree tree = treeViewer.getTree();
        tree.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e)  {
                if (e.character == 0x1B) {// ESC
                    dispose();
                }
            }
            public void keyReleased(KeyEvent e) {
                // do nothing
            }
        });

        tree.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                // do nothing
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                gotoSelectedElement();
            }
        });

        tree.addMouseMoveListener(new TreeViewMouseListener(treeViewer));

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (tree.getSelectionCount()>=0 && 
                        e.button==1 &&
                        tree.equals(e.getSource())) {
                    Object o = tree.getItem(new Point(e.x, e.y));
                    TreeItem[] results = tree.getSelection();
                    if (results.length>0) {
                        TreeItem selection = results[0];
                        if (selection.equals(o)) {
                            gotoSelectedElement();
                        }
                    }
                }
            }
        });

        installFilter();

        addDisposeListener(this);
        return treeViewer.getControl();
    }

    protected abstract TreeViewer createTreeViewer(Composite parent);

    /**
     * Returns the name of the dialog settings section.
     *
     * @return the name of the dialog settings section
     */
    protected abstract String getId();

    protected TreeViewer getTreeViewer() {
        return treeViewer;
    }
    
    protected Text getFilterText() {
        return filterText;
    }

    protected Text createFilterText(Composite parent) {
        filterText = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
        filterText.setMessage("type filter text");
        Dialog.applyDialogFont(filterText);

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.CENTER;
        filterText.setLayoutData(data);

        filterText.addKeyListener(createViewerKeyListener());

        return filterText;
    }

    protected KeyListener createViewerKeyListener() {
        return new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 0x0D || e.keyCode == SWT.KEYPAD_CR) // Enter key
                    gotoSelectedElement();
                if (e.keyCode == SWT.ARROW_DOWN)
                    treeViewer.getTree().setFocus();
                if (e.keyCode == SWT.ARROW_UP)
                    treeViewer.getTree().setFocus();
                if (e.character == 0x1B) // ESC
                    dispose();
            }
            public void keyReleased(KeyEvent e) {
                // do nothing
            }
        };
    }

    protected void updateStatusFieldText() {
        setInfoText(getStatusFieldText());
    }

    protected String getStatusFieldText() {
        return "";
    }

    protected void installFilter() {
        filterText.setText("");
        filterText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.widget).getText();
                setMatcherString(text, true);
            }
        });
    }

    protected void reveal() {
        treeViewer.expandToLevel(getDefaultLevel());
    }

    protected int getDefaultLevel() {
//        return ALL_LEVELS;
        return 4;
    }

    /**
     * Sets the patterns to filter out for the receiver.
     *
     * @param pattern the pattern
     * @param update <code>true</code> if the viewer should be updated
     * 
     */
    protected void setMatcherString(String pattern, boolean update) {
        /*if (pattern.length() == 0) {
            fPatternMatcher= null;
        } else {
            fPatternMatcher= new JavaElementPrefixPatternMatcher(pattern);
        }*/

        if (update) {
            update();
        }
    }

    protected void update() {
        if (!getShell().isDisposed()) {
            treeViewer.getControl().setRedraw(false);
            // refresh viewer to re-filter
            treeViewer.refresh();
            reveal();
            //fTreeViewer.expandAll();
            selectFirstMatch(); //TODO select the main declaration instead!
            treeViewer.getControl().setRedraw(true);
        }
    }
    
    /**
     * Implementers can modify
     *
     * @return the selected element
     */
    protected Object getSelectedElement() {
        if (treeViewer == null) {
            return null;
        }
        else {
            IStructuredSelection selection = 
                    (IStructuredSelection) treeViewer.getSelection();
            return selection.getFirstElement();
        }
    }
    
    protected abstract void gotoSelectedElement();

    /**
     * Selects the first element in the tree which
     * matches the current filter pattern.
     */
    protected void selectFirstMatch() {
        //Object selectedElement= fTreeViewer.testFindItem(fInitiallySelectedType);
        TreeItem element;
        final Tree tree = treeViewer.getTree();
        /*if (selectedElement instanceof TreeItem)
            element= findElement(new TreeItem[] { (TreeItem)selectedElement });
        else*/
            element = findElement(tree.getItems());

        if (element != null) {
            tree.setSelection(element);
            tree.showItem(element);
        }
        else {
            treeViewer.setSelection(StructuredSelection.EMPTY);
        }
    }

    private TreeItem findElement(TreeItem[] items) {
        return findElement(items, null, true);
    }

    private TreeItem findElement(TreeItem[] items, TreeItem[] toBeSkipped, 
            boolean allowToGoUp) {
        return items.length > 0 ? items[0] : null;
        //TODO: reenable filtering
        /*if (fPatternMatcher == null)
            return items.length > 0 ? items[0] : null;

        ILabelProvider labelProvider= (ILabelProvider)fTreeViewer.getLabelProvider();

        // First search at same level
        for (int i= 0; i < items.length; i++) {
            final TreeItem item= items[i];
            IJavaElement element= (IJavaElement)item.getData();
            if (element != null) {
                String label= labelProvider.getText(element);
                if (fPatternMatcher.matches(label))
                    return item;
            }
        }

        // Go one level down for each item
        for (int i= 0; i < items.length; i++) {
            final TreeItem item= items[i];
            TreeItem foundItem= findElement(selectItems(item.getItems(), toBeSkipped), null, false);
            if (foundItem != null)
                return foundItem;
        }

        if (!allowToGoUp || items.length == 0)
            return null;

        // Go one level up (parent is the same for all items)
        TreeItem parentItem= items[0].getParentItem();
        if (parentItem != null)
            return findElement(new TreeItem[] { parentItem }, items, true);

        // Check root elements
        return findElement(selectItems(items[0].getParent().getItems(), items), null, false);*/
    }
    
    /*private boolean canSkip(TreeItem item, TreeItem[] toBeSkipped) {
        if (toBeSkipped == null)
            return false;
        
        for (int i= 0; i < toBeSkipped.length; i++) {
            if (toBeSkipped[i] == item)
                return true;
        }
        return false;
    }*/

    /*private TreeItem[] selectItems(TreeItem[] items, TreeItem[] toBeSkipped) {
        if (toBeSkipped == null || toBeSkipped.length == 0)
            return items;

        int j= 0;
        for (int i= 0; i < items.length; i++) {
            TreeItem item= items[i];
            if (!canSkip(item, toBeSkipped))
                items[j++]= item;
        }
        if (j == items.length)
            return items;

        TreeItem[] result= new TreeItem[j];
        System.arraycopy(items, 0, result, 0, j);
        return result;
    }*/


    public void setInformation(String information) {
        // this method is ignored, see IInformationControlExtension2
    }

    public abstract void setInput(Object information);

    /**
     * Fills the view menu.
     * Clients can extend or override.
     *
     * @param viewMenu the menu manager that manages the menu
     * @since 3.0
     */
    protected void fillViewMenu(IMenuManager viewMenu) {
        //fCustomFiltersActionGroup.fillViewMenu(viewMenu);
    }
    
    @Override
    protected void fillDialogMenu(IMenuManager dialogMenu) {
        super.fillDialogMenu(dialogMenu);
        fillViewMenu(dialogMenu);
    }

    protected void inputChanged(Object newInput, Object newSelection) {
        treeViewer.setInput(newInput);
        if (newSelection!=null) {
            treeViewer.setSelection(new StructuredSelection(newSelection));
        }
        filterText.setText("");
    }

    public void setVisible(boolean visible) {
        if (visible) {
            open();
        }
        else {
            removeHandlerAndKeyBindingSupport();
            saveDialogBounds(getShell());
            getShell().setVisible(false);
        }
    }

    @Override
    public int open() {
        addHandlerAndKeyBindingSupport();
        return super.open();
    }

    public final void dispose() {
        close();
    }

    public void widgetDisposed(DisposeEvent event) {
        removeHandlerAndKeyBindingSupport();
        treeViewer = null;
        filterText = null;
    }
    
    protected void addHandlerAndKeyBindingSupport() {
        // Register action with command support
        if (showViewMenuHandlerSubmission == null) {
            showViewMenuHandlerSubmission= new HandlerSubmission(null, getShell(),
                    null, showViewMenuAction.getActionDefinitionId(), 
                    new ActionHandler(showViewMenuAction), Priority.MEDIUM);
            PlatformUI.getWorkbench().getCommandSupport()
                .addHandlerSubmission(showViewMenuHandlerSubmission);
        }
    }
    
    protected void removeHandlerAndKeyBindingSupport() {
        // Remove handler submission
        if (showViewMenuHandlerSubmission != null) {
            PlatformUI.getWorkbench().getCommandSupport()
                .removeHandlerSubmission(showViewMenuHandlerSubmission);
        }

    }
    
    public boolean hasContents() {
        return treeViewer != null && 
                treeViewer.getInput() != null;
    }
    
    public void setSizeConstraints(int maxWidth, int maxHeight) {
        // ignore
    }

    public Point computeSizeHint() {
        // return the shell's size - note that it already has the persisted size if persisting
        // is enabled.
        return getShell().getSize();
    }

    public void setLocation(Point location) {
        /*
         * If the location is persisted, it gets managed by PopupDialog - fine. Otherwise, the location is
         * computed in Window#getInitialLocation, which will center it in the parent shell / main
         * monitor, which is wrong for two reasons:
         * - we want to center over the editor / subject control, not the parent shell
         * - the center is computed via the initalSize, which may be also wrong since the size may
         *   have been updated since via min/max sizing of AbstractInformationControlManager.
         * In that case, override the location with the one computed by the manager. Note that
         * the call to constrainShellSize in PopupDialog.open will still ensure that the shell is
         * entirely visible.
         */
        if (!getPersistLocation() || 
                getDialogSettings() == null) {
            getShell().setLocation(location);
        }
    }

    public void setSize(int width, int height) {
        getShell().setSize(width, height);
    }

    public void addDisposeListener(DisposeListener listener) {
        getShell().addDisposeListener(listener);
    }

    public void removeDisposeListener(DisposeListener listener) {
        getShell().removeDisposeListener(listener);
    }

    public void setForegroundColor(Color foreground) {
        applyForegroundColor(foreground, getContents());
    }

    public void setBackgroundColor(Color background) {
        applyBackgroundColor(background, getContents());
    }

    public boolean isFocusControl() {
        return getShell().getDisplay().getActiveShell() == getShell();
    }

    public void setFocus() {
        getShell().forceFocus();
        filterText.setFocus();
    }

    public void addFocusListener(FocusListener listener) {
        getShell().addFocusListener(listener);
    }

    public void removeFocusListener(FocusListener listener) {
        getShell().removeFocusListener(listener);
    }
    
    @Override
    protected IDialogSettings getDialogSettings() {
        String sectionName = getId();
        IDialogSettings dialogSettings = 
                CeylonPlugin.getInstance().getDialogSettings();
        IDialogSettings settings = 
                dialogSettings.getSection(sectionName);
        if (settings == null) {
            settings = dialogSettings.addNewSection(sectionName);
        }
        return settings;
    }
    
    @Override
    protected Control createTitleMenuArea(Composite parent) {
        viewMenuButtonComposite = 
                (Composite) super.createTitleMenuArea(parent);
        // the filter text must be created
        // underneath the title and menu area.
        filterText = createFilterText(parent);
        
        // Create show view menu action
        showViewMenuAction = 
                new Action("showViewMenu") {
            @Override
            public void run() {
                showDialogMenu();
            }
        };
        showViewMenuAction.setEnabled(true);
        showViewMenuAction.setActionDefinitionId(WINDOW_SHOW_VIEW_MENU);

        return viewMenuButtonComposite;
    }

    protected StyledString styleTitle(final StyledText title) {
        StyledString result = new StyledString();
        StringTokenizer tokens = 
                new StringTokenizer(title.getText(), "-", false);
        styleDescription(title, result, tokens.nextToken());
        result.append("-").append(tokens.nextToken());
        return result;
    }

    protected void styleDescription(final StyledText title, StyledString result,
            String desc) {
        final FontData[] fontDatas = title.getFont().getFontData();
        for (int i=0; i<fontDatas.length; i++) {
            fontDatas[i].setStyle(SWT.BOLD);
        }
        result.append(desc, new Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                textStyle.font=new Font(title.getDisplay(), fontDatas);
            }
        });
    }

    @Override
    protected Control createTitleControl(Composite parent) {
        titleLabel = new StyledText(parent, SWT.NONE);
        titleLabel.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                titleLabel.setStyleRanges(styleTitle(titleLabel).getStyleRanges());
            }
        });
        titleLabel.setEditable(false);
        GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.CENTER)
            .grab(true,false)
            .span(1, 1)
            .applyTo(titleLabel);
        return null;
    }
    
    @Override
    protected void setTitleText(String text) {
        if (titleLabel!=null) {
            titleLabel.setText(text);
        }
    }
    
    @Override
    protected void setTabOrder(Composite composite) {
        composite.setTabList(new Control[] { 
                filterText, 
                treeViewer.getTree()
        });
    }
    
    @Override
    public boolean restoresLocation() {
        return false;
    }
    
    @Override
    public boolean restoresSize() {
        return true;
    }
    
    @Override
    public Rectangle getBounds() {
        return getShell().getBounds();
    }
    
    @Override
    public Rectangle computeTrim() {
        return getShell().computeTrim(0, 0, 0, 0);
    }

    protected CeylonEditor getEditor() {
        return editor;
    }
}

