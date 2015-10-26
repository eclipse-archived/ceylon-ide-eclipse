package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoLocation;
import static com.redhat.ceylon.eclipse.code.imports.ModuleImportUtil.appendImportStatement;
import static com.redhat.ceylon.eclipse.code.preferences.ModuleImportSelectionDialog.selectModules;
import static com.redhat.ceylon.eclipse.code.wizard.WizardUtil.runOperation;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleSearchResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleVersionNode;
import com.redhat.ceylon.eclipse.code.preferences.ModuleImportContentProvider;
import com.redhat.ceylon.eclipse.code.preferences.ModuleImportSelectionDialog;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewModuleWizard extends Wizard implements INewWizard {
    
    private final class CreateModuleOperation extends AbstractOperation {
        
        private IFile result;
        private List<IUndoableOperation> ops = 
                new ArrayList<IUndoableOperation>(3);
        private Map<String, String> imports;
        private Set<String> sharedImports;
        
        public IFile getResult() {
            return result;
        }
        
        public CreateModuleOperation(Map<String,String> imports,
                Set<String> sharedImports) {
            super("New Ceylon Module");
            this.imports = imports;
            this.sharedImports = sharedImports;
        }
        
        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            
            IPackageFragment pf = page.getPackageFragment();
            
            CreateSourceFileOperation op = runFunctionOp(pf);
            ops.add(op);
            IStatus status = op.execute(monitor, info);
            if (!status.isOK()) {
                return status;
            }
            result = op.getFile();
            
            op = moduleDescriptorOp(pf);
            status = op.execute(monitor, info);
            ops.add(op);
            if (!status.isOK()) {
                return status;
            }
            
            op = packageDescriptorOp(pf);
            status = op.execute(monitor, info);
            ops.add(op);
            if (!status.isOK()) {
                return status;
            }
            
            return Status.OK_STATUS;
        }

        private CreateSourceFileOperation packageDescriptorOp(IPackageFragment pf) {
            String moduleName = pf.getElementName();
            boolean preamble = page.isIncludePreamble();
            String newline = System.lineSeparator();
            StringBuilder packageDescriptor = 
                    new StringBuilder();
            if (page.isShared()) {
                packageDescriptor.append("shared "); 
            }
            packageDescriptor
                    .append("package ")
                    .append(moduleName)
                    .append(";")
                    .append(newline);
            return new CreateSourceFileOperation(page.getSourceDir(), 
                    pf, "package", preamble, 
                    packageDescriptor.toString());
        }

        private CreateSourceFileOperation moduleDescriptorOp(IPackageFragment pf) {
            String moduleName = pf.getElementName();
            boolean preamble = page.isIncludePreamble();
            String newline = System.lineSeparator();
            StringBuilder moduleDescriptor = 
                    new StringBuilder("module ")
                        .append(moduleName)
                        .append(" \"")
                        .append(page.getVersion())
                        .append("\"")
                        .append(" {");
            for (Map.Entry<String,String> entry: 
                    imports.entrySet()) {
                String name = entry.getKey();
                String version = entry.getValue();
                boolean shared = sharedImports.contains(name);
                appendImportStatement(moduleDescriptor, 
                        shared, null, name, version, newline);
            }
            if (!imports.isEmpty()) {
                moduleDescriptor.append(newline);
            }
            moduleDescriptor.append("}").append(newline);
            return new CreateSourceFileOperation(page.getSourceDir(), 
                    pf, "module", preamble, 
                    moduleDescriptor.toString());
        }

        private CreateSourceFileOperation runFunctionOp(IPackageFragment pf) {
            String moduleName = pf.getElementName();
            boolean preamble = page.isIncludePreamble();
            String newline = System.lineSeparator();
            String runFunction = 
                    new StringBuilder()
                        .append("\"Run the module `")
                        .append(moduleName)
                        .append("`.\"")
                        .append(newline)
                        .append("shared void run() {")
                        .append(newline)
                        .append("    ")
                        .append(newline)
                        .append("}")
                        .toString();
            
            return new CreateSourceFileOperation(page.getSourceDir(), 
                    pf, page.getUnitName(), 
                    preamble, runFunction);
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
    
    public NewModuleWizard() {
        setDialogSettings(CeylonPlugin.getInstance().getDialogSettings());
        setWindowTitle("New Ceylon Module");
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public boolean performFinish() {
        CreateModuleOperation op = 
                new CreateModuleOperation(importsPage.getImports(), 
                        importsPage.getSharedImports());
        if (runOperation(op, getContainer())) {        
            BasicNewResourceWizard.selectAndReveal(op.getResult(), 
                    workbench.getActiveWorkbenchWindow());
            gotoLocation(op.getResult().getFullPath(), 0);
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            page = new NewModuleWizardPage();
            page.init(workbench, selection);
        }
        if (importsPage == null) {
            importsPage = new ImportModulesWizardPage() {
                IProject getProject() {
                    IPackageFragmentRoot sourceDir = 
                            page.getSourceDir();
                    return sourceDir==null ? null : 
                            sourceDir.getResource().getProject();
                }
                @Override
                Map<String, ModuleVersionNode> getModules() {
                    return selectModules(new ModuleImportSelectionDialog(getShell(), 
                            new ModuleImportContentProvider(null, getProject()) {
                        @Override
                        public ModuleSearchResult getModules(String prefix) {
                            IProject project = 
                                    page.getSourceDir()
                                        .getJavaProject()
                                        .getProject();
                            return getModuleSearchResults(prefix, null,
                                    getProjectTypeChecker(project), project);
                        }
                    }), getProject());
                }
            };
        }
        addPage(page);
        addPage(importsPage);
    }
}
