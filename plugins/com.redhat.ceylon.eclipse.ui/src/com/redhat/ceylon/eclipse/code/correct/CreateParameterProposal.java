package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.computeSelection;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.defaultValue;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getClassOrInterfaceBody;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getRootNode;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_CORR;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.ProducedReference;
import com.redhat.ceylon.model.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

class CreateParameterProposal extends InitializerProposal {
    
    CreateParameterProposal(String def, String desc, 
            Declaration dec, ProducedType type,
            Image image, int offset, TextFileChange change,
            int exitPos) {
        super(desc, change, dec, type, 
                computeSelection(offset,def), 
                image, exitPos, null);
    }
    
    private static void addCreateParameterProposal(Collection<ICompletionProposal> proposals, 
            String def, String desc, Image image, Declaration dec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.ParameterList paramList, 
            ProducedType returnType, Set<Declaration> imports, Node node) {
        IFile file = getFile(unit);
        TextFileChange change = 
                new TextFileChange("Add Parameter", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = EditorUtil.getDocument(change);
        int offset = paramList.getStopIndex();
        int il = applyImports(change, imports, 
                unit.getCompilationUnit(), doc);
        change.addEdit(new InsertEdit(offset, def));
        int exitPos = node.getStopIndex()+1;
        proposals.add(new CreateParameterProposal(def, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                dec, returnType, image, offset+il, change, exitPos));
    }

    private static void addCreateParameterAndAttributeProposal(Collection<ICompletionProposal> proposals, 
            String pdef, String adef, String desc, Image image, Declaration dec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.ParameterList paramList, Tree.Body body, 
            ProducedType returnType, Node node) {
        IFile file = getFile(unit);
        TextFileChange change = 
                new TextFileChange("Add Attribute", file);
        change.setEdit(new MultiTextEdit());
        int offset = paramList.getStopIndex();
        IDocument doc = EditorUtil.getDocument(change);
        String indent;
        String indentAfter;
        int offset2;
        List<Tree.Statement> statements = body.getStatements();
        if (statements.isEmpty()) {
            indentAfter = getDefaultLineDelimiter(doc) + getIndent(decNode, doc);
            indent = indentAfter + getDefaultIndent();
            offset2 = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = statements.get(statements.size()-1);
            indent = getDefaultLineDelimiter(doc) + getIndent(statement, doc);
            offset2 = statement.getStopIndex()+1;
            indentAfter = "";
        }
        HashSet<Declaration> decs = new HashSet<Declaration>();
        Tree.CompilationUnit cu = unit.getCompilationUnit();
        importType(decs, returnType, cu);
        int il = applyImports(change, decs, cu, doc);
        change.addEdit(new InsertEdit(offset, pdef));
        change.addEdit(new InsertEdit(offset2, indent+adef+indentAfter));
        int exitPos = node.getStopIndex()+1;
        proposals.add(new CreateParameterProposal(pdef, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                dec, returnType, image, offset+il, change, exitPos));
    }

    static void addCreateParameterProposal(Collection<ICompletionProposal> proposals, 
            IProject project, ValueFunctionDefinitionGenerator dg) {
        if (Character.isLowerCase(dg.getBrokenName().codePointAt(0))) {
            Tree.Declaration decl = 
                    findDeclarationWithBody(dg.getRootNode(), dg.getNode());
            if (decl == null || 
                    decl.getDeclarationModel() == null || 
                    decl.getDeclarationModel().isActual()) {
                return;
            }

            Tree.ParameterList paramList = getParameters(decl);
            if (paramList != null) {
                String def = dg.generate("", "");
                //TODO: really ugly and fragile way to strip off the trailing ;
                String paramDef = (paramList.getParameters().isEmpty() ? "" : ", ") + 
                        def.substring(0, def.length() - (def.endsWith("{}")?3:1));
                String paramDesc = "parameter '" + dg.getBrokenName() + "'";
                for (PhasedUnit unit: getUnits(project)) {
                    if (unit.getUnit().equals(dg.getRootNode().getUnit())) {
                        addCreateParameterProposal(proposals, paramDef, paramDesc, ADD_CORR, 
                                decl.getDeclarationModel(), unit, decl, paramList, dg.getReturnType(),
                                dg.getImports(), dg.getNode());
                        break;
                    }
                }
            }
        }
    }

    static void addCreateParameterProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
        FindInvocationVisitor fav = new FindInvocationVisitor(node);
        fav.visit(cu);
        if (fav.result==null) return;
        Tree.Primary prim = fav.result.getPrimary();
        if (prim instanceof Tree.MemberOrTypeExpression) {
            ProducedReference pr = ((Tree.MemberOrTypeExpression) prim).getTarget();
            if (pr!=null) {
                Declaration d = pr.getDeclaration();
                ProducedType t=null;
                String parameterName=null;
                if (node instanceof Tree.Term) {
                    t = ((Tree.Term) node).getTypeModel();
                    parameterName = t.getDeclaration().getName();
                    if (parameterName!=null) {
                        parameterName = 
                                Escaping.toInitialLowercase(parameterName)
                                        .replace("?", "").replace("[]", "");
                        if ("string".equals(parameterName)) {
                            parameterName = "text";
                        }
                    }
                }
                else if (node instanceof Tree.SpecifiedArgument) {
                    Tree.SpecifiedArgument sa = (Tree.SpecifiedArgument) node;
                    Tree.SpecifierExpression se = sa.getSpecifierExpression();
                    if (se!=null && se.getExpression()!=null) {
                        t = se.getExpression().getTypeModel();
                    }
                    parameterName = sa.getIdentifier().getText();
                }
                else if (node instanceof Tree.TypedArgument) {
                    Tree.TypedArgument ta = (Tree.TypedArgument) node;
                    t = ta.getType().getTypeModel();
                    parameterName = ta.getIdentifier().getText();
                }
                if (t!=null && parameterName!=null) {
                    t = node.getUnit().denotableType(t);
                    String defaultValue = defaultValue(prim.getUnit(), t);
                    String parameterType = t.getProducedTypeName();
                    String def = parameterType + " " + parameterName + " = " + defaultValue;
                    String desc = "parameter '" + parameterName +"'";
                    addCreateParameterProposals(proposals, project, def, desc, d, t, node);
                    String pdef = parameterName + " = " + defaultValue;
                    String adef = parameterType + " " + parameterName + ";";
                    String padesc = "attribute '" + parameterName +"'";
                    addCreateParameterAndAttributeProposals(proposals, project, 
                            pdef, adef, padesc, d, t, node);
                }
            }
        }
    }

    private static Tree.ParameterList getParameters(Tree.Declaration decNode) {
        if (decNode instanceof Tree.AnyClass) {
            return ((Tree.AnyClass) decNode).getParameterList();
        }
        else if (decNode instanceof Tree.AnyMethod){
            List<Tree.ParameterList> pls = ((Tree.AnyMethod) decNode).getParameterLists();
            return pls.isEmpty() ? null : pls.get(0);
        }
        else if (decNode instanceof Tree.Constructor) {
            return ((Tree.Constructor) decNode).getParameterList();
        }
        return null;
    }

    private static void addCreateParameterProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Declaration typeDec, ProducedType t,
            Node node) {
        if (typeDec!=null && typeDec instanceof Functional) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationNodeVisitor fdv = 
                            new FindDeclarationNodeVisitor(typeDec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = 
                            (Tree.Declaration) fdv.getDeclarationNode();
                    Tree.ParameterList paramList = getParameters(decNode);
                    if (paramList!=null) {
                        if (!paramList.getParameters().isEmpty()) {
                            def = ", " + def;
                        }
                        Set<Declaration> imports = new HashSet<Declaration>();
                        importType(imports, t, unit.getCompilationUnit());
                        addCreateParameterProposal(proposals, def, desc, ADD_CORR, 
                                typeDec, unit, decNode, paramList, t, imports, node);
                        break;
                    }
                }
            }
        }
    }

    private static void addCreateParameterAndAttributeProposals(Collection<ICompletionProposal> proposals,
            IProject project, String pdef, String adef, String desc, Declaration typeDec, ProducedType t,
            Node node) {
        if (typeDec!=null && typeDec instanceof ClassOrInterface) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationNodeVisitor fdv = 
                            new FindDeclarationNodeVisitor(typeDec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = 
                            (Tree.Declaration) fdv.getDeclarationNode();
                    Tree.ParameterList paramList = getParameters(decNode);
                    Tree.Body body = getClassOrInterfaceBody(decNode);
                    if (body!=null && paramList!=null) {
                        if (!paramList.getParameters().isEmpty()) {
                            pdef = ", " + pdef;
                        }
                        addCreateParameterAndAttributeProposal(proposals, pdef, 
                                adef, desc, ADD_CORR, typeDec, unit, decNode, 
                                paramList, body, t, node);
                    }
                }
            }
        }
    }
        
}