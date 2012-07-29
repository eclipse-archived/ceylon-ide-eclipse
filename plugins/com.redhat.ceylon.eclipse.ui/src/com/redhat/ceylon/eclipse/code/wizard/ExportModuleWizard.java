package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getRepositoryPaths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class ExportModuleWizard extends Wizard implements IExportWizard {

    private IStructuredSelection selection;
    private ExportModuleWizardPage page;
    
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
	}

    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            IJavaElement selectedElement = getSelectedElement();
            String repoPath=null;
            IJavaProject project=null;
            if (selectedElement!=null) {
                project = selectedElement.getJavaProject();
                String[] paths = getRepositoryPaths(project.getProject());
				repoPath = paths==null || paths.length==0 ? null : paths[paths.length-1];
            }
			if (repoPath==null) repoPath = getDefaultRepositoryPath();
            page = new ExportModuleWizardPage(repoPath, project);
            //page.init(selection);
        }
        addPage(page);
    }

	public static String getDefaultRepositoryPath() {
		String repositoryPath = CeylonPlugin.getInstance().getDialogSettings()
        		.get("repositoryPath");
        if (repositoryPath==null) {
        	repositoryPath = System.getProperty("user.home") + "/.ceylon/repo";
        }
        return repositoryPath;
	}

	//TODO: fix copy/paste from NewUnitWizardPage
    private IJavaElement getSelectedElement() {
        if (selection!=null && selection.size()==1) {
            Object element = selection.getFirstElement();
            if (element instanceof IFile) {
            	return JavaCore.create(((IFile) element).getParent());
            }
            else {
			    return (IJavaElement) ((IAdaptable) element)
                        .getAdapter(IJavaElement.class);
            }
        }
        else {
            return null;
        }
    }
    
    private Exception ex;
    
	@Override
	public boolean performFinish() {
		final String repositoryPath = page.getRepositoryPath();
		final IJavaProject project = page.getProject();
		if (project==null) {
			MessageDialog.openError(getShell(), "Export Module Error", 
					"No Java project selected.");
			return false;
		}
		else {
			/*IProject project = javaProject.getProject();
			List<PhasedUnit> list = CeylonBuilder.getProjectTypeChecker(project)
				.getPhasedUnits().getPhasedUnits();
			Set<String> moduleNames = new HashSet<String>();
			for (PhasedUnit phasedUnit: list) {
				Module module = phasedUnit.getUnit().getPackage().getModule();
				moduleNames.add(module.getNameAsString());
			}*/
			ex = null;
			TableItem[] selectedItems = page.getModules().getSelection();
			final String[] selectedModules = new String[selectedItems.length];
			for (int i=0; i<selectedItems.length; i++) {
				selectedModules[i] = selectedItems[i].getText();
			}
			try {
				Job job = new Job("Exporting modules") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						File outputDir = CeylonBuilder.getCeylonModulesOutputDirectory(project.getProject());
						Path outputPath = Paths.get(outputDir.getAbsolutePath());
						Path repoPath = Paths.get(repositoryPath);
						if (!Files.exists(repoPath)) {
							MessageDialog.openError(getShell(), "Export Module Error", 
									"No repository at location: " + repositoryPath);
							return Status.CANCEL_STATUS;
						}
						for (String moduleNameVersion: selectedModules) {
							String moduleGlob = moduleNameVersion.replace('/', '-') + ".*";
							Path repoOutputPath = outputPath.resolve(moduleNameVersion);
							Path repoModulePath = repoPath.resolve(moduleNameVersion);
							try {
								Files.createDirectories(repoModulePath);
								DirectoryStream<Path> ds = Files.newDirectoryStream(repoOutputPath, moduleGlob);
								try {
									for (Path p: ds) {
										Files.copy(p, repoModulePath.resolve(p.getFileName()), REPLACE_EXISTING);
									}
								}
								finally {
									ds.close();
								}
							}
							catch (Exception e) {
								ex = e;
								return Status.CANCEL_STATUS;
							}
						}
						return Status.OK_STATUS;
					}
				};
				PlatformUI.getWorkbench().getProgressService().showInDialog(getShell(), job);
				job.setUser(true);
				job.schedule();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			if (ex!=null) {
				ex.printStackTrace();
				MessageDialog.openError(getShell(), "Export Module Error", 
						"Error occurred exporting module: " + ex.getMessage());
			}
			persistDefaultRepositoryPath(repositoryPath);
		}
		return true;
	}

	public static void persistDefaultRepositoryPath(String repositoryPath) {
		if (repositoryPath!=null && !repositoryPath.isEmpty()) {
		    CeylonPlugin.getInstance().getDialogSettings()
		            .put("repositoryPath", repositoryPath);
		}
	}
	
	/*public static void copyFolder(File src, File dest)
			throws IOException{
    	if (src.isDirectory()) {
    		if ( !dest.exists() ) dest.mkdir();
    		for (String file: src.list()) {
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   copyFolder(srcFile, destFile);
    		}
    	}
    	else if (src.getName().endsWith(".car") ||
    			src.getName().endsWith(".src") ||
    			src.getName().endsWith(".sha1")) {
    	    FileChannel source = null;
    	    FileChannel destination = null;
    	    try {
    	        source = new FileInputStream(src).getChannel();
    	        destination = new FileOutputStream(dest).getChannel();
    	        destination.transferFrom(source, 0, source.size());
    	    }
    	    finally {
    	        if (source != null) {
    	            source.close();
    	        }
    	        if (destination != null) {
    	            destination.close();
    	        }
    	    }
	    	System.out.println("Archive exported from " + src + " to " + dest);
    	}
    }*/
	
	@Override
	public boolean canFinish() {
		return page.isPageComplete();
	}
    
}
