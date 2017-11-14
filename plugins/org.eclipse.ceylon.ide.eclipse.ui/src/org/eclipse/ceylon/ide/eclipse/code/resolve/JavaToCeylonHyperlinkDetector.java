/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.resolve;

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoDeclaration;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getFile;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.toCeylonDeclaration;
import static java.lang.Character.isJavaIdentifierPart;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.ceylon.ide.eclipse.util.JavaSearch;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

public class JavaToCeylonHyperlinkDetector extends AbstractHyperlinkDetector {

    private static final class JavaToCeylonLink implements IHyperlink {
        private final IRegion region;
        private final IDocument doc;
        private final Declaration ceylonDeclaration;

        private JavaToCeylonLink(IRegion region, IDocument doc, 
                Declaration ceylonDeclaration) {
            this.region = region;
            this.doc = doc;
            this.ceylonDeclaration = ceylonDeclaration;
        }

        @Override
        public void open() {
            gotoDeclaration(ceylonDeclaration);
        }
        
        @Override
        public String getTypeLabel() {
            return null;
        }

        @Override
        public String getHyperlinkText() {
            return "Open Ceylon Declaration";
        }

        @Override
        public IRegion getHyperlinkRegion() {
            int offset = region.getOffset();
            int length = region.getLength();
            try {
                while (isJavaIdentifierPart(
                        doc.getChar(offset-1))) {
                    offset--;
                }
                while (isJavaIdentifierPart(
                        doc.getChar(offset+length))) {
                    length++;
                }
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
            return new Region(offset, length);
        }
    }

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
            final IRegion region, boolean canShowMultipleHyperlinks) {
        try {
            final IDocument doc = textViewer.getDocument();
            IEditorInput editorInput = 
                    getCurrentEditor().getEditorInput();
            ICodeAssist ca = null;
            
            if (editorInput instanceof FileEditorInput) {
                IFile file = getFile(editorInput);
                ca = (ICodeAssist) JavaCore.create(file);
            }
            else if (editorInput instanceof IClassFileEditorInput) {
                IClassFileEditorInput cfei = 
                        (IClassFileEditorInput) editorInput;
                ca = cfei.getClassFile();
            }
            
                    
            if (ca==null) return null;
            IJavaElement[] selection = 
                    ca.codeSelect(region.getOffset(), 
                            region.getLength());
            for (IJavaElement javaElement: selection) {
                if (JavaSearch.isCeylonDeclaration(javaElement) && 
                        !(javaElement instanceof IPackageFragment)) {
                    IProject project = 
                            javaElement.getJavaProject()
                                .getProject();
                    Declaration declaration = 
                            toCeylonDeclaration(project, 
                                    javaElement);
                    if (declaration != null) {
                        return new IHyperlink[] {
                                new JavaToCeylonLink(region, 
                                        doc, declaration)
                            };
                    }
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
