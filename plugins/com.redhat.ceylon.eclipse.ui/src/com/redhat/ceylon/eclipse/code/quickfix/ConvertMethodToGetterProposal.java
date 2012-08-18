package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CHANGE;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoring;

public class ConvertMethodToGetterProposal extends ChangeCorrectionProposal {

    public static void addConvertMethodToGetterProposal(Collection<ICompletionProposal> proposals, CeylonEditor editor, Node node) {
        Method method = null;

        if (node instanceof Tree.AnyMethod) {
            method = ((Tree.AnyMethod) node).getDeclarationModel();
        }
        if (node instanceof Tree.MemberOrTypeExpression) {
            Declaration decl = ((Tree.MemberOrTypeExpression) node).getDeclaration();
            if (decl instanceof Method) {
                method = (Method) decl;
            }
        }

        if (method != null 
                && !method.isDeclaredVoid()
                && method.getParameterLists().size() == 1 
                && method.getParameterLists().get(0).getParameters().size() == 0 ) {
            addConvertMethodToGetterProposal(proposals, editor, method);
        }
    }

    private static void addConvertMethodToGetterProposal(Collection<ICompletionProposal> proposals, CeylonEditor editor, Method method) {
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
                    
                    if (node instanceof Tree.MethodDefinition) {
                        ParameterList parameterList = ((Tree.MethodDefinition) node).getParameterLists().get(0);
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
                    || !refactoring.isEnabled()
                    || !refactoring.checkAllConditions(new NullProgressMonitor()).isOK()) {
                return;
            }

            Change change = refactoring.createChange(new NullProgressMonitor());
            ConvertMethodToGetterProposal proposal = new ConvertMethodToGetterProposal(change, method);
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }
        } catch (OperationCanceledException e) {
            // noop
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private ConvertMethodToGetterProposal(Change change, Method method) {
        super("Convert method '" + method.getName() + "()' to getter", change, 10, CHANGE);
    }

}