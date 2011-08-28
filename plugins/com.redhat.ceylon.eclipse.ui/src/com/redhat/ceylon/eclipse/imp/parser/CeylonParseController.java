package com.redhat.ceylon.eclipse.imp.parser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
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
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.AnalysisMessage;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonParseController extends ParseControllerBase implements IParseController {

  private static final class ErrorVisitor extends Visitor {
		private final IMessageHandler handler;

		private ErrorVisitor(IMessageHandler handler) {
			this.handler = handler;
		}

		@Override 
        public void visitAny(Node node) { 
          super.visitAny(node);
          for (Message error: node.getErrors()) 
          { 
            String errorMessage = error.getMessage();
            int startOffset=0;
            int endOffset=0;
            int startCol=0;
            int startLine=0;
            
            //Map<String, Object> attributes = new HashMap<String, Object>();
            if (error instanceof RecognitionError)
            {
              RecognitionError recognitionError = (RecognitionError) error;
              CommonToken token = (CommonToken) recognitionError.getRecognitionException().token;
              startOffset = token.getStartIndex();              
              endOffset = token.getStopIndex();
              startCol = token.getCharPositionInLine();
              startLine = token.getLine();
              //attributes.put(SEVERITY_KEY, ERROR);
            }
            if (error instanceof AnalysisMessage)
            {
              AnalysisMessage analysisMessage = (AnalysisMessage) error;
              Node errorNode = CeylonSourcePositionLocator.getIdentifyingNode(analysisMessage.getTreeNode());
              Token token = errorNode.getToken();
              startOffset = errorNode.getStartIndex();              
              endOffset = errorNode.getStopIndex();
              startCol = token.getCharPositionInLine();
              startLine = token.getLine();
              /*if (error instanceof AnalysisWarning) {
            	  attributes.put(SEVERITY_KEY, WARNING);
              }
              else {
            	  attributes.put(SEVERITY_KEY, ERROR);
              }*/
            }
            
              handler.handleSimpleMessage(errorMessage, startOffset, endOffset, 
            		  startCol, startCol, startLine, startLine/*, attributes*/);
          }
        }
	}

  public CeylonParseController() {
    super(CeylonPlugin.kLanguageID);
  }

  private final SimpleAnnotationTypeInfo simpleAnnotationTypeInfo= new SimpleAnnotationTypeInfo();
  private CeylonSourcePositionLocator sourcePositionLocator;
  private CeylonParser parser;
  private final Set<Integer> annotations = new HashSet<Integer>();

  /**
   * @param filePath		Project-relative path of file
   * @param project		Project that contains the file
   * @param handler		A message handler to receive error messages (or any others)
   * 						from the parser
   */
  public void initialize(IPath filePath, ISourceProject project, IMessageHandler handler) {
    super.initialize(filePath, project, handler);
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
			return new String[][] { { "'", "'" }, 
					{ "\"", "\"" }, { "`", "`" }, 
					{ "(", ")" }, { "[", "]" }, 
					{ "{", "}" }, { "/*", "*/" } };
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
//    VirtualFile srcDir;
// TODO : manage the case of a file along with the corresponding source directory    
    
    if (getPath() != null)
    {
      file = new SourceCodeVirtualFile(contents, getPath());      
    }
    else
    {
      file = new SourceCodeVirtualFile(contents);
    }
        
    TypeChecker typeChecker = new TypeCheckerBuilder().verbose(true).getTypeChecker();
    typeChecker.getPhasedUnits().parseUnit(file);
    
    // TODO manage canceling and parsing errors
    if (monitor.isCanceled())
      return fCurrentAst; // currentAst might (probably will) be inconsistent with the lex stream now

    PhasedUnit phasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(file);
    if (phasedUnit != null)
    {
      annotations.clear();
      AnnotationVisitor annotationVisitor = new AnnotationVisitor(annotations);
      phasedUnit.getCompilationUnit().visit(annotationVisitor);
      parser = phasedUnit.getParser();
      fCurrentAst = (Node) phasedUnit.getCompilationUnit();
    }
    
    final IMessageHandler handler = getHandler();
    typeChecker.process();
    if (handler != null)
    {
      Tree.CompilationUnit compilationUnit = (Tree.CompilationUnit) fCurrentAst; 
      compilationUnit.visit(new ErrorVisitor(handler));      
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
    int low = 0,
        high = tokens.size();
    while (high > low)
    {
        int mid = (high + low) / 2;
        CommonToken mid_element = (CommonToken) tokens.get(mid);
        if (offset >= mid_element.getStartIndex() &&
            offset <= mid_element.getStopIndex())
             return mid;
        else if (offset < mid_element.getStartIndex())
             high = mid;
        else low = mid + 1;
    }

    return -(low - 1);
  }

  public Iterator<Token> getTokenIterator(IRegion region) {
    int regionOffset= region.getOffset();
    int regionLength= region.getLength();
    int regionEnd= regionOffset + regionLength - 1;

    {
      CommonTokenStream stream = getTokenStream();
      if (stream!=null) {
      List<Token> tokens = stream.getTokens();
      int firstTokIdx= getTokenIndexAtCharacter(tokens, regionOffset);
      // getTokenIndexAtCharacter() answers the negative of the index of the
      // preceding token if the given offset is not actually within a token.
      if (firstTokIdx < 0) {
        firstTokIdx= -firstTokIdx + 1;
      }
      int lastTokIdx = getTokenIndexAtCharacter(tokens, regionEnd);
      if (lastTokIdx < 0) {
        lastTokIdx= -lastTokIdx + 1;
      }      
      List<Token> tokensToIterate = stream.getTokens(firstTokIdx, lastTokIdx);
      if (tokensToIterate!=null) return tokensToIterate.iterator();
      }
    }
    return null;
  }


  public CommonTokenStream getTokenStream() {
	CeylonParser parser = getParser();
	if (parser==null) return null;
	return (CommonTokenStream) parser.getTokenStream();
  }


  public CeylonParser getParser() {
    return parser;
  }
  
  public Set<Integer> getAnnotations() {
	return annotations;
  }
  
  public Tree.CompilationUnit getRootNode() {
	  return (Tree.CompilationUnit) getCurrentAst();
  }

}
