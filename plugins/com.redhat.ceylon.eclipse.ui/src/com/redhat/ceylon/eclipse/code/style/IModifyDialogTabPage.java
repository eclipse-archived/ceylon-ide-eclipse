package com.redhat.ceylon.eclipse.code.style;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

public interface IModifyDialogTabPage {

    public interface IModificationListener {

        void updateStatus(IStatus status);

        void valuesModified(FormatterPreferences preferences);

    }

    public void setWorkingValues(FormatterPreferences workingValues);

    public void setModifyListener(IModificationListener modifyListener);

    public Composite createContents(Composite parent);

    public void makeVisible();

    public void setInitialFocus();

}