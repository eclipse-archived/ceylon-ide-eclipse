package ceylon.imp.tokenColorer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.Token;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.ITokenColorer;
import org.eclipse.imp.services.base.TokenColorerBase;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import ceylon.imp.parser.CeylonParseController;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;

public class CeylonTokenColorer extends TokenColorerBase implements ITokenColorer {
  protected final Set<String> keywords = new HashSet<String>(Arrays.asList("import", "class", "interface", "object", "given", "value", "assign", "void", "function", "of", "extends", "satisfies", "adapts", "abstracts",
      "in", "out", "return", "break", "continue", "throw", "if", "else", "switch", "case", "for", "while", "try", "catch", "finally", "this", "outer", "super", "is",
      "exists", "nonempty"));
  
  protected final TextAttribute identifierAttribute, typeAttribute, keywordAttribute, numberAttribute, 
  		annotationAttribute, annotationStringAttribute, commentAttribute, stringAttribute;

  public CeylonTokenColorer() {
    // TODO Define text attributes for the various token types that will have their text colored
    //
    // NOTE: Colors (i.e., instances of org.eclipse.swt.graphics.Color) are system resources
    // and are limited in number.  THEREFORE, it is good practice to reuse existing system Colors
    // or to allocate a fixed set of new Colors and reuse those.  If new Colors are instantiated
    // beyond the bounds of your system capacity then your Eclipse invocation may cease to function
    // properly or at all.
    Display display = Display.getDefault();
    identifierAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_BLACK), null, SWT.NORMAL);
    typeAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_BLUE), null, SWT.NORMAL);
    keywordAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_MAGENTA), null, SWT.BOLD);
    numberAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_BLUE), null, SWT.NORMAL);
    commentAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_GRAY), null, SWT.ITALIC);
    stringAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_BLUE), null, SWT.NORMAL);
    annotationStringAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_GRAY), null, SWT.NORMAL);
    annotationAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_CYAN), null, SWT.NORMAL);
  }

  public TextAttribute getColoring(IParseController controller, Object o) {
    if (o == null)
      return null;
    Token token = (Token) o;
    if (token.getType() == CeylonParser.EOF)
      return null;
    
    switch (token.getType()) {
      // START_HERE
      case CeylonParser.UIDENTIFIER:
        if ( inAnnotation(controller, token)) {
          return annotationAttribute;
        }
        else {
          return typeAttribute;
        }
      case CeylonParser.LIDENTIFIER:
        if ( inAnnotation(controller, token)) {
          return annotationAttribute;
        }
        else {
          return identifierAttribute;
        }
      case CeylonParser.FLOAT_LITERAL:
      case CeylonParser.NATURAL_LITERAL:
        return numberAttribute;
      case CeylonParser.STRING_LITERAL:
      case CeylonParser.CHAR_LITERAL:
      case CeylonParser.QUOTED_LITERAL:
        if ( inAnnotation(controller, token)) {
          return annotationStringAttribute;
        }
        else {
          return stringAttribute;
        }
      case CeylonParser.MULTI_COMMENT:
      case CeylonParser.LINE_COMMENT:
        return commentAttribute;
      default:
        if (keywords.contains(token.getText()))
          return keywordAttribute;
        return super.getColoring(controller, token);
    }
  }

  private boolean inAnnotation(IParseController controller, Token token) {
    return ((CeylonParseController) controller).getAnnotations()
                    .contains(token.getTokenIndex());
  }

  public IRegion calculateDamageExtent(IRegion seed) {
    return seed;
  }
}
