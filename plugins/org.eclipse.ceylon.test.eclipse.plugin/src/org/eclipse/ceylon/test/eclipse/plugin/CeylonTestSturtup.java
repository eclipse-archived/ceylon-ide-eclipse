package org.eclipse.ceylon.test.eclipse.plugin;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

public class CeylonTestSturtup implements IStartup {

    @Override
    public void earlyStartup() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                CeylonTestBuildPathMenu.install();
            }
        });
    }

}