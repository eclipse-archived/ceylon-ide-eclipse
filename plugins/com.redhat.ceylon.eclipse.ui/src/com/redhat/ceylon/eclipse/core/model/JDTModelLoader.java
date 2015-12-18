/*
 * Copyright Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the authors tag. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License version 2.
 * 
 * This particular file is subject to the "Classpath" exception as provided in the 
 * LICENSE file that accompanied this code.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.redhat.ceylon.eclipse.core.model;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.core.internal.utils.Cache;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.JavaProject;

import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;
import com.redhat.ceylon.eclipse.core.model.mirror.JDTClass;
import com.redhat.ceylon.eclipse.core.model.mirror.JDTMethod;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.BaseIdeModuleManager;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.IdeModelLoader;
import com.redhat.ceylon.ide.common.model.IdeModuleManager;
import com.redhat.ceylon.ide.common.model.IdeModuleSourceMapper;
import com.redhat.ceylon.ide.common.util.toCeylonString_;
import com.redhat.ceylon.model.cmr.ArtifactResult;
import com.redhat.ceylon.model.loader.ModelResolutionException;
import com.redhat.ceylon.model.loader.mirror.AnnotatedMirror;
import com.redhat.ceylon.model.loader.mirror.ClassMirror;
import com.redhat.ceylon.model.loader.mirror.MethodMirror;
import com.redhat.ceylon.model.loader.model.LazyPackage;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Unit;

/**
 * A model loader which uses the JDT model.
 *
 * @author David Festal <david.festal@serli.com>
 */
public class JDTModelLoader extends IdeModelLoader<IProject, IResource, IFolder, IFile, ITypeRoot> {

    private IJavaProject javaProject;
    private CompilerOptions compilerOptions;

    private ProblemReporter problemReporter;
    private LookupEnvironment lookupEnvironment;
    private MissingTypeBinding missingTypeBinding;
    private final Object lookupEnvironmentMutex = new Object();

    public JDTModelLoader(final IdeModuleManager<IProject, IResource, IFolder, IFile> moduleManager,
            IdeModuleSourceMapper<IProject, IResource, IFolder, IFile> moduleSourceMapper, 
            final Modules modules){
        super(
                TypeDescriptor.klass(IProject.class),
                TypeDescriptor.klass(IResource.class),
                TypeDescriptor.klass(IFolder.class),
                TypeDescriptor.klass(IFile.class),
                TypeDescriptor.klass(ITypeRoot.class),
                moduleManager, moduleSourceMapper, modules);

        javaProject = getJavaProject(moduleManager);
        
        if (javaProject != null) {
            compilerOptions = new CompilerOptions(javaProject.getOptions(true));
            compilerOptions.ignoreMethodBodies = true;
            compilerOptions.storeAnnotations = true;
            problemReporter = new ProblemReporter(
                    DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                    compilerOptions,
                    new DefaultProblemFactory());
        }
        
        createLookupEnvironment();
        if (javaProject != null) {
            modelLoaders.put(javaProject.getProject(), new WeakReference<JDTModelLoader>(this));
        }
    }

    public void createLookupEnvironment() {
        if (javaProject == null) {
            return;
        }
        try {
            ModelLoaderTypeRequestor requestor = new ModelLoaderTypeRequestor(compilerOptions);
            lookupEnvironment = new LookupEnvironment(requestor, compilerOptions, problemReporter, createSearchableEnvironment());
            requestor.initialize(lookupEnvironment);
            lookupEnvironment.mayTolerateMissingType = true;
            missingTypeBinding = new MissingTypeBinding(lookupEnvironment.defaultPackage, new char[][] {"unknown".toCharArray()}, lookupEnvironment);
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    public LookupEnvironment createLookupEnvironmentForGeneratedCode() {
        if (javaProject == null) {
            return null;
        }
        try {
            ModelLoaderTypeRequestor requestor = new ModelLoaderTypeRequestor(compilerOptions);
            LookupEnvironment lookupEnvironmentForGeneratedCode = new LookupEnvironment(requestor, 
                    compilerOptions, 
                    problemReporter, 
                    ((JavaProject)javaProject).newSearchableNameEnvironment((WorkingCopyOwner)null));
            requestor.initialize(lookupEnvironmentForGeneratedCode);
            lookupEnvironmentForGeneratedCode.mayTolerateMissingType = true;
            return lookupEnvironmentForGeneratedCode;
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private INameEnvironment createSearchableEnvironment() throws JavaModelException {
        return new ModelLoaderNameEnvironment(javaProject);
    }
    
    private LookupEnvironment getLookupEnvironment() {
        resetJavaModelSourceIfNecessary(new Runnable() {
            @Override
            public void run() {
                synchronized (lookupEnvironment) {
                    createLookupEnvironment();
                }
            }
        });
        return lookupEnvironment;
    }
    
    @Override
    public boolean moduleContainsClass(BaseIdeModule ideModule, String packageName, String className) {
        boolean moduleContainsJava = false;
        for (IPackageFragmentRoot root : modelJ2C().getModulePackageFragmentRoots(ideModule)) {
            try {
                IPackageFragment pf = root.getPackageFragment(packageName);
                if (pf.exists() && 
                        javaProject.isOnClasspath(pf)) {
                    if (((IPackageFragment)pf).containsJavaResources()) {
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
            ModelLoaderNameEnvironment nameEnvironment = getNameEnvironment();
            if (nameEnvironment.findTypeInNameLookup(className, packageName) != null ||
                    nameEnvironment.findTypeInNameLookup(className + "_", packageName) != null) {
                return true;
            }
        }
        return false;
    }

    public MissingTypeBinding getMissingTypeBinding() {
        synchronized (getLock()) {
            return missingTypeBinding;
        }
    }
    
    @Override
    public boolean loadPackage(Module module, String packageName, boolean loadDeclarations) {
        synchronized (getLock()) {
            packageName = Util.quoteJavaKeywords(packageName);
            if(loadDeclarations && !loadedPackages.add(cacheKeyByModule(module, packageName))){
                return true;
            }
            
            if (module instanceof BaseIdeModule) {
                BaseIdeModule jdtModule = (BaseIdeModule) module;
                List<IPackageFragmentRoot> roots = modelJ2C().getModulePackageFragmentRoots(jdtModule);
                IPackageFragment packageFragment = null;
                for (IPackageFragmentRoot root : roots) {
                    // skip packages that are not present
                    if(! root.exists() || ! javaProject.isOnClasspath(root))
                        continue;
                    try {
                        IClasspathEntry entry = root.getRawClasspathEntry();
                        
                        //TODO: is the following really necessary?
                        //Note that getContentKind() returns an undefined
                        //value for a classpath container or variable
                        if (entry.getEntryKind()!=IClasspathEntry.CPE_CONTAINER &&
                                entry.getEntryKind()!=IClasspathEntry.CPE_VARIABLE &&
                                entry.getContentKind()==IPackageFragmentRoot.K_SOURCE && 
                                !CeylonBuilder.isCeylonSourceEntry(entry)) {
                            continue;
                        }
                        
                        packageFragment = root.getPackageFragment(packageName);
                        if(! packageFragment.exists()){
                            continue;
                        }
                    } catch (JavaModelException e) {
                        if (! e.isDoesNotExist()) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    if(!loadDeclarations) {
                        // we found the package
                        return true;
                    }
                    
                    // we have a few virtual types in java.lang that we need to load but they are not listed from class files
                    if(module.getNameAsString().equals(JAVA_BASE_MODULE_NAME)
                            && packageName.equals("java.lang")) {
                        loadJavaBaseArrays();
                    }
                    
                    IClassFile[] classFiles = new IClassFile[] {};
                    org.eclipse.jdt.core.ICompilationUnit[] compilationUnits = new org.eclipse.jdt.core.ICompilationUnit[] {};
                    try {
                        classFiles = packageFragment.getClassFiles();
                    } catch (JavaModelException e) {
                        e.printStackTrace();
                    }
                    try {
                        compilationUnits = packageFragment.getCompilationUnits();
                    } catch (JavaModelException e) {
                        e.printStackTrace();
                    }
                    
                    List<IType> typesToLoad = new LinkedList<>();
                    for (IClassFile classFile : classFiles) {
                        IType type = classFile.getType();
                        typesToLoad.add(type);
                    }
                    
                    for (org.eclipse.jdt.core.ICompilationUnit compilationUnit : compilationUnits) {
                        // skip removed CUs
                        if(!compilationUnit.exists())
                            continue;
                        try {
                            for (IType type : compilationUnit.getTypes()) {
                                typesToLoad.add(type);
                            }
                        } catch (JavaModelException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    for (IType type : typesToLoad) {
                        String typeFullyQualifiedName = type.getFullyQualifiedName();
                        String[] nameParts = typeFullyQualifiedName.split("\\.");
                        String typeQualifiedName = nameParts[nameParts.length - 1];
                        // only top-levels are added in source declarations
                        if (typeQualifiedName.indexOf('$') > 0) {
                            continue;
                        }
                        
                        if (type.exists() 
                                && !getSourceDeclarations().defines(toCeylonString_.toCeylonString(getToplevelQualifiedName(type.getPackageFragment().getElementName(), typeFullyQualifiedName)))
                                && ! isTypeHidden(module, typeFullyQualifiedName)) {  
                            convertToDeclaration(module, typeFullyQualifiedName, DeclarationType.VALUE);
                        }
                    }
                }
            }
            return false;
        }
    }

    public void refreshNameEnvironment() {
        synchronized (getLock()) {
            try {
                lookupEnvironment.nameEnvironment = createSearchableEnvironment();
            } catch (JavaModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
        }
    }
    
    @Override
    public JDTClass buildClassMirrorInternal(String name) {
        if (javaProject == null) {
            return null;
        }
        
        try {
            LookupEnvironment theLookupEnvironment = getLookupEnvironment();
            ModelLoaderNameEnvironment nameEnvironment = (ModelLoaderNameEnvironment)theLookupEnvironment.nameEnvironment;
            char[][] uncertainCompoundName = CharOperation.splitOn('.', name.toCharArray());
            int numberOfParts = uncertainCompoundName.length;
            char[][] compoundName = null;
            IType type = null;
            
            if (numberOfParts > 0) {
                boolean searchingInPreviousParts = false;
                for (int packagePartsEndIndex=numberOfParts-1; 
                        packagePartsEndIndex >= 0; 
                        packagePartsEndIndex--) {
                    char[][] triedPackageName = new char[packagePartsEndIndex][0];
                    for (int j=0; j<packagePartsEndIndex; j++) {
                        triedPackageName[j] = uncertainCompoundName[j];
                    }
                    
                    if (searchingInPreviousParts 
                             && nameEnvironment.isPackage(triedPackageName, uncertainCompoundName[packagePartsEndIndex])) {
                        // Don't search for an inner class whose top-level class has the same name as an existing package;
                        break;
                    }
                    
                    int triedClassNameSize = 0;
                    for (int k=packagePartsEndIndex; k<numberOfParts; k++) {
                        triedClassNameSize += uncertainCompoundName[k].length + 1;
                    }
                    triedClassNameSize --;
                    
                    char[] triedClassName = new char[triedClassNameSize];
                    int currentDestinationIndex = 0;
                    int currentPartIndex=packagePartsEndIndex;
                    char[] currentPart = uncertainCompoundName[currentPartIndex];
                    int currentPartLength = currentPart.length;
                    System.arraycopy(currentPart, 0, triedClassName, currentDestinationIndex, currentPartLength);
                    currentDestinationIndex += currentPartLength;
                    for (currentPartIndex=packagePartsEndIndex+1; currentPartIndex<numberOfParts; currentPartIndex++) {
                        triedClassName[currentDestinationIndex++] = '$';
                        currentPart = uncertainCompoundName[currentPartIndex];
                        currentPartLength = currentPart.length;
                        System.arraycopy(currentPart, 0, triedClassName, currentDestinationIndex, currentPartLength);
                        currentDestinationIndex += currentPartLength;
                    }
                    
                    type = nameEnvironment.findTypeInNameLookup(CharOperation.charToString(triedClassName), 
                            CharOperation.toString(triedPackageName));
                    if (type != null) {
                        compoundName = CharOperation.arrayConcat(triedPackageName, triedClassName);
                        break;
                    }
                    searchingInPreviousParts = true;
                }
            }

            if (type == null) {
                return null;
            }

            ClassFileReader[] classReaderHolder = new ClassFileReader[1];
            ReferenceBinding binding = toBinding(type, theLookupEnvironment, compoundName, classReaderHolder);
            if (binding != null) {
                return new JDTClass(binding, type, classReaderHolder[0]);
            }
            
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ModelLoaderNameEnvironment getNameEnvironment() {
        return (ModelLoaderNameEnvironment)getLookupEnvironment().nameEnvironment;
    }
    
    @Override
    public Object addModuleToClasspathInternal(ArtifactResult artifact) {
        CeylonProjectModulesContainer container = CeylonClasspathUtil.getCeylonProjectModulesClasspathContainer(javaProject);

        if (container != null) {
            IPath modulePath = new Path(artifact.artifact().getPath());
            IClasspathEntry newEntry = container.addNewClasspathEntryIfNecessary(modulePath);
            if (newEntry!=null) {
                try {
                    JavaCore.setClasspathContainer(container.getPath(), new IJavaProject[] { javaProject }, 
                            new IClasspathContainer[] {new CeylonProjectModulesContainer(container)}, null);
                } catch (JavaModelException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                refreshNameEnvironment();
            }
        }
        return null;
    }
    
    @Override
    protected boolean isOverridingMethod(MethodMirror methodSymbol) {
        return ((JDTMethod)methodSymbol).isOverridingMethod();
    }

    @Override
    protected boolean isOverloadingMethod(MethodMirror methodSymbol) {
        return ((JDTMethod)methodSymbol).isOverloadingMethod();
    }

    @Override
    public ITypeRoot getJavaClassRoot(ClassMirror classMirror) {
        if (classMirror instanceof JDTClass) {
            JDTClass jdtClass = (JDTClass) classMirror;
            IType type = jdtClass.getType();
            if (type != null) {
                return type.getTypeRoot();
            }
        }
        return null;
    }
    
    @Override
    protected boolean isDeprecated(AnnotatedMirror classMirror){
        if (classMirror instanceof JDTClass) {
            return ((JDTClass)classMirror).isDeprecated();
        }
        if (classMirror instanceof JDTMethod) {
            return ((JDTMethod)classMirror).isDeprecated();
        }
        return super.isDeprecated(classMirror);
    }

    @Override
    public Unit newCrossProjectBinaryUnit(ITypeRoot typeRoot,
            String relativePath, String fileName, String fullPath,
            LazyPackage pkg) {
        return modelJ2C().newCrossProjectBinaryUnit(typeRoot, relativePath, fileName, fullPath, pkg);
    }

    @Override
    public Unit newJavaCompilationUnit(ITypeRoot typeRoot, String relativePath,
            String fileName, String fullPath, LazyPackage pkg) {
        return modelJ2C().newJavaCompilationUnit(typeRoot, relativePath, fileName, fullPath, pkg);
    }

    @Override
    public Unit newCeylonBinaryUnit(ITypeRoot typeRoot, String relativePath,
            String fileName, String fullPath, LazyPackage pkg) {
        return modelJ2C().newCeylonBinaryUnit(typeRoot, relativePath, fileName, fullPath, pkg);
    }

    @Override
    public Unit newJavaClassFile(ITypeRoot typeRoot, String relativePath,
            String fileName, String fullPath, LazyPackage pkg) {
        return modelJ2C().newJavaClassFile(typeRoot, relativePath, fileName, fullPath, pkg);
    }

    

    
    private static IJavaProject getJavaProject(BaseIdeModuleManager moduleManager) {
        @SuppressWarnings("unchecked")
        CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject = 
                (CeylonProject<IProject, IResource, IFolder, IFile>) moduleManager.getCeylonProject();
        
        if (ceylonProject != null) {
            IProject project = ceylonProject.getIdeArtifact();
            return JavaCore.create(project);
        }
        return null;
    }
    
    private static final ThreadLocal<Object> isSettingInterfaceCompanionClassTL = new ThreadLocal<>();
    private static final Object isSettingInterfaceCompanionClassObj = new Object();
    
    static boolean isSettingInterfaceCompanionClass() {
        return isSettingInterfaceCompanionClassTL.get() != null;
    }
    
    @Override
    protected void setInterfaceCompanionClass(Declaration d, ClassOrInterface container, LazyPackage pkg) {
        isSettingInterfaceCompanionClassTL.set(isSettingInterfaceCompanionClassObj);
        super.setInterfaceCompanionClass(d, container, pkg);
        isSettingInterfaceCompanionClassTL.set(null);
    }
    
    private static WeakHashMap<IProject, WeakReference<JDTModelLoader>> modelLoaders = new WeakHashMap<>();
    private static Cache archivesRootsToModelLoaderCache = new Cache(20);
    
    public static JDTModelLoader getModelLoader(IProject project) {
        JDTModelLoader modelLoader = null;
        WeakReference<JDTModelLoader> modelLoaderRef = modelLoaders.get(project);
        if (modelLoaderRef != null) {
            modelLoader = modelLoaderRef.get();
        }
        return modelLoader;
    }

    public static JDTModelLoader getModelLoader(IJavaProject javaProject) {
        return getModelLoader(javaProject.getProject());
    }
    
    public static JDTModelLoader getModelLoader(IType type) {
        if (type == null) {
            return null;
        }
        JDTModelLoader modelLoader = getModelLoader(type.getJavaProject());
        if (modelLoader == null) {
            IPackageFragmentRoot pfr = (IPackageFragmentRoot) type.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
            if (pfr.isExternal() && pfr.isArchive()) {
                synchronized (archivesRootsToModelLoaderCache) {
                    Cache.Entry cacheEntry = archivesRootsToModelLoaderCache.getEntry(pfr.getPath());
                    if (cacheEntry != null) {
                        @SuppressWarnings("unchecked")
                        WeakReference<JDTModelLoader> cachedModelLoaderRef = (WeakReference<JDTModelLoader>) cacheEntry.getCached();
                        if (cachedModelLoaderRef != null) {
                            modelLoader = cachedModelLoaderRef.get();
                        }
                    }
                    if (modelLoader == null) {
                        for (WeakReference<JDTModelLoader> loaderRef : modelLoaders.values()) {
                            JDTModelLoader loader = loaderRef.get();
                            if (loader == null) {
                                continue;
                            }
                            BaseIdeModuleManager moduleManager = loader.getModuleManager();
                            if (moduleManager == null) {
                                continue;
                            }
                            IJavaProject javaProject = getJavaProject(moduleManager);
                            if (javaProject == null) {
                                continue;
                            }
                            try {
                                if (javaProject.findPackageFragmentRoot(pfr.getPath()) != null){
                                    modelLoader = loader;
                                    archivesRootsToModelLoaderCache.addEntry(pfr.getPath(), new WeakReference<>(modelLoader));
                                    break;
                                }
                            } catch (JavaModelException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return modelLoader;
    }

    public static interface ActionOnResolvedType {
        void doWithBinding(ReferenceBinding referenceBinding);
    }
    
    public static interface ActionOnMethodBinding {
        void doWithBinding(IType declaringClassModel, ReferenceBinding declaringClassBinding, MethodBinding methodBinding);
    }
    
    public static interface ActionOnClassBinding {
        void doWithBinding(IType classModel, ReferenceBinding classBinding);
    }
    
    public static boolean doWithReferenceBinding(final IType typeModel, final ReferenceBinding binding, final ActionOnClassBinding action) {
        if (typeModel == null) {
            throw new ModelResolutionException("Resolving action requested on a missing declaration");
        }
        
        if (binding == null) {
            return false;
        }
        
        PackageBinding packageBinding = binding.getPackage();
        if (packageBinding == null) {
            return false;
        }
        LookupEnvironment lookupEnvironment = packageBinding.environment;
        if (lookupEnvironment == null) {
            return false;
        }
        JDTModelLoader modelLoader = getModelLoader(typeModel);
        if (modelLoader == null) {
            throw new ModelResolutionException("The Model Loader corresponding to the type '" + typeModel.getFullyQualifiedName() + "' was not available");
        }
        
        synchronized (modelLoader.lookupEnvironmentMutex) {
            if (modelLoader.lookupEnvironment != lookupEnvironment) {
                return false;
            }
            action.doWithBinding(typeModel, binding);
            return true;
        }
    }

    public static boolean doWithMethodBinding(final IType declaringClassModel, final MethodBinding binding, final ActionOnMethodBinding action) {
        if (declaringClassModel == null) {
            throw new ModelResolutionException("Resolving action requested on a missing declaration");
        }

        if (binding == null) {
            return false;
        }
        ReferenceBinding declaringClassBinding = binding.declaringClass;
        if (declaringClassBinding == null) {
            return false;
        }
        PackageBinding packageBinding = declaringClassBinding.getPackage();
        if (packageBinding == null) {
            return false;
        }
        LookupEnvironment lookupEnvironment = packageBinding.environment;
        if (lookupEnvironment == null) {
            return false;
        }
        
        JDTModelLoader modelLoader = getModelLoader(declaringClassModel);
        if (modelLoader == null) {
            throw new ModelResolutionException("The Model Loader corresponding the type '" + declaringClassModel.getFullyQualifiedName() + "' doesn't exist");
        }
        
        synchronized (modelLoader.lookupEnvironmentMutex) {
            if (modelLoader.lookupEnvironment != lookupEnvironment) {
                return false;
            }
            action.doWithBinding(declaringClassModel, declaringClassBinding, binding);
            return true;
        }
    }

    public static interface ActionOnResolvedGeneratedType {
        void doWithBinding(IType classModel, ReferenceBinding classBinding, IBinaryType binaryType);
    }

    public static void doOnResolvedGeneratedType(IType typeModel, ActionOnResolvedGeneratedType action) {
        if (typeModel == null || ! typeModel.exists()) {
            throw new ModelResolutionException("Resolving action requested on a missing declaration");
        }
        
        JDTModelLoader modelLoader = getModelLoader(typeModel);
        if (modelLoader == null) {
            throw new ModelResolutionException("The Model Loader is not available to resolve type '" + typeModel.getFullyQualifiedName() + "'");
        }
        char[][] compoundName = CharOperation.splitOn('.', typeModel.getFullyQualifiedName().toCharArray());
        LookupEnvironment lookupEnvironment = modelLoader.createLookupEnvironmentForGeneratedCode();
        ReferenceBinding binding = null;
        IBinaryType binaryType = null;
        try {
            ITypeRoot typeRoot = typeModel.getTypeRoot();
            
            if (typeRoot instanceof IClassFile) {
                ClassFile classFile = (ClassFile) typeRoot;
                
                IFile classFileRsrc = (IFile) classFile.getCorrespondingResource();
                if (classFileRsrc!=null && !classFileRsrc.exists()) {
                    //the .class file has been deleted
                    return;
                }
                
                BinaryTypeBinding binaryTypeBinding = null;
                try {
                    binaryType = classFile.getBinaryTypeInfo(classFileRsrc, true);
                    binaryTypeBinding = lookupEnvironment.cacheBinaryType(binaryType, null);
                } catch(JavaModelException e) {
                    if (! e.isDoesNotExist()) {
                        throw e;
                    }
                }
                
                if (binaryTypeBinding == null) {
                    ReferenceBinding existingType = lookupEnvironment.getCachedType(compoundName);
                    if (existingType == null || ! (existingType instanceof BinaryTypeBinding)) {
                        return;
                    }
                    binaryTypeBinding = (BinaryTypeBinding) existingType;
                }
                binding = binaryTypeBinding;
            }
        } catch (JavaModelException e) {
            throw new ModelResolutionException(e);
        }
        if (binaryType != null
                && binding != null) {
            action.doWithBinding(typeModel, binding, binaryType);
        }
    }
    
    public static void doWithResolvedType(IType typeModel, ActionOnResolvedType action) {
        if (typeModel == null || ! typeModel.exists()) {
            throw new ModelResolutionException("Resolving action requested on a missing declaration");
        }
        
        JDTModelLoader modelLoader = getModelLoader(typeModel);
        if (modelLoader == null) {
            throw new ModelResolutionException("The Model Loader is not available to resolve type '" + typeModel.getFullyQualifiedName() + "'");
        }
        char[][] compoundName = CharOperation.splitOn('.', typeModel.getFullyQualifiedName().toCharArray());
        LookupEnvironment lookupEnvironment = modelLoader.getLookupEnvironment();
        synchronized (modelLoader.lookupEnvironmentMutex) {
            ReferenceBinding binding;
            try {
                binding = toBinding(typeModel, lookupEnvironment, compoundName);
            } catch (JavaModelException e) {
                throw new ModelResolutionException(e);
            }
            if (binding == null) {
                throw new ModelResolutionException("Binding not found for type : '" + typeModel.getFullyQualifiedName() + "'");
            }
            action.doWithBinding(binding);
        }
    }
    
    public static IType toType(ReferenceBinding binding) {
        ModelLoaderNameEnvironment nameEnvironment = (ModelLoaderNameEnvironment) binding.getPackage().environment.nameEnvironment;
        char[][] compoundName = ((ReferenceBinding) binding).compoundName;
        IType typeModel = nameEnvironment.findTypeInNameLookup(compoundName);
        
        if (typeModel == null && ! (binding instanceof MissingTypeBinding)) {
            throw new ModelResolutionException("JDT reference binding without a JDT IType element : " + CharOperation.toString(compoundName));
        }
        return typeModel;
    }

    private static final String OLD_PACKAGE_DESCRIPTOR_CLASS_NAME = Naming.PACKAGE_DESCRIPTOR_CLASS_NAME.substring(1);
    static final char[] packageDescriptorName = Naming.PACKAGE_DESCRIPTOR_CLASS_NAME.toCharArray();
    static final char[] moduleDescriptorName = Naming.MODULE_DESCRIPTOR_CLASS_NAME.toCharArray();
    static final char[] oldPackageDescriptorName = OLD_PACKAGE_DESCRIPTOR_CLASS_NAME.toCharArray();
    static final char[] oldModuleDescriptorName = Naming.OLD_MODULE_DESCRIPTOR_CLASS_NAME.toCharArray();
    static final char[][] descriptorClassNames = new char[][] { packageDescriptorName, moduleDescriptorName };

    private static ReferenceBinding toBinding(IType type, LookupEnvironment theLookupEnvironment, char[][] compoundName) throws JavaModelException {
        return toBinding(type, theLookupEnvironment, compoundName, null);
    }

    private static ReferenceBinding toBinding(IType type, LookupEnvironment theLookupEnvironment, char[][] compoundName, ClassFileReader[] readerHolder) throws JavaModelException {
        ITypeRoot typeRoot = type.getTypeRoot();
        
        if (typeRoot instanceof IClassFile) {
            ClassFile classFile = (ClassFile) typeRoot;
            
            IFile classFileRsrc = (IFile) classFile.getCorrespondingResource();
            if (classFileRsrc!=null && !classFileRsrc.exists()) {
                //the .class file has been deleted
                return null;
            }
            
            BinaryTypeBinding binaryTypeBinding = null;
            try {
                IBinaryType binaryType = classFile.getBinaryTypeInfo(classFileRsrc, true);
                if (readerHolder != null 
                        && readerHolder.length == 1
                        && binaryType instanceof ClassFileReader) {
                    readerHolder[0] = (ClassFileReader) binaryType;
                }
                binaryTypeBinding = theLookupEnvironment.cacheBinaryType(binaryType, null);
            } catch(JavaModelException e) {
                if (! e.isDoesNotExist()) {
                    throw e;
                }
            }
            
            if (binaryTypeBinding == null) {
                ReferenceBinding existingType = theLookupEnvironment.getCachedType(compoundName);
                if (existingType == null || ! (existingType instanceof BinaryTypeBinding)) {
                    return null;
                }
                binaryTypeBinding = (BinaryTypeBinding) existingType;
            }
            return binaryTypeBinding;
        } else {
            ReferenceBinding referenceBinding = theLookupEnvironment.getType(compoundName);
            if (referenceBinding != null  && ! (referenceBinding instanceof BinaryTypeBinding)) {
                
                if (referenceBinding instanceof ProblemReferenceBinding) {
                    ProblemReferenceBinding problemReferenceBinding = (ProblemReferenceBinding) referenceBinding;
                    if (problemReferenceBinding.problemId() == ProblemReasons.InternalNameProvided) {
                        referenceBinding = problemReferenceBinding.closestReferenceMatch();
                    } else {
                        System.out.println(ProblemReferenceBinding.problemReasonString(problemReferenceBinding.problemId()));
                        return null;
                    }
                }
                return referenceBinding;
            }
            return null;
        }
    }
}
