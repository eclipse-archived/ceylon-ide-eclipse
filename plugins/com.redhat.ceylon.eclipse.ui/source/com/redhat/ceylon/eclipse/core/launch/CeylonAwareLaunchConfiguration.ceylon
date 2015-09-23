import org.eclipse.debug.core {
    ILaunch,
    ILaunchConfiguration,
    DebugPlugin
}
import org.eclipse.debug.core.model {
    ILaunchConfigurationDelegate
}
import org.eclipse.jdt.launching {
    JavaLaunchDelegate
}
import org.eclipse.debug.core.sourcelookup {
    ISourceLookupDirector
}
import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.jdt.internal.launching {
    JavaRemoteApplicationLaunchConfigurationDelegate
}
import org.eclipse.jdt.junit.launcher {
    JUnitLaunchConfigurationDelegate
}
import org.eclipse.pde.launching {
    EclipseApplicationLaunchConfiguration,
    PDEJUnitLaunchConfigurationDelegate=JUnitLaunchConfigurationDelegate
}
import org.eclipse.pde.internal.launching.sourcelookup {
    PDESourceLookupDirector
}


shared interface CeylonAwareLaunchConfigurationDelegate satisfies ILaunchConfigurationDelegate {
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
            => (super of JavaLaunchDelegate)
            .launch(c, m, l, p);
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
            => (super of JUnitLaunchConfigurationDelegate)
            .launch(c, m, l, p);
}

shared class CeylonAwareEclipseApplicationLaunchConfiguration()
        extends EclipseApplicationLaunchConfiguration()
        satisfies CeylonAwareLaunchConfigurationDelegate {

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
}

shared class CeylonAwarePDEJUnitLaunchConfigurationDelegate()
        extends PDEJUnitLaunchConfigurationDelegate()
        satisfies CeylonAwareLaunchConfigurationDelegate {

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
}

shared class CeylonAwareSWTBotJUnitLaunchConfigurationDelegate()
        extends PDEJUnitLaunchConfigurationDelegate()
        satisfies CeylonAwareLaunchConfigurationDelegate {

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
}

