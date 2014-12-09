package com.redhat.ceylon.eclipse.code.resolve;

import static java.lang.Character.isJavaIdentifierPart;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;

import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.JavaSearch;

public class JavaToCeylonHyperlinkDetector extends AbstractHyperlinkDetector {

    private static final class JavaToCeylonLink implements IHyperlink {
        private final IRegion region;
        private final IDocument doc;
        private final IProject project;
        private final IJavaElement javaElement;

        private JavaToCeylonLink(IRegion region, IDocument doc, 
                IProject project, IJavaElement javaElement) {
            this.region = region;
            this.doc = doc;
            this.project = project;
            this.javaElement = javaElement;
        }

        @Override
        public void open() {
            Navigation.gotoCeylonDeclaration(project, javaElement);
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
                while (isJavaIdentifierPart(doc.getChar(offset-1))) {
                    offset--;
                }
                while (isJavaIdentifierPart(doc.getChar(offset+length))) {
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
                    EditorUtil.getCurrentEditor().getEditorInput();
            ICompilationUnit cu = (ICompilationUnit) 
                    JavaCore.create(EditorUtil.getFile(editorInput));
            if (cu==null) return null;
            IJavaElement[] selection = 
                    cu.codeSelect(region.getOffset(), region.getLength());
            for (final IJavaElement javaElement: selection) {
                final IProject project = javaElement.getJavaProject().getProject();

                if (JavaSearch.isCeylonDeclaration(javaElement) && !(javaElement instanceof IPackageFragment)) {
                    return new IHyperlink[] {
                            new JavaToCeylonLink(region, doc, project, javaElement)
                        };
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
