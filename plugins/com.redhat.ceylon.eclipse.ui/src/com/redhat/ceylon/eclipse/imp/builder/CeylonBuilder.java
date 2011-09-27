package com.redhat.ceylon.eclipse.imp.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.builder.DependencyInfo;
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

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;
import com.redhat.ceylon.eclipse.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.vfs.ResourceVirtualFile;

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

    protected DependencyInfo fDependencyInfo;

    private final Collection<IFile> fChangedSources= new HashSet<IFile>();

    private final Collection<IFile> fSourcesToCompile= new HashSet<IFile>();

    private final Collection<IFile> fSourcesForDeps= new HashSet<IFile>();

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

            if (file.exists()) {
                if (isSourceFile(file) || isNonRootSourceFile(file)) {
                    fChangedSources.add(file);
                }
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

    protected DependencyInfo createDependencyInfo(IProject project) {
        return new DependencyInfo(project);
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
     * Collects compilation-unit dependencies for the given file, and records
     * them via calls to <code>fDependency.addDependency()</code>.
     */
    protected void collectDependencies(IFile file) {
        // String fromPath = file.getFullPath().toString();

        /*getPlugin().writeInfoMsg(
                "Collecting dependencies from ceylon file: " + file.getName());*/

        // TODO: implement dependency collector
        // E.g. for each dependency:
        // fDependencyInfo.addDependency(fromPath, uponPath);
    }

    /**
     * @return true iff this resource identifies the output folder
     */
    protected boolean isOutputFolder(IResource resource) {
        return resource.getFullPath().lastSegment().equals("target/classes");
    }

    @Override
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
        if (getPreferencesService().getProject() == null) {
            getPreferencesService().setProject(getProject());
        }

        fChangedSources.clear();
        fSourcesForDeps.clear();
        fSourcesToCompile.clear();

        boolean partialDeps= true;
        Collection<IFile> allSources= new ArrayList<IFile>();

        IProject project = getProject();
        ISourceProject sourceProject = getSourceProject();
        if (sourceProject == null) {
            return new IProject[0];
        }

        TypeChecker typeChecker = typeCheckers.get(project);

        List<PhasedUnit> builtPhasedUnits = null;
        if (fDependencyInfo == null || kind == FULL_BUILD || kind == CLEAN_BUILD || typeChecker == null) {
            fDependencyInfo= createDependencyInfo(getProject());
            try {
                getProject().accept(new AllSourcesVisitor(allSources));
            } catch (CoreException e) {
                getPlugin().getLog().log(new Status(IStatus.ERROR, getPlugin().getID(), e.getLocalizedMessage(), e));
            }
            fSourcesForDeps.addAll(allSources);
            clearMarkersOn(allSources);
            builtPhasedUnits = fullBuild(project, sourceProject, monitor);
            if (builtPhasedUnits== null)
                return new IProject[0];
            
            // Collect deps now, so we can compile everything necessary in the case where
            // we have no dep info yet (e.g. first build for this Eclipse invocation --
            // we don't persist the dep info yet) but we've been asked to do an auto build
            // b/c of workspace changes.
            
            collectDependencies(monitor);
        }
        else
        {
            try {
                collectSourcesToCompile(monitor);
                clearDependencyInfoForChangedFiles();
                fSourcesForDeps.addAll(fSourcesToCompile); // should be void ... ?
                fSourcesForDeps.addAll(fChangedSources);
                collectDependencies(monitor);
                clearMarkersOn(fSourcesToCompile);
                builtPhasedUnits = incrementalBuild(project, sourceProject, monitor);
                if (builtPhasedUnits== null)
                    return new IProject[0];
                // TODO : remettre à jour les dépendances ?
                
                if (getDiagPreference()) {
                    getConsoleStream().print(fDependencyInfo.toString());
                }
            } catch (CoreException e) {
                getPlugin().writeErrorMsg("Build failed: " + e.getMessage());
            }
        }
        
        for (PhasedUnit phasedUnit : builtPhasedUnits)
        {
            IFile file = getFile(phasedUnit);
            CommonTokenStream tokens = phasedUnit.getTokenStream();
            phasedUnit.getCompilationUnit()
                .visit(new ErrorVisitor(new MarkerCreator(file, PROBLEM_MARKER_ID)) {
                    @Override
                    public int getSeverity(Message error) {
                        return IMarker.SEVERITY_ERROR;
                    }
                });
            addTaskMarkers(file, tokens);
        }
        
        monitor.worked(1);
        monitor.done();
        return new IProject[] {project};
    }

    private List<PhasedUnit> incrementalBuild(IProject project,
        ISourceProject sourceProject, IProgressMonitor monitor) {
        TypeChecker typeChecker = typeCheckers.get(project);
        List<PhasedUnit> phasedUnitsToUpdate = new ArrayList<PhasedUnit>();
        for (IFile fileToUpdate : fSourcesToCompile)
        {
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
                // Editing a new file
                // Retrieve the target package from the file src-relative path
                //TODO: this is very fragile!
                String packageName = file.getPath().replaceFirst(srcDir.getPath() + "/", "")
                        .replace("/" + file.getName(), "").replace('/', '.');
                Modules modules = typeChecker.getContext().getModules();
                for (Module module : modules.getListOfModules()) {
                    for (Package p : module.getPackages()) {
                        if (p.getQualifiedNameString().equals(packageName)) {
                            pkg = p;
                            break;
                        }
                        if (pkg != null)
                            break;
                    }
                }
                if (pkg == null) {
                    // Add the default package
                    pkg = modules.getDefaultModule().getPackages().get(0);

                    // TODO : iterate through parents to get the sub-package 
                    // in which the package has been created, until we find the module
                    // Then the package can be created.
                    // However this should preferably be done on notification of the 
                    // resource creation
                    // A more global/systematic integration between the model element 
                    // (modules, packages, Units) and the IResourceModel should
                    // maybe be considered. But for now it is not required.
                }
            }
            
            phasedUnitsToUpdate.add(new PhasedUnit(file, srcDir, cu, pkg, 
                    typeChecker.getPhasedUnits().getModuleBuilder(), 
                    typeChecker.getContext(), tokenStream));
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate)
        {
            phasedUnit.validateTree();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate)
        {
            phasedUnit.scanDeclarations();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate)
        {
            phasedUnit.scanTypeDeclarations();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate)
        {
            phasedUnit.validateRefinement();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate)
        {
            phasedUnit.analyseTypes();
        }
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate)
        {
            phasedUnit.analyseFlow();
        }
        
        for (PhasedUnit phasedUnit : phasedUnitsToUpdate)
        {
            typeChecker.getPhasedUnits().addPhasedUnit(phasedUnit.getUnitFile(), phasedUnit);
        }
        
        return phasedUnitsToUpdate;
    }

    private List<PhasedUnit> fullBuild(IProject project, ISourceProject sourceProject, IProgressMonitor monitor) {
        System.out.println("Starting full build");
        
        monitor.beginTask("Full Ceylon Build", 4);
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
        
        monitor.worked(1);
        monitor.subTask("Collecting Ceylon problems for project " 
                    + project.getName());
        
        typeCheckers.put(project, typeChecker);
        
        System.out.println("Finished full build");
        return typeChecker.getPhasedUnits().getPhasedUnits();
    }

    protected void collectDependencies(IProgressMonitor monitor) {
        for(IFile srcFile: fSourcesForDeps) {
            collectDependencies(srcFile);
        }
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

    /**
     * Clears the dependency information maintained for all files marked as
     * having changed (i.e. in <code>fSourcesToCompile</code>).
     */
    private void clearDependencyInfoForChangedFiles() {
        for(Iterator<IFile> iter= fSourcesToCompile.iterator(); iter.hasNext(); ) {
            IFile srcFile= iter.next();

            fDependencyInfo.clearDependenciesOf(srcFile.getFullPath().toString());
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
     * Visits the project delta, if any, or the entire project, and determines the set
     * of files needed recompilation, and adds them to <code>fSourcesToCompile</code>.
     * @param monitor
     * @throws CoreException
     */
    private void collectSourcesToCompile(IProgressMonitor monitor) throws CoreException {
        IResourceDelta delta= getDelta(getProject());
        boolean emitDiags= getDiagPreference();

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
        collectChangeDependents();
        if (emitDiags) {
            getConsoleStream().println("All files to compile:");
            dumpSourceList(fSourcesToCompile);
        }
    }

    // TODO This really *shouldn't* be transitive; the real problem w/ the LPGBuilder is that it
    // doesn't account for transitive includes itself when computing its dependency info. That is,
    // when file A includes B includes C, A should be marked as a dependent of C.
    private void collectChangeDependents() {
        if (fChangedSources.size() == 0) return;

        Collection<IFile> changeDependents= new HashSet<IFile>();
        boolean emitDiags= getDiagPreference();

        changeDependents.addAll(fChangedSources);
        if (emitDiags) {
            getConsoleStream().println("Changed files:");
            dumpSourceList(changeDependents);
        }

        boolean changed= false;
        do {
            Collection<IFile> additions= new HashSet<IFile>();
            scanSourceList(changeDependents, additions);
            changed= changeDependents.addAll(additions);
        } while (changed);

        for(IFile f: changeDependents) {
            if (isSourceFile(f)) {
                fSourcesToCompile.add(f);
            }
        }
//      getConsoleStream().println("Changed files + dependents:");
//      dumpSourceList(fSourcesToCompile);
    }

    private boolean scanSourceList(Collection<IFile> srcList, Collection<IFile> changeDependents) {
        boolean result= false;
        for(Iterator<IFile> iter= srcList.iterator(); iter.hasNext(); ) {
            IFile srcFile= iter.next();
            Set<String> fileDependents= fDependencyInfo.getDependentsOf(srcFile.getFullPath().toString());

            if (fileDependents != null) {
                for(Iterator<String> iterator= fileDependents.iterator(); iterator.hasNext(); ) {
                    String depPath= iterator.next();
                    IFile depFile= getProject().getWorkspace().getRoot().getFile(new Path(depPath));

                    result= result || changeDependents.add(depFile);
                }
            }
        }
        return result;
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

    /**
     * Posts a dialog displaying the given message as soon as "conveniently possible".
     * This is not a synchronous call, since this method will get called from a
     * different thread than the UI thread, which is the only thread that can
     * post the dialog box.
     */
    protected void postMsgDialog(final String title, final String msg) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                Shell shell= RuntimePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
    
                MessageDialog.openInformation(shell, title, msg);
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
                Shell shell= RuntimePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
                boolean response= MessageDialog.openQuestion(shell, title, query);
    
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

    private void addTaskMarkers(IFile file, CommonTokenStream tokens) {
        //clearTaskMarkersOnFile(file);
        for (CommonToken token: (List<CommonToken>) tokens.getTokens()) {
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
                        for (IPath pattern: entry.getInclusionPatterns())
                        {
                            if (LANGUAGE.hasExtension(pattern.getFileExtension()))
                            {
                                sourceFolders.add(ModelFactory.createPathEntry(PathEntryType.SOURCE_FOLDER, path)
                                        .getPath());
                                break;
                            }                            
                        }
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
    
}
