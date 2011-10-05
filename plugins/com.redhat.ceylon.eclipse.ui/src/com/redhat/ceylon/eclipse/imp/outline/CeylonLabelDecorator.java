package com.redhat.ceylon.eclipse.imp.outline;

import org.eclipse.imp.editor.ProblemsLabelDecorator;
import org.eclipse.imp.language.Language;

public class CeylonLabelDecorator extends ProblemsLabelDecorator {
    public CeylonLabelDecorator(Language lang) {
        super(lang);
    }
    @Override
    protected int computeAdornmentFlags(Object obj) {
        return 1;
    }
}