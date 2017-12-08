/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil.computeSelection;

import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;

import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Unit;

class CreateProposal extends InitializerProposal {
    
    private CreateProposal(String def, String desc, 
            Scope scope, Unit unit, Type returnType,
            Image image, int offset, TextFileChange change,
            int exitPos, boolean isObjectOrClass) {
        super(desc, change, scope, unit, returnType, 
                isObjectOrClass ? 
                        new Region(offset, 0): 
                        computeSelection(offset, def), 
                image, exitPos);
    }

    CreateProposal(String desc,
            Scope scope, Unit unit, Type returnType,
            Image image, TextChange change,
            int exitPos, Region selection) {
        super(desc, change, scope, unit, returnType,
                selection, image, exitPos);
    }

//    static void addCreateMemberProposal(
//            Collection<ICompletionProposal> proposals,
//            DefinitionGenerator dg, Declaration typeDec,
//            ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> unit, Tree.Declaration decNode,
//            Tree.Body body, Tree.Statement statement) {
//        IFile file = unit.getResourceFile();
//        if (file == null) {
//            return;
//        }
//        TextFileChange change = 
//                new TextFileChange("Create Member", file);
//        change.setEdit(new MultiTextEdit());
//        IDocument doc = getDocument(change);
//        String indentBefore;
//        String indentAfter;
//        String indent;
//        int offset;
//        List<Tree.Statement> statements =
//                body.getStatements();
//        String delim = utilJ2C().indents().getDefaultLineDelimiter(doc);
//        if (statements.isEmpty()) {
//            String bodyIndent = utilJ2C().indents().getIndent(decNode, doc);
//            indent = bodyIndent + utilJ2C().indents().getDefaultIndent();
//            indentBefore = delim + indent;
//            try {
//                boolean singleLineBody = 
//                        doc.getLineOfOffset(body.getStartIndex())
//                        == doc.getLineOfOffset(body.getStopIndex());
//                if (singleLineBody) {
//                    indentAfter = delim + bodyIndent;
//                }
//                else {
//                    indentAfter = "";
//                }
//            }
//            catch (BadLocationException e) {
//                e.printStackTrace();
//                indentAfter = delim;
//            }
//            offset = body.getStartIndex()+1;
//        }
//        else {
//            Tree.Statement st;
//            if (statement!=null && 
//                    statement.getUnit().equals(body.getUnit()) &&
//                    statement.getStartIndex()>=body.getStartIndex() &&
//                    statement.getEndIndex()<=body.getEndIndex()) {
//                st = statements.get(0);
//                for (Tree.Statement s: statements) {
//                    if (statement.getStartIndex()>=s.getStartIndex() &&
//                        statement.getEndIndex()<=s.getEndIndex()) {
//                        st = s;
//                    }
//                }
//                indent = utilJ2C().indents().getIndent(st, doc);
//                indentBefore = "";
//                indentAfter = delim + indent;
//                offset = st.getStartIndex();
//            }
//            else {
//                st = statements.get(statements.size()-1);
//                indent = utilJ2C().indents().getIndent(st, doc);
//                indentBefore = delim + indent;
//                indentAfter = "";
//                offset = st.getEndIndex();
//            }
//        }
//        String generated =
//                typeDec instanceof Interface ?
//                    dg.generateSharedFormal(indent, delim) :
//                    dg.generateShared(indent, delim);
//        String def = indentBefore + generated + indentAfter;
//        int il =
//                (int) importProposals().applyImports(change, dg.getImports(),
//                unit.getCompilationUnit(), doc);
//        change.addEdit(new InsertEdit(offset, def));
//        String desc =
//                "Create " + memberKind(dg) +
//                " in '" + typeDec.getName() + "'";
//        int exitPos = dg.getNode().getEndIndex();
//        proposals.add(new CreateProposal(def, desc, 
//                body.getScope(), body.getUnit(),
//                dg.getReturnType(), dg.getImage(),
//                offset+il, change, exitPos,
//                dg instanceof ObjectClassDefinitionGenerator));
//    }
//
//    private static String memberKind(DefinitionGenerator dg) {
//        String desc = dg.getDescription();
//        if (desc.startsWith("function")) {
//            return "method" + desc.substring(8);
//        }
//        if (desc.startsWith("value")) {
//            return "attribute" + desc.substring(5);
//        }
//        if (desc.startsWith("class")) {
//            return "member class" + desc.substring(5);
//        }
//        return desc;
//    }
//
//    private static void addCreateProposal(
//            Collection<ICompletionProposal> proposals,
//            boolean local, DefinitionGenerator dg,
//            ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> unit,
//            Tree.Statement statement) {
//        IFile file = unit.getResourceFile();
//        if (file == null) {
//            return;
//        }
//        TextFileChange change =
//                new TextFileChange(
//                        local ?
//                                "Create Local" :
//                                "Create Toplevel",
//                        file);
//        change.setEdit(new MultiTextEdit());
//        IDocument doc = getDocument(change);
//        String indent = utilJ2C().indents().getIndent(statement, doc);
//        int offset = statement.getStartIndex();
//        String delim = utilJ2C().indents().getDefaultLineDelimiter(doc);
//        Tree.CompilationUnit rootNode =
//                unit.getCompilationUnit();
//        int il =
//                (int) importProposals().applyImports(change, dg.getImports(),
//                rootNode, doc);
//        String def =
//                dg.generate(indent, delim) + delim + indent;
//        if (!local) def += delim;
//        change.addEdit(new InsertEdit(offset, def));
//        String desc =
//                (local ?
//                        "Create local " :
//                        "Create toplevel ")
//                    + dg.getDescription();
//        final Scope scope =
//                local ?
//                    statement.getScope() :
//                    rootNode.getUnit().getPackage();
//        int exitPos = dg.getNode().getEndIndex();
//        proposals.add(new CreateProposal(def, desc, 
//                scope, rootNode.getUnit(),
//                dg.getReturnType(), dg.getImage(),
//                offset+il, change, exitPos,
//                dg instanceof ObjectClassDefinitionGenerator));
//    }
//    
//    static void addCreateMemberProposals(
//            Collection<ICompletionProposal> proposals,
//            IProject project, DefinitionGenerator dg,
//            Tree.QualifiedMemberOrTypeExpression qmte,
//            Tree.Statement statement) {
//        Tree.Primary p = qmte.getPrimary();
//        if (p.getTypeModel()!=null) {
//            Declaration typeDec =
//                    p.getTypeModel()
//                        .getDeclaration();
//            addCreateMemberProposals(proposals, project, dg,
//                    typeDec, statement);
//        }
//    }
//
//    static void addCreateMemberProposals(
//            Collection<ICompletionProposal> proposals,
//            IProject project, DefinitionGenerator dg,
//            Declaration typeDec, Tree.Statement statement) {
//        if (typeDec!=null && (typeDec instanceof Class
//                || (typeDec instanceof Interface
//                        && dg.isFormalSupported()))) {
//            Unit u = typeDec.getUnit();
//            if (u instanceof ModifiableSourceFile) {
//                ModifiableSourceFile msf =
//                        (ModifiableSourceFile) u;
//                ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> phasedUnit =
//                        msf.getPhasedUnit();
//                //TODO: "object" declarations?
//                FindDeclarationNodeVisitor fdv =
//                        new FindDeclarationNodeVisitor(typeDec);
//                phasedUnit.getCompilationUnit().visit(fdv);
//                Tree.Declaration decNode =
//                        (Tree.Declaration)
//                            fdv.getDeclarationNode();
//                Tree.Body body =
//                        getClassOrInterfaceBody(decNode);
//                IFile file = phasedUnit.getResourceFile();
//                if (body!=null
//                        && file != null) {
//                    addCreateMemberProposal(proposals, dg,
//                            typeDec, phasedUnit, decNode, body,
//                            statement);
//                }
//            }
//        }
//    }
//
//    static void addCreateLocalProposals(
//            Collection<ICompletionProposal> proposals,
//            IProject project, DefinitionGenerator dg) {
//        Tree.Statement statement = 
//                findStatement(dg.getRootNode(),
//                        dg.getNode());
//        if (statement!=null) {
//            Unit u = dg.getRootNode().getUnit();
//            if (u instanceof ModifiableSourceFile) {
//                ModifiableSourceFile cu =
//                        (ModifiableSourceFile) u;
//                addCreateProposal(proposals, true, dg,
//                        cu.getPhasedUnit(), statement);
//            }
//        }
//    }
//
//    static void addCreateToplevelProposals(
//            Collection<ICompletionProposal> proposals,
//            IProject project, DefinitionGenerator dg) {
//        Tree.Statement statement = 
//                findToplevelStatement(dg.getRootNode(),
//                        dg.getNode());
//        if (statement!=null) {
//            Unit u = dg.getRootNode().getUnit();
//            if (u instanceof ModifiableSourceFile) {
//                ModifiableSourceFile cu =
//                        (ModifiableSourceFile) u;
//                addCreateProposal(proposals, false, dg,
//                        cu.getPhasedUnit(), statement);
//            }
//        }
//    }
//        
//    static void addCreateProposals(
//            Tree.CompilationUnit rootNode, Node node,
//            Collection<ICompletionProposal> proposals,
//            IProject project, IFile file) {
//        Tree.MemberOrTypeExpression smte = 
//                (Tree.MemberOrTypeExpression) node;
//        String brokenName =
//                getIdentifyingNode(node)
//                    .getText();
//        if (!brokenName.isEmpty()) {
//            ValueFunctionDefinitionGenerator vfdg = 
//                    ValueFunctionDefinitionGenerator.create(
//                            brokenName, smte, rootNode);
//            if (vfdg!=null) {
//                if (smte instanceof Tree.BaseMemberExpression) {
//                    Tree.BaseMemberExpression bme = 
//                            (Tree.BaseMemberExpression) smte;
//                    Tree.Identifier id = bme.getIdentifier();
//                    int tt = id.getToken().getType();
//                    if (tt!=CeylonLexer.AIDENTIFIER) {
//                        addCreateParameterProposal(proposals,
//                                project, vfdg);
//                    }
//                }
//                addCreateProposals(rootNode, proposals,
//                        project, file, smte, vfdg);
//            }
//            ObjectClassDefinitionGenerator ocdg = 
//                    ObjectClassDefinitionGenerator.create(
//                            brokenName, smte, rootNode);
//            if (ocdg!=null) {
//                addCreateProposals(rootNode, proposals,
//                        project, file, smte, ocdg);
//            }
//        }
//    }
//
//    private static void addCreateProposals(
//            Tree.CompilationUnit rootNode,
//            Collection<ICompletionProposal> proposals,
//            IProject project, IFile file,
//            Tree.MemberOrTypeExpression smte,
//            DefinitionGenerator dg) {
//        if (smte instanceof Tree.QualifiedMemberOrTypeExpression) {
//            Tree.QualifiedMemberOrTypeExpression qmte =
//                    (Tree.QualifiedMemberOrTypeExpression) smte;
//            addCreateMemberProposals(proposals, project, dg,
//                    qmte, findStatement(rootNode, smte));
//        }
//        else {
//            Node node = dg.getNode();
//            if (!(node instanceof Tree.ExtendedTypeExpression)) {
//                addCreateLocalProposals(proposals, project, dg);
//                ClassOrInterface container = 
//                        findClassContainer(rootNode, smte);
//                if (container!=null && 
//                        container!=smte.getScope()) { //if the statement appears directly in an initializer, propose a local, not a member 
//                    do {
//                        addCreateMemberProposals(proposals,
//                                project, dg, container,
//                                //TODO: this is a little lame because
//                                //      it doesn't handle some cases
//                                //      of nesting
//                                findStatement(rootNode, smte));
//                        Scope cc = container.getContainer();
//                        if (cc instanceof Declaration) {
//                            Declaration outerContainer = 
//                                    (Declaration)
//                                        cc;
//                            container =
//                                    findClassContainer(
//                                            outerContainer);
//                        }
//                        else { 
//                            break;
//                        }
//                    }
//                    while (container!=null);
//                }
//            }
//            addCreateToplevelProposals(proposals, project, dg);
//            addCreateInNewUnitProposal(proposals, dg, file, rootNode);
//        }
//    }
//
//
//    private static ClassOrInterface findClassContainer(
//            Tree.CompilationUnit rootNode, Node node){
//        FindContainerVisitor fcv =
//                new FindContainerVisitor(node);
//        fcv.visit(rootNode);
//        Tree.Declaration declaration = fcv.getDeclaration();
//        if (declaration == null || declaration == node) {
//            return null;
//        }
//        Declaration model = declaration.getDeclarationModel();
//        if (declaration instanceof Tree.ClassOrInterface) {
//            return (ClassOrInterface) model;
//        }
//        else if (declaration instanceof Tree.MethodDefinition) {
//            return findClassContainer(model);
//        }
//        else if (declaration instanceof Tree.ObjectDefinition) {
//            return findClassContainer(model);
//        }
//        else {
//            return null;
//        }
//    }
//    
//    private static ClassOrInterface findClassContainer(
//            Declaration declarationModel) {
//        do {
//            if (declarationModel == null) {
//                return null;
//            }
//            if (declarationModel instanceof ClassOrInterface) {
//                return (ClassOrInterface) declarationModel;
//            }
//            Scope container = declarationModel.getContainer();
//            if (container instanceof Declaration) {
//                declarationModel = (Declaration) container;
//            }
//            else {
//                return null;
//            }
//        }
//        while(true);
//    }
//    
}