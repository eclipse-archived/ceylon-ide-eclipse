package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.DocLinks.hasPackage;
import static com.redhat.ceylon.eclipse.util.DocLinks.nameRegion;
import static com.redhat.ceylon.eclipse.util.Nodes.findImport;
import static com.redhat.ceylon.eclipse.util.Nodes.getAbstraction;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Alias;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class EnterAliasRefactoring extends AbstractRefactoring {
    
    private final class EnterAliasVisitor extends Visitor {
        private final TextChange change;

        private EnterAliasVisitor(TextChange change) {
            this.change = change;
        }

        @Override
        public void visit(Tree.StaticMemberOrTypeExpression that) {
            super.visit(that);
            addEdit(document, that.getIdentifier(), 
                    that.getDeclaration());
        }

        @Override
        public void visit(Tree.SimpleType that) {
            super.visit(that);
            addEdit(document, that.getIdentifier(), 
                    that.getDeclarationModel());
        }

        @Override
        public void visit(Tree.MemberLiteral that) {
            super.visit(that);
            addEdit(document, that.getIdentifier(), 
                    that.getDeclaration());
        }

        @Override
        public void visit(Tree.DocLink that) {
            super.visit(that);
            Declaration base = that.getBase();
            if (!hasPackage(that) && isReference(base)) {
                Region region = nameRegion(that, 0);
                change.addEdit(new ReplaceEdit(region.getOffset(), 
                        region.getLength(), newName));
            }
        }

        private void addEdit(IDocument document, Tree.Identifier id, 
                Declaration d) {
            if (id!=null && isReference(d)) {
                int pos = id.getStartIndex();
                int len = id.getText().length();
                change.addEdit(new ReplaceEdit(pos, len, newName));
            }
        }
    }

    private String newName;
    private Tree.ImportMemberOrType element;
    
    boolean isReference(Declaration declaration) {
        return declaration!=null && 
                getElement().getDeclarationModel()
                    .equals(getAbstraction(declaration));
    }
    
    public Tree.ImportMemberOrType getElement() {
        return element;
    }
    
    public EnterAliasRefactoring(IEditorPart editor) {
        super(editor);
        element = findImport(rootNode, node);
        if (element!=null) {
            final Alias alias = element.getAlias();
            if (alias==null) {
                newName = element.getIdentifier().getText();
            }
            else {
                newName = alias.getIdentifier().getText();
            }
        }
    }

    @Override
    public boolean getEnabled() {
        return element!=null &&
                element.getDeclarationModel()!=null;
    }

    public String getName() {
        return "Enter Alias";
    }

    public boolean forceWizardMode() {
        Declaration existing = element.getScope()
                .getMemberOrParameter(element.getUnit(), newName, null, false);
        return existing!=null;
    }
    
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        Declaration existing = element.getScope()
                .getMemberOrParameter(element.getUnit(), newName, null, false);
        if (null!=existing) {
            return createWarningStatus("An existing declaration named '" +
                    newName + "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    public TextChange createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        TextChange tfc = newLocalChange();
        renameInFile(tfc);
        return tfc;
    }
    
    int renameInFile(final TextChange change) {
        change.setEdit(new MultiTextEdit());
        Tree.Alias alias = element.getAlias();
        final Declaration dec = element.getDeclarationModel();
        
        final int adjust;
        boolean same = newName.equals(dec.getName());
        if (alias==null) {
//            if (!same) {
                change.addEdit(new InsertEdit(element.getStartIndex(), 
                        newName + "="));
                adjust = newName.length()+1;
//            }
//            else {
//                adjust = 0;
//            }
        }
        else {
            int start = alias.getStartIndex();
            int length = alias.getIdentifier().getText().length();
            if (same) {
                int stop = element.getIdentifier().getStartIndex();
                change.addEdit(new DeleteEdit(start, stop-start));
                adjust = start - stop; 
            }
            else {
                change.addEdit(new ReplaceEdit(start, length, newName));
                adjust = newName.length()-length;
            }
        }
        
        new EnterAliasVisitor(change).visit(rootNode);
        
        return adjust;
        
    }

    public void setNewName(String text) {
        newName = text;
    }
    
    public String getNewName() {
        return newName;
    }
    
}
