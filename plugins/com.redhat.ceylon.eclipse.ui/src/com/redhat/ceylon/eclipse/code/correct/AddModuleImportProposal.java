package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.JDK_MODULE_VERSION;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.IMPORT;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleQueryType;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.imports.ModuleImportUtil;

public class AddModuleImportProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private IProject project;
    private Unit unit; 
    String name; 
    String version;
    
    AddModuleImportProposal(IProject project, Unit unit, ModuleDetails details) {
        this.project = project;
        this.unit = unit;
        this.name = details.getName();
        this.version = details.getLastVersion().getVersion();
    }
    
    AddModuleImportProposal(IProject project, Unit unit, String name, String version) {
        this.project = project;
        this.unit = unit;
        this.name = name;
        this.version = version;
    }
    
    @Override
    public void apply(IDocument document) {
        ModuleImportUtil.addModuleImport(project, 
                unit.getPackage().getModule(), 
                name, version);
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Add 'import " + name + "' to module descriptor";
    }

    @Override
    public Image getImage() {
        return IMPORT;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return CorrectionUtil.styleProposal(getDisplayString());
    }

	static void addModuleImportProposals(Collection<ICompletionProposal> proposals, 
			IProject project, TypeChecker tc, Node node) {
		Unit unit = node.getUnit();
		if (unit.getPackage().getModule().isDefault()) {
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
					proposals.add(new AddModuleImportProposal(project, unit, mod, 
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
			ModuleSearchResult msr = tc.getContext().getRepositoryManager()
					.searchModules(query);
			ModuleDetails md = msr.getResult(pn);
			if (md!=null) {
				proposals.add(new AddModuleImportProposal(project, unit, md));
			}
			if (!msr.getResults().isEmpty()) break;
		}
	}

}
