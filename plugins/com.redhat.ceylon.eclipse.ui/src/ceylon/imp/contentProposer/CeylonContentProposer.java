package ceylon.imp.contentProposer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.eclipse.imp.editor.ErrorProposal;
import org.eclipse.imp.editor.SourceProposal;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IContentProposer;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
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
      int offset, ITextViewer viewer) {
    CeylonParseController parseController = (CeylonParseController) ctlr;
    List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

    if (ctlr.getCurrentAst() != null) {
      CeylonSourcePositionLocator locator = parseController.getSourcePositionLocator();
      String prefix;
      Node node = locator.findNode(ctlr.getCurrentAst(), offset);
      if (node==null) {
        //TODO: need to do something much better here:
        //      search for a surrounding scope
        //      search for a token, and treat it as a base expression or type
        prefix = "";
        node = (Tree.CompilationUnit) ctlr.getCurrentAst();
      }
      else {
        prefix = getPrefix(node, offset);
      }

      /*SearchVisitor sv = new SearchVisitor( new SearchVisitor.Matcher() {
        @Override
        public boolean matches(String string) {
          return string.startsWith(prefix);
        }
        @Override
        public boolean includeReferences() {
          return false;
        }
        @Override
        public boolean includeDeclarations() {
          return true;
        }
      });
      ((Node) parseController.getCurrentAst()).visit(sv);
      for (Node n: sv.getNodes()) {
        Declaration d = (Declaration) n;
        result.add(new SourceProposal(CeylonLabelProvider.getLabelFor(n), d.getIdentifier().getText(), prefix, offset));
      }*/
      for (final Declaration d: getProposals(node, prefix).values()) {
        boolean includeArgs = node instanceof Tree.StaticMemberOrTypeExpression;
        result.add(new SourceProposal(getDescriptionFor(d, includeArgs), getTextFor(d, includeArgs), prefix, offset) { 
          public Image getImage() {
            return CeylonLabelProvider.getImage(d);
          }; 
        });
      }
    } 
    else {
      result.add(new ErrorProposal(
          "No proposals available due to syntax errors", offset));
    }
    return result.toArray(new ICompletionProposal[result.size()]);
  }

  private Map<String, Declaration> getProposals(Node node, final String prefix) {
    //TODO: substitute type arguments to receiving type
    if (node instanceof Tree.QualifiedMemberExpression) {
      ProducedType type = ((Tree.QualifiedMemberExpression) node).getPrimary().getTypeModel();
      if (type!=null) {
        return type.getDeclaration().getMatchingMemberDeclarations(prefix);
      }
    }
    else if (node instanceof Tree.QualifiedTypeExpression) {
      ProducedType type = ((Tree.QualifiedTypeExpression) node).getPrimary().getTypeModel();
      return type.getDeclaration().getMatchingMemberDeclarations(prefix);
    }
    return node.getScope().getMatchingDeclarations(node.getUnit(), prefix);
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
    else {
      return "";
    }
  }

  private String getPrefix(int offset, Tree.Identifier id) {
    if (id==null||id.getText().equals("")) return "";
    return id.getText().substring(0, offset-((CommonToken) id.getToken()).getStartIndex());
  }
}
