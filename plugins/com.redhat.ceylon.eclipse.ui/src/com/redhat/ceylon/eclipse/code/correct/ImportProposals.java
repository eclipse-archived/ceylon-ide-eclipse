package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.IMPORT;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.Escaping.escapePackageName;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Nodes.getAbstraction;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.model.typechecker.model.Module.LANGUAGE_MODULE_NAME;
import static java.util.Collections.singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.ide.common.completion.FindImportNodeVisitor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Import;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;

public class ImportProposals {

    static void addImportProposals(
            Tree.CompilationUnit rootNode, 
            Node node,
            Collection<ICompletionProposal> proposals, 
            IFile file) {
        if (node instanceof Tree.BaseMemberOrTypeExpression ||
                node instanceof Tree.SimpleType) {
            Node id = getIdentifyingNode(node);
            if (id!=null) {
                String brokenName = id.getText();
                Module module = 
                        rootNode.getUnit()
                            .getPackage()
                            .getModule();
                for (Declaration dec: 
                        findImportCandidates(module, 
                                brokenName, rootNode)) {
                    ICompletionProposal ip = 
                            createImportProposal(rootNode, 
                                    file, dec);
                    if (ip!=null) {
                        proposals.add(ip);
                    }
                }
            }
        }
    }
    
    private static Set<Declaration> findImportCandidates(
            Module module, String name, 
            Tree.CompilationUnit rootNode) {
        Set<Declaration> result = 
                new HashSet<Declaration>();
        for (Package pkg: module.getAllVisiblePackages()) {
            if (!pkg.getNameAsString().isEmpty()) {
                Declaration member = 
                        pkg.getMember(name, null, false);
                if (member!=null) {
                    result.add(member);
                }
            }
        }
        /*if (result.isEmpty()) {
            for (Package pkg: module.getAllPackages()) {
                for (Declaration member: pkg.getMembers()) {
                    if (!isImported(member, cu)) {
                        int dist = getLevenshteinDistance(name, member.getName());
                        //TODO: would it be better to just sort by dist, and
                        //      then select the 3 closest possibilities?
                        if (dist<=name.length()/3+1) {
                            result.add(member);
                        }
                    }
                }
            }
        }*/
        return result;
    }
    
    private static ICompletionProposal createImportProposal(
            Tree.CompilationUnit rootNode, 
            IFile file, Declaration declaration) {
        TextFileChange change = 
                new TextFileChange("Add Import", file);
        IDocument doc = EditorUtil.getDocument(change);
        List<InsertEdit> ies = 
                importEdits(rootNode, 
                        singleton(declaration), 
                        null, null, doc);
        if (ies.isEmpty()) return null;
        change.setEdit(new MultiTextEdit());
        for (InsertEdit ie: ies) {
            change.addEdit(ie);
        }
        String proposedName = declaration.getName();
        /*String brokenName = id.getText();
        if (!brokenName.equals(proposedName)) {
            change.addEdit(new ReplaceEdit(id.getStartIndex(), brokenName.length(), 
                    proposedName));
        }*/
        String pname = 
                declaration.getUnit().getPackage()
                        .getNameAsString();
        String description = 
                "Add import of '" + proposedName + "'" + 
                " in package '" + pname + "'";
        return new CorrectionProposal(description, change, null, IMPORT) {
            @Override
            public StyledString getStyledDisplayString() {
                return Highlights.styleProposal(getDisplayString(), true);
            }
        };
    }
    
    public static List<InsertEdit> importEdits(
            Tree.CompilationUnit rootNode,
            Iterable<Declaration> declarations, 
            Iterable<String> aliases,
            Declaration declarationBeingDeleted, 
            IDocument doc) {
        String delim = getDefaultLineDelimiter(doc);
        List<InsertEdit> result = 
                new ArrayList<InsertEdit>();
        Set<Package> packages = new HashSet<Package>();
        for (Declaration declaration: declarations) {
            packages.add(declaration.getUnit().getPackage());
        }
        for (Package p: packages) {
            StringBuilder text = new StringBuilder();
            if (aliases==null) {
                for (Declaration d: declarations) {
                    if (d.getUnit().getPackage().equals(p)) {
                        text.append(",")
                            .append(delim)
                            .append(getDefaultIndent())
                            .append(escapeName(d));
                    }
                }
            }
            else {
                Iterator<String> aliasIter = 
                        aliases.iterator();
                for (Declaration d: declarations) {
                    String alias = aliasIter.next();
                    if (d.getUnit().getPackage().equals(p)) {
                        text.append(",")
                            .append(delim)
                            .append(getDefaultIndent());
                        if (alias!=null && 
                                !alias.equals(d.getName())) {
                            text.append(alias).append('=');
                        }
                        text.append(escapeName(d));
                    }
                }
            }
            Tree.Import importNode = 
                    findImportNode(rootNode, 
                            p.getNameAsString());
            if (importNode!=null) {
                Tree.ImportMemberOrTypeList imtl = 
                        importNode.getImportMemberOrTypeList();
                if (imtl.getImportWildcard()!=null) {
                    //Do nothing
                }
                else {
                    int insertPosition = 
                            getBestImportMemberInsertPosition(importNode);
                    if (declarationBeingDeleted!=null &&
                        imtl.getImportMemberOrTypes().size()==1 &&
                        imtl.getImportMemberOrTypes().get(0)
                            .getDeclarationModel()
                            .equals(declarationBeingDeleted)) {
                        text.delete(0, 2);
                    }
                    result.add(new InsertEdit(insertPosition, 
                            text.toString()));
                }
            } 
            else {
                int insertPosition = 
                        getBestImportInsertPosition(rootNode);
                text.delete(0, 2);
                text.insert(0, 
                        "import " + 
                        escapePackageName(p) + 
                        " {" + 
                        delim)
                    .append(delim + "}"); 
                if (insertPosition==0) {
                    text.append(delim);
                }
                else {
                    text.insert(0, delim);
                }
                result.add(new InsertEdit(insertPosition, 
                        text.toString()));
            }
        }
        return result;
    }
    
    public static List<TextEdit> importEditForMove(
            Tree.CompilationUnit rootNode,
            Iterable<Declaration> declarations, 
            Iterable<String> aliases,
            String newPackageName, String oldPackageName, 
            IDocument doc) {
        String delim = getDefaultLineDelimiter(doc);
        List<TextEdit> result = new ArrayList<TextEdit>();
        Set<Declaration> set = new HashSet<Declaration>();
        for (Declaration d: declarations) {
            set.add(d);
        }
        StringBuilder text = new StringBuilder();
        if (aliases==null) {
            for (Declaration d: declarations) {
                text.append(",")
                    .append(delim).append(getDefaultIndent())
                    .append(d.getName());
            }
        }
        else {
            Iterator<String> aliasIter = aliases.iterator();
            for (Declaration d: declarations) {
                String alias = aliasIter.next();
                text.append(",")
                    .append(delim).append(getDefaultIndent());
                if (alias!=null && !alias.equals(d.getName())) {
                    text.append(alias).append('=');
                }
                text.append(d.getName());
            }
        }
        Tree.Import oldImportNode = 
                findImportNode(rootNode, oldPackageName);
        if (oldImportNode!=null) {
            Tree.ImportMemberOrTypeList imtl = 
                    oldImportNode.getImportMemberOrTypeList();
            if (imtl!=null) {
                int remaining = 0;
                for (Tree.ImportMemberOrType imt: 
                        imtl.getImportMemberOrTypes()) {
                    if (!set.contains(imt.getDeclarationModel())) {
                        remaining++;
                    }
                }
                if (remaining==0) {
                    int start = oldImportNode.getStartIndex();
                    int stop = oldImportNode.getStopIndex();
                    result.add(new DeleteEdit(start, stop-start+1));
                }
                else {
                    //TODO: format it better!!!!
                    StringBuilder sb = 
                            new StringBuilder("{").append(delim);
                    for (Tree.ImportMemberOrType imt: 
                            imtl.getImportMemberOrTypes()) {
                        Declaration dec = imt.getDeclarationModel();
                        if (!set.contains(dec)) {
                            sb.append(getDefaultIndent());
                            if (imt.getAlias()!=null) {
                                String alias = 
                                        imt.getAlias()
                                            .getIdentifier()
                                            .getText();
                                sb.append(alias).append('=');
                            }
                            String id = 
                                    imt.getIdentifier()
                                        .getText();
                            sb.append(id).append(",")
                                .append(delim);
                        }
                    }
                    sb.setLength(sb.length()-1-delim.length());
                    sb.append(delim).append("}");
                    int start = imtl.getStartIndex();
                    int stop = imtl.getStopIndex();
                    result.add(new ReplaceEdit(start, stop-start+1, 
                            sb.toString()));
                }
            }
        }
        Package pack = rootNode.getUnit().getPackage();
        if (!pack.getQualifiedNameString()
                .equals(newPackageName)) {
            Tree.Import importNode = 
                    findImportNode(rootNode, 
                            newPackageName);
            if (importNode!=null) {
                Tree.ImportMemberOrTypeList imtl = 
                        importNode.getImportMemberOrTypeList();
                if (imtl.getImportWildcard()!=null) {
                    //Do nothing
                }
                else {
                    int insertPosition = 
                            getBestImportMemberInsertPosition(importNode);
                    result.add(new InsertEdit(insertPosition, 
                            text.toString()));
                }
            } 
            else {
                int insertPosition = 
                        getBestImportInsertPosition(rootNode);
                text.delete(0, 2);
                text.insert(0, "import " + newPackageName + " {" + delim)
                    .append(delim + "}"); 
                if (insertPosition==0) {
                    text.append(delim);
                }
                else {
                    text.insert(0, delim);
                }
                result.add(new InsertEdit(insertPosition, 
                        text.toString()));
            }
        }
        return result;
    }
    
    public static int getBestImportInsertPosition(
            Tree.CompilationUnit cu) {
        Integer stopIndex = 
                cu.getImportList()
                    .getStopIndex();
        if (stopIndex == null) return 0;
        return stopIndex+1;
    }

    public static Tree.Import findImportNode(
            Tree.CompilationUnit cu, 
            String packageName) {
        FindImportNodeVisitor visitor = 
                new FindImportNodeVisitor(packageName);
        cu.visit(visitor);
        return visitor.getResult();
    }

    public static int getBestImportMemberInsertPosition(
            Tree.Import importNode) {
        Tree.ImportMemberOrTypeList imtl = 
                importNode.getImportMemberOrTypeList();
        if (imtl.getImportWildcard()!=null) {
            return imtl.getImportWildcard()
                    .getStartIndex();
        }
        else {
            List<Tree.ImportMemberOrType> imts = 
                    imtl.getImportMemberOrTypes();
            if (imts.isEmpty()) {
                return imtl.getStartIndex()+1;
            }
            else {
                return imts.get(imts.size()-1)
                        .getStopIndex()+1;
            }
        }
    }

    public static int applyImports(TextChange change,
            Set<Declaration> declarations, 
            Tree.CompilationUnit cu, IDocument doc) {
        return applyImports(change, declarations, null, 
                cu, doc);
    }
    
    public static int applyImports(TextChange change,
            Set<Declaration> declarations, 
            Declaration declarationBeingDeleted,
            Tree.CompilationUnit cu, IDocument doc) {
        int il=0;
        for (InsertEdit ie: 
            importEdits(cu, declarations, null, 
                    declarationBeingDeleted, doc)) {
            il+=ie.getText().length();
            change.addEdit(ie);
        }
        return il;
    }

    public static int applyImports(TextChange change,
            Map<Declaration,String> declarations, 
            Tree.CompilationUnit cu, IDocument doc,
            Declaration declarationBeingDeleted) {
        int il=0;
        for (InsertEdit ie: 
            importEdits(cu, declarations.keySet(), 
                declarations.values(), 
                declarationBeingDeleted, doc)) {
            il+=ie.getText().length();
            change.addEdit(ie);
        }
        return il;
    }

    public static void importSignatureTypes(
            Declaration declaration, 
            Tree.CompilationUnit rootNode, 
            Set<Declaration> declarations) {
        if (declaration instanceof TypedDeclaration) {
            TypedDeclaration td = 
                    (TypedDeclaration) declaration;
            importType(declarations, td.getType(), rootNode);
        }
        if (declaration instanceof Functional) {
            Functional fun = (Functional) declaration;
            for (ParameterList pl: 
                    fun.getParameterLists()) {
                for (Parameter p: pl.getParameters()) {
                    importSignatureTypes(p.getModel(), 
                            rootNode, declarations);
                }
            }
        }
    }
    
    public static void importTypes(
            Set<Declaration> declarations, 
            Collection<Type> types, 
            Tree.CompilationUnit rootNode) {
        if (types==null) return;
        for (Type type: types) {
            importType(declarations, type, rootNode);
        }
    }
    
    public static void importType(
            Set<Declaration> declarations, 
            Type type, 
            Tree.CompilationUnit rootNode) {
        if (type!=null) {
            if (type.isUnknown() || type.isNothing()) {
                //nothing to do
            }
            else if (type.isUnion()) {
                for (Type t: type.getCaseTypes()) {
                    importType(declarations, t, rootNode);
                }
            }
            else if (type.isIntersection()) {
                for (Type t: type.getSatisfiedTypes()) {
                    importType(declarations, t, rootNode);
                }
            }
            else {
                importType(declarations, 
                        type.getQualifyingType(), rootNode);
                TypeDeclaration td = type.getDeclaration();
                if (type.isClassOrInterface() && 
                        td.isToplevel()) {
                    importDeclaration(declarations, td, rootNode);
                    for (Type arg: 
                            type.getTypeArgumentList()) {
                        importType(declarations, arg, rootNode);
                    }
                }
            }
        }
    }

    public static void importDeclaration(
            Set<Declaration> declarations,
            Declaration declaration, 
            Tree.CompilationUnit rootNode) {
        if (!declaration.isParameter()) {
            Package p = declaration.getUnit().getPackage();
            Package pack = rootNode.getUnit().getPackage();
            if (!p.getNameAsString().isEmpty() && 
                    !p.equals(pack) &&
                    !p.getNameAsString().equals(LANGUAGE_MODULE_NAME) &&
                    (!declaration.isClassOrInterfaceMember() ||
                            declaration.isStaticallyImportable())) {
                if (!isImported(declaration, rootNode)) {
                    declarations.add(declaration);
                }
            }
        }
    }

    public static boolean isImported(
            Declaration declaration,
            Tree.CompilationUnit rootNode) {
        for (Import i: rootNode.getUnit().getImports()) {
            if (i.getDeclaration()
                    .equals(getAbstraction(declaration))) {
                return true;
            }
        }
        return false;
    }

    public static void importCallableParameterParamTypes(
            Declaration declaration, 
            HashSet<Declaration> decs, 
            Tree.CompilationUnit cu) {
        if (declaration instanceof Functional) {
            Functional fun = (Functional) declaration;
            List<ParameterList> pls = 
                    fun.getParameterLists();
            if (!pls.isEmpty()) {
                for (Parameter p: pls.get(0).getParameters()) {
                    FunctionOrValue pm = p.getModel();
                    importParameterTypes(pm, cu, decs);
                }
            }            
        }
    }

    public static void importParameterTypes(
            Declaration dec,
            Tree.CompilationUnit cu, 
            HashSet<Declaration> decs) {
        if (dec instanceof Function) {
            Function m = (Function) dec;
            for (ParameterList ppl: m.getParameterLists()) {
                for (Parameter pp: ppl.getParameters()) {
                    importSignatureTypes(pp.getModel(), 
                            cu, decs);
                }
            }
        }
    }
    
}