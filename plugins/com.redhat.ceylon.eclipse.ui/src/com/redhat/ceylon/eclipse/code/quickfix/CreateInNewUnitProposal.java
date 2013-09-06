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
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
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
        
        List<Declaration> imports = new ArrayList<Declaration>();
        resolveImports(imports, returnType);
        resolveImports(imports, paramTypes);
        
        def = imports(imports) + "\n\n" + def;
        proposals.add(new CreateInNewUnitProposal(desc, file, def, unitName, image));
    }
    
    private static void resolveImports(List<Declaration> imports, List<ProducedType> pts) {
        if (pts != null) {
            for (ProducedType pt : pts) {
                resolveImports(imports, pt);
            }
        }
    }

    private static void resolveImports(List<Declaration> imports, ProducedType pt) {
        if (pt != null) {
            if (pt.getDeclaration() instanceof UnionType) {
                resolveImports(imports, pt.getCaseTypes());
            }
            else if (pt.getDeclaration() instanceof IntersectionType) {
                resolveImports(imports, pt.getSatisfiedTypes());
            }
            else if (pt.getDeclaration() instanceof TypeParameter) {
                TypeParameter typeParam = (TypeParameter) pt.getDeclaration();
                if (typeParam.isConstrained()) {
                    resolveImports(imports, typeParam.getCaseTypes());
                    resolveImports(imports, typeParam.getSatisfiedTypes());
                }
                if (typeParam.isDefaulted()) {
                    resolveImports(imports, typeParam.getDefaultTypeArgument());
                }
            } else {
                resolveImports(imports, pt.getTypeArgumentList());
                Package p = pt.getDeclaration().getUnit().getPackage();
                if (!p.getQualifiedNameString().isEmpty() && 
                    !p.getQualifiedNameString().equals("ceylon.language")) {
                    if (!imports.contains(pt.getDeclaration())) {
                        imports.add(pt.getDeclaration());
                    }
                }
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        return ChangeCorrectionProposal.style(getDisplayString());
    }
}