import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.complete {
    EclipseLinkedModeSupport
}
import com.redhat.ceylon.ide.common.correct {
    DeclareLocalQuickFix
}

import org.eclipse.core.resources {
    IFile,
    IProject
}
import org.eclipse.jface.text {
    IDocument,
    Region
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseDeclareLocalQuickFix
        satisfies DeclareLocalQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,LinkedModeModel,ICompletionProposal,IProject,EclipseQuickFixData,Region> & EclipseAbstractQuickFix & EclipseDocumentChanges & EclipseLinkedModeSupport {
    
    shared actual void newDeclareLocalQuickFix(EclipseQuickFixData data, String desc, TextChange change,
        Tree.Term term, Tree.BaseMemberExpression bme) {
        
        data.proposals.add(DeclareLocalProposal(change, desc, term, bme, data.rootNode, data.editor));
    }
}
