package com.redhat.ceylon.eclipse.code.html;

import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.convertToHTMLContent;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.toHex;
import static com.redhat.ceylon.eclipse.util.Highlights.CHARS;
import static com.redhat.ceylon.eclipse.util.Highlights.COMMENTS;
import static com.redhat.ceylon.eclipse.util.Highlights.IDENTIFIERS;
import static com.redhat.ceylon.eclipse.util.Highlights.KEYWORDS;
import static com.redhat.ceylon.eclipse.util.Highlights.NUMBERS;
import static com.redhat.ceylon.eclipse.util.Highlights.PACKAGES;
import static com.redhat.ceylon.eclipse.util.Highlights.STRINGS;
import static com.redhat.ceylon.eclipse.util.Highlights.TYPES;
import static com.redhat.ceylon.eclipse.util.Highlights.getCurrentThemeColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.antlr.runtime.Token;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.redhat.ceylon.compiler.java.tools.NewlineFixingStringStream;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Escaping;

public class HTML {

    /**
     * The style sheet (css).
     */
    private static String fgStyleSheet;

    public static URL fileUrl(String icon) {
        try {
            Bundle bundle = CeylonPlugin.getInstance().getBundle();
            return FileLocator.toFileURL(FileLocator.find(bundle, 
                    new Path("icons/").append(icon), null));
        } 
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the Javadoc hover style sheet with the current Javadoc font from the preferences.
     * @return the updated style sheet
     * @since 3.4
     */
    public static String getStyleSheet() {
        if (fgStyleSheet == null) {
            fgStyleSheet = loadStyleSheet();
        }
        final StringBuffer monospaceSize = new StringBuffer();
        final Font editorFont = CeylonEditor.getEditorFont();
        final Font hoverFont = CeylonEditor.getHoverFont();
        final FontData monospaceFontData = editorFont.getFontData()[0];
        final FontData textFontData = hoverFont.getFontData()[0];
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                GC gc = new GC(Display.getDefault().getActiveShell());
                Font font = gc.getFont();
                gc.setFont(hoverFont);
                int hoverFontHeight = gc.getFontMetrics().getAscent();
                gc.setFont(editorFont);
                int monospaceFontHeight = gc.getFontMetrics().getAscent();
                gc.setFont(font);
                int ratio = 100 * monospaceFontData.getHeight() * hoverFontHeight 
                        / monospaceFontHeight / textFontData.getHeight();
                monospaceSize.append(ratio).append("%");
            }
        });
        return HTMLPrinter.convertTopLevelFont(fgStyleSheet, textFontData)
                .replaceFirst("pre", "pre, tt, code")
                .replaceFirst("font-family: monospace;", 
                        "font-family: '" + 
                                monospaceFontData.getName() + "', monospace;" +
                        "font-size: " + monospaceSize + ";") + 
                "body { padding: 15px; }\n";
    }

    /**
     * Loads and returns the Javadoc hover style sheet.
     * @return the style sheet, or <code>null</code> if unable to load
     * @since 3.4
     */
    public static String loadStyleSheet() {
        Bundle bundle= Platform.getBundle(JavaPlugin.getPluginId());
        URL styleSheetURL= bundle.getEntry("/JavadocHoverStyleSheet.css"); 
        if (styleSheetURL != null) {
            BufferedReader reader= null;
            try {
                reader= new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
                StringBuilder buffer= new StringBuilder(1500);
                String line= reader.readLine();
                while (line != null) {
                    buffer.append(line);
                    buffer.append('\n');
                    line= reader.readLine();
                }
                return buffer.toString();
            } catch (IOException ex) {
                JavaPlugin.log(ex);
                return ""; 
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static void addImageAndLabel(StringBuilder buf, Referenceable model, String imageSrcPath, 
            int imageWidth, int imageHeight, String label, int labelLeft, int labelTop) {
        buf.append("<div style='word-wrap: break-word; position: relative; "); 
        
        if (imageSrcPath != null) {
            buf.append("margin-left: ").append(labelLeft).append("px; ");  
            buf.append("padding-top: ").append(labelTop).append("px; ");  
        }
    
        buf.append("'>"); 
        if (imageSrcPath != null) {
            if (model!=null) {
                buf.append("<a ").append(HTML.link(model)).append(">");  
            }
            addImage(buf, imageSrcPath, imageWidth, imageHeight,
                    labelLeft);
            if (model!=null) {
                buf.append("</a>"); 
            }
        }
        
        buf.append(label);
        
        buf.append("</div>"); 
    }

    public static void addImage(StringBuilder buf, String imageSrcPath, 
            int imageWidth, int imageHeight, int labelLeft) {
        StringBuilder imageStyle= new StringBuilder("border:none; position: absolute; "); 
        imageStyle.append("width: ").append(imageWidth).append("px; ");  
        imageStyle.append("height: ").append(imageHeight).append("px; ");  
        imageStyle.append("left: ").append(- labelLeft - 1).append("px; ");  
    
        // hack for broken transparent PNG support in IE 6, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=223900 :
        buf.append("<!--[if lte IE 6]><![if gte IE 5.5]>\n"); 
        //String tooltip= element == null ? "" : "alt='" + "Open Declaration" + "' ";   
        buf.append("<span ").append("style=\"").append(imageStyle)  
                .append("filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='")
                .append(imageSrcPath).append("')\"></span>\n");  
        buf.append("<![endif]><![endif]-->\n"); 
    
        buf.append("<!--[if !IE]>-->\n"); 
        buf.append("<img ").append("style='").append(imageStyle).append("' src='")
                .append(imageSrcPath).append("'/>\n");    
        buf.append("<!--<![endif]-->\n"); 
        buf.append("<!--[if gte IE 7]>\n"); 
        buf.append("<img ").append("style='").append(imageStyle).append("' src='")
                .append(imageSrcPath).append("'/>\n");    
        buf.append("<![endif]-->\n"); 
    }

    public static String getAddress(Referenceable model) {
        if (model==null) return null;
        return "dec:" + declink(model);
    }

    public static String link(Referenceable model) {
        return "href='doc:" + declink(model) + "'";
    }

    public static String declink(Referenceable model) {
        if (model instanceof Package) {
            Package p = (Package) model;
            return declink(p.getModule()) + ":" + p.getNameAsString();
        }
        if (model instanceof Module) {
            return  ((Module) model).getNameAsString();
        }
        else if (model instanceof Declaration) {
            String result = ":" + ((Declaration) model).getName();
            Scope container = ((Declaration) model).getContainer();
            if (container instanceof Referenceable) {
                return declink((Referenceable) container)
                        + result;
            }
            else {
                return result;
            }
        }
        else {
           return "";
        }
    }
    
    public static String keyword(String kw) {
        String kwc = toHex(getCurrentThemeColor(KEYWORDS));
        return "<span style='color:"+kwc+"'>"+ kw + "</span>";
    }

    public static String highlightLine(String line) {
        String kwc = toHex(getCurrentThemeColor(KEYWORDS));
        String tc = toHex(getCurrentThemeColor(TYPES));
        String ic = toHex(getCurrentThemeColor(IDENTIFIERS));
        String sc = toHex(getCurrentThemeColor(STRINGS));
        String nc = toHex(getCurrentThemeColor(NUMBERS));
        String cc = toHex(getCurrentThemeColor(CHARS));
        String pc = toHex(getCurrentThemeColor(PACKAGES));
        String lcc = toHex(getCurrentThemeColor(COMMENTS));
        CeylonLexer lexer = new CeylonLexer(new NewlineFixingStringStream(line));
        Token token;
        boolean inPackageName = false;
        StringBuilder result = new StringBuilder();
        while ((token=lexer.nextToken()).getType()!=CeylonLexer.EOF) {
            String s = convertToHTMLContent(token.getText());
            int type = token.getType();
            if (type!=CeylonLexer.LIDENTIFIER &&
                type!=CeylonLexer.MEMBER_OP) {
                inPackageName = false;
            }
            else if (inPackageName) {
                result.append("<span style='color:"+pc+"'>").append(s).append("</span>");
                continue;
            }
            switch (type) {
            case CeylonLexer.FLOAT_LITERAL:
            case CeylonLexer.NATURAL_LITERAL:
                result.append("<span style='color:"+nc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.CHAR_LITERAL:
                result.append("<span style='color:"+cc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.STRING_LITERAL:
            case CeylonLexer.STRING_START:
            case CeylonLexer.STRING_MID:
            case CeylonLexer.VERBATIM_STRING:
                result.append("<span style='color:"+sc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.UIDENTIFIER:
                result.append("<span style='color:"+tc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.LIDENTIFIER:
                result.append("<span style='color:"+ic+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.MULTI_COMMENT:
            case CeylonLexer.LINE_COMMENT:
                result.append("<span style='color:"+lcc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.IMPORT:
            case CeylonLexer.PACKAGE:
            case CeylonLexer.MODULE:
                inPackageName = true; //then fall through!
            default:
                if (Escaping.KEYWORDS.contains(s)) {
                    result.append("<span style='color:"+kwc+"'>").append(s).append("</span>");
                }
                else {
                    result.append(s);
                }
            }
        }
        return result.toString();
    }

}
