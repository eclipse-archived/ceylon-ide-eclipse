package com.redhat.ceylon.eclipse.imp.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.builder.BuilderBase;
import org.eclipse.imp.builder.MarkerCreator;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.runtime.PluginBase;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
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
public class CeylonBuilder extends BuilderBase {

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

    private final Collection<IFile> fSourcesToCompileTogether = new HashSet<IFile>();

    private final static Map<IProject, TypeChecker> typeCheckers = new HashMap<IProject, TypeChecker>();
    
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

    // returns workspace-relative paths
    
    private Collection<IPath> retrieveSourceFolders(
            ISourceProject sourceProject) {
        List<IPath> result = new ArrayList<IPath>();
        List<IPathEntry> buildPath = sourceProject.getBuildPath();
        for (IPathEntry buildPathEntry : buildPath) {
            if (buildPathEntry.getEntryType().equals(
                    PathEntryType.SOURCE_FOLDER)) {                
                result.add(buildPathEntry.getPath().makeRelativeTo(sourceProject.getRawProject().getFullPath()));
            }
        }
        return result;
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
        IPath path = file.getProjectRelativePath();
        if (path == null)
            return false;

        if (!LANGUAGE.hasExtension(path.getFileExtension()))
            return false;

        ISourceProject sourceProject = getSourceProject();
        if (sourceProject != null) {
            Collection<IPath> sourceFolders = retrieveSourceFolders(sourceProject);
            for (IPath sourceFolder : sourceFolders) {
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
        IProject project = getProject();
        ISourceProject sourceProject = getSourceProject();
        if (sourceProject == null) {
            return new IProject[0];
        }

        fSourcesToCompileTogether.clear();
        IProject[] result = super.build(kind, args, monitor);

        // parseController.getAnnotationTypeInfo().addProblemMarkerType(getErrorMarkerID());

        switch (kind) {
        case AUTO_BUILD:
        case INCREMENTAL_BUILD: 
            //Temporary for now!
            try {
                clean(monitor);
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
            //now fall through:
        case FULL_BUILD:
            System.out.println("Starting full build");
            
            monitor.beginTask("Full Ceylon Build", 4);
            monitor.subTask("collecting source files");
            
            typeCheckers.remove(project);
            Collection<IPath> sourceFolders = retrieveSourceFolders(sourceProject);
            TypeCheckerBuilder typeCheckerBuilder = new TypeCheckerBuilder()
                    .verbose(false);
            for (IPath sourceFolder : sourceFolders) {
                IFolderVirtualFile srcDir = new IFolderVirtualFile(project,
                        sourceFolder);
                typeCheckerBuilder.addSrcDirectory(srcDir);
            }
            
            monitor.worked(1);
            monitor.subTask("parsing source files");            
            
            TypeChecker typeChecker = typeCheckerBuilder.getTypeChecker();
            
            monitor.worked(1);
            monitor.subTask("compiling source files");
            
            // Parsing of ALL units in the source folder should have been done
            typeChecker.process();
            
            monitor.worked(1);
            monitor.subTask("collecting problems");
            
            typeCheckers.put(project, typeChecker);
            for (PhasedUnit phasedUnit : typeChecker.getPhasedUnits().getPhasedUnits())
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
            
            System.out.println("Finished full build");
            break;
        }
        return result;
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
    protected void clearMarkersOn(IFile file) {
        clearTaskMarkersOnFile(file);
        super.clearMarkersOn(file);
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
            } 
            else if (isOutputFolder(resource)) {
                return false;
            }
            return true;
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

    /**
     * Compile one ceylon file.
     */
    protected void compile(final IFile file, IProgressMonitor monitor) {
        fSourcesToCompileTogether.add(file);
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
}
