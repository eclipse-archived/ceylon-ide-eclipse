package com.redhat.ceylon.eclipse.code.correct;
/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.performChange;
import static com.redhat.ceylon.eclipse.util.Highlights.styleProposal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;


/**
 * A quick fix/assist proposal based on a {@link Change}.
 */
class CorrectionProposal 
        implements ICompletionProposal, ICompletionProposalExtension5, 
                   ICompletionProposalExtension6 {

    private static final NullChange COMPUTING_CHANGE = 
            new NullChange("ChangeCorrectionProposal computing...");
    
    private Change change;
    private final String name;
    private final Image image;
    private final Region selection;

    /**
     * Constructs a change correction proposal.
     * 
     * @param name the name that is displayed in the proposal selection dialog
     * @param change the change that is executed when the proposal is applied or <code>null</code>
     *            if the change will be created by implementors of {@link #createChange()}
     * @param relevance the relevance of this proposal
     * @param image the image that is displayed for this proposal or <code>null</code> if no image
     *            is desired
     */
    public CorrectionProposal(String name, Change change, Region selection, Image image) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name= name;
        this.change= change;
        this.image= image;
        this.selection= selection;
    }

    /**
     * Constructs a change correction proposal. Uses the default image for this proposal.
     * 
     * @param name The name that is displayed in the proposal selection dialog.
     * @param change The change that is executed when the proposal is applied or <code>null</code>
     *            if the change will be created by implementors of {@link #createChange()}.
     * @param relevance The relevance of this proposal.
     */
    public CorrectionProposal(String name, Change change, Region selection) {
        this(name, change, selection, MINOR_CHANGE);
    }

    @Override
    public void apply(IDocument document) {
        try {
            performChange(getCurrentEditor(), document, getChange(), getName());
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAdditionalProposalInfo() {
        Object info= getAdditionalProposalInfo(new NullProgressMonitor());
        return info == null ? null : info.toString();
    }

    @Override
    public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
        StringBuffer buf= new StringBuffer();
        buf.append("<p>");
        try {
            Change change= getChange();
            if (change != null) {
                String name= change.getName();
                if (name.length() == 0) {
                    return null;
                }
                buf.append(name);
            } else {
                return null;
            }
        } catch (CoreException e) {
            buf.append("Unexpected error when accessing this proposal:<p><pre>");
            buf.append(e.getLocalizedMessage());
            buf.append("</pre>");
        }
        buf.append("</p>");
        return buf.toString();
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return getName();
    }

    @Override
    public StyledString getStyledDisplayString() {
        return styleProposal(getDisplayString(), false);
    }
    
    /**
     * Returns the name of the proposal.
     *
     * @return the name of the proposal
     */
    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public Point getSelection(IDocument document) {
        if (selection==null) {
            return null;
        }
        else {
            return new Point(
                    selection.getOffset(), 
                    selection.getLength());
        }
    }

    /**
     * Returns the change that will be executed when the proposal is applied.
     * This method calls {@link #createChange()} to compute the change.
     * 
     * @return the change for this proposal, can be <code>null</code> in rare cases if creation of
     *         the change failed
     * @throws CoreException when the change could not be created
     */
    public final Change getChange() throws CoreException {
        if (Util.isGtk()) {
            // workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=293995 :
            // [Widgets] Deadlock while UI thread displaying/computing a change proposal and non-UI thread creating image
            
            // Solution is to create the change outside a 'synchronized' block.
            // Synchronization is achieved by polling fChange, using "fChange == COMPUTING_CHANGE" as barrier.
            // Timeout of 10s for safety reasons (should not be reached).
            long end= System.currentTimeMillis() + 10000;
            do {
                boolean computing;
                synchronized (this) {
                    computing= change == COMPUTING_CHANGE;
                }
                if (computing) {
                    try {
                        Display display= Display.getCurrent();
                        if (display != null) {
                            while (!display.isDisposed() && 
                                    display.readAndDispatch()) {
                                // empty the display loop
                            }
                            display.sleep();
                        } else {
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        //continue
                    }
                } else {
                    synchronized (this) {
                        if (change == COMPUTING_CHANGE) {
                            continue;
                        } else if (change != null) {
                            return change;
                        } else {
                            change= COMPUTING_CHANGE;
                        }
                    }
                    Change change= createChange();
                    synchronized (this) {
                        this.change= change;
                    }
                    return change;
                }
            } while (System.currentTimeMillis() < end);
            
            synchronized (this) {
                if (change == COMPUTING_CHANGE) {
                    return null; //failed
                }
            }
            
        } else {
            synchronized (this) {
                if (change == null) {
                    change= createChange();
                }
            }
        }
        return change;
    }

    /**
     * Creates the change for this proposal.
     * This method is only called once and only when no change has been passed in
     * {@link #ChangeCorrectionProposal(String, Change, int, Image)}.
     *
     * Subclasses may override.
     * 
     * @return the created change
     * @throws CoreException if the creation of the change failed
     */
    protected Change createChange() throws CoreException {
        return new NullChange();
    }

}
