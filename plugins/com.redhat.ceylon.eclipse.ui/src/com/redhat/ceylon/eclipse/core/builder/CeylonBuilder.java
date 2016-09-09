package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;
import static com.redhat.ceylon.compiler.java.util.Util.getModuleArchiveName;
import static com.redhat.ceylon.compiler.java.util.Util.getModulePath;
import static com.redhat.ceylon.compiler.java.util.Util.getSourceArchiveName;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathContainers;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.vfsJ2C;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.CeylonHelper.list;
import static com.redhat.ceylon.eclipse.util.CeylonHelper.td;
import static com.redhat.ceylon.eclipse.util.CeylonHelper.toJavaStringList;
import static com.redhat.ceylon.eclipse.util.InteropUtils.toJavaString;
import static com.redhat.ceylon.model.typechecker.model.Module.LANGUAGE_MODULE_NAME;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.antlr.runtime.CommonToken;
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
import org.eclipse.core.resources.IWorkspaceRoot;
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
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.ArtifactCreator;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.ceylon.CeylonUtils;
import com.redhat.ceylon.cmr.impl.ShaSigner;
import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.common.Backends;
import com.redhat.ceylon.common.Constants;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.DefaultToolOptions;
import com.redhat.ceylon.common.log.Logger;
import com.redhat.ceylon.compiler.CeylonCompileTool;
import com.redhat.ceylon.compiler.java.codegen.CeylonCompilationUnit;
import com.redhat.ceylon.compiler.java.codegen.CeylonFileObject;
import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.loader.ModelLoaderFactory;
import com.redhat.ceylon.compiler.java.loader.TypeFactory;
import com.redhat.ceylon.compiler.java.loader.UnknownTypeCollector;
import com.redhat.ceylon.compiler.java.tools.CeylonLog;
import com.redhat.ceylon.compiler.java.tools.CeyloncFileManager;
import com.redhat.ceylon.compiler.java.tools.CeyloncTaskImpl;
import com.redhat.ceylon.compiler.java.tools.JarEntryFileObject;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler;
import com.redhat.ceylon.compiler.java.util.RepositoryLister;
import com.redhat.ceylon.compiler.js.JsCompiler;
import com.redhat.ceylon.compiler.js.util.Options;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.analyzer.Warning;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.AnalysisMessage;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.UnexpectedError;
import com.redhat.ceylon.compiler.typechecker.util.WarningSuppressionVisitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil;
import com.redhat.ceylon.eclipse.core.classpath.CeylonLanguageModuleContainer;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.CeylonHelper;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.redhat.ceylon.ide.common.model.BaseCeylonProject;
import com.redhat.ceylon.ide.common.model.BaseIdeModelLoader;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.BaseIdeModuleManager;
import com.redhat.ceylon.ide.common.model.BaseIdeModuleSourceMapper;
import com.redhat.ceylon.ide.common.model.CeylonBinaryUnit;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;
import com.redhat.ceylon.ide.common.model.CeylonUnit;
import com.redhat.ceylon.ide.common.model.IJavaModelAware;
import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.model.IdeModelLoader;
import com.redhat.ceylon.ide.common.model.JavaCompilationUnit;
import com.redhat.ceylon.ide.common.model.JavaUnit;
import com.redhat.ceylon.ide.common.model.ModuleDependencies;
import com.redhat.ceylon.ide.common.model.ProjectSourceFile;
import com.redhat.ceylon.ide.common.model.ProjectState;
import com.redhat.ceylon.ide.common.model.SourceFile;
import com.redhat.ceylon.ide.common.model.delta.CompilationUnitDelta;
import com.redhat.ceylon.ide.common.model.parsing.ProjectSourceParser;
import com.redhat.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.ide.common.util.CarUtils;
import com.redhat.ceylon.ide.common.util.ProgressMonitor;
import com.redhat.ceylon.ide.common.util.ProgressMonitor$impl;
import com.redhat.ceylon.ide.common.util.ProgressMonitorChild;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import com.redhat.ceylon.ide.common.vfs.FolderVirtualFile;
import com.redhat.ceylon.ide.common.vfs.ResourceVirtualFile;
import com.redhat.ceylon.javax.tools.DiagnosticListener;
import com.redhat.ceylon.javax.tools.FileObject;
import com.redhat.ceylon.javax.tools.JavaFileObject;
import com.redhat.ceylon.langtools.source.tree.CompilationUnitTree;
import com.redhat.ceylon.langtools.source.util.TaskEvent;
import com.redhat.ceylon.langtools.source.util.TaskEvent.Kind;
import com.redhat.ceylon.langtools.source.util.TaskListener;
import com.redhat.ceylon.langtools.tools.javac.file.RegularFileObject;
import com.redhat.ceylon.langtools.tools.javac.file.RelativePath.RelativeFile;
import com.redhat.ceylon.langtools.tools.javac.tree.JCTree;
import com.redhat.ceylon.langtools.tools.javac.tree.JCTree.JCClassDecl;
import com.redhat.ceylon.langtools.tools.javac.tree.JCTree.JCCompilationUnit;
import com.redhat.ceylon.model.loader.AbstractModelLoader;
import com.redhat.ceylon.model.typechecker.context.TypeCache;
import com.redhat.ceylon.model.typechecker.model.Cancellable;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;

import ceylon.interop.java.JavaIterable;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;


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
     * A marker ID that identifies Invalid Overrides XML file problems
     */

    public static final String CEYLON_INVALID_OVERRIDES_MARKER = PLUGIN_ID + ".invalidOverridesProblem";
    /**
     * A marker ID that identifies tasks
     */
    public static final String TASK_MARKER_ID = PLUGIN_ID + ".ceylonTask";
    
    public static final String SOURCE = "Ceylon";
    
    static {
        TypeCache.setEnabledByDefault(false);
    }
    
    public static <T> T doWithCeylonModelCaching(final Callable<T> action) 
            throws CoreException {
        Boolean was = TypeCache.setEnabled(true);
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
            TypeCache.setEnabled(was);
        }
    }
    
    private static ReadWriteLock getProjectSourceModelLock(IProject project) {
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        return ceylonProject != null ? ceylonProject.getSourceModelLock() : null;
    }

    /*
     * Allows synchronizing some code that touches the source-related
     * Ceylon model, by setting up the typechecker, creating or 
     * typechecking PhasedUnits, etc ...
     * 
     * It's based on a ReentrantReadWriteLock.
     * 
     * To avoid deadlock, it always takes a time limit,
     * after which the it stops waiting for the source 
     * model availability and throw an OperationCanceled Exception
     * 
     */
    public static <T> T doWithSourceModel(
                                IProject project, 
                                boolean readonly,
                                final long waitForModelInSeconds, 
                                Callable<T> action) {
        try {
            if (project== null) {
                return action.call();
            }
            ReadWriteLock projectLock = getProjectSourceModelLock(project);
            if (projectLock == null) {
                return action.call();
            }
            Lock sourceModelLock = 
                    readonly ? 
                        projectLock.readLock() : 
                            projectLock.writeLock();
            if (sourceModelLock.tryLock(waitForModelInSeconds, TimeUnit.SECONDS)) {
                try {
                    return action.call();
                } finally {
                    sourceModelLock.unlock();
                }
            } else {
                throw new OperationCanceledException("The source model "
                        + (readonly ? "read" : "write")
                        + " lock of project " 
                        + project + " could not be acquired within "
                            + waitForModelInSeconds+ " seconds");
            }
        } catch(InterruptedException ie) {
            throw new OperationCanceledException("The thread was interrupted "
                    + "while waiting for the source model "
                    + (readonly ? "read" : "write")
                    + " lock of project " + project);
        } catch(Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class BuildFileManager extends CeyloncFileManager {
        private final IProject project;
        final boolean explodeModules;
        private Map<RegularFileObject, Set<String>> inputFilesToGenerate = null;
        
        private BuildFileManager(com.redhat.ceylon.langtools.tools.javac.util.Context context,
                boolean register, Charset charset, IProject project, Map<RegularFileObject, Set<String>> inputFilesToGenerate) {
            super(context, register, charset);
            this.project = project;
            explodeModules = isExplodeModulesEnabled(project);
            this.inputFilesToGenerate = inputFilesToGenerate;
        }

        public static RegularFileObject getSourceFile(FileObject fileObject) {
            JavaFileObject sourceJavaFileObject;
            if (fileObject instanceof JavaFileObject
                    && ((JavaFileObject) fileObject).getKind() == com.redhat.ceylon.javax.tools.JavaFileObject.Kind.SOURCE){
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
                if (expectedClasses != null) {
                    expectedClasses.remove(shortname);
                    if (expectedClasses.isEmpty()) {
                        inputFilesToGenerate.remove(sourceFile);
                    }
                } else {
                    System.out.println("WARNING : com.redhat.ceylon.eclipse.core.builder.CeylonBuilder$BuildFileManager.getFileForOutput().expectedClasses is null for source file "
                            + sourceFile
                            + "\n Is it normal ? it seems getFileForOutput was called several times on the same file during binary generation.");
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

    private static Set<IProject> containersInitialized = new HashSet<IProject>();

    public static final String CEYLON_CONSOLE= "Ceylon Build";
    //private long startTime;

    public static boolean isModelTypeChecked(IProject project) {
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getTypechecked();
        }
        return false;
    }
    
    public static boolean isModelParsed(IProject project) {
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getParsed();
        }
        return false;
    }

    
    public static List<PhasedUnit> getUnits(IProject project) {
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        if (ceylonProject != null) {
            return list(PhasedUnit.class,
                        ceylonProject.getParsedUnits());
        }
        return Collections.emptyList();
    }

    public static List<PhasedUnit> getUnits() {
        return list(PhasedUnit.class, 
                    modelJ2C().ceylonModel().getParsedUnits());
    }
    
//    public static List<PhasedUnit> getUnits(String[] projects) {
//        List<PhasedUnit> result = new ArrayList<PhasedUnit>();
//        if (projects!=null) {
//            for (Map.Entry<IProject, TypeChecker> me: typeCheckers.entrySet()) {
//                for (String pname: projects) {
//                    if (me.getKey().getName().equals(pname)) {
//                        IProject project = me.getKey();
//                        if (isModelParsed(project)) {
//                            result.addAll(me.getValue().getPhasedUnits().getPhasedUnits());
//                        }
//                    }
//                }
//            }
//        }
//        return result;
//    }
    
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

    @SuppressWarnings("unchecked")
    public static IdeModelLoader<IProject, IResource, IFolder, IFile, ITypeRoot, IType> getModelLoader(TypeChecker typeChecker) {
        BaseIdeModuleManager moduleManager = (BaseIdeModuleManager) 
                typeChecker.getPhasedUnits().getModuleManager();
        return (IdeModelLoader<IProject, IResource, IFolder, IFile, ITypeRoot, IType>) moduleManager.getModelLoader();
    }

    public static IdeModelLoader<IProject, IResource, IFolder, IFile, ITypeRoot, IType> getProjectModelLoader(IProject project) {
        TypeChecker typeChecker = 
                getProjectTypeChecker(project);
        if (typeChecker == null) {
            return null;
        }
        return getModelLoader(typeChecker);
    }

    public static BaseIdeModuleManager getProjectModuleManager(IProject project) {
        BaseIdeModelLoader modelLoader = 
                getProjectModelLoader(project);
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
        modelJ2C().ceylonModel().addProject(project.getProject());
        final CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject = modelJ2C().ceylonModel().getProject(project);
        final IJavaProject javaProject = JavaCore.create(project);
        
        final ProgressMonitor$impl<IProgressMonitor>.Progress  ceylonMonitor = utilJ2C().wrapProgressMonitor(new ProgressMonitorWrapper(mon) {
            @Override
            public boolean isCanceled() {
                return super.isCanceled() || 
                        PlatformUI.getWorkbench().isClosing();
            }
        }).Progress$new$(1000, ceylon.language.String.instance("Ceylon build of project " + project.getName()));
        try {    
            try {
                buildHook.startBuild(kind, args, project, getBuildConfig(), getContext(), ceylonMonitor.newChild(10).getWrapped());
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
                
                boolean somethingToDo = chooseBuildTypeFromDeltas(kind, ceylonProject,
                        projectDeltas, mustDoFullBuild, mustResolveClasspathContainer);
                
                if (!somethingToDo && (args==null || !args.containsKey(BUILDER_ID + ".reentrant"))) {
                    return project.getReferencedProjects();
                }
                
                if (ceylonMonitor.isCancelled()) {
                    throw new OperationCanceledException();
                }
                
                if (mustResolveClasspathContainer.value) {
                    if (cpContainers != null) {
                        buildHook.resolvingClasspathContainer(cpContainers);
                        for (IClasspathContainer container: cpContainers) {
                            if (container instanceof CeylonProjectModulesContainer) {
                                CeylonProjectModulesContainer applicationModulesContainer = (CeylonProjectModulesContainer) container;
                                boolean changed = applicationModulesContainer.resolveClasspath(ceylonMonitor.newChild(150), true);
                                if(changed) {
                                    buildHook.setAndRefreshClasspathContainer();
                                    JavaCore.setClasspathContainer(applicationModulesContainer.getPath(), 
                                            new IJavaProject[]{javaProject}, 
                                            new IClasspathContainer[]{null} , ceylonMonitor.newChild(25).getWrapped());
                                    applicationModulesContainer.refreshClasspathContainer(ceylonMonitor.newChild(25));
                                }
                            }
                        }
                    }
                }
    
                ceylonMonitor.updateRemainingWork(800);
                
                if (ceylonMonitor.isCancelled()) {
                    throw new OperationCanceledException();
                }
                
                final TypeChecker typeChecker;
                Collection<IFile> filesForBinaryGeneration = Collections.emptyList();
    
                if (mustDoFullBuild.value) {
                    ceylonMonitor.changeTaskName("Full Ceylon build of project " + project.getName());
                    buildHook.doFullBuild();
                    
                    if (ceylonMonitor.isCancelled()) {
                        throw new OperationCanceledException();
                    }
                    
                    cleanupModules(ceylonMonitor.getWrapped(), project);
                    ceylonMonitor.worked(1);
                    cleanupJdtClasses(ceylonMonitor.getWrapped(), project);
                    ceylonMonitor.worked(1);
                    
                    ceylonMonitor.subTask("Clearing existing markers of project " + project.getName());
                    clearProjectMarkers(project, true, false);
                    ceylonMonitor.worked(1);
                    clearMarkersOn(project, true);
                    ceylonMonitor.worked(1);
                    
                    if (ceylonMonitor.isCancelled()) {
                        throw new OperationCanceledException();
                    }
    
                    //if (! getModelState(project).equals(ModelState.Parsed)) {
                    if (!mustResolveClasspathContainer.value) {
                        ceylonMonitor.subTask("Parsing source of project " + project.getName());
                        //if we already resolved the classpath, the
                        //model has already been freshly-parsed
                        buildHook.parseCeylonModel();
                        ceylonProject.parseCeylonModel(
                                ceylonMonitor.newChild(96));
                    }                
                    typeChecker = ceylonProject.getTypechecker();
                    
                    ceylonMonitor.updateRemainingWork(700);
                    if (ceylonMonitor.isCancelled()) {
                        throw new OperationCanceledException();
                    }
    
                    ceylonMonitor.subTask("Typechecking all source  files of project " + project.getName());
                    builtPhasedUnits = fullTypeCheck(ceylonProject, typeChecker, 
                            ceylonMonitor.newChild(300));
    
                    if (ceylonMonitor.isCancelled()) {
                        throw new OperationCanceledException();
                    }
                    
                    filesForBinaryGeneration = list(IFile.class,ceylonProject.getProjectNativeFiles());
                }
                else
                {
                    buildHook.doIncrementalBuild();
                    typeChecker = ceylonProject.getTypechecker();
                    PhasedUnits phasedUnits = typeChecker.getPhasedUnits();
    
                    
                    List<IFile> filesToRemove = new ArrayList<IFile>();
                    Set<IFile> changedFiles = new HashSet<IFile>(); 
    
                    ceylonMonitor.getWrapped().setTaskName("Incremental Ceylon build of project " + project.getName());
    
                    ceylonMonitor.subTask("Scanning deltas of project " + project.getName()); 
                    scanChanges(ceylonProject, currentDelta, projectDeltas, filesToRemove, 
                            changedFiles);
                    ceylonMonitor.worked(9);
                    
                    if (ceylonMonitor.isCancelled()) {
                        throw new OperationCanceledException();
                    }
    
                    if (!isModelTypeChecked(project)) {
                        buildHook.fullTypeCheckDuringIncrementalBuild();
                        if (ceylonMonitor.isCancelled()) {
                            throw new OperationCanceledException();
                        }
    
                        ceylonMonitor.subTask("Clearing existing markers of project (except backend errors)" + project.getName());
                        clearProjectMarkers(project, true, false);
                        clearMarkersOn(project, false);
                        ceylonMonitor.worked(1);
    
                        ceylonMonitor.subTask("Initial typechecking all source files of project " + project.getName());
                        
                        builtPhasedUnits = fullTypeCheck(ceylonProject, typeChecker, 
                                        ceylonMonitor.newChild(170));
    
                        if (ceylonMonitor.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        
                        ceylonMonitor.subTask("Collecting dependencies of project " + project.getName());
    //                  getConsoleStream().println(timedMessage("Collecting dependencies"));
                        collectDependencies(project, typeChecker, builtPhasedUnits);
                        ceylonMonitor.worked(17);
                        
                        ceylonMonitor.subTask("Collecting problems for project " 
                                + project.getName());
                        addProblemAndTaskMarkers(builtPhasedUnits, project);
                        ceylonMonitor.worked(3);
                    }
                    
                    ceylonMonitor.updateRemainingWork(600);
    
                    ceylonMonitor.subTask("Scanning dependencies of deltas of project " + project.getName()); 
                    final Set<IFile> filesToCompile = new HashSet<>();
                    final Set<IFile> filesToTypecheck = new HashSet<>();
                           
                    calculateDependencies(ceylonProject, currentDelta, 
                            changedFiles, typeChecker, phasedUnits, filesToTypecheck, filesToCompile, ceylonMonitor);
                    ceylonMonitor.worked(10);
                    
                    if (ceylonMonitor.isCancelled()) {
                        throw new OperationCanceledException();
                    }
                        
                    ceylonMonitor.subTask("Cleaning files and markers for project " + project.getName());
                    cleanRemovedFilesFromCeylonModel(filesToRemove, phasedUnits, ceylonProject);
                    ceylonMonitor.worked(3);
                    cleanRemovedFilesFromOutputs(filesToRemove, ceylonProject);
                    ceylonMonitor.worked(4);
                    
                    if (ceylonMonitor.isCancelled()) {
                        throw new OperationCanceledException();
                    }
    
                    buildHook.incrementalBuildSources(changedFiles, filesToRemove, filesToTypecheck);
                    
                    clearProjectMarkers(project, true, false);
                    ceylonMonitor.worked(1);
                    clearMarkersOn(filesToTypecheck, true);
                    ceylonMonitor.worked(1);
                    clearMarkersOn(filesToCompile, true, true);
                    ceylonMonitor.worked(1);
    
                    ceylonMonitor.subTask("Compiling " + filesToTypecheck.size() + " source files in project " + 
                            project.getName());
                    builtPhasedUnits = doWithCeylonModelCaching(new Callable<List<PhasedUnit>>() {
                        @Override
                        public List<PhasedUnit> call() throws Exception {
                            return incrementalBuild(ceylonProject, filesToTypecheck, 
                                    ceylonMonitor.newChild(190));
                            
                        }
                    });
                    
                    if (builtPhasedUnits.isEmpty() && filesToTypecheck.isEmpty() && filesToCompile.isEmpty()) {
                        return project.getReferencedProjects();
                    }
                    
                    if (ceylonMonitor.isCancelled()) {
                        throw new OperationCanceledException();
                    }
    
                    buildHook.incrementalBuildResult(builtPhasedUnits);
    
                    filesForBinaryGeneration = filesToCompile;
                
                }
                
                clearProjectMarkers(project, false, true);
    
                ceylonMonitor.updateRemainingWork(400);
                
                ceylonMonitor.subTask("Collecting problems for project " 
                        + project.getName());
                addProblemAndTaskMarkers(builtPhasedUnits, project);
                ceylonMonitor.worked(3);
    
                if (ceylonMonitor.isCancelled()) {
                    throw new OperationCanceledException();
                }
    
                ceylonMonitor.subTask("Collecting dependencies of project " + project.getName());
    //            getConsoleStream().println(timedMessage("Collecting dependencies"));
                collectDependencies(project, typeChecker, builtPhasedUnits);
                ceylonMonitor.worked(17);
        
                if (ceylonMonitor.isCancelled()) {
                    throw new OperationCanceledException();
                }
    
                buildHook.beforeGeneratingBinaries();
                ceylonMonitor.subTask("Generating binaries for project " + project.getName());
                
                final Collection<IFile> filesToProcess = filesForBinaryGeneration;
                final Collection<PhasedUnit> unitsTypecheckedIncrementally = mustDoFullBuild.value ? Collections.<PhasedUnit>emptyList() : builtPhasedUnits;
                cleanChangedFilesFromExplodedDirectory(filesToProcess, ceylonProject);
                doWithCeylonModelCaching(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws CoreException {
                        return generateBinaries(javaProject, unitsTypecheckedIncrementally,
                                filesToProcess, typeChecker, 
                                ceylonMonitor.newChild(370));
                    }
                });
                buildHook.afterGeneratingBinaries();
              
                if (ceylonMonitor.isCancelled()) {
                    throw new OperationCanceledException();
                }
    
                if (isExplodeModulesEnabled(project)) {
                    ceylonMonitor.subTask("Rebuilding using exploded modules directory of " + project.getName());
                    sheduleIncrementalRebuild(args, project, ceylonMonitor.newChild(10));
                }
                
                return project.getReferencedProjects();
            } catch(OperationCanceledException e) {
                e.printStackTrace();
                throw e;
            } finally {
                buildHook.endBuild();
            }
        } finally {
            ceylonMonitor.destroy(null);
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
        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = modelJ2C().ceylonModel().getProject(javaProject.getProject());
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
        
        if (! ceylonProject.getSynchronizedWithConfiguration()) {
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

    private void sheduleIncrementalRebuild(@SuppressWarnings("rawtypes") Map args, final IProject project, 
            ProgressMonitorChild<IProgressMonitor> monitor) {
        try {
            getCeylonClassesOutputFolder(project).refreshLocal(DEPTH_INFINITE, monitor.getWrapped());
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
            PhasedUnits phasedUnits, CeylonProject<IProject, IResource, IFolder, IFile> project) {
        for (IFile fileToRemove: filesToRemove) {
            if(isCeylon(fileToRemove)) {
                // Remove the ceylon phasedUnit (which will also remove the unit from the package)
                PhasedUnit phasedUnitToDelete = phasedUnits.getPhasedUnit(vfsJ2C().createVirtualResource(fileToRemove, project.getIdeArtifact()));
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

    private void calculateDependencies(CeylonProject<IProject, IResource,IFolder, IFile> ceylonProject,
            IResourceDelta currentDelta,
            Collection<IFile> changedFiles, TypeChecker typeChecker, 
            PhasedUnits phasedUnits, Set<IFile> filesToTypeCheck, Set<IFile> filesToCompile, Cancellable cancellable) {

        IProject project = ceylonProject.getIdeArtifact();
        Set<IFile> filesToAddInTypecheck = new HashSet<IFile>();
        Set<IFile> filesToAddInCompile = new HashSet<IFile>();

        if (!changedFiles.isEmpty()) {
            Set<IFile> allTransitivelyDependingFiles = searchForDependantFiles(
                    project, changedFiles, typeChecker, cancellable,
                    false);

            Set<IFile> dependingFilesAccordingToStructureDelta;
            boolean astAwareIncrementalBuild = areAstAwareIncrementalBuildsEnabled(project);
            if (astAwareIncrementalBuild) {
                dependingFilesAccordingToStructureDelta = searchForDependantFiles(
                        project, changedFiles, typeChecker, cancellable,
                        true);
            } else {
                dependingFilesAccordingToStructureDelta = allTransitivelyDependingFiles;
            }

            if (cancellable.isCancelled()) {
                throw new OperationCanceledException();
            }
            
            for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                Unit unit = phasedUnit.getUnit();
                if (unit.getUnresolvedReferences()) {
                    IFile fileToAdd = vfsJ2C().getIFileVirtualFile(phasedUnit.getUnitFile()).getNativeResource();
                    if (fileToAdd.exists()) {
                        filesToAddInTypecheck.add(fileToAdd);
                        filesToAddInCompile.add(fileToAdd);
                    }
                }
                Set<Declaration> duplicateDeclarations = unit.getDuplicateDeclarations();
                if (!duplicateDeclarations.isEmpty()) {
                    IFile fileToAdd = vfsJ2C().getIFileVirtualFile(phasedUnit.getUnitFile()).getNativeResource();
                    if (fileToAdd.exists()) {
                        filesToAddInTypecheck.add(fileToAdd);
                        filesToAddInCompile.add(fileToAdd);
                    }
                    for (Declaration duplicateDeclaration : duplicateDeclarations) {
                        Unit duplicateUnit = duplicateDeclaration.getUnit();
                        if ((duplicateUnit instanceof SourceFile) && 
                            (duplicateUnit instanceof IResourceAware)) {
                            IResourceAware<IProject,IFolder,IFile> ra = (IResourceAware<IProject,IFolder,IFile>) duplicateUnit;
                            IFile duplicateDeclFile = ra.getResourceFile();
                            if (duplicateDeclFile != null && duplicateDeclFile.exists()) {
                                filesToAddInTypecheck.add(duplicateDeclFile);
                                filesToAddInCompile.add(duplicateDeclFile);
                            }
                        }
                    }
                }
            }
            
            if (cancellable.isCancelled()) {
                throw new OperationCanceledException();
            }
    
            for (IFile f: allTransitivelyDependingFiles) {
                if (f.getProject() == project) {
                    if (isSourceFile(f) || isResourceFile(f)) {
                        if (f.exists()) {
                            filesToAddInTypecheck.add(f);
                            if (!astAwareIncrementalBuild || dependingFilesAccordingToStructureDelta.contains(f) || isJava(f)) {
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
        
        for (IFile file : list(IFile.class, ceylonProject.getProjectNativeFiles())) {
            try {
                boolean backendErrorOrMissingClassFile = false;
                for (IMarker marker : file.findMarkers(PROBLEM_MARKER_ID + ".backend", false, IResource.DEPTH_ZERO)) {
                    Object attribute = marker.getAttribute(IMarker.SEVERITY);
                    if (attribute instanceof Integer) {
                        switch (((Integer)attribute).intValue()) {
                        case IMarker.SEVERITY_ERROR:
                            // For backend errors
                        case IMarker.SEVERITY_INFO:
                            // For missing class files
                            backendErrorOrMissingClassFile = true;
                        }
                        if (backendErrorOrMissingClassFile) {
                            filesToAddInTypecheck.add(file);
                            filesToAddInCompile.add(file);
                            break;
                        }
                    }
                }
            } catch (CoreException e) {
                e.printStackTrace();
                filesToAddInTypecheck.add(file);
                filesToAddInCompile.add(file);
            }
        }

        filesToTypeCheck.addAll(filesToAddInTypecheck);
        filesToCompile.addAll(filesToAddInCompile);
    }

    private Set<IFile> searchForDependantFiles(IProject project,
            Collection<IFile> changedFiles, TypeChecker typeChecker,
            Cancellable cancellable, boolean filterAccordingToStructureDelta) {
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
                CeylonProject<IProject, IResource, IFolder, IFile> currentFileCeylonProject = modelJ2C().ceylonModel().getProject(currentFileProject);
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
                        currentFileTypeChecker, currentFileCeylonProject);
   
                for (String dependingFile: filesDependingOn) {
                    if (cancellable.isCancelled()) {
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

    private void scanChanges(final CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject, final IResourceDelta currentDelta, 
            List<IResourceDelta> projectDeltas, final List<IFile> filesToRemove, 
            final Set<IFile> changedSources) 
                    throws CoreException {
        for (final IResourceDelta projectDelta: projectDeltas) {
            if (projectDelta != null) {
                final IProject project = (IProject) projectDelta.getResource();
                List<IFolder> allRootFolders = new ArrayList<>();
                allRootFolders.addAll(getSourceFolders(project));
                allRootFolders.addAll(getResourceFolders(project));
                ceylonProject.getRootFolders();
                
                final Iterable<FolderVirtualFile<IProject, IResource, IFolder, IFile>> iter = 
                        (Iterable<FolderVirtualFile<IProject, IResource, IFolder, IFile>>) 
                        new JavaIterable<FolderVirtualFile<IProject, IResource, IFolder, IFile>>(
                                td(FolderVirtualFile.class), 
                                (ceylon.language.Iterable<? extends FolderVirtualFile<IProject, IResource, IFolder, IFile>, ? extends Object>) ceylonProject.getRootFolders());
                for (final FolderVirtualFile<IProject, IResource, IFolder, IFile> rootVirtualFolder : iter) {
                    IFolder rootFolder = rootVirtualFolder.getNativeResource();
                    IResourceDelta affectedRoot = projectDelta.findMember(rootFolder.getProjectRelativePath());
                    if (affectedRoot != null) {
                        RootFolderType rootFolderType = getRootFolderType(rootFolder);
                        final boolean inSourceDirectory = rootFolderType == RootFolderType.SOURCE;
                        final boolean inResourceDirectory = rootFolderType == RootFolderType.RESOURCE;
                        
                        if (inResourceDirectory || inSourceDirectory) {
                            // a real Ceylon source or resource folder so scan for changes
                            affectedRoot.accept(new IResourceDeltaVisitor() {
                                public boolean visit(IResourceDelta delta) throws CoreException {
                                    IResource resource = delta.getResource();
                                    if (resource instanceof IFile) {
                                        IFile file= (IFile) resource;
                                        if (inResourceDirectory || (isCompilable(file) && inSourceDirectory) ) {
                                            changedSources.add(file);
                                            if (projectDelta == currentDelta) {
                                                if (delta.getKind() == IResourceDelta.REMOVED) {
                                                    filesToRemove.add(file);
                                                    ceylonProject.removeFileFromModel(file);
                                                }
                                                if (delta.getKind() == IResourceDelta.ADDED) {
                                                    IFile addedFile = (IFile) resource;
                                                    ceylonProject.addFileToModel(addedFile);
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
                                                    ceylonProject.addFolderToModel(folder);
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
    }

    public boolean chooseBuildTypeFromDeltas(final int kind, final CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject,
            final List<IResourceDelta> currentDeltas,
            final BooleanHolder mustDoFullBuild,
            final BooleanHolder mustResolveClasspathContainer) {
        
        mustDoFullBuild.value = kind == FULL_BUILD || kind == CLEAN_BUILD || 
                !ceylonProject.getParsed();
        mustResolveClasspathContainer.value = kind==FULL_BUILD; //false;
        final BooleanHolder somethingToBuild = new BooleanHolder();
        
        if (JavaProjectStateMirror.hasClasspathChanged(ceylonProject.getIdeArtifact())) {
            mustDoFullBuild.value = true;
        }
        if (!mustDoFullBuild.value || !mustResolveClasspathContainer.value) {
            for (IResourceDelta currentDelta: currentDeltas) {
                if (currentDelta != null) {
                    try {
                        currentDelta.accept(new DeltaScanner(mustDoFullBuild, ceylonProject,
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
                        ! ceylonProject.getTypechecked();
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
            CeylonProject<IProject, IResource, IFolder, IFile> currentFileCeylonProject) {
        
        if (isCeylon(srcFile)) {
            PhasedUnit phasedUnit = currentFileTypeChecker.getPhasedUnits()
                    .getPhasedUnit(vfsJ2C().createVirtualResource(srcFile, currentFileCeylonProject.getIdeArtifact()));
            if (phasedUnit != null && phasedUnit.getUnit() != null) {
                return phasedUnit.getUnit().getDependentsOf();
            }
        } 
        else {
            Unit unit = getJavaUnit(currentFileCeylonProject.getIdeArtifact(), srcFile);
            if (unit instanceof JavaCompilationUnit) {
                return unit.getDependentsOf();
            }
        }
        
        return Collections.emptySet();
    }
    
    @SuppressWarnings("unchecked")
    static ProjectPhasedUnit<IProject, IResource, IFolder, IFile> parseFileToPhasedUnit(final ModuleManager moduleManager, final ModuleSourceMapper moduleSourceMapper,
            final TypeChecker typeChecker,
            final FileVirtualFile<IProject, IResource, IFolder, IFile> file, 
            final FolderVirtualFile<IProject, IResource, IFolder, IFile> srcDir,
            final Package pkg) {
        return (ProjectPhasedUnit<IProject, IResource, IFolder, IFile>) new ProjectSourceParser<IProject, IResource, IFolder, IFile>(
                td(IProject.class), 
                td(IResource.class), 
                td(IFolder.class), 
                td(IFile.class), 
                file.getCeylonProject(), file, srcDir).parseFileToPhasedUnit(moduleManager, typeChecker, file, srcDir, pkg);
    }

    private List<PhasedUnit> incrementalBuild(final CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject, final Collection<IFile> sourceToCompile,
            final ProgressMonitor<IProgressMonitor> mon) {
        final IProject project = ceylonProject.getIdeArtifact();
        
        final TypeChecker typeChecker = ceylonProject.getTypechecker();
        final PhasedUnits pus = typeChecker.getPhasedUnits();
        final BaseIdeModuleManager moduleManager = (BaseIdeModuleManager) pus.getModuleManager(); 
        final BaseIdeModuleSourceMapper moduleSourceMapper = (BaseIdeModuleSourceMapper) pus.getModuleSourceMapper(); 
        final BaseIdeModelLoader modelLoader = getModelLoader(typeChecker);

        return doWithSourceModel(project, false, 20, new Callable<List<PhasedUnit>>() {
            @Override
            public List<PhasedUnit> call() {
                List<BaseIdeModule> modulesToRefresh = new ArrayList<>();
                for (Module m : typeChecker.getContext().getModules().getListOfModules()) {
                    if (m instanceof BaseIdeModule) {
                        BaseIdeModule module = (BaseIdeModule) m;
                        if (module.getIsCeylonArchive()) {
                            modulesToRefresh.add(module);
                        }
                    }
                }
                long ceylonArchivesRefreshingTicks = modulesToRefresh.size()*10;
                long sourceTypecheckingTicks = sourceToCompile.size()*10;
                long dependenciesTypecheckingTicks = typeChecker.getPhasedUnitsOfDependencies().size()*6;
                long sourceUpdatingTicks = sourceToCompile.size()*4;
                final ProgressMonitor$impl<IProgressMonitor>.Progress progress = 
                        mon.Progress$new$(
                                ceylonArchivesRefreshingTicks 
                                + dependenciesTypecheckingTicks 
                                + sourceUpdatingTicks
                                + sourceTypecheckingTicks
                                , null);
                try {
                    
                    // First refresh the modules that are cross-project references to sources modules
                    // in referenced projects. This will :
                    // - clean the binary declarations and reload the class-to-source mapping file for binary-based modules,
                    // - remove old PhasedUnits and parse new or updated PhasedUnits from the source archive for source-based modules
                    
                    progress.changeTaskName("RefreshingCeylonArchives");
                    for (BaseIdeModule moduleToRefresh : modulesToRefresh) {
                        moduleToRefresh.refresh();
                        progress.worked(10);
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
                        progress.subTask("scanning declarations in " + pu.getUnit().getFilename());
                        pu.scanDeclarations();
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(1);
                    }
                    for (PhasedUnit pu: dependencies) {
                        progress.subTask("scanning types in " + pu.getUnit().getFilename());
                        pu.scanTypeDeclarations(Cancellable.ALWAYS_CANCELLED);
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(2);
                    }
                    for (PhasedUnit pu: dependencies) {
                        progress.subTask("validating refinement in " + pu.getUnit().getFilename());
                        pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(1);
                    }
                    for (PhasedUnit pu: dependencies) {
                        progress.subTask("analysing types in " + pu.getUnit().getFilename());
                        pu.analyseTypes(Cancellable.ALWAYS_CANCELLED); // Needed to have the right values in the Value.trans field (set in Expression visitor)
                                            // which in turn is important for debugging !
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(2);
                    }
                    
                    // Then typecheck the changed source of this project
                    
                    Set<String> cleanedPackages = new HashSet<String>();
                    
                    List<PhasedUnit> phasedUnitsToUpdate = new ArrayList<PhasedUnit>();
                    
                    progress.changeTaskName(
                            "Updating " + sourceToCompile.size() + " source files of project " + project.getName());

                    for (IFile fileToUpdate : sourceToCompile) {
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        // skip non-ceylon files
                        if(!isCeylon(fileToUpdate)) {
                            if (isJava(fileToUpdate)) {
                                Unit toRemove = getJavaUnit(project, fileToUpdate);
                                if(toRemove instanceof JavaUnit) {
                                    ((JavaUnit) toRemove).update();
                                }
                                else {
                                    String packageName = getPackageName(fileToUpdate);
                                    if (! cleanedPackages.contains(packageName)) {
                                        modelLoader.clearCachesOnPackage(packageName);
                                        cleanedPackages.add(packageName);
                                    }
                                }
                            }
                            progress.worked(4);
                            continue;
                        }
                        
                        FileVirtualFile<IProject, IResource, IFolder, IFile> file = vfsJ2C().createVirtualFile(fileToUpdate, ceylonProject.getIdeArtifact());
                        IFolder srcFolder = getRootFolder(fileToUpdate);
    
                        ProjectPhasedUnit alreadyBuiltPhasedUnit = (ProjectPhasedUnit) pus.getPhasedUnit(file);
    
                        Package pkg = null;
                        if (alreadyBuiltPhasedUnit!=null) {
                            // Editing an already built file
                            pkg = alreadyBuiltPhasedUnit.getPackage();
                        }
                        else {
                            IFolder packageFolder = (IFolder) file.getNativeResource().getParent();
                            pkg = getPackage(packageFolder);
                        }
                        if (srcFolder == null || pkg == null) {
                            progress.worked(4);
                            continue;
                        }
                        FolderVirtualFile<IProject, IResource, IFolder, IFile> srcDir = vfsJ2C().createVirtualFolder(project, srcFolder.getProjectRelativePath());
                        PhasedUnit newPhasedUnit = parseFileToPhasedUnit(moduleManager, moduleSourceMapper, typeChecker, file, srcDir, pkg);
                        phasedUnitsToUpdate.add(newPhasedUnit);
                        progress.worked(4);
                    }
                    if (progress.isCancelled()) {
                        throw new OperationCanceledException();
                    }
                    if (phasedUnitsToUpdate.size() == 0) {
                        return phasedUnitsToUpdate;
                    }
                    
                    progress.changeTaskName(
                            "Typechecking " + sourceToCompile.size() + " source files of project " + project.getName());
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        assert(phasedUnit instanceof ProjectPhasedUnit);
                        ((ProjectPhasedUnit)phasedUnit).install();
                        progress.worked(1);
                    }
                    
                    modelLoader.setupSourceFileObjects(phasedUnitsToUpdate);
                    
                    if (progress.isCancelled()) {
                        throw new OperationCanceledException();
                    }
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        if (! phasedUnit.isDeclarationsScanned()) {
                            phasedUnit.validateTree();
                        }
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(1);
                    }
                    
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        phasedUnit.visitSrcModulePhase();
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                    }
                    
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        phasedUnit.visitRemainingModulePhase();
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                    }
                    
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        if (! phasedUnit.isDeclarationsScanned()) {
                            progress.subTask("scanning declarations " + phasedUnit.getUnit().getFilename());
                            phasedUnit.scanDeclarations();
                        }
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(1);
                    }
    
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        if (! phasedUnit.isTypeDeclarationsScanned()) {
                            progress.subTask("scanning types " + phasedUnit.getUnit().getFilename());
                            phasedUnit.scanTypeDeclarations(Cancellable.ALWAYS_CANCELLED);
                        }
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(2);
                    }
                    
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        progress.subTask("validating refinement in " + phasedUnit.getUnit().getFilename());
                        if (! phasedUnit.isRefinementValidated()) {
                            phasedUnit.validateRefinement();
                        }
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(1);
                    }
                    
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        if (! phasedUnit.isFullyTyped()) {
                            progress.subTask("typechecking " + phasedUnit.getUnit().getFilename());
                            phasedUnit.analyseTypes(Cancellable.ALWAYS_CANCELLED);
                            if (showWarnings(project)) {
                                phasedUnit.analyseUsage();
                            }
                        }
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(3);
                    }
    
                    for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
                        phasedUnit.analyseFlow();
                        if (progress.isCancelled()) {
                            throw new OperationCanceledException();
                        }
                        progress.worked(1);
                    }
    
                    UnknownTypeCollector utc = new UnknownTypeCollector();
                    for (PhasedUnit pu : phasedUnitsToUpdate) { 
                        pu.getCompilationUnit().visit(utc);
                    }
                    
                    if (progress.isCancelled()) {
                        throw new OperationCanceledException();
                    }
                    
                    return phasedUnitsToUpdate;
                } finally {
                    progress.destroy(null);
                }
            }
        });
    }

    private Unit getJavaUnit(IProject project, IFile fileToUpdate) {
        IJavaElement javaElement = (IJavaElement) fileToUpdate.getAdapter(IJavaElement.class);
        if (javaElement instanceof ICompilationUnit) {
            ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
            IJavaElement packageFragment = compilationUnit.getParent();
            BaseIdeModelLoader projectModelLoader = getProjectModelLoader(project);
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

    private List<PhasedUnit> fullTypeCheck(
                                final CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject, 
                                final TypeChecker typeChecker, 
                                final ProgressMonitor<IProgressMonitor> mon) 
                    throws CoreException {
        ceylonProject.setState(ProjectState.getProjectState$typechecking());
        final IProject project = ceylonProject.getIdeArtifact();
        List<PhasedUnit> builtPhasedUnits = 
            doWithCeylonModelCaching(new Callable<List<PhasedUnit>>() {
                @Override
                public List<PhasedUnit> call() throws CoreException {

                    final List<PhasedUnits> phasedUnitsOfDependencies = typeChecker.getPhasedUnitsOfDependencies();
                    final List<PhasedUnit> dependencies = new ArrayList<PhasedUnit>();
                    for (PhasedUnits phasedUnits: phasedUnitsOfDependencies) {
                        for (PhasedUnit phasedUnit: phasedUnits.getPhasedUnits()) {
                            dependencies.add(phasedUnit);
                        }
                    }
                    final List<PhasedUnit> listOfUnits = typeChecker.getPhasedUnits().getPhasedUnits();
    
                    final ProgressMonitor$impl<IProgressMonitor>.Progress progress = 
                            mon.Progress$new$(dependencies.size()*6+listOfUnits.size()*8, 
                                    ceylon.language.String.instance(
                                            "Typechecking " + listOfUnits.size() + " source files of project " + project.getName()));
                    try {
                        final BaseIdeModelLoader loader = getModelLoader(typeChecker);

                        return doWithSourceModel(project, false, 20, new Callable<List<PhasedUnit>>() {
                            @Override
                            public List<PhasedUnit> call() {

                                for (PhasedUnit pu: dependencies) {
                                    progress.subTask("scanning declarations in " + pu.getUnit().getFilename());
                                    pu.scanDeclarations();
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(1);
                                }
                                
                                for (PhasedUnit pu: dependencies) {
                                    progress.subTask("scanning types in " + pu.getUnit().getFilename());
                                    pu.scanTypeDeclarations(Cancellable.ALWAYS_CANCELLED);
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(2);
                                }
                                        
                                for (PhasedUnit pu: dependencies) {
                                    progress.subTask("validating refinement in " + pu.getUnit().getFilename());
                                    pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(1);
                                }
                
                                for (PhasedUnit pu: dependencies) {
                                    progress.subTask("analysing types in " + pu.getUnit().getFilename());
                                    pu.analyseTypes(Cancellable.ALWAYS_CANCELLED); // Needed to have the right values in the Value.trans field (set in Expression visitor)
                                                        // which in turn is important for debugging !
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(2);
                                }
                
                                progress.subTask("loading language module packages for project " 
                                        + project.getName());
                
                                Module languageModule = loader.getLanguageModule();
                                loader.loadPackage(languageModule, "com.redhat.ceylon.compiler.java.metadata", true);
                                loader.loadPackage(languageModule, LANGUAGE_MODULE_NAME, true);
                                loader.loadPackage(languageModule, "ceylon.language.descriptor", true);
                                loader.loadPackageDescriptors();
                                
                                progress.subTask("typechecking source files for project " 
                                        + project.getName());
                
                                for (PhasedUnit pu : listOfUnits) {
                                    if (! pu.isDeclarationsScanned()) {
                                        progress.subTask("scanning declarations in " + pu.getUnit().getFilename());
                                        pu.validateTree();
                                        pu.scanDeclarations();
                                    }
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(1);
                                }
                                
                                for (PhasedUnit pu : listOfUnits) {
                                    if (! pu.isTypeDeclarationsScanned()) {
                                        progress.subTask("scanning types in " + pu.getUnit().getFilename());
                                        pu.scanTypeDeclarations(Cancellable.ALWAYS_CANCELLED);
                                    }
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(2);
                                }
                                
                                for (PhasedUnit pu: listOfUnits) {
                                    if (! pu.isRefinementValidated()) {
                                        progress.subTask("validating refinement in " + pu.getUnit().getFilename());
                                        pu.validateRefinement();
                                    }
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(1);
                                }
                
                                for (PhasedUnit pu : listOfUnits) {
                                    if (! pu.isFullyTyped()) {
                                        progress.subTask("analysing types in " + pu.getUnit().getFilename());
                                        pu.analyseTypes(Cancellable.ALWAYS_CANCELLED);
                                        if (showWarnings(project)) {
                                            pu.analyseUsage();
                                        }
                                    }
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(3);
                                }
                                
                                for (PhasedUnit pu: listOfUnits) {
                                    progress.subTask("analysing flow in " + pu.getUnit().getFilename());
                                    pu.analyseFlow();
                                    if (progress.isCancelled()) {
                                        throw new OperationCanceledException();
                                    }
                                    progress.worked(1);
                                }
                
                                UnknownTypeCollector utc = new UnknownTypeCollector();
                                for (PhasedUnit pu : listOfUnits) { 
                                    pu.getCompilationUnit().visit(utc);
                                }
                                
                                progress.subTask("");
                                return typeChecker.getPhasedUnits().getPhasedUnits();
                            }
                        });
                    } finally {
                        progress.destroy(null);
                    }
                }
            });
        ceylonProject.setState(ProjectState.getProjectState$typechecked());
        return builtPhasedUnits;
    }

    private static void addProblemAndTaskMarkers(final List<PhasedUnit> units, 
            final IProject project) {
        for (PhasedUnit phasedUnit: units) {
            ProjectPhasedUnit<IProject,IResource,IFolder,IFile> projectPhasedUnit = (ProjectPhasedUnit<IProject,IResource,IFolder,IFile>)phasedUnit;
            IFile file = projectPhasedUnit.getResourceFile();
            CompilationUnit compilationUnit = phasedUnit.getCompilationUnit();
            compilationUnit.visit(new WarningSuppressionVisitor<Warning>(Warning.class,
                    getSuppressedWarnings(project)));
            compilationUnit.visit(new MarkerCreator(file));
            addTaskMarkers(file, phasedUnit.getTokens());
        }
    }

    private boolean generateBinaries(IJavaProject javaProject,
            Collection<PhasedUnit> unitsTypecheckedIncrementally,
            Collection<IFile> filesToCompile, TypeChecker typeChecker, 
            ProgressMonitor<IProgressMonitor> monitor) throws CoreException {
        List<String> options = new ArrayList<String>();
        List<File> js_srcdir = new ArrayList<File>();
        List<File> js_rsrcdir = new ArrayList<File>();
        List<String> js_repos = new ArrayList<String>();
        String js_verbose = null;
        String js_outRepo = null;
        
        IProject project = javaProject.getProject();
        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = modelJ2C().ceylonModel().getProject(project);

        final ProgressMonitor$impl<IProgressMonitor>.Progress progress = 
                monitor.Progress$new$(370,
                        ceylon.language.String.instance(
                                "Generating binaries for project " + project.getName()));
        try {
            progress.subTask("Preparing binary generation...");
            
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
            
            CeylonProjectConfig config = ceylonProject.getConfiguration();
            CeylonIdeConfig ideConfig = ceylonProject.getIdeConfiguration();
            
            String overrides = toJavaString(config.getOverrides());
            if (overrides != null) {
                options.add("-overrides");
                options.add(overrides);
            }

            String jdkProvider = toJavaString(config.getJdkProvider());
            if (jdkProvider != null) {
                options.add("-jdk-provider");
                options.add(jdkProvider);
            }

            if (config.getFlatClasspath()) {
                options.add("-flat-classpath");
            }

            if (config.getAutoExportMavenDependencies()) {
                options.add("-auto-export-maven-dependencies");
            }

            final File modulesOutputDir = getCeylonModulesOutputDirectory(project);
            if (modulesOutputDir!=null) {
                options.add("-out");
                options.add(modulesOutputDir.getAbsolutePath());
                js_outRepo = modulesOutputDir.getAbsolutePath();
            }

            CeylonConfig ceylonConfig = config.getCeylonConfig();

            if (ceylonConfig.isOptionDefined(DefaultToolOptions.COMPILER_NOOSGI)) {
                options.add("-noosgi");
            }
            if (ceylonConfig.isOptionDefined(DefaultToolOptions.COMPILER_OSGIPROVIDEDBUNDLES)) {
                options.add("-osgi-provided-bundles");
                options.add(ceylonConfig.getOption(DefaultToolOptions.COMPILER_OSGIPROVIDEDBUNDLES));
            }
            if (ceylonConfig.isOptionDefined(DefaultToolOptions.COMPILER_NOPOM)) {
                options.add("-nopom");
            }
            if (ceylonConfig.isOptionDefined(DefaultToolOptions.COMPILER_PACK200)) {
                options.add("-pack200");
            }
            if (ceylonConfig.isOptionDefined(DefaultToolOptions.COMPILER_RESOURCE_ROOT)) {
                options.add("-resroot");
                options.add(ceylonConfig.getOption(DefaultToolOptions.COMPILER_RESOURCE_ROOT));
            }

            
            List<String> javacOptions = CeylonHelper.toJavaStringList(ideConfig.getJavacOptions());
            if (javacOptions != null) {
                CeylonCompileTool.addJavacArguments(options, javacOptions);
            }
            
            progress.worked(20);
            List<File> forJavaBackend = new ArrayList<File>();
            List<File> forJavascriptBackend = new ArrayList<File>();
            List<File> javaResources = new ArrayList<File>();
            List<File> javaScriptResources = new ArrayList<File>();
            ProgressMonitor$impl<IProgressMonitor>.Progress scanFilesProgress = progress.newChild(20).Progress$new$(filesToCompile.size(), null);
            try {
                for (IFile file : filesToCompile) {
                    Module module = getModule(file);
                    Backends nativeBackends = null;
                    if (module != null) {
                        nativeBackends = module.getNativeBackends();
                    }
                    if (nativeBackends.none()
                            || nativeBackends.supports(Backend.Java.asSet())) {
                        if (isInSourceFolder(file)) {
                            if(isCeylon(file) || isJava(file)) {
                                forJavaBackend.add(file.getLocation().toFile());
                            }
                        }
                        if (isResourceFile(file)) {
                            javaResources.add(file.getLocation().toFile());
                        }
                    }
                    scanFilesProgress.worked(1);
                }
            } finally {
                scanFilesProgress.destroy(null);
            }
            
            // For the moment the JSCompiler doesn't support partial compilation of a module
            // so we add all the files to the source files list.
            // TODO : When JS partial module compilation is supported, re-integrate these lines 
            // in the loop above
            if (compileToJs(project)) {
                scanFilesProgress = progress.newChild(30).Progress$new$(ceylonProject.getProjectNativeFiles().getSize(), null);
                for (IFile file : list(IFile.class, ceylonProject.getProjectNativeFiles())) {
                    Module module = getModule(file);
                    Backends nativeBackends = null;
                    if (module != null) {
                        nativeBackends = module.getNativeBackends();
                    }
                    if (nativeBackends.none()
                            || nativeBackends.supports(Backend.JavaScript.asSet())) {
                        if (isInSourceFolder(file)) {
                            if(isCeylon(file) || isJavascript(file)) {
                                forJavascriptBackend.add(file.getLocation().toFile());
                            }
                        }
                        if (isResourceFile(file)) {
                            javaScriptResources.add(file.getLocation().toFile());
                        }
                    }
                    scanFilesProgress.worked(1);
                }
            }

            progress.updateRemainingWork(300);
            PrintWriter printWriter = new PrintWriter(verbose==null ? System.out : getConsoleStream(), true);
            boolean success = true;
            //Compile JS first
            if ((forJavascriptBackend.size() + javaScriptResources.size() > 0) && compileToJs(project)) {
                progress.subTask("Javascript Backend Generation");
                success = compileJs(ceylonProject, typeChecker, js_srcdir, js_rsrcdir, js_repos,
                        js_verbose, js_outRepo, printWriter, 
                        /*!compileToJava(project) uncomment isntead of true 
                        when https://github.com/ceylon/ceylon-compiler/issues/2175 is fixed */true,
                        forJavascriptBackend, javaScriptResources);
                progress.worked(70);
            }
            
            progress.updateRemainingWork(230);
            if ((forJavaBackend.size() + javaResources.size() > 0) && compileToJava(project)) {
                // For Java don't stop compiling when encountering errors
                options.add("-continue");
                // always add the java files, otherwise ceylon code won't see them 
                // and they won't end up in the archives (src/car)
                progress.subTask("Java Backend Generation");
                success = success & compile(ceylonProject, javaProject, options,
                        unitsTypecheckedIncrementally,
                        forJavaBackend, javaResources, typeChecker, printWriter, progress.newChild(200));
            }
            
            progress.updateRemainingWork(30);
            if (! compileToJs(project) &&
                    /*! compileToJava(project) &&*/ // TODO : uncomment when https://github.com/ceylon/ceylon-compiler/issues/2175 is fixed
                    modulesOutputDir != null) {
                progress.subTask("Source Archives Generation");

                EclipseLogger logger = new EclipseLogger();
                RepositoryManager outRepo = repoManager()
                .offline(config.getOffline())
                .cwd(project.getLocation().toFile())
                .outRepo(js_outRepo)
                .logger(logger)
                .buildOutputManager();
                
                ProgressMonitor$impl<IProgressMonitor>.Progress sourceGenerationMonitor = progress.newChild(30).Progress$new$(getProjectDeclaredSourceModules(project).size(), null);
                try {
                    for (Module m : getProjectDeclaredSourceModules(project)) {
                        if (m instanceof BaseIdeModule) {
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
                        sourceGenerationMonitor.worked(1);
                    }
                } finally {
                    sourceGenerationMonitor.destroy(null);
                }
            }
            return success;
        } finally {
            progress.destroy(null);
        }
        
        
    }

    private boolean compileJs(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, TypeChecker typeChecker,
            List<File> js_srcdir, List<File> js_rsrcdir, List<String> js_repos, 
            String js_verbose, String js_outRepo, PrintWriter printWriter, 
            boolean generateSourceArchive, List<File> sources, List<File> resources) 
                    throws CoreException {
        IProject project = ceylonProject.getIdeArtifact();
        CeylonProjectConfig config = ceylonProject.getConfiguration();
        Options jsopts = new Options()
                .cwd(ceylonProject.getRootDirectory())
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
                .offline(config.getOffline());
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
                if (vfsJ2C().instanceOfIFileVirtualFile(virtualFile)) {
                    return vfsJ2C().getIFileVirtualFile(virtualFile).getNativeResource().getLocation().toFile();
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
                boolean failed = false;
                for (Message e : jsc.getErrors()) {
                    if (e instanceof AnalysisMessage) {
                        if (e instanceof UnexpectedError) {
                            failed = true;
                        }
                        if (errorReporter == null) {
                            errorReporter = new CompileErrorReporter(project);
                        }
                        errorReporter.report(new CeylonCompilationError(project, (AnalysisMessage)e));
                    }
                }
                if (failed) {
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
    private boolean compile(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IJavaProject javaProject, 
            List<String> options, Collection<PhasedUnit> unitsTypecheckedIncrementally,
            List<File> sources, List<File> resources,
            final TypeChecker typeChecker, PrintWriter printWriter,
            ProgressMonitor<IProgressMonitor> mon) 
                    throws VerifyError {
        
        final IProject project = ceylonProject.getIdeArtifact();
        
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
        
        final ProgressMonitor$impl<IProgressMonitor>.Progress progress = mon.Progress$new$(
                numberOfSourceFiles * 2,
                ceylon.language.String.instance("Generating binaries for " + numberOfSourceFiles + 
                                " source files in project " + project.getName()));
        try {
            com.redhat.ceylon.compiler.java.tools.CeyloncTool compiler;
            try {
                compiler = new com.redhat.ceylon.compiler.java.tools.CeyloncTool();
            } catch (VerifyError e) {
                System.err.println("ERROR: Cannot run tests! Did you maybe forget to configure the -Xbootclasspath/p: parameter?");
                throw e;
            }

            CompileErrorReporter errorReporter = new CompileErrorReporter(project);

            final com.redhat.ceylon.langtools.tools.javac.util.Context context = new com.redhat.ceylon.langtools.tools.javac.util.Context();
            context.put(com.redhat.ceylon.langtools.tools.javac.util.Log.outKey, printWriter);
            context.put(DiagnosticListener.class, errorReporter);
            CeylonLog.preRegister(context);
            
            final Map<RegularFileObject, Set<String>> inputFilesToGenerate = new HashMap<RegularFileObject, Set<String>>();
            BuildFileManager fileManager = new BuildFileManager(context, true, null, project, inputFilesToGenerate);

            final TaskListener taskListener = new TaskListener() {
                @Override
                public void started(TaskEvent ta) {
                    if (progress.isCancelled()) {
                        throw new RuntimeException("Cancelled Java Backend compilation");
                    }
                    if (! ta.getKind().equals(Kind.PARSE) && ! ta.getKind().equals(Kind.ANALYZE)) {
                        return;
                    }
                    String name = ta.getSourceFile().getName();
                    name = name.substring(name.lastIndexOf("/")+1);
                    if (ta.getKind().equals(Kind.PARSE)) {
                        CompilationUnitTree cut = ta.getCompilationUnit();
                        if (cut != null && cut instanceof CeylonCompilationUnit) {
                            progress.subTask("transforming " + name);
                        } else {
                            progress.subTask("parsing " + name);
                        }
                    } 
                    if (ta.getKind().equals(Kind.ANALYZE)) {
                        progress.subTask("generating bytecode for " + name);
                    }
                }
                @Override
                public void finished(TaskEvent ta) {
                    if (progress.isCancelled()) {
                        throw new RuntimeException("Cancelled Java Backend compilation");
                    }
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
                    progress.worked(1);
                }
            };
            context.put(TaskListener.class, taskListener);
            
            computeCompilerClasspath(project, javaProject, options);
            
            List<File> allFiles = new ArrayList<>(sources.size()+ resources.size());
            allFiles.addAll(sources);
            allFiles.addAll(resources);
            Iterable<? extends JavaFileObject> unitsToCompile =
                    fileManager.getJavaFileObjectsFromFiles(allFiles);
            
            if (reuseEclipseModelInCompilation(project)) {
                setupJDTModelLoader(ceylonProject, typeChecker, context, unitsTypecheckedIncrementally);
            }
            
            CeyloncTaskImpl task = (CeyloncTaskImpl) compiler.getTask(printWriter, 
                    fileManager, errorReporter, options, null, 
                    unitsToCompile);
            task.setTaskListener(taskListener);
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
            return success;
        } finally {
            progress.destroy(null);
        }
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

    private void setupJDTModelLoader(
            final CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject,
            final TypeChecker typeChecker,
            final com.redhat.ceylon.langtools.tools.javac.util.Context context,
            final Collection<PhasedUnit> unitsTypecheckedIncrementally) {

        
        final BaseIdeModelLoader modelLoader = getModelLoader(typeChecker);
        
        context.put(LanguageCompiler.ceylonContextKey, typeChecker.getContext());
        context.put(TypeFactory.class, modelLoader.getTypeFactory());
        context.put(LanguageCompiler.compilerDelegateKey, 
                new JdtCompilerDelegate(modelLoader, ceylonProject, typeChecker, context, unitsTypecheckedIncrementally));
        
        context.put(TypeFactory.class, modelLoader.getTypeFactory());
        context.put(ModelLoaderFactory.class, new ModelLoaderFactory() {
            @Override
            public AbstractModelLoader createModelLoader(
                    com.redhat.ceylon.langtools.tools.javac.util.Context context) {
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
        return compileToJava(project) 
                && "false".equals(System.getProperty("ceylon.disableExplodeModules", "false"));
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
        if (project!=null) {
            CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = 
                    modelJ2C().ceylonModel().getProject(project);
            if (ceylonProject != null) {
                return ceylonProject.getConfiguration().getSuppressWarningsEnum();
            }
        }
        return EnumSet.noneOf(Warning.class);
    }

    public static List<String> getUserRepositories(IProject project) throws CoreException {
        List<String> ceylonRepos = getCeylonRepositories(project);
        List<String> userRepos = new ArrayList<>(ceylonRepos);
        userRepos.addAll(getReferencedProjectsOutputRepositories(project));
        return userRepos;
    }
    
    public static List<String> getReferencedProjectsOutputRepositories(IProject project) throws CoreException {
        List<String> repos = new ArrayList<String>();
        if (project != null) {
            for (IProject referencedProject: project.getReferencedProjects()) {
                if (referencedProject.isAccessible() && CeylonNature.isEnabled(referencedProject)) {
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
        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = 
                modelJ2C().ceylonModel()
                    .getProject(project);
        if (ceylonProject != null) {
            return toJavaStringList(ceylonProject.getCeylonRepositories());
        } else {
            return Collections.emptyList();
        }
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

    private static MessageConsoleStream getConsoleStream() {
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
     * Find or create the console with the given name, and
     * bring it to the top
     * @param consoleName
     */
    protected static MessageConsole findConsole() {
        MessageConsole myConsole = null;
        final IConsoleManager consoleManager = 
                ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] consoles = consoleManager.getConsoles();
        for(int i= 0; i < consoles.length; i++) {
            IConsole console = consoles[i];
            if (console.getName().equals(CEYLON_CONSOLE))
                myConsole = (MessageConsole) console;
        }
        if (myConsole == null) {
            ImageDescriptor image = 
                    CeylonPlugin.imageRegistry()
                        .getDescriptor(CeylonResources.BUILDER);
            myConsole = new MessageConsole(CEYLON_CONSOLE, image);
            consoleManager.addConsoles(new IConsole[] { myConsole });
        }
        consoleManager.showConsoleView(myConsole);
        return myConsole;
    }

    private static void addTaskMarkers(IFile file, List<CommonToken> tokens) {
        // clearTaskMarkersOn(file);
        for (CommonToken token : tokens) {
            int tt = token.getType();
            if (tt == CeylonLexer.LINE_COMMENT || 
                tt == CeylonLexer.MULTI_COMMENT) {
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
        
        monitor.subTask("Clearing project and source markers for project " 
                + project.getName());
        clearProjectMarkers(project, true, true);
        clearMarkersOn(project, true);

//        getConsoleStream().println("-----------------------------------");
//        getConsoleStream().println(timedMessage("End Ceylon clean on project: " + project.getName()));
//        getConsoleStream().println("===================================");
    }

    private void cleanupJdtClasses(IProgressMonitor monitor, IProject project) {
        if (isExplodeModulesEnabled(project)) {
            monitor.subTask("Cleaning exploded modules directory of project " 
                    + project.getName());
            final File ceylonOutputDirectory = 
                    getCeylonClassesOutputDirectory(project);
            new RepositoryLister(Arrays.asList(".*"))
                .list(ceylonOutputDirectory, 
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
        final File modulesOutputDirectory = 
                getCeylonModulesOutputDirectory(project);
        if (modulesOutputDirectory != null) {
            monitor.subTask("Cleaning existing artifacts of project " 
                    + project.getName());
            final List<String> extensionsToDelete = 
                    Arrays.asList(".jar", ".js", ".car", ".src", ".sha1");
            final List<String> deleteEverything = 
                    Arrays.asList(".*");
            new RepositoryLister(extensionsToDelete)
                .list(modulesOutputDirectory, 
                    new RepositoryLister.Actions() {

                @Override
                public void doWithFile(File path) {
                    if (path.getName().endsWith(ArtifactContext.CAR)) {
                        File moduleResourcesDirectory = 
                                new File(path.getParentFile(), 
                                        ArtifactContext.RESOURCES);
                        if (moduleResourcesDirectory.exists()) {
                            new RepositoryLister(deleteEverything)
                                .list(moduleResourcesDirectory, 
                                new RepositoryLister.Actions() {
                                    @Override
                                    public void doWithFile(File path) {
                                        path.delete();
                                    }
                                    
                                    @Override
                                    public void exitDirectory(File path) {
                                        if (path.list().length == 0) {
                                            path.delete();
                                        }
                                    }
                            });
                        }
                    }
                    path.delete();
                }
                
                @Override
                public void exitDirectory(File path) {
                    if (path.list().length == 0 && 
                            !path.equals(modulesOutputDirectory)) {
                        path.delete();
                    }
                }
            });
        }
    }
    
    // TODO think: doRefresh(file.getParent()); // N.B.: Assumes all
    // generated files go into parent folder

    public static RepositoryManager getProjectRepositoryManager(IProject project) {
        CeylonProject ceylonProject = 
                modelJ2C().ceylonModel()
                    .getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getRepositoryManager();
        }
        return null;
    }
    
    public static TypeChecker getProjectTypeChecker(IProject project) {
        CeylonProject ceylonProject = 
                modelJ2C().ceylonModel()
                    .getProject(project);
        if (ceylonProject != null && ceylonProject.getParsed()) {
            return ceylonProject.getTypechecker();
        }
        return null;
    }

    public static PhasedUnits getProjectPhasedUnits(IProject project) {
        TypeChecker typeChecker = 
                getProjectTypeChecker(project);
        if (typeChecker != null) {
            return typeChecker.getPhasedUnits();
        }
        return null;
    }

    public static Modules getProjectModules(IProject project) {
        TypeChecker typeChecker = 
                getProjectTypeChecker(project);
        if (typeChecker == null) {
            return null;
        }
        return typeChecker.getContext()
                .getModules();
    }
    
    public static Collection<BaseIdeModule> getProjectExternalModules(IProject project) {
        TypeChecker typeChecker = 
                getProjectTypeChecker(project);
        if (typeChecker == null) {
            return Collections.emptyList();
        }
        List<BaseIdeModule> modules = new ArrayList<>();
        for (Module m : 
                typeChecker.getContext()
                    .getModules()
                    .getListOfModules()) {
            if (m instanceof BaseIdeModule) {
                BaseIdeModule module = (BaseIdeModule) m;
                if (! module.getIsProjectModule()) {
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
        TypeChecker typeChecker = 
                getProjectTypeChecker(project);
        if (typeChecker == null) {
            return Collections.emptyList();
        }
        List<Module> modules = new ArrayList<>();
        for (Module m : 
                typeChecker.getPhasedUnits()
                    .getModuleSourceMapper()
                    .getCompiledModules()) {
            if (m instanceof BaseIdeModule) {
                BaseIdeModule module = (BaseIdeModule) m;
                if (module.getIsProjectModule()) {
                    modules.add(module);
                }
            }
        }
        return modules;
    }

    public static void removeOverridesProblemMarker(final IProject project) {
        Job job = new Job("Remove Overrides problem marker") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    IResource lastOverridesWithProblem = 
                            findLastOverridesProblemMarker(project);
                    if (lastOverridesWithProblem != null) {
                        if (lastOverridesWithProblem.findMarkers(
                                CEYLON_INVALID_OVERRIDES_MARKER, 
                                false, DEPTH_ZERO).length > 0) {
                            lastOverridesWithProblem.deleteMarkers(
                                    CEYLON_INVALID_OVERRIDES_MARKER, 
                                    false, DEPTH_ZERO);
                            project.setPersistentProperty(
                                    new QualifiedName(CeylonPlugin.PLUGIN_ID, 
                                            "lastOverridesProblemMarker"), 
                                    null);
                        }
                    }
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
                return Status.OK_STATUS;
            }
        };
        job.setRule(project);
        job.schedule();
    }

    
    public static IFile fileToIFile(File file, IProject project) {
        IPath projectLocation = project.getLocation();
        IPath absolutePath = new Path(file.getAbsolutePath());
        
        if (projectLocation.isPrefixOf(absolutePath)) {
            IPath projectRelativePath = 
                    absolutePath.removeFirstSegments(
                            projectLocation.segmentCount());
            IResource resource = 
                    project.findMember(projectRelativePath);
            if (resource != null 
                    && resource.isAccessible()
                    && resource instanceof IFile) {
                return (IFile) resource;
            }
        }
        return null;
    }
    
    private static IResource findLastOverridesProblemMarker(IProject project) {
        try {
            String projectRelativePath = 
                    project.getPersistentProperty(
                            new QualifiedName(CeylonPlugin.PLUGIN_ID, 
                                    "lastOverridesProblemMarker"));
            if (projectRelativePath != null) {
                IResource resource = 
                        project.findMember(projectRelativePath);
                if (resource != null && resource.isAccessible()) {
                    return resource;
                }
            }
        } catch(CoreException e) {
        }
        return null;
    }
    
    public static void createOverridesProblemMarker(final IProject project,
            final Exception e, final File overridesFile, final int line, final int column) {
        Job job = new Job("Create Overrides problem marker") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    IResource markerResource = project;
                    IFile overridesResource = fileToIFile(overridesFile, project);
                    if (overridesResource != null) {
                        markerResource = overridesResource;
                    }
                    
                    IResource lastOverridesWithProblem = 
                            findLastOverridesProblemMarker(project);
                    if (lastOverridesWithProblem != null) {
                        lastOverridesWithProblem.deleteMarkers(
                                CEYLON_INVALID_OVERRIDES_MARKER, 
                                false, DEPTH_ZERO);
                    }
                    
                    project.setPersistentProperty(
                            new QualifiedName(CeylonPlugin.PLUGIN_ID, 
                                    "lastOverridesProblemMarker"), 
                            markerResource.getProjectRelativePath().toString());
                    
                    IMarker marker = 
                            markerResource.createMarker(CEYLON_INVALID_OVERRIDES_MARKER);
                    marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                    if (line > -1) {
                        marker.setAttribute(IMarker.LOCATION, "Line " + line);
                        marker.setAttribute(IMarker.LINE_NUMBER, line);
                        if (column > -1 && markerResource instanceof IFile) {
                            TextFileDocumentProvider docProvider = 
                                    new TextFileDocumentProvider();
                            docProvider.connect(markerResource);
                            IDocument overridesDocument = 
                                    docProvider.getDocument(markerResource);
                            if (overridesDocument != null) {
                                IRegion lineInfo;
                                try {
                                    lineInfo = overridesDocument.getLineInformation(line-1);
                                    int endCharOffset  = lineInfo.getOffset() + column - 1;
                                    if (endCharOffset == lineInfo.getOffset() + lineInfo.getLength()) {
                                         while (endCharOffset > lineInfo.getOffset()) {
                                            char lineEnd = overridesDocument.getChar(endCharOffset);
                                            if (lineEnd == '\n' || lineEnd == '\r') {
                                                endCharOffset --;
                                            } else {
                                                break;
                                            }
                                         }
                                    }
                                    char endChar = overridesDocument.getChar(endCharOffset);
                                    int firstCharOffset = endCharOffset - 2;
                                    if (firstCharOffset < 0) {
                                        firstCharOffset= 0;
                                    }
                                    if (endChar == '>') {
                                        int offset = endCharOffset -1;
                                        while (offset >= lineInfo.getOffset()) {
                                            if (overridesDocument.getChar(offset) == '<') {
                                                firstCharOffset = offset;
                                                break;
                                            }
                                            offset--;
                                        }
                                    }
                                    marker.setAttribute(IMarker.CHAR_START, firstCharOffset);
                                    marker.setAttribute(IMarker.CHAR_END, endCharOffset +1);
                                } catch (BadLocationException e1) {
                                }
                            }
                        }
                    }
                    marker.setAttribute(IMarker.SOURCE_ID, CeylonBuilder.SOURCE);
                    marker.setAttribute(IMarker.MESSAGE, 
                            "The Module Resolver Overrides file is invalid : " 
                                    + e.getMessage());
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
                return Status.OK_STATUS;
            }
        };
        job.setRule(project);
        job.schedule();
    }
    

//    public static RepositoryManager createProjectRepositoryManager(final IProject project) throws CoreException {
//        modelJ2C().ceylonModel().addProject(project);
//        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = modelJ2C().ceylonModel().getProject(project);
//
//        RepositoryManager repositoryManager = new CeylonUtils.CeylonRepoManagerBuilder() {
//                    protected com.redhat.ceylon.cmr.api.Overrides getOverrides(String path) {
//                        if (path == null) {
//                            removeOverridesProblemMarker(project);
//                        }
//                        return super.getOverrides(path);
//                    }
//                    protected com.redhat.ceylon.cmr.api.Overrides getOverrides(File absoluteFile) {
//                        Overrides result = null;
//                        Exception overridesException = null;
//                        int overridesLine = -1;
//                        int overridesColumn = -1;
//                        try {
//                            result = super.getOverrides(absoluteFile);
//                        } catch(Overrides.InvalidOverrideException e) {
//                            overridesException = e;
//                            overridesLine = e.line;
//                            overridesColumn = e.column;
//                        } catch(IllegalStateException e) {
//                            Throwable cause = e.getCause();
//                            if (cause instanceof SAXParseException) {
//                                SAXParseException parseException = (SAXParseException) cause;
//                                overridesException = parseException;
//                                overridesLine = parseException.getLineNumber();
//                                overridesColumn = parseException.getColumnNumber();
//                            } else if (cause instanceof Exception) {
//                                overridesException = (Exception) cause;
//                            } else {
//                                overridesException = e;
//                            }
//                        } catch(Exception e) {
//                            overridesException = e;
//                        }
//
//                        if (overridesException != null) {
//                            createOverridesProblemMarker(
//                                    project, 
//                                    overridesException, 
//                                    absoluteFile, 
//                                    overridesLine, 
//                                    overridesColumn);
//                        } else {
//                            removeOverridesProblemMarker(project);
//                        }
//                        return result;
//                    };
//                    
//                }
//                .offline(ceylonProject.getConfiguration().getOffline())
//                .cwd(project.getLocation().toFile())
//                .systemRepo(getInterpolatedCeylonSystemRepo(project))
//                .extraUserRepos(getReferencedProjectsOutputRepositories(project))
//                .logger(new EclipseLogger())
//                .isJDKIncluded(true)
//                .buildManager();
//
//        return repositoryManager;
//    }
    
    public static Collection<CeylonProject<IProject, IResource, IFolder, IFile>> getCeylonProjects() {
        return modelJ2C().ceylonModel()
                    .getCeylonProjectsAsJavaList();
    }

    public static Collection<IProject> getProjects() {
        return modelJ2C().ceylonModel()
                    .getNativeProjectsAsJavaList();
    }

    public static Collection<TypeChecker> getTypeCheckers() {
        Collection<CeylonProject<IProject, IResource, IFolder, IFile>> ceylonProjects = 
                getCeylonProjects();
        ArrayList<TypeChecker> typeCheckers = 
                new ArrayList<>(ceylonProjects.size());
        for (CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject : ceylonProjects) {
            TypeChecker tc = ceylonProject.getTypechecker();
            if (tc != null) {
                typeCheckers.add(tc);
            }
        }
        return typeCheckers;
    }
    
    public static void removeProject(IProject project) {
        containersInitialized.remove(project);
        JavaProjectStateMirror.cleanup(project);
    }
    
  public static List<IFolder> getSourceFolders(IProject project) {
      CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject = 
              modelJ2C().ceylonModel().getProject(project);
      if (ceylonProject != null) {
          return list(IFolder.class, 
                  ceylonProject.getSourceNativeFolders());
      }
      return Collections.emptyList();
  }

  public static List<IFolder> getResourceFolders(IProject project) {
      CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject = 
              modelJ2C().ceylonModel().getProject(project);
      if (ceylonProject != null) {
          return list(IFolder.class, 
                  ceylonProject.getResourceNativeFolders());
      }
      return Collections.emptyList();
  }


//    public static List<IFolder> getSourceFolders(IProject project) {
//        //TODO: is the call to JavaCore.create() very expensive??
//        List<IPath> folderPaths = getSourceFolders(JavaCore.create(project));
//        List<IFolder> sourceFolders = new ArrayList<>(folderPaths.size());
//        for (IPath path : folderPaths) {
//            IResource r = project.findMember(path.makeRelativeTo(project.getFullPath()));
//            if (r instanceof IFolder) {
//                sourceFolders.add((IFolder) r);
//            }
//        }
//        return sourceFolders;
//    }
//
//    /**
//     * Read the IJavaProject classpath configuration and populate the ISourceProject's
//     * build path accordingly.
//     */
//    public static List<IPath> getSourceFolders(IJavaProject javaProject) {
//        if (javaProject.exists()) {
//            try {
//                List<IPath> projectSourceFolders = new ArrayList<IPath>();
//                for (IClasspathEntry entry: javaProject.getRawClasspath()) {
//                    IPath path = entry.getPath();
//                    if (isCeylonSourceEntry(entry)) {
//                        projectSourceFolders.add(path);
//                    }
//                }
//                return projectSourceFolders;
//            } 
//            catch (JavaModelException e) {
//                e.printStackTrace();
//            }
//        }
//        return Collections.emptyList();
//    }
//
//    public static List<IFolder> getResourceFolders(IProject project) {
//        LinkedList<IFolder> resourceFolers = new LinkedList<>();
//        if (project.exists()) {
//            CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject  = modelJ2C().ceylonModel().getProject(project);
//            if (ceylonProject != null) {
//                for (String resourceInConfig : toJavaStringList(ceylonProject.getConfiguration().getResourceDirectories())) {
//                    class FolderHolder {
//                        IFolder resourceFolder;
//                    }
//                    final FolderHolder folderHolder = new FolderHolder();;
//                    final IPath path = Path.fromOSString(resourceInConfig);
//                    if (! path.isAbsolute()) {
//                        folderHolder.resourceFolder = project.getFolder(path);
//                    } else {
//                        try {   
//                            project.accept(new IResourceVisitor() {
//                                @Override
//                                public boolean visit(IResource resource) 
//                                        throws CoreException {
//                                    if (resource instanceof IFolder &&
//                                            resource.isLinked() && 
//                                            resource.getLocation() != null &&
//                                            resource.getLocation().equals(path)) {
//                                        folderHolder.resourceFolder = (IFolder) resource;
//                                        return false;
//                                    }
//                                    return resource instanceof IFolder || 
//                                            resource instanceof IProject;
//                                }
//                            });
//                        }
//                        catch (CoreException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (folderHolder.resourceFolder != null && 
//                            folderHolder.resourceFolder.exists()) {
//                        resourceFolers.add(folderHolder.resourceFolder);
//                    }
//                }
//            }
//        }
//        return resourceFolers;
//    }
//
//    public static List<IFolder> getRootFolders(IProject project) {
//        LinkedList<IFolder> rootFolders = new LinkedList<>();
//        rootFolders.addAll(getSourceFolders(project));
//        rootFolders.addAll(getResourceFolders(project));
//        return rootFolders;
//    }
//
//    public static boolean isCeylonSourceEntry(IClasspathEntry entry) {
//        if (entry.getEntryKind()!=IClasspathEntry.CPE_SOURCE) {
//            return false;
//        }
//        
//        for (IPath exclusionPattern : entry.getExclusionPatterns()) {
//            if (exclusionPattern.toString().endsWith(".ceylon")) {
//                return false;
//            }
//        }
//
//        return true;
//    }

    public static IFolder getRootFolder(IFolder folder) {
        FolderVirtualFile<IProject, IResource, IFolder, IFile> rootVirtualFile = 
                vfsJ2C().createVirtualFolder(folder, folder.getProject()).getRootFolder();
        if (rootVirtualFile == null) {
            return null;
        }
        return rootVirtualFile.getNativeResource();
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
        ceylon.language.Boolean isSourceFolder = 
                vfsJ2C().createVirtualFolder(folder, folder.getProject())
                    .getIsSource();
        if (isSourceFolder == null) {
            return null;
        }
        return isSourceFolder.booleanValue() ? 
                RootFolderType.SOURCE : RootFolderType.RESOURCE;
    }
    
    public static RootFolderType getRootFolderType(IFile file) {
        ceylon.language.Boolean isSourceFolder = 
                vfsJ2C().createVirtualFile(file, file.getProject())
                    .getIsSource();
        if (isSourceFolder == null) {
            return null;
        }
        return isSourceFolder.booleanValue() ? 
                RootFolderType.SOURCE : RootFolderType.RESOURCE;
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
        ResourceVirtualFile<IProject, IResource, IFolder, IFile> resourceVirtualFile = 
                vfsJ2C().createVirtualResource(resource, resource.getProject());
        return resourceVirtualFile.getCeylonPackage();
    }
    
    public static Package getPackage(IFile file) {
        ResourceVirtualFile<IProject, IResource, IFolder, IFile> resourceVirtualFile = 
                vfsJ2C().createVirtualResource(file, file.getProject());
        return resourceVirtualFile.getCeylonPackage();
    }    

    @SuppressWarnings("unchecked")
    public static Package getPackage(VirtualFile virtualFile) {
        if (virtualFile instanceof FileVirtualFile) {
            FileVirtualFile<IProject, IResource, IFolder, IFile> fvf = 
                    (FileVirtualFile<IProject, IResource, IFolder, IFile>)
                        virtualFile;
            return fvf.getCeylonPackage();
        }
        if (virtualFile instanceof FolderVirtualFile) {
            FolderVirtualFile<IProject, IResource, IFolder, IFile> fvf = 
                    (FolderVirtualFile<IProject, IResource, IFolder, IFile>)
                        virtualFile;
            return fvf.getCeylonPackage();
        }
        String virtualPath = virtualFile.getPath();
        if (virtualPath.contains("!/")) { // TODO : this test could be replaced by an instanceof if the ZipEntryVirtualFile was public
            Unit ceylonUnit = getUnit(virtualFile);
            if (ceylonUnit != null) {
                return ceylonUnit.getPackage();
            }
        }
        return null;
    }    

    public static CeylonUnit getUnit(VirtualFile virtualFile) {
        if (vfsJ2C().instanceOfIFileVirtualFile(virtualFile)) {
            IFile file = 
                    vfsJ2C()
                        .getIFileVirtualFile(virtualFile)
                        .getNativeResource();
            Package p = getPackage(file);
            if (p != null) {
                for (Unit u : p.getUnits()) {
                    if (u instanceof SourceFile && 
                            u.getFilename()
                             .equals(file.getName())) {
                        return (SourceFile) u;
                    }
                }
            }
            return null;
        }
        
        String virtualPath = virtualFile.getPath();
        if (virtualPath.contains("!/")) { // TODO : this test could be replaced by an instanceof if the ZipEntryVirtualFile was public
            for (IProject p : getProjects()) {
                BaseIdeModuleManager moduleManager = 
                        getProjectModuleManager(p);
                if (moduleManager != null) {
                    BaseIdeModule archiveModule = 
                            moduleManager.getArchiveModuleFromSourcePath(
                                    ceylon.language.String.instance(virtualPath));
                    if (archiveModule != null) {
                        ExternalPhasedUnit pu = 
                                archiveModule.getPhasedUnit(virtualFile);
                        if (pu != null) {
                            return pu.getUnit();
                        }
                    }
                }
            }
        }
        return null;
    }    

    @SuppressWarnings("unchecked")
    public static IResourceAware<IProject,IFolder,IFile> getUnit(IFile file) {
        Package p = getPackage(file);
        if (p != null) {
            for (Unit u: p.getUnits()) {
                if (u instanceof IResourceAware) {
                    if (u.getFilename().equals(file.getName())) {
                        return (IResourceAware<IProject,IFolder,IFile>) u;
                    }
                }
            }
        }
        return null;
    }    

    public static Package getPackage(IPackageFragment packageFragment) {
        try {
            IFolder srcPkgFolder = (IFolder) 
                    packageFragment.getCorrespondingResource();
            if (srcPkgFolder != null) {
                return getPackage(srcPkgFolder);
            }
        } catch (JavaModelException e) {
        }

        IPackageFragmentRoot root = 
                (IPackageFragmentRoot) 
                packageFragment.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
        IProject project = 
                packageFragment.getJavaProject()
                    .getProject();
        Modules projectModules = 
                getProjectModules(project);
        if (projectModules == null) {
            return null;
        }
        
        for (Module m : projectModules.getListOfModules()) {
            if (m instanceof BaseIdeModule && 
                    !m.getNameAsString()
                        .equals(Module.DEFAULT_MODULE_NAME)) {
                BaseIdeModule module = (BaseIdeModule) m;
                for (IPackageFragmentRoot moduleRoot : 
                        modelJ2C().getModulePackageFragmentRoots(module)) {
                    if (root.getPath().equals(moduleRoot.getPath())) {
                        Package result = 
                                module.getDirectPackage(
                                        packageFragment.getElementName());
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        BaseIdeModule defaultModule = (BaseIdeModule) 
                projectModules.getDefaultModule();
        for (IPackageFragmentRoot moduleRoot : 
            modelJ2C().getModulePackageFragmentRoots(defaultModule)) {
            if (root.getPath().equals(moduleRoot.getPath())) {
                Package result = 
                        defaultModule.getDirectPackage(
                                packageFragment.getElementName());
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static BaseIdeModule asSourceModule(IFolder moduleFolder) {
        Package p = getPackage(moduleFolder);
        if (p != null) {
            Module m = p.getModule();
            if (m instanceof BaseIdeModule && 
                    m.getNameAsString()
                        .equals(p.getNameAsString())) {
                return (BaseIdeModule) m;
            }
        }
        return null;
    }

    public static BaseIdeModule asSourceModule(IPackageFragment sourceModuleFragment) {
        IFolder moduleFolder;
        try {
            moduleFolder = (IFolder) 
                    sourceModuleFragment.getCorrespondingResource();
            if (moduleFolder != null) {
                return asSourceModule(moduleFolder);
            }
        } catch (JavaModelException e) {
        }
        return null;
    }

    public static BaseIdeModule getModule(IFile file) {
        Package p = getPackage(file);
        if (p != null) {
            Module m = p.getModule();
            if (m instanceof BaseIdeModule) {
                return (BaseIdeModule) m;
            }
        }
        return null;
    }

    public static BaseIdeModule getModule(IFolder moduleFolder) {
        Package p = getPackage(moduleFolder);
        if (p != null) {
            Module m = p.getModule();
            if (m instanceof BaseIdeModule) {
                return (BaseIdeModule) m;
            }
        }
        return null;
    }

    public static BaseIdeModule getModule(IPackageFragment packageFragment) {
        Package p = getPackage(packageFragment);
        if (p != null) {
            Module m = p.getModule();
            if (m instanceof BaseIdeModule) {
                return (BaseIdeModule) m;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static IJavaModelAware<IProject,ITypeRoot,IJavaElement> getUnit(IJavaElement javaElement) {
        IOpenable openable = javaElement.getOpenable();
        if (openable instanceof ITypeRoot) {
            ITypeRoot typeRoot = (ITypeRoot) openable;
            IPackageFragment pf = 
                    (IPackageFragment) 
                        typeRoot.getParent();
            Package p = getPackage(pf);
            if (p != null) {
                String className = typeRoot.getElementName();
                if (className.equals(Naming.PACKAGE_DESCRIPTOR_CLASS_NAME+".class") ||
                    className.equals(Naming.PACKAGE_DESCRIPTOR_CLASS_NAME.substring(1)+".class")) {
                    Unit packageUnit = p.getUnit();
                    if (packageUnit instanceof IJavaModelAware) {
                        IJavaModelAware<IProject, ITypeRoot, IJavaElement> jma = 
                                (IJavaModelAware<IProject, ITypeRoot, IJavaElement>) 
                                    packageUnit;
                        if (jma.getTypeRoot().equals(openable)) {
                            return jma;
                        }
                    }
                }
                if (className.equals(Naming.MODULE_DESCRIPTOR_CLASS_NAME+".class") ||
                    className.equals(Naming.OLD_MODULE_DESCRIPTOR_CLASS_NAME+".class")) {
                    Unit moduleUnit = p.getModule().getUnit();
                    if (moduleUnit instanceof IJavaModelAware) {
                        IJavaModelAware<IProject, ITypeRoot, IJavaElement> jma = 
                                (IJavaModelAware<IProject, ITypeRoot, IJavaElement>) 
                                    moduleUnit;
                        if (jma.getTypeRoot().equals(openable)) {
                            return jma;
                        }
                    }
                }
                for (Declaration d : p.getMembers()) {
                    Unit u = d.getUnit();
                    if (u instanceof IJavaModelAware) {
                        IJavaModelAware<IProject, ITypeRoot, IJavaElement> jma = 
                                (IJavaModelAware<IProject, ITypeRoot, IJavaElement>) u;
                        if (u.getFilename().equals(typeRoot.getElementName())) {
                            return jma;
                        }
                    }
                }
            }
        }
        return null;
    }    

    private void cleanRemovedFilesFromOutputs(
            Collection<IFile> filesToRemove, 
            CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject) {
        if (filesToRemove.size() == 0) {
            return;
        }
        
        IProject project = ceylonProject.getIdeArtifact();
        Set<File> moduleJars = new HashSet<File>();
        
        for (IFile file : filesToRemove) {
            IFolder rootFolder = getRootFolder(file);
            if (rootFolder == null) {
                return;
            }
            IPath projectRelativePath = 
                    rootFolder.getProjectRelativePath();
            String relativeFilePath = 
                    file.getProjectRelativePath()
                        .makeRelativeTo(projectRelativePath)
                        .toString();
            IFolder folder = (IFolder) file.getParent();
            Package pkg = getPackage(folder);
            if (pkg == null) {
                return;
            }
            Module module = pkg.getModule();
            TypeChecker typeChecker = 
                    ceylonProject.getTypechecker();
            if (typeChecker == null) {
                return;
            }
            
            final File modulesOutputDirectory = 
                    getCeylonModulesOutputDirectory(project);
            boolean explodeModules = isExplodeModulesEnabled(project);
            final File ceylonOutputDirectory = explodeModules ? 
                    getCeylonClassesOutputDirectory(project) : null;
            File moduleDir = getModulePath(modulesOutputDirectory, module);
            
            boolean fileIsResource = isResourceFile(file);
            
            //Remove the classes belonging to the source file from the
            //module archive and from the JDTClasses directory
            File moduleJar = 
                    new File(moduleDir, 
                            getModuleArchiveName(module));
            if(moduleJar.exists()){
                moduleJars.add(moduleJar);
                try {
                    List<String> entriesToDelete = 
                            new ArrayList<String>();
                    ZipFile zipFile = new ZipFile(moduleJar);
                    
                    Properties mapping = 
                            CarUtils.retrieveMappingFile(zipFile);

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
                        try {
                            zipFile.removeFile(entryToDelete);
                        } catch (ZipException e) {
                        }

                        if (explodeModules) {
                            new File(ceylonOutputDirectory, 
                                    entryToDelete.replace('/', 
                                            File.separatorChar))
                                    .delete();
                        }
                    }
                } catch (ZipException e) {
                    e.printStackTrace();
                }
            }
            
            if (!fileIsResource) {
                //Remove the source file from the source archive
                File moduleSrc = 
                        new File(moduleDir, 
                                getSourceArchiveName(module));
                if(moduleSrc.exists()){
                    moduleJars.add(moduleSrc);
                    try {
                        ZipFile zipFile = new ZipFile(moduleSrc);
                        FileHeader fileHeader = 
                                zipFile.getFileHeader(relativeFilePath);
                        if(fileHeader != null){
                            zipFile.removeFile(fileHeader);
                        }
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            if (fileIsResource) {
                File resourceFile = new File(
                        moduleDir, 
                        "module-resources" 
                                + File.separator 
                                + relativeFilePath.replace('/', 
                                        File.separatorChar));
                resourceFile.delete();
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

    private void cleanChangedFilesFromExplodedDirectory(
            Collection<IFile> changedFiles, 
            CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject) {
        if (changedFiles.size() == 0) {
            return;
        }
        
        IProject project = ceylonProject.getIdeArtifact();
        
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
            
            IPath projectRelativePath = 
                    rootFolder.getProjectRelativePath();
            String relativeFilePath = 
                    file.getProjectRelativePath()
                        .makeRelativeTo(projectRelativePath)
                        .toString();
            Package pkg = getPackage((IFolder)file.getParent());
            if (pkg == null) {
                return;
            }
            Module module = pkg.getModule();
            TypeChecker typeChecker = ceylonProject.getTypechecker();
            if (typeChecker == null) {
                return;
            }
            
            final File modulesOutputDirectory = 
                    getCeylonModulesOutputDirectory(project);
            final File ceylonOutputDirectory = 
                    getCeylonClassesOutputDirectory(project);
            File moduleDir = getModulePath(modulesOutputDirectory, module);
            
            //Remove the classes belonging to the source file from the
            //from the .exploded directory
            File moduleJar = 
                    new File(moduleDir, 
                            getModuleArchiveName(module));
            if(moduleJar.exists()) {
                try {
                    List<String> entriesToDelete = 
                            new ArrayList<String>();
                    ZipFile zipFile = new ZipFile(moduleJar);
                    
                    Properties mapping = 
                            CarUtils.retrieveMappingFile(zipFile);

                    for (String className : mapping.stringPropertyNames()) {
                        String sourceFile = mapping.getProperty(className);
                        if (relativeFilePath.equals(sourceFile)) {
                            entriesToDelete.add(className);
                        }
                    }

                    for (String entryToDelete : entriesToDelete) {
                        new File(ceylonOutputDirectory, 
                                entryToDelete.replace('/', 
                                        File.separatorChar))
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
        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = 
                modelJ2C().ceylonModel().getProject(project);
        String outputRepoRelativePath;
        if (ceylonProject != null) {
            CeylonProjectConfig config = 
                    ceylonProject.getConfiguration();
            outputRepoRelativePath = 
                    config.getOutputRepoProjectRelativePath();
        } else {
            outputRepoRelativePath = "modules";
        }
        return project.getFolder(outputRepoRelativePath);
    }
    
    public static String getCeylonSystemRepo(IProject project) {
        String defaultRepo = "${ceylon.repo}";
        if (!project.isAccessible()) {
            return defaultRepo;
        }
        
        String systemRepo = (String) getBuilderArgs(project).get("systemRepo");
        if (systemRepo == null || systemRepo.isEmpty()) {
            return defaultRepo;
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
        String pluginRepoPath = 
                CeylonPlugin.getInstance()
                    .getCeylonRepository()
                    .getAbsolutePath();
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
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.isAccessible() && CeylonNature.isEnabled(project)
                    && ! containersInitialized.contains(project)) {
                return false;
            }
        }
        return true;
    }

    public static ModuleDependencies getModuleDependenciesForProject(
            IProject project) {
        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = 
                modelJ2C().ceylonModel().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getModuleDependencies();
        }
        return null;
    }
}
