package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.IMessageHandler;
import com.redhat.ceylon.eclipse.code.parse.ParserScheduler;

public class CeylonParserScheduler extends ParserScheduler {

    private boolean canceling = false;
    
    private CeylonParseController fParseController;
    private IDocumentProvider fDocumentProvider;
    private IEditorPart fEditorPart;
    private IMessageHandler fMsgHandler;
    
    public CeylonParserScheduler(CeylonParseController parseController,
            IEditorPart editorPart, IDocumentProvider docProvider,
            final IMessageHandler msgHandler) {
        super(parseController, editorPart, docProvider, msgHandler);
        this.fParseController = parseController;
        this.fEditorPart = editorPart;
        this.fDocumentProvider = docProvider;
        this.fMsgHandler = msgHandler;
    }

    @Override
    protected void canceling() {
        canceling = true;
        super.canceling();
    }

    private boolean sourceStillExists() {
        ISourceProject project= fParseController.getProject();
        if (project == null) {
            return true; // this wasn't a workspace resource to begin with
        }
        IProject rawProject= project.getRawProject();
        if (!rawProject.exists()) {
            return false;
        }
        IFile file= rawProject.getFile(fParseController.getPath());
        return file.exists();
    }
    
    @Override
    public IStatus run(IProgressMonitor monitor) {
        try {
            if (canceling) {
                if (monitor != null) {
                    monitor.setCanceled(true);
                }
                return Status.CANCEL_STATUS;
            }
            
            IProgressMonitor wrappedMonitor = new ProgressMonitorWrapper(monitor) {
                @Override
                public boolean isCanceled() {
                    boolean isCanceled = false;
                    if (Job.getJobManager().currentJob() == CeylonParserScheduler.this) {
                        isCanceled = CeylonParserScheduler.this.isCanceling();
                    }
                    return isCanceled || super.isCanceled();
                }
            };
            
            if (fParseController == null || fDocumentProvider == null) {
                /* Editor was closed, or no parse controller */
                return Status.OK_STATUS;
            }

            IEditorInput editorInput= fEditorPart.getEditorInput();
            try {
                IDocument document= fDocumentProvider.getDocument(editorInput);

                if (document == null)
                    return Status.OK_STATUS;

//              System.out.println("Parsing started.");
                // If we're editing a workspace resource, check to make sure that it still exists
                if (sourceStillExists()) {
                    // Don't bother to retrieve the AST; we don't need it; just make sure the document gets parsed.
                    fParseController.parse(document, wrappedMonitor);
                    if (!wrappedMonitor.isCanceled()) {
                        fMsgHandler.endMessages();
                    }
//              } else {
//                  System.err.println("Scheduled parsing was bypassed due to project deletion.");
                }
//              System.out.println("Parsing complete.");
                if (!wrappedMonitor.isCanceled()) {
                    notifyModelListeners(wrappedMonitor);
                } else {
                    return Status.CANCEL_STATUS;
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
                // RMF 8/2/2006 - Notify the AST listeners even on an exception - the compiler front end
                // may have failed at some phase, but there may be enough info to drive IDE services.
                if (!wrappedMonitor.isCanceled()) {
                    notifyModelListeners(wrappedMonitor);
                } else {
                    return Status.CANCEL_STATUS;
                }
            }
            return Status.OK_STATUS;
        }
        finally {
            canceling = false;
        }
    }

    public boolean isCanceling() {
        return canceling;
    }
}
