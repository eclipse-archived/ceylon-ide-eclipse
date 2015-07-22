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
    Nodes
}
import org.eclipse.ui {
    IEditorPart
}
import org.eclipse.jface.text {
    Region
}

class EclipseExtractValueRefactoring(IEditorPart editorPart, String newName, Boolean explicitType, Boolean getter) extends EclipseAbstractRefactoring(editorPart) satisfies ExtractValueRefactoring {
    shared actual variable Boolean canBeInferred=false;
    shared variable Region? typeRegion=null;
    shared variable Region? decRegion=null;
    shared variable Region? refRegion=null;

    shared actual RefactoringStatus checkFinalConditions(IProgressMonitor? iProgressMonitor)
            => if (exists node=ceylonEditorData?.node,
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

    void extractInFile(TextChange tfc) {
        "This method will only be called when the [[ceylonEditorData]]is not [[null]]"
        assert(exists ceylonEditorData);
        tfc.edit = MultiTextEdit();
        value doc = tfc.getCurrentDocument(null);

        assert (is Tree.Term node=ceylonEditorData.node,
                    exists rootNode=ceylonEditorData.rootNode);

        value result = extractValue(node, rootNode, newName, explicitType, getter);

        Integer il;
        if (!result.declarationsToImport.empty) {
            il = ImportProposals.applyImports(tfc, result.declarationsToImport, rootNode, doc);
        } else {
            il=0;
        }

        value text = result.declaration + Indents.getDefaultLineDelimiter(doc)
                + Indents.getIndent(result.statement, doc);

        if (exists st = result.statement) {
            Integer start = st.startIndex.intValue();

            tfc.addEdit(InsertEdit(start, text));
            tfc.addEdit(ReplaceEdit(Nodes.getNodeStartOffset(node), Nodes.getNodeLength(node), newName));
            typeRegion = Region(start+il, result.typeDec.size);
            decRegion = Region(start+il+result.typeDec.size+1, newName.size);
            refRegion = Region(Nodes.getNodeStartOffset(node)+il+text.size,
                newName.size);

        }
    }

    shared actual Boolean enabled
            => if (exists ceylonEditorData,
                    exists sourceFile=ceylonEditorData.sourceFile,
                    isEditable() &&
                    sourceFile.name != "module.ceylon" &&
                    sourceFile.name != "package.ceylon" &&
                    ceylonEditorData.node is Tree.Term)
                then true
                else false;

    shared actual String name => "Extract Value";
}