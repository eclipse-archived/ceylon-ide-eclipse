import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.ide.common.correct {
    IdeQuickFixManager,
    QuickFixData,
    SpecifyTypeQuickFix
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
    
    importProposals => eclipseImportProposals;
    
    addAnnotations => eclipseAnnotationsQuickFix;
    removeAnnotations => eclipseAnnotationsQuickFix;
    createQuickFix => eclipseCreateQuickFix;
    changeReferenceQuickFix => eclipseChangeReferenceQuickFix;
    declareLocalQuickFix => eclipseDeclareLocalQuickFix;
    createEnumQuickFix => eclipseCreateEnumQuickFix;
    refineFormalMembersQuickFix => eclipseRefineFormalMembersQuickFix;
    exportModuleImportQuickFix => eclipseExportModuleImportQuickFix;
    addPunctuationQuickFix => eclipseAddPunctuationQuickFix;
    addParameterListQuickFix => eclipseAddParameterListQuickFix;
    addParameterQuickFix => eclipseAddParameterQuickFix;
    addInitializerQuickFix => eclipseAddInitializerQuickFix;
    addConstructorQuickFix => eclipseAddConstructorQuickFix;
    changeDeclarationQuickFix => eclipseChangeDeclarationQuickFix;
    fixAliasQuickFix => eclipseFixAliasQuickFix;
    appendMemberReferenceQuickFix => eclipseAppendMemberReferenceQuickFix;
    
    shared actual void addImportProposals(Collection<ICompletionProposal> proposals, EclipseQuickFixData data) {
        data.proposals.addAll(proposals);
    }
    
    shared actual SpecifyTypeQuickFix<IFile,IDocument,InsertEdit,TextEdit,
        TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal,
        LinkedModeModel> specifyTypeQuickFix => eclipseSpecifyTypeQuickFix;
    
    shared actual void addCreateTypeParameterProposal<Data>(Data data,
        Tree.BaseType bt, String brokenName)
            given Data satisfies QuickFixData<IProject> {
        
        assert (is EclipseQuickFixData data);
        
        CreateTypeParameterProposal.addCreateTypeParameterProposal(
            data.proposals, data.project, data.rootNode, bt, brokenName);
    }
}
