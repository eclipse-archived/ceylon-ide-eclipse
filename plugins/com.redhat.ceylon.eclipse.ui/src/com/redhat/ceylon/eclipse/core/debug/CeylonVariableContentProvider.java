package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.jdt.internal.debug.ui.variables.JavaVariableContentProvider;

public class CeylonVariableContentProvider extends JavaVariableContentProvider {

    @Override
    public void update(IChildrenCountUpdate[] updates) {
        super.update(updates);
    }

    @Override
    public void update(IChildrenUpdate[] updates) {
        super.update(updates);
    }

    @Override
    public void update(IHasChildrenUpdate[] updates) {
        super.update(updates);
    }

}
