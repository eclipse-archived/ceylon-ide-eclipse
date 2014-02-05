package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.JDK_MODULE_VERSION;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleQueryType;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class ModuleImportProposal {

	static void addModuleImportProposals(Tree.CompilationUnit cu,
			Collection<ICompletionProposal> proposals, IProject project,
			TypeChecker tc, Node node) {
		if (cu.getUnit().getPackage().getModule().isDefault()) {
			return;
		}
		if (node instanceof Tree.Import) {
			node = ((Tree.Import) node).getImportPath();
		}
		List<Tree.Identifier> ids = ((Tree.ImportPath) node).getIdentifiers();
		String pkg = formatPath(ids);
		if (JDKUtils.isJDKAnyPackage(pkg)) {
			for (String mod: new TreeSet<String>(JDKUtils.getJDKModuleNames())) {
				if (JDKUtils.isJDKPackage(mod, pkg)) {
					proposals.add(new AddModuleImportProposal(project, cu.getUnit(), mod, 
							JDK_MODULE_VERSION));
					return;
				}
			}
		}
		for (int i=ids.size(); i>0; i--) {
			String pn = formatPath(ids.subList(0, i));
			ModuleQuery query = new ModuleQuery(pn, getModuleQueryType(project));
			query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
			query.setCount(2l);
			ModuleSearchResult msr = tc.getContext().getRepositoryManager().searchModules(query);
			ModuleDetails md = msr.getResult(pn);
			if (md!=null) {
				proposals.add(new AddModuleImportProposal(project, cu.getUnit(), md));
			}
			if (!msr.getResults().isEmpty()) break;
		}
	}

}