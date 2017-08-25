package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoFile;
import static com.redhat.ceylon.eclipse.code.open.OpenDeclarationDialog.isMatchingGlob;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.FULL_LOC_SEARCH_RESULTS;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.imageRegistry;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_DECS;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_IMPORT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_REFS;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.FLAT_MODE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.TREE_MODE;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCommandBinding;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getEnterBinding;
import static com.redhat.ceylon.eclipse.util.EditorUtil.triggersBinding;
import static com.redhat.ceylon.eclipse.util.Highlights.PACKAGE_STYLER;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedExplicitDeclaration;
import static java.util.Collections.emptySet;
import static org.eclipse.jface.action.IAction.AS_CHECK_BOX;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.TreeNodeLabelProvider;
import com.redhat.ceylon.eclipse.code.outline.TreeViewMouseListener;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.ide.common.util.FindReferencesVisitor;
import com.redhat.ceylon.ide.common.util.FindRefinementsVisitor;
import com.redhat.ceylon.ide.common.util.FindSubtypesVisitor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public final class ReferencesPopup extends PopupDialog 
        implements IInformationControl, 
                   IInformationControlExtension2,
                   IInformationControlExtension3 {
    
    private static final Image REFS_IMAGE = 
            imageRegistry().get(CEYLON_REFS);
    private static final Image DECS_IMAGE = 
            imageRegistry().get(CEYLON_DECS);

    public class ChangeLayoutListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            switchLayout();
        }
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {}
    }

    private String getPrefix() {
        return filterText.getText();
    }
    
    private Font getFont() {
        return viewer.getControl().getFont();
    }

    public final class LabelProvider extends TreeNodeLabelProvider {
        public LabelProvider() {
            super(new SearchResultsLabelProvider() {
                @Override
                public String getPrefix() {
                    return ReferencesPopup.this.getPrefix();
                }
                @Override
                public Font getFont() {
                    return ReferencesPopup.this.getFont();
                }
            });
        }
        
        @Override
        public StyledString getStyledText(Object element) {
            Object unwrapped = unwrap(element);
            if (unwrapped instanceof CeylonElement) {
                CeylonElement ce = (CeylonElement) unwrapped;
                StyledString label = 
                        ce.getLabel(getPrefix(), getFont());
                if (!ReferencesPopup.this.treeLayout) {
                    label = new StyledString().append(label);
                    label.append(" \u2014 ", PACKAGE_STYLER)
                        .append(ce.getPackageLabel(), PACKAGE_STYLER);
                    if (CeylonPlugin.getPreferences()
                            .getBoolean(FULL_LOC_SEARCH_RESULTS)) {
                        label.append(" \u2014 ", COUNTER_STYLER)
                            .append(ce.getPathString(), COUNTER_STYLER);
                    }
                }
                Integer matchCount = matchCounts.get(ce);
                if (matchCount!=null && matchCount>1) {
                    label = new StyledString().append(label);
                    label.append(" (" + matchCount + " matches)", 
                            Highlights.ARROW_STYLER);
                }
                return label;
            }
            else {
                return super.getStyledText(element);
            }
        }

        @Override
        public Object unwrap(Object element) {
            Object unwrapped = super.unwrap(element);
            if (unwrapped instanceof CeylonSearchMatch) {
                CeylonSearchMatch match = 
                        (CeylonSearchMatch) unwrapped;
                return match.getElement();
            }
            else {
                return unwrapped;
            }
        }
    }

    public final class ClickListener implements MouseListener {
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
    }

    public final class ShortcutKeyListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            triggerCommand(e);
            
            char character = e.character;
            int keyCode = e.keyCode;
            int stateMask = e.stateMask;
            
            if (stateMask==SWT.NONE || stateMask==SWT.SHIFT) {
                if (Character.isLetter(character) 
                        && keyCode==Character.toLowerCase(character)) {
                    String string = filterText.getText() + character;
                    filterText.setText(string);
                    filterText.setFocus();
                    filterText.setSelection(string.length());
                }
                if (character == SWT.BS && keyCode==SWT.BS) {
                    String string = filterText.getText();
                    if (string.length()>0) {
                        string = string.substring(0,string.length()-1);
                    }
                    filterText.setText(string);
                    filterText.setFocus();
                    filterText.setSelection(string.length());
                }
            }
            
            if (keyCode == 0x0D || keyCode == SWT.KEYPAD_CR) { // Enter key
                gotoSelectedElement();
            }
        }
    }

    public final class Filter extends ViewerFilter {
        @Override
        public boolean select(Viewer viewer, 
                Object parentElement, Object element) {
            TreeNode treeNode = (TreeNode) element;
            Object value = treeNode.getValue();
            if (value instanceof CeylonSearchMatch) {
                CeylonSearchMatch match = 
                        (CeylonSearchMatch) value;
                String filter = filterText.getText();
                CeylonElement e = match.getElement();
                String[] split = 
                        e.getLabel()
                            .getString()
                            .split(" ");
                return split.length>1 && 
                        isMatchingGlob(filter, split[1]) ||
                        isMatchingGlob(filter, 
                                e.getPackageLabel()) ||
                        isMatchingGlob(filter, 
                                e.getFile().getName());
            }
            else {
                for (TreeNode child: treeNode.getChildren()) {
                    if (select(viewer, element, child)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    protected Text filterText;
    
    private ColumnViewer viewer;
    
    private final CeylonEditor editor;
    
    private StyledText titleLabel;

    private TriggerSequence commandBinding;
    private TriggerSequence findCommandBinding;
    
    private boolean showingRefinements = false;
    
    public ReferencesPopup(Shell parent, int shellStyle, 
            CeylonEditor editor) {
        super(parent, shellStyle, true, true, false, true,
                true, null, null);
        treeLayout = 
                getDialogSettings()
                    .getBoolean("treeLayout");
        includeImports = 
                getDialogSettings()
                    .getBoolean("includeImports");
        setTitleText("Quick Find References");
        this.editor = editor;
        commandBinding = 
                getCommandBinding(PLUGIN_ID + 
                        ".editor.findReferences");
        findCommandBinding = 
                getCommandBinding(PLUGIN_ID + 
                        ".action.findReferences");
        setStatusText();
        create();
        
        /*Color bg = parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        getShell().setBackground(bg);
        setBackgroundColor(bg);

        //setBackgroundColor(getEditorWidget(editor).getBackground());
        setForegroundColor(getEditorWidget(editor).getForeground());*/     
    }
    
    private void setIcon() {
        if (showingRefinements) {
            icon.setImage(DECS_IMAGE);
        }
        else {
            icon.setImage(REFS_IMAGE);
        }
    }

    private void setStatusText() {
        StringBuilder builder = new StringBuilder();
        if (findCommandBinding!=null) {
            builder.append(findCommandBinding.format())
                .append(" to find all references");
        }
        if (commandBinding!=null) {
            String message;
            if (showingRefinements) {
                message = " to show references";
            }
            else {
                if (type) {
                    message = " to show subtypes";
                }
                else {
                    message = " to show refinements";
                }
            }
            if (builder.length()>0) {
                builder.append(" \u00b7 ");
            }
            builder.append(commandBinding.format())
                .append(message);
        }
        if (builder.length()>0) {
            builder.append(" \u00b7 ");
        }
        builder.append(getEnterBinding())
            .append(" to open");
        if (builder.length()>0) {
            setInfoText(builder.toString());
        }
    }

    /*private StyledText getEditorWidget(CeylonEditor editor) {
        return editor.getCeylonSourceViewer().getTextWidget();
    }*/

    protected Control createContents(Composite parent) {
        Composite composite = (Composite) super.createContents(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.verticalSpacing=8;
        layout.marginLeft=8;
        layout.marginRight=8;
        layout.marginTop=8;
        layout.marginBottom=8;
        Control[] children = composite.getChildren();
        children[children.length-2].setVisible(false);
        return composite;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        treeViewer = new TreeViewer(parent, SWT.SINGLE);
        treeViewer.getTree().setVisible(treeLayout);
        GridData gdTree = new GridData(GridData.FILL_BOTH);
        gdTree.exclude = !treeLayout;
        treeViewer.getTree().setLayoutData(gdTree);
        treeViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
        tableViewer = new TableViewer(parent, SWT.SINGLE);
        tableViewer.getTable().setVisible(!treeLayout);
        GridData gdTable = new GridData(GridData.FILL_BOTH);
        gdTable.exclude = treeLayout;
        tableViewer.getTable().setLayoutData(gdTable);
        viewer = treeLayout ? treeViewer : tableViewer;
        tableViewer.setComparator(new CeylonViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                TreeNode treeNode1 = (TreeNode) e1;
                TreeNode treeNode2 = (TreeNode) e2;
                Object v1 = treeNode1.getValue();
                Object v2 = treeNode2.getValue();
                return super.compare(viewer, v1, v2);
            }
        });
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        treeViewer.setContentProvider(new TreeNodeContentProvider());
        tableViewer.setLabelProvider(new LabelProvider());
        treeViewer.setLabelProvider(new LabelProvider());
        installFilter();
        ViewerFilter filter = new Filter();
        tableViewer.addFilter(filter);
        treeViewer.addFilter(filter);
//        viewer.getTable().addSelectionListener(new SelectionListener() {
//            public void widgetSelected(SelectionEvent e) {
//                // do nothing
//            }
//            public void widgetDefaultSelected(SelectionEvent e) {
//                gotoSelectedElement();
//            }
//        });
        Display display = getShell().getDisplay();
        Cursor cursor = new Cursor(display, SWT.CURSOR_HAND);
        tableViewer.getControl().setCursor(cursor);
        treeViewer.getControl().setCursor(cursor);
        tableViewer.getControl()
            .addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                Item item = 
                        tableViewer.getTable()
                            .getItem(new Point(e.x, e.y));
                if (item!=null) {
                    StructuredSelection selection = 
                            new StructuredSelection(item.getData());
                    tableViewer.setSelection(selection);
                }
            }
        });
        treeViewer.getControl().addMouseMoveListener(
                new TreeViewMouseListener(treeViewer));
        ShortcutKeyListener listener = new ShortcutKeyListener();
        tableViewer.getControl().addKeyListener(listener);
        treeViewer.getControl().addKeyListener(listener);
        MouseListener clickListener = new ClickListener();
        tableViewer.getControl().addMouseListener(clickListener);
        treeViewer.getControl().addMouseListener(clickListener);
        viewer.getControl().getParent().layout(/*true*/);
        Font outlineFont = CeylonPlugin.getOutlineFont();
        tableViewer.getControl().setFont(outlineFont);
        treeViewer.getControl().setFont(outlineFont);
        return viewer.getControl();
    }
    
    protected void gotoSelectedElement() {
        StructuredSelection selection = 
                (StructuredSelection) 
                    viewer.getSelection();
        Object node = selection.getFirstElement();
        if (node!=null) {
            TreeNode treeNode = (TreeNode) node;
            Object elem = treeNode.getValue();
            if (elem instanceof CeylonSearchMatch) {
                CeylonSearchMatch match = 
                        (CeylonSearchMatch) elem;
                gotoFile(match.getElement().getFile(), 
                        match.getOffset(), match.getLength());
            }
        }
    }

    private static GridLayoutFactory popupLayoutFactory;
    protected static GridLayoutFactory getPopupLayout() {
        if (popupLayoutFactory == null) {
            popupLayoutFactory = 
                    GridLayoutFactory.fillDefaults()
                        .margins(POPUP_MARGINWIDTH, POPUP_MARGINHEIGHT)
                        .spacing(POPUP_HORIZONTALSPACING, POPUP_VERTICALSPACING);
        }
        return popupLayoutFactory;
    }
    
    protected StyledString styleTitle(final StyledText title) {
        StyledString result = new StyledString();
        StringTokenizer tokens = 
                new StringTokenizer(title.getText(), 
                        "\u2014", false);
        styleDescription(title, result, tokens.nextToken());
        result.append("\u2014");
        String rest = tokens.nextToken();
        int loc = rest.indexOf(" to ");
        if (loc<1) loc = rest.indexOf(" of ");
        loc+=4;
        result.append(rest.substring(0,loc));
        int end = rest.indexOf(" in ", loc);
        Highlights.styleFragment(result, 
                rest.substring(loc, end), 
                false, null, 
                CeylonPlugin.getOutlineFont());
        return result;
    }

    protected void styleDescription(final StyledText title, 
            StyledString result, String desc) {
        final FontData[] fontDatas = 
                title.getFont().getFontData();
        for (int i = 0; i < fontDatas.length; i++) {
            fontDatas[i].setStyle(SWT.BOLD);
        }
        result.append(desc, new Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                textStyle.font = 
                        new Font(title.getDisplay(), 
                                fontDatas);
            }
        });
    }
    
    private boolean includeImports = false;
    private boolean treeLayout = false;
    
    @Override
    protected Control createTitleControl(Composite parent) {
        getPopupLayout().copy()
            .numColumns(4)
            .spacing(6, 6)
            .applyTo(parent);
        icon = new Label(parent, SWT.NONE);
        icon.setImage(REFS_IMAGE);
//        getShell().addKeyListener(new GotoListener());
        titleLabel = new StyledText(parent, SWT.NONE);
        titleLabel.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                StyleRange[] styleRanges = 
                        styleTitle(titleLabel)
                            .getStyleRanges();
                titleLabel.setStyleRanges(styleRanges);
            }
        });
        titleLabel.setEditable(false);
        GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.CENTER)
            .grab(true,false)
            .span(1, 1)
            .applyTo(titleLabel);
//        Button button = new Button(parent, SWT.TOGGLE);
//        button.setImage(CeylonLabelProvider.IMPORT);
//        button.setText("include imports");
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
        createModeButtons(toolBar);
        new ToolItem(toolBar, SWT.SEPARATOR);
        createLayoutButtons(toolBar);
        new ToolItem(toolBar, SWT.SEPARATOR);
        createImportsButton(toolBar);
        return null;
    }

    private void switchMatchesInImports() {
        includeImports = !includeImports;
        setInput(null);
        getDialogSettings()
            .put("includeImports", includeImports);
    }
    
    private void createImportsButton(ToolBar toolBar) {
        importsButton = new ToolItem(toolBar, SWT.CHECK);
        importsButton.setImage(CeylonResources.IMPORT);
        importsButton.setToolTipText("Show Matches in Import Statements");
        importsButton.setSelection(includeImports);
        importsButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                switchMatchesInImports();
                if (importsAction!=null) {
                    importsAction.setChecked(importsButton.getSelection());
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private void createModeButtons(ToolBar toolBar) {
        refsButton = new ToolItem(toolBar, SWT.CHECK);
        refsButton.setImage(REFS_IMAGE);
        refsButton.setToolTipText("Show References");
        subsButton = new ToolItem(toolBar, SWT.CHECK);
        subsButton.setImage(DECS_IMAGE);
        subsButton.setToolTipText("Show Refinements/Subtypes");
        updateButtonSelection();
        refsButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (refsButton.getSelection()) {
                    showingRefinements = false;
                    setInput(null);
                    subsButton.setSelection(false);
                }
                else {
                    refsButton.setSelection(true);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        subsButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (subsButton.getSelection()) {
                    showingRefinements = true;
                    setInput(null);
                    refsButton.setSelection(false);
                }
                else {
                    subsButton.setSelection(true);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }
    
    private void updateButtonSelection() {
        subsButton.setSelection(showingRefinements);
        refsButton.setSelection(!showingRefinements);
    }
    
    private void createLayoutButtons(ToolBar toolBar) {
        flatLayoutButton = new ToolItem(toolBar, SWT.CHECK);
        flatLayoutButton.setImage(imageRegistry.get(FLAT_MODE));
        flatLayoutButton.setToolTipText("Show as List");
        flatLayoutButton.setSelection(!treeLayout);
        treeLayoutButton = new ToolItem(toolBar, SWT.CHECK);
        treeLayoutButton.setImage(imageRegistry.get(TREE_MODE));
        treeLayoutButton.setToolTipText("Show as Tree");
        treeLayoutButton.setSelection(treeLayout);
        flatLayoutButton.addSelectionListener(
                new ChangeLayoutListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (flatLayoutButton.getSelection()) {
                    super.widgetSelected(e);
                    treeLayoutButton.setSelection(false);
                    if (treeLayoutAction!=null) {
                        treeLayoutAction.setChecked(false);
                    }
                    if (flatLayoutAction!=null) {
                        flatLayoutAction.setChecked(true);
                    }
                }
                else {
                    treeLayoutButton.setSelection(true);
                    if (flatLayoutAction!=null) {
                        flatLayoutAction.setChecked(false);
                    }
                    if (treeLayoutAction!=null) {
                        treeLayoutAction.setChecked(true);
                    }
                }
            }
        });
        treeLayoutButton.addSelectionListener(new ChangeLayoutListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (treeLayoutButton.getSelection()) {
                    super.widgetSelected(e);
                    flatLayoutButton.setSelection(false);
                    if (flatLayoutAction!=null) {
                        flatLayoutAction.setChecked(false);
                    }
                    if (treeLayoutAction!=null) {
                        treeLayoutAction.setChecked(true);
                    }
                }
                else {
                    flatLayoutButton.setSelection(true);
                    if (treeLayoutAction!=null) {
                        treeLayoutAction.setChecked(false);
                    }
                    if (flatLayoutAction!=null) {
                        flatLayoutAction.setChecked(true);
                    }
                }
            }
        });
    }
    
    protected Text createFilterText(Composite parent) {
        filterText = new Text(parent, 
                SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
        filterText.setMessage("type filter text");
        Dialog.applyDialogFont(filterText);

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.CENTER;
        filterText.setLayoutData(data);

        filterText.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                triggerCommand(e);
                if (e.keyCode == 0x0D || e.keyCode == SWT.KEYPAD_CR) // Enter key
                    gotoSelectedElement();
                if (e.keyCode == SWT.ARROW_DOWN)
                    viewer.getControl().setFocus();
                if (e.keyCode == SWT.ARROW_UP)
                    viewer.getControl().setFocus();
                if (e.character == 0x1B) // ESC
                    dispose();
            }
            public void keyReleased(KeyEvent e) {
                // do nothing
            }
        });
        
        return filterText;
    }

    private void triggerCommand(KeyEvent e) {
        if (triggersBinding(e, commandBinding)) {
            showingRefinements = !showingRefinements;
            setInput(null);
            e.doit=false;
        }
        else if (triggersBinding(e, findCommandBinding)) {
            showingRefinements = !showingRefinements;
            new FindReferencesAction(editor).run();
            e.doit=false;
        }
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
                Text input = (Text) e.widget;
                String text = input.getText();
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
            viewer.getControl()
                .setRedraw(false);
            viewer.refresh();
            viewer.getControl()
                .setRedraw(true);
            selectFirst();
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
        return getShell() == 
                getShell()
                    .getDisplay()
                    .getActiveShell();
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
    
    private boolean type;

    private TreeViewer treeViewer;
    private TableViewer tableViewer;
    private Label icon;
    private ToolItem refsButton;
    private ToolItem subsButton;
    private ToolItem flatLayoutButton;
    private ToolItem treeLayoutButton;
    private LayoutAction flatLayoutAction;
    private LayoutAction treeLayoutAction;
    private ToolItem importsButton;
    private Action importsAction;
    
    private HashMap<CeylonElement,Integer> matchCounts = 
            new HashMap<CeylonElement,Integer>();
    
    public void show(boolean refinements) {
        showingRefinements = refinements;
        setInput(null);
        open();
    }
    
    @Override
    public void setInput(Object input) {
        CeylonParseController pc = 
                editor.getParseController();
        Referenceable declaration = 
                getReferencedExplicitDeclaration(
                        editor.getSelectedNode(), 
                        pc.getLastCompilationUnit());
        if (declaration==null) {
            return;
        }
        matchCounts.clear();
        type = declaration instanceof TypeDeclaration;
        String message;
        if (showingRefinements) {
            if (type) {
                message = "subtypes of";
            }
            else {
                message = "refinements of";
            }
        } else {
            message = "references to";
        }
        String name;
        Unit unit = pc.getLastCompilationUnit().getUnit();
        if (declaration instanceof Declaration) {
            Declaration dec = (Declaration) declaration;
            name = dec.getName(unit);
            if (dec.isClassOrInterfaceMember()) {
                Declaration container = 
                        (Declaration) 
                            dec.getContainer();
                name = container.getName() + '.' + name;
            }
        }
        else {
            name = declaration.getNameAsString();
        }
        setTitleText("Quick Find References \u2014 " + 
                message + " " + name + " in project source");
        TreeNode root = new TreeNode(new Object());
        Map<Package,TreeNode> packageNodes = 
                new HashMap<Package,TreeNode>();
        Map<Module,TreeNode> moduleNodes = 
                new HashMap<Module,TreeNode>();
        List<TreeNode> allMatchesList = 
                new ArrayList<TreeNode>();
        List<TreeNode> allUnitsList = 
                new ArrayList<TreeNode>();
        List<PhasedUnit> phasedUnits = 
                pc.getTypeChecker()
                    .getPhasedUnits()
                    .getPhasedUnits();
        for (PhasedUnit pu: phasedUnits) {
            Tree.CompilationUnit cu = 
                    pu.getCompilationUnit();
            if (pu.getUnit().equals(unit) && 
                    editor.isDirty()) {
                //search in the current dirty editor
                cu = pc.getLastCompilationUnit();
            }
            Unit u = cu.getUnit();
            TreeNode unitNode = new TreeNode(u);
            List<TreeNode> unitList = 
                    new ArrayList<TreeNode>();
            Set<Node> nodes;
            if (showingRefinements) {
                if (declaration instanceof Declaration) {
                    if (type) {
                        TypeDeclaration td = 
                                (TypeDeclaration) 
                                    declaration;
                        FindSubtypesVisitor frv = 
                                new FindSubtypesVisitor(td);
                        frv.visit(cu);
                        @SuppressWarnings("unchecked")
                        Set<Node> dns = frv.getDeclarationNodeSet();
                        nodes = new HashSet<Node>(dns);
                    }
                    else {
                        Declaration d = 
                                (Declaration) 
                                    declaration;
                        FindRefinementsVisitor frv = 
                                new FindRefinementsVisitor(d);
                        frv.visit(cu);
                        Set<Tree.StatementOrArgument> dns = 
                                frv.getDeclarationNodeSet();
                        nodes = new HashSet<Node>(dns);
                    }
                }
                else {
                    nodes = emptySet();
                }
            }
            else {
                FindReferencesVisitor frv = 
                        new FindReferencesVisitor(declaration);
                frv.visit(cu);
                nodes = frv.getReferenceNodeSet();
            }
            for (Node node: nodes) {
                CeylonSearchMatch match = 
                        CeylonSearchMatch.create(node, cu, 
                                pu.getUnitFile());
                if (includeImports || !match.isInImport()) {
                    CeylonElement element = match.getElement();
                    Integer count = matchCounts.get(element);
                    if (count==null) {
                        count = 1;
                        TreeNode matchNode = new TreeNode(match);
                        matchNode.setParent(unitNode);
                        allMatchesList.add(matchNode);
                        unitList.add(matchNode);
                    }
                    else {
                        count += 1;
                    }
                    matchCounts.put(element, count);
                }
            }
            if (!unitList.isEmpty()) {
                allUnitsList.add(unitNode);
                TreeNode[] array = 
                        unitList.toArray(new TreeNode[0]);
                unitNode.setChildren(array);
                Package p = u.getPackage();
                TreeNode packageNode = packageNodes.get(p);
                if (packageNode==null) {
                    packageNode = new TreeNode(p);
                    TreeNode moduleNode = 
                            moduleNodes.get(p.getModule());
                    if (moduleNode==null) {
                        moduleNode = new TreeNode(p.getModule());
                        moduleNode.setParent(root);
                        moduleNodes.put(p.getModule(), moduleNode);
                        moduleNode.setChildren(new TreeNode[] {packageNode});
                    }
                    else {
                        TreeNode[] oldChildren = 
                                moduleNode.getChildren();
                        TreeNode[] children = 
                                new TreeNode[oldChildren.length+1];
                        for (int i=0; i<oldChildren.length; i++) {
                            children[i] = oldChildren[i];
                        }
                        children[oldChildren.length] = packageNode;
                        moduleNode.setChildren(children);
                    }
                    packageNode.setParent(moduleNode);
                    packageNodes.put(p, packageNode);
                    packageNode.setChildren(new TreeNode[] {unitNode});
                }
                else {
                    TreeNode[] oldChildren = 
                            packageNode.getChildren();
                    TreeNode[] children = 
                            new TreeNode[oldChildren.length+1];
                    for (int i=0; i<oldChildren.length; i++) {
                        children[i] = oldChildren[i];
                    }
                    children[oldChildren.length] = unitNode;
                    packageNode.setChildren(children);
                }
                unitNode.setParent(packageNode);
            }
        }
        root.setChildren(moduleNodes.values().toArray(new TreeNode[0]));
//        root.setChildren(allUnitsList.toArray(new TreeNode[0]));
        treeViewer.setInput(root.getChildren());
        tableViewer.setInput(allMatchesList);
        selectFirst();
        setStatusText();
        updateButtonSelection();
        setIcon();
    }

    private void selectFirst() {
        Object firstElem;
        if (viewer instanceof TableViewer) {
            TableViewer tv = (TableViewer) viewer;
            firstElem = tv.getElementAt(0);
        }
        else {
            TreeViewer tv = (TreeViewer) viewer;
            org.eclipse.swt.widgets.Tree tree = tv.getTree();
            if (tree.getItemCount()>0) {
                firstElem = tree.getItem(0).getData();
            }
            else {
                firstElem = null;
            }
            
        }
        if (firstElem!=null) {
            StructuredSelection selection = 
                    new StructuredSelection(firstElem);
            viewer.setSelection(selection, true);
        }
    }

    @Override
    public boolean restoresLocation() {
        return getPersistLocation();
    }
    
    @Override
    public boolean restoresSize() {
        return getPersistSize();
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
        String section = 
                CeylonPlugin.PLUGIN_ID 
                    + ".FindReferences";
        IDialogSettings dialogSettings = 
                CeylonPlugin.getInstance()
                    .getDialogSettings();
        IDialogSettings settings = 
                dialogSettings.getSection(section);
        if (settings == null)
            settings = dialogSettings.addNewSection(section);
        return settings;
    }
    
    class LayoutAction extends Action {
        LayoutAction(String name, String image) {
            super(name, AS_CHECK_BOX);
            setImageDescriptor(imageRegistry.getDescriptor(image));
        }
        @Override
        public void run() {
            switchLayout();
        }
    }
    
    @Override
    protected void fillDialogMenu(IMenuManager dialogMenu) {
        flatLayoutAction = 
                new LayoutAction("Show as List", FLAT_MODE) {
            @Override
            public void run() {
                super.run();
                if (isChecked()) {
                    treeLayoutAction.setChecked(false);
                    treeLayoutButton.setSelection(false);
                    flatLayoutButton.setSelection(true);
                }
                else {
                    treeLayoutAction.setChecked(true);
                    treeLayoutButton.setSelection(true);
                    flatLayoutButton.setSelection(false);
                }
            }
        };
        flatLayoutAction.setChecked(!treeLayout);
        treeLayoutAction = 
                new LayoutAction("Show as Tree", TREE_MODE) {
            @Override
            public void run() {
                super.run();
                if (isChecked()) {
                    flatLayoutAction.setChecked(false);
                    flatLayoutButton.setSelection(false);
                    treeLayoutButton.setSelection(true);
                }
                else {
                    flatLayoutAction.setChecked(true);
                    flatLayoutButton.setSelection(true);
                    treeLayoutButton.setSelection(false);
                }
            }
        };
        treeLayoutAction.setChecked(treeLayout);
        dialogMenu.add(flatLayoutAction);
        dialogMenu.add(treeLayoutAction);
        dialogMenu.add(new Separator());
        importsAction = 
                new Action("Show Matches in Imports", 
                        AS_CHECK_BOX) {
            {
                setImageDescriptor(imageRegistry.getDescriptor(CEYLON_IMPORT));
            }
            @Override
            public void run() {
                switchMatchesInImports();
                importsButton.setSelection(isChecked());
            }
        };
        importsAction.setChecked(includeImports);
        dialogMenu.add(importsAction);
        dialogMenu.add(new Separator());
        final IPreferenceStore prefs = CeylonPlugin.getPreferences();
        Action showLocAction = 
                new Action("Show Full Paths", AS_CHECK_BOX) {
            @Override
            public void run() {
                prefs.setValue(FULL_LOC_SEARCH_RESULTS, 
                        isChecked());
                tableViewer.refresh();
            }
        };
        showLocAction.setChecked(prefs.getBoolean(
                FULL_LOC_SEARCH_RESULTS));
        dialogMenu.add(showLocAction);
        dialogMenu.add(new Separator());
        super.fillDialogMenu(dialogMenu);
    }

    private void switchLayout() {
        treeLayout = !treeLayout;
        viewer = treeLayout ? treeViewer : tableViewer;
        treeViewer.getTree().setVisible(treeLayout);
        tableViewer.getTable().setVisible(!treeLayout);
        ((GridData)treeViewer.getControl().getLayoutData()).exclude=!treeLayout;
        ((GridData)tableViewer.getControl().getLayoutData()).exclude=treeLayout;
        viewer.getControl().getParent().layout(/*true*/);
        setInput(null);
        getDialogSettings().put("treeLayout", treeLayout);
    }

}