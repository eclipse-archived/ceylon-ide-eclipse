package com.redhat.ceylon.eclipse.imp.builder;

import static com.redhat.ceylon.compiler.typechecker.model.Util.formatPath;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.tools.JavaFileObject;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IBuildConfiguration;
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
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.builder.MarkerCreator;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceAction;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.progress.UIJob;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.java.codegen.CeylonCompilationUnit;
import com.redhat.ceylon.compiler.java.loader.CeylonClassReader;
import com.redhat.ceylon.compiler.java.loader.TypeFactory;
import com.redhat.ceylon.compiler.java.loader.mirror.JavacClass;
import com.redhat.ceylon.compiler.java.tools.CeylonLog;
import com.redhat.ceylon.compiler.java.tools.CeyloncFileManager;
import com.redhat.ceylon.compiler.java.tools.CeyloncTaskImpl;
import com.redhat.ceylon.compiler.java.tools.CeyloncTool;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler.PhasedUnitsManager;
import com.redhat.ceylon.compiler.java.util.RepositoryLister;
import com.redhat.ceylon.compiler.java.util.ShaSigner;
import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.ModelLoaderFactory;
import com.redhat.ceylon.compiler.loader.SourceDeclarationVisitor;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.model.LazyPackage;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleValidator;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.Helper;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ExternalUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.eclipse.core.cpcontainer.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.core.cpcontainer.CeylonClasspathUtil;
import com.redhat.ceylon.eclipse.core.model.CeylonSourceFile;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader.SourceFileObjectManager;
import com.redhat.ceylon.eclipse.core.model.loader.mirror.JDTClass;
import com.redhat.ceylon.eclipse.core.model.loader.mirror.SourceClass;
import com.redhat.ceylon.eclipse.core.model.loader.model.JDTModuleManager;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;
import com.redhat.ceylon.eclipse.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.vfs.ResourceVirtualFile;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;

/**
 * A builder may be activated on a file containing ceylon code every time it has
 * changed (when "Build automatically" is on), or when the programmer chooses to
 * "Build" a project.
 * 
 * TODO This default implementation was generated from a template, it needs to
 * be completed manually.
 */
public class CeylonBuilder extends IncrementalProjectBuilder{

    public static boolean compileWithJDTModelLoader = true;
    /**
     * Extension ID of the Ceylon builder, which matches the ID in the
     * corresponding extension definition in plugin.xml.
     */
    public static final String BUILDER_ID = CeylonPlugin.PLUGIN_ID
            + ".ceylonBuilder";

    /**
     * A marker ID that identifies problems detected by the builder
     */
    public static final String PROBLEM_MARKER_ID = CeylonPlugin.PLUGIN_ID
            + ".ceylonProblem";

    /*public static final String TASK_MARKER_ID = CeylonPlugin.PLUGIN_ID
            + ".ceylonTask";*/

    public static final String LANGUAGE_NAME = "ceylon";

    public static final Language LANGUAGE = LanguageRegistry
            .findLanguage(LANGUAGE_NAME);

    public static enum ModelState {
        Missing,
        Parsing,
        Parsed,
        TypeChecking,
        TypeChecked,
        Compiled
    };
    
    private final static Map<IProject, ModelState> modelStates = new HashMap<IProject, ModelState>();
    private final static Map<IProject, TypeChecker> typeCheckers = new HashMap<IProject, TypeChecker>();
    private final static Map<IProject, List<IPath>> sourceFolders = new HashMap<IProject, List<IPath>>();
    private final static Map<IProject, List<IFile>> projectSources = new HashMap<IProject, List<IFile>>();

    public static final String CEYLON_CONSOLE= "Ceylon";
    private long startTime;

    private IPreferencesService fPrefService;

    private final Collection<IFile> fSourcesToCompile= new HashSet<IFile>();
   
    public static ModelState getModelState(IProject project) {
        ModelState modelState = modelStates.get(project);
        if (modelState == null) {
            return ModelState.Missing;
        }
        return modelState;
    }
    
    public static boolean isModelAvailable(IProject project) {
        ModelState modelState = getModelState(project);
        return modelState.ordinal() >= ModelState.TypeChecked.ordinal();
    }
    
    public static List<PhasedUnit> getUnits(IProject project) {
        if (! isModelAvailable(project)) {
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
            if (isModelAvailable(project)) {
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
                        if (isModelAvailable(project)) {
                            result.addAll(me.getValue().getPhasedUnits().getPhasedUnits());
                        }
                    }
                }
            }
        }
        return result;
    }

    protected PluginBase getPlugin() {
        return CeylonPlugin.getInstance();
    }

    public String getBuilderID() {
        return BUILDER_ID;
    }

    protected String getErrorMarkerID() {
        return PROBLEM_MARKER_ID;
    }

    protected String getWarningMarkerID() {
        return PROBLEM_MARKER_ID;
    }

    protected String getInfoMarkerID() {
        return PROBLEM_MARKER_ID;
    }
    
    private ISourceProject getSourceProject() {
        ISourceProject sourceProject = null;
        try {
            sourceProject = ModelFactory.open(getProject());
        } catch (ModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sourceProject;
    }

    public static boolean isCeylon(IFile file) {
        return LANGUAGE.hasExtension(file.getFileExtension());
    }

    public static boolean isJava(IFile file) {
        return JavaCore.isJavaLikeFileName(file.getName());
    }

    public static boolean isCeylonOrJava(IFile file) {
        return isCeylon(file) || isJava(file);
    }

    /**
     * Decide whether a file needs to be build using this builder. Note that
     * <code>isNonRootSourceFile()</code> and <code>isSourceFile()</code> should
     * never return true for the same file.
     * 
     * @return true iff an arbitrary file is a ceylon source file.
     */
    protected boolean isSourceFile(IFile file) {
        IPath path = file.getFullPath(); //getProjectRelativePath();
        if (path == null)
            return false;

        if (!isCeylonOrJava(file)) {
            return false;
        }
        
        IProject project = file.getProject();
        if (project != null) {
            for (IPath sourceFolder: getSourceFolders(project)) {
                if (sourceFolder.isPrefixOf(path)) {
                    return true;
                }
            }
        }
        return false;
    }

	public static JDTModelLoader getProjectModelLoader(IProject project) {
	    TypeChecker typeChecker = getProjectTypeChecker(project);
	    if (typeChecker == null) {
	        return null;
	    }
	    return (JDTModelLoader) ((JDTModuleManager) typeChecker.getPhasedUnits().getModuleManager()).getModelLoader();
	}

    final static class BooleanHolder {
        public boolean value;
    };
	
	@Override
    protected IProject[] build(final int kind, Map args, IProgressMonitor monitor) throws CoreException {
        if (getPreferencesService().getProject() == null) {
            getPreferencesService().setProject(getProject());
        }
        
        boolean emitDiags= getDiagPreference();

        fSourcesToCompile.clear();

        IProject project = getProject();
        ISourceProject sourceProject = getSourceProject();
        if (sourceProject == null) {
            return new IProject[0];
        }

        List<PhasedUnit> builtPhasedUnits = Collections.emptyList();
        List<IProject> requiredProjects = getRequiredProjects(project);
        
        final BooleanHolder mustDoFullBuild = new BooleanHolder();
        final BooleanHolder mustResolveClasspathContainer = new BooleanHolder();
        final IResourceDelta currentDelta = getDelta(getProject());
        
        boolean somethingToDo = chooseBuildTypeFromDeltas(kind, currentDelta,
                monitor, mustDoFullBuild,
                mustResolveClasspathContainer);

        if (! somethingToDo) {
            return requiredProjects.toArray(new IProject[0]);
        }
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        if (mustResolveClasspathContainer.value) {
            IJavaProject javaProject = JavaCore.create(project);
            if (javaProject != null) {
                List<CeylonClasspathContainer> cpContainers = CeylonClasspathUtil.getCeylonClasspathContainers(javaProject);
                for (CeylonClasspathContainer container : cpContainers) {
                    container.launchResolve(false, null);
                }
                return requiredProjects.toArray(new IProject[0]);
            }
        }
        
        try {
            startTime = System.nanoTime();
            MessageConsole console = findConsole();
            IBuildConfiguration[] buildConfsBefore = getContext().getAllReferencedBuildConfigs();
            if (buildConfsBefore.length == 0) {
                console.activate();
            }
            getConsoleStream().println("\n===================================");
            getConsoleStream().println(timedMessage("Starting Ceylon build on project : " + getProject()));
            getConsoleStream().println("-----------------------------------");
            boolean binariesGenerationOK;
            
            sourceFolders.clear();
            
            if (mustDoFullBuild.value) {
                monitor.beginTask("Full Ceylon build of project " + project.getName(), 9);
                getConsoleStream().println(timedMessage("Full build of model"));
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                
                monitor.subTask("Clearing existing markers");
                clearProjectMarkers(project);
                clearMarkersOn(project);
                monitor.worked(1);
                
                if (! getModelState(project).equals(ModelState.Parsed)) {
                    parseCeylonModel(project, monitor);
                }
                
                project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                final TypeChecker typeChecker = getProjectTypeChecker(project);
                
                modelStates.put(project, ModelState.TypeChecking);
                builtPhasedUnits = fullTypeCheck(project, sourceProject, typeChecker, monitor);
                modelStates.put(project, ModelState.TypeChecked);
                
                getConsoleStream().println(timedMessage("Full generation of class files..."));
                monitor.subTask("Generating binaries");
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                final List<IFile> allSources = getProjectSources(project);
                binariesGenerationOK = generateBinaries(project, sourceProject, allSources, monitor);
                monitor.worked(1);
                getConsoleStream().println(successMessage(binariesGenerationOK));
            }
            else
            {
                monitor.beginTask("Incremental Ceylon build of project " + project.getName(), 7);
                getConsoleStream().println(timedMessage("Incremental build of model"));
                List<IResourceDelta> projectDeltas = new ArrayList<IResourceDelta>();
                projectDeltas.add(currentDelta);
                for (IProject requiredProject : requiredProjects) {
                    projectDeltas.add(getDelta(requiredProject));
                }
                
                final List<IFile> filesToRemove = new ArrayList<IFile>();
                final Set<IFile> fChangedSources = new HashSet<IFile>(); 
                monitor.subTask("Scanning deltas"); 
                for (final IResourceDelta projectDelta : projectDeltas) {
                    if (projectDelta != null) {
                        List<IPath> deltaSourceFolders = getSourceFolders((IProject) projectDelta.getResource());
                        for (IResourceDelta sourceDelta : projectDelta.getAffectedChildren()) {
                            if (! deltaSourceFolders.contains(sourceDelta.getResource().getFullPath())) {
                                // Not a real Ceylon source folder : don't scan changes to it 
                                continue;
                            }
                            if (emitDiags)
                                getConsoleStream().println("==> Scanning resource delta for '" + 
                                        projectDelta.getResource().getName() + "'... <==");
                            sourceDelta.accept(new IResourceDeltaVisitor() {
                                public boolean visit(IResourceDelta delta) throws CoreException {
                                    IResource resource = delta.getResource();
                                    if (resource instanceof IFile) {
                                        IFile file= (IFile) resource;
                                        if (isCeylonOrJava(file)) {
                                            fChangedSources.add(file);
                                            if (projectDelta == currentDelta) {
                                                if (delta.getKind() == IResourceDelta.REMOVED) {
                                                    filesToRemove.add((IFile) resource);
                                                }
                                            }
                                        }
                                        
                                        return false;
                                    }
                                    return true;
                                }
                            });
                            if (emitDiags)
                                getConsoleStream().println("Delta scan completed for project '" + 
                                        projectDelta.getResource().getName() + "'...");
                        }
                    }
                }
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                TypeChecker typeChecker = typeCheckers.get(project);
                PhasedUnits phasedUnits = typeChecker.getPhasedUnits();
                
                monitor.worked(1);
                monitor.subTask("Scanning dependencies of deltas"); 
                if (fChangedSources.size() > 0) {
                    Collection<IFile> changeDependents= new HashSet<IFile>();
    
                    changeDependents.addAll(fChangedSources);
                    if (emitDiags) {
                        getConsoleStream().println("Changed files:");
                        dumpSourceList(changeDependents);
                    }
    
                    boolean changed = false;
                    do {
                        Collection<IFile> additions= new HashSet<IFile>();
                        for(Iterator<IFile> iter= changeDependents.iterator(); iter.hasNext(); ) {
                            IFile srcFile= iter.next();
                            IProject currentFileProject = srcFile.getProject();
                            TypeChecker currentFileTypeChecker = null;
                            if (currentFileProject == project) {
                                currentFileTypeChecker = typeChecker;
                            } else {
                                currentFileTypeChecker = getProjectTypeChecker(currentFileProject);
                            }
                            
                            Set<PhasedUnit> phasedUnitsDependingOn = Collections.emptySet();
                            
                            phasedUnitsDependingOn = getDependentsOf(srcFile,
                                    currentFileTypeChecker, currentFileProject);
    
                            for(PhasedUnit dependingPhasedUnit : phasedUnitsDependingOn ) {
                                if (monitor.isCanceled()) {
                                    throw new OperationCanceledException();
                                }
                                IFile depFile= (IFile) ((IFileVirtualFile) dependingPhasedUnit.getUnitFile()).getResource();
                                IProject newDependencyProject = depFile.getProject();
                                if (newDependencyProject == project || newDependencyProject == currentFileProject) {
                                    additions.add(depFile);
                                } else {
    //                                            System.out.println("a depending resource is in a third-party project");
                                }
                                
                            }
                        }
                        changed = changeDependents.addAll(additions);
                    } while (changed);
    
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                    for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                        Unit unit = phasedUnit.getUnit();
                        if (unit.getUnresolvedReferences().size() > 0) {
                            IFile fileToAdd = (IFile) ((IFileVirtualFile)(phasedUnit.getUnitFile())).getResource();
                            if (fileToAdd.exists()) {
                                fSourcesToCompile.add(fileToAdd);
                            }
                        }
                        Set<Declaration> duplicateDeclarations = unit.getDuplicateDeclarations();
                        if (duplicateDeclarations.size() > 0) {
                            IFile fileToAdd = (IFile) ((IFileVirtualFile)(phasedUnit.getUnitFile())).getResource();
                            if (fileToAdd.exists()) {
                                fSourcesToCompile.add(fileToAdd);
                            }
                            for (Declaration duplicateDeclaration : duplicateDeclarations) {
                                PhasedUnit duplicateDeclPU = CeylonReferenceResolver.getPhasedUnit(project, duplicateDeclaration);
                                if (duplicateDeclPU != null) {
                                    IFile duplicateDeclFile = (IFile) ((IFileVirtualFile)(duplicateDeclPU.getUnitFile())).getResource();
                                    if (duplicateDeclFile.exists()) {
                                        fSourcesToCompile.add(duplicateDeclFile);
                                    }
                                }
                            }
                        }
                    }
                    
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                    for(IFile f: changeDependents) {
                        if (isSourceFile(f) && f.getProject() == project) {
                            if (f.exists()) {
                                fSourcesToCompile.add(f);
                            }
                            else {
                                // If the file is moved : add a dependency on the new file
                                if (currentDelta != null) {
                                    IResourceDelta removedFile = currentDelta.findMember(f.getProjectRelativePath());
                                    if (removedFile != null && 
                                            (removedFile.getFlags() & IResourceDelta.MOVED_TO) != 0 &&
                                            removedFile.getMovedToPath() != null) {
                                        fSourcesToCompile.add(project.getFile(removedFile.getMovedToPath().removeFirstSegments(1)));
                                    }
                                }
                            }
                        }
                    }
                }

                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                monitor.worked(1);
                monitor.subTask("Cleaning removed files"); 
                removeObsoleteClassFiles(filesToRemove);
                for (IFile fileToRemove : filesToRemove) {
                    if(isCeylon(fileToRemove)) {
                        // Remove the ceylon phasedUnit (which will also remove the unit from the package)
                        PhasedUnit phasedUnitToDelete = phasedUnits.getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(fileToRemove));
                        if (phasedUnitToDelete != null) {
                            phasedUnits.removePhasedUnitForRelativePath(phasedUnitToDelete.getPathRelativeToSrcDir());
                        }
                    }
                    else if (isJava(fileToRemove)) {
                        // Remove the external unit from the package
                        Package pkg = retrievePackage(fileToRemove.getParent());
                        if (pkg != null) {
                            Unit unitToRemove = null;
                            for (Unit unitToTest : pkg.getUnits()) {
                                if (unitToTest.getFilename().equals(fileToRemove.getName())) {
                                    unitToRemove = unitToTest;
                                    break;
                                }
                            }
                            if (unitToRemove != null) {
                                pkg.removeUnit(unitToRemove);
                            }
                        }
                    }
                }
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                monitor.worked(1);
                monitor.subTask("Compiling " + fSourcesToCompile + " file(s)"); 
                if (emitDiags) {
                    getConsoleStream().println("All files to compile:");
                    dumpSourceList(fSourcesToCompile);
                }
                builtPhasedUnits = incrementalBuild(project, sourceProject, monitor);
                if (builtPhasedUnits== null)
                    return new IProject[0];
                
                if (builtPhasedUnits.isEmpty() && fSourcesToCompile.isEmpty()) {
                    return requiredProjects.toArray(new IProject[0]);
                }
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                monitor.worked(1);
                monitor.subTask("Generating binaries");
                getConsoleStream().println(timedMessage("Incremental generation of class files..."));
                binariesGenerationOK = generateBinaries(project, sourceProject, fSourcesToCompile, monitor);
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                getConsoleStream().println(successMessage(binariesGenerationOK));
                monitor.worked(1);
                monitor.subTask("Updating referencing projects");
                getConsoleStream().println(timedMessage("Updating model in referencing projects"));
                updateExternalPhasedUnitsInReferencingProjects(project, builtPhasedUnits);
                monitor.worked(1);
            }
            
            if (!binariesGenerationOK) {
                // Add a problem marker if binary generation went wrong for ceylon files
                IMarker marker = project.createMarker(PROBLEM_MARKER_ID);
                marker.setAttribute(IMarker.MESSAGE, "Bytecode generation has failed on some Ceylon source files. Look at the Ceylon console for more information.");
                marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.LOCATION, "Bytecode generation");
            }
            
            TypeChecker typeChecker = typeCheckers.get(project); // could have been instanciated and added into the map by the full build
            
            monitor.subTask("Collecting dependencies");
            getConsoleStream().println(timedMessage("Collecting dependencies"));
            List<PhasedUnits> phasedUnitsForDependencies = new ArrayList<PhasedUnits>();
            
            for (IProject requiredProject : requiredProjects) {
                TypeChecker requiredProjectTypeChecker = getProjectTypeChecker(requiredProject);
                if (requiredProjectTypeChecker != null) {
                    phasedUnitsForDependencies.add(requiredProjectTypeChecker.getPhasedUnits());
                }
            }
            
            for (PhasedUnit pu : builtPhasedUnits) {
                pu.collectUnitDependencies(typeChecker.getPhasedUnits(), phasedUnitsForDependencies);
            }
    
            
            monitor.worked(1);
            monitor.done();
            return requiredProjects.toArray(new IProject[0]);
        }
        finally {
            getConsoleStream().println("-----------------------------------");
            getConsoleStream().println(timedMessage("End Ceylon build on project : " + getProject()));
            getConsoleStream().println("===================================");
        }
    }

    public boolean chooseBuildTypeFromDeltas(final int kind, final IResourceDelta currentDelta,
            IProgressMonitor monitor,
            final CeylonBuilder.BooleanHolder mustDoFullBuild,
            final CeylonBuilder.BooleanHolder mustResolveClasspathContainer) {
        mustDoFullBuild.value = kind == FULL_BUILD || kind == CLEAN_BUILD || ! isModelAvailable(getProject());
        mustResolveClasspathContainer.value = false;
        final BooleanHolder sourceModified = new BooleanHolder();
        sourceModified.value = false;
        
        if (! mustDoFullBuild.value) {
            if (currentDelta != null) {
                try {
                    currentDelta.accept(new IResourceDeltaVisitor() {
                        @Override
                        public boolean visit(IResourceDelta resourceDelta) throws CoreException {
                            IResource resource = resourceDelta.getResource();
                            if (resourceDelta.getKind() == IResourceDelta.REMOVED) {
                                if (resource instanceof IFolder) {
                                    IFolder folder = (IFolder) resource; 
                                    Package pkg = retrievePackage(folder);
                                    // If a package has been removed, then trigger a full build
                                    if (pkg != null) {
                                        mustDoFullBuild.value = true;
                                        return true;
                                    }
                                }
                            }
                            
                            if (resource instanceof IFile) {
                                if (resource.getName().equals(ModuleManager.PACKAGE_FILE)) {
                                    // If a package descriptor has been added, removed or changed, trigger a full build
                                    mustDoFullBuild.value = true;
                                    return false;
                                }
                                if (resource.getName().equals(ModuleManager.MODULE_FILE)) {
                                    // If a module descriptor has been added, removed or changed, resolve the Ceylon classpath container
                                    mustResolveClasspathContainer.value = true;
                                    return false;
                                }
                                if ( isCeylonOrJava((IFile)resource) ) {
                                    sourceModified.value = true;
                                }
                            }
                            
                            if (resource instanceof IProject && 
                                    ((resourceDelta.getFlags() & IResourceDelta.DESCRIPTION) != 0)) {
                                mustDoFullBuild.value = true;
                                return false;
                            }
                            
                            return true;
                        }
                    });
                } catch (CoreException e) {
                    getPlugin().getLog().log(new Status(IStatus.ERROR, getPlugin().getID(), e.getLocalizedMessage(), e));
                    mustDoFullBuild.value = true;
                }
            } else {
                mustDoFullBuild.value = true;
            }
        }
        return mustDoFullBuild.value || 
                mustResolveClasspathContainer.value ||
                sourceModified.value;
    }

    private static String successMessage(boolean binariesGenerationOK) {
        return "             " + (binariesGenerationOK ? 
                "...binary generation succeeded" : "...binary generation FAILED");
    }

    private Set<PhasedUnit> getDependentsOf(IFile srcFile,
            TypeChecker currentFileTypeChecker,
            IProject currentFileProject) {
        if(LANGUAGE.hasExtension(srcFile.getRawLocation().getFileExtension())) {
            PhasedUnit phasedUnit = currentFileTypeChecker.getPhasedUnits().getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(srcFile));
            if (phasedUnit != null) {
                return phasedUnit.getDependentsOf();
            }
        } else {
            Unit unit = getJavaUnit(currentFileProject, srcFile);
            if (unit instanceof ExternalUnit) {
                return ((ExternalUnit)unit).getDependentsOf();
            }
        }
        
        return Collections.emptySet();
    }

    private void updateExternalPhasedUnitsInReferencingProjects(
            IProject project, List<PhasedUnit> builtPhasedUnits) {
        for (IProject referencingProject : project.getReferencingProjects()) {
            TypeChecker referencingTypeChecker = getProjectTypeChecker(referencingProject);
            if (referencingTypeChecker != null) {
                List<PhasedUnit> referencingPhasedUnits = new ArrayList<PhasedUnit>();
                for (PhasedUnit builtPhasedUnit : builtPhasedUnits) {
                    List<PhasedUnits> phasedUnitsOfDependencies = referencingTypeChecker.getPhasedUnitsOfDependencies();
                    for (PhasedUnits phasedUnitsOfDependency : phasedUnitsOfDependencies) {
                        String relativePath = builtPhasedUnit.getPathRelativeToSrcDir();
                        PhasedUnit referencingPhasedUnit = phasedUnitsOfDependency.getPhasedUnitFromRelativePath(relativePath);
                        if (referencingPhasedUnit != null) {
                            phasedUnitsOfDependency.removePhasedUnitForRelativePath(relativePath);
                            PhasedUnit newReferencingPhasedUnit = new PhasedUnit(referencingPhasedUnit.getUnitFile(), 
                                    referencingPhasedUnit.getSrcDir(), 
                                    builtPhasedUnit.getCompilationUnit(), 
                                    referencingPhasedUnit.getPackage(), 
                                    phasedUnitsOfDependency.getModuleManager(), 
                                    referencingTypeChecker.getContext(), 
                                    builtPhasedUnit.getTokens());
                            phasedUnitsOfDependency.addPhasedUnit(newReferencingPhasedUnit.getUnitFile(), 
                                    newReferencingPhasedUnit);
                            // replace referencingPhasedUnit
                            referencingPhasedUnits.add(newReferencingPhasedUnit);
                        }
                    }
                }
                
                for (PhasedUnit pu : referencingPhasedUnits) {
                    pu.scanDeclarations();
                }
                for (PhasedUnit pu : referencingPhasedUnits) {
                    pu.scanTypeDeclarations();
                }
                for (PhasedUnit pu : referencingPhasedUnits) {
                    pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
                }
            }
        }
    }

    private static PhasedUnit parseFileToPhasedUnit(ModuleManager moduleManager, TypeChecker typeChecker,
            ResourceVirtualFile file, ResourceVirtualFile srcDir,
            Package pkg) {
        Context context = typeChecker.getContext();
        ANTLRInputStream input;
        try {
            input = new ANTLRInputStream(file.getInputStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        CeylonLexer lexer = new CeylonLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        CeylonParser parser = new CeylonParser(tokenStream);
        Tree.CompilationUnit cu;
        try {
            cu = parser.compilationUnit();
        }
        catch (org.antlr.runtime.RecognitionException e) {
            throw new RuntimeException(e);
        }
        
        List<CommonToken> tokens = new ArrayList<CommonToken>(tokenStream.getTokens().size()); 
        tokens.addAll(tokenStream.getTokens());

        List<LexError> lexerErrors = lexer.getErrors();
        for (LexError le : lexerErrors) {
            //System.out.println("Lexer error in " + file.getName() + ": " + le.getMessage());
            cu.addLexError(le);
        }
        lexerErrors.clear();

        List<ParseError> parserErrors = parser.getErrors();
        for (ParseError pe : parserErrors) {
            //System.out.println("Parser error in " + file.getName() + ": " + pe.getMessage());
            cu.addParseError(pe);
        }
        parserErrors.clear();
        
        
        PhasedUnit newPhasedUnit = new CeylonSourceFile(file, srcDir, cu, pkg, 
                moduleManager, typeChecker, tokens);
        
        return newPhasedUnit;
    }
    
    private List<PhasedUnit> incrementalBuild(IProject project,
        ISourceProject sourceProject, IProgressMonitor monitor) {
        TypeChecker typeChecker = typeCheckers.get(project);
        JDTModuleManager moduleManager = (JDTModuleManager) typeChecker.getPhasedUnits().getModuleManager(); 
        JDTModelLoader modelLoader = getProjectModelLoader(project);
        Set<String> cleanedPackages = new HashSet<String>();
        
        List<PhasedUnit> phasedUnitsToUpdate = new ArrayList<PhasedUnit>();
        for (IFile fileToUpdate : fSourcesToCompile) {
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            // skip non-ceylon files
            if(!isCeylon(fileToUpdate)) {
                if (isJava(fileToUpdate)) {
                    Unit toRemove = getJavaUnit(project, fileToUpdate);
                    if(toRemove != null) { // If the unit is not null, the package should never be null
                        toRemove.getPackage().removeUnit(toRemove);
                    }
                    else {
                        String packageName = getPackageName(fileToUpdate);
                        if (! cleanedPackages.contains(packageName)) {
                            modelLoader.clearCachesOnPackage(packageName);
                        }
                    }
                }
                continue;
            }
            
            ResourceVirtualFile file = ResourceVirtualFile.createResourceVirtualFile(fileToUpdate);
            IPath srcFolderPath = retrieveSourceFolder(fileToUpdate);
            ResourceVirtualFile srcDir = new IFolderVirtualFile(project, srcFolderPath);

            PhasedUnit alreadyBuiltPhasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(file);

            Package pkg = null;
            Set<PhasedUnit> dependentsOf = Collections.emptySet();
            if (alreadyBuiltPhasedUnit!=null) {
                // Editing an already built file
                pkg = alreadyBuiltPhasedUnit.getPackage();
                dependentsOf = alreadyBuiltPhasedUnit.getDependentsOf();
            }
            else {
                IContainer packageFolder = file.getResource().getParent();
                pkg = retrievePackage(packageFolder);
                if (pkg == null) {
                    pkg = createNewPackage(packageFolder);
                }
            }
            PhasedUnit newPhasedUnit = parseFileToPhasedUnit(moduleManager, typeChecker, file, srcDir, pkg);
            newPhasedUnit.getDependentsOf().addAll(dependentsOf);
            phasedUnitsToUpdate.add(newPhasedUnit);
            
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        if (phasedUnitsToUpdate.size() == 0) {
            return phasedUnitsToUpdate;
        }
        
        clearProjectMarkers(project);
        clearMarkersOn(fSourcesToCompile);
        
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            if (typeChecker.getPhasedUnits().getPhasedUnitFromRelativePath(phasedUnit.getPathRelativeToSrcDir()) != null) {
                typeChecker.getPhasedUnits().removePhasedUnitForRelativePath(phasedUnit.getPathRelativeToSrcDir());
            }
            typeChecker.getPhasedUnits().addPhasedUnit(phasedUnit.getUnitFile(), phasedUnit);
        }
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
            if (! phasedUnit.isDeclarationsScanned()) {
                phasedUnit.scanDeclarations();
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            if (! phasedUnit.isTypeDeclarationsScanned()) {
                phasedUnit.scanTypeDeclarations();
            }
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
                phasedUnit.analyseTypes();
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.analyseFlow();
        }

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        addProblemAndTaskMarkers(phasedUnitsToUpdate);
        
        return phasedUnitsToUpdate;
    }

    private Unit getJavaUnit(IProject project, IFile fileToUpdate) {
        IJavaElement javaElement = (IJavaElement) fileToUpdate.getAdapter(IJavaElement.class);
        if (javaElement instanceof ICompilationUnit) {
            ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
            IJavaElement packageFragment = compilationUnit.getParent();
            JDTModelLoader projectModelLoader = getProjectModelLoader(project);
            if (projectModelLoader != null) {
                Package pkg = projectModelLoader.findPackage(packageFragment.getElementName());
                if (pkg != null) {
                    for (Unit unit : pkg.getUnits()) {
                        if (unit.getFilename().equals(fileToUpdate.getName())) {
                            return unit;
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<PhasedUnit> fullTypeCheck(IProject project, ISourceProject sourceProject, TypeChecker typeChecker, IProgressMonitor monitor) throws CoreException {
        System.out.println("Starting ceylon full build of project " + project.getName());

        monitor.subTask("Typechecking Ceylon source archives for project " 
                + project.getName());

        List<PhasedUnits> phasedUnitsOfDependencies = typeChecker.getPhasedUnitsOfDependencies();

        List<PhasedUnit> dependencies = new ArrayList<PhasedUnit>();
 
        for (PhasedUnits phasedUnits : phasedUnitsOfDependencies) {
            for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                dependencies.add(phasedUnit);
            }
        }

        JDTModelLoader loader = getProjectModelLoader(project);
        loader.reset();
        
        for (PhasedUnits dependencyPhasedUnits : phasedUnitsOfDependencies) {
            loader.addSourceArchivePhasedUnits(dependencyPhasedUnits.getPhasedUnits());
        }
        
        for (PhasedUnit pu : dependencies) {
            pu.scanDeclarations();
        }
        for (PhasedUnit pu : dependencies) {
            pu.scanTypeDeclarations();
        }
        for (PhasedUnit pu : dependencies) {
            pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
        }

        
        loader.loadPackage("com.redhat.ceylon.compiler.java.metadata", true);
        loader.loadPackage("ceylon.language", true);
        loader.loadPackage("ceylon.language.descriptor", true);
        loader.loadPackageDescriptors();

        final List<PhasedUnit> listOfUnits = typeChecker.getPhasedUnits().getPhasedUnits();

        monitor.subTask("Typechecking Ceylon source files for project " 
                + project.getName());

        for (PhasedUnit pu : listOfUnits) {
            if (! pu.isDeclarationsScanned()) {
                pu.validateTree();
                pu.scanDeclarations();
            }
        }
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit pu : listOfUnits) {
            if (! pu.isTypeDeclarationsScanned()) {
                pu.scanTypeDeclarations();
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
                pu.analyseTypes();
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit pu: listOfUnits) {
            pu.analyseFlow();
        }

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        monitor.worked(1);
        monitor.subTask("Collecting Ceylon problems for project " 
                + project.getName());
    
        addProblemAndTaskMarkers(typeChecker.getPhasedUnits().getPhasedUnits());
        monitor.worked(1);

        System.out.println("Finished ceylon full build of project " + project.getName());
        return typeChecker.getPhasedUnits().getPhasedUnits();
    }

    public static TypeChecker parseCeylonModel(
            final IProject project,
            IProgressMonitor monitor) throws CoreException {
        monitor.subTask("Collecting Ceylon source files for project " 
                    + project.getName());

        modelStates.put(project, ModelState.Parsing);
        typeCheckers.remove(project);
        projectSources.remove(project);
        final List<IFile> scannedSources = new ArrayList<IFile>();

        monitor.subTask("Setting up typechecker for project " 
                + project.getName());

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        final IJavaProject javaProject = JavaCore.create(project);
        TypeCheckerBuilder typeCheckerBuilder = new TypeCheckerBuilder()
            .verbose(false)
            .moduleManagerFactory(new ModuleManagerFactory(){
                @Override
                public ModuleManager createModuleManager(Context context) {
                    return new JDTModuleManager(context, javaProject);
                }
            });

        List<String> repos = getUserRepositories(project);
        typeCheckerBuilder.setRepositoryManager(Util.makeRepositoryManager(repos, getModulesOutputDirectory(javaProject).getAbsolutePath(), new EclipseLogger()));
        final TypeChecker typeChecker = typeCheckerBuilder.getTypeChecker();
        final PhasedUnits phasedUnits = typeChecker.getPhasedUnits();

        final JDTModuleManager moduleManager = (JDTModuleManager) phasedUnits.getModuleManager();
        moduleManager.setTypeChecker(typeChecker);
        final Context context = typeChecker.getContext();
        final JDTModelLoader modelLoader = (JDTModelLoader) moduleManager.getModelLoader();
        final Module defaultModule = context.getModules().getDefaultModule();

        monitor.worked(1);
        
        monitor.subTask("Parsing Ceylon source files for project " 
                    + project.getName());

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        final Collection<IPath> sourceFolders = getSourceFolders(project);
        for (final IPath srcAbsoluteFolderPath : sourceFolders) {
            final IPath srcFolderPath = srcAbsoluteFolderPath.makeRelativeTo(project.getFullPath());
            final ResourceVirtualFile srcDir = new IFolderVirtualFile(project, srcFolderPath);

            IResource srcDirResource = srcDir.getResource();
            if (! srcDirResource.exists()) {
                continue;
            }
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            srcDirResource.accept(new IResourceVisitor() {
                private Module module;
                
                public boolean visit(IResource resource) throws CoreException {
                    Package pkg;
                    if (resource.equals(srcDir.getResource())) {
                        IFile moduleFile = ((IFolder) resource).getFile(ModuleManager.MODULE_FILE);
                        if (moduleFile.exists()) {
                            moduleManager.addTopLevelModuleError();
                        }
                        return true;
                    }

                    if (resource.getParent().equals(srcDir.getResource())) {
                        // We've come back to a source directory child : 
                        //  => reset the current Module to default and set the package to emptyPackage
                        module = defaultModule;
                        pkg = modelLoader.findPackage("");
                        assert(pkg != null);
                    }

                    if (resource instanceof IFolder) {
                        List<String> pkgName = Arrays.asList(resource.getProjectRelativePath().makeRelativeTo(srcFolderPath).segments());
                        String pkgNameAsString = formatPath(pkgName);
                        
                        if ( module != defaultModule ) {
                            if (! pkgNameAsString.startsWith(module.getNameAsString() + ".")) {
                                // We've ran above the last module => reset module to default 
                                module = defaultModule;
                            }
                        }
                        
                        IFile moduleFile = ((IFolder) resource).getFile(ModuleManager.MODULE_FILE);
                        if (moduleFile.exists()) {
                            if ( module != defaultModule ) {
                                moduleManager.addTwoModulesInHierarchyError(module.getName(), pkgName);
                            } else {
                                final List<String> moduleName = pkgName;
                                //we don't know the version at this stage, will be filled later
                                module = moduleManager.getOrCreateModule(moduleName, null);
                                assert(module != null);
                            }
                        }
                        
                        pkg = modelLoader.findOrCreatePackage(module, pkgNameAsString);
                        return true;
                    }

                    if (resource instanceof IFile) {
                        IFile file = (IFile) resource;
                        if (file.exists() && isCeylonOrJava(file)) {
                            List<String> pkgName = Arrays.asList(file.getParent().getProjectRelativePath().makeRelativeTo(srcFolderPath).segments());
                            String pkgNameAsString = formatPath(pkgName);
                            pkg = modelLoader.findOrCreatePackage(module, pkgNameAsString);
                            
                            if (isCeylon(file)) {
                                if (scannedSources != null) {
                                    scannedSources.add(file);
                                }
                                ResourceVirtualFile virtualFile = ResourceVirtualFile.createResourceVirtualFile(file);
                                PhasedUnit newPhasedUnit = parseFileToPhasedUnit(moduleManager, typeChecker, virtualFile, srcDir, pkg);
                                phasedUnits.addPhasedUnit(virtualFile, newPhasedUnit);
                            }
                            if (isJava((IFile)resource)) {
                                if (scannedSources != null) {
                                    scannedSources.add((IFile)resource);
                                }
                            }
                        }
                    }
                    return false;
                }
            });
        }

        monitor.worked(1);
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        modelLoader.setupSourceFileObjects(typeChecker.getPhasedUnits().getPhasedUnits());

        monitor.worked(1);
        
        // Parsing of ALL units in the source folder should have been done

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        phasedUnits.getModuleManager().prepareForTypeChecking();
        phasedUnits.visitModules();

        //By now the language module version should be known (as local)
        //or we should use the default one.
        Module languageModule = context.getModules().getLanguageModule();
        if (languageModule.getVersion() == null) {
            languageModule.setVersion(TypeChecker.LANGUAGE_MODULE_VERSION);
        }

        final ModuleValidator moduleValidator = new ModuleValidator(context, phasedUnits) {

            @Override
            protected void executeExternalModulePhases() {
            }
            
        };
        
        monitor.worked(1);
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        moduleValidator.verifyModuleDependencyTree();
        typeChecker.setPhasedUnitsOfDependencies(moduleValidator.getPhasedUnitsOfDependencies());

        typeCheckers.put(project, typeChecker);
        projectSources.put(project, scannedSources);
        modelStates.put(project, ModelState.Parsed);
        
        return typeChecker;
    }

    private static void addProblemAndTaskMarkers(List<PhasedUnit> units) {
        for (PhasedUnit phasedUnit : units) {
            IFile file = getFile(phasedUnit);
            List<CommonToken> tokens = phasedUnit.getTokens();
            phasedUnit.getCompilationUnit()
                .visit(new ErrorVisitor(new MarkerCreator(file, PROBLEM_MARKER_ID)) {
                    @Override
                    public int getSeverity(Message error, boolean expected) {
                        return expected ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR;
                    }
                });
            addTaskMarkers(file, tokens);
        }
    }

    private boolean generateBinaries(IProject project, ISourceProject sourceProject, Collection<IFile> filesToCompile, IProgressMonitor monitor) throws CoreException {
        List<String> options = new ArrayList<String>();

        String srcPath = "";
        for (IPath sourceFolder : getSourceFolders(project)) {
            File sourcePathElement = toFile(project,sourceFolder.makeRelativeTo(project.getFullPath()));
            if (! srcPath.isEmpty()) {
                srcPath += File.pathSeparator;
            }
            srcPath += sourcePathElement.getAbsolutePath();
        }
        options.add("-src");
        options.add(srcPath);
        
        for (String repository : getUserRepositories(project)) {
            options.add("-rep");
            options.add(repository);
        }

        String verbose = System.getProperty("ceylon.verbose");
		if (verbose!=null && "true".equals(verbose)) {
            options.add("-verbose");
        }
        options.add("-g:lines,vars,source");

        IJavaProject javaProject = JavaCore.create(project);
        final File modulesOutputDir = getModulesOutputDirectory(javaProject);
        final File ceylonOutputDir = getCeylonOutputDirectory(javaProject);
        if (modulesOutputDir!=null) {
            options.add("-out");
            options.add(modulesOutputDir.getAbsolutePath());
        }

        java.util.List<File> javaSourceFiles = new ArrayList<File>();
        java.util.List<File> sourceFiles = new ArrayList<File>();
        for (IFile file : filesToCompile) {
            if(isCeylon(file))
                sourceFiles.add(file.getRawLocation().toFile());
            else if(isJava(file))
                javaSourceFiles.add(file.getRawLocation().toFile());
        }

        if (sourceFiles.size() > 0 || javaSourceFiles.size() > 0)
        {
            PrintWriter printWriter = new PrintWriter(getConsoleStream(), true);

            boolean success = true;
            if (! compileWithJDTModelLoader) {
                // first java source files
                if(!javaSourceFiles.isEmpty()){
                    compile(project, options, javaSourceFiles, printWriter);
                }
            } else {
                sourceFiles.addAll(javaSourceFiles);
            }
            // then ceylon source files if that last run worked
            if(!sourceFiles.isEmpty()){
                success = compile(project, options, sourceFiles, printWriter);
            }
            if (modulesOutputDir != null) {
                monitor.subTask("Unzipping archives files in the Ceylon output folder");
                List<String> extensionsToUnzip = Arrays.asList(".car");
                new RepositoryLister(extensionsToUnzip).list(modulesOutputDir, new RepositoryLister.Actions() {
                    @Override
                    public void doWithFile(File path) {
                        try {
                            ZipFile carFile = new ZipFile(path);
                            carFile.extractAll(ceylonOutputDir.getAbsolutePath());
                        } catch (ZipException e) {
                            e.printStackTrace();
                        }
                    }
                });
                monitor.subTask("Refreshing the Ceylon output folder");
                javaProject.getProject().getFolder("JDTClasses")
                        .refreshLocal(IResource.DEPTH_INFINITE, null);
            }
            
            return success;
        }
        else
            return true;
    }

    private boolean compile(final IProject project, List<String> options,
            java.util.List<File> sourceFiles, PrintWriter printWriter) throws VerifyError {
        CeyloncTool compiler;
        try {
            compiler = new CeyloncTool();
        } catch (VerifyError e) {
            System.err.println("ERROR: Cannot run tests! Did you maybe forget to configure the -Xbootclasspath/p: parameter?");
            throw e;
        }

        final com.sun.tools.javac.util.Context context = new com.sun.tools.javac.util.Context();
        context.put(com.sun.tools.javac.util.Log.outKey, printWriter);
        CeylonLog.preRegister(context);

        CeyloncFileManager fileManager = new CeyloncFileManager(context, true, null); //(CeyloncFileManager)compiler.getStandardFileManager(null, null, null);
        
        ArtifactContext ctx = null;
        Modules projectModules = getProjectModules(project);
        if (projectModules != null) {
            Module languageModule = projectModules.getLanguageModule();
            ctx = new ArtifactContext(languageModule.getNameAsString(), languageModule.getVersion());
        } else {
            ctx = new ArtifactContext("ceylon.language", TypeChecker.LANGUAGE_MODULE_VERSION);
        }
        List<String> classpathElements = new ArrayList<String>();
        ctx.setSuffix(ArtifactContext.CAR);
        RepositoryManager repositoryManager = getProjectRepositoryManager(project);
        if (repositoryManager != null) {
            File languageModuleArchive;
            try {
                languageModuleArchive = repositoryManager.getArtifact(ctx);
                classpathElements.add(languageModuleArchive.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        IJavaProject javaProject = JavaCore.create(project);
        IPath workspaceLocation = project.getWorkspace().getRoot().getLocation();
        try {
            List<CeylonClasspathContainer> containers = CeylonClasspathUtil.getCeylonClasspathContainers(javaProject);
            for (CeylonClasspathContainer container : containers) {
                for (IClasspathEntry cpEntry : container.getClasspathEntries()) {
                    classpathElements.add(cpEntry.getPath().toOSString());
                }
            }
            
            classpathElements.add(workspaceLocation.append(javaProject.getOutputLocation()).toOSString());
        } catch (JavaModelException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        options.add("-classpath");
        String classpath = "";
        for (String cpElement : classpathElements) {
            if (! classpath.isEmpty()) {
                classpath += File.pathSeparator;
            }
            classpath += cpElement;
        }
        options.add(classpath);
        
        final JDTModelLoader modelLoader = getProjectModelLoader(project);
        
        Iterable<? extends JavaFileObject> compilationUnits1 =
                fileManager.getJavaFileObjectsFromFiles(sourceFiles);
        if (compileWithJDTModelLoader) {
            final TypeChecker typeChecker = getProjectTypeChecker(project);
            context.put(LanguageCompiler.ceylonContextKey, typeChecker.getContext());
            context.put(TypeFactory.class, modelLoader.getTypeFactory());
            context.put(LanguageCompiler.phasedUnitsManagerKey, new PhasedUnitsManager() {
                @Override
                public ModuleManager getModuleManager() {
                    return typeChecker.getPhasedUnits().getModuleManager();
                }

                @Override
                public void resolveDependencies() {
                }

                @Override
                public PhasedUnit getExternalSourcePhasedUnit(
                        VirtualFile srcDir, VirtualFile file) {
                    return typeChecker.getPhasedUnits().getPhasedUnitFromRelativePath(Helper.computeRelativePath(file, srcDir));
                }

                @Override
                public Iterable<PhasedUnit> getPhasedUnitsForExtraPhase(
                        List<PhasedUnit> sourceUnits) {
                    if (getModelState(project).equals(ModelState.Compiled)) {
                        return sourceUnits;
                    }
                    List<PhasedUnit> dependencies = new ArrayList<PhasedUnit>();
                    for (PhasedUnits phasedUnits : typeChecker.getPhasedUnitsOfDependencies()) {
                        for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                            dependencies.add(phasedUnit);
                        }
                    }
                    
                    for (PhasedUnit dependency : dependencies) {
                        dependency.analyseTypes();
                    }
                    for (PhasedUnit dependency : dependencies) {
                        dependency.analyseFlow();
                    }
                    List<PhasedUnit> allPhasedUnits = new ArrayList<PhasedUnit>();
                    allPhasedUnits.addAll(dependencies);
                    allPhasedUnits.addAll(sourceUnits);
                    return allPhasedUnits;
                }
                @Override
                public void extraPhasesApplied() {
                    modelStates.put(project, ModelState.Compiled);
                }
                
            });
            
            modelLoader.setSourceFileObjectManager(new SourceFileObjectManager() {
                @Override
                public void setupSourceFileObjects(List<?> treeHolders) {
                    for(Object treeHolder : treeHolders){
                        if (!(treeHolder instanceof CeylonCompilationUnit)) {
                            continue;
                        }
                        final CeylonCompilationUnit tree = (CeylonCompilationUnit)treeHolder;
                        CompilationUnit ceylonTree = tree.ceylonTree;
                        final String pkgName = tree.getPackageName() != null ? tree.getPackageName().toString() : "";
                        ceylonTree.visit(new SourceDeclarationVisitor(){
                            @Override
                            public void loadFromSource(Tree.Declaration decl) {
                                String name = Util.quoteIfJavaKeyword(decl.getIdentifier().getText());
                                String fqn = pkgName.isEmpty() ? name : pkgName+"."+name;
                                try{
                                    CeylonClassReader.instance(context).enterClass(Names.instance(context).fromString(fqn), tree.getSourceFile());
                                }
                                catch (AssertionError error){
                                    // this happens when we have already registered a source file for this decl, so let's
                                    // print out a helpful message
                                    // see https://github.com/ceylon/ceylon-compiler/issues/250
                                    ClassMirror previousClass = modelLoader.lookupClassMirror(fqn);
                                    CeylonLog.instance(context).error("ceylon", "Duplicate declaration error: " + 
                                            fqn + " is declared twice: once in " + tree.getSourceFile() + 
                                            " and again in: " + fileName(previousClass));
                                }
                            }
                        });
                    }
                }
            });

            context.put(TypeFactory.class, modelLoader.getTypeFactory());
            context.put(ModelLoaderFactory.class, new ModelLoaderFactory() {
                @Override
                public AbstractModelLoader createModelLoader(
                        com.sun.tools.javac.util.Context context) {
                    return modelLoader;
                }
                
            });
        }
        CeyloncTaskImpl task = (CeyloncTaskImpl) compiler.getTask(printWriter, 
                fileManager, null, options, null, compilationUnits1);
        boolean success=false;
        try {
            success = task.call();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return success;
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
    
	public static List<IProject> getRequiredProjects(IJavaProject javaProject) {
	    List<IProject> requiredProjects = new ArrayList<IProject>();
        try {
            for (String requiredProjectName : javaProject.getRequiredProjectNames()) {
                IProject requiredProject = javaProject.getProject().getWorkspace().getRoot().getProject(requiredProjectName);
                
                if (requiredProject != null) {
                    requiredProjects.add(requiredProject);
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return requiredProjects;
	}
	
    public static List<IProject> getRequiredProjects(IProject project) {
        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject == null) {
            return Collections.emptyList();
        }
        return getRequiredProjects(javaProject);
    }
    

    public static List<String> getUserRepositories(IProject project) throws CoreException {
        List<String> userRepos = new ArrayList<String>();

        File repo = getCeylonRepository(project);
        
        userRepos.add(repo.getAbsolutePath());
        
        
        if (project != null) {
            List<IProject> requiredProjects = getRequiredProjects(project);
            for (IProject requiredProject : requiredProjects) {
                if (!requiredProject.isOpen()) {
                    continue;
                }
                
                if (! requiredProject.hasNature(CeylonNature.NATURE_ID)) {
                    continue;
                }
                    
                IJavaProject requiredJavaProject = JavaCore.create(requiredProject);
                if (requiredJavaProject == null) {
                    continue;
                }
                userRepos.add(getModulesOutputDirectory(requiredJavaProject).getAbsolutePath());
            }
            
            /*userRepos.add(project.getLocation().append("modules").toOSString());
            
            for (IProject requiredProject : requiredProjects) {
                userRepos.add(requiredProject.getLocation().append("modules").toOSString());
            }*/
        }
        
        return userRepos;
    }

    public static File getCeylonRepository(IProject project) {
        String repoPath = new ProjectScope(project)
                .getNode(CeylonPlugin.PLUGIN_ID)
                .get("repo", null);
        //project.getPersistentProperty(new QualifiedName(CeylonPlugin.PLUGIN_ID,"repo"));
        File repo;
        if (repoPath==null) {
            repo = CeylonPlugin.getInstance().getCeylonRepository();
        }
        else { 
            repo = CeylonPlugin.getCeylonRepository(repoPath);
        }
        return repo;
    }

    private static File toFile(IProject project, IPath path) {
		return project.getFolder(path).getRawLocation().toFile();
	}
    
    /**
     * Clears all problem markers (all markers whose type derives from IMarker.PROBLEM)
     * from the given file. A utility method for the use of derived builder classes.
     */
    private static void clearMarkersOn(IFile file) {
        try {
            clearTaskMarkersOn(file);
            file.deleteMarkers(CeylonBuilder.PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
        } catch (CoreException e) {
        }
    }

    /**
     * Clears all problem markers (all markers whose type derives from IMarker.PROBLEM)
     * from the given file. A utility method for the use of derived builder classes.
     */
    private static void clearMarkersOn(IResource resource) {
        try {
            clearTaskMarkersOn(resource);
            resource.deleteMarkers(CeylonBuilder.PROBLEM_MARKER_ID, true, IResource.DEPTH_INFINITE);
        } catch (CoreException e) {
        }
    }

    private static void clearProjectMarkers(IProject project) {
        try {
            project.deleteMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
            project.deleteMarkers(PROBLEM_MARKER_ID, true, IResource.DEPTH_ZERO);
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void clearMarkersOn(Collection<IFile> files) {
        for(IFile file: files) {
            clearMarkersOn(file);
        }
    }

    private void dumpSourceList(Collection<IFile> sourcesToCompile) {
        MessageConsoleStream consoleStream= getConsoleStream();

        for(Iterator<IFile> iter= sourcesToCompile.iterator(); iter.hasNext(); ) {
            IFile srcFile= iter.next();

            consoleStream.println("  " + srcFile.getFullPath());
        }
    }

    protected IPreferencesService getPreferencesService() {
        if (fPrefService == null) {
            fPrefService= new PreferencesService(null, getPlugin().getLanguageID());        
        }
        return fPrefService;
    }

    protected boolean getDiagPreference() {
        final IPreferencesService builderPrefSvc= getPlugin().getPreferencesService();
        final IPreferencesService impPrefSvc= RuntimePlugin.getInstance().getPreferencesService();
        
        boolean msgs= builderPrefSvc.isDefined(PreferenceConstants.P_EMIT_BUILDER_DIAGNOSTICS) ?
            builderPrefSvc.getBooleanPreference(PreferenceConstants.P_EMIT_BUILDER_DIAGNOSTICS) :
                impPrefSvc.getBooleanPreference(PreferenceConstants.P_EMIT_BUILDER_DIAGNOSTICS);
        return msgs;
    }

    /**
     * Refreshes all resources in the entire project tree containing the given resource.
     * Crude but effective.
     */
    /*protected void doRefresh(final IResource resource) {
        IWorkspaceRunnable r= new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) throws CoreException {
                resource.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
            }
        };
        try {
            getProject().getWorkspace().run(r, resource.getProject(), IWorkspace.AVOID_UPDATE, null);
        } catch (CoreException e) {
            getPlugin().logException("Error while refreshing after a build", e);
        }
    }*/

    /**
     * @return the ID of the marker type for the given marker severity (one of
     * <code>IMarker.SEVERITY_*</code>). If the severity is unknown/invalid,
     * returns <code>getInfoMarkerID()</code>.
     */
    protected String getMarkerIDFor(int severity) {
        switch(severity) {
            case IMarker.SEVERITY_ERROR: return getErrorMarkerID();
            case IMarker.SEVERITY_WARNING: return getWarningMarkerID();
            case IMarker.SEVERITY_INFO: return getInfoMarkerID();
            default: return getInfoMarkerID();
        }
    }

    /**
     * Utility method to create a marker on the given resource using the given
     * information.
     * @param errorResource
     * @param startLine the line with which the error is associated
     * @param charStart the offset of the first character with which the error is associated               
     * @param charEnd the offset of the last character with which the error is associated
     * @param message a human-readable text message to appear in the "Problems View"
     * @param severity the message severity, one of <code>IMarker.SEVERITY_*</code>
     */
    public IMarker createMarker(IResource errorResource, int startLine, int charStart, int charEnd, String message, int severity) {
        try {
            // TODO Handle resources that are templates and not in user's workspace
            if (!errorResource.exists())
                return null;

            IMarker m = errorResource.createMarker(getMarkerIDFor(severity));

            final int MIN_ATTR= 4;
            final boolean hasStart= (charStart >= 0);
            final boolean hasEnd= (charEnd >= 0);
            final int numAttr= MIN_ATTR + (hasStart ? 1 : 0) + (hasEnd ? 1 : 0);
            String[] attrNames = new String[numAttr];
            Object[] attrValues = new Object[numAttr]; // N.B. Any "null" values will be treated as undefined
            int idx= 0;
            attrNames[idx]= IMarker.LINE_NUMBER; attrValues[idx++]= startLine;
            attrNames[idx]= IMarker.MESSAGE;     attrValues[idx++]= message;
            attrNames[idx]= IMarker.PRIORITY;    attrValues[idx++]= IMarker.PRIORITY_HIGH;
            attrNames[idx]= IMarker.SEVERITY;    attrValues[idx++]= severity;

            if (hasStart) {
                attrNames[idx]= IMarker.CHAR_START; attrValues[idx++]= charStart;
            }
            if (hasEnd) {
                attrNames[idx]= IMarker.CHAR_END;   attrValues[idx++]= charEnd;
            }

            m.setAttributes(attrNames, attrValues);
            return m;
        } catch (CoreException e) {
            getPlugin().writeErrorMsg("Unable to create marker: " + e.getMessage());
        }
        return null;
    }

    public static Shell getShell() {
        return RuntimePlugin.getInstance().getWorkbench()
                .getActiveWorkbenchWindow().getShell();
    }

    /**
     * Posts a dialog displaying the given message as soon as "conveniently possible".
     * This is not a synchronous call, since this method will get called from a
     * different thread than the UI thread, which is the only thread that can
     * post the dialog box.
     */
    protected void postMsgDialog(final String title, final String msg) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                MessageDialog.openInformation(getShell(), title, msg);
            }

        });
    }

    /**
     * Posts a dialog displaying the given message as soon as "conveniently possible".
     * This is not a synchronous call, since this method will get called from a
     * different thread than the UI thread, which is the only thread that can
     * post the dialog box.
     */
    protected void postQuestionDialog(final String title, final String query, final Runnable runIfYes, final Runnable runIfNo) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                boolean response= MessageDialog.openQuestion(getShell(), title, query);

                if (response)
                    runIfYes.run();
                else if (runIfNo != null)
                    runIfNo.run();
            }
        });
    }

    protected static MessageConsoleStream getConsoleStream() {
        return findConsole().newMessageStream();
    }
    
    private String timedMessage(String message) {
        long elapsedTimeMs = (System.nanoTime() - startTime) / 1000000;
        return String.format("[%1$10d] %2$s", elapsedTimeMs, message);
    }

    /**
     * Find or create the console with the given name
     * @param consoleName
     */
    protected static MessageConsole findConsole() {
        String consoleName = CEYLON_CONSOLE;
        MessageConsole myConsole= null;
        final IConsoleManager consoleManager= ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] consoles= consoleManager.getConsoles();
        for(int i= 0; i < consoles.length; i++) {
            IConsole console= consoles[i];
            if (console.getName().equals(consoleName))
                myConsole= (MessageConsole) console;
        }
        if (myConsole == null) {
            myConsole= new MessageConsole(consoleName, null);
            consoleManager.addConsoles(new IConsole[] { myConsole });
        }
//      consoleManager.showConsoleView(myConsole);
        return myConsole;
    }

    private static void addTaskMarkers(IFile file, List<CommonToken> tokens) {
        //clearTaskMarkersOnFile(file);
        for (CommonToken token: tokens) {
            if (token.getType()==CeylonLexer.LINE_COMMENT) {
                int priority = priority(token);
                if (priority>=0) {
                    Map<String, Object> attributes = new HashMap<String, Object>();
                    attributes.put(IMessageHandler.SEVERITY_KEY, IMarker.SEVERITY_INFO);
                    attributes.put(IMarker.PRIORITY, priority);
                    attributes.put(IMarker.USER_EDITABLE, false);
                    new MarkerCreator(file, IMarker.TASK)
                        .handleSimpleMessage(token.getText().substring(2), 
                            token.getStartIndex(), token.getStopIndex(), 
                            token.getCharPositionInLine(), token.getCharPositionInLine(), 
                            token.getLine(), token.getLine(), attributes);
                }
            }
        }
    }

    private static void clearTaskMarkersOn(IResource resource) {
        try {
            resource.deleteMarkers(IMarker.TASK, false, IResource.DEPTH_INFINITE);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
        super.clean(monitor);
        
        IJavaProject javaProject = JavaCore.create(getProject());
        final File modulesOutputDirectory = getModulesOutputDirectory(javaProject);
        if (modulesOutputDirectory != null) {
            monitor.subTask("Cleaning existing artifacts");
            List<String> extensionsToDelete = Arrays.asList(".jar", ".car", ".src", ".sha1");
            new RepositoryLister(extensionsToDelete).list(modulesOutputDirectory, new RepositoryLister.Actions() {
                @Override
                public void doWithFile(File path) {
                    path.delete();
                }
                
                public void exitDirectory(File path) {
                    if (path.list().length == 0 && ! path.equals(modulesOutputDirectory)) {
                        path.delete();
                    }
                }
            });
        }

        final File ceylonOutputDirectory = getCeylonOutputDirectory(javaProject);
        if (ceylonOutputDirectory != null) {
            new RepositoryLister(Arrays.asList(".*")).list(ceylonOutputDirectory, new RepositoryLister.Actions() {
                @Override
                public void doWithFile(File path) {
                    path.delete();
                }
                
                public void exitDirectory(File path) {
                    if (path.list().length == 0 && ! path.equals(ceylonOutputDirectory)) {
                        path.delete();
                    }
                }
            });
        }
        
        monitor.subTask("Clear project and source markers");
        clearProjectMarkers(getProject());
        clearMarkersOn(getProject());
    }
    
    public static IFile getFile(PhasedUnit phasedUnit) {
        return (IFile) ((ResourceVirtualFile) phasedUnit.getUnitFile()).getResource();
    }

    // TODO penser à : doRefresh(file.getParent()); // N.B.: Assumes all
    // generated files go into parent folder

    public static int priority(Token token) {
        String comment = token.getText().toLowerCase();
        if (comment.startsWith("//todo")) {
            return IMarker.PRIORITY_NORMAL;
        }
        else if (comment.startsWith("//fix")) {
            return IMarker.PRIORITY_HIGH;
        }
        else {
            return -1;
        }
    }

    private static List<IFile> getProjectSources(IProject project) {
        return projectSources.get(project);
    }

    public static TypeChecker getProjectTypeChecker(IProject project) {
        return typeCheckers.get(project);
    }

    public static Modules getProjectModules(IProject project) {
        TypeChecker typeChecker = getProjectTypeChecker(project);
        if (typeChecker == null) {
            return null;
        }
        return typeChecker.getContext().getModules();
    }

    public static RepositoryManager getProjectRepositoryManager(IProject project) {
        TypeChecker typeChecker = getProjectTypeChecker(project);
        if (typeChecker == null) {
            return null;
        }
        return typeChecker.getContext().getRepositoryManager();
    }
    
    public static Iterable<IProject> getProjects() {
        return typeCheckers.keySet();
    }

    public static void removeProject(IProject project) {
        typeCheckers.remove(project);
        sourceFolders.remove(project);
        projectSources.remove(project);
        modelStates.remove(project);
    }
    

    /**
     * Read the IJavaProject classpath configuration and populate the ISourceProject's
     * build path accordingly.
     */
    public static List<IPath> getSourceFolders(IProject theProject) {
        if (sourceFolders.containsKey(theProject)) {
            return sourceFolders.get(theProject);
        }
        
        IJavaProject javaProject = JavaCore.create(theProject);
        if (javaProject.exists()) {
            try {
                List<IPath> projectSourceFolders = new ArrayList<IPath>();
                for (IClasspathEntry entry: javaProject.getResolvedClasspath(true)) {
                    IPath path = entry.getPath();
                    if (isCeylonSourceEntry(entry)) {
                        projectSourceFolders.add(path);
                    }
                }
                sourceFolders.put(theProject, projectSourceFolders);
                return projectSourceFolders;
            } 
            catch (JavaModelException e) {
                ErrorHandler.reportError(e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    public static boolean isCeylonSourceEntry(IClasspathEntry entry) {
        if (entry.getEntryKind()!=IClasspathEntry.CPE_SOURCE) {
            return false;
        }
        
        for (IClasspathAttribute attribute : entry.getExtraAttributes()) {
            if (attribute.getName().equals("CEYLON") && "true".equalsIgnoreCase(attribute.getValue())) {
                return true;
            }
        }

        for (IPath exclusionPattern : entry.getExclusionPatterns()) {
            if (exclusionPattern.toString().endsWith(".ceylon")) {
                return false;
            }
        }

        return true;
    }

    private IPath retrieveSourceFolder(IFile file)
    {
        IPath path = file.getProjectRelativePath();
        if (path == null)
            return null;

        if (! isCeylonOrJava(file))
            return null;

        return retrieveSourceFolder(path);
    }

    // path is project-relative
    private IPath retrieveSourceFolder(IPath path) {
        IProject project = getProject();
        if (project != null) {
            Collection<IPath> sourceFolders = getSourceFolders(project);
            for (IPath sourceFolderAbsolute : sourceFolders) {
                IPath sourceFolder = sourceFolderAbsolute.makeRelativeTo(project.getFullPath());
                if (sourceFolder.isPrefixOf(path)) {
                    return sourceFolder;
                }
            }
        }
        return null;
    }
    
    private Package retrievePackage(IResource folder) {
        String packageName = getPackageName(folder);
        if (packageName == null) {
            return null;
        }
        TypeChecker typeChecker = typeCheckers.get(folder.getProject());
        Context context = typeChecker.getContext();
        Modules modules = context.getModules();
        for (Module module : modules.getListOfModules()) {
            for (Package p : module.getPackages()) {
                if (p.getQualifiedNameString().equals(packageName)) {
                    return p;
                }
            }
        }
        return null;
    }

    public String getPackageName(IResource resource) {
        IContainer folder = null;
        if (resource instanceof IFile) {
            folder = resource.getParent();
        }
        else {
            folder = (IContainer) resource;
        }
        String packageName = null;
        IPath folderPath = folder.getProjectRelativePath();
        IPath sourceFolder = retrieveSourceFolder(folderPath);
        if (sourceFolder != null) {
            IPath relativeFolderPath = folderPath.makeRelativeTo(sourceFolder);
            packageName = relativeFolderPath.toString().replace('/', '.');
        }
        return packageName;
    }
    
    private Package createNewPackage(IContainer folder) {
        IPath folderPath = folder.getProjectRelativePath();
        IPath sourceFolder = retrieveSourceFolder(folderPath);
        if (sourceFolder == null) {
            return null;
        }
        
        IContainer parent = folder.getParent();
        IPath packageRelativePath = folder.getProjectRelativePath().makeRelativeTo(parent.getProjectRelativePath());
        Package parentPackage = null;
        while (parentPackage == null && ! parent.equals(folder.getProject())) {
            packageRelativePath = folder.getProjectRelativePath().makeRelativeTo(parent.getProjectRelativePath());
            parentPackage = retrievePackage(parent);
            parent = parent.getParent();
        }
        
        Context context = typeCheckers.get(folder.getProject()).getContext();
        return createPackage(parentPackage, packageRelativePath, context.getModules());
    }
    
    private Package createPackage(Package parentPackage, IPath packageRelativePath, Modules modules) {
        String[] packageSegments = packageRelativePath.segments();
        if (packageSegments.length == 1) {
            Package pkg = new LazyPackage(CeylonBuilder.getProjectModelLoader(getProject()));
            List<String> parentName = null;
            if (parentPackage == null) {
                parentName = Collections.emptyList();
            }
            else {
                parentName = parentPackage.getName();
            }
            final ArrayList<String> name = new ArrayList<String>(parentName.size() + 1);
            name.addAll(parentName);
            name.add(packageRelativePath.segment(0));
            pkg.setName(name);
            Module module = null;
            if (parentPackage != null) {
                module = parentPackage.getModule();
            }
            
            if (module == null) {
                module = modules.getDefaultModule();
            }
            
            module.getPackages().add(pkg);
            pkg.setModule(module);
            return pkg;
        }
        else {
            Package childPackage = createPackage(parentPackage, packageRelativePath.uptoSegment(1), modules);
            return createPackage(childPackage, packageRelativePath.removeFirstSegments(1), modules);
        }
    }
    

    private void removeObsoleteClassFiles(List<IFile> filesToRemove) {
        if (filesToRemove.size() == 0) {
            return;
        }
        
        Set<File> moduleJars = new HashSet<File>();
        
        for (IFile file : filesToRemove) {
            IPath filePath = file.getProjectRelativePath();
            IPath sourceFolder = retrieveSourceFolder(filePath);
            if (sourceFolder == null) {
                return;
            }
            
            Package pkg = retrievePackage(file.getParent());
            if (pkg == null) {
                return;
            }
            IProject project = file.getProject();
            Module module = pkg.getModule();
            TypeChecker typeChecker = typeCheckers.get(project);
            if (typeChecker == null) {
                return;
            }
            
            IJavaProject javaProject = JavaCore.create(project);
            final File modulesOutputDirectory = getModulesOutputDirectory(javaProject);
            final File ceylonOutputDirectory = getCeylonOutputDirectory(javaProject);

            File moduleDir = Util.getModulePath(modulesOutputDirectory, module);
            File moduleJar = new File(moduleDir, Util.getModuleArchiveName(module));
            if(moduleJar.exists()){
                moduleJars.add(moduleJar);
                String relativeFilePath = filePath.makeRelativeTo(sourceFolder).toString();
                
                try {
                    ZipFile zipFile = new ZipFile(moduleJar);
                    FileHeader fileHeader = zipFile.getFileHeader("META-INF/mapping.txt");
                    List<String> entriesToDelete = new ArrayList<String>();
                    ZipInputStream zis = zipFile.getInputStream(fileHeader);
                    try {
                        Properties mapping = new Properties();
                        mapping.load(zis);
                        for (String className : mapping.stringPropertyNames()) {
                            String sourceFile = mapping.getProperty(className);
                            if (relativeFilePath.equals(sourceFile)) {
                                entriesToDelete.add(className);
                            }
                        }
                    } finally {
                        zis.close();
                    }
                    for (String entryToDelete : entriesToDelete) {
                        zipFile.removeFile(entryToDelete);
                        File classFile = new File(ceylonOutputDirectory, entryToDelete.replace('/', File.separatorChar));
                        classFile.delete();
                    }
                } catch (ZipException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        final com.sun.tools.javac.util.Context dummyContext = new com.sun.tools.javac.util.Context();
        class MyLog extends Log {
            public MyLog() {
                super(dummyContext, new PrintWriter(getConsoleStream()));
            }
        }
        Log log = new MyLog();
        Options options = Options.instance(dummyContext);
        for (File moduleJar : moduleJars) {
            ShaSigner.sign(moduleJar, log, options);
        }
    }

    private static IPath getProjectRelativeOutputDir(IJavaProject javaProject) {
        if (!javaProject.exists()) return null;
        try {
            return javaProject.getOutputLocation()
                    .makeRelativeTo(javaProject.getProject().getFullPath());
        } 
        catch (JavaModelException e) {
            return null;
        }
    }

    public static File getJavaOutputDirectory(IJavaProject javaProject) {
        return toFile(javaProject.getProject(), getProjectRelativeOutputDir(javaProject));
    }

    public static File getCeylonOutputDirectory(IJavaProject javaProject) {
        File ceylonOutputDir = javaProject.getProject().getFolder("JDTClasses").getRawLocation().toFile();
        if (! ceylonOutputDir.exists()) {
            ceylonOutputDir.mkdirs();
        }
        return ceylonOutputDir;
    }

    public static File getModulesOutputDirectory(IJavaProject javaProject) {
        File modulesOutputDir = javaProject.getProject().getFolder("modules").getRawLocation().toFile();
        if (! modulesOutputDir.exists()) {
            modulesOutputDir.mkdirs();
        }
        return modulesOutputDir;
    }
    
    /**
     * String representation for debugging purposes
     */
    public String toString() {
        return this.getProject() == null
            ? "CeylonBuilder for unknown project" //$NON-NLS-1$
            : "CeylonBuilder for " + getProject().getName(); //$NON-NLS-1$
    }
}
