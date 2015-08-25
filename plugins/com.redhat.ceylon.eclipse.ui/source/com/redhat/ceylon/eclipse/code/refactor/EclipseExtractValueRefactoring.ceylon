import com.redhat.ceylon.ide.common.refactoring {
    ExtractValueRefactoring
}
import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.ltk.core.refactoring {
    Change,
    RefactoringStatus,
    TextChange
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseImportProposals,
    eclipseImportProposals
}
import com.redhat.ceylon.eclipse.util {
    Indents,
    Nodes,
    EditorUtil
}
import org.eclipse.ui {
    IEditorPart
}
import org.eclipse.jface.text {
    IRegion,
    IDocument,
    Region
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}
import org.eclipse.core.resources {
    IFile
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
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

        tfc.edit = MultiTextEdit();
        value doc = EditorUtil.getDocument(tfc);

    shared actual String name => (super of ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion>).name;

        Integer il;
        if (!result.declarationsToImport.empty) {
            il = importProposals.applyImports(tfc, result.declarationsToImport, rootNode, doc);
        } else {
            il=0;
        }

        value text = result.declaration +
                Indents.getDefaultLineDelimiter(doc) +
                Indents.getIndent(result.statement, doc);

        if (exists st = result.statement) {
            Integer start = st.startIndex.intValue();

            tfc.addEdit(InsertEdit(start, text));
            tfc.addEdit(ReplaceEdit(node.startIndex.intValue(), node.distance.intValue(), newName));
            typeRegion = Region(start+il, result.typeDec.size);
            decRegion = Region(start+il+result.typeDec.size+1, newName.size);
            refRegion = Region(node.startIndex.intValue()+il+text.size, newName.size);

        }
    }

    shared actual String name => "Extract Value";

    shared actual ObjectArray<JavaString> nameProposals
        => Nodes.nameProposals(editorData?.node);
}