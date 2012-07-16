package com.redhat.ceylon.eclipse.code.parse;

import java.util.ArrayList;
import java.util.List;

class SimpleAnnotationTypeInfo implements IAnnotationTypeInfo {
    /*
     * For the management of associated problem-marker types
     */
    private List<String> problemMarkerTypes= new ArrayList<String>();

    public List<String> getProblemMarkerTypes() {
        return problemMarkerTypes;
    }

    public void addProblemMarkerType(String problemMarkerType) {
        problemMarkerTypes.add(problemMarkerType);
    }

    public void removeProblemMarkerType(String problemMarkerType) {
        problemMarkerTypes.remove(problemMarkerType);
    }
}