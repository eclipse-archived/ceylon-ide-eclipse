package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.jface.text.IInformationControlCreator;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.hover.SourceInfoHover;
import com.redhat.ceylon.ide.common.doc.DocGenerator;

public interface HoverJ2C {

    DocGenerator getDocGenerator();

    SourceInfoHover newEclipseDocGeneratorAsSourceInfoHover(
            CeylonEditor editor);

    DocGenerator newEclipseDocGenerator(CeylonEditor editor);

    IInformationControlCreator getInformationPresenterControlCreator(
            DocGenerator docGenerator);

}