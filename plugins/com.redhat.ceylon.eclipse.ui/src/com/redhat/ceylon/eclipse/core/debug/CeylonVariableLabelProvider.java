package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.jdt.internal.debug.ui.variables.JavaVariableLabelProvider;

public class CeylonVariableLabelProvider extends JavaVariableLabelProvider{

    @Override
    public void update(ILabelUpdate[] updates) {
        super.update(updates);
    }

}
