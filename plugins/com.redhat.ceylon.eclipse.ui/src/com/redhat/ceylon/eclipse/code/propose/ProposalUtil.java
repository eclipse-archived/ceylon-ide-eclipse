package com.redhat.ceylon.eclipse.code.propose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;

public class ProposalUtil {

	public static List<Declaration> overloads(Declaration dec) {
	    if (dec instanceof Functional && ((Functional) dec).isAbstraction()) {
	        return ((Functional) dec).getOverloads();
	    }
	    else {
	        return Collections.singletonList(dec);
	    }
	}

	static List<Parameter> getParameters(boolean includeDefaults,
	        List<Parameter> pl) {
	    if (includeDefaults) {
	        return pl;
	    }
	    else {
	        List<Parameter> list = new ArrayList<Parameter>();
	        for (Parameter p: pl) {
	            if (!p.isDefaulted() || 
	            		(p==pl.get(pl.size()-1) && 
				                p.getDeclaration().getUnit()
				                        .isIterableParameterType(p.getType()))) {
	            	list.add(p);
	            }
	        }
	        return list;
	    }
	}

}
