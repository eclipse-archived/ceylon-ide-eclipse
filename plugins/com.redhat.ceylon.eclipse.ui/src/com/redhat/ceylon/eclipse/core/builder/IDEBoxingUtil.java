package com.redhat.ceylon.eclipse.core.builder;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

/**
 * FIXME: this entire class is copied from AbstractTransformer and needs to be refactored to be abstracted
 * from it without copying.
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class IDEBoxingUtil {
    
    static public boolean isCeylonBasicType(ProducedType type) {
        return isCeylonString(type) || 
                isCeylonBoolean(type) || 
                isCeylonInteger(type) || 
                isCeylonFloat(type) || 
                isCeylonCharacter(type);
    }

    static public boolean isBooleanTrue(Declaration decl) {
        return decl.getUnit().getLanguageModuleDeclaration("true") == decl;
    }

    static public boolean isBooleanFalse(Declaration decl) {
        return decl.getUnit().getLanguageModuleDeclaration("false") == decl;
    }

    static private boolean isCeylonBoolean(ProducedType type) {
        return type.isSubtypeOf(unit(type).getBooleanDeclaration().getType())
                && !(type.getDeclaration() instanceof NothingType);
    }

    static private boolean isCeylonString(ProducedType type) {
        return unit(type).getStringDeclaration().equals(type.getDeclaration());
    }

    static private Unit unit(ProducedType type) {
        return type.getDeclaration().getUnit();
    }

    static private boolean isCeylonInteger(ProducedType type) {
        return unit(type).getIntegerDeclaration().equals(type.getDeclaration());
    }

    static private boolean isCeylonFloat(ProducedType type) {
        return unit(type).getFloatDeclaration().equals(type.getDeclaration());
    }

    static private boolean isCeylonCharacter(ProducedType type) {
        return unit(type).getCharacterDeclaration().equals(type.getDeclaration());
    }

    static public boolean isNull(ProducedType type) {
        return unit(type).getNullDeclaration().equals(type.getDeclaration());
    }

    static public boolean isObject(ProducedType type) {
        return unit(type).getObjectDeclaration().equals(type.getDeclaration());
    }

    static public boolean willEraseToObject(ProducedType type) {
        //TODO: is this correct??
        Unit unit = unit(type);
        TypeDeclaration dec = type.getDeclaration();
        return unit.getObjectDeclaration()==dec ||
                unit.getIdentifiableDeclaration()==dec ||
                unit.getBasicDeclaration()==dec ||
                unit.getNullDeclaration()==dec ||
                unit.getNullValueDeclaration().getTypeDeclaration()==dec ||
                unit.getAnythingDeclaration()==dec ||
                dec instanceof NothingType ||
                dec instanceof UnionType || 
                dec instanceof IntersectionType;
    }

    static public boolean isCallable(ProducedType type) {
        return type.getSupertype(unit(type).getCallableDeclaration()) != null;
    }

    /**
     * Copy of AbstractTransformer.isTurnedToRaw to keep in sync
     */
    static public boolean isTurnedToRaw(ProducedType type){
        return isTurnedToRawResolved(type.resolveAliases());
    }
    
    private static boolean isTurnedToRawResolved(ProducedType type) {
        // if we don't have type arguments we can't be raw
        if(type.getTypeArguments().isEmpty())
            return false;

        // we only go raw if every type param is an erased union/intersection
        
        // special case for Callable where we stop after the first type param
        boolean isCallable = isCallable(type);
        
        boolean everyTypeParameterIsErasedUnionIntersection = true;
        
        for(ProducedType typeArg : type.getTypeArgumentList()){
            // skip invalid input
            if(typeArg == null)
                return false;
            
            everyTypeParameterIsErasedUnionIntersection &= isErasedUnionOrIntersection(typeArg);
            
            // Callable really has a single type arg in Java
            if(isCallable)
                break;
            // don't recurse
        }
        // we're only raw if every type param is an erased union/intersection
        return everyTypeParameterIsErasedUnionIntersection;
    }

    private static boolean isErasedUnionOrIntersection(ProducedType producedType) {
        TypeDeclaration typeDeclaration = producedType.getDeclaration();
        if(typeDeclaration instanceof UnionType){
            UnionType ut = (UnionType) typeDeclaration;
            java.util.List<ProducedType> caseTypes = ut.getCaseTypes();
            // special case for optional types
            if(caseTypes.size() == 2){
                if(isNull(caseTypes.get(0))){
                    return isErasedUnionOrIntersection(caseTypes.get(1));
                }else if(isNull(caseTypes.get(1))){
                    return isErasedUnionOrIntersection(caseTypes.get(0));
                }
            }
            // it is erased unless we turn it into Sequential something
            return !willEraseToSequential(producedType);
        }
        if(typeDeclaration instanceof IntersectionType){
            IntersectionType ut = (IntersectionType) typeDeclaration;
            java.util.List<ProducedType> satisfiedTypes = ut.getSatisfiedTypes();
            // special case for non-optional types
            if(satisfiedTypes.size() == 2){
                if(isObject(satisfiedTypes.get(0))){
                    return isErasedUnionOrIntersection(satisfiedTypes.get(1));
                }else if(isObject(satisfiedTypes.get(1))){
                    return isErasedUnionOrIntersection(satisfiedTypes.get(0));
                }
            }
            // it is erased unless we turn it into Sequential something
            return !willEraseToSequential(producedType);
        }
        // we found something which is not erased entirely
        return false;
    }

    static boolean willEraseToSequential(ProducedType type) {
        type = simplifyType(type);
        TypeDeclaration decl = type.getDeclaration();
        return (decl instanceof UnionType || decl instanceof IntersectionType)
                && unit(type).isSequentialType(type);
    }

    static public boolean hasErasure(ProducedType type) {
        return hasErasureResolved(type.resolveAliases());
    }
    
    static private boolean hasErasureResolved(ProducedType type) {
        TypeDeclaration declaration = type.getDeclaration();
        if(declaration == null)
            return false;
        if(declaration instanceof UnionType){
            UnionType ut = (UnionType) declaration;
            java.util.List<ProducedType> caseTypes = ut.getCaseTypes();
            // special case for optional types
            if(caseTypes.size() == 2){
                if(isOptional(caseTypes.get(0)))
                    return hasErasureResolved(caseTypes.get(1));
                if(isOptional(caseTypes.get(1)))
                    return hasErasureResolved(caseTypes.get(0));
            }
            // must be erased
            return true;
        }
        if(declaration instanceof IntersectionType){
            IntersectionType ut = (IntersectionType) declaration;
            java.util.List<ProducedType> satisfiedTypes = ut.getSatisfiedTypes();
            // special case for non-optional types
            if(satisfiedTypes.size() == 2){
                if(isObject(satisfiedTypes.get(0)))
                    return hasErasureResolved(satisfiedTypes.get(1));
                if(isObject(satisfiedTypes.get(1)))
                    return hasErasureResolved(satisfiedTypes.get(0));
            }
            // must be erased
            return true;
        }
        // Note: we don't consider types like Anything, Null, Basic, Identifiable as erased because
        // they can never be better than Object as far as Java is concerned
        // FIXME: what about Nothing then?
        
        // special case for Callable where we stop after the first type param
        boolean isCallable = isCallable(type);
        
        // now check its type parameters
        for(ProducedType pt : type.getTypeArgumentList()){
            if(hasErasureResolved(pt))
                return true;
            if(isCallable)
                break;
        }
        // no erasure here
        return false;
    }

    static private boolean isOptional(ProducedType type) {
        // Note we don't use typeFact().isOptionalType(type) because
        // that implements a stricter test used in the type checker.
        return unit(type).getNullValueDeclaration().getType().isSubtypeOf(type);
    }

    static private ProducedType simplifyType(ProducedType orgType) {
        if(orgType == null)
            return null;
        ProducedType type = orgType.resolveAliases();
        if (isOptional(type)) {
            // For an optional type T?:
            //  - The Ceylon type T? results in the Java type T
            type = unit(type).getDefiniteType(type);
            if (type.getUnderlyingType() != null) {
                // A definite type should not have its underlyingType set so we make a copy
                type = type.withoutUnderlyingType();
            }
        }
        
        TypeDeclaration tdecl = type.getDeclaration();
        if (tdecl instanceof UnionType && tdecl.getCaseTypes().size() == 1) {
            // Special case when the Union contains only a single CaseType
            // FIXME This is not correct! We might lose information about type arguments!
            type = tdecl.getCaseTypes().get(0);
        } else if (tdecl instanceof IntersectionType) {
            java.util.List<ProducedType> satisfiedTypes = tdecl.getSatisfiedTypes();
            if (satisfiedTypes.size() == 1) {
                // Special case when the Intersection contains only a single SatisfiedType
                // FIXME This is not correct! We might lose information about type arguments!
                type = satisfiedTypes.get(0);
            } else if (satisfiedTypes.size() == 2) {
                // special case for T? simplified as T&Object
                if (isTypeParameter(satisfiedTypes.get(0)) && isObject(satisfiedTypes.get(1))) {
                    type = satisfiedTypes.get(0);
                }
            }
        }
        
        return type;
    }

    static private boolean isTypeParameter(ProducedType type) {
        if (unit(type).isOptionalType(type)) {
            type = type.minus(unit(type).getNullDeclaration().getType());
        } 
        return type.getDeclaration() instanceof TypeParameter;
    }

}
