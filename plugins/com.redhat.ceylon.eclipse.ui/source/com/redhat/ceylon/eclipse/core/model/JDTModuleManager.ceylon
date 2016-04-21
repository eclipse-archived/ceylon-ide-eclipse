import ceylon.collection {
    MutableList,
    ArrayList
}

import com.redhat.ceylon.compiler.typechecker.context {
    Context
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.ide.common.model {
    IdeModuleManager,
    CeylonProject,
    BaseIdeModuleManager,
    BaseIdeModuleSourceMapper,
    BaseCeylonProject
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
}
import com.redhat.ceylon.model.typechecker.model {
    Module,
    Modules
}

import java.lang.ref {
    WeakReference
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}
import org.eclipse.jdt.core {
    IClasspathEntry,
    IJavaProject,
    IPackageFragmentRoot,
    JavaCore,
    JavaModelException
}
import org.eclipse.jdt.internal.core {
    JarPackageFragmentRoot
}

shared class JDTModuleManager(Context context, CeylonProject<IProject,IResource,IFolder,IFile>? ceylonProject)
         extends IdeModuleManager<IProject,IResource,IFolder,IFile>(ceylonProject) {

    shared IJavaProject? javaProject => 
            if (exists nativeProject = ceylonProject?.ideArtifact) 
            then JavaCore.create(nativeProject) 
            else null;

    shared actual JDTModelLoader newModelLoader(BaseIdeModuleManager self, BaseIdeModuleSourceMapper sourceMapper, Modules modules) {
        assert (is JDTModuleSourceMapper sourceMapper);
        assert (is JDTModuleManager self);
        value modelLoader = JDTModelLoader(self, sourceMapper, modules, ceylonProject?.configuration?.jdkProvider);
        if (exists nativeProject = ceylonProject?.ideArtifact) {
            modelLoaders.put(nativeProject, WeakReference(modelLoader));
        }
        return modelLoader;
    }
    
    shared actual Boolean moduleFileInProject(String moduleName, BaseCeylonProject? ceylonProject) {
        if (!exists ceylonProject) {
            return false;
        }
        assert(is EclipseCeylonProject ceylonProject);
        value nativeProject = ceylonProject.ideArtifact;
        IJavaProject javaProject = JavaCore.create(nativeProject);
        try {
            for (sourceFolder in javaProject.packageFragmentRoots.array.coalesced) {
                if (!sourceFolder.archive, 
                    sourceFolder.\iexists(), 
                    sourceFolder.kind == IPackageFragmentRoot.\iK_SOURCE, 
                    sourceFolder.getPackageFragment(moduleName).\iexists()) {
                    return true;
                }
            }
        }
        catch (JavaModelException e) {
            e.printStackTrace();
        }
        return false;
    }

    shared actual JDTModule newModule(String moduleName, String version) {
        MutableList<IPackageFragmentRoot> roots = ArrayList<IPackageFragmentRoot>();
        if (exists ceylonProject) {
            value javaProject = JavaCore.create(ceylonProject.ideArtifact);
            try {
                if (moduleName.equals(Module.\iDEFAULT_MODULE_NAME)) {
                    for (root in javaProject.packageFragmentRoots.array.coalesced) {
                        if (root.\iexists(), javaProject.isOnClasspath(root)) {
                            value entry = root.resolvedClasspathEntry;
                            if (entry.entryKind == IClasspathEntry.\iCPE_SOURCE,
                                !root.external) {
                                roots.add(root);
                            }
                        }
                    }
                }
                else {
                    for (root in javaProject.packageFragmentRoots.array.coalesced) {
                        if (root.\iexists(), 
                            javaProject.isOnClasspath(root)) {
                            if (JDKUtils.isJDKModule(moduleName)) {
                                for (pkg in JDKUtils.getJDKPackagesByModule(moduleName)) {
                                    if (root.getPackageFragment(pkg.string).\iexists()) {
                                        roots.add(root);
                                        break;
                                    }
                                }
                            }
                            else if (JDKUtils.isOracleJDKModule(moduleName)) {
                                for (pkg in JDKUtils.getOracleJDKPackagesByModule(moduleName)) {
                                    if (root.getPackageFragment(pkg.string).\iexists()) {
                                        roots.add(root);
                                        break;
                                    }
                                }
                            }
                            else if (!(root is JarPackageFragmentRoot), 
                                !CeylonBuilder.isInCeylonClassesOutputFolder(root.path)) {
                                String packageToSearch = moduleName;
                                if (root.getPackageFragment(packageToSearch).\iexists()) {
                                    roots.add(root);
                                }
                            }
                        }
                    }
                }
            }
            catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        assert(is JDTModuleSourceMapper msm=moduleSourceMapper);
        return JDTModule(this, msm, roots);
    }
}
