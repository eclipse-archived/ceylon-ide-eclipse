package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.jdt.internal.debug.ui.variables.JavaExpressionContentProvider;

public class CeylonExpressionContentProvider extends JavaExpressionContentProvider {

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
