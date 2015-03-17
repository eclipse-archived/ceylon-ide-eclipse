package com.redhat.ceylon.eclipse.core.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.common.Constants;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.common.config.ConfigWriter;
import com.redhat.ceylon.common.config.DefaultToolOptions;
import com.redhat.ceylon.common.config.Repositories;
import com.redhat.ceylon.common.config.Repositories.Repository;
import com.redhat.ceylon.compiler.typechecker.analyzer.Warning;
import com.redhat.ceylon.eclipse.ui.CeylonEncodingSynchronizer;

public class CeylonProjectConfig {

    private static final Map<IProject, CeylonProjectConfig> PROJECT_CONFIGS = new HashMap<IProject, CeylonProjectConfig>();

    public static CeylonProjectConfig get(IProject project) {
        CeylonProjectConfig projectConfig = PROJECT_CONFIGS.get(project);
        if (projectConfig == null) {
            projectConfig = new CeylonProjectConfig(project);
            PROJECT_CONFIGS.put(project, projectConfig);
        }
        return projectConfig;
    }

    public static void remove(IProject project) {
        PROJECT_CONFIGS.remove(project);
    }

    private final IProject project;
    
    private CeylonConfig mergedConfig;
    private CeylonConfig projectConfig;
    private Repositories mergedRepositories;
    private Repositories projectRepositories;
    
    private String transientOutputRepo;
    private List<String> transientProjectLocalRepos;
    private List<String> transientProjectRemoteRepos;
    
    private boolean isOfflineChanged = false;
    private boolean isEncodingChanged = false;
    private boolean isOverridesChanged = false;
    private boolean isFlatClasspathChanged = false;
    private boolean isAutoExportMavenDependenciesChanged = false;
    private Boolean transientOffline;
    private String transientEncoding;
    private String transientOverrides;
    private Boolean transientFlatClasspath;
    private Boolean transientAutoExportMavenDependencies;

	private List<String> transientSourceDirectories;
	private List<String> transientResourceDirectories;

    private List<String> transientSuppressWarnings;
    private boolean isSuppressWarningsChanged = false;
    
    private CeylonProjectConfig(IProject project) {
        this.project = project;
        initMergedConfig();
        initProjectConfig();
    }

    private void initMergedConfig() {
        mergedConfig = CeylonConfig.createFromLocalDir(project.getLocation().toFile());
        mergedRepositories = Repositories.withConfig(mergedConfig);
    }

    private void initProjectConfig() {
        projectConfig = new CeylonConfig();
        File projectConfigFile = getProjectConfigFile();
        if (projectConfigFile.exists() && projectConfigFile.isFile()) {
            try {
                projectConfig = CeylonConfigFinder.loadConfigFromFile(projectConfigFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        projectRepositories = Repositories.withConfig(projectConfig);
    }
    
    public Repositories getMergedRepositories() {
        return mergedRepositories;
    }
    
    public Repositories getProjectRepositories() {
        return projectRepositories;
    }

    public String getOutputRepo() {
        Repository outputRepo = mergedRepositories.getOutputRepository();
        return outputRepo.getUrl();
    }

    public void setOutputRepo(String outputRepo) {
        transientOutputRepo = outputRepo;
    }

    public IPath getOutputRepoPath() {
        Repository outputRepo = mergedRepositories.getOutputRepository();
        String outputRepoUrl = outputRepo.getUrl();

        IPath outputRepoPath;
        if (outputRepoUrl.startsWith("./") || outputRepoUrl.startsWith(".\\")) {
            outputRepoPath = project.getFullPath().append(outputRepoUrl.substring(2));
        } else {
            outputRepoPath = project.getFullPath().append(outputRepoUrl);
        }

        return outputRepoPath;
    }

    public List<String> getGlobalLookupRepos() {
        return toRepositoriesUrlList(mergedRepositories.getGlobalLookupRepositories());
    }

    public List<String> getOtherRemoteRepos() {
        return toRepositoriesUrlList(mergedRepositories.getOtherLookupRepositories());
    }

    public List<String> getProjectLocalRepos() {
        return toRepositoriesUrlList(projectRepositories.getRepositoriesByType(Repositories.REPO_TYPE_LOCAL_LOOKUP));
    }

    public void setProjectLocalRepos(List<String> projectLocalRepos) {
        transientProjectLocalRepos = projectLocalRepos;
    }
    
    public List<String> getProjectRemoteRepos() {
        return toRepositoriesUrlList(projectRepositories.getRepositoriesByType(Repositories.REPO_TYPE_REMOTE_LOOKUP));
    }
    
    public void setProjectRemoteRepos(List<String> projectRemoteRepos) {
        transientProjectRemoteRepos = projectRemoteRepos;
    }
    
    public String getEncoding() {
        return mergedConfig.getOption(DefaultToolOptions.DEFAULTS_ENCODING);
    }

    public String getProjectEncoding() {
        return projectConfig.getOption(DefaultToolOptions.DEFAULTS_ENCODING);
    }
    
    public void setProjectEncoding(String encoding) {
        this.isEncodingChanged = true;
        this.transientEncoding = encoding;
    }
    
    public boolean isOffline() {
        return mergedConfig.getBoolOption(DefaultToolOptions.DEFAULTS_OFFLINE, false);
    }

    public Boolean isProjectOffline() {
        return projectConfig.getBoolOption(DefaultToolOptions.DEFAULTS_OFFLINE);
    }

    public void setProjectOffline(Boolean offline) {
        this.isOfflineChanged = true;
        this.transientOffline = offline;
    }

    public String getOverrides() {
        return DefaultToolOptions.getDefaultOverrides(mergedConfig);
    }

    public String getProjectOverrides() {
        return DefaultToolOptions.getDefaultOverrides(projectConfig);
    }

    public void setProjectOverrides(String overrides) {
        this.isOverridesChanged = true;
        this.transientOverrides = overrides;
    }

    public Boolean isFlatClasspath() {
        return DefaultToolOptions.getDefaultFlatClasspath(mergedConfig);
    }

    public Boolean isProjectFlatClasspath() {
        return DefaultToolOptions.getDefaultFlatClasspath(projectConfig);
    }

    public void setProjectFlatClasspath(Boolean flatClasspath) {
        this.isFlatClasspathChanged = true;
        this.transientFlatClasspath = flatClasspath;
    }

    public Boolean isAutoExportMavenDependencies() {
        return DefaultToolOptions.getDefaultAutoExportMavenDependencies(mergedConfig);
    }

    public Boolean isProjectAutoExportMavenDependencies() {
        return DefaultToolOptions.getDefaultAutoExportMavenDependencies(projectConfig);
    }

    public void setProjectAutoExportMavenDependencies(Boolean autoExportMavenDependencies) {
        this.isAutoExportMavenDependenciesChanged = true;
        this.transientAutoExportMavenDependencies = autoExportMavenDependencies;
    }

    
    public List<String> getSourceDirectories() {
        return getConfigSourceDirectories(mergedConfig);
    }

    public List<String> getProjectSourceDirectories() {
        return getConfigSourceDirectories(projectConfig);
    }

    public static List<String> getConfigSourceDirectories(CeylonConfig config) {
        return getConfigValuesAsList(config, DefaultToolOptions.COMPILER_SOURCE, Constants.DEFAULT_SOURCE_DIR);
    }

    public void setProjectSourceDirectories(List<String> dirs) {
        transientSourceDirectories = dirs;
    }

    public List<String> getResourceDirectories() {
        return getConfigResourceDirectories(mergedConfig);
    }

    public List<String> getProjectResourceDirectories() {
        return getConfigResourceDirectories(projectConfig);
    }

    public static List<String> getConfigResourceDirectories(CeylonConfig config) {
        return getConfigValuesAsList(config, DefaultToolOptions.COMPILER_RESOURCE, Constants.DEFAULT_RESOURCE_DIR);
    }

    public void setProjectResourceDirectories(List<String> dirs) {
        transientResourceDirectories = dirs;
    }
    
    public EnumSet<Warning> getSuppressWarningsEnum() {
        List<String> suppressWarnings = 
                getConfigValuesAsList(mergedConfig, DefaultToolOptions.COMPILER_SUPPRESSWARNING, null);
        return getSuppressWarningsEnum(suppressWarnings);
    }
    
    public List<String> getProjectSuppressWarnings() {
        return getConfigValuesAsList(projectConfig, DefaultToolOptions.COMPILER_SUPPRESSWARNING, null);
    }
    
    public EnumSet<Warning> getProjectSuppressWarningsEnum() {
        return getSuppressWarningsEnum(getProjectSuppressWarnings());
    }
    
    private EnumSet<Warning> getSuppressWarningsEnum(List<String> suppressWarnings) {
        if (suppressWarnings==null) {
            return EnumSet.noneOf(Warning.class);
        }
        else if (suppressWarnings.isEmpty()) {
            return EnumSet.allOf(Warning.class);
        }
        else if (suppressWarnings.size()==1 && 
                suppressWarnings.get(0).isEmpty()) {
            //special case because all warnings is encoded as the empty string
            return EnumSet.allOf(Warning.class);
        }
        else {
            EnumSet<Warning> suppressedWarnings = EnumSet.noneOf(Warning.class);
            for (String name : suppressWarnings) {
                try {
                    suppressedWarnings.add(Warning.valueOf(name.trim()));
                }
                catch (IllegalArgumentException iae) {}
            }
            return suppressedWarnings;
        }
    }
    
    public void setProjectSuppressWarnings(List<String> warnings) {
        transientSuppressWarnings = warnings;
        isSuppressWarningsChanged = true;
    }

    public void setProjectSuppressWarningsEnum(EnumSet<Warning> warnings) {
        List<String> ws;
        if (warnings == null || warnings.isEmpty()) {
            ws = null;
        } else if (warnings.containsAll(EnumSet.allOf(Warning.class))) {
            ws = Collections.singletonList("");
        } else {
            ws = new ArrayList<String>(warnings.size());
            for (Warning w : warnings) {
                ws.add(w.name());
            }
        }
        transientSuppressWarnings = ws;
        isSuppressWarningsChanged = true;
    }
    
    public void refresh() {
        initMergedConfig();
        initProjectConfig();
        isOfflineChanged = false;
        isEncodingChanged = false;
        isSuppressWarningsChanged = false;
        isOverridesChanged = false;
        isFlatClasspathChanged = false;
        isAutoExportMavenDependenciesChanged = false;
        transientEncoding = null;
        transientOffline = null;
        transientOutputRepo = null;
        transientProjectLocalRepos = null;
        transientProjectRemoteRepos = null;
        transientSourceDirectories = null;
        transientResourceDirectories = null;
        transientSuppressWarnings = null;
        transientOverrides = null;
        transientFlatClasspath = null;
        transientAutoExportMavenDependencies = null;
    }

    public void save() {
        initProjectConfig();
        
        String oldOutputRepo = getOutputRepo();
        List<String> oldProjectLocalRepos = getProjectLocalRepos();
        List<String> oldProjectRemoteRepos = getProjectRemoteRepos();
        List<String> oldSourceDirectories = getProjectSourceDirectories();
        List<String> oldResourceDirectories = getProjectResourceDirectories();
        
        boolean isOutputRepoChanged = transientOutputRepo != null && !transientOutputRepo.equals(oldOutputRepo);
        boolean isProjectLocalReposChanged = transientProjectLocalRepos != null && !transientProjectLocalRepos.equals(oldProjectLocalRepos);
        boolean isProjectRemoteReposChanged = transientProjectRemoteRepos != null && !transientProjectRemoteRepos.equals(oldProjectRemoteRepos);
        boolean isSourceDirsChanged = transientSourceDirectories != null && !transientSourceDirectories.equals(oldSourceDirectories);
        boolean isResourceDirsChanged = transientResourceDirectories != null && !transientResourceDirectories.equals(oldResourceDirectories);
        
        fixHiddenOutputFolder(oldOutputRepo);
        if (isOutputRepoChanged) {
            deleteOldOutputFolder(oldOutputRepo);
            createNewOutputFolder();
        } else if (transientOutputRepo != null) {
            // fix #422: output folder must be create for new projects
            IFolder newOutputRepoFolder = project.getFolder(removeCurrentDirPrefix(transientOutputRepo));
            if (!newOutputRepoFolder.exists()) {
                createNewOutputFolder();
            }
        }
        
        IResource config = project.findMember(".ceylon/config");
        if (config==null || 
                   isOutputRepoChanged || isProjectLocalReposChanged || isProjectRemoteReposChanged || 
                   isOfflineChanged || isEncodingChanged || isSourceDirsChanged || isResourceDirsChanged ||
                   isSuppressWarningsChanged || isOverridesChanged || isFlatClasspathChanged || isAutoExportMavenDependenciesChanged) {
            try {
                if (isOutputRepoChanged) {
                    Repository newOutputRepo = new Repositories.SimpleRepository("", transientOutputRepo, null);
                    projectRepositories.setRepositoriesByType(Repositories.REPO_TYPE_OUTPUT, new Repository[] { newOutputRepo });
                }
                if (isProjectLocalReposChanged) {
                    Repository[] newLocalRepos = toRepositoriesArray(transientProjectLocalRepos);
                    projectRepositories.setRepositoriesByType(Repositories.REPO_TYPE_LOCAL_LOOKUP, newLocalRepos);
                }
                if (isProjectRemoteReposChanged) {
                    Repository[] newRemoteRepos = toRepositoriesArray(transientProjectRemoteRepos);
                    projectRepositories.setRepositoriesByType(Repositories.REPO_TYPE_REMOTE_LOOKUP, newRemoteRepos);
                }
                if (isOfflineChanged) {
                    projectConfig.setBoolOption(DefaultToolOptions.DEFAULTS_OFFLINE, transientOffline);
                }
                if (isEncodingChanged) {
                    projectConfig.setOption(DefaultToolOptions.DEFAULTS_ENCODING, transientEncoding);
                }
                if (isSourceDirsChanged) {
                	setConfigValuesAsList(projectConfig, DefaultToolOptions.COMPILER_SOURCE, transientSourceDirectories);
                }
                if (isResourceDirsChanged) {
                	setConfigValuesAsList(projectConfig, DefaultToolOptions.COMPILER_RESOURCE, transientResourceDirectories);
                }
                if (isSuppressWarningsChanged) {
                    setConfigValuesAsList(projectConfig, DefaultToolOptions.COMPILER_SUPPRESSWARNING, transientSuppressWarnings);
                }
                if (isOverridesChanged) {
                    projectConfig.setOption(DefaultToolOptions.DEFAULTS_OVERRIDES, transientOverrides);
                }
                if (isFlatClasspathChanged) {
                    projectConfig.setBoolOption(DefaultToolOptions.DEFAULTS_FLAT_CLASSPATH, transientFlatClasspath);
                }
                if (isAutoExportMavenDependenciesChanged) {
                    projectConfig.setBoolOption(DefaultToolOptions.DEFAULTS_AUTO_EPORT_MAVEN_DEPENDENCIES, transientAutoExportMavenDependencies);
                }

                ConfigWriter.write(projectConfig, getProjectConfigFile());
                refresh();
                try {
                    if (config!=null) {
                        config.refreshLocal(IResource.DEPTH_ZERO, 
                                new NullProgressMonitor());
                    }
                    else {
                        project.refreshLocal(IResource.DEPTH_INFINITE, 
                                new NullProgressMonitor());
                    }
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Un-hide previously hidden the existing output folder in old projects
    private void fixHiddenOutputFolder(String oldOutputRepo) {
        IFolder oldOutputRepoFolder = project.getFolder(removeCurrentDirPrefix(oldOutputRepo));
        if (oldOutputRepoFolder.exists() && oldOutputRepoFolder.isHidden()) {
            try {
                oldOutputRepoFolder.setHidden(false);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    private File getProjectConfigFile() {
        File projectCeylonDir = new File(project.getLocation().toFile(), ".ceylon");
        File projectCeylonConfigFile = new File(projectCeylonDir, "config");
        return projectCeylonConfigFile;
    }

    private List<String> toRepositoriesUrlList(Repository[] repositories) {
        List<String> result = new ArrayList<String>();
        if (repositories != null) {
            for (Repository repository : repositories) {
                result.add(repository.getUrl());
            }
        }
        return result;
    }
    
    private Repository[] toRepositoriesArray(List<String> repositoriesUrl) {
        Repository[] repositories = new Repository[repositoriesUrl.size()];
        for (int i = 0; i < repositoriesUrl.size(); i++) {
            repositories[i] = new Repositories.SimpleRepository("", repositoriesUrl.get(i), null);
        }
        return repositories;
    }

    private void deleteOldOutputFolder(String oldOutputRepo) {
        IFolder oldOutputRepoFolder = project.getFolder(removeCurrentDirPrefix(oldOutputRepo));
        if( oldOutputRepoFolder.exists() ) {
            boolean remove = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                    "Changing Ceylon output repository", 
                    "The Ceylon output repository has changed. Do you want to remove the old output repository folder '" + 
                            oldOutputRepoFolder.getFullPath().toString() + "' and all its contents?");
            if (remove) {
                try {
                    oldOutputRepoFolder.delete(true, null);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        if (oldOutputRepoFolder.exists() && oldOutputRepoFolder.isDerived()) {
            try {
                oldOutputRepoFolder.setDerived(false, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    private void createNewOutputFolder() {
        IFolder newOutputRepoFolder = 
                project.getFolder(removeCurrentDirPrefix(transientOutputRepo));
        try {
            newOutputRepoFolder.refreshLocal(IResource.DEPTH_ONE, 
                    new NullProgressMonitor());
        }
        catch (CoreException ce) {
            ce.printStackTrace();
        }
        if (!newOutputRepoFolder.exists()) {
            try {
                CoreUtility.createDerivedFolder(newOutputRepoFolder, true, true, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        CeylonEncodingSynchronizer.getInstance().refresh(project, null);
    }

    private String removeCurrentDirPrefix(String url) {
        return url.startsWith("./") || url.startsWith(".\\") ? url.substring(2) : url;
    }

    private static List<String> getConfigValuesAsList(CeylonConfig config, String optionKey, String defaultValue) {
        String[] values = config.getOptionValues(optionKey);
        if (values != null) {
            return Arrays.asList(values);
        } else {
            if (defaultValue != null) {
                return Collections.singletonList(defaultValue);
            } else {
                return null;
            }
        }
    }

    private void setConfigValuesAsList(CeylonConfig config, String optionKey, List<String> values) {
        if (values != null) {
        	String[] array = new String[values.size()];
            config.setOptionValues(optionKey, values.toArray(array));
        } else {
            config.removeOption(optionKey);
        }
    }
}