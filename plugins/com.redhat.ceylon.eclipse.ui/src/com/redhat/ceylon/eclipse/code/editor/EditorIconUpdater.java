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
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

/**
 * Updates the file icon at the top of the editor window.
 * 
 * @author Robert M. Fuhrer
 */
public class EditorIconUpdater implements IProblemChangedListener {
	
    private final CeylonEditor fEditor;
    private final CeylonLabelProvider fLabelProvider= new CeylonLabelProvider();

    public EditorIconUpdater(CeylonEditor editor) {
        Assert.isNotNull(editor);
        fEditor= editor;
        fEditor.getProblemMarkerManager().addListener(this);
    }

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
        						updateEditorImage(file);
        					}
        				}
        			}
        		}
        	}
        }
    }

    public void updateEditorImage(IFile file) {
        Image titleImage= fEditor.getTitleImage();
        if (titleImage==null) {
            return;
        }
        
    	// If there's no label provider or the file doesn't exist, do nothing
        if (fLabelProvider==null || !file.exists()) {
        	return;
        }

        Image newImage= fLabelProvider.getImage(file);
        if (titleImage!=newImage) {
            postImageChange(newImage);
        }
    }

    private void postImageChange(final Image newImage) {
        Shell shell= fEditor.getEditorSite().getShell();
        if (shell!=null && !shell.isDisposed()) {
            shell.getDisplay().syncExec(new Runnable() {
                public void run() {
                    fEditor.setTitleImage(newImage);
                }
            });
        }
    }

    public void dispose() {
    	fLabelProvider.dispose();
        fEditor.getProblemMarkerManager().removeListener(this);
    }
}
