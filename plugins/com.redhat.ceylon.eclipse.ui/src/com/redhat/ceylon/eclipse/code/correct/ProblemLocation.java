package com.redhat.ceylon.eclipse.code.correct;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.core.builder.MarkerCreator;

public class ProblemLocation implements Comparable<ProblemLocation> {
    
    private final int fId;
    // private final String[] fArguments;
    private final int fOffset;
    private final int fLength;
    // private final boolean fIsError;
    private final String fMarkerType;

    //IMarker marker;
    //CeylonAnnotation annotation;

    public ProblemLocation(int offset, int length, CeylonAnnotation annotation) {
        fId = annotation.getId();
        // fArguments= annotation.getArguments();
        fOffset = offset;
        fLength = length;
        // fIsError= annotation.getMarker().get
        fMarkerType = IMarker.PROBLEM;
        //this.annotation = annotation;
    }

    public ProblemLocation(IMarker marker) throws CoreException {
        fId = marker.getAttribute(MarkerCreator.ERROR_CODE_KEY, 0);
        // fArguments= annotation.getArguments();
        fOffset = marker.getAttribute(IMarker.CHAR_START, 0);
        fLength = marker.getAttribute(IMarker.CHAR_END, 0) - fOffset;
        // fIsError= annotation.getMarker().get
        fMarkerType = marker.getType();
        //this.marker = marker;
    }
    
    @Override
    public int compareTo(ProblemLocation that) {
        if (fOffset<that.fOffset) return -1;
        if (fOffset>that.fOffset) return 1;
        if (fId<that.fId) return -1;
        if (fId>that.fId) return 1;
        return 0;
    }

    public int getProblemId() {
        return fId;
    }

    public int getLength() {
        return fLength;
    }

    public int getOffset() {
        return fOffset;
    }

    public String getMarkerType() {
        return fMarkerType;
    }

}