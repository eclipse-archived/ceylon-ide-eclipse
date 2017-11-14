/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getSelectionText;
import static org.eclipse.search.ui.ISearchPageContainer.SELECTED_PROJECTS_SCOPE;
import static org.eclipse.search.ui.ISearchPageContainer.SELECTION_SCOPE;
import static org.eclipse.search.ui.ISearchPageContainer.WORKING_SET_SCOPE;
import static org.eclipse.search.ui.ISearchPageContainer.WORKSPACE_SCOPE;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.editors.text.TextEditor;

import org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonFiltersPreferencePage;
import org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonOutlinesPreferencePage;
import org.eclipse.ceylon.ide.eclipse.util.SearchVisitor;

public class CeylonSearchDialogPage extends DialogPage 
        implements ISearchPage {

    private static final IResource[] NO_RESOURCES = 
            new IResource[0];
    
    private String searchPattern;

    private static boolean caseSensitive = false;
    private static boolean regex = false;
    private static boolean declarations = true;
    private static boolean imports = true;
    private static boolean references = true;
    private static boolean types = true;
    private static boolean archives = true;
    private static boolean sources = true;
    private static boolean docs = true;
    private static int kinds = SearchVisitor.ANYTHING;
    private static ISearchPageContainer container;
    private static List<String> previousPatterns = 
            new ArrayList<String>();
    
    public CeylonSearchDialogPage() {
        super("Ceylon Search");
    }
    
    @Override
    public void createControl(Composite parent) {
        initSearchPattern();
        Composite control = new Composite(parent, SWT.NONE);
        control.setLayout(new GridLayout());
        control.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                true, false));
        setControl(control);
        
        Composite outer = new Composite(control, SWT.NONE);
        outer.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                true, false));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        outer.setLayout(layout);
        
        Composite result = new Composite(outer, SWT.NONE);
        result.setLayout(new GridLayout());
        GridData mgd = new GridData(
                SWT.FILL, SWT.BEGINNING, 
                true, false);
        mgd.horizontalSpan = 2;
        result.setLayoutData(mgd);
        Label title = new Label(result, SWT.RIGHT);  
        title.setText("Search string (* = any string, ? = any character):");
        final Combo text = new Combo(result, 0);
        text.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                true, false));
        text.setText(searchPattern);
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                searchPattern = text.getText();
            }
        });
        for (String string: previousPatterns) {
            text.add(string);
        }
        
        Composite options = new Composite(outer, SWT.NONE);
        options.setLayout(new GridLayout());
        options.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                false, false));
        Button regExp = new Button(options, SWT.CHECK);
        regExp.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                false, false));
        regExp.setText("Regular expression");
        regExp.setSelection(regex);
        regExp.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                regex = !regex;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        Button caseSense = new Button(options, SWT.CHECK);
        caseSense.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                false, false));
        caseSense.setText("Case sensitive");
        caseSense.setSelection(caseSensitive);
        caseSense.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                caseSensitive = !caseSensitive;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        
        
        outer = new Composite(control, SWT.NONE);
        outer.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                true, false));
        layout = new GridLayout();
        layout.numColumns = 3;
        outer.setLayout(layout);
        
        Group sub = new Group(outer, SWT.SHADOW_ETCHED_IN);
        sub.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                true, false));
        sub.setText("Search For");
        sub.setLayout(new GridLayout(2,false));
        final Button ab = new Button(sub, SWT.CHECK);
        GridData bdg = new GridData();
        bdg.horizontalSpan = 2;
        bdg.horizontalAlignment = SWT.FILL;
        bdg.grabExcessHorizontalSpace = true;
        ab.setLayoutData(bdg);
        ab.setText("Anything");
        ab.setSelection(kinds==SearchVisitor.ANYTHING);
        final Button tb = new Button(sub, SWT.RADIO);
        tb.setText("Types");
        tb.setSelection(kinds==SearchVisitor.TYPE);
        tb.setEnabled(kinds!=SearchVisitor.ANYTHING);
        final Button fb = new Button(sub, SWT.RADIO);
        fb.setText("Functions");
        fb.setSelection(kinds==SearchVisitor.FUNCTION);
        fb.setEnabled(kinds!=SearchVisitor.ANYTHING);
        final Button vb = new Button(sub, SWT.RADIO);
        vb.setText("Values");
        vb.setSelection(kinds==SearchVisitor.VALUE);
        vb.setEnabled(kinds!=SearchVisitor.ANYTHING);
        final Button pb = new Button(sub, SWT.RADIO);
        pb.setText("Packages");
        pb.setSelection(kinds==SearchVisitor.PACKAGES);
        pb.setEnabled(kinds!=SearchVisitor.ANYTHING);
        ab.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (ab.getSelection()) {
                    kinds = SearchVisitor.ANYTHING;
                    tb.setEnabled(false);
                    fb.setEnabled(false);
                    vb.setEnabled(false);
                    pb.setEnabled(false);
                    tb.setSelection(false);
                    fb.setSelection(false);
                    vb.setSelection(false);
                    pb.setSelection(false);
                }
                else {
                    kinds = SearchVisitor.TYPE;
                    tb.setEnabled(true);
                    fb.setEnabled(true);
                    vb.setEnabled(true);
                    pb.setEnabled(true);
                    tb.setSelection(true);
                    fb.setSelection(false);
                    vb.setSelection(false);
                    pb.setSelection(false);
                }
            }
        });
        tb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tb.getSelection()) {
                    kinds = SearchVisitor.TYPE;
                }
            }
        });
        fb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (fb.getSelection()) {
                    kinds = SearchVisitor.FUNCTION;
                }
            }
        });
        vb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (vb.getSelection()) {
                    kinds = SearchVisitor.VALUE;
                }
            }
        });
        pb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (pb.getSelection()) {
                    kinds = SearchVisitor.PACKAGES;
                }
            }
        });
        
        sub = new Group(outer, SWT.SHADOW_ETCHED_IN);
        sub.setLayoutData(new GridData(
                SWT.FILL, SWT.BEGINNING, 
                true, false));
        sub.setText("Limit To");
        sub.setLayout(new GridLayout(2,false));
        Button dec = new Button(sub, SWT.CHECK);
        dec.setText("Declarations");
        dec.setSelection(declarations);
        dec.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                declarations = !declarations;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        Button ref = new Button(sub, SWT.CHECK);
        ref.setText("Expressions");
        ref.setSelection(references);
        ref.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                references = !references;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        Button type = new Button(sub, SWT.CHECK);
        type.setText("Types");
        type.setSelection(types);
        type.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                types = !types;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        Button imp = new Button(sub, SWT.CHECK);
        imp.setText("Imports");
        imp.setSelection(imports);
        imp.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                imports = !imports;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        Button doc = new Button(sub, SWT.CHECK);
        GridData gd = bdg;
        gd.horizontalSpan = 2;
        doc.setLayoutData(gd);
        doc.setText("Documentation strings");
        doc.setSelection(docs);
        doc.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                docs = !docs;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        
        
        sub = new Group(outer, SWT.SHADOW_ETCHED_IN);
        sub.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, 
                true, false));
        sub.setText("Search In");
        sub.setLayout(new GridLayout());
        Button inSources = new Button(sub, SWT.CHECK);
        inSources.setText("Project sources");
        inSources.setSelection(sources);
        inSources.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                sources = !sources;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        Button inArchives = new Button(sub, SWT.CHECK);
        inArchives.setText("Source archives");
        inArchives.setSelection(archives);
        inArchives.addSelectionListener(
                new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                archives = !archives;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        Link link = new Link(sub, 0);
        link.setText("<a>Configure filters...</a>");
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferenceDialog preferenceDialog = 
                        createPreferenceDialogOn(getShell(), 
                        CeylonFiltersPreferencePage.ID, 
                        new String[] {
                                CeylonFiltersPreferencePage.ID,
                                CeylonOutlinesPreferencePage.ID
                        }, null);
                preferenceDialog.setBlockOnOpen(false);
                preferenceDialog.open();
            }
        });
    }

    @Override
    public boolean performAction() {
        saveSearchPattern();
        
        String[] projectNames = null;
        List<IResource> resources = null;
        switch (container.getSelectedScope()) {
            case WORKSPACE_SCOPE:
                break;
            case SELECTED_PROJECTS_SCOPE: //called "Enclosing Projects" in the UI
                projectNames = 
                    container.getSelectedProjectNames();
                break;
            case SELECTION_SCOPE: //the resources selected in the view with focus
                resources = new ArrayList<IResource>();
                ISelection selection = 
                        container.getSelection();
                if (selection 
                        instanceof IStructuredSelection && 
                        !selection.isEmpty()) {
                    IStructuredSelection ss = 
                            (IStructuredSelection) 
                                selection;
                    for (@SuppressWarnings("unchecked") 
                        Iterator<Object> it = ss.iterator(); 
                            it.hasNext();) {
                        Object elem = it.next();
                        if (elem instanceof IAdaptable) {
                            IAdaptable a = 
                                    (IAdaptable) elem;
                            IResource resource = 
                                    (IResource)
                                    a.getAdapter(IResource.class);
                            if (resource!=null && 
                                    resource.isAccessible()) {
                                resources.add(resource);
                            }
                        }
                    }
                }
                break;
            case WORKING_SET_SCOPE:
                for (IWorkingSet ws: 
                        container.getSelectedWorkingSets()) {
                    resources = new ArrayList<IResource>();
                    for (IAdaptable a: ws.getElements()) {
                        IResource resource = 
                                (IResource) 
                                a.getAdapter(IResource.class);
                        if (resource!=null && 
                                resource.isAccessible()) {
                            resources.add(resource);
                        }
                    }
                }
                break;
            default:
                MessageDialog.openError(getShell(), 
                        "Ceylon Search Error", 
                        "Unsupported scope");
                return false;
        }
        
        CeylonSearchQuery query = 
                new CeylonSearchQuery(
                        searchPattern, 
                        projectNames, 
                        resources==null ? null : 
                            resources.toArray(NO_RESOURCES), 
                            references, types, 
                            declarations, imports, docs,
                            caseSensitive, regex, 
                            sources, archives,
                            kinds);
        NewSearchUI.runQueryInBackground(query);
        return true;
    }

    private void initSearchPattern() {
        searchPattern = "";
        IEditorPart currentEditor = getCurrentEditor();
        if (currentEditor instanceof TextEditor) {
            TextEditor textEditor = 
                    (TextEditor) 
                        currentEditor;
            String text = getSelectionText(textEditor);
            if (text!=null) {
                StringTokenizer tokens = 
                        new StringTokenizer(text);
                if (tokens.hasMoreTokens()) {
                    searchPattern = tokens.nextToken();
                }
            }
        }
        if ("".equals(searchPattern) && 
                !previousPatterns.isEmpty()) {
            searchPattern = previousPatterns.get(0);
        }
    }

    public void saveSearchPattern() {
        if (previousPatterns.isEmpty() || 
                !previousPatterns.get(0)
                    .equals(searchPattern)) {
            previousPatterns.add(0, searchPattern);
            if (previousPatterns.size()>10) {
                previousPatterns.remove(10);
            }
        }
    }
    
    @Override
    public void setContainer(ISearchPageContainer searchContainer) {
        container = searchContainer;
    }
    
    @Override
    public void setVisible(boolean visible) {
        container.setPerformActionEnabled(true);
        super.setVisible(visible);
    }
    
}
