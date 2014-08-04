package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getRootNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.UnknownType;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

public class AddAnnotionProposal extends CorrectionProposal {
    
    private static final List<String> ANNOTATIONS_ORDER = 
            asList("doc", "throws", "see", "tagged", "shared", "abstract", 
                    "actual", "formal", "default", "variable");
    private static final List<String> ANNOTATIONS_ON_SEPARATE_LINE = 
            asList("doc", "throws", "see", "tagged");
    
    private final Declaration dec;
    private final String annotation;
    
    AddAnnotionProposal(Declaration dec, String annotation, 
            String desc, int offset, TextFileChange change, 
            Region selection) {
        super(desc, change, selection);
        this.dec = dec;
        this.annotation = annotation;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddAnnotionProposal) {
            AddAnnotionProposal that = (AddAnnotionProposal) obj;
            return that.dec.equals(dec) && 
                    that.annotation.equals(annotation);
        }
        else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return dec.hashCode();
    }

    private static void addAddAnnotationProposal(Node node, String annotation, 
            String desc, Declaration dec, Collection<ICompletionProposal> proposals, 
            IProject project) {
        if (dec!=null && dec.getName()!=null && 
                !(node instanceof Tree.MissingDeclaration)) {
            for (PhasedUnit unit: getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationNodeVisitor fdv = 
                            new FindDeclarationNodeVisitor(dec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = 
                            (Tree.Declaration) fdv.getDeclarationNode();
                    if (decNode!=null) {
                        addAddAnnotationProposal(annotation, desc, dec,
                                proposals, unit, node, decNode);
                    }
                    break;
                }
            }
        }
    }

    private static void addAddAnnotationProposal(String annotation, String desc, 
            Declaration dec, Collection<ICompletionProposal> proposals, 
            PhasedUnit unit, Node node, Tree.Declaration decNode) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange(desc, file);
        change.setEdit(new MultiTextEdit());
        TextEdit edit = createReplaceAnnotationEdit(annotation, node, change);
        if (edit==null) {
        	edit = createInsertAnnotationEdit(annotation, decNode, 
        			EditorUtil.getDocument(change));
        }
        change.addEdit(edit);
        if (decNode instanceof Tree.TypedDeclaration &&
                !(decNode instanceof Tree.ObjectDefinition)) {
            Tree.Type type = ((Tree.TypedDeclaration) decNode).getType();
            if (type.getToken()!=null &&
                    (type instanceof Tree.FunctionModifier || 
                     type instanceof Tree.ValueModifier)) {
                ProducedType it = type.getTypeModel();
                if (it!=null && !(it.getDeclaration() instanceof UnknownType)) {
                    String explicitType = it.getProducedTypeName();
                    change.addEdit(new ReplaceEdit(type.getStartIndex(), 
                            type.getText().length(), explicitType));
                }
            }
        }
        Region selection;
        if (node!=null && node.getUnit().equals(decNode.getUnit())) {
            selection = new Region(edit.getOffset(), annotation.length());
        }
        else {
            selection = null;
        }
        Scope container = dec.getContainer();
        String containerDesc = container instanceof TypeDeclaration ?
                " in '" + ((TypeDeclaration) container).getName() + "'" : "";
        String description = 
                "Make '" + dec.getName() + "' " + annotation + containerDesc;
        AddAnnotionProposal p = new AddAnnotionProposal(dec, annotation, 
        		description, edit.getOffset(), change, selection);
        if (!proposals.contains(p)) {
            proposals.add(p);
        }
    }

	private static ReplaceEdit createReplaceAnnotationEdit(String annotation,
			Node node, TextFileChange change) {
		String toRemove;
		if ("formal".equals(annotation)) {
			toRemove = "default";
		}
		else if ("abstract".equals(annotation)) {
			toRemove = "final";
		}
		else {
			return null;
		}
		Tree.AnnotationList annotationList = getAnnotationList(node);
		if (annotationList != null) {
			for (Tree.Annotation ann:
				annotationList.getAnnotations()) {
				if (toRemove.equals(getAnnotationIdentifier(ann))) {
					return new ReplaceEdit(ann.getStartIndex(), 
							ann.getStopIndex()+1-ann.getStartIndex(),
							annotation);
				}
			}
		}
		return null;
	}

    public static InsertEdit createInsertAnnotationEdit(String newAnnotation, 
            Node node, IDocument doc) {
        String newAnnotationName = getAnnotationWithoutParam(newAnnotation);

        Tree.Annotation prevAnnotation = null;
        Tree.Annotation nextAnnotation = null;
        Tree.AnnotationList annotationList = getAnnotationList(node);
        if (annotationList != null) {
            for (Tree.Annotation annotation:
                    annotationList.getAnnotations()) {
                if (isAnnotationAfter(newAnnotationName, 
                        getAnnotationIdentifier(annotation))) {
                    prevAnnotation = annotation;
                } else if (nextAnnotation == null) {
                    nextAnnotation = annotation;
                    break;
                }
            }
        }

        int nextNodeStartIndex;
        if (nextAnnotation != null) {
            nextNodeStartIndex = nextAnnotation.getStartIndex();
        }
        else {
            if (node instanceof Tree.AnyAttribute || 
                node instanceof Tree.AnyMethod ) {
                nextNodeStartIndex = 
                        ((Tree.TypedDeclaration) node).getType().getStartIndex();
            }
            else if (node instanceof Tree.ObjectDefinition ) {
                nextNodeStartIndex = 
                        ((CommonToken) node.getMainToken()).getStartIndex();
            }
            else if (node instanceof Tree.ClassOrInterface) {
                nextNodeStartIndex = 
                        ((CommonToken) node.getMainToken()).getStartIndex();
            }
            else {
                nextNodeStartIndex = node.getStartIndex();
            }
        }

        int newAnnotationOffset;
        StringBuilder newAnnotationText = new StringBuilder();

        if (isAnnotationOnSeparateLine(newAnnotationName) && 
                !(node instanceof Tree.Parameter)) {
            if (prevAnnotation != null && 
                    isAnnotationOnSeparateLine(getAnnotationIdentifier(prevAnnotation))) {
                newAnnotationOffset = prevAnnotation.getStopIndex() + 1;
                newAnnotationText.append(System.lineSeparator());
                newAnnotationText.append(getIndent(node, doc));
                newAnnotationText.append(newAnnotation);
            } else {
                newAnnotationOffset = nextNodeStartIndex;
                newAnnotationText.append(newAnnotation);
                newAnnotationText.append(System.lineSeparator());
                newAnnotationText.append(getIndent(node, doc));
            }
        } else {
            newAnnotationOffset = nextNodeStartIndex;
            newAnnotationText.append(newAnnotation);
            newAnnotationText.append(" ");
        }

        return new InsertEdit(newAnnotationOffset, 
                newAnnotationText.toString());
    }

    public static Tree.AnnotationList getAnnotationList(Node node) {
        Tree.AnnotationList annotationList = null;
        if (node instanceof Tree.Declaration) {
            annotationList = 
                    ((Tree.Declaration) node).getAnnotationList();
        }
        else if (node instanceof Tree.ModuleDescriptor) {
            annotationList = 
                    ((Tree.ModuleDescriptor) node).getAnnotationList();
        }
        else if (node instanceof Tree.PackageDescriptor) {
            annotationList = 
                    ((Tree.PackageDescriptor) node).getAnnotationList();
        }
        else if (node instanceof Tree.Assertion) {
            annotationList = 
                    ((Tree.Assertion) node).getAnnotationList();
        }
        return annotationList;
    }

    public static String getAnnotationIdentifier(Tree.Annotation annotation) {
        String annotationName = null;
        if (annotation != null) {
            if (annotation.getPrimary() instanceof Tree.BaseMemberExpression) {
                Tree.BaseMemberExpression bme = 
                        (Tree.BaseMemberExpression) annotation.getPrimary();
                annotationName = bme.getIdentifier().getText();
            }
        }
        return annotationName;
    }

    private static String getAnnotationWithoutParam(String annotation) {
        int index = annotation.indexOf("(");
        if (index != -1) {
            return annotation.substring(0, index).trim();
        }
        index = annotation.indexOf("\"");
        if (index != -1) {
            return annotation.substring(0, index).trim();
        }
        index = annotation.indexOf(" ");
        if (index != -1) {
            return annotation.substring(0, index).trim();
        }
        return annotation.trim();
    }

    private static boolean isAnnotationAfter(String annotation1, String annotation2) {
        int index1 = ANNOTATIONS_ORDER.indexOf(annotation1);
        int index2 = ANNOTATIONS_ORDER.indexOf(annotation2);
        return index1 >= index2;
    }

    private static boolean isAnnotationOnSeparateLine(String annotation) {
        return ANNOTATIONS_ON_SEPARATE_LINE.contains(annotation);
    }
    
    static void addMakeActualDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            dec = ((Tree.Declaration) node).getDeclarationModel();
        }
        else {
            dec = (Declaration) node.getScope();
        }
        boolean shared = dec.isShared();
        addAddAnnotationProposal(node, 
        		shared ? "actual" : "shared actual", 
                shared ? "Make Actual" : "Make Shared Actual",
                dec, proposals, project);
    }

    static void addMakeDefaultProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        
        Declaration d;
        if (node instanceof Tree.Declaration) {
            Tree.Declaration decNode = (Tree.Declaration) node;
            d = decNode.getDeclarationModel();
        }
        else if (node instanceof Tree.BaseMemberExpression) {
            d = ((Tree.BaseMemberExpression) node).getDeclaration();
        }
        else {
            return;
        }
        
        if (d.isClassOrInterfaceMember()) {
            List<Declaration> rds = 
                    ((ClassOrInterface) d.getContainer())
                            .getInheritedMembers(d.getName());
            Declaration rd=null;
            if (rds.isEmpty()) {
                rd=d; //TODO: is this really correct? What case does it handle?
            }
            else {
                for (Declaration r: rds) {
                    if (!r.isDefault()) {
                        //just take the first one :-/
                        //TODO: this is very wrong! Instead, make them all default!
                        rd = r;
                        break;
                    }
                }
            }
            if (rd!=null) {
                addAddAnnotationProposal(node, 
                		"default", "Make Default", 
                        rd, proposals, project);
            }
        }
    }

    static void addMakeDefaultDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
    	Declaration dec;
        if (node instanceof Tree.Declaration) {
            dec = ((Tree.Declaration) node).getDeclarationModel();
        }
        else {
            dec = (Declaration) node.getScope();
        }
        addAddAnnotationProposal(node, 
                dec.isShared() ? "default" : "shared default", 
                dec.isShared() ? "Make Default" : "Make Shared Default", 
                dec, proposals, project);
    }

    static void addMakeFormalDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
    	Declaration dec;
        if (node instanceof Tree.Declaration) {
            dec = ((Tree.Declaration) node).getDeclarationModel();
        }
        else {
            dec = (Declaration) node.getScope();
        }
        addAddAnnotationProposal(node, 
                dec.isShared() ? "formal" : "shared formal",
                dec.isShared() ? "Make Formal" : "Make Shared Formal",
                dec, proposals, project);
    }

    static void addMakeAbstractDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            dec = ((Tree.Declaration) node).getDeclarationModel();
        }
        else {
            dec = (Declaration) node.getScope();
        }
        if (dec instanceof Class) {
            addAddAnnotationProposal(node, 
            		"abstract", "Make Abstract", 
                    dec, proposals, project);
        }
    }

    static void addMakeContainerAbstractProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            Scope container = 
                    ((Tree.Declaration) node).getDeclarationModel().getContainer();
            if (container instanceof Declaration) {
                dec = (Declaration) container;
            }
            else {
                return;
            }
        }
        else {
            dec = (Declaration) node.getScope();
        }
        addAddAnnotationProposal(node, 
        		"abstract", "Make Abstract", 
                dec, proposals, project);
    }

    static void addMakeVariableProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Term term;
        if (node instanceof Tree.AssignmentOp) {
            term = ((Tree.AssignOp) node).getLeftTerm();
        }
        else if (node instanceof Tree.UnaryOperatorExpression) {
            term = ((Tree.PrefixOperatorExpression) node).getTerm();
        }
        else if (node instanceof Tree.MemberOrTypeExpression) {
            term = (Tree.MemberOrTypeExpression) node;
        }
        else if (node instanceof Tree.SpecifierStatement) {
            term = ((Tree.SpecifierStatement) node).getBaseMemberExpression();
        }
        else {
            return;
        }
        if (term instanceof Tree.MemberOrTypeExpression) {
            Declaration dec = 
                    ((Tree.MemberOrTypeExpression) term).getDeclaration();
            if (dec instanceof Value) {
                if (((Value) dec).getOriginalDeclaration()==null) {
                    addAddAnnotationProposal(node, 
                    		"variable", "Make Variable", 
                            dec, proposals, project);
                }
            }
        }
    }
    
    static void addMakeVariableDecProposal(Collection<ICompletionProposal> proposals,
            IProject project, Tree.Declaration node) {
        final Declaration dec = node.getDeclarationModel();
        if (dec instanceof Value && node instanceof Tree.AttributeDeclaration) {
            final Value v = (Value) dec;
            if (!v.isVariable() && !v.isTransient()) {
                addAddAnnotationProposal(node, 
                		"variable", "Make Variable",
                        dec, proposals, project);
            }
        }
    }
    static void addMakeVariableDecProposal(Collection<ICompletionProposal> proposals,
            IProject project, Tree.CompilationUnit cu, Node node) {
        final Tree.SpecifierOrInitializerExpression sie = 
                (Tree.SpecifierOrInitializerExpression) node;
        class GetInitializedVisitor extends Visitor {
            Value dec;
            @Override
            public void visit(Tree.AttributeDeclaration that) {
                super.visit(that);
                if (that.getSpecifierOrInitializerExpression()==sie) {
                    dec = that.getDeclarationModel();
                }
            }
        }
        GetInitializedVisitor v = new GetInitializedVisitor();
        v.visit(cu);
        addAddAnnotationProposal(node, 
        		"variable", "Make Variable",
                v.dec, proposals, project);
    }
    
    static void addMakeSharedProposalForSupertypes(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        if (node instanceof Tree.ClassOrInterface) {
            Tree.ClassOrInterface c = (Tree.ClassOrInterface) node;

            ProducedType extendedType = 
                    c.getDeclarationModel().getExtendedType();
            if (extendedType!=null) {
                addMakeSharedProposal(proposals, project, 
                        extendedType.getDeclaration());
                for (ProducedType typeArgument:
                        extendedType.getTypeArgumentList()) {
                    addMakeSharedProposal(proposals, project, 
                            typeArgument.getDeclaration());
                }
            }
            
            List<ProducedType> satisfiedTypes = 
                    c.getDeclarationModel().getSatisfiedTypes();
            if (satisfiedTypes!=null) {
                for (ProducedType satisfiedType: satisfiedTypes) {
                    addMakeSharedProposal(proposals, project, 
                            satisfiedType.getDeclaration());
                    for (ProducedType typeArgument: 
                            satisfiedType.getTypeArgumentList()) {
                        addMakeSharedProposal(proposals, project, 
                                typeArgument.getDeclaration());
                    }
                }
            }
        }
    }
    
    static void addMakeRefinedSharedProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        if (node instanceof Tree.Declaration) {
            Declaration refined = ((Tree.Declaration) node).getDeclarationModel()
                    .getRefinedDeclaration();
            if (refined.isDefault() || refined.isFormal()) {
                addMakeSharedProposal(proposals, project, refined);
            }
            else {
                addAddAnnotationProposal(node, 
                		"shared default", "Make Shared Default", 
                        refined, proposals, project);
            }
        }
    }
    
    static void addMakeSharedProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec = null;
        List<ProducedType> typeArgumentList = null;
        if (node instanceof Tree.StaticMemberOrTypeExpression) {
            Tree.StaticMemberOrTypeExpression qmte = 
                    (Tree.StaticMemberOrTypeExpression) node;
            dec = qmte.getDeclaration();
        }
        //TODO: handle much more kinds of types!
        else if (node instanceof Tree.SimpleType) {
            Tree.SimpleType st = (Tree.SimpleType) node;
            dec = st.getDeclarationModel();
        }
        else if (node instanceof Tree.OptionalType) {
            Tree.OptionalType ot = (Tree.OptionalType) node;
            if (ot.getDefiniteType() instanceof Tree.SimpleType) {
                Tree.SimpleType st = (Tree.SimpleType) ot.getDefiniteType();
                dec = st.getDeclarationModel();
            }
        }
        else if (node instanceof Tree.IterableType) {
            Tree.IterableType it = (Tree.IterableType) node;
            if (it.getElementType() instanceof Tree.SimpleType) {
                Tree.SimpleType st = (Tree.SimpleType) it.getElementType();
                dec = st.getDeclarationModel();
            }
        }
        else if (node instanceof Tree.SequenceType) {
            Tree.SequenceType qt = (Tree.SequenceType) node;
            if (qt.getElementType() instanceof Tree.SimpleType) {
                Tree.SimpleType st = (Tree.SimpleType) qt.getElementType();
                dec = st.getDeclarationModel();
            }
        }
        else if (node instanceof Tree.ImportMemberOrType) {
            Tree.ImportMemberOrType imt = (Tree.ImportMemberOrType) node;
            dec = imt.getDeclarationModel();
        }
        else if (node instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td = ((Tree.TypedDeclaration) node);
            if (td.getDeclarationModel() != null) {
                ProducedType pt = td.getDeclarationModel().getType();
                dec = pt.getDeclaration();
                typeArgumentList = pt.getTypeArgumentList();
            }
        }
        else if (node instanceof Tree.Parameter) {
            Tree.Parameter parameter = ((Tree.Parameter) node);
            if (parameter.getParameterModel()!=null && 
                    parameter.getParameterModel().getType()!=null) {
                ProducedType pt = parameter.getParameterModel().getType();
                dec = pt.getDeclaration();
                typeArgumentList = pt.getTypeArgumentList();
            }
        }
        addMakeSharedProposal(proposals, project, dec);
        if (typeArgumentList != null) {
            for (ProducedType typeArgument : typeArgumentList) {
                addMakeSharedProposal(proposals, project, 
                        typeArgument.getDeclaration());
            }
        }
    }
    
    static void addMakeSharedProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Declaration dec) {
        if (dec!=null) {
            if (dec instanceof UnionType) {
                List<ProducedType> caseTypes = 
                        ((UnionType) dec).getCaseTypes();
                for (ProducedType caseType: caseTypes) {
                        addMakeSharedProposal(proposals, project, 
                                caseType.getDeclaration());
                    for (ProducedType typeArgument: 
                            caseType.getTypeArgumentList()) {
                        addMakeSharedProposal(proposals, project, 
                                typeArgument.getDeclaration());
                    }
                }
            }
            else if (dec instanceof IntersectionType) {
                List<ProducedType> satisfiedTypes = 
                        ((IntersectionType) dec).getSatisfiedTypes();
                for (ProducedType satisfiedType: satisfiedTypes) {
                    addMakeSharedProposal(proposals, project,
                            satisfiedType.getDeclaration());
                    for (ProducedType typeArgument: 
                            satisfiedType.getTypeArgumentList()) {
                        addMakeSharedProposal(proposals, project,
                                typeArgument.getDeclaration());
                    }
                }
            }
            else if (dec instanceof TypedDeclaration || 
                       dec instanceof ClassOrInterface || 
                       dec instanceof TypeAlias) {
                if (!dec.isShared()) {
                    addAddAnnotationProposal(null, 
                    		"shared", "Make Shared", 
                            dec, proposals, project);
                }
            }
        }
    }
    
    static void addMakeSharedDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        if (node instanceof Tree.Declaration) {
            Declaration d = ((Tree.Declaration) node).getDeclarationModel();
            addAddAnnotationProposal(node, 
            		"shared", "Make Shared",  
                    d, proposals, project);
        }
    }
    
}