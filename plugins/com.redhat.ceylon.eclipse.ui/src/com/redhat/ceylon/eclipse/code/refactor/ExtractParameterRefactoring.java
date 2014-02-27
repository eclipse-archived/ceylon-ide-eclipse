package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class ExtractParameterRefactoring extends AbstractRefactoring {
    
    private String newName;
    private Tree.Declaration methodOrClass;

    private static class FindFunctionVisitor 
            extends Visitor 
            implements NaturalVisitor {
        
        private final Node term;
        private Tree.Declaration declaration;
        private Tree.Declaration current;
        
        public FindFunctionVisitor(Node term) {
            this.term = term;
        }
        
        public Tree.Declaration getDefinitionNode() {
            return declaration;
        }
        
        @Override
        public void visit(Tree.MethodDefinition that) {
            Tree.Declaration outer = current;
            current = that;
            super.visit(that);
            current = outer;
        }
        
        @Override
        public void visit(Tree.ClassDefinition that) {
            Tree.Declaration outer = current;
            current = that;
            super.visit(that);
            current = outer;
        }
        
        @Override
        public void visitAny(Node node) {
            if (node == term) {
                declaration = current;
            }
            if (declaration == null) {
                super.visitAny(node);
            }
        }
        
    }
        
    public ExtractParameterRefactoring(ITextEditor editor) {
        super(editor);
        newName = guessName(node);
        FindFunctionVisitor ffv = new FindFunctionVisitor(node);
        ffv.visit(rootNode);
        methodOrClass = ffv.getDefinitionNode();
    }
    
    /*public ExtractValueRefactoring(IQuickFixInvocationContext context) {
        super(context);
        newName = guessName();
    }*/
    
    @Override
    boolean isEnabled() {
        return node instanceof Tree.Term && 
                methodOrClass!=null &&
                !methodOrClass.getDeclarationModel().isActual();
    }

    public String getName() {
        return "Extract Parameter";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        Declaration existing = node.getScope()
                .getMemberOrParameter(node.getUnit(), newName, null, false);
        if (null!=existing) {
            return createWarningStatus("An existing declaration named '" +
                    newName + "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        TextChange tfc = newLocalChange();
        extractInFile(tfc);
        return tfc;
    }

    private void extractInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = tfc.getCurrentDocument(null);
        
        tfc.addEdit(new ReplaceEdit(getStartOffset(node), 
                getLength(node), newName));
        Tree.ParameterList pl;
        if (methodOrClass instanceof Tree.MethodDefinition) {
            List<Tree.ParameterList> pls = 
                    ((Tree.MethodDefinition) methodOrClass).getParameterLists();
            if (pls.isEmpty()) {
                return; //TODO
            }
            pl = pls.get(0);
        }
        else if (methodOrClass instanceof Tree.ClassDefinition) {
            pl = ((Tree.ClassDefinition) methodOrClass).getParameterList();
            if (pl==null) {
                return; //TODO
            }
        }
        else {
            return;
        }
        String text;
        try {
            text = doc.get(getStartOffset(node), getLength(node));
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return;
        }
        Tree.Term term = (Tree.Term) node;
        ProducedType tm = term.getTypeModel();
        String typeDec;
        if (tm==null || tm.isUnknown()) {
            typeDec = "dynamic";
        }
        else {
            ProducedType type = node.getUnit().denotableType(tm);
            typeDec = type.getProducedTypeName();
            HashSet<Declaration> decs = new HashSet<Declaration>();
            importType(decs, type, rootNode);
            applyImports(tfc, decs, rootNode, doc);
        }
        tfc.addEdit(new InsertEdit(pl.getStopIndex(), 
                (pl.getParameters().isEmpty()?"":", ") + 
                typeDec + " " + newName + " = " + text));
    }

    public void setNewName(String text) {
        newName = text;
    }
    
    public String getNewName() {
        return newName;
    }
    
}
