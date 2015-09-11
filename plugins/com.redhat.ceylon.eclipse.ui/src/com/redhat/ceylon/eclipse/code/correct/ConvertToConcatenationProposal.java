package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_MID;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_START;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class ConvertToConcatenationProposal extends CorrectionProposal {

    private ConvertToConcatenationProposal(String name, Change change) {
        super(name, change, null);
    }

    static void addConvertToConcatenationProposal(Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, final Node node, IDocument doc) {
        class TemplateVisitor extends Visitor {
            Tree.StringTemplate result;
            @Override
            public void visit(Tree.StringTemplate that) {
                if (that.getStartIndex()<=node.getStartIndex() &&
                    that.getEndIndex()>=node.getEndIndex()) {
                    result = that;
                }
                super.visit(that);
            }
        }
        TemplateVisitor tv = new TemplateVisitor();
        tv.visit(cu);
        Tree.StringTemplate template = tv.result;
        if (template!=null) {
            TextFileChange change = new TextFileChange("Convert to Concatenation", file);
            change.setEdit(new MultiTextEdit());
            List<Tree.StringLiteral> literals = template.getStringLiterals();
            List<Tree.Expression> expressions = template.getExpressions();
            for (int i=0; i<literals.size(); i++) {
                Tree.StringLiteral s = literals.get(i);
                int stt = s.getToken().getType();
                if (stt==STRING_END||stt==STRING_MID) {
                    change.addEdit(new ReplaceEdit(s.getStartIndex(), 2, " + \""));
                }
                if (stt==STRING_START||stt==STRING_MID) {
                    change.addEdit(new ReplaceEdit(s.getStartIndex()-1, 2, "\" + "));
                }
                if (i<expressions.size()) {
                    Tree.Expression e = expressions.get(i);
                    if (!e.getTypeModel().isSubtypeOf(node.getUnit().getStringDeclaration().getType())) {
                        change.addEdit(new InsertEdit(e.getEndIndex(), ".string"));
                    }
                }
            }
            proposals.add(new ConvertToConcatenationProposal("Convert to string concatenation", change));
        }
    }
    
    
}