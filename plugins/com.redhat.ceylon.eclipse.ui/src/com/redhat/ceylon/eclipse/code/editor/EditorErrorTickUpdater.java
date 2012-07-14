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
import org.eclipse.imp.editor.IProblemChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

/**
 * @author Robert M. Fuhrer
 */
public class EditorErrorTickUpdater implements IProblemChangedListener {
	
    private CeylonEditor fEditor;
    private ILabelProvider fLabelProvider;

    public EditorErrorTickUpdater(CeylonEditor editor) {
        Assert.isNotNull(editor);
        fEditor= editor;
        fLabelProvider= new CeylonLabelProvider();
//        fLabelProvider.addLabelDecorator(new ProblemsLabelDecorator(null));
        fEditor.getProblemMarkerManager().addListener(this);
    }

    public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
        if (!isMarkerChange)
            return;

        IEditorInput input= fEditor.getEditorInput();

        if (input != null) { // might run async, tests needed
            if (!(input instanceof IFileEditorInput)) { // The editor might be looking at something outside the workspace (e.g. system include files).
                return;
            }

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
                    fEditor.updatedTitleImage(newImage);
                }
            });
        }
    }

    public void dispose() {
    	// SMS 9 Aug 2006
    	// as noted above, fLabelProvoider may be referenced when null,
    	// so I've added a guard here for that condition
    	if (fLabelProvider!=null) {
    		fLabelProvider.dispose();
    	}
        fEditor.getProblemMarkerManager().removeListener(this);
    }
}
