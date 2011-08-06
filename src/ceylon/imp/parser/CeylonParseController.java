package ceylon.imp.parser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.ParseControllerBase;
import org.eclipse.imp.parser.SimpleAnnotationTypeInfo;
import org.eclipse.imp.services.IAnnotationTypeInfo;
import org.eclipse.imp.services.ILanguageSyntaxProperties;
import org.eclipse.jface.text.IRegion;

import ceylon.CeylonPlugin;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Node;

public class CeylonParseController extends ParseControllerBase implements IParseController {

  public CeylonParseController() {
    super(CeylonPlugin.kLanguageID);
  }

  private final SimpleAnnotationTypeInfo simpleAnnotationTypeInfo= new SimpleAnnotationTypeInfo();
  private ISourcePositionLocator sourcePositionLocator;
  private Node currentAst; 
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

//    parser.setMessageHandler(handler); // TODO ...
  }


  public ISourcePositionLocator getSourcePositionLocator() {
    if (sourcePositionLocator == null) {
        sourcePositionLocator= new CeylonSourcePositionLocator(this);
    }
    return sourcePositionLocator;
}

  public ILanguageSyntaxProperties getSyntaxProperties() {
    return null;
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
      return currentAst; // currentAst might (probably will) be inconsistent with the lex stream now

    PhasedUnit phasedUnit = typeChecker.getPhasedUnits().getPhasedUnit(file);
    if (phasedUnit != null)
    {
      AnnotationVisitor annotationVisitor = new AnnotationVisitor(annotations);
      phasedUnit.getCompilationUnit().visit(annotationVisitor);
      parser = phasedUnit.getParser();
      currentAst = (Node) phasedUnit.getCompilationUnit();
    }
    
    typeChecker.process();

    return currentAst;
  }

  //
  // This function returns the index of the token element
  // containing the offset specified. If such a token does
  // not exist, it returns the negation of the index of the 
  // element immediately preceding the offset.
  //
  private int getTokenIndexAtCharacter(List tokens, int offset) {
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

  public Iterator getTokenIterator(IRegion region) {
    int regionOffset= region.getOffset();
    int regionLength= region.getLength();
    int regionEnd= regionOffset + regionLength - 1;

    CommonTokenStream stream= (CommonTokenStream) getParser().getTokenStream();
    List tokens = stream.getTokens();
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
    return stream.getTokens(firstTokIdx, lastTokIdx).iterator();
  }


  public CeylonParser getParser() {
    return parser;
  }
  
  public Set<Integer> getAnnotations() {
	return annotations;
  }

}
