import ceylon.collection {
    HashSet,
    MutableSet,
    MutableList
}
import ceylon.interop.java {
    javaObjectArray
}

import com.redhat.ceylon.eclipse.core.classpath {
    CeylonLanguageModuleContainer,
    CeylonProjectModulesContainer
}
import com.redhat.ceylon.ide.common.model {
    IdeModule
}
import com.redhat.ceylon.ide.common.util {
    ...
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
}

import java.io {
    File
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}
import org.eclipse.core.runtime {
    CoreException
}
import org.eclipse.jdt.core {
    IClasspathEntry,
    IPackageFragmentRoot,
    IParent,
    JavaCore,
    JavaModelException
}
import org.eclipse.jdt.internal.core {
    JarPackageFragmentRoot,
    JavaModelManager,
    PackageFragment
}

shared class JDTModule(
    JDTModuleManager jdtModuleManager, 
    JDTModuleSourceMapper jdtModuleSourceMapper, 
    MutableList<IPackageFragmentRoot> thePackageFragmentRoots) extends IdeModule<IProject, IResource, IFolder, IFile>() {
    
    shared actual JDTModuleManager moduleManager = jdtModuleManager;
    shared actual JDTModuleSourceMapper moduleSourceMapper = jdtModuleSourceMapper;
    MutableList<IPackageFragmentRoot> _packageFragmentRoots=thePackageFragmentRoots;
    
    shared List<IPackageFragmentRoot> packageFragmentRoots =>
            let (do = () {
                if (_packageFragmentRoots.empty 
                    && !moduleManager.isExternalModuleLoadedFromSource(nameAsString)) {
                    value ceylonProject = moduleManager.ceylonProject;
                    if (exists ceylonProject) {
                        value javaProject = JavaCore.create(ceylonProject.ideArtifact);
                        if (this.equals(languageModule)) {
                            variable IClasspathEntry? runtimeClasspathEntry = null;
                            try {
                                for (entry in javaProject.rawClasspath.array.coalesced) {
                                    if (entry.entryKind == IClasspathEntry.\iCPE_CONTAINER && entry.path.segment(0).equals(CeylonLanguageModuleContainer.\iCONTAINER_ID)) {
                                        runtimeClasspathEntry = entry;
                                        break;
                                    }
                                }
                                if (exists existingEntry=runtimeClasspathEntry) {
                                    for (root in javaProject.packageFragmentRoots.array.coalesced) {
                                        if (root.\iexists() && javaProject.isOnClasspath(root) && root.rawClasspathEntry == existingEntry) {
                                            _packageFragmentRoots.add(root);
                                        }
                                    }
                                }
                            }
                            catch (JavaModelException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            variable File? jarToSearch = null;
                            try {
                                jarToSearch = returnCarFile();
                                if (! jarToSearch exists) {
                                    jarToSearch = CeylonProjectModulesContainer.getModuleArtifact(ceylonProject.repositoryManager, this);
                                }
                                if (exists foundJar = jarToSearch) {
                                    IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(foundJar.string);
                                    if (is JarPackageFragmentRoot root) {
                                        if (root.jar.name == foundJar.path) {
                                            _packageFragmentRoots.add(root);
                                        }
                                    }
                                }
                            }
                            catch (CoreException e) {
                                if (exists foundJar=jarToSearch) {
                                    process.writeErrorLine("Exception trying to get Jar file '``foundJar``' :");
                                }
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return _packageFragmentRoots;
            }) synchronize(_packageFragmentRoots, do);
    
    shared actual JDTModelLoader modelLoader =>
            unsafeCast<JDTModelLoader>(moduleManager.modelLoader);
    
    shared actual Set<String> listPackages() {
        MutableSet<String> packageList = HashSet<String>();
        value name = nameAsString;
        if (JDKUtils.isJDKModule(name)) {
            packageList.addAll { for (p in JDKUtils.getJDKPackagesByModule(name)) p.string };
        }
        else if (JDKUtils.isOracleJDKModule(name)) {
            packageList.addAll { for (p in JDKUtils.getOracleJDKPackagesByModule(name)) p.string };
        }
        else if (java || true) {  // TODO : check this - the `|| true` part is strange
            for (fragmentRoot in packageFragmentRoots) {
                if (!fragmentRoot.\iexists()) {
                    continue;
                }
                listPackagesInternal(packageList, fragmentRoot);
            }
        }
        return packageList;
    }
    
    void listPackagesInternal(MutableSet<String> packageList, IParent parent) {
        try {
            for (child in parent.children.array.coalesced) {
                if (is PackageFragment child) {
                    packageList.add(child.elementName);
                    listPackagesInternal(packageList, child);
                }
            }
        }
        catch (JavaModelException e) {
            e.printStackTrace();
        }
    }
    
    shared actual void refreshJavaModel() {
        JavaModelManager.javaModelManager.resetClasspathListCache();
        JavaModelManager.javaModelManager.javaModel.refreshExternalArchives(
            javaObjectArray(Array<IPackageFragmentRoot?>(packageFragmentRoots)), null);
    }
}
