package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

public class CeylonSearchDialogPage extends DialogPage 
		implements ISearchPage/*, IReplacePage*/ {

	private String searchString="";
	private boolean caseSensitive = false;
	private boolean references = true;
	private boolean declarations = true;
	private ISearchPageContainer container;
	
	public CeylonSearchDialogPage() {
		super("Ceylon Search");
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		setControl(result);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		result.setLayout(layout);
		Label title = new Label(result, SWT.RIGHT);  
		title.setText("Search string");
		GridData gd = new GridData();
		gd.horizontalSpan=2;
		title.setLayoutData(gd);
		final Combo text = new Combo(result, 0);
		text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		text.setText(searchString);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				searchString = text.getText();
			}
		});
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
		int scope = container.getSelectedScope();
		String[] projectNames;
		switch (scope) {
		    case ISearchPageContainer.WORKSPACE_SCOPE:
		        MessageDialog.openError(getShell(), "Ceylon Search Error", "Please select a project to search");
		        return false;
		    case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:
		        projectNames = container.getSelectedProjectNames();
		        break;
		    case ISearchPageContainer.SELECTION_SCOPE:
	            projectNames = new String[] { getProject(container.getActiveEditorInput()).getName() };
	            break;
	        default:
                MessageDialog.openError(getShell(), "Ceylon Search Error", "Unsupported scope");
	            return false;
		}
		
		NewSearchUI.runQueryInBackground(new CeylonSearchQuery(
				searchString, projectNames, references, declarations, 
				caseSensitive));
		return true;
	}

    public static IProject getProject(IEditorInput editor) {
        return ((IFileEditorInput) editor).getFile().getProject();
    }
	/*@Override
	public boolean performReplace() {
		// TODO Auto-generated method stub
		return true;
	}*/
	
	@Override
	public void setContainer(ISearchPageContainer container) {
		this.container = container;
	}
	
}
