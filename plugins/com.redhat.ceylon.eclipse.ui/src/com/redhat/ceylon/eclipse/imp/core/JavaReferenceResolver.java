package com.redhat.ceylon.eclipse.imp.core;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;

import org.eclipse.jdt.core.IJavaProject;
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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class JavaReferenceResolver implements IHyperlinkDetector {

    private CeylonEditor editor;
    
    public JavaReferenceResolver(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer tv, IRegion region,
            boolean csmh) {
        CeylonParseController pc = editor.getParseController();
        if (pc==null) {
            return null;
        }
        else {
            Node node = findNode(pc.getRootNode(), region.getOffset(), 
                    region.getOffset()+region.getLength());
            if (node==null) {
                return null;
            }
            else {
                final Node id = CeylonReferenceResolver.getIdentifyingNode(node);
                Declaration dec = getReferencedDeclaration(node);
                if (dec==null) {
                    return null;
                }
                else {
                    IJavaProject jp = JavaCore.create(Util.getProject(editor));//dec.getUnit().getPackage().getModule();
                    if (jp==null) {
                        return null;
                    }
                    else {
                        try {
                            final IType type = jp.findType(dec.getQualifiedNameString());
                            if (type==null) {
                                return null;
                            }
                            else {
                                return new IHyperlink[] { new IHyperlink() {
                                    @Override
                                    public void open() {
                                        try {
                                            IEditorPart part = EditorUtility.openInEditor(type, true);
                                            if(part!=null) {
                                                EditorUtility.revealInEditor(part, type);
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
                                } };
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

}
