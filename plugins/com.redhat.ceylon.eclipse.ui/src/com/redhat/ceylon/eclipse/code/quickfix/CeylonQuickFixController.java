package com.redhat.ceylon.eclipse.code.quickfix;

/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationModel;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationsForLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtility;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.AnnotationUtils;
import com.redhat.ceylon.eclipse.util.MarkerUtils;

public class CeylonQuickFixController extends QuickAssistAssistant implements IQuickAssistProcessor {
	
    private CeylonQuickFixAssistant fAssistant;
    private CeylonEditor editor; //may only be used for quick assists!!!
    private IFile file;
    private ICompilationUnit model;
	
	public CeylonQuickFixController(CeylonEditor editor) {
        fAssistant = new CeylonQuickFixAssistant();
        this.editor = editor;
        
        if (editor.getEditorInput() instanceof FileEditorInput) {
            FileEditorInput input = (FileEditorInput) editor.getEditorInput();
            if (input!=null) {
            	file = input.getFile();
            }
        }

        setQuickAssistProcessor(this);        
    }
    
    public CeylonQuickFixController(IMarker marker) {
        fAssistant = new CeylonQuickFixAssistant();
        IFileEditorInput input = MarkerUtils.getInput(marker);
		if (input!=null) {
			file = input.getFile();
			//TODO: get the tree from CeylonBuilder!!!!
			model = ModelFactory.open(file, EditorUtility.getSourceProject(input));
		}
		/*try {
			fCU = (ICompilationUnit) ModelFactory.open(marker.getResource());
		} 
        catch (ModelException e) {
			e.printStackTrace();
		} */
        setQuickAssistProcessor(this);
	}

    
    Tree.CompilationUnit getRootNode() {
    	if (editor!=null) {
    		return editor.getParseController().getRootNode();
    	}
    	else if (model!=null) {
    		//TODO: this is really slow ... get the tree from CeylonBuilder
    		return (Tree.CompilationUnit) model.getAST(new NullMessageHandler(), 
    				new NullProgressMonitor());
    	}
    	else {
    		return null;
    	}
    }
    
	/*public IQuickAssistInvocationContext getContext(IQuickAssistInvocationContext quickAssistContext) {
        return new DefaultQuickFixInvocationContext(quickAssistContext, fCU);
    }*/

    public String getErrorMessage() {
        return null;
    }

    @Override
    public boolean canFix(Annotation annotation) {
        return fAssistant.canFix(annotation);
    }

    @Override
    public boolean canAssist(IQuickAssistInvocationContext quickAssistContext) {
        return fAssistant.canAssist(quickAssistContext);
    }

    public boolean canFix(IMarker marker) throws CoreException {
        for (String type : fAssistant.getSupportedMarkerTypes()) {
            if (marker.getType().equals(type)) {
                MarkerAnnotation ma = new MarkerAnnotation(marker);
                return fAssistant.canFix(ma);
            }
        }
        return false;
    }

    public void collectProposals(IQuickAssistInvocationContext context,
            IAnnotationModel model, Collection<Annotation> annotations,
            boolean addQuickFixes, boolean addQuickAssists,
            Collection<ICompletionProposal> proposals) {
        ArrayList<ProblemLocation> problems = new ArrayList<ProblemLocation>();
        // collect problem locations and corrections from marker annotations
        for (Annotation curr: annotations) {
            ProblemLocation problemLocation = null;
            if (curr instanceof CeylonAnnotation) {
                problemLocation = getProblemLocation((CeylonAnnotation) curr, model);
                if (problemLocation != null) {
                    problems.add(problemLocation);
                }
            }
            if (problemLocation == null && addQuickFixes
                    && curr instanceof SimpleMarkerAnnotation) {
                collectMarkerProposals((SimpleMarkerAnnotation) curr, proposals);
            }
        }

        ProblemLocation[] problemLocations =
            (ProblemLocation[]) problems.toArray(new ProblemLocation[problems.size()]);
        if (addQuickFixes) {
            collectCorrections(context, problemLocations, proposals);
        }
        if (addQuickAssists) {
            collectAssists(context, problemLocations, proposals);
        }
    }

    private static ProblemLocation getProblemLocation(CeylonAnnotation annotation, IAnnotationModel model) {
        int problemId = annotation.getId();
        if (problemId != -1) {
            Position pos = model.getPosition((Annotation) annotation);
            if (pos != null) {
                return new ProblemLocation(pos.getOffset(), pos.getLength(),
                        annotation); // java problems all handled by the quick assist processors
            }
        }
        return null;
    }

    public void collectAssists(IQuickAssistInvocationContext context,
            ProblemLocation[] locations, Collection<ICompletionProposal> proposals) {
        //if (locations.length==0) {
            ((CeylonQuickFixAssistant) fAssistant).addProposals(context, editor, proposals);
        //}
    }

    private static void collectMarkerProposals(SimpleMarkerAnnotation annotation, Collection<ICompletionProposal> proposals) {
        IMarker marker = annotation.getMarker();
        IMarkerResolution[] res = IDE.getMarkerHelpRegistry().getResolutions(marker);
        if (res.length > 0) {
            for (int i = 0; i < res.length; i++) {
                proposals.add(new CeylonMarkerResolutionProposal(res[i], marker));
            }
        }
    }

    public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext quickAssistContext) {
        ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        ISourceViewer viewer = quickAssistContext.getSourceViewer();
        collectProposals(quickAssistContext, getAnnotationModel(viewer),
                getAnnotationsForLine(viewer, getLine(quickAssistContext, viewer)), 
                        true, true, proposals);
        return proposals.toArray(new ICompletionProposal[proposals.size()]);
    }

    private int getLine(IQuickAssistInvocationContext quickAssistContext,
            ISourceViewer viewer) {
        try {
            return viewer.getDocument().getLineOfOffset(quickAssistContext.getOffset());
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*private static class DefaultQuickFixInvocationContext implements
            IQuickAssistInvocationContext {

        private IQuickAssistInvocationContext context;
        private ICompilationUnit model;

        public DefaultQuickFixInvocationContext(IQuickAssistInvocationContext context, 
        		ICompilationUnit model) {
            this.context = context;
            this.model = model;
        }

        public int getLength() {
            return context.getLength();
        }

        public int getOffset() {
            return context.getOffset();
        }

        public ISourceViewer getSourceViewer() {
            return context.getSourceViewer();
        }

        public ICompilationUnit getModel() {
            return model;
        }
    }*/
    
    public void collectCorrections(IQuickAssistInvocationContext quickAssistContext,
            ProblemLocation[] locations, Collection<ICompletionProposal> proposals) {
        if (locations!= null && locations.length>0) {
        	Tree.CompilationUnit rootNode = getRootNode();
        	HashSet<Integer> handledProblems = new HashSet<Integer>(locations.length);
        	for (int i = 0; i < locations.length; i++) {
        		ProblemLocation curr = locations[i];
        		Integer id = new Integer(curr.getProblemId());
        		if (handledProblems.add(id)) {
        			fAssistant.addProposals(quickAssistContext, curr, file, rootNode, proposals);
        		}
        	}
        }
    }

}


