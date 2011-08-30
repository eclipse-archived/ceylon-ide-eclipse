package com.redhat.ceylon.eclipse.imp.contentProposer;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getDeclarationNode;
import static com.redhat.ceylon.eclipse.imp.editor.CeylonDocumentationProvider.getDocumentation;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.CommonToken;
import org.eclipse.imp.editor.ErrorProposal;
import org.eclipse.imp.editor.SourceProposal;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IContentProposer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonLabelProvider;

public class CeylonContentProposer implements IContentProposer {
  /**
   * Returns an array of content proposals applicable relative to the AST of the given
   * parse controller at the given position.
   * 
   * (The provided ITextViewer is not used in the default implementation provided here
   * but but is stipulated by the IContentProposer interface for purposes such as accessing
   * the IDocument for which content proposals are sought.)
   * 
   * @param controller  A parse controller from which the AST of the document being edited
   *             can be obtained
   * @param int      The offset for which content proposals are sought
   * @param viewer    The viewer in which the document represented by the AST in the given
   *             parse controller is being displayed (may be null for some implementations)
   * @return        An array of completion proposals applicable relative to the AST of the given
   *             parse controller at the given position
   */
  public ICompletionProposal[] getContentProposals(IParseController ctlr,
      final int offset, ITextViewer viewer) {
    CeylonParseController parseController = (CeylonParseController) ctlr;
    List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
    String p="";
    CommonToken tok=null;
    CommonToken prev = null;
    for (CommonToken t: (List<CommonToken>) parseController.getTokenStream().getTokens()) {
    	if (t.getStartIndex()<=offset && t.getStopIndex()+1>=offset) {
    		p = t.getText().substring(0, offset-t.getStartIndex());
    		tok = t;
    		if (t.getType()==CeylonLexer.MEMBER_OP) {
    			p = "";
    		}
    		else if (t.getType()!=CeylonLexer.LIDENTIFIER && 
    				t.getType()!=CeylonLexer.UIDENTIFIER) {
    			p = prev.getText();
    			tok = prev;
    		}
    		break;
    	}
    	prev = t;
    }
    final String prefix=p;
    if (parseController.getRootNode() != null) {
      CeylonSourcePositionLocator locator = parseController.getSourcePositionLocator();
      Node node = locator.findNode(parseController.getRootNode(), tok.getStartIndex(), tok.getStopIndex());
      if (node==null) {
        node = parseController.getRootNode();
      }
            
      Map<String, Declaration> proposals = getProposals(node, prefix, parseController);
      TreeMap<String, Declaration> map = new TreeMap<String, Declaration>(new Comparator<String>() {
        public int compare(String x, String y) {
        	int lowers = prefix.length()==0 || isLowerCase(prefix.charAt(0)) ? -1 : 1;
        	if (isLowerCase(x.charAt(0)) && 
        			isUpperCase(y.charAt(0))) {
        		return lowers;
        	}
        	else if (isUpperCase(x.charAt(0)) && 
        			isLowerCase(y.charAt(0))) {
        		return -lowers;
        	}
        	else {
        		return x.compareTo(y);
        	}
        }
      });
      map.putAll(proposals);
      for (final Declaration d: map.values()) {
        if (d instanceof TypeDeclaration) {
          result.add(sourceProposal(offset, prefix, d, getDescriptionFor(d, false), 
        		  getTextFor(d, false), parseController));
        }
        if (d instanceof TypedDeclaration || d instanceof Class) {
          result.add(sourceProposal(offset, prefix, d, getDescriptionFor(d, true), 
        		  getTextFor(d, true), parseController));
        }
      }
    } 
    else {
      result.add(new ErrorProposal("No proposals available due to syntax errors", 
                 offset));
    }
    return result.toArray(new ICompletionProposal[result.size()]);
  }

  private SourceProposal sourceProposal(final int offset, final String prefix,
		final Declaration d, String desc, final String text, CeylonParseController ctlr) {
	return new SourceProposal(desc, text, prefix, new Region(offset, 0), 
			                  offset + text.length() - prefix.length(),
			                  getDocumentation(getDeclarationNode(ctlr, d))) { 
      @Override
      public Image getImage() {
	    return CeylonLabelProvider.getImage(d);
	  }
	  @Override
	    public Point getSelection(IDocument document) {
	      int loc = text.indexOf('(');
	      int start;
	      int length;
	      if (loc<0||text.contains("()")) {
	    	start = offset+text.length()-prefix.length();
	    	length = 0;
	      }
	      else {
		    start = offset-prefix.length()+loc+1;
		    length = text.length()-loc-2;
	      }
	      return new Point(start, length);
	    }
	};
  }

  private Map<String, Declaration> getProposals(Node node, final String prefix,
		  CeylonParseController parseController) {
    //TODO: substitute type arguments to receiving type
    if (node instanceof Tree.QualifiedMemberExpression) {
      ProducedType type = ((Tree.QualifiedMemberExpression) node).getPrimary().getTypeModel();
      if (type!=null) {
        return type.getDeclaration().getMatchingMemberDeclarations(prefix);
      }
      else {
        return Collections.emptyMap();
      }
    }
    else if (node instanceof Tree.QualifiedTypeExpression) {
      ProducedType type = ((Tree.QualifiedTypeExpression) node).getPrimary().getTypeModel();
      if (type!=null) {
        return type.getDeclaration().getMatchingMemberDeclarations(prefix);
      }
      else {
        return Collections.emptyMap();
      }
    }
    else if (node instanceof Tree.NamedArgument) {
      //TODO: this case is really problematic because the
      //      model doesn't have anything about named args
      //      and the tree can't be walked upward
      Declaration p = ((Tree.NamedArgument) node).getParameter();
      return Collections.singletonMap(p.getName(), p);
    }
    else {
      Map<String, Declaration> result = new TreeMap<String, Declaration>(); 
      Module languageModule = parseController.getContext().getModules().getLanguageModule();
      if (languageModule!=null) {
        for (Package languageScope: languageModule.getPackages() ) {
          result.putAll(languageScope.getMatchingDeclarations(null, prefix));
        }
      }
      result.putAll(node.getScope().getMatchingDeclarations(node.getUnit(), prefix));
      return result;
    }
  }

  public static String getTextFor(Declaration d, boolean includeArgs) {
    String result = d.getName();
    if (d instanceof Generic) {
      List<TypeParameter> types = ((Generic) d).getTypeParameters();
      if (!types.isEmpty()) {
        result += "<";
        for (TypeParameter p: types) {
          result += p.getName() + ", ";
        }
        result = result.substring(0, result.length()-2) + ">";
      }
    }
    if (includeArgs && d instanceof Functional) {
      List<ParameterList> plists = ((Functional) d).getParameterLists();
      if (plists!=null && !plists.isEmpty()) {
        ParameterList params = plists.get(0);
        if (params.getParameters().isEmpty()) {
          result += "()";
        }
        else {
          result += "(";
          for (Parameter p: params.getParameters()) {
            result += p.getName() + ", ";
          }
          result = result.substring(0, result.length()-2) + ")";
        }
      }
    }
    return result;
  }
  
  public static String getDescriptionFor(Declaration d, boolean includeArgs) {
    String result = d.getName();
    if (d instanceof Generic) {
      List<TypeParameter> types = ((Generic) d).getTypeParameters();
      if (!types.isEmpty()) {
        result += "<";
        for (TypeParameter p: types) {
          result += p.getName() + ", ";
        }
        result = result.substring(0, result.length()-2) + ">";
      }
    }
    if (includeArgs && d instanceof Functional) {
      List<ParameterList> plists = ((Functional) d).getParameterLists();
      if (plists!=null && !plists.isEmpty()) {
        ParameterList params = plists.get(0);
        if (params.getParameters().isEmpty()) {
          result += "()";
        }
        else {
          result += "(";
          for (Parameter p: params.getParameters()) {
            result += p.getType().getProducedTypeName() + " " + p.getName() + ", ";
          }
          result = result.substring(0, result.length()-2) + ")";
        }
      }
    }
    return result;
  }
  
  private String getPrefix(Node node, int offset) {
    if (node instanceof Tree.SimpleType) {
      Tree.Identifier id = ((Tree.SimpleType) node).getIdentifier();
      return getPrefix(offset, id);
    }
    else if (node instanceof Tree.StaticMemberOrTypeExpression) {
        Tree.Identifier id = ((Tree.StaticMemberOrTypeExpression) node).getIdentifier();
        return getPrefix(offset, id);
      }
    else if (node instanceof Tree.Declaration) {
      Tree.Identifier id = ((Tree.Declaration) node).getIdentifier();
      return getPrefix(offset, id);
    }
    else if (node instanceof Tree.NamedArgument) {
        Tree.Identifier id = ((Tree.NamedArgument) node).getIdentifier();
        return getPrefix(offset, id);
      }
    else {
      return "";
    }
  }

  private String getPrefix(int offset, Tree.Identifier id) {
    if (id==null||id.getText().equals("")) return "";
    if (offset<0) return id.getText(); 
    int index = offset-((CommonToken) id.getToken()).getStartIndex();
    if (index<=0) return "";
	return id.getText().substring(0, index);
  }
}
