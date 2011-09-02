package com.redhat.ceylon.eclipse.imp.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.java.hosted.ProjectUtils;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParseController;
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
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonParseController extends ParseControllerBase implements IParseController {

  public CeylonParseController() {
    super(CeylonPlugin.kLanguageID);
  }

  private final SimpleAnnotationTypeInfo simpleAnnotationTypeInfo= new SimpleAnnotationTypeInfo();
  private CeylonSourcePositionLocator sourcePositionLocator;
  private CeylonParser parser;
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
    return new ILanguageSyntaxProperties() {
		
		@Override
		public boolean isWhitespace(char ch) {
			return ch==' '||ch=='\r'||ch=='\n'||ch=='\t'||ch=='\u000C';
		}
		
		@Override
		public boolean isIdentifierStart(char ch) {
			return Character.isJavaIdentifierStart(ch) && ch!='$';
		}
		
		@Override
		public boolean isIdentifierPart(char ch) {
			return Character.isJavaIdentifierPart(ch) && ch!='$';
		}
		
		@Override
		public String getSingleLineCommentPrefix() {
			return "//";
		}
		
		@Override
		public String getIdentifierConstituentChars() {
			return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
		}
		
		@Override
		public int[] getIdentifierComponents(String ident) {
			//this doesn't seem to actually work...
	        List<Integer> listResult= new LinkedList<Integer>();
	        for (int i=1; i<ident.length(); i++) {
	            if ((Character.isLowerCase(ident.charAt(i-1)) && 
	            		Character.isUpperCase(ident.charAt(i)) 
	            		    || ident.charAt(i) == '_')) {
	                listResult.add(i);
	            }
	        }
	        int[] result = new int[listResult.size()];
	        for (int i=0; i<listResult.size(); i++) {
	            result[i++] = listResult.get(i);
	        }
	        return result;
		}
		
		@Override
		public String[][] getFences() {
			return new String[][] { { "(", ")" }, { "[", "]" }, { "{", "}" }, };
		}
		
		@Override
		public IRegion getDoubleClickRegion(int offset, IParseController pc) {
			//this seems to be unnecessary ... default behavior is fine
		    /*CommonTokenStream stream = (CommonTokenStream) parser.getTokenStream();
		    if (stream!=null) {
		      List<Token> tokens = stream.getTokens();
		      int firstTokIdx= getTokenIndexAtCharacter(tokens, offset);
		      CommonToken token = (CommonToken) tokens.get(firstTokIdx);
		      return new Region(token.getStartIndex(), token.getText().length());
		    }
		    else {
		      return null;
		    }*/
			return null;
		}
		
		@Override
		public String getBlockCommentStart() {
			return "/*";
		}
		
		@Override
		public String getBlockCommentEnd() {
			return "*/";
		}
		
		@Override
		public String getBlockCommentContinuation() {
			return null;
		}
	};
  }

  public IAnnotationTypeInfo getAnnotationTypeInfo() {
    return simpleAnnotationTypeInfo;
  }

  public Object parse(String contents, IProgressMonitor monitor) {
    VirtualFile file;
    VirtualFile srcDir = null;

    IPath path = getPath();
    ISourceProject sourceProject = getProject();
    IProject project = sourceProject.getRawProject();
    
    IPath resolvedPath = path;    
    if (path != null)
    {
      if (sourceProject != null)
      {
        resolvedPath = sourceProject.resolvePath(path);
      } 

      file = new SourceCodeVirtualFile(contents, path);      
    }
    else {
      file = new SourceCodeVirtualFile(contents);
    }
    if (sourceProject != null) {
      List<IPathEntry> buildPath = sourceProject.getBuildPath();

      for (IPathEntry buildPathEntry : buildPath)
      {
        
        if (buildPathEntry.getEntryType().equals(PathEntryType.SOURCE_FOLDER) && buildPathEntry.getPath().isPrefixOf(resolvedPath))
        {
          srcDir = new IFolderVirtualFile(sourceProject.getRawProject(), buildPathEntry.getPath().removeFirstSegments(1));
          break;
        }
      }
    }
        
    typeChecker = CeylonBuilder.getProjectTypeChecker(project);
    PhasedUnit phasedUnit = null;
    PhasedUnits temporaryPhasedUnits = null;
    
    boolean newTypeCheckerCreated = false;
    if (srcDir==null || typeChecker == null) {
      typeChecker = new TypeCheckerBuilder().verbose(true).getTypeChecker();
      typeChecker.getPhasedUnits().parseUnit(file);      
      phasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(file);
      newTypeCheckerCreated = true;
    }
    else {        
        temporaryPhasedUnits = new PhasedUnits(typeChecker.getContext(), typeChecker.getPhasedUnits().getModuleBuilder());
        temporaryPhasedUnits.parseUnit(file, srcDir);         
        phasedUnit = temporaryPhasedUnits.getPhasedUnit(file);
        
    }
    
    if (phasedUnit!=null) {
      annotations.clear();
      phasedUnit.getCompilationUnit().visit(new AnnotationVisitor(annotations));
      parser = phasedUnit.getParser();
    
      // TODO manage canceling and parsing errors
      if (monitor.isCanceled()) return fCurrentAst; // currentAst might (probably will) be inconsistent with the lex stream now

      if (newTypeCheckerCreated) {
          typeChecker.process();
      }
      else {
          List<PhasedUnit> listOfUnits = temporaryPhasedUnits.getPhasedUnits();
          for (PhasedUnit pu : listOfUnits) {
              pu.validateTree();
              pu.scanDeclarations();
          }
          for (PhasedUnit pu : listOfUnits) {
              pu.scanTypeDeclarations();
          }
          for (PhasedUnit pu: listOfUnits) {
              pu.validateRefinement();
          }
          for (PhasedUnit pu : listOfUnits) {
              pu.analyseTypes();
          }
          for (PhasedUnit pu: listOfUnits) {
              pu.analyseFlow();
          }
          for (PhasedUnit pu: listOfUnits) {
              pu.display();
          }
      }
      fCurrentAst = phasedUnit.getCompilationUnit();

      if (monitor.isCanceled()) return fCurrentAst; // currentAst might (probably will) be inconsistent with the lex stream now

      final IMessageHandler handler = getHandler();
      if (handler!=null) {
        phasedUnit.getCompilationUnit().visit(new ErrorVisitor(handler));      
      }
    }
    
    return fCurrentAst;
  }

  //
  // This function returns the index of the token element
  // containing the offset specified. If such a token does
  // not exist, it returns the negation of the index of the 
  // element immediately preceding the offset.
  //
  private int getTokenIndexAtCharacter(List<Token> tokens, int offset) {
    //search using bisection
    int low = 0,
        high = tokens.size();
    while (high > low)
    {
        int mid = (high + low) / 2;
        CommonToken midElement = (CommonToken) tokens.get(mid);
        if (offset >= midElement.getStartIndex() &&
            offset <= midElement.getStopIndex())
             return mid;
        else if (offset < midElement.getStartIndex())
             high = mid;
        else low = mid + 1;
    }

    return -(low - 1);
  }

  public Iterator<Token> getTokenIterator(IRegion region) {
    int regionOffset = region.getOffset();
    int regionLength = region.getLength();
    if (regionLength<=0) {
    	return Collections.<Token>emptyList().iterator();
    }
    int regionEnd = regionOffset + regionLength - 1;
    CommonTokenStream stream = getTokenStream();
    if (stream==null) {
      return null;
    }
    else {
      List<Token> tokens = stream.getTokens();
      int firstTokIdx = getTokenIndexAtCharacter(tokens, regionOffset);
      // getTokenIndexAtCharacter() answers the negative of the index of the
      // preceding token if the given offset is not actually within a token.
      if (firstTokIdx < 0) {
        firstTokIdx= -firstTokIdx + 1;
      }
      int lastTokIdx = getTokenIndexAtCharacter(tokens, regionEnd);
      if (lastTokIdx < 0) {
        lastTokIdx= -lastTokIdx;
      }
      return tokens.subList(firstTokIdx, lastTokIdx+1).iterator();
    }
  }


  public CommonTokenStream getTokenStream() {
    CeylonParser parser = getParser();
    if (parser==null) {
      return null;
    }
    else {
      return (CommonTokenStream) parser.getTokenStream();
    }
  }


  public CeylonParser getParser() {
    return parser;
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
