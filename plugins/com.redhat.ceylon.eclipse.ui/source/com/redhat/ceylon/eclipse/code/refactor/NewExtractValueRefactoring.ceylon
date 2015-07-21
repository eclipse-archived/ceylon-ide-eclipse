import com.redhat.ceylon.ide.common.refactoring {
    CommonExtractValueRefactoring
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
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}

class NewExtractValueRefactoring(CeylonEditor editor, String newName, Boolean explicitType, Boolean getter) extends NewAbstractRefactoring(editor) satisfies CommonExtractValueRefactoring {

    shared actual RefactoringStatus checkFinalConditions(IProgressMonitor? iProgressMonitor) {
        if (exists node, exists mop = node.scope.getMemberOrParameter(node.unit, newName, null, false)) {
            return RefactoringStatus.createWarningStatus("An existing declaration named '" +
                newName + "' already exists in the same scope");
        }
        
        return RefactoringStatus();
    }
    
    shared actual RefactoringStatus checkInitialConditions(IProgressMonitor? iProgressMonitor) => RefactoringStatus();
    
    shared actual Change createChange(IProgressMonitor? iProgressMonitor) {
        TextChange tc = newLocalChange();
        extractInFile(tc);
        
        return tc;
    }
    
    void extractInFile(TextChange tfc) {
        tfc.edit = MultiTextEdit();
        value doc = tfc.getCurrentDocument(null);

        assert (exists node, node is Tree.Term);

        value result = extractValue(node, rootNode, newName, explicitType, getter);
        variable Integer il = 0;
        
        if (!result.declarationsToImport.empty) {
            il = ImportProposals.applyImports(tfc, result.declarationsToImport, rootNode, doc);
        }
        
        value text = result.declaration + Indents.getDefaultLineDelimiter(doc)
                + Indents.getIndent(result.statement, doc);
        
        if (exists st = result.statement) {
            value start = st.startIndex;
        
            tfc.addEdit(InsertEdit(start.intValue(), text));
            tfc.addEdit(ReplaceEdit(Nodes.getNodeStartOffset(node), Nodes.getNodeLength(node), newName));
        }
    }
    
    shared actual Boolean enabled => nothing;
    
    shared actual String name => nothing;
}