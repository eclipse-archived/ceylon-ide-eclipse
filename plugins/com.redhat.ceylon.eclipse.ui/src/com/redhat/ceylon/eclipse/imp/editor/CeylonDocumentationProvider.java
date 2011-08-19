package com.redhat.ceylon.eclipse.imp.editor;

import java.util.List;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;

import com.redhat.ceylon.compiler.typechecker.tree.Tree.Annotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnnotationList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PositionalArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PositionalArgumentList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Primary;
import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonLabelProvider;

public class CeylonDocumentationProvider implements IDocumentationProvider {

  public String getDocumentation(Object entity, IParseController ctlr) {
    if (entity == null)
      return null;

    if (entity instanceof Declaration) {
      String documentation = "";
      Declaration decl = (Declaration) entity; 
      // START_HERE
      // Create a case for each kind of node or token for which you
      // want to provide help text and return the text corresponding
      // to that entity (such as in the following examples)

      // Address node types of interest, which may represent multiple tokens
      //String qualifiedName = decl.getIdentifier().getText();
      documentation += "<b>" + CeylonLabelProvider.getLabelFor(decl)
    		  .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</b>";
      
      AnnotationList annotationList = decl.getAnnotationList();
      if (annotationList != null)
      {
        List<Annotation> annotations = annotationList.getAnnotations();
        for (Annotation annotation : annotations)
        {
          Primary annotPrim = annotation.getPrimary();
          if (annotPrim != null)
          {
            com.redhat.ceylon.compiler.typechecker.model.Declaration annotDecl = annotPrim.getDeclaration(); 
            String name = annotDecl.getName();
            if ("doc".equals(name))
            {
              PositionalArgumentList argList = annotation.getPositionalArgumentList();
              List<PositionalArgument> args = argList.getPositionalArguments();
              if (args.size() > 0)
              {
                String docLine = args.get(0).getExpression().getTerm().getText();
                documentation += "<br/>" + docLine;
              }
            }            
          }
        }
      }
      
      return documentation;
    }
    return null;
  }
}
