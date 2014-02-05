package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.keywords;

import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public class Escaping {

	public static String escape(String suggestedName) {
	    if (keywords.contains(suggestedName)) {
	        return "\\i" + suggestedName;
	    }
	    else {
	        return suggestedName;
	    }
	}

	public static String escapedPackageName(Package p) {
		List<String> path = p.getName();
	    StringBuilder sb = new StringBuilder();
	    for (int i=0; i<path.size(); i++) {
	        String pathPart = path.get(i);
	        if (!pathPart.isEmpty()) {
	            if (CeylonTokenColorer.keywords.contains(pathPart)) {
	            	pathPart = "\\i" + pathPart;
	            }
	            sb.append(pathPart);
	            if (i<path.size()-1) sb.append('.');
	        }
	    }
	    return sb.toString();
	}

}
