/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.ui;

import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.ERROR_OVR;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.FAILED_OVR;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.counterErrors;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.counterFailures;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages.counterRuns;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ceylon.test.eclipse.plugin.model.TestRun;

public class CounterPanel extends Composite {

    private Label runsLabel;
    private Text runsText;
    private Label errorsImage;
    private Label errorsLabel;
    private Text errorsText;
    private Label failuresImage;
    private Label failuresLabel;
    private Text failuresText;

    public CounterPanel(Composite parent) {
        super(parent, SWT.WRAP);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 8;
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);

        createRuns();
        createFailures();
        createErrors();
        updateView(null);
    }

    private void createRuns() {
        runsLabel = new Label(this, SWT.NONE);
        runsLabel.setText(counterRuns);
        runsText = new Text(this, SWT.READ_ONLY);
        runsText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        runsText.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    }

    private void createFailures() {
        failuresImage = new Label(this, SWT.NONE);
        failuresImage.setImage(getImage(FAILED_OVR));
        failuresLabel = new Label(this, SWT.NONE);
        failuresLabel.setText(counterFailures);
        failuresText = new Text(this, SWT.READ_ONLY);
        failuresText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        failuresText.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    }

    private void createErrors() {
        errorsImage = new Label(this, SWT.NONE);
        errorsImage.setImage(getImage(ERROR_OVR));
        errorsLabel = new Label(this, SWT.NONE);
        errorsLabel.setText(counterErrors);
        errorsText = new Text(this, SWT.READ_ONLY);
        errorsText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        errorsText.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    }

    public void updateView(TestRun currentTestRun) {
        int totalCount = 0;
        int finishedCount = 0;
        int failureCount = 0;
        int errorCount = 0;

        if( currentTestRun != null ) {
            totalCount = currentTestRun.getTotalCount();
            finishedCount = currentTestRun.getFinishedCount();
            failureCount = currentTestRun.getFailureCount();
            errorCount = currentTestRun.getErrorCount();
        }

        runsText.setText(Integer.toString(finishedCount) + "/" + Integer.toString(totalCount));
        failuresText.setText(Integer.toString(failureCount));
        errorsText.setText(Integer.toString(errorCount));

        redraw();
    }

}