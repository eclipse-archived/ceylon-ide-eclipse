package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importProposals;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoLocation;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.performChange;
import static com.redhat.ceylon.eclipse.util.Indents.indents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.imports.importsJ2C;
import com.redhat.ceylon.eclipse.code.refactor.CreateUnitChange;
import com.redhat.ceylon.eclipse.code.wizard.SelectNewUnitWizard;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;

import ceylon.interop.java.CeylonList;

class CreateInNewUnitProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {
    
    private final IFile file;
    private final DefinitionGenerator dg;
    private final Tree.CompilationUnit rootNode;

    CreateInNewUnitProposal(IFile file, DefinitionGenerator dg, 
            Tree.CompilationUnit rootNode) {
        this.file = file;
        this.dg = dg;
        this.rootNode = rootNode;
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
        SelectNewUnitWizard w = 
                new SelectNewUnitWizard("Create in New Source File", 
                    "Create a new Ceylon source file with the missing declaration.",
                    dg.getBrokenName());
        if (w.open(file)) {
            IPackageFragment pack = w.getPackageFragment();
            String packageName = 
                    pack==null ? "" : pack.getElementName();
            boolean samePackage = 
                    packageName.equals(rootNode.getUnit()
                            .getPackage().getNameAsString());
            Change change = createChange(doc, w, samePackage);
            if (!samePackage) {
                change = new CompositeChange("Create in New Source File", 
                        new Change[] {change, importChange(doc, packageName)});
            }
            try {
                performChange(getCurrentEditor(), 
                        doc, change, 
                        "Create in New Source File");
                gotoLocation(w.getFile().getFullPath(), 0);
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    private CreateUnitChange createChange(IDocument doc, 
            SelectNewUnitWizard w,
            boolean samePackage) {
        return new CreateUnitChange(w.getFile(), 
                w.includePreamble(), 
                getText(doc, samePackage), 
                w.getProject(),
                "Create in New Source File");
    }

    private TextFileChange importChange(IDocument doc, 
            String packageName) {
        TextFileChange tfc = 
                new TextFileChange("Add Import", file);
        Tree.Import importNode = 
                importProposals().findImportNode(rootNode, packageName);
        if (importNode==null) {
            tfc.setEdit(new InsertEdit(
                    (int) importProposals().getBestImportInsertPosition(rootNode), 
                    "import " + packageName + 
                    " { " + dg.getBrokenName() + " }" + 
                    indents().getDefaultLineDelimiter(doc)));
        }
        else {
            tfc.setEdit(new InsertEdit(
                    (int) importProposals().getBestImportMemberInsertPosition(importNode), 
                    "," + indents().getDefaultLineDelimiter(doc) + 
                    indents().getDefaultIndent() + dg.getBrokenName()));
        }
        return tfc;
    }

    private String getText(IDocument doc, boolean samePackage) {
        String delim = indents().getDefaultLineDelimiter(doc);
        String definition = samePackage ? 
                dg.generate("", delim) :
                dg.generateShared("", delim);
        List<Declaration> imports = new ArrayList<Declaration>();
        resolveImports(imports, dg.getReturnType());
        if (dg.getParameters()!=null) {
            resolveImports(imports, dg.getParameters().values());
        }
        String imps = importsJ2C.importCleaner().createImports(
                new CeylonList<>(TypeDescriptor.klass(Declaration.class), imports),
                doc);
        if (!imps.isEmpty()) {
            definition = imps + delim + delim + definition;
        }
        return definition;
    }

    static void addCreateInNewUnitProposal(Collection<ICompletionProposal> proposals, 
            DefinitionGenerator dg, IFile file, Tree.CompilationUnit rootNode) {        
        proposals.add(new CreateInNewUnitProposal(file, dg, rootNode));
    }
    
    private static void resolveImports(List<Declaration> imports, 
            Collection<Type> producedTypes) {
        if (producedTypes!=null) {
            for (Type pt : producedTypes) {
                resolveImports(imports, pt);
            }
        }
    }

    private static void resolveImports(List<Declaration> imports, Type pt) {
        if (pt!=null) {
            if (pt.isUnknown() || pt.isNothing()) {
                //nothing to do
            }
            else if (pt.isUnion()) {
                resolveImports(imports, pt.getCaseTypes());
            }
            else if (pt.isIntersection()) {
                resolveImports(imports, pt.getSatisfiedTypes());
            }
            else if (pt.isTypeParameter()) {
                TypeParameter typeParam = 
                        (TypeParameter) 
                            pt.getDeclaration();
                if (typeParam.isConstrained()) {
                    resolveImports(imports, typeParam.getCaseTypes());
                    resolveImports(imports, typeParam.getSatisfiedTypes());
                }
                if (typeParam.isDefaulted()) {
                    resolveImports(imports, typeParam.getDefaultTypeArgument());
                }
            }
            else {
                TypeDeclaration td = pt.getDeclaration();
                resolveImports(imports, pt.getTypeArgumentList());
                Package p = td.getUnit().getPackage();
                if (!p.getQualifiedNameString().isEmpty() && 
                    !p.getQualifiedNameString().equals(Module.LANGUAGE_MODULE_NAME)) {
                    if (!imports.contains(td)) {
                        imports.add(td);
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