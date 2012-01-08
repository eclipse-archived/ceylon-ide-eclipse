package com.redhat.ceylon.eclipse.ui;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.intro.config.IntroURLFactory.createIntroURL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.intro.config.IIntroURL;

public class WelcomeAction implements IWorkbenchWindowActionDelegate {
    
    @Override
    public void run(IAction action) {
        final IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
        getWorkbench().getIntroManager().showIntro(window, false);
        IIntroURL url = createIntroURL("http://org.eclipse.ui.intro/showPage?id=com.redhat.ceylon.ui.intro");
        url.execute();
    }

    @Override
    public void init(IWorkbenchWindow window) {}

    @Override
    public void selectionChanged(IAction action, ISelection selection) {}

    @Override
    public void dispose() {}   
    
}
