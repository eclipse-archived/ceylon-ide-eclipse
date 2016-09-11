package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.MATCH_HIGHLIGHTING;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentTheme;
import static java.lang.Character.isDigit;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.themes.IThemeManager;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil;
import com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil.Task;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.ide.common.util.escaping_;

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
    public static String MATCHES = "matches";
    
    public static final String DOC_BACKGROUND = "documentationBackground";
    
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
        initColors(getCurrentTheme().getColorRegistry());
        IThemeManager themeManager = 
                getWorkbench().getThemeManager();
        themeManager.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (isColorChange(event)) {
                    refreshColors();
                }
            }

        });
    }

    public static boolean isColorChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        return property.startsWith(PLUGIN_ID + ".theme.color.") ||
                property.equals(IThemeManager.CHANGE_CURRENT_THEME);
    }
    
    public static Color getCurrentThemeColor(String key) {
        return color(getCurrentTheme().getColorRegistry(), key);
    }
    
    public static void refreshColors() {
        initColors(getCurrentTheme().getColorRegistry());
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
                if (escaping_.get_().isKeyword(token.getText())) {
                    return keywordAttribute;
                }
                else {
                    return null;
                }
        }
    }
    
    private static final Pattern path = 
            Pattern.compile("\\b(\\p{javaLowerCase}|_)(\\p{Digit}|\\p{javaLowerCase}|_)*(\\.(\\p{javaLowerCase}|_)(\\p{Digit}|\\p{javaLowerCase}|_)*)+\\b");
    
    public static class FontStyler extends Styler {
        private final Font font;
        private Styler styler;
        public FontStyler(Font font) {
            this.font = font;
        }
        public FontStyler(Font font, Styler styler) {
            this(font);
            this.styler = styler;
            
        }
        @Override
        public void applyStyles(TextStyle textStyle) {
            if (font!=null) {
                textStyle.font = font;
            }
            if (styler!=null) {
                styler.applyStyles(textStyle);
            }
        }
    }
    
    public static void styleFragment(
            StyledString result, 
            String codeFragment, 
            boolean qualifiedNameIsPath,
            String prefix, Font font) {
        qualifiedNameIsPath &=
                path.matcher(codeFragment).find();
        StringTokenizer tokens = 
                new StringTokenizer(codeFragment, 
                        qualifiedNameIsPath ? 
                                " |&()<>*+?,:{}[]@\"'!^/%~-=;" : 
                                " |&()<>*+?,:{}[]@\"'!^/%~-=;.", 
                        true);
        boolean version = false;
        boolean qualified = false;
        boolean matchHighlighting = prefix!=null; 
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            if (token.equals("\"") || token.equals("'")) {
                version = !version;
                append(result, token, font, STRING_STYLER);
            }
            else if (version) {
                append(result, token, font, STRING_STYLER);
            }
            else if (token.equals(".")) {
                qualified = true;
                append(result, token, font, null);
                continue;
            }
            else {
                int initial = token.codePointAt(0);
                if (initial=='\\' && token.length()>1) {
                    initial = token.codePointAt(1);
                }
                if (isDigit(initial)) {
                    append(result, token, font, NUM_STYLER);
                }
                else if (isUpperCase(initial)) {
                	if (matchHighlighting) {
                		styleIdentifier(
                		        result, prefix, token, 
                		        new FontStyler(font, 
                		                TYPE_ID_STYLER), 
                		        font);
                		matchHighlighting = false;
                	}
                	else {
                		append(result, token, font, 
                		        TYPE_ID_STYLER);
                	}
                }
                else if (isLowerCase(initial)) {
                    if (escaping_.get_().isKeyword(token)) {
                        append(result, token, font, 
                                KW_STYLER);
                    }
                    else if (token.contains(".")) {
                        append(result, token, font, 
                                PACKAGE_STYLER);
                    }
                    else if (qualified) {
                    	if (matchHighlighting) {
                    		styleIdentifier(
                    		        result, prefix, token, 
                    		        new FontStyler(font, 
                    		                MEMBER_STYLER), 
                    		        font);
                    		matchHighlighting = false;
                    	}
                    	else {
                    	    append(result, token, font, 
                    	            MEMBER_STYLER);
                    	}
                    }
                    else {
                    	if (matchHighlighting) {
                    		styleIdentifier(
                    		        result, prefix, token, 
                    		        new FontStyler(font, 
                    		                ID_STYLER), 
                    		        font);
                    		matchHighlighting = false;
                    	}
                    	else {
                    	    append(result, token, font, 
                    	            ID_STYLER);
                    	}
                    }
                }
                else {
                    append(result, token, font, null);
                }
            }
            qualified = false;
        }
    }

    private static void append(StyledString result, 
            String token, Font font, Styler styler) {
        result.append(token, new FontStyler(font, styler));
    }

    private static Font getBoldFont(Font font) {
    	FontData[] data = font.getFontData();
    	for (int i= 0; i<data.length; i++) {
    		data[i].setStyle(SWT.BOLD);
    	}
    	return new Font(font.getDevice(), data);
    }

    private static final Pattern HUMP = 
            Pattern.compile("(\\w|\\\\[iI])\\p{Ll}*");
    
	public static void styleIdentifier(StyledString result, 
			String prefix, String token, 
			Styler colorStyler, final Font font) {
	    final Styler fontAndColorStyler = 
	            new FontStyler(font, colorStyler);
        final String type = 
                CeylonPlugin.getPreferences()
                    .getString(MATCH_HIGHLIGHTING);
        if ("none".equals(type) || prefix.isEmpty()) {
            result.append(token, fontAndColorStyler);
            return;
        }
		Matcher m = HUMP.matcher(prefix);
        int i = 0;
		while (i<token.length() && m.find()) {
			String bit = m.group();
			//look for an exact-case match
			int loc = token.indexOf(bit, i);
			if (loc<0) {
			  //look for an inexact-case match
				loc = token.toLowerCase()
				        .indexOf(bit.toLowerCase(), i);
			}
			if (i==0 && loc>0 && !prefix.startsWith("*")) {
			    //first match must be at start of identifier
			    break;
			}
			if (loc<0) {
			    //roll back the highlighting already done
			    result.setStyle(result.length()-i, i, 
			            fontAndColorStyler);
			    break;
			}
			result.append(token.substring(i, loc), 
			        fontAndColorStyler);
			Styler matchStyler = new Styler() {
				@Override
				public void applyStyles(TextStyle textStyle) {
				    fontAndColorStyler.applyStyles(textStyle);
					switch (type) {
					case "underline": 
						textStyle.underline = true;
						break;
					case "bold":
						textStyle.font = getBoldFont(font);
						break;
					case "color": 
						textStyle.foreground = 
						    color(getCurrentTheme()
						            .getColorRegistry(), 
						            MATCHES);
						break;
					case "background": 
						textStyle.background = 
						    color(getCurrentTheme()
						            .getColorRegistry(), 
						            MATCHES);
						break;
					}					
				}
			};
			result.append(token.substring(loc, loc+bit.length()), 
			        matchStyler);
			i = loc + bit.length();
		}
		result.append(token.substring(i), fontAndColorStyler);
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

    public static StyledString styleProposal(
            String description, 
            boolean qualifiedNameIsPath, 
            boolean eliminateQuotes) {
        StyledString result = new StyledString();
        StringTokenizer tokens = 
                new StringTokenizer(description, 
                        "'\"", true);
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
                        styleFragment(result, token, 
                                qualifiedNameIsPath, null, 
                                CeylonPlugin.getCompletionFont());
                    }
                }
            }
            else {
                result.append(tok);
            }
        }
        return result;
    }

    public static StyledString styleProposal(
            String description, 
            boolean qualifiedNameIsPath) {
        return styleProposal(description, 
                qualifiedNameIsPath, true);
    }

    public static final Styler TYPE_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            TYPES);
        }
    };
    public static final Styler MEMBER_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            MEMBERS);
        }
    };
    public static final Styler TYPE_ID_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            TYPES);
        }
    };
    public static final Styler KW_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            KEYWORDS);
        }
    };
    public static final Styler STRING_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            STRINGS);
        }
    };
    public static final Styler NUM_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            NUMBERS);
        }
    };
    public static final Styler PACKAGE_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            PACKAGES);
        }
    };
    public static final Styler ARROW_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            OUTLINE_TYPES);
        }
    };
    public static final Styler ANN_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground =
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            ANNOTATIONS);
        }
    };
    public static final Styler ID_STYLER = 
            new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground = 
                    color(getCurrentTheme()
                            .getColorRegistry(), 
                            IDENTIFIERS);
        }
    };
        
}