package com.redhat.ceylon.eclipse.code.quickfix;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.code.wizard.NewUnitWizard;

class CreateInNewUnitProposal implements ICompletionProposal {
    
    private final String desc;
    private final IFile file;
    private final String def;
    private final String unitName;
    private final Image image;

    CreateInNewUnitProposal(String desc, IFile file, String def,
            String unitName, Image image) {
        this.desc = desc;
        this.file = file;
        this.def = def;
        this.unitName = unitName;
        this.image = image;
    }

    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return image;
    }

    @Override
    public String getDisplayString() {
    	return "Create toplevel " + desc + " in new unit";
    }

    @Override
    public IContextInformation getContextInformation() {
    	return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
    	return null;
    }

    @Override
    public void apply(IDocument doc) {
    	NewUnitWizard.open(def, file, unitName, 
    	        "Create Missing Declaration in New Unit",
    	        "Create a new Ceylon compilation unit with the missing declaration.");
    }

    static void addCreateToplevelProposal(Collection<ICompletionProposal> proposals, final String def,
            final String desc, final Image image, final IFile file, final String unitName) {
        proposals.add(new CreateInNewUnitProposal(desc, file, def, unitName, image));
    }
}