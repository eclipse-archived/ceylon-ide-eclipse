package org.eclipse.ceylon.ide.eclipse.code.navigator;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.navigator.IExtensionStateConstants.Values;
import org.eclipse.jdt.internal.ui.navigator.JavaNavigatorContentProvider;
import org.eclipse.jdt.internal.ui.navigator.JavaNavigatorLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.eclipse.ui.navigator.INavigatorContentExtension;

import org.eclipse.ceylon.cmr.api.ArtifactContext;
import org.eclipse.ceylon.cmr.impl.JDKRepository;
import org.eclipse.ceylon.cmr.impl.NodeUtils;
import org.eclipse.ceylon.common.Constants;
import org.eclipse.ceylon.common.config.Repositories;
import org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;
import org.eclipse.ceylon.ide.eclipse.core.external.CeylonArchiveFileStore;
import org.eclipse.ceylon.ide.eclipse.core.external.ExternalSourceArchiveManager;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.common.model.BaseCeylonProject;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.model.cmr.Repository;

public class CeylonNavigatorLabelProvider extends
        CeylonLabelProvider implements ICommonLabelProvider {

    ICommonContentExtensionSite extensionSite;
    
    private org.eclipse.ui.navigator.IExtensionStateModel javaNavigatorStateModel;

    private boolean isFlatLayout() {
        return javaNavigatorStateModel.getBooleanProperty(Values.IS_LAYOUT_FLAT);
    }
    
    public CeylonNavigatorLabelProvider() {
        super(true); // small images
    }

    @Override
    public StyledString getStyledText(Object element) {
        if (element instanceof ExternalModuleNode) {
            ExternalModuleNode externalModule = (ExternalModuleNode) element;
            BaseIdeModule jdtModule = externalModule.getModule();
			String name = jdtModule == null ? externalModule.getSignature() : super.getStyledText(jdtModule).toString();
			StyledString moduleText = new StyledString(name);
            if (jdtModule != null) {
                moduleText.append(" \"" + jdtModule.getVersion() + "\"", Highlights.STRING_STYLER);
            }
            return moduleText;
        }
        if (element instanceof SourceModuleNode) {
            BaseIdeModule module = ((SourceModuleNode) element).getModule();
            if (module==null) {
                return new StyledString(((SourceModuleNode) element).getElementName());
            }
            else {
//                return super.getStyledText(module);
                return new StyledString(super.getStyledText(module).getString());
            }
        }
        if (element instanceof RepositoryNode) {
            RepositoryNode repoNode = (RepositoryNode) element;
            String stringToDisplay = getRepositoryString(repoNode);
            return new StyledString(stringToDisplay);
        }
        
        if (element instanceof Package) {
            if (isFlatLayout()) {
                return new StyledString(super.getStyledText(element).getString());
            } else {
                String name = ((Package) element).getName();
                int loc = name.lastIndexOf('.');
                if (loc>=0) name = name.substring(loc+1);
                return new StyledString(name);
            }
        }
        
        if (element instanceof IPackageFragment) {
            if (isFlatLayout()) {
                return new StyledString(super.getStyledText(element).getString());
            } else {
                String name = ((IPackageFragment) element).getElementName();
                int loc = name.lastIndexOf('.');
                if (loc>=0) name = name.substring(loc+1);
                return new StyledString(name);
            }
        }
        
        if (element instanceof CeylonArchiveFileStore) {
            CeylonArchiveFileStore archiveFileStore = (CeylonArchiveFileStore)element;
            if (archiveFileStore.getParent() == null) {
                return new StyledString("Ceylon Sources").append(" \u2014 ", QUALIFIER_STYLER).append(archiveFileStore.getArchivePath().toOSString(), QUALIFIER_STYLER);
            }
            return new StyledString(archiveFileStore.getName());
        }

        if (element instanceof JarPackageFragmentRoot) {
            JarPackageFragmentRoot jpfr = (JarPackageFragmentRoot) element;
            if (ArtifactContext.CAR.substring(1).equalsIgnoreCase(jpfr.getPath().getFileExtension())) {
                return new StyledString("Java Binaries").append(" \u2014 ", QUALIFIER_STYLER).append(jpfr.getPath().toOSString(), QUALIFIER_STYLER);
            } else {
                return getJavaNavigatorLabelProvider().getStyledText(element);
            }
        }

        if (element instanceof IProject || element instanceof IJavaProject) {
            return getJavaNavigatorLabelProvider().getStyledText(element);
        }
        
        StyledString styledString = super.getStyledText(element);
        if (styledString.getString().equals("<something>")) {
            StyledString javaResult = getJavaNavigatorLabelProvider().getStyledText(element);
            if (! javaResult.getString().trim().isEmpty()) {
                return javaResult;
            }
        }
        
        return styledString;
    }

    private String getRepositoryString(RepositoryNode repoNode) {
        String displayString = repoNode.getDisplayString();
        String stringToDisplay = null;
        if (Constants.REPO_URL_CEYLON.equals(displayString)) {
            stringToDisplay = "Herd Modules";
        }
        if (stringToDisplay == null && JDKRepository.JDK_REPOSITORY_DISPLAY_STRING.equals(displayString)) {
            stringToDisplay = "Java SE Modules";
        }
        if (stringToDisplay == null && CeylonBuilder.getInterpolatedCeylonSystemRepo(repoNode.project).equals(displayString)) {
            stringToDisplay = "IDE System Modules";
        }
        if (stringToDisplay == null && CeylonBuilder.getCeylonModulesOutputDirectory(repoNode.project).getAbsolutePath().equals(displayString)) {
            stringToDisplay = "Output Modules";
        }
        
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(repoNode.project);
        Repositories mergedRepos = ceylonProject != null ? 
                ceylonProject.getConfiguration().getRepositories() :
                    Repositories.get();

        if (stringToDisplay == null && mergedRepos.getCacheRepoDir().getAbsolutePath().equals(displayString)) {
            stringToDisplay = "Cached Modules";
        }
        if (stringToDisplay == null && mergedRepos.getUserRepoDir().getAbsolutePath().equals(displayString)) {
            stringToDisplay = "Imported Modules";
        }
        if (stringToDisplay == null && displayString.startsWith("[Maven]")) {
            stringToDisplay = "Maven Modules \u2014 " + displayString.substring(7).trim();
        }
        if (stringToDisplay == null) {
            try {
                for (IProject referencedProject: repoNode.project.getReferencedProjects()) {
                    if (referencedProject.isOpen() && CeylonNature.isEnabled(referencedProject)) {
                        if (CeylonBuilder.getCeylonModulesOutputDirectory(referencedProject).getAbsolutePath().equals(displayString)) {
                            stringToDisplay = "Modules of Referenced Project \u2014 " + referencedProject.getName() + "";
                            break;
                        }
                    }
                }
            } catch (CoreException e) {
            }
        }

        if (stringToDisplay == null) {
            for (Repositories.Repository repo : mergedRepos.getLocalLookupRepositories()) {
                if (repo.getUrl().startsWith("./") && repo.getUrl().length() > 2) {
                    IPath relativePath = Path.fromPortableString(repo.getUrl().substring(2));
                    IFolder folder = repoNode.project.getFolder(relativePath);
                    if (folder.exists() && folder.getLocation().toFile().getAbsolutePath().equals(displayString)) {
                        stringToDisplay = "Local Repository \u2014 " + relativePath.toString() + "";
                        break;
                    }
                }
            }
        }
        if (stringToDisplay == null && NodeUtils.UNKNOWN_REPOSITORY.equals(displayString)) {
            stringToDisplay = "Unknown Repository";
        }

        if (stringToDisplay == null) {
            stringToDisplay = displayString;
        }
        return stringToDisplay;
    }

    
    
    @Override
    public Image getImage(Object element) {
        JavaNavigatorLabelProvider javaProvider = getJavaNavigatorLabelProvider();

        if (element instanceof IProject || element instanceof IJavaProject) {
            Image javaContributedImage = javaProvider.getImage(element);
            if (javaContributedImage != null) {
                return javaContributedImage;
            }
        }
        
        if (element instanceof IPackageFragment &&
                ! CeylonBuilder.isInSourceFolder((IPackageFragment)element)) {
            return javaProvider.getImage(element);
        }
        
        if (element instanceof ExternalModuleNode) {
            return super.getImage(((ExternalModuleNode)element).getModule());
        }
        if (element instanceof SourceModuleNode) {
            int decorationAttributes = 0;
            for (Object child : getContentProvider().getChildren(element)) {
                if (!hasPipelinedChildren(child)) {
                    continue;
                }
                int childValue = getDecorationAttributes(child);
                if ((childValue & ERROR) != 0) {
                    decorationAttributes = ERROR;
                    break;
                }
                if ((childValue & WARNING) != 0) {
                    decorationAttributes = WARNING;
                }
            }
            
            BaseIdeModule module = ((SourceModuleNode)element).getModule();
            if (module==null) {
                return getDecoratedImage(CEYLON_MODULE, decorationAttributes, true);
            }
            else {
                return getDecoratedImage(getImageKey(module), decorationAttributes, true);
            }
        }
        
        if (element instanceof CeylonArchiveFileStore) {
            CeylonArchiveFileStore archiveFileStore = (CeylonArchiveFileStore)element;
            if (archiveFileStore.getParent() != null 
                    && ! archiveFileStore.fetchInfo().isDirectory()) {
                IFolder sourceArchiveFolder = ExternalSourceArchiveManager.getExternalSourceArchiveManager().getSourceArchive(archiveFileStore.getArchivePath());
                if (sourceArchiveFolder != null && sourceArchiveFolder.exists()) {
                    IResource file = sourceArchiveFolder.findMember(archiveFileStore.getEntryPath());
                    if (file instanceof IFile) {
                        element = file;
                    }
                }
            }
        }
        
        if (element instanceof IFile) {
            if (! CeylonBuilder.isCeylon((IFile) element)) {
                return javaProvider.getImage(element);
            }
        }

        return super.getImage(element); 
    }
    
    private boolean hasPipelinedChildren(Object child) {
        return getContentProvider().hasPipelinedChildren(child, 
                getJavaNavigatorContentProvider().hasChildren(child));
    }
    
    @Override
    protected String getImageKey(Object element) {
        if (element instanceof RepositoryNode) {
            return RUNTIME_OBJ;
        }
        if (element instanceof IPackageFragment) {
            return CEYLON_PACKAGE;
        }
        if (element instanceof CeylonArchiveFileStore) {
            CeylonArchiveFileStore archiveFileStore = (CeylonArchiveFileStore)element;
            if (archiveFileStore.getParent() == null) {
                return CEYLON_SOURCE_ARCHIVE;
            } else {
                if (archiveFileStore.fetchInfo().isDirectory()) {
                    return CEYLON_PACKAGE;
                } else {
                    return CEYLON_FILE;
                }
            }
        }

        if (element instanceof JarPackageFragmentRoot) {
            return CEYLON_BINARY_ARCHIVE;
        }
        
        return super.getImageKey(element);
    }

    @Override
    public void restoreState(IMemento aMemento) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveState(IMemento aMemento) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getDescription(Object anElement) {
        if (anElement instanceof RepositoryNode) {
            Repository repo = ((RepositoryNode)anElement).getRepository();
            if (repo != null) {
                return "Repository path : " + repo.getDisplayString();
            }
        }

        if (anElement instanceof CeylonArchiveFileStore) {
            CeylonArchiveFileStore archive = (CeylonArchiveFileStore)anElement;
            if (archive.getParent() == null) {
                return archive.getArchivePath().toOSString();
            }
        }

        return null;
    }

    @Override
    public void init(ICommonContentExtensionSite aConfig) {
        extensionSite = aConfig;
        INavigatorContentExtension javaNavigatorExtension = null;
        @SuppressWarnings("unchecked")
        Set<INavigatorContentExtension> set = aConfig.getService().findContentExtensionsByTriggerPoint(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
        for (INavigatorContentExtension extension : set) {
            if (extension.getDescriptor().equals(aConfig.getExtension().getDescriptor().getOverriddenDescriptor())) {
                javaNavigatorExtension = extension;
                break;
            }
        }
        javaNavigatorStateModel = javaNavigatorExtension.getStateModel();
    }
    
    private INavigatorContentExtension getJavaNavigatorExtension() {
        @SuppressWarnings("unchecked")
        Set<INavigatorContentExtension> set = extensionSite.getService().findContentExtensionsByTriggerPoint(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
        for (INavigatorContentExtension extension : set) {
            if (extension.getDescriptor().equals(extensionSite.getExtension().getDescriptor().getOverriddenDescriptor())) {
                return extension;
            }
        }
        return null;
    }
    
    private JavaNavigatorLabelProvider getJavaNavigatorLabelProvider() {
        INavigatorContentExtension javaExtension = getJavaNavigatorExtension();
        if (javaExtension != null) {
            return (JavaNavigatorLabelProvider) javaExtension.getLabelProvider();
        }
        return null;
    }

    private JavaNavigatorContentProvider getJavaNavigatorContentProvider() {
        INavigatorContentExtension javaExtension = getJavaNavigatorExtension();
        if (javaExtension != null) {
            return (JavaNavigatorContentProvider) javaExtension.getContentProvider();
        }
        return null;
    }
    
    private CeylonNavigatorContentProvider getContentProvider() {
        return (CeylonNavigatorContentProvider) extensionSite.getExtension().getContentProvider();
    }
}
