package com.redhat.ceylon.eclipse.imp.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.runtime.IPluginLog;
import org.eclipse.jdt.core.JavaCore;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonNature extends ProjectNatureBase {
    
    public static final String NATURE_ID = CeylonPlugin.PLUGIN_ID + ".ceylonNature";
    
    public String getNatureID() {
        return NATURE_ID;
    }
    
    public String getBuilderID() {
        return CeylonBuilder.BUILDER_ID;
    }
    
    public void addToProject(IProject project) {
        super.addToProject(project);
    }
    
    protected void refreshPrefs() {
        // TODO implement preferences and hook in here
    }
    
    public IPluginLog getLog() {
        return CeylonPlugin.getInstance();
    }
    
    /**
     * Returns the ID of the builder that processes the artifacts that this
     * nature's builder produces. If there is no such dependency, returns null.
     */
    // TODO this should be a property of the builder itself...
    protected String getDownstreamBuilderID() {
        return JavaCore.BUILDER_ID;
    }
}
