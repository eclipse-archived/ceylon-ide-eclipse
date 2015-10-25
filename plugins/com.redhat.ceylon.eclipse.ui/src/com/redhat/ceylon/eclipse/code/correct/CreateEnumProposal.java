package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.INTERFACE;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclaration;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.model.ModifiableSourceFile;
import com.redhat.ceylon.eclipse.core.typechecker.ModifiablePhasedUnit;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.Nodes;

class CreateEnumProposal extends CorrectionProposal {
    
    CreateEnumProposal(String def, String desc, Image image, 
            int offset, TextFileChange change) {
        super(desc, change, new Region(offset, 0), image);
    }
    
    static void addCreateEnumProposal(
            Tree.CompilationUnit rootNode, 
            Node node, 
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IProject project) {
        Node idn = Nodes.getIdentifyingNode(node);
        if (idn==null) return;
        String brokenName = idn.getText();
        if (brokenName.isEmpty()) return;
        Tree.Declaration dec = findDeclaration(rootNode, node);
        if (dec instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition cd = (Tree.ClassDefinition) dec;
            Tree.CaseTypes caseTypes = cd.getCaseTypes();
            if (caseTypes!=null) {
                Tree.TypeParameterList tpl = cd.getTypeParameterList();
                if (caseTypes.getTypes().contains(node)) {
                    addCreateEnumProposal(proposals, project, 
                            "class " + brokenName + parameters(tpl) +
                                parameters(cd.getParameterList()) +
                                " extends " + cd.getDeclarationModel().getName() + 
                                parameters(tpl) + 
                                arguments(cd.getParameterList()) + " {}", 
                            "class '"+ brokenName + parameters(tpl) +
                            parameters(cd.getParameterList()) + "'", 
                            CeylonResources.CLASS, rootNode, cd);
                }
                if (caseTypes.getBaseMemberExpressions().contains(node)) {
                    addCreateEnumProposal(proposals, project, 
                            "object " + brokenName + 
                                " extends " + cd.getDeclarationModel().getName() + 
                                parameters(tpl) + 
                                arguments(cd.getParameterList()) + " {}", 
                            "object '"+ brokenName + "'", 
                            ATTRIBUTE, rootNode, cd);
                }
            }
        }
        if (dec instanceof Tree.InterfaceDefinition) {
            Tree.InterfaceDefinition cd = (Tree.InterfaceDefinition) dec;
            Tree.CaseTypes caseTypes = cd.getCaseTypes();
            if (caseTypes!=null) {
                Tree.TypeParameterList tpl = cd.getTypeParameterList();
                if (caseTypes.getTypes().contains(node)) {
                    addCreateEnumProposal(proposals, project, 
                            "interface " + brokenName + parameters(tpl) +
                                " satisfies " + cd.getDeclarationModel().getName() + 
                                parameters(tpl) + " {}", 
                            "interface '"+ brokenName + parameters(tpl) +  "'", 
                            INTERFACE, rootNode, cd);
                }
                if (caseTypes.getBaseMemberExpressions().contains(node)) {
                    addCreateEnumProposal(proposals, project, 
                            "object " + brokenName + 
                                " satisfies " + cd.getDeclarationModel().getName() + 
                                parameters(tpl) + " {}", 
                            "object '"+ brokenName + "'", 
                            ATTRIBUTE, rootNode, cd);
                }
            }
        }
    }
    
    private static void addCreateEnumProposal(
            Collection<ICompletionProposal> proposals, 
            String def, String desc, Image image, 
            ModifiablePhasedUnit unit, 
            Tree.Statement statement) {
        IFile file = unit.getResourceFile();
        if (file != null) {
            TextFileChange change = 
                    new TextFileChange("Create Enumerated", 
                            file);
            IDocument doc = getDocument(change);
            String indent = getIndent(statement, doc);
            String s = 
                    indent + def + 
                    getDefaultLineDelimiter(doc);
            int offset = statement.getEndIndex()+1;
            if (offset>doc.getLength()) {
                offset = doc.getLength();
                s = getDefaultLineDelimiter(doc) + s;
            }
            change.setEdit(new InsertEdit(offset, s));
            proposals.add(new CreateEnumProposal(def, 
                    "Create enumerated " + desc, 
                    image, offset + def.indexOf("{}")+1, 
                    change));
        }
    }

    private static void addCreateEnumProposal(
            Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Tree.CompilationUnit rootNode, Tree.TypeDeclaration cd) {
            TypecheckerUnit u = rootNode.getUnit();
        if (u instanceof ModifiableSourceFile) {
            ModifiableSourceFile cu = (ModifiableSourceFile) u;
                addCreateEnumProposal(proposals, def, desc, image, 
                        cu.getPhasedUnit(), cd);
        }
    }

    private static String parameters(Tree.ParameterList pl) {
        StringBuilder result = new StringBuilder();
        if (pl==null ||
                pl.getParameters().isEmpty()) {
            result.append("()");
        }
        else {
            result.append("(");
            int len = pl.getParameters().size(), i=0;
            for (Tree.Parameter p: pl.getParameters()) {
                if (p!=null) {
                    if (p instanceof Tree.ParameterDeclaration) {
                        Tree.ParameterDeclaration pd = 
                                (Tree.ParameterDeclaration) p;
                        Tree.TypedDeclaration td = pd.getTypedDeclaration();
                        result.append(td.getType().getTypeModel().asString()) 
                                .append(" ")
                                .append(td.getIdentifier().getText());
                    }
                    else if (p instanceof Tree.InitializerParameter) {
                        Tree.InitializerParameter ip = 
                                (Tree.InitializerParameter) p;
                        result.append(p.getParameterModel().getType().asString()) 
                            .append(" ")
                            .append(ip.getIdentifier().getText());
                    }
                    //TODO: easy to add back in:
                    /*if (p instanceof Tree.FunctionalParameterDeclaration) {
                        Tree.FunctionalParameterDeclaration fp = (Tree.FunctionalParameterDeclaration) p;
                        for (Tree.ParameterList ipl: fp.getParameterLists()) {
                            parameters(ipl, label);
                        }
                    }*/
                }
                if (++i<len) result.append(", ");
            }
            result.append(")");
        }
        return result.toString();
    }
    
    private static String parameters(Tree.TypeParameterList tpl) {
        StringBuilder result = new StringBuilder();
        if (tpl!=null &&
                !tpl.getTypeParameterDeclarations().isEmpty()) {
            result.append("<");
            int len = tpl.getTypeParameterDeclarations().size();
            int i=0;
            for (Tree.TypeParameterDeclaration p: 
                    tpl.getTypeParameterDeclarations()) {
                result.append(p.getIdentifier().getText());
                if (++i<len) result.append(", ");
            }
            result.append(">");
        }
        return result.toString();
    }
    
    private static String arguments(Tree.ParameterList pl) {
        StringBuilder result = new StringBuilder();
        if (pl==null ||
                pl.getParameters().isEmpty()) {
            result.append("()");
        }
        else {
            result.append("(");
            int len = pl.getParameters().size(), i=0;
            for (Tree.Parameter p: pl.getParameters()) {
                if (p!=null) {
                    Tree.Identifier id;
                    if (p instanceof Tree.InitializerParameter) {
                        Tree.InitializerParameter ip = 
                                (Tree.InitializerParameter) p;
                        id = ip.getIdentifier();
                    }
                    else if (p instanceof Tree.ParameterDeclaration) {
                        Tree.ParameterDeclaration pd = 
                                (Tree.ParameterDeclaration) p;
                        id = pd.getTypedDeclaration().getIdentifier();
                    }
                    else {
                        continue;
                    }
                    result.append(id.getText());
                    //TODO: easy to add back in:
                    /*if (p instanceof Tree.FunctionalParameterDeclaration) {
                        Tree.FunctionalParameterDeclaration fp = (Tree.FunctionalParameterDeclaration) p;
                        for (Tree.ParameterList ipl: fp.getParameterLists()) {
                            parameters(ipl, label);
                        }
                    }*/
                }
                if (++i<len) result.append(", ");
            }
            result.append(")");
        }
        return result.toString();
    }
    
}