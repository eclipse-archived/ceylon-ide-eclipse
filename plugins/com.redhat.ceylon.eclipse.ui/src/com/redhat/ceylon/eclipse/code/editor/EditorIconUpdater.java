package com.redhat.ceylon.eclipse.code.editor;

/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

/**
 * Updates the file icon at the top of the editor window.
 * 
 * @author Robert M. Fuhrer
 */
public class EditorIconUpdater implements IProblemChangedListener {
	
    private final CeylonEditor fEditor;

    public EditorIconUpdater(CeylonEditor editor) {
        fEditor = editor;
    }
    
    @Override
    public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
        if (isMarkerChange) {
        	IEditorInput input= fEditor.getEditorInput();
        	if (input!=null) { // might run async, tests needed
        		if (input instanceof IFileEditorInput) { // The editor might be looking at something outside the workspace (e.g. system include files).
        			IFileEditorInput fileInput= (IFileEditorInput) input;
        			IFile file= fileInput.getFile();
        			if (file != null) {
        				for(int i= 0; i<changedResources.length; i++) {
        					if (changedResources[i].equals(file)) {
        					    updateTitleImage();
//        					    fEditor.setTitleImage();
        					}
        				}
        			}
        		}
        	}
        }
    }
    
    private void updateTitleImage() {
        Shell shell= fEditor.getEditorSite().getShell();
        if (shell!=null && !shell.isDisposed()) {
            shell.getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    fEditor.setTitleImage();
                }
            });
        }
    }
    
}
