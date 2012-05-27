package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.compiler.typechecker.model.Util.intersectionType;
import static com.redhat.ceylon.compiler.typechecker.model.Util.unionType;
import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.BottomType;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnknownType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.editor.Util;

class SpecifyTypeProposal extends ChangeCorrectionProposal {

    final int offset;
    final int length;
    final IFile file;
    
    SpecifyTypeProposal(ProblemLocation problem, IFile file, 
            String type, TextFileChange change) {
        super("Specify type '" + type + "'", change, 10, CORRECTION);
        offset = problem.getOffset();
        length = type.length();
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addSpecifyTypeProposal(Tree.CompilationUnit cu, Node node, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file) {
        final Tree.Type type = (Tree.Type) node;
        class InferTypeVisitor extends Visitor {
            Declaration dec;
            ProducedType inferredType;
            void intersect(ProducedType pt) {
                if (pt!=null && !(pt.getDeclaration() instanceof UnknownType)) {
                    if (inferredType==null) {
                        inferredType = pt;
                    }
                    else {
                        ProducedType it = intersectionType(inferredType, pt, type.getUnit());
                        if (!(it.getDeclaration() instanceof BottomType)) {
                            inferredType = it;
                        }
                    }
                }
            }
            void union(ProducedType pt) {
                if (pt!=null && !(pt.getDeclaration() instanceof UnknownType)) {
                    if (inferredType==null) {
                        inferredType = pt;
                    }
                    else {
                        inferredType = unionType(inferredType, pt, type.getUnit());
                    }
                }
            }
            @Override public void visit(Tree.TypedDeclaration that) {
                if (that.getType()==type) {
                    dec = that.getDeclarationModel();
                    union(that.getType().getTypeModel());
                }
                super.visit(that);
            }
            @Override public void visit(Tree.AttributeDeclaration that) {
                super.visit(that);
                Term term = that.getSpecifierOrInitializerExpression()==null ? 
                        null : that.getSpecifierOrInitializerExpression().getExpression().getTerm();
                if (term instanceof Tree.BaseMemberExpression) {
                    if (((Tree.BaseMemberExpression) term).getDeclaration().equals(dec)) {
                        intersect(that.getType().getTypeModel());
                    }
                }
            }
            @Override public void visit(Tree.SpecifierStatement that) {
                super.visit(that);
                Tree.Term bme = that.getBaseMemberExpression();
                Term term = that.getSpecifierExpression()==null ? 
                        null : that.getSpecifierExpression().getExpression().getTerm();
                if (bme instanceof Tree.BaseMemberExpression) {
                	if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                		if (term!=null) union(term.getTypeModel());
                	}
                } 
                if (term instanceof Tree.BaseMemberExpression) {
                    if (((Tree.BaseMemberExpression) term).getDeclaration().equals(dec)) {
                        if (bme!=null) intersect(bme.getTypeModel());
                    }
                }
            }
            @Override public void visit(Tree.AssignmentOp that) {
                super.visit(that);
                Term rt = that.getRightTerm();
                Term lt = that.getLeftTerm();
                if (lt instanceof Tree.BaseMemberExpression) {
                    if (((Tree.BaseMemberExpression) lt).getDeclaration().equals(dec)) {
                        if (rt!=null) union(rt.getTypeModel());
                    }
                }
                if (rt instanceof Tree.BaseMemberExpression) {
                    if (((Tree.BaseMemberExpression) rt).getDeclaration().equals(dec)) {
                        if (lt!=null) intersect(lt.getTypeModel());
                    }
                }
            }
            @Override public void visit(Tree.PositionalArgument that) {
                super.visit(that);
                Tree.Term bme = that.getExpression().getTerm();
                if (bme instanceof Tree.BaseMemberExpression) {
                    if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                        intersect(that.getParameter().getType());
                    }
                }
            }
            @Override public void visit(Tree.SpecifiedArgument that) {
                super.visit(that);
                Tree.Term bme = that.getSpecifierExpression().getExpression().getTerm();
                if (bme instanceof Tree.BaseMemberExpression) {
                    if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                        intersect(that.getParameter().getType());
                    }
                }
            }
            @Override public void visit(Tree.Return that) {
                super.visit(that);
                Tree.Term bme = that.getExpression().getTerm();
                if (bme instanceof Tree.BaseMemberExpression) {
                    if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                        Declaration d = that.getDeclaration();
                        if (d instanceof TypedDeclaration) {
                            intersect(((TypedDeclaration) d).getType());
                        }
                    }
                }
            }
            //TODO: MethodDeclarations
        }
        InferTypeVisitor itv = new InferTypeVisitor();
        itv.visit(cu);
        ProducedType it = itv.inferredType;
        String explicitType = it==null ? "Object" : it.getProducedTypeName();
        TextFileChange change =  new TextFileChange("Specify Type", file);
        change.setEdit(new ReplaceEdit(problem.getOffset(), type.getText().length(), 
                explicitType)); //Note: don't use problem.getLength() because it's wrong from the problem list
        proposals.add(new SpecifyTypeProposal(problem, file, explicitType, change));
    }
    
}