package com.redhat.ceylon.eclipse.code.propose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnnotationList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Util;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
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
	            		(p==pl.get(pl.size()-1) && p.getType()!=null &&
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

	static int nextTokenType(final CeylonParseController cpc,
			final CommonToken token) {
		for (int i=token.getTokenIndex()+1; i<cpc.getTokens().size(); i++) {
			CommonToken tok = cpc.getTokens().get(i);
			if (tok.getChannel()!=CommonToken.HIDDEN_CHANNEL) {
				return tok.getType();
			}
		}
		return -1;
	}

	/**
	 * BaseMemberExpressions in Annotations have funny lying
	 * scopes, but we can extract the real scope out of the
	 * identifier! (Yick)
	 */
	static Scope getRealScope(final Node node, CompilationUnit cu) {
		
	    class FindScopeVisitor extends Visitor {
	        Scope scope;
	        public void visit(Tree.Declaration that) {
	            super.visit(that);
	            AnnotationList al = that.getAnnotationList();
	            if (al!=null) {
	                for (Tree.Annotation a: al.getAnnotations()) {
	                    Integer i = a.getPrimary().getStartIndex();
	                    Integer j = node.getStartIndex();
	                    if (i.intValue()==j.intValue()) {
	                        scope = that.getDeclarationModel().getScope();
	                    }
	                }
	            }
	        }
	        
	        public void visit(Tree.DocLink that) {
	            super.visit(that);
	            scope = ((Tree.DocLink)node).getPkg();
	        }
	    };
	    FindScopeVisitor fsv = new FindScopeVisitor();
	    fsv.visit(cu);
	    return fsv.scope==null ? node.getScope() : fsv.scope;
	}

	static int getLine(final int offset, ITextViewer viewer) {
	    int line=-1;
	    try {
	        line = viewer.getDocument().getLineOfOffset(offset);
	    }
	    catch (BadLocationException e) {
	        e.printStackTrace();
	    }
	    return line;
	}

}
