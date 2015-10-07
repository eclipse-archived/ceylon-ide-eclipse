import ceylon.interop.java {
    CeylonList
}

import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents
}
import com.redhat.ceylon.ide.common.correct {
    CreateEnumQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.ide.common.util {
    Indents
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    TextFileChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object createEnumQuickFix
        satisfies CreateEnumQuickFix<IProject, IDocument, InsertEdit, TextEdit, TextChange,EclipseQuickFixData> 
                & EclipseDocumentChanges {
    
    shared actual void consumeNewQuickFix(String desc, Icons image, Integer offset, TextChange change, EclipseQuickFixData data) {
        assert(is TextFileChange change);
        value img = switch(image)
        case (Icons.classes) CeylonResources.\iCLASS
        case (Icons.interfaces) CeylonResources.\iINTERFACE
        else CeylonResources.\iATTRIBUTE;
        
        data.proposals.add(CreateEnumProposal(null, desc, img, offset, change));
    }
    
    shared actual Integer getDocLength(IDocument doc) => doc.length;
    
    shared actual List<PhasedUnit> getUnits(IProject project) => CeylonList(CeylonBuilder.getUnits(project));
    
    shared actual Indents<IDocument> indents => eclipseIndents;
    
    shared actual TextChange newTextChange(PhasedUnit unit) {
        value file = CeylonBuilder.getFile(unit);
        return TextFileChange("Create Enumerated", file);
    }
}
