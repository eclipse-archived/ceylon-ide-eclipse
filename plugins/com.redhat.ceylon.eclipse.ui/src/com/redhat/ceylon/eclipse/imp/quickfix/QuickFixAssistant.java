package com.redhat.ceylon.eclipse.imp.quickfix;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.imp.editor.quickfix.IAnnotation;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.services.IQuickFixAssistant;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.contentProposer.CeylonContentProposer;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.util.Util;

/**
 * Popup quick fixes for problem annotations displayed in editor
 * @author gavin
 */
public class QuickFixAssistant implements IQuickFixAssistant {

    @Override
    public boolean canFix(Annotation annotation) {
        return annotation instanceof IAnnotation 
                && ((IAnnotation) annotation).getId()==100 ||
               annotation instanceof MarkerAnnotation && 
               ((MarkerAnnotation) annotation).getMarker()
                   .getAttribute(IMessageHandler.ERROR_CODE_KEY, -666)==100;
    }

    @Override
    public boolean canAssist(IQuickFixInvocationContext invocationContext) {
        return false;
    }

    @Override
    public String[] getSupportedMarkerTypes() {
        return new String[] { CeylonBuilder.PROBLEM_MARKER_ID };
    }

    @Override
    public void addProposals(IQuickFixInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals) {
        switch ( problem.getProblemId() ) {
        case 100:
            IProject project = context.getModel().getProject().getRawProject();
            IFile file = context.getModel().getFile();
            TypeChecker tc = CeylonBuilder.getProjectTypeChecker(project);
            if (tc!=null) {
                Tree.CompilationUnit cu = (Tree.CompilationUnit) context.getModel()
                        .getAST(new NullMessageHandler(), new NullProgressMonitor());
                addRenameProposals(cu, problem, proposals, file, tc);
            }
            break;
        }
    }

    private void addRenameProposals(Tree.CompilationUnit cu, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file, TypeChecker tc) {
          Node node = CeylonSourcePositionLocator.findNode(cu, problem.getOffset(), 
                  problem.getOffset() + problem.getLength());
          String brokenName = CeylonSourcePositionLocator.getIdentifyingNode(node).getText();
          for (Map.Entry<String,DeclarationWithProximity> entry: 
              CeylonContentProposer.getProposals(node, "", tc.getContext()).entrySet()) {
            String name = entry.getKey();
            Declaration d = entry.getValue().getDeclaration();
            int dist = Util.getLevenshteinDistance(brokenName, name);
            if (dist<=brokenName.length()/3+3) {
                TextFileChange change = new TextFileChange("Rename", file);
                change.setEdit(new ReplaceEdit(problem.getOffset(), 
                        brokenName.length(), name)); //TODO: don't use problem.getLength() because it's wrong from the problem list
                proposals.add(new ChangeCorrectionProposal("Rename to '" + name + "'", 
                        change, 50, CeylonLabelProvider.getImage(d)));
            }
          }
    }
    
}
