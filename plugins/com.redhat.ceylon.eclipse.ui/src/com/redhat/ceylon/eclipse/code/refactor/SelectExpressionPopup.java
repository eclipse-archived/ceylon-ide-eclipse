package com.redhat.ceylon.eclipse.code.refactor;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.Nodes;

abstract class SelectExpressionPopup extends PopupDialog {
    
    private CeylonEditor editor;
    private TableViewer table;
    private List<Term> containingExpressions;
    
    abstract ExtractLinkedMode linkedMode();

    SelectExpressionPopup(Shell parent, int shellStyle, 
            CeylonEditor editor, String title) {
        super(parent, shellStyle, true, true, false, false,
                false, null, null);
        this.editor = editor;
        setTitleText(title);
        containingExpressions = containingExpressions();
    }
    
    private Point getLocation() {
        StyledText text = 
                editor.getCeylonSourceViewer()
                    .getTextWidget();
        Point selection = text.getSelection();
        Point p = text.getLocationAtOffset(selection.x);
        p.x -= getShell().getBorderWidth();
        if (p.x < 0) p.x= 0;
        if (p.y < 0) p.y= 0;
        p = new Point(p.x, p.y + 
                text.getLineHeight(selection.x));
        p = text.toDisplay(p);
        return p;
    }
    
    @Override
    public int open() {
        if (containingExpressions.size()>1) {
            int result = super.open();
            getShell().setLocation(getLocation());
            setFocus();
            return result;
        }
        else {
            linkedMode().start();
            return OK;
        }
    }
    
    private List<Tree.Term> containingExpressions() {
        Tree.CompilationUnit rootNode = 
                editor.getParseController()
                    .getRootNode();
        final IRegion selection = editor.getSelection();
        final List<Tree.Term> expressions = 
                new ArrayList<Tree.Term>();
        new Visitor() {
            @Override
            public void visit(Tree.Term that) {
                super.visit(that);
                if (!(that instanceof Tree.Expression)) {
                    if (Nodes.getNodeStartOffset(that) 
                            <= selection.getOffset() &&
                        Nodes.getNodeEndOffset(that)+1
                            >= selection.getOffset()+
                               selection.getLength()) {
                        expressions.add(that);
                    }
                }
            }
        }.visit(rootNode);
        return expressions;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.marginTop = 0;
        layout.marginLeft = 2;
        layout.marginRight = 2;
        layout.marginBottom = 2;
        parent.setLayout(layout);
        table = new TableViewer(parent, 
                SWT.NO_TRIM|SWT.SINGLE|SWT.FULL_SELECTION);
        final Table tab = table.getTable();
        tab.setFont(CeylonPlugin.getCompletionFont());
        Display display = getShell().getDisplay();
        tab.setCursor(new Cursor(display, SWT.CURSOR_HAND));
        tab.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                Tree.Term e = 
                        (Tree.Term) 
                            cell.getElement();
                StyledString result = new StyledString();
                List<CommonToken> tokens = 
                        editor.getParseController()
                            .getTokens();
                String text = 
                        Nodes.toString(e, tokens)
                            .replaceAll("\\s\\s+|\n|\r|\f", " ");
                Highlights.styleFragment(result, 
                        text, false, null, 
                        CeylonPlugin.getCompletionFont());
                cell.setText(result.toString());
                cell.setStyleRanges(result.getStyleRanges());
                super.update(cell);
            }
        });
        table.setContentProvider(ArrayContentProvider.getInstance());
        table.setInput(containingExpressions);
        tab.setSelection(0);
        tab.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 0x0D || 
                        e.keyCode == SWT.KEYPAD_CR) { // Enter key
                    select();
                }
                if (e.character == 0x1B) { // ESC
                    close();
                }
            }
        });
        tab.addListener(SWT.MouseMove, 
                new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle bounds = event.getBounds();
                Point point = new Point(bounds.x, bounds.y);
                TableItem item = 
                        tab.getItem(point);
                if (item!=null) {
                    StructuredSelection selection = 
                            new StructuredSelection(
                                    item.getData());
                    table.setSelection(selection);
                }
            }
        });
        tab.addMouseListener(new MouseListener() {
            @Override
            public void mouseUp(MouseEvent e) {
                select();
            }
            @Override
            public void mouseDown(MouseEvent e) {}
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                select();
            }
        });
        return parent;
    }
        
    public void setFocus() {
        getShell().forceFocus();
        table.getControl().setFocus();
    }

    private void select() {
        IStructuredSelection selection = 
                (IStructuredSelection) 
                    table.getSelection();
        Node e = (Node) selection.getFirstElement();
        if (e!=null) {
            editor.getSelectionProvider()
                .setSelection(new TextSelection(
                        Nodes.getNodeStartOffset(e), 
                        Nodes.getNodeLength(e)));
        }
        close();
        linkedMode().start();
    }


}
