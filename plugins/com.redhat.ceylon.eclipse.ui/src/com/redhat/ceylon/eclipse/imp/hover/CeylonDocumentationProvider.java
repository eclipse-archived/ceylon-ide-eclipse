package com.redhat.ceylon.eclipse.imp.hover;

import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.getPackageLabel;

import java.util.List;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;

public class CeylonDocumentationProvider implements IDocumentationProvider {
    
    public String getDocumentation(Object entity, IParseController ctlr) {
        if (entity instanceof Tree.Declaration) {
            return getDocumentation((Tree.Declaration) entity);
        }
        else if (entity instanceof Tree.MemberOrTypeExpression) {
            return getDocumentation(((Tree.MemberOrTypeExpression) entity).getDeclaration());
        }
        else if (entity instanceof Tree.SimpleType) {
            return getDocumentation(((Tree.SimpleType) entity).getDeclarationModel());
        }
        else {
            return null;
        }
    }
    
    private static String getDocumentation(Declaration model) {
        if (model==null) {
            return null;
        }
        else {
            StringBuilder documentation = new StringBuilder();
            appendDescription(model, documentation);
            appendInheritance(documentation, model);
            appendDeclaringType(documentation, model);
            appendContainingPackage(documentation, model);
            return documentation.toString();
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
                appendContainingPackage(documentation, model);
            }
            
            appendDocAnnotationContent(decl, documentation);
        }
        return documentation.toString();
    }

    private static void appendDescription(Declaration model, StringBuilder documentation) {
        documentation.append("<p><b>")
                .append(sanitize(CeylonContentProposer.getDescriptionFor(model)))
                .append("</b></p>");
    }

    private static void appendDescription(Tree.Declaration decl, StringBuilder documentation) {
        documentation.append("<p><b>")
                .append(sanitize(CeylonLabelProvider.getLabelFor(decl)))
                .append("</b></p>");
    }

    private static void appendContainingPackage(StringBuilder documentation, Declaration model) {
        if (model.isToplevel()) {
            documentation.append("<ul><li>in ").append(getPackageLabel(model))
                    .append("</li><ul>");
        }
    }

    private static void appendDeclaringType(StringBuilder documentation, Declaration model) {
        if (model.isClassOrInterfaceMember()) {
            TypeDeclaration declaring = (TypeDeclaration) model.getContainer();
            documentation.append("<ul><li>declared by ").append(declaring.getName())
                    //.append(" - ").append(getPackageLabel(declaring))
                    .append("</li>");
            if (model.isActual()) {
                documentation.append("<li>");
                appendRefinement(documentation, model.getRefinedDeclaration());
                documentation.append("</li>");
            }
            documentation.append("</ul>");
        }
    }

    private static void appendRefinement(StringBuilder documentation, Declaration refined) {
        TypeDeclaration supertype = (TypeDeclaration) refined.getContainer();
        String spkg = supertype.getUnit().getPackage().getQualifiedNameString();
        if (spkg.isEmpty()) spkg="default package";
        documentation.append("refines '").append(CeylonContentProposer.getDescriptionFor(refined)) 
                .append("' declared by ").append(supertype.getName());
                //.append(" - ").append(getPackageLabel(refined));
    }

    private static void appendInheritance(StringBuilder documentation, Declaration model) {
        if (model instanceof Class) {
            ProducedType extended = ((Class) model).getExtendedType();
            if (extended!=null) {
                documentation.append("<ul><li>extends ")
                        .append(sanitize(extended.getProducedTypeName()));
                        //.append(" - ").append(getPackageLabel(extended.getDeclaration())).append("</li></ul>");
            }
        }
        if (model instanceof TypeDeclaration) {
            List<ProducedType> types = ((TypeDeclaration) model).getSatisfiedTypes();
            if (!types.isEmpty()) {
                documentation.append("<ul>");
                for (ProducedType satisfied : types) {
                    documentation.append("<li>satisfies ")
                            .append(sanitize(satisfied.getProducedTypeName()));
                            //.append(" - ").append(getPackageLabel(satisfied.getDeclaration())).append("</li>");
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
                if (annotPrim instanceof BaseMemberExpression)
                {
                    String name = ((BaseMemberExpression) annotPrim).getIdentifier().getText();
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
    
    private static String sanitize(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
    
    static String getRefinementDocumentation(Declaration refined) {
        StringBuilder result = new StringBuilder();
        appendRefinement(result, refined);
        return result.toString();
    }
    
}
