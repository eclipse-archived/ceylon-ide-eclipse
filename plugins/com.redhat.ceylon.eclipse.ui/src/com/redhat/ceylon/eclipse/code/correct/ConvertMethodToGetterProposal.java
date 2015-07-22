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
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoring;

public class ConvertMethodToGetterProposal extends CorrectionProposal {

    public static void addConvertMethodToGetterProposal(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, IFile file, Node node) {
        Function method = null;
        Tree.Type type = null;

        if (node instanceof Tree.MethodDefinition) {
            method = ((Tree.MethodDefinition) node).getDeclarationModel();
            type = ((Tree.MethodDefinition) node).getType();
        }
        else if (node instanceof Tree.MethodDeclaration && 
                ((Tree.MethodDeclaration) node).getSpecifierExpression() 
                        instanceof Tree.LazySpecifierExpression) {
            method = ((Tree.MethodDeclaration) node).getDeclarationModel();
            type = ((Tree.MethodDeclaration) node).getType();
        }

        if (method != null 
                && !method.isDeclaredVoid()
                && method.getParameterLists().size() == 1 
                && method.getParameterLists().get(0).getParameters().size() == 0 ) {
            addConvertMethodToGetterProposal(proposals, editor, file, method, type);
        }
    }

    private static void addConvertMethodToGetterProposal(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, IFile file, Function method, Tree.Type type) {
        try {
            RenameRefactoring refactoring = new RenameRefactoring(editor) {
                @Override
                public String getName() {
                    return "Convert method to getter";
                };
                @Override
                protected void renameNode(TextChange tfc, Node node, Tree.CompilationUnit root) {
                    Integer startIndex = null;
                    Integer stopIndex = null;
                    
                    if (node instanceof Tree.AnyMethod) {
                        ParameterList parameterList = ((Tree.AnyMethod) node).getParameterLists().get(0);
                        startIndex = parameterList.getStartIndex();
                        stopIndex = parameterList.getStopIndex();
                    } else {
                        FindInvocationVisitor fiv = new FindInvocationVisitor(node);
                        fiv.visit(root);
                        if (fiv.result != null && fiv.result.getPrimary() == node) {
                            startIndex = fiv.result.getPositionalArgumentList().getStartIndex();
                            stopIndex = fiv.result.getPositionalArgumentList().getStopIndex();
                        }
                    }
                    
                    if (startIndex != null && stopIndex != null) {
                        tfc.addEdit(new DeleteEdit(startIndex, stopIndex - startIndex + 1));
                    }
                }
            };
            
            if (refactoring.getDeclaration() == null 
                    || !refactoring.getDeclaration().equals(method) 
                    || !refactoring.getEnabled()
                    || !refactoring.checkAllConditions(new NullProgressMonitor()).isOK()) {
                return;
            }

            CompositeChange change = refactoring.createChange(new NullProgressMonitor());
            if (change.getChildren().length == 0) {
                return;
            }
            
            if (type instanceof Tree.FunctionModifier) {
                TextFileChange tfc = new TextFileChange("Convert method to getter", file);
                tfc.setEdit(new ReplaceEdit(type.getStartIndex(), 
                        type.getStopIndex() - type.getStartIndex() + 1, 
                        "value"));
                change.add(tfc);
            }
            
            ConvertMethodToGetterProposal proposal = 
                    new ConvertMethodToGetterProposal(change, method);
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }
        } catch (OperationCanceledException e) {
            // noop
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private ConvertMethodToGetterProposal(Change change, Function method) {
        super("Convert method '" + method.getName() + "()' to getter", 
                change, null, CHANGE);
    }

}