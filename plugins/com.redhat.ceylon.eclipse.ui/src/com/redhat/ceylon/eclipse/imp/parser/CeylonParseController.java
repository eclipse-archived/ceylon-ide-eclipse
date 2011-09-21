package com.redhat.ceylon.eclipse.imp.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.editor.quickfix.IAnnotation;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.ParseControllerBase;
import org.eclipse.imp.parser.SimpleAnnotationTypeInfo;
import org.eclipse.imp.services.IAnnotationTypeInfo;
import org.eclipse.imp.services.ILanguageSyntaxProperties;
import org.eclipse.jface.text.IRegion;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
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
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;
import com.redhat.ceylon.eclipse.vfs.IFolderVirtualFile;
import com.redhat.ceylon.eclipse.vfs.SourceCodeVirtualFile;

public class CeylonParseController extends ParseControllerBase {

  public CeylonParseController() {
    super(CeylonPlugin.LANGUAGE_ID);
  }

  private final SimpleAnnotationTypeInfo simpleAnnotationTypeInfo = new SimpleAnnotationTypeInfo();
  private CeylonSourcePositionLocator sourcePositionLocator;
  private CommonTokenStream tokenStream;
  private final Set<Integer> annotations = new HashSet<Integer>();
  private TypeChecker typeChecker;

  /**
   * @param filePath		Project-relative path of file
   * @param project		Project that contains the file
   * @param handler		A message handler to receive error messages (or any others)
   * 						from the parser
   */
  public void initialize(IPath filePath, ISourceProject project, IMessageHandler handler) {
    super.initialize(filePath, project, handler);
    simpleAnnotationTypeInfo.addProblemMarkerType(CeylonBuilder.PROBLEM_MARKER_ID);
  }

  public CeylonSourcePositionLocator getSourcePositionLocator() {
    if (sourcePositionLocator == null) {
        sourcePositionLocator= new CeylonSourcePositionLocator(this);
    }
    return sourcePositionLocator;
  }

  public ILanguageSyntaxProperties getSyntaxProperties() {
    return CeylonLanguageSyntaxProperties.INSTANCE;
  }

  public IAnnotationTypeInfo getAnnotationTypeInfo() {
    return simpleAnnotationTypeInfo;
  }

  public Object parse(String contents, IProgressMonitor monitor) {

    IPath path = getPath();
    ISourceProject sourceProject = getProject();
    IPath resolvedPath = path;    
    VirtualFile file;
    if (path!=null) {
      if (sourceProject!=null) {
        resolvedPath = sourceProject.resolvePath(path);
      }
      file = new SourceCodeVirtualFile(contents, path);      
    }
    else {
      file = new SourceCodeVirtualFile(contents);
    }
    
    if (file.getName().endsWith(".ceylon")) {

        System.out.println("Compiling " + file.getPath());

        ANTLRInputStream input;
        try {
            input = new ANTLRInputStream(file.getInputStream());
        } 
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        CeylonLexer lexer = new CeylonLexer(input);
        tokenStream = new CommonTokenStream(lexer);
        
        if (monitor.isCanceled()) return fCurrentAst;

        CeylonParser parser = new CeylonParser(tokenStream);
        Tree.CompilationUnit cu;
        try {
            cu = parser.compilationUnit();
        }
        catch (RecognitionException e) {
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
        
        annotations.clear();
        cu.visit(new AnnotationVisitor(annotations));
        
        fCurrentAst = cu;
        
        if (monitor.isCanceled()) return fCurrentAst; // currentAst might (probably will) be inconsistent with the lex stream now
        
        VirtualFile srcDir = null;

        if (sourceProject!=null) {
            IProject project = sourceProject.getRawProject();
            srcDir = getSourceFolder(sourceProject, resolvedPath);
            typeChecker = CeylonBuilder.getProjectTypeChecker(project);
            if (typeChecker == null) return fCurrentAst;
            //Disable for now, since it causes build to loop sometimes
            /*{
                //Note: this is now pretty much obsolete, since
                //      we now do a full build at at startup
                try {
                    project.build(IncrementalProjectBuilder.FULL_BUILD, 
                            CeylonBuilder.BUILDER_ID, null, 
                            new SubProgressMonitor(monitor, 0));
                } 
                catch (CoreException e) {
                    e.printStackTrace();
                }
            }*/
        }

        if (srcDir==null || typeChecker == null) {
            typeChecker = new TypeCheckerBuilder().verbose(true).getTypeChecker();
            typeChecker.process();
        }
        
        if (monitor.isCanceled()) return fCurrentAst;
        
        PhasedUnit builtPhasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(file);
        Package pkg = null;
        if (builtPhasedUnit!=null) {
            // Editing an already built file
            pkg = new TemporaryPackage(builtPhasedUnit.getPackage());
        }
        else {
            // Editing a new file
            Modules modules = typeChecker.getContext().getModules();
            if (srcDir==null) {
                srcDir = new VirtualFile() {
                    @Override
                    public boolean isFolder() {
                        return true;
                    }
                    @Override
                    public String getPath() {
                        return "";
                    }
                    @Override
                    public String getName() {
                        return "";
                    }
                    @Override
                    public InputStream getInputStream() {
                        return null;
                    }
                    @Override
                    public List<VirtualFile> getChildren() {
                        return Collections.emptyList();
                    }
                };
            }
            else {
                // Retrieve the target package from the file src-relative path
                //TODO: this is very fragile!
                String packageName = file.getPath().replaceFirst(srcDir.getPath() + "/", "")
                        .replace("/" + file.getName(), "").replace('/', '.');
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
        
        PhasedUnit phasedUnit = new PhasedUnit(file, srcDir, cu, pkg, 
                typeChecker.getPhasedUnits().getModuleBuilder(), 
                typeChecker.getContext(), tokenStream);

        phasedUnit.validateTree();
        phasedUnit.scanDeclarations(false);
        phasedUnit.scanTypeDeclarations();
        phasedUnit.validateRefinement();
        phasedUnit.analyseTypes();
        phasedUnit.analyseFlow();
        //phasedUnit.display();
                
        //fCurrentAst = cu;

        if (monitor.isCanceled()) return fCurrentAst; // currentAst might (probably will) be inconsistent with the lex stream now

        final IMessageHandler handler = getHandler();
        if (handler!=null) {
            cu.visit(new ErrorVisitor(handler) {
                @Override
                public int getSeverity(Message error) {
                    return IAnnotation.ERROR;
                }
            });      
        }

        System.out.println("Finished compiling " + file.getPath());
    }
    
    return fCurrentAst;
  }

  private VirtualFile getSourceFolder(ISourceProject project, IPath resolvedPath) {
    for (IPath folderPath: CeylonBuilder.getSourceFolders(project)) {
        if (folderPath.isPrefixOf(resolvedPath)) {
            return new IFolderVirtualFile(project.getRawProject(), 
                    folderPath.makeRelativeTo(project.getRawProject().getFullPath()));
        }
    }
    return null;
  }

  public Iterator<Token> getTokenIterator(IRegion region) {
    return CeylonSourcePositionLocator.getTokenIterator(getTokenStream(), region);
  }

  public CommonTokenStream getTokenStream() {
    return tokenStream;
  }


  public Context getContext() {
    return typeChecker.getContext();
  }
  
  public PhasedUnits getPhasedUnits() {
      return typeChecker.getPhasedUnits();
  }
  
  public Set<Integer> getAnnotations() {
	return annotations;
  }
  
  public Tree.CompilationUnit getRootNode() {
	  return (Tree.CompilationUnit) getCurrentAst();
  }

}
