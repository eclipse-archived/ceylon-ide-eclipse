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
import static com.redhat.ceylon.eclipse.core.model.modelJ2C.ceylonModel;
import static java.util.Arrays.asList;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.core.runtime.jobs.Job.getJobManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.compiler.java.loader.UnknownTypeCollector;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleValidator;
import com.redhat.ceylon.compiler.typechecker.analyzer.Warning;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
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
import com.redhat.ceylon.compiler.typechecker.util.NewlineFixingStringStream;
import com.redhat.ceylon.compiler.typechecker.util.WarningSuppressionVisitor;
import com.redhat.ceylon.eclipse.code.editor.AnnotationCreator;
import com.redhat.ceylon.eclipse.code.parse.CeylonParserScheduler.Stager;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.external.CeylonArchiveFileSystem;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.model.JDTModuleManager;
import com.redhat.ceylon.eclipse.core.model.JDTModuleSourceMapper;
import com.redhat.ceylon.eclipse.core.typechecker.EditedPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.vfs.SourceCodeVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.TemporaryFile;
import com.redhat.ceylon.eclipse.core.vfs.vfsJ2C;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;

public class CeylonParseController implements LocalAnalysisResult<IDocument> {
    
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
    
    public PhasedUnit getLastPhasedUnit() {
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
        PhasedUnit newPhasednit;
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
        JDTModuleSourceMapper moduleSourceMapper = (JDTModuleSourceMapper) 
                typeChecker.getPhasedUnits().getModuleSourceMapper();
        if (builtPhasedUnit instanceof ProjectPhasedUnit) {
            newPhasednit = 
                    new EditedPhasedUnit(file, srcDir, cu, pkg, 
                            moduleManager, moduleSourceMapper, typeChecker, tokens, 
                            (ProjectPhasedUnit) builtPhasedUnit);  
        }
        else {
            newPhasednit = 
                    new EditedPhasedUnit(file, srcDir, cu, pkg, 
                    moduleManager, moduleSourceMapper, typeChecker, tokens, null);
            moduleManager.getModelLoader()
                         .setupSourceFileObjects(asList(newPhasednit));
        }
        
        final PhasedUnit phasedUnitToTypeCheck = newPhasednit;
        
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
                phasedUnitToTypeCheck.getCompilationUnit()
                    .visit(new WarningSuppressionVisitor<Warning>(Warning.class, 
                            CeylonBuilder.getSuppressedWarnings(project)));
            }
        });
        
        return newPhasednit;
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
        CeylonProject<IProject> ceylonProject = ceylonModel().getProject(project);
        
        final IJavaProject javaProject = 
                project != null ? JavaCore.create(project) : null;

        TypeCheckerBuilder tcb = new TypeCheckerBuilder()
                .verbose(false)
                .moduleManagerFactory(new ModuleManagerFactory(){
                    @Override
                    public ModuleManager createModuleManager(Context context) {
                        return new JDTModuleManager(context, javaProject);
                    }

                    @Override
                    public ModuleSourceMapper createModuleManagerUtil(Context context, ModuleManager moduleManager) {
                        return new JDTModuleSourceMapper(context, javaProject, (JDTModuleManager) moduleManager);
                    }
                })
                .usageWarnings(showWarnings);
        
        File cwd;
        String systemRepo;
        boolean offline;
        if (ceylonProject == null) {
            cwd = null;
            systemRepo = CeylonPlugin.getInstance()
                    .getCeylonRepository()
                    .getAbsolutePath();
            offline = CeylonConfig.get()
                    .getBoolOption(DEFAULTS_OFFLINE, false);
        }
        else {
            cwd = project.getLocation().toFile();
            systemRepo = getInterpolatedCeylonSystemRepo(project);
            offline = ceylonModel().getProject(project).getConfiguration().getOffline();
        }
        
        RepositoryManager repositoryManager = repoManager()
                .offline(offline)
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
        JDTModuleSourceMapper moduleSourceMapper = 
                (JDTModuleSourceMapper) phasedUnits.getModuleSourceMapper();
        moduleSourceMapper.setTypeChecker(tc);
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
        for (PhasedUnit pu: dependencies) {
            pu.analyseTypes(); //TODO: Needed to have the right values in the Value.trans field (set in Expression visitor)
                                // which in turn is important for debugging !
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
            if (path.toString().contains(CeylonArchiveFileSystem.JAR_SUFFIX)) {
                for (Module m : CeylonBuilder.getProjectExternalModules(p)) {
                    if (m instanceof JDTModule) {
                        String sourceArchivePath = ((JDTModule) m).getSourceArchivePath();
                        if (sourceArchivePath != null 
                                && path.toOSString().contains(sourceArchivePath)) {
                            return p;
                        }
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
                return vfsJ2C.createVirtualFolder(project, 
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
    
    /*
     * returns the last fully-typechecked AST.
     * It might be different from the most recently parsed AST,
     * and thus inconsistent with the source code.
     */
    public Tree.CompilationUnit getLastCompilationUnit() {
        return phasedUnit != null ? phasedUnit.getCompilationUnit() : null;
    }

    /*
     * returns the most recently parsed AST.
     *
     * Be careful it can be returned *before* the typechecking or
     * *during* the typechecking (in case of cancellation)
     * So *never* use this from places that need a fully typechecked AST
     * (with model elements such as declarations or units).
     */
    public Tree.CompilationUnit getParsedRootNode() {
        return rootNode;
    }
    
    /*
     * returns the last parsed AST only if it is fully typechecked.
     *
     * Returns null if the last parsed AST could not be fully
     * typechecked
     * (cancellation, source model read lock not obtained,
     * running typechecking ...)
     */
    public Tree.CompilationUnit getTypecheckedRootNode() {
        Tree.CompilationUnit lastRootNode = getLastCompilationUnit();
        if (lastRootNode == rootNode) {
            return lastRootNode;
        }
        return null;
    }

    /*
     * returns true is the the last AST was parsed *and* typechecked
     * until the end (=> stage == TYPE_ANALYSIS)
     */
    public PhasedUnit parseAndTypecheck(
                    IDocument doc,
                    long waitForModelInSeconds,
                    final IProgressMonitor monitor,
                    final Stager stager) {
      document = doc;
      final String contents = doc.get();
      IPath path = this.filePath;
      IProject project = this.project;
      IPath resolvedPath = path;

      stage = Stage.NONE;

      if (path!=null) {
          String ext = path.getFileExtension();
          if (ext==null || !ext.equals("ceylon")) {
              return null;
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
              IdePhasedUnit builtPhasedUnit = null;
              for (IProject p: new ArrayList<IProject>(getProjects())) {
                  if (project != null && project != p) continue;

                  JDTModuleManager moduleManager = (JDTModuleManager)
                          getProjectTypeChecker(p).getPhasedUnits().getModuleManager();
                  JDTModule module = moduleManager.getArchiveModuleFromSourcePath(path);
                  if (module != null) {
                      builtPhasedUnit = module.getPhasedUnit(path);
                      if (builtPhasedUnit != null) {
                          if (project == p) {
                              break;
                          }
                          if (module.isCeylonBinaryArchive()) {
                              project = p;
                              break;
                          }
                      }
                  }
              }
              if (builtPhasedUnit != null) {
                  phasedUnit = builtPhasedUnit;
                  typeChecker = builtPhasedUnit.getTypeChecker();
                  rootNode = builtPhasedUnit.getCompilationUnit();
                  tokens = builtPhasedUnit.getTokens();
                  stage = SYNTACTIC_ANALYSIS;
                  if (stager!=null) {
                      stager.afterStage(LEXICAL_ANALYSIS, monitor);
                      stager.afterStage(SYNTACTIC_ANALYSIS, monitor);
                  }
                  final IProject finalProject = project;
                  useTypechecker(phasedUnit, new Runnable() {
                      @Override
                      public void run() {
                          phasedUnit.analyseTypes();
                          if (showWarnings(finalProject)) {
                              phasedUnit.analyseUsage();
                          }
                      }
                  });

                  stage = TYPE_ANALYSIS;
                  if (stager!=null) {
                      stager.afterStage(FOR_OUTLINE, monitor);
                      stager.afterStage(TYPE_ANALYSIS, monitor);
                  }
                  return phasedUnit;
              }
          }
      }

      if (isCanceling(monitor)) {
          return null;
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
          return null;
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
          return null;
      }

      VirtualFile srcDir = null;
      if (project!=null) {
          srcDir = getSourceFolder(project, resolvedPath);
      }
      else if (path!=null) { //path==null in structured compare editor
          srcDir = inferSrcDir(path);
          project = findProject(path);
      }

      if (!allClasspathContainersInitialized() ||
              CeylonNature.isEnabled(project)
              && !isModelTypeChecked(project)) {
          // Ceylon projects have not been setup, so don't try to typecheck
          //
          // or
          //
          // TypeChecking has not been performed
          // on the main model, so don't do it
          // on the editor's tree
          stage = FOR_OUTLINE;
          if (stager!=null) {
              stager.afterStage(FOR_OUTLINE, monitor);
          }
          return null;
      }

      final IProject finalProject = project;
      final IPath finalPath = path;
      final VirtualFile finalSrcDir = srcDir;
      try {
          return CeylonBuilder.doWithSourceModel(
                  project,
                  true,
                  waitForModelInSeconds,
                  new Callable<PhasedUnit>() {
                    @Override
                    public PhasedUnit call() throws Exception {
                        if (CeylonNature.isEnabled(finalProject)) {
                            typeChecker = getProjectTypeChecker(finalProject);
                        }

                        boolean showWarnings = showWarnings(finalProject);

                        if (isCanceling(monitor)) {
                            return null;
                        }

                        if (typeChecker==null) {
                            try {
                                typeChecker = createTypeChecker(finalProject, showWarnings);
                            }
                            catch (CoreException e) {
                                return null;
                            }
                        }

                        if (isCanceling(monitor)) {
                            return null;
                        }

                        VirtualFile file = createSourceCodeVirtualFile(contents, finalPath);
                        IdePhasedUnit builtPhasedUnit = (IdePhasedUnit) typeChecker.getPhasedUnit(file); // TODO : refactor !
                        phasedUnit = typecheck(finalPath, file, rootNode, finalSrcDir,
                                showWarnings, builtPhasedUnit);
                        rootNode = phasedUnit.getCompilationUnit();
                        if (finalProject != null && !CeylonNature.isEnabled(finalProject)) {
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

                        return phasedUnit;
                    }

                  });
          } catch(OperationCanceledException e) {
              if (monitor!= null) {
                  // Sets the current monitor to canceled,
                  // so that the scheduler will reschedule it later
                  monitor.setCanceled(true);
              }

              // Consider that the previous steps of the anaysis
              // are OK, and still notify the related model listeners
              stage = FOR_OUTLINE;
              if (stager!=null) {
                  stager.afterStage(FOR_OUTLINE, monitor);
              }
              return null;
          }
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

