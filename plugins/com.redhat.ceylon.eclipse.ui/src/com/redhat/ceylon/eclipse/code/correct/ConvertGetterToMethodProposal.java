package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.CHANGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoring;

public class ConvertGetterToMethodProposal extends CorrectionProposal {

    public static void addConvertGetterToMethodProposal(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, IFile file, Node node) {
        Value getter = null;
        Tree.Type type = null;

        if (node instanceof Tree.AttributeGetterDefinition) {
            getter = ((Tree.AttributeGetterDefinition) node).getDeclarationModel();
            type = ((Tree.AttributeGetterDefinition) node).getType();
        }
        else if (node instanceof Tree.AttributeDeclaration && 
                ((Tree.AttributeDeclaration) node).getSpecifierOrInitializerExpression() 
                instanceof Tree.LazySpecifierExpression) {
            getter = ((Tree.AttributeDeclaration) node).getDeclarationModel();
            type = ((Tree.AttributeDeclaration) node).getType();
        }

        if (getter != null) {
            addConvertGetterToMethodProposal(proposals, editor, file, getter, type);
        }
    }

    private static void addConvertGetterToMethodProposal(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, IFile file, Value getter, Tree.Type type) {
        try {
            RenameRefactoring refactoring = new RenameRefactoring(editor) {
                @Override
                public String getName() {
                    return "Convert Getter to Method";
                };
            };
            refactoring.setNewName(getter.getName() + "()");
            
            if (refactoring.getDeclaration() == null 
                    || !refactoring.getDeclaration().equals(getter) 
                    || !refactoring.isEnabled()
                    || !refactoring.checkAllConditions(new NullProgressMonitor()).isOK()) {
                return;
            }

            CompositeChange change = refactoring.createChange(new NullProgressMonitor());
            if (change.getChildren().length == 0) {
                return;
            }
            
            if (type instanceof Tree.ValueModifier) {
                TextFileChange tfc = new TextFileChange("Convert Getter to Method", file);
                tfc.setEdit(new ReplaceEdit(type.getStartIndex(), 
                        type.getStopIndex() - type.getStartIndex() + 1, 
                        "function"));
                change.add(tfc);
            }
            
            ConvertGetterToMethodProposal proposal = 
                    new ConvertGetterToMethodProposal(change, getter);
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
        super("Convert getter '" + getter.getName() + "' to method", 
                change, null, CHANGE);
    }

}