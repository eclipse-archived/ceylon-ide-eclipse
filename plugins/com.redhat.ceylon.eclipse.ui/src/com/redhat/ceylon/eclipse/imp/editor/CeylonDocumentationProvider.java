package com.redhat.ceylon.eclipse.imp.editor;

import java.util.List;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.contentProposer.CeylonContentProposer;
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
  
  private static String sanitize(String s) {
      return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  public static String getDocumentation(Tree.Declaration decl) {
    String documentation = "";
    if (decl!=null) {
	  documentation += "<p>[" + getPackageLabel(decl) + "]</p>";
      documentation += "<p><b>" + sanitize(CeylonLabelProvider.getLabelFor(decl))
    		   + "</b></p>";
      
      Declaration model = decl.getDeclarationModel();
      if (model!=null) {
          if (model instanceof Class) {
            ProducedType extended = ((Class) model).getExtendedType();
            if (extended!=null) {
              documentation += "<ul><li>extends " + sanitize(extended.getProducedTypeName()) + 
                      " [" + getPackageLabel(extended.getDeclaration()) + "]</li></ul>";
            }
          }
          if (model instanceof TypeDeclaration) {
            List<ProducedType> types = ((TypeDeclaration) model).getSatisfiedTypes();
            if (!types.isEmpty()) {
                documentation += "<ul>";
                for (ProducedType satisfied : types) {
                    documentation += "<li>satisfies "+ sanitize(satisfied.getProducedTypeName()) 
                            + " [" + getPackageLabel(satisfied.getDeclaration()) + "]</li>";
                }
                documentation += "</ul>";
            }
          }
          if (model.isClassOrInterfaceMember()) {
              TypeDeclaration declaring = (TypeDeclaration) model.getContainer();
              documentation += "<ul><li>declared by " + declaring.getName() + 
                      " [" + getPackageLabel(declaring) + "]</li>";
              if (model.isActual()) {
                  Declaration refined = model.getRefinedDeclaration();
                  TypeDeclaration supertype = (TypeDeclaration) refined.getContainer();
                  String spkg = supertype.getUnit().getPackage().getQualifiedNameString();
                  if (spkg.isEmpty()) spkg="default package";
                  documentation += "<li>refines '" + CeylonContentProposer.getDescriptionFor(refined) + 
                          "' declared by " + supertype.getName() + 
                          " [" + getPackageLabel(refined) + "]</li>";
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

  public static String getPackageLabel(Tree.Declaration decl) {
    String pkg = decl.getUnit().getPackage().getQualifiedNameString();
	if (pkg.isEmpty()) pkg="default package";
    return pkg;
  }
  
  public static String getPackageLabel(Declaration decl) {
    String pkg = decl.getUnit().getPackage().getQualifiedNameString();
    if (pkg.isEmpty()) pkg="default package";
    return pkg;
  }
    
}
