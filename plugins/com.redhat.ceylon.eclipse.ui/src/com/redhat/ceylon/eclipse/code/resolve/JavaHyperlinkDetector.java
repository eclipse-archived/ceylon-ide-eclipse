package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModelLoader;
import static org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.openInEditor;
import static org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.revealInEditor;

import java.util.List;
import java.util.Stack;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.loader.model.JavaBeanValue;
import com.redhat.ceylon.compiler.loader.model.JavaMethod;
import com.redhat.ceylon.compiler.loader.model.LazyClass;
import com.redhat.ceylon.compiler.loader.model.LazyInterface;
import com.redhat.ceylon.compiler.loader.model.LazyMethod;
import com.redhat.ceylon.compiler.loader.model.LazyValue;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.IJavaModelAware;
import com.redhat.ceylon.eclipse.core.model.loader.IBindingProvider;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModule;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;

public class JavaHyperlinkDetector implements IHyperlinkDetector {

    private CeylonParseController pc;
    
    public JavaHyperlinkDetector(CeylonParseController pc) {
        this.pc = pc;
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
                IEditorPart part = openInEditor(elem, true);
                if (part!=null) {
                    revealInEditor(part, elem);
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
            return "Java Declaration";
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
                Node id = getIdentifyingNode(node);
                Declaration dec = getReferencedDeclaration(node);
                if (dec==null) {
                    return null;
                }
                else {
                    Unit declarationUnit = dec.getUnit();
                    IJavaProject jp = JavaCore.create(pc.getProject());
                    
                    if (! (declarationUnit instanceof IJavaModelAware)) {
                        if (declarationUnit instanceof ExternalSourceFile) {
                            Declaration binaryDeclaration = null;
                            JDTModule module = ((ExternalSourceFile) declarationUnit).getModule();
                            if (module.isCeylonBinaryArchive()) {
                                if (declarationUnit.getPackage() instanceof SingleSourceUnitPackage) {
                                    SingleSourceUnitPackage sourceUnitPackage = (SingleSourceUnitPackage) declarationUnit.getPackage();
                                    Package binaryPackage = sourceUnitPackage.getModelPackage();
                                    Stack<Declaration> ancestors = new Stack<>();
                                    Scope container = dec.getContainer();
                                    while (container instanceof Declaration) {
                                        Declaration ancestor = (Declaration) container;
                                        ancestors.push(ancestor);
                                        container = ancestor.getContainer();
                                    }
                                    if (container.equals(sourceUnitPackage)) {
                                        Scope curentBinaryScope = binaryPackage;
                                        while (! ancestors.isEmpty()) {
                                            Declaration binaryAncestor = curentBinaryScope.getDirectMember(ancestors.pop().getName(), null, false);
                                            if (binaryAncestor instanceof Value) {
                                                binaryAncestor = ((Value) binaryAncestor).getTypeDeclaration();
                                            }
                                            if (binaryAncestor instanceof Scope) {
                                                curentBinaryScope = (Scope) binaryAncestor;
                                            } else {
                                                break;
                                            }
                                        }
                                        if (curentBinaryScope != null) {
                                            binaryDeclaration = curentBinaryScope.getDirectMember(dec.getName(), null, false);
                                        }
                                    }
                                }
                            }
                            if (binaryDeclaration != null) {
                                dec = binaryDeclaration;
                                declarationUnit = binaryDeclaration.getUnit();
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                    if (declarationUnit instanceof CeylonBinaryUnit) {
                        CeylonBinaryUnit ceylonBinaryUnit = (CeylonBinaryUnit) declarationUnit;
                        if (! JavaCore.isJavaLikeFileName(ceylonBinaryUnit.getSourceRelativePath())) {
                            return null; 
                        }
                        jp = ceylonBinaryUnit.getJavaElement().getJavaProject();
                    }
                    //IJavaProject jp = JavaCore.create(Util.getProject(editor));
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
                                return new IHyperlink[] { new JavaElementLink(element, id) };
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

    public static void gotoJavaNode(Declaration dec, CeylonParseController cpc) {
    	gotoJavaNode(dec, cpc.getProject());
    }
    
    public static void gotoJavaNode(Declaration dec, IProject project) {
    	gotoJavaNode(dec, null, project);
    }
    
    public static void gotoJavaNode(Declaration dec, Node node, IProject project) {
		IJavaProject jp = JavaCore.create(project);
		if (jp!=null) {
		    try {
		        IJavaElement element = getJavaElement(dec, jp, node);
		        if (element!=null) {
		            IEditorPart part = openInEditor(element, true);
		            if (part!=null) {
		                revealInEditor(part, element);
		            }
		        }
		    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}

	private static IType findType(IJavaProject jp, String fullyQualifiedName) 
    		throws JavaModelException {
	    if (fullyQualifiedName==null) {
	        return null;
	    }
        JDTModelLoader modelLoader = getProjectModelLoader(jp.getProject());
        if (modelLoader==null) {
            return null;
        }
        String javaQualifiedName = fullyQualifiedName.replace("::", ".");
        if (modelLoader.getSourceDeclarations().contains(javaQualifiedName)) {
            return null;
        }
        return jp.findType(javaQualifiedName);
    }
    
    public static IJavaElement getJavaElement(final Declaration dec, IJavaProject jp, Node node)
            throws JavaModelException {
        if (dec.getUnit() instanceof IJavaModelAware) {
            ITypeRoot typeRoot = ((IJavaModelAware) dec.getUnit()).getJavaElement();
            ASTParser parser = ASTParser.newParser(AST.JLS4);
            parser.setBindingsRecovery(true);
            parser.setResolveBindings(true);
            parser.setSource(typeRoot);
            class MatchingASTVisitor extends ASTVisitor {
                IBinding declarationBinding = null;
                private boolean declarationMatched(IBinding currentBinding,
                        IBindingProvider mirror) {
                    if (mirror != null && CharOperation.equals(currentBinding.getKey().toCharArray(), mirror.getBindingKey())) {
                        declarationBinding = currentBinding;
                        return true;
                    }
                    return false;
                }
                @Override
                public boolean visit(MethodDeclaration node) {
                    IBindingProvider mirror = null;
                    if (dec instanceof LazyMethod) {
                        mirror = (IBindingProvider) ((LazyMethod) dec).classMirror;
                    }
                    if (dec instanceof JavaMethod) {
                        mirror = (IBindingProvider) ((JavaMethod) dec).mirror;
                    }
                    if (declarationMatched(node.resolveBinding(), mirror)) {
                        return false;
                    }

                    return super.visit(node);
                }
                @Override
                public boolean visit(org.eclipse.jdt.core.dom.TypeDeclaration node) {
                    IBindingProvider mirror = null;
                    if (dec instanceof LazyClass) {
                        mirror = (IBindingProvider) ((LazyClass) dec).classMirror; 
                    }
                    if (dec instanceof LazyInterface) {
                        mirror = (IBindingProvider) ((LazyInterface) dec).classMirror; 
                    }                    
                    if (dec instanceof LazyValue) {
                        mirror = (IBindingProvider) ((LazyValue) dec).classMirror; 
                    }
                    if (dec instanceof JavaBeanValue) {
                        JavaBeanValue javaBeanValue = ((JavaBeanValue) dec);
                        Scope container = javaBeanValue.getContainer();
                        if (container instanceof LazyClass) {
                            mirror = (IBindingProvider) ((LazyClass) container).classMirror;
                        }
                        if (container instanceof LazyInterface) {
                            mirror = (IBindingProvider) ((LazyInterface) container).classMirror;
                        }
                        if (dec instanceof LazyValue) {
                            mirror = (IBindingProvider) ((LazyValue) container).classMirror; 
                        }
                        if (declarationMatched(node.resolveBinding(), mirror)) {
                            for (MethodDeclaration methodDecl : node.getMethods()) {
                                if (methodDecl.getName().toString().equals(javaBeanValue.getGetterName())) {
                                    declarationBinding = methodDecl.resolveBinding();
                                    return false;
                                }
                            }
                        }
                        return super.visit(node);
                    }
                    if (declarationMatched(node.resolveBinding(), mirror)) {
                        return false;
                    }
                    return super.visit(node);
                }
                @Override
                public boolean visit(org.eclipse.jdt.core.dom.EnumDeclaration node) {
                    IBindingProvider mirror = null;
                    if (dec instanceof LazyClass) {
                        mirror = (IBindingProvider) ((LazyClass) dec).classMirror; 
                    }
                    if (declarationMatched(node.resolveBinding(), mirror)) {
                        return false;
                    }
                    return super.visit(node);
                }
                @Override
                public boolean visit(org.eclipse.jdt.core.dom.AnnotationTypeDeclaration node) {
                    IBindingProvider mirror = null;
                    if (dec instanceof LazyClass) {
                        mirror = (IBindingProvider) ((LazyClass) dec).classMirror; 
                    }
                    if (declarationMatched(node.resolveBinding(), mirror)) {
                        return false;
                    }
                    return super.visit(node);
                }
            }
            MatchingASTVisitor matchingVisitor = new MatchingASTVisitor();            
            parser.createAST(null).accept(matchingVisitor);
            if (matchingVisitor.declarationBinding != null) {
                return matchingVisitor.declarationBinding.getJavaElement();
            }
        }
            
        if (dec instanceof TypeDeclaration || 
                (dec.isToplevel() && (dec instanceof LazyValue || 
                 dec instanceof LazyMethod))) {
            IType type = findType(jp, dec.getQualifiedNameString());
            if (type==null) {
                if (! (dec instanceof TypeDeclaration)) {
                    type = findType(jp, dec.getQualifiedNameString() + "_");
                }
            }
            
            if (type==null) {
                return null;
            } else {
                if (node instanceof Tree.MemberOrTypeExpression &&
                        dec instanceof Class && 
                        ((Class) dec).getParameterList()!=null) {
                    for (IMethod method: type.getMethods()) {
                        if (method.isConstructor() && !Flags.isPrivate(method.getFlags())) {
                            //TODO: correctly resolve overloaded constructors
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
                    String methodName = method.getElementName();
                    if (dec instanceof Value && method.getParameters().length==0) {
                        if (("get" + dec.getName()).equalsIgnoreCase(methodName)||
                            ("is" + dec.getName()).equalsIgnoreCase(methodName)) {
                            return method;
                        }
                    }
                    else if (dec instanceof Method) {
                        if (!method.isConstructor() && !Flags.isPrivate(method.getFlags())) {
                            //TODO: correctly resolve overloaded methods
                            List<ParameterList> pls = ((Method) dec).getParameterLists();
                            if (dec.getName().equalsIgnoreCase(methodName) &&
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
