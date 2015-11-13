package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.java2ceylon.HoverJ2C;
import com.redhat.ceylon.ide.common.doc.DocGenerator;

public class hoverJ2C implements HoverJ2C {

    @Override
    public DocGenerator<IDocument> getDocGenerator() {
        return eclipseDocGenerator_.get_();
    }
    
    @Override
    public SourceInfoHover newEclipseDocGeneratorAsSourceInfoHover(CeylonEditor editor) {
        return new EclipseDocGenerator(editor);
    }
    
    @Override
    public DocGenerator<IDocument> newEclipseDocGenerator(CeylonEditor editor) {
        return new EclipseDocGenerator(editor);
    }
    
    @Override
    public IInformationControlCreator getInformationPresenterControlCreator(DocGenerator<IDocument> docGenerator) {
        if (docGenerator instanceof EclipseDocGenerator) {
            EclipseDocGenerator eclipseDocGenerator =
                    (EclipseDocGenerator) docGenerator;
            return eclipseDocGenerator.getInformationPresenterControlCreator();
        }
        else {
            return null;
        }
    }

}
