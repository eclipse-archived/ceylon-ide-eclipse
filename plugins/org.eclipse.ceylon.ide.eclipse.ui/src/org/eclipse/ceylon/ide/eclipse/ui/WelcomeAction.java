/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.ui;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.intro.config.IntroURLFactory.createIntroURL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.intro.config.IIntroURL;

public class WelcomeAction extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
        if (window == null || window.getActivePage() == null) {
            Shell shell = CeylonPlugin.getInstance().getWorkbench()
                    .getActiveWorkbenchWindow().getShell();
            MessageDialog.openError(shell, "Ceylon Welcome Error", "No active window");
        }
        else {
            getWorkbench().getIntroManager().showIntro(window, false);
            IIntroURL url = createIntroURL("http://org.eclipse.ui.intro/showPage?id=org.eclipse.ceylon.ui.intro");
            url.execute();
        }
        return null;
    }
    
}
