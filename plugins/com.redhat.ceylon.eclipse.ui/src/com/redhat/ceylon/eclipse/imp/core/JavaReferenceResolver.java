package com.redhat.ceylon.eclipse.imp.core;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;

import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class JavaReferenceResolver implements IHyperlinkDetector {

    private CeylonEditor editor;
    
    public JavaReferenceResolver(CeylonEditor editor) {
        this.editor = editor;
    }
    
    private final class JavaElementLink implements IHyperlink {
        private final IJavaElement elem;
        private final Node id;

        private JavaElementLink(IJavaElement elem, Node id) {
            this.elem = elem;
            this.id = id;
        }

        @Override
        public void open() {
            try {
                IEditorPart part = EditorUtility.openInEditor(elem, true);
                if(part!=null) {
                    EditorUtility.revealInEditor(part, elem);
                }
            } 
            catch (PartInitException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return null;
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), 
                    id.getStopIndex()-id.getStartIndex()+1);
        }
    }

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer tv, IRegion region,
            boolean csmh) {
        CeylonParseController pc = editor.getParseController();
        if (pc==null||pc.getRootNode()==null) {
            return null;
        }
        else {
            Node node = findNode(pc.getRootNode(), region.getOffset(), 
                    region.getOffset()+region.getLength());
            if (node==null) {
                return null;
            }
            else {
                final Node id = getIdentifyingNode(node);
                Declaration dec = getReferencedDeclaration(node);
                if (dec==null) {
                    return null;
                }
                else {
                    IJavaProject jp = JavaCore.create(Util.getProject(editor));
                    if (jp==null) {
                        return null;
                    }
                    else {
                        try {
                            IJavaElement element = getJavaElement(dec, jp, node);
                            if (element==null) {
                                return null;
                            }
                            else {
                                final IJavaElement elem = element;
                                return new IHyperlink[] { new JavaElementLink(elem, id) };
                            }
                        }
                        catch (JavaModelException jme) {
                            jme.printStackTrace();
                            return null;
                        }
                    }
                }
            }
        }
    }

    private static IType findType(IJavaProject jp, String fullyQualifiedName) throws JavaModelException {
        JDTModelLoader modelLoader = CeylonBuilder.getProjectModelLoader(jp.getProject());
        if (modelLoader.getSourceDeclarations().contains(fullyQualifiedName)) {
            return null;
        }
        return jp.findType(fullyQualifiedName);
    }
    
    public static IJavaElement getJavaElement(Declaration dec, IJavaProject jp, Node node)
            throws JavaModelException {
        if (dec instanceof TypeDeclaration) {
            IType type = findType(jp, dec.getQualifiedNameString());
            if (type==null) {
                return null;
            }
            else {
                if (node instanceof Tree.MemberOrTypeExpression &&
                        dec instanceof Class && 
                        ((Class) dec).getParameterList()!=null) {
                    for (IMethod method: type.getMethods()) {
                        if (method.isConstructor()) {
                            if (((Class) dec).getParameterList().getParameters().size()==
                                                method.getNumberOfParameters()) {
                                return method;
                            }
                        }
                    }
                }
                return type;
            }
        }
        else {
            IType type = findType(jp, dec.getContainer().getQualifiedNameString());
            if (type==null) {
                return null;
            }
            else {
                for (IMethod method: type.getMethods()) {
                    if (dec instanceof Value) {
                        if (("get" + dec.getName()).equalsIgnoreCase(method.getElementName())) {
                            return method;
                        }
                    }
                    else if (dec instanceof Method) {
                        if (!method.isConstructor()) {
                            //TODO: some kind of half-assed attempt to match up
                            //      the parameter types for overloaded methods?
                            List<ParameterList> pls = ((Method) dec).getParameterLists();
                            if (dec.getName().equalsIgnoreCase(method.getElementName()) &&
                                    !pls.isEmpty() && pls.get(0).getParameters().size()==
                                                method.getNumberOfParameters()) {
                                return method;
                            }
                        }
                    }
                }
                for (IField field: type.getFields()) {
                    if ((dec.getName()).equalsIgnoreCase(field.getElementName())) {
                        return field;
                    }
                }
                return type;
            }
        }
    }

}
