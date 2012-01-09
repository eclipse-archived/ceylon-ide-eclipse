package com.redhat.ceylon.eclipse.imp.parser;

import static org.eclipse.jface.preference.PreferenceConverter.getColor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.Token;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.ITokenColorer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonTokenColorer /*extends TokenColorerBase*/ implements ITokenColorer {
    
    public static final Set<String> keywords = new HashSet<String>(Arrays.asList("import", 
            "class", "interface", "object", "given", "value", "assign", "void", "function", "of", 
            "extends", "satisfies", "adapts", "abstracts", "in", "out", "return", "break", "continue", 
            "throw", "if", "else", "switch", "case", "for", "while", "try", "catch", "finally", 
            "this", "outer", "super", "is", "exists", "nonempty", "then"));
    
    private static final Display display = Display.getDefault();
    private static final IPreferenceStore store = CeylonPlugin.getInstance().getPreferenceStore();

    private static TextAttribute identifierAttribute, typeAttribute, keywordAttribute, numberAttribute, 
    annotationAttribute, annotationStringAttribute, commentAttribute, stringAttribute, todoAttribute;
    
    private static TextAttribute text(String key, int style) {
        return new TextAttribute(new Color(display, getColor(store, "color."+key)), null, style); 
    }
    
    static {
        // NOTE: Colors (i.e., instances of org.eclipse.swt.graphics.Color) are system resources
        // and are limited in number.  THEREFORE, it is good practice to reuse existing system Colors
        // or to allocate a fixed set of new Colors and reuse those.  If new Colors are instantiated
        // beyond the bounds of your system capacity then your Eclipse invocation may cease to function
        // properly or at all.
        initColors();
        store.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().startsWith("color.")) {
                    initColors();
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().redraw();
                }
            }
        });
    }

    private static void initColors() {
        identifierAttribute = text("identifiers", SWT.NORMAL);
        typeAttribute = text("types", SWT.NORMAL);
        keywordAttribute = text("keywords", SWT.BOLD);
        numberAttribute = text("numbers", SWT.NORMAL);
        commentAttribute = text("comments", SWT.NORMAL);
        stringAttribute = text("strings", SWT.NORMAL);
        annotationStringAttribute = text("annotationstrings", SWT.NORMAL);
        annotationAttribute = text("annotations", SWT.NORMAL);
        todoAttribute = text("todos", SWT.NORMAL);
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
