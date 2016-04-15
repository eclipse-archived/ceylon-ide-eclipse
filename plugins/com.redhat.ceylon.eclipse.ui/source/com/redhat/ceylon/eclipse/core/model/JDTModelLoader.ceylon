import ceylon.interop.java {
    javaString,
    createJavaObjectArray
}

import com.redhat.ceylon.eclipse.core.classpath {
    CeylonClasspathUtil,
    CeylonProjectModulesContainer
}
import com.redhat.ceylon.eclipse.core.model {
    isCeylonSourceEntry
}
import com.redhat.ceylon.eclipse.core.model.mirror {
    JDTClass,
    JDTMethod
}
import com.redhat.ceylon.eclipse.util {
    withJavaModel
}
import com.redhat.ceylon.ide.common.model {
    IdeModelLoader,
    BaseIdeModule
}
import com.redhat.ceylon.ide.common.util {
    synchronize,
    unsafeCast
}
import com.redhat.ceylon.model.cmr {
    ArtifactResult
}
import com.redhat.ceylon.model.loader.mirror {
    ClassMirror,
    MethodMirror,
    AnnotatedMirror
}
import com.redhat.ceylon.model.loader.model {
    LazyPackage
}
import com.redhat.ceylon.model.typechecker.model {
    Modules,
    Unit,
    Declaration,
    ClassOrInterface
}

import java.lang {
    CharArray,
    Runnable,
    ObjectArray,
    System
}
import java.lang.ref {
    WeakReference
}
import java.util {
    WeakHashMap
}

import org.eclipse.core.internal.utils {
    Cache
}
import org.eclipse.core.resources {
    IFile,
    IFolder,
    IResource,
    IProject
}
import org.eclipse.core.runtime {
    Path,
    IPath
}
import org.eclipse.jdt.core {
    IJavaProject,
    IType,
    ITypeRoot,
    JavaModelException,
    WorkingCopyOwner,
    IPackageFragment,
    IClasspathEntry,
    IClasspathContainer,
    JavaCore,
    IPackageFragmentRoot,
    ICompilationUnit,
    IJavaElement
}
import org.eclipse.jdt.core.compiler {
    CharOperation
}
import org.eclipse.jdt.internal.compiler {
    DefaultErrorHandlingPolicies
}
import org.eclipse.jdt.internal.compiler.classfmt {
    ClassFileReader
}
import org.eclipse.jdt.internal.compiler.env {
    INameEnvironment
}
import org.eclipse.jdt.internal.compiler.impl {
    CompilerOptions
}
import org.eclipse.jdt.internal.compiler.lookup {
    LookupEnvironment,
    MissingTypeBinding,
    ReferenceBinding
}
import org.eclipse.jdt.internal.compiler.problem {
    ProblemReporter,
    DefaultProblemFactory
}
import org.eclipse.jdt.internal.core {
    JavaProject
}

CharArray toCharArray(String s) => javaString(s).toCharArray();

WeakHashMap<IProject, WeakReference<JDTModelLoader>> modelLoaders = WeakHashMap<IProject, WeakReference<JDTModelLoader>>();
Cache archivesRootsToModelLoaderCache = Cache(20);

shared JDTModelLoader? projectModelLoader(IProject project) {
    variable JDTModelLoader? modelLoader = null;
    if (exists modelLoaderRef = modelLoaders.get(project)) {
        modelLoader = modelLoaderRef.get();
    }
    return modelLoader;
}

shared Boolean isCeylonSourceEntry(IClasspathEntry entry) => 
        every {
    entry.entryKind == IClasspathEntry.\iCPE_SOURCE,
    entry.exclusionPatterns.iterable.coalesced.filter((path) => path.string.endsWith(".ceylon")).empty
};

shared JDTModelLoader? javaProjectModelLoader(IJavaProject javaProject) =>
        projectModelLoader(javaProject.project);

shared LookupEnvironmentUtilities.Provider? typeModelLoader(IType? type) {
    if (! exists type) {
        return null;
    }
    
    variable JDTModelLoader? modelLoader = javaProjectModelLoader(type.javaProject);
    if (! modelLoader exists) {
        IPackageFragmentRoot pfr = unsafeCast<IPackageFragmentRoot>(type.getAncestor(IJavaElement.\iPACKAGE_FRAGMENT_ROOT));
        if (pfr.external && pfr.archive) {
            synchronize {
                on = archivesRootsToModelLoaderCache;
                void do() {
                    Cache.Entry? cacheEntry = archivesRootsToModelLoaderCache.getEntry(pfr.path);
                    if (exists cacheEntry) {
                        WeakReference<JDTModelLoader>? cachedModelLoaderRef = unsafeCast<WeakReference<JDTModelLoader>>(cacheEntry.cached);
                        if (exists cachedModelLoaderRef) {
                            modelLoader = cachedModelLoaderRef.get();
                        }
                    }
                    if (modelLoader is Null) {
                        for (loaderRef in modelLoaders.values()) {
                            if (exists loader = loaderRef.get(),
                                exists javaProject = loader.moduleManager.javaProject) {
                                
                                try {
                                    if (javaProject.findPackageFragmentRoot(pfr.path) exists){
                                        modelLoader = loader;
                                        archivesRootsToModelLoaderCache.addEntry(pfr.path, WeakReference<JDTModelLoader>(modelLoader));
                                        break;
                                    }
                                } catch (JavaModelException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            };
        }
    }
    return modelLoader;
}

shared class JDTModelLoader 
        extends IdeModelLoader<IProject, IResource, IFolder, IFile, ITypeRoot, IType>
        satisfies LookupEnvironmentUtilities.Provider {
    class JavaProjectInfos(shared IJavaProject javaProject) {
        CompilerOptions compilerOptions = CompilerOptions(javaProject.getOptions(true));
        compilerOptions.ignoreMethodBodies = true;
        compilerOptions.storeAnnotations = true;
        ProblemReporter problemReporter = ProblemReporter(
            DefaultErrorHandlingPolicies.proceedWithAllProblems(),
            compilerOptions,
            DefaultProblemFactory());
        shared late variable LookupEnvironment lookupEnvironment;
        shared late variable MissingTypeBinding missingTypeBinding;

        throws(`class JavaModelException`)
        INameEnvironment createSearchableEnvironment()  {
            return ModelLoaderNameEnvironment(javaProject);
        }
        
        throws(`class JavaModelException`)
        shared void createLookupEnvironment() {
            ModelLoaderTypeRequestor requestor = ModelLoaderTypeRequestor(compilerOptions);
            lookupEnvironment = LookupEnvironment(requestor, compilerOptions, problemReporter, createSearchableEnvironment());
            requestor.initialize(lookupEnvironment);
            lookupEnvironment.mayTolerateMissingType = true;
            missingTypeBinding = MissingTypeBinding(lookupEnvironment.defaultPackage, createJavaObjectArray { toCharArray("unknown") }, lookupEnvironment);
        }

        createLookupEnvironment();
        
        shared LookupEnvironment? createLookupEnvironmentForGeneratedCode() {
            try {
                ModelLoaderTypeRequestor requestor = ModelLoaderTypeRequestor(compilerOptions);
                LookupEnvironment lookupEnvironmentForGeneratedCode = LookupEnvironment(requestor, 
                    compilerOptions, 
                    problemReporter, 
                    unsafeCast<JavaProject>(javaProject).newSearchableNameEnvironment(unsafeCast<WorkingCopyOwner?>(null)));
                requestor.initialize(lookupEnvironmentForGeneratedCode);
                lookupEnvironmentForGeneratedCode.mayTolerateMissingType = true;
                return lookupEnvironmentForGeneratedCode;
            } catch (JavaModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        
        shared void refreshNameEnvironment() {
            try {
                lookupEnvironment.nameEnvironment = createSearchableEnvironment();
            } catch (JavaModelException e) {
                e.printStackTrace();
            }            
        }
    }
    JavaProjectInfos? javaProjectInfos;
    
    shared actual Object lookupEnvironmentMutex = object extends Basic() {};
    
    shared new (
        JDTModuleManager moduleManager,
        JDTModuleSourceMapper moduleSourceMapper,
        Modules modules
    ) extends IdeModelLoader<IProject, IResource, IFolder, IFile, ITypeRoot, IType>(
        moduleManager, moduleSourceMapper, modules){
        if (exists javaProject = moduleManager.javaProject) {
            variable JavaProjectInfos? newProjectInfos = null; 
            try {
                newProjectInfos = JavaProjectInfos(javaProject);
            } catch (JavaModelException e) {
                e.printStackTrace();
                newProjectInfos = null;
            }
            javaProjectInfos = newProjectInfos;
        } else {
            javaProjectInfos = null;
        }
    }
    
    shared actual class PackageLoader(BaseIdeModule theIdeModule) extends super.PackageLoader(theIdeModule) {
        
        value javaProject = javaProjectInfos?.javaProject;
        value jdtModule => unsafeCast<JDTModule>(ideModule);
        
        {IPackageFragment*} getPackageFragments(String quotedPackageName) =>
                jdtModule.packageFragmentRoots
                    // skip package fragment roots that are not accessible on project casspath
                    .filter((root) => 
                                every {
                                    root.\iexists(),
                                    unsafeCast<IJavaProject>(javaProject).isOnClasspath(root),
                                    withJavaModel { 
                                        do() => 
                                                let(entry = root.rawClasspathEntry)
                                                any {
                                                    //TODO: is the following really necessary?
                                                    //Note that getContentKind() returns an undefined
                                                    //value for a classpath container or variable
                                                    entry.entryKind == IClasspathEntry.\iCPE_CONTAINER,
                                                    entry.entryKind == IClasspathEntry.\iCPE_VARIABLE,
                                                    entry.contentKind != IPackageFragmentRoot.\iK_SOURCE,
                                                    isCeylonSourceEntry(entry)
                                                };
                                        void onException(JavaModelException e) {
                                            if (! e.doesNotExist) {
                                                e.printStackTrace();
                                            }
                                        } 
                                    } else false
                                })
                    .map((root) => 
                                root.getPackageFragment(quotedPackageName))
                    .filter((packageFragment) => 
                                packageFragment.\iexists());
        
        shared actual Boolean packageExists(String quotedPackageName) => 
                ! getPackageFragments(quotedPackageName).empty;
        
        shared actual {IType*}? packageMembers(String quotedPackageName) => 
                let (packageFragments = getPackageFragments(quotedPackageName))
                if (packageFragments.empty)
                then null
                else packageFragments
                        .flatMap {
                            collecting(IPackageFragment pf) => 
                                expand {
                                    withJavaModel {
                                        do() => 
                                            pf.classFiles.array.coalesced
                                                .map((classFile) =>
                                                    classFile.type);
                                    } else {},
                                    withJavaModel {
                                        do() => 
                                            pf.compilationUnits.array.coalesced
                                                .flatMap { 
                                                    function collecting(ICompilationUnit cu) {
                                                        if (! (cu of IJavaElement).\iexists()) {
                                                            return {};
                                                        }
                                                        return withJavaModel {
                                                            do() => 
                                                                cu.types.array.coalesced;
                                                        } else {};
                                                     }
                                                 };
                                    } else {}
                                };
                        };
        
        shared actual Boolean shouldBeOmitted(IType type) =>
            //  skip all classes whose flat name has a '$' after the first character
            type.fullyQualifiedName.split('.'.equals)
                .last // class flatName
                .rest.any('$'.equals);
        
    }
    
    shared actual JDTModuleManager moduleManager => 
            unsafeCast<JDTModuleManager>(super.moduleManager);
    
    shared void createLookupEnvironment() { 
        try {
            javaProjectInfos?.createLookupEnvironment();
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
    }
    
    shared actual LookupEnvironment upToDateLookupEnvironment {
        assert(exists javaProjectInfos);
        resetJavaModelSourceIfNecessary(object satisfies Runnable {
            run() =>
                synchronize {
                    on = lookupEnvironmentMutex;
                    do() => createLookupEnvironment();
                };
        });
        return javaProjectInfos.lookupEnvironment;
    }

    shared actual LookupEnvironment currentLookupEnvironment {
        assert(exists javaProjectInfos);
        return javaProjectInfos.lookupEnvironment;
    }
    
    shared MissingTypeBinding missingTypeBinding =>
            synchronize {
                on=lock;
                function do() {
                    assert(exists javaProjectInfos);
                    return javaProjectInfos.missingTypeBinding;
                }
            };
    shared actual LookupEnvironment? createLookupEnvironmentForGeneratedCode() =>
            javaProjectInfos?.createLookupEnvironmentForGeneratedCode();
    shared actual void refreshNameEnvironment() =>
        synchronize {
            on = lock;
            void do() {
                assert(exists javaProjectInfos);
                javaProjectInfos.refreshNameEnvironment();
            }
        };
    shared actual void addModuleToClasspathInternal(ArtifactResult? artifact) {
        assert(exists javaProject = javaProjectInfos?.javaProject);
        CeylonProjectModulesContainer? container = CeylonClasspathUtil.getCeylonProjectModulesClasspathContainer(javaProject);
        
        if (exists container) {
            assert(exists artifact);
            IPath modulePath = Path(artifact.artifact().path);
            IClasspathEntry? newEntry = container.addNewClasspathEntryIfNecessary(modulePath);
            if (exists newEntry) {
                try {
                    JavaCore.setClasspathContainer(container.path, createJavaObjectArray { javaProject }, 
                        createJavaObjectArray<IClasspathContainer>{ CeylonProjectModulesContainer(container) } , null);
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
                refreshNameEnvironment();
            }
        }
    }
    shared actual ClassMirror? buildClassMirrorInternal(String name) {
        if (! exists javaProjectInfos) {
            return null;
        }
        
        try {
            value theLookupEnvironment = upToDateLookupEnvironment;
            value nameEnvironment = unsafeCast<ModelLoaderNameEnvironment>(theLookupEnvironment.nameEnvironment);
            value uncertainCompoundName = CharOperation.splitOn('.', javaString(name).toCharArray());
            Integer numberOfParts = uncertainCompoundName.size;

            variable ObjectArray<CharArray>? compoundName = null;
            variable IType? type = null;
            if (numberOfParts > 0) {
                variable value searchingInPreviousParts = false;
                for (packagePartsEndIndex in numberOfParts-1..0) {
                    value triedPackageName = ObjectArray<CharArray>(packagePartsEndIndex, CharArray(0));
                    for (j in 0:packagePartsEndIndex) {
                        triedPackageName.set(j, uncertainCompoundName.get(j));
                    }
                    
                    if (searchingInPreviousParts
                        && nameEnvironment.isPackage(triedPackageName, uncertainCompoundName.get(packagePartsEndIndex))) {
                        // Don't search for an inner class whose top-level class has the same name as an existing package;
                        break;
                    }
                    variable value triedClassNameSize = 0;
                    for (k in packagePartsEndIndex:numberOfParts-packagePartsEndIndex) {
                        triedClassNameSize += uncertainCompoundName.get(k).size + 1;
                    }
                    triedClassNameSize --;
                    
                    value triedClassName = CharArray(triedClassNameSize);
                    variable Integer currentDestinationIndex = 0;
                    variable value currentPart = uncertainCompoundName.get(packagePartsEndIndex);
                    variable value currentPartLength = currentPart.size;
                    System.arraycopy(currentPart, 0, triedClassName, currentDestinationIndex, currentPartLength);
                    currentDestinationIndex += currentPartLength;
                    for (currentPartIndex in packagePartsEndIndex+1:numberOfParts-packagePartsEndIndex-1) {
                        triedClassName.set(currentDestinationIndex, '$');
                        currentDestinationIndex++;
                        currentPart = uncertainCompoundName.get(currentPartIndex);
                        currentPartLength = currentPart.size;
                        System.arraycopy(currentPart, 0, triedClassName, currentDestinationIndex, currentPartLength);
                        currentDestinationIndex += currentPartLength;
                    }
                    
                    type = nameEnvironment.findTypeInNameLookup(
                                                CharOperation.charToString(triedClassName), 
                                                CharOperation.toString(triedPackageName));
                    if (type exists) {
                        compoundName = CharOperation.arrayConcat(triedPackageName, triedClassName);
                        break;
                    }
                    searchingInPreviousParts = true;
                }
            }
            
            if (exists existingType = type) {
                value classReaderHolder = ObjectArray<ClassFileReader>(1);
                ReferenceBinding? binding = LookupEnvironmentUtilities.toBinding(type, theLookupEnvironment, compoundName, classReaderHolder);
                if (exists binding) {
                    return JDTClass(binding, type, classReaderHolder.get(0));
                }
            } else {
                return null;
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return null;
    }
    shared actual Boolean isDeprecated(AnnotatedMirror classMirror){
        if (is JDTClass classMirror) {
            return classMirror.deprecated;
        }
        if (is JDTMethod classMirror) {
            return classMirror.deprecated;
        }
        return super.isDeprecated(classMirror);
    }
    shared actual ITypeRoot? getJavaClassRoot(ClassMirror classMirror) {
        if (is JDTClass jdtClass=classMirror) {
            IType? type = jdtClass.type;
            if (exists type) {
                return type.typeRoot;
            }
        }
        return null;
    }
    shared actual Boolean isOverloadingMethod(MethodMirror? methodMirror)  => 
            unsafeCast<JDTMethod>(methodMirror).overloadingMethod;
    
    shared actual Boolean isOverridingMethod(MethodMirror? methodMirror) => 
            unsafeCast<JDTMethod>(methodMirror).overridingMethod;
    
    shared actual Boolean moduleContainsClass(BaseIdeModule ideModule, String packageName, String className) {
        value jdtModule = unsafeCast<JDTModule>(ideModule);
        value javaProject = javaProjectInfos?.javaProject;
        variable Boolean moduleContainsJava = false;
        for (root in jdtModule.packageFragmentRoots) {
            try {
                value pf = root.getPackageFragment(packageName); // TODO : shouldn't we quote the package name ??
                if (pf.\iexists(), 
                    exists javaProject,
                    javaProject.isOnClasspath(pf)) {
                    if (pf.containsJavaResources()) {
                        moduleContainsJava = true;
                        break;
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
                moduleContainsJava = true; // Just in case ...
            }
        }
        if (moduleContainsJava) {
            ModelLoaderNameEnvironment nameEnvironment = 
                    unsafeCast<ModelLoaderNameEnvironment>(upToDateLookupEnvironment.nameEnvironment);

            if (nameEnvironment.findTypeInNameLookup(className, packageName) exists ||
                nameEnvironment.findTypeInNameLookup(className + "_", packageName) exists) {
                return true;
            }
        }
        return false;
    }
    
    shared actual Unit newCeylonBinaryUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseCeylonBinaryUnit(typeRoot, fileName, relativePath, fullPath, pkg);

    shared actual Unit newCrossProjectBinaryUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseCrossProjectBinaryUnit(typeRoot, fileName, relativePath, fullPath, pkg);

    shared actual Unit newJavaClassFile(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseJavaClassFile(typeRoot, fileName, relativePath, fullPath, pkg);

    shared actual Unit newJavaCompilationUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseJavaCompilationUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual String typeName(IType type) => type.elementName;

    shared actual Boolean typeExists(IType type) =>
            withJavaModel(() => (type of IJavaElement).\iexists()) else false;
    
    shared actual void setInterfaceCompanionClass(Declaration d, ClassOrInterface container, LazyPackage pkg) {
        LookupEnvironmentUtilities.isSettingInterfaceCompanionClassTL.set(LookupEnvironmentUtilities.isSettingInterfaceCompanionClassObj);
        super.setInterfaceCompanionClass(d, container, pkg);
        LookupEnvironmentUtilities.isSettingInterfaceCompanionClassTL.set(null);
    }
    
    logVerbose(String message) => noop();
}