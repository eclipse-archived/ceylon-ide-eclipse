package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.analyzer.UsageWarning;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Declaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.NamedArgument;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.OperatorExpression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Statement;
import org.eclipse.ceylon.ide.eclipse.code.correct.ProblemLocation;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.common.correct.QuickFixData;
import org.eclipse.ceylon.ide.common.correct.ideQuickFixManager_;
import org.eclipse.ceylon.ide.common.correct.importProposals_;
import org.eclipse.ceylon.ide.common.platform.CommonDocument;

public interface CorrectJ2C {
    importProposals_ importProposals();

    ideQuickFixManager_ eclipseQuickFixManager();

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

    void addWarningFixes(
            ProblemLocation problem,
            UsageWarning message,
            Tree.CompilationUnit rootNode,
            Node node,
            IProject project,
            Collection<ICompletionProposal> proposals,
            CeylonEditor editor, 
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
    
    void importEdits(Object editOrChange,
            CompilationUnit rootNode,
            Set<org.eclipse.ceylon.model.typechecker.model.Declaration> declarations,
            Collection<String> aliases,
            IDocument doc);
    
    void importEditForMove(TextFileChange change,
            CompilationUnit rootNode,
            Set<org.eclipse.ceylon.model.typechecker.model.Declaration> declarations,
            Collection<String> aliases,
            String newName, String oldName,
            IDocument doc);

    QuickFixData newData(CompilationUnit rootNode, Node node,
            List<ICompletionProposal> proposals, CeylonEditor ce,
            IProject project, IDocument doc);
}
