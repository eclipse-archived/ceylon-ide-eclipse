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
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import org.eclipse.text.edits {
    MultiTextEdit,
    InsertEdit,
    ReplaceEdit
}
import com.redhat.ceylon.eclipse.code.correct {
    ImportProposals
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
    Region,
    IRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}
import java.lang {
    ObjectArray,
    JavaString=String
}

class EclipseExtractValueRefactoring(IEditorPart editorPart) extends EclipseAbstractRefactoring(editorPart)
        satisfies ExtractValueRefactoring & ExtractLinkedModeEnabled {
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

    shared actual void extractInFile(TextChange tfc) {
        "This method will only be called when the [[editorData]]is not [[null]]"
        assert(exists editorData);
        assert (is Tree.Term node=editorData.node,
            exists rootNode=editorData.rootNode);
        
        tfc.edit = MultiTextEdit();
        value doc = EditorUtil.getDocument(tfc);
        
        value result = extractValue();
        
        Integer il;
        if (!result.declarationsToImport.empty) {
            il = ImportProposals.applyImports(tfc, result.declarationsToImport, rootNode, doc);
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