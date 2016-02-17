package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.core.model.modelJ2C;
import com.redhat.ceylon.eclipse.java2ceylon.CorrectJ2C;
import com.redhat.ceylon.ide.common.correct.IdeQuickFixManager;
import com.redhat.ceylon.ide.common.correct.ImportProposals;
import com.redhat.ceylon.ide.common.correct.QuickFixData;
import com.redhat.ceylon.ide.common.model.BaseCeylonProject;

public class correctJ2C implements CorrectJ2C {
    public ImportProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange>
        importProposals() {
        return eclipseImportProposals_.get_();
    }
    
    public IdeQuickFixManager<IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,IFile,ICompletionProposal,? extends QuickFixData<IProject>,LinkedModeModel> eclipseQuickFixManager() {
        return eclipseQuickFixManager_.get_();
    }
    
    public void addQuickFixes(
            ProblemLocation problem,
            Tree.CompilationUnit rootNode,
            Node node,
            IProject project,
            Collection<ICompletionProposal> proposals,
            CeylonEditor editor, 
            TypeChecker tc, 
            IFile file) {
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        EclipseQuickFixData data = new EclipseQuickFixData(problem, rootNode, 
                node, project, proposals, editor, ceylonProject);

        eclipseQuickFixManager_.get_().addQuickFixes(data, tc, file);
    }
}
