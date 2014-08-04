package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.EditorUtil;

class VerboseRefinementProposal extends CorrectionProposal {

    private VerboseRefinementProposal(Change change) {
        super("Convert to verbose refinement", change, null);
    }

    static void addVerboseRefinementProposal(
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.Statement statement, Tree.CompilationUnit cu) {
        if (statement instanceof Tree.SpecifierStatement) {
            Tree.SpecifierStatement ss = (Tree.SpecifierStatement) statement;
            if (ss.getRefinement()) {
                TextFileChange change = new TextFileChange("Convert to Verbose Refinement", file);
                change.setEdit(new MultiTextEdit());
                Tree.Expression e = ss.getSpecifierExpression().getExpression();
                if (e!=null && !isTypeUnknown(e.getTypeModel())) {
                    Unit unit = ss.getUnit();
                    ProducedType t = unit.denotableType(e.getTypeModel());
                    HashSet<Declaration> decs = new HashSet<Declaration>();
                    ImportProposals.importType(decs, t, cu);
                    applyImports(change, decs, cu, EditorUtil.getDocument(change));
                    String type = t.getProducedTypeName(unit);
                    change.addEdit(new InsertEdit(statement.getStartIndex(), 
                            "shared actual " + type + " "));
                    proposals.add(new VerboseRefinementProposal(change));
                }
            }
        }
    }

}