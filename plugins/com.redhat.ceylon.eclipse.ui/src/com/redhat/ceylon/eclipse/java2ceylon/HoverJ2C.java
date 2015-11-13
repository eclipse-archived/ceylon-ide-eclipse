package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.hover.SourceInfoHover;
import com.redhat.ceylon.ide.common.doc.DocGenerator;

public interface HoverJ2C {

    DocGenerator<IDocument> getDocGenerator();

    SourceInfoHover newEclipseDocGeneratorAsSourceInfoHover(
            CeylonEditor editor);

    DocGenerator<IDocument> newEclipseDocGenerator(CeylonEditor editor);

    IInformationControlCreator getInformationPresenterControlCreator(
            DocGenerator<IDocument> docGenerator);

}