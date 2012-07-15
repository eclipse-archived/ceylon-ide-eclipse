package com.redhat.ceylon.eclipse.code.parse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.preferences.PreferenceCache;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * Parsing may take a long time, and is not done inside the UI thread. Therefore, we create a job that is executed in a
 * background thread by the platform's job service.
 */
// TODO Perhaps this should be driven off of the "IReconcilingStrategy" mechanism?
public class ParserScheduler extends Job {
    private final CeylonParseController fParseController;

    private final IEditorPart fEditorPart;

    private final IDocumentProvider fDocumentProvider;

    private final IMessageHandler fMsgHandler;

    private final List<IModelListener> fAstListeners= new ArrayList<IModelListener>();

//  private final IPreferencesService fPrefService;

    public ParserScheduler(CeylonParseController parseController, IEditorPart editorPart,
            IDocumentProvider docProvider, IMessageHandler msgHandler) {
    	super("ParserScheduler for " + editorPart.getEditorInput().getName());
        setSystem(true); // do not show this job in the Progress view
        fParseController= parseController;
        fEditorPart= editorPart;
        fDocumentProvider= docProvider;
        fMsgHandler= msgHandler;
//      fPrefService= new PreferencesService(fParseController.getProject().getRawProject(), fParseController.getLanguage().getName());

        // rmf 7/1/2008 - N.B. The parse controller is now initialized before it gets handed to us here,
        // since some other services may actually depend on that.
    }

    public IStatus run(IProgressMonitor monitor) {
        if (fParseController == null || fDocumentProvider == null) {
            /* Editor was closed, or no parse controller */
            return Status.OK_STATUS;
        }

        IEditorInput editorInput= fEditorPart.getEditorInput();
        try {
            IDocument document= fDocumentProvider.getDocument(editorInput);

            if (document == null)
                return Status.OK_STATUS;

//          System.out.println("Parsing started.");
            // If we're editing a workspace resource, check to make sure that it still exists
            if (sourceStillExists()) {
                fMsgHandler.clearMessages();
                // Don't bother to retrieve the AST; we don't need it; just make sure the document gets parsed.
                fParseController.parse(document, monitor);
                fMsgHandler.endMessages();
//          } else {
//              System.err.println("Scheduled parsing was bypassed due to project deletion.");
            }
//          System.out.println("Parsing complete.");
            if (!monitor.isCanceled() && sourceStillExists()) {
                notifyModelListeners(monitor);
            }
        } 
        catch (Exception e) {
        	e.printStackTrace();
            // RMF 8/2/2006 - Notify the AST listeners even on an exception - the compiler front end
            // may have failed at some phase, but there may be enough info to drive IDE services.
            notifyModelListeners(monitor);
        } 
        return Status.OK_STATUS;
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

    public void addModelListener(IModelListener listener) {
        fAstListeners.add(listener);
    }

    public void removeModelListener(IModelListener listener) {
        fAstListeners.remove(listener);
    }

    public void notifyModelListeners(IProgressMonitor monitor) {
        // Suppress the notification if there's no AST (e.g. due to a parse error)
        if (fParseController != null) {
            for(int n= fAstListeners.size() - 1; n >= 0 && !monitor.isCanceled(); n--) {
                IModelListener listener= fAstListeners.get(n);
                // Pretend to get through the highest level of analysis so all services execute (for now)
                int analysisLevel= IModelListener.AnalysisRequired.POINTER_ANALYSIS.level();

                if (fParseController.getCurrentAst() == null)
                    analysisLevel= IModelListener.AnalysisRequired.LEXICAL_ANALYSIS.level();
                // TODO How to tell how far we got with the source analysis? The IAnalysisController should tell us!
                // TODO Rename IParseController to IAnalysisController
                // TODO Compute the minimum amount of analysis sufficient for all current listeners, and pass that to
                // the IAnalysisController.
                if (listener.getAnalysisRequired().level() <= analysisLevel) {
                    listener.update(fParseController, monitor);
                }
            }
//            long curTime= System.currentTimeMillis();
//            System.out.println("All model listeners notified; time = " + curTime);
//            long diffToRuntimeStart= curTime - RuntimePlugin.PRE_STARTUP_TIME;
//            long diffToEditorStart= curTime - RuntimePlugin.EDITOR_START_TIME;
//            System.out.println("Time from runtime start: " + diffToRuntimeStart);
//            System.out.println("Time from editor start: " + diffToEditorStart);
        }
    }
}
