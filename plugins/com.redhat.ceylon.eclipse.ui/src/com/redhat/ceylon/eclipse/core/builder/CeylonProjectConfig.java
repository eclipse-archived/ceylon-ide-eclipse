package com.redhat.ceylon.eclipse.core.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.ConfigParser;
import com.redhat.ceylon.common.config.ConfigWriter;
import com.redhat.ceylon.common.config.DefaultToolOptions;
import com.redhat.ceylon.common.config.Repositories;
import com.redhat.ceylon.common.config.Repositories.Repository;

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
    private Boolean transientOffline;
    private String transientEncoding;

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
                projectConfig = ConfigParser.loadConfigFromFile(projectConfigFile);
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
    
    public void refresh() {
        initMergedConfig();
        initProjectConfig();
        isOfflineChanged = false;
        isEncodingChanged = false;
        transientEncoding = null;
        transientOffline = null;
        transientOutputRepo = null;
        transientProjectLocalRepos = null;
        transientProjectRemoteRepos = null;
    }

    public void save() {
        initProjectConfig();
        
        String oldOutputRepo = getOutputRepo();
        List<String> oldProjectLocalRepos = getProjectLocalRepos();
        List<String> oldProjectRemoteRepos = getProjectRemoteRepos();
        
        boolean isOutputRepoChanged = transientOutputRepo != null && !transientOutputRepo.equals(oldOutputRepo);
        boolean isProjectLocalReposChanged = transientProjectLocalRepos != null && !transientProjectLocalRepos.equals(oldProjectLocalRepos);
        boolean isProjectRemoteReposChanged = transientProjectRemoteRepos != null && !transientProjectRemoteRepos.equals(oldProjectRemoteRepos);
        
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
        
        if (isOutputRepoChanged || isProjectLocalReposChanged || isProjectRemoteReposChanged || isOfflineChanged || isEncodingChanged) {
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
                    if (transientOffline != null) {
                        projectConfig.setBoolOption(DefaultToolOptions.DEFAULTS_OFFLINE, transientOffline);
                    } else {
                        projectConfig.removeOption(DefaultToolOptions.DEFAULTS_OFFLINE);
                    }
                }
                if (isEncodingChanged) {
                    if (transientEncoding != null) {
                        projectConfig.setOption(DefaultToolOptions.DEFAULTS_ENCODING, transientEncoding);
                    } else {
                        projectConfig.removeOption(DefaultToolOptions.DEFAULTS_ENCODING);
                    }
                }

                ConfigWriter.write(projectConfig, getProjectConfigFile());
                refresh();
            } catch (IOException e) {
                throw new RuntimeException(e);
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
        if (oldOutputRepoFolder.exists() && oldOutputRepoFolder.isHidden()) {
            try {
                oldOutputRepoFolder.setHidden(false);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    private void createNewOutputFolder() {
        IFolder newOutputRepoFolder = project.getFolder(removeCurrentDirPrefix(transientOutputRepo));
        if (!newOutputRepoFolder.exists()) {
            try {
                CoreUtility.createDerivedFolder(newOutputRepoFolder, true, true, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        if (!newOutputRepoFolder.isHidden()) {
            try {
                newOutputRepoFolder.setHidden(true);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        try {
            project.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private String removeCurrentDirPrefix(String url) {
        return url.startsWith("./") || url.startsWith(".\\") ? url.substring(2) : url;
    }

}