package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDeltaVisitor;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ModelDelta;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.ui.threadgroups.JavaDebugTargetProxy;

import org.eclipse.ceylon.ide.common.debug.agent.CeylonDebugEvaluationThread;

public class CeylonDebugTargetProxy extends JavaDebugTargetProxy {
    public CeylonDebugTargetProxy(IDebugTarget target) {
        super(target);
    }
    
    @Override
    public void fireModelChanged(IModelDelta delta) {
        delta.accept(new IModelDeltaVisitor() {
            boolean isInCeylonDebugEvaluationThread = false;

            private void filterSelectAndRevealFlags(IModelDelta delta) {
                if (isInCeylonDebugEvaluationThread) {
                    if ((delta.getFlags() & (IModelDelta.EXPAND | IModelDelta.SELECT | IModelDelta.REVEAL)) != 0) {
                        if (delta instanceof ModelDelta) {
                            int newFlags = delta.getFlags() & ~(IModelDelta.EXPAND | IModelDelta.SELECT | IModelDelta.REVEAL);
                            ((ModelDelta)delta).setFlags(newFlags);
                        }
                    }
                }
            }
            
            @Override
            public boolean visit(IModelDelta delta, int depth) {
                if (delta.getElement() instanceof IJavaThread) {
                    try {
                        isInCeylonDebugEvaluationThread = CeylonDebugEvaluationThread.name.equals(((IJavaThread)delta.getElement()).getName());
                    } catch (DebugException e) {
                        e.printStackTrace();
                    }
                    filterSelectAndRevealFlags(delta);
                }
                if (delta.getElement() instanceof IJavaStackFrame) {
                    filterSelectAndRevealFlags(delta);
                }
                return true;
            }
        });
        super.fireModelChanged(delta);
    }
}
