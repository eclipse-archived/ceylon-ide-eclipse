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
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}

shared class EclipseQuickFixData(ProblemLocation location,
    shared actual Tree.CompilationUnit rootNode,
    shared actual Node node,
    shared actual IProject project,
    shared Collection<ICompletionProposal> proposals,
    shared CeylonEditor editor,
    shared actual BaseCeylonProject ceylonProject)
        satisfies QuickFixData<IProject> {
    
    errorCode => location.problemId;
    problemOffset => location.offset;
    problemLength => location.length;
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
    changeTypeQuickFix => eclipseChangeTypeQuickFix;
    addSatisfiesQuickFix => eclipseAddSatisfiesQuickFix;
    addSpreadToVariadicParameterQuickFix => eclipseAddSpreadToVariadicParameterQuickFix;
    addTypeParameterQuickFix => eclipseAddTypeParameterQuickFix;
    shadowReferenceQuickFix => eclipseShadowReferenceQuickFix;
    changeInitialCaseQuickFix => eclipseChangeInitialCaseQuickFix;
    fixMultilineStringIndentationQuickFix => eclipseFixMultilineStringIndentationQuickFix;
    addModuleImportQuickFix => eclipseAddModuleImportQuickFix;
    renameDescriptorQuickFix => eclipseRenameDescriptorQuickFix;
    changeRefiningTypeQuickType => eclipseChangeRefiningTypeQuickType;
    switchQuickFix => eclipseSwitchQuickFix;
    changeToQuickFix => eclipseChangeToQuickFix;
    addNamedArgumentQuickFix => eclipseAddNamedArgumentQuickFix;
    
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
    
    shared void addQuickAssists(EclipseQuickFixData data, IFile file,
        IDocument doc, Tree.Statement? statement,
        Tree.Declaration? declaration, Tree.NamedArgument? namedArgument,
        Tree.ImportMemberOrType? imp, Tree.OperatorExpression? oe,
        Integer currentOffset) {
        
        if (is Tree.BinaryOperatorExpression oe) {
            operatorQuickFix.addReverseOperatorProposal(data, file, oe);
            operatorQuickFix.addInvertOperatorProposal(data, file, oe);
            operatorQuickFix.addSwapBinaryOperandsProposal(data, file, oe);
        }
        operatorQuickFix.addParenthesesProposals(data, file, oe);
        
        verboseRefinementQuickFix.addVerboseRefinementProposal(data, file, statement);
        verboseRefinementQuickFix.addShortcutRefinementProposal(data, file, statement);
        
        addAnnotations.addContextualAnnotationProposals(data, declaration, doc, currentOffset);
        specifyTypeQuickFix.addTypingProposals(data, file, declaration);
        
        eclipseMiscQuickFix.addAnonymousFunctionProposals(data, file);
        
        eclipseMiscQuickFix.addDeclarationProposals(data, file, declaration, currentOffset);
        
        assignToFieldQuickFix.addAssignToFieldProposal(data, file, statement, declaration);
        
        changeToIfQuickFix.addChangeToIfProposal(data, file, statement);
        
        convertToDefaultConstructorQuickFix.addConvertToDefaultConstructorProposal(data, file, statement);

        convertToClassQuickFix.addConvertToClassProposal(data, declaration);
        assertExistsDeclarationQuickFix.addAssertExistsDeclarationProposals(data, file, declaration);
        splitDeclarationQuickFix.addSplitDeclarationProposals(data, file, declaration, statement);
        joinDeclarationQuickFix.addJoinDeclarationProposal(data, file, statement);
        addParameterQuickFix.addParameterProposals(data, file);

        eclipseMiscQuickFix.addArgumentProposals(data, file, namedArgument);

        convertThenElseToIfElse.addConvertToIfElseProposal(data, file, doc, statement);
        convertIfElseToThenElse.addConvertToThenElseProposal(data, file, doc, statement);
        invertIfElseQuickFix.addInvertIfElseProposal(data, file, doc, statement);
        
        convertSwitchToIfQuickFix.addConvertSwitchToIfProposal(data, file, statement);
        convertSwitchToIfQuickFix.addConvertIfToSwitchProposal(data, file, statement);
        
        splitIfStatementQuickFix.addSplitIfStatementProposal(data, file, statement);
        joinIfStatementsQuickFix.addJoinIfStatementsProposal(data, file, statement);
        
        refineFormalMembersQuickFix.addRefineFormalMembersProposal(data, false);
    }
}
