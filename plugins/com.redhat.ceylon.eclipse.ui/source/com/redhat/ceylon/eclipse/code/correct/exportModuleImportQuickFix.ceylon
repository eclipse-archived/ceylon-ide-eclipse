import com.redhat.ceylon.eclipse.code.imports {
    eclipseModuleImportUtils
}
import com.redhat.ceylon.ide.common.correct {
    ExportModuleImportQuickFix
}
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}

import org.eclipse.core.resources {
    IProject,
    IFile
}
import org.eclipse.jface.text {
    Region,
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseExportModuleImportQuickFix
        satisfies ExportModuleImportQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseDocumentChanges & EclipseAbstractQuickFix {

    shared actual AbstractModuleImportUtil<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange> importUtil
            => eclipseModuleImportUtils;
    
    shared actual void newExportModuleImportProposal(EclipseQuickFixData data, Unit u, String desc, String name, String version) {
        data.proposals.add(object extends ExportModuleImportProposal(desc) {
            shared actual void apply(IDocument doc) {
                applyChanges(data.project, u, name);
            }
        });
    }
}
