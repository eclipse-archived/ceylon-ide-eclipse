/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model.mirror;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

import org.eclipse.ceylon.model.loader.mirror.AnnotationMirror;

public class JDTUtils {

    public static Map<String, AnnotationMirror> getAnnotations(AnnotationBinding[] annotations) {
        HashMap<String, AnnotationMirror> result = new HashMap<String, AnnotationMirror>();
        for(AnnotationBinding annotation : annotations){
            result.put(getFullyQualifiedName(annotation.getAnnotationType()), new JDTAnnotation(annotation));
        }
        return result;
    }

    public static String getFlatName(TypeBinding type) {
        StringBuilder builder = new StringBuilder();
        char[] packageName = type.qualifiedPackageName();
        if (packageName != CharOperation.NO_CHAR) {
            builder.append(packageName).append('.');
        }
        return builder.append(new String(type.qualifiedSourceName()).replace('.', '$')).toString();
    }
    
    public static String getFullyQualifiedName(TypeBinding type) {
        StringBuilder builder = new StringBuilder();
        char[] packageName = type.qualifiedPackageName();
        if (packageName != CharOperation.NO_CHAR) {
            builder.append(packageName).append('.');
        }
        return builder.append(type.qualifiedSourceName()).toString();
    }
    
    public static Object fromConstant(Constant constant) {
        switch(constant.typeID()) {
        case Constant.T_boolean :
            return new Boolean(constant.booleanValue());
        case Constant.T_byte :
            return new Byte(constant.byteValue());
        case Constant.T_char :
            return new Character(constant.charValue());
        case Constant.T_double :
            return new Double(constant.doubleValue());
        case Constant.T_float :
            return new Float(constant.floatValue());
        case Constant.T_int :
            return new Integer(constant.intValue());
        case Constant.T_short :
            return new Short(constant.shortValue());
        case Constant.T_long :
            return new Long(constant.longValue());
        case Constant.T_JavaLangString :
            return new String(constant.stringValue());
        }
        return null;
    }
    
    public static ReferenceBinding inferTypeParametersFromSuperClass(
            ReferenceBinding declaringClass, ReferenceBinding superClass) {
        if (superClass instanceof RawTypeBinding && declaringClass instanceof ParameterizedTypeBinding) {
            LookupEnvironment lookupEnvironment = superClass.getPackage().environment;
            ParameterizedTypeBinding rawSuperType = (ParameterizedTypeBinding) superClass;
            ParameterizedTypeBinding declaringType = (ParameterizedTypeBinding) declaringClass;
            superClass = lookupEnvironment.createParameterizedType(
                    rawSuperType.genericType(), declaringType.arguments, rawSuperType.enclosingType());
        }
        return superClass;
    }


    public static boolean hasAnnotation(MethodBinding inheritedMethod, String ceylonIgnoreAnnotation) {
        for(AnnotationBinding annotation : inheritedMethod.getAnnotations()){
            if(getFullyQualifiedName(annotation.getAnnotationType()).equals(ceylonIgnoreAnnotation))
                return true;
        }
        return false;
    }
}
