/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.common.platform {
    platformUtils,
    Status
}

import java.lang {
    JClass=Class,
    Types {
        classForType,
        classForInstance
    }
}

import org.eclipse.debug.core {
    DebugPlugin
}
import org.eclipse.debug.core.model {
    ILaunchConfigurationDelegate
}

JClass<out ILaunchConfigurationDelegate>? ceylonAwareJUnitLaunchConfigurationDelegateClass() {
    try {
        return classForType<CeylonAwareJUnitLaunchConfigurationDelegate>();
    } catch(Throwable e) {}
    return null;
}

JClass<out ILaunchConfigurationDelegate>? ceylonAwareEclipseApplicationLaunchConfigurationClass() {
    try {
        return classForType<CeylonAwareEclipseApplicationLaunchConfiguration>();
    } catch(Throwable e) {}
    return null;
}

JClass<out ILaunchConfigurationDelegate>? ceylonAwarePDEJUnitLaunchConfigurationDelegateClass() {
    try {
        return classForType<CeylonAwarePDEJUnitLaunchConfigurationDelegate>();
    } catch(Throwable e) {}
    return null;
}

JClass<out ILaunchConfigurationDelegate>? ceylonAwareSWTBotJUnitLaunchConfigurationDelegateClass() {
    try {
        return classForType<CeylonAwareSWTBotJUnitLaunchConfigurationDelegate>();
    } catch(Throwable e) {}
    return null;
}

shared void setDefaultLaunchDelegateToNonCeylonAware() {
    try {
        value ceylonDelegatesClasses = [
            for (classGetter in {
                    classForType<CeylonAwareJavaLaunchDelegate>,
                    classForType<CeylonAwareJavaRemoteApplicationLaunchConfigurationDelegate>,
                    ceylonAwareJUnitLaunchConfigurationDelegateClass,
                    ceylonAwareEclipseApplicationLaunchConfigurationClass,
                    ceylonAwarePDEJUnitLaunchConfigurationDelegateClass,
                    ceylonAwareSWTBotJUnitLaunchConfigurationDelegateClass })
                if (exists c = classGetter()) c
        ];
        
        value launchManager = DebugPlugin.default.launchManager;
        for (type in launchManager.launchConfigurationTypes) {
            for (modeCombination in type.supportedModeCombinations) {
                value delegates = type.getDelegates(modeCombination);
                if (delegates.size != 2 ||
                    type.getPreferredDelegate(modeCombination) exists) {
                    continue;
                }
                value delegatesWithClasses = {
                    for (delegate in delegates)
                    delegate -> classForInstance(delegate.delegate)
                };
                if (delegatesWithClasses.any((delegate -> clazz) 
                    => clazz in ceylonDelegatesClasses)) {
                    value originalDelegate = delegatesWithClasses.find((delegate -> clazz) 
                        => ! clazz in ceylonDelegatesClasses)?.key;
                    if (exists originalDelegate) {
                        type.setPreferredDelegate(modeCombination, originalDelegate);
                    }
                }
            }
        }
    } catch(Exception e) {
        platformUtils.log(Status._WARNING, "Error when setting the default launch configurations", e);
    }
}