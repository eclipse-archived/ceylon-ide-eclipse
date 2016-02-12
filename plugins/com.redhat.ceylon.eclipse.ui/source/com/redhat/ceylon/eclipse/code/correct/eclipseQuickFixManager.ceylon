import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.ide.common.correct {
    IdeQuickFixManager,
    ImportProposals,
    AddAnnotationQuickFix,
    RemoveAnnotationQuickFix,
    QuickFixData,
    CreateQuickFix,
    ChangeReferenceQuickFix,
    DeclareLocalQuickFix,
    CreateEnumQuickFix,
    RefineFormalMembersQuickFix,
    SpecifyTypeQuickFix,
    ExportModuleImportQuickFix,
    AddPunctuationQuickFix,
    AddParameterListQuickFix,
    AddParameterQuickFix,
    AddInitializerQuickFix,
    AddConstructorQuickFix
}

import java.util {
    Collection
}

import org.eclipse.core.resources {
    IProject,
    IFile
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


class EclipseQuickFixData(ProblemLocation location,
    shared actual Tree.CompilationUnit rootNode,
    shared actual Node node,
    shared actual IProject project,
    shared Collection<ICompletionProposal> proposals,
    shared CeylonEditor editor)
        satisfies QuickFixData<IProject> {
    
    shared actual Integer errorCode => location.problemId;
    shared actual Integer problemOffset => location.offset;
}

object eclipseQuickFixManager
        extends IdeQuickFixManager<IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,IFile,ICompletionProposal,EclipseQuickFixData,LinkedModeModel>() {

    shared actual AddAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> addAnnotations
            => eclipseAnnotationsQuickFix;
    shared actual RemoveAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> removeAnnotations
            => eclipseAnnotationsQuickFix;
    shared actual CreateQuickFix<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal> createQuickFix
            => eclipseCreateQuickFix;
    
    shared actual void addImportProposals(Collection<ICompletionProposal> proposals, EclipseQuickFixData data) {
        data.proposals.addAll(proposals);
    }
    
    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;
    
    shared actual ChangeReferenceQuickFix<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange,EclipseQuickFixData,Region,ICompletionProposal> changeReferenceQuickFix
            => eclipseChangeReferenceQuickFix;
    
    shared actual DeclareLocalQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,LinkedModeModel,ICompletionProposal,IProject,EclipseQuickFixData,Region> declareLocalQuickFix
            => eclipseDeclareLocalQuickFix;
    
    shared actual CreateEnumQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> createEnumQuickFix
            => eclipseCreateEnumQuickFix;
    
    shared actual RefineFormalMembersQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> refineFormalMembersQuickFix
            => eclipseRefineFormalMembersQuickFix;
    
    shared actual SpecifyTypeQuickFix<IFile,IDocument,InsertEdit,TextEdit,
        TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal,
        LinkedModeModel> specifyTypeQuickFix => eclipseSpecifyTypeQuickFix;

    shared actual void addCreateTypeParameterProposal<Data>(Data data,
        Tree.BaseType bt, String brokenName)
            given Data satisfies QuickFixData<IProject> {
        
        assert(is EclipseQuickFixData data);
        
        CreateTypeParameterProposal.addCreateTypeParameterProposal(
            data.proposals, data.project, data.rootNode, bt, brokenName);
    }
    
    shared actual ExportModuleImportQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> exportModuleImportQuickFix
            => eclipseExportModuleImportQuickFix;
    
    shared actual AddPunctuationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> addPunctuationQuickFix
            => eclipseAddPunctuationQuickFix;
    
    shared actual AddParameterListQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> addParameterListQuickFix
            => eclipseAddParameterListQuickFix;
    
    shared actual AddParameterQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> addParameterQuickFix
            => eclipseAddParameterQuickFix;
    
    shared actual AddInitializerQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> addInitializerQuickFix
            => eclipseAddInitializerQuickFix;
    
    shared actual AddConstructorQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> addConstructorQuickFix 
            => eclipseAddConstructorQuickFix;
}
