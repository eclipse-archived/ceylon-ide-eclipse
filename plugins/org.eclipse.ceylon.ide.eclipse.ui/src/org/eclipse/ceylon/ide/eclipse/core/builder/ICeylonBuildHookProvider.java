package org.eclipse.ceylon.ide.eclipse.core.builder;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;

public interface ICeylonBuildHookProvider {
    CeylonBuildHook getHook();
}
