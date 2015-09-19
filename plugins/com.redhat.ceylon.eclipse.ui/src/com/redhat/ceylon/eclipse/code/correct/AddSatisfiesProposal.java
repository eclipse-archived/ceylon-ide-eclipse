package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.asIntersectionTypeString;

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

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraint;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraintList;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.core.model.ModifiableSourceFile;
import com.redhat.ceylon.eclipse.core.typechecker.ModifiablePhasedUnit;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;

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

    public static void addSatisfiesProposals(
            Tree.CompilationUnit rootNode, Node node, 
            Collection<ICompletionProposal> proposals, 
            IProject project) {
        node = determineNode(node);
        if (node == null) {
            return;
        }

        TypeDeclaration typeDec = 
                determineTypeDeclaration(node);
        if (typeDec == null) {
            return;
        }
        boolean isTypeParam = 
                typeDec instanceof TypeParameter;

        List<Type> missingSatisfiedTypes = 
                determineMissingSatisfiedTypes(rootNode, 
                        node, typeDec);
        if (!isTypeParam) {
            for (Iterator<Type> it = 
                    missingSatisfiedTypes.iterator();
                    it.hasNext();) {
                if (!it.next().isInterface()) {
                    it.remove();
                }
                //TODO: add extends clause if the type is a 
                //      Class which extends Basic or Object
            }
        }
        if (missingSatisfiedTypes.isEmpty()) {
            return;
        }

        String changeText = 
                asIntersectionTypeString(missingSatisfiedTypes);

        Unit unit = typeDec.getUnit();
        if (unit instanceof ModifiableSourceFile) {
            ModifiableSourceFile msf = 
                    (ModifiableSourceFile) unit;
            ModifiablePhasedUnit phasedUnit = 
                    msf.getPhasedUnit();
            Tree.CompilationUnit decRootNode = 
                    phasedUnit.getCompilationUnit();
            Node declaration = 
                    determineContainer(decRootNode, typeDec);
            if (declaration!=null) {
                IFile file = phasedUnit.getResourceFile();
                if (file != null) {
                    createProposals(proposals, typeDec, 
                            isTypeParam, changeText, file, 
                            declaration,
                            node.getUnit().equals(unit));
                }
            }
        }
    }

    private static void createProposals(
            Collection<ICompletionProposal> proposals, 
            TypeDeclaration typeDec, boolean isTypeParam, 
            String changeText, IFile file, Node declaration, 
            boolean sameFile) {
        if (isTypeParam) {
            if (declaration instanceof Tree.ClassDefinition) {
                Tree.ClassDefinition classDefinition = 
                        (Tree.ClassDefinition) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        file, proposals, 
                        classDefinition.getTypeConstraintList(), 
                        classDefinition.getClassBody().getStartIndex(), 
                        sameFile);
            }
            else if (declaration instanceof Tree.InterfaceDefinition) {
                Tree.InterfaceDefinition interfaceDefinition = 
                        (Tree.InterfaceDefinition) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        file, proposals, 
                        interfaceDefinition.getTypeConstraintList(), 
                        interfaceDefinition.getInterfaceBody().getStartIndex(), 
                        sameFile);
            }
            else if (declaration instanceof Tree.MethodDefinition) {
                Tree.MethodDefinition methodDefinition = 
                        (Tree.MethodDefinition) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        file, proposals, 
                        methodDefinition.getTypeConstraintList(), 
                        methodDefinition.getBlock().getStartIndex(), 
                        sameFile);
            }
            else if (declaration instanceof Tree.ClassDeclaration) {
                Tree.ClassDeclaration classDefinition = 
                        (Tree.ClassDeclaration) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        file, proposals, 
                        classDefinition.getTypeConstraintList(), 
                        classDefinition.getClassSpecifier().getStartIndex(), 
                        sameFile);
            }
            else if (declaration instanceof Tree.InterfaceDefinition) {
                Tree.InterfaceDeclaration interfaceDefinition = 
                        (Tree.InterfaceDeclaration) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText, 
                        file, proposals, 
                        interfaceDefinition.getTypeConstraintList(), 
                        interfaceDefinition.getTypeSpecifier().getStartIndex(), 
                        sameFile);
            }
            else if (declaration instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration methodDefinition = 
                        (Tree.MethodDeclaration) declaration;
                addConstraintSatisfiesProposals(typeDec, changeText,
                        file, proposals, 
                        methodDefinition.getTypeConstraintList(), 
                        methodDefinition.getSpecifierExpression().getStartIndex(), 
                        sameFile);
            }
        }
        else {
            if (declaration instanceof Tree.ClassDefinition) {
                Tree.ClassDefinition classDefinition = 
                        (Tree.ClassDefinition) declaration;
                addSatisfiesProposals(typeDec, changeText, file, proposals, 
                        classDefinition.getSatisfiedTypes(), 
                        classDefinition.getTypeConstraintList()==null ?
                                classDefinition.getClassBody().getStartIndex() :
                                classDefinition.getTypeConstraintList().getStartIndex(), 
                                sameFile);
            }
            else if (declaration instanceof Tree.ObjectDefinition) {
                Tree.ObjectDefinition objectDefinition = 
                        (Tree.ObjectDefinition) declaration;
                addSatisfiesProposals(typeDec, changeText, file, proposals, 
                        objectDefinition.getSatisfiedTypes(), 
                        objectDefinition.getClassBody().getStartIndex(), 
                        sameFile);
            }
            else if (declaration instanceof Tree.InterfaceDefinition) {
                Tree.InterfaceDefinition interfaceDefinition = 
                        (Tree.InterfaceDefinition) declaration;
                addSatisfiesProposals(typeDec, changeText, file, proposals, 
                        interfaceDefinition.getSatisfiedTypes(), 
                        interfaceDefinition.getTypeConstraintList()==null ?
                                interfaceDefinition.getInterfaceBody().getStartIndex() :
                                interfaceDefinition.getTypeConstraintList().getStartIndex(), 
                                sameFile);
            }
        }
    }

    private static void addConstraintSatisfiesProposals(
            TypeDeclaration typeParam, 
            String missingSatisfiedType, IFile file, 
            Collection<ICompletionProposal> proposals, 
            TypeConstraintList typeConstraints, 
            Integer typeContainerBodyStartIndex,
            boolean sameFile) {
        String changeText = null;
        Integer changeIndex = null;
    
        if (typeConstraints != null) {
            for (TypeConstraint typeConstraint: 
                    typeConstraints.getTypeConstraints()) {
                if (typeConstraint.getDeclarationModel()
                        .equals(typeParam)) {
                    changeText = " & " + missingSatisfiedType;
                    changeIndex = typeConstraint.getEndIndex();
                    break;
                }
            }
        }
        if (changeText == null) {
            changeText = 
                    "given "+ typeParam.getName() + 
                    " satisfies " + missingSatisfiedType + " ";
            changeIndex = typeContainerBodyStartIndex;
        }
        if (changeText != null) {
            TextFileChange change = 
                    new TextFileChange(
                            "Add Type Constraint", file);
            change.setEdit(new InsertEdit(changeIndex, changeText));
            String desc = 
                    "Add generic type constraint '" + typeParam.getName() + 
                    " satisfies " + missingSatisfiedType + "'";
            Region region =
                    sameFile ? new Region(changeIndex, changeText.length()) : null;
            AddSatisfiesProposal proposal = 
                    new AddSatisfiesProposal(
                            typeParam, desc, 
                            missingSatisfiedType, 
                            change, region);
            if ( !proposals.contains(proposal)) {
                proposals.add(proposal);
            }                               
        }
    }
    
    private static void addSatisfiesProposals(
            TypeDeclaration typeParam, 
            String missingSatisfiedType, IFile file, 
            Collection<ICompletionProposal> proposals, 
            Tree.SatisfiedTypes typeConstraints, 
            Integer typeContainerBodyStartIndex,
            boolean sameFile) {
        String changeText = null;
        Integer changeIndex = null;
    
        if (typeConstraints != null) {
            changeText = " & " + missingSatisfiedType;
            changeIndex = typeConstraints.getEndIndex();
        }
        else if (changeText == null) {
            changeText = "satisfies " + missingSatisfiedType + " ";
            changeIndex = typeContainerBodyStartIndex;
        }
        if (changeText != null) {
            TextFileChange change = 
                    new TextFileChange(
                            "Add Inherited Interface", file);
            change.setEdit(new InsertEdit(changeIndex, changeText));
            String desc = 
                    "Add inherited interface '" + typeParam.getName() + 
                    " satisfies " + missingSatisfiedType + "'";
            Region region =
                    sameFile ? new Region(changeIndex, changeText.length()) : null;
            AddSatisfiesProposal proposal = 
                    new AddSatisfiesProposal(
                            typeParam, desc, 
                            missingSatisfiedType, 
                            change, region);
            if (!proposals.contains(proposal)) {
                proposals.add(proposal);
            }                               
        }
    }

    private static Node determineNode(Node node) {
        if (node instanceof Tree.SpecifierExpression) {
            Tree.SpecifierExpression specifierExpression = 
                    (Tree.SpecifierExpression) node;
            node = specifierExpression.getExpression();
        }
        if (node instanceof Tree.Expression) {
            Tree.Expression expression = 
                    (Tree.Expression) node;
            node = expression.getTerm();
        }
        return node;
    }

    private static TypeDeclaration determineTypeDeclaration(Node node) {
        TypeDeclaration typeDec = null;
        if (node instanceof Tree.ClassOrInterface || 
            node instanceof Tree.TypeParameterDeclaration) {
            Tree.Declaration d = (Tree.Declaration) node;
            Declaration declaration = 
                    d.getDeclarationModel();
            if (declaration instanceof ClassOrInterface) {
                typeDec = (TypeDeclaration) declaration;
            }
        }
        else if (node instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition od = 
                    (Tree.ObjectDefinition) node;
            Value val = od.getDeclarationModel();
            return val.getType().getDeclaration();
        }
        else if (node instanceof Tree.BaseType) {
            Tree.BaseType bt = (Tree.BaseType) node;
            TypeDeclaration baseTypeDecl = 
                    bt.getDeclarationModel();
            if (baseTypeDecl instanceof TypeDeclaration) {
                typeDec = baseTypeDecl;
            }
        }
        else if (node instanceof Tree.Term) {
            Tree.Term t = (Tree.Term) node;
            Type type = t.getTypeModel();
            if (type != null) {
                typeDec = type.getDeclaration();
            }
        }
        return typeDec;
    }

    private static Node determineContainer(
            Tree.CompilationUnit rootNode, 
            final TypeDeclaration typeDec) {
        FindDeclarationNodeVisitor fdv = 
                new FindDeclarationNodeVisitor(typeDec) {
            @Override
            public void visit(Tree.ObjectDefinition that) {
                if (that.getDeclarationModel().getType()
                        .getDeclaration().equals(typeDec)) {
                    declarationNode = that;
                }
                super.visit(that);
            }
        };
        fdv.visit(rootNode);
        Tree.Declaration dec = 
                (Tree.Declaration) fdv.getDeclarationNode();
        if (dec != null) {
            FindContainerVisitor fcv = 
                    new FindContainerVisitor(dec);
            fcv.visit(rootNode);
            return fcv.getStatementOrArgument();
        }
        return null;
    }

    private static List<Type> determineMissingSatisfiedTypes(
            Tree.CompilationUnit rootNode, Node node, 
            TypeDeclaration typeDec) {
        List<Type> missingSatisfiedTypes = 
                new ArrayList<Type>();
        if (node instanceof Tree.Term) {
            FindInvocationVisitor fav = 
                    new FindInvocationVisitor(node);
            fav.visit(rootNode);
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
                    determineSatisfiedTypesTypeParams(
                            rootNode, node, typeDec);
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
                                stTypeParamSatisfiedType.substitute(
                                        substitutions, null);
    
                        boolean isMissing = true;
    
                        for (Type typeParamSatisfiedType: 
                                typeDec.getSatisfiedTypes()) {
                            if (stTypeParamSatisfiedType.isSupertypeOf(
                                    typeParamSatisfiedType)) {
                                isMissing = false;
                                break;
                            }
                        }
    
                        if (isMissing) {
                            for(Type missingSatisfiedType: 
                                    missingSatisfiedTypes) {
                                if( missingSatisfiedType.isExactly(
                                        stTypeParamSatisfiedType) ) {
                                    isMissing = false;
                                    break;
                                }
                            }
                        }
    
                        if (isMissing) {
                            missingSatisfiedTypes.add(
                                    stTypeParamSatisfiedType);
                        }
                    }
                }
            }
    
        }
    
        return missingSatisfiedTypes;
    }

    private static List<TypeParameter> determineSatisfiedTypesTypeParams(
            Tree.CompilationUnit rootNode, Node typeParamNode, 
            final TypeDeclaration typeDec) {
        final List<TypeParameter> stTypeParams = 
                new ArrayList<TypeParameter>();
        
        FindContainerVisitor fcv = 
                new FindContainerVisitor(typeParamNode);
        fcv.visit(rootNode);
        Tree.StatementOrArgument soa = 
                fcv.getStatementOrArgument();
        soa.visit(new Visitor() {
            @Override
            public void visit(Tree.SimpleType that) {
                super.visit(that);
                determineSatisfiedTypesTypeParams(typeDec, 
                        that, stTypeParams);
            }
            @Override
            public void visit(Tree.StaticMemberOrTypeExpression that) {
                super.visit(that);
                determineSatisfiedTypesTypeParams(typeDec, 
                        that, stTypeParams);
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
    
    private static void determineSatisfiedTypesTypeParams(
            TypeDeclaration typeParam, Tree.SimpleType st, 
            List<TypeParameter> stTypeParams) {
        Tree.TypeArgumentList args = st.getTypeArgumentList();
        if (args != null) {
            List<Tree.Type> stTypeArguments = 
                    args.getTypes();
            for (int i=0; i<stTypeArguments.size(); i++) {
                Type stTypeArgument = 
                        stTypeArguments.get(i)
                            .getTypeModel();
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

    private static void determineSatisfiedTypesTypeParams(
            TypeDeclaration typeParam, 
            Tree.StaticMemberOrTypeExpression st, 
            List<TypeParameter> stTypeParams) {
        Tree.TypeArguments args = st.getTypeArguments();
        if (args instanceof Tree.TypeArgumentList) {
            Tree.TypeArgumentList tal = 
                    (Tree.TypeArgumentList) args;
            List<Tree.Type> stTypeArguments = tal.getTypes();
            for (int i=0; i<stTypeArguments.size(); i++) {
                Type stTypeArgument = 
                        stTypeArguments.get(i)
                            .getTypeModel();
                if (stTypeArgument!=null && 
                        typeParam.equals(stTypeArgument.getDeclaration())) {
                    Declaration stDecl = st.getDeclaration();
                    if (stDecl instanceof TypeDeclaration) {
                        TypeDeclaration td = 
                                (TypeDeclaration) stDecl;
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
            String description, 
            String missingSatisfiedTypeText, 
            TextFileChange change, Region selection) {
        super(description, change, selection);
        this.typeParam = typeParam;
        this.missingSatisfiedTypeText = missingSatisfiedTypeText;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddSatisfiesProposal) {
            AddSatisfiesProposal that = 
                    (AddSatisfiesProposal) obj;
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