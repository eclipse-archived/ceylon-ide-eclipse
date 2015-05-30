package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.model.typechecker.model.Util.intersectionType;
import static com.redhat.ceylon.model.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.model.typechecker.model.Util.unionType;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getRootNode;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.IntersectionType;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

class ChangeTypeProposal extends CorrectionProposal {

    ChangeTypeProposal(ProblemLocation problem, 
            String name, String type, int offset, int len,
            TextFileChange change) {
        super("Change type of "+ name + " to '" + type + "'", 
                change, new Region(offset, len));
    }
        
    static void addChangeTypeProposal(Node node, ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, Declaration dec, 
            Type newType, IFile file, Tree.CompilationUnit cu) {
        if (node.getStartIndex() == null || node.getStopIndex() == null) {
            return;
        }
        if (newType.isNothing()) {
            return;
        }
        TextFileChange change =  new TextFileChange("Change Type", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = EditorUtil.getDocument(change);
        int offset = node.getStartIndex();
        int length = node.getStopIndex()-offset+1;
        HashSet<Declaration> decs = new HashSet<Declaration>();
        importType(decs, newType, cu);
        int il=applyImports(change, decs, cu, doc);
        String newTypeName = 
                newType.getProducedTypeNameInSource(cu.getUnit());
        change.addEdit(new ReplaceEdit(offset, length, 
                newTypeName));
        String name;
        if (dec.isParameter()) {
            name = "parameter '" + dec.getName() + "' of '" + 
                    ((Declaration) dec.getContainer()).getName() + "'";
        }
        else if (dec.isClassOrInterfaceMember()) {
            name = "member '" +  dec.getName() + "' of '" + 
                    ((ClassOrInterface) dec.getContainer()).getName() + "'";
        }
        else {
            name = "'" + dec.getName() + "'";
        }
        proposals.add(new ChangeTypeProposal(problem, name, 
                newType.getProducedTypeName(cu.getUnit()), 
                offset+il, newTypeName.length(), change));
    }
    
    static void addChangeTypeArgProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project) {
        if (node instanceof Tree.SimpleType) {
            TypeDeclaration decl = ((Tree.SimpleType) node).getDeclarationModel();
            if (decl instanceof TypeParameter) {
                Tree.Statement statement = findStatement(cu, node);
                if (statement instanceof Tree.TypedDeclaration) {
                    Tree.TypedDeclaration ad = (Tree.TypedDeclaration) statement;
                    if (ad.getType() instanceof Tree.SimpleType) {
                        Tree.SimpleType st = (Tree.SimpleType) ad.getType();
                        
                        TypeParameter stTypeParam = null;
                        if (st.getTypeArgumentList() != null) {
                            List<Tree.Type> stTypeArguments = 
                                    st.getTypeArgumentList().getTypes();
                            for (int i=0; i<stTypeArguments.size(); i++) {
                                Tree.SimpleType stTypeArgument = 
                                        (Tree.SimpleType) stTypeArguments.get(i);
                                if (decl.getName().equals(
                                        stTypeArgument.getDeclarationModel().getName())) {
                                    TypeDeclaration stDecl = st.getDeclarationModel();
                                    if (stDecl != null) {
                                        if (stDecl.getTypeParameters()!=null && 
                                                stDecl.getTypeParameters().size()>i) {
                                            stTypeParam = stDecl.getTypeParameters().get(i);
                                            break;
                                        }
                                    }                            
                                }
                            }                    
                        }
                        
                        if (stTypeParam != null && 
                                !stTypeParam.getSatisfiedTypes().isEmpty()) {
                            IntersectionType it = new IntersectionType(cu.getUnit());
                            it.setSatisfiedTypes(stTypeParam.getSatisfiedTypes());
                            addChangeTypeProposals(proposals, problem, project, node, 
                                    it.canonicalize().getType(), decl, true);
                        }
                    }
                }
            }
        }
    }
    
    static void addChangeTypeProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project) {
        if (node instanceof Tree.SpecifierExpression) {
            Tree.Expression e = ((Tree.SpecifierExpression) node).getExpression();
            if (e!=null) {
                node = e.getTerm();
            }
        }
        if (node instanceof Tree.Expression) {
            node = ((Tree.Expression) node).getTerm();
        }
        if (node instanceof Tree.Term) {
            Type t = ((Tree.Term) node).getTypeModel();
            if (t==null) return;
            Type type = node.getUnit().denotableType(t);
            FindInvocationVisitor fav = new FindInvocationVisitor(node);
            fav.visit(cu);
            TypedDeclaration td = fav.parameter;
            if (td!=null) {
                if (node instanceof Tree.InvocationExpression) {
                    node = ((Tree.InvocationExpression) node).getPrimary(); 
                }
                if (node instanceof Tree.BaseMemberExpression) {
                    TypedDeclaration d = (TypedDeclaration) 
                            ((Tree.BaseMemberExpression) node).getDeclaration();
                    addChangeTypeProposals(proposals, problem, project, node, 
                            td.getType(), d, true);
                }
                if (node instanceof Tree.QualifiedMemberExpression){
                    TypedDeclaration d = (TypedDeclaration) 
                            ((Tree.QualifiedMemberExpression) node).getDeclaration();
                    addChangeTypeProposals(proposals, problem, project, node, 
                            td.getType(), d, true);
                }
                addChangeTypeProposals(proposals, problem, project, 
                        node, type, td, false);
            }
        }
    }
    
    private static void addChangeTypeProposals(Collection<ICompletionProposal> proposals,
            ProblemLocation problem, IProject project, Node node, Type type, 
            Declaration dec, boolean intersect) {
        if (dec!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    Type t = null;
                    Node typeNode = null;
                    
                    if (dec instanceof TypeParameter) {
                        t = ((TypeParameter) dec).getType();
                        typeNode = node;
                    }
                    
                    if (dec instanceof TypedDeclaration) {
                        TypedDeclaration typedDec = (TypedDeclaration) dec;
                        FindDeclarationNodeVisitor fdv = 
                                new FindDeclarationNodeVisitor(typedDec);
                        getRootNode(unit).visit(fdv);
                        Tree.TypedDeclaration decNode = 
                                (Tree.TypedDeclaration) fdv.getDeclarationNode();
                        if (decNode!=null) {
                            typeNode = decNode.getType();
                            if (typeNode!=null) {
                                t= ((Tree.Type) typeNode).getTypeModel();
                            }
                        }
                    }
                    
                    //TODO: fix this condition to properly distinguish
                    //      between a method reference and an invocation
                    if (dec instanceof Function && 
                            node.getUnit().isCallableType(type)) {
                        type = node.getUnit().getCallableReturnType(type);
                    }
                    
                    if (typeNode != null && !isTypeUnknown(type)) {
                        IFile file = getFile(unit);
                        Tree.CompilationUnit rootNode = unit.getCompilationUnit();
                        addChangeTypeProposal(typeNode, problem, 
                                proposals, dec, type, file, rootNode);
                        if (t != null) {
                            Type newType = intersect ? 
                                    intersectionType(t, type, unit.getUnit()) : 
                                    unionType(t, type, unit.getUnit());
                            if (!newType.isExactly(t) && !newType.isExactly(type)) {
                                addChangeTypeProposal(typeNode, problem, 
                                        proposals, dec, newType, file, rootNode);
                            }
                        }
                    }
                }
            }
        }
    }

}
