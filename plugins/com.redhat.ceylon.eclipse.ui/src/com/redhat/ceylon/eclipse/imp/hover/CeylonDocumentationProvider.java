package com.redhat.ceylon.eclipse.imp.hover;

import static com.redhat.ceylon.eclipse.imp.core.JavaReferenceResolver.getJavaElement;
import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.getPackageLabel;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;

public class CeylonDocumentationProvider implements IDocumentationProvider {
    
    public String getDocumentation(Object entity, IParseController ctlr) {
        IProject proj = ((CeylonParseController) ctlr).getProject().getRawProject();
        if (entity instanceof Tree.Declaration) {
            return getDocumentation((Tree.Declaration) entity);
        }
        //its coming from a binary or java project:
        else if (entity instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression node = (Tree.MemberOrTypeExpression) entity;
            return getDocumentation(node.getDeclaration(), proj, node);
        }
        else if (entity instanceof Tree.SimpleType) {
            Tree.SimpleType node = (Tree.SimpleType) entity;
            return getDocumentation(node.getDeclarationModel(), proj, node);
        }
        else if (entity instanceof Tree.ImportMemberOrType) {
            Tree.ImportMemberOrType node = (Tree.ImportMemberOrType) entity;
            return getDocumentation(node.getDeclarationModel(), proj, node);
        }
        else if (entity instanceof Tree.NamedArgument) {
            Tree.NamedArgument node = (Tree.NamedArgument) entity;
            return getDocumentation(node.getParameter(), proj, node);
        } 
        else {
            return null;
        }
    }
    
    private static String getDocumentation(Declaration model, IProject project, Node node) {
        if (model==null) {
            return null;
        }
        else {
            StringBuilder documentation = new StringBuilder();
            appendDescription(model, documentation);
            appendInheritance(documentation, model);
            appendDeclaringType(documentation, model);
            appendContainingPackage(documentation, model);
            appendJavadoc(model, project, documentation, node);
            return documentation.toString();
        }
    }

    private static void appendJavadoc(Declaration model, IProject project,
            StringBuilder documentation, Node node) {
        IJavaProject jp = JavaCore.create(project);
        if (jp!=null) {
            try {
                IJavaElement je = getJavaElement(model, jp, node);
                if (je!=null) {
                    String javadoc = je.getAttachedJavadoc(new NullProgressMonitor());
                    if (javadoc!=null) {
                        documentation.append("<br/>" + javadoc);
                    }
                }
            }
            catch (JavaModelException jme) {
                jme.printStackTrace();
            }
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
            documentation.append("<ul><li>declared by ").append(declaring.getName());
            if (declaring.isToplevel()) {
                documentation.append(" in ").append(getPackageLabel(declaring));
            }
            documentation.append("</li>");
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
        if (refined.isToplevel()) {
            documentation.append(" in ").append(getPackageLabel(refined));
        }
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
                            documentation.append("<br/><p>" + docLine.subSequence(1, docLine.length()-1) + "</p>");
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
