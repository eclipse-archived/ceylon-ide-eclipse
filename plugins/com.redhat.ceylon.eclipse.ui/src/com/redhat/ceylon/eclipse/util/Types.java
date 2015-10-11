package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getInterveningRefinements;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getSignature;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isAbstraction;

import java.util.List;

import org.antlr.runtime.CommonToken;

import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class Types {

    public static Type getResultType(Declaration d) {
        if (d instanceof TypeDeclaration) {
            if (d instanceof Class) {
                if (!((Class) d).isAbstract()) {
                    return ((TypeDeclaration) d).getType();
                }
            }
            return null;
        }
        else if (d instanceof TypedDeclaration) {
            return ((TypedDeclaration) d).getType();
        }
        else {
            return null;//impossible
        }
    }
    
    public interface Required {
        public Type getType();
        public String getParameterName();
    }

    public static Required getRequiredType(
            Tree.CompilationUnit rootNode,
            Node node, CommonToken token) {
        RequiredTypeVisitor rtv = 
                new RequiredTypeVisitor(node, token);
        rtv.visit(rootNode);
        return rtv;
    }

    public static Declaration getRefinedDeclaration(
            Declaration declaration) {
        //Reproduces the algorithm used to build the type hierarchy
        //first walk up the superclass hierarchy
        if (declaration.isClassOrInterfaceMember() && 
                declaration.isShared()) {
            TypeDeclaration dec = 
                    (TypeDeclaration) 
                        declaration.getContainer();
            List<Type> signature = 
                    getSignature(declaration);
            Declaration refined = 
                    declaration.getRefinedDeclaration();
            while (dec!=null) {
                Type extended = dec.getExtendedType();
                if (extended!=null) {
                    TypeDeclaration superDec = 
                            extended.getDeclaration();
                    Declaration superMemberDec = 
                            superDec.getDirectMember(
                                    declaration.getName(), 
                                    signature, false);
                    if (superMemberDec!=null) {
                        Declaration superRefined = 
                                superMemberDec.getRefinedDeclaration();
                        if (superRefined!=null && 
                            refined!=null && 
                                !isAbstraction(superMemberDec) && 
                                superRefined.equals(refined)) {
                            return superMemberDec;
                        }
                    }
                    dec = superDec;
                }
                else {
                    dec = null;
                }
            }
            //now look at the very top of the hierarchy, even if it is an interface
            Declaration refinedDeclaration = refined;
            if (refinedDeclaration!=null &&
                    !declaration.equals(refinedDeclaration)) {
                List<Declaration> directlyInheritedMembers = 
                        getInterveningRefinements(
                                declaration.getName(), 
                                signature,
                                refinedDeclaration,
                                (TypeDeclaration) 
                                    declaration.getContainer(), 
                                (TypeDeclaration) 
                                    refinedDeclaration.getContainer());
                directlyInheritedMembers.remove(
                        refinedDeclaration);
                //TODO: do something for the case of
                //      multiple intervening interfaces?
                if (directlyInheritedMembers.size()==1) {
                    //exactly one intervening interface
                    return directlyInheritedMembers.get(0);
                }
                else {
                    //no intervening interfaces
                    return refinedDeclaration;
                }
            }
        }
        return null;
    }

}
