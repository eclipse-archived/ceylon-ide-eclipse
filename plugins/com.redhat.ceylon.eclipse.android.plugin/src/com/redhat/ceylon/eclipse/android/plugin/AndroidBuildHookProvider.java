package com.redhat.ceylon.eclipse.android.plugin;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.util.CoreUtility;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.BooleanHolder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.CeylonBuildHook;
import com.redhat.ceylon.eclipse.core.builder.ICeylonBuildHookProvider;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.IdeModule;
import com.redhat.ceylon.model.cmr.ArtifactResult;
import com.redhat.ceylon.model.loader.JvmBackendUtil;
import com.redhat.ceylon.model.typechecker.model.Module;

@SuppressWarnings("restriction")
public class AndroidBuildHookProvider implements ICeylonBuildHookProvider {
    private static final class AndroidCeylonBuildHook extends CeylonBuildHook {
        public static final String CEYLON_RENAMED_CARS_FOLDER = ".renamed-cars-for-android";
        public static final String CEYLON_RENAMED_CARS_CPC_NAME = CeylonAndroidPlugin.PLUGIN_ID + ".RENAMED_CARS";
        private static final String[] NECESSARY_CEYLON_RUNTIME_LIBRARIES = new String[] {
                "com.redhat.ceylon.module-resolver-"+Versions.CEYLON_VERSION_NUMBER+".jar",
                "com.redhat.ceylon.common-"+Versions.CEYLON_VERSION_NUMBER+".jar",
                "com.redhat.ceylon.model-"+Versions.CEYLON_VERSION_NUMBER+".jar",
                "com.redhat.ceylon.langtools.classfile-"+Versions.CEYLON_VERSION_NUMBER+".jar",
            };
        private static Path CPC_PATH = new Path(CEYLON_RENAMED_CARS_CPC_NAME + "/default");

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
            
            areModulesChanged = false;
            monitorRef = new WeakReference<IProgressMonitor>(monitor);
            projectRef = new WeakReference<IProject>(project);
            isReentrantBuild = args.containsKey(CeylonBuilder.BUILDER_ID + ".reentrant");
            	
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

        @Override
        protected void deltasAnalyzed(List<IResourceDelta> currentDeltas, 
                BooleanHolder sourceModified, 
                BooleanHolder mustDoFullBuild,
                BooleanHolder mustResolveClasspathContainer,
                boolean mustContinueBuild) {
            if (mustContinueBuild && hasAndroidNature()) {
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
