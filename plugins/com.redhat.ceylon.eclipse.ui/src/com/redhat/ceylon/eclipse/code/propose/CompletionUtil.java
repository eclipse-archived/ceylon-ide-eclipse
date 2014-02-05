package com.redhat.ceylon.eclipse.code.propose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Util;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CompletionUtil {

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

    static String fullPath(int offset, String prefix,
            Tree.ImportPath path) {
        StringBuilder fullPath = new StringBuilder();
        if (path!=null) {
            fullPath.append(Util.formatPath(path.getIdentifiers()));
            fullPath.append('.');
            fullPath.setLength(offset-path.getStartIndex()-prefix.length());
        }
        return fullPath.toString();
    }

    static boolean isPackageDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null && 
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("package.ceylon"); 
    }

    static boolean isModuleDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null && 
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("module.ceylon"); 
    }

    static boolean isEmptyModuleDescriptor(CeylonParseController cpc) {
        return isModuleDescriptor(cpc) && 
                cpc.getRootNode() != null && 
                cpc.getRootNode().getModuleDescriptors().isEmpty(); 
    }

    static boolean isEmptyPackageDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null &&
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("package.ceylon") && 
                cpc.getRootNode().getPackageDescriptors().isEmpty();
    }

	public static OccurrenceLocation getOccurrenceLocation(Tree.CompilationUnit cu, Node node) {
	    if (node.getToken()==null) return null;
	    FindOccurrenceLocationVisitor visitor = new FindOccurrenceLocationVisitor(node);
	    cu.visit(visitor);
	    return visitor.getOccurrenceLocation();
	}

}
