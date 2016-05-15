import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.platform {
    EclipseTextChange
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.correct {
    IdeQuickFixManager,
    QuickFixData,
    SpecifyTypeQuickFix,
    convertForToWhileQuickFix,
    joinIfStatementsQuickFix,
    splitIfStatementQuickFix,
    verboseRefinementQuickFix,
    addParameterQuickFix,
    miscQuickFixes,
    changeToIfQuickFix,
    splitDeclarationQuickFix,
    expandTypeQuickFix,
    convertStringQuickFix,
    assignToFieldQuickFix,
    joinDeclarationQuickFix,
    operatorQuickFix,
    convertToDefaultConstructorQuickFix,
    convertSwitchToIfQuickFix,
    invertIfElseQuickFix,
    convertIfElseToThenElseQuickFix,
    convertThenElseToIfElse,
    assertExistsDeclarationQuickFix
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    CommonTextChange=TextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type,
    Scope
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

shared class EclipseQuickFixData(ProblemLocation location,
    shared actual Tree.CompilationUnit rootNode,
    shared actual Node node,
    shared IProject project,
    shared Collection<ICompletionProposal> proposals,
    shared CeylonEditor editor,
    shared actual BaseCeylonProject ceylonProject,
    IDocument doc)
        satisfies QuickFixData {
    
    errorCode => location.problemId;
    problemOffset => location.offset;
    problemLength => location.length;
    
    phasedUnit => editor.parseController.lastPhasedUnit;
    document = EclipseDocument(doc);
    
    shared actual void addQuickFix(String desc, CommonTextChange change,
        DefaultRegion? selection, Boolean qualifiedNameIsPath) {
        
        if (is EclipseTextChange change) {
            value region 
                    = if (exists selection)
                    then Region(selection.start, selection.length)
                    else null;
            proposals.add(CorrectionProposal(desc, 
                change.nativeChange, region, 
                qualifiedNameIsPath));
        }
    }
    
    shared actual void addInitializerQuickFix(String desc, CommonTextChange change,
        DefaultRegion selection, Unit unit, Scope scope, Type? type) {
        
        if (is EclipseTextChange change) {
            proposals.add(EclipseInitializerProposal {
                name = desc;
                change = change.nativeChange;
                unit = unit;
                scope = scope;
                type = type;
                selection = Region(selection.start, selection.length);
                image = CeylonResources.\iMINOR_CHANGE;
                exitPos = -1;
            });
        }
    }

    shared actual void addParameterQuickFix(String desc, CommonTextChange change,
        DefaultRegion selection, Unit unit, Scope scope, Type? type, Integer exitPos) {
        
        if (is EclipseTextChange change) {
            proposals.add(EclipseInitializerProposal {
                name = desc;
                change = change.nativeChange;
                unit = unit;
                scope = scope;
                type = type;
                selection = Region(selection.start, selection.length);
                image = CeylonResources.\iADD_CORR;
                exitPos = exitPos;
            });
        }
    }
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
    changeTypeQuickFix => eclipseChangeTypeQuickFix;
    addSatisfiesQuickFix => eclipseAddSatisfiesQuickFix;
    addModuleImportQuickFix => eclipseAddModuleImportQuickFix;
    assignToLocalQuickFix => eclipseAssignToLocalQuickFix;
    
    shared actual void addImportProposals(Collection<ICompletionProposal> proposals, EclipseQuickFixData data) {
        data.proposals.addAll(proposals);
    }
    
    shared actual SpecifyTypeQuickFix<IFile,IDocument,InsertEdit,TextEdit,
        TextChange,Region,EclipseQuickFixData,ICompletionProposal,
        LinkedModeModel> specifyTypeQuickFix => eclipseSpecifyTypeQuickFix;
    
    shared actual void addCreateTypeParameterProposal<Data>(Data data,
        Tree.BaseType bt, String brokenName)
            given Data satisfies QuickFixData {
        
        assert (is EclipseQuickFixData data);
        
        CreateTypeParameterProposal.addCreateTypeParameterProposal(
            data.proposals, data.rootNode, bt, brokenName);
    }
    
    shared void addQuickAssists(EclipseQuickFixData data, IFile file,
        IDocument doc, Tree.Statement? statement,
        Tree.Declaration? declaration, Tree.NamedArgument? namedArgument,
        Tree.ImportMemberOrType? imp, Tree.OperatorExpression? oe,
        Integer currentOffset) {
        
        assignToLocalQuickFix.addProposal(data, file, currentOffset);
        
        if (is Tree.BinaryOperatorExpression oe) {
            operatorQuickFix.addReverseOperatorProposal(data,  oe);
            operatorQuickFix.addInvertOperatorProposal(data, oe);
            operatorQuickFix.addSwapBinaryOperandsProposal(data, oe);
        }
        operatorQuickFix.addParenthesesProposals(data, oe);
        
        verboseRefinementQuickFix.addVerboseRefinementProposal(data, statement);
        verboseRefinementQuickFix.addShortcutRefinementProposal(data, statement);
        
        addAnnotations.addContextualAnnotationProposals(data, declaration, doc, currentOffset);
        specifyTypeQuickFix.addTypingProposals(data, file, declaration);
        
        miscQuickFixes.addAnonymousFunctionProposals(data);
        
        miscQuickFixes.addDeclarationProposals(data, declaration, currentOffset);
        
        assignToFieldQuickFix.addAssignToFieldProposal(data, statement, declaration);
        
        changeToIfQuickFix.addChangeToIfProposal(data, statement);
        
        convertToDefaultConstructorQuickFix.addConvertToDefaultConstructorProposal(data, statement);

        convertToClassQuickFix.addConvertToClassProposal(data, declaration);
        assertExistsDeclarationQuickFix.addAssertExistsDeclarationProposals(data, declaration);
        splitDeclarationQuickFix.addSplitDeclarationProposals(data, declaration, statement);
        joinDeclarationQuickFix.addJoinDeclarationProposal(data, statement);
        addParameterQuickFix.addParameterProposals(data);

        miscQuickFixes.addArgumentProposals(data, namedArgument);

        value document = EclipseDocument(doc);
        convertThenElseToIfElse.addConvertToIfElseProposal(data, document, statement);
        convertIfElseToThenElseQuickFix.addConvertToThenElseProposal(data, document, statement);
        invertIfElseQuickFix.addInvertIfElseProposal(data, document, statement);
        
        convertSwitchToIfQuickFix.addConvertSwitchToIfProposal(data, statement);
        convertSwitchToIfQuickFix.addConvertIfToSwitchProposal(data, statement);
        
        splitIfStatementQuickFix.addSplitIfStatementProposal(data, statement);
        joinIfStatementsQuickFix.addJoinIfStatementsProposal(data, statement);
        
        convertForToWhileQuickFix.addConvertForToWhileProposal(data, statement);
        
        addThrowsAnnotationQuickFix.addThrowsAnnotationProposal(data, file, doc, statement);
        
        refineFormalMembersQuickFix.addRefineFormalMembersProposal(data, false);
        refineEqualsHashQuickFix.addRefineEqualsHashProposal(data, file, currentOffset);
        
        convertStringQuickFix.addConvertToVerbatimProposal(data);
        convertStringQuickFix.addConvertFromVerbatimProposal(data);
        convertStringQuickFix.addConvertToConcatenationProposal(data);
        convertStringQuickFix.addConvertToInterpolationProposal(data);
        
        expandTypeQuickFix.addExpandTypeProposal {
            data = data;
            node = statement;
            selectionStart 
                    = data.editor.selection.offset;
            selectionStop 
                    = data.editor.selection.offset 
                    + data.editor.selection.length;
        };
    }
}
