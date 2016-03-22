import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.correct {
    eclipseImportProposals,
    EclipseDocumentChanges
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.refactoring {
    ExtractParameterRefactoring,
    FindFunctionVisitor
}
import com.redhat.ceylon.ide.common.util {
    nodes
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
    RefactoringStatus,
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

class EclipseExtractParameterRefactoring(CeylonEditor editorPart) 
        extends EclipseAbstractRefactoring<TextChange>(editorPart)
        satisfies ExtractParameterRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion>
        & EclipseDocumentChanges
        & EclipseExtractLinkedModeEnabled {
    
    importProposals => eclipseImportProposals;
    
    shared actual variable String? internalNewName = null;
    shared actual variable Boolean canBeInferred = false;
    shared actual variable Boolean explicitType = false;
    shared actual variable Type? type = null;
    shared actual variable IRegion? typeRegion = null;
    shared actual variable IRegion? decRegion = null;
    shared actual variable IRegion? refRegion = null;
    shared actual variable Tree.Declaration? methodOrClass = null;
    
    value selection = EditorUtil.getSelection(editorPart);
    
    Tree.CompilationUnit? rootNode 
            = editorPart.parseController
                .typecheckedRootNode;
    if (!exists rootNode) {
        return;
    }
    
    value node = nodes.findNode {
        node = rootNode;
        tokens = editorPart.parseController.tokens;
        startOffset = selection.offset;
        endOffset = selection.offset+selection.length;
    };
    
    if (exists node) {
        value ffv = FindFunctionVisitor(node);
        ffv.visit(rootNode);
        methodOrClass = ffv.definitionNode;
    }
    
    checkFinalConditions(IProgressMonitor? monitor)
            => let(node = editorData.node) 
            if (exists mop=node.scope.getMemberOrParameter(node.unit, newName, null, false))
            then RefactoringStatus.createWarningStatus(
                    "An existing declaration named '``newName``' is already visible this scope")
            else RefactoringStatus();
    
    checkInitialConditions(IProgressMonitor? monitor)
            => RefactoringStatus();
    
    shared actual TextChange createChange(IProgressMonitor? monitor) {
        TextChange tc = newLocalChange();
        extractInFile(tc);
        return tc;
    }
    
    newRegion(Integer start, Integer length) => Region(start, length);
    
    extractInFile(TextChange tfc) => build(tfc);
    
    shared actual String name => (super of ExtractParameterRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion>).name;
}
