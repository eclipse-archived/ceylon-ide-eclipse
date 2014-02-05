package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.compiler.typechecker.model.Util.intersectionType;
import static com.redhat.ceylon.compiler.typechecker.model.Util.unionType;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getRootNode;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateProposal.getDocument;
import static com.redhat.ceylon.eclipse.code.quickfix.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

class ChangeTypeProposal extends ChangeCorrectionProposal {

    final int offset;
    final int length;
    final IFile file;
    
    ChangeTypeProposal(ProblemLocation problem, IFile file, 
            String name, String type, int offset,
            TextFileChange change) {
        super("Change type of '"+ name + "' to '" + type + "'", 
                change);
        this.offset = offset;
        this.length = type.length();
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addChangeTypeProposal(Node node, ProblemLocation problem, 
            Collection<ICompletionProposal> proposals, Declaration dec, 
            ProducedType newType, IFile file, Tree.CompilationUnit cu) {
        // better safe than throwing
        if(node.getStartIndex() == null || node.getStopIndex() == null)
            return;
        TextFileChange change =  new TextFileChange("Change Type", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        String typeName = newType.getProducedTypeName();
        int offset = node.getStartIndex();
        int length = node.getStopIndex()-offset+1;
        HashSet<Declaration> decs = new HashSet<Declaration>();
		importType(decs, newType, cu);
		int il=applyImports(change, decs, cu, doc);
        change.addEdit(new ReplaceEdit(offset, length, typeName));
        proposals.add(new ChangeTypeProposal(problem, file, dec.getName(), 
                typeName, offset+il, change));
    }
    
    static void addChangeTypeProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project) {
        if (node instanceof Tree.SimpleType) {
            TypeDeclaration decl = ((Tree.SimpleType)node).getDeclarationModel();
            if( decl instanceof TypeParameter ) {
            	Tree.Statement statement = findStatement(cu, node);
                if(statement instanceof Tree.AttributeDeclaration ) {
                    Tree.AttributeDeclaration ad = (Tree.AttributeDeclaration) statement;
                    Tree.SimpleType st = (Tree.SimpleType) ad.getType();

                    TypeParameter stTypeParam = null;
                    if( st.getTypeArgumentList() != null ) {
                        List<Tree.Type> stTypeArguments = st.getTypeArgumentList().getTypes();
                        for (int i = 0; i < stTypeArguments.size(); i++) {
                            Tree.SimpleType stTypeArgument = (Tree.SimpleType)stTypeArguments.get(i);
                            if (decl.getName().equals(
                                    stTypeArgument.getDeclarationModel().getName())) {
                                TypeDeclaration stDecl = st.getDeclarationModel();
                                if( stDecl != null ) {
                                    if( stDecl.getTypeParameters() != null && stDecl.getTypeParameters().size() > i ) {
                                        stTypeParam = stDecl.getTypeParameters().get(i);
                                        break;
                                    }
                                }                            
                            }
                        }                    
                    }

                    if (stTypeParam != null && !stTypeParam.getSatisfiedTypes().isEmpty()) {
                        IntersectionType it = new IntersectionType(cu.getUnit());
                        it.setSatisfiedTypes(stTypeParam.getSatisfiedTypes());
                        addChangeTypeProposals(proposals, problem, project, node, it.canonicalize().getType(), decl, true);
                    }
                }
            }
        }
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
            ProducedType t = ((Tree.Term) node).getTypeModel();
            if (t==null) return;
            ProducedType type = node.getUnit().denotableType(t);
            FindInvocationVisitor fav = new FindInvocationVisitor(node);
            fav.visit(cu);
            TypedDeclaration td = fav.parameter;
            if (td!=null) {
            	if (node instanceof Tree.BaseMemberExpression){
            		addChangeTypeProposals(proposals, problem, project, node, td.getType(), 
            				(TypedDeclaration) ((Tree.BaseMemberExpression) node).getDeclaration(), true);
            	}
            	if (node instanceof Tree.QualifiedMemberExpression){
            		addChangeTypeProposals(proposals, problem, project, node, td.getType(), 
            				(TypedDeclaration) ((Tree.QualifiedMemberExpression) node).getDeclaration(), true);
            	}
            	addChangeTypeProposals(proposals, problem, project, node, type, td, false);
            }
        }
    }
    
    static void addChangeTypeProposals(Collection<ICompletionProposal> proposals,
            ProblemLocation problem, IProject project, Node node, ProducedType type, 
            Declaration dec, boolean intersect) {
        if (dec!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    ProducedType t = null;
                    Node typeNode = null;
                    
                    if( dec instanceof TypeParameter) {
                        t = ((TypeParameter) dec).getType();
                        typeNode = node;
                    }
                    
                    if( dec instanceof TypedDeclaration ) {
                        TypedDeclaration typedDec = (TypedDeclaration)dec;
                        FindDeclarationNodeVisitor fdv = 
                        		new FindDeclarationNodeVisitor(typedDec);
                        getRootNode(unit).visit(fdv);
                        Tree.TypedDeclaration decNode = 
                        		(Tree.TypedDeclaration) fdv.getDeclarationNode();
                        if (decNode!=null) {
                            typeNode = decNode.getType();
                            if (typeNode!=null) {
                                t=((Tree.Type)typeNode).getTypeModel();
                            }
                        }
                    }
                    
                    if (t != null && typeNode != null) {
                        ProducedType newType = intersect ? 
                                intersectionType(t, type, unit.getUnit()) : 
                                unionType(t, type, unit.getUnit());
                        addChangeTypeProposal(typeNode, problem, 
                                proposals, dec, newType, getFile(unit), 
                                unit.getCompilationUnit());
                    }
                }
            }
        }
    }

}
