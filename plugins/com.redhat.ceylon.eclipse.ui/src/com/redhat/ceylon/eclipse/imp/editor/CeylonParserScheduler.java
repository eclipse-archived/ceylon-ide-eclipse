package com.redhat.ceylon.eclipse.imp.editor;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.preferences.PreferenceCache;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.jdt.internal.core.search.processing.JobManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.progress.ProgressManager;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class CeylonParserScheduler extends org.eclipse.imp.editor.ParserScheduler {

    private boolean canceling = false;
    
    private IParseController fParseController;
    private IDocumentProvider fDocumentProvider;
    private IEditorPart fEditorPart;
    private IMessageHandler fMsgHandler;
    
    public CeylonParserScheduler(IParseController parseController,
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

                if (PreferenceCache.emitMessages /* fPrefService.getBooleanPreference(PreferenceConstants.P_EMIT_MESSAGES) */) {
                    RuntimePlugin.getInstance().writeInfoMsg(
                            "Parsing language " + fParseController.getLanguage().getName() + " for input " + editorInput.getName());
                }

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
                    System.out.println("Notifying listeners");
                    notifyModelListeners(wrappedMonitor);
                } else {
                    return Status.CANCEL_STATUS;
                }
            } catch (Exception e) {
                Language lang = fParseController.getLanguage();
                String input = editorInput != null ? editorInput.getName() : "<unknown editor input";
                String name = lang != null ? lang.getName() : "<unknown language>";
                ErrorHandler.reportError("Error running parser for language " + name + " and input " + input + ":", e);
                // RMF 8/2/2006 - Notify the AST listeners even on an exception - the compiler front end
                // may have failed at some phase, but there may be enough info to drive IDE services.
                if (!wrappedMonitor.isCanceled()) {
                    notifyModelListeners(wrappedMonitor);
                } else {
                    return Status.CANCEL_STATUS;
                }
            } catch (LinkageError e) {
                // Catch things like NoClassDefFoundError that might result from, e.g., errors in plugin metadata, classpath, etc.
                ErrorHandler.reportError("Error loading IParseController implementation class for language " + fParseController.getLanguage().getName(), e);
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
