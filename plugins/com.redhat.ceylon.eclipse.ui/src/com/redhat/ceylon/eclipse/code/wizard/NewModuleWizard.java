package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.imports.ModuleImportUtil.appendImportStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class NewModuleWizard extends Wizard implements INewWizard {
    
    private final class CreateCeylonModuleOperation extends AbstractOperation {
        private IFile result;
        private List<IUndoableOperation> ops = new ArrayList<IUndoableOperation>(3);
        private Map<String, String> imports;
        
        public IFile getResult() {
            return result;
        }
        
        public CreateCeylonModuleOperation(Map<String,String> imports) {
            super("New Ceylon Module");
            this.imports = imports;
        }
        
        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            
            IPackageFragment pf = page.getPackageFragment();
            String moduleName = pf.getElementName();
            boolean preamble = page.isIncludePreamble();
            String newline = System.getProperty("line.separator");
            
            String runFunction = "\"Run the module `" + moduleName + "`.\""+newline+
                    "shared void run() {"+newline+"    "+newline+"}";
            CreateCeylonSourceFileOperation op = new CreateCeylonSourceFileOperation(page.getSourceDir(), 
                    pf, page.getUnitName(), preamble, runFunction, getShell());
            ops.add(op);
            IStatus status = op.execute(monitor, info);
            if (!status.isOK()) {
                return status;
            }
            result = op.getResult();
            
            StringBuilder moduleDescriptor = new StringBuilder("module ").append(moduleName)
                    .append(" \"").append(page.getVersion()).append("\" {");
            for (Map.Entry<String,String> entry: imports.entrySet()) {
                appendImportStatement(moduleDescriptor, entry.getKey(), entry.getValue(), newline);
            }
            if (!imports.isEmpty()) {
                moduleDescriptor.append(newline);
            }
            moduleDescriptor.append("}").append(newline);
            op = new CreateCeylonSourceFileOperation(page.getSourceDir(), pf, "module", 
                    preamble, moduleDescriptor.toString(), getShell());
            status = op.execute(monitor, info);
            ops.add(op);
            if (!status.isOK()) {
                return status;
            }
            
            String packageDescriptor = (page.isShared() ? "shared " : "") + 
                    "package " + moduleName + ";"+newline;
            op = new CreateCeylonSourceFileOperation(page.getSourceDir(), pf, "package", 
                    preamble, packageDescriptor, getShell());
            status = op.execute(monitor, info);
            ops.add(op);
            if (!status.isOK()) {
                return status;
            }
            
            return Status.OK_STATUS;
        }
        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            return execute(monitor, info);
        }
        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            for (IUndoableOperation op: ops) {
                op.undo(monitor, info);
            }
            return Status.OK_STATUS;
        }
    }

    private IStructuredSelection selection;
    private NewModuleWizardPage page;
    private ImportModulesWizardPage importsPage;
    private IWorkbench workbench;
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public boolean performFinish() {
        final CreateCeylonModuleOperation op = new CreateCeylonModuleOperation(importsPage.getImports());
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException {
                    //TODO: should we do this in a WorkspaceModifyOperation?
                    try {
                        IWorkbenchOperationSupport os = getWorkbench().getOperationSupport();
                        op.addContext(os.getUndoContext());
                        os.getOperationHistory().execute(op, monitor, 
                                getUIInfoAdapter(getShell()));
                    } 
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            });
        } 
        catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        } 
        catch (InterruptedException e) {
            return false;
        }
//        IPackageFragment pf = page.getPackageFragment();
        BasicNewResourceWizard.selectAndReveal(op.getResult(), 
                workbench.getActiveWorkbenchWindow());
        gotoLocation(op.getResult().getFullPath(), 0);
//        PreferenceDialog dialog = createPropertyDialogOn(Util.getShell(), 
//                pf, "com.redhat.ceylon.eclipse.ui.moduleProperties", 
//                new String[] { "com.redhat.ceylon.eclipse.ui.moduleProperties",
//                               "org.eclipse.ui.propertypages.info.file"}, 
//                null);
//        dialog.setBlockOnOpen(false);
//        dialog.open();
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            page = new NewModuleWizardPage();
            page.init(workbench, selection);
        }
        if (importsPage == null) {
            importsPage = new ImportModulesWizardPage(page);
        }
        addPage(page);
        addPage(importsPage);
    }
}
