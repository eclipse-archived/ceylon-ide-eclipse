package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.compiler.java.util.Util.getModuleArchiveName;
import static com.redhat.ceylon.compiler.java.util.Util.getModulePath;
import static com.redhat.ceylon.compiler.java.util.Util.getSourceArchiveName;
import static com.redhat.ceylon.compiler.java.util.Util.makeRepositoryManager;
import static com.redhat.ceylon.compiler.java.util.Util.quoteIfJavaKeyword;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getPhasedUnit;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathContainers;
import static com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile.createResourceVirtualFile;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.LANGUAGE_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
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

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
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
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.builder.MarkerCreator;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

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
import com.redhat.ceylon.compiler.java.tools.JarEntryFileObject;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler.PhasedUnitsManager;
import com.redhat.ceylon.compiler.java.util.RepositoryLister;
import com.redhat.ceylon.compiler.java.util.ShaSigner;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.ModelLoaderFactory;
import com.redhat.ceylon.compiler.loader.SourceDeclarationVisitor;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.model.LazyPackage;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleValidator;
import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
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
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.core.model.CeylonSourceFile;
import com.redhat.ceylon.eclipse.core.model.loader.JDTClass;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader.SourceFileObjectManager;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModuleManager;
import com.redhat.ceylon.eclipse.core.model.loader.SourceClass;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;
import com.sun.tools.javac.file.RelativePath.RelativeFile;
import com.sun.tools.javac.file.ZipFileIndexCache;
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

    public static final String CEYLON_CLASSES_FOLDER_NAME = ".exploded";

	private static boolean compileWithJDTModelLoader = false;
    
    /**
     * Extension ID of the Ceylon builder, which matches the ID in the
     * corresponding extension definition in plugin.xml.
     */
    public static final String BUILDER_ID = PLUGIN_ID + ".ceylonBuilder";

    /**
     * A marker ID that identifies problems detected by the builder
     */
    public static final String PROBLEM_MARKER_ID = PLUGIN_ID + ".ceylonProblem";

    /*public static final String TASK_MARKER_ID = PLUGIN_ID + ".ceylonTask";*/

    public static final Language LANGUAGE = LanguageRegistry.findLanguage(LANGUAGE_ID);

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
    private final static Map<IProject, List<IFile>> projectSources = new HashMap<IProject, List<IFile>>();

    public static final String CEYLON_CONSOLE= "Ceylon";
    private long startTime;

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

    final static class BooleanHolder {
        public boolean value;
    }
	
	@Override
    protected IProject[] build(final int kind, Map args, final IProgressMonitor monitor) 
    		throws CoreException {
		
        final IProject project = getProject();
        IJavaProject javaProject = JavaCore.create(project);
        final Collection<IFile> sourceToCompile= new HashSet<IFile>();

        ISourceProject sourceProject = getSourceProject();
        if (sourceProject == null) {
            return new IProject[0];
        }
        
        IMarker[] buildMarkers = project.findMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, DEPTH_ZERO);
        for (IMarker m: buildMarkers) {
        	Object message = m.getAttribute("message");
			if (message!=null && message.toString().endsWith("'JDTClasses'")) {
				//ignore message from JDT about missing JDTClasses dir
        		m.delete();
        	}
        	else if (message!=null && message.toString().contains("is missing required Java project:")) {
            	return project.getReferencedProjects();
        	}
        }
        
        List<CeylonClasspathContainer> cpContainers = getCeylonClasspathContainers(javaProject);
        
        if (cpContainers.isEmpty()) {
            //if the ClassPathContainer is missing, add an error
            IMarker marker = project.createMarker(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER);
            marker.setAttribute(IMarker.MESSAGE, "the Ceylon classpath container is not set on the project " + 
                    project.getName() + " (try running Enable Ceylon Builder on the project)");
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.LOCATION, "Bytecode generation");
            return project.getReferencedProjects();
        }
        
        List<PhasedUnit> builtPhasedUnits = Collections.emptyList();
        
        final BooleanHolder mustDoFullBuild = new BooleanHolder();
        final BooleanHolder mustResolveClasspathContainer = new BooleanHolder();
        final IResourceDelta currentDelta = getDelta(getProject());
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
                for (CeylonClasspathContainer container: cpContainers) {
                	container.resolveClasspath(monitor, true);
                }
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
            getConsoleStream().println(timedMessage("Starting Ceylon build on project: " + project.getName()));
            getConsoleStream().println("-----------------------------------");
            
            boolean binariesGenerationOK;
            final TypeChecker typeChecker;
            if (mustDoFullBuild.value) {
                monitor.beginTask("Full Ceylon build of project " + project.getName(), 11);
                getConsoleStream().println(timedMessage("Full build of model"));
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                
                cleanupModules(monitor, project);
                cleanupJdtClasses(monitor, project);
                
                monitor.subTask("Clearing existing markers of project " + project.getName());
                clearProjectMarkers(project);
                clearMarkersOn(project);
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                //if (! getModelState(project).equals(ModelState.Parsed)) {
                if (!mustResolveClasspathContainer.value) {
                    monitor.subTask("Parsing source of project " + project.getName());
                	//if we already resolved the classpath, the
                	//model has already been freshly-parsed
                    typeChecker = parseCeylonModel(project, monitor);
                    monitor.worked(1);
                }
                else {
                    typeChecker = getProjectTypeChecker(project);
                }
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Typechecking source of project " + project.getName());
                modelStates.put(project, ModelState.TypeChecking);
                builtPhasedUnits = fullTypeCheck(project, sourceProject, typeChecker, monitor);
                modelStates.put(project, ModelState.TypeChecked);
                monitor.worked(1);
                
                //we do this before the binary generation, in order to 
                //display the errors quicker, but if the backend starts
                //adding its own errors, we should do it afterwards
                monitor.subTask("Collecting problems for project " 
                        + project.getName());
                addProblemAndTaskMarkers(typeChecker.getPhasedUnits().getPhasedUnits(), project);
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Generating binaries for project " + project.getName());
                getConsoleStream().println(timedMessage("Full generation of class files..."));
                final List<IFile> allSources = getProjectSources(project);
                getConsoleStream().println("             ...compiling " + 
                        allSources.size() + " source files...");
                binariesGenerationOK = generateBinaries(project, javaProject, 
                		sourceProject, allSources, typeChecker, monitor);
                getConsoleStream().println(successMessage(binariesGenerationOK));
                monitor.worked(1);
                
            }
            else
            {
                monitor.beginTask("Incremental Ceylon build of project " + project.getName(), 7);
                getConsoleStream().println(timedMessage("Incremental build of model"));
                
                List<IFile> filesToRemove = new ArrayList<IFile>();
                Set<IFile> changedSources = new HashSet<IFile>(); 

                monitor.subTask("Scanning deltas of project " + project.getName()); 
                calculateChangedSources(currentDelta, projectDeltas, filesToRemove, 
                		changedSources, monitor);                
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                
                typeChecker = typeCheckers.get(project);
                PhasedUnits phasedUnits = typeChecker.getPhasedUnits();

                monitor.subTask("Scanning dependencies of deltas of project " + project.getName()); 
                calculateDependencies(project, sourceToCompile, currentDelta, 
                		changedSources, typeChecker, phasedUnits, monitor);

                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Cleaning removed files for project " + project.getName()); 
                cleanRemovedSources(filesToRemove, phasedUnits, project);
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Compiling " + sourceToCompile + " source files in project " + 
                        project.getName()); 
                /*if (emitDiags) {
                    getConsoleStream().println("All files to compile:");
                    dumpSourceList(sourceToCompile);
                }*/
                builtPhasedUnits = incrementalBuild(project, sourceToCompile, 
                		sourceProject, monitor);                
                if (builtPhasedUnits.isEmpty() && sourceToCompile.isEmpty()) {
                    return project.getReferencedProjects();
                }
                monitor.worked(1);
                
                //we do this before the binary generation, in order to 
                //display the errors quicker, but if the backend starts
                //adding its own errors, we should do it afterwards
                monitor.subTask("Collecting problems for project " 
                        + project.getName());
                addProblemAndTaskMarkers(builtPhasedUnits, project);
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Generating binaries for project " + project.getName());
                getConsoleStream().println(timedMessage("Incremental generation of class files..."));
                getConsoleStream().println("             ...compiling " + 
                        sourceToCompile.size() + " source files...");
                binariesGenerationOK = generateBinaries(project, javaProject, sourceProject, 
                		sourceToCompile, typeChecker, monitor);
                getConsoleStream().println(successMessage(binariesGenerationOK));
                monitor.worked(1);

                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Updating referencing projects of project " + project.getName());
                getConsoleStream().println(timedMessage("Updating model in referencing projects"));
                updateExternalPhasedUnitsInReferencingProjects(project, builtPhasedUnits);
                monitor.worked(1);
            }
            
            if (!binariesGenerationOK) {
                // Add a problem marker if binary generation went wrong for ceylon files
                addBinaryGenerationProblemMarker(project);
            }
                        
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            monitor.subTask("Collecting dependencies of project " + project.getName());
            getConsoleStream().println(timedMessage("Collecting dependencies"));
            collectDependencies(project, typeChecker, builtPhasedUnits);
            monitor.worked(1);
    
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            if (getJdtClassesEnabled(project)) {
                monitor.subTask("Rebuilding using Ceylon class directory of " + project.getName());
            	sheduleIncrementalRebuild(args, project, monitor);
                monitor.worked(1);
            }
            
            monitor.done();
            return project.getReferencedProjects();
        }
        finally {
            getConsoleStream().println("-----------------------------------");
            getConsoleStream().println(timedMessage("End Ceylon build on project: " + project.getName()));
            getConsoleStream().println("===================================");
        }
    }

	private void sheduleIncrementalRebuild(Map args, final IProject project, 
			IProgressMonitor monitor) {
		try {
			getCeylonClassesOutputFolder(project).refreshLocal(DEPTH_INFINITE, monitor);
		} 
		catch (CoreException e) {
			e.printStackTrace();
		}//monitor);
		if (args==null || !args.containsKey(BUILDER_ID + ".reentrant")) {
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
		List<PhasedUnits> phasedUnitsForDependencies = new ArrayList<PhasedUnits>();
		for (IProject referencingProject: project.getReferencedProjects()) {
		    TypeChecker requiredProjectTypeChecker = getProjectTypeChecker(referencingProject);
		    if (requiredProjectTypeChecker!=null) {
		        phasedUnitsForDependencies.add(requiredProjectTypeChecker.getPhasedUnits());
		    }
		}
		for (PhasedUnit pu : builtPhasedUnits) {
		    new UnitDependencyVisitor(pu, typeChecker.getPhasedUnits(), phasedUnitsForDependencies)
		            .visit(pu.getCompilationUnit());
		}
	}

	private void addBinaryGenerationProblemMarker(final IProject project)
			throws CoreException {
		IMarker marker = project.createMarker(PROBLEM_MARKER_ID);
		marker.setAttribute(IMarker.MESSAGE, "Bytecode generation has failed on some Ceylon source files in project " + 
		        project.getName() + ". Look at the Ceylon console for more information.");
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.LOCATION, "Bytecode generation");
	}

	private void cleanRemovedSources(List<IFile> filesToRemove,
			PhasedUnits phasedUnits, IProject project) {
		removeObsoleteClassFiles(filesToRemove, project);
		for (IFile fileToRemove: filesToRemove) {
		    if(isCeylon(fileToRemove)) {
		        // Remove the ceylon phasedUnit (which will also remove the unit from the package)
		        PhasedUnit phasedUnitToDelete = phasedUnits.getPhasedUnit(createResourceVirtualFile(fileToRemove));
		        if (phasedUnitToDelete != null) {
		            phasedUnits.removePhasedUnitForRelativePath(phasedUnitToDelete.getPathRelativeToSrcDir());
		        }
		    }
		    else if (isJava(fileToRemove)) {
		        // Remove the external unit from the package
		        Package pkg = retrievePackage(fileToRemove.getParent());
		        if (pkg != null) {
		            for (Unit unitToTest: pkg.getUnits()) {
		                if (unitToTest.getFilename().equals(fileToRemove.getName())) {
			                pkg.removeUnit(unitToTest);
		                    break;
		                }
		            }
		        }
		    }
		}
	}

	private void calculateDependencies(IProject project,
			Collection<IFile> sourceToCompile, IResourceDelta currentDelta,
			Set<IFile> fChangedSources, TypeChecker typeChecker, 
			PhasedUnits phasedUnits, IProgressMonitor monitor) {
		if (!fChangedSources.isEmpty()) {
			
		    Collection<IFile> changeDependents= new HashSet<IFile>();
		    changeDependents.addAll(fChangedSources);
		    /*if (emitDiags) {
		        getConsoleStream().println("Changed files:");
		        dumpSourceList(changeDependents);
		    }*/
   
		    boolean changed = false;
		    do {
		        Collection<IFile> additions= new HashSet<IFile>();
		        for (Iterator<IFile> iter=changeDependents.iterator(); iter.hasNext();) {
		            IFile srcFile= iter.next();
		            IProject currentFileProject = srcFile.getProject();
		            TypeChecker currentFileTypeChecker = null;
		            if (currentFileProject == project) {
		                currentFileTypeChecker = typeChecker;
		            } 
		            else {
		                currentFileTypeChecker = getProjectTypeChecker(currentFileProject);
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
		    } while (changed);
   
		    if (monitor.isCanceled()) {
		        throw new OperationCanceledException();
		    }
		    
		    for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
		        Unit unit = phasedUnit.getUnit();
		        if (!unit.getUnresolvedReferences().isEmpty()) {
		            IFile fileToAdd = ((IFileVirtualFile)(phasedUnit.getUnitFile())).getFile();
		            if (fileToAdd.exists()) {
		                sourceToCompile.add(fileToAdd);
		            }
		        }
		        Set<Declaration> duplicateDeclarations = unit.getDuplicateDeclarations();
		        if (!duplicateDeclarations.isEmpty()) {
		            IFile fileToAdd = ((IFileVirtualFile)(phasedUnit.getUnitFile())).getFile();
		            if (fileToAdd.exists()) {
		                sourceToCompile.add(fileToAdd);
		            }
		            for (Declaration duplicateDeclaration : duplicateDeclarations) {
		                PhasedUnit duplicateDeclPU = getPhasedUnit(project, duplicateDeclaration);
		                if (duplicateDeclPU != null) {
		                    IFile duplicateDeclFile = ((IFileVirtualFile)(duplicateDeclPU.getUnitFile())).getFile();
		                    if (duplicateDeclFile.exists()) {
		                        sourceToCompile.add(duplicateDeclFile);
		                    }
		                }
		            }
		        }
		    }
		    
		    if (monitor.isCanceled()) {
		        throw new OperationCanceledException();
		    }
		    
		    for (IFile f: changeDependents) {
		        if (isSourceFile(f) && f.getProject() == project) {
		            if (f.exists()) {
		                sourceToCompile.add(f);
		            }
		            else {
		                // If the file is moved : add a dependency on the new file
		                if (currentDelta != null) {
		                    IResourceDelta removedFile = currentDelta.findMember(f.getProjectRelativePath());
		                    if (removedFile != null && 
		                            (removedFile.getFlags() & IResourceDelta.MOVED_TO) != 0 &&
		                            removedFile.getMovedToPath() != null) {
		                        sourceToCompile.add(project.getFile(removedFile.getMovedToPath().removeFirstSegments(1)));
		                    }
		                }
		            }
		        }
		    }
		}
	}

	private void calculateChangedSources(final IResourceDelta currentDelta, 
			List<IResourceDelta> projectDeltas, final List<IFile> filesToRemove, 
			final Set<IFile> changedSources, IProgressMonitor monitor) 
					throws CoreException {
		for (final IResourceDelta projectDelta: projectDeltas) {
		    if (projectDelta != null) {
		        IProject p = (IProject) projectDelta.getResource();
				List<IPath> deltaSourceFolders = getSourceFolders(p);
		        for (IResourceDelta sourceDelta: projectDelta.getAffectedChildren()) {
		            for (IPath ip: deltaSourceFolders) {
		                if (sourceDelta.getResource().getFullPath().isPrefixOf(ip)) {
			                //a real Ceylon source folder so scan for changes
			            	/*if (emitDiags)
			            		getConsoleStream().println("==> Scanning resource delta for '" + 
			            				p.getName() + "'... <==");*/
			            	sourceDelta.accept(new IResourceDeltaVisitor() {
			            		public boolean visit(IResourceDelta delta) throws CoreException {
			            			IResource resource = delta.getResource();
			            			if (resource instanceof IFile) {
			            				IFile file= (IFile) resource;
			            				if (isCeylonOrJava(file)) {
			            					changedSources.add(file);
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
			            	/*if (emitDiags)
			            		getConsoleStream().println("Delta scan completed for project '" + 
			            				projectDelta.getResource().getName() + "'...");*/
		                    break;
		                }
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
        		!isModelAvailable(project);
        mustResolveClasspathContainer.value = kind==FULL_BUILD; //false;
        final BooleanHolder sourceModified = new BooleanHolder();
        
        if (!mustDoFullBuild.value) {
            for (IResourceDelta currentDelta: currentDeltas) {
                if (currentDelta != null) {
                    try {
                        currentDelta.accept(new DeltaScanner(mustDoFullBuild, project,
								sourceModified, mustResolveClasspathContainer));
                    } 
                    catch (CoreException e) {
                        getPlugin().getLog().log(new Status(IStatus.ERROR, 
                        		getPlugin().getID(), 
                        		e.getLocalizedMessage(), e));
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
        return mustDoFullBuild.value || 
                mustResolveClasspathContainer.value ||
                sourceModified.value;
    }

    private static String successMessage(boolean binariesGenerationOK) {
        return "             " + (binariesGenerationOK ? 
                "...binary generation succeeded" : "...binary generation FAILED");
    }

    private Set<String> getDependentsOf(IFile srcFile,
            TypeChecker currentFileTypeChecker,
            IProject currentFileProject) {
    	
        if (LANGUAGE.hasExtension(srcFile.getRawLocation().getFileExtension())) {
            PhasedUnit phasedUnit = currentFileTypeChecker.getPhasedUnits()
            		.getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(srcFile));
            if (phasedUnit != null && phasedUnit.getUnit() != null) {
                return phasedUnit.getUnit().getDependentsOf();
            }
        } 
        else {
            Unit unit = getJavaUnit(currentFileProject, srcFile);
            if (unit instanceof ExternalUnit) {
                return unit.getDependentsOf();
            }
        }
        
        return Collections.emptySet();
    }

    private void updateExternalPhasedUnitsInReferencingProjects(IProject project, 
    		List<PhasedUnit> builtPhasedUnits) {
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

    static PhasedUnit parseFileToPhasedUnit(ModuleManager moduleManager, TypeChecker typeChecker,
            ResourceVirtualFile file, ResourceVirtualFile srcDir,
            Package pkg) {
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

    private List<PhasedUnit> incrementalBuild(IProject project, Collection<IFile> sourceToCompile,
        ISourceProject sourceProject, IProgressMonitor monitor) {
        TypeChecker typeChecker = typeCheckers.get(project);
        PhasedUnits pus = typeChecker.getPhasedUnits();
		JDTModuleManager moduleManager = (JDTModuleManager) pus.getModuleManager(); 
        JDTModelLoader modelLoader = getModelLoader(typeChecker);
        Set<String> cleanedPackages = new HashSet<String>();
        
        List<PhasedUnit> phasedUnitsToUpdate = new ArrayList<PhasedUnit>();
        List<Set<String>> dependentsOfList = new ArrayList<Set<String>>();
        
        for (IFile fileToUpdate : sourceToCompile) {
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
                    continue;
                }
                
            }
            
            ResourceVirtualFile file = ResourceVirtualFile.createResourceVirtualFile(fileToUpdate);
            IPath srcFolderPath = retrieveSourceFolder(fileToUpdate, project);
            ResourceVirtualFile srcDir = new IFolderVirtualFile(project, srcFolderPath);

            PhasedUnit alreadyBuiltPhasedUnit = pus.getPhasedUnit(file);

            Package pkg = null;
            Set<String> dependentsOf = Collections.emptySet();
            if (alreadyBuiltPhasedUnit!=null) {
                // Editing an already built file
                pkg = alreadyBuiltPhasedUnit.getPackage();
                if (alreadyBuiltPhasedUnit.getUnit() != null) {
                    dependentsOf = alreadyBuiltPhasedUnit.getUnit().getDependentsOf();
                }
            }
            else {
                IContainer packageFolder = file.getResource().getParent();
                pkg = retrievePackage(packageFolder);
                if (pkg == null) {
                    pkg = createNewPackage(packageFolder);
                }
            }
            PhasedUnit newPhasedUnit = parseFileToPhasedUnit(moduleManager, typeChecker, file, srcDir, pkg);
            dependentsOfList.add(dependentsOf);
            phasedUnitsToUpdate.add(newPhasedUnit);
            
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        if (phasedUnitsToUpdate.size() == 0) {
            return phasedUnitsToUpdate;
        }
        
        clearProjectMarkers(project);
        clearMarkersOn(sourceToCompile);
        
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            if (pus.getPhasedUnitFromRelativePath(phasedUnit.getPathRelativeToSrcDir()) != null) {
                pus.removePhasedUnitForRelativePath(phasedUnit.getPathRelativeToSrcDir());
            }
            pus.addPhasedUnit(phasedUnit.getUnitFile(), phasedUnit);
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
                if (showWarnings(project)) {
                	phasedUnit.analyseUsage();
                }
            }
        }
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.analyseFlow();
        }

        Iterator<Set<String>> itr = dependentsOfList.iterator();
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.getUnit().getDependentsOf().addAll(itr.next());
            
        }
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }

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

    private List<PhasedUnit> fullTypeCheck(IProject project, ISourceProject sourceProject, 
    		TypeChecker typeChecker, IProgressMonitor monitor) 
    				throws CoreException {

        monitor.subTask("Typechecking source archives for project " 
                + project.getName());

        List<PhasedUnits> phasedUnitsOfDependencies = typeChecker.getPhasedUnitsOfDependencies();

        List<PhasedUnit> dependencies = new ArrayList<PhasedUnit>();
 
        for (PhasedUnits phasedUnits : phasedUnitsOfDependencies) {
            for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                dependencies.add(phasedUnit);
            }
        }

        JDTModelLoader loader = getModelLoader(typeChecker);
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
        
        if (compileWithJDTModelLoader()) {
            loader.completeFromClasses();
        }
        
        for (PhasedUnit pu : dependencies) {
            pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
        }

        loader.loadPackage("com.redhat.ceylon.compiler.java.metadata", true);
        loader.loadPackage("ceylon.language", true);
        loader.loadPackage("ceylon.language.descriptor", true);
        loader.loadPackageDescriptors();
        
        
        final List<PhasedUnit> listOfUnits = typeChecker.getPhasedUnits().getPhasedUnits();

        monitor.subTask("Typechecking source files for project " 
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
                if (showWarnings(project)) {
                	pu.analyseUsage();
                }
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
        
        return typeChecker.getPhasedUnits().getPhasedUnits();
    }

    private boolean compileWithJDTModelLoader() {
        return compileWithJDTModelLoader;
    }

    public static TypeChecker parseCeylonModel(IProject project,
            IProgressMonitor monitor) throws CoreException {

    	modelStates.put(project, ModelState.Parsing);
    	typeCheckers.remove(project);
    	projectSources.remove(project);
        
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
        typeCheckerBuilder.setRepositoryManager(makeRepositoryManager(repos, 
        		getCeylonModulesOutputDirectory(project).getAbsolutePath(), 
        		new EclipseLogger()));
        TypeChecker typeChecker = typeCheckerBuilder.getTypeChecker();
        PhasedUnits phasedUnits = typeChecker.getPhasedUnits();

        JDTModuleManager moduleManager = (JDTModuleManager) phasedUnits.getModuleManager();
        moduleManager.setTypeChecker(typeChecker);
        Context context = typeChecker.getContext();
        JDTModelLoader modelLoader = (JDTModelLoader) moduleManager.getModelLoader();
        Module defaultModule = context.getModules().getDefaultModule();

        monitor.worked(1);
        
        monitor.subTask("Parsing source files for project " 
                    + project.getName());

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        List<IFile> scannedSources = scanSources(project, javaProject, 
        		typeChecker, phasedUnits, moduleManager, modelLoader, 
        		defaultModule, monitor);

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

        monitor.subTask("Determining module dependencies for " 
                + project.getName());

        phasedUnits.getModuleManager().prepareForTypeChecking();
        phasedUnits.visitModules();

        //By now the language module version should be known (as local)
        //or we should use the default one.
        Module languageModule = context.getModules().getLanguageModule();
        if (languageModule.getVersion() == null) {
            languageModule.setVersion(TypeChecker.LANGUAGE_MODULE_VERSION);
        }

        monitor.worked(1);
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }

        final ModuleValidator moduleValidator = new ModuleValidator(context, phasedUnits) {
            @Override
            protected void executeExternalModulePhases() {}
        };
        
        moduleValidator.verifyModuleDependencyTree();
        typeChecker.setPhasedUnitsOfDependencies(moduleValidator.getPhasedUnitsOfDependencies());
        
        monitor.worked(1);

        typeCheckers.put(project, typeChecker);
        projectSources.put(project, scannedSources);
        modelStates.put(project, ModelState.Parsed);
        
        return typeChecker;

    }

	private static List<IFile> scanSources(IProject project, IJavaProject javaProject, 
			final TypeChecker typeChecker, final PhasedUnits phasedUnits, 
			final JDTModuleManager moduleManager, final JDTModelLoader modelLoader, 
			final Module defaultModule, IProgressMonitor monitor) throws CoreException {
		
		final List<IFile> scannedSources = new ArrayList<IFile>();
		final Collection<IPath> sourceFolders = getSourceFolders(javaProject);
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
            srcDirResource.accept(new SourceScanner(defaultModule, modelLoader, moduleManager,
					srcDir, srcFolderPath, typeChecker, scannedSources,
					phasedUnits));
        }
        return scannedSources;
	}

    private static void addProblemAndTaskMarkers(final List<PhasedUnit> units, 
    		final IProject project) {
    	new Job("updating markers for project: " + project) {
    		@Override
    		protected IStatus run(IProgressMonitor monitor) {
    	        for (PhasedUnit phasedUnit: units) {
    	            IFile file = getFile(phasedUnit);
    	            phasedUnit.getCompilationUnit()
    	                .visit(new ErrorVisitor(new MarkerCreator(file, PROBLEM_MARKER_ID)) {
    	                    @Override
    	                    public int getSeverity(Message error, boolean expected) {
    	                        return expected || error instanceof UsageWarning ? 
    	                        		IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR;
    	                    }
    	                    @Override
    	                    //workaround for bug in IMP's MarkerCreator
    	                    protected int adjust(int stopIndex) {
    	                        return stopIndex+1;
    	                    }
    	                    @Override
    	                    protected boolean include(Message msg) {
    	                    	return !isCompilerError(msg);
    	                    }
    	                });
    	            phasedUnit.getCompilationUnit()
    		            .visit(new ErrorVisitor(new MarkerCreator(file, IJavaModelMarker.BUILDPATH_PROBLEM_MARKER)) {
    		                @Override
    		                public int getSeverity(Message error, boolean expected) {
    		                    return IMarker.SEVERITY_ERROR;
    		                }
    		                @Override
    		                //workaround for bug in IMP's MarkerCreator
    		                protected int adjust(int stopIndex) {
    		                    return stopIndex+1;
    		                }
    		                @Override
    		                protected boolean include(Message msg) {
    		                	return isCompilerError(msg);
    		                }
    		            });
    	            addTaskMarkers(file, phasedUnit.getTokens());
    	        }
    			return Status.OK_STATUS;
    		}
    	}.schedule();
    }

	private static boolean isCompilerError(Message msg) {
        //TODO: we need a MUCH better way to distinguish 
        //      compiler errors from typechecker errors
		String ms = msg.getMessage();
		return ms.startsWith("cannot find module") || 
				ms.startsWith("unable to read source artifact for");
	}
	
    private boolean generateBinaries(IProject project, IJavaProject javaProject,
    		ISourceProject sourceProject, Collection<IFile> filesToCompile, 
    		TypeChecker typeChecker, IProgressMonitor monitor) throws CoreException {
        List<String> options = new ArrayList<String>();

        String srcPath = "";
        for (IPath sourceFolder : getSourceFolders(javaProject)) {
            File sourcePathElement = toFile(project,sourceFolder
            		.makeRelativeTo(project.getFullPath()));
            if (! srcPath.isEmpty()) {
                srcPath += File.pathSeparator;
            }
            srcPath += sourcePathElement.getAbsolutePath();
        }
        options.add("-src");
        options.add(srcPath);
        options.add("-encoding");
        options.add(project.getDefaultCharset());
        
        for (String repository : getUserRepositories(project)) {
            options.add("-rep");
            options.add(repository);
        }

        String verbose = System.getProperty("ceylon.verbose");
		if (verbose!=null && "true".equals(verbose)) {
            options.add("-verbose");
        }
        options.add("-g:lines,vars,source");

        final File modulesOutputDir = getCeylonModulesOutputDirectory(project);
        if (modulesOutputDir!=null) {
            options.add("-out");
            options.add(modulesOutputDir.getAbsolutePath());
        }

        List<File> javaSourceFiles = new ArrayList<File>();
        List<File> sourceFiles = new ArrayList<File>();
        List<File> moduleFiles = new ArrayList<File>();
        for (IFile file : filesToCompile) {
            if(isCeylon(file)) {
                sourceFiles.add(file.getRawLocation().toFile());
                if (file.getName().equals(ModuleManager.MODULE_FILE)) {
                    moduleFiles.add(file.getRawLocation().toFile());
                }
            }
            else if(isJava(file))
                javaSourceFiles.add(file.getRawLocation().toFile());
        }

        if (sourceFiles.size() > 0 || javaSourceFiles.size() > 0) {
            PrintWriter printWriter = new PrintWriter(getConsoleStream(), true);

            boolean success = true;
            if (compileWithJDTModelLoader()) {
                sourceFiles.addAll(javaSourceFiles);
            } 
            if(!sourceFiles.isEmpty()){
                success = compile(project, javaProject, options, sourceFiles, 
                		typeChecker, printWriter);
            }
            
            return success;
        }
        else
            return true;
    }

    private boolean compile(final IProject project, IJavaProject javaProject, 
    		List<String> options, java.util.List<File> sourceFiles, 
    		final TypeChecker typeChecker, PrintWriter printWriter) 
    				throws VerifyError {
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
        
        CeyloncFileManager fileManager = new CeyloncFileManager(context, true, null) {
            final boolean enabedJdtClassesDir = getJdtClassesEnabled(project);        
            final File ceylonOutputDirectory = enabedJdtClassesDir ? 
            		getCeylonClassesOutputDirectory(project) : null;
            @Override
            protected JavaFileObject getFileForOutput(Location location,
                    final RelativeFile fileName, FileObject sibling)
                    throws IOException {
                final JavaFileObject javaFileObject = super.getFileForOutput(location, fileName, sibling);
                if (javaFileObject instanceof JarEntryFileObject) {
                    try {
                        final File classFile;
                        if (enabedJdtClassesDir) {
                        	classFile = fileName.getFile(ceylonOutputDirectory);
                        	classFile.getParentFile().mkdirs();
                        }
                        else {
                        	classFile = null;
                        }
                        return new JavaFileObject() {
                        	@Override
                        	public String toString() {
                        		return fileName.getPath();
                        	}
                            @Override
                            public boolean delete() {
                                return javaFileObject.delete();
                            }
                            @Override
                            public CharSequence getCharContent(boolean b)
                                    throws IOException {
                                return javaFileObject.getCharContent(b);
                            }
                            @Override
                            public long getLastModified() {
                                return javaFileObject.getLastModified();
                            }
                            @Override
                            public String getName() {
                                return javaFileObject.getName();
                            }
                            @Override
                            public InputStream openInputStream() throws IOException {
                                return javaFileObject.openInputStream();
                            }
                            @Override
                            public OutputStream openOutputStream()
                                    throws IOException {
                                final OutputStream jarStream = javaFileObject.openOutputStream();
                                if (!enabedJdtClassesDir) {
                                	return jarStream;
                                }
                                return new OutputStream() {
                                	final OutputStream classFileStream = new BufferedOutputStream(new FileOutputStream(classFile));
                                	@Override
                                	public void write(int b) throws IOException {
                                		jarStream.write(b);
                                		classFileStream.write(b);
                                	}
                                	@Override
                                	public void write(byte[] b, int off, int len)
                                			throws IOException {
                                		jarStream.write(b, off, len);
                                		classFileStream.write(b, off, len);
                                	}
                                	@Override
                                	public void write(byte[] b) throws IOException {
                                		jarStream.write(b);
                                		classFileStream.write(b);
                                	}
                                	@Override
                                	public void close() throws IOException {
                                		classFileStream.close();
                                		jarStream.close();
                                	}
                                	@Override
                                	public void flush() throws IOException {
                                		classFileStream.flush();
                                		jarStream.flush();
                                	}
                                };
                            }
                            @Override
                            public Reader openReader(boolean b)
                                    throws IOException {
                                return javaFileObject.openReader(b);
                            }
                            @Override
                            public Writer openWriter() throws IOException {
                                return javaFileObject.openWriter();
                            }
                            @Override
                            public URI toUri() {
                                return javaFileObject.toUri();
                            }
                            @Override
                            public Modifier getAccessLevel() {
                                return javaFileObject.getAccessLevel();
                            }
                            @Override
                            public Kind getKind() {
                                return javaFileObject.getKind();
                            }
                            @Override
                            public NestingKind getNestingKind() {
                                return javaFileObject.getNestingKind();
                            }
                            @Override
                            public boolean isNameCompatible(String simpleName,
                                    Kind kind) {
                                return javaFileObject.isNameCompatible(simpleName, kind);
                            }
                        };
                    } catch(Exception e) {
                        CeylonPlugin.log(e);
                    }
                }
                return javaFileObject;
            }
        };
        
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
        
        IPath workspaceLocation = project.getWorkspace().getRoot().getLocation();
        addProjectClasspathElements(classpathElements, workspaceLocation,
				javaProject);
        try {
			for (IProject p: project.getReferencedProjects()) {
				addProjectClasspathElements(classpathElements, workspaceLocation,
						JavaCore.create(p));
			}
		} 
        catch (CoreException ce) {
			ce.printStackTrace();
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
        
        final JDTModelLoader modelLoader = getModelLoader(typeChecker);
        
        Iterable<? extends JavaFileObject> compilationUnits1 =
                fileManager.getJavaFileObjectsFromFiles(sourceFiles);
        if (compileWithJDTModelLoader()) {
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
                    
                    ClassMirror objectMirror = modelLoader.lookupClassMirror("ceylon.language.Object");
                    if (objectMirror instanceof SourceClass) {
                        Declaration objectClass = ((SourceClass) objectMirror).getModelDeclaration();
                        if (objectClass != null) {
                            Declaration hashMethod = objectClass.getDirectMember("hash", Collections.<ProducedType>emptyList());
                            if (hashMethod instanceof TypedDeclaration) {
                                ((TypedDeclaration)hashMethod).getType().setUnderlyingType("int");
                            }
                        }
                        
                    }
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
                                String name = quoteIfJavaKeyword(decl.getIdentifier().getText());
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
        ZipFileIndexCache.getSharedInstance().clearCache();
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

	private void addProjectClasspathElements(List<String> classpathElements,
			IPath workspaceLocation, IJavaProject javaProj) {
		try {
			List<CeylonClasspathContainer> containers = getCeylonClasspathContainers(javaProj);
			for (CeylonClasspathContainer container : containers) {
				for (IClasspathEntry cpEntry : container.getClasspathEntries()) {
					if (!isInCeylonClassesOutputFolder(cpEntry.getPath())) {
						classpathElements.add(cpEntry.getPath().toOSString());
					}
				}
			}

			classpathElements.add(workspaceLocation.append(javaProj.getOutputLocation()).toOSString());
			for (IClasspathEntry cpEntry : javaProj.getResolvedClasspath(true)) {
				if (isInCeylonClassesOutputFolder(cpEntry.getPath())) {
					classpathElements.add(workspaceLocation.append(cpEntry.getPath()).toOSString());
				}
			}
		} catch (JavaModelException e1) {
			CeylonPlugin.log(e1);
		}
	}

	public static boolean getJdtClassesEnabled(final IProject project) {
        return getBuilderArgs(project).get("enableJdtClasses")!=null;
	}

	public static boolean showWarnings(IProject project) {
		return getBuilderArgs(project).get("hideWarnings")==null;
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
        List<String> userRepos = new ArrayList<String>();
        userRepos.add(getCeylonRepository(project).getAbsolutePath());
        
        if (project != null) {
            for (IProject requiredProject: project.getReferencedProjects()) {
                if (requiredProject.isOpen() &&
                		requiredProject.hasNature(CeylonNature.NATURE_ID)) {
                	userRepos.add(getCeylonModulesOutputDirectory(requiredProject)
                			.getAbsolutePath());	
                }
            }
            
            /*userRepos.add(project.getLocation().append("modules").toOSString());
                
            for (IProject requiredProject : requiredProjects) {
                userRepos.add(requiredProject.getLocation().append("modules").toOSString());
            }*/
        }
        
        return userRepos;
    }
    
    public static String getRepositoryPath(IProject project) {
    	return (String) getBuilderArgs(project).get("repositoryPath");
    }

	private static Map getBuilderArgs(IProject project) {
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
    	return Collections.EMPTY_MAP;
	}

    public static File getCeylonRepository(IProject project) {
		String repoPath = getRepositoryPath(project);
        if (repoPath==null) {
            return CeylonPlugin.getInstance().getCeylonRepository();
        }
        else { 
            return CeylonPlugin.getCeylonRepository(repoPath);
        }
    }

    private static File toFile(IProject project, IPath path) {
		return project.getFolder(path).getRawLocation().toFile();
	}
    
    private static void clearMarkersOn(IResource resource) {
        try {
            clearTaskMarkersOn(resource);
            resource.deleteMarkers(PROBLEM_MARKER_ID, true, DEPTH_INFINITE);
            //these are actually errors from the Ceylon compiler, but
            //we did not bother creating a separate annotation type!
            resource.deleteMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, DEPTH_INFINITE);
        } catch (CoreException e) {
        }
    }

    private static void clearProjectMarkers(IProject project) {
        try {
            //project.deleteMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, DEPTH_ZERO);
            project.deleteMarkers(PROBLEM_MARKER_ID, true, DEPTH_ZERO);
        } catch (CoreException e) {
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
            resource.deleteMarkers(IMarker.TASK, false, DEPTH_INFINITE);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
        super.clean(monitor);
        
        IProject project = getProject();
        
        startTime = System.nanoTime();
        getConsoleStream().println("\n===================================");
        getConsoleStream().println(timedMessage("Starting Ceylon clean on project: " + project.getName()));
        getConsoleStream().println("-----------------------------------");
        
        cleanupModules(monitor, project);
        cleanupJdtClasses(monitor, project);
        
        monitor.subTask("Clearing project and source markers for project " + project.getName());
        clearProjectMarkers(project);
        clearMarkersOn(project);

        getConsoleStream().println("-----------------------------------");
        getConsoleStream().println(timedMessage("End Ceylon clean on project: " + project.getName()));
        getConsoleStream().println("===================================");
    }

	private void cleanupJdtClasses(IProgressMonitor monitor, IProject project) {
		if (getJdtClassesEnabled(project)) {
            monitor.subTask("Cleaning JDTClasses directory of project " + project.getName());
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
            List<String> extensionsToDelete = Arrays.asList(".jar", ".car", ".src", ".sha1");
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
        projectSources.remove(project);
        modelStates.remove(project);
    }
    
    public static List<IPath> getSourceFolders(IProject project) {
    	//TODO: is the call to JavaCore.create() very expensive??
        return getSourceFolders(JavaCore.create(project));
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
                ErrorHandler.reportError(e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    public static boolean isCeylonSourceEntry(IClasspathEntry entry) {
        if (entry.getEntryKind()!=IClasspathEntry.CPE_SOURCE) {
            return false;
        }
        
        /*for (IClasspathAttribute attribute: entry.getExtraAttributes()) {
            if (attribute.getName().equals("ceylonSource")) {
                return true;
            }
        }*/

        for (IPath exclusionPattern : entry.getExclusionPatterns()) {
            if (exclusionPattern.toString().endsWith(".ceylon")) {
                return false;
            }
        }

        return true;
    }

    private IPath retrieveSourceFolder(IFile file, IProject project) {
        IPath path = file.getProjectRelativePath();
        if (path == null)
            return null;

        if (! isCeylonOrJava(file))
            return null;

        return retrieveSourceFolder(path, project);
    }

    // path is project-relative
    private static IPath retrieveSourceFolder(IPath path, IProject project) {
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
    
    static Package retrievePackage(IResource folder) {
    	IProject project = folder.getProject();
    	if (project.isOpen()) {
    		String packageName = getPackageName(folder);
    		if (packageName != null) {
        		TypeChecker typeChecker = typeCheckers.get(project);
        		Context context = typeChecker.getContext();
        		Modules modules = context.getModules();
        		for (Module module : modules.getListOfModules()) {
        			for (Package p : module.getPackages()) {
        				if (p.getQualifiedNameString().equals(packageName)) {
        					return p;
        				}
        			}
        		}
        	}
        }
        return null;
    }

    public static String getPackageName(IResource resource) {
    	IProject project = resource.getProject();
        IContainer folder = null;
        if (resource instanceof IFile) {
            folder = resource.getParent();
        }
        else {
            folder = (IContainer) resource;
        }
        String packageName = null;
        IPath folderPath = folder.getProjectRelativePath();
        IPath sourceFolder = retrieveSourceFolder(folderPath, project);
        if (sourceFolder != null) {
            IPath relativeFolderPath = folderPath.makeRelativeTo(sourceFolder);
            packageName = relativeFolderPath.toString().replace('/', '.');
        }
        return packageName;
    }
    
    private Package createNewPackage(IContainer folder) {
        IPath folderPath = folder.getProjectRelativePath();
        IPath sourceFolder = retrieveSourceFolder(folderPath, getProject());
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
            Package pkg = new LazyPackage(getProjectModelLoader(getProject()));
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
            Package childPackage = createPackage(parentPackage, 
            		packageRelativePath.uptoSegment(1), modules);
            return createPackage(childPackage, 
            		packageRelativePath.removeFirstSegments(1), 
            		modules);
        }
    }
    

    private void removeObsoleteClassFiles(List<IFile> filesToRemove, 
    		IProject project) {
        if (filesToRemove.size() == 0) {
            return;
        }
        
        Set<File> moduleJars = new HashSet<File>();
        
        for (IFile file : filesToRemove) {
            IPath filePath = file.getProjectRelativePath();
            IPath sourceFolder = retrieveSourceFolder(filePath, project);
            if (sourceFolder == null) {
                return;
            }
            
            Package pkg = retrievePackage(file.getParent());
            if (pkg == null) {
                return;
            }
            Module module = pkg.getModule();
            TypeChecker typeChecker = typeCheckers.get(project);
            if (typeChecker == null) {
                return;
            }
            
            final File modulesOutputDirectory = getCeylonModulesOutputDirectory(project);
            boolean jdtClassesEnabled = getJdtClassesEnabled(project);
			final File ceylonOutputDirectory = jdtClassesEnabled ? 
            		getCeylonClassesOutputDirectory(project) : null;
            File moduleDir = getModulePath(modulesOutputDirectory, module);
            
            //Remove the classes belonging to the source file from the
            //module archive and from the JDTClasses directory
            File moduleJar = new File(moduleDir, getModuleArchiveName(module));
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
                        if (jdtClassesEnabled) {
	                        new File(ceylonOutputDirectory, 
	                        		entryToDelete.replace('/', File.separatorChar))
	                                .delete();
                        }
                    }
                } catch (ZipException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Remove the source file from the source archive
	        File moduleSrc = new File(moduleDir, getSourceArchiveName(module));
	        if(moduleSrc.exists()){
	        	moduleJars.add(moduleSrc);
	            String relativeFilePath = filePath.makeRelativeTo(sourceFolder).toString();
	            try {
	                new ZipFile(moduleSrc).removeFile(relativeFilePath);
	            } catch (ZipException e) {
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
		IPath path = getCeylonModulesOutputPath(project);
		return path==null ? 
				project.getFolder("modules") : 
			    project.getFolder(path.makeRelativeTo(project.getLocation()));
	}

	public static IPath getCeylonModulesOutputPath(IProject project) {
		String op = (String)getBuilderArgs(project).get("outputPath");
		return op==null ? null : new Path(op);
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
