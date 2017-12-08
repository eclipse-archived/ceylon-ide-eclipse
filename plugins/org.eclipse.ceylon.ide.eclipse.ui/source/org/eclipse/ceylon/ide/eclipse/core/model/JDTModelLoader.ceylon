/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.common {
    ModuleSpec
}
import org.eclipse.ceylon.ide.eclipse.core.classpath {
    CeylonClasspathUtil,
    CeylonProjectModulesContainer
}
import org.eclipse.ceylon.ide.eclipse.core.model {
    isCeylonSourceEntry
}
import org.eclipse.ceylon.ide.eclipse.core.model.mirror {
    JDTClass,
    JDTMethod,
    JDTType
}
import org.eclipse.ceylon.ide.eclipse.util {
    withJavaModel
}
import org.eclipse.ceylon.ide.common.model {
    IdeModelLoader,
    BaseIdeModule
}
import org.eclipse.ceylon.ide.common.util {
    synchronize,
    unsafeCast
}
import org.eclipse.ceylon.model.cmr {
    ArtifactResult
}
import org.eclipse.ceylon.model.loader.mirror {
    ClassMirror,
    MethodMirror,
    AnnotatedMirror,
    FunctionalInterfaceType,
    TypeMirror
}
import org.eclipse.ceylon.model.loader.model {
    LazyPackage
}
import org.eclipse.ceylon.model.typechecker.model {
    Modules,
    Declaration,
    ClassOrInterface
}

import java.lang {
    CharArray,
    ObjectArray,
    System,
    Types
}
import java.lang.ref {
    WeakReference
}
import java.util {
    WeakHashMap
}
import java.util.concurrent.locks {
    ReentrantReadWriteLock
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
import org.eclipse.jdt.internal.compiler.ast {
    CompilationUnitDeclaration
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
    ReferenceBinding,
    CompilationUnitScope,
    MethodBinding,
    TypeIds
}
import org.eclipse.jdt.internal.compiler.problem {
    ProblemReporter,
    DefaultProblemFactory
}
import org.eclipse.jdt.internal.core {
    JavaProject
}

CharArray toCharArray(String s) => Types.nativeString(s).toCharArray();

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
        shared late variable CompilationUnitScope dummyCompilationUnitScope;

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
            missingTypeBinding = MissingTypeBinding(lookupEnvironment.defaultPackage, ObjectArray.with { toCharArray("unknown") }, lookupEnvironment);
            dummyCompilationUnitScope = CompilationUnitScope(
                CompilationUnitDeclaration(
                    lookupEnvironment.problemReporter, 
                    null, 
                    0), lookupEnvironment);
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
    String? jdkProvider;
    
    shared actual Object lookupEnvironmentMutex = object extends Basic() {};

    value typeMirrorCache = Cache(2000);
    value typeMirrorCacheLock = ReentrantReadWriteLock();
    
    shared new (
        JDTModuleManager moduleManager,
        JDTModuleSourceMapper moduleSourceMapper,
        Modules modules,
        String? jdkProvider
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
        this.jdkProvider = jdkProvider;
    }
    
    shared actual String? alternateJdkModuleSpec => jdkProvider;
        
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
        try {
            typeMirrorCache.discardAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    shared actual LookupEnvironment upToDateLookupEnvironment {
        assert(exists javaProjectInfos);
        resetJavaModelSourceIfNecessary(() =>
                synchronize {
                    on = lookupEnvironmentMutex;
                    do() => createLookupEnvironment();
                }
        );
        return javaProjectInfos.lookupEnvironment;
    }

    shared actual LookupEnvironment currentLookupEnvironment {
        assert(exists javaProjectInfos);
        return javaProjectInfos.lookupEnvironment;
    }
    
    shared MissingTypeBinding missingTypeBinding =>
            callWithLock {
                function fun() {
                    assert(exists javaProjectInfos);
                    return javaProjectInfos.missingTypeBinding;
                }
            };
    shared actual LookupEnvironment? createLookupEnvironmentForGeneratedCode() =>
            javaProjectInfos?.createLookupEnvironmentForGeneratedCode();
    shared actual void refreshNameEnvironment() =>
            runWithLock {
        void action() {
            assert(exists javaProjectInfos);
            javaProjectInfos.refreshNameEnvironment();
        }
    };
    shared actual void addModuleToClasspathInternal(ArtifactResult? artifact) {
        if (exists jp = jdkProvider,
            exists artifact) {
            ModuleSpec jpSpec = ModuleSpec.parse(jp);
            if (artifact.name() == jpSpec.name &&
            	artifact.version() == jpSpec.version) {
                return;
            }
        }
        assert(exists javaProject = javaProjectInfos?.javaProject);
        CeylonProjectModulesContainer? container = CeylonClasspathUtil.getCeylonProjectModulesClasspathContainer(javaProject);
        
        if (exists container) {
            assert(exists artifact);
            IPath modulePath = Path(artifact.artifact().path);
            IClasspathEntry? newEntry = container.addNewClasspathEntryIfNecessary(modulePath);
            if (exists newEntry) {
                try {
                    JavaCore.setClasspathContainer(container.path, ObjectArray.with { javaProject }, 
                        ObjectArray<IClasspathContainer>.with{ CeylonProjectModulesContainer(container) } , null);
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
            ObjectArray<CharArray> uncertainCompoundName = CharOperation.splitOn('.', Types.nativeString(name).toCharArray());
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
                    variable CharArray currentPart = uncertainCompoundName.get(packagePartsEndIndex);
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
    
    shared actual String typeName(IType type) => type.elementName;

    shared actual Boolean typeExists(IType type) =>
            withJavaModel(() => (type of IJavaElement).\iexists()) else false;
    
    shared actual void setInterfaceCompanionClass(Declaration d, ClassOrInterface container, LazyPackage pkg) {
        LookupEnvironmentUtilities.isSettingInterfaceCompanionClassTL.set(LookupEnvironmentUtilities.isSettingInterfaceCompanionClassObj);
        super.setInterfaceCompanionClass(d, container, pkg);
        LookupEnvironmentUtilities.isSettingInterfaceCompanionClassTL.set(null);
    }
    
    logVerbose(String message) => noop();
    
    shared actual Boolean isGetter(MethodBinding methodBinding, String methodName) {
        if(! methodBinding.typeVariables().size > 0) {            
            return false;
        }
        function matchesPrefix(String prefix) => 
                let(stringAfterPrefix = methodName[prefix.size...])
                if (exists charAfterPrefix = stringAfterPrefix.first,
                    methodName.startsWith("get"),
                    isStartOfJavaBeanPropertyName(charAfterPrefix.integer),
                    stringAfterPrefix != "String",
                    stringAfterPrefix != "Hash",
                    stringAfterPrefix != "Equals")
                then true
                else false;
        
        value matchesIs = matchesPrefix("is");
        value matchesGet = matchesPrefix("get");
        value hasNoParams = methodBinding.parameters.size == 0;
        value hasNonVoidReturn = methodBinding.returnType.id != TypeIds.t_void;
        value hasBooleanReturn = methodBinding.returnType.id == TypeIds.t_boolean;
        return (matchesGet && hasNonVoidReturn || matchesIs && hasBooleanReturn) && hasNoParams;
    }
    
    shared actual String? isFunctionalInterface(ClassMirror klass){
        if (is JDTClass klass) {
            return klass.isFunctionalInterface();
        }
        return null;
    }

    shared actual Boolean isFunctionalInterfaceType(TypeMirror typeMirror){
        if (is JDTType jdtType = typeMirror,
            exists fit = jdtType.functionalInterfaceType) {
            return true;
        }
        return false;
    }
    
    shared actual FunctionalInterfaceType? getFunctionalInterfaceType(TypeMirror typeMirror) {
        if (is JDTType jdtType = typeMirror,
            exists fit = jdtType.functionalInterfaceType) {
            return fit;
        }
        return null;
    }
    
    
    shared actual TypeMirror? getCachedTypeMirror(CharArray bindingKey) {
        value lock = typeMirrorCacheLock.readLock();
        lock.lock();
        try {
            Cache.Entry? cacheEntry = typeMirrorCache.getEntry(bindingKey);
            if (exists cacheEntry) {
                WeakReference<TypeMirror>? cachedTypeMirrorRef = unsafeCast<WeakReference<TypeMirror>>(cacheEntry.cached);
                if (exists cachedTypeMirrorRef) {
                    return cachedTypeMirrorRef.get();
                }
            }
        } finally {
            lock.unlock();
        }
        return null;
    }
    
    shared actual void cacheTypeMirror(CharArray bindingKey, TypeMirror mirror) {
        value lock = typeMirrorCacheLock.writeLock();
        lock.lock();
        try {
            typeMirrorCache.addEntry(bindingKey, WeakReference(mirror));
        } finally {
            lock.unlock();
        }
    }
}
