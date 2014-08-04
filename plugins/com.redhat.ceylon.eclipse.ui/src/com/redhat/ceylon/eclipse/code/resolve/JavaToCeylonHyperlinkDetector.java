package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonClassesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getActivePage;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getEditorInput;
import static java.lang.Character.isJavaIdentifierPart;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Nodes;

public class JavaToCeylonHyperlinkDetector extends AbstractHyperlinkDetector {

    private static final class JavaToCeylonLink implements IHyperlink {
        private final IRegion region;
        private final IDocument doc;
        private final IProject p;
        private final IJavaElement je;

        private JavaToCeylonLink(IRegion region, IDocument doc, IProject p,
                IJavaElement je) {
            this.region = region;
            this.doc = doc;
            this.p = p;
            this.je = je;
        }

        @Override
        public void open() {
            for (PhasedUnit pu: getProjectTypeChecker(p).getPhasedUnits().getPhasedUnits()) {
                for (Declaration d: pu.getDeclarations()) {
                    //TODO: the following is not quite right because
                    //      there can be multiple declarations with
                    //      the same (unqualified) name in a unit
                    if (d.getName().equals(je.getElementName())) {
                        IEditorInput editorInput = getEditorInput(p.findMember(pu.getUnitFile().getPath()));
                        try {
                            CeylonEditor editor = (CeylonEditor) getActivePage().openEditor(editorInput, EDITOR_ID);
                            int offset = Nodes.getIdentifyingNode(Nodes.getReferencedNode(d, pu.getCompilationUnit())).getStartIndex();
                            editor.selectAndReveal(offset, 0);
                        } 
                        catch (PartInitException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
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
            ICompilationUnit cu = (ICompilationUnit) JavaCore.create(EditorUtil.getFile(EditorUtil.getCurrentEditor().getEditorInput()));
            if (cu==null) return null;
            IJavaElement[] selection = cu.codeSelect(region.getOffset(), region.getLength());
            for (final IJavaElement je: selection) {
                final IProject p = je.getJavaProject().getProject();
                if (isExplodeModulesEnabled(p)) {
                    if (getCeylonClassesOutputFolder(p).getFullPath()
                            .isPrefixOf(je.getPath())) {
                        return new IHyperlink[] { new JavaToCeylonLink(region, doc, p, je) };
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
