package com.redhat.ceylon.eclipse.code.editor;

import java.lang.ref.WeakReference;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.swt.custom.VerifyKeyListener;

public class CeylonContentAssistant extends ContentAssistant {
    private WeakReference<VerifyKeyListener> autoAssistListener = new WeakReference<VerifyKeyListener>(null);
    private boolean incompleteResults = false;
    
    public static String secondLevelStatusMessage = "";
    public static String retrieveCompleteResultsStatusMessage = "";
    
    
    protected AutoAssistListener createAutoAssistListener() {
        AutoAssistListener listener = super.createAutoAssistListener();
        autoAssistListener = new WeakReference<VerifyKeyListener>(listener);
        return listener;            
    }
    
    public VerifyKeyListener getAutoAssistListener() {
        return autoAssistListener.get();
    }
    
    @Override
    public void setStatusMessage(String message) {
        super.setStatusMessage(message);
        if (! secondLevelStatusMessage.equals(message)) {
            incompleteResults = true;
        } else {
            incompleteResults = false;
        }
    }
    
    public boolean areResultIncomplete() {
        return incompleteResults;
    }
}