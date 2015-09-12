package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;

public class CeylonAnnotation extends Annotation 
        implements IQuickFixableAnnotation {
    
    /** 
     * Parent annotation ID
     */
    public static final String PARSE_ANNOTATION_TYPE = PLUGIN_ID + 
            ".parseAnnotation";

    /**
     * Annotation ID for a parser annotation w/ severity = error.
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_ERROR = PARSE_ANNOTATION_TYPE + 
            ".error";

    /**
     * Annotation ID for a parser annotation w/ severity = warning. 
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_WARNING = PARSE_ANNOTATION_TYPE + 
            ".warning";

    /**
     * Annotation ID for a parser annotation w/ severity = info. 
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_INFO = PARSE_ANNOTATION_TYPE + 
            ".info";

    private static int counter = 0;
    
    private Boolean fixable;
    private final int count;
    private final int code;
    private final int severity;

    public static boolean isParseAnnotation(Annotation a) {
        return a.getType().startsWith(PARSE_ANNOTATION_TYPE);
    }

    public CeylonAnnotation(String type, String text, 
            int code, int severity) {
        super(type, false, text);
        this.code = code;
        this.severity = severity;
        count = counter++;
    }
    
    public boolean isFixable() {
        return getSeverity() == SEVERITY_WARNING 
                || getId() > 0;
    }

    public int getId() {
        return code;
    }

    public int getSeverity() {
        return severity;
    }

    @Override
    public boolean isQuickFixable() 
            throws AssertionFailedException {
        return fixable;
    }
    
    @Override
    public boolean isQuickFixableStateSet() {
        return fixable!=null;
    }
    
    @Override
    public void setQuickFixable(boolean state) {
        fixable = state;
    }
    
    @Override
    public String toString() {
        return "{" + count + "}"; //"[" + getId() + "] " + getText();
    }
}