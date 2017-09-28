package org.eclipse.ceylon.ide.eclipse.code.imports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

@Deprecated
final class ImportProposalsVisitor extends Visitor {
    private final CompilationUnit cu;
    private final List<Declaration> proposals;

    ImportProposalsVisitor(CompilationUnit cu,
            List<Declaration> proposals) {
        this.cu = cu;
        this.proposals = proposals;
    }

    public void visit(Tree.BaseMemberOrTypeExpression that) {
        super.visit(that);
        if (that.getDeclaration()==null) {
            String name = that.getIdentifier().getText();
            addProposal(cu, proposals, name);
        }
    }

    public void visit(Tree.BaseType that) {
        super.visit(that);
        if (that.getDeclarationModel()==null) {
            String name = that.getIdentifier().getText();
            addProposal(cu, proposals, name);
        }
    }

    private void addProposal(final Tree.CompilationUnit cu,
            final List<Declaration> proposals, String name) {
        for (Declaration p: proposals) {
            if (p.getName().equals(name)) return;
        }
        List<Declaration> possibles = new ArrayList<Declaration>();
        Module module = cu.getUnit().getPackage().getModule();
        for (Package p: module.getAllVisiblePackages()) {
            Declaration d = p.getMember(name, null, false); //TODO: pass sig
            if (d!=null && d.isToplevel() && 
                    d.isShared() && !d.isAnonymous()) {
                possibles.add(d);
            }
        }
        Declaration prop;
        if (possibles.isEmpty()) {
            prop = null;
        }
        else if (possibles.size()==1) {
            prop = possibles.get(0);
        }
        else {
            prop = CleanImportsHandler.select(possibles);
        }
        if (prop!=null) {
            proposals.add(prop);
        }
    }
}