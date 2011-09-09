package com.redhat.ceylon.eclipse.imp.editor;

import java.util.List;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonLabelProvider;

public class CeylonDocumentationProvider implements IDocumentationProvider {

  public String getDocumentation(Object entity, IParseController ctlr) {
    if (entity instanceof Tree.Declaration) {
      return getDocumentation((Tree.Declaration) entity);
    }
    else {
      return null;
    }
  }

  public static String getDocumentation(Tree.Declaration decl) {
	  String documentation = "";
	  if (decl!=null) {
	  String pkg = decl.getUnit().getPackage().getQualifiedNameString();
	  if (pkg.isEmpty()) pkg="default package";
	  documentation += "<p>[" + pkg + "]</p>";
      documentation += "<p><b>" + CeylonLabelProvider.getLabelFor(decl)
    		  .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</b></p>";
      
      if (decl.getDeclarationModel()!=null) {
          if (decl.getDeclarationModel().isClassOrInterfaceMember()) {
              TypeDeclaration declaring = (TypeDeclaration) decl.getDeclarationModel().getContainer();
              documentation += "<ul><li>declared by " + declaring.getName() + " [" + pkg + "]</li>";
              if (decl.getDeclarationModel().isActual()) {
                  Declaration refined = decl.getDeclarationModel().getRefinedDeclaration();
                  TypeDeclaration supertype = (TypeDeclaration) refined.getContainer();
                  String spkg = supertype.getUnit().getPackage().getQualifiedNameString();
                  if (spkg.isEmpty()) spkg="default package";
                  documentation += "<li>refines " + refined.getName() + " declared by " + 
                              supertype.getName() + " [" + pkg + "]</li>";
              }
              documentation +="</ul>";
          }
      }
      
      Tree.AnnotationList annotationList = decl.getAnnotationList();
      if (annotationList != null)
      {
        List<Tree.Annotation> annotations = annotationList.getAnnotations();
        for (Tree.Annotation annotation : annotations)
        {
            Tree.Primary annotPrim = annotation.getPrimary();
          if (annotPrim != null)
          {
            com.redhat.ceylon.compiler.typechecker.model.Declaration annotDecl = annotPrim.getDeclaration(); 
            if (annotDecl!=null) {
            String name = annotDecl.getName();
            if ("doc".equals(name))
            {
                Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
              List<Tree.PositionalArgument> args = argList.getPositionalArguments();
              if (args.size() > 0)
              {
                String docLine = args.get(0).getExpression().getTerm().getText();
                documentation += "<br/><p>" + docLine + "</p>";
              }
            }
            }
          }
        }
      }
	  }
      return documentation;
  }
}
