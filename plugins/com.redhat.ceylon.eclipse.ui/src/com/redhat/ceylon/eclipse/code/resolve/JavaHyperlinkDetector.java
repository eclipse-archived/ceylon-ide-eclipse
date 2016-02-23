package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.getJavaElement;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedDeclaration;
import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;
import static org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.openInEditor;
import static org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.revealInEditor;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.ide.common.model.CeylonBinaryUnit;
import com.redhat.ceylon.ide.common.model.EditedSourceFile;
import com.redhat.ceylon.ide.common.model.ExternalSourceFile;
import com.redhat.ceylon.ide.common.model.IJavaModelAware;
import com.redhat.ceylon.ide.common.model.ProjectSourceFile;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Unit;

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
            return new Region(id.getStartIndex(), id.getDistance());
        }
    }

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer tv, IRegion region,
            boolean csmh) {
        if (pc==null||pc.getLastCompilationUnit()==null) {
            return null;
        }
        else {
            Node node = 
                    findNode(pc.getLastCompilationUnit(), 
                            pc.getTokens(), 
                            region.getOffset(), 
                            region.getOffset()+region.getLength());
            if (node==null) {
                return null;
            }
            else {
                Node id = getIdentifyingNode(node);
                Referenceable ref = getReferencedDeclaration(node);
                if (ref instanceof Declaration) {
                    Declaration dec = (Declaration) ref;
                    Unit declarationUnit = dec.getUnit();
                    IJavaProject jp = JavaCore.create(pc.getProject());
                    
                    if (!(declarationUnit instanceof IJavaModelAware)) {
                        if (declarationUnit instanceof ExternalSourceFile) {
                            final ExternalSourceFile externalSourceFile = 
                                    (ExternalSourceFile)declarationUnit;
                            Declaration binaryDeclaration = 
                                    externalSourceFile.retrieveBinaryDeclaration(dec);
                            if (binaryDeclaration != null) {
                                dec = binaryDeclaration;
                                declarationUnit = binaryDeclaration.getUnit();
                            }
                            else {
                                return null;
                            }
                        }
                        else {
                            boolean hasFoundAJavaImplementation = false;
                            if (dec.isNative()) {
                                if (declarationUnit instanceof EditedSourceFile) {
                                    ProjectSourceFile<IProject, IResource, IFolder, IFile> projectSourceFile = ((EditedSourceFile)declarationUnit).getOriginalSourceFile();
                                    if (projectSourceFile != null) {

                                        Declaration modelDeclaration = null;
                                        Declaration modelHeaderDeclaration = ModelUtil.getNativeHeader(projectSourceFile.getPackage(), dec.getName());
                                        if (modelHeaderDeclaration != null) {
                                            List<Declaration> overloads = modelHeaderDeclaration.getOverloads();
                                            if (overloads != null) {
                                                for (Declaration packageDeclarationOverload : overloads) {
                                                    if (packageDeclarationOverload.equals(dec)) {
                                                        modelDeclaration = packageDeclarationOverload;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        
                                        if (modelDeclaration != null) {
                                            dec = modelDeclaration;
                                            declarationUnit = projectSourceFile;
                                        }
                                    }
                                }
                                
                                Declaration javaOverload = ModelUtil.getNativeDeclaration(dec, Backend.Java.asSet());
                                if (javaOverload != null) {
                                    if (javaOverload.getUnit() instanceof IJavaModelAware) {
                                        dec = javaOverload;
                                        declarationUnit = dec.getUnit();
                                        jp = ((IJavaModelAware<IProject, ITypeRoot, IJavaElement>)declarationUnit).getTypeRoot().getJavaProject();
                                        hasFoundAJavaImplementation = true;
                                    }
                                }
                                if (!hasFoundAJavaImplementation) {
                                    return null;
                                }
                            }
                        }
                    }
                    else {
                        final IJavaModelAware<IProject, ITypeRoot, IJavaElement> havaModelAware = 
                                (IJavaModelAware<IProject, ITypeRoot, IJavaElement>)declarationUnit;
                        jp = havaModelAware.getTypeRoot().getJavaProject();
                    }
                    if (declarationUnit instanceof CeylonBinaryUnit) {
                        CeylonBinaryUnit<IProject,ITypeRoot,IJavaElement> ceylonBinaryUnit = 
                                (CeylonBinaryUnit<IProject,ITypeRoot,IJavaElement>) declarationUnit;
                        String path = toJavaString(ceylonBinaryUnit.getSourceRelativePath());
                        if (! JavaCore.isJavaLikeFileName(path)) {
                            return null; 
                        }
                        jp = ceylonBinaryUnit.getTypeRoot().getJavaProject();
                    }
                    if (jp==null) {
                        return null;
                    }
                    else {
                        try {
                            IJavaElement element = getJavaElement(dec);
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
                else {
                    return null;
                }
            }
        }
    }
}
