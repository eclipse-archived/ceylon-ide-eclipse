package com.redhat.ceylon.eclipse.code.quickfix;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.code.imports.AddModuleImportUtil;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

public class AddModuleImportProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private IProject project;
    private Unit unit; 
    private ModuleDetails details;
    
    AddModuleImportProposal(IProject project, Unit unit, ModuleDetails details) {
        this.project = project;
        this.unit = unit;
        this.details = details;
    }
    
    @Override
    public void apply(IDocument document) {
        AddModuleImportUtil.addModuleImport(project, 
                unit.getPackage().getModule(), 
                details.getName(), details.getLastVersion());
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
        return "Add 'import " + details.getName() + "' to module descriptor";
    }

    @Override
    public Image getImage() {
        return CeylonLabelProvider.IMPORT;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return ChangeCorrectionProposal.style(getDisplayString());
    }

}
