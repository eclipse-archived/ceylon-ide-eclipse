package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.IMPORT;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.imports.ModuleImportUtil;

public class ExportModuleImportProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private IProject project;
    private Unit unit; 
    String name;
    
    ExportModuleImportProposal(IProject project, Unit unit, String name) {
        this.project = project;
        this.unit = unit;
        this.name = name;
    }
    
    @Override
    public void apply(IDocument document) {
        ModuleImportUtil.exportModuleImports(project, 
                unit.getPackage().getModule(), 
                name);
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
        return "Export 'import " + name + "' to clients of module";
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

	static void addExportModuleImportProposal(
	        Collection<ICompletionProposal> proposals, IProject project,
	        Node node) {
	    if (node instanceof Tree.SimpleType) {
	    	Declaration dec = ((Tree.SimpleType) node).getDeclarationModel();
		    proposals.add(new ExportModuleImportProposal(project, node.getUnit(), 
		    		dec.getUnit().getPackage().getModule().getNameAsString()));
	    }
	}

}
