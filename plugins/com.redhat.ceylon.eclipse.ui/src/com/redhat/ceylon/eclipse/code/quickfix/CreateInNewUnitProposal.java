package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler.imports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.eclipse.code.wizard.NewUnitWizard;

class CreateInNewUnitProposal implements ICompletionProposal,
		ICompletionProposalExtension6 {
    
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

    static void addCreateToplevelProposal(Collection<ICompletionProposal> proposals, 
    		String def, String desc, Image image, IFile file, String unitName, 
            ProducedType returnType, List<ProducedType> paramTypes) {
    	//TODO: this implementation does not handle 
    	//      unions/intersections/type args/etc
    	List<Declaration> imports = new ArrayList<Declaration>();
    	if (returnType!=null) imports.add(returnType.getDeclaration());
    	if (paramTypes!=null) {
    		for (ProducedType pt: paramTypes) {
    			imports.add(pt.getDeclaration());
    		}
    	}
    	def = imports(imports) + "\n\n" + def;
        proposals.add(new CreateInNewUnitProposal(desc, file, def, unitName, image));
    }

	@Override
	public StyledString getStyledDisplayString() {
		return ChangeCorrectionProposal.style(getDisplayString());
	}
}