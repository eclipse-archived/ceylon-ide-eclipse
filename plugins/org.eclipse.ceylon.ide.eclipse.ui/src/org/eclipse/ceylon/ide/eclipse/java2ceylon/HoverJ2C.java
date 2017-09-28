package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import org.eclipse.jface.text.IInformationControlCreator;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.hover.SourceInfoHover;
import org.eclipse.ceylon.ide.common.doc.DocGenerator;

public interface HoverJ2C {

    DocGenerator getDocGenerator();

    SourceInfoHover newEclipseDocGeneratorAsSourceInfoHover(
            CeylonEditor editor);

    DocGenerator newEclipseDocGenerator(CeylonEditor editor);

    IInformationControlCreator getInformationPresenterControlCreator(
            DocGenerator docGenerator);

}