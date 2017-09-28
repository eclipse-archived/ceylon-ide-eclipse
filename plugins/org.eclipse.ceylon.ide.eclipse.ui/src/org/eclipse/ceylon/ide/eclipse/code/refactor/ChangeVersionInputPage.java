package org.eclipse.ceylon.ide.eclipse.code.refactor;


import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ChangeVersionInputPage extends UserInputWizardPage {
    public ChangeVersionInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Label title = new Label(result, SWT.LEFT);  
        title.setText("Change " + getRenameVersionRefactoring().getCount() + 
                " occurrences of module version \"" + 
                getRenameVersionRefactoring().getModule().getVersion() + "\"");
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        title.setLayoutData(gd);
        Label label = new Label(result, SWT.RIGHT);  
        label.setText("Change to: ");
        final Text text = new Text(result, SWT.SINGLE|SWT.BORDER);
        text.setText(getRenameVersionRefactoring().getNewVersion());
        text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                String name = text.getText();
                validateVersion(name);
                getRenameVersionRefactoring().setNewVersion(name);
            }
        });
        text.addKeyListener(new SubwordIterator(text));
        text.selectAll();
        text.setFocus();
    }
    
    private ChangeVersionRefactoring getRenameVersionRefactoring() {
        return (ChangeVersionRefactoring) getRefactoring();
    }
    
    void validateVersion(String name) {
        if (name.isEmpty()) {
            setErrorMessage("Empty version");
            setPageComplete(false);
        }
        else {
            setErrorMessage(null);
            setPageComplete(true);
        }
    }

}
