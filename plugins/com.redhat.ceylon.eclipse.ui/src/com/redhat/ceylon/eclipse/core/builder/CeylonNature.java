package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.BUILDER_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer;

public class CeylonNature extends ProjectNatureBase {
    
    public static final String NATURE_ID = PLUGIN_ID + ".ceylonNature";
    
    public static boolean isEnabled(IProject project) {
        boolean isEnabled = false;
        try {
            isEnabled = project.hasNature(NATURE_ID);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return isEnabled;
    }
    
    private String systemRepo;
	boolean enableJdtClasses;
	boolean hideWarnings;
	boolean keepSettings;
	boolean compileJs;

    public CeylonNature() {
    	keepSettings=true;
    }
    
    public CeylonNature(String systemRepo, boolean enableJdtClasses, boolean hideWarnings, boolean js) {
    	this.systemRepo = systemRepo;
    	this.enableJdtClasses = enableJdtClasses;
    	this.hideWarnings = hideWarnings;
    	compileJs = js;
    }
    
    public String getNatureID() {
        return NATURE_ID;
    }
    
    public String getBuilderID() {
        return BUILDER_ID;
    }
    
    public void addToProject(final IProject project) {
        super.addToProject(project);
        new CeylonClasspathContainer(project).runReconfigure();
    }
    
    protected void refreshPrefs() {
        // TODO implement preferences and hook in here
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
    @SuppressWarnings("unchecked")
    protected Map<String, String> getBuilderArguments() {
        Map<String, String> args = super.getBuilderArguments();
    	if (!keepSettings) {
            if (!"${ceylon.repo}".equals(systemRepo)) {
                args.put("systemRepo", systemRepo);
            } else {
                args.remove("systemRepo");
            }
    		if (hideWarnings) {
    			args.put("hideWarnings", "true");
    		}
    		else {
    			args.remove("hideWarnings");
    		}
    		if (enableJdtClasses) {
    			args.put("explodeModules", "true");
    		}
    		else {
    			args.remove("explodeModules");
    		}
    		if (compileJs) {
    		    args.put("compileJs", "true");
    		} else {
    		    args.remove("compileJs");
    		}
    	}
		return args;
    }
    
}