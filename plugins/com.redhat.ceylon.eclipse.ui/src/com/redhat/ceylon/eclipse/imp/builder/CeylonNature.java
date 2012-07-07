package com.redhat.ceylon.eclipse.imp.builder;

import static com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder.getCeylonModulesOutputPath;
import static com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder.setCeylonModulesOutputPath;

import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.runtime.IPluginLog;
import org.eclipse.jdt.core.JavaCore;

import com.redhat.ceylon.eclipse.core.cpcontainer.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonNature extends ProjectNatureBase {
    
    public static final String NATURE_ID = CeylonPlugin.PLUGIN_ID + ".ceylonNature";
    
    private IPath outputPath;
    
    public CeylonNature() {}
    
    public CeylonNature(IPath outputPath) {
    	this.outputPath = outputPath;
    }
    
    public String getNatureID() {
        return NATURE_ID;
    }
    
    public String getBuilderID() {
        return CeylonBuilder.BUILDER_ID;
    }
    
	public void addToProject(final IProject project) {
        super.addToProject(project);
        if (outputPath!=null) {
        	IPath oldPath = getCeylonModulesOutputPath(project);
        	if (oldPath!=null) {
				IFolder old = project.getFolder(oldPath.makeRelativeTo(project.getLocation()));
	        	if (old.exists() && old.isHidden()) {
	        		try {
	        			old.setHidden(false);
	        			//old.touch(null);
	        		} 
	        		catch (CoreException e) {
	        			e.printStackTrace();
	        		}
	        	}
        	}
        	setCeylonModulesOutputPath(project, outputPath);
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
    	if (outputPath!=null) {
    		args.put("outputPath", outputPath.toString());
    	}
		return args;
    }
}
