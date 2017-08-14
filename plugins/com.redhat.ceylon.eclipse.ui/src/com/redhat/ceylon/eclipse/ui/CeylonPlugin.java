package com.redhat.ceylon.eclipse.ui;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.ALTERNATE_ICONS;
import static com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager.getExternalSourceArchiveManager;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.platformJ2C;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentTheme;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.redhat.ceylon.common.FileUtil;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.dist.osgi.Activator;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.builder.ProjectChangeListener;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil;
import com.redhat.ceylon.eclipse.core.debug.CeylonDebugElementAdapterFactory;
import com.redhat.ceylon.eclipse.core.debug.preferences.CeylonDebugOptionsManager;
import com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;


public class CeylonPlugin extends AbstractUIPlugin implements CeylonResources {

    public static final String PLUGIN_ID = "com.redhat.ceylon.eclipse.ui";
    public static final String DIST_PLUGIN_ID = "com.redhat.ceylon.dist";
    public static final String EMBEDDED_REPO_PLUGIN_ID = "com.redhat.ceylon.dist.repo";
    public static final String LANGUAGE_ID = "ceylon";
    public static final String EDITOR_ID = PLUGIN_ID + ".editor";
    private static final String[] MODULE_LAUNCHER_LIBRARIES = new String[] { 
        "ceylon.bootstrap-"+Versions.CEYLON_VERSION_NUMBER+".jar" 
    };
    private static final String[] RUNTIME_LIBRARIES = new String[] {
        "ceylon.bootstrap-"+Versions.CEYLON_VERSION_NUMBER+".car",
        "com.redhat.ceylon.module-resolver-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.common-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.model-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "org.jboss.modules-"+Versions.DEPENDENCY_JBOSS_MODULES_VERSION+".jar",
    };
    private static final String[] COMPILETIME_LIBRARIES = new String[] {
        "com.redhat.ceylon.typechecker-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.model-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.common-"+Versions.CEYLON_VERSION_NUMBER+".jar",
    };
    
    public static final String EDITOR_FONT_PREFERENCE = 
            PLUGIN_ID + ".editorFont";
    public static final String HOVER_FONT_PREFERENCE = 
            PLUGIN_ID + ".hoverFont";
    public static final String COMPLETION_FONT_PREFERENCE = 
            PLUGIN_ID + ".completionFont";
    public static final String OPEN_FONT_PREFERENCE = 
            PLUGIN_ID + ".openFont";
    public static final String OUTLINE_FONT_PREFERENCE = 
            PLUGIN_ID + ".outlineFont";
    
    public static final String COLORS_AND_FONTS_PAGE_ID = 
            "org.eclipse.ui.preferencePages.ColorsAndFonts";
    
    private FontRegistry fontRegistry;

    /**
     * The unique instance of this plugin class
     */
    protected static CeylonPlugin pluginInstance;
    
    private File ceylonRepository = null;

    private BundleContext bundleContext;

    /**
     * - If the 'ceylon.repo' property exist, returns the corresponding file
     * <br>
     * - Else return the internal repo folder
     * 
     * @return
     */
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
    public void start(BundleContext context) 
            throws Exception {
        super.start(context);
        this.bundleContext = context;
        
        long startTime = System.currentTimeMillis();
        log(IStatus.INFO, "Starting Ceylon metamodel registering");
        Activator.loadBundleAsModule(bundleContext.getBundle());
        log(IStatus.INFO, "Finished Ceylon metamodel registering in " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
        
        String ceylonRepositoryProperty = 
                System.getProperty("ceylon.repo", "");
        ceylonRepository = 
                getCeylonPluginRepository(
                        ceylonRepositoryProperty);
        
        javaSourceArchiveCacheDirectory = 
                new File(getStateLocation().toFile(), 
                        "JavaSourceArchiveCache");
        javaSourceArchiveCacheDirectory.mkdirs();

        addResourceFilterPreference();

        InputStream contributionStream = 
                new ByteArrayInputStream(new String(
                "<plugin>\n" +
                "<extension point=\"org.eclipse.wst.xml.core.catalogContributions\">\n" +
                "  <catalogContribution>\n" +
                "    <uri "
                        + "name=\"http://www.ceylon-lang.org/xsd/overrides\" "
                        + "uri=\"platform:/plugin/" + PLUGIN_ID + "/META-INF/overrides.xsd\"/>\n" + 
                "  </catalogContribution>\n"+
                "</extension>\n" +
                "</plugin>").getBytes("ASCII"));

        IExtensionRegistry reg = RegistryFactory.getRegistry();
        ExtensionRegistry er = (ExtensionRegistry) reg;
        Object key = er.getTemporaryUserToken();
        IContributor contributor = 
                ContributorFactoryOSGi.createContributor(
                        context.getBundle());
                
        RegistryFactory.getRegistry()
            .addContribution(contributionStream, 
                    contributor, false, 
                    PLUGIN_ID + ".xmlCatalogContribution", 
                    null, key);
        
        /*iconChangeListener = 
        new IPropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent event) {
		        if (event.getProperty()
		                .equals(ALTERNATE_ICONS)) {
		            initializeImageRegistry(getImageRegistry());
		        }
		    }
		};
		getPreferences()
		        .addPropertyChangeListener(iconChangeListener);*/
		
        
        final IWorkspace workspace = getWorkspace();
        final IWorkspaceRoot root = workspace.getRoot();
        
        platformJ2C().platformServices().register();

        for (IProject project: root.getProjects()) {
            if (project.isAccessible() && 
                    CeylonNature.isEnabled(project)) {
                modelJ2C().ceylonModel().addProject(project);
            }
        }

        for (IProject project: root.getProjects()) {
            if (project.isAccessible() && 
                    CeylonNature.isEnabled(project)) {
            	IJavaProject javaProject = JavaCore.create(project);
            	CeylonClasspathUtil.getCeylonClasspathContainers(javaProject);
            }
        }

        registerProjectOpenCloseListener();
        CeylonEncodingSynchronizer.getInstance().install();

        Job refreshExternalSourceArchiveManager = 
                new Job("Refresh External Ceylon Source Archives") {
            protected IStatus run(IProgressMonitor monitor) {
                ExternalSourceArchiveManager esam = 
                        getExternalSourceArchiveManager();
                esam.initialize();
                workspace.addResourceChangeListener(esam);
                return Status.OK_STATUS;
            };
        };
        refreshExternalSourceArchiveManager.setRule(root);
        refreshExternalSourceArchiveManager.schedule();
        
        CeylonDebugOptionsManager.getDefault().startup();

        com.redhat.ceylon.eclipse.code.complete.setupCompletionExecutors_.setupCompletionExecutors();
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        unregisterProjectOpenCloseListener();
        CeylonEncodingSynchronizer.getInstance().uninstall();
        CeylonDebugElementAdapterFactory.restoreJDTDebugElementAdapters();
        CeylonDebugOptionsManager.getDefault().shutdown();
        FileUtil.deleteQuietly(getJavaSourceArchiveCacheDirectory());
        /*getPreferences()
            .removePropertyChangeListener(iconChangeListener);*/
        com.redhat.ceylon.eclipse.code.complete.shutdownCompletionExecutors_.shutdownCompletionExecutors();
        
    }

    private void addResourceFilterPreference() 
            throws BackingStoreException {
        new Job("Add Resource Filter for Ceylon projects") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IEclipsePreferences instancePreferences = 
                        InstanceScope.INSTANCE
                            .getNode(JavaCore.PLUGIN_ID);
                /*IEclipsePreferences defaultPreferences = DefaultScope.INSTANCE
                        .getNode(JavaCore.PLUGIN_ID);*/
                String filter = 
                        instancePreferences.get(
                                CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, 
                                "");
                if (filter.isEmpty()) {
                    filter = "*.launch, *.ceylon";
                }
                else if (!filter.contains("*.ceylon")) {
                    filter += ", *.ceylon";
                }
                instancePreferences.put(
                        CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, 
                        filter);
                try {
                    instancePreferences.flush();
                } 
                catch (BackingStoreException e) {
                    e.printStackTrace();
                }
                return Status.OK_STATUS;
            }
            
        }.schedule();
    }

    public static File getEmbeddedCeylonRepository() {
        File repo = null;
        try {
            Bundle bundle = Platform.getBundle(EMBEDDED_REPO_PLUGIN_ID);
            IPath path = new Path("repo");
            if (bundle == null) {
                bundle = Platform.getBundle(DIST_PLUGIN_ID);
                path = new Path("embeddedRepository").append(path);
            }
            URL eclipseUrl = 
                    FileLocator.find(bundle, path, null);
            URL fileURL = 
                    FileLocator.resolve(eclipseUrl);
            String urlPath = fileURL.getPath();
            URI fileURI = 
                    new URI("file", null, urlPath, null);
            repo = new File(fileURI);
            try {
                repo = repo.getCanonicalFile();
            } catch(Exception e) {}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return repo;
    }
    
    public File getDebugAgentJar() {
        try {
            Bundle bundle = Platform.getBundle("com.redhat.ceylon.ide.common");
            if (bundle != null) {
                File debugAgentBundleFile = FileLocator.getBundleFile(bundle);
                if (debugAgentBundleFile != null) {
                    if (debugAgentBundleFile.isDirectory()) {
                        File[] found = debugAgentBundleFile.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return name.startsWith("com.redhat.ceylon.ide.common"+ "-") &&
                                        name.endsWith(".car");
                            }
                        });
                        if (found.length == 1) {
                            return found[0];
                        }
                        
                        found = debugAgentBundleFile.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return pathname.isDirectory() && pathname.getName().equals("META-INF");
                            }
                        });
                        if (found.length == 1) {
                            // The IDE common bundle has been unpacked during installation
                            // jar it back in the eclipse plugin state location.
                            File utilitiesDirectory = 
                                    new File(getStateLocation().toFile(), 
                                            "Utilities");
                            utilitiesDirectory.mkdirs();
                            final File debugAgentFile = new File(utilitiesDirectory, debugAgentBundleFile.getName() + ".car");
                            if (! debugAgentFile.exists()) {
                                ZipFile debugAgentZipFile = new ZipFile(
                                        debugAgentFile);
                                ZipParameters zipParams = new ZipParameters();
                                zipParams.setIncludeRootFolder(false);
                                debugAgentZipFile.createZipFileFromFolder(debugAgentBundleFile, zipParams, false, -1);
                            }
                            return debugAgentFile;
                        }
                        
                    } else {
                        return debugAgentBundleFile;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * - If the property is not empty, return the 
     *   corresponding file
     * <br>
     * - Else return the internal repo folder
     * 
     * @param ceylonRepositoryProperty
     * @return
     * 
     */
    public static File getCeylonPluginRepository(
            String ceylonRepositoryProperty) {
        File ceylonRepository=null;
        if (!"".equals(ceylonRepositoryProperty)) {
            File ceylonRepositoryPath = 
                    new File(ceylonRepositoryProperty);
            if (ceylonRepositoryPath.exists()) {
                ceylonRepository = ceylonRepositoryPath;
            }
        }
        if (ceylonRepository == null) {
            ceylonRepository = getEmbeddedCeylonRepository();
        }
        return ceylonRepository;
    }

    /**
     * Returns the list of jars in the bundled system repo 
     * that are required by the ceylon.language module at 
     * runtime
     */
    public static List<String> getRuntimeRequiredJars(){
        return getRequiredJars(RUNTIME_LIBRARIES);
    }

    /**
     * Returns the list of jars in the bundled system repo 
     * that are required by the ceylon.language module at 
     * compiletime
     */
    public static List<String> getCompiletimeRequiredJars(){
        return getRequiredJars(COMPILETIME_LIBRARIES);
    }

    /**
     * Returns the list of jars required to launch a module 
     */
    public static List<String> getModuleLauncherJars(){
        return getRequiredJars(MODULE_LAUNCHER_LIBRARIES);
    }

    public static List<String> getRequiredJars(String[] libraries) {
        File repoDir = 
                getCeylonPluginRepository(
                        System.getProperty("ceylon.repo", ""));
        try{
            List<String> jars = 
                    new ArrayList<String>
                        (libraries.length);
            for(String jar : libraries){
                File libDir = getRepoFolder(repoDir, jar);
                if( libDir == null) {
                    System.out.println("WARNING lib directory not found for: " + jar);
                } else {
                    if( !libDir.exists() ) {
                        System.out.println("WARNING directory doesn't exist: " + libDir);
                    }
                    String path = 
                            new File(libDir, jar)
                                .getAbsolutePath();
                    jars.add(path);
                }
            }
            return jars;
        } catch (Exception x) {
            x.printStackTrace();
            return Collections.emptyList();
        }
    }
    

    private static Pattern pattern = 
            Pattern.compile("(.+)-(" 
            + Pattern.quote(Versions.CEYLON_VERSION_NUMBER) 
            + ")\\.(j|c)ar");
    
    private static File getRepoFolder(File repoDir, String jarName) {
       Matcher matcher = pattern.matcher(jarName);
       if (matcher.matches()) {
           String name = matcher.group(1);
           String version = matcher.group(2);
           String folderPath = name.replace('.', '/')
                   + "/" + version;
           return new File(repoDir, folderPath);
       }
       return null;
    }
    
    public String getID() {
        return PLUGIN_ID;
    }

    public String getLanguageID() {
        return LANGUAGE_ID;
    }

    private static IPath iconsPath = new Path("icons/");

    public ImageDescriptor image(String file) {
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
        
        if (CeylonPlugin.getPreferences().getBoolean(ALTERNATE_ICONS)) {
            reg.put(CEYLON_OBJECT, image("anonymousClass.png"));
            reg.put(CEYLON_LOCAL_OBJECT, image("anonymousClass.png"));
            reg.put(CEYLON_CONSTRUCTOR, image("classInitializer.png"));
            reg.put(CEYLON_CLASS, image("class.png"));
            reg.put(CEYLON_INTERFACE, image("interface.png"));
            reg.put(CEYLON_LOCAL_CLASS, image("class.png"));
            reg.put(CEYLON_LOCAL_INTERFACE, image("interface.png"));
            reg.put(CEYLON_METHOD, image("method.png"));
            reg.put(CEYLON_LOCAL_METHOD, image("function.png"));
            reg.put(CEYLON_PARAMETER_METHOD, image("methpro_obj.png"));
            reg.put(CEYLON_ATTRIBUTE, image("field.png"));
            reg.put(CEYLON_LOCAL_ATTRIBUTE, image("field.png"));
            reg.put(CEYLON_PARAMETER, image("parameter.png"));
            reg.put(CEYLON_TYPE_PARAMETER, image("variable.png"));
        }
        else {
          reg.put(CEYLON_OBJECT, image("field_public_obj.png"));
          reg.put(CEYLON_LOCAL_OBJECT, image("field_private_obj.png"));
          reg.put(CEYLON_CONSTRUCTOR, image("new_obj.png"));
          reg.put(CEYLON_CLASS, image("class_obj.png"));
          reg.put(CEYLON_INTERFACE, image("int_obj.png"));
          reg.put(CEYLON_LOCAL_CLASS, image("innerclass_private_obj.png"));
          reg.put(CEYLON_LOCAL_INTERFACE, image("innerinterface_private_obj.png"));
          reg.put(CEYLON_METHOD, image("methpub_obj.png"));
          reg.put(CEYLON_PARAMETER_METHOD, image("methpro_obj.png"));
          reg.put(CEYLON_LOCAL_METHOD, image("methpri_obj.png"));
          reg.put(CEYLON_ATTRIBUTE, image("field_public_obj.png"));
          reg.put(CEYLON_LOCAL_ATTRIBUTE, image("field_private_obj.png"));
          reg.put(CEYLON_PARAMETER, image("field_protected_obj.png"));
          reg.put(CEYLON_TYPE_PARAMETER, image("typevariable_obj.png"));
        }
        
        reg.put(CEYLON_ALIAS, image("types.png"));
        reg.put(JAVA_FILE, image("jcu_obj.png"));
        reg.put(JAVA_CLASS_FILE, image("jclassf_obj.png"));
        reg.put(GENERIC_FILE, image("file_obj.png"));
        reg.put(CEYLON_PROJECT, image("prj_obj.png"));
        reg.put(CEYLON_FILE, image("unit_obj.png"));
        reg.put(CEYLON_MODULE_DESC, image("module_desc_obj.png"));
        reg.put(CEYLON_PACKAGE_DESC, image("package_desc_obj.png"));
        reg.put(CEYLON_FOLDER, image("fldr_obj.png"));
        reg.put(CEYLON_SOURCE_FOLDER, image("packagefolder_obj.png"));
        reg.put(CEYLON_MODULE, image("module_obj.png"));
        reg.put(CEYLON_BINARY_ARCHIVE, image("jar_obj.png"));
        reg.put(CEYLON_SOURCE_ARCHIVE, image("jar_lsrc_obj.png"));
        reg.put(CEYLON_PACKAGE, image("package_obj.png"));
        reg.put(CEYLON_IMPORT_LIST, image("impc_obj.png"));
        reg.put(CEYLON_IMPORT, image("imp_obj.png"));
        reg.put(CEYLON_ARGUMENT, image("arg_co.gif"));
        reg.put(CEYLON_DEFAULT_REFINEMENT, image("over_co.png"));
        reg.put(CEYLON_FORMAL_REFINEMENT, image("implm_co.png"));
        reg.put(CEYLON_OPEN_DECLARATION, image("opentype.png"));
        reg.put(CEYLON_SEARCH_RESULTS, image("search_ref_obj.png"));
        reg.put(CEYLON_CORRECTION, image("correction_change.png"));
        reg.put(CEYLON_DELETE_IMPORT, image("correction_delete_import.png"));
        reg.put(CEYLON_SUPPRESS_WARNINGS, image("suppress_warning_obj.gif"));
        reg.put(CEYLON_CHANGE, image("change.png"));
        reg.put(CEYLON_COMPOSITE_CHANGE, image("composite_change.png"));
        reg.put(CEYLON_RENAME, image("correction_rename.png"));
        reg.put(CEYLON_DELETE, image("delete_edit.gif"));
        reg.put(CEYLON_MOVE, image("file_change.png"));
        reg.put(CEYLON_ADD, image("add_obj.png"));
        reg.put(CEYLON_REORDER, image("order_obj.gif"));
        reg.put(CEYLON_REVEAL, image("reveal.gif"));
        reg.put(CEYLON_ADD_CORRECTION, image("add_correction.png"));
        reg.put(CEYLON_REMOVE_CORRECTION, image("remove_correction.png"));
        reg.put(CEYLON_NEW_PROJECT, image("newprj_wiz.png"));
        reg.put(CEYLON_NEW_FILE, image("newfile_wiz.png"));
        reg.put(CEYLON_NEW_MODULE, image("library_wiz.png"));
        reg.put(CEYLON_NEW_PACKAGE, image("newpack_wiz.png"));
        reg.put(CEYLON_NEW_FOLDER, image("newfolder_wiz.png"));
        reg.put(CEYLON_EXPORT_CAR, image("addlibrary_wiz.png"));
        reg.put(CEYLON_EXPORT_JAR, image("jar_pack_wiz.png"));
        reg.put(CEYLON_REFS, image("search_ref_obj.png"));
        reg.put(CEYLON_DECS, image("search_decl_obj.png"));
        reg.put(CEYLON_INHERITED, image("inher_co.png"));
        reg.put(CEYLON_HIER, image("hierarchy_co.png"));
        reg.put(CEYLON_SUP, image("super_co.png"));
        reg.put(CEYLON_SUB, image("sub_co.png"));
        reg.put(CEYLON_OUTLINE, image("outline_co.png"));
        reg.put(CEYLON_HIERARCHY, image("class_hi.png"));
        reg.put(CEYLON_SOURCE, image("source.png"));
        reg.put(ELE32, image("ceylon_icon_32px.png"));
        reg.put(CEYLON_ERR, image("error_co.png"));
        reg.put(CEYLON_WARN, image("warning_co.png"));
        reg.put(GOTO, image("goto_obj.png"));
        reg.put(HIERARCHY, image("class_hi.png"));
        reg.put(SHIFT_LEFT, image("shift_l_edit.png"));
        reg.put(SHIFT_RIGHT, image("shift_r_edit.png"));
        reg.put(QUICK_ASSIST, image("quickassist_obj.png"));
        reg.put(BUILDER, image("builder.png"));
        reg.put(CONFIG_LABELS, image("labels.gif"));
        reg.put(CONFIG_WARNINGS, image("configure_problem_severity.png"));
        reg.put(CONFIG_ANN, image("configure_annotations.png"));
        reg.put(CONFIG_ANN_DIS, image("configure_annotations_d.png"));
        reg.put(MODULE_VERSION, image("module_version.gif"));
        reg.put(HIDE_PRIVATE, image("hideprivate.gif"));
        reg.put(EXPAND_ALL, image("expandall.png"));
        reg.put(PAGING, image("paging.gif"));
        reg.put(SHOW_DOC, image("show_doc.gif"));
        reg.put(SHOW_MEMBERS, image("members.gif"));
        reg.put(REPOSITORIES, image("repositories.png"));
        reg.put(RUNTIME_OBJ, image("repo.png"));
        reg.put(CEYLON_LOCAL_NAME, image("localvariable_obj.png"));
        reg.put(MULTIPLE_TYPES, image("types.png"));
        reg.put(CEYLON_ERROR, image("error_obj.png"));
        reg.put(CEYLON_WARNING, image("warning_obj.png"));
        reg.put(CEYLON_FUN, image("public_fun.gif"));
        reg.put(CEYLON_LOCAL_FUN, image("private_fun.gif"));
        
        reg.put(WARNING_IMAGE, image(WARNING_IMAGE));
        reg.put(ERROR_IMAGE, image(ERROR_IMAGE));
        reg.put(REFINES_IMAGE, image(REFINES_IMAGE));
        reg.put(IMPLEMENTS_IMAGE, image(IMPLEMENTS_IMAGE));
        reg.put(FORMAL_IMAGE, image(FORMAL_IMAGE));
        reg.put(DEFAULT_IMAGE, image(DEFAULT_IMAGE));
        reg.put(FINAL_IMAGE, image(FINAL_IMAGE));
        reg.put(SEALED_IMAGE, image(SEALED_IMAGE));
        reg.put(NATIVE_IMAGE, image(NATIVE_IMAGE));
        reg.put(ABSTRACT_IMAGE, image(ABSTRACT_IMAGE));
        reg.put(VARIABLE_IMAGE, image(VARIABLE_IMAGE));
        reg.put(ANNOTATION_IMAGE, image(ANNOTATION_IMAGE));
        reg.put(ENUM_IMAGE, image(ENUM_IMAGE));
        reg.put(ALIAS_IMAGE, image(ALIAS_IMAGE));
        reg.put(DEPRECATED_IMAGE, image(DEPRECATED_IMAGE));
        reg.put(FOCUS_IMAGE, image(FOCUS_IMAGE));
        reg.put(RUN_IMAGE, image(RUN_IMAGE));
        
        reg.put(PROJECT_MODE, image("prj_mode.png"));
        reg.put(PACKAGE_MODE, image("package_mode.png"));
        reg.put(MODULE_MODE, image("module_mode.png"));
        reg.put(FOLDER_MODE, image("folder_mode.png"));
        reg.put(UNIT_MODE, image("unit_mode.png"));
        reg.put(TYPE_MODE, image("type_mode.png"));
        reg.put(FLAT_MODE, image("flatLayout.png"));
        reg.put(TREE_MODE, image("hierarchicalLayout.png"));
        
        reg.put(TERMINATE_STATEMENT, image("correction_cast.gif"));
        reg.put(FORMAT_BLOCK, image("format_block.gif"));
        reg.put(REMOVE_COMMENT, image("remove_comment_edit.gif"));
        reg.put(ADD_COMMENT, image("comment_edit.png"));
        reg.put(TOGGLE_COMMENT, image("url.gif"));
        reg.put(CORRECT_INDENT, image("correctindent.gif"));
        reg.put(LAST_EDIT, image("last_edit_pos.png"));
        reg.put(NEXT_ANN, image("next_nav.png"));
        reg.put(PREV_ANN, image("prev_nav.png"));
        reg.put(SORT_ALPHA, image("alphab_sort_co.png"));
        
//        reg.put(CEYLON_SEARCH, image("problem_category.gif"));
        reg.put(CEYLON_SEARCH, image("reltopics_co.gif"));
        
        reg.put(HISTORY, image("history_list.png"));
        
        reg.put(CEYLON_LITERAL, image("correction_change.png"));
    }
    
    private void registerProjectOpenCloseListener() {
         getWorkspace()
             .addResourceChangeListener(
                     projectOpenCloseListener, 
                     IResourceChangeEvent.POST_CHANGE);
    }

    private void unregisterProjectOpenCloseListener() {
          getWorkspace()
              .removeResourceChangeListener(
                      projectOpenCloseListener);
    }

    IResourceChangeListener projectOpenCloseListener = 
            new ProjectChangeListener();
    private File javaSourceArchiveCacheDirectory;
//    private IPropertyChangeListener iconChangeListener;
    
    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    /**
     * Utility class that tries to adapt a non null object to the specified type
     * 
     * @param object
     *            the object to adapt
     * @param type
     *            the class to adapt to
     * @return the adapted object
     */

    public static Object adapt(Object object, Class<?> type) {
        if (type.isInstance(object)) {
            return object;
        }
        else if (object instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) object;
            return adaptable.getAdapter(type);
        }
        return Platform.getAdapterManager()
                .getAdapter(object, type);
    }

    public FontRegistry getFontRegistry() {
        // Hopefully this gets called late enough, i.e., after a Display has been
        // created on the current thread (see FontRegistry constructor).
        if (fontRegistry == null) {
            fontRegistry = new FontRegistry();
        }
        return fontRegistry;
    }

    public File getJavaSourceArchiveCacheDirectory() {
        return javaSourceArchiveCacheDirectory;
    }

    public static IPreferenceStore getPreferences() {
        try {
            return getInstance().getPreferenceStore();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static ImageRegistry imageRegistry() {
        return getInstance().getImageRegistry();
    }
    
    public static void log(int severity, String message) {
        Status status =
                new Status(severity, PLUGIN_ID, message);
        getInstance().getLog().log(status);
    }

    public static void log(int severity, String message, Throwable cause) {
        Status status =
            new Status(severity, PLUGIN_ID, message, cause);
        getInstance().getLog().log(status);
    }

    private static Font getFont(final String pref) {
        class GetFont implements Runnable {
            public Font result;
            @Override
            public void run() {
                result =
                        getCurrentTheme()
                            .getFontRegistry()
                            .get(pref);
                if (result==null) {
                    //because I can't trust the ThemeManager
                    result = pref.equals(EDITOR_FONT_PREFERENCE) ?
                            JFaceResources.getTextFont() :
                            JFaceResources.getDialogFont();
                }
            }
        } 
        GetFont gf = new GetFont();
        Display.getDefault().syncExec(gf);
        return gf.result;
    }

    public static Font getHoverFont() {
        return getFont(HOVER_FONT_PREFERENCE);
    }

    public static Font getEditorFont() {
        return getFont(EDITOR_FONT_PREFERENCE);
    }

    public static Font getCompletionFont() {
        return getFont(COMPLETION_FONT_PREFERENCE);
    }

    public static Font getOpenDialogFont() {
        return getFont(OPEN_FONT_PREFERENCE);
    }

    public static Font getOutlineFont() {
        return getFont(OUTLINE_FONT_PREFERENCE);
    }

}

