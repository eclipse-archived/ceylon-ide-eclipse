package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static java.lang.Character.isDigit;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.List;
import java.util.StringTokenizer;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.themes.ITheme;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil.Task;

public class Highlights  {
    
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
    public static String ANNOTATION_STRINGS = "annotationstrings";
    public static String SEMIS = "semis";
    public static String BRACES = "braces";    
    public static String PACKAGES = "packages";    
    public static String MEMBERS = "members";    
    public static String OUTLINE_TYPES = "outlineTypes";    
    
    private static TextAttribute identifierAttribute, 
            typeAttribute, typeLiteralAttribute, 
            keywordAttribute, numberAttribute, 
            annotationAttribute, annotationStringAttribute, 
            commentAttribute, stringAttribute, todoAttribute, 
            semiAttribute, braceAttribute, packageAttribute, 
            interpAttribute, charAttribute, memberAttribute;
    
    private static TextAttribute text(ColorRegistry colorRegistry, 
            String key, int style) {
        return new TextAttribute(color(colorRegistry, key), 
                null, style); 
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
        return getWorkbench().getThemeManager().getCurrentTheme();
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
        annotationStringAttribute = text(colorRegistry, ANNOTATION_STRINGS, SWT.NORMAL);
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
                if (Escaping.KEYWORDS.contains(token.getText())) {
                    return keywordAttribute;
                }
                else {
                    return null;
                }
        }
    }

    public static void styleProposal(StyledString result, 
            String string, boolean qualifiedNameIsPath) {
        StringTokenizer tokens = 
                new StringTokenizer(string, 
                        qualifiedNameIsPath ? 
                                " |&()<>*+?,{}[]@\"" : 
                                " |&()<>*+?,{}[]@\".", 
                        true);
        boolean version = false;
        boolean qualified = false;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.equals("\"")) {
                version = !version;
                result.append(token, STRING_STYLER);
            }
            else if (version) {
                result.append(token, STRING_STYLER);
            }
            else if (token.equals(".")) {
                qualified = true;
                result.append(token);
                continue;
            }
            else {
                int initial = token.codePointAt(0);
                if (isDigit(initial)) {
                    result.append(token, NUM_STYLER);
                }
                else if (isUpperCase(initial)) {
                    result.append(token, TYPE_ID_STYLER);
                }
                else if (isLowerCase(initial)) {
                    if (Escaping.KEYWORDS.contains(token)) {
                        result.append(token, KW_STYLER);
                    }
                    else if (token.contains(".")) {
                        result.append(token, PACKAGE_STYLER);
                    }
                    else if (qualified) {
                        result.append(token, MEMBER_STYLER);
                    }
                    else {
                        result.append(token, ID_STYLER);
                    }
                }
                else {
                    result.append(token);
                }
            }
            qualified = false;
        }
    }

    public static void styleJavaType(StyledString result, 
            String string) {
        styleJavaType(result, string, TYPE_ID_STYLER);
    }

    public static void styleJavaType(StyledString result, 
            String string, Styler styler) {
        StringTokenizer tokens = 
                new StringTokenizer(string,
                        " <>,[]?.", true);
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.equals("int") || 
                token.equals("short") || 
                token.equals("byte") || 
                token.equals("long") || 
                token.equals("double") || 
                token.equals("float") || 
                token.equals("boolean") || 
                token.equals("char") ||
                token.equals("extends") ||
                token.equals("super")) {
                result.append(token, KW_STYLER);
            }
            else if (isJavaIdentifierStart(token.charAt(0))) {
                result.append(token, styler);
            }
            else {
                result.append(token);
            }
        }
    }

    public static StyledString styleProposal(String description, 
            boolean qualifiedNameIsPath, 
            boolean eliminateQuotes) {
        StyledString result = new StyledString();
        StringTokenizer tokens = 
                new StringTokenizer(description, "'\"", true);
        result.append(tokens.nextToken());
        while (tokens.hasMoreTokens()) {
            String tok = tokens.nextToken();
            if (tok.equals("\'")) {
                if (!eliminateQuotes) {
                    result.append(tok);
                }
                while (tokens.hasMoreTokens()) {
                    String token = tokens.nextToken();
                    if (token.equals("\'")) {
                        if (!eliminateQuotes) {
                            result.append(token);
                        }
                        break;
                    }
                    else if (token.equals("\"")) {
                        result.append(token, STRING_STYLER);
                        while (tokens.hasMoreTokens()) {
                            String quoted = tokens.nextToken();
                            result.append(quoted, STRING_STYLER);
                            if (quoted.equals("\"")) {
                                break;
                            }
                        }
                    }
                    else {
                        styleProposal(result, token, 
                                qualifiedNameIsPath);
                    }
                }
            }
            else {
                result.append(tok);
            }
        }
        return result;
    }

    public static StyledString styleProposal(String description, 
            boolean qualifiedNameIsPath) {
        return styleProposal(description, 
                qualifiedNameIsPath, true);
    }

    public static final Styler TYPE_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(colorRegistry, TYPES);
        }
    };
    public static final Styler MEMBER_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(colorRegistry, MEMBERS);
        }
    };
    public static final Styler TYPE_ID_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(colorRegistry, TYPES);
        }
    };
    public static final Styler KW_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(colorRegistry, KEYWORDS);
        }
    };
    public static final Styler STRING_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground=color(colorRegistry, STRINGS);
        }
    };
    public static final Styler NUM_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(colorRegistry, NUMBERS);
        }
    };
    public static final Styler PACKAGE_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(colorRegistry, PACKAGES);
        }
    };
    public static final Styler ARROW_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(colorRegistry, OUTLINE_TYPES);
        }
    };
    public static final Styler ANN_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(colorRegistry, ANNOTATIONS);
        }
    };
    public static final Styler ID_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground = 
                    color(colorRegistry, IDENTIFIERS);
        }
    };
    
    private static ColorRegistry colorRegistry = 
            getWorkbench().getThemeManager()
                          .getCurrentTheme()
                          .getColorRegistry();
        
}