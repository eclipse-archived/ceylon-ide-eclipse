import com.redhat.ceylon.ide.common.correct {
    OperatorQuickFix,
    VerboseRefinementQuickFix,
    AssignToFieldQuickFix,
    ChangeToIfQuickFix,
    ConvertToDefaultConstructorQuickFix,
    AssertExistsDeclarationQuickFix,
    SplitDeclarationQuickFix,
    JoinDeclarationQuickFix,
    ConvertThenElseToIfElse,
    ConvertIfElseToThenElseQuickFix,
    InvertIfElseQuickFix,
    ConvertSwitchToIfQuickFix,
    SplitIfStatementQuickFix,
    JoinIfStatementsQuickFix
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

object operatorQuickFix
        satisfies OperatorQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseGenericQuickFix {
}

object verboseRefinementQuickFix
        satisfies VerboseRefinementQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object assignToFieldQuickFix
        satisfies AssignToFieldQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}

object changeToIfQuickFix
        satisfies ChangeToIfQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object convertToDefaultConstructorQuickFix
        satisfies ConvertToDefaultConstructorQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object assertExistsDeclarationQuickFix
        satisfies AssertExistsDeclarationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object splitDeclarationQuickFix
        satisfies SplitDeclarationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object joinDeclarationQuickFix
        satisfies JoinDeclarationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object convertThenElseToIfElse
        satisfies ConvertThenElseToIfElse<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object convertIfElseToThenElse
        satisfies ConvertIfElseToThenElseQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object invertIfElseQuickFix
        satisfies InvertIfElseQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}

object convertSwitchToIfQuickFix
        satisfies ConvertSwitchToIfQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}

object splitIfStatementQuickFix
        satisfies SplitIfStatementQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}
      
object joinIfStatementsQuickFix
        satisfies JoinIfStatementsQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}
      