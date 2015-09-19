package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.COMPOSITE_CHANGE;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoring;
import com.redhat.ceylon.model.typechecker.model.Value;

class ConvertGetterToFunctionProposal extends CorrectionProposal {

    private static final class ConvertToFunctionRefactoring extends RenameRefactoring {
        private ConvertToFunctionRefactoring(IEditorPart editor) {
            super(editor);
        }

        @Override
        public String getName() {
            return "Convert to Function";
        }
        
        @Override
        public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
                throws CoreException, OperationCanceledException {
            return new RefactoringStatus();
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

    }

    static void addConvertGetterToFunctionProposal(
            Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, IFile file, Node node) {
        Value getter = null;
        Tree.Type type = null;

        if (node instanceof Tree.AttributeGetterDefinition) {
            Tree.AttributeGetterDefinition agd = 
                    (Tree.AttributeGetterDefinition) node;
            getter = agd.getDeclarationModel();
            type = agd.getType();
        }
        else if (node instanceof Tree.AttributeDeclaration) {
            Tree.AttributeDeclaration ad = 
                    (Tree.AttributeDeclaration) node;
            if (ad.getSpecifierOrInitializerExpression() 
                    instanceof Tree.LazySpecifierExpression) {
                getter = ad.getDeclarationModel();
                type = ad.getType();
            }
        }

        if (getter != null) {
            addConvertGetterToFunctionProposal(proposals, 
                    editor, file, getter, type);
        }
    }

    private static void addConvertGetterToFunctionProposal(
            Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, IFile file, Value getter, 
            Tree.Type type) {
        try {
            RenameRefactoring refactoring = 
                    new ConvertToFunctionRefactoring(editor);
            refactoring.setNewName(getter.getName() + "()");
            
            if (refactoring.getDeclaration() == null 
                    || !refactoring.getDeclaration().equals(getter) 
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
            
            if (type instanceof Tree.ValueModifier) {
                TextFileChange tfc = 
                        new TextFileChange(
                                "Convert to Function", file);
                tfc.setEdit(new ReplaceEdit(
                        type.getStartIndex(), 
                        type.getDistance(), 
                        "function"));
                change.add(tfc);
            }
            
            String desc = 
                    "Convert getter '" + 
                    getter.getName() + "' to " + 
                    (getter.isToplevel() ? "function" : "method");
            ConvertGetterToFunctionProposal proposal = 
                    new ConvertGetterToFunctionProposal(
                            desc, change, getter);
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }
        } catch (OperationCanceledException e) {
            // noop
        }
        catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private ConvertGetterToFunctionProposal(
            String desc, Change change, Value getter) {
        super(desc, change, null, COMPOSITE_CHANGE);
    }

}