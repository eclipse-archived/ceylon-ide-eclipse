package com.redhat.ceylon.eclipse.code.imports;

import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

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
		Declaration prop = null;
		for (Package p: cu.getUnit().getPackage().getModule()
		        .getAllPackages()) {
			Declaration d = p.getMember(name, null); //TODO: pass sig
			if (d!=null && d.isToplevel() && 
					d.isShared() && !d.isAnonymous()) {
				if (prop==null) {
					prop=d;
				}
				else {
					//ambiguous
					//TODO: pop up a window!
					prop=null;
					break;
				}
			}
			if (prop!=null && !proposals.contains(prop)) {
				proposals.add(prop);
			}
		}
	}
}