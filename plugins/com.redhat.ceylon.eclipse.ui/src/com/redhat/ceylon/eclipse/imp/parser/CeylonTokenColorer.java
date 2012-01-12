package com.redhat.ceylon.eclipse.imp.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.Token;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.ITokenColorer;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;

public class CeylonTokenColorer /*extends TokenColorerBase*/ implements ITokenColorer {
    
    public static String IDENTIFIERS = "identifiers";
    public static String TYPES = "types";
    public static String KEYWORDS = "keywords";
    public static String NUMBERS = "numbers";
    public static String STRINGS = "strings";
    public static String COMMENTS = "comments";
    public static String TODOS = "todos";
    public static String ANNOTATIONS = "annotations";
    public static String ANNOTATION__STRINGS = "annotationstrings";
    public static String SEMIS = "semis";
    public static String BRACES = "braces";    
    public static String PACKAGES = "packages";    
    
    public static final Set<String> keywords = new HashSet<String>(Arrays.asList("import", 
            "class", "interface", "object", "given", "value", "assign", "void", "function", "of", 
            "extends", "satisfies", "adapts", "abstracts", "in", "out", "return", "break", "continue", 
            "throw", "if", "else", "switch", "case", "for", "while", "try", "catch", "finally", 
            "this", "outer", "super", "is", "exists", "nonempty", "then"));
    
    private static TextAttribute identifierAttribute, typeAttribute, keywordAttribute, numberAttribute, 
    annotationAttribute, annotationStringAttribute, commentAttribute, stringAttribute, todoAttribute, 
    semiAttribute, braceAttribute, packageAttribute;
    
    private static TextAttribute text(ColorRegistry colorRegistry, String key, int style) {
        return new TextAttribute(color(colorRegistry, key), null, style); 
    }
    
    public static Color color(ColorRegistry colorRegistry, String key) {
        return colorRegistry.get("com.redhat.ceylon.eclipse.ui.theme.color." + key);
    }
    
    static {
        final ITheme currentTheme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();        
        initColors(currentTheme.getColorRegistry());
        currentTheme.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().startsWith("com.redhat.ceylon.eclipse.ui.theme.color.")) {
                    initColors(currentTheme.getColorRegistry());
                }
            }
        });
    }

    private static void initColors(ColorRegistry colorRegistry) {
        identifierAttribute = text(colorRegistry, IDENTIFIERS, SWT.NORMAL);
        typeAttribute = text(colorRegistry, TYPES, SWT.NORMAL);
        keywordAttribute = text(colorRegistry, KEYWORDS, SWT.BOLD);
        numberAttribute = text(colorRegistry, NUMBERS, SWT.NORMAL);
        commentAttribute = text(colorRegistry, COMMENTS, SWT.NORMAL);
        stringAttribute = text(colorRegistry, STRINGS, SWT.NORMAL);
        annotationStringAttribute = text(colorRegistry, ANNOTATION__STRINGS, SWT.NORMAL);
        annotationAttribute = text(colorRegistry, ANNOTATIONS, SWT.NORMAL);
        todoAttribute = text(colorRegistry, TODOS, SWT.NORMAL);
        semiAttribute = text(colorRegistry, SEMIS, SWT.NORMAL);
        braceAttribute = text(colorRegistry, BRACES, SWT.NORMAL);
        packageAttribute = text(colorRegistry, PACKAGES, SWT.NORMAL);
    }
    
    public TextAttribute getColoring(IParseController controller, Object o) {
        if (o == null) return null;
        Token token = (Token) o;
        switch (token.getType()) {
            case CeylonParser.PIDENTIFIER:
                return packageAttribute;
            case CeylonParser.AIDENTIFIER:
                return annotationAttribute;
            case CeylonParser.UIDENTIFIER:
                return typeAttribute;
            case CeylonParser.LIDENTIFIER:
                return identifierAttribute;
            case CeylonParser.FLOAT_LITERAL:
            case CeylonParser.NATURAL_LITERAL:
                return numberAttribute;
            case CeylonParser.ASTRING_LITERAL:
                return annotationStringAttribute;
            case CeylonParser.STRING_LITERAL:
            case CeylonParser.CHAR_LITERAL:
            case CeylonParser.QUOTED_LITERAL:
                return stringAttribute;
            case CeylonParser.MULTI_COMMENT:
            case CeylonParser.LINE_COMMENT:
                if (CeylonBuilder.priority(token)>=0) {
                    return todoAttribute;
                }
                else {
                    return commentAttribute;
                }
            case CeylonParser.SEMICOLON:
                return semiAttribute;
            case CeylonParser.LBRACE:
            case CeylonParser.RBRACE:
                return braceAttribute;
            case CeylonParser.EOF:
            case CeylonParser.WS:
                return null;
            default:
                if (keywords.contains(token.getText())) {
                    return keywordAttribute;
                }
                else {
                    return null;
                }
        }
    }
    
    public IRegion calculateDamageExtent(IRegion seed, IParseController ctlr) {
        return seed;
    }
    
}
