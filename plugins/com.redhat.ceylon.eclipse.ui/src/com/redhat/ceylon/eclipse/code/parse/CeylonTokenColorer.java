package com.redhat.ceylon.eclipse.code.parse;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil.Task;

public class CeylonTokenColorer  {
    
    public static String IDENTIFIERS = "identifiers";
    public static String TYPES = "types";
    public static String TYPE_LITERALS = "typeLiterals";
    public static String KEYWORDS = "keywords";
    public static String NUMBERS = "numbers";
    public static String STRINGS = "strings";
    public static String CHARS = "characters";
    public static String INTERP = "interpolation";
    public static String COMMENTS = "comments";
    public static String TODOS = "todos";
    public static String ANNOTATIONS = "annotations";
    public static String ANNOTATION__STRINGS = "annotationstrings";
    public static String SEMIS = "semis";
    public static String BRACES = "braces";    
    public static String PACKAGES = "packages";    
    public static String MEMBERS = "members";    
    
    public static final Set<String> keywords = new LinkedHashSet<String>(Arrays.asList("import", "assert",
            "alias", "class", "interface", "object", "given", "value", "assign", "void", "function", 
            "assembly", "module", "package", "of", "extends", "satisfies", "abstracts", "in", "out", 
            "return", "break", "continue", "throw", "if", "else", "switch", "case", "for", "while", 
            "try", "catch", "finally", "this", "outer", "super", "is", "exists", "nonempty", "then",
            "dynamic", "new", "let"));
    
    private static TextAttribute identifierAttribute, typeAttribute, typeLiteralAttribute, keywordAttribute, numberAttribute, 
    annotationAttribute, annotationStringAttribute, commentAttribute, stringAttribute, todoAttribute, 
    semiAttribute, braceAttribute, packageAttribute, interpAttribute, charAttribute, memberAttribute;
    
    private static TextAttribute text(ColorRegistry colorRegistry, String key, int style) {
        return new TextAttribute(color(colorRegistry, key), null, style); 
    }
    
    public static Color color(ColorRegistry colorRegistry, String key) {
        return colorRegistry.get(PLUGIN_ID + ".theme.color." + key);
    }
    
    static {
        final ITheme currentTheme = getCurrentTheme();        
        initColors(currentTheme.getColorRegistry());
        currentTheme.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().startsWith(PLUGIN_ID + ".theme.color.")) {
                    initColors(currentTheme.getColorRegistry());
                }
            }
        });
    }

    public static ITheme getCurrentTheme() {
        return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
    }
    
    public static Color getCurrentThemeColor(String key) {
        return color(getCurrentTheme().getColorRegistry(), key);
    }

    private static void initColors(ColorRegistry colorRegistry) {
        identifierAttribute = text(colorRegistry, IDENTIFIERS, SWT.NORMAL);
        typeAttribute = text(colorRegistry, TYPES, SWT.NORMAL);
        typeLiteralAttribute = text(colorRegistry, TYPE_LITERALS, SWT.NORMAL);
        keywordAttribute = text(colorRegistry, KEYWORDS, SWT.BOLD);
        numberAttribute = text(colorRegistry, NUMBERS, SWT.NORMAL);
        commentAttribute = text(colorRegistry, COMMENTS, SWT.NORMAL);
        stringAttribute = text(colorRegistry, STRINGS, SWT.NORMAL);
        charAttribute = text(colorRegistry, CHARS, SWT.NORMAL);
        interpAttribute = text(colorRegistry, INTERP, SWT.NORMAL);
        annotationStringAttribute = text(colorRegistry, ANNOTATION__STRINGS, SWT.NORMAL);
        annotationAttribute = text(colorRegistry, ANNOTATIONS, SWT.NORMAL);
        todoAttribute = text(colorRegistry, TODOS, SWT.NORMAL);
        semiAttribute = text(colorRegistry, SEMIS, SWT.NORMAL);
        braceAttribute = text(colorRegistry, BRACES, SWT.NORMAL);
        packageAttribute = text(colorRegistry, PACKAGES, SWT.NORMAL);
        memberAttribute = text(colorRegistry, MEMBERS, SWT.NORMAL);
    }
    
    public static TextAttribute getInterpolationColoring() {
        return interpAttribute;
    }
    
    public static TextAttribute getMetaLiteralColoring() {
        return typeLiteralAttribute;
    }
    
    public static TextAttribute getMemberColoring() {
        return memberAttribute;
    }
    
    public static TextAttribute getColoring(CommonToken token) {
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
            case CeylonParser.AVERBATIM_STRING:
                return annotationStringAttribute;
            case CeylonParser.CHAR_LITERAL:
                return charAttribute;
            case CeylonParser.STRING_LITERAL:
            case CeylonParser.STRING_END:
            case CeylonParser.STRING_START:
            case CeylonParser.STRING_MID:
            case CeylonParser.VERBATIM_STRING:
                return stringAttribute;
            case CeylonParser.MULTI_COMMENT:
            case CeylonParser.LINE_COMMENT:
                List<Task> tasks = CeylonTaskUtil.getTasks(token);
                if (tasks != null && tasks.size() > 0) {
                    return todoAttribute;
                }
                else {
                    return commentAttribute;
                }
            case CeylonParser.BACKTICK:
                return typeLiteralAttribute;
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
        
}