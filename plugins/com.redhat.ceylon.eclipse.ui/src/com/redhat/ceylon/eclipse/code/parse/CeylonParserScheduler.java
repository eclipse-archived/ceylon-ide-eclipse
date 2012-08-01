package com.redhat.ceylon.eclipse.code.parse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.redhat.ceylon.eclipse.code.editor.AnnotationCreator;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage;

public class CeylonParserScheduler extends Job {

    private boolean canceling = false;
    
    private CeylonParseController fParseController;
    private IDocumentProvider fDocumentProvider;
    private IEditorPart fEditorPart;
    private AnnotationCreator fMsgHandler;
    
    private final List<TreeLifecycleListener> fAstListeners = new ArrayList<TreeLifecycleListener>();

    public CeylonParserScheduler(CeylonParseController parseController,
            IEditorPart editorPart, IDocumentProvider docProvider,
            AnnotationCreator msgHandler) {
    	super("ParserScheduler for " + editorPart.getEditorInput().getName());
        setSystem(true); //do not show this job in the Progress view
        setPriority(SHORT);
        
        // Note: The parse controller is now initialized before  
        // it gets handed to us here, since some other services  
        // may actually depend on that.
        this.fParseController = parseController;
        this.fEditorPart = editorPart;
        this.fDocumentProvider = docProvider;
        this.fMsgHandler = msgHandler;
    }

    @Override
    protected void canceling() {
        canceling = true;
    }

    public boolean isCanceling() {
        return canceling;
    }
    
    public class Stager {
    	void afterStage(Stage stage, IProgressMonitor monitor) {
    		notifyModelListeners(stage, monitor);
    	}
    }

    private boolean sourceStillExists() {
        IProject project= fParseController.getProject();
        if (project == null) {
            return true; // this wasn't a workspace resource to begin with
        }
        if (!project.exists()) {
            return false;
        }
        IFile file= project.getFile(fParseController.getPath());
        return file.exists();
    }
    
    @Override
    public IStatus run(IProgressMonitor monitor) {
        try {
            if (canceling) {
                if (monitor!=null) {
                    monitor.setCanceled(true);
                }
                return Status.CANCEL_STATUS;
            }
            
            IProgressMonitor wrappedMonitor = new ProgressMonitorWrapper(monitor) {
                @Override
                public boolean isCanceled() {
                    boolean isCanceled = false;
                    if (Job.getJobManager().currentJob() == CeylonParserScheduler.this) {
                        isCanceled = canceling;
                    }
                    return isCanceled || super.isCanceled();
                }
            };
            
            if (fParseController==null || fDocumentProvider==null) {
                // Editor was closed, or no parse controller
                return Status.OK_STATUS;
            }

            IEditorInput editorInput= fEditorPart.getEditorInput();
            try {
                IDocument document= fDocumentProvider.getDocument(editorInput);
                if (document == null) {
                    return Status.OK_STATUS;
                }

                // If we're editing a workspace resource, check   
                // to make sure that it still exists
                else if (sourceStillExists()) {
                	//TODO: is this a better way to clear existing
                	//      annotations/markers:
                	//fMsgHandler.clearMessages();
                    // don't bother to retrieve the AST; we don't 
                	// need it; just make sure the document gets 
                	// parsed
                    fParseController.parse(document, wrappedMonitor, new Stager());
                    if (!wrappedMonitor.isCanceled()) {
                        fMsgHandler.updateAnnotations();
                    }
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
            return wrappedMonitor.isCanceled() ? //&& sourceStillExists()
            		Status.OK_STATUS : Status.CANCEL_STATUS;
        }
        finally {
            canceling = false;
        }
    }

    public void addModelListener(TreeLifecycleListener listener) {
        fAstListeners.add(listener);
    }

    public void removeModelListener(TreeLifecycleListener listener) {
        fAstListeners.remove(listener);
    }

    private void notifyModelListeners(Stage stage, IProgressMonitor monitor) {
        // Suppress the notification if there's no AST (e.g. due to a parse error)
        if (fParseController!=null) {
            for (TreeLifecycleListener listener: new ArrayList<TreeLifecycleListener>(fAstListeners)) {
            	if (monitor.isCanceled()) break;
                // TODO: How to tell how far we got with the source analysis? 
            	//       CeylonParseController should tell us!
                // Pretend to get through the highest level of analysis so 
            	// all services execute (for now)
                if (listener.getStage()==stage) {
                    listener.update(fParseController, monitor);
                }
            }
        }
    }
}
