package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;

public class Types {

	public static ProducedType getResultType(Declaration d) {
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

}
