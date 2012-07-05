package com.redhat.ceylon.eclipse.imp.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.runtime.IPluginLog;
import org.eclipse.jdt.core.JavaCore;

import com.redhat.ceylon.eclipse.core.cpcontainer.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonNature extends ProjectNatureBase {
    
    public static final String NATURE_ID = CeylonPlugin.PLUGIN_ID + ".ceylonNature";
    
    public String getNatureID() {
        return NATURE_ID;
    }
    
    public String getBuilderID() {
        return CeylonBuilder.BUILDER_ID;
    }
    
	public void addToProject(final IProject project) {
        super.addToProject(project);
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
}
