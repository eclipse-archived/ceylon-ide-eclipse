package com.redhat.ceylon.eclipse.imp.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ExportModuleWizard extends Wizard implements IExportWizard {

    IStructuredSelection selection;
    IWorkbench workbench;
    ExportModuleWizardPage page;
    
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
	}

    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            page = new ExportModuleWizardPage();
            page.init(selection);
        }
        addPage(page);
    }

    private IJavaElement getSelectedElement() {
        if (selection!=null && selection.size()==1) {
            IJavaElement je = (IJavaElement) ((IAdaptable) selection.getFirstElement())
                    .getAdapter(IJavaElement.class);
            //TODO: handle the case of an IFile
            return je;
        }
        else {
            return null;
        }
    }
    
	@Override
	public boolean performFinish() {
		IJavaElement selectedElem = getSelectedElement();
		if (selectedElem==null) {
			return false;
		}
		else {
			IJavaProject javaProject = selectedElem.getJavaProject();
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
				IPath projectLoc = javaProject.getProject().getLocation().makeAbsolute();
				projectLoc = projectLoc.uptoSegment(projectLoc.segmentCount()-1);
				IPath outputDir = javaProject.getOutputLocation();
				File source = projectLoc.append(outputDir).toFile();
				/*File source = new File(platformLoc.getFile(), 
						javaProject.getOutputLocation().toFile().getPath());*/
				File dest = new File(page.getRepositoryPath());
				copyFolder(source, dest);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			return true;
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
		return super.canFinish() &&
				page.isPageComplete() && 
				new File(page.getRepositoryPath()).exists();
	}

}
