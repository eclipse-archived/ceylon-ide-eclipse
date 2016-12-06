import ceylon.interop.java {
    javaClass,
    javaClassFromInstance
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import org.eclipse.debug.core {
    DebugPlugin
}
import java.lang {
    JClass = Class
}
import org.eclipse.debug.core.model {
    ILaunchConfigurationDelegate
}

JClass<out ILaunchConfigurationDelegate>? ceylonAwareJUnitLaunchConfigurationDelegateClass() {
    try {
        return javaClass<CeylonAwareJUnitLaunchConfigurationDelegate>();
    } catch(Throwable e) {
        noop();
    }
    return null;
}

JClass<out ILaunchConfigurationDelegate>? ceylonAwareEclipseApplicationLaunchConfigurationClass() {
    try {
        return javaClass<CeylonAwareEclipseApplicationLaunchConfiguration>();
    } catch(Throwable e) {
        noop();
    }
    return null;
}

JClass<out ILaunchConfigurationDelegate>? ceylonAwarePDEJUnitLaunchConfigurationDelegateClass() {
    try {
        return javaClass<CeylonAwarePDEJUnitLaunchConfigurationDelegate>();
    } catch(Throwable e) {
        noop();
    }
    return null;
}

JClass<out ILaunchConfigurationDelegate>? ceylonAwareSWTBotJUnitLaunchConfigurationDelegateClass() {
    try {
        return javaClass<CeylonAwareSWTBotJUnitLaunchConfigurationDelegate>();
    } catch(Throwable e) {
        noop();
    }
    return null;
}

shared void setDefaultLaunchDelegateToNonCeylonAware() {
    try {
        value ceylonDelegatesClasses = [
            for (classGetter in {
                    javaClass<CeylonAwareJavaLaunchDelegate>,
                    javaClass<CeylonAwareJavaRemoteApplicationLaunchConfigurationDelegate>,
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
                    delegate -> javaClassFromInstance(delegate.delegate)
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