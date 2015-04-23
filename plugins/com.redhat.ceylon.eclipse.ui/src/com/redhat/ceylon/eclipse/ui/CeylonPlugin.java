package com.redhat.ceylon.eclipse.ui;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.redhat.ceylon.common.FileUtil;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.dist.osgi.Activator;
import com.redhat.ceylon.eclipse.core.builder.ProjectChangeListener;
import com.redhat.ceylon.eclipse.core.debug.CeylonDebugElementAdapterFactory;
import com.redhat.ceylon.eclipse.core.debug.preferences.CeylonDebugOptionsManager;
import com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager;


public class CeylonPlugin extends AbstractUIPlugin implements CeylonResources {

    public static final String PLUGIN_ID = "com.redhat.ceylon.eclipse.ui";
    public static final String DIST_PLUGIN_ID = "com.redhat.ceylon.dist";
    public static final String EMBEDDED_REPO_PLUGIN_ID = "com.redhat.ceylon.dist.repo";
    public static final String LANGUAGE_ID = "ceylon";
    public static final String EDITOR_ID = PLUGIN_ID + ".editor";
    private static final String[] MODULE_LAUNCHER_LIBRARIES = new String[]{ 
        "ceylon.bootstrap-"+Versions.CEYLON_VERSION_NUMBER+".jar" 
    };
    private static final String[] RUNTIME_LIBRARIES = new String[]{
        "ceylon.bootstrap-"+Versions.CEYLON_VERSION_NUMBER+".car",
        "com.redhat.ceylon.compiler.java-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.typechecker-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.module-resolver-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.common-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.model-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "org.jboss.modules-1.3.3.Final.jar",
    };
    private static final String[] COMPILETIME_LIBRARIES = new String[]{
        "com.redhat.ceylon.typechecker-"+Versions.CEYLON_VERSION_NUMBER+".jar",
        "com.redhat.ceylon.model-"+Versions.CEYLON_VERSION_NUMBER+".jar",
    };
    
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
    public void start(BundleContext context) throws Exception {
        javaSourceArchiveCacheDirectory = new File(getStateLocation().toFile(), "JavaSourceArchiveCache");
        javaSourceArchiveCacheDirectory.mkdirs();
        String ceylonRepositoryProperty = System.getProperty("ceylon.repo", "");
        ceylonRepository = getCeylonPluginRepository(ceylonRepositoryProperty);
        super.start(context);
        this.bundleContext = context;
        addResourceFilterPreference();
        registerProjectOpenCloseListener();
        CeylonEncodingSynchronizer.getInstance().install();

        Job registerCeylonModules = new Job("Load the Ceylon Metamodel for plugin dependencies") {
            protected IStatus run(IProgressMonitor monitor) {
                Activator.loadBundleAsModule(bundleContext.getBundle());
                return Status.OK_STATUS;
            };
        };
        registerCeylonModules.setRule(ResourcesPlugin.getWorkspace().getRoot());
        registerCeylonModules.schedule();
        
        Job refreshExternalSourceArchiveManager = new Job("Load the Ceylon Metamodel for plugin dependencies") {
            protected IStatus run(IProgressMonitor monitor) {
                ExternalSourceArchiveManager esam = ExternalSourceArchiveManager.getExternalSourceArchiveManager();
                esam.initialize();
                ResourcesPlugin.getWorkspace().addResourceChangeListener(esam);
                return Status.OK_STATUS;
            };
        };
        refreshExternalSourceArchiveManager.setRule(ResourcesPlugin.getWorkspace().getRoot());
        refreshExternalSourceArchiveManager.schedule();
        
        CeylonDebugOptionsManager.getDefault().startup();
        InputStream contributionStream = new ByteArrayInputStream(new String(
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
        Object key = ((ExtensionRegistry) reg).getTemporaryUserToken();
        IContributor contributor = ContributorFactoryOSGi.createContributor(context.getBundle());
                
        RegistryFactory.getRegistry().addContribution(contributionStream, contributor, false, PLUGIN_ID + ".xmlCatalogContribution", null, key);
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        unregisterProjectOpenCloseListener();
        CeylonEncodingSynchronizer.getInstance().uninstall();
        CeylonDebugElementAdapterFactory.restoreJDTDebugElementAdapters();
        CeylonDebugOptionsManager.getDefault().shutdown();
        FileUtil.deleteQuietly(getJavaSourceArchiveCacheDirectory());
    }

    private void addResourceFilterPreference() throws BackingStoreException {
        new Job("Add Resource Filter for Ceylon projects") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IEclipsePreferences instancePreferences = InstanceScope.INSTANCE
                        .getNode(JavaCore.PLUGIN_ID);
                /*IEclipsePreferences defaultPreferences = DefaultScope.INSTANCE
                        .getNode(JavaCore.PLUGIN_ID);*/
                String filter = instancePreferences.get(CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, "");
                if (filter.isEmpty()) {
                    filter = "*.launch, *.ceylon";
                }
                else if (!filter.contains("*.ceylon")) {
                    filter += ", *.ceylon";
                }
                instancePreferences.put(CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, filter);
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

    
    /**
     * - If the property is not empty, return the corresponding file
     * <br>
     * - Else return the internal repo folder
     * 
     * @param ceylonRepositoryProperty
     * @return
     * 
     */
    public static File getCeylonPluginRepository(String ceylonRepositoryProperty) {
        File ceylonRepository=null;
        if (!"".equals(ceylonRepositoryProperty)) {
            File ceylonRepositoryPath = new File(ceylonRepositoryProperty);
            if (ceylonRepositoryPath.exists()) {
                ceylonRepository = ceylonRepositoryPath;
            }
        }
        if (ceylonRepository == null) {
            try {
                Bundle bundle = Platform.getBundle(EMBEDDED_REPO_PLUGIN_ID);
                IPath path = new Path("repo");
                if (bundle == null) {
                    bundle = Platform.getBundle(DIST_PLUGIN_ID);
                    path = new Path("embeddedRepository").append(path);
                }
                URL eclipseUrl = FileLocator.find(bundle, path, null);
                URL fileURL = FileLocator.resolve(eclipseUrl);
                String urlPath = fileURL.getPath();
                URI fileURI = new URI("file", null, urlPath, null);
                ceylonRepository = new File(fileURI);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ceylonRepository;
    }

    /**
     * Returns the list of jars in the bundled system repo that are required by the ceylon.language module at runtime
     */
    public static List<String> getRuntimeRequiredJars(){
        return getRequiredJars(RUNTIME_LIBRARIES);
    }

    /**
     * Returns the list of jars in the bundled system repo that are required by the ceylon.language module at compiletime
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

    private static List<String> getRequiredJars(String[] libraries){
        File repoDir = getCeylonPluginRepository(System.getProperty("ceylon.repo", ""));
        try{
            List<String> jars = new ArrayList<String>(libraries.length);
            for(String jar : libraries){
                File libDir = new File(repoDir, getRepoFolder(jar));
                if( !libDir.exists() ) {
                    System.out.println("WARNING directory doesn't exist: " + libDir);
                }
                jars.add(new File(libDir, jar).getAbsolutePath());
            }
            return jars;
        } catch (Exception x) {
            x.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    private static String getRepoFolder(String jarName) {   
       int lastDot = jarName.lastIndexOf('.');
       int lastDash = jarName.lastIndexOf('-');
       return jarName.substring(0, lastDash).replace('.', '/')
            + "/" + jarName.substring(lastDash + 1, lastDot);
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
        reg.put(JAVA_FILE, image("jcu_obj.gif"));
        reg.put(GENERIC_FILE, image("file_obj.gif"));
        reg.put(CEYLON_PROJECT, image("prj_obj.gif"));
        reg.put(CEYLON_FILE, image("unit.gif"));
        reg.put(CEYLON_MODULE_DESC, image("m_desc.gif"));
        reg.put(CEYLON_PACKAGE_DESC, image("p_desc.gif"));
        reg.put(CEYLON_FOLDER, image("fldr_obj.gif"));
        reg.put(CEYLON_SOURCE_FOLDER, image("packagefolder_obj.gif"));
        reg.put(CEYLON_MODULE, image("jar_l_obj.gif"));
        reg.put(CEYLON_BINARY_ARCHIVE, image("jar_obj.gif"));
        reg.put(CEYLON_SOURCE_ARCHIVE, image("jar_src_obj.gif"));
        reg.put(CEYLON_PACKAGE, image("package_obj.gif"));
        reg.put(CEYLON_IMPORT_LIST, image("impc_obj.gif"));
        reg.put(CEYLON_IMPORT, image("imp_obj.gif"));
        reg.put(CEYLON_ALIAS, image("types.gif"));
        reg.put(CEYLON_CONSTRUCTOR, image("constructor.gif"));
        reg.put(CEYLON_CLASS, image("class_obj.gif"));
        reg.put(CEYLON_INTERFACE, image("int_obj.gif"));
        reg.put(CEYLON_LOCAL_CLASS, image("innerclass_private_obj.gif"));
        reg.put(CEYLON_LOCAL_INTERFACE, image("innerinterface_private_obj.gif"));
        reg.put(CEYLON_METHOD, image("public_co.gif"));
        reg.put(CEYLON_ATTRIBUTE, image("field_public_obj.gif"));
        reg.put(CEYLON_LOCAL_METHOD, image("private_co.gif"));
        reg.put(CEYLON_LOCAL_ATTRIBUTE, image("field_private_obj.gif"));
        reg.put(CEYLON_PARAMETER_METHOD, image("methpro_obj.gif"));
        reg.put(CEYLON_PARAMETER, image("field_protected_obj.gif"));
        reg.put(CEYLON_TYPE_PARAMETER, image("typevariable_obj.gif"));
        reg.put(CEYLON_ARGUMENT, image("arg_co.gif"));
        reg.put(CEYLON_DEFAULT_REFINEMENT, image("over_co.gif"));
        reg.put(CEYLON_FORMAL_REFINEMENT, image("implm_co.gif"));
        reg.put(CEYLON_OPEN_DECLARATION, image("opentype.gif"));
        reg.put(CEYLON_SEARCH_RESULTS, image("search_ref_obj.gif"));
        reg.put(CEYLON_CORRECTION, image("correction_change.gif"));
        reg.put(CEYLON_DELETE_IMPORT, image("correction_delete_import.gif"));
        reg.put(CEYLON_SUPPRESS_WARNINGS, image("suppress_warning_obj.gif"));
        reg.put(CEYLON_CHANGE, image("change.png"));
        reg.put(CEYLON_COMPOSITE_CHANGE, image("composite_change.png"));
        reg.put(CEYLON_RENAME, image("correction_rename.png"));
        reg.put(CEYLON_DELETE, image("delete_edit.gif"));
        reg.put(CEYLON_MOVE, image("file_change.png"));
        reg.put(CEYLON_ADD, image("add_obj.gif"));
        reg.put(CEYLON_REORDER, image("order_obj.gif"));
        reg.put(CEYLON_REVEAL, image("reveal.gif"));
        reg.put(CEYLON_ADD_CORRECTION, image("add_correction.gif"));
        reg.put(CEYLON_REMOVE_CORRECTION, image("remove_correction.gif"));
        reg.put(CEYLON_NEW_PROJECT, image("newprj_wiz.png"));
        reg.put(CEYLON_NEW_FILE, image("newfile_wiz.png"));
        reg.put(CEYLON_NEW_MODULE, image("addlibrary_wiz.png"));
        reg.put(CEYLON_NEW_PACKAGE, image("newpack_wiz.png"));
        reg.put(CEYLON_NEW_FOLDER, image("newfolder_wiz.gif"));
        reg.put(CEYLON_EXPORT_CAR, image("jar_pack_wiz.png"));
        reg.put(CEYLON_REFS, image("search_ref_obj.png"));
        reg.put(CEYLON_DECS, image("search_decl_obj.png"));
        reg.put(CEYLON_INHERITED, image("inher_co.gif"));
        reg.put(CEYLON_HIER, image("hierarchy_co.gif"));
        reg.put(CEYLON_SUP, image("super_co.gif"));
        reg.put(CEYLON_SUB, image("sub_co.gif"));
        reg.put(CEYLON_OUTLINE, image("outline_co.gif"));
        reg.put(CEYLON_HIERARCHY, image("class_hi.gif"));
        reg.put(CEYLON_SOURCE, image("source.gif"));
        reg.put(ELE32, image("ceylon_icon_32px.png"));
        reg.put(CEYLON_ERR, image("error_co.gif"));
        reg.put(CEYLON_WARN, image("warning_co.gif"));
        reg.put(GOTO, image("goto_obj.gif"));
        reg.put(HIERARCHY, image("class_hi_view.gif"));
        reg.put(SHIFT_LEFT, image("shift_l_edit.gif"));
        reg.put(SHIFT_RIGHT, image("shift_r_edit.gif"));
        reg.put(QUICK_ASSIST, image("quickassist_obj.gif"));
        reg.put(BUILDER, image("builder.gif"));
        reg.put(CONFIG_LABELS, image("labels.gif"));
        reg.put(CONFIG_WARNINGS, image("configure_problem_severity.gif"));
        reg.put(CONFIG_ANN, image("configure_annotations.gif"));
        reg.put(CONFIG_ANN_DIS, image("configure_annotations_disabled.gif"));
        reg.put(MODULE_VERSION, image("module_version.gif"));
        reg.put(HIDE_PRIVATE, image("hideprivate.gif"));
        reg.put(EXPAND_ALL, image("expandall.gif"));
        reg.put(PAGING, image("paging.gif"));
        reg.put(SHOW_DOC, image("show_doc.gif"));
        reg.put(SHOW_MEMBERS, image("members.gif"));
        reg.put(REPOSITORIES, image("repositories.gif"));
        reg.put(RUNTIME_OBJ, image("runtime_obj.gif"));
        reg.put(CEYLON_LOCAL_NAME, image("localvariable_obj.gif"));
        reg.put(MULTIPLE_TYPES, image("types.gif"));
        reg.put(CEYLON_ERROR, image("error_obj.gif"));
        reg.put(CEYLON_WARNING, image("warning_obj.gif"));
        reg.put(CEYLON_FUN, image("public_fun.gif"));
        reg.put(CEYLON_LOCAL_FUN, image("private_fun.gif"));
        
        reg.put(WARNING_IMAGE, image(WARNING_IMAGE));
        reg.put(ERROR_IMAGE, image(ERROR_IMAGE));
        reg.put(REFINES_IMAGE, image(REFINES_IMAGE));
        reg.put(IMPLEMENTS_IMAGE, image(IMPLEMENTS_IMAGE));
        reg.put(FINAL_IMAGE, image(FINAL_IMAGE));
        reg.put(ABSTRACT_IMAGE, image(ABSTRACT_IMAGE));
        reg.put(VARIABLE_IMAGE, image(VARIABLE_IMAGE));
        reg.put(ANNOTATION_IMAGE, image(ANNOTATION_IMAGE));
        reg.put(ENUM_IMAGE, image(ENUM_IMAGE));
        reg.put(ALIAS_IMAGE, image(ALIAS_IMAGE));
        reg.put(DEPRECATED_IMAGE, image(DEPRECATED_IMAGE));
        
        reg.put(PROJECT_MODE, image("prj_mode.gif"));
        reg.put(PACKAGE_MODE, image("package_mode.gif"));
        reg.put(MODULE_MODE, image("module_mode.gif"));
        reg.put(FOLDER_MODE, image("folder_mode.gif"));
        reg.put(UNIT_MODE, image("unit_mode.gif"));
        reg.put(TYPE_MODE, image("type_mode.gif"));
        reg.put(FLAT_MODE, image("flatLayout.gif"));
        reg.put(TREE_MODE, image("hierarchicalLayout.gif"));
        
        reg.put(TERMINATE_STATEMENT, image("correction_cast.gif"));
        reg.put(FORMAT_BLOCK, image("format_block.gif"));
        reg.put(REMOVE_COMMENT, image("remove_comment_edit.gif"));
        reg.put(ADD_COMMENT, image("comment_edit.gif"));
        reg.put(TOGGLE_COMMENT, image("url.gif"));
        reg.put(CORRECT_INDENT, image("correctindent.gif"));
        reg.put(LAST_EDIT, image("last_edit_pos.gif"));
        reg.put(NEXT_ANN, image("next_nav.gif"));
        reg.put(PREV_ANN, image("prev_nav.gif"));
        reg.put(SORT_ALPHA, image("alphab_sort_co.gif"));
        
        reg.put(HISTORY, image("history.gif"));
        
        reg.put(CEYLON_LITERAL, image("correction_change.gif"));
    }
    
    private void registerProjectOpenCloseListener() {
         getWorkspace().addResourceChangeListener(projectOpenCloseListener, 
                IResourceChangeEvent.POST_CHANGE);
    }

    private void unregisterProjectOpenCloseListener() {
          getWorkspace().removeResourceChangeListener(projectOpenCloseListener);
    }

    IResourceChangeListener projectOpenCloseListener = new ProjectChangeListener();
    private File javaSourceArchiveCacheDirectory;
    
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
        } else if (object instanceof IAdaptable) {
            return ((IAdaptable) object).getAdapter(type);
        }
        return Platform.getAdapterManager().getAdapter(object, type);
    }

    public FontRegistry getFontRegistry() {
        // Hopefully this gets called late enough, i.e., after a Display has been
        // created on the current thread (see FontRegistry constructor).
        if (fontRegistry == null) {
            fontRegistry= new FontRegistry();
        }
        return fontRegistry;
    }

    public File getJavaSourceArchiveCacheDirectory() {
        return javaSourceArchiveCacheDirectory;
    }
}

