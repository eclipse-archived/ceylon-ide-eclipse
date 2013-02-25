package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;
import static com.redhat.ceylon.compiler.java.util.Util.getModuleArchiveName;
import static com.redhat.ceylon.compiler.java.util.Util.getModulePath;
import static com.redhat.ceylon.compiler.java.util.Util.getSourceArchiveName;
import static com.redhat.ceylon.eclipse.core.builder.CeylonNature.NATURE_ID;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathContainers;
import static com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile.createResourceVirtualFile;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.core.runtime.SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.cmr.api.Logger;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.impl.ShaSigner;
import com.redhat.ceylon.compiler.Options;
import com.redhat.ceylon.compiler.java.loader.TypeFactory;
import com.redhat.ceylon.compiler.java.loader.mirror.JavacClass;
import com.redhat.ceylon.compiler.java.tools.CeylonLog;
import com.redhat.ceylon.compiler.java.tools.CeyloncFileManager;
import com.redhat.ceylon.compiler.java.tools.CeyloncTaskImpl;
import com.redhat.ceylon.compiler.java.tools.CeyloncTool;
import com.redhat.ceylon.compiler.java.tools.JarEntryFileObject;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler;
import com.redhat.ceylon.compiler.java.util.RepositoryLister;
import com.redhat.ceylon.compiler.js.JsCompiler;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.ModelLoaderFactory;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.model.LazyPackage;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleValidator;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.model.JavaCompilationUnit;
import com.redhat.ceylon.eclipse.core.model.SourceFile;
import com.redhat.ceylon.eclipse.core.model.loader.JDTClass;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModuleManager;
import com.redhat.ceylon.eclipse.core.model.loader.SourceClass;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.CarUtils;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.file.RelativePath.RelativeFile;
import com.sun.tools.javac.file.ZipFileIndexCache;

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

	private static boolean compileWithJDTModelLoader = false;
    
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
     * A marker ID that identifies tasks
     */
    public static final String TASK_MARKER_ID = PLUGIN_ID + ".ceylonTask";
    
    public static final String SOURCE = "Ceylon"; 

    private final class BuildFileManager extends CeyloncFileManager {
		private final IProject project;
		final boolean explodeModules;

		private BuildFileManager(com.sun.tools.javac.util.Context context,
				boolean register, Charset charset, IProject project) {
			super(context, register, charset);
			this.project = project;
			explodeModules = isExplodeModulesEnabled(project);
		}

		@Override
		protected JavaFileObject getFileForOutput(Location location,
		        final RelativeFile fileName, FileObject sibling)
		        throws IOException {
		    JavaFileObject javaFileObject = super.getFileForOutput(location, fileName, sibling);
		    if (explodeModules && 
		    		javaFileObject instanceof JarEntryFileObject) {
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
    private final static Map<IProject, List<IFile>> projectSources = new HashMap<IProject, List<IFile>>();

    public static final String CEYLON_CONSOLE= "Ceylon Build";
    //private long startTime;

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
    protected IProject[] build(final int kind, Map args, IProgressMonitor mon) 
    		throws CoreException {
        final IProject project = getProject();
        IJavaProject javaProject = JavaCore.create(project);
		SubMonitor monitor = SubMonitor.convert(mon, "Ceylon build of project " + project.getName(),100);
		
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
        
        if (mustDoFullBuild.value) {
            System.out.println("full build of project " + project.getName());
        }
        else {
            System.out.println("incremental build of project " + project.getName());
        }
        
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        if (mustResolveClasspathContainer.value) {
            if (cpContainers != null) {
                for (CeylonClasspathContainer container: cpContainers) {
                	boolean changed = container.resolveClasspath(monitor, true);
                	if(changed) {
                		JavaCore.setClasspathContainer(container.getPath(), new IJavaProject[]{javaProject}, new IClasspathContainer[]{null} , monitor);
                		container.refreshClasspathContainer(monitor, javaProject);
                	}
                }
            }
        }
        
        try {
//            startTime = System.nanoTime();
            /*IBuildConfiguration[] buildConfsBefore = getContext().getAllReferencedBuildConfigs();
            if (buildConfsBefore.length == 0) {
            	//don't clear the console unless 
            	//we are the first project in 
            	//the build invocation
            	findConsole().clearConsole();
            }*/
//            getConsoleStream().println("\n===================================");
//            getConsoleStream().println(timedMessage("Starting Ceylon build on project: " + project.getName()));
//            getConsoleStream().println("-----------------------------------");
            
            boolean binariesGenerationOK;
            final TypeChecker typeChecker;
            if (mustDoFullBuild.value) {
                monitor.subTask("Full Ceylon build of project " + project.getName());
//                getConsoleStream().println(timedMessage("Full build of model"));
                
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
                    typeChecker = parseCeylonModel(project, 
                    		monitor.newChild(5, PREPEND_MAIN_LABEL_TO_SUBTASK));
                    monitor.worked(1);
                }
                else {
                    typeChecker = getProjectTypeChecker(project);
                }
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Typechecking all source  files of project " + project.getName());
                modelStates.put(project, ModelState.TypeChecking);
                builtPhasedUnits = fullTypeCheck(project, typeChecker, 
                		monitor.newChild(35, PREPEND_MAIN_LABEL_TO_SUBTASK ));
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
                final List<IFile> allSources = getProjectSources(project);
//                getConsoleStream().println(timedMessage("Full generation of class files..."));
//                getConsoleStream().println("             ...compiling " + 
//                        allSources.size() + " source files...");
                binariesGenerationOK = generateBinaries(project, javaProject, 
                		allSources, typeChecker, 
                		monitor.newChild(45, PREPEND_MAIN_LABEL_TO_SUBTASK));
//                getConsoleStream().println(successMessage(binariesGenerationOK));
                monitor.worked(1);
                
            }
            else
            {
                monitor.subTask("Incremental Ceylon build of project " + project.getName());
//                getConsoleStream().println(timedMessage("Incremental build of model"));
                
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
                final Collection<IFile> sourceToCompile= new HashSet<IFile>();
                
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

                /*if (emitDiags) {
                    getConsoleStream().println("All files to compile:");
                    dumpSourceList(sourceToCompile);
                }*/
                monitor.subTask("Compiling " + sourceToCompile.size() + " source files in project " + 
                        project.getName());
                builtPhasedUnits = incrementalBuild(project, sourceToCompile, 
                		monitor.newChild(35, PREPEND_MAIN_LABEL_TO_SUBTASK));                
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
//                getConsoleStream().println(timedMessage("Incremental generation of class files..."));
//                getConsoleStream().println("             ...compiling " + 
//                        sourceToCompile.size() + " source files...");
                binariesGenerationOK = generateBinaries(project, javaProject,
                		sourceToCompile, typeChecker, 
                		monitor.newChild(45, PREPEND_MAIN_LABEL_TO_SUBTASK));
//                getConsoleStream().println(successMessage(binariesGenerationOK));
                monitor.worked(1);
                
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                monitor.subTask("Updating referencing projects of project " + project.getName());
//                getConsoleStream().println(timedMessage("Updating model in referencing projects"));
                updateExternalPhasedUnitsInReferencingProjects(project, builtPhasedUnits);
                monitor.worked(1);
            }
            
            if (!binariesGenerationOK) {
                // Add a problem marker if binary generation went wrong for ceylon files
                addBinaryGenerationProblemMarker(project);
                //findConsole().activate();
                //TODO: show the Problems view??
            }
                        
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            monitor.subTask("Collecting dependencies of project " + project.getName());
//            getConsoleStream().println(timedMessage("Collecting dependencies"));
            collectDependencies(project, typeChecker, builtPhasedUnits);
            monitor.worked(1);
    
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            if (isExplodeModulesEnabled(project)) {
                monitor.subTask("Rebuilding using exploded modules directory of " + project.getName());
            	sheduleIncrementalRebuild(args, project, monitor);
                monitor.worked(1);
            }
            
            monitor.done();
            
            if (mustDoFullBuild.value) {
            	warmupCompletionProcessor(project, typeChecker);
            }
            
            return project.getReferencedProjects();
        }
        finally {
//            getConsoleStream().println("-----------------------------------");
//            getConsoleStream().println(timedMessage("End Ceylon build on project: " + project.getName()));
//            getConsoleStream().println("===================================");
        }
    }

	private void warmupCompletionProcessor(final IProject project,
			final TypeChecker typeChecker) {
		Job job = new WarmupJob(project.getName(), typeChecker);
		job.setPriority(Job.BUILD);
		//job.setSystem(true);
		job.setRule(project.getWorkspace().getRoot());
		job.schedule();
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
		for (PhasedUnit pu : builtPhasedUnits) {
		    new UnitDependencyVisitor(pu).visit(pu.getCompilationUnit());
		}
	}

	private void addBinaryGenerationProblemMarker(final IProject project)
			throws CoreException {
		/*IMarker marker = project.createMarker(PROBLEM_MARKER_ID);
		marker.setAttribute(IMarker.MESSAGE, "Bytecode generation has failed on some Ceylon source files in project " + 
		        project.getName() + ". Look at the Ceylon console for more information.");
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.LOCATION, "Bytecode generation");*/
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
		                Unit duplicateUnit = duplicateDeclaration.getUnit();
	                    if ((duplicateUnit instanceof SourceFile) && 
                            (duplicateUnit instanceof IResourceAware)) {
                            IFile duplicateDeclFile = ((IResourceAware) duplicateUnit).getFileResource();
                            if (duplicateDeclFile != null && duplicateDeclFile.exists()) {
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
        return mustDoFullBuild.value || 
                mustResolveClasspathContainer.value ||
                sourceModified.value;
    }

//    private static String successMessage(boolean binariesGenerationOK) {
//        return "             " + (binariesGenerationOK ? 
//                "...binary generation succeeded" : "...binary generation FAILED");
//    }

    private Set<String> getDependentsOf(IFile srcFile,
            TypeChecker currentFileTypeChecker,
            IProject currentFileProject) {
    	
        if (srcFile.getRawLocation().getFileExtension().equals("ceylon")) {
            PhasedUnit phasedUnit = currentFileTypeChecker.getPhasedUnits()
            		.getPhasedUnit(createResourceVirtualFile(srcFile));
            Set<String> result = new LinkedHashSet<String>();
            if (phasedUnit != null && phasedUnit.getUnit() != null) {
//                if (ModuleManager.MODULE_FILE.equals(srcFile.getName())) {
//                    for (Package p: phasedUnit.getPackage().getModule().getPackages()) {
//                        for (Unit u: p.getUnits()) {
//                            result.add(u.getFullPath());
//                            for (Declaration d: u.getDeclarations()) {
//                                result.addAll(d.getUnit().getDependentsOf());
//                            }
//                        }
//                    }
//                }
                if (ModuleManager.PACKAGE_FILE.equals(srcFile.getName())) {
                    for (Unit u: phasedUnit.getPackage().getUnits()) {
                        result.add(u.getFullPath());
                        for (Declaration d: u.getDeclarations()) {
                            result.addAll(d.getUnit().getDependentsOf());
                        }
                    }
                }
                result.addAll(phasedUnit.getUnit().getDependentsOf());
            }
            return result;
        } 
        else {
            Unit unit = getJavaUnit(currentFileProject, srcFile);
            if (unit instanceof JavaCompilationUnit) {
                return unit.getDependentsOf();
            }
            else {
                return Collections.emptySet();
            }
        }
        
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
                            PhasedUnit newReferencingPhasedUnit = new CrossProjectPhasedUnit(referencingPhasedUnit.getUnitFile(), 
                                    referencingPhasedUnit.getSrcDir(), 
                                    builtPhasedUnit.getCompilationUnit(), 
                                    referencingPhasedUnit.getPackage(), 
                                    phasedUnitsOfDependency.getModuleManager(), 
                                    referencingTypeChecker, 
                                    builtPhasedUnit.getTokens(),
                                    project);
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
            cu.addLexError(le);
        }
        lexerErrors.clear();

        List<ParseError> parserErrors = parser.getErrors();
        for (ParseError pe : parserErrors) {
            cu.addParseError(pe);
        }
        parserErrors.clear();
        
        
        PhasedUnit newPhasedUnit = new ProjectPhasedUnit(file, srcDir, cu, pkg, 
                moduleManager, typeChecker, tokens);
        
        return newPhasedUnit;
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
            Set<String> dependentsOf = new TreeSet<String>();
            if (alreadyBuiltPhasedUnit!=null) {
                // Editing an already built file
                pkg = alreadyBuiltPhasedUnit.getPackage();
                if (alreadyBuiltPhasedUnit.getUnit() != null) {
                    dependentsOf.addAll(alreadyBuiltPhasedUnit.getUnit().getDependentsOf());
                    /*if (ModuleManager.MODULE_FILE.equals(alreadyBuiltPhasedUnit.getUnit().getFilename())) {
                        for (Package p: alreadyBuiltPhasedUnit.getPackage().getModule().getPackages()) {
                            for (Unit u: p.getUnits()) {
                                dependentsOf.add(u.getFullPath());
                                for (Declaration d: u.getDeclarations()) {
                                    dependentsOf.addAll(d.getUnit().getDependentsOf());
                                }
                            }
                        }
                    }
                    else if (ModuleManager.PACKAGE_FILE.equals(alreadyBuiltPhasedUnit.getUnit().getFilename())) {
                        for (Unit u: alreadyBuiltPhasedUnit.getPackage().getUnits()) {
                            dependentsOf.add(u.getFullPath());
                            for (Declaration d: u.getDeclarations()) {
                                dependentsOf.addAll(d.getUnit().getDependentsOf());
                            }
                        }
                    }*/
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
            phasedUnit.visitSrcModulePhase();
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

        Iterator<Set<String>> itr = dependentsOfList.iterator();
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.getUnit().getDependentsOf().addAll(itr.next());
            
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
        loader.reset();
        
        for (PhasedUnits dependencyPhasedUnits: phasedUnitsOfDependencies) {
            loader.addSourceArchivePhasedUnits(dependencyPhasedUnits.getPhasedUnits());
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
        
        if (compileWithJDTModelLoader()) {
            loader.completeFromClasses();
            monitor.worked(2);
        }
        
        for (PhasedUnit pu: dependencies) {
            pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
        }

        loader.loadPackage("com.redhat.ceylon.compiler.java.metadata", true);
        loader.loadPackage("ceylon.language", true);
        loader.loadPackage("ceylon.language.descriptor", true);
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

        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        
        monitor.done();
        
        return typeChecker.getPhasedUnits().getPhasedUnits();
    }

    private boolean compileWithJDTModelLoader() {
        return compileWithJDTModelLoader;
    }

    public static TypeChecker parseCeylonModel(IProject project,
            IProgressMonitor mon) throws CoreException {

    	modelStates.put(project, ModelState.Parsing);
    	typeCheckers.remove(project);
    	projectSources.remove(project);
        
        SubMonitor monitor = SubMonitor.convert(mon,
        		"Setting up typechecker for project " + project.getName(), 5);

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

        monitor.subTask("- determining module dependencies for " 
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
        
        monitor.done();
        
        return typeChecker;

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
		
		RepositoryManager repositoryManager = repoManager()
		        .cwd(project.getLocation().toFile())
		        .systemRepo(getInterpolatedCeylonSystemRepo(project))
		        .extraUserRepos(getReferencedProjectsOutputRepositories(project))
		        .logger(new EclipseLogger())
                .isJDKIncluded(true)
		        .buildManager();
		
        typeCheckerBuilder.setRepositoryManager(repositoryManager);
        TypeChecker typeChecker = typeCheckerBuilder.getTypeChecker();
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
    	            phasedUnit.getCompilationUnit().visit(new MarkerCreator(file));
    	            addTaskMarkers(file, phasedUnit.getTokens());
    	        }
    			return Status.OK_STATUS;
    		}
    	}.schedule();
    }

    private boolean generateBinaries(IProject project, IJavaProject javaProject,
    		Collection<IFile> filesToCompile, TypeChecker typeChecker, 
    		IProgressMonitor monitor) throws CoreException {
        List<String> options = new ArrayList<String>();
        List<String> js_srcdir = new ArrayList<String>();
        List<String> js_repos = new ArrayList<String>();
        boolean js_verbose = false;
        String js_outRepo = null;

        String srcPath = "";
        for (IPath sourceFolder : getSourceFolders(javaProject)) {
            File sourcePathElement = toFile(project,sourceFolder
            		.makeRelativeTo(project.getFullPath()));
            if (! srcPath.isEmpty()) {
                srcPath += File.pathSeparator;
            }
            srcPath += sourcePathElement.getAbsolutePath();
            js_srcdir.add(sourcePathElement.getAbsolutePath());
        }
        options.add("-src");
        options.add(srcPath);
        options.add("-encoding");
        options.add(project.getDefaultCharset());
        
        for (String repository : getUserRepositories(project)) {
            options.add("-rep");
            options.add(repository);
            js_repos.add(repository);
        }

        String verbose = System.getProperty("ceylon.verbose");
		if (verbose!=null && "true".equals(verbose)) {
            options.add("-verbose");
            js_verbose = true;
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

        if (!sourceFiles.isEmpty() || !javaSourceFiles.isEmpty()) {
            PrintWriter printWriter = new PrintWriter(System.out);//(getConsoleErrorStream(), true);
            boolean success = true;
            //Compile JS first
            if (compileToJs(project) && !sourceFiles.isEmpty()) {
                Options jsopts = new Options(js_repos, js_srcdir, null/*sys repo*/, js_outRepo, null/*uname*/,
                        null/*pass*/, true, true, true, true, js_verbose, false, false, false,
                        project.getDefaultCharset());
                JsCompiler jsc = new JsCompiler(typeChecker, jsopts).stopOnErrors(false);
                try {
                    jsc.generate();
                } catch (IOException ex) {
                    ex.printStackTrace(printWriter);
                }
            }
            if (compileToJava(project)) {
                // always add the java files, otherwise ceylon code won't see them and they won't end up in the archives (src/car)
                sourceFiles.addAll(javaSourceFiles);
                if(!sourceFiles.isEmpty()){
                    success = compile(project, javaProject, options, sourceFiles, 
                            typeChecker, printWriter, monitor);
                }
            }
            return success;
        }
        else {
            return true;
        }
    }

    private boolean compile(final IProject project, IJavaProject javaProject, 
    		List<String> options, java.util.List<File> sourceFiles, 
    		final TypeChecker typeChecker, PrintWriter printWriter,
    		IProgressMonitor mon) 
    				throws VerifyError {
    	
    	final SubMonitor monitor = SubMonitor.convert(mon, 
        		"Generating binaries for " + sourceFiles.size() + 
        		" source files in project " + project.getName(), 
        		sourceFiles.size());

        CeyloncTool compiler;
        try {
            compiler = new CeyloncTool();
        } catch (VerifyError e) {
            System.err.println("ERROR: Cannot run tests! Did you maybe forget to configure the -Xbootclasspath/p: parameter?");
            throw e;
        }

        CompileErrorReporter errorReporter = new CompileErrorReporter(project);

        final com.sun.tools.javac.util.Context context = new com.sun.tools.javac.util.Context();
        context.put(com.sun.tools.javac.util.Log.outKey, printWriter);
        context.put(DiagnosticListener.class, errorReporter);
        CeylonLog.preRegister(context);
        
        BuildFileManager fileManager = new BuildFileManager(context, true, null, project);
        
        computeCompilerClasspath(project, javaProject, options);
        
        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(sourceFiles);
        
        if (compileWithJDTModelLoader()) {
            setupJDTModelLoader(project, typeChecker, context);
        }
        
        ZipFileIndexCache.getSharedInstance().clearCache();
        
        CeyloncTaskImpl task = (CeyloncTaskImpl) compiler.getTask(printWriter, 
                fileManager, errorReporter, options, null, 
                compilationUnits);
        task.setTaskListener(new TaskListener() {
			@Override
			public void started(TaskEvent ta) {
				String name = ta.getSourceFile().getName();
				name = name.substring(name.lastIndexOf("/")+1);
				monitor.subTask("- compiling " + name);
			}
			@Override
			public void finished(TaskEvent ta) {
				monitor.worked(1);
			}
		});
        boolean success=false;
        try {
            success = task.call();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        monitor.done();
        return success;
    }

	private void computeCompilerClasspath(IProject project,
			IJavaProject javaProject, List<String> options) {
		
        List<String> classpathElements = new ArrayList<String>();

//        Modules projectModules = getProjectModules(project);
//		ArtifactContext ctx;
//        if (projectModules != null) {
//            Module languageModule = projectModules.getLanguageModule();
//            ctx = new ArtifactContext(languageModule.getNameAsString(), 
//            		languageModule.getVersion());
//        } 
//        else {
//            ctx = new ArtifactContext("ceylon.language", 
//            		TypeChecker.LANGUAGE_MODULE_VERSION);
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
        
        IPath workspaceLocation = project.getWorkspace().getRoot().getLocation();
        addProjectClasspathElements(classpathElements, workspaceLocation,
				javaProject);
        try {
			for (IProject p: project.getReferencedProjects()) {
				if(p.isAccessible()){
					addProjectClasspathElements(classpathElements, workspaceLocation,
							JavaCore.create(p));
				}
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
	}

	private void setupJDTModelLoader(final IProject project,
			final TypeChecker typeChecker,
			final com.sun.tools.javac.util.Context context) {

		final JDTModelLoader modelLoader = getModelLoader(typeChecker);
        
		context.put(LanguageCompiler.ceylonContextKey, typeChecker.getContext());
		context.put(TypeFactory.class, modelLoader.getTypeFactory());
		context.put(LanguageCompiler.phasedUnitsManagerKey, 
				new JdtPhasedUnitsManager(modelLoader, project, typeChecker));
		
		modelLoader.setSourceFileObjectManager(new JdtSourceFileObjectManager(context, modelLoader));
		
		context.put(TypeFactory.class, modelLoader.getTypeFactory());
		context.put(ModelLoaderFactory.class, new ModelLoaderFactory() {
		    @Override
		    public AbstractModelLoader createModelLoader(
		            com.sun.tools.javac.util.Context context) {
		        return modelLoader;
		    }
		});
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

			File outputDir = toFile(javaProj.getProject(), javaProj.getOutputLocation()
                    .makeRelativeTo(javaProj.getProject().getFullPath()));			
			classpathElements.add(outputDir.getAbsolutePath());
			for (IClasspathEntry cpEntry : javaProj.getResolvedClasspath(true)) {
				if (isInCeylonClassesOutputFolder(cpEntry.getPath())) {
					classpathElements.add(workspaceLocation.append(cpEntry.getPath()).toOSString());
				}
			}
		} 
		catch (JavaModelException e1) {
			e1.printStackTrace();
		}
	}

	public static boolean isExplodeModulesEnabled(IProject project) {
        Map args = getBuilderArgs(project);
		return args.get("explodeModules")!=null ||
        		args.get("enableJdtClasses")!=null;
	}

	public static boolean showWarnings(IProject project) {
		return getBuilderArgs(project).get("hideWarnings")==null;
	}
	public static boolean compileToJs(IProject project) {
        return getBuilderArgs(project).get("compileJs")!=null;
	}
    public static boolean compileToJava(IProject project) {
        return getBuilderArgs(project).get("compileJava")==null;
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
                if (referencedProject.isOpen() && referencedProject.hasNature(NATURE_ID)) {
                    repos.add(getCeylonModulesOutputDirectory(referencedProject).getAbsolutePath());
                }
            }
        }
        return repos;
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

    public static List<String> getCeylonRepositories(IProject project) {
        CeylonProjectConfig projectConfig = CeylonProjectConfig.get(project);
        List<String> projectLookupRepos = projectConfig.getProjectLocalRepos();
        List<String> globalLookupRepos = projectConfig.getGlobalLookupRepos();
        List<String> projectRemoteRepos = projectConfig.getProjectRemoteRepos();

        List<String> repos = new ArrayList<String>();
        repos.addAll(projectLookupRepos);
        repos.addAll(globalLookupRepos);
        repos.addAll(projectRemoteRepos);
        return repos;
    }

    private static File toFile(IProject project, IPath path) {
		return project.getFolder(path).getRawLocation().toFile();
	}
    
    private static void clearMarkersOn(IResource resource) {
        try {
            resource.deleteMarkers(TASK_MARKER_ID, false, DEPTH_INFINITE);
            resource.deleteMarkers(PROBLEM_MARKER_ID, true, DEPTH_INFINITE);
            resource.deleteMarkers(PROBLEM_MARKER_ID + ".backend", true, DEPTH_INFINITE);
            //these are actually errors from the Ceylon compiler, but
            //we did not bother creating a separate annotation type!
            resource.deleteMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, DEPTH_INFINITE);
        } 
        catch (CoreException e) {
        	e.printStackTrace();
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

    /*private void dumpSourceList(Collection<IFile> sourcesToCompile) {
        MessageConsoleStream consoleStream= getConsoleStream();
        for(Iterator<IFile> iter= sourcesToCompile.iterator(); iter.hasNext(); ) {
            IFile srcFile= iter.next();
            consoleStream.println("  " + srcFile.getFullPath());
        }
    }*/

//    protected static MessageConsoleStream getConsoleStream() {
//        return findConsole().newMessageStream();
//    }
//    
//    protected static MessageConsoleStream getConsoleErrorStream() {
//        final MessageConsoleStream stream = findConsole().newMessageStream();
//        //TODO: all this, just to get the color red? can that be right??
//        /*try {
//			getWorkbench().getProgressService().runInUI(getWorkbench().getWorkbenchWindows()[0], 
//					new IRunnableWithProgress() {
//				
//				@Override
//				public void run(IProgressMonitor monitor) throws InvocationTargetException,
//						InterruptedException {
//					stream.setColor(getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED));
//				}
//			}, null);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}*/
//		return stream;
//    }
//    
//    private String timedMessage(String message) {
//        long elapsedTimeMs = (System.nanoTime() - startTime) / 1000000;
//        return String.format("[%1$10d] %2$s", elapsedTimeMs, message);
//    }

//    /**
//     * Find or create the console with the given name
//     * @param consoleName
//     */
//    protected static MessageConsole findConsole() {
//        String consoleName = CEYLON_CONSOLE;
//        MessageConsole myConsole= null;
//        final IConsoleManager consoleManager= ConsolePlugin.getDefault().getConsoleManager();
//        IConsole[] consoles= consoleManager.getConsoles();
//        for(int i= 0; i < consoles.length; i++) {
//            IConsole console= consoles[i];
//            if (console.getName().equals(consoleName))
//                myConsole= (MessageConsole) console;
//        }
//        if (myConsole == null) {
//            myConsole= new MessageConsole(consoleName, 
//            		CeylonPlugin.getInstance().getImageRegistry()
//            		    .getDescriptor(CeylonResources.BUILDER));
//            consoleManager.addConsoles(new IConsole[] { myConsole });
//        }
////      consoleManager.showConsoleView(myConsole);
//        return myConsole;
//    }

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
        clearProjectMarkers(project);
        clearMarkersOn(project);

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

    public static Iterable<TypeChecker> getTypeCheckers() {
        return typeCheckers.values();
    }

    public static void removeProject(IProject project) {
        typeCheckers.remove(project);
        projectSources.remove(project);
        modelStates.remove(project);
        CeylonProjectConfig.remove(project);
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
                e.printStackTrace();
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
            boolean explodeModules = isExplodeModulesEnabled(project);
			final File ceylonOutputDirectory = explodeModules ? 
            		getCeylonClassesOutputDirectory(project) : null;
            File moduleDir = getModulePath(modulesOutputDirectory, module);
            
            //Remove the classes belonging to the source file from the
            //module archive and from the JDTClasses directory
            File moduleJar = new File(moduleDir, getModuleArchiveName(module));
            if(moduleJar.exists()){
                moduleJars.add(moduleJar);
                String relativeFilePath = filePath.makeRelativeTo(sourceFolder).toString();
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
                        zipFile.removeFile(entryToDelete);
                        if (explodeModules) {
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
        class ConsoleLog implements Logger {
        	PrintWriter writer;
            ConsoleLog() {
                writer = new PrintWriter(System.out); //new PrintWriter(getConsoleStream()));
            }

			@Override
			public void error(String str) {
				writer.append("Error: " + str + "\n");
			}

			@Override
			public void warning(String str) {
				writer.append("Warning: " + str + "\n");
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
        if (systemRepo == null) {
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
				"http://modules.ceylon-lang.org/test"
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

}
