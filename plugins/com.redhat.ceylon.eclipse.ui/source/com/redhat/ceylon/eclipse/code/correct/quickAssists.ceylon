import com.redhat.ceylon.eclipse.code.complete {
    RefinementCompletionProposal
}
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
    AddThrowsAnnotationQuickFix,
    RefineEqualsHashQuickFix,
    ConvertStringQuickFix,
    ExpandTypeQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
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
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object operatorQuickFix
        satisfies OperatorQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
                & EclipseGenericQuickFix {
}

object verboseRefinementQuickFix
        satisfies VerboseRefinementQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object assignToFieldQuickFix
        satisfies AssignToFieldQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}

object changeToIfQuickFix
        satisfies ChangeToIfQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object convertToDefaultConstructorQuickFix
        satisfies ConvertToDefaultConstructorQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object assertExistsDeclarationQuickFix
        satisfies AssertExistsDeclarationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object splitDeclarationQuickFix
        satisfies SplitDeclarationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object joinDeclarationQuickFix
        satisfies JoinDeclarationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object convertThenElseToIfElse
        satisfies ConvertThenElseToIfElse<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object convertIfElseToThenElse
        satisfies ConvertIfElseToThenElseQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {   
}

object invertIfElseQuickFix
        satisfies InvertIfElseQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}

object convertSwitchToIfQuickFix
        satisfies ConvertSwitchToIfQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}

object addThrowsAnnotationQuickFix
        satisfies AddThrowsAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
    
    addAnnotationsQuickFix => eclipseAnnotationsQuickFix;
}

object refineEqualsHashQuickFix
        satisfies RefineEqualsHashQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        TextChange _change, DefaultRegion? region) { 
        
        value proposal = object extends CorrectionProposal(desc, _change, 
                null, RefinementCompletionProposal.\iDEFAULT_REFINEMENT) {
             
             styledDisplayString =>
                     let(hint=CorrectionUtil.shortcut("com.redhat.ceylon.eclipse.ui.action.refineEqualsHash"))
                     super.styledDisplayString.append(hint, StyledString.\iQUALIFIER_STYLER);
        };
        data.proposals.add(proposal); 
    }

}

object convertStringQuickFix
        satisfies ConvertStringQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}

object expandTypeQuickFix
        satisfies ExpandTypeQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
}
