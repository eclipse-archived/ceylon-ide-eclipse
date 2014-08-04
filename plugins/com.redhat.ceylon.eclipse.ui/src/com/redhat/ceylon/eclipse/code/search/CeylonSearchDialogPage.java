package com.redhat.ceylon.eclipse.code.search;

import static org.eclipse.search.ui.ISearchPageContainer.SELECTED_PROJECTS_SCOPE;
import static org.eclipse.search.ui.ISearchPageContainer.SELECTION_SCOPE;
import static org.eclipse.search.ui.ISearchPageContainer.WORKING_SET_SCOPE;
import static org.eclipse.search.ui.ISearchPageContainer.WORKSPACE_SCOPE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.editors.text.TextEditor;

import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonSearchDialogPage extends DialogPage 
        implements ISearchPage {

    private String searchPattern;
    private boolean caseSensitive = false;
    private boolean references = true;
    private boolean declarations = true;
    private ISearchPageContainer container;
    private static List<String> previousPatterns = new ArrayList<String>();
    
    public CeylonSearchDialogPage() {
        super("Ceylon Search");
    }
    
    @Override
    public void createControl(Composite parent) {
        initSearchPattern();

        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Label title = new Label(result, SWT.RIGHT);  
        title.setText("Search string (* = any string, ? = any character):");
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        title.setLayoutData(gd);
        final Combo text = new Combo(result, 0);
        text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
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
        final Button caseSense = new Button(result, SWT.CHECK);
        caseSense.setText("Case sensitive");
        caseSense.setSelection(caseSensitive);
        caseSense.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                caseSensitive = !caseSensitive;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        Group sub = new Group(result, SWT.SHADOW_ETCHED_IN);
        sub.setText("Search For");
        GridLayout sgl = new GridLayout();
        sgl.numColumns = 2;
        sub.setLayout(sgl);
        final Button refs = new Button(sub, SWT.CHECK);
        refs.setText("References");
        refs.setSelection(references);
        refs.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                references = !references;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });        
        final Button decs = new Button(sub, SWT.CHECK);
        decs.setText("Declarations");
        decs.setSelection(declarations);
        decs.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                declarations = !declarations;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
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
                projectNames = container.getSelectedProjectNames();
                break;
            case SELECTION_SCOPE: //the resources selected in the view with focus
                resources = new ArrayList<IResource>();
                ISelection selection = container.getSelection();
                if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
                    IStructuredSelection ss = (IStructuredSelection) selection;
                    for (@SuppressWarnings("unchecked") Iterator<Object> it = ss.iterator(); 
                            it.hasNext();) {
                        Object elem = it.next();
                        if (elem instanceof IAdaptable) {
                            IResource resource = (IResource) ((IAdaptable) elem).getAdapter(IResource.class);
                            if (resource!=null && resource.isAccessible()) {
                                resources.add(resource);
                            }
                        }
                    }
                }
                break;
            case WORKING_SET_SCOPE:
                for (IWorkingSet ws: container.getSelectedWorkingSets()) {
                    resources = new ArrayList<IResource>();
                    for (IAdaptable a: ws.getElements()) {
                        IResource resource = (IResource) a.getAdapter(IResource.class);
                        if (resource!=null && resource.isAccessible()) {
                            resources.add(resource);
                        }
                    }
                }
                break;
            default:
                MessageDialog.openError(getShell(), "Ceylon Search Error", 
                        "Unsupported scope");
                return false;
        }
        
        NewSearchUI.runQueryInBackground(new CeylonSearchQuery(searchPattern, 
                projectNames, resources==null ? null : resources.toArray(new IResource[0]), 
                references, declarations, caseSensitive, false));
        return true;
    }

    private void initSearchPattern() {
        searchPattern = "";
        IEditorPart currentEditor = EditorUtil.getCurrentEditor();
        if (currentEditor instanceof TextEditor) {
            String s = EditorUtil.getSelectionText((TextEditor) currentEditor);
            if (s!=null) {
                StringTokenizer tokens = new StringTokenizer(s);
                if (tokens.hasMoreTokens()) {
                    searchPattern = tokens.nextToken();
                }
            }
        }
        if ("".equals(searchPattern) && !previousPatterns.isEmpty()) {
            searchPattern = previousPatterns.get(0);
        }
    }

    public void saveSearchPattern() {
        if (previousPatterns.isEmpty() || 
                !previousPatterns.get(0).equals(searchPattern)) {
            previousPatterns.add(0, searchPattern);
            if (previousPatterns.size()>10) {
                previousPatterns.remove(10);
            }
        }
    }
    
    @Override
    public void setContainer(ISearchPageContainer container) {
        this.container = container;
    }
    
    @Override
    public void setVisible(boolean visible) {
        container.setPerformActionEnabled(true);
        super.setVisible(visible);
    }
    
}
