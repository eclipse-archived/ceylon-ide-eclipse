/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

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

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;

abstract class SelectExpressionPopup extends PopupDialog {
    
    private CeylonEditor editor;
    private TableViewer table;
    private List<Tree.Term> containingExpressions;
    
    abstract void finish();

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
            finish();
            return OK;
        }
    }
    
    private List<Tree.Term> containingExpressions() {
        final List<Tree.Term> expressions = 
                new ArrayList<Tree.Term>();
        Tree.CompilationUnit rootNode = 
                editor.getParseController()
                    .getLastCompilationUnit();
        if (rootNode!=null) {
            new Visitor() {
                IRegion selection = editor.getSelection();
                private void option(Tree.Term that) {
                    if (that.getStartIndex() 
                            <= selection.getOffset() &&
                        that.getEndIndex()
                            >= selection.getOffset() +
                                selection.getLength()) {
                        expressions.add(that);
                    }
                }
                @Override
                public void visit(Tree.Expression that) {
                    //don't propose parenthesized expressions
                    if (that.getTerm()!=null) {
                        that.getTerm().visit(this);
                    }
                }
                @Override
                public void visit(Tree.AssignmentOp that) {
                    //don't visit LHS
                    if (that.getRightTerm()!=null) {
                        that.getRightTerm().visit(this);
                    }
                }
                @Override
                public void visit(Tree.SpecifierStatement that) {
                    //don't visit LHS
                    if (that.getSpecifierExpression()!=null) {
                        that.getSpecifierExpression().visit(this);
                    }
                }
                @Override
                public void visit(Tree.Term that) {
                    super.visit(that);
                    option(that);
                }
                @Override
                public void visit(Tree.StringTemplate that) {
                    //don't visit the string fragments
                    for (Tree.Expression e: that.getExpressions()) {
                        e.visit(this);
                    }
                    option(that);
                }
            }.visit(rootNode);
        }
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
                        Nodes.text(e, tokens)
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
                TableItem item = tab.getItem(point);
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
                        e.getStartIndex(), 
                        e.getDistance()));
        }
        close();
        finish();
    }


}
