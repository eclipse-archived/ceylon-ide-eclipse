package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;
import static com.redhat.ceylon.compiler.java.util.Util.getModuleArchiveName;
import static com.redhat.ceylon.compiler.java.util.Util.getModulePath;
import static com.redhat.ceylon.compiler.java.util.Util.getSourceArchiveName;
import static com.redhat.ceylon.compiler.typechecker.model.Module.LANGUAGE_MODULE_NAME;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathContainers;
import static com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager.getExternalSourceArchiveManager;
import static com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager.getExternalSourceArchives;
import static com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile.createResourceVirtualFile;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.core.runtime.SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.internal.events.NotificationManager;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IBuildContext;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.redhat.ceylon.cmr.api.ArtifactCallback;
import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.ArtifactCreator;
import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.ceylon.CeylonUtils;
import com.redhat.ceylon.cmr.impl.ShaSigner;
import com.redhat.ceylon.common.Constants;
import com.redhat.ceylon.common.log.Logger;
import com.redhat.ceylon.compiler.Options;
import com.redhat.ceylon.compiler.java.codegen.CeylonCompilationUnit;
import com.redhat.ceylon.compiler.java.codegen.CeylonFileObject;
import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.loader.TypeFactory;
import com.redhat.ceylon.compiler.java.loader.UnknownTypeCollector;
import com.redhat.ceylon.compiler.java.loader.mirror.JavacClass;
import com.redhat.ceylon.compiler.java.tools.CeylonLog;
import com.redhat.ceylon.compiler.java.tools.CeyloncFileManager;
import com.redhat.ceylon.compiler.java.tools.CeyloncTaskImpl;
import com.redhat.ceylon.compiler.java.tools.JarEntryFileObject;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler;
import com.redhat.ceylon.compiler.java.util.RepositoryLister;
import com.redhat.ceylon.compiler.js.JsCompiler;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.ModelLoaderFactory;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleValidator;
import com.redhat.ceylon.compiler.typechecker.analyzer.Warning;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.context.ProducedTypeCache;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Util;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.UnexpectedError;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.compiler.typechecker.util.WarningSuppressionVisitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil;
import com.redhat.ceylon.eclipse.core.classpath.CeylonLanguageModuleContainer;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;
import com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.ICeylonModelListener;
import com.redhat.ceylon.eclipse.core.model.IJavaModelAware;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.model.JDTModuleManager;
import com.redhat.ceylon.eclipse.core.model.JavaCompilationUnit;
import com.redhat.ceylon.eclipse.core.model.JavaUnit;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.eclipse.core.model.SourceFile;
import com.redhat.ceylon.eclipse.core.model.mirror.JDTClass;
import com.redhat.ceylon.eclipse.core.model.mirror.SourceClass;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.ui.ceylon.model.delta.CompilationUnitDelta;
import com.redhat.ceylon.eclipse.util.CarUtils;
import com.redhat.ceylon.eclipse.util.CeylonSourceParser;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskEvent.Kind;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.file.RegularFileObject;
import com.sun.tools.javac.file.RelativePath.RelativeFile;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

/**
 * A builder may be activated on a file containing ceylon code every time it has
 * changed (when "Build automatically" is on), or when the programmer chooses to
 * "Build" a project.
 * 
 * TODO This default implementation was generated from a template, it needs to
 * be completed manually.
 */
public class CeylonBuilder extends IncrementalProjectBuilder {

    public static final String CEYLON_CLASSES_FOLDER_NAME = ".exploded";

    /**
     * Extension ID of the Ceylon builder, which matches the ID in the
     * corresponding extension definition in plugin.xml.
     */
    public static final String BUILDER_ID = PLUGIN_ID + ".ceylonBuilder";

    /**
     * A marker ID that identifies problems
     */
    public static final String PROBLEM_MARKER_ID = PLUGIN_ID + ".ceylonProblem";

    /**
     * A marker ID that identifies module dependency problems
     */
    public static final String MODULE_DEPENDENCY_PROBLEM_MARKER_ID = PLUGIN_ID + ".ceylonModuleDependencyProblem";

    /**
     * A marker ID that identifies character encoding problems
     */
    public static final String CHARSET_PROBLEM_MARKER_ID = PLUGIN_ID + ".ceylonCharsetProblem";

    /**
     * A marker ID that identifies character encoding problems
     */
    public static final String CEYLON_CONFIG_NOT_IN_SYNC_MARKER = PLUGIN_ID + ".ceylonConfigProblem";
        
    /**
     * A marker ID that identifies tasks
     */
    public static final String TASK_MARKER_ID = PLUGIN_ID + ".ceylonTask";
    
    public static final String SOURCE = "Ceylon";
    
    static {
        ProducedTypeCache.setEnabledByDefault(false);
    }
    
    public static <T> T doWithCeylonModelCaching(final Callable<T> action) 
            throws CoreException {
        boolean was = ProducedTypeCache.setEnabled(true);
        try {
            return action.call();
        } catch(CoreException ce) {
            throw ce;
        } catch(Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            ProducedTypeCache.setEnabled(was);
        }
    }
    
    private static final class BuildFileManager extends CeyloncFileManager {
        private final IProject project;
        final boolean explodeModules;
        private Map<RegularFileObject, Set<String>> inputFilesToGenerate = null;
        
        private BuildFileManager(com.sun.tools.javac.util.Context context,
                boolean register, Charset charset, IProject project, Map<RegularFileObject, Set<String>> inputFilesToGenerate) {
            super(context, register, charset);
            this.project = project;
            explodeModules = isExplodeModulesEnabled(project);
            this.inputFilesToGenerate = inputFilesToGenerate;
        }

        public static RegularFileObject getSourceFile(FileObject fileObject) {
            JavaFileObject sourceJavaFileObject;
            if (fileObject instanceof JavaFileObject
                    && ((JavaFileObject) fileObject).getKind() == javax.tools.JavaFileObject.Kind.SOURCE){
                if (fileObject instanceof CeylonFileObject) {
                    sourceJavaFileObject = ((CeylonFileObject) fileObject).getFile();
                } else {
                    sourceJavaFileObject = (JavaFileObject) fileObject;
                }
                if (sourceJavaFileObject instanceof RegularFileObject) {
                    return ((RegularFileObject) sourceJavaFileObject);
                }
            }
            return null;
        }
        
        @Override
        protected JavaFileObject getFileForOutput(Location location,
                final RelativeFile fileName, FileObject sibling)
                throws IOException {
            RegularFileObject sourceFile = getSourceFile(sibling);
            if (sourceFile != null) {
                Set<String> expectedClasses = inputFilesToGenerate.get(sourceFile);
                String shortname = fileName.basename();
                if (shortname.endsWith(".class")) {
                    shortname = shortname.substring(0, shortname.length() - 6);
                }
                expectedClasses.remove(shortname);
                if (expectedClasses.isEmpty()) {
                    inputFilesToGenerate.remove(sourceFile);
                }
            }
            JavaFileObject javaFileObject = super.getFileForOutput(location, fileName, sibling);
            if (explodeModules && 
                    javaFileObject instanceof JarEntryFileObject && 
                    sibling instanceof CeylonFileObject) {
                final File ceylonOutputDirectory = getCeylonClassesOutputDirectory(project);
                final File classFile = fileName.getFile(ceylonOutputDirectory);
                classFile.getParentFile().mkdirs();
                return new ExplodingJavaFileObject(classFile, fileName,
                        javaFileObject);
            }
            return javaFileObject;
        }

        @Override
        protected String getCurrentWorkingDir() {
            return project.getLocation().toFile().getAbsolutePath();
        }
        
        public void addUngeneratedErrors() {
            if (inputFilesToGenerate.size() > 0) {
                try {
                    String markerId = PROBLEM_MARKER_ID + ".backend";
                    String message = "Some classes are missing from the generated module archives, probably because of an error in the Java backend compilation.\n"
                            + "The detail of missing classes is given in the Information markers.";
                    IMarker marker = project.createMarker(markerId);
                    marker.setAttribute(IMarker.MESSAGE, message);
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                    marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                }
                catch (CoreException ce) {
                    ce.printStackTrace();
                }
            }
            for (RegularFileObject sourceFileNotGenerated : inputFilesToGenerate.keySet()) {
                IPath absolutePath = new Path(sourceFileNotGenerated.getName());
                IFile file = null;
                for (IFolder sourceDirectory : CeylonBuilder.getSourceFolders(project)) {
                    IPath sourceDirPath = sourceDirectory.getLocation();
                    if (sourceDirPath.isPrefixOf(absolutePath)) {
                        IResource r = sourceDirectory.findMember(absolutePath.makeRelativeTo(sourceDirPath));
                        if (r instanceof IFile) {
                            file = (IFile) r;
                        }
                    }
                }
                if (file == null) {
                    file = getWorkspace().getRoot()
                            .getFileForLocation(new Path(sourceFileNotGenerated.getName()));
                }
                if (file != null) {
                    try {
                        String markerId = PROBLEM_MARKER_ID + ".backend";
                        String message = "The following classes were not generated by the backend :";
                        Iterator<String> classes = inputFilesToGenerate.get(sourceFileNotGenerated).iterator();
                        String line = "";
                        if (classes.hasNext()) {
                            line += "\n    " + classes.next();
                        }
                        while (classes.hasNext()) {
                            if (line.length() > 70) {
                                message += line;
                                line = "\n    ";
                            } else {
                                line += ", ";
                            }
                            line += classes.next();
                        }
                        if (! line.trim().isEmpty()) {
                            message += line;
                        }
                        IMarker marker = file.createMarker(markerId);
                        marker.setAttribute(IMarker.MESSAGE, message);
                        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                    }
                    catch (CoreException ce) {
                        ce.printStackTrace();
                    }
                }
            }
        }
    }

    public static enum ModelState {
        Missing,
        Parsing,
        Parsed,
        TypeChecking,
        TypeChecked,
        Compiled
    };
    
    final static Map<IProject, ModelState> modelStates = new HashMap<IProject, ModelState>();
    private final static Map<IProject, TypeChecker> typeCheckers = new HashMap<IProject, TypeChecker>();
    private final static Map<IProject, List<IFile>> projectFiles = new HashMap<IProject, List<IFile>>();
    private static Set<IProject> containersInitialized = new HashSet<IProject>();
    private final static Map<IProject, RepositoryManager> projectRepositoryManagers = new HashMap<IProject, RepositoryManager>();
    private final static Map<IProject, ModuleDependencies> projectModuleDependencies = new HashMap<IProject, ModuleDependencies>();
    private final static Set<ICeylonModelListener> modelListeners = new LinkedHashSet<ICeylonModelListener>();

    public static void addModelListener(ICeylonModelListener listener) {
        modelListeners.add(listener);
    }
    
    public static void removeModelListener(ICeylonModelListener listener) {
        modelListeners.remove(listener);
    }

    public static final String CEYLON_CONSOLE= "Ceylon Build";
    //private long startTime;

    public static ModelState getModelState(IProject project) {
        ModelState modelState = modelStates.get(project);
        if (modelState == null) {
            return ModelState.Missing;
        }
        return modelState;
    }
    
    public static boolean isModelTypeChecked(IProject project) {
        ModelState modelState = getModelState(project);
        return modelState.ordinal() >= ModelState.TypeChecked.ordinal();
    }
    
    public static boolean isModelParsed(IProject project) {
        ModelState modelState = getModelState(project);
        return modelState.ordinal() >= ModelState.Parsed.ordinal();
    }

    public static List<PhasedUnit> getUnits(IProject project) {
        if (! isModelParsed(project)) {
            return Collections.emptyList();
        }
        List<PhasedUnit> result = new ArrayList<PhasedUnit>();
        TypeChecker tc = typeCheckers.get(project);
        if (tc!=null) {
            for (PhasedUnit pu: tc.getPhasedUnits().getPhasedUnits()) {
                result.add(pu);
            }
        }
        return result;
    }

    public static List<PhasedUnit> getUnits() {
        List<PhasedUnit> result = new ArrayList<PhasedUnit>();
        for (IProject project : typeCheckers.keySet()) {
            if (isModelParsed(project)) {
                TypeChecker tc = typeCheckers.get(project);
                for (PhasedUnit pu: tc.getPhasedUnits().getPhasedUnits()) {
                    result.add(pu);
                }
            }
        }
        return result;
    }

    public static List<PhasedUnit> getUnits(String[] projects) {
        List<PhasedUnit> result = new ArrayList<PhasedUnit>();
        if (projects!=null) {
            for (Map.Entry<IProject, TypeChecker> me: typeCheckers.entrySet()) {
                for (String pname: projects) {
                    if (me.getKey().getName().equals(pname)) {
                        IProject project = me.getKey();
                        if (isModelParsed(project)) {
                            result.addAll(me.getValue().getPhasedUnits().getPhasedUnits());
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public String getBuilderID() {
        return BUILDER_ID;
    }
    
    public static boolean isCeylon(IFile file) {
        String ext = file.getFileExtension();
        return ext!=null && ext.equals("ceylon");
    }

    public static boolean isJava(IFile file) {
        return JavaCore.isJavaLikeFileName(file.getName());
    }

    public static boolean isJavascript(IFile file) {
        String ext = file.getFileExtension();
        return ext!=null && ext.equals("js");
    }

    /*
     * A source file is compilable and located in a Ceylon source folder
     */
    public static boolean isSourceFile(IFile file) {
        // If the file is not in a ceylon source folder
        // it's not considered as a source file
        // even if it is compilable
        return isCompilable(file) && isInSourceFolder(file);
    }

    public static boolean isCompilable(IFile file) {
        if (isCeylon(file)) {
            return true;
        }
        if (isJava(file) && compileToJava(file.getProject())) {
            return true;
        }
        if (isJavascript(file) && compileToJs(file.getProject())) {
            return true;
        }
        return false;
    }
    
    public static boolean isResourceFile(IFile file) {
        RootFolderType rootFolderType = getRootFolderType(file);
        return rootFolderType == RootFolderType.RESOURCE;
    }

    public static JDTModelLoader getModelLoader(TypeChecker tc) {
        return (JDTModelLoader) ((JDTModuleManager) tc.getPhasedUnits()
                .getModuleManager()).getModelLoader();
    }

    public static JDTModelLoader getProjectModelLoader(IProject project) {
        TypeChecker typeChecker = getProjectTypeChecker(project);
        if (typeChecker == null) {
            return null;
        }
        return getModelLoader(typeChecker);
    }

    public static JDTModuleManager getProjectModuleManager(IProject project) {
        JDTModelLoader modelLoader = getProjectModelLoader(project);
        if (modelLoader == null) {
            return null;
        }
        return modelLoader.getModuleManager();
    }

    public final static class BooleanHolder {
        public boolean value;
    }

    public static class CeylonBuildHook {
        protected void startBuild(int kind, @SuppressWarnings("rawtypes") Map args, 
                IProject javaProject, IBuildConfiguration config, IBuildContext context, IProgressMonitor monitor) throws CoreException {}
        protected void deltasAnalyzed(List<IResourceDelta> currentDeltas,
                BooleanHolder sourceModified, BooleanHolder mustDoFullBuild,
                BooleanHolder mustResolveClasspathContainer, boolean mustContinueBuild) {}
        protected void resolvingClasspathContainer(
                List<IClasspathContainer> cpContainers) {}
        protected void setAndRefreshClasspathContainer() {}
        protected void doFullBuild() {}
        protected void parseCeylonModel() {}
        protected void doIncrementalBuild() {}
        protected void fullTypeCheckDuringIncrementalBuild() {}
        protected void incrementalBuildChangedSources(Set<IFile> changedSources) {}
        protected void incrementalBuildSources(Set<IFile> changedSources,
                List<IFile> filesToRemove, Collection<IFile> sourcesToCompile) {}
        protected void incrementalBuildResult(List<PhasedUnit> builtPhasedUnits) {}
        protected void beforeGeneratingBinaries() {}
        protected void afterGeneratingBinaries() {}
        protected void scheduleReentrantBuild() {}
        protected void afterReentrantBuild() {}
        protected void endBuild() {}
    };
    
    public static final CeylonBuildHook noOpHook = new CeylonBuildHook();

    public static enum RootFolderType {
        SOURCE,
        RESOURCE
    }
    
    public static final QualifiedName RESOURCE_PROPERTY_PACKAGE_MODEL = new QualifiedName(CeylonPlugin.PLUGIN_ID, "resourceProperty_packageModel");
    public static final QualifiedName RESOURCE_PROPERTY_ROOT_FOLDER = new QualifiedName(CeylonPlugin.PLUGIN_ID, "resourceProperty_rootFolder"); 
    public static final QualifiedName RESOURCE_PROPERTY_ROOT_FOLDER_TYPE = new QualifiedName(CeylonPlugin.PLUGIN_ID, "resourceProperty_rootFolderType"); 
    
    private static CeylonBuildHook buildHook = new CeylonBuildHook() {
        List<CeylonBuildHook> contributedHooks = new LinkedList<>();

        private synchronized void resetContributedHooks() {
            contributedHooks.clear();
            for (IConfigurationElement confElement : Platform.getExtensionRegistry().getConfigurationElementsFor(CeylonPlugin.PLUGIN_ID + ".ceylonBuildHook")) {
                try {
                    Object extension = confElement.createExecutableExtension("class");
                    if (extension instanceof ICeylonBuildHookProvider) {
                        CeylonBuildHook hook = ((ICeylonBuildHookProvider) extension).getHook();
                        if (hook != null) {
                            contributedHooks.add(hook);
                        }
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        
        protected void startBuild(int kind, @SuppressWarnings("rawtypes") Map args, 
                IProject javaProject, IBuildConfiguration config, IBuildContext context, IProgressMonitor monitor) throws CoreException {
            resetContributedHooks();
            for (CeylonBuildHook hook : contributedHooks) {
                hook.startBuild(kind, args, javaProject, config, context, monitor);
            }
        }
        protected void deltasAnalyzed(List<IResourceDelta> currentDeltas,
                BooleanHolder sourceModified, BooleanHolder mustDoFullBuild,
                BooleanHolder mustResolveClasspathContainer, boolean mustContinueBuild) {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.deltasAnalyzed(currentDeltas, sourceModified, mustDoFullBuild, mustResolveClasspathContainer, mustContinueBuild);
            }
        }
        protected void resolvingClasspathContainer(
                List<IClasspathContainer> cpContainers) {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.resolvingClasspathContainer(cpContainers);
            }
        }
        protected void setAndRefreshClasspathContainer() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.setAndRefreshClasspathContainer();
            }
        }
        protected void doFullBuild() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.doFullBuild();
            }
        }
        protected void parseCeylonModel() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.parseCeylonModel();
            }
        }
        protected void doIncrementalBuild() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.doIncrementalBuild();
            }
        }
        protected void fullTypeCheckDuringIncrementalBuild() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.fullTypeCheckDuringIncrementalBuild();
            }
        }
        protected void incrementalBuildChangedSources(Set<IFile> changedSources) {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.incrementalBuildChangedSources(changedSources);
            }
        }
        protected void incrementalBuildSources(Set<IFile> changedSources,
                List<IFile> filesToRemove, Collection<IFile> sourcesToCompile) {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.incrementalBuildSources(changedSources, filesToRemove, sourcesToCompile);
            }
        }
        protected void incrementalBuildResult(List<PhasedUnit> builtPhasedUnits) {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.incrementalBuildResult(builtPhasedUnits);
            }
        }
        protected void beforeGeneratingBinaries() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.beforeGeneratingBinaries();
            }
        }
        protected void afterGeneratingBinaries() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.afterGeneratingBinaries();
            }
        }
        protected void scheduleReentrantBuild() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.beforeGeneratingBinaries();
            }
        }
        protected void afterReentrantBuild() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.afterReentrantBuild();
            }
        }
        protected void endBuild() {
            for (CeylonBuildHook hook : contributedHooks) {
                hook.endBuild();
            }
        }
    };
    
    public static CeylonBuildHook replaceHook(CeylonBuildHook hook){
        CeylonBuildHook previousHook = buildHook;
        buildHook = hook;
        return previousHook;
    }

    private static WeakReference<Job> notificationJobReference = null;
    private static synchronized Job getNotificationJob() {
        Job job = null;
        if (notificationJobReference != null) {
            job = notificationJobReference.get();
        }
        
        if (job == null) {
            for (Job j : Job.getJobManager().find(null)) {
                if (NotificationManager.class.equals(j.getClass().getEnclosingClass())) {
                    job = j;
                    notificationJobReference = new WeakReference<Job>(job);
                    break;
                }
            }
        }
        return job;
    }
    
    public static void waitForUpToDateJavaModel(long timeout, IProject project, IProgressMonitor monitor) {
        Job job = getNotificationJob();
        if (job == null) {
            return;
        }
        
        monitor.subTask("Taking in account the resource changes of the previous builds" + project != null ? project.getName() : "");
        long timeLimit = System.currentTimeMillis() + timeout;
        while (job.getState() != Job.NONE) {
            boolean stopWaiting = false;
            if (job.isBlocking()) {
                stopWaiting = true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                stopWaiting = true;
            }
            if (System.currentTimeMillis() > timeLimit) {
                stopWaiting = true;
            }
            if (stopWaiting) {
                break;
            }
        }
    }
    
    @Override
    protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor mon) 
            throws CoreException {
        final IProject project = getProject();
        final IJavaProject javaProject = JavaCore.create(project);
        final SubMonitor monitor = SubMonitor.convert(mon, "Ceylon build of project " + project.getName(), 100);
        try {
            buildHook.startBuild(kind, args, project, getBuildConfig(), getContext(), monitor);
        } catch (CoreException e) {
            if (e.getStatus().getSeverity() == IStatus.CANCEL) {
                return project.getReferencedProjects();
            }
        }

        try {
            IMarker[] buildMarkers = project.findMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, DEPTH_ZERO);
            for (IMarker m: buildMarkers) {
                Object message = m.getAttribute(IMarker.MESSAGE);
                Object sourceId = m.getAttribute(IMarker.SOURCE_ID);
                if (message!=null && message.toString().endsWith("'.exploded'")) {
                    //ignore message from JDT about missing JDTClasses dir
                    m.delete();
                }
                if (sourceId!=null && sourceId.equals(PLUGIN_ID)) {
                    // Delete markers added by this builder since they will be added again just after.
                    m.delete();
                }
                else if (message!=null && message.toString().contains("is missing required Java project:")) {
                    return project.getReferencedProjects();
                }
            }
            
            List<IClasspathContainer> cpContainers = getCeylonClasspathContainers(javaProject);
            
            if (! preBuildChecks(project, javaProject, cpContainers)) {
                return project.getReferencedProjects();
            }
            
            List<PhasedUnit> builtPhasedUnits = Collections.emptyList();
            
            final BooleanHolder mustDoFullBuild = new BooleanHolder();
            final BooleanHolder mustResolveClasspathContainer = new BooleanHolder();
            final IResourceDelta currentDelta = getDelta(project);
            List<IResourceDelta> projectDeltas = new ArrayList<IResourceDelta>();
            projectDeltas.add(currentDelta);
            for (IProject requiredProject : project.getReferencedProjects()) {
                projectDeltas.add(getDelta(requiredProject));
            }
            
            boolean somethingToDo = chooseBuildTypeFromDeltas(kind, project,
                    projectDeltas, mustDoFullBuild, mustResolveClasspathContainer);
            
            if (!somethingToDo && (args==null || !args.containsKey(BUILDER_ID + ".reentrant"))) {
                return project.getReferencedProjects();
            }
            
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            
            if (mustResolveClasspathContainer.value) {
                if (cpContainers != null) {
                    buildHook.resolvingClasspathContainer(cpContainers);
                    for (IClasspathContainer container: cpContainers) {
                        if (container instanceof CeylonProjectModulesContainer) {
                            CeylonProjectModulesContainer applicationModulesContainer = (CeylonProjectModulesContainer) container;
                            boolean changed = applicationModulesContainer.resolveClasspath(monitor.newChild(19, PREPEND_MAIN_LABEL_TO_SUBTASK), true);
                            if(changed) {
                                buildHook.setAndRefreshClasspathContainer();
                                JavaCore.setClasspathContainer(applicationModulesContainer.getPath(), 
                                        new IJavaProject[]{javaProject}, 
                                        new IClasspathContainer[]{null} , monitor);
                                applicationModulesContainer.refreshClasspathContainer(monitor);
                            }
                        }
                    }
                }
            }
            
            boolean mustWarmupCompletionProcessor = false;
        
            final TypeChecker typeChecker;
            Collection<IFile> filesForBinaryGeneration = Collections.emptyList();

            if (mustDoFullBuild.value) {
                buildHook.doFullBuild();
                monitor.setTaskName("Full Ceylon build of project " + project.getName());
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                
                cleanupModules(monitor, project);
                cleanupJdtClasses(monitor, project);
                
                monitor.subTask("Clearing existing markers of project " + project.getName());
                clearProjectMarkers(project, true, false);
                clearMarkersOn(project, true);
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                //if (! getModelState(project).equals(ModelState.Parsed)) {
                if (!mustResolveClasspathContainer.value) {
                    monitor.subTask("Parsing source of project " + project.getName());
                    //if we already resolved the classpath, the
                    //model has already been freshly-parsed
                    buildHook.parseCeylonModel();
                    typeChecker = parseCeylonModel(project, 
                            monitor.newChild(19, PREPEND_MAIN_LABEL_TO_SUBTASK));
                }
                else {
                    typeChecker = getProjectTypeChecker(project);
                }
                
                monitor.setWorkRemaining(80);
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Typechecking all source  files of project " + project.getName());
                modelStates.put(project, ModelState.TypeChecking);
                builtPhasedUnits = doWithCeylonModelCaching(new Callable<List<PhasedUnit>>() {
                    @Override
                    public List<PhasedUnit> call() throws Exception {
                        return fullTypeCheck(project, typeChecker, 
                                monitor.newChild(30, PREPEND_MAIN_LABEL_TO_SUBTASK ));
                    }
                });
                modelStates.put(project, ModelState.TypeChecked);
                
                filesForBinaryGeneration = getProjectFiles(project);
                
                mustWarmupCompletionProcessor = true;
            }
            else
            {
                buildHook.doIncrementalBuild();
                typeChecker = typeCheckers.get(project);
                PhasedUnits phasedUnits = typeChecker.getPhasedUnits();

                
                List<IFile> filesToRemove = new ArrayList<IFile>();
                Set<IFile> changedFiles = new HashSet<IFile>(); 

                monitor.subTask("Scanning deltas of project " + project.getName()); 
                scanChanges(currentDelta, projectDeltas, filesToRemove, 
                        getProjectFiles(project), changedFiles, monitor);
                monitor.worked(4);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                    
                monitor.subTask("Cleaning removed files for project " + project.getName());
                cleanRemovedFilesFromCeylonModel(filesToRemove, phasedUnits, project);
                cleanRemovedFilesFromOutputs(filesToRemove, project);
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                if (!isModelTypeChecked(project)) {
                    buildHook.fullTypeCheckDuringIncrementalBuild();
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    monitor.subTask("Clearing existing markers of project (except backend errors)" + project.getName());
                    clearProjectMarkers(project, true, false);
                    clearMarkersOn(project, false);
                    monitor.worked(1);

                    monitor.subTask("Initial typechecking all source files of project " + project.getName());
                    modelStates.put(project, ModelState.TypeChecking);
                    builtPhasedUnits = doWithCeylonModelCaching(new Callable<List<PhasedUnit>>() {
                        @Override
                        public List<PhasedUnit> call() throws Exception {
                            return fullTypeCheck(project, typeChecker, 
                                    monitor.newChild(22, PREPEND_MAIN_LABEL_TO_SUBTASK ));
                        }
                    });
                    modelStates.put(project, ModelState.TypeChecked);

                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                    
                    monitor.subTask("Collecting dependencies of project " + project.getName());
//                  getConsoleStream().println(timedMessage("Collecting dependencies"));
                    collectDependencies(project, typeChecker, builtPhasedUnits);
                    monitor.worked(1);
                    
                    monitor.subTask("Collecting problems for project " 
                            + project.getName());
                    addProblemAndTaskMarkers(builtPhasedUnits, project);
                    monitor.worked(1);
                    
                    mustWarmupCompletionProcessor = true;
                }
                
                monitor.setWorkRemaining(70);
                monitor.subTask("Incremental Ceylon build of project " + project.getName());

                monitor.subTask("Scanning dependencies of deltas of project " + project.getName()); 
                final Set<IFile> filesToCompile = new HashSet<>();
                final Set<IFile> filesToTypecheck = new HashSet<>();
                       
                calculateDependencies(project, currentDelta, 
                        changedFiles, typeChecker, phasedUnits, filesToTypecheck, filesToCompile, monitor);
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                buildHook.incrementalBuildSources(changedFiles, filesToRemove, filesToTypecheck);
                
                clearProjectMarkers(project, true, false);
                clearMarkersOn(filesToTypecheck, true);
                clearMarkersOn(filesToCompile, true, true);

                monitor.subTask("Compiling " + filesToTypecheck.size() + " source files in project " + 
                        project.getName());
                builtPhasedUnits = doWithCeylonModelCaching(new Callable<List<PhasedUnit>>() {
                    @Override
                    public List<PhasedUnit> call() throws Exception {
                        return incrementalBuild(project, filesToTypecheck, 
                                monitor.newChild(19, PREPEND_MAIN_LABEL_TO_SUBTASK));
                    }
                });
                
                if (builtPhasedUnits.isEmpty() && filesToTypecheck.isEmpty() && filesToCompile.isEmpty()) {
                    
                    if (mustWarmupCompletionProcessor) {
                        warmupCompletionProcessor(project);
                    }

                    return project.getReferencedProjects();
                }
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                buildHook.incrementalBuildResult(builtPhasedUnits);

                filesForBinaryGeneration = filesToCompile;
            
            }
            
            clearProjectMarkers(project, false, true);

            monitor.setWorkRemaining(50);
            
            monitor.subTask("Collecting problems for project " 
                    + project.getName());
            addProblemAndTaskMarkers(builtPhasedUnits, project);
            monitor.worked(1);

            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            monitor.subTask("Collecting dependencies of project " + project.getName());
//            getConsoleStream().println(timedMessage("Collecting dependencies"));
            collectDependencies(project, typeChecker, builtPhasedUnits);
            monitor.worked(4);
    
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            buildHook.beforeGeneratingBinaries();
            monitor.subTask("Generating binaries for project " + project.getName());
            
            final Collection<IFile> filesToProcess = filesForBinaryGeneration;
            final Collection<PhasedUnit> unitsTypecheckedIncrementally = mustDoFullBuild.value ? Collections.<PhasedUnit>emptyList() : builtPhasedUnits;
            cleanChangedFilesFromExplodedDirectory(filesToProcess, project);
            doWithCeylonModelCaching(new Callable<Boolean>() {
                @Override
                public Boolean call() throws CoreException {
                    return generateBinaries(project, javaProject, unitsTypecheckedIncrementally,
                            filesToProcess, typeChecker, 
                            monitor.newChild(45, PREPEND_MAIN_LABEL_TO_SUBTASK));
                }
            });
            buildHook.afterGeneratingBinaries();
          
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            if (isExplodeModulesEnabled(project)) {
                monitor.subTask("Rebuilding using exploded modules directory of " + project.getName());
                sheduleIncrementalRebuild(args, project, monitor);
            }
                        
            if (mustWarmupCompletionProcessor) {
                warmupCompletionProcessor(project);
            }
            
            return project.getReferencedProjects();
        }
        finally {
            monitor.done();
            buildHook.endBuild();
        }
    }

    /*
     * Checks for global build error and add the relevant markers.
     * 
     * Returns true if the build can continue, and false if the build should stop.
     * 
     */
    private boolean preBuildChecks(final IProject project,
            final IJavaProject javaProject,
            List<IClasspathContainer> cpContainers) throws CoreException,
            JavaModelException {
        boolean languageModuleContainerFound = false;
        boolean applicationModulesContainerFound = false;
        
        boolean buildCanContinue = true;
        
        for (IClasspathContainer container : cpContainers) {
            if (container instanceof CeylonLanguageModuleContainer) {
                languageModuleContainerFound = true;
            }
            if (container instanceof CeylonProjectModulesContainer) {
                applicationModulesContainerFound = true;
            }
        }
        if (! languageModuleContainerFound) {
            //if the ClassPathContainer is missing, add an error
            IMarker marker = project.createMarker(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER);
            marker.setAttribute(IMarker.MESSAGE, "The Ceylon classpath container for the language module is not set on the project " + 
                    " (try running Enable Ceylon Builder on the project)");
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.LOCATION, project.getName());
            marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
            buildCanContinue = false;
        }
        if (! applicationModulesContainerFound) {
            //if the ClassPathContainer is missing, add an error
            IMarker marker = project.createMarker(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER);
            marker.setAttribute(IMarker.MESSAGE, "The Ceylon classpath container for application modules is not set on the project " + 
                    " (try running Enable Ceylon Builder on the project)");
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.LOCATION, project.getName());
            marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
            buildCanContinue = false;
        }
        
        /* Begin issue #471 */
        ICommand[] builders = project.getDescription().getBuildSpec();
        int javaOrder=0, ceylonOrder = 0;
        for (int n=0; n<builders.length; n++) {
            if (builders[n].getBuilderName().equals(JavaCore.BUILDER_ID)) {
                javaOrder = n;
            }
            else if (builders[n].getBuilderName().equals(CeylonBuilder.BUILDER_ID)) {
                ceylonOrder = n;
            }
        }
        if (ceylonOrder < javaOrder) {
            //if the build order is not correct, add an error and return
            IMarker marker = project.createMarker(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER);
            marker.setAttribute(IMarker.MESSAGE, "The Ceylon Builder should run after the Java Builder. Change the order of builders in the project properties");
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.LOCATION, "Project " + project.getName());
            marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
            buildCanContinue = false;
        }
        /* End issue #471 */
        
        boolean sourceDirectoryInProjectFolder = false;
        boolean outputDirectoryInProjectFolder = javaProject.getOutputLocation().equals(javaProject.getPath());
        
        for (IPackageFragmentRoot root : javaProject.getAllPackageFragmentRoots()) {
            if (root.getRawClasspathEntry().getEntryKind() == IClasspathEntry.CPE_SOURCE
                    && root.getResource().getLocation().equals(project.getLocation())) {
                sourceDirectoryInProjectFolder = true;
                break;
            }
        }
        
        if (sourceDirectoryInProjectFolder || outputDirectoryInProjectFolder) {
            if (sourceDirectoryInProjectFolder) {
                IMarker marker = project.createMarker(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER);
                marker.setAttribute(IMarker.MESSAGE, "One source directory is the root folder of the project, which is not supported for Ceylon projects." + 
                        " Change it in the project properties");
                marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.LOCATION, "Project " + project.getName());
                marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
            }
            if (outputDirectoryInProjectFolder) {
                IMarker marker = project.createMarker(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER);
                marker.setAttribute(IMarker.MESSAGE, "The project Java class directory is the root folder of the project, which is not supported for Ceylon projects." + 
                        " Change it in the project properties");
                marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.LOCATION, "Project " + project.getName());
                marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
            }
            buildCanContinue = false;
        }
        
        IPath modulesOutputFolderPath = getCeylonModulesOutputFolder(project).getRawLocation();
        IPath jdtOutputFolderPath = javaProject.getOutputLocation();
        IFolder jdtOutputFolder = project.getWorkspace().getRoot().getFolder(jdtOutputFolderPath);
        if (jdtOutputFolder.exists()) {
            jdtOutputFolderPath = jdtOutputFolder.getRawLocation();
        }
        if (modulesOutputFolderPath.isPrefixOf(jdtOutputFolderPath) || jdtOutputFolderPath.isPrefixOf(modulesOutputFolderPath)) {
            //if the build order is not correct, add an error and return
            IMarker marker = project.createMarker(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER);
            marker.setAttribute(IMarker.MESSAGE, "The Ceylon modules output directory and Java class directory shoudln't collide." + 
                    " Change one of them in the project properties");
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.LOCATION, "Project " + project.getName());
            marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
            buildCanContinue = false;
        }
        
        if (! isInSyncWithCeylonConfig(project)) {
            //if the build order is not correct, add an error and return
            IMarker marker = project.createMarker(CEYLON_CONFIG_NOT_IN_SYNC_MARKER);
            marker.setAttribute(IMarker.MESSAGE, "The Ceylon Build Paths are not in sync with those in the ceylon configuration file (" 
                                                    + "./ceylon/config)\n"
                                                    + "Either modify this file or change the build paths accordingly in the project properties");
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.LOCATION, project.getName());
            marker.setAttribute(IMarker.SOURCE_ID, PLUGIN_ID);
            buildCanContinue = false;
        }

        if (project.findMarkers(CHARSET_PROBLEM_MARKER_ID, false, IResource.DEPTH_ZERO).length > 0) {
            buildCanContinue = false;
        }

        return buildCanContinue;
    }

    public static boolean isInSyncWithCeylonConfig(final IProject project) {
        Set<String> sourceFoldersFromCeylonConfig = new TreeSet<String>();
        Set<String> sourceFoldersFromEclipseProject = new TreeSet<String>();
        Set<String> resourceFoldersFromCeylonConfig = new TreeSet<String>();
        Set<String> resourceFoldersFromEclipseProject = new TreeSet<String>();
        CeylonProjectConfig ceylonConfig = CeylonProjectConfig.get(project);
        for (String path : ceylonConfig.getProjectSourceDirectories()) {
            sourceFoldersFromCeylonConfig.add(Path.fromOSString(path).toString());
        }
        for (String path : ceylonConfig.getProjectResourceDirectories()) {
            resourceFoldersFromCeylonConfig.add(Path.fromOSString(path).toString());
        }
        for (IFolder folder : getSourceFolders(project)) {
            IPath path = folder.isLinked() ? folder.getLocation() : folder.getProjectRelativePath();
            sourceFoldersFromEclipseProject.add(path.toString());
        }
        for (IFolder folder : getResourceFolders(project)) {
            IPath path = folder.isLinked() ? folder.getLocation() : folder.getProjectRelativePath();
            resourceFoldersFromEclipseProject.add(path.toString());
        }
        if (sourceFoldersFromEclipseProject.isEmpty()) {
            sourceFoldersFromEclipseProject.add(Constants.DEFAULT_SOURCE_DIR);
        }
        if (resourceFoldersFromEclipseProject.isEmpty()) {
            resourceFoldersFromEclipseProject.add(Constants.DEFAULT_RESOURCE_DIR);
        }
        return sourceFoldersFromCeylonConfig.equals(sourceFoldersFromEclipseProject) &&
                resourceFoldersFromCeylonConfig.equals(resourceFoldersFromEclipseProject);
    }
    
    private void warmupCompletionProcessor(final IProject project) {
        /*Job job = new WarmupJob(project);
        job.setPriority(Job.BUILD);
        //job.setSystem(true);
        job.setRule(project.getWorkspace().getRoot());
        job.schedule();*/
    }

    private void sheduleIncrementalRebuild(@SuppressWarnings("rawtypes") Map args, final IProject project, 
            IProgressMonitor monitor) {
        try {
            getCeylonClassesOutputFolder(project).refreshLocal(DEPTH_INFINITE, monitor);
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }//monitor);
        if (args==null || !args.containsKey(BUILDER_ID + ".reentrant")) {
            buildHook.scheduleReentrantBuild();
            final CeylonBuildHook currentBuildHook = buildHook;
            Job job = new Job("Rebuild with Ceylon classes") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        //we have already done a build of both the Java and Ceylon classes
                        //so now go back and try to build the both the Java and Ceylon
                        //classes again, using the classes we previously generated - this
                        //is to allow references from Java to Ceylon
                        project.build(INCREMENTAL_BUILD, JavaCore.BUILDER_ID, null, monitor);
                        Map<String,String> map = new HashMap<String,String>();
                        map.put(BUILDER_ID + ".reentrant", "true");
                        project.build(INCREMENTAL_BUILD, BUILDER_ID, map, monitor);
                        currentBuildHook.afterReentrantBuild();
                    } 
                    catch (CoreException e) {
                        e.printStackTrace();
                    }
                    return Status.OK_STATUS;
                }
            };
            job.setRule(project.getWorkspace().getRoot());
            job.schedule();
        }
    }

    private void collectDependencies(IProject project, TypeChecker typeChecker,
            List<PhasedUnit> builtPhasedUnits) throws CoreException {
        for (PhasedUnit pu : builtPhasedUnits) {
            new UnitDependencyVisitor(pu).visit(pu.getCompilationUnit());
        }
    }

    private void cleanRemovedFilesFromCeylonModel(Collection<IFile> filesToRemove,
            PhasedUnits phasedUnits, IProject project) {
        for (IFile fileToRemove: filesToRemove) {
            if(isCeylon(fileToRemove)) {
                // Remove the ceylon phasedUnit (which will also remove the unit from the package)
                PhasedUnit phasedUnitToDelete = phasedUnits.getPhasedUnit(createResourceVirtualFile(fileToRemove));
                if (phasedUnitToDelete != null) {
                    assert(phasedUnitToDelete instanceof ProjectPhasedUnit);
                    ((ProjectPhasedUnit) phasedUnitToDelete).remove();
                }
            }
            else if (isJava(fileToRemove)) {
                // Remove the external unit from the package
                Package pkg = getPackage(fileToRemove);
                if (pkg != null) {
                    for (Unit unitToTest: pkg.getUnits()) {
                        if (unitToTest.getFilename().equals(fileToRemove.getName())) {
                            assert(unitToTest instanceof JavaUnit);
                            JavaUnit javaUnit = (JavaUnit) unitToTest;
                            javaUnit.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void calculateDependencies(IProject project,
            IResourceDelta currentDelta,
            Collection<IFile> changedFiles, TypeChecker typeChecker, 
            PhasedUnits phasedUnits, Set<IFile> filesToTypeCheck, Set<IFile> filesToCompile, IProgressMonitor monitor) {

        Set<IFile> filesToAddInTypecheck = new HashSet<IFile>();
        Set<IFile> filesToAddInCompile = new HashSet<IFile>();

        if (!changedFiles.isEmpty()) {
            Set<IFile> allTransitivelyDependingFiles = searchForDependantFiles(
                    project, changedFiles, typeChecker, monitor,
                    false);

            Set<IFile> dependingFilesAccordingToStructureDelta;
            boolean astAwareIncrementalBuild = areAstAwareIncrementalBuildsEnabled(project);
            if (astAwareIncrementalBuild) {
                dependingFilesAccordingToStructureDelta = searchForDependantFiles(
                        project, changedFiles, typeChecker, monitor,
                        true);
            } else {
                dependingFilesAccordingToStructureDelta = allTransitivelyDependingFiles;
            }

            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            
            for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                Unit unit = phasedUnit.getUnit();
                if (!unit.getUnresolvedReferences().isEmpty()) {
                    IFile fileToAdd = ((IFileVirtualFile)(phasedUnit.getUnitFile())).getFile();
                    if (fileToAdd.exists()) {
                        filesToAddInTypecheck.add(fileToAdd);
                        filesToAddInCompile.add(fileToAdd);
                    }
                }
                Set<Declaration> duplicateDeclarations = unit.getDuplicateDeclarations();
                if (!duplicateDeclarations.isEmpty()) {
                    IFile fileToAdd = ((IFileVirtualFile)(phasedUnit.getUnitFile())).getFile();
                    if (fileToAdd.exists()) {
                        filesToAddInTypecheck.add(fileToAdd);
                        filesToAddInCompile.add(fileToAdd);
                    }
                    for (Declaration duplicateDeclaration : duplicateDeclarations) {
                        Unit duplicateUnit = duplicateDeclaration.getUnit();
                        if ((duplicateUnit instanceof SourceFile) && 
                            (duplicateUnit instanceof IResourceAware)) {
                            IFile duplicateDeclFile = ((IResourceAware) duplicateUnit).getFileResource();
                            if (duplicateDeclFile != null && duplicateDeclFile.exists()) {
                                filesToAddInTypecheck.add(duplicateDeclFile);
                                filesToAddInCompile.add(duplicateDeclFile);
                            }
                        }
                    }
                }
            }
            
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
    
            for (IFile f: allTransitivelyDependingFiles) {
                if (f.getProject() == project) {
                    if (isSourceFile(f) || isResourceFile(f)) {
                        if (f.exists()) {
                            filesToAddInTypecheck.add(f);
                            if (!astAwareIncrementalBuild || dependingFilesAccordingToStructureDelta.contains(f)) {
                                filesToAddInCompile.add(f);
                            }
                        }
                        else {
                            // If the file is moved : add a dependency on the new file
                            if (currentDelta != null) {
                                IResourceDelta removedFile = currentDelta.findMember(f.getProjectRelativePath());
                                if (removedFile != null && 
                                        (removedFile.getFlags() & IResourceDelta.MOVED_TO) != 0 &&
                                        removedFile.getMovedToPath() != null) {
                                    IFile movedFile = project.getFile(removedFile.getMovedToPath().removeFirstSegments(1));
                                    if (isSourceFile(movedFile) || isResourceFile(movedFile)) {
                                        filesToAddInTypecheck.add(movedFile);
                                        if (!astAwareIncrementalBuild || dependingFilesAccordingToStructureDelta.contains(movedFile)) {
                                            filesToAddInCompile.add(movedFile);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        for (IFile file : getProjectFiles(project)) {
            try {
                if (file.findMarkers(PROBLEM_MARKER_ID + ".backend", false, IResource.DEPTH_ZERO).length > 0) {
                    filesToAddInCompile.add(file);
                }
            } catch (CoreException e) {
                e.printStackTrace();
                filesToAddInCompile.add(file);
            }
        }

        filesToTypeCheck.addAll(filesToAddInTypecheck);
        filesToCompile.addAll(filesToAddInCompile);
    }

    private Set<IFile> searchForDependantFiles(IProject project,
            Collection<IFile> changedFiles, TypeChecker typeChecker,
            IProgressMonitor monitor, boolean filterAccordingToStructureDelta) {
        Set<IFile> changeDependents= new HashSet<IFile>();
        Set<IFile> analyzedFiles= new HashSet<IFile>();
        changeDependents.addAll(changedFiles);
      
        boolean changed = false;
        do {
            Collection<IFile> additions= new HashSet<IFile>();
            for (Iterator<IFile> iter=changeDependents.iterator(); iter.hasNext();) {
                final IFile srcFile= iter.next();
                if (analyzedFiles.contains(srcFile)) {
                    continue;
                }
                analyzedFiles.add(srcFile);
                IProject currentFileProject = srcFile.getProject();
                TypeChecker currentFileTypeChecker = null;
                if (currentFileProject == project) {
                    currentFileTypeChecker = typeChecker;
                } 
                else {
                    currentFileTypeChecker = getProjectTypeChecker(currentFileProject);
                }
                
                if (! CeylonBuilder.isInSourceFolder(srcFile)) {
                    // Don't search dependencies inside resource folders.
                    continue;
                }
                
                if (filterAccordingToStructureDelta) {
                    IResourceAware unit = getUnit(srcFile);
                    if (unit instanceof ProjectSourceFile) {
                        ProjectSourceFile projectSourceFile = (ProjectSourceFile) unit;
                        if (projectSourceFile.getDependentsOf().size() > 0) {
                            CompilationUnitDelta delta = projectSourceFile.buildDeltaAgainstModel();
                            if (delta != null 
                                    && delta.getChanges().getSize() == 0
                                    && delta.getChildrenDeltas().getSize() == 0) {
                                    continue;
                                }
                        }
                    }
                }
                
                Set<String> filesDependingOn = getDependentsOf(srcFile,
                        currentFileTypeChecker, currentFileProject);
   
                for (String dependingFile: filesDependingOn) {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                    
                    //TODO: note that the following is slightly
                    //      fragile - it depends on the format 
                    //      of the path that we use to track
                    //      dependents!
                    IPath pathRelativeToProject = new Path(dependingFile);
                            //.makeRelativeTo(project.getLocation());
                    IFile depFile= (IFile) project.findMember(pathRelativeToProject);
                    if (depFile == null) {
                        depFile= (IFile) currentFileProject.findMember(dependingFile);
                    }
                    if (depFile != null) {
                        additions.add(depFile);
                    }
                    else {
                        System.err.println("could not resolve dependent unit: " + 
                                    dependingFile);
                    }
                }
            }
            changed = changeDependents.addAll(additions);
        } while (changed && !filterAccordingToStructureDelta);
        return changeDependents;
    }

    private void scanChanges(final IResourceDelta currentDelta, 
            List<IResourceDelta> projectDeltas, final List<IFile> filesToRemove, 
            final List<IFile> currentProjectSources, final Set<IFile> changedSources, IProgressMonitor monitor) 
                    throws CoreException {
        for (final IResourceDelta projectDelta: projectDeltas) {
            if (projectDelta != null) {
                final IProject project = (IProject) projectDelta.getResource();
                for (IResourceDelta projectAffectedChild: projectDelta.getAffectedChildren()) {
                    if (! (projectAffectedChild.getResource() instanceof IFolder)) {
                        continue;
                    }
                    final IFolder rootFolder = (IFolder) projectAffectedChild.getResource();

                    RootFolderType rootFolderType = getRootFolderType(rootFolder);
                    final boolean inSourceDirectory = rootFolderType == RootFolderType.SOURCE;
                    final boolean inResourceDirectory = rootFolderType == RootFolderType.RESOURCE;
                    
                    if (inResourceDirectory || inSourceDirectory) {
                        // a real Ceylon source or resource folder so scan for changes
                        projectAffectedChild.accept(new IResourceDeltaVisitor() {
                            public boolean visit(IResourceDelta delta) throws CoreException {
                                IResource resource = delta.getResource();
                                if (resource instanceof IFile) {
                                    IFile file= (IFile) resource;
                                    if (inResourceDirectory || (isCompilable(file) && inSourceDirectory) ) {
                                        changedSources.add(file);
                                        if (projectDelta == currentDelta) {
                                            if (delta.getKind() == IResourceDelta.REMOVED) {
                                                filesToRemove.add(file);
                                                currentProjectSources.remove(file);
                                            }
                                            if (delta.getKind() == IResourceDelta.ADDED) {
                                                IFile addedFile = (IFile) resource;
                                                int index = currentProjectSources.indexOf(addedFile);
                                                if ((index >= 0)) {
                                                    currentProjectSources.remove(index);
                                                }
                                                currentProjectSources.add(addedFile);
                                            }
                                        }
                                    }
                                    return false;
                                }
                                if (resource instanceof IFolder) {
                                    IFolder folder= (IFolder) resource;
                                    if (projectDelta == currentDelta) {
                                        if (folder.exists() && delta.getKind() != IResourceDelta.REMOVED) {
                                            if (getPackage(folder) == null || getRootFolder(folder) == null) {
                                                IContainer parent = folder.getParent();
                                                if (parent instanceof IFolder) {
                                                    Package parentPkg = getPackage((IFolder)parent);
                                                    if (parentPkg != null) {
                                                        Package pkg = getProjectModelLoader(project).findOrCreatePackage(parentPkg.getModule(), parentPkg.getNameAsString() + "." + folder.getName());
                                                        resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_PACKAGE_MODEL, new WeakReference<Package>(pkg));
                                                        resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_ROOT_FOLDER, rootFolder);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                return true;
                            }
                        });
                    }
                }
            }
        }
    }

    public boolean chooseBuildTypeFromDeltas(final int kind, final IProject project,
            final List<IResourceDelta> currentDeltas,
            final BooleanHolder mustDoFullBuild,
            final BooleanHolder mustResolveClasspathContainer) {
        
        mustDoFullBuild.value = kind == FULL_BUILD || kind == CLEAN_BUILD || 
                !isModelParsed(project);
        mustResolveClasspathContainer.value = kind==FULL_BUILD; //false;
        final BooleanHolder somethingToBuild = new BooleanHolder();
        
        if (JavaProjectStateMirror.hasClasspathChanged(project)) {
            mustDoFullBuild.value = true;
        }
        if (!mustDoFullBuild.value || !mustResolveClasspathContainer.value) {
            for (IResourceDelta currentDelta: currentDeltas) {
                if (currentDelta != null) {
                    try {
                        currentDelta.accept(new DeltaScanner(mustDoFullBuild, project,
                                somethingToBuild, mustResolveClasspathContainer));
                    } 
                    catch (CoreException e) {
                        e.printStackTrace();
                        mustDoFullBuild.value = true;
                        mustResolveClasspathContainer.value = true;
                    }
                } 
                else {
                    mustDoFullBuild.value = true;
                    mustResolveClasspathContainer.value = true;
                }
            }
        }

        class DecisionMaker {
            public boolean mustContinueBuild() {
                return mustDoFullBuild.value || 
                        mustResolveClasspathContainer.value ||
                        somethingToBuild.value ||
                        ! isModelTypeChecked(project);
            }
        }; DecisionMaker decisionMaker = new DecisionMaker(); 
        
        buildHook.deltasAnalyzed(currentDeltas, somethingToBuild, mustDoFullBuild, mustResolveClasspathContainer, decisionMaker.mustContinueBuild());

        return decisionMaker.mustContinueBuild();
    }

//    private static String successMessage(boolean binariesGenerationOK) {
//        return "             " + (binariesGenerationOK ? 
//                "...binary generation succeeded" : "...binary generation FAILED");
//    }

    private Set<String> getDependentsOf(IFile srcFile,
            TypeChecker currentFileTypeChecker,
            IProject currentFileProject) {
        
        if (isCeylon(srcFile)) {
            PhasedUnit phasedUnit = currentFileTypeChecker.getPhasedUnits()
                    .getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(srcFile));
            if (phasedUnit != null && phasedUnit.getUnit() != null) {
                return phasedUnit.getUnit().getDependentsOf();
            }
        } 
        else {
            Unit unit = getJavaUnit(getProject(), srcFile);
            if (unit instanceof JavaCompilationUnit) {
                return unit.getDependentsOf();
            }
        }
        
        return Collections.emptySet();
    }

    static ProjectPhasedUnit parseFileToPhasedUnit(final ModuleManager moduleManager, final TypeChecker typeChecker,
            final ResourceVirtualFile file, final ResourceVirtualFile srcDir,
            final Package pkg) {
        return new CeylonSourceParser<ProjectPhasedUnit>() {
            
            @Override
            protected String getCharset() {
                try {
                    return file.getResource().getProject().getDefaultCharset();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            
            @SuppressWarnings("unchecked")
            @Override
            protected ProjectPhasedUnit createPhasedUnit(CompilationUnit cu, Package pkg, CommonTokenStream tokenStream) {
                return new ProjectPhasedUnit(file, srcDir, cu, pkg, 
                        moduleManager, typeChecker, tokenStream.getTokens());
            }
        }.parseFileToPhasedUnit(moduleManager, typeChecker, file, srcDir, pkg);
    }

    private List<PhasedUnit> incrementalBuild(IProject project, Collection<IFile> sourceToCompile,
            IProgressMonitor mon) {
        
        SubMonitor monitor = SubMonitor.convert(mon,
                "Typechecking " + sourceToCompile.size() + " source files in project " + 
                project.getName(), sourceToCompile.size()*6); 

        TypeChecker typeChecker = typeCheckers.get(project);
        PhasedUnits pus = typeChecker.getPhasedUnits();
        JDTModuleManager moduleManager = (JDTModuleManager) pus.getModuleManager(); 
        JDTModelLoader modelLoader = getModelLoader(typeChecker);
        
        // First refresh the modules that are cross-project references to sources modules
        // in referenced projects. This will :
        // - clean the binary declarations and reload the class-to-source mapping file for binary-based modules,
        // - remove old PhasedUnits and parse new or updated PhasedUnits from the source archive for source-based modules
        
        for (Module m : typeChecker.getContext().getModules().getListOfModules()) {
            if (m instanceof JDTModule) {
                JDTModule module = (JDTModule) m;
                if (module.isCeylonArchive()) {
                    module.refresh();
                }
            }
        }
        
        // Secondly typecheck again the changed PhasedUnits in changed external source modules
        // (those which come from referenced projects)
        List<PhasedUnits> phasedUnitsOfDependencies = typeChecker.getPhasedUnitsOfDependencies();
        List<PhasedUnit> dependencies = new ArrayList<PhasedUnit>();
        for (PhasedUnits phasedUnits: phasedUnitsOfDependencies) {
            for (PhasedUnit phasedUnit: phasedUnits.getPhasedUnits()) {
                dependencies.add(phasedUnit);
            }
        }
        for (PhasedUnit pu: dependencies) {
            monitor.subTask("- scanning declarations " + pu.getUnit().getFilename());
            pu.scanDeclarations();
            monitor.worked(1);
        }
        for (PhasedUnit pu: dependencies) {
            monitor.subTask("- scanning type declarations " + pu.getUnit().getFilename());
            pu.scanTypeDeclarations();
            monitor.worked(2);
        }
        for (PhasedUnit pu: dependencies) {
            pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
        }
        for (PhasedUnit pu: dependencies) {
            pu.analyseTypes(); // Needed to have the right values in the Value.trans field (set in Expression visitor)
                                // which in turn is important for debugging !
        }
        
        // Then typecheck the changed source of this project
        
        Set<String> cleanedPackages = new HashSet<String>();
        
        List<PhasedUnit> phasedUnitsToUpdate = new ArrayList<PhasedUnit>();
        
        for (IFile fileToUpdate : sourceToCompile) {
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            // skip non-ceylon files
            if(!isCeylon(fileToUpdate)) {
                if (isJava(fileToUpdate)) {
                    Unit toRemove = getJavaUnit(project, fileToUpdate);
                    if(toRemove instanceof JavaUnit) {
                        ((JavaUnit) toRemove).remove();
                    }
                    else {
                        String packageName = getPackageName(fileToUpdate);
                        if (! cleanedPackages.contains(packageName)) {
                            modelLoader.clearCachesOnPackage(packageName);
                            cleanedPackages.add(packageName);
                        }
                    }                    
                }
                continue;
            }
            
            ResourceVirtualFile file = ResourceVirtualFile.createResourceVirtualFile(fileToUpdate);
            IFolder srcFolder = getRootFolder(fileToUpdate);

            ProjectPhasedUnit alreadyBuiltPhasedUnit = (ProjectPhasedUnit) pus.getPhasedUnit(file);

            Package pkg = null;
            if (alreadyBuiltPhasedUnit!=null) {
                // Editing an already built file
                pkg = alreadyBuiltPhasedUnit.getPackage();
            }
            else {
                IFolder packageFolder = (IFolder) file.getResource().getParent();
                pkg = getPackage(packageFolder);
            }
            if (srcFolder == null || pkg == null) {
                continue;
            }
            ResourceVirtualFile srcDir = new IFolderVirtualFile(project, srcFolder.getProjectRelativePath());
            PhasedUnit newPhasedUnit = parseFileToPhasedUnit(moduleManager, typeChecker, file, srcDir, pkg);
            phasedUnitsToUpdate.add(newPhasedUnit);
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        if (phasedUnitsToUpdate.size() == 0) {
            return phasedUnitsToUpdate;
        }
        
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            assert(phasedUnit instanceof ProjectPhasedUnit);
            ((ProjectPhasedUnit)phasedUnit).install();
        }
        
        modelLoader.setupSourceFileObjects(phasedUnitsToUpdate);
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            if (! phasedUnit.isDeclarationsScanned()) {
                phasedUnit.validateTree();
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.visitSrcModulePhase();
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.visitRemainingModulePhase();
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            if (! phasedUnit.isDeclarationsScanned()) {
                monitor.subTask("- scanning declarations " + phasedUnit.getUnit().getFilename());
                phasedUnit.scanDeclarations();
            }
            monitor.worked(1);
        }

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            if (! phasedUnit.isTypeDeclarationsScanned()) {
                monitor.subTask("- scanning type declarations " + phasedUnit.getUnit().getFilename());
                phasedUnit.scanTypeDeclarations();
            }
            monitor.worked(2);
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            if (! phasedUnit.isRefinementValidated()) {
                phasedUnit.validateRefinement();
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            if (! phasedUnit.isFullyTyped()) {
                monitor.subTask("- typechecking " + phasedUnit.getUnit().getFilename());
                phasedUnit.analyseTypes();
                if (showWarnings(project)) {
                    phasedUnit.analyseUsage();
                }
                monitor.worked(3);
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.analyseFlow();
        }

        UnknownTypeCollector utc = new UnknownTypeCollector();
        for (PhasedUnit pu : phasedUnitsToUpdate) { 
            pu.getCompilationUnit().visit(utc);
        }
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        monitor.done();

        return phasedUnitsToUpdate;
    }

    private Unit getJavaUnit(IProject project, IFile fileToUpdate) {
        IJavaElement javaElement = (IJavaElement) fileToUpdate.getAdapter(IJavaElement.class);
        if (javaElement instanceof ICompilationUnit) {
            ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
            IJavaElement packageFragment = compilationUnit.getParent();
            JDTModelLoader projectModelLoader = getProjectModelLoader(project);
            // TODO : Why not use the Model Loader cache to get the declaration 
            //      instead of iterating through all the packages ?
            if (projectModelLoader != null) {
                Package pkg = projectModelLoader.findPackage(packageFragment.getElementName());
                if (pkg != null) {
                    for (Declaration decl : pkg.getMembers()) {
                        Unit unit = decl.getUnit();
                        if (unit.getFilename().equals(fileToUpdate.getName())) {
                            return unit;
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<PhasedUnit> fullTypeCheck(IProject project, 
            TypeChecker typeChecker, IProgressMonitor mon) 
                    throws CoreException {

        List<PhasedUnits> phasedUnitsOfDependencies = typeChecker.getPhasedUnitsOfDependencies();

        List<PhasedUnit> dependencies = new ArrayList<PhasedUnit>();
 
        for (PhasedUnits phasedUnits: phasedUnitsOfDependencies) {
            for (PhasedUnit phasedUnit: phasedUnits.getPhasedUnits()) {
                dependencies.add(phasedUnit);
            }
        }
        
        final List<PhasedUnit> listOfUnits = typeChecker.getPhasedUnits().getPhasedUnits();

        SubMonitor monitor = SubMonitor.convert(mon,
                "Typechecking " + listOfUnits.size() + " source files of project " + 
                project.getName(), dependencies.size()*5+listOfUnits.size()*6);
        
        monitor.subTask("- typechecking source archives for project " 
                + project.getName());

        JDTModelLoader loader = getModelLoader(typeChecker);
//        loader.reset();
        
        for (PhasedUnit pu: dependencies) {
            monitor.subTask("- scanning declarations " + pu.getUnit().getFilename());
            pu.scanDeclarations();
            monitor.worked(1);
        }
        for (PhasedUnit pu: dependencies) {
            monitor.subTask("- scanning type declarations " + pu.getUnit().getFilename());
            pu.scanTypeDeclarations();
            monitor.worked(2);
        }
                
        for (PhasedUnit pu: dependencies) {
            pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
        }

        for (PhasedUnit pu: dependencies) {
            pu.analyseTypes(); // Needed to have the right values in the Value.trans field (set in Expression visitor)
                                // which in turn is important for debugging !
        }

        Module languageModule = loader.getLanguageModule();
        loader.loadPackage(languageModule, "com.redhat.ceylon.compiler.java.metadata", true);
        loader.loadPackage(languageModule, LANGUAGE_MODULE_NAME, true);
        loader.loadPackage(languageModule, "ceylon.language.descriptor", true);
        loader.loadPackageDescriptors();
        
        monitor.subTask("(typechecking source files for project " 
                + project.getName() +")");

        for (PhasedUnit pu : listOfUnits) {
            if (! pu.isDeclarationsScanned()) {
                monitor.subTask("- scanning declarations " + pu.getUnit().getFilename());
                pu.validateTree();
                pu.scanDeclarations();
                monitor.worked(1);
            }
        }
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit pu : listOfUnits) {
            if (! pu.isTypeDeclarationsScanned()) {
                monitor.subTask("- scanning types " + pu.getUnit().getFilename());
                pu.scanTypeDeclarations();
                monitor.worked(2);
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit pu: listOfUnits) {
            if (! pu.isRefinementValidated()) {
                pu.validateRefinement();
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }

        for (PhasedUnit pu : listOfUnits) {
            if (! pu.isFullyTyped()) {
                monitor.subTask("- typechecking " + pu.getUnit().getFilename());
                pu.analyseTypes();
                if (showWarnings(project)) {
                    pu.analyseUsage();
                }
                monitor.worked(3);
            }
        }
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit pu: listOfUnits) {
            pu.analyseFlow();
        }

        UnknownTypeCollector utc = new UnknownTypeCollector();
        for (PhasedUnit pu : listOfUnits) { 
            pu.getCompilationUnit().visit(utc);
        }

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        projectModuleDependencies.get(project).addModulesWithDependencies(typeChecker.getContext().getModules().getListOfModules());

        monitor.done();
        
        return typeChecker.getPhasedUnits().getPhasedUnits();
    }

    public static TypeChecker parseCeylonModel(final IProject project,
            final IProgressMonitor mon) throws CoreException {
        return doWithCeylonModelCaching(new Callable<TypeChecker>() {
            @Override
            public TypeChecker call() throws CoreException {
                SubMonitor monitor = SubMonitor.convert(mon,
                        "Setting up typechecker for project " + project.getName(), 113);

                modelStates.put(project, ModelState.Parsing);
                typeCheckers.remove(project);
                projectRepositoryManagers.remove(project);
                projectFiles.remove(project);
                if (projectModuleDependencies.containsKey(project)) {
                    projectModuleDependencies.get(project).reset();
                } else {
                    projectModuleDependencies.put(project, new ModuleDependencies());
                }

                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                
                final IJavaProject javaProject = JavaCore.create(project);
                TypeChecker typeChecker = buildTypeChecker(project, javaProject);
                PhasedUnits phasedUnits = typeChecker.getPhasedUnits();

                JDTModuleManager moduleManager = (JDTModuleManager) phasedUnits.getModuleManager();
                moduleManager.setTypeChecker(typeChecker);
                Context context = typeChecker.getContext();
                JDTModelLoader modelLoader = (JDTModelLoader) moduleManager.getModelLoader();
                Module defaultModule = context.getModules().getDefaultModule();

                monitor.worked(1);
                
                monitor.subTask("- parsing source files for project " 
                            + project.getName());

                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                
                phasedUnits.getModuleManager().prepareForTypeChecking();
                
                List<IFile> scannedFiles = scanFiles(project, javaProject, 
                        typeChecker, phasedUnits, moduleManager, modelLoader, 
                        defaultModule, monitor.newChild(10));
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                modelLoader.setupSourceFileObjects(typeChecker.getPhasedUnits().getPhasedUnits());

                monitor.worked(1);
                
                // Parsing of ALL units in the source folder should have been done

                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("- determining module dependencies for " 
                        + project.getName());

                phasedUnits.visitModules();

                //By now the language module version should be known (as local)
                //or we should use the default one.
                Module languageModule = context.getModules().getLanguageModule();
                if (languageModule.getVersion() == null) {
                    languageModule.setVersion(TypeChecker.LANGUAGE_MODULE_VERSION);
                }

                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                final ModuleValidator moduleValidator = new ModuleValidator(context, phasedUnits) {
                    @Override
                    protected void executeExternalModulePhases() {}
                    @Override
                    protected Exception catchIfPossible(Exception e) {
                        if (e instanceof OperationCanceledException) {
                            throw (OperationCanceledException)e;
                        }
                        return e;
                    }
                };

                final int maxModuleValidatorWork = 100000;
                final SubMonitor validatorProgress = SubMonitor.convert(monitor.newChild(100), maxModuleValidatorWork);
                moduleValidator.setListener(new ModuleValidator.ProgressListener() {
                    @Override
                    public void retrievingModuleArtifact(final Module module,
                            final ArtifactContext artifactContext) {
                        final long numberOfModulesNotAlreadySearched = moduleValidator.numberOfModulesNotAlreadySearched();
                        final long totalNumberOfModules = numberOfModulesNotAlreadySearched + moduleValidator.numberOfModulesAlreadySearched();
                        final long oneModuleWork = maxModuleValidatorWork / totalNumberOfModules;
                        final int workRemaining = (int) ((double)numberOfModulesNotAlreadySearched * oneModuleWork);
                        validatorProgress.setWorkRemaining(workRemaining);
                        artifactContext.setCallback(new ArtifactCallback() {
                            SubMonitor artifactProgress = null;
                            long size;
                            long alreadyDownloaded = 0;
                            StringBuilder messageBuilder = new StringBuilder("- downloading module ")
                                                            .append(module.getSignature())
                                                            .append(' ');
                            @Override
                            public void start(String nodeFullPath, long size, String contentStore) {
                                this.size = size;
                                int ticks = size > 0 ? (int) size : 100000; 
                                artifactProgress = SubMonitor.convert(validatorProgress.newChild((int)oneModuleWork), ticks);
                                if (! contentStore.isEmpty()) {
                                    messageBuilder.append("from ").append(contentStore);
                                }
                                artifactProgress.subTask(messageBuilder.toString());
                                if (artifactProgress.isCanceled()) {
                                    throw new OperationCanceledException("Interrupted the download of module : " + module.getSignature());
                                }
                            }
                            @Override
                            public void read(byte[] bytes, int length) {
                                if (artifactProgress.isCanceled()) {
                                    throw new OperationCanceledException("Interrupted the download of module : " + module.getSignature());
                                }
                                if (size < 0) {
                                    artifactProgress.setWorkRemaining(length*100);
                                } else {
                                    artifactProgress.subTask(new StringBuilder(messageBuilder)
                                                                .append(" ( ")
                                                                .append(alreadyDownloaded * 100 / size)
                                                                .append("% )").toString());
                                }
                                alreadyDownloaded += length;
                                artifactProgress.worked(length);
                            }
                            @Override
                            public void error(File localFile, Throwable t) {
                                localFile.delete();
                                artifactProgress.setWorkRemaining(0);
                            }
                            @Override
                            public void done(File arg0) {
                                artifactProgress.setWorkRemaining(0);
                            }
                        });
                    }

                    @Override
                    public void resolvingModuleArtifact(Module module,
                            ArtifactResult artifactResult) {
                        long numberOfModulesNotAlreadySearched = moduleValidator.numberOfModulesNotAlreadySearched();
                        validatorProgress.setWorkRemaining((int) (numberOfModulesNotAlreadySearched * 100
                                                                   / (numberOfModulesNotAlreadySearched + moduleValidator.numberOfModulesAlreadySearched())));
                        validatorProgress.subTask(new StringBuilder("- resolving module ")
                        .append(module.getSignature())
                        .toString());
                    }
                });

                moduleValidator.verifyModuleDependencyTree();

                validatorProgress.setWorkRemaining(0);

                typeChecker.setPhasedUnitsOfDependencies(moduleValidator.getPhasedUnitsOfDependencies());
                
                for (PhasedUnits dependencyPhasedUnits: typeChecker.getPhasedUnitsOfDependencies()) {
                    modelLoader.addSourceArchivePhasedUnits(dependencyPhasedUnits.getPhasedUnits());
                }
                
                modelLoader.setModuleAndPackageUnits();
                
                if (compileToJs(project)) {
                    for (Module module : typeChecker.getContext().getModules().getListOfModules()) {
                        if (module instanceof JDTModule) {
                            JDTModule jdtModule = (JDTModule) module;
                            if (jdtModule.isCeylonArchive()) {
                                File artifact = getProjectRepositoryManager(project).getArtifact(
                                        new ArtifactContext(
                                                jdtModule.getNameAsString(), 
                                                jdtModule.getVersion(), 
                                                ArtifactContext.JS));
                                if (artifact == null) {
                                    moduleManager.attachErrorToOriginalModuleImport(jdtModule, 
                                            "module not available for JavaScript platform: '" + 
                                                    module.getNameAsString() + "' \"" + 
                                                    module.getVersion() + "\"");
                                }
                            }
                        }
                    }
                }
                

                monitor.worked(1);

                typeCheckers.put(project, typeChecker);
                projectFiles.put(project, scannedFiles);
                modelStates.put(project, ModelState.Parsed);

                ExternalSourceArchiveManager externalArchiveManager = getExternalSourceArchiveManager();
                if (allClasspathContainersInitialized()) {
                    externalArchiveManager.cleanUp(monitor);
                }
                for (IPath sourceArchivePath : getExternalSourceArchives(getProjectExternalModules(project))) {
                    if (externalArchiveManager.getSourceArchive(sourceArchivePath) == null) {
                        externalArchiveManager.addSourceArchive(sourceArchivePath, true);                    
                    }
                }
                externalArchiveManager.createPendingSourceArchives(monitor);
                
                for (ICeylonModelListener listener : modelListeners) {
                    listener.modelParsed(project);
                }
                
                monitor.done();
                
                return typeChecker;
            }
        });
    }

    private static TypeChecker buildTypeChecker(IProject project,
            final IJavaProject javaProject) throws CoreException {
        TypeCheckerBuilder typeCheckerBuilder = new TypeCheckerBuilder()
            .verbose(false)
            .moduleManagerFactory(new ModuleManagerFactory(){
                @Override
                public ModuleManager createModuleManager(Context context) {
                    return new JDTModuleManager(context, javaProject);
                }
            });
        
        RepositoryManager repositoryManager = getProjectRepositoryManager(project);
        
        typeCheckerBuilder.setRepositoryManager(repositoryManager);
        TypeChecker typeChecker = typeCheckerBuilder.getTypeChecker();
        return typeChecker;
    }

    private static List<IFile> scanFiles(IProject project, IJavaProject javaProject, 
            final TypeChecker typeChecker, final PhasedUnits phasedUnits, 
            final JDTModuleManager moduleManager, final JDTModelLoader modelLoader, 
            final Module defaultModule, IProgressMonitor mon) throws CoreException {
        SubMonitor monitor = SubMonitor.convert(mon, 10000);
        final List<IFile> projectFiles = new ArrayList<IFile>();
        final Collection<IFolder> sourceFolders = new LinkedList<>();
        for (IFolder sourceFolder : getSourceFolders(project)) {
            if (sourceFolder.exists()) {
                sourceFolders.add(sourceFolder);
            }
        }
        final Collection<IFolder> resourceFolders = new LinkedList<>();
        for (IFolder resourceFolder : getResourceFolders(project)) {
            if (resourceFolder.exists()) {
                resourceFolders.add(resourceFolder);
            }
        }

        // First scan all non-default source modules and attach the contained packages 
        for (IFolder srcFolder : sourceFolders) {
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            final ResourceVirtualFile srcDir = ResourceVirtualFile.createResourceVirtualFile(srcFolder);
            srcFolder.accept(new ModulesScanner(defaultModule, modelLoader, moduleManager,
                    srcDir, typeChecker, monitor));
        }

        // Then scan all source files
        for (final IFolder sourceFolder : sourceFolders) {
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            final IFolderVirtualFile srcDir = ResourceVirtualFile.createResourceVirtualFile(sourceFolder);
            sourceFolder.accept(new RootFolderScanner(RootFolderType.SOURCE, defaultModule, modelLoader, moduleManager,
                    srcDir, typeChecker, projectFiles,
                    phasedUnits, monitor));
        }

        // Then scan all resource files
        for (final IFolder resourceFolder : resourceFolders) {
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            final IFolderVirtualFile srcDir = ResourceVirtualFile.createResourceVirtualFile(resourceFolder);
            resourceFolder.accept(new RootFolderScanner(RootFolderType.RESOURCE, defaultModule, modelLoader, moduleManager,
                    srcDir, typeChecker, projectFiles,
                    phasedUnits, monitor));
        }
        return projectFiles;
    }

    private static void addProblemAndTaskMarkers(final List<PhasedUnit> units, 
            final IProject project) {
        for (PhasedUnit phasedUnit: units) {
            IFile file = getFile(phasedUnit);
            CompilationUnit compilationUnit = phasedUnit.getCompilationUnit();
            compilationUnit.visit(new WarningSuppressionVisitor<Warning>(Warning.class,
                    CeylonBuilder.getSuppressedWarnings(project)));
            compilationUnit.visit(new MarkerCreator(file));
            addTaskMarkers(file, phasedUnit.getTokens());
        }
    }

    private boolean generateBinaries(IProject project, IJavaProject javaProject,
            Collection<PhasedUnit> unitsTypecheckedIncrementally,
            Collection<IFile> filesToCompile, TypeChecker typeChecker, 
            IProgressMonitor monitor) throws CoreException {
        List<String> options = new ArrayList<String>();
        List<File> js_srcdir = new ArrayList<File>();
        List<File> js_rsrcdir = new ArrayList<File>();
        List<String> js_repos = new ArrayList<String>();
        String js_verbose = null;
        String js_outRepo = null;

        String srcPath = "";
        for (IFolder sourceFolder : getSourceFolders(project)) {
            File sourcePathElement = sourceFolder.getRawLocation().toFile();
            if (!srcPath.isEmpty()) {
                srcPath += File.pathSeparator;
            }
            srcPath += sourcePathElement.getAbsolutePath();
            js_srcdir.add(sourcePathElement);
        }
        options.add("-src");
        options.add(srcPath);
        String resPath = "";
        for (IFolder resourceFolder : getResourceFolders(project)) {
            File resourcePathElement = resourceFolder.getRawLocation().toFile();
            if (!resPath.isEmpty()) {
                resPath += File.pathSeparator;
            }
            resPath += resourcePathElement.getAbsolutePath();
            js_rsrcdir.add(resourcePathElement);
        }
        options.add("-res");
        options.add(resPath);
        
        options.add("-encoding");
        options.add(project.getDefaultCharset());
        
        for (String repository : getUserRepositories(project)) {
            options.add("-rep");
            options.add(repository);
            js_repos.add(repository);
        }
        
        EnumSet<Warning> suppressedWarnings = getSuppressedWarnings(project);
        if (suppressedWarnings!=null && 
                !suppressedWarnings.isEmpty()) {
            options.add("-suppress-warnings");
            options.add(suppressedWarnings.toString()
                    .replace("[", "").replace("]", ""));
        }

        String verbose = System.getProperty("ceylon.verbose");
        if (verbose!=null && "true".equals(verbose)) {
            options.add("-verbose");
            js_verbose = "all";
        }
        else {
            verbose = getVerbose(project);
            if (verbose!=null) {
                options.add("-verbose:"+verbose);
                js_verbose = verbose;
            }
        }
        
        options.add("-g:lines,vars,source");

        String systemRepo = getInterpolatedCeylonSystemRepo(project);
        if(systemRepo != null && !systemRepo.isEmpty()){
            options.add("-sysrep");
            options.add(systemRepo);
        }
        
        final File modulesOutputDir = getCeylonModulesOutputDirectory(project);
        if (modulesOutputDir!=null) {
            options.add("-out");
            options.add(modulesOutputDir.getAbsolutePath());
            js_outRepo = modulesOutputDir.getAbsolutePath();
        }

        List<File> forJavaBackend = new ArrayList<File>();
        List<File> forJavascriptBackend = new ArrayList<File>();
        List<File> resources = new ArrayList<File>();
        for (IFile file : filesToCompile) {
        	if (isInSourceFolder(file)) {
                if(isCeylon(file)) {
                    forJavaBackend.add(file.getLocation().toFile());
                }
                if(isJava(file)) {
                    forJavaBackend.add(file.getLocation().toFile());
                }
        	}
            if (isResourceFile(file)) {
                resources.add(file.getLocation().toFile());
            }
        }

        // For the moment the JSCompiler doesn't support partial compilation of a module
        // so we add all the files to the source files list.
        // TODO : When JS partial module compilation is supported, re-integrate these lines 
        // in the loop above
        if (compileToJs(project)) {
            for (IFile file : getProjectFiles(project)) {
            	if (isInSourceFolder(file)) {
                    if(isCeylon(file) || isJavascript(file)) {
                        forJavascriptBackend.add(file.getLocation().toFile());
                    }
            	}
            }
        }

        PrintWriter printWriter = new PrintWriter(verbose==null ? System.out : getConsoleStream(), true);
        boolean success = true;
        //Compile JS first
        if ((forJavascriptBackend.size() + resources.size() > 0) && compileToJs(project)) {
            success = compileJs(project, typeChecker, js_srcdir, js_rsrcdir, js_repos,
                    js_verbose, js_outRepo, printWriter, ! compileToJava(project),
                    forJavascriptBackend, resources);
        }
        if ((forJavaBackend.size() + resources.size() > 0) && compileToJava(project)) {
            // For Java don't stop compiling when encountering errors
            options.add("-continue");
            // always add the java files, otherwise ceylon code won't see them 
            // and they won't end up in the archives (src/car)
            success = success & compile(project, javaProject, options,
            		unitsTypecheckedIncrementally,
                    forJavaBackend, resources, typeChecker, printWriter, monitor);
        }
        
        if (! compileToJs(project) &&
        		! compileToJava(project) &&
        		modulesOutputDir != null) {
        	EclipseLogger logger = new EclipseLogger();
			RepositoryManager outRepo = repoManager()
            .offline(CeylonProjectConfig.get(project).isOffline())
            .cwd(project.getLocation().toFile())
            .outRepo(js_outRepo)
            .logger(logger)
            .buildOutputManager();
        	
            for (Module m : getProjectDeclaredSourceModules(project)) {
            	if (m instanceof JDTModule) {
                	ArtifactCreator sac;
					try {
						sac = CeylonUtils.makeSourceArtifactCreator(outRepo, js_srcdir,
						        m.getNameAsString(), m.getVersion(), js_verbose!=null, logger);
	                	List<String> moduleFiles = new ArrayList<>();
	                	for (IFile file : filesToCompile) {
	                		IContainer container = file.getParent();
	                		if (container instanceof IFolder) {
	                    		if (isSourceFile(file)) {
	                    			Module fileModule = getModule(((IFolder)container));
	                    			if (m.equals(fileModule)) {
	                    				moduleFiles.add(file.getLocation().toFile().getPath());
	                    			}
	                    		}                			
	                		}
	                	}
	                    sac.copy(moduleFiles);
					} catch (IOException e) {
						e.printStackTrace();
						success = false;
					}
            	}
            }
        }
        return success;
    }

    private boolean compileJs(IProject project, TypeChecker typeChecker,
            List<File> js_srcdir, List<File> js_rsrcdir, List<String> js_repos, 
            String js_verbose, String js_outRepo, PrintWriter printWriter, 
            boolean generateSourceArchive, List<File> sources, List<File> resources) 
                    throws CoreException {
        
        Options jsopts = new Options()
                .outWriter(printWriter)
                .repos(js_repos)
                .sourceDirs(js_srcdir)
                .resourceDirs(js_rsrcdir)
                .systemRepo(getInterpolatedCeylonSystemRepo(project))
                .outRepo(js_outRepo)
                .optimize(true)
                .verbose(js_verbose)
                .generateSourceArchive(generateSourceArchive)
                .encoding(project.getDefaultCharset())
                .offline(CeylonProjectConfig.get(project).isOffline());
        JsCompiler jsc = new JsCompiler(typeChecker, jsopts) {

            @Override
            protected boolean nonCeylonUnit(Unit u) {
                if (! super.nonCeylonUnit(u)) {
                    return false;
                }
                if (u instanceof CeylonBinaryUnit) {
                    CeylonBinaryUnit ceylonBinaryUnit = (CeylonBinaryUnit) u;
                	Module module = u.getPackage().getModule();
                	if (module != null) {
                		if (module.equals(module.getLanguageModule())) {
                			return false;
                		}
                	}
                    if (ceylonBinaryUnit.getCeylonSourceRelativePath() != null) {
                        return false;
                    }
                }
                return true;
            }
            
            public File getFullPath(PhasedUnit pu) {
                VirtualFile virtualFile = pu.getUnitFile();
                if (virtualFile instanceof ResourceVirtualFile) {
                    return ((IFileVirtualFile) virtualFile).getFile().getLocation().toFile();
                } else {
                    return new File(virtualFile.getPath());
                }
            };
        }.stopOnErrors(false);
        try {
        	jsc.setSourceFiles(sources);
            jsc.setResourceFiles(resources);
            if (!jsc.generate()) {
                CompileErrorReporter errorReporter = null;
                //Report backend errors
                for (Message e : jsc.getErrors()) {
                    if (e instanceof UnexpectedError) {
                        if (errorReporter == null) {
                            errorReporter = new CompileErrorReporter(project);
                        }
                        errorReporter.report(new CeylonCompilationError(project, (UnexpectedError)e));
                    }
                }
                if (errorReporter != null) {
                    //System.out.println("Ceylon-JS compiler failed for " + project.getName());
                    errorReporter.failed();
                }
                return false;
            }
            else {
                //System.out.println("compile ok to js");
                return true;
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(printWriter);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private boolean compile(final IProject project, IJavaProject javaProject, 
            List<String> options, Collection<PhasedUnit> unitsTypecheckedIncrementally,
            List<File> sources, List<File> resources,
            final TypeChecker typeChecker, PrintWriter printWriter,
            IProgressMonitor mon) 
                    throws VerifyError {
        
        int numberOfJavaFiles = 0;
        int numberOfCeylonFiles = 0;
        
        for (File file : sources) {
            if (JavaCore.isJavaLikeFileName(file.getName())) {
                numberOfJavaFiles ++;
            } else if (file.getName().endsWith(".ceylon")){
                numberOfCeylonFiles ++;
            }
        }

        int numberOfSourceFiles = numberOfCeylonFiles + numberOfJavaFiles;
        
        final SubMonitor monitor = SubMonitor.convert(mon, 
                "Generating binaries for " + numberOfSourceFiles + 
                " source files in project " + project.getName(), 
                numberOfSourceFiles * 2);

        com.redhat.ceylon.compiler.java.tools.CeyloncTool compiler;
        try {
            compiler = new com.redhat.ceylon.compiler.java.tools.CeyloncTool();
        } catch (VerifyError e) {
            System.err.println("ERROR: Cannot run tests! Did you maybe forget to configure the -Xbootclasspath/p: parameter?");
            throw e;
        }

        CompileErrorReporter errorReporter = new CompileErrorReporter(project);

        final com.sun.tools.javac.util.Context context = new com.sun.tools.javac.util.Context();
        context.put(com.sun.tools.javac.util.Log.outKey, printWriter);
        context.put(DiagnosticListener.class, errorReporter);
        CeylonLog.preRegister(context);
        
        final Map<RegularFileObject, Set<String>> inputFilesToGenerate = new HashMap<RegularFileObject, Set<String>>();
        BuildFileManager fileManager = new BuildFileManager(context, true, null, project, inputFilesToGenerate);
        
        computeCompilerClasspath(project, javaProject, options);
        
        List<File> allFiles = new ArrayList<>(sources.size()+ resources.size());
        allFiles.addAll(sources);
        allFiles.addAll(resources);
        Iterable<? extends JavaFileObject> unitsToCompile =
                fileManager.getJavaFileObjectsFromFiles(allFiles);
        
        if (reuseEclipseModelInCompilation(project)) {
            setupJDTModelLoader(project, typeChecker, context, unitsTypecheckedIncrementally);
        }
        
        CeyloncTaskImpl task = (CeyloncTaskImpl) compiler.getTask(printWriter, 
                fileManager, errorReporter, options, null, 
                unitsToCompile);
        task.setTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent ta) {
                if (! ta.getKind().equals(Kind.PARSE) && ! ta.getKind().equals(Kind.ANALYZE)) {
                    return;
                }
                String name = ta.getSourceFile().getName();
                name = name.substring(name.lastIndexOf("/")+1);
                if (ta.getKind().equals(Kind.PARSE)) {
                    CompilationUnitTree cut = ta.getCompilationUnit();
                    if (cut != null && cut instanceof CeylonCompilationUnit) {
                        monitor.subTask("- transforming " + name);
                    } else {
                        monitor.subTask("- parsing " + name);
                    }
                } 
                if (ta.getKind().equals(Kind.ANALYZE)) {
                    monitor.subTask("- generating bytecode for " + name);
                }
            }
            @Override
            public void finished(TaskEvent ta) {
                if (! ta.getKind().equals(Kind.PARSE) && ! ta.getKind().equals(Kind.ANALYZE)) {
                    return;
                }
                if (ta.getKind().equals(Kind.PARSE)) {
                    RegularFileObject sourceFile = BuildFileManager.getSourceFile(ta.getSourceFile());
                    Set<String> expectedClasses = inputFilesToGenerate.get(sourceFile);
                    if (expectedClasses == null) {
                        expectedClasses = new HashSet<String>();
                        inputFilesToGenerate.put(sourceFile, expectedClasses);
                    }
                    
                    if (ta.getCompilationUnit() instanceof JCCompilationUnit) {
                        JCCompilationUnit cu = (JCCompilationUnit) ta.getCompilationUnit();
                        for (JCTree def : cu.defs) {
                            if (def instanceof JCClassDecl) {
                                expectedClasses.add(((JCClassDecl) def).name.toString());
                            }
                        }
                    }
                    
                    if (expectedClasses.isEmpty()) {
                        inputFilesToGenerate.remove(sourceFile);
                    }
                }
                monitor.worked(1);
            }
        });
        boolean success=false;
        try {
            success = task.call();
        }
        catch (Exception e) {
            e.printStackTrace(printWriter);
        }
        if (!success) {
            errorReporter.failed(task.getExitState());
        }
        fileManager.addUngeneratedErrors();
        monitor.done();
        return success;
    }

    private void computeCompilerClasspath(IProject project,
            IJavaProject javaProject, List<String> options) {
        
        List<String> classpathElements = new ArrayList<String>();

//        Modules projectModules = getProjectModules(project);
//      ArtifactContext ctx;
//        if (projectModules != null) {
//            Module languageModule = projectModules.getLanguageModule();
//            ctx = new ArtifactContext(languageModule.getNameAsString(), 
//                  languageModule.getVersion());
//        } 
//        else {
//            ctx = new ArtifactContext(LANGUAGE_MODULE_NAME, 
//                  TypeChecker.LANGUAGE_MODULE_VERSION);
//        }
//        
//        ctx.setSuffix(ArtifactContext.CAR);
//        RepositoryManager repositoryManager = getProjectRepositoryManager(project);
//        if (repositoryManager!=null) {
//            //try {
//            File languageModuleArchive = repositoryManager.getArtifact(ctx);
//            classpathElements.add(languageModuleArchive.getAbsolutePath());
//            /*} 
//            catch (Exception e) {
//                e.printStackTrace();
//            }*/
//        }
        
        addProjectClasspathElements(classpathElements,
                javaProject);
        try {
            for (IProject p: project.getReferencedProjects()) {
                if(p.isAccessible()){
                    addProjectClasspathElements(classpathElements,
                            JavaCore.create(p));
                }
            }
        } 
        catch (CoreException ce) {
            ce.printStackTrace();
        }
        
        options.add("-classpath");
        // add the compiletime required jars (those used by the language module implicitely)
        classpathElements.addAll(CeylonPlugin.getCompiletimeRequiredJars());
        String classpath = "";
        for (String cpElement : classpathElements) {
            if (! classpath.isEmpty()) {
                classpath += File.pathSeparator;
            }
            classpath += cpElement;
        }
        options.add(classpath);
    }

    private void setupJDTModelLoader(final IProject project,
            final TypeChecker typeChecker,
            final com.sun.tools.javac.util.Context context,
            final Collection<PhasedUnit> unitsTypecheckedIncrementally) {

        final JDTModelLoader modelLoader = getModelLoader(typeChecker);
        
        context.put(LanguageCompiler.ceylonContextKey, typeChecker.getContext());
        context.put(TypeFactory.class, modelLoader.getTypeFactory());
        context.put(LanguageCompiler.compilerDelegateKey, 
                new JdtCompilerDelegate(modelLoader, project, typeChecker, context, unitsTypecheckedIncrementally));
        
        context.put(TypeFactory.class, modelLoader.getTypeFactory());
        context.put(ModelLoaderFactory.class, new ModelLoaderFactory() {
            @Override
            public AbstractModelLoader createModelLoader(
                    com.sun.tools.javac.util.Context context) {
                return modelLoader;
            }
        });
    }

    private void addProjectClasspathElements(List<String> classpathElements, IJavaProject javaProj) {
        try {
            List<IClasspathContainer> containers = getCeylonClasspathContainers(javaProj);
            for (IClasspathContainer container : containers) {
                for (IClasspathEntry cpEntry : container.getClasspathEntries()) {
                    if (!isInCeylonClassesOutputFolder(cpEntry.getPath())) {
                        classpathElements.add(cpEntry.getPath().toOSString());
                    }
                }
            }

            File outputDir = toFile(javaProj.getProject(), javaProj.getOutputLocation()
                    .makeRelativeTo(javaProj.getProject().getFullPath()));          
            classpathElements.add(outputDir.getAbsolutePath());
            for (IClasspathEntry cpEntry : javaProj.getResolvedClasspath(true)) {
                if (isInCeylonClassesOutputFolder(cpEntry.getPath())) {
                    classpathElements.add(javaProj.getProject().getLocation().append(cpEntry.getPath().lastSegment()).toOSString());
                }
            }
        } 
        catch (JavaModelException e1) {
            e1.printStackTrace();
        }
    }
    
    public static String getVerbose(IProject project) {
        return getBuilderArgs(project).get("verbose");
    }

    public static void setVerbose(IProject project, String verbose) {
        if (verbose==null) {
            getBuilderArgs(project).remove(verbose);
        }
        else {
            getBuilderArgs(project).put("verbose", verbose);
        }
    }

    public static boolean isExplodeModulesEnabled(IProject project) {
        Map<String,String> args = getBuilderArgs(project);
        return args.get("explodeModules")!=null ||
                args.get("enableJdtClasses")!=null;
    }

    public static boolean areAstAwareIncrementalBuildsEnabled(IProject project) {
        return CeylonNature.isEnabled(project) && getBuilderArgs(project).get("astAwareIncrementalBuilds")==null;
    }

    public static boolean compileWithJDTModel = true;
    public static boolean reuseEclipseModelInCompilation(IProject project) {
        return loadDependenciesFromModelLoaderFirst(project) && compileWithJDTModel; 
    }

    // Keep it false on master until we fix the associated cross-reference and search issues 
    // by correctly managing source to binary links and indexes
    public static boolean loadBinariesFirst = "true".equals(System.getProperty("ceylon.loadBinariesFirst", "true"));
    public static boolean loadDependenciesFromModelLoaderFirst(IProject project) {
        return compileToJava(project) && loadBinariesFirst;
    }

    public static boolean compileToJs(IProject project) {
        return getBuilderArgs(project).get("compileJs")!=null;
    }
    public static boolean compileToJava(IProject project) {
        return CeylonNature.isEnabled(project) && getBuilderArgs(project).get("compileJava")==null;
    }
    
    public static boolean showWarnings(IProject project) {
        return !getSuppressedWarnings(project).equals(EnumSet.allOf(Warning.class));
    }
    
    public static EnumSet<Warning> getSuppressedWarnings(IProject project) {
        if (project==null) {
            return EnumSet.noneOf(Warning.class);
        }
        else {
            return CeylonProjectConfig.get(project).getSuppressWarningsEnum();
        }
    }

    public static String fileName(ClassMirror c) {
        if (c instanceof JavacClass) {
            return ((JavacClass) c).classSymbol.classfile.getName();
        }
        else if (c instanceof JDTClass) {
            return ((JDTClass) c).getFileName();
        }
        else if (c instanceof SourceClass) {
            return ((SourceClass) c).getModelDeclaration().getUnit().getFilename();
        }
        else {
            return "another file";
        }
    }

    public static List<String> getUserRepositories(IProject project) throws CoreException {
        List<String> userRepos = getCeylonRepositories(project);
        userRepos.addAll(getReferencedProjectsOutputRepositories(project));
        return userRepos;
    }
    
    public static List<String> getAllRepositories(IProject project) throws CoreException {
        List<String> allRepos = getUserRepositories(project);
        allRepos.add(CeylonProjectConfig.get(project).getMergedRepositories().getCacheRepository().getUrl());
        return allRepos;
    }
    
    public static List<String> getReferencedProjectsOutputRepositories(IProject project) throws CoreException {
        List<String> repos = new ArrayList<String>();
        if (project != null) {
            for (IProject referencedProject: project.getReferencedProjects()) {
                if (referencedProject.isOpen() && CeylonNature.isEnabled(referencedProject)) {
                    repos.add(getCeylonModulesOutputDirectory(referencedProject).getAbsolutePath());
                }
            }
        }
        return repos;
    }

    private static Map<String,String> getBuilderArgs(IProject project) {
        if (project!=null) {
            try {
                for (ICommand c: project.getDescription().getBuildSpec()) {
                    if (c.getBuilderName().equals(BUILDER_ID)) {
                        return c.getArguments();
                    }
                }
            } 
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyMap();
    }

    public static List<String> getCeylonRepositories(IProject project) {
        CeylonProjectConfig projectConfig = CeylonProjectConfig.get(project);
        List<String> projectLookupRepos = projectConfig.getProjectLocalRepos();
        List<String> globalLookupRepos = projectConfig.getGlobalLookupRepos();
        List<String> projectRemoteRepos = projectConfig.getProjectRemoteRepos();
        List<String> otherRemoteRepos = projectConfig.getOtherRemoteRepos();

        List<String> repos = new ArrayList<String>();
        repos.addAll(projectLookupRepos);
        repos.addAll(globalLookupRepos);
        repos.addAll(projectRemoteRepos);
        repos.addAll(otherRemoteRepos);
        return repos;
    }

    private static File toFile(IProject project, IPath path) {
        return project.getFolder(path).getRawLocation().toFile();
    }
    
    private static void clearMarkersOn(IResource resource, boolean alsoDeleteBackendErrors) {
        clearMarkersOn(resource, alsoDeleteBackendErrors, false);
    }
    
    private static void clearMarkersOn(IResource resource, boolean alsoDeleteBackendErrors, boolean onlyBackendErrors) {
        try {
            if (!onlyBackendErrors) {
                resource.deleteMarkers(TASK_MARKER_ID, false, DEPTH_INFINITE);
                resource.deleteMarkers(PROBLEM_MARKER_ID, true, DEPTH_INFINITE);
            }
            if (alsoDeleteBackendErrors) {
                resource.deleteMarkers(PROBLEM_MARKER_ID + ".backend", true, DEPTH_INFINITE);
                for (IMarker javaMarker : resource.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_INFINITE)) {
                    if (CeylonPlugin.PLUGIN_ID.equals(javaMarker.getAttribute(IMarker.SOURCE_ID))) {
                        javaMarker.delete();
                    }
                }
            }
            if (!onlyBackendErrors) {
                //these are actually errors from the Ceylon compiler, but
                //we did not bother creating a separate annotation type!
                resource.deleteMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, DEPTH_INFINITE);
            }
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private static void clearProjectMarkers(IProject project, boolean nonBackendMarkers, boolean backendMarkers) {
            //project.deleteMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, DEPTH_ZERO);
            if (nonBackendMarkers) {
                try {
                    project.deleteMarkers(PROBLEM_MARKER_ID, true, DEPTH_ZERO);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
            if (backendMarkers) {
                try {
                    project.deleteMarkers(PROBLEM_MARKER_ID + ".backend", true, DEPTH_ZERO);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
    }

    private static void clearMarkersOn(Collection<IFile> files, boolean alsoDeleteBackendErrors, boolean onlyBackendErrors) {
        for(IFile file: files) {
            clearMarkersOn(file, alsoDeleteBackendErrors, onlyBackendErrors);
        }
    }

    private static void clearMarkersOn(Collection<IFile> files, boolean alsoDeleteBackendErrors) {
        clearMarkersOn(files, alsoDeleteBackendErrors, false);
    }
    
    /*private void dumpSourceList(Collection<IFile> sourcesToCompile) {
        MessageConsoleStream consoleStream= getConsoleStream();
        for(Iterator<IFile> iter= sourcesToCompile.iterator(); iter.hasNext(); ) {
            IFile srcFile= iter.next();
            consoleStream.println("  " + srcFile.getFullPath());
        }
    }*/

    protected static MessageConsoleStream getConsoleStream() {
        return findConsole().newMessageStream();
    }
//    
//    protected static MessageConsoleStream getConsoleErrorStream() {
//        final MessageConsoleStream stream = findConsole().newMessageStream();
//        //TODO: all this, just to get the color red? can that be right??
//        /*try {
//          getWorkbench().getProgressService().runInUI(getWorkbench().getWorkbenchWindows()[0], 
//                  new IRunnableWithProgress() {
//              
//              @Override
//              public void run(IProgressMonitor monitor) throws InvocationTargetException,
//                      InterruptedException {
//                  stream.setColor(getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED));
//              }
//          }, null);
//      }
//      catch (Exception e) {
//          e.printStackTrace();
//      }*/
//      return stream;
//    }
//    
//    private String timedMessage(String message) {
//        long elapsedTimeMs = (System.nanoTime() - startTime) / 1000000;
//        return String.format("[%1$10d] %2$s", elapsedTimeMs, message);
//    }

    /**
     * Find or create the console with the given name
     * @param consoleName
     */
    protected static MessageConsole findConsole() {
        MessageConsole myConsole= null;
        final IConsoleManager consoleManager= ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] consoles= consoleManager.getConsoles();
        for(int i= 0; i < consoles.length; i++) {
            IConsole console= consoles[i];
            if (console.getName().equals(CEYLON_CONSOLE))
                myConsole= (MessageConsole) console;
        }
        if (myConsole == null) {
            myConsole= new MessageConsole(CEYLON_CONSOLE, 
                  CeylonPlugin.getInstance().getImageRegistry()
                      .getDescriptor(CeylonResources.BUILDER));
            consoleManager.addConsoles(new IConsole[] { myConsole });
        }
//      consoleManager.showConsoleView(myConsole);
        return myConsole;
    }

    private static void addTaskMarkers(IFile file, List<CommonToken> tokens) {
        // clearTaskMarkersOn(file);
        for (CommonToken token : tokens) {
            if (token.getType() == CeylonLexer.LINE_COMMENT || token.getType() == CeylonLexer.MULTI_COMMENT) {
                CeylonTaskUtil.addTaskMarkers(token, file);
            }
        }
    }
    
    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
        super.clean(monitor);
        
        IProject project = getProject();
        
//        startTime = System.nanoTime();
//        getConsoleStream().println("\n===================================");
//        getConsoleStream().println(timedMessage("Starting Ceylon clean on project: " + project.getName()));
//        getConsoleStream().println("-----------------------------------");
        
        cleanupModules(monitor, project);
        cleanupJdtClasses(monitor, project);
        
        monitor.subTask("Clearing project and source markers for project " + project.getName());
        clearProjectMarkers(project, true, true);
        clearMarkersOn(project, true);

//        getConsoleStream().println("-----------------------------------");
//        getConsoleStream().println(timedMessage("End Ceylon clean on project: " + project.getName()));
//        getConsoleStream().println("===================================");
    }

    private void cleanupJdtClasses(IProgressMonitor monitor, IProject project) {
        if (isExplodeModulesEnabled(project)) {
            monitor.subTask("Cleaning exploded modules directory of project " + project.getName());
            final File ceylonOutputDirectory = getCeylonClassesOutputDirectory(project);
            new RepositoryLister(Arrays.asList(".*")).list(ceylonOutputDirectory, 
                    new RepositoryLister.Actions() {
                @Override
                public void doWithFile(File path) {
                    path.delete();
                }

                public void exitDirectory(File path) {
                    if (path.list().length == 0 && 
                            !path.equals(ceylonOutputDirectory)) {
                        path.delete();
                    }
                }
            });
        }
    }

    private void cleanupModules(IProgressMonitor monitor, IProject project) {
        final File modulesOutputDirectory = getCeylonModulesOutputDirectory(project);
        if (modulesOutputDirectory != null) {
            monitor.subTask("Cleaning existing artifacts of project " + project.getName());
            List<String> extensionsToDelete = Arrays.asList(".jar", ".js", ".car", ".src", ".sha1");
            new RepositoryLister(extensionsToDelete).list(modulesOutputDirectory, 
                    new RepositoryLister.Actions() {
                @Override
                public void doWithFile(File path) {
                    path.delete();
                }
                
                public void exitDirectory(File path) {
                    if (path.list().length == 0 && 
                            !path.equals(modulesOutputDirectory)) {
                        path.delete();
                    }
                }
            });
        }
    }
    
    public static IFile getFile(PhasedUnit phasedUnit) {
        return ((IFileVirtualFile) phasedUnit.getUnitFile()).getFile();
    }

    // TODO think: doRefresh(file.getParent()); // N.B.: Assumes all
    // generated files go into parent folder

    private static List<IFile> getProjectFiles(IProject project) {
        return projectFiles.get(project);
}

    public static TypeChecker getProjectTypeChecker(IProject project) {
        return typeCheckers.get(project);
    }

    public static PhasedUnits getProjectPhasedUnits(IProject project) {
        TypeChecker typeChecker = getProjectTypeChecker(project);
        if (typeChecker != null) {
            return typeChecker.getPhasedUnits();
        }
        return null;
    }

    public static Modules getProjectModules(IProject project) {
        TypeChecker typeChecker = getProjectTypeChecker(project);
        if (typeChecker == null) {
            return null;
        }
        return typeChecker.getContext().getModules();
    }
    
    public static Collection<JDTModule> getProjectExternalModules(IProject project) {
        TypeChecker typeChecker = getProjectTypeChecker(project);
        if (typeChecker == null) {
            return Collections.emptyList();
        }
        List<JDTModule> modules = new ArrayList<>();
        for (Module m : typeChecker.getContext().getModules().getListOfModules()) {
            if (m instanceof JDTModule) {
                JDTModule module = (JDTModule) m;
                if (! module.isProjectModule()) {
                    modules.add(module);
                }
            }
        }
        return modules;
    }

    public static Collection<Module> getProjectSourceModules(IProject project) {
        List<Module> moduleList = new ArrayList<Module>();
        moduleList.addAll(getProjectDeclaredSourceModules(project));
        Modules projectModules = getProjectModules(project);
        if (projectModules != null) {
            moduleList.add(projectModules.getDefaultModule());
        }
        return moduleList;
    }

    public static Collection<Module> getProjectDeclaredSourceModules(IProject project) {
        TypeChecker typeChecker = getProjectTypeChecker(project);
        if (typeChecker == null) {
            return Collections.emptyList();
        }
        List<Module> modules = new ArrayList<>();
        for (Module m : typeChecker.getPhasedUnits().getModuleManager().getCompiledModules()) {
            if (m instanceof JDTModule) {
                JDTModule module = (JDTModule) m;
                if (module.isProjectModule()) {
                    modules.add(module);
                }
            }
        }
        return modules;
    }

    public static RepositoryManager getProjectRepositoryManager(IProject project) {
        RepositoryManager repoManager = projectRepositoryManagers.get(project);
        if (repoManager == null) {
            try {
                repoManager = resetProjectRepositoryManager(project);
            } catch(CoreException e) {
                e.printStackTrace();
            }
        }
        return repoManager;
    }
    
    public static RepositoryManager resetProjectRepositoryManager(IProject project) throws CoreException {
        RepositoryManager repositoryManager = repoManager()
                .offline(CeylonProjectConfig.get(project).isOffline())
                .cwd(project.getLocation().toFile())
                .systemRepo(getInterpolatedCeylonSystemRepo(project))
                .extraUserRepos(getReferencedProjectsOutputRepositories(project))
                .logger(new EclipseLogger())
                .isJDKIncluded(true)
                .buildManager();

        projectRepositoryManagers.put(project, repositoryManager);
        return repositoryManager;
    }
    
    public static Collection<IProject> getProjects() {
        return typeCheckers.keySet();
    }

    public static Collection<TypeChecker> getTypeCheckers() {
        return typeCheckers.values();
    }

    public static void removeProject(IProject project) {
        typeCheckers.remove(project);
        projectFiles.remove(project);
        modelStates.remove(project);
        containersInitialized.remove(project);
        projectRepositoryManagers.remove(project);
        CeylonProjectConfig.remove(project);
        JavaProjectStateMirror.cleanup(project);
        projectModuleDependencies.remove(project);
    }
    
    public static List<IFolder> getSourceFolders(IProject project) {
        //TODO: is the call to JavaCore.create() very expensive??
        List<IPath> folderPaths = getSourceFolders(JavaCore.create(project));
        List<IFolder> sourceFolders = new ArrayList<>(folderPaths.size());
        for (IPath path : folderPaths) {
            IResource r = project.findMember(path.makeRelativeTo(project.getFullPath()));
            if (r instanceof IFolder) {
                sourceFolders.add((IFolder) r);
            }
        }
        return sourceFolders;
    }

    /**
     * Read the IJavaProject classpath configuration and populate the ISourceProject's
     * build path accordingly.
     */
    public static List<IPath> getSourceFolders(IJavaProject javaProject) {
        if (javaProject.exists()) {
            try {
                List<IPath> projectSourceFolders = new ArrayList<IPath>();
                for (IClasspathEntry entry: javaProject.getRawClasspath()) {
                    IPath path = entry.getPath();
                    if (isCeylonSourceEntry(entry)) {
                        projectSourceFolders.add(path);
                    }
                }
                return projectSourceFolders;
            } 
            catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    public static List<IFolder> getResourceFolders(IProject project) {
        LinkedList<IFolder> resourceFolers = new LinkedList<>();
        if (project.exists()) {
            for (String resourceInConfig : CeylonProjectConfig.get(project).getResourceDirectories()) {
                class FolderHolder {
                    IFolder resourceFolder;
                }
                final FolderHolder folderHolder = new FolderHolder();;
                final IPath path = Path.fromOSString(resourceInConfig);
                if (! path.isAbsolute()) {
                    folderHolder.resourceFolder = project.getFolder(path);
                } else {
                    try {   
                        project.accept(new IResourceVisitor() {
                            @Override
                            public boolean visit(IResource resource) 
                                    throws CoreException {
                                if (resource instanceof IFolder &&
                                        resource.isLinked() && 
                                        resource.getLocation() != null &&
                                        resource.getLocation().equals(path)) {
                                    folderHolder.resourceFolder = (IFolder) resource;
                                    return false;
                                }
                                return resource instanceof IFolder || 
                                        resource instanceof IProject;
                            }
                        });
                    }
                    catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
                if (folderHolder.resourceFolder != null && 
                        folderHolder.resourceFolder.exists()) {
                    resourceFolers.add(folderHolder.resourceFolder);
                }
            }
        }
        return resourceFolers;
    }

    public static List<IFolder> getRootFolders(IProject project) {
        LinkedList<IFolder> rootFolders = new LinkedList<>();
        rootFolders.addAll(getSourceFolders(project));
        rootFolders.addAll(getResourceFolders(project));
        return rootFolders;
    }

    public static boolean isCeylonSourceEntry(IClasspathEntry entry) {
        if (entry.getEntryKind()!=IClasspathEntry.CPE_SOURCE) {
            return false;
        }
        
        for (IPath exclusionPattern : entry.getExclusionPatterns()) {
            if (exclusionPattern.toString().endsWith(".ceylon")) {
                return false;
            }
        }

        return true;
    }

    public static IFolder getRootFolder(IFolder folder) {
        if (folder.isLinked(IResource.CHECK_ANCESTORS)) {
            return null;
        }
        if (! folder.exists()) {
            for (IFolder sourceFolder: getSourceFolders(folder.getProject())) {
                if (sourceFolder.getFullPath().isPrefixOf(folder.getFullPath())) {
                    return sourceFolder;
                }
            }
            for (IFolder resourceFolder: getResourceFolders(folder.getProject())) {
                if (resourceFolder.getFullPath().isPrefixOf(folder.getFullPath())) {
                    return resourceFolder;
                }
            }
            return null;
        }

        try {
            Object property = folder.getSessionProperty(RESOURCE_PROPERTY_ROOT_FOLDER);
            if (property instanceof IFolder) {
                return (IFolder) property;
            }
        } catch (CoreException e) {
            CeylonPlugin.getInstance().getLog().log(new Status(Status.WARNING, CeylonPlugin.PLUGIN_ID, "Unexpected exception", e));
        }
        return null;
    }
    
    public static RootFolderType getRootFolderType(IPackageFragmentRoot pfr) {
        IResource resource = null;
        try {
            resource = pfr.getCorrespondingResource();
        } catch (JavaModelException e) {
        }
        if (resource instanceof IFolder) {
            return getRootFolderType((IFolder) resource);
        }
        return null;
    }

    public static boolean isSourceFolder(IPackageFragmentRoot pfr) {
        return RootFolderType.SOURCE.equals(getRootFolderType(pfr));
    }

    public static boolean isResourceFolder(IPackageFragmentRoot pfr) {
        return RootFolderType.RESOURCE.equals(getRootFolderType(pfr));
    }
    
    public static boolean isInSourceFolder(IPackageFragment pf) {
        return RootFolderType.SOURCE.equals(getRootFolderType(pf));
    }

    public static boolean isInResourceFolder(IPackageFragment pf) {
        return RootFolderType.RESOURCE.equals(getRootFolderType(pf));
    }
    

    public static RootFolderType getRootFolderType(IPackageFragment pf) {
        IResource resource = null;
        try {
            resource = pf.getCorrespondingResource();
        } catch (JavaModelException e) {
        }
        if (resource instanceof IFolder) {
            return getRootFolderType((IFolder) resource);
        }
        return null;
    }

    public static IFolder getRootFolder(IFile file) {
        if (file.getParent() instanceof IFolder) {
            return getRootFolder((IFolder) file.getParent());
        }
        return null;
    }

    public static RootFolderType getRootFolderType(IFolder folder) {
        IFolder rootFolder = getRootFolder(folder);
        if (rootFolder == null) {
            return null;
        }
        try {
            Object property = rootFolder.getSessionProperty(RESOURCE_PROPERTY_ROOT_FOLDER_TYPE);
            if (property instanceof RootFolderType) {
                return (RootFolderType) property;
            }
        } catch (CoreException e) {
            CeylonPlugin.getInstance().getLog().log(new Status(Status.WARNING, CeylonPlugin.PLUGIN_ID, "Unexpected exception", e));
        }
        return null;
    }
    
    public static RootFolderType getRootFolderType(IFile file) {
        IFolder rootFolder = getRootFolder(file);
        if (rootFolder == null) {
            return null;
        }
        return getRootFolderType(rootFolder);
    }
    
    public static boolean isInSourceFolder(IFile file) {
        return getRootFolderType(file) == RootFolderType.SOURCE;
    }

    public static String getPackageName(IResource resource) {
        if (resource instanceof IFolder) {
            return getPackage((IFolder) resource).getQualifiedNameString();
        }

        if (resource instanceof IFile) {
            return getPackage((IFile) resource).getQualifiedNameString();
        }
        return null;
    }
    
    public static Package getPackage(IFolder resource) {
        if (resource.isLinked(IResource.CHECK_ANCESTORS)) {
            return null;
        }
        Object property = null;
        if (! resource.exists()) {
            IFolder rootFolder = getRootFolder(resource);
            if (rootFolder != null) {
                IPath rootRelativePath = resource.getFullPath().makeRelativeTo(rootFolder.getFullPath());
                JDTModelLoader modelLoader = getProjectModelLoader(resource.getProject());
                if (modelLoader != null) {
                    return modelLoader.findPackage(Util.formatPath(Arrays.asList(rootRelativePath.segments()), '.'));
                }
            }
            return null;
        }
        try {
            property = resource.getSessionProperty(RESOURCE_PROPERTY_PACKAGE_MODEL);
        } catch (CoreException e) {
            CeylonPlugin.getInstance().getLog().log(new Status(Status.WARNING, CeylonPlugin.PLUGIN_ID, "Unexpected exception", e));
        }
        if (property instanceof WeakReference<?>) {
            Object pkg = ((WeakReference<?>) property).get();
            if (pkg instanceof Package) {
                return (Package) pkg;
            }
        }
        return null;
    }
    
    public static Package getPackage(IFile file) {
        if (file.getParent() instanceof IFolder) {
            return getPackage((IFolder) file.getParent());
        }
        return null;
    }    

    public static Package getPackage(VirtualFile virtualFile) {
        if (virtualFile instanceof IFileVirtualFile) {
            return getPackage(((IFileVirtualFile)virtualFile).getFile());
        }
        if (virtualFile instanceof IFolderVirtualFile) {
            return getPackage(((IFolderVirtualFile)virtualFile).getFolder());
        }
        String virtualPath = virtualFile.getPath();
        if (virtualPath.contains("!/")) { // TODO : this test could be replaced by an instanceof if the ZipEntryVirtualFile was public
            CeylonUnit ceylonUnit = getUnit(virtualFile);
            if (ceylonUnit != null) {
                return ceylonUnit.getPackage();
            }
        }
        return null;
    }    


    public static SourceFile getUnit(VirtualFile virtualFile) {
        if (virtualFile instanceof IFileVirtualFile) {
            IFile file = ((IFileVirtualFile)virtualFile).getFile();
            Package p = getPackage(file);
            if (p != null) {
                for (Unit u : p.getUnits()) {
                    if (u instanceof SourceFile && u.getFilename().equals(file.getName())) {
                        return (SourceFile) u;
                    }
                }
            }
            return null;
        }
        
        String virtualPath = virtualFile.getPath();
        if (virtualPath.contains("!/")) { // TODO : this test could be replaced by an instanceof if the ZipEntryVirtualFile was public
            for (IProject p : getProjects()) {
                JDTModuleManager moduleManager = getProjectModuleManager(p);
                if (moduleManager != null) {
                    JDTModule archiveModule = moduleManager.getArchiveModuleFromSourcePath(virtualPath);
                    if (archiveModule != null) {
                        ExternalPhasedUnit pu = archiveModule.getPhasedUnit(virtualFile);
                        if (pu != null) {
                            return pu.getUnit();
                        }
                    }
                }
            }
        }
        return null;
    }    

    public static IResourceAware getUnit(IFile file) {
        Package p = getPackage(file);
        if (p != null) {
            for (Unit u: p.getUnits()) {
                if (u instanceof IResourceAware) {
                    if (u.getFilename().equals(file.getName())) {
                        return (IResourceAware) u;
                    }
                }
            }
        }
        return null;
    }    

    public static Package getPackage(IPackageFragment packageFragment) {
        PackageFragment pkg = (PackageFragment) packageFragment;
        try {
            IFolder srcPkgFolder = (IFolder) pkg.getCorrespondingResource();
            if (srcPkgFolder != null) {
                return getPackage(srcPkgFolder);
            }
        } catch (JavaModelException e) {
        }

        IPackageFragmentRoot root = pkg.getPackageFragmentRoot();
        Modules projectModules = getProjectModules(packageFragment.getJavaProject().getProject());
        if (projectModules == null) {
            return null;
        }
        
        for (Module m : projectModules.getListOfModules()) {
            if (m instanceof JDTModule && ! m.getNameAsString().equals(Module.DEFAULT_MODULE_NAME)) {
                JDTModule module = (JDTModule) m;
                for (IPackageFragmentRoot moduleRoot : module.getPackageFragmentRoots()) {
                    if (root.getPath().equals(moduleRoot.getPath())) {
                        Package result = module.getDirectPackage(packageFragment.getElementName());
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        JDTModule defaultModule = (JDTModule) projectModules.getDefaultModule();
        for (IPackageFragmentRoot moduleRoot : defaultModule.getPackageFragmentRoots()) {
            if (root.getPath().equals(moduleRoot.getPath())) {
                Package result = defaultModule.getDirectPackage(packageFragment.getElementName());
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static JDTModule asSourceModule(IFolder moduleFolder) {
        Package p = getPackage(moduleFolder);
        if (p != null) {
            Module m = p.getModule();
            if (m instanceof JDTModule && m.getNameAsString().equals(p.getNameAsString())) {
                return (JDTModule) m;
            }
        }
        return null;
    }

    public static JDTModule asSourceModule(IPackageFragment sourceModuleFragment) {
        IFolder moduleFolder;
        try {
            moduleFolder = (IFolder) sourceModuleFragment.getCorrespondingResource();
            if (moduleFolder != null) {
                return asSourceModule(moduleFolder);
            }
        } catch (JavaModelException e) {
        }
        return null;
    }

    public static JDTModule getModule(IFolder moduleFolder) {
        Package p = getPackage(moduleFolder);
        if (p != null) {
            Module m = p.getModule();
            if (m instanceof JDTModule) {
                return (JDTModule) m;
            }
        }
        return null;
    }

    public static JDTModule getModule(IPackageFragment packageFragment) {
        Package p = getPackage(packageFragment);
        if (p != null) {
            Module m = p.getModule();
            if (m instanceof JDTModule) {
                return (JDTModule) m;
            }
        }
        return null;
    }

    public static IJavaModelAware getUnit(IJavaElement javaElement) {
        IOpenable openable = javaElement.getOpenable();
        if (openable instanceof ITypeRoot) {
            Package p = getPackage((IPackageFragment)((ITypeRoot)openable).getParent());
            if (p != null) {
                String className = ((ITypeRoot)openable).getElementName();
                if (className.equals(Naming.PACKAGE_DESCRIPTOR_CLASS_NAME+".class") ||
                		className.equals(Naming.PACKAGE_DESCRIPTOR_CLASS_NAME.substring(1)+".class")) {
                    Unit packageUnit = p.getUnit();
                    if (packageUnit instanceof IJavaModelAware && ((IJavaModelAware) packageUnit).getTypeRoot().equals(openable)) {
                        return (IJavaModelAware) packageUnit;
                    }
                }
                if (className.equals(Naming.MODULE_DESCRIPTOR_CLASS_NAME+".class") ||
                		className.equals(Naming.OLD_MODULE_DESCRIPTOR_CLASS_NAME+".class")) {
                    Unit moduleUnit = p.getModule().getUnit();
                    if (moduleUnit instanceof IJavaModelAware && ((IJavaModelAware) moduleUnit).getTypeRoot().equals(openable)) {
                        return (IJavaModelAware) moduleUnit;
                    }
                }
                for (Declaration d : p.getMembers()) {
                    Unit u = d.getUnit();
                    if (u instanceof IJavaModelAware) {
                        if (u.getFilename().equals(((ITypeRoot) openable).getElementName())) {
                            return (IJavaModelAware) u;
                        }
                    }
                }
            }
        }
        return null;
    }    

    private void cleanRemovedFilesFromOutputs(Collection<IFile> filesToRemove, 
            IProject project) {
        if (filesToRemove.size() == 0) {
            return;
        }
        
        Set<File> moduleJars = new HashSet<File>();
        
        for (IFile file : filesToRemove) {
            IFolder rootFolder = getRootFolder(file);
            if (rootFolder == null) {
                return;
            }
            String relativeFilePath = file.getProjectRelativePath().makeRelativeTo(rootFolder.getProjectRelativePath()).toString();
            Package pkg = getPackage((IFolder)file.getParent());
            if (pkg == null) {
                return;
            }
            Module module = pkg.getModule();
            TypeChecker typeChecker = typeCheckers.get(project);
            if (typeChecker == null) {
                return;
            }
            
            final File modulesOutputDirectory = getCeylonModulesOutputDirectory(project);
            boolean explodeModules = isExplodeModulesEnabled(project);
            final File ceylonOutputDirectory = explodeModules ? 
                    getCeylonClassesOutputDirectory(project) : null;
            File moduleDir = getModulePath(modulesOutputDirectory, module);
            
            boolean fileIsResource = isResourceFile(file);
            
            //Remove the classes belonging to the source file from the
            //module archive and from the JDTClasses directory
            File moduleJar = new File(moduleDir, getModuleArchiveName(module));
            if(moduleJar.exists()){
                moduleJars.add(moduleJar);
                try {
                    List<String> entriesToDelete = new ArrayList<String>();
                    ZipFile zipFile = new ZipFile(moduleJar);
                    
                    Properties mapping = CarUtils.retrieveMappingFile(zipFile);

                    if (fileIsResource) {
                        entriesToDelete.add(relativeFilePath);
                    } else {
                        for (String className : mapping.stringPropertyNames()) {
                            String sourceFile = mapping.getProperty(className);
                            if (relativeFilePath.equals(sourceFile)) {
                                entriesToDelete.add(className);
                            }
                        }
                    }

                    for (String entryToDelete : entriesToDelete) {
                        zipFile.removeFile(entryToDelete);
                        if (explodeModules) {
                            new File(ceylonOutputDirectory, 
                                    entryToDelete.replace('/', File.separatorChar))
                                    .delete();
                        }
                    }
                } catch (ZipException e) {
                    e.printStackTrace();
                }
            }
            
            if (!fileIsResource) {
                //Remove the source file from the source archive
                File moduleSrc = new File(moduleDir, getSourceArchiveName(module));
                if(moduleSrc.exists()){
                    moduleJars.add(moduleSrc);
                    try {
                        ZipFile zipFile = new ZipFile(moduleSrc);
                        FileHeader fileHeader = zipFile.getFileHeader(relativeFilePath);
                        if(fileHeader != null){
                            zipFile.removeFile(fileHeader);
                        }
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        //        final com.sun.tools.javac.util.Context dummyContext = new com.sun.tools.javac.util.Context();
        class ConsoleLog implements Logger {
            PrintWriter writer;
            ConsoleLog() {
                writer = new PrintWriter(System.out); //new PrintWriter(getConsoleStream()));
            }

            @Override
            public void error(String str) {
                writer.println("Error: " + str);
            }

            @Override
            public void warning(String str) {
                writer.println("Warning: " + str);
            }

            @Override
            public void info(String str) {
            }

            @Override
            public void debug(String str) {
            }
        }
        ConsoleLog log = new ConsoleLog();
        for (File moduleJar: moduleJars) {
            ShaSigner.sign(moduleJar, log, false);
        }
    }

    private void cleanChangedFilesFromExplodedDirectory(Collection<IFile> changedFiles, 
            IProject project) {
        if (changedFiles.size() == 0) {
            return;
        }
        
        if (! isExplodeModulesEnabled(project)) {
            return;
        }
        
        for (IFile file : changedFiles) {
            IFolder rootFolder = getRootFolder(file);
            if (rootFolder == null) {
                return;
            }

            if (isResourceFile(file)) {
                return;
            }
            
            String relativeFilePath = file.getProjectRelativePath().makeRelativeTo(rootFolder.getProjectRelativePath()).toString();
            Package pkg = getPackage((IFolder)file.getParent());
            if (pkg == null) {
                return;
            }
            Module module = pkg.getModule();
            TypeChecker typeChecker = typeCheckers.get(project);
            if (typeChecker == null) {
                return;
            }
            
            final File modulesOutputDirectory = getCeylonModulesOutputDirectory(project);
            final File ceylonOutputDirectory = getCeylonClassesOutputDirectory(project);
            File moduleDir = getModulePath(modulesOutputDirectory, module);
            
            //Remove the classes belonging to the source file from the
            //from the .exploded directory
            File moduleJar = new File(moduleDir, getModuleArchiveName(module));
            if(moduleJar.exists()){
                try {
                    List<String> entriesToDelete = new ArrayList<String>();
                    ZipFile zipFile = new ZipFile(moduleJar);
                    
                    Properties mapping = CarUtils.retrieveMappingFile(zipFile);

                    for (String className : mapping.stringPropertyNames()) {
                        String sourceFile = mapping.getProperty(className);
                        if (relativeFilePath.equals(sourceFile)) {
                            entriesToDelete.add(className);
                        }
                    }

                    for (String entryToDelete : entriesToDelete) {
                        new File(ceylonOutputDirectory, 
                                entryToDelete.replace('/', File.separatorChar))
                                .delete();
                    }
                } catch (ZipException e) {
                    e.printStackTrace();
                }
            }
        }
    }    
    private static File getCeylonClassesOutputDirectory(IProject project) {
        return getCeylonClassesOutputFolder(project)
                .getRawLocation().toFile();
    }

    public static IFolder getCeylonClassesOutputFolder(IProject project) {
        return project.getFolder(CEYLON_CLASSES_FOLDER_NAME);
    }
    
    public static boolean isInCeylonClassesOutputFolder(IPath path) {
        //TODO: this is crap!
        return path.lastSegment().equals(CEYLON_CLASSES_FOLDER_NAME);
    }

    public static File getCeylonModulesOutputDirectory(IProject project) {
        return getCeylonModulesOutputFolder(project).getRawLocation().toFile();
    }
    
    public static IFolder getCeylonModulesOutputFolder(IProject project) {
        IPath path = CeylonProjectConfig.get(project).getOutputRepoPath();
        return project.getFolder(path.removeFirstSegments(1));
    }
    
    public static String getCeylonSystemRepo(IProject project) {
        String systemRepo = (String) getBuilderArgs(project).get("systemRepo");
        if (systemRepo == null || systemRepo.isEmpty()) {
            systemRepo = "${ceylon.repo}";
        }
        return systemRepo;
    }
    
    public static String getInterpolatedCeylonSystemRepo(IProject project) {
        return interpolateVariablesInRepositoryPath(getCeylonSystemRepo(project));
    }    

    public static String[] getDefaultUserRepositories() {
        return new String[]{
                "${ceylon.repo}",
                "${user.home}/.ceylon/repo",
                Constants.REPO_URL_CEYLON
        };
    }
    
    public static String interpolateVariablesInRepositoryPath(String repoPath) {
        String userHomePath = System.getProperty("user.home");
        String pluginRepoPath = CeylonPlugin.getInstance().getCeylonRepository().getAbsolutePath();
        return repoPath.replace("${user.home}", userHomePath).
                replace("${ceylon.repo}", pluginRepoPath);
    }
    
    /**
     * String representation for debugging purposes
     */
    public String toString() {
        return this.getProject() == null ? 
                "CeylonBuilder for unknown project" : 
                "CeylonBuilder for " + getProject().getName();
    }

    public static void setContainerInitialized(IProject project) {
        containersInitialized.add(project);
    }
    
    public static boolean isContainerInitialized(IProject project) {
        return containersInitialized.contains(project);
    }
    
    public static boolean allClasspathContainersInitialized() {
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            if (project.isAccessible() && CeylonNature.isEnabled(project)
                    && ! containersInitialized.contains(project)) {
                return false;
            }
        }
        return true;
    }

    public static ModuleDependencies getModuleDependenciesForProject(
            IProject project) {
        return projectModuleDependencies.get(project);
    }
}
