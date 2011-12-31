package com.redhat.ceylon.eclipse.imp.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

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
                try {
                    repoPath = project.getProject()
                            .getPersistentProperty(new QualifiedName(CeylonPlugin.PLUGIN_ID,"repo"));
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
            }
			if (repoPath==null) repoPath = getDefaultRepositoryPath();
            page = new ExportModuleWizardPage(repoPath, project);
            //page.init(selection);
        }
        addPage(page);
    }

	static String getDefaultRepositoryPath() {
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
    
	@Override
	public boolean performFinish() {
		String repositoryPath = page.getRepositoryPath();
		IJavaProject project = page.getProject();
		if (project==null) {
			MessageDialog.openError(getShell(), "Export Module Error", 
					"No Java project selected.");
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
			try {
				//URL platformLoc = Platform.getInstanceLocation().getURL();
				IPath projectLoc = project.getProject().getLocation().makeAbsolute();
				projectLoc = projectLoc.uptoSegment(projectLoc.segmentCount()-1);
				IPath outputDir = project.getOutputLocation();
				File source = projectLoc.append(outputDir).toFile();
				/*File source = new File(platformLoc.getFile(), 
						javaProject.getOutputLocation().toFile().getPath());*/
				File dest = new File(repositoryPath);
				if (dest.exists()) {
					copyFolder(source, dest);
					persistDefaultRepositoryPath(repositoryPath);
				}
				else {
					MessageDialog.openError(getShell(), "Export Module Error", 
							"No repository at location: " + repositoryPath);
					return false;
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Export Module Error", 
						"Error occurred exporting module: " + e.getMessage());
			}
		}
		return true;
	}

	static void persistDefaultRepositoryPath(String repositoryPath) {
		if (repositoryPath!=null && !repositoryPath.isEmpty()) {
		    CeylonPlugin.getInstance().getDialogSettings()
		            .put("repositoryPath", repositoryPath);
		}
	}
	
	public static void copyFolder(File src, File dest)
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
    }
	
	@Override
	public boolean canFinish() {
		return true;
	}
    
}
