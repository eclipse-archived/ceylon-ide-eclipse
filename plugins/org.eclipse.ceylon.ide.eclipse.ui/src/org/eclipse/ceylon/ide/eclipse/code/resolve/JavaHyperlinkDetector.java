package org.eclipse.ceylon.ide.eclipse.code.resolve;

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.getJavaElement;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedModel;
import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toJavaString;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.getNativeDeclaration;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.getNativeHeader;
import static org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.openInEditor;
import static org.eclipse.jdt.internal.ui.javaeditor.EditorUtility.revealInEditor;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import org.eclipse.ceylon.common.Backend;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.common.model.CeylonBinaryUnit;
import org.eclipse.ceylon.ide.common.model.EditedSourceFile;
import org.eclipse.ceylon.ide.common.model.ExternalSourceFile;
import org.eclipse.ceylon.ide.common.model.IJavaModelAware;
import org.eclipse.ceylon.ide.common.model.ProjectSourceFile;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.Unit;

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
            return "Declaration \u2014 native Java";
        }

        @Override
        public IRegion getHyperlinkRegion() {
            return new Region(id.getStartIndex(), id.getDistance());
        }
    }

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer tv, IRegion region,
            boolean csmh) {
        if (pc==null) {
            return null;
        }
        Tree.CompilationUnit rootNode = 
                pc.getLastCompilationUnit();
        if (rootNode==null) {
            return null;
        }
        else {
            Node node = 
                    findNode(rootNode, 
                            pc.getTokens(), 
                            region.getOffset(), 
                            region.getOffset() +
                            region.getLength());
            if (node==null) {
                return null;
            }
            else {
                Node id = getIdentifyingNode(node);
                Referenceable ref = getReferencedModel(node);
                if (ref instanceof Declaration) {
                    Declaration dec = (Declaration) ref;
                    Unit declarationUnit = dec.getUnit();
                    
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
                                    ProjectSourceFile projectSourceFile = 
                                            ((EditedSourceFile)declarationUnit).getOriginalSourceFile();
                                    if (projectSourceFile != null) {

                                        Declaration modelDeclaration = null;
                                        Declaration modelHeaderDeclaration = 
                                                getNativeHeader(projectSourceFile.getPackage(), 
                                                        dec.getName());
                                        if (modelHeaderDeclaration != null) {
                                            List<Declaration> overloads = 
                                                    modelHeaderDeclaration.getOverloads();
                                            if (overloads != null) {
                                                for (Declaration packageDeclarationOverload: overloads) {
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
                                
                                Declaration javaOverload = 
                                        getNativeDeclaration(dec, 
                                                Backend.Java.asSet());
                                if (javaOverload != null 
                                        && javaOverload.getUnit() 
                                            instanceof IJavaModelAware) {
                                    dec = javaOverload;
                                    declarationUnit = dec.getUnit();
                                    hasFoundAJavaImplementation = true;
                                }
                                if (!hasFoundAJavaImplementation) {
                                    return null;
                                }
                            }
                        }
                    }

                    if (declarationUnit instanceof CeylonBinaryUnit) {
                        CeylonBinaryUnit ceylonBinaryUnit = 
                                (CeylonBinaryUnit) declarationUnit;
                        String path = toJavaString(ceylonBinaryUnit.getSourceRelativePath());
                        if (! JavaCore.isJavaLikeFileName(path)) {
                            return null; 
                        }
                    }

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
                else {
                    return null;
                }
            }
        }
    }
}
