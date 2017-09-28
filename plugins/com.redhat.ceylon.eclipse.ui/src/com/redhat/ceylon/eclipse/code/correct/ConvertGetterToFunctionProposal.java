package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.COMPOSITE_CHANGE;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

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
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Identifier;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.RenameRefactoring;
import org.eclipse.ceylon.model.typechecker.model.Value;

class ConvertGetterToFunctionProposal extends CorrectionProposal {

    private Value getter;
    private ConvertToFunctionRefactoring refactoring;

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
        protected void refactorJavaReferences(IProgressMonitor pm, CompositeChange cc) {
            // TODO!
        }
        
        @Override
        protected void renameIdentifier(TextChange tfc, Identifier id, CompilationUnit root) {}

        @Override
        protected void renameRegion(TextChange tfc, Region region, CompilationUnit root) {}
        
        @Override
        protected void renameNode(TextChange tfc, Node node, CompilationUnit root) {
            if (node instanceof Tree.AnyAttribute) {
                Tree.AnyAttribute am = (Tree.AnyAttribute) node;
                Tree.Type type = am.getType();
                if (type instanceof Tree.ValueModifier) {
                    tfc.addEdit(new ReplaceEdit(
                            type.getStartIndex(), 
                            type.getDistance(), 
                            "function"));
                }
            }
            Node identifyingNode = getIdentifier(node);
            tfc.addEdit(new InsertEdit(
                    identifyingNode.getEndIndex(), 
                    "()"));
        }
        
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
            CeylonEditor editor, Node node) {
        
        Value getter;
        if (node instanceof Tree.AttributeGetterDefinition) {
            Tree.AttributeGetterDefinition agd = 
                    (Tree.AttributeGetterDefinition) node;
            getter = agd.getDeclarationModel();
        }
        else if (node instanceof Tree.AttributeDeclaration) {
            Tree.AttributeDeclaration ad = 
                    (Tree.AttributeDeclaration) node;
            if (ad.getSpecifierOrInitializerExpression() 
                    instanceof Tree.LazySpecifierExpression) {
                getter = ad.getDeclarationModel();
            }
            else {
                return;
            }
        }
        else {
            return;
        }

        if (getter!=null) {
            addConvertGetterToFunctionProposal(proposals, 
                    editor, getter);
        }
    }

    private static void addConvertGetterToFunctionProposal(
            Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, Value getter) {
        ConvertToFunctionRefactoring refactoring = 
                new ConvertToFunctionRefactoring(editor);
        try {
            if (refactoring.getDeclaration() == null 
                    || !refactoring.getDeclaration().equals(getter) 
                    || !refactoring.getEnabled()
                    || !refactoring.checkAllConditions(new NullProgressMonitor()).isOK()) {
                return;
            }
        }
        catch (OperationCanceledException e) {
            return;
        }
        catch (CoreException e) {
            e.printStackTrace();
            return;
        }

        String desc = 
                "Convert getter '" + 
                getter.getName() + "' to " + 
                (getter.isToplevel() ? "function" : "method");
        ConvertGetterToFunctionProposal proposal = 
                new ConvertGetterToFunctionProposal(
                        desc, getter, refactoring);
        if (!proposals.contains(proposal)) {
            proposals.add(proposal);
        }
    }

    @Override
    public Change createChange() throws CoreException {
        refactoring.setNewName(getter.getName() + "()");
        CompositeChange change = 
                refactoring.createChange(
                        new NullProgressMonitor());
//        if (change.getChildren().length == 0) {
//            return;
//        }        
        return change;
    }

    private ConvertGetterToFunctionProposal(
            String desc, Value getter, 
            ConvertToFunctionRefactoring refactoring) {
        super(desc, null, null, COMPOSITE_CHANGE);
        this.getter = getter;
        this.refactoring = refactoring;
    }

}