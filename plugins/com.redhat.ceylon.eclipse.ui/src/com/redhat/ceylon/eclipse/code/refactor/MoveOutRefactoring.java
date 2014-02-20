package com.redhat.ceylon.eclipse.code.refactor;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.Indents;

public class MoveOutRefactoring extends AbstractRefactoring {
    
    private Tree.Declaration declaration;

    public MoveOutRefactoring(ITextEditor editor) {
        super(editor);
        if (editor instanceof CeylonEditor && 
                editor.getSelectionProvider()!=null) {
            init((ITextSelection) editor.getSelectionProvider()
                    .getSelection());
        }
    }

    private void init(ITextSelection selection) {
        if (node instanceof Tree.Declaration) {
            declaration = (Tree.Declaration) node;
        }
    }

    @Override
    boolean isEnabled() {
        return (node instanceof Tree.MethodDefinition || 
                node instanceof Tree.ClassDefinition) &&
                    ((Tree.Declaration) node).getDeclarationModel()
                            .isClassOrInterfaceMember();
    }
    
    public String getName() {
        return "Move Out";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        TextChange tfc = newLocalChange();
        tfc.setEdit(new MultiTextEdit());
        final Scope container = declaration.getDeclarationModel().getContainer();
        class FindContainer extends Visitor {
            Tree.Declaration dec;
            @Override
            public void visit(Tree.Declaration that) {
                super.visit(that);
                if (that.getDeclarationModel().equals(container)) {
                    dec = that;
                }
            }
        }
        FindContainer fc = new FindContainer();
        rootNode.visit(fc);
        Tree.TypeDeclaration owner = (Tree.TypeDeclaration) fc.dec;
        final String qtype = owner.getDeclarationModel().getType()
                .getProducedTypeName(declaration.getUnit());
        String indent = Indents.getIndent(owner, document);
        String originalIndent = Indents.getIndent(declaration, document);
        String delim = Indents.getDefaultLineDelimiter(document);
        StringBuilder sb = new StringBuilder();
        if (declaration instanceof Tree.MethodDefinition) {
            Tree.MethodDefinition md = (Tree.MethodDefinition) declaration;
            appendAnnotations(sb, md);
            sb.append(toString(md.getType())).append(" ")
                .append(toString(md.getIdentifier()));
            List<Tree.ParameterList> parameterLists = md.getParameterLists();
            if (parameterLists.isEmpty()) {
                throw new IllegalStateException("missing parameter list"); //TODO: do it in checkInitialConditions()
            }
            Tree.ParameterList first = parameterLists.get(0);
            sb.append(toString(first));
            if (!first.getParameters().isEmpty()) {
                sb.insert(sb.length()-1, ", ");
            }
            sb.insert(sb.length()-1, qtype+ " it");
            for (int i=1; i<parameterLists.size(); i++) {
                sb.append(toString(parameterLists.get(i)));
            }
            if (md.getTypeConstraintList()!=null) {
                appendConstraints(indent, delim, sb, 
                        md.getTypeConstraintList());
            }
            if (md.getBlock()!=null) {
                appendBody(container, indent, originalIndent, 
                        delim, sb, md.getBlock());
            }
        }
        else if (declaration instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition cd = (Tree.ClassDefinition) declaration;
            appendAnnotations(sb, cd);
            sb.append("class ")
                .append(toString(cd.getIdentifier()));
            Tree.ParameterList first = cd.getParameterList();
            sb.append(toString(first));
            if (!first.getParameters().isEmpty()) {
                sb.insert(sb.length()-1, ", ");
            }
            sb.insert(sb.length()-1, qtype+ " it");
            if (cd.getCaseTypes()!=null) {
                appendClause(indent, delim, sb, cd.getCaseTypes());
            }
            if (cd.getExtendedType()!=null) {
                appendClause(indent, delim, sb, cd.getExtendedType());
            }
            if (cd.getSatisfiedTypes()!=null) {
                appendClause(indent, delim, sb, cd.getSatisfiedTypes());
            }
            if (cd.getTypeConstraintList()!=null) {
                appendConstraints(indent, delim, sb, 
                        cd.getTypeConstraintList());
            }
            if (cd.getClassBody()!=null) {
                appendBody(container, indent, originalIndent, 
                        delim, sb, cd.getClassBody());
            }
        }
        else {
            throw new IllegalStateException();
        }
        tfc.addEdit(new InsertEdit(owner.getStopIndex()+1, 
                delim+indent+delim+indent+sb));
        tfc.addEdit(new DeleteEdit(declaration.getStartIndex(), 
                declaration.getStopIndex()-declaration.getStartIndex()+1));
        return tfc;
    }

    private void appendAnnotations(StringBuilder sb, Tree.Declaration d) {
        if (!d.getAnnotationList().getAnnotations().isEmpty()) {
            sb.append(toString(d.getAnnotationList())
                .replaceAll("shared|default|formal|actual", ""))
                .append(" ");
        }
    }

    private void appendConstraints(String indent, String delim,
            StringBuilder sb, Tree.TypeConstraintList tcl) {
        for (Tree.TypeConstraint tc: tcl.getTypeConstraints()) {
            appendClause(indent, delim, sb, tc);
        }
    }

    private void appendClause(String indent, String delim, StringBuilder sb,
            Node clause) {
        sb.append(delim).append(indent)
            .append(Indents.getDefaultIndent())
            .append(Indents.getDefaultIndent())
            .append(toString(clause));
    }

    private void appendBody(final Scope container, String indent, String originalIndent, 
            String delim, StringBuilder sb, Tree.Body block) {
        sb.append(" {");
        for (final Tree.Statement st: block.getStatements()) {
            final StringBuilder stb = new StringBuilder(toString(st));
            new Visitor() {
                int offset = 0;
                @Override
                public void visit(Tree.BaseMemberOrTypeExpression that) {
                    if (that.getDeclaration().getContainer().equals(container)) {
                        stb.insert(that.getStartIndex()-st.getStartIndex()+offset, "it.");
                        offset+=3;
                    }
                }
                @Override
                public void visit(Tree.QualifiedMemberOrTypeExpression that) {
                    if (that.getPrimary() instanceof Tree.This) {
                        stb.replace(that.getStartIndex()+offset-st.getStartIndex(), 
                                that.getPrimary().getStopIndex()+offset+1-st.getStartIndex(), 
                                "it");
                        offset+=2-that.getPrimary().getStopIndex()-that.getStartIndex()+1;
                    }
                    if (that.getPrimary() instanceof Tree.Outer) {
                        stb.replace(that.getStartIndex()+offset-st.getStartIndex(), 
                                that.getPrimary().getStopIndex()+offset+1-st.getStartIndex(), 
                                "this");
                        offset-=1;
                    }
                }
            }.visit(st);
            sb.append(delim).append(indent)
                .append(Indents.getDefaultIndent())
                .append(stb.toString().replaceAll(delim+originalIndent, delim+indent));
        }
        sb.append(delim).append(indent).append("}");
    }
    
}
