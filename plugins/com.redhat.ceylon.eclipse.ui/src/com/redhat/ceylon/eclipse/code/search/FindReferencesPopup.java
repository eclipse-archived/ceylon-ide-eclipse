package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelectedNode;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoFile;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_REFS;
import static com.redhat.ceylon.eclipse.util.Highlights.getCurrentThemeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.complete.CompletionUtil;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;

public final class FindReferencesPopup extends PopupDialog 
        implements IInformationControl, IInformationControlExtension2,
                   IInformationControlExtension3 {
    
    protected Text filterText;
    
    public TableViewer viewer;
    
    CeylonEditor editor;
    
    private StyledText titleLabel;

    private TriggerSequence commandBinding;
    
    protected TriggerSequence getCommandBinding() {
        return commandBinding;
    }
    
    public FindReferencesPopup(Shell parent, int shellStyle, CeylonEditor editor) {
        super(parent, shellStyle, true, true, false, true,
                true, null, null);
        setTitleText("Quick Find References");
        this.editor = editor;
        //TODO: cycle through the various kinds of Find query?
//        commandBinding = EditorUtil.getCommandBinding("com.redhat.ceylon.eclipse.ui.editor.findReferences");
//        if (commandBinding!=null) {
//            setInfoText(commandBinding.format() + " to open editor");
//        }
        
        create();
        
        Color color = getCurrentThemeColor("outline");
        getShell().setBackground(color);
        setBackgroundColor(color);

        //setBackgroundColor(getEditorWidget(editor).getBackground());
        setForegroundColor(getEditorWidget(editor).getForeground());
    }

    private StyledText getEditorWidget(CeylonEditor editor) {
        return editor.getCeylonSourceViewer().getTextWidget();
    }

    protected Control createContents(Composite parent) {
        Composite composite = (Composite) super.createContents(parent);
        Control[] children = composite.getChildren();
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.verticalSpacing=8;
        layout.marginLeft=8;
        layout.marginRight=8;
        layout.marginTop=8;
        layout.marginBottom=8;
        children[children.length-2].setVisible(false);
        viewer.getTable().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        return composite;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        viewer = new TableViewer(parent, SWT.FLAT);
//        GridData data = new GridData();
//        viewer.getTable().setLayoutData(new GridData());
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setLabelProvider(new CeylonLabelProvider(true) {
            @Override
            public StyledString getStyledText(Object element) {
                return super.getStyledText(((CeylonSearchMatch) element).getElement());
            }
            @Override
            public Image getImage(Object element) {
                return super.getImage(((CeylonSearchMatch) element).getElement());
            }
        });
        installFilter();
        viewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                CeylonSearchMatch match = (CeylonSearchMatch) element;
                String filter = filterText.getText().toLowerCase();
                String[] split = match.getElement().getLabel().getString().split(" ");
                return split.length>1 && split[1].toLowerCase().startsWith(filter) ||
                        match.getElement().getPackageLabel()
                            .toLowerCase().startsWith(filter) ||
                        match.getElement().getFile().getName().toString()
                            .toLowerCase().startsWith(filter);
            }
        });
//        viewer.getTable().addSelectionListener(new SelectionListener() {
//            public void widgetSelected(SelectionEvent e) {
//                // do nothing
//            }
//            public void widgetDefaultSelected(SelectionEvent e) {
//                gotoSelectedElement();
//            }
//        });
        viewer.getTable().setCursor(new Cursor(getShell().getDisplay(), SWT.CURSOR_HAND));
        viewer.getTable().addListener(SWT.MouseMove, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle bounds = event.getBounds();
                TableItem item = viewer.getTable().getItem(new Point(bounds.x, bounds.y));
                if (item!=null) {
                    viewer.setSelection(new StructuredSelection(item.getData()));
                }
            }
        });
        viewer.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 0x0D || e.keyCode == SWT.KEYPAD_CR) { // Enter key
                    gotoSelectedElement();
                }
            }
        });
        viewer.getTable().addMouseListener(new MouseListener() {
            @Override
            public void mouseUp(MouseEvent e) {
                gotoSelectedElement();
            }
            @Override
            public void mouseDown(MouseEvent e) {}
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                gotoSelectedElement();
            }
        });

        return viewer.getControl();
    }
    
    protected void gotoSelectedElement() {
        Object elem = ((StructuredSelection) viewer.getSelection()).getFirstElement();
        if (elem instanceof CeylonSearchMatch) {
            CeylonSearchMatch match = (CeylonSearchMatch) elem;
            gotoFile(match.getElement().getFile(), match.getOffset(), match.getLength());
        }
    }

    private static GridLayoutFactory popupLayoutFactory;
    protected static GridLayoutFactory getPopupLayout() {
        if (popupLayoutFactory == null) {
            popupLayoutFactory = GridLayoutFactory.fillDefaults()
                    .margins(POPUP_MARGINWIDTH, POPUP_MARGINHEIGHT)
                    .spacing(POPUP_HORIZONTALSPACING, POPUP_VERTICALSPACING);
        }
        return popupLayoutFactory;
    }
    
    protected StyledString styleTitle(final StyledText title) {
        StyledString result = new StyledString();
        StringTokenizer tokens = 
                new StringTokenizer(title.getText(), "-", false);
        styleDescription(title, result, tokens.nextToken());
        result.append("-");
        CompletionUtil.styleProposal(result, tokens.nextToken());
        return result;
    }

    protected void styleDescription(final StyledText title, StyledString result,
            String desc) {
        final FontData[] fontDatas = title.getFont().getFontData();
        for (int i = 0; i < fontDatas.length; i++) {
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
        getPopupLayout().copy().numColumns(3).spacing(6, 6).applyTo(parent);
        Label iconLabel = new Label(parent, SWT.NONE);
        iconLabel.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CEYLON_REFS));
//        getShell().addKeyListener(new GotoListener());
        titleLabel = new StyledText(parent, SWT.NONE);
        titleLabel.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                titleLabel.setStyleRanges(styleTitle(titleLabel).getStyleRanges());
            }
        });
        titleLabel.setEditable(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER)
            .grab(true,false).span(1, 1).applyTo(titleLabel);
        return null;
    }
    
    protected Text createFilterText(Composite parent) {
        filterText= new Text(parent, SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
        filterText.setMessage("type filter text");
        Dialog.applyDialogFont(filterText);

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.CENTER;
        filterText.setLayoutData(data);

        filterText.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 0x0D || e.keyCode == SWT.KEYPAD_CR) // Enter key
                    gotoSelectedElement();
                if (e.keyCode == SWT.ARROW_DOWN)
                    viewer.getTable().setFocus();
                if (e.keyCode == SWT.ARROW_UP)
                    viewer.getTable().setFocus();
                if (e.character == 0x1B) // ESC
                    dispose();
            }
            public void keyReleased(KeyEvent e) {
                // do nothing
            }
        });
        
        return filterText;
    }

    @Override
    protected void setTitleText(String text) {
        if (titleLabel!=null) {
            titleLabel.setText(text);
        }
    }
    
    private void installFilter() {
        filterText.setText("");
        filterText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.widget).getText();
                setMatcherString(text, true);
            }
        });
    }

    protected void setMatcherString(String pattern, boolean update) {
        /*if (pattern.length() == 0) {
            fPatternMatcher= null;
        } else {
            fPatternMatcher= new JavaElementPrefixPatternMatcher(pattern);
        }*/

        if (update) {
            viewer.getControl().setRedraw(false);
            viewer.refresh();
            viewer.getControl().setRedraw(true);
        }
    }

    @Override
    protected Control createTitleMenuArea(Composite parent) {
        Control result = super.createTitleMenuArea(parent);
        filterText = createFilterText(parent);
        return result;
    }
    
    /*@Override
    protected void adjustBounds() {
        Rectangle bounds = getShell().getBounds();
        int h = bounds.height;
        if (h>400) {
            bounds.height=400;
            bounds.y = bounds.y + (h-400)/3;
            getShell().setBounds(bounds);
        }
        int w = bounds.width;
        if (w<600) {
            bounds.width=600;
            getShell().setBounds(bounds);
        }
    }*/
    
    public void setInformation(String information) {
        // this method is ignored, see IInformationControlExtension2
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


    public void setSizeConstraints(int maxWidth, int maxHeight) {
        // ignore
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
        if (!getPersistLocation() || getDialogSettings() == null)
            getShell().setLocation(location);
    }

    public Point computeSizeHint() {
        // return the shell's size - note that it already has the persisted size if persisting
        // is enabled.
        return getShell().getSize();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            open();
        }
        else {
            saveDialogBounds(getShell());
            getShell().setVisible(false);
        }
    }

    public final void dispose() {
        close();
    }
    
    @Override
    public void setInput(Object input) {
        CeylonParseController pc = editor.getParseController();
//        IProject project = getProject(editor);
        Declaration declaration = 
                Nodes.getReferencedExplicitDeclaration(getSelectedNode(editor), 
                        pc.getRootNode());
        setTitleText("Quick Find References - " + declaration.getName(pc.getRootNode().getUnit()));
        List<CeylonSearchMatch> list = new ArrayList<CeylonSearchMatch>();
        for (PhasedUnit pu: pc.getTypeChecker().getPhasedUnits().getPhasedUnits()) {
            FindReferencesVisitor frv = new FindReferencesVisitor(declaration);
            frv.visit(pu.getCompilationUnit());
            for (Node node: frv.getNodes()) {
                FindContainerVisitor fcv = new FindContainerVisitor(node);
                pu.getCompilationUnit().visit(fcv);
                Tree.StatementOrArgument c = fcv.getStatementOrArgument();
                if (c!=null) {
                    list.add(new CeylonSearchMatch(c, pu.getUnitFile(), node));
                }
            }
        }
        viewer.setInput(list);
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
    
    @Override
    protected IDialogSettings getDialogSettings() {
        String sectionName = "com.redhat.ceylon.eclipse.ui.FindReferences";
        IDialogSettings dialogSettings = CeylonPlugin.getInstance()
                .getDialogSettings();
        IDialogSettings settings = dialogSettings.getSection(sectionName);
        if (settings == null)
            settings= dialogSettings.addNewSection(sectionName);
        return settings;
    }

}