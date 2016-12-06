import java.lang {
    ObjectArray
}

import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.debug.core {
    ILaunch,
    ILaunchConfiguration,
    DebugPlugin {
        parseArguments,
        renderArguments
    },
    DebugEvent
}
import org.eclipse.debug.core.model {
    ILaunchConfigurationDelegate
}
import org.eclipse.debug.core.sourcelookup {
    ISourceLookupDirector
}
import org.eclipse.jdt.internal.launching {
    JavaRemoteApplicationLaunchConfigurationDelegate
}
import org.eclipse.jdt.junit.launcher {
    JUnitLaunchConfigurationDelegate
}
import org.eclipse.jdt.launching {
    JavaLaunchDelegate
}
import org.eclipse.pde.internal.launching.launcher {
    VMHelper
}
import org.eclipse.pde.internal.launching.sourcelookup {
    PDESourceLookupDirector
}
import org.eclipse.pde.launching {
    EclipseApplicationLaunchConfiguration,
    PDEJUnitLaunchConfigurationDelegate=JUnitLaunchConfigurationDelegate
}

shared interface CeylonAwareLaunchConfigurationDelegate 
        of CeylonAwareJavaLaunchDelegate
        | CeylonAwareJavaRemoteApplicationLaunchConfigurationDelegate
        | CeylonAwareJUnitLaunchConfigurationDelegate
        | CeylonAwareEclipseApplicationLaunchConfiguration
        | CeylonAwarePDEJUnitLaunchConfigurationDelegate
        | CeylonAwareSWTBotJUnitLaunchConfigurationDelegate
        satisfies ILaunchConfigurationDelegate {
    shared default String overridenSourcePathComputerId
            => "com.redhat.ceylon.eclipse.ui.launching.sourceLookup.ceylonSourcePathComputer";

    shared default ISourceLookupDirector overridenSourceLocator()
            => CeylonSourceLookupDirector();

    shared formal void originalLaunch(
        ILaunchConfiguration configuration,
        String mode,
        ILaunch launch,
        IProgressMonitor monitor);

    shared void overrideSourceLocator(
        ILaunchConfiguration configuration,
        ILaunch launch) {
        value sourceLocator = overridenSourceLocator();
        sourceLocator.sourcePathComputer =
                DebugPlugin.default
                .launchManager
                .getSourcePathComputer(overridenSourcePathComputerId);
        sourceLocator.initializeDefaults(configuration);
        launch.sourceLocator = sourceLocator;
    }

    shared actual default void launch(
        ILaunchConfiguration configuration,
        String mode,
        ILaunch launch,
        IProgressMonitor monitor) {
        overrideSourceLocator(configuration, launch);
        originalLaunch(configuration, mode, launch, monitor);
    }
}

shared class CeylonAwareJavaLaunchDelegate()
        extends JavaLaunchDelegate()
        satisfies CeylonAwareLaunchConfigurationDelegate
                    & ClassPathEnricher & CeylonDebuggingSupportEnabled {

    launch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of CeylonAwareLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    originalLaunch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of JavaLaunchDelegate)
            .launch(c, m, l, p);
    
    getClasspath(ILaunchConfiguration launchConfiguration) 
            => enrichClassPath(super.getClasspath(launchConfiguration), launchConfiguration);
    getTheJavaProject(ILaunchConfiguration launchConfiguration) 
            => getJavaProject(launchConfiguration);
        
    handleDebugEvents(ObjectArray<DebugEvent> _DebugEventArray) =>
            (super of CeylonDebuggingSupportEnabled).handleDebugEvents(_DebugEventArray);
    
    getOriginalVMArguments(ILaunchConfiguration configuration) => 
            (super of JavaLaunchDelegate).getVMArguments(configuration);
    
    getVMArguments(ILaunchConfiguration configuration) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMArguments(configuration);
    
    getOriginalVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of JavaLaunchDelegate).getVMRunner(configuration, mode);
    
    getVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMRunner(configuration, mode);
    
    getStartLocation(ILaunchConfiguration configuration) => null;
    
    getOriginalVMInstall(ILaunchConfiguration configuration) =>
            (super of JavaLaunchDelegate).getVMInstall(configuration);
    
    shouldStopInMain(ILaunchConfiguration configuration) => false;
}

shared class CeylonAwareJavaRemoteApplicationLaunchConfigurationDelegate()
        extends JavaRemoteApplicationLaunchConfigurationDelegate()
        satisfies CeylonAwareLaunchConfigurationDelegate {

    shared actual void launch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of CeylonAwareLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    shared actual void originalLaunch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of JavaRemoteApplicationLaunchConfigurationDelegate)
            .launch(c, m, l, p);
}

shared class CeylonAwareJUnitLaunchConfigurationDelegate()
        extends JUnitLaunchConfigurationDelegate()
        satisfies CeylonAwareLaunchConfigurationDelegate
        & ClassPathEnricher
        & CeylonDebuggingSupportEnabled {

    launch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of CeylonAwareLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    originalLaunch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of JUnitLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    getClasspath(ILaunchConfiguration launchConfiguration) 
            => enrichClassPath(super.getClasspath(launchConfiguration), 
                               launchConfiguration);
    getTheJavaProject(ILaunchConfiguration launchConfiguration) 
            => getJavaProject(launchConfiguration);
    
    
    handleDebugEvents(ObjectArray<DebugEvent> _DebugEventArray) =>
            (super of CeylonDebuggingSupportEnabled).handleDebugEvents(_DebugEventArray);
    
    getOriginalVMArguments(ILaunchConfiguration configuration) => 
            (super of JUnitLaunchConfigurationDelegate).getVMArguments(configuration);
    
    getVMArguments(ILaunchConfiguration configuration) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMArguments(configuration);
    
    getOriginalVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of JUnitLaunchConfigurationDelegate).getVMRunner(configuration, mode);
    
    getVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMRunner(configuration, mode);
    
    getStartLocation(ILaunchConfiguration configuration) => null;
    
    getOriginalVMInstall(ILaunchConfiguration configuration) =>
            (super of JUnitLaunchConfigurationDelegate).getVMInstall(configuration);
    
    shouldStopInMain(ILaunchConfiguration configuration) => false;
}

shared class CeylonAwareEclipseApplicationLaunchConfiguration()
        extends EclipseApplicationLaunchConfiguration()
        satisfies CeylonAwareLaunchConfigurationDelegate &
        CeylonDebuggingSupportEnabled {

    overridenSourceLocator()
            => object extends PDESourceLookupDirector()
            satisfies CeylonAwareSourceLookupDirector {};

    launch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of CeylonAwareLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    originalLaunch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of EclipseApplicationLaunchConfiguration)
            .launch(c, m, l, p);
    
    handleDebugEvents(ObjectArray<DebugEvent> _DebugEventArray) =>
            (super of CeylonDebuggingSupportEnabled).handleDebugEvents(_DebugEventArray);
    
    getOriginalVMArguments(ILaunchConfiguration configuration) => 
            renderArguments(
                (super of EclipseApplicationLaunchConfiguration).getVMArguments(configuration), null);
    
    getVMArguments(ILaunchConfiguration configuration) => 
            parseArguments(
                (super of CeylonDebuggingSupportEnabled).getOverridenVMArguments(configuration));
    
    getOriginalVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of EclipseApplicationLaunchConfiguration).getVMRunner(configuration, mode);
    
    getVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMRunner(configuration, mode);
    
    getStartLocation(ILaunchConfiguration configuration) => null;
    
    getOriginalVMInstall(ILaunchConfiguration configuration) => VMHelper.createLauncher(configuration);
    
    shouldStopInMain(ILaunchConfiguration configuration) => false;
}

shared class CeylonAwarePDEJUnitLaunchConfigurationDelegate()
        extends PDEJUnitLaunchConfigurationDelegate()
        satisfies CeylonAwareLaunchConfigurationDelegate
        & CeylonDebuggingSupportEnabled {

    overridenSourceLocator()
            => object extends PDESourceLookupDirector()
            satisfies CeylonAwareSourceLookupDirector {};

    launch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of CeylonAwareLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    originalLaunch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of PDEJUnitLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    handleDebugEvents(ObjectArray<DebugEvent> _DebugEventArray) =>
            (super of CeylonDebuggingSupportEnabled).handleDebugEvents(_DebugEventArray);
    
    getOriginalVMArguments(ILaunchConfiguration configuration) => 
            (super of PDEJUnitLaunchConfigurationDelegate).getVMArguments(configuration);
    
    getVMArguments(ILaunchConfiguration configuration) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMArguments(configuration);
    
    getOriginalVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of PDEJUnitLaunchConfigurationDelegate).getVMRunner(configuration, mode);
    
    getVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMRunner(configuration, mode);
    
    getStartLocation(ILaunchConfiguration configuration) => null;
    
    getOriginalVMInstall(ILaunchConfiguration configuration) =>
            (super of PDEJUnitLaunchConfigurationDelegate).getVMInstall(configuration);
    
    shouldStopInMain(ILaunchConfiguration configuration) => false;
}

shared class CeylonAwareSWTBotJUnitLaunchConfigurationDelegate()
        extends PDEJUnitLaunchConfigurationDelegate()
        satisfies CeylonAwareLaunchConfigurationDelegate
        & CeylonDebuggingSupportEnabled {

    overridenSourceLocator()
            => object extends PDESourceLookupDirector()
            satisfies CeylonAwareSourceLookupDirector {};

    launch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of CeylonAwareLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    originalLaunch(
        ILaunchConfiguration c,
        String m,
        ILaunch l,
        IProgressMonitor p)
            => (super of PDEJUnitLaunchConfigurationDelegate)
            .launch(c, m, l, p);

    getApplication(ILaunchConfiguration configuration)
            => "org.eclipse.swtbot.eclipse.core.swtbottestapplication";

    
    handleDebugEvents(ObjectArray<DebugEvent> _DebugEventArray) =>
            (super of CeylonDebuggingSupportEnabled).handleDebugEvents(_DebugEventArray);
    
    getOriginalVMArguments(ILaunchConfiguration configuration) => 
            (super of PDEJUnitLaunchConfigurationDelegate).getVMArguments(configuration);
    
    getVMArguments(ILaunchConfiguration configuration) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMArguments(configuration);
    
    getOriginalVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of PDEJUnitLaunchConfigurationDelegate).getVMRunner(configuration, mode);
    
    getVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMRunner(configuration, mode);
    
    getStartLocation(ILaunchConfiguration configuration) => null;
    
    getOriginalVMInstall(ILaunchConfiguration configuration) =>
            (super of PDEJUnitLaunchConfigurationDelegate).getVMInstall(configuration);
    
    shouldStopInMain(ILaunchConfiguration configuration) => false;
}

