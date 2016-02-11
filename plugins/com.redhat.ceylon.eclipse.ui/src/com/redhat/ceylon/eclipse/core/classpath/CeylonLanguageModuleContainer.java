package com.redhat.ceylon.eclipse.core.classpath;



import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.ceylonSourceArchiveToJavaSourceArchive;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.jdt.core.JavaCore.newLibraryEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

/** 
 * Ceylon Language Module Classpath Container - resolves a classpath container id to the ceylon.language binary archive
 */
public class CeylonLanguageModuleContainer implements IClasspathContainer {

    public static final String CONTAINER_ID = PLUGIN_ID + ".cpcontainer.RUNTIME_CONTAINER";

    /**
     * Container path used to resolve to this CPC
     */
    private IPath fPath = null;
    
    /**
     * The project this container is for
     */
    private IJavaProject fProject = null;
    
    IClasspathEntry[] entries = null;
    
    /**
     * Constructs a classpath container for the ceylon language runtime archive on the given project
     * 
     */
    public CeylonLanguageModuleContainer(IProject project) {
        modelJ2C().ceylonModel().addProject(project);
        fPath = new Path(CeylonLanguageModuleContainer.CONTAINER_ID + "/default");
        fProject = JavaCore.create(project);
        RepositoryManager repoManager;
        repoManager = CeylonBuilder.getProjectRepositoryManager(fProject.getProject());
        if (repoManager != null) {
            String moduleName = "ceylon.language";
            String moduleVersion = TypeChecker.LANGUAGE_MODULE_VERSION;
            IPath ceylonLanguageBinaries = new Path(repoManager.getArtifact(new ArtifactContext(moduleName, moduleVersion, ArtifactContext.CAR)).getAbsolutePath());
            File ceylonLanguageJavaSources = ceylonSourceArchiveToJavaSourceArchive(
                    moduleName,
                    moduleVersion,
                    repoManager.getArtifact(new ArtifactContext(moduleName,moduleVersion, ArtifactContext.SRC)));
            IPath ceylonLanguageSources = ceylonLanguageJavaSources != null ? 
                    new Path(ceylonLanguageJavaSources.getAbsolutePath()) : null;
            IClasspathEntry entry = newLibraryEntry(ceylonLanguageBinaries, ceylonLanguageSources, null);
            entries = new IClasspathEntry[] { entry };
        }
        else {
            entries = new IClasspathEntry[] {};
        }
    }
    
    public IClasspathEntry[] constructModifiedClasspath() 
            throws JavaModelException {
        // Modifies the project classpath :
        //   Beware to always add the language module container before the application modules container 
        IClasspathEntry runtimeEntry = JavaCore.newContainerEntry(fPath, null, 
                new IClasspathAttribute[0], false);
        IClasspathEntry[] entries = fProject.getRawClasspath();
        List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(Arrays.asList(entries));
        int indexFirstContainer = -1;
        int indexSecondContainer = -1;
        int index = 0;
        boolean languageModuleEntryWasExported = false;
        IClasspathEntry ceylonClasspathEntry = null;
        for (IClasspathEntry entry: newEntries) {
            boolean containerToReplace = false;
            if (entry.getPath().equals(runtimeEntry.getPath()) ) {
                containerToReplace = true;
                languageModuleEntryWasExported = entry.isExported();
            }
            if (entry.getPath().segment(0)
                    .equals(CeylonProjectModulesContainer.CONTAINER_ID) ) {
                containerToReplace = true;
                ceylonClasspathEntry = entry;
                containerToReplace = true;
            }
            if (containerToReplace) {
                if (indexFirstContainer == -1) {
                    indexFirstContainer = index;
                }
                else {
                    indexSecondContainer = index;
                }
            }
            index++;
        }

        IClasspathEntry newEntry = JavaCore.newContainerEntry(fPath, null, 
                new IClasspathAttribute[0], languageModuleEntryWasExported);

        if (indexFirstContainer >= 0) {
            newEntries.set(indexFirstContainer, newEntry);
        }
        else {
            newEntries.add(newEntry);
        }
        
        if (ceylonClasspathEntry != null) {
            if (indexSecondContainer >= 0) {
                newEntries.set(indexSecondContainer, ceylonClasspathEntry);
            }
            else {
                newEntries.add(ceylonClasspathEntry);
            }
        }
        
        return (IClasspathEntry[]) newEntries.toArray(new IClasspathEntry[newEntries.size()]);
    }
    
    
    
    /**
     * @see IClasspathContainer#getClasspathEntries()
     */
    public IClasspathEntry[] getClasspathEntries() {
        return entries;
    }

    public String getRuntimeVersion() {
        return TypeChecker.LANGUAGE_MODULE_VERSION;
    }
    
    /**
     * @see IClasspathContainer#getDescription()
     */
    public String getDescription() {
        return "Ceylon Language Module \u2014 " + getRuntimeVersion();
    }

    /**
     * @see IClasspathContainer#getKind()
     */
    public int getKind() {
        return IClasspathContainer.K_APPLICATION;
    }

    /**
     * @see IClasspathContainer#getPath()
     */
    public IPath getPath() {
        return fPath;
    }
    
    public void install() throws JavaModelException {
        fProject.setRawClasspath(constructModifiedClasspath(), null);
        JavaCore.setClasspathContainer(getPath(), 
                new IJavaProject[] { fProject }, 
                new IClasspathContainer[] { this }, null);
    }
}
