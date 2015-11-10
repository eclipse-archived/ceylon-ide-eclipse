package com.redhat.ceylon.eclipse.code.correct;

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

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.ide.common.correct.IdeQuickFixManager;
import com.redhat.ceylon.ide.common.correct.ImportProposals;
import com.redhat.ceylon.ide.common.correct.QuickFixData;

public class correctJ2C {
    public static ImportProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange>
        importProposals() {
        return eclipseImportProposals_.get_();
    }
    
    public static IdeQuickFixManager<IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,IFile,ICompletionProposal,QuickFixData<IProject>,LinkedModeModel> eclipseQuickFixManager() {
        return eclipseQuickFixManager_.get_();
    }
    
    public static QuickFixData<IProject> newEclipseQuickFixData(
            ProblemLocation problem,
            Tree.CompilationUnit rootNode,
            Node node,
            IProject project,
            Collection<ICompletionProposal> proposals,
            CeylonEditor editor) {
        return new EclipseQuickFixData(problem, rootNode, node, project, proposals, editor);
    }
}
