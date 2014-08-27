package com.redhat.ceylon.eclipse.code.parse;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;
import static com.redhat.ceylon.common.config.DefaultToolOptions.DEFAULTS_OFFLINE;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.FOR_OUTLINE;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.LEXICAL_ANALYSIS;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.SYNTACTIC_ANALYSIS;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.allClasspathContainersInitialized;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getInterpolatedCeylonSystemRepo;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getReferencedProjectsOutputRepositories;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getSourceFolders;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isModelTypeChecked;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.showWarnings;
import static com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager.isTheSourceArchiveProject;
import static com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager.toFullPath;
import static java.util.Arrays.asList;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.core.runtime.jobs.Job.getJobManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.compiler.java.loader.UnknownTypeCollector;
import com.redhat.ceylon.compiler.java.tools.NewlineFixingStringStream;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleValidator;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.eclipse.code.editor.AnnotationCreator;
import com.redhat.ceylon.eclipse.code.parse.CeylonParserScheduler.Stager;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.model.JDTModuleManager;
import com.redhat.ceylon.eclipse.core.typechecker.EditedPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.SourceCodeVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.TemporaryFile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;

public class CeylonParseController {
    
    /**
     * The project containing the source being parsed. May be 
     * null if the source isn't actually part of an Eclipse 
     * project (e.g., a random bit of source text living 
     * outside the workspace).
     */
    protected IProject project;

    /**
     * The path to the file containing the source being parsed.
     */
    protected IPath filePath;

    /**
     * The {@link AnnotationCreator} to which parser/compiler 
     * messages are directed.
     */
    protected AnnotationCreator handler;

    /**
     * The current AST (if any) produced by the most recent 
     * successful parse.<br>
     * N.B.: "Successful" may mean that there were syntax 
     * errors, but the parser managed to perform error recovery 
     * and still produce an AST.
     */
    protected Tree.CompilationUnit rootNode;

    /**
     * The EditedPhasedUnit associated with the most recent typecheck. 
     * May be null if this parse controller has never parsed or 
     * successfully typechecked anything.
     */
    private PhasedUnit phasedUnit;

    /**
     * The most-recently parsed source document. May be null 
     * if this parse controller has never parsed anything.
     */
    protected IDocument document;

    /**
     * The most-recently parsed token stream. May be null if 
     * this parse controller has never parsed anything.
     */
    private List<CommonToken> tokens;
    
    /**
     * The type checker associated with the most recent parse. 
     * May be null if this parse controller has never parsed 
     * anything.
     */
    private TypeChecker typeChecker;
    
    private Stage stage = Stage.NONE;
     
    /**
     * @param filePath        the project-relative path of file
     * @param project        the project that contains the file
     * @param handler        a message handler to receive error 
     *                      messages/warnings
     */
    public void initialize(IPath filePath, IProject project, 
            AnnotationCreator handler) {
        if (isTheSourceArchiveProject(project)) {
            IResource archiveEntry = project.findMember(filePath);
            if (archiveEntry instanceof IFile && archiveEntry.exists()) {
                IPath entryPath = toFullPath((IFile) archiveEntry);
                if (entryPath != null) {
                    filePath = entryPath;
                }
                project = null;
            }
        }
        this.project = project;
        this.filePath = filePath;
        this.handler = handler;
    }
    
    public AnnotationCreator getHandler() {
        return handler;
    }
        
    public Stage getStage() {
        return stage;
    }
    
    public PhasedUnit getPhasedUnit() {
        return phasedUnit;
    }
    
    private boolean isCanceling(IProgressMonitor monitor) {
        boolean isCanceling = false;
        if (monitor!=null) {
            isCanceling = monitor.isCanceled();
        }
        CeylonParserScheduler scheduler = getScheduler();
        if (scheduler!=null && scheduler.isCanceling()) {
            if (monitor!=null && !monitor.isCanceled()) {
                monitor.setCanceled(true);
            }
            isCanceling = true;
        }
        return isCanceling;
    }
    
    private CeylonParserScheduler getScheduler() {
        final Job parsingJob = getJobManager().currentJob();
        if (parsingJob instanceof CeylonParserScheduler) {
            return (CeylonParserScheduler) parsingJob;
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public void parse(String contents, 
            IProgressMonitor monitor, Stager stager) {
          
        IPath path = this.filePath;
        IProject project = this.project;
        IPath resolvedPath = path;
        IdePhasedUnit builtPhasedUnit = null;
        
        stage = Stage.NONE;
        
        if (path!=null) {
            String ext = path.getFileExtension();
            if (ext==null || !ext.equals("ceylon")) {
                return;
            }
            if (!path.isAbsolute() && project!=null) {
                resolvedPath = project.getFullPath().append(filePath);
                //TODO: do we need to add in the source folder???
                if (!project.getWorkspace().getRoot().exists(resolvedPath)) {
                    // file has been deleted for example
                    path = null;
                    project = null;
                }
            }
            
            if (path.isAbsolute()) {
                for (final IProject p: getProjects()) {
                    if (project != null && project != p) continue;
                    
                    JDTModuleManager moduleManager = (JDTModuleManager) 
                            getProjectTypeChecker(p).getPhasedUnits().getModuleManager();
                    JDTModule module = moduleManager.getArchiveModuleFromSourcePath(path);
                    if (module != null) {
                        builtPhasedUnit = module.getPhasedUnit(path);
                        if (builtPhasedUnit != null) {
                            project = p;
                            phasedUnit = builtPhasedUnit;
                            typeChecker = builtPhasedUnit.getTypeChecker();
                            rootNode = builtPhasedUnit.getCompilationUnit();
                            tokens = builtPhasedUnit.getTokens();
                            stage = SYNTACTIC_ANALYSIS;
                            if (stager!=null) {
                                stager.afterStage(LEXICAL_ANALYSIS, monitor);
                                stager.afterStage(SYNTACTIC_ANALYSIS, monitor);
                            }
                            
                            useTypechecker(phasedUnit, new Runnable() {
                                @Override
                                public void run() {
                                    phasedUnit.analyseTypes();
                                    if (showWarnings(p)) {
                                        phasedUnit.analyseUsage();
                                    }
                                }
                            });
                            
                            stage = TYPE_ANALYSIS;
                            if (stager!=null) {
                                stager.afterStage(FOR_OUTLINE, monitor);
                                stager.afterStage(TYPE_ANALYSIS, monitor);
                            }
                            return;
                        }
                    }
                }
            }
        }
        
        if (isCanceling(monitor)) {
            return;
        }
        
        NewlineFixingStringStream stream = 
                new NewlineFixingStringStream(contents);
        CeylonLexer lexer = new CeylonLexer(stream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        tokens = tokenStream.getTokens();
        
        stage = LEXICAL_ANALYSIS;
        if (stager!=null) {
            stager.afterStage(LEXICAL_ANALYSIS, monitor);
        }
        
        if (isCanceling(monitor)) {
            return;
        }
        
        CeylonParser parser = new CeylonParser(tokenStream);
        Tree.CompilationUnit cu;
        try {
            cu = parser.compilationUnit();
        }
        catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
        
        //TODO: make the AST available now, so that
        //      services like FoldingUpdater can
        //      make use of it in the callback
        rootNode = cu;
        
        collectLexAndParseErrors(lexer, parser, cu);
        
        stage = SYNTACTIC_ANALYSIS;
        if (stager!=null) {
            stager.afterStage(SYNTACTIC_ANALYSIS, monitor);
        }
        
        if (isCanceling(monitor)) {
            return;
        }
        
        VirtualFile srcDir = null;
        if (project!=null) {
            srcDir = getSourceFolder(project, resolvedPath);
        }
        else if (path!=null) { //path==null in structured compare editor
            srcDir = inferSrcDir(path);
            project = findProject(path);
        }
        
        if (!allClasspathContainersInitialized()) {
            // Ceylon projects have not been setup, so don't try to typecheck 
            stage = FOR_OUTLINE;
            if (stager!=null) {
                stager.afterStage(FOR_OUTLINE, monitor);
            }
            return; 
        }
        
        if (project!=null) {
            if (CeylonNature.isEnabled(project)) {
                if (!isModelTypeChecked(project)) {
                    // TypeChecking has not been performed
                    // on the main model, so don't do it 
                    // on the editor's tree
                    stage = FOR_OUTLINE;
                    if (stager!=null) {
                        stager.afterStage(FOR_OUTLINE, monitor);
                    }
                    return;
                }
                typeChecker = getProjectTypeChecker(project);
            }
        }

        boolean showWarnings = showWarnings(project);
        
        if (isCanceling(monitor)) {
            return;
        }

        if (typeChecker==null) {
            try {
                typeChecker = createTypeChecker(project, showWarnings);
            } 
            catch (CoreException e) {
                return; 
            }
        }
        
        if (isCanceling(monitor)) {
            return;
        }

        VirtualFile file = createSourceCodeVirtualFile(contents, path);
        builtPhasedUnit = (IdePhasedUnit) typeChecker.getPhasedUnit(file); // TODO : refactor !
        phasedUnit = typecheck(path, file, cu, srcDir, 
                showWarnings, builtPhasedUnit);
        rootNode = phasedUnit.getCompilationUnit();
        if (project != null && !CeylonNature.isEnabled(project)) {
            rootNode.visit(new Visitor() {
                @Override
                public void visitAny(Node node) {
                    super.visitAny(node);
                    List<Message> errorsToRemove = new ArrayList<>();
                    List<Message> nodeErrors = node.getErrors();
                    for (Message error: nodeErrors) {
                        
                        if (! (error instanceof RecognitionError)) {
                            errorsToRemove.add(error);
                        }
                    }
                    for (Message error: errorsToRemove) {
                        nodeErrors.remove(error);
                    }
                }
            });
        }
        collectErrors(rootNode);
        
        stage = TYPE_ANALYSIS;
        if (stager!=null) {
            stager.afterStage(FOR_OUTLINE, monitor);
            stager.afterStage(TYPE_ANALYSIS, monitor);
        }
        
    }

    private VirtualFile createSourceCodeVirtualFile(String contents, 
            IPath path) {
        if (path == null) {
            return new SourceCodeVirtualFile(contents);
        } 
        else {
            return new SourceCodeVirtualFile(contents, path);
        }
    }

    private VirtualFile inferSrcDir(IPath path) {
        String pathString = path.toString();
        int lastBangIdx = pathString.lastIndexOf('!');
        if (lastBangIdx>0) {
            String srcArchivePath = 
                    pathString.substring(0, lastBangIdx);
            return new TemporaryFile(srcArchivePath+'!');
        }
        else {
            return null;
        }
    }

    private void collectLexAndParseErrors(CeylonLexer lexer,
            CeylonParser parser, Tree.CompilationUnit cu) {
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
    }

    private void collectErrors(Tree.CompilationUnit cu) {
        if (handler!=null) {
            cu.visit(handler);      
        }
    }

    private PhasedUnit typecheck(IPath path, VirtualFile file,
            Tree.CompilationUnit cu, VirtualFile srcDir, 
            final boolean showWarnings, 
            final PhasedUnit builtPhasedUnit) {
        if (isExternalPath(path) && builtPhasedUnit!=null) {
            // reuse the existing AST
            phasedUnit = builtPhasedUnit;
            useTypechecker(builtPhasedUnit, new Runnable() {
                @Override
                public void run() {
                    builtPhasedUnit.analyseTypes();
                    if (showWarnings) {
                        builtPhasedUnit.analyseUsage();
                    }
                }
            });
            return builtPhasedUnit;
        }
        PhasedUnit phasedUnit;
        Package pkg;
        if (srcDir==null) {
            srcDir = new TemporaryFile();
            //put it in the default module
            pkg = typeChecker.getContext().getModules()
                    .getDefaultModule().getPackages().get(0);
        }
        else {
            pkg = getPackage(file, srcDir, builtPhasedUnit);
        }
        
        JDTModuleManager moduleManager = (JDTModuleManager) 
                typeChecker.getPhasedUnits().getModuleManager();
        if (builtPhasedUnit instanceof ProjectPhasedUnit) {
            phasedUnit = 
                    new EditedPhasedUnit(file, srcDir, cu, pkg, 
                            moduleManager, typeChecker, tokens, 
                            (ProjectPhasedUnit) builtPhasedUnit);  
        }
        else {
            phasedUnit = 
                    new EditedPhasedUnit(file, srcDir, cu, pkg, 
                    moduleManager, typeChecker, tokens, null);
            moduleManager.getModelLoader()
                         .setupSourceFileObjects(asList(phasedUnit));
        }
        
        final PhasedUnit phasedUnitToTypeCheck = phasedUnit;
        
        useTypechecker(phasedUnitToTypeCheck, new Runnable() {
            @Override
            public void run() {
                phasedUnitToTypeCheck.validateTree();
                phasedUnitToTypeCheck.visitSrcModulePhase();
                phasedUnitToTypeCheck.visitRemainingModulePhase();
                phasedUnitToTypeCheck.scanDeclarations();
                phasedUnitToTypeCheck.scanTypeDeclarations();
                phasedUnitToTypeCheck.validateRefinement();
                phasedUnitToTypeCheck.analyseTypes();
                if (showWarnings) {
                    phasedUnitToTypeCheck.analyseUsage();
                }
                phasedUnitToTypeCheck.analyseFlow();
                UnknownTypeCollector utc = new UnknownTypeCollector();
                phasedUnitToTypeCheck.getCompilationUnit().visit(utc);
            }
        });
        
        return phasedUnit;
    }

    private void useTypechecker(final PhasedUnit phasedUnitToTypeCheck,
            final Runnable typecheckSteps) {
        Job typecheckJob = 
                new Job("Typechecking the working copy of " + 
                        phasedUnitToTypeCheck.getPathRelativeToSrcDir()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                typecheckSteps.run();
                return Status.OK_STATUS;
            }
        };
        typecheckJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
        CeylonParserScheduler scheduler = getScheduler();
        if (scheduler != null) {
            typecheckJob.setPriority(scheduler.getPriority());
        }
        typecheckJob.setSystem(true);
        typecheckJob.schedule();
        try {
            typecheckJob.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static TypeChecker createTypeChecker(IProject project, 
            boolean showWarnings) 
            throws CoreException {
        final IJavaProject javaProject = 
                project != null ? JavaCore.create(project) : null;
        TypeCheckerBuilder tcb = new TypeCheckerBuilder()
                .verbose(false)
                .moduleManagerFactory(new ModuleManagerFactory(){
                    @Override
                    public ModuleManager createModuleManager(Context context) {
                        return new JDTModuleManager(context, javaProject);
                    }
                })
                .usageWarnings(showWarnings);
        
        File cwd;
        String systemRepo;
        boolean isOffline;
        if (project == null) {
            //I believe this case can only happen
            //in the structure compare editor, so
            //it does not really matter what repo
            //we use as long as it has the language
            //module.
            cwd = null;
            systemRepo = CeylonPlugin.getInstance()
                    .getCeylonRepository()
                    .getAbsolutePath();
            isOffline = CeylonConfig.get()
                    .getBoolOption(DEFAULTS_OFFLINE, false);
        }
        else {
            cwd = project.getLocation().toFile();
            systemRepo = getInterpolatedCeylonSystemRepo(project);
            isOffline = CeylonProjectConfig.get(project).isOffline();
        }
        
        RepositoryManager repositoryManager = repoManager()
                .offline(isOffline)
                .cwd(cwd)
                .systemRepo(systemRepo)
                .extraUserRepos(getReferencedProjectsOutputRepositories(project))
                .logger(new EclipseLogger())
                .isJDKIncluded(true)
                .buildManager();
        
        tcb.setRepositoryManager(repositoryManager);
        
        TypeChecker tc = tcb.getTypeChecker();
        PhasedUnits phasedUnits = tc.getPhasedUnits();

        JDTModuleManager moduleManager = 
                (JDTModuleManager) phasedUnits.getModuleManager();
        moduleManager.setTypeChecker(tc);
        Context context = tc.getContext();
        JDTModelLoader modelLoader = 
                (JDTModelLoader) moduleManager.getModelLoader();

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
            protected void executeExternalModulePhases() {}
        };
        
        moduleValidator.verifyModuleDependencyTree();
        tc.setPhasedUnitsOfDependencies(moduleValidator.getPhasedUnitsOfDependencies());
        
        List<PhasedUnit> dependencies = new ArrayList<PhasedUnit>();
        for (PhasedUnits dependencyPhasedUnits: tc.getPhasedUnitsOfDependencies()) {
            modelLoader.addSourceArchivePhasedUnits(dependencyPhasedUnits.getPhasedUnits());
            for (PhasedUnit phasedUnit: dependencyPhasedUnits.getPhasedUnits()) {
                dependencies.add(phasedUnit);
            }
        }

        for (PhasedUnit pu: dependencies) {
            pu.scanDeclarations();
        }
        for (PhasedUnit pu: dependencies) {
            pu.scanTypeDeclarations();
        }
        for (PhasedUnit pu: dependencies) {
            pu.validateRefinement(); //TODO: only needed for type hierarchy view in IDE!
        }
        
        return tc;
    }

    private IProject findProject(IPath path) {
        
        //search for the project by iterating all 
        //projects in the workspace
        //TODO: should we use CeylonBuilder.getProjects()?
        for (IProject p: getWorkspace().getRoot().getProjects()) {
            if (p.getLocation().isPrefixOf(path)) {
                return p;
            }
        }

        for (IProject p: getProjects()) {
            TypeChecker typeChecker = getProjectTypeChecker(p);
            for (PhasedUnit unit: typeChecker.getPhasedUnits().getPhasedUnits()) {
                if (unit.getUnit().getFullPath().equals(path)) {
                    return p;
                }
            }
            for (PhasedUnits units: typeChecker.getPhasedUnitsOfDependencies()) {
                for (PhasedUnit unit: units.getPhasedUnits()) {
                    if (unit.getUnit().getFullPath().equals(path.toString())) {
                        return p;
                    }
                }
            }
            
        }
        return null;
    }

    private Package getPackage(VirtualFile file, VirtualFile srcDir,
            PhasedUnit builtPhasedUnit) {
        Package pkg = null;
        if (builtPhasedUnit!=null) {
            // Editing an already built file
            pkg = builtPhasedUnit.getPackage();
        }
        else {
            // Editing a new file
            Modules modules = typeChecker.getContext().getModules();
            // Retrieve the target package from the file src-relative path
            //TODO: this is very fragile!
            String packageName = constructPackageName(file, srcDir);
            for (Module module: modules.getListOfModules()) {
                for (Package p: module.getPackages()) {
                    if (p.getQualifiedNameString().equals(packageName)) {
                        pkg = p;
                        break;
                    }
                }
                if (pkg!=null) {
                    break;
                }
            }
            if (pkg==null) {
                // assume the default package
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
        return new SingleSourceUnitPackage(pkg, file.getPath());
    }

    public boolean isExternalPath(IPath path) {
        IWorkspaceRoot wsRoot = getWorkspace().getRoot();
        // If the path is outside the workspace, or pointing inside the workspace, 
        // but is still file-system-absolute.
        return path!=null && path.isAbsolute() && 
                (wsRoot.getLocation().isPrefixOf(path) || 
                        !wsRoot.exists(path));
    }
    
    private String constructPackageName(VirtualFile file, VirtualFile srcDir) {
        return file.getPath().substring(srcDir.getPath().length()+1)
                .replace("/" + file.getName(), "").replace('/', '.');
    }
    
    private VirtualFile getSourceFolder(IProject project, IPath resolvedPath) {
        for (IFolder sourceFolder: getSourceFolders(project)) {
            if (sourceFolder.getFullPath().isPrefixOf(resolvedPath)) {
                return new IFolderVirtualFile(project, 
                        sourceFolder.getProjectRelativePath());
            }
        }
        return null;
    }
    
    public List<CommonToken> getTokens() {
        return tokens;
    }
    
    public TypeChecker getTypeChecker() {
        return typeChecker;
    }
    
    public Tree.CompilationUnit getRootNode() {
        return rootNode;
    }
    
    public void parse(IDocument doc, IProgressMonitor monitor, 
            Stager stager) {
        document = doc;
        parse(document.get(), monitor, stager);
    }

    public IProject getProject() {
        return project;
    }

    public IPath getPath() {
        return filePath;
    }

    public IDocument getDocument() {
        return document;
    }

    public void resetStage() {
        stage = Stage.NONE;
    }
    
}

