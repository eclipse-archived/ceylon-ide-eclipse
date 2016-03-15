package com.redhat.ceylon.eclipse.code.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.*;

import javax.swing.JOptionPane;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.versionsAvailableForBoostrap_;
import com.redhat.ceylon.ide.common.util.toJavaStringList_;

import ceylon.interop.java.toJavaStringArray_;

public class CreateBootstrapFilesHandler implements IWorkbenchWindowActionDelegate {
    
    private IProject fProject;
    
    public void dispose() {}
    
    public void init(IWorkbenchWindow window) {}

    public static boolean createBootstrapFiles(CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject, String chosenVersion, Shell shell) {
        if (shell == null) {
            shell = Display.getDefault().getActiveShell();
        }
        
        boolean success = false;
        boolean force = false;
        do {
            Object result = ceylonProject.createBootstrapFiles(CeylonPlugin.getEmbeddedCeylonRepository().getParentFile(), chosenVersion, force);
            
            if (result instanceof ceylon.language.Boolean) {
                success = ((ceylon.language.Boolean) result).booleanValue();
                if (!success) {
                    if (shell == null) {
                        CeylonPlugin.log(Status.WARNING, "The Ceylon bootstrap files already exist.");
                        break;
                    }
                    MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    mb.setText("Ceylon bootstrap files creation");
                    mb.setMessage("The Ceylon bootstrap files already exist. Would you like to overwrite them?");
                    if (mb.open() == SWT.NO) {
                        break;
                    }
                    force = true;
                }
            } else if (result instanceof ceylon.language.String ) {
                if (shell == null) {
                    CeylonPlugin.log(Status.ERROR, "An error occured during the creation of the Ceylon bootstrap files:\n"
                            + result.toString());
                    break;
                }
                MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
                mb.setText("Ceylon bootstrap files creation");
                mb.setMessage("An error occured during the creation of the Ceylon bootstrap files:\n\n"
                        + "    " + result.toString() + "\n\n"
                        + "Would you like to retry ?");
                if (mb.open() == SWT.NO) {
                    break;
                }
            } else {
                break;
            }
        } while (!success);
        return success;
    }

    private class ComboDialog extends Dialog {
    	Combo createBootstrapFilesVersionsCombo = null;
		String bootstrapVersion;
    	
		public ComboDialog(Shell parentShell) {
			super(parentShell);
		}
    	
	    @Override
	    public void create() {
	        super.create();
	        getShell().setText("Ceylon bootstrap files creation");
	        setBlockOnOpen(true);
	    }

	    @Override
	    protected Control createDialogArea(Composite parent) {
	        Composite area = (Composite) super.createDialogArea(parent);

	        Composite bootstrapComposite = new Composite(area, SWT.NONE);
	        GridData bcgdb = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	        bcgdb.grabExcessHorizontalSpace=true;
	        bootstrapComposite.setLayoutData(bcgdb);
	        GridLayout bclayoutb = new GridLayout();
	        bclayoutb.numColumns = 2;
	        bclayoutb.marginLeft = 0;
	        bootstrapComposite.setLayout(bclayoutb);

	        final Label createBootstrapFilesLabel = new Label(bootstrapComposite, SWT.NONE);
	        createBootstrapFilesLabel.setText("Select the version of the bootstrapped distribution: ");
	        createBootstrapFilesVersionsCombo = new Combo(bootstrapComposite, SWT.READ_ONLY);
	        String[] choices = toJavaStringList_.toJavaStringList(versionsAvailableForBoostrap_.get_()).toArray(new String[0]);
	        createBootstrapFilesVersionsCombo.setItems(choices);
	        createBootstrapFilesVersionsCombo.select(0);
	        bootstrapVersion = choices[0];
	        createBootstrapFilesVersionsCombo.addListener(SWT.Selection, new Listener() {
	            public void handleEvent(Event e) {
	                bootstrapVersion = createBootstrapFilesVersionsCombo.getItem(
	                                    createBootstrapFilesVersionsCombo.getSelectionIndex());
	            }
	        });

	        return area;
	    }
	    
	    public String getChosenVersion() {
	    	return bootstrapVersion;
	    }
    }
    
    public void run(IAction action) {
        CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject = modelJ2C().ceylonModel().getProject(fProject);
        if (ceylonProject != null) {
            Shell shell = Display.getDefault().getActiveShell();
            ComboDialog comboDialog = new ComboDialog(shell);
            if (comboDialog.open() == Window.OK) {
            	String chosenVersion = comboDialog.getChosenVersion();
                boolean success = createBootstrapFiles(ceylonProject, chosenVersion, shell);
                if (success) {
                    if (shell == null) {
                        CeylonPlugin.log(Status.WARNING, "The Ceylon boostrap files already exist.");
                    } else {
                        MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mb.setText("Ceylon bootstrap files creation");
                        mb.setMessage("The Ceylon boostrap files have been successfuly created.");
                        mb.open();
                    }
                }
            }
        }
    }
    
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object first = ss.getFirstElement();
            
            if (first instanceof IProject) {
                fProject = (IProject) first;
            } else if (first instanceof IJavaProject) {
                fProject = ((IJavaProject) first).getProject();
            }
        }
    }
}
