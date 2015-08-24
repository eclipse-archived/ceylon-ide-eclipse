package com.redhat.ceylon.eclipse.util;

import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.ide.common.util.Indents;

public class utilJ2C {
    public static Indents<IDocument> indents() {
        return eclipseIndents_.get_();
    }
}
