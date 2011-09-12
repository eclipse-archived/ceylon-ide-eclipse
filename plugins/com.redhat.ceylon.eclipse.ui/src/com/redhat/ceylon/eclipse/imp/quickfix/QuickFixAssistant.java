package com.redhat.ceylon.eclipse.imp.quickfix;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.imp.editor.quickfix.IAnnotation;
import org.eclipse.imp.services.IQuickFixAssistant;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.contentProposer.CeylonContentProposer;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.util.Util;

/**
 * Popup quick fixes for problem annotations displayed in editor
 * @author gavin
 */
public class QuickFixAssistant implements IQuickFixAssistant {

    @Override
    public boolean canFix(Annotation annotation) {
        return annotation instanceof IAnnotation 
                && ((IAnnotation) annotation).getId()==100;
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
              Node node = CeylonSourcePositionLocator.findNode(cu, problem.getOffset(), 
                      problem.getOffset() + problem.getLength());
              Node id = CeylonSourcePositionLocator.getIdentifyingNode(node);
              for (String name: CeylonContentProposer.getProposals(node, "", 
                    tc.getContext()).keySet()) {
                int dist = Util.getLevenshteinDistance(id.getText(), name);
                System.out.println(id.getText() + "-" + name + "=" + dist);
                if (dist<=id.getText().length()/3+1) {
                    TextFileChange change = new TextFileChange("Rename", file);
                    change.setEdit(new MultiTextEdit());
                    change.getEdit().addChild(new ReplaceEdit(problem.getOffset(), 
                            problem.getLength(), name));
                    proposals.add(new ChangeCorrectionProposal("Rename to '" + name + "'", 
                            change, 50, null));
                }
              }
            }
            break;
        }
    }
    
}
