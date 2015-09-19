package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.CHANGE;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MethodDeclaration;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoring;
import com.redhat.ceylon.model.typechecker.model.Function;

public class ConvertMethodToGetterProposal extends CorrectionProposal {

    private static final class ConvertToGetterRefactoring extends RenameRefactoring {
        private ConvertToGetterRefactoring(IEditorPart editor) {
            super(editor);
        }

        @Override
        public String getName() {
            return "Convert To Getter";
        }

        @Override
        protected void renameIdentifier(TextChange tfc, Identifier id, CompilationUnit root) {}

        @Override
        protected void renameRegion(TextChange tfc, Region region, CompilationUnit root) {}

        @Override
        public List<Identifier> getIdentifiersToRename(CompilationUnit root) {
            return emptyList();
        }

        @Override
        public List<Region> getStringsToReplace(CompilationUnit root) {
            return emptyList();
        }

        @Override
        protected void renameNode(TextChange tfc, Node node, Tree.CompilationUnit root) {
            Integer startIndex = null;
            Integer endIndex = null;
            
            if (node instanceof Tree.AnyMethod) {
                Tree.AnyMethod em = (Tree.AnyMethod) node;
                Tree.ParameterList parameterList = 
                        em.getParameterLists().get(0);
                startIndex = parameterList.getStartIndex();
                endIndex = parameterList.getEndIndex();
            }
            else {
                FindInvocationVisitor fiv = 
                        new FindInvocationVisitor(node);
                fiv.visit(root);
                if (fiv.result != null && 
                        fiv.result.getPrimary() == node) {
                    Tree.PositionalArgumentList pal = 
                            fiv.result.getPositionalArgumentList();
                    startIndex = pal.getStartIndex();
                    endIndex = pal.getEndIndex();
                }
            }
            
            if (startIndex != null && endIndex != null) {
                tfc.addEdit(new DeleteEdit(startIndex, endIndex - startIndex));
            }
        }
    }

    public static void addConvertMethodToGetterProposal(
            Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, IFile file, Node node) {
        Function method = null;
        Tree.Type type = null;

        if (node instanceof Tree.MethodDefinition) {
            Tree.MethodDefinition md = (Tree.MethodDefinition) node;
            method = md.getDeclarationModel();
            type = md.getType();
        }
        else if (node instanceof Tree.MethodDeclaration) {
            MethodDeclaration md = (Tree.MethodDeclaration) node;
            if (md.getSpecifierExpression() 
                    instanceof Tree.LazySpecifierExpression) {
                method = md.getDeclarationModel();
                type = md.getType();
            }
        }

        if (method != null 
                && !method.isDeclaredVoid()
                && method.getParameterLists().size() == 1 
                && method.getParameterLists().get(0).getParameters().size() == 0 ) {
            addConvertMethodToGetterProposal(proposals, editor, file, method, type);
        }
    }

    private static void addConvertMethodToGetterProposal(
            Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, IFile file, Function method, 
            Tree.Type type) {
        try {
            RenameRefactoring refactoring = 
                    new ConvertToGetterRefactoring(editor);
            
            if (refactoring.getDeclaration() == null 
                    || !refactoring.getDeclaration().equals(method) 
                    || !refactoring.getEnabled()
                    || !refactoring.checkAllConditions(new NullProgressMonitor()).isOK()) {
                return;
            }

            CompositeChange change = 
                    refactoring.createChange(
                            new NullProgressMonitor());
            if (change.getChildren().length == 0) {
                return;
            }
            
            if (type instanceof Tree.FunctionModifier) {
                TextFileChange tfc = 
                        new TextFileChange(
                                "Convert To Getter", file);
                tfc.setEdit(new ReplaceEdit(
                        type.getStartIndex(), 
                        type.getDistance(), 
                        "value"));
                change.add(tfc);
            }
            
            String desc = 
                    "Convert " +
                    (method.isToplevel() ? "function" : "method") +
                    " '" + method.getName() + "()' to getter";
            ConvertMethodToGetterProposal proposal = 
                    new ConvertMethodToGetterProposal(
                            desc, change, method);
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }
        }
        catch (OperationCanceledException e) {
            // noop
        }
        catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private ConvertMethodToGetterProposal(
            String desc, Change change, Function method) {
        super(desc, change, null, CHANGE);
    }

}