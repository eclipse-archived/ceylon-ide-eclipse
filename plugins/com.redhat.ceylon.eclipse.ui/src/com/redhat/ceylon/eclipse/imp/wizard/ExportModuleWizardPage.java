package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_EXPORT_CAR;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class ExportModuleWizardPage extends WizardPage implements IWizardPage {

    //private IStructuredSelection selection;
    private String repositoryPath;
    
    ExportModuleWizardPage(String defaultRepositoryPath) {
        super("Export Ceylon Module", "Export Ceylon Module", CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(CEYLON_EXPORT_CAR));
        setDescription("Export a Ceylon module to a module repository.");
        repositoryPath = defaultRepositoryPath;
        setPageComplete(isComplete());
    }

    /*public void init(IStructuredSelection selection) {
        this.selection = selection;
    }*/
    
    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        Composite composite= new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);
        
        //TODO: let you select a module descriptor to
        //      export just that module!
        
		addSelectRepo(composite);
        
        setControl(composite);

        Dialog.applyDialogFont(composite);
    }

	void addSelectRepo(Composite composite) {
		Label folderLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        folderLabel.setText("Target module repository: ");
        GridData flgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        flgd.horizontalSpan = 1;
        folderLabel.setLayoutData(flgd);

        final Text folder = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData fgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 2;
        fgd.grabExcessHorizontalSpace = true;
        folder.setLayoutData(fgd);
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                repositoryPath = folder.getText();
        		setPageComplete(isComplete());
            }
        });
        
        folder.setText(repositoryPath);
        
        Button selectFolder = new Button(composite, SWT.PUSH);
        selectFolder.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectFolder.setLayoutData(sfgd);
        selectFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	String dir = new DirectoryDialog(getShell()).open();
            	if (dir!=null) {
            		repositoryPath = dir;
            		folder.setText(repositoryPath);
            	}
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
	
	}
	
	private boolean isComplete() {
		return repositoryPath!=null &&
				!repositoryPath.isEmpty() &&
				new File(repositoryPath).exists();
	}
	
	public String getRepositoryPath() {
		return repositoryPath;
	}
    
}
