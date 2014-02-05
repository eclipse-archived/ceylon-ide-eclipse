package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.quickfix.Util.getRootNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
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
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

class AddAnnotionProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    final Declaration dec;
    final String annotation;
    
    AddAnnotionProposal(Declaration dec, String annotation,
            int offset, IFile file, TextFileChange change) {
        super("Make '" + dec.getName() + "' " + annotation +
            (dec.getContainer() instanceof TypeDeclaration ?
                    " in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'" : ""), 
                    change);
        this.offset=offset;
        this.file=file;
        this.dec = dec;
        this.annotation = annotation;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
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

    private static void addAddAnnotationProposal(Node node, String annotation, String desc,
            Declaration dec, Collection<ICompletionProposal> proposals, IProject project) {
        if (dec!=null && dec.getName()!=null && !(node instanceof Tree.MissingDeclaration)) {
            for (PhasedUnit unit: getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationNodeVisitor fdv = new FindDeclarationNodeVisitor(dec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    if (decNode!=null) {
                        addAddAnnotationProposal(annotation, desc, dec,
                                proposals, unit, decNode);
                    }
                    break;
                }
            }
        }
    }

    private static void addAddAnnotationProposal(String annotation, String desc, 
            Declaration dec, Collection<ICompletionProposal> proposals, 
            PhasedUnit unit, Tree.Declaration decNode) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange(desc, file);
        change.setEdit(new MultiTextEdit());
        InsertEdit insertEdit = createInsertAnnotationEdit(annotation, 
        		decNode, getDocument(change));
        change.addEdit(insertEdit);
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
        AddAnnotionProposal p = new AddAnnotionProposal(dec, annotation, 
        		insertEdit.getOffset(), file, change);
        if (!proposals.contains(p)) {
            proposals.add(p);
        }
    }

    public static InsertEdit createInsertAnnotationEdit(String newAnnotation, 
    		Node node, IDocument doc) {
        String newAnnotationName = getAnnotationWithoutParam(newAnnotation);

        Tree.Annotation prevAnnotation = null;
        Tree.Annotation nextAnnotation = null;
        Tree.AnnotationList annotationList = getAnnotationList(node);
        if (annotationList != null) {
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
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
        } else {
            if (node instanceof Tree.AnyAttribute || node instanceof Tree.AnyMethod ) {
                nextNodeStartIndex = ((Tree.TypedDeclaration) node).getType().getStartIndex();
            } else if (node instanceof Tree.ObjectDefinition ) {
                nextNodeStartIndex = ((CommonToken) node.getMainToken()).getStartIndex();
            } else if (node instanceof Tree.ClassOrInterface) {
                nextNodeStartIndex = ((CommonToken) node.getMainToken()).getStartIndex();
            } else {
                nextNodeStartIndex = node.getStartIndex();
            }
        }

        int newAnnotationOffset;
        StringBuilder newAnnotationText = new StringBuilder();

        if (isAnnotationOnSeparateLine(newAnnotationName) && !(node instanceof Tree.Parameter)) {
            if (prevAnnotation != null && isAnnotationOnSeparateLine(getAnnotationIdentifier(prevAnnotation))) {
                newAnnotationOffset = prevAnnotation.getStopIndex() + 1;
                newAnnotationText.append(System.getProperty("line.separator"));
                newAnnotationText.append(getIndent(node, doc));
                newAnnotationText.append(newAnnotation);
            } else {
                newAnnotationOffset = nextNodeStartIndex;
                newAnnotationText.append(newAnnotation);
                newAnnotationText.append(System.getProperty("line.separator"));
                newAnnotationText.append(getIndent(node, doc));
            }
        } else {
            newAnnotationOffset = nextNodeStartIndex;
            newAnnotationText.append(newAnnotation);
            newAnnotationText.append(" ");
        }

        return new InsertEdit(newAnnotationOffset, newAnnotationText.toString());
    }

    public static Tree.AnnotationList getAnnotationList(Node node) {
    	Tree.AnnotationList annotationList = null;
        if (node instanceof Tree.Declaration) {
            annotationList = ((Tree.Declaration) node).getAnnotationList();
        } else if (node instanceof Tree.ModuleDescriptor) {
            annotationList = ((Tree.ModuleDescriptor) node).getAnnotationList();
        } else if (node instanceof Tree.PackageDescriptor) {
            annotationList = ((Tree.PackageDescriptor) node).getAnnotationList();
        } else if (node instanceof Tree.Assertion) {
            annotationList = ((Tree.Assertion) node).getAnnotationList();
        }
        return annotationList;
    }

    public static String getAnnotationIdentifier(Tree.Annotation annotation) {
        String annotationName = null;
        if (annotation != null) {
            if (annotation.getPrimary() instanceof Tree.BaseMemberExpression) {
                annotationName = ((Tree.BaseMemberExpression) annotation.getPrimary()).getIdentifier().getText();
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

    private static IDocument getDocument(TextFileChange change) {
        try {
            return change.getCurrentDocument(null);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static final List<String> ANNOTATIONS_ORDER = 
    		Arrays.asList("doc", "throws", "see", "tagged", "shared", 
    				"abstract", "actual", "formal", "default", "variable");
    private static final List<String> ANNOTATIONS_ON_SEPARATE_LINE = 
    		Arrays.asList("doc", "throws", "see", "tagged");
    

    static void addMakeActualProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        boolean shared = decNode.getDeclarationModel().isShared();
        addAddAnnotationProposal(node, shared ? "actual" : "shared actual", 
                shared ? "Make Actual" : "Make Shared Actual",
                decNode.getDeclarationModel(), proposals, project);
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
        	List<Declaration> rds = ((ClassOrInterface)d.getContainer()).getInheritedMembers(d.getName());
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
        		addAddAnnotationProposal(node, "default", "Make Default", rd, 
        				proposals, project);
        	}
        }
    }

    static void addMakeDefaultDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        Declaration d = decNode.getDeclarationModel();
        addAddAnnotationProposal(node, "default", "Make Default", d, 
                proposals, project);
    }

    static void addMakeFormalProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        boolean shared = decNode.getDeclarationModel().isShared();
        addAddAnnotationProposal(node, shared ? "formal" : "shared formal",
                shared ? "Make Formal" : "Make Shared Formal",
                decNode.getDeclarationModel(), proposals, project);
    }

    static void addMakeAbstractProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            dec = (Declaration) ((Tree.Declaration) node).getDeclarationModel();
        }
        else {
            dec = (Declaration) node.getScope();
        }
        addAddAnnotationProposal(node, "abstract", "Make Abstract", dec, 
                proposals, project);
    }

    static void addMakeContainerAbstractProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            Scope container = ((Tree.Declaration) node).getDeclarationModel().getContainer();
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
        addAddAnnotationProposal(node, "abstract", "Make Abstract", dec, 
                proposals, project);
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
            Declaration dec = ((Tree.MemberOrTypeExpression) term).getDeclaration();
            if (dec instanceof Value) {
            	if (((Value) dec).getOriginalDeclaration()==null) {
            		addAddAnnotationProposal(node, "variable", "Make Variable", 
            				dec, proposals, project);
            	}
            }
        }
    }
    
    static void addMakeVariableDecProposal(Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IProject project, Node node) {
        final Tree.SpecifierOrInitializerExpression sie = (Tree.SpecifierOrInitializerExpression) node;
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
        addAddAnnotationProposal(node, "variable", "Make Variable", v.dec, 
                proposals, project);
    }
    
    static void addMakeSharedPropsalForSupertypes(Collection<ICompletionProposal> proposals, IProject project, Node node) {
        if (node instanceof Tree.ClassOrInterface) {
            Tree.ClassOrInterface c = (Tree.ClassOrInterface) node;

            ProducedType extendedType = c.getDeclarationModel().getExtendedType();
            if( extendedType != null ) {
                addMakeSharedProposal(extendedType.getDeclaration(), proposals, project);
                for (ProducedType typeArgument : extendedType.getTypeArgumentList()) {
                    addMakeSharedProposal(typeArgument.getDeclaration(), proposals, project);
                }
            }
            
            List<ProducedType> satisfiedTypes = c.getDeclarationModel().getSatisfiedTypes();
            if( satisfiedTypes != null ) {
                for (ProducedType satisfiedType : satisfiedTypes) {
                    addMakeSharedProposal(satisfiedType.getDeclaration(), proposals, project);
                    for (ProducedType typeArgument : satisfiedType.getTypeArgumentList()) {
                        addMakeSharedProposal(typeArgument.getDeclaration(), proposals, project);
                    }
                }
            }
        }
    }
    
    static void addMakeSharedProposal(Collection<ICompletionProposal> proposals, IProject project, Node node) {
        Declaration dec = null;
        List<ProducedType> typeArgumentList = null;
        if (node instanceof Tree.StaticMemberOrTypeExpression) {
            Tree.StaticMemberOrTypeExpression qmte = (Tree.StaticMemberOrTypeExpression) node;
            dec = qmte.getDeclaration();
        }
        else if (node instanceof Tree.SimpleType) {
            Tree.SimpleType qmte = (Tree.SimpleType) node;
            dec = qmte.getDeclarationModel();
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
            if (parameter.getParameterModel() != null && parameter.getParameterModel().getType() != null) {
                ProducedType pt = parameter.getParameterModel().getType();
                dec = pt.getDeclaration();
                typeArgumentList = pt.getTypeArgumentList();
            }
        }
        addMakeSharedProposal(dec, proposals, project);
        if (typeArgumentList != null) {
            for (ProducedType typeArgument : typeArgumentList) {
                addMakeSharedProposal(typeArgument.getDeclaration(), proposals, project);
            }
        }
    }
    
    static void addMakeSharedProposal(Declaration dec, Collection<ICompletionProposal> proposals, IProject project) {
        if (dec != null) {
            if (dec instanceof UnionType) {
                List<ProducedType> caseTypes = ((UnionType) dec).getCaseTypes();
                for (ProducedType caseType : caseTypes) {
                    addMakeSharedProposal(caseType.getDeclaration(), proposals, project);
                    for (ProducedType typeArgument : caseType.getTypeArgumentList()) {
                        addMakeSharedProposal(typeArgument.getDeclaration(), proposals, project);
                    }
                }
            } else if (dec instanceof IntersectionType) {
                List<ProducedType> satisfiedTypes = ((IntersectionType) dec).getSatisfiedTypes();
                for (ProducedType satisfiedType : satisfiedTypes) {
                    addMakeSharedProposal(satisfiedType.getDeclaration(), proposals, project);
                    for (ProducedType typeArgument : satisfiedType.getTypeArgumentList()) {
                        addMakeSharedProposal(typeArgument.getDeclaration(), proposals, project);
                    }
                }
            } else if (dec instanceof TypedDeclaration || 
                       dec instanceof ClassOrInterface || 
                       dec instanceof TypeAlias) {
                if (!dec.isShared()) {
                    addAddAnnotationProposal(null, "shared", "Make Shared", dec, proposals, project);
                }
            }
        }
    }
    
    static void addMakeSharedDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
    	if (node instanceof Tree.Declaration) {
    		addAddAnnotationProposal(node, "shared", "Make Shared",  
    				((Tree.Declaration) node).getDeclarationModel(), 
    				proposals, project);
    	}
    }
    
}