package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.BUILDER_ID;
import static com.redhat.ceylon.eclipse.core.model.modelJ2C.ceylonModel;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.eclipse.core.classpath.CeylonLanguageModuleContainer;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;

public class CeylonNature extends ProjectNatureBase {
    
    public static final String NATURE_ID = PLUGIN_ID + ".ceylonNature";
    
    public static boolean isEnabled(IProject project) {
        try {
            return project.hasNature(NATURE_ID);
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String systemRepo;
    boolean astAwareIncrementalBuilds;
    boolean keepSettings;
    boolean compileJs;
    boolean compileJava;
    String verbose;

    public CeylonNature() {
        keepSettings=true;
    }
    
    public CeylonNature(String systemRepo, 
            boolean enableJdtClasses, 
            boolean java,
            boolean js,
            boolean astAwareIncrementalBuilds,
            String verbose) {
        this.systemRepo = systemRepo;
        compileJs = js;
        compileJava = java;
        this.astAwareIncrementalBuilds = astAwareIncrementalBuilds;
        this.verbose = verbose;
    }
    
    public String getNatureID() {
        return NATURE_ID;
    }
    
    public String getBuilderID() {
        return BUILDER_ID;
    }
    
    public void addToProject(final IProject project) {
        ceylonModel().addProject(project);
        super.addToProject(project);
        try {
            new CeylonLanguageModuleContainer(project).install();
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        new CeylonProjectModulesContainer(project).runReconfigure();
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
    protected Map<String, String> getBuilderArguments() {
        Map<String, String> args = super.getBuilderArguments();
        if (!keepSettings) {
            if (!"${ceylon.repo}".equals(systemRepo)) {
                args.put("systemRepo", systemRepo);
            } else {
                args.remove("systemRepo");
            }
            if (astAwareIncrementalBuilds) {
                args.remove("astAwareIncrementalBuilds");
            } else {
                args.put("astAwareIncrementalBuilds", "false");
            }
            if (compileJava) {
                args.remove("compileJava");
            } else {
                args.put("compileJava", "false");
            }
            if (compileJs) {
                args.put("compileJs", "true");
            } else {
                args.remove("compileJs");
            }
            if (verbose==null) {
                args.remove("verbose");
            }
            else {
                args.put("verbose", verbose);
            }
        }
        return args;
    }
    
}
