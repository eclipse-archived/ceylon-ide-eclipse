package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.refactor.ExtractLinkedMode.useLinkedMode;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CHANGE;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.ExtractParameterLinkedMode;
import com.redhat.ceylon.eclipse.code.refactor.ExtractParameterRefactoring;
import com.redhat.ceylon.eclipse.code.refactor.ExtractParameterRefactoringAction;

public class ExtractParameterProposal 
        implements ICompletionProposal, 
                   ICompletionProposalExtension6 {

    private CeylonEditor editor;

    public ExtractParameterProposal(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return CHANGE;
    }

    @Override
    public String getDisplayString() {
        return "Extract parameter";
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public void apply(IDocument doc) {
        if (useLinkedMode()) {
            ExtractParameterLinkedMode.selectExpressionAndStart(editor);
        }
        else {
            new ExtractParameterRefactoringAction(editor).run();
        }
    }
    
    public static void add(
            Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, Node node) {
        if (node instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = 
                    (Tree.BaseMemberExpression) node;
            Tree.Identifier id = bme.getIdentifier();
            if (id==null || id.getToken().getType()==CeylonLexer.AIDENTIFIER) {
                return;
            }
        }
        ExtractParameterRefactoring epr = 
                new ExtractParameterRefactoring(editor);
        if (epr.getEnabled()) {
            proposals.add(new ExtractParameterProposal(editor));
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "com.redhat.ceylon.eclipse.ui.action.extractParameter");
        return new StyledString(getDisplayString())
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

}