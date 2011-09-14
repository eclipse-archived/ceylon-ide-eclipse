package com.redhat.ceylon.eclipse.imp.hover;

import java.util.List;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;

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
        StringBuilder documentation = new StringBuilder();
        if (decl!=null) {
            appendDescription(decl, documentation);
            Declaration model = decl.getDeclarationModel();
            if (model!=null) {
                appendInheritance(documentation, model);
                appendDeclaringType(documentation, model);
            }
            
            appendDocAnnotationContent(decl, documentation);
        }
        return documentation.toString();
    }

    private static void appendDescription(Tree.Declaration decl, StringBuilder documentation) {
        documentation.append("<p>[").append(getPackageLabel(decl)).append("]</p>");
        documentation.append("<p><b>").append(sanitize(CeylonLabelProvider.getLabelFor(decl)))
                .append("</b></p>");
    }

    private static void appendDeclaringType(StringBuilder documentation, Declaration model) {
        if (model.isClassOrInterfaceMember()) {
            TypeDeclaration declaring = (TypeDeclaration) model.getContainer();
            documentation.append("<ul><li>declared by ").append(declaring.getName())
                    .append(" [" + getPackageLabel(declaring) + "]</li>");
            if (model.isActual()) {
                Declaration refined = model.getRefinedDeclaration();
                TypeDeclaration supertype = (TypeDeclaration) refined.getContainer();
                String spkg = supertype.getUnit().getPackage().getQualifiedNameString();
                if (spkg.isEmpty()) spkg="default package";
                documentation.append("<li>refines '" + CeylonContentProposer.getDescriptionFor(refined)) 
                        .append("' declared by ").append(supertype.getName()) 
                        .append(" [" + getPackageLabel(refined) + "]</li>");
            }
            documentation.append("</ul>");
        }
    }

    private static void appendInheritance(StringBuilder documentation, Declaration model) {
        if (model instanceof Class) {
            ProducedType extended = ((Class) model).getExtendedType();
            if (extended!=null) {
                documentation.append("<ul><li>extends ")
                        .append(sanitize(extended.getProducedTypeName()))
                        .append(" [" + getPackageLabel(extended.getDeclaration()) + "]</li></ul>");
            }
        }
        if (model instanceof TypeDeclaration) {
            List<ProducedType> types = ((TypeDeclaration) model).getSatisfiedTypes();
            if (!types.isEmpty()) {
                documentation.append("<ul>");
                for (ProducedType satisfied : types) {
                    documentation.append("<li>satisfies ")
                            .append(sanitize(satisfied.getProducedTypeName()) )
                            .append(" [" + getPackageLabel(satisfied.getDeclaration()) + "]</li>");
                }
                documentation.append("</ul>");
            }
        }
    }

    private static void appendDocAnnotationContent(Tree.Declaration decl,
            StringBuilder documentation) {
        Tree.AnnotationList annotationList = decl.getAnnotationList();
        if (annotationList != null)
        {
            for (Tree.Annotation annotation : annotationList.getAnnotations())
            {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim != null)
                {
                    Declaration annotDecl = annotPrim.getDeclaration(); 
                    if (annotDecl!=null) {
                        String name = annotDecl.getName();
                        if ("doc".equals(name))
                        {
                            Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                            List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                            if (!args.isEmpty())
                            {
                                String docLine = args.get(0).getExpression().getTerm().getText();
                                documentation.append("<br/><p>" + docLine + "</p>");
                            }
                        }
                    }
                }
            }
        }
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
    
    private static String sanitize(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
    
}
