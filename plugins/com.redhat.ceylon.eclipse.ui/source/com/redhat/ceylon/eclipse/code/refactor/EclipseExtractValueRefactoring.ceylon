import com.redhat.ceylon.eclipse.code.correct {
    EclipseImportProposals,
    eclipseImportProposals,
    EclipseDocumentChanges
}
import com.redhat.ceylon.ide.common.refactoring {
    ExtractValueRefactoring
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}

import org.eclipse.core.resources {
    IFile
}
import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.jface.text {
    IRegion,
    IDocument,
    Region
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    Change,
    RefactoringStatus,
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.ui {
    IEditorPart
}

class EclipseExtractValueRefactoring(IEditorPart editorPart) extends EclipseAbstractRefactoring<TextChange>(editorPart)
        satisfies ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion>
        & EclipseDocumentChanges
        & EclipseExtractLinkedModeEnabled {
    shared actual EclipseImportProposals importProposals => eclipseImportProposals;
    shared actual variable String? internalNewName=null;
    shared actual variable Boolean canBeInferred=false;
    shared actual variable Boolean explicitType=false;
    shared actual variable Type? type=null;
    shared actual variable IRegion? typeRegion=null;
    shared actual variable IRegion? decRegion=null;
    shared actual variable IRegion? refRegion=null;
    shared actual variable Boolean getter=false;
    
    shared actual RefactoringStatus checkFinalConditions(IProgressMonitor? iProgressMonitor)
            => if (exists node=editorData?.node,
        exists mop=node.scope.getMemberOrParameter(node.unit, newName, null, false))
    then RefactoringStatus.createWarningStatus(
            "An existing declaration named '``newName``' already exists in the same scope")
    else RefactoringStatus();
    
    shared actual RefactoringStatus checkInitialConditions(IProgressMonitor? iProgressMonitor)
            => RefactoringStatus();
    
    shared actual Change createChange(IProgressMonitor? iProgressMonitor) {
        TextChange tc = newLocalChange();
        extractInFile(tc);
        return tc;
    }
    
    shared actual IRegion newRegion(Integer start, Integer length)
            => Region(start, length);
    
    shared actual void extractInFile(TextChange tfc)
            => build(tfc);
    
    shared actual String name => (super of ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion>).name;
}
