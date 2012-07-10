package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.BUILDER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputPath;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.runtime.IPluginLog;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonNature extends ProjectNatureBase {
    
    public static final String NATURE_ID = PLUGIN_ID + ".ceylonNature";
    
    private IPath outputPath;
	String repositoryPath;
	boolean enableJdtClasses;
	boolean hideWarnings;
	boolean keepSettings;
	
    public CeylonNature() {
    	keepSettings=true;
    }
    
    public CeylonNature(IPath outputPath, String repositoryPath,
    		boolean enableJdtClasses, boolean hideWarnings) {
    	this.outputPath = outputPath;
    	this.repositoryPath = repositoryPath;
    	this.enableJdtClasses = enableJdtClasses;
    	this.hideWarnings = hideWarnings;
    }
    
    public String getNatureID() {
        return NATURE_ID;
    }
    
    public String getBuilderID() {
        return BUILDER_ID;
    }
    
	public void addToProject(final IProject project) {
        if (outputPath!=null) {
        	IPath oldPath = getCeylonModulesOutputPath(project);
        	if (oldPath!=null) {
				IFolder old = project.getFolder(oldPath.makeRelativeTo(project.getLocation()));
	        	/*if (old.exists() && old.isHidden()) {
	        		try {
	        			old.setHidden(false);
	        			//old.touch(null);
	        		} 
	        		catch (CoreException e) {
	        			e.printStackTrace();
	        		}
	        	}*/
				if (old.exists() && !oldPath.equals(outputPath)) {
					boolean remove = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						    .getShell(), "Changing Ceylon output folder", 
						    "The Ceylon output folder has changed. Do you want to remove the old output folder '" +
						    old.getFullPath().toString() + "' and all its contents?");
					if (remove) {
						try {
							old.delete(true, null);
						} 
						catch (CoreException e) {
							e.printStackTrace();
						}
					}
				}
        	}
        }
        super.addToProject(project);
        if (outputPath!=null) {
        	IFolder folder = project.getFolder(outputPath.makeRelativeTo(project.getLocation()));
        	if (!folder.isHidden()) {
        		try {
        			folder.setHidden(true);
        			//folder.touch(null);
        		} 
        		catch (CoreException e) {
        			e.printStackTrace();
        		}
        	}
        	/*try {
				project.refreshLocal(IResource.DEPTH_ONE, null);
			} 
        	catch (CoreException e) {
				e.printStackTrace();
			}*/
        }
        new CeylonClasspathContainer(project).runReconfigure();
    }
    
    protected void refreshPrefs() {
        // TODO implement preferences and hook in here
    }
    
    public IPluginLog getLog() {
        return CeylonPlugin.getInstance();
    }
    
    /**
     * Run the Java builder before the Ceylon builder, since
     * it's more common for Ceylon to call Java than the
     * other way around, and because the Java builder erases
     * the output directory during a full build.
     */
    protected String getUpstreamBuilderID() {
        return JavaCore.BUILDER_ID;
    }
    
    @Override
    protected Map getBuilderArguments() {
    	Map args = super.getBuilderArguments();
    	if (!keepSettings) {
    		args.put("outputPath", outputPath.toString());
    		if (repositoryPath!=null) {
    			args.put("repositoryPath", repositoryPath);
    		}
    		else {
    			args.remove("repositoryPath");
    		}
    		if (hideWarnings) {
    			args.put("hideWarnings", "true");
    		}
    		else {
    			args.remove("hideWarnings");
    		}
    		if (enableJdtClasses) {
    			args.put("enableJdtClasses", "true");
    		}
    		else {
    			args.remove("enableJdtClasses");
    		}
    	}
		return args;
    }
}
