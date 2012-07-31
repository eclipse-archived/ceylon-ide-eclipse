package com.redhat.ceylon.eclipse.code.parse;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.makeRepositoryManager;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.LEXICAL_ANALYSIS;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.SYNTACTIC_ANALYSIS;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputDirectory;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getSourceFolders;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUserRepositories;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isModelAvailable;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.showWarnings;
import static org.eclipse.core.runtime.jobs.Job.getJobManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.compiler.loader.model.LazyPackage;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
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
import com.redhat.ceylon.eclipse.code.parse.CeylonParserScheduler.Stager;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.SourceCodeVirtualFile;
import com.redhat.ceylon.eclipse.core.vfs.TemporaryFile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EclipseLogger;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;

public class CeylonParseController {
    
    /**
     * The project containing the source being parsed by this 
     * IParseController. May be null if the source isn't actually 
     * part of an Eclipse project (e.g., a random bit of source
     * text living outside the workspace).
     */
	protected IProject project;

	/**
	 * The path to the file containing the source being parsed 
	 * by this {@link IParseController}.
	 */
	protected IPath filePath;

	/**
	 * The {@link MessageHandler} to which parser/compiler 
	 * messages are directed.
	 */
	protected MessageHandler handler;

	/**
	 * The current AST (if any) produced by the most recent 
	 * successful parse.<br>
	 * N.B.: "Successful" may mean that there were syntax 
	 * errors, but the parser managed to perform error recovery 
	 * and still produce an AST.
	 */
	protected Tree.CompilationUnit rootNode;

	/**
	 * The most-recently parsed source document. May be null 
	 * if this parse controller has never parsed an IDocument 
	 * before.
	 */
	protected IDocument document;

    private List<CommonToken> tokens;
    private TypeChecker typeChecker;
    
    /**
     * @param filePath		the project-relative path of file
     * @param project		the project that contains the file
     * @param handler		a message handler to receive error 
     *                      messages/warnings
     */
    public void initialize(IPath filePath, IProject project, 
    		MessageHandler handler) {
		this.project= project;
		this.filePath= filePath;
		this.handler= (MessageHandler) handler;
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
    
    public Tree.CompilationUnit parse(String contents, 
    		IProgressMonitor monitor, Stager stager) {
        
    	IPath path = this.filePath;
    	IProject project = this.project;
        IPath resolvedPath = path;
        if (path!=null) {
        	String ext = path.getFileExtension();
			if (ext==null || !ext.equals("ceylon")) {
        		return rootNode;
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
        }

        VirtualFile file = createSourceCodeVirtualFile(contents, path);
        
        if (isCanceling(monitor)) {
            return rootNode;
        }
        
        CeylonLexer lexer = new CeylonLexer(createInputStream(file));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        tokens = new ArrayList<CommonToken>(tokenStream.getTokens().size()); 
        tokens.addAll(tokenStream.getTokens());

        if (stager!=null) {
        	stager.afterStage(LEXICAL_ANALYSIS, monitor);
        }
        
        if (isCanceling(monitor)) {
            return rootNode;
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
        
        if (stager!=null) {
        	stager.afterStage(SYNTACTIC_ANALYSIS, monitor);
        }
        
        if (isCanceling(monitor)) {
            return rootNode;
        }
        
        VirtualFile srcDir = null;        
        if (project!=null) {
            srcDir = getSourceFolder(project, resolvedPath);
        }
        else if (path!=null) { //path==null in structured compare editor
        	srcDir = inferSrcDir(path);
        	project = findProject(path);
        }
        
        if (project!=null) {
            if (!isModelAvailable(project)) {
                return rootNode; // TypeChecking has not been performed
            }
            typeChecker = getProjectTypeChecker(project);
            //modelLoader = getProjectModelLoader(project);
        }
        
        if (isCanceling(monitor)) {
            return rootNode;
        }

        boolean showWarnings = showWarnings(project);
        
        if (typeChecker==null) {
        	try {
        		typeChecker = createTypeChecker(project, showWarnings);
    		} 
    		catch (CoreException e) {
    		    return rootNode; 
    		}
        }
        
        if (isCanceling(monitor)) {
            return rootNode;
        }

        PhasedUnit builtPhasedUnit = typeChecker.getPhasedUnit(file);
        cu = typecheck(path, file, cu, srcDir, showWarnings, builtPhasedUnit);
        
        collectErrors(cu);
        
        if (stager!=null) {
        	stager.afterStage(TYPE_ANALYSIS, monitor);
        }
        
        return rootNode;
    }

	private VirtualFile createSourceCodeVirtualFile(String contents, IPath path) {
        if (path == null) {
            return new SourceCodeVirtualFile(contents);
        } 
        else {
            return new SourceCodeVirtualFile(contents, path);
        }
	}

	private ANTLRInputStream createInputStream(VirtualFile file) {
        try {
            return new ANTLRInputStream(file.getInputStream());
        } 
        catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	private VirtualFile inferSrcDir(IPath path) {
		String pathString = path.toString();
		int lastBangIdx = pathString.lastIndexOf('!');
		if (lastBangIdx>0) {
			String srcArchivePath= pathString.substring(0, lastBangIdx);
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
            cu.visit(new ErrorVisitor(handler) {
                @Override
                public int getSeverity(Message error, boolean expected) {
                    return expected || error instanceof UsageWarning ? 
                    		IStatus.WARNING : IStatus.ERROR;
                }
            });      
        }
	}

	private Tree.CompilationUnit typecheck(IPath path, VirtualFile file,
			Tree.CompilationUnit cu, VirtualFile srcDir, 
			boolean showWarnings, PhasedUnit builtPhasedUnit) {
		PhasedUnit phasedUnit;
        if (isExternalPath(path) && builtPhasedUnit!=null) {
            // reuse the existing AST
        	cu = builtPhasedUnit.getCompilationUnit();
            rootNode = cu;
            phasedUnit = builtPhasedUnit;
            phasedUnit.analyseTypes();
			if (showWarnings) {
                phasedUnit.analyseUsage();
            }
        }
        else {
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
            
            phasedUnit = new PhasedUnit(file, srcDir, cu, pkg, 
                    typeChecker.getPhasedUnits().getModuleManager(), 
                    typeChecker.getContext(), tokens);  
            
            phasedUnit.validateTree();
            phasedUnit.visitSrcModulePhase();
            phasedUnit.visitRemainingModulePhase();
            phasedUnit.scanDeclarations();
            phasedUnit.scanTypeDeclarations();
            phasedUnit.validateRefinement();
            phasedUnit.analyseTypes();
            if (showWarnings) {
            	phasedUnit.analyseUsage();
            }
            phasedUnit.analyseFlow();
        }
        return cu;
	}

	private static TypeChecker createTypeChecker(IProject project, 
			boolean showWarnings) 
	        throws CoreException {
		TypeCheckerBuilder tcb = new TypeCheckerBuilder()
		        .verbose(false).usageWarnings(showWarnings);
		
		List<String> repos = new LinkedList<String>();
		if (project==null) {
			//I believe this case can only happen
			//in the structure compare editor, so
			//it does not really matter what repo
			//we use as long as it has the language
			//module
			repos.add(CeylonPlugin.getInstance().getCeylonRepository().getAbsolutePath());
		}
		else {
			for (String repo: getUserRepositories(project)) {
				repos.add(repo);
			}
			repos.add(getCeylonModulesOutputDirectory(project).getAbsolutePath());
		}
		tcb.setRepositoryManager(makeRepositoryManager(repos, null, 
				new EclipseLogger()));
		
		TypeChecker tc = tcb.getTypeChecker();
		tc.process();
		return tc;
	}

	private IProject findProject(IPath path) {
		
		//search for the project by iterating all 
		//projects in the workspace
		//TODO: should we use CeylonBuilder.getProjects()?
		for (IProject p: ResourcesPlugin.getWorkspace()
				.getRoot().getProjects()) {
		    if (p.getLocation().isPrefixOf(path)) {
		        return p;
		    }
		}
		
		//for files from external repos, search for
		//the repo by iterating all repos referenced
		//by all projects (yuck, this is fragile!!!)
		//TODO: this causes a bug where we see errors
		//      in the source file if we find the
		//      the wrong project here
		String pp = path.toPortableString();
		String osp = path.toOSString();
		int i = pp.indexOf('!'); //this is a bit fragile
		if (i>0) {
			String relPath = pp.substring(i+2);
			for (IProject p: getProjects()) {
				try {
					for (String repo: getUserRepositories(p)) {
						if (osp.startsWith(repo)) {
							PhasedUnit pu = getProjectTypeChecker(p)
									.getPhasedUnitFromRelativePath(relPath);
							if (pu!=null) {
								Module m = pu.getPackage().getModule();
								if (pp.contains(m.getNameAsString()+ '-' + m.getVersion())) { //this is a bit fragile
									return p;
								}
							}
						}
					}
				} 
				catch (CoreException e) {
					e.printStackTrace();
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
			Package sourcePackage = builtPhasedUnit.getPackage();
			if (sourcePackage instanceof LazyPackage) {
				JDTModelLoader modelLoader = getProjectModelLoader(getProject());
				if (modelLoader != null) {
					pkg = new LazyPackage(modelLoader);
				}
				else {
					pkg = new Package();
				}
			}
			else {
				pkg = new Package();
			}

			pkg.setName(sourcePackage.getName());
			pkg.setModule(sourcePackage.getModule());
			for (Unit pkgUnit : sourcePackage.getUnits()) {
				pkg.addUnit(pkgUnit);
			}
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
		return pkg;
	}

	public boolean isExternalPath(IPath path) {
        IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();
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
        for (IPath folderPath: getSourceFolders(project)) {
            if (folderPath.isPrefixOf(resolvedPath)) {
                return new IFolderVirtualFile(project, 
                        folderPath.makeRelativeTo(project.getFullPath()));
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
    
	public Tree.CompilationUnit parse(IDocument doc, 
			IProgressMonitor monitor, Stager stager) {
	    document= doc;
	    return parse(document.get(), monitor, stager);
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
}

