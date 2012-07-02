package com.redhat.ceylon.eclipse.imp.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.runtime.IPluginLog;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

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
    
    public void addToProject(IProject project) {
        super.addToProject(project);
        IJavaProject javaProject = JavaCore.create(project);
        try {
            IPath path = new Path(CeylonClasspathContainer.CONTAINER_ID + "/default");
            IClasspathEntry newEntry = JavaCore.newContainerEntry(path, null, new IClasspathAttribute[0], false);
            CeylonClasspathContainer ceyloncp = new CeylonClasspathContainer(javaProject, path,
                    new IClasspathEntry[0], new IClasspathAttribute[0]);
            JavaCore.setClasspathContainer(path, new IJavaProject[] {javaProject},
                new IClasspathContainer[] {ceyloncp}, null);
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(Arrays.asList(entries));
            int index = 0;
            boolean mustReplace = false;
            for (IClasspathEntry entry : newEntries) {
                if (entry.getPath().equals(newEntry.getPath()) ) {
                    mustReplace = true;
                    break;
                }
                index ++;
            }
            if (mustReplace) {
                newEntries.set(index, newEntry);
            }
            else {
                newEntries.add(newEntry);
            }
            entries = (IClasspathEntry[]) newEntries.toArray(new IClasspathEntry[newEntries.size()]);
            javaProject.setRawClasspath(entries, javaProject.getOutputLocation(), null);
            ceyloncp.launchResolve(true, null);
        } catch (JavaModelException e) {
            CeylonPlugin.log(e);
        }
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
