package com.redhat.ceylon.eclipse.imp.builder;

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
import java.util.Set;

import javax.tools.JavaFileObject;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.builder.MarkerCreator;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.redhat.ceylon.compiler.tools.CeyloncFileManager;
import com.redhat.ceylon.compiler.tools.CeyloncTaskImpl;
import com.redhat.ceylon.compiler.tools.CeyloncTool;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleBuilder;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.util.RepositoryLister;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;
import com.redhat.ceylon.eclipse.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.vfs.ResourceVirtualFile;
import com.sun.tools.javac.util.Log;

/**
 * A builder may be activated on a file containing ceylon code every time it has
 * changed (when "Build automatically" is on), or when the programmer chooses to
 * "Build" a project.
 * 
 * TODO This default implementation was generated from a template, it needs to
 * be completed manually.
 */
public class CeylonBuilder extends IncrementalProjectBuilder {

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

    private final static Map<IProject, TypeChecker> typeCheckers = new HashMap<IProject, TypeChecker>();

    public static final String CEYLON_CONSOLE= "Ceylon";

    private final IResourceVisitor fResourceVisitor= new SourceCollectorVisitor();

    private final IResourceDeltaVisitor fDeltaVisitor= new SourceDeltaVisitor();

    private IPreferencesService fPrefService;

    private final Collection<IFile> fChangedSources= new HashSet<IFile>();

    private final Collection<IFile> fSourcesToCompile= new HashSet<IFile>();

    private final class SourceDeltaVisitor implements IResourceDeltaVisitor {
        public boolean visit(IResourceDelta delta) throws CoreException {
            return processResource(delta.getResource());
        }
    }

    private class SourceCollectorVisitor implements IResourceVisitor {
        public boolean visit(IResource res) throws CoreException {
            return processResource(res);
        }
    }

    private boolean processResource(IResource resource) {
        if (resource instanceof IFile) {
            IFile file= (IFile) resource;

            if (isSourceFile(file) || isNonRootSourceFile(file)) {
                fChangedSources.add(file);
            }
            
            return false;
        } else if (isOutputFolder(resource)) {
            return false;
        }
        return true;
    }

    private class AllSourcesVisitor implements IResourceVisitor {
        private final Collection<IFile> fResult;

        public AllSourcesVisitor(Collection<IFile> result) {
            fResult= result;
        }

        public boolean visit(IResource resource) throws CoreException {
            if (resource instanceof IFile) {
                IFile file= (IFile) resource;

                if (file.exists()) {
                    if (isSourceFile(file) || isNonRootSourceFile(file)) {
                        fResult.add(file);
                    }
                }
                return false;
            } else if (isOutputFolder(resource)) {
                return false;
            }
            return true;
        }
    }

    public static List<PhasedUnit> getUnits(IProject project) {
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
        for (TypeChecker tc: typeCheckers.values()) {
            for (PhasedUnit pu: tc.getPhasedUnits().getPhasedUnits()) {
                result.add(pu);
            }
        }
        return result;
    }

    /*public static PhasedUnit getPhasedUnit(IFile file) {
        for (PhasedUnit pu: getUnits(file.getProject())) {
            if (getFile(pu).getFullPath().equals(file.getFullPath())) {
                return pu;
            }
        }
        return null;
    }*/

    public static List<PhasedUnit> getUnits(String[] projects) {
        List<PhasedUnit> result = new ArrayList<PhasedUnit>();
        if (projects!=null) {
            for (Map.Entry<IProject, TypeChecker> me: typeCheckers.entrySet()) {
                for (String pname: projects) {
                    if (me.getKey().getName().equals(pname)) {
                        result.addAll(me.getValue().getPhasedUnits().getPhasedUnits());
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

        if (!LANGUAGE.hasExtension(path.getFileExtension()))
            return false;

        ISourceProject sourceProject = getSourceProject();
        if (sourceProject != null) {
            for (IPath sourceFolder: getSourceFolders(sourceProject)) {
                if (sourceFolder.isPrefixOf(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Decide whether or not to scan a file for dependencies. Note:
     * <code>isNonRootSourceFile()</code> and <code>isSourceFile()</code> should
     * never return true for the same file.
     * 
     * @return true iff the given file is a source file that this builder should
     *         scan for dependencies, but not compile as a top-level compilation
     *         unit.
     * 
     */
    protected boolean isNonRootSourceFile(IFile resource) {
        return false;
    }

    /**
     * @return true iff this resource identifies the output folder
     */
    protected boolean isOutputFolder(IResource resource) {
        IProject project = resource.getProject();
        IJavaProject javaProject = JavaCore.create(resource.getProject());
        if (javaProject.exists()) {
            IPath outputDirectory = null;
            try {
                outputDirectory = javaProject.getOutputLocation().makeRelativeTo(project.getFullPath());
            } catch (JavaModelException e) {
                e.printStackTrace();
                return false;
            }
            if (outputDirectory != null) {
                return resource.getProjectRelativePath().equals(outputDirectory);
            }
        }
        return false;
    }

    @Override
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
        if (getPreferencesService().getProject() == null) {
            getPreferencesService().setProject(getProject());
        }
        
        boolean emitDiags= getDiagPreference();

        fChangedSources.clear();
        fSourcesToCompile.clear();

        Collection<IFile> allSources= new ArrayList<IFile>();

        IProject project = getProject();
        ISourceProject sourceProject = getSourceProject();
        if (sourceProject == null) {
            return new IProject[0];
        }

        TypeChecker typeChecker = typeCheckers.get(project);

        final class BooleanHolder {
            public boolean value;
        };
        final BooleanHolder mustDoFullBuild = new BooleanHolder();
        mustDoFullBuild.value = kind == FULL_BUILD || kind == CLEAN_BUILD || typeChecker == null;
        
        IResourceDelta delta = null;
        if (! mustDoFullBuild.value) {
            delta = getDelta(getProject());
            if (delta != null) {
                try {
                    delta.accept(new IResourceDeltaVisitor() {
                        @Override
                        public boolean visit(IResourceDelta resourceDelta) throws CoreException {
                            IResource resource = resourceDelta.getResource();
                            if (resourceDelta.getKind() == IResourceDelta.REMOVED && resource instanceof IFolder) {
                                IFolder folder = (IFolder) resource; 
                                Package pkg = retrievePackage(folder);
                                // If a package has been removed, then trigger a full build
                                if (pkg != null) {
                                    mustDoFullBuild.value = true;
                                    return false;
                                }
                            }
                            if (resource instanceof IFile && 
                                    (resource.getName().equals(ModuleBuilder.MODULE_FILE) ||
                                            resource.getName().equals(ModuleBuilder.PACKAGE_FILE))) {
                                // If a module descriptor has been added, removed or changed, trigger a full build
                                mustDoFullBuild.value = true;
                                return false;
                            }
                            return true;
                        }
                    });
                } catch (CoreException e) {
                    e.printStackTrace();
                    mustDoFullBuild.value = true;
                }
            }
        }

        List<PhasedUnit> builtPhasedUnits = null;
        if (mustDoFullBuild.value) {
            IJavaProject javaProject = JavaCore.create(project);
            final File outputDirectory = getOutputDirectory(javaProject);
            if (outputDirectory != null) {
                List<String> extensionsToDelete = Arrays.asList(".jar", ".car", ".src", ".sha1");
                new RepositoryLister(extensionsToDelete).list(outputDirectory, new RepositoryLister.Actions() {
                    @Override
                    public void doWithFile(File path) {
                        path.delete();
                    }
                    
                    public void exitDirectory(File path) {
                        if (path.list().length == 0 && ! path.equals(outputDirectory)) {
                            path.delete();
                        }
                    }
                });
            }
            
            try {
                getProject().accept(new AllSourcesVisitor(allSources));
            } catch (CoreException e) {
                getPlugin().getLog().log(new Status(IStatus.ERROR, getPlugin().getID(), e.getLocalizedMessage(), e));
            }
            clearMarkersOn(allSources);
            builtPhasedUnits = fullBuild(project, sourceProject, monitor);
            if (builtPhasedUnits== null)
                return new IProject[0];
            generateBinaries(project, sourceProject, allSources, monitor);
        }
        else
        {
            try {
                if (delta != null) {
                    if (emitDiags)
                        getConsoleStream().println("==> Scanning resource delta for project '" + getProject().getName() + "'... <==");
                    delta.accept(fDeltaVisitor);
                    if (emitDiags)
                        getConsoleStream().println("Delta scan completed for project '" + getProject().getName() + "'...");
                } else {
                    if (emitDiags)
                        getConsoleStream().println("==> Scanning for source files in project '" + getProject().getName() + "'... <==");
                    getProject().accept(fResourceVisitor);
                    if (emitDiags)
                        getConsoleStream().println("Source file scan completed for project '" + getProject().getName() + "'...");
                }
                if (fChangedSources.size() > 0) {
                    Collection<IFile> changeDependents= new HashSet<IFile>();

                    changeDependents.addAll(fChangedSources);
                    if (emitDiags) {
                        getConsoleStream().println("Changed files:");
                        dumpSourceList(changeDependents);
                    }

                    boolean changed= false;
                    do {
                        Collection<IFile> additions= new HashSet<IFile>();
                        for(Iterator<IFile> iter= changeDependents.iterator(); iter.hasNext(); ) {
                            IFile srcFile= iter.next();
                            PhasedUnit phasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(srcFile));
                            if (phasedUnit != null) {
                                Set<PhasedUnit> phasedUnitsDependingOn = phasedUnit.getDependentsOf();
                                if (phasedUnitsDependingOn != null) {
                                    for(PhasedUnit dependingPhasedUnit : phasedUnitsDependingOn ) {
                                        IFile depFile= (IFile) ((IFileVirtualFile) dependingPhasedUnit.getUnitFile()).getResource();
                                        additions.add(depFile);
                                    }
                                }
                            }
                        }
                        changed = changeDependents.addAll(additions);
                    } while (changed);

                    PhasedUnits phasedUnits = typeChecker.getPhasedUnits();
                    
                    for(IFile f: changeDependents) {
                        if (isSourceFile(f)) {
                            if (f.exists()) {
                                fSourcesToCompile.add(f);
                            }
                            else {
                                if (delta != null) {
                                    IResourceDelta removedFile = delta.findMember(f.getProjectRelativePath());
                                    if (removedFile != null && 
                                            (removedFile.getFlags() & IResourceDelta.MOVED_TO) != 0 &&
                                            removedFile.getMovedToPath() != null) {
                                        fSourcesToCompile.add(project.getFile(removedFile.getMovedToPath().removeFirstSegments(1)));
                                    }
                                }
                                    
                                // If the file is moved : add a dependency on the new file
                                PhasedUnit phasedUnitToDelete = phasedUnits.getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(f));
                                if (phasedUnitToDelete != null) {
                                    phasedUnits.removePhasedUnitForRelativePath(phasedUnitToDelete.getPathRelativeToSrcDir());
                                }
                            }
                        }
                    }

                    for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                        if (phasedUnit.getUnit().getUnresolvedReferences().size() > 0) {
                            fSourcesToCompile.add((IFile) ((IFileVirtualFile)(phasedUnit.getUnitFile())).getResource());
                        }
                    }
                }
                if (emitDiags) {
                    getConsoleStream().println("All files to compile:");
                    dumpSourceList(fSourcesToCompile);
                }
                clearMarkersOn(fSourcesToCompile);
                builtPhasedUnits = incrementalBuild(project, sourceProject, monitor);
                if (builtPhasedUnits== null)
                    return new IProject[0];
                generateBinaries(project, sourceProject, fSourcesToCompile, monitor);
            } catch (CoreException e) {
                getPlugin().writeErrorMsg("Build failed: " + e.getMessage());
            }
        }
        
        typeChecker = typeCheckers.get(project); // could have been instanciated and added into the map by the full build
        for (PhasedUnit phasedUnit : builtPhasedUnits) {
            IFile file = getFile(phasedUnit);
            List<CommonToken> tokens = phasedUnit.getTokens();
            phasedUnit.getCompilationUnit()
                .visit(new ErrorVisitor(new MarkerCreator(file, PROBLEM_MARKER_ID)) {
                    @Override
                    public int getSeverity(Message error) {
                        return IMarker.SEVERITY_ERROR;
                    }
                });
            addTaskMarkers(file, tokens);
        }
        for (PhasedUnit pu : builtPhasedUnits) {
            pu.collectUnitDependencies(typeChecker.getPhasedUnits());
        }

        monitor.worked(1);
        monitor.done();
        return new IProject[] {project};
    }

    private List<PhasedUnit> incrementalBuild(IProject project,
        ISourceProject sourceProject, IProgressMonitor monitor) {
        TypeChecker typeChecker = typeCheckers.get(project);
        List<PhasedUnit> phasedUnitsToUpdate = new ArrayList<PhasedUnit>();
        for (IFile fileToUpdate : fSourcesToCompile) {
            ResourceVirtualFile file = ResourceVirtualFile.createResourceVirtualFile(fileToUpdate);
            IPath srcFolderPath = retrieveSourceFolder(fileToUpdate);
            ResourceVirtualFile srcDir = new IFolderVirtualFile(project, srcFolderPath);
            ANTLRInputStream input;
            try {
                input = new ANTLRInputStream(file.getInputStream());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            CeylonLexer lexer = new CeylonLexer(input);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            if (monitor.isCanceled()) return null;

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
            
            PhasedUnit alreadyBuiltPhasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(file);

            Package pkg = null;
            if (alreadyBuiltPhasedUnit!=null) {
                // Editing an already built file
                pkg = alreadyBuiltPhasedUnit.getPackage();
            }
            else {
                IContainer packageFolder = file.getResource().getParent();
                pkg = retrievePackage(packageFolder);
                if (pkg == null) {
                    pkg = createNewPackage(packageFolder);
                }
            }

            phasedUnitsToUpdate.add(new PhasedUnit(file, srcDir, cu, pkg, 
                    typeChecker.getPhasedUnits().getModuleBuilder(), 
                    typeChecker.getContext(), tokens));
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            typeChecker.getPhasedUnits().addPhasedUnit(phasedUnit.getUnitFile(), phasedUnit);
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.validateTree();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.scanDeclarations();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.scanTypeDeclarations();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.validateRefinement();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.analyseTypes();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate) {
            phasedUnit.analyseFlow();
        }

        return phasedUnitsToUpdate;
    }

    private List<PhasedUnit> fullBuild(IProject project, ISourceProject sourceProject, IProgressMonitor monitor) {
        System.out.println("Starting full build");

        monitor.beginTask("Full Ceylon Build", 4);
        TypeChecker typeChecker = buildCeylonModel(project, sourceProject,
                monitor);

        monitor.worked(1);
        monitor.subTask("Collecting Ceylon problems for project " 
                    + project.getName());

        System.out.println("Finished full build");
        return typeChecker.getPhasedUnits().getPhasedUnits();
    }

    public static TypeChecker buildCeylonModel(IProject project,
            ISourceProject sourceProject, IProgressMonitor monitor) {
        monitor.subTask("Collecting Ceylon source files for project " 
                    + project.getName());

        typeCheckers.remove(project);

        TypeCheckerBuilder typeCheckerBuilder = new TypeCheckerBuilder()
                .verbose(false);
        for (IPath sourceFolder : getSourceFolders(sourceProject)) {
            typeCheckerBuilder.addSrcDirectory(new IFolderVirtualFile(project,
                    sourceFolder.makeRelativeTo(project.getFullPath())));
        }

        monitor.worked(1);
        monitor.subTask("Parsing Ceylon source files for project " 
                    + project.getName());

        TypeChecker typeChecker = typeCheckerBuilder.getTypeChecker();

        monitor.worked(1);
        monitor.subTask("Compiling Ceylon source files for project " 
                    + project.getName());

        // Parsing of ALL units in the source folder should have been done
        typeChecker.process();

        typeCheckers.put(project, typeChecker);
        return typeChecker;
    }

    private static String languageVersion = "0.1";
    private final static String languageCar = System.getProperty("user.home")+"/.ceylon/repo/ceylon/language/"+languageVersion +"/ceylon.language-"+languageVersion+".car";

    private boolean generateBinaries(IProject project, ISourceProject sourceProject, Collection<IFile> filesToCompile, IProgressMonitor monitor) {
        List<String> options = new ArrayList<String>();

        String srcPath = "";
        for (IPath sourceFolder : getSourceFolders(sourceProject)) {
            File sourcePathElement = project.getFolder(sourceFolder.makeRelativeTo(project.getFullPath())).getRawLocation().toFile();
            if (! srcPath.isEmpty()) {
                srcPath += File.pathSeparator;
            }
            srcPath += sourcePathElement.getAbsolutePath();
        }
        options.add("-src");
        options.add(srcPath);
        
        if (System.getProperty("ceylon.verbose")!=null) {
            options.add("-verbose");
        }
        options.add("-g:lines,vars,source");

        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject.exists()) {
            IPath outputLocation;
            try {
                outputLocation = javaProject.getOutputLocation();
                options.add("-out");
                options.add(project.getFolder(outputLocation.makeRelativeTo(project.getFullPath())).getRawLocation().toFile().getAbsolutePath());
            } catch (JavaModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

//        options.add("-verbose");
        options.add("-cp");
        options.add(languageCar);
        
        java.util.List<File> sourceFiles = new ArrayList<File>();
        for (IFile file : filesToCompile) {
            sourceFiles.add(file.getRawLocation().toFile());
        }

        if (sourceFiles.size() > 0)
        {
            CeyloncTool compiler;
            try {
                compiler = new CeyloncTool();
            } catch (VerifyError e) {
                System.err.println("ERROR: Cannot run tests! Did you maybe forget to configure the -Xbootclasspath/p: parameter?");
                throw e;
            }
            
            com.sun.tools.javac.util.Context context = new com.sun.tools.javac.util.Context();
            MessageConsole console = findConsole(getConsoleName());
            console.clearConsole();
            context.put(Log.outKey, new PrintWriter(console.newMessageStream(), true));
            
            CeyloncFileManager fileManager = new CeyloncFileManager(context, true, null); //(CeyloncFileManager)compiler.getStandardFileManager(null, null, null);
            Iterable<? extends JavaFileObject> compilationUnits1 =
                    fileManager.getJavaFileObjectsFromFiles(sourceFiles);
            CeyloncTaskImpl task = (CeyloncTaskImpl) compiler.getTask(new PrintWriter(console.newMessageStream(), true), 
            		fileManager, null, options, null, compilationUnits1);
            boolean success=false;
            try {
            	success = task.call();
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
            if (!success) console.activate();
			return success;
        }
        else
            return false;
    }
    
    /**
     * Clears all problem markers (all markers whose type derives from IMarker.PROBLEM)
     * from the given file. A utility method for the use of derived builder classes.
     */
    protected void clearMarkersOn(IFile file) {
        try {
            clearTaskMarkersOnFile(file);
            file.deleteMarkers(getErrorMarkerID(), true, IResource.DEPTH_INFINITE);
            file.deleteMarkers(getWarningMarkerID(), true, IResource.DEPTH_INFINITE);
            file.deleteMarkers(getInfoMarkerID(), true, IResource.DEPTH_INFINITE);
        } catch (CoreException e) {
        }
    }

    protected void clearMarkersOn(Collection<IFile> files) {
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
    protected void doRefresh(final IResource resource) {
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
    }

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

    /**
     * Derived classes may override to specify a unique name for a separate console;
     * otherwise, all IMP builders share a single console. @see IMP_BUILDER_CONSOLE.
     * @return the name of the console to use for diagnostic output, if any
     */
    protected String getConsoleName() {
        return CEYLON_CONSOLE;
    }

    /**
     * If you want your builder to have its own console, be sure to override
     * getConsoleName().
     * @return the console whose name is returned by getConsoleName()
     */
    protected MessageConsoleStream getConsoleStream() {
        return findConsole(getConsoleName()).newMessageStream();
    }

    /**
     * Find or create the console with the given name
     * @param consoleName
     */
    protected MessageConsole findConsole(String consoleName) {
        if (consoleName == null) {
            RuntimePlugin.getInstance().getLog().log(new Status(IStatus.ERROR, RuntimePlugin.IMP_RUNTIME, "BuilderBase.findConsole() called with a null console name; substituting default console"));
            consoleName= CEYLON_CONSOLE;
        }
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

    private void addTaskMarkers(IFile file, List<CommonToken> tokens) {
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

    private void clearTaskMarkersOnFile(IFile file) {
        try {
            file.deleteMarkers(IMarker.TASK, false, IResource.DEPTH_INFINITE);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
        super.clean(monitor);
        Collection<IFile> allSources= new ArrayList<IFile>();
        try {
            getProject().accept(new AllSourcesVisitor(allSources));
        } 
        catch (CoreException e) {
            getPlugin().getLog().log(new Status(IStatus.ERROR, getPlugin().getID(), e.getLocalizedMessage(), e));
        }
        clearMarkersOn(allSources);
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

    public static TypeChecker getProjectTypeChecker(IProject project) {
        return typeCheckers.get(project);
    }
    
    public static Collection<IProject> getProjects() {
        return typeCheckers.keySet();
    }

    /**
     * Read the IJavaProject classpath configuration and populate the ISourceProject's
     * build path accordingly.
     */
    public static List<IPath> getSourceFolders(ISourceProject project) {
        IJavaProject javaProject = JavaCore.create(project.getRawProject());
        if (javaProject.exists()) {
            try {
                List<IPath> sourceFolders = new ArrayList<IPath>();
                for (IClasspathEntry entry: javaProject.getResolvedClasspath(true)) {
                    IPath path = entry.getPath();
                    if (entry.getEntryKind()==IClasspathEntry.CPE_SOURCE)
                    {
                        /*for (IPath pattern: entry.getInclusionPatterns())
                        {
                            if (LANGUAGE.hasExtension(pattern.getFileExtension()))
                            {*/
                                sourceFolders.add(ModelFactory.createPathEntry(PathEntryType.SOURCE_FOLDER, path)
                                        .getPath());
                                /*break;
                            }
                        }*/
                    }
                }
                return sourceFolders;
            } 
            catch (JavaModelException e) {
                ErrorHandler.reportError(e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    private Collection<IPath> retrieveSourceFolders(
            ISourceProject sourceProject) {
        List<IPath> result = new ArrayList<IPath>();

        List<IPath> workspaceRelativeFolders = getSourceFolders(sourceProject);
        for (IPath folder : workspaceRelativeFolders) {
            result.add(folder.makeRelativeTo(sourceProject.getRawProject().getFullPath()));
        }
        return result;
    }

    private IPath retrieveSourceFolder(IFile file)
    {
        IPath path = file.getProjectRelativePath();
        if (path == null)
            return null;

        if (!LANGUAGE.hasExtension(path.getFileExtension()))
            return null;

        return retrieveSourceFolder(path);
    }

    private IPath retrieveSourceFolder(IFolder folder)
    {
        IPath path = folder.getProjectRelativePath();
        if (path == null)
            return null;
        
        return retrieveSourceFolder(path);
    }

    private IPath retrieveSourceFolder(IPath path) {
        ISourceProject sourceProject = getSourceProject();
        if (sourceProject != null) {
            Collection<IPath> sourceFolders = retrieveSourceFolders(sourceProject);
            for (IPath sourceFolder : sourceFolders) {
                if (sourceFolder.isPrefixOf(path)) {
                    return sourceFolder;
                }
            }
        }
        return null;
    }
    
    private Package retrievePackage(IResource folder) {
        IPath folderPath = folder.getProjectRelativePath();
        IPath sourceFolder = retrieveSourceFolder(folderPath);
        if (sourceFolder == null) {
            return null;
        }
        IPath relativeFolderPath = folderPath.makeRelativeTo(sourceFolder);
        String packageName = relativeFolderPath.toString().replace('/', '.');
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
            Package pkg = new Package();
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
    

    public static File getOutputDirectory(IJavaProject javaProject) {
        IProject project = javaProject.getProject();
        if (project.exists()) {
            try {
                return project.getFolder(javaProject.getOutputLocation().makeRelativeTo(project.getFullPath())).getRawLocation().toFile();
            } catch (JavaModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    };
}
