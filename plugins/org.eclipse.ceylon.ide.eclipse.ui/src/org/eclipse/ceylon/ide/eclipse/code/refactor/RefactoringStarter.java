package org.eclipse.ceylon.ide.eclipse.code.refactor;
/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import static org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation.INITIAL_CONDITION_CHECKING_FAILED;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;


/**
 * A helper class to activate the UI of a refactoring
 */
public class RefactoringStarter {

    private RefactoringStatus fStatus;

    public boolean activate(RefactoringWizard wizard, Shell parent, String dialogTitle, int saveMode) {
        
        RefactoringSaveHelper saveHelper = 
                new RefactoringSaveHelper(saveMode);
        if (saveHelper.saveEditors(parent)) {
            saveHelper.triggerIncrementalBuild();
        }
        else {
            return false;
        }

        try {
            RefactoringWizardOpenOperation op = 
                    new RefactoringWizardOpenOperation(wizard);
            int result = op.run(parent, dialogTitle);
            fStatus = op.getInitialConditionCheckingStatus();
            if (result == IDialogConstants.CANCEL_ID || 
                result == INITIAL_CONDITION_CHECKING_FAILED) {
//                saveHelper.triggerIncrementalBuild();
                return false;
            }
            else {
                return true;
            }
        }
        catch (InterruptedException e) {
            return false; // User action got cancelled
        }
    }

    public RefactoringStatus getInitialConditionCheckingStatus() {
        return fStatus;
    }
}
