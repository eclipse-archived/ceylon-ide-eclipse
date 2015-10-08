package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import com.redhat.ceylon.ide.common.util.Escaping;

public class ExtractValueInputPage extends UserInputWizardPage {
    public ExtractValueInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Label label = new Label(result, SWT.RIGHT);  
        label.setText("Value name: ");
        final Text text = new Text(result, SWT.SINGLE|SWT.BORDER);
        text.setText(getExtractValueRefactoring().getNewName());
        text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                String name = text.getText();
                validateIdentifier(name);
                getExtractValueRefactoring().setNewName(name);
            }
        });
        final Button et = new Button(result, SWT.CHECK);
        et.setText("Use explicit type declaration");
        et.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getExtractValueRefactoring().setExplicitType(true);
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        final Button gs = new Button(result, SWT.CHECK);
        gs.setText("Create a getter");
        gs.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getExtractValueRefactoring().setGetter(true);
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        text.addKeyListener(new SubwordIterator(text));
        text.selectAll();
        text.setFocus();
    }

    @SuppressWarnings("unchecked")
    private ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion> getExtractValueRefactoring() {
        return (ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion>) getRefactoring();
    }

    void validateIdentifier(String name) {
        if (!name.matches("^[a-z_]\\w*$")) {
            setErrorMessage("Not a legal Ceylon identifier");
            setPageComplete(false);
        }
        else if (Escaping.KEYWORDS.contains(name)) {
            setErrorMessage("'" + name + "' is a Ceylon keyword");
            setPageComplete(false);
        }
        else {
            setErrorMessage(null);
            setPageComplete(true);
        }
    }

}
