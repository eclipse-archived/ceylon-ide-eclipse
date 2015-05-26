package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.computeSelection;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getClassOrInterfaceBody;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getRootNode;
import static com.redhat.ceylon.eclipse.code.correct.CreateInNewUnitProposal.addCreateInNewUnitProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateParameterProposal.addCreateParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;
import static com.redhat.ceylon.eclipse.util.Nodes.findToplevelStatement;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.ProducedType;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.eclipse.util.Nodes;

class CreateProposal extends InitializerProposal {
    
    private CreateProposal(String def, String desc, 
            Scope scope, Unit unit, ProducedType returnType,
            Image image, int offset, TextFileChange change,
            int exitPos, boolean isObjectOrClass) {
        super(desc, change, scope, unit, returnType, 
                isObjectOrClass ? 
                        new Region(offset, 0): 
                        computeSelection(offset, def), 
                image, exitPos, null);
    }
    
    static void addCreateMemberProposal(Collection<ICompletionProposal> proposals, 
            DefinitionGenerator dg, Declaration typeDec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.Body body, Tree.Statement statement) {
        IFile file = getFile(unit);
        TextFileChange change = 
                new TextFileChange("Create Member", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = EditorUtil.getDocument(change);
        String indentBefore;
        String indentAfter;
        String indent;
        int offset;
        List<Tree.Statement> statements = body.getStatements();
        String delim = getDefaultLineDelimiter(doc);
        if (statements.isEmpty()) {
            String bodyIndent = getIndent(decNode, doc);
            indent = bodyIndent + getDefaultIndent();
            indentBefore = delim + indent;
            try {
                boolean singleLineBody = 
                        doc.getLineOfOffset(body.getStartIndex())
                        == doc.getLineOfOffset(body.getStopIndex());
                if (singleLineBody) {
                    indentAfter = delim + bodyIndent;
                }
                else {
                    indentAfter = "";
                }
            }
            catch (BadLocationException e) {
                e.printStackTrace();
                indentAfter = delim;
            }
            offset = body.getStartIndex()+1;
        }
        else {
            Tree.Statement st;
            if (statement!=null && 
                    statement.getUnit().equals(body.getUnit()) &&
                    statement.getStartIndex()>=body.getStartIndex() &&
                    statement.getStopIndex()<=body.getStopIndex()) {
                st = statements.get(0);
                for (Tree.Statement s: statements) {
                    if (statement.getStartIndex()>=s.getStartIndex() &&
                        statement.getStopIndex()<=s.getStopIndex()) {
                        st = s;
                    }
                }
                indent = getIndent(st, doc);
                indentBefore = "";
                indentAfter = delim + indent;
                offset = st.getStartIndex();
            }
            else {
                st = statements.get(statements.size()-1);
                indent = getIndent(st, doc);
                indentBefore = delim + indent;
                indentAfter = "";
                offset = st.getStopIndex()+1;
            }
        }
        String generated = typeDec instanceof Interface ?
            dg.generateSharedFormal(indent, delim) :
            dg.generateShared(indent, delim);
        String def = indentBefore + generated + indentAfter;
        int il = applyImports(change, dg.getImports(), 
                unit.getCompilationUnit(), doc);
        change.addEdit(new InsertEdit(offset, def));
        String desc = "Create " + memberKind(dg) + 
                " in '" + typeDec.getName() + "'";
        int exitPos = dg.getNode().getStopIndex()+1;
        proposals.add(new CreateProposal(def, desc, 
                body.getScope(), body.getUnit(), dg.getReturnType(), 
                dg.getImage(), offset+il, change, exitPos,
                dg instanceof ObjectClassDefinitionGenerator));
    }

    private static String memberKind(DefinitionGenerator dg) {
        String desc = dg.getDescription();
        if (desc.startsWith("function")) {
            return "method" + desc.substring(8);
        }
        if (desc.startsWith("value")) {
            return "attribute" + desc.substring(5);
        }
        if (desc.startsWith("class")) {
            return "member class" + desc.substring(5);
        }
        return desc;
    }

    private static void addCreateProposal(Collection<ICompletionProposal> proposals, 
            boolean local, DefinitionGenerator dg, PhasedUnit unit, 
            Tree.Statement statement) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange(local ? 
                "Create Local" : "Create Toplevel", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = EditorUtil.getDocument(change);
        String indent = getIndent(statement, doc);
        int offset = statement.getStartIndex();
        String delim = getDefaultLineDelimiter(doc);
        Tree.CompilationUnit cu = unit.getCompilationUnit();
        int il = applyImports(change, dg.getImports(), cu, doc);
        String def = dg.generate(indent, delim) + delim + indent;
        if (!local) def += delim;
        change.addEdit(new InsertEdit(offset, def));
        String desc = (local ? "Create local " : "Create toplevel ") + dg.getDescription();
        final Scope scope = local ? 
                statement.getScope() : 
                cu.getUnit().getPackage();
        int exitPos = dg.getNode().getStopIndex()+1;
        proposals.add(new CreateProposal(def, desc, 
                scope, cu.getUnit(), dg.getReturnType(), 
                dg.getImage(), offset+il, change, exitPos,
                dg instanceof ObjectClassDefinitionGenerator));
    }
    
    static void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, DefinitionGenerator dg,
            Tree.QualifiedMemberOrTypeExpression qmte, Tree.Statement statement) {
        Tree.Primary p = ((Tree.QualifiedMemberOrTypeExpression) qmte).getPrimary();
        if (p.getTypeModel()!=null) {
            Declaration typeDec = p.getTypeModel().getDeclaration();
            addCreateMemberProposals(proposals, project, dg, typeDec, statement);
        }
    }

    static void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, DefinitionGenerator dg, Declaration typeDec, 
            Tree.Statement statement) {
        if (typeDec!=null && (typeDec instanceof Class
                || (typeDec instanceof Interface && dg.isFormalSupported()))) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    //TODO: "object" declarations?
                    FindDeclarationNodeVisitor fdv = 
                            new FindDeclarationNodeVisitor(typeDec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = 
                            (Tree.Declaration) fdv.getDeclarationNode();
                    Tree.Body body = getClassOrInterfaceBody(decNode);
                    if (body!=null) {
                        addCreateMemberProposal(proposals, dg, 
                                typeDec, unit, decNode, body,
                                statement);
                        break;
                    }
                }
            }
        }
    }

    static void addCreateLocalProposals(Collection<ICompletionProposal> proposals,
            IProject project, DefinitionGenerator dg) {
        //if (!fsv.isToplevel()) {
        Tree.Statement statement = 
                findStatement(dg.getRootNode(), dg.getNode());
        if (statement!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (unit.getUnit().equals(dg.getRootNode().getUnit())) {
                    addCreateProposal(proposals, true, dg, 
                            unit, statement);
                    break;
                }
            }
        }
        //}
    }

    static void addCreateToplevelProposals(Collection<ICompletionProposal> proposals,
            IProject project, DefinitionGenerator dg) {
        Tree.Statement statement = 
                findToplevelStatement(dg.getRootNode(), dg.getNode());
        if (statement!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (unit.getUnit().equals(dg.getRootNode().getUnit())) {
                    addCreateProposal(proposals, 
                            false, dg, unit, statement);
                    break;
                }
            }
        }
    }
        
    static void addCreateProposals(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IProject project,
            IFile file) {
        Tree.MemberOrTypeExpression smte = (Tree.MemberOrTypeExpression) node;
        String brokenName = Nodes.getIdentifyingNode(node).getText();
        if (!brokenName.isEmpty()) {
            ValueFunctionDefinitionGenerator vfdg = 
                    ValueFunctionDefinitionGenerator.create(brokenName, smte, cu);
            if (vfdg!=null) {
                if (smte instanceof Tree.BaseMemberExpression) {
                    addCreateParameterProposal(proposals, project, vfdg);
                }
                addCreateProposals(cu, proposals, project, file, smte, vfdg);
            }
            ObjectClassDefinitionGenerator ocdg = 
                    ObjectClassDefinitionGenerator.create(brokenName, smte, cu);
            if (ocdg!=null) {
                addCreateProposals(cu, proposals, project, file, smte, ocdg);
            }
        }
    }

    private static void addCreateProposals(Tree.CompilationUnit rootNode,
            Collection<ICompletionProposal> proposals, IProject project,
            IFile file, Tree.MemberOrTypeExpression smte, DefinitionGenerator dg) {
        if (smte instanceof Tree.QualifiedMemberOrTypeExpression) {
            addCreateMemberProposals(proposals, project, dg, 
                    (Tree.QualifiedMemberOrTypeExpression) smte,
                    Nodes.findStatement(rootNode, smte));
        }
        else {
            if (!(dg.getNode() instanceof Tree.ExtendedTypeExpression)) {
                addCreateLocalProposals(proposals, project, dg);
                ClassOrInterface container = 
                        findClassContainer(rootNode, smte);
                if (container!=null && 
                        container!=smte.getScope()) { //if the statement appears directly in an initializer, propose a local, not a member 
                    do {
                        addCreateMemberProposals(proposals, project, 
                                dg, container,
                                //TODO: this is a little lame because
                                //      it doesn't handle some cases
                                //      of nesting
                                Nodes.findStatement(rootNode, smte));
                        if (container.getContainer() instanceof Declaration) {
                            Declaration outerContainer = 
                                    (Declaration) container.getContainer();
                            container = findClassContainer(outerContainer);
                        }
                        else { 
                            break;
                        }
                    }
                    while (container!=null);
                }
            }
            addCreateToplevelProposals(proposals, project, dg);
            addCreateInNewUnitProposal(proposals, dg, file, rootNode);
        }
    }


    private static ClassOrInterface findClassContainer(Tree.CompilationUnit cu, Node node){
        FindContainerVisitor fcv = new FindContainerVisitor(node);
        fcv.visit(cu);
        Tree.Declaration declaration = fcv.getDeclaration();
        if(declaration == null || declaration == node)
            return null;
        if(declaration instanceof Tree.ClassOrInterface)
            return (ClassOrInterface) declaration.getDeclarationModel();
        if(declaration instanceof Tree.MethodDefinition)
            return findClassContainer(declaration.getDeclarationModel());
        if(declaration instanceof Tree.ObjectDefinition)
            return findClassContainer(declaration.getDeclarationModel());
        return null;
    }
    
    private static ClassOrInterface findClassContainer(
            Declaration declarationModel) {
        do {
            if (declarationModel == null) {
                return null;
            }
            if (declarationModel instanceof ClassOrInterface) {
                return (ClassOrInterface) declarationModel;
            }
            Scope container = declarationModel.getContainer();
            if (container instanceof Declaration) {
                declarationModel = (Declaration) container;
            }
            else {
                return null;
            }
        }
        while(true);
    }
    
}