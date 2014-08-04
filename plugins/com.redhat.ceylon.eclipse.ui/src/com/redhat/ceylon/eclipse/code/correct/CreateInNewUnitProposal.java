package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoLocation;
import static com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler.imports;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.performChange;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.eclipse.code.refactor.CreateUnitChange;
import com.redhat.ceylon.eclipse.code.wizard.SelectNewUnitWizard;
import com.redhat.ceylon.eclipse.util.Highlights;

class CreateInNewUnitProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {
    
    private final IFile file;
    private final DefinitionGenerator dg;

    CreateInNewUnitProposal(IFile file, DefinitionGenerator dg) {
        this.file = file;
        this.dg = dg;
    }

    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return dg.getImage();
    }

    @Override
    public String getDisplayString() {
        return "Create toplevel " + dg.getDescription() + 
                " in a new source file";
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
        SelectNewUnitWizard w = new SelectNewUnitWizard("Create in New Source File", 
                "Create a new Ceylon source file with the missing declaration.",
                dg.getBrokenName());
        if (w.open(file)) {
            CreateUnitChange change = new CreateUnitChange(w.getFile(), 
                    w.includePreamble(), getText(doc), w.getProject(),
                    "Create in New Source File");
            try {
                performChange(getCurrentEditor(), doc, change, 
                        "Move to New Source File");
                gotoLocation(w.getFile().getFullPath(), 0);
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    private String getText(IDocument doc) {
        String delim = getDefaultLineDelimiter(doc);
        String definition = dg.generate("", delim);
        List<Declaration> imports = new ArrayList<Declaration>();
        resolveImports(imports, dg.getReturnType());
        if (dg.getParameters()!=null) {
            resolveImports(imports, dg.getParameters().values());
        }
        String imps = imports(imports, doc);
        if (!imps.isEmpty()) {
            definition = imps + delim + delim + definition;
        }
        return definition;
    }

    static void addCreateInNewUnitProposal(Collection<ICompletionProposal> proposals, 
            DefinitionGenerator dg, IFile file) {        
        proposals.add(new CreateInNewUnitProposal(file, dg));
    }
    
    private static void resolveImports(List<Declaration> imports, 
            Collection<ProducedType> producedTypes) {
        if (producedTypes!=null) {
            for (ProducedType pt : producedTypes) {
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
                    !p.getQualifiedNameString().equals(Module.LANGUAGE_MODULE_NAME)) {
                    if (!imports.contains(pt.getDeclaration())) {
                        imports.add(pt.getDeclaration());
                    }
                }
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), false);
    }
}