package com.redhat.ceylon.eclipse.android.plugin;

import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IBuildContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.jarpackager.JarPackageData;
import org.eclipse.jdt.ui.jarpackager.JarWriter3;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.BooleanHolder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;
import com.redhat.ceylon.eclipse.core.builder.ICeylonBuildHookProvider;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class AndroidBuildHookProvider implements ICeylonBuildHookProvider {
    private static final class AndroidCeylonBuildHook extends CeylonBuildHook {
        public static final String CEYLON_GENERATED_ARCHIVES_PREFIX = "ceylonGenerated-";
        public static final String CEYLON_GENERATED_CLASSES_ARCHIVE = CEYLON_GENERATED_ARCHIVES_PREFIX + "CeylonClasses.jar";
        public static final String ANDROID_LIBS_DIRECTORY = "libs";
        public static final  String[] ANDROID_PROVIDED_PACKAGES = new String[] {"android.app"};
        public static final  String[] UNNECESSARY_CEYLON_RUNTIME_LIBRARIES = new String[] {"org.jboss.modules",
                                                                                                "com.redhat.ceylon.module-resolver",
                                                                                                "com.redhat.ceylon.common"};
        boolean areModulesChanged = false;
        boolean hasAndroidNature = false;
        boolean isReentrantBuild = false;
        boolean isFullBuild = false;
        WeakReference<IProgressMonitor> monitorRef = null;
        WeakReference<IProject> projectRef = null;

        private IProgressMonitor getMonitor() {
            if (monitorRef != null) {
                return monitorRef.get();
            }
            return null;
        }

        private IProject getProject() {
            if (projectRef != null) {
                return projectRef.get();
            }
            return null;
        }

        @Override
        protected void startBuild(int kind, @SuppressWarnings("rawtypes") Map args, 
                IProject project, IBuildConfiguration config, IBuildContext context, IProgressMonitor monitor) throws CoreException {
            try {
                hasAndroidNature = project.hasNature("com.android.ide.eclipse.adt.AndroidNature");
            } catch (CoreException e) {
                hasAndroidNature= false;
            }
            areModulesChanged = false;
            monitorRef = new WeakReference<IProgressMonitor>(monitor);
            projectRef = new WeakReference<IProject>(project);
            isReentrantBuild = args.containsKey(CeylonBuilder.BUILDER_ID + ".reentrant");
            if (hasAndroidNature) {
                IJavaProject javaProject =JavaCore.create(project);
                boolean CeylonCPCFound = false;
                IMarker[] buildMarkers = project.findMarkers(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, DEPTH_ZERO);
                for (IMarker m: buildMarkers) {
                    if (CeylonAndroidPlugin.PLUGIN_ID.equals(m.getAttribute(IMarker.SOURCE_ID))) {
                        m.delete();
                    }
                }
                for (IClasspathEntry entry : javaProject.getRawClasspath()) {
                    if (CeylonClasspathUtil.isProjectModulesClasspathContainer(entry.getPath())) {
                        CeylonCPCFound = true;
                    } else {
                        IPath containerPath = entry.getPath();
                        int size = containerPath.segmentCount();
                        if (size > 0) {
                            if (containerPath.segment(0).equals("com.android.ide.eclipse.adt.LIBRARIES") ||
                                    containerPath.segment(0).equals("com.android.ide.eclipse.adt.DEPENDENCIES")) {
                                if (! CeylonCPCFound) {
                                    //if the ClassPathContainer is missing, add an error
                                    IMarker marker = project.createMarker(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER);
                                    marker.setAttribute(IMarker.SOURCE_ID, CeylonAndroidPlugin.PLUGIN_ID);
                                    marker.setAttribute(IMarker.MESSAGE, "Invalid Java Build Path for project " + project.getName() + " : " +
                                            "The Ceylon libraries should be set before the Android libraries in the Java Build Path. " + 
                                            "Move down the 'Android Private Libraries' and 'Android Dependencies' after the Ceylon Libraries " +
                                            "in the 'Order and Export' tab of the 'Java Build Path' properties page.");
                                    marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                                    marker.setAttribute(IMarker.LOCATION, "Java Build Path Order");
                                    throw new CoreException(new Status(IStatus.CANCEL, CeylonAndroidPlugin.PLUGIN_ID, IResourceStatus.OK, 
                                            "Build cancelled because of invalid build path", null));
                                }
                            }
                        }
                        
                    }
                }
            }
        }

        @Override
        protected void deltasAnalyzed(List<IResourceDelta> currentDeltas, 
                BooleanHolder sourceModified, 
                BooleanHolder mustDoFullBuild,
                BooleanHolder mustResolveClasspathContainer,
                boolean mustContinueBuild) {
            if (mustContinueBuild && hasAndroidNature) {
                CeylonBuilder.waitForUpToDateJavaModel(10000, getProject(), getMonitor());
            }
        }

        @Override
        protected void setAndRefreshClasspathContainer() {
            areModulesChanged = true;
        }

        @Override
        protected void doFullBuild() {
            isFullBuild = true;
        }

        @Override
        protected void afterGeneratingBinaries() {
            IProject project = getProject();
            if (project == null) {
                return;
            }
            
            if (! isReentrantBuild && hasAndroidNature) {
                try {
                    File libsDirectory = project.findMember(ANDROID_LIBS_DIRECTORY).getLocation().toFile();
                    
                    if (!libsDirectory.exists()) {
                        libsDirectory.mkdirs();
                    }
                    
                    Files.walkFileTree(java.nio.file.FileSystems.getDefault().getPath(libsDirectory.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException
                        {
                            if (areModulesChanged || isFullBuild ? 
                                    path.getFileName().toString().startsWith(CEYLON_GENERATED_ARCHIVES_PREFIX) :
                                        path.getFileName().toString().equals(CEYLON_GENERATED_CLASSES_ARCHIVE)) {
                                try {
                                    Files.delete(path);
                                } catch(IOException ioe) {
                                    CeylonAndroidPlugin.logError("Could not delete a ceylon jar from the android libs directory", ioe);
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });

                    final List<IFile> filesToAddInArchive = new LinkedList<>();
                    final IFolder ceylonOutputFolder = CeylonBuilder.getCeylonClassesOutputFolder(project);
                    ceylonOutputFolder.refreshLocal(DEPTH_INFINITE, getMonitor());
                    ceylonOutputFolder.accept(new IResourceVisitor() {
                        @Override
                        public boolean visit(IResource resource) throws CoreException {
                            if (resource instanceof IFile) {
                                filesToAddInArchive.add((IFile)resource);
                            }
                            return true;
                        }
                    });
                    if (! filesToAddInArchive.isEmpty()) {
                        JarPackageData jarPkgData = new JarPackageData();
                        jarPkgData.setBuildIfNeeded(false);
                        jarPkgData.setOverwrite(true);
                        jarPkgData.setGenerateManifest(true);
                        jarPkgData.setExportClassFiles(true);
                        jarPkgData.setCompress(true);
                        jarPkgData.setJarLocation(project.findMember("libs").getLocation().append(CEYLON_GENERATED_CLASSES_ARCHIVE).makeAbsolute());
                        jarPkgData.setElements(filesToAddInArchive.toArray());
                        JarWriter3 jarWriter = null;
                        try  {
                            jarWriter = new JarWriter3(jarPkgData, null);
                            for (IFile fileToAdd : filesToAddInArchive) {
                                jarWriter.write(fileToAdd, fileToAdd.getFullPath().makeRelativeTo(ceylonOutputFolder.getFullPath()));
                            }
                            
                        } finally {
                            if (jarWriter != null) {
                                jarWriter.close();
                            }
                        }
                    }
                    
                    if (isFullBuild || areModulesChanged) {
                        List<Path> jarsToCopyToLib = new LinkedList<>();
                        IJavaProject javaProject = JavaCore.create(project);
                        List<IClasspathContainer> cpContainers = CeylonClasspathUtil.getCeylonClasspathContainers(javaProject);
                        if (cpContainers != null) {
                            for (IClasspathContainer cpc : cpContainers) {
                                for (IClasspathEntry cpe : cpc.getClasspathEntries()) {
                                    if (cpe.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                                        Path path = FileSystems.getDefault().getPath(cpe.getPath().toOSString());
                                        if (! Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) &&
                                                Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
                                            boolean isAndroidProvidedJar = false;
                                            providerPackageFound:
                                            for (IPackageFragmentRoot root : javaProject.getAllPackageFragmentRoots()) {
                                                if (javaProject.isOnClasspath(root) && 
                                                        cpe.equals(root.getResolvedClasspathEntry())) {
                                                    for (String providedPackage : ANDROID_PROVIDED_PACKAGES) {
                                                        if (root.getPackageFragment(providedPackage).exists()) {
                                                            isAndroidProvidedJar = true;
                                                            break providerPackageFound;
                                                        }
                                                    }
                                                }
                                            }
                                            if (! isAndroidProvidedJar) {
                                                jarsToCopyToLib.add(path);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for (String runtimeJar : CeylonPlugin.getRuntimeRequiredJars()) {
                            boolean isNecessary = true;
                            for (String unnecessaryRuntime : UNNECESSARY_CEYLON_RUNTIME_LIBRARIES) {
                                if (runtimeJar.contains(unnecessaryRuntime + "-")) {
                                    isNecessary = false;
                                    break;
                                }
                            }
                            if (isNecessary) {
                                jarsToCopyToLib.add(FileSystems.getDefault().getPath(runtimeJar));
                            }
                        }
                        for (Path archive : jarsToCopyToLib) {
                            String newName = CEYLON_GENERATED_ARCHIVES_PREFIX + archive.getFileName();
                            if (newName.endsWith(ArtifactContext.CAR)) {
                                newName = newName.replaceFirst("\\.car$", "\\.jar");
                            }
                            Path destinationPath = FileSystems.getDefault().getPath(project.findMember(ANDROID_LIBS_DIRECTORY).getLocation().toOSString(), newName);
                            try {
                                Files.copy(archive, destinationPath);
                            } catch (IOException e) {
                                CeylonAndroidPlugin.logError("Could not copy a ceylon jar to the android libs directory", e);
                            }
                        }
                    }
                    project.findMember(ANDROID_LIBS_DIRECTORY).refreshLocal(DEPTH_INFINITE, getMonitor());
                } catch (Exception e) {
                    CeylonAndroidPlugin.logError("Error during the generation of ceylon-derived archives for Android", e);
                }
            }
        }

        @Override
        protected void endBuild() {
            areModulesChanged = false;
            hasAndroidNature = false;
            isReentrantBuild = false;
            isFullBuild = false;
            monitorRef = null;
            projectRef = null;
        }
    }

    private static CeylonBuildHook buildHook = new AndroidCeylonBuildHook();

    @Override
    public CeylonBuildHook getHook() {
        return buildHook;
    }
}
