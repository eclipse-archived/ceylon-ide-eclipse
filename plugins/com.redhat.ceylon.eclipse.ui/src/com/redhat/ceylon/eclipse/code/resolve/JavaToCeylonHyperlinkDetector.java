package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonClassesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.EDITOR_ID;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getActivePage;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getEditorInput;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getProjectAndReferencedProjects;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static java.lang.Character.isJavaIdentifierPart;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
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
            openCeylonDeclaration(project, javaElement);
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

    private static void openCeylonDeclaration(IProject project, IJavaElement javaElement) {
        Collection<IProject> projects = getProjects();
        for (IProject referencedProject: getProjectAndReferencedProjects(project)) {
            if (projects.contains(referencedProject)) {
                TypeChecker typeChecker = getProjectTypeChecker(referencedProject);
                if (typeChecker!=null) {
                    for (PhasedUnit pu: typeChecker.getPhasedUnits().getPhasedUnits()) {
                        for (Declaration declaration: pu.getDeclarations()) {
                            if (JavaSearch.isDeclarationOfLinkedElement(declaration, javaElement)) {
                                openCeylonDeclaration(project, pu, declaration);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void openCeylonDeclaration(IProject project, PhasedUnit pu,
            Declaration declaration) {
        IResource file = project.findMember(pu.getUnitFile().getPath());
        IEditorInput editorInput = getEditorInput(file);
        try {
            CeylonEditor editor = (CeylonEditor) 
                    getActivePage().openEditor(editorInput, EDITOR_ID);
            Node node = getReferencedNode(declaration, pu.getCompilationUnit());
            editor.selectAndReveal(getIdentifyingNode(node).getStartIndex(), 
                    declaration.getName().length());
        } 
        catch (PartInitException e) {
            e.printStackTrace();
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
            for (final IJavaElement je: selection) {
                final IProject p = je.getJavaProject().getProject();
                if (isExplodeModulesEnabled(p)) {
                    if (getCeylonClassesOutputFolder(p).getFullPath()
                            .isPrefixOf(je.getPath())) {
                        return new IHyperlink[] { 
                                new JavaToCeylonLink(region, doc, p, je)
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
