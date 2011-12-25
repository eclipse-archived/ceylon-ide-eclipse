package com.redhat.ceylon.eclipse.ui;

import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_CLASS;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_CORRECTION;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_DEFAULT_REFINEMENT;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_EXPORT_CAR;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE_ERROR;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE_WARNING;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FORMAL_REFINEMENT;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_IMPORT;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_IMPORT_LIST;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_INTERFACE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_CLASS;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_INTERFACE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_METHOD;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_METHOD;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_FILE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_MODULE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_OPEN_DECLARATION;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_PACKAGE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_PARAMETER;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_PROJECT;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_SEARCH_RESULTS;
import static org.eclipse.imp.preferences.PreferenceConstants.EDITOR_CLOSE_FENCES;
import static org.eclipse.imp.preferences.PreferenceConstants.EDITOR_CORRECTION_INDICATION;
import static org.eclipse.imp.preferences.PreferenceConstants.EDITOR_MATCHING_BRACKETS;
import static org.eclipse.imp.preferences.PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR;
import static org.eclipse.imp.preferences.PreferenceConstants.P_DUMP_TOKENS;
import static org.eclipse.imp.preferences.PreferenceConstants.P_EMIT_BUILDER_DIAGNOSTICS;
import static org.eclipse.imp.preferences.PreferenceConstants.P_EMIT_MESSAGES;
import static org.eclipse.imp.preferences.PreferenceConstants.P_SOURCE_FONT;
import static org.eclipse.imp.preferences.PreferenceConstants.P_SPACES_FOR_TABS;
import static org.eclipse.imp.preferences.PreferenceConstants.P_TAB_WIDTH;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.builder.CeylonNature;

public class CeylonPlugin extends PluginBase {

	public static final String PLUGIN_ID = "com.redhat.ceylon.eclipse.ui";
	public static final String LANGUAGE_ID = "ceylon";
	public static final String EDITOR_ID = PLUGIN_ID + ".editor";

	/**
	 * The unique instance of this plugin class
	 */
	protected static CeylonPlugin pluginInstance;
	
	private File ceylonRepository = null;

	public File getCeylonRepository() {
        return ceylonRepository;
    }

    public static CeylonPlugin getInstance() {
		if (pluginInstance==null) new CeylonPlugin();
		return pluginInstance;
	}

	public CeylonPlugin() {
		pluginInstance = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
        String ceylonRepositoryProperty = System.getProperty("ceylon.repo", "");
        if (! "".equals(ceylonRepositoryProperty)) {
            File ceylonRepositoryPath = new java.io.File(ceylonRepositoryProperty);
            if (ceylonRepositoryPath.exists()) {
                ceylonRepository = ceylonRepositoryPath;
            }
        }
        if (ceylonRepository == null) {
            try {
                Bundle bundle = Platform.getBundle(CeylonPlugin.PLUGIN_ID);
                Path path = new Path("defaultRepository");
                URL eclipseUrl = FileLocator.find(bundle, path, null);
                URL fileURL = FileLocator.resolve(eclipseUrl);
                String urlPath = fileURL.getPath();
                URI fileURI = new URI("file", null, urlPath, null);
                ceylonRepository = new File(fileURI);
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	    super.start(context);
	    setPreferenceDefaults(RuntimePlugin.getInstance().getPreferenceStore());
//        copyDefaultRepoIfNecessary();
        runInitialBuild();
        registerProjectOpenCloseListener();
	}

	@Override
	public String getID() {
		return PLUGIN_ID;
	}

	@Override
	public String getLanguageID() {
		return LANGUAGE_ID;
	}

    private static IPath iconsPath = new Path("icons/");

    private ImageDescriptor image(String file) {
        URL url = FileLocator.find(getBundle(), 
                iconsPath.append(file), null);
        if (url!=null) {
        	return ImageDescriptor.createFromURL(url);
        }
        else {
        	return null;
        }
	}
	    
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
        reg.put(CEYLON_PROJECT, image("prj_obj.gif"));
		reg.put(CEYLON_PACKAGE, image("package_obj.gif"));
		reg.put(CEYLON_IMPORT_LIST, image("impc_obj.gif"));
		reg.put(CEYLON_IMPORT, image("imp_obj.gif"));
		reg.put(CEYLON_FILE, image("template_obj.gif"));
		reg.put(CEYLON_FILE_WARNING, image("file_obj.gif"));
		reg.put(CEYLON_FILE_ERROR, image("file_obj.gif"));
		reg.put(CEYLON_CLASS, image("class_obj.gif"));
		reg.put(CEYLON_INTERFACE, image("int_obj.gif"));
		reg.put(CEYLON_LOCAL_CLASS, image("innerclass_private_obj.gif"));
		reg.put(CEYLON_LOCAL_INTERFACE, image("innerinterface_private_obj.gif"));
		reg.put(CEYLON_METHOD, image("public_co.gif"));
		reg.put(CEYLON_ATTRIBUTE, image("public_co.gif"));
		reg.put(CEYLON_LOCAL_METHOD, image("private_co.gif"));
		reg.put(CEYLON_LOCAL_ATTRIBUTE, image("private_co.gif"));
        reg.put(CEYLON_PARAMETER, image("methpro_obj.gif"));
	    reg.put(CEYLON_DEFAULT_REFINEMENT, image("over_co.gif"));
        reg.put(CEYLON_FORMAL_REFINEMENT, image("implm_co.gif"));
        reg.put(CEYLON_OPEN_DECLARATION, image("opentype.gif"));
        reg.put(CEYLON_SEARCH_RESULTS, image("search_ref_obj.gif"));
        reg.put(CEYLON_CORRECTION, image("correction_change.gif"));
        reg.put(CEYLON_NEW_FILE, image("new_wiz.png"));
        reg.put(CEYLON_NEW_MODULE, image("newftrprj_wiz.png"));
        reg.put(CEYLON_EXPORT_CAR, image("jar_pack_wiz.png"));
	}
	
	/**
	 * Kick off an initial build at startup time in order
	 * to build the model.
	 */
    private void runInitialBuild() {
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        Job buildJob = new Job("Building Ceylon Model") {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                try {
                    List<IProject> interestingProjects = new ArrayList<IProject>();
                    for (IProject project : workspaceRoot.getProjects()) {
                        if (project.isOpen() && project.hasNature(CeylonNature.NATURE_ID)) {
                            interestingProjects.add(project);
                        }
                    }
                    
                    monitor.beginTask("Building Ceylon Model", 3 * interestingProjects.size());

                    for (IProject project : interestingProjects) {
                        ISourceProject sourceProject = ModelFactory.open(project);
                        CeylonBuilder.buildCeylonModel(project, sourceProject, monitor);
                    }
                    
                    for (IProject project : interestingProjects) {
                        TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
                        if (typeChecker != null) {
                            List<PhasedUnits> phasedUnitsForDependencies = new ArrayList<PhasedUnits>();
                            
                            for (IProject requiredProject : CeylonBuilder.getRequiredProjects(project)) {
                                TypeChecker requiredProjectTypeChecker = CeylonBuilder.getProjectTypeChecker(requiredProject);
                                if (requiredProjectTypeChecker != null) {
                                    phasedUnitsForDependencies.add(requiredProjectTypeChecker.getPhasedUnits());
                                }
                            }
                            
                            for (PhasedUnit pu : typeChecker.getPhasedUnits().getPhasedUnits()) {
                                pu.collectUnitDependencies(typeChecker.getPhasedUnits(), phasedUnitsForDependencies);
                            }
                        }
                    }
                } catch (CoreException e) {
                    return new Status(IStatus.ERROR, getID(), "Job '" + this.getName() + "' failed", e);
                } catch (ModelException e) {
                    return new Status(IStatus.ERROR, getID(), "Job '" + this.getName() + "' failed", e);
                }
                return Status.OK_STATUS;
            }
            
        };
        buildJob.setRule(workspaceRoot);
        buildJob.schedule();
    }
    
    private void registerProjectOpenCloseListener() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot workspaceRoot = workspace.getRoot();
        
        workspace.addResourceChangeListener(new IResourceChangeListener() {
            
            @Override
            public void resourceChanged(IResourceChangeEvent event) {
                try {
                    event.getDelta().accept(new IResourceDeltaVisitor() {
                        
                        @Override
                        public boolean visit(IResourceDelta delta) throws CoreException {
                            IResource resource = delta.getResource();
                            if (resource.equals(workspaceRoot)) {
                                return true;
                            }
                            if (resource instanceof IProject && (delta.getFlags() & IResourceDelta.OPEN) != 0) {
                                final IProject project = (IProject) resource;
                                try {
                                    List<IProject> projectsToBuild = new ArrayList<IProject>();
                                    projectsToBuild.add(project); 
                                    for (IProject referencingProject : project.getReferencingProjects()) {
                                        projectsToBuild.add(referencingProject);
                                    }
                                    for (final IProject projectToBuild : projectsToBuild) {
                                        if (projectToBuild.isOpen() && projectToBuild.hasNature(CeylonNature.NATURE_ID)) {
                                            Job buildJob = new Job("Building Ceylon Model for project " + projectToBuild.getName()) {
                                                @Override
                                                public IStatus run(IProgressMonitor monitor) {
                                                    try {
                                                        monitor.beginTask("Building Ceylon Model", 3);
                                                        ISourceProject sourceProject = ModelFactory.open(projectToBuild);
                                                        CeylonBuilder.buildCeylonModel(projectToBuild, sourceProject,
                                                                monitor);
                                                    } catch (ModelException e) {
                                                        return new Status(IStatus.ERROR, getID(), "Job '" + this.getName() + "' failed", e);
                                                    } catch (CoreException e) {
                                                        return new Status(IStatus.ERROR, getID(), "Job '" + this.getName() + "' failed", e);
                                                    }
                                                    return Status.OK_STATUS;
                                                }
                                                
                                            };
                                            buildJob.setRule(workspaceRoot);
                                            buildJob.schedule();
                                        }
                                    }
                                    for (IProject projectToBuild : projectsToBuild) {
                                        TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
                                        if (typeChecker != null) {
                                            List<PhasedUnits> phasedUnitsForDependencies = new ArrayList<PhasedUnits>();
                                            
                                            for (IProject requiredProject : CeylonBuilder.getRequiredProjects(projectToBuild)) {
                                                TypeChecker requiredProjectTypeChecker = CeylonBuilder.getProjectTypeChecker(requiredProject);
                                                if (requiredProjectTypeChecker != null) {
                                                    phasedUnitsForDependencies.add(requiredProjectTypeChecker.getPhasedUnits());
                                                }
                                            }
                                            
                                            for (PhasedUnit pu : typeChecker.getPhasedUnits().getPhasedUnits()) {
                                                pu.collectUnitDependencies(typeChecker.getPhasedUnits(), phasedUnitsForDependencies);
                                            }
                                        }
                                    }
                                } catch (CoreException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            return false;
                        }
                    });
                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, IResourceChangeEvent.POST_CHANGE);
    }
    

    /*private void copyDefaultRepoIfNecessary() {
        File home = new File( System.getProperty("user.home") );
        File ceylon = new File( home, ".ceylon" );
        File repo = new File( ceylon, "repo" );
        repo.mkdirs();
        if (repo.list().length == 0) {
            try {
                Bundle bundle = Platform.getBundle(CeylonPlugin.PLUGIN_ID);
                Path path = new Path("defaultRepository");
                URL eclipseUrl = FileLocator.find(bundle, path, null);
                URL fileURL = FileLocator.resolve(eclipseUrl);
                File internalRepoCopy;
                String urlPath = fileURL.getPath();
                URI fileURI = new URI("file", null, urlPath, null);
                internalRepoCopy = new File(fileURI);
                if (internalRepoCopy.exists()) {
                    copyDirectory(internalRepoCopy, repo);
                }
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }*/
    
    public void copyDirectory(File sourceLocation , File targetLocation)
    throws IOException {
        
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }    
    
	/**
	 * Awful little hack to get a nonugly icon on already-open 
	 * editors at startup time, since IMP doesn't init the
	 * editor to the language-specific icon until the editor
	 * becomes active.
	 */
    /*private void setIconForOpenWindows(IWorkbenchWindow window) {
        for (IWorkbenchPage page: window.getPages()) {
             for (IEditorReference ref: page.findEditors(null, UniversalEditor.EDITOR_ID, IWorkbenchPage.MATCH_ID)) {
                 try {
                    Method method = WorkbenchPart.class.getDeclaredMethod("setTitleImage", Image.class);
                    method.setAccessible(true);
                    method.invoke(ref.getEditor(true), CeylonLabelProvider.FILE_IMAGE);
                 }
                 catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
    }*/
    
    private static final String FLAG = "preferencesInitialized";

    /**
     * Really awful hack to initialize some preferences the 
     * first time we run the IDE, since IMP sometimes fails
     * to set the defaults in time for already-open editors
     * to detect them.
     */
    private static void setPreferenceDefaults(IPreferenceStore store) {
        if (!store.getBoolean(FLAG)) {
            System.out.println("INITIALIZING PREFERENCES");
            store.setValue(FLAG, true);
            int size = Platform.getOS().equals("macosx") ? 11 : 10;
            PreferenceConverter.setValue(store, P_SOURCE_FONT, 
                    new FontData[] { new FontData("Monaco", size, SWT.NORMAL),
                                     new FontData("Courier New", size, SWT.NORMAL), 
                                     new FontData("Monospace", size, SWT.NORMAL)});
    
            store.setValue(P_EMIT_MESSAGES, false);
            store.setValue(P_EMIT_BUILDER_DIAGNOSTICS, false);
            store.setValue(P_TAB_WIDTH, 4);
            store.setValue(P_SPACES_FOR_TABS, true);
            store.setValue(P_DUMP_TOKENS, false);
            store.setValue(EDITOR_MATCHING_BRACKETS, true);
            store.setValue(EDITOR_CORRECTION_INDICATION, true);
            store.setValue(EDITOR_CLOSE_FENCES, true);
            
            store.setValue(EDITOR_TAB_WIDTH, 4);
            store.setValue(EDITOR_SPACES_FOR_TABS, true);
        
            PreferenceConverter.setValue(store, EDITOR_MATCHING_BRACKETS_COLOR,
                    new RGB(0,120,255));
        }
    }

}
