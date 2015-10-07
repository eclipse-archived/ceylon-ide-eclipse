package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.computeSelection;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.defaultValue;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getClassOrInterfaceBody;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_CORR;
import static com.redhat.ceylon.eclipse.util.Indents.indents;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;
import static com.redhat.ceylon.ide.common.util.Escaping.toInitialLowercase;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.model.ModifiableSourceFile;
import com.redhat.ceylon.eclipse.core.typechecker.ModifiablePhasedUnit;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;

class CreateParameterProposal extends InitializerProposal {
    
    @Deprecated
    CreateParameterProposal(String def, String desc, 
            Declaration dec, Type type,
            Image image, int offset, TextFileChange change,
            int exitPos) {
        super(desc, change, dec, type, 
                computeSelection(offset,def), 
                image, exitPos);
    }
    
    CreateParameterProposal(String desc, 
            Declaration dec, Type type, Region selection,
            Image image, TextChange change,
            int exitPos) {
        super(desc, change, dec, type, selection, 
                image, exitPos, null);
    }
    
    @Deprecated
    private static void addCreateParameterProposal(
            Collection<ICompletionProposal> proposals, 
            String def, String desc, Image image, 
            Declaration dec, ModifiablePhasedUnit unit,
            Tree.Declaration decNode, 
            Tree.ParameterList paramList, 
            Type returnType, 
            Set<Declaration> imports, Node node) {
        IFile file = unit.getResourceFile();
        if (file == null) {
            return;
        }
        TextFileChange change = 
                new TextFileChange("Add Parameter", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = EditorUtil.getDocument(change);
        int offset = paramList.getEndIndex()-1;
        int il = applyImports(change, imports,
                unit.getCompilationUnit(), doc);
        change.addEdit(new InsertEdit(offset, def));
        int exitPos = node.getEndIndex();
        proposals.add(new CreateParameterProposal(def, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                dec, returnType, image, offset+il, change, exitPos));
    }

    @Deprecated
    private static void addCreateParameterAndAttributeProposal(
            Collection<ICompletionProposal> proposals, 
            String pdef, String adef, String desc, 
            Image image, Declaration dec, ModifiablePhasedUnit unit,
            Tree.Declaration decNode, 
            Tree.ParameterList paramList, Tree.Body body, 
            Type returnType, Node node) {
        IFile file = unit.getResourceFile();
        if (file == null) {
            return;
        }
        TextFileChange change = 
                new TextFileChange("Add Attribute", file);
        change.setEdit(new MultiTextEdit());
        int offset = paramList.getEndIndex()-1;
        IDocument doc = EditorUtil.getDocument(change);
        String indent;
        String indentAfter;
        int offset2;
        List<Tree.Statement> statements = body.getStatements();
        if (statements.isEmpty()) {
            indentAfter = indents().getDefaultLineDelimiter(doc) +
                    indents().getIndent(decNode, doc);
            indent = indentAfter + indents().getDefaultIndent();
            offset2 = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = 
                    statements.get(statements.size()-1);
            indent = getDefaultLineDelimiter(doc) +
                    getIndent(statement, doc);
            offset2 = statement.getEndIndex();
            indentAfter = "";
        }
        HashSet<Declaration> decs = new HashSet<Declaration>();
        Tree.CompilationUnit cu = unit.getCompilationUnit();
        importProposals().importType(decs, returnType, cu);
        int il = (int) importProposals().applyImports(change, decs, cu, doc);
        change.addEdit(new InsertEdit(offset, pdef));
        change.addEdit(new InsertEdit(offset2, indent+adef+indentAfter));
        int exitPos = node.getEndIndex();
        proposals.add(new CreateParameterProposal(pdef, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                dec, returnType, image, offset+il, change, exitPos));
    }

    @Deprecated
    static void addCreateParameterProposal(
            Collection<ICompletionProposal> proposals, 
            IProject project, 
            ValueFunctionDefinitionGenerator dg) {
        if (Character.isLowerCase(dg.getBrokenName().codePointAt(0))) {
            Tree.Declaration decl = 
                    findDeclarationWithBody(dg.getRootNode(), 
                            dg.getNode());
            if (decl == null || 
                    decl.getDeclarationModel() == null || 
                    decl.getDeclarationModel().isActual()) {
                return;
            }

            Tree.ParameterList paramList = getParameters(decl);
            if (paramList != null) {
                String def = dg.generate("", "");
                //TODO: really ugly and fragile way to strip off the trailing ;
                String paramDef = 
                        (paramList.getParameters().isEmpty() ? "" : ", ") + 
                        def.substring(0, def.length() - (def.endsWith("{}")?3:1));
                String paramDesc = 
                        "parameter '" + dg.getBrokenName() + "'";
                TypecheckerUnit u = 
                        dg.getRootNode().getUnit();
                if (u instanceof ModifiableSourceFile) {
                    ModifiableSourceFile cu = (ModifiableSourceFile) u;
                    addCreateParameterProposal(
                            proposals, paramDef, paramDesc,
                            ADD_CORR,
                            decl.getDeclarationModel(),
                            cu.getPhasedUnit(), decl, paramList,
                            dg.getReturnType(),
                            dg.getImports(),
                            dg.getNode());
                }
            }
        }
    }

    @Deprecated
    static void addCreateParameterProposals(
            Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IProject project) {
        FindInvocationVisitor fav = 
                new FindInvocationVisitor(node);
        fav.visit(cu);
        if (fav.result==null) return;
        Tree.Primary prim = fav.result.getPrimary();
        if (prim instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression) prim;
            Reference pr = mte.getTarget();
            if (pr!=null) {
                Declaration d = pr.getDeclaration();
                Type t=null;
                String parameterName=null;
                if (node instanceof Tree.Term) {
                    Tree.Term term = (Tree.Term) node;
                    t = term.getTypeModel();
                    if (t!=null) {
                        parameterName = 
                                t.getDeclaration()
                                    .getName();
                        if (parameterName!=null) {
                            parameterName = 
                                    toInitialLowercase(
                                            parameterName)
                                        .replace("?", "")
                                        .replace("[]", "");
                            if ("string".equals(parameterName)) {
                                parameterName = "text";
                            }
                        }
                    }
                }
                else if (node instanceof Tree.SpecifiedArgument) {
                    Tree.SpecifiedArgument sa = 
                            (Tree.SpecifiedArgument) node;
                    Tree.SpecifierExpression se = 
                            sa.getSpecifierExpression();
                    if (se!=null) {
                        Tree.Expression e = 
                                se.getExpression();
                        if (e!=null) {
                            t = e.getTypeModel();
                        }
                    }
                    parameterName = 
                            sa.getIdentifier().getText();
                }
                else if (node instanceof Tree.TypedArgument) {
                    Tree.TypedArgument ta = 
                            (Tree.TypedArgument) node;
                    t = ta.getType().getTypeModel();
                    parameterName = 
                            ta.getIdentifier().getText();
                }
                if (t!=null && parameterName!=null) {
                    t = node.getUnit().denotableType(t);
                    String defaultValue = 
                            defaultValue(prim.getUnit(), t);
                    String parameterType = 
                            t.asString();
                    String def = parameterType + " " + 
                            parameterName + 
                            " = " + defaultValue;
                    String desc = "parameter '" + 
                            parameterName +"'";
                    addCreateParameterProposals(proposals, 
                            project, def, desc, d, t, node);
                    String pdef = parameterName + 
                            " = " + defaultValue;
                    String adef = parameterType + " " + 
                            parameterName + ";";
                    String padesc = "attribute '" + 
                            parameterName +"'";
                    addCreateParameterAndAttributeProposals(
                            proposals, project, pdef, adef, 
                            padesc, d, t, node);
                }
            }
        }
    }

    @Deprecated
    private static Tree.ParameterList getParameters(
            Tree.Declaration decNode) {
        if (decNode instanceof Tree.AnyClass) {
            Tree.AnyClass ac = (Tree.AnyClass) decNode;
            return ac.getParameterList();
        }
        else if (decNode instanceof Tree.AnyMethod){
            Tree.AnyMethod am = (Tree.AnyMethod) decNode;
            List<Tree.ParameterList> pls = 
                    am.getParameterLists();
            return pls.isEmpty() ? null : pls.get(0);
        }
        else if (decNode instanceof Tree.Constructor) {
            Tree.Constructor c = (Tree.Constructor) decNode;
            return c.getParameterList();
        }
        return null;
    }

    @Deprecated
    private static void addCreateParameterProposals(
            Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, 
            Declaration typeDec, Type t,
            Node node) {
        if (typeDec!=null && typeDec instanceof Functional) {
            Unit u = typeDec.getUnit();
            if (u instanceof ModifiableSourceFile) {
                ModifiableSourceFile cu = (ModifiableSourceFile) u;
                ModifiablePhasedUnit unit = cu.getPhasedUnit();
                FindDeclarationNodeVisitor fdv =
                        new FindDeclarationNodeVisitor(typeDec);
                unit.getCompilationUnit().visit(fdv);
                Tree.Declaration decNode =
                        (Tree.Declaration)
                            fdv.getDeclarationNode();
                Tree.ParameterList paramList =
                        getParameters(decNode);
                if (paramList!=null) {
                    if (!paramList.getParameters().isEmpty()) {
                        def = ", " + def;
                    }
                    Set<Declaration> imports =
                            new HashSet<Declaration>();
                    importType(imports, t,
                            unit.getCompilationUnit());
                    addCreateParameterProposal(
                            proposals, def, desc,
                            ADD_CORR, typeDec, unit, decNode,
                            paramList, t, imports, node);
                }
            }
        }
    }

    @Deprecated
    private static void addCreateParameterAndAttributeProposals(
            Collection<ICompletionProposal> proposals,
            IProject project, String pdef, String adef, 
            String desc, Declaration typeDec, Type t,
            Node node) {
        if (typeDec instanceof ClassOrInterface) {
            Unit u = typeDec.getUnit();
            if (u instanceof ModifiableSourceFile) {
                ModifiablePhasedUnit phasedUnit = ((ModifiableSourceFile) u).getPhasedUnit();
                FindDeclarationNodeVisitor fdv =
                        new FindDeclarationNodeVisitor(typeDec);
                phasedUnit.getCompilationUnit().visit(fdv);
                Tree.Declaration decNode =
                        (Tree.Declaration)
                            fdv.getDeclarationNode();
                Tree.ParameterList paramList =
                        getParameters(decNode);
                Tree.Body body =
                        getClassOrInterfaceBody(decNode);
                if (body!=null
                        && paramList!=null) {
                    if (!paramList.getParameters().isEmpty()) {
                        pdef = ", " + pdef;
                    }
                    addCreateParameterAndAttributeProposal(
                            proposals, pdef, adef, desc,
                            ADD_CORR, typeDec, phasedUnit, decNode,
                            paramList, body, t, node);
                }
            }
        }
    }
        
}
