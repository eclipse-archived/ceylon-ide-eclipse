package com.redhat.ceylon.eclipse.java2ceylon;

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
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.NamedArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.OperatorExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.code.correct.ProblemLocation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.ide.common.correct.IdeQuickFixManager;
import com.redhat.ceylon.ide.common.correct.ImportProposals;
import com.redhat.ceylon.ide.common.correct.QuickFixData;
import com.redhat.ceylon.ide.common.platform.CommonDocument;

public interface CorrectJ2C {
    ImportProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange> importProposals();

    IdeQuickFixManager<IDocument,InsertEdit,TextEdit,TextChange,Region,IFile,ICompletionProposal,? extends QuickFixData,LinkedModeModel> eclipseQuickFixManager();

    void addQuickFixes(
        ProblemLocation problem,
        Tree.CompilationUnit rootNode,
        Node node,
        IProject project,
        Collection<ICompletionProposal> proposals,
        CeylonEditor editor, 
        TypeChecker tc, 
        IFile file,
        IDocument doc);

    void addQuickAssists(
            CompilationUnit rootNode,
            Node node,
            IProject project,
            Collection<ICompletionProposal> proposals,
            CeylonEditor editor,
            IFile file, 
            IDocument doc, 
            Statement statement,
            Declaration declaration,
            NamedArgument argument,
            ImportMemberOrType imp, 
            OperatorExpression oe, 
            int currentOffset);
    
    void addRefineFormalMembersProposal(
            CompilationUnit rootNode,
            Node node,
            List<ICompletionProposal> list,
            CeylonEditor ce,
            IProject project);

    
    void addRefineEqualsHashProposal(
            CompilationUnit rootNode,
            Node node,
            List<ICompletionProposal> list,
            CeylonEditor ce,
            IProject project);
    
    void addAssignToLocalProposal(
            CompilationUnit rootNode,
            Node node,
            List<ICompletionProposal> list,
            CeylonEditor ce);
    
    CommonDocument newDocument(IDocument nativeDoc);
}
