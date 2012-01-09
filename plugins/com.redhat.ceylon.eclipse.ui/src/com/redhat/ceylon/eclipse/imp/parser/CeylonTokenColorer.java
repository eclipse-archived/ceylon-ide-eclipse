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
    
    public static final Set<String> keywords = new HashSet<String>(Arrays.asList("import", 
            "class", "interface", "object", "given", "value", "assign", "void", "function", "of", 
            "extends", "satisfies", "adapts", "abstracts", "in", "out", "return", "break", "continue", 
            "throw", "if", "else", "switch", "case", "for", "while", "try", "catch", "finally", 
            "this", "outer", "super", "is", "exists", "nonempty", "then"));
    
    private static TextAttribute identifierAttribute, typeAttribute, keywordAttribute, numberAttribute, 
    annotationAttribute, annotationStringAttribute, commentAttribute, stringAttribute, todoAttribute;
    
    private static TextAttribute text(ColorRegistry colorRegistry, String key, int style) {
        return new TextAttribute(color(colorRegistry, key), null, style); 
    }
    
    private static Color color(ColorRegistry colorRegistry, String key) {
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
        identifierAttribute = text(colorRegistry, "identifiers", SWT.NORMAL);
        typeAttribute = text(colorRegistry, "types", SWT.NORMAL);
        keywordAttribute = text(colorRegistry, "keywords", SWT.BOLD);
        numberAttribute = text(colorRegistry, "numbers", SWT.NORMAL);
        commentAttribute = text(colorRegistry, "comments", SWT.NORMAL);
        stringAttribute = text(colorRegistry, "strings", SWT.NORMAL);
        annotationStringAttribute = text(colorRegistry, "annotationstrings", SWT.NORMAL);
        annotationAttribute = text(colorRegistry, "annotations", SWT.NORMAL);
        todoAttribute = text(colorRegistry, "todos", SWT.NORMAL);
    }
    
    public TextAttribute getColoring(IParseController controller, Object o) {
        if (o == null) return null;
        Token token = (Token) o;
        CeylonParseController cpc = (CeylonParseController) controller;
        switch (token.getType()) {
            case CeylonParser.UIDENTIFIER:
                if (cpc.inAnnotationSpan(token)) {
                    return annotationAttribute;
                }
                else {
                    return typeAttribute;
                }
            case CeylonParser.LIDENTIFIER:
                if (cpc.inAnnotationSpan(token)) {
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
                if (cpc.inAnnotationSpan(token)) {
                    return annotationStringAttribute;
                }
                else {
                    return stringAttribute;
                }
            case CeylonParser.MULTI_COMMENT:
            case CeylonParser.LINE_COMMENT:
                if (CeylonBuilder.priority(token)>=0) {
                    return todoAttribute;
                }
                else {
                    return commentAttribute;
                }
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
