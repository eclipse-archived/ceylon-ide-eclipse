package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.util.Collection;
import java.util.List;

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
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.java2ceylon.CorrectJ2C;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.correct.AddAnnotationQuickFix;
import com.redhat.ceylon.ide.common.correct.IdeQuickFixManager;
import com.redhat.ceylon.ide.common.correct.ImportProposals;
import com.redhat.ceylon.ide.common.correct.QuickFixData;
import com.redhat.ceylon.ide.common.model.BaseCeylonProject;
import com.redhat.ceylon.ide.common.platform.CommonDocument;

public class correctJ2C implements CorrectJ2C {
    @Override
    public ImportProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange>
        importProposals() {
        return eclipseImportProposals_.get_();
    }
    
    @Override
    public IdeQuickFixManager<IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,IFile,ICompletionProposal,? extends QuickFixData<IProject>,LinkedModeModel> eclipseQuickFixManager() {
        return eclipseQuickFixManager_.get_();
    }
    
    public AddAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,? extends QuickFixData<IProject>,ICompletionProposal> addAnnotationsQuickFix() {
        return eclipseAnnotationsQuickFix_.get_();
    }
    
    @Override
    public void addQuickFixes(
            ProblemLocation problem,
            Tree.CompilationUnit rootNode,
            Node node,
            IProject project,
            Collection<ICompletionProposal> proposals,
            CeylonEditor editor, 
            TypeChecker tc, 
            IFile file,
            IDocument doc) {
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        EclipseQuickFixData data = new EclipseQuickFixData(problem, rootNode, 
                node, project, proposals, editor, ceylonProject, doc);

        eclipseQuickFixManager_.get_().addQuickFixes(data, tc, file);
    }

    @Override
    public void addQuickAssists(
            Tree.CompilationUnit rootNode,
            Node node,
            IProject project,
            Collection<ICompletionProposal> proposals,
            CeylonEditor editor, 
            IFile file,
            IDocument doc,
            Tree.Statement statement,
            Tree.Declaration declaration,
            Tree.NamedArgument argument,
            Tree.ImportMemberOrType imp,
            Tree.OperatorExpression oe,
            int currentOffset) {
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        EclipseQuickFixData data = new EclipseQuickFixData(null, rootNode, 
                node, project, proposals, editor, ceylonProject, doc);

        eclipseQuickFixManager_.get_().addQuickAssists(data, file, doc, statement, declaration, argument, imp, oe, currentOffset);
    }

    @Override
    public void addRefineFormalMembersProposal(
            CompilationUnit rootNode,
            Node node,
            List<ICompletionProposal> list,
            CeylonEditor ce,
            IProject project) {

        IDocument doc = EditorUtil.getDocument(ce.getEditorInput());
        EclipseQuickFixData data = (EclipseQuickFixData) newData(rootNode, node, list, ce, project, doc);

        eclipseRefineFormalMembersQuickFix_.get_()
            .addRefineFormalMembersProposal(data, false);
    }

    @Override
    public void addRefineEqualsHashProposal(
            CompilationUnit rootNode,
            Node node,
            List<ICompletionProposal> list,
            CeylonEditor ce,
            IProject project) {
        
        IDocument doc = EditorUtil.getDocument(ce.getEditorInput());
        EclipseQuickFixData data = (EclipseQuickFixData) newData(rootNode, node, list, ce, project, doc);
        IFile file = EditorUtil.getFile(ce.getEditorInput());

        refineEqualsHashQuickFix_.get_()
            .addRefineEqualsHashProposal(data, file, ce.getSelection().getOffset());        
    }

    @Override
    public void addAssignToLocalProposal(CompilationUnit rootNode, Node node,
            List<ICompletionProposal> list, CeylonEditor ce) {

        IDocument doc = EditorUtil.getDocument(ce.getEditorInput());
        IProject project = EditorUtil.getProject(ce.getEditorInput());
        EclipseQuickFixData data = (EclipseQuickFixData) newData(rootNode, node, list, ce, project, doc);
        IFile file = EditorUtil.getFile(ce.getEditorInput());
       
        eclipseAssignToLocalQuickFix_.get_().addProposal(data, file);
    }
    
    Object newData(CompilationUnit rootNode, Node node,
            List<ICompletionProposal> list, CeylonEditor ce,
            IProject project, IDocument doc) {
        
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel()
                .getProject(project);
        
        return new EclipseQuickFixData(
                new ProblemLocation(0, 0, 0),
                rootNode, node, null, list, ce, ceylonProject, doc
        );
    }

    @Override
    public CommonDocument newDocument(IDocument nativeDoc) {
        return new EclipseDocument(nativeDoc);
    }
}
