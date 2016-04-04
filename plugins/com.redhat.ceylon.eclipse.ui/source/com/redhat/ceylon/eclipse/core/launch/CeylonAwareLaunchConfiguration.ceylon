import ceylon.collection {
    HashSet
}
import ceylon.interop.java {
    CeylonIterable
}

import com.redhat.ceylon.cmr.ceylon {
    CeylonUtils {
        CeylonRepoManagerBuilder
    }
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.eclipse.core.model {
    ceylonModel,
    JDTModule
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils
}
import com.redhat.ceylon.ide.common.util {
    toJavaStringList
}
import com.redhat.ceylon.model.cmr {
    ArtifactResult
}
import com.redhat.ceylon.model.typechecker.model {
    Modules
}
import com.redhat.ceylon.tools.classpath {
    CeylonClasspathTool
}

import java.io {
    File
}
import java.lang {
    JString=String,
    ObjectArray
}

import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.debug.core {
    ILaunch,
    ILaunchConfiguration,
    DebugPlugin
}
import org.eclipse.debug.core.model {
    ILaunchConfigurationDelegate
}
import org.eclipse.debug.core.sourcelookup {
    ISourceLookupDirector
}
import org.eclipse.jdt.core {
    IJavaProject
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
import org.eclipse.pde.internal.launching.sourcelookup {
    PDESourceLookupDirector
}
import org.eclipse.pde.launching {
    EclipseApplicationLaunchConfiguration,
    PDEJUnitLaunchConfigurationDelegate=JUnitLaunchConfigurationDelegate
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

String[] requiredRuntimeJars = [
    "ceylon.bootstrap",
    "com.redhat.ceylon.module-resolver",
    "com.redhat.ceylon.common",
    "com.redhat.ceylon.model",
    "org.jboss.modules"
];


shared interface ClassPathEnricher {
    shared ObjectArray<JString> enrichClassPath(ObjectArray<JString> original, ILaunchConfiguration launchConfig) {
        IJavaProject? javaProject = getTheJavaProject(launchConfig);
        if (!exists javaProject) {
            return original;
        }
        value project = javaProject.project;
        
        
        value classpathEntries = HashSet<String>();
        for (referencedProject in 
                        project.referencedProjects.array.coalesced
                            .map((p) => ceylonModel.getProject(p))
                            .coalesced) {
            CeylonRepoManagerBuilder repoManagerBuilder = CeylonRepoManagerBuilder().offline(referencedProject.configuration.offline)
                            .cwd(referencedProject.rootDirectory)
                            .systemRepo(referencedProject.systemRepository)
                            .outRepo(CeylonBuilder.getCeylonModulesOutputDirectory(referencedProject.ideArtifact).absolutePath)
                            .extraUserRepos(
                                toJavaStringList(
                                    referencedProject.referencedCeylonProjects.map((p) 
                                        => p.ceylonModulesOutputDirectory.absolutePath)))
                            .logger(platformUtils.cmrLogger)
                            .isJDKIncluded(false);
                    
            Modules? modules = CeylonBuilder.getProjectModules(referencedProject.ideArtifact);
            if (exists modules) {
                function moduleClassPath(JDTModule m) {
                    object tool extends CeylonClasspathTool() {
                        shared {ArtifactResult*} modules => CeylonIterable(super.loadedModules.values());
                        createRepositoryManagerBuilderNoOut(Boolean forInput) => repoManagerBuilder;
                    }
                    tool.setModules(toJavaStringList{m.nameAsString+"/"+m.version});
                    tool.run();
                    return tool.modules;
                }
                value moduleList = CeylonIterable(modules.listOfModules)
                    .narrow<JDTModule>()
                    .filter((m)=>m.isProjectModule && !m.default)
                    .flatMap(
                        (m) => 
                            moduleClassPath(m))
                    .coalesced
                    .map(
                        (artifactResult) => 
                            artifactResult.artifact()?.absolutePath)
                    .coalesced;
                classpathEntries.addAll(moduleList);
                value defaultCar = File(
                            CeylonBuilder.getCeylonModulesOutputDirectory(referencedProject.ideArtifact),
                            "default.car");
                if (defaultCar.\iexists()) {
                    classpathEntries.add(defaultCar.absolutePath);
                }
            }
        }
        classpathEntries.addAll(
            original.iterable.coalesced.map(
                (js) => js.string));
        value result = ObjectArray<JString>(classpathEntries.size);
        return toJavaStringList(classpathEntries).toArray(result);
    }
    
    shared formal IJavaProject getTheJavaProject(ILaunchConfiguration launchConfiguration);
}

shared class CeylonAwareJavaLaunchDelegate()
        extends JavaLaunchDelegate()
        satisfies CeylonAwareLaunchConfigurationDelegate
                    & ClassPathEnricher {

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
    
    shared actual ObjectArray<JString> getClasspath(ILaunchConfiguration iLaunchConfiguration) { 
        return enrichClassPath(super.getClasspath(iLaunchConfiguration), iLaunchConfiguration);
    }
    shared actual IJavaProject getTheJavaProject(ILaunchConfiguration launchConfiguration) => 
            getJavaProject(launchConfiguration);
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
                    & ClassPathEnricher {

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

    shared actual ObjectArray<JString> getClasspath(ILaunchConfiguration iLaunchConfiguration) { 
        return enrichClassPath(super.getClasspath(iLaunchConfiguration), iLaunchConfiguration);
    }
    shared actual IJavaProject getTheJavaProject(ILaunchConfiguration launchConfiguration) => 
            getJavaProject(launchConfiguration);
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

