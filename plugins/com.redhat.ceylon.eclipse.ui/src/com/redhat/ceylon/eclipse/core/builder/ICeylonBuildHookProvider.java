package com.redhat.ceylon.eclipse.core.builder;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;

public interface ICeylonBuildHookProvider {
    CeylonBuildHook getHook();
}
