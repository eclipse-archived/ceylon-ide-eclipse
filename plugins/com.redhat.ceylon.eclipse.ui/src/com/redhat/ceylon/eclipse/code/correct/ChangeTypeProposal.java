package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importProposals;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoFile;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.intersectionType;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.unionType;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.core.model.ModifiableSourceFile;
import com.redhat.ceylon.eclipse.core.typechecker.ModifiablePhasedUnit;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.IntersectionType;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

class ChangeTypeProposal extends CorrectionProposal {

    private Unit unit;

    ChangeTypeProposal(ProblemLocation problem,
            String name, String type, 
            int offset, int len, Unit unit,
            TextFileChange change) {
        super("Change type of "+ name + " to '" + type + "'", 
                change, new Region(offset, len));
        this.unit = unit;
    }

    @Override
    public void apply(IDocument document) {
        CeylonEditor editor = null;
        if (unit instanceof ModifiableSourceFile) {
            ModifiableSourceFile cu =
                    (ModifiableSourceFile) unit;
            IFile file = cu.getResourceFile();
            if (file!=null) {
                editor = (CeylonEditor) gotoFile(file, 0, 0);
                //NOTE: the document we're given is the one
                //for the editor from which the quick fix was
                //invoked, not the one to which the fix applies
                IDocument ed =
                        editor.getParseController()
                            .getDocument();
                if (ed!=document) {
                    document = ed;
                }
            }
        }
        super.apply(document);
        if (editor!=null) {
            Point point = super.getSelection(document);
            editor.selectAndReveal(point.x, point.y);
        }
    }

    @Override
    public Point getSelection(IDocument document) {
        //we don't apply a selection because
        //the change might have been applied to
        //a different editor to the one from
        //which the quick fix was invoked.
        return null;
    }

    private static void addChangeTypeProposal(Node node,
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals,
            Declaration dec, 
            Type newType, 
            IFile file, 
            Tree.CompilationUnit cu) {
        if (node.getStartIndex() == null || 
                node.getEndIndex() == null) {
            return;
        }
        if (newType.isNothing()) {
            return;
        }
        if (ModelUtil.isConstructor(dec)) {
            return;
        }
        TextFileChange change = 
                new TextFileChange("Change Type", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        int offset = node.getStartIndex();
        int length = node.getDistance();
        HashSet<Declaration> decs = 
                new HashSet<Declaration>();
        importProposals().importType(decs, newType, cu);
        int il = (int) importProposals().applyImports(change, decs, cu, doc);
        Unit unit = cu.getUnit();
        String newTypeName = 
                newType.asSourceCodeString(unit);
        change.addEdit(new ReplaceEdit(offset, length, 
                newTypeName));
        String name;
        if (dec.isParameter()) {
            Declaration container = 
                    (Declaration)
                        dec.getContainer();
            name = "parameter '" +
                    dec.getName() + "' of '" +
                    container.getName() + "'";
        }
        else if (dec.isClassOrInterfaceMember()) {
            ClassOrInterface container = 
                    (ClassOrInterface)
                        dec.getContainer();
            name = "member '" +
                    dec.getName() + "' of '" +
                    container.getName() + "'";
        }
        else {
            name = "'" + dec.getName() + "'";
        }
        proposals.add(new ChangeTypeProposal(
                problem, name,
                newType.asString(unit),
                offset+il, newTypeName.length(),
                unit, change));
    }
    
    static void addChangeTypeArgProposals(
            Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IProject project) {
        if (node instanceof Tree.SimpleType) {
            Tree.SimpleType stn = (Tree.SimpleType) node;
            TypeDeclaration decl = stn.getDeclarationModel();
            if (decl instanceof TypeParameter) {
                Tree.Statement statement = 
                        findStatement(cu, node);
                if (statement instanceof Tree.TypedDeclaration) {
                    Tree.TypedDeclaration ad = 
                            (Tree.TypedDeclaration) 
                                statement;
                    Tree.Type adt = ad.getType();
                    if (adt instanceof Tree.SimpleType) {
                        Tree.SimpleType st = 
                                (Tree.SimpleType) adt;
                        TypeParameter stTypeParam = null;
                        Tree.TypeArgumentList tal = 
                                st.getTypeArgumentList();
                        if (tal!=null) {
                            List<Tree.Type> stas = 
                                    tal.getTypes();
                            for (int i=0; i<stas.size(); i++) {
                                Tree.Type sta = stas.get(i);
                                Tree.SimpleType ssta = 
                                        (Tree.SimpleType) sta;
                                TypeDeclaration d = 
                                        ssta.getDeclarationModel();
                                if (decl.getName()
                                        .equals(d.getName())) {
                                    TypeDeclaration std = 
                                            st.getDeclarationModel();
                                    if (std!=null) {
                                        List<TypeParameter> tps = 
                                                std.getTypeParameters();
                                        if (tps!=null && tps.size()>i) {
                                            stTypeParam = tps.get(i);
                                            break;
                                        }
                                    }                            
                                }
                            }                    
                        }
                        
                        if (stTypeParam!=null) {
                            List<Type> sts = 
                                    stTypeParam.getSatisfiedTypes();
                            if (!sts.isEmpty()) {
                                IntersectionType it = 
                                        new IntersectionType(
                                                cu.getUnit());
                                it.setSatisfiedTypes(sts);
                                addChangeTypeProposals(proposals, 
                                        problem, project, node, 
                                        it.canonicalize().getType(), 
                                        decl, true);
                            }
                        }
                    }
                }
            }
        }
    }
    
    static void addChangeTypeProposals(
            Tree.CompilationUnit rootNode, Node node,
            ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, 
            IProject project) {
        if (node instanceof Tree.SpecifierExpression) {
            Tree.SpecifierExpression se = 
                    (Tree.SpecifierExpression) node;
            Tree.Expression e = se.getExpression();
            if (e!=null) {
                node = e.getTerm();
            }
        }
        if (node instanceof Tree.Expression) {
            Tree.Expression e = (Tree.Expression) node;
            node = e.getTerm();
        }
        if (node instanceof Tree.Term) {
            Tree.Term term = (Tree.Term) node;
            Type t = term.getTypeModel();
            if (t==null) return;
            Type type = node.getUnit().denotableType(t);
            FindInvocationVisitor fav = 
                    new FindInvocationVisitor(node);
            fav.visit(rootNode);
            TypedDeclaration td = fav.parameter;
            if (td!=null) {
                if (node instanceof Tree.InvocationExpression) {
                    Tree.InvocationExpression ie = 
                            (Tree.InvocationExpression) node;
                    node = ie.getPrimary(); 
                }
                if (node instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = 
                            (Tree.BaseMemberExpression) node;
                    TypedDeclaration d = (TypedDeclaration) 
                            bme.getDeclaration();
                    addChangeTypeProposals(proposals, 
                            problem, project, node, 
                            td.getType(), d, true);
                }
                if (node instanceof Tree.QualifiedMemberExpression) {
                    Tree.QualifiedMemberExpression qme = 
                            (Tree.QualifiedMemberExpression) node;
                    TypedDeclaration d = (TypedDeclaration) 
                            qme.getDeclaration();
                    addChangeTypeProposals(proposals, 
                            problem, project, node, 
                            td.getType(), d, true);
                }
                addChangeTypeProposals(proposals, 
                        problem, project, node, 
                        type, td, false);
            }
        }
    }
    
    private static void addChangeTypeProposals(
            Collection<ICompletionProposal> proposals,
            ProblemLocation problem, IProject project, 
            Node node, Type type, 
            Declaration dec, boolean intersect) {
        if (dec!=null) {
            Unit u = dec.getUnit();
            if (u instanceof ModifiableSourceFile) {
                ModifiableSourceFile msf =
                        (ModifiableSourceFile) u;
                ModifiablePhasedUnit phasedUnit =
                        msf.getPhasedUnit();
                Type t = null;
                Node typeNode = null;

                if (dec instanceof TypeParameter) {
                    TypeParameter tp =
                            (TypeParameter) dec;
                    t = tp.getType();
                    typeNode = node;
                }

                if (dec instanceof TypedDeclaration) {
                    TypedDeclaration typedDec =
                            (TypedDeclaration) dec;
                    FindDeclarationNodeVisitor fdv =
                            new FindDeclarationNodeVisitor(
                                    typedDec);
                    phasedUnit.getCompilationUnit()
                            .visit(fdv);
                    Tree.StatementOrArgument dn =
                            fdv.getDeclarationNode();
                    if (dn instanceof Tree.TypedDeclaration) {
                        Tree.TypedDeclaration decNode =
                                (Tree.TypedDeclaration) dn;
                        if (decNode!=null) {
                            typeNode = decNode.getType();
                            if (typeNode!=null) {
                                Tree.Type tn =
                                        (Tree.Type)
                                            typeNode;
                                t = tn.getTypeModel();
                            }
                        }
                    }
                }

                //TODO: fix this condition to properly
                //      distinguish between a method
                //      reference and an invocation
                Unit nu = node.getUnit();
                if (dec instanceof Function &&
                        nu.isCallableType(type)) {
                    type = nu.getCallableReturnType(type);
                }

                IFile file = phasedUnit.getResourceFile();
                if (typeNode != null
                        && file != null
                            && !isTypeUnknown(type)) {
                    Tree.CompilationUnit rootNode =
                            phasedUnit.getCompilationUnit();
                    addChangeTypeProposal(typeNode,
                            problem, proposals, dec,
                            type, file, rootNode);
                    if (t != null) {
                        Type newType = intersect ?
                                intersectionType(t, type, u) :
                                unionType(t, type, u);
                        if (!newType.isExactly(t) &&
                                !newType.isExactly(type)) {
                            addChangeTypeProposal(typeNode,
                                    problem, proposals,
                                    dec, newType, file,
                                    rootNode);
                        }
                    }
                }
            }
        }
    }

}
