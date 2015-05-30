package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.asIntersectionTypeString;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.IntersectionType;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraint;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraintList;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

/**
 * Add generic type constraints proposal for following code:
 * <pre>
 * interface Foo&lt;T&gt; given T satisfies Comparable&lt;T&gt; {}
 * 
 * class Bar&lt;T&gt;() satisfies Foo&lt;T&gt; {}
 * 
 * ------
 * 
 * void foo&lt;T&gt;(T t) {
 *     Bar b = t;
 * }
 * 
 * ------
 * 
 * void foo&lt;T&gt;(T t) {
 *     Entry&lt;Integer, T&gt; e;
 * }
 * </pre>
 */
public class AddSatisfiesProposal extends CorrectionProposal {

    public static void addSatisfiesProposals(Tree.CompilationUnit cu, 
            Node node, Collection<ICompletionProposal> proposals, 
            IProject project) {
        node = determineNode(node);
        if (node == null) {
            return;
        }

        TypeDeclaration typeDec = determineTypeDeclaration(node);
        if (typeDec == null) {
            return;
        }
        boolean isTypeParam = typeDec instanceof TypeParameter;

        List<Type> missingSatisfiedTypes = 
                determineMissingSatisfiedTypes(cu, node, typeDec);
        if (!isTypeParam) {
            for (Iterator<Type> it = 
                    missingSatisfiedTypes.iterator();
                    it.hasNext();) {
                Type pt = it.next();
                if (!(pt.getDeclaration() instanceof Interface)) {
                    it.remove();
                }
                //TODO: add extends clause for if the type is a Class
            }
        }
        if (missingSatisfiedTypes.isEmpty()) {
            return;
        }

        String changeText = 
                asIntersectionTypeString(missingSatisfiedTypes);

        for (PhasedUnit unit: getUnits(project)) {
            if (!isTypeParam || 
                    typeDec.getUnit().equals(unit.getUnit())) {
                Node declaration = 
                        determineContainer(unit.getCompilationUnit(), typeDec);
                if (declaration==null) {
                    continue;
                }
                createProposals(proposals, typeDec, isTypeParam, 
                        changeText, unit, declaration);
                break;
            }
        }        
    }

    private static void createProposals(Collection<ICompletionProposal> proposals, 
            TypeDeclaration typeDec, boolean isTypeParam, String changeText, 
            PhasedUnit unit, Node declaration) {
        if (isTypeParam) {
            if (declaration instanceof Tree.ClassDefinition) {
                Tree.ClassDefinition classDefinition = 
                        (Tree.ClassDefinition) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        unit, proposals, 
                        classDefinition.getTypeConstraintList(), 
                        classDefinition.getClassBody().getStartIndex());
            }
            else if (declaration instanceof Tree.InterfaceDefinition) {
                Tree.InterfaceDefinition interfaceDefinition = 
                        (Tree.InterfaceDefinition) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        unit, proposals, 
                        interfaceDefinition.getTypeConstraintList(), 
                        interfaceDefinition.getInterfaceBody().getStartIndex());
            }
            else if (declaration instanceof Tree.MethodDefinition) {
                Tree.MethodDefinition methodDefinition = 
                        (Tree.MethodDefinition)declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        unit, proposals, 
                        methodDefinition.getTypeConstraintList(), 
                        methodDefinition.getBlock().getStartIndex());
            }
            else if (declaration instanceof Tree.ClassDeclaration) {
                Tree.ClassDeclaration classDefinition = 
                        (Tree.ClassDeclaration) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        unit, proposals, 
                        classDefinition.getTypeConstraintList(), 
                        classDefinition.getClassSpecifier().getStartIndex());
            }
            else if (declaration instanceof Tree.InterfaceDefinition) {
                Tree.InterfaceDeclaration interfaceDefinition = 
                        (Tree.InterfaceDeclaration) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        unit, proposals, 
                        interfaceDefinition.getTypeConstraintList(), 
                        interfaceDefinition.getTypeSpecifier().getStartIndex());
            }
            else if (declaration instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration methodDefinition = 
                        (Tree.MethodDeclaration)declaration;
                addConstraintSatisfiesProposals(typeDec, changeText,
                        unit, proposals, 
                        methodDefinition.getTypeConstraintList(), 
                        methodDefinition.getSpecifierExpression().getStartIndex());
            }
        }
        else {
            if (declaration instanceof Tree.ClassDefinition) {
                Tree.ClassDefinition classDefinition = 
                        (Tree.ClassDefinition) declaration;
                addSatisfiesProposals(typeDec, changeText, unit, proposals, 
                        classDefinition.getSatisfiedTypes(), 
                        classDefinition.getTypeConstraintList()==null ?
                                classDefinition.getClassBody().getStartIndex() :
                                classDefinition.getTypeConstraintList().getStartIndex());
            }
            else if (declaration instanceof Tree.ObjectDefinition) {
                Tree.ObjectDefinition objectDefinition = 
                        (Tree.ObjectDefinition) declaration;
                addSatisfiesProposals(typeDec, changeText, unit, proposals, 
                        objectDefinition.getSatisfiedTypes(), 
                        objectDefinition.getClassBody().getStartIndex());
            }
            else if (declaration instanceof Tree.InterfaceDefinition) {
                Tree.InterfaceDefinition interfaceDefinition = 
                        (Tree.InterfaceDefinition) declaration;
                addSatisfiesProposals(typeDec, changeText, unit, proposals, 
                        interfaceDefinition.getSatisfiedTypes(), 
                        interfaceDefinition.getTypeConstraintList()==null ?
                                interfaceDefinition.getInterfaceBody().getStartIndex() :
                                interfaceDefinition.getTypeConstraintList().getStartIndex());
            }
        }
    }

    private static void addConstraintSatisfiesProposals(TypeDeclaration typeParam, 
            String missingSatisfiedType, PhasedUnit unit, 
            Collection<ICompletionProposal> proposals, 
            TypeConstraintList typeConstraints, 
            Integer typeContainerBodyStartIndex) {
        String changeText = null;
        Integer changeIndex = null;
    
        if (typeConstraints != null) {
            for (TypeConstraint typeConstraint: 
                    typeConstraints.getTypeConstraints()) {
                if (typeConstraint.getDeclarationModel().equals(typeParam)) {
                    changeText = " & " + missingSatisfiedType;
                    changeIndex = typeConstraint.getStopIndex() + 1;
                    break;
                }
            }
        }
        if (changeText == null) {
            changeText = "given "+ typeParam.getName() + 
                    " satisfies " + missingSatisfiedType + " ";
            changeIndex = typeContainerBodyStartIndex;
        }
        if (changeText != null) {
            IFile file = CeylonBuilder.getFile(unit);
            TextFileChange change = 
                    new TextFileChange("Add generic type constraint", file);
            change.setEdit(new InsertEdit(changeIndex, changeText));
            String desc = "Add generic type constraint '" + typeParam.getName() + 
                    " satisfies " + missingSatisfiedType + "'";
            AddSatisfiesProposal p = 
                    new AddSatisfiesProposal(typeParam, desc, 
                            missingSatisfiedType, change);
            if ( !proposals.contains(p)) {
                proposals.add(p);
            }                               
        }
    }

    private static void addSatisfiesProposals(TypeDeclaration typeParam, 
            String missingSatisfiedType, PhasedUnit unit, 
            Collection<ICompletionProposal> proposals, 
            Tree.SatisfiedTypes typeConstraints, 
            Integer typeContainerBodyStartIndex) {
        String changeText = null;
        Integer changeIndex = null;
    
        if (typeConstraints != null) {
            changeText = " & " + missingSatisfiedType;
            changeIndex = typeConstraints.getStopIndex() + 1;
        }
        else if (changeText == null) {
            changeText = "satisfies " + missingSatisfiedType + " ";
            changeIndex = typeContainerBodyStartIndex;
        }
        if (changeText != null) {
            IFile file = CeylonBuilder.getFile(unit);
            TextFileChange change = 
                    new TextFileChange("Add satisfies type", file);
            change.setEdit(new InsertEdit(changeIndex, changeText));
            String desc = "Add inherited interface '" + typeParam.getName() + 
                    " satisfies " + missingSatisfiedType + "'";
            AddSatisfiesProposal p = 
                    new AddSatisfiesProposal(typeParam, desc, 
                            missingSatisfiedType, change);
            if (!proposals.contains(p)) {
                proposals.add(p);
            }                               
        }
    }

    private static Node determineNode(Node node) {
        if (node instanceof Tree.SpecifierExpression) {
            node = ((Tree.SpecifierExpression) node).getExpression();
        }
        if (node instanceof Tree.Expression) {
            node = ((Tree.Expression) node).getTerm();
        }
        return node;
    }

    private static TypeDeclaration determineTypeDeclaration(Node node) {
        TypeDeclaration typeDec = null;
        if (node instanceof Tree.ClassOrInterface || 
            node instanceof Tree.TypeParameterDeclaration) {
            Declaration declaration = 
                    ((Tree.Declaration) node).getDeclarationModel();
            if (declaration instanceof ClassOrInterface) {
                typeDec = (TypeDeclaration) declaration;
            }
        }
        else if (node instanceof Tree.ObjectDefinition) {
            Value val = 
                    ((Tree.ObjectDefinition) node).getDeclarationModel();
            return val.getType().getDeclaration();
        }
        else if (node instanceof Tree.BaseType) {
            TypeDeclaration baseTypeDecl = 
                    ((Tree.BaseType) node).getDeclarationModel();
            if (baseTypeDecl instanceof TypeDeclaration) {
                typeDec = baseTypeDecl;
            }
        }
        else if (node instanceof Tree.Term) {
//            Type type = node.getUnit()
//                    .denotableType(((Tree.Term)node).getTypeModel());
            Type type = ((Tree.Term) node).getTypeModel();
            if (type != null) {
                typeDec = type.getDeclaration();
            }
        }
        return typeDec;
    }

    private static Node determineContainer(CompilationUnit cu, final TypeDeclaration typeDec) {
        FindDeclarationNodeVisitor fdv = new FindDeclarationNodeVisitor(typeDec) {
            @Override
            public void visit(Tree.ObjectDefinition that) {
                if (that.getDeclarationModel().getType().getDeclaration().equals(typeDec)) {
                    declarationNode = that;
                }
                super.visit(that);
            }
        };
        fdv.visit(cu);
        Tree.Declaration dec = 
                (Tree.Declaration) fdv.getDeclarationNode();
        if (dec != null) {
            FindContainerVisitor fcv = 
                    new FindContainerVisitor(dec);
            fcv.visit(cu);
            return fcv.getStatementOrArgument();
        }
        return null;
    }

    private static List<Type> determineMissingSatisfiedTypes(CompilationUnit cu, 
            Node node, TypeDeclaration typeDec) {
        List<Type> missingSatisfiedTypes = 
                new ArrayList<Type>();
        if (node instanceof Tree.Term) {
            FindInvocationVisitor fav = new FindInvocationVisitor(node);
            fav.visit(cu);
            if (fav.parameter != null) {
                Type type = fav.parameter.getType();
                if (type!=null && type.getDeclaration()!=null) {
                    if (type.isClassOrInterface()) {
                        missingSatisfiedTypes.add(type);
                    }
                    else if (type.isIntersection()) {
                        for (Type it: type.getSatisfiedTypes()) {
                            if (!typeDec.inherits(it.getDeclaration())) {
                                missingSatisfiedTypes.add(it);
                            }
                        }
                    }
                }
            }
        }
        else {
            List<TypeParameter> stTypeParams = 
                    determineSatisfiedTypesTypeParams(cu, node, typeDec);
            if (!stTypeParams.isEmpty()) {
                Type typeParamType = typeDec.getType();
                Map<TypeParameter, Type> substitutions = 
                        new HashMap<TypeParameter, Type>();
                for (TypeParameter stTypeParam : stTypeParams) {
                    substitutions.put(stTypeParam, typeParamType);
                }
    
                for (TypeParameter stTypeParam : stTypeParams) {
                    for (Type stTypeParamSatisfiedType: 
                            stTypeParam.getSatisfiedTypes()) {
                        stTypeParamSatisfiedType = 
                                stTypeParamSatisfiedType.substitute(substitutions, null);
    
                        boolean isMissing = true;
    
                        for (Type typeParamSatisfiedType: 
                                typeDec.getSatisfiedTypes()) {
                            if (stTypeParamSatisfiedType.isSupertypeOf(typeParamSatisfiedType)) {
                                isMissing = false;
                                break;
                            }
                        }
    
                        if (isMissing) {
                            for(Type missingSatisfiedType: 
                                    missingSatisfiedTypes) {
                                if( missingSatisfiedType.isExactly(stTypeParamSatisfiedType) ) {
                                    isMissing = false;
                                    break;
                                }
                            }
                        }
    
                        if (isMissing) {
                            missingSatisfiedTypes.add(stTypeParamSatisfiedType);
                        }
                    }
                }
            }
    
        }
    
        return missingSatisfiedTypes;
    }

    private static List<TypeParameter> determineSatisfiedTypesTypeParams(
            Tree.CompilationUnit cu, Node typeParamNode, 
            final TypeDeclaration typeDec) {
        final List<TypeParameter> stTypeParams = new ArrayList<TypeParameter>();
        
        FindContainerVisitor fcv = new FindContainerVisitor(typeParamNode);
        fcv.visit(cu);
        Tree.StatementOrArgument soa = fcv.getStatementOrArgument();
        soa.visit(new Visitor() {
            @Override
            public void visit(Tree.SimpleType that) {
                super.visit(that);
                determineSatisfiedTypesTypeParams(typeDec, that, stTypeParams);
            }
            @Override
            public void visit(Tree.StaticMemberOrTypeExpression that) {
                super.visit(that);
                determineSatisfiedTypesTypeParams(typeDec, that, stTypeParams);
            }
        });
//        if (soa instanceof Tree.ClassOrInterface) {
//            Tree.ClassOrInterface coi = (Tree.ClassOrInterface) soa;
//            if (coi.getSatisfiedTypes() != null) {
//                for (Tree.StaticType st: coi.getSatisfiedTypes().getTypes()) {
//                    // FIXME: gavin this needs checking
//                    if (st instanceof Tree.SimpleType) {
//                    }
//                }
//            }
//        }
//        else if (soa instanceof Tree.AttributeDeclaration) {
//            Tree.AttributeDeclaration ad = (Tree.AttributeDeclaration) soa;
//            Tree.Type at = ad.getType();
//            if (at instanceof Tree.SimpleType) {
//                determineSatisfiedTypesTypeParams(typeDec, 
//                        (Tree.SimpleType) at, stTypeParams);
//            }
//        }

        return stTypeParams;
    }
    
    private static void determineSatisfiedTypesTypeParams(TypeDeclaration typeParam, 
            Tree.SimpleType st, List<TypeParameter> stTypeParams) {
        Tree.TypeArgumentList args = st.getTypeArgumentList();
        if (args != null) {
            List<Tree.Type> stTypeArguments = 
                    args.getTypes();
            for (int i=0; i<stTypeArguments.size(); i++) {
                Type stTypeArgument = 
                        stTypeArguments.get(i).getTypeModel();
                if (stTypeArgument!=null && 
                        typeParam.equals(stTypeArgument.getDeclaration())) {
                    TypeDeclaration stDecl = st.getDeclarationModel();
                    if (stDecl!=null) {
                        if (stDecl.getTypeParameters()!=null && 
                                stDecl.getTypeParameters().size()>i) {
                            stTypeParams.add(stDecl.getTypeParameters().get(i));
                        }
                    }                            
                }
            }                    
        }
    }

    private static void determineSatisfiedTypesTypeParams(TypeDeclaration typeParam, 
            Tree.StaticMemberOrTypeExpression st, List<TypeParameter> stTypeParams) {
        Tree.TypeArguments args = st.getTypeArguments();
        if (args instanceof Tree.TypeArgumentList) {
            List<Tree.Type> stTypeArguments = ((Tree.TypeArgumentList) args).getTypes();
            for (int i=0; i<stTypeArguments.size(); i++) {
                Type stTypeArgument = 
                        stTypeArguments.get(i).getTypeModel();
                if (stTypeArgument!=null && 
                        typeParam.equals(stTypeArgument.getDeclaration())) {
                    Declaration stDecl = st.getDeclaration();
                    if (stDecl instanceof TypeDeclaration) {
                        TypeDeclaration td = (TypeDeclaration)stDecl;
                        if (td.getTypeParameters()!=null && 
                                td.getTypeParameters().size()>i) {
                            stTypeParams.add(td.getTypeParameters().get(i));
                        }
                    }                            
                }
            }                    
        }
    }

    private final TypeDeclaration typeParam;
    private final String missingSatisfiedTypeText;

    private AddSatisfiesProposal(TypeDeclaration typeParam, 
            String description, String missingSatisfiedTypeText, 
            TextFileChange change) {
        super(description, change, 
                new Region(change.getEdit().getOffset(), 0));
        this.typeParam = typeParam;
        this.missingSatisfiedTypeText = missingSatisfiedTypeText;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddSatisfiesProposal) {
            AddSatisfiesProposal that = (AddSatisfiesProposal) obj;
            return that.typeParam.equals(typeParam) && 
                    that.missingSatisfiedTypeText
                            .equals(missingSatisfiedTypeText);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return typeParam.hashCode() + 
                missingSatisfiedTypeText.hashCode();
    }

}