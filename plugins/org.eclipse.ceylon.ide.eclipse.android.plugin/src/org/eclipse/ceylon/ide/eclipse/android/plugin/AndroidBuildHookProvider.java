/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.android.plugin;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IBuildContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.util.CoreUtility;

import org.eclipse.ceylon.cmr.api.ArtifactContext;
import org.eclipse.ceylon.cmr.api.RepositoryManager;
import org.eclipse.ceylon.cmr.ceylon.CeylonUtils.CeylonRepoManagerBuilder;
import org.eclipse.ceylon.cmr.ceylon.LegacyImporter;
import org.eclipse.ceylon.common.FileUtil;
import org.eclipse.ceylon.common.Versions;
import org.eclipse.ceylon.compiler.java.runtime.model.TypeDescriptor;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.BooleanHolder;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;
import org.eclipse.ceylon.ide.eclipse.core.builder.ICeylonBuildHookProvider;
import org.eclipse.ceylon.ide.eclipse.core.classpath.CeylonClasspathUtil;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import org.eclipse.ceylon.ide.common.model.CeylonProjectConfig;
import org.eclipse.ceylon.ide.common.model.IdeModule;
import org.eclipse.ceylon.model.cmr.ArtifactResult;
import org.eclipse.ceylon.model.loader.JvmBackendUtil;
import org.eclipse.ceylon.model.typechecker.model.Module;

import ceylon.interop.java.CeylonIterable;
import ceylon.interop.java.JavaIterable;

@SuppressWarnings("restriction")
public class AndroidBuildHookProvider implements ICeylonBuildHookProvider {
    private static final class AndroidCeylonBuildHook extends CeylonBuildHook {
        public static final String CEYLON_RENAMED_CARS_FOLDER = ".renamed-cars-for-android";
        public static final String CEYLON_RENAMED_CARS_CPC_NAME = CeylonAndroidPlugin.PLUGIN_ID + ".RENAMED_CARS";
        private static final String[] NECESSARY_CEYLON_RUNTIME_LIBRARIES = new String[] {
                "org.eclipse.ceylon.module-resolver-"+Versions.CEYLON_VERSION_NUMBER+".jar",
                "org.eclipse.ceylon.common-"+Versions.CEYLON_VERSION_NUMBER+".jar",
                "org.eclipse.ceylon.model-"+Versions.CEYLON_VERSION_NUMBER+".jar",
                "org.eclipse.ceylon.langtools.classfile-"+Versions.CEYLON_VERSION_NUMBER+".jar",
            };
        private static Path CPC_PATH = new Path(CEYLON_RENAMED_CARS_CPC_NAME + "/default");

        boolean configFileChanged = false;
        boolean areModulesChanged = false;
        boolean hasAdtNature = false;
        boolean hasAndMoreNature = false;
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

        private boolean hasAndroidNature() {
        	return hasAdtNature || hasAndMoreNature;
        }

        public class AndroidRenamedCarsContainer implements IClasspathContainer {

            private IClasspathEntry[] classpathEntries = new IClasspathEntry[0];
            
            @Override
			public IPath getPath() {
				return CPC_PATH;
			}
			
			@Override
			public int getKind() {
				return IClasspathContainer.K_APPLICATION;
			}
			
			@Override
			public String getDescription() {
				return null;
			}
			
			@Override
			public IClasspathEntry[] getClasspathEntries() {
				return classpathEntries;
			}

			public void resetClasspathEntries() throws CoreException {
				ArrayList<IClasspathEntry> entries = new ArrayList<>();
				IFolder renamedCarsFolder = getProject().getFolder(CEYLON_RENAMED_CARS_FOLDER);
				renamedCarsFolder.refreshLocal(IResource.DEPTH_ONE, getMonitor());
				if (! renamedCarsFolder.exists()) {
                    CoreUtility.createDerivedFolder(renamedCarsFolder, true, true, getMonitor());
				}
				if (renamedCarsFolder.isHidden()) {
					renamedCarsFolder.setHidden(false);
				}
				
				for (IResource member : renamedCarsFolder.members(IContainer.INCLUDE_HIDDEN | IContainer.INCLUDE_PHANTOMS)) {
					member.delete(true, getMonitor());
				}
				
				IJavaProject javaProject = JavaCore.create(getProject());
				for (IClasspathContainer ceylonContainer : 
					CeylonClasspathUtil.getCeylonClasspathContainers(javaProject)) {
					for (IClasspathEntry ceylonEntry : ceylonContainer.getClasspathEntries()) {
						if (ceylonEntry.getPath().getFileExtension().equalsIgnoreCase("CAR")) {
                            java.nio.file.Path sourcePath = FileSystems.getDefault().getPath(ceylonEntry.getPath().toOSString());
							
                            String ceylonCarName = ceylonEntry.getPath().lastSegment();
							String jarName = ceylonCarName.substring(0, ceylonCarName.length()-3) + "jar";
							
			                java.nio.file.Path destinationPath = FileSystems.getDefault().getPath(renamedCarsFolder.getLocation().toOSString(), jarName);
			                
			                try {
			                    Files.copy(sourcePath, destinationPath);
			                } catch (IOException e) {
			                    CeylonAndroidPlugin.logError("Could not copy a ceylon jar to the android libs directory", e);
			                }
						}
					}
				}
				
				renamedCarsFolder.refreshLocal(IResource.DEPTH_INFINITE, getMonitor());
				
				for (IResource resource : renamedCarsFolder.members(IContainer.INCLUDE_HIDDEN | IContainer.INCLUDE_PHANTOMS)) {
					if (resource.getFileExtension().equals("jar")) {
						IClasspathEntry entry = JavaCore.newLibraryEntry(resource.getFullPath(), null, null);
						entries.add(entry);
					}
				}

				for (String requiredPath : CeylonPlugin.getRequiredJars(NECESSARY_CEYLON_RUNTIME_LIBRARIES)) {
					IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(requiredPath), null, null);
					entries.add(entry);
				}
				classpathEntries = new IClasspathEntry[entries.size()];
				classpathEntries = entries.toArray(classpathEntries);
			}
        }
        
        private AndroidRenamedCarsContainer getRenamedCarsCPC(IProject project) throws CoreException {
            IJavaProject javaProject = JavaCore.create(project);
            IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
            for (IClasspathEntry entry : oldEntries) {
            	if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
            			entry.getPath().segment(0).equals(CEYLON_RENAMED_CARS_CPC_NAME)) {
            		IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(CPC_PATH, javaProject);
            		if (classpathContainer instanceof AndroidRenamedCarsContainer) {
    					return (AndroidRenamedCarsContainer) classpathContainer;
            		} else {
                        AndroidRenamedCarsContainer cpc = new AndroidRenamedCarsContainer();
                        cpc.resetClasspathEntries();
                        JavaCore.setClasspathContainer(cpc.getPath(), new IJavaProject[] { javaProject } , new IClasspathContainer[] { cpc }, getMonitor());
                        return cpc;
            		}
            	}
            }

            
            AndroidRenamedCarsContainer cpc = new AndroidRenamedCarsContainer();
            cpc.resetClasspathEntries();
            
            IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
            System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
            IClasspathEntry renamedCarsContainerEntry = JavaCore.newContainerEntry(cpc.getPath(), true);
            newEntries[oldEntries.length] = renamedCarsContainerEntry;
            javaProject.setRawClasspath(newEntries, getMonitor());
            JavaCore.setClasspathContainer(cpc.getPath(), new IJavaProject[] { javaProject } , new IClasspathContainer[] { cpc }, getMonitor());
            return cpc;
        }

        private String getProjectAndroidVersion(IProject project) {
            try {
                java.io.File projectProperties = new java.io.File(project.getLocation().toFile(), "project.properties");
                if (projectProperties.exists()) {
                    Properties props = new Properties();
                    props.load(new FileReader(projectProperties));
                    String target = props.getProperty("target");
                    if (target.indexOf('-') > 0) {
                       return target.split("-")[1];
                    }
                }
            } catch (IOException e) {
                CeylonAndroidPlugin.logError("Exception occured in the CeylonAndroidPlugin", e);
            }
            return null;
        }
        
        
        private String getSdkHome() {
            IEclipsePreferences preferences = InstanceScope.INSTANCE
                    .getNode("org.eclipse.andmore");
            if (preferences != null) {
                return preferences.get("org.eclipse.andmore.sdk", null);            
            }
            return null;
        }
        
        private java.io.File getAndroidSupportInstalledFolder() {
            String sdkHome = getSdkHome();
            if (sdkHome != null) {
                java.io.File supportFolder = new java.io.File(new java.io.File(new java.io.File(sdkHome, "extras"), "android"), "support");
                if (supportFolder.exists()) {
                    return supportFolder;
                }
            }
            return null;
        }

        private String getAndroidSupportInstalledVersion() {
            java.io.File supportFolder = getAndroidSupportInstalledFolder();
            if (supportFolder != null) {
                try {
                    java.io.File supportSourceProperties = new java.io.File(supportFolder, "source.properties");
                    if (supportSourceProperties.exists()) {
                        Properties props = new Properties();
                        props.load(new FileReader(supportSourceProperties));
                        return props.getProperty("Pkg.Revision");
                    }
                } catch (IOException e) {
                    CeylonAndroidPlugin.logError("Exception occured in the CeylonAndroidPlugin", e);
                }
            }
            return null;
        }

        private File getCeylonAndroidRepositoryFile(IProject project) {
            java.io.File androidCeylonRepository = new java.io.File(project.getLocation().toFile(), "androidCeylonRepository");
            return androidCeylonRepository;
        }
        
        private List<File> getFilesInClasspathContainer(String cpcName) {
            IJavaProject javaProject = JavaCore.create(getProject());
            ArrayList<File> files = new ArrayList<File>();
            try {
                for (IClasspathEntry entry : javaProject.getRawClasspath()) {
                    if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
                        entry.getPath().segment(0).equals(cpcName)) {
                            IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), javaProject);
                            if (container != null){
                                for (IClasspathEntry cpcEntry : container.getClasspathEntries()) {
                                    if (cpcEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                                        files.add(cpcEntry.getPath().toFile());
                                    }
                                }
                            }
                            break;
                    }
                }
            } catch (JavaModelException e) {
                CeylonAndroidPlugin.logError("", e);
            }
            return files;
        }
        
        @Override
        protected void startBuild(int kind, @SuppressWarnings("rawtypes") Map args, 
                IProject project, IBuildConfiguration config, IBuildContext context, IProgressMonitor monitor) throws CoreException {
            try {
                hasAdtNature = project.hasNature("com.android.ide.eclipse.adt.AndroidNature");
            } catch (CoreException e) {
                hasAdtNature= false;
            }
            try {
                hasAdtNature = project.hasNature("org.eclipse.andmore.AndroidNature");
            } catch (CoreException e) {
                hasAndMoreNature= false;
            }
            
            configFileChanged = false;
            areModulesChanged = false;
            
            if (!hasAndroidNature()) {
                return;
            }
            
            monitorRef = new WeakReference<IProgressMonitor>(monitor);
            projectRef = new WeakReference<IProject>(project);
            isReentrantBuild = args.containsKey(CeylonBuilder.BUILDER_ID + ".reentrant");
            	
            CeylonProjectConfig projectConfig = modelJ2C().ceylonConfig(getProject());
            
            ceylon.language.String jdkProvider = projectConfig.getProjectJdkProvider();
            String androidVersion = getProjectAndroidVersion(getProject());
            String newJdkProvider = null;
            if (androidVersion != null) {
                newJdkProvider = "android/" + androidVersion;
            }
            if (jdkProvider == null && newJdkProvider != null) {
                projectConfig.setProjectJdkProvider(new ceylon.language.String(newJdkProvider));
                configFileChanged = true;
            } else {
                String existingJdkProvider = jdkProvider.toString();
                if (newJdkProvider != null && 
                        ! newJdkProvider.equals(existingJdkProvider)) {
                    projectConfig.setProjectJdkProvider(new ceylon.language.String(newJdkProvider));
                    configFileChanged = true;
                }
            }

            final TypeDescriptor stringTD = TypeDescriptor.klass(ceylon.language.String.class);
            ceylon.language.String ceylonAndroidRepository = 
                    new ceylon.language.String("./" + getCeylonAndroidRepositoryFile(getProject()).getName());
            if (! projectConfig.getProjectLocalRepos().contains(
                    ceylonAndroidRepository)) {
                ArrayList<ceylon.language.String> newProjectLoclRepos = new ArrayList<>();
                Iterable<ceylon.language.String> javaIter = 
                        new JavaIterable<ceylon.language.String>(
                                stringTD, 
                                projectConfig.getProjectLocalRepos());
                newProjectLoclRepos.add(ceylonAndroidRepository);
                for (ceylon.language.String oldRepo : javaIter) {
                    newProjectLoclRepos.add(oldRepo);
                }
                projectConfig.setProjectLocalRepos(new CeylonIterable<ceylon.language.String>(stringTD, newProjectLoclRepos).sequence());
                configFileChanged = true;
            }
            
            if (configFileChanged) {
                projectConfig.save();
            }
            
            /*
            if (hasAndroidNature()) {
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
*/
        }

        String[][] supportLibraries = new String[][] {
            new String[] { "android-support-annotations.jar", "support-annotations" },
            new String[] { "android-support-customtabs.jar", "customtabs" },
            new String[] { "android-support-design.jar", "design" },
            new String[] { "android-support-multidex.jar", "multidex" },
            new String[] { "android-support-percent.jar", "percent" },
            new String[] { "android-support-recommendation.jar", "recommendation" },
            new String[] { "android-support-v4.jar", "support-v4" },
            new String[] { "android-support-v7-appcompat.jar", "appcompat-v7", "support-v4.jar"},
            new String[] { "android-support-v7-cardview.jar", "cardview-v7"},
            new String[] { "android-support-v7-gridlayout.jar", "gridlayout-v7"},
            new String[] { "android-support-v7-mediarouter.jar", "mediarouter-v7"},
            new String[] { "android-support-v7-palette.jar", "palette-v7"},
            new String[] { "android-support-v7-preference.jar", "preference-v7"},
            new String[] { "android-support-v7-recyclerview.jar", "recyclerview-v7"},
            new String[] { "android-support-v13.jar", "support-v13" },
            new String[] { "android-support-v14-preference.jar", "preference-v14"},
            new String[] { "android-support-v17-leanback.jar", "leanback-v17"},
            new String[] { "android-support-v17-preference-leanback.jar", "preference-leanback-v17"}
        };
        
        @Override
        protected void deltasAnalyzed(List<IResourceDelta> currentDeltas, 
                BooleanHolder sourceModified, 
                BooleanHolder mustDoFullBuild,
                BooleanHolder mustResolveClasspathContainer,
                boolean mustContinueBuild) {
            if (! mustContinueBuild || ! hasAndroidNature()) {
                return;
            }
            CeylonBuilder.waitForUpToDateJavaModel(10000, getProject(), getMonitor());
            CeylonProjectConfig projectConfig = modelJ2C().ceylonConfig(getProject());
            
            if (configFileChanged) {
                mustResolveClasspathContainer.value = true;
                mustDoFullBuild.value = true;
            }
            
            if (mustResolveClasspathContainer.value || mustDoFullBuild.value) {
                String androidVersion = getProjectAndroidVersion(getProject());
                String androidSupportVersion = getAndroidSupportInstalledVersion();
                if (androidVersion != null) {
                    File androidCeylonRepo = getCeylonAndroidRepositoryFile(getProject());
                    if (androidCeylonRepo.exists()) {
                        FileUtil.delete(androidCeylonRepo);
                    }
                    
                    RepositoryManager outputRepoMgr = new CeylonRepoManagerBuilder()
                    .cwd(getProject().getLocation().toFile())
                    .outRepo("./" + androidCeylonRepo.getName())
                    .buildOutputManager();
                    
                    androidCeylonRepo.mkdirs();
                    for (File archive : getFilesInClasspathContainer("org.eclipse.andmore.ANDROID_FRAMEWORK")) {
                        if (archive.getName().equals("android.jar")) {
                            LegacyImporter importer = new LegacyImporter("android", androidVersion, archive, outputRepoMgr, outputRepoMgr);
                            importer.publish();
                            break;
                        }
                    }
                    
                    if (androidSupportVersion != null) {
                        for (File archive : getFilesInClasspathContainer("org.eclipse.andmore.LIBRARIES")) {
                            if (archive.getName().startsWith("android-support-")) {
                                for (String[] libDesc : supportLibraries) {
                                    if (libDesc[0].equals(archive.getName())) {
                                        String moduleName = "com.android.support." + libDesc[1];
                                        File propsFile = null;
                                        FileWriter writer = null;
                                        try {
                                            propsFile = File.createTempFile(moduleName, ".properties");
                                            writer = new FileWriter(propsFile);
                                            writer.write("+android=" + androidVersion);
                                            if (libDesc.length > 2) {
                                                for (int i = 2; i<libDesc.length; i++) {
                                                    String dep = libDesc[i];
                                                    writer.write("+" + dep + "=" + androidSupportVersion);
                                                }
                                            }
                                            writer.flush();
                                            writer.close();
                                            
                                            LegacyImporter importer = new LegacyImporter(moduleName, androidSupportVersion, archive, outputRepoMgr, outputRepoMgr);
                                            importer.moduleDescriptor(propsFile);
                                            importer.publish();
                                            break;
                                        } catch (IOException e) {
                                            CeylonAndroidPlugin.logError("", e);
                                        } finally {
                                            if (writer!= null) {
                                                try {
                                                    writer.close();
                                                } catch (IOException e) {}
                                            }
                                            if (propsFile != null) {
                                                propsFile.delete();
                                                propsFile.deleteOnExit();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

            if (! isReentrantBuild && hasAndroidNature()) {

                try {
                    AndroidRenamedCarsContainer renamedCarsContainer = getRenamedCarsCPC(project);
                	
                	if (isFullBuild || areModulesChanged) {
                    	renamedCarsContainer.resetClasspathEntries();
                    }    
                	
                	@SuppressWarnings("rawtypes")
                    CeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(getProject());
                	
                	ArrayList<ArtifactResult> moduleArtifacts = new ArrayList<>();
                	RepositoryManager repositoryManager = ceylonProject.newRepositoryManagerBuilder(true).buildManager();
                	for (Module m : CeylonBuilder.getProjectDeclaredSourceModules(getProject())) {
                		ArtifactContext artifactContext = new ArtifactContext(null, m.getNameAsString(), m.getVersion(), ArtifactContext.CAR);
						ArtifactResult result = repositoryManager.getArtifactResult(artifactContext);
                		if (result != null) {
                			moduleArtifacts.add(result);
                		} else {
                            CeylonAndroidPlugin.logError("Generated Car " + artifactContext + " not found in output repository manager", null);
                		}
                	}

                	for (Module m : CeylonBuilder.getProjectExternalModules(getProject())) {
                		if (m instanceof IdeModule) {
                		    @SuppressWarnings("rawtypes")
                            IdeModule ideModule = (IdeModule) m;
                            if (! (ideModule.getIsCeylonBinaryArchive() || ideModule.getIsJavaBinaryArchive())) {
                                continue;
                            }
                            
                            ArtifactContext artifactContext = new ArtifactContext(null, m.getNameAsString(), m.getVersion(), ArtifactContext.CAR, ArtifactContext.JAR);
                            ArtifactResult result = repositoryManager.getArtifactResult(artifactContext);
                            if (result != null) {
                                moduleArtifacts.add(result);
                            } else {
                                CeylonAndroidPlugin.logError("Dependency archive " + artifactContext + " not found in output repository manager", null);
                            }
                 		}
                	}
                	
                	java.io.File explodedDir = CeylonBuilder.getCeylonClassesOutputDirectory(getProject());
                	JvmBackendUtil.writeStaticMetamodel(
                			explodedDir,
                			moduleArtifacts,
                			ceylonProject.getModelLoader().getJdkProvider(),
                			"ANDROID-META-INF");
                	
                } catch(Exception e) {
                    CeylonAndroidPlugin.logError("Exception in Ceylon Android Plugin", e);
                }
            }
        }

        @Override
        protected void endBuild() {
            configFileChanged = false;
            areModulesChanged = false;
            hasAdtNature = false;
            hasAndMoreNature = false;
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
