package com.redhat.ceylon.eclipse.imp.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.builder.BuilderBase;
import org.eclipse.imp.builder.BuilderUtils;
import org.eclipse.imp.builder.MarkerCreator;
import org.eclipse.imp.builder.MarkerCreatorWithBatching;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.runtime.PluginBase;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.ErrorVisitor;
import com.redhat.ceylon.eclipse.imp.parser.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.imp.parser.ResourceVirtualFile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

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
    public static final String BUILDER_ID = CeylonPlugin.kPluginID
            + ".ceylon.imp.builder";

    /**
     * A marker ID that identifies problems detected by the builder
     */
    public static final String PROBLEM_MARKER_ID = CeylonPlugin.kPluginID
            + ".ceylon.imp.builder.problem";

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

    public static List<PhasedUnit> getUnits(String[] projects) {
        List<PhasedUnit> result = new ArrayList<PhasedUnit>();
        for (Map.Entry<IProject, TypeChecker> me: typeCheckers.entrySet()) {
            for (String pname: projects) {
                if (me.getKey().getName().equals(pname)) {
                    result.addAll(me.getValue().getPhasedUnits().getPhasedUnits());
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

        getPlugin().writeInfoMsg(
                "Collecting dependencies from ceylon file: " + file.getName());

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

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.imp.builder.BuilderBase#build(int, java.util.Map,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
        IProject project = getProject();
        ISourceProject sourceProject = getSourceProject();
        if (sourceProject == null) {
            return new IProject[0];
        }

        fSourcesToCompileTogether.clear();
        IProject[] result = super.build(kind, args, monitor);

        // TODO: Pick a version of the marker creator (or just go with this one)
        // MarkerCreator markerCreator = new MarkerCreator(file,
        // parseController, PROBLEM_MARKER_ID);
        // MarkerCreatorWithBatching markerCreator = new
        // MarkerCreatorWithBatching(file, parseController, this);

        // parseController.getAnnotationTypeInfo().addProblemMarkerType(getErrorMarkerID());

        switch (kind) {
        case FULL_BUILD:
            typeCheckers.remove(project);
            Collection<IPath> sourceFolders = retrieveSourceFolders(sourceProject);
            TypeCheckerBuilder typeCheckerBuilder = new TypeCheckerBuilder()
                    .verbose(true);
            for (IPath sourceFolder : sourceFolders) {
                IFolderVirtualFile srcDir = new IFolderVirtualFile(project,
                        sourceFolder);
                typeCheckerBuilder.addSrcDirectory(srcDir);
            }
            TypeChecker typeChecker = typeCheckerBuilder.getTypeChecker();
            // Parsing of ALL units in the source folder should have been done
            typeChecker.process();
            typeCheckers.put(project, typeChecker);
            for (PhasedUnit phasedUnit : typeChecker.getPhasedUnits().getPhasedUnits())
            {
                phasedUnit.getCompilationUnit()
                    .visit(new ErrorVisitor(new MarkerCreator(getFile(phasedUnit), 
                            PROBLEM_MARKER_ID)) {
                        @Override
                        public int getSeverity(Message error) {
                            return IMarker.SEVERITY_ERROR;
                        }
                    });      
            }
            break;
        }
        return result;
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

    /**
     * This is an example "compiler" implementation that simply uses the parse
     * controller to parse the given file, adding error markers to the file for
     * any parse errors that are reported.
     * 
     * Error markers are created by a special type of message handler (i.e.,
     * implementation of IMessageHandler) known as a MarkerCreator. The
     * MarkerCreator is passed to the parse controller. The parser reports its
     * error messages to the MarkerCreator, and the MarkerCreator puts
     * corresponding error markers on the file.
     * 
     * This example shows the use of two different types of marker creator: the
     * MarkerCreator base type and an the MarkerCreatorWithBatching subtype. In
     * MarkerCreator the error markers are added to the file one at a time, as
     * error messages are received. In MarkerCreatorWithBatching, the
     * information from each error message is cached; the corresponding error
     * markers are not created until the flush(..) method is called, at which
     * point all markers are created together. MarkerCreatorWithBatching is more
     * complicated internally and requires proper use of the flush(..) method,
     * but it may be more efficient at runtime for files that have many errors.
     * That is because a Workspace operation is required to add the error
     * markers to the file. There is one of these for each of the error markers
     * added in MarkerCreator, but only one for all of the markers in
     * MarkerCreatorWithBatching.
     * 
     * In this example we have declared a marker creator of each type but
     * commented out the batching version. The example should also execute
     * correctly if you comment out the base version and uncomment the batching
     * version, so it should be easy to experiment with them.
     * 
     * TODO remove or rename this method once an actual compiler is being
     * called.
     * 
     * @param file
     *            input source file
     * @param monitor
     *            progress monitor
     */
    protected void runParserForCompiler(final IFile file,
            IProgressMonitor monitor) {
        try {
            CeylonParseController parseController = new CeylonParseController();

            // Pick a version of the marker creator (or just go with this one)
            // MarkerCreator markerCreator = new MarkerCreator(file, parseController, PROBLEM_MARKER_ID);
            MarkerCreatorWithBatching markerCreator = new MarkerCreatorWithBatching(file, parseController, this);
            
            ISourceProject sourceProject = ModelFactory.open(file.getProject());
            parseController.initialize(file.getProjectRelativePath(), sourceProject, markerCreator);
            parseController.parse(BuilderUtils.getFileContents(file), monitor);
            
            markerCreator.flush(monitor);

          } 
          catch (ModelException e) {
            getPlugin().logException("Example builder returns without parsing due to a ModelException", e);
          }
    }
    
    public static TypeChecker getProjectTypeChecker(IProject project) {
        return typeCheckers.get(project);
    }
}
