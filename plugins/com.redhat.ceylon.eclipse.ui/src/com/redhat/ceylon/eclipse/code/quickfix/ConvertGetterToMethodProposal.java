package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CHANGE;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoring;

public class ConvertGetterToMethodProposal extends ChangeCorrectionProposal {

    public static void addConvertGetterToMethodProposal(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, Node node) {
        Value getter = null;

        if (node instanceof Tree.AttributeGetterDefinition) {
            getter = ((Tree.AttributeGetterDefinition) node).getDeclarationModel();
        }
        else if (node instanceof Tree.AttributeDeclaration && 
                ((Tree.AttributeDeclaration) node).getSpecifierOrInitializerExpression() 
                instanceof Tree.LazySpecifierExpression) {
            getter = ((Tree.AttributeDeclaration) node).getDeclarationModel();
        }
        else if (node instanceof Tree.MemberOrTypeExpression) {
            Declaration decl = ((Tree.MemberOrTypeExpression) node).getDeclaration();
            if (decl instanceof Value) {
                getter = (Value) decl;
            }
        }

        if (getter != null) {
            addConvertGetterToMethodProposal(proposals, editor, getter);
        }
    }

    private static void addConvertGetterToMethodProposal(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, Value getter) {
        try {
            RenameRefactoring refactoring = new RenameRefactoring(editor) {
                @Override
                public String getName() {
                    return "Convert getter to method";
                };
            };
            refactoring.setNewName(getter.getName() + "()");
            
            if (refactoring.getDeclaration() == null 
                    || !refactoring.getDeclaration().equals(getter) 
                    || !refactoring.isEnabled()
                    || !refactoring.checkAllConditions(new NullProgressMonitor()).isOK()) {
                return;
            }

            Change change = refactoring.createChange(new NullProgressMonitor());
            ConvertGetterToMethodProposal proposal = new ConvertGetterToMethodProposal(change, getter);
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }
        } catch (OperationCanceledException e) {
            // noop
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private ConvertGetterToMethodProposal(Change change, Value getter) {
        super("Convert getter '" + getter.getName() + "' to method", change, 10, CHANGE);
    }

}