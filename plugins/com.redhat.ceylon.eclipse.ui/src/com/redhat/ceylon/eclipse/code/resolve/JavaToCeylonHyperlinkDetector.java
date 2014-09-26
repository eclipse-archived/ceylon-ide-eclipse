package com.redhat.ceylon.eclipse.code.resolve;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonClassesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getProjectAndReferencedProjects;
import static com.redhat.ceylon.eclipse.util.JavaSearch.isDeclarationOfLinkedElement;
import static java.lang.Character.isJavaIdentifierPart;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;

import java.util.HashSet;
import java.util.Set;

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

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.util.EditorUtil;

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
        Set<String> searchedArchives = new HashSet<String>();
        for (IProject referencedProject: getProjectAndReferencedProjects(project)) {
            if (CeylonNature.isEnabled(referencedProject)) {
                TypeChecker typeChecker = getProjectTypeChecker(referencedProject);
                if (typeChecker!=null) {
                    for (PhasedUnit pu: typeChecker.getPhasedUnits().getPhasedUnits()) {
                        for (Declaration declaration: pu.getDeclarations()) {
                            if (isDeclarationOfLinkedElement(declaration, javaElement)) {
                                gotoDeclaration(project, pu, declaration);
                            }
                        }
                    }
                    Modules modules = typeChecker.getContext().getModules();
                    for (Module m: modules.getListOfModules()) {
                        if (m instanceof JDTModule) {
                            JDTModule module = (JDTModule) m;
                            if (module.isCeylonArchive() && module.getArtifact()!=null) { 
                                String archivePath = module.getArtifact().getAbsolutePath();
                                if (searchedArchives.add(archivePath) && 
                                        belongsToModule(javaElement, module)) {
                                    for (PhasedUnit pu: module.getPhasedUnits()) {
                                        for (Declaration declaration: pu.getDeclarations()) {
                                            if (isDeclarationOfLinkedElement(declaration, javaElement)) {
                                                gotoDeclaration(project, pu, declaration);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean belongsToModule(IJavaElement javaElement,
            JDTModule module) {
        return javaElement.getAncestor(PACKAGE_FRAGMENT).getElementName()
                .startsWith(module.getNameAsString());
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
                if (isExplodeModulesEnabled(project)) {
                    if (javaElement.getPath().getFileExtension().equals("car") ||
                            getCeylonClassesOutputFolder(project).getFullPath()
                                .isPrefixOf(javaElement.getPath())) {
                        return new IHyperlink[] {
                                new JavaToCeylonLink(region, doc, project, javaElement)
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
