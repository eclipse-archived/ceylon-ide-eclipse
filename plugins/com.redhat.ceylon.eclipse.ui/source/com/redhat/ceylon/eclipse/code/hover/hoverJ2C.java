package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.ide.common.doc.DocGenerator;

public class hoverJ2C {

    public static DocGenerator<IDocument> getDocGenerator() {
        return eclipseDocGenerator_.get_();
    }
}
