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

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isInCeylonClassesOutputFolder;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.eclipse.core.internal.jobs.InternalJob;
import org.eclipse.core.internal.utils.Cache;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.BasicCompilationUnit;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementRequestor;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.NameLookup;
import org.eclipse.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.core.SourceTypeElementInfo;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;

import com.redhat.ceylon.common.JVMModuleUtil;
import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.loader.AnnotationLoader;
import com.redhat.ceylon.compiler.java.loader.TypeFactory;
import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.java.loader.SourceDeclarationVisitor;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PackageDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PositionalArgument;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;
import com.redhat.ceylon.eclipse.core.model.mirror.JDTClass;
import com.redhat.ceylon.eclipse.core.model.mirror.JDTMethod;
import com.redhat.ceylon.eclipse.core.model.mirror.SourceClass;
import com.redhat.ceylon.eclipse.core.model.mirror.SourceDeclarationHolder;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.model.cmr.ArtifactResult;
import com.redhat.ceylon.model.loader.AbstractModelLoader;
import com.redhat.ceylon.model.loader.JvmBackendUtil;
import com.redhat.ceylon.model.loader.ModelResolutionException;
import com.redhat.ceylon.model.loader.Timer;
import com.redhat.ceylon.model.loader.TypeParser;
import com.redhat.ceylon.model.loader.mirror.AnnotatedMirror;
import com.redhat.ceylon.model.loader.mirror.AnnotationMirror;
import com.redhat.ceylon.model.loader.mirror.ClassMirror;
import com.redhat.ceylon.model.loader.mirror.MethodMirror;
import com.redhat.ceylon.model.loader.model.AnnotationProxyClass;
import com.redhat.ceylon.model.loader.model.AnnotationProxyMethod;
import com.redhat.ceylon.model.loader.model.LazyClass;
import com.redhat.ceylon.model.loader.model.LazyElement;
import com.redhat.ceylon.model.loader.model.LazyFunction;
import com.redhat.ceylon.model.loader.model.LazyInterface;
import com.redhat.ceylon.model.loader.model.LazyModule;
import com.redhat.ceylon.model.loader.model.LazyPackage;
import com.redhat.ceylon.model.loader.model.LazyValue;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.UnknownType;
import com.redhat.ceylon.model.typechecker.model.UnknownType.ErrorReporter;

/**
 * A model loader which uses the JDT model.
 *
 * @author David Festal <david.festal@serli.com>
 */
public class JDTModelLoader extends AbstractModelLoader {

    private IJavaProject javaProject;
    private CompilerOptions compilerOptions;

    private ProblemReporter problemReporter;
    private LookupEnvironment lookupEnvironment;
    private MissingTypeBinding missingTypeBinding;
    private final Object lookupEnvironmentMutex = new Object();
    private boolean mustResetLookupEnvironment = false;
    private Set<Module> modulesInClassPath = new HashSet<Module>();
    private AnnotationLoader annotationLoader;
    private JDTModuleSourceMapper moduleSourceMapper;
    
    public JDTModelLoader(final JDTModuleManager moduleManager, JDTModuleSourceMapper moduleSourceMapper, final Modules modules){
        this.moduleSourceMapper = moduleSourceMapper;
        initModuleManager(moduleManager);
        moduleManager.setModelLoader(this);
        this.modules = modules;
        javaProject = moduleManager.getJavaProject();
        if (javaProject != null) {
            compilerOptions = new CompilerOptions(javaProject.getOptions(true));
            compilerOptions.ignoreMethodBodies = true;
            compilerOptions.storeAnnotations = true;
            problemReporter = new ProblemReporter(
                    DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                    compilerOptions,
                    new DefaultProblemFactory());
        }
        this.timer = new Timer(false);
        internalCreate();
        if (javaProject != null) {
            modelLoaders.put(javaProject.getProject(), new WeakReference<JDTModelLoader>(this));
        }
        annotationLoader = new AnnotationLoader(this, typeFactory);
    }

    public JDTModuleManager getModuleManager() {
        return (JDTModuleManager) super.getModuleManager();
    }
    
    private void internalCreate() {
        this.typeFactory = new GlobalTypeFactory();
        this.typeParser = new TypeParser(this);
        this.timer = new Timer(false);
        createLookupEnvironment();
    }

    public void createLookupEnvironment() {
        if (javaProject == null) {
            return;
        }
        try {
            ModelLoaderTypeRequestor requestor = new ModelLoaderTypeRequestor();
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
            ModelLoaderTypeRequestor requestor = new ModelLoaderTypeRequestor();
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
    
    // TODO : remove when the bug in the AbstractModelLoader is corrected
    @Override
    public synchronized LazyPackage findOrCreatePackage(Module module, String pkgName) {
        LazyPackage pkg = super.findOrCreatePackage(module, pkgName);

        if (pkg.getModule() != null 
                && pkg.getModule().isJava()){
            pkg.setShared(true);
        }
        Module currentModule = pkg.getModule();
        if (currentModule.equals(modules.getDefaultModule()) && ! currentModule.equals(module)) {
            currentModule.getPackages().remove(pkg);
            pkg.setModule(null);
            if (module != null) {
                module.getPackages().add(pkg);
                pkg.setModule(module);
            }
        }
        return pkg;
    }

    @Override
    protected Module loadLanguageModuleAndPackage() {
        Module languageModule = getLanguageModule();
        if (getModuleManager().isLoadDependenciesFromModelLoaderFirst() && !isBootstrap) {
            findOrCreatePackage(languageModule, CEYLON_LANGUAGE);
        }
        return languageModule;
    }
    
    private String getToplevelQualifiedName(final String pkgName, String name) {
        if (name != null && ! JvmBackendUtil.isInitialLowerCase(name)) {
            name = Util.quoteIfJavaKeyword(name);
        }

        String className = pkgName.isEmpty() ? name : Util.quoteJavaKeywords(pkgName) + "." + name;
        return className;
    }
    
    private String getToplevelQualifiedName(String fullyQualifiedName) {
        String pkgName = "";
        String name = fullyQualifiedName;
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fullyQualifiedName.length()-1) {
            pkgName = fullyQualifiedName.substring(0, lastDot);
            name = fullyQualifiedName.substring(lastDot+1, fullyQualifiedName.length());
        }
        return getToplevelQualifiedName(pkgName, name);
    }

    @Override
    public boolean loadPackage(Module module, String packageName, boolean loadDeclarations) {
        synchronized (getLock()) {
            packageName = Util.quoteJavaKeywords(packageName);
            if(loadDeclarations && !loadedPackages.add(cacheKeyByModule(module, packageName))){
                return true;
            }
            
            if (module instanceof JDTModule) {
                JDTModule jdtModule = (JDTModule) module;
                List<IPackageFragmentRoot> roots = jdtModule.getPackageFragmentRoots();
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
                                && !sourceDeclarations.containsKey(getToplevelQualifiedName(type.getPackageFragment().getElementName(), typeFullyQualifiedName))
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
    

    public final class ModelLoaderTypeRequestor implements
            ITypeRequestor {
        private Parser basicParser;
        private LookupEnvironment lookupEnvironment;

        public void initialize(LookupEnvironment lookupEnvironment) {
            this.lookupEnvironment = lookupEnvironment;
        }
        
        @Override
        public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding,
                AccessRestriction accessRestriction) {
            // case of SearchableEnvironment of an IJavaProject is used
            ISourceType sourceType = sourceTypes[0];
            while (sourceType.getEnclosingType() != null)
                sourceType = sourceType.getEnclosingType();
            if (sourceType instanceof SourceTypeElementInfo) {
                // get source
                SourceTypeElementInfo elementInfo = (SourceTypeElementInfo) sourceType;
                IType type = elementInfo.getHandle();
                ICompilationUnit sourceUnit = (ICompilationUnit) type.getCompilationUnit();
                accept(sourceUnit, accessRestriction);
            } else {
                CompilationResult result = new CompilationResult(sourceType.getFileName(), 1, 1, 0);
                CompilationUnitDeclaration unit =
                    SourceTypeConverter.buildCompilationUnit(
                        sourceTypes,
                        SourceTypeConverter.FIELD_AND_METHOD // need field and methods
                        | SourceTypeConverter.MEMBER_TYPE, // need member types
                        // no need for field initialization
                        lookupEnvironment.problemReporter,
                        result);
                lookupEnvironment.buildTypeBindings(unit, accessRestriction);
                lookupEnvironment.completeTypeBindings(unit, true);
            }
        }

        @Override
        public void accept(IBinaryType binaryType, PackageBinding packageBinding,
                AccessRestriction accessRestriction) {
            BinaryTypeBinding btb = lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);

            if (btb.isNestedType() && !btb.isStatic()) {
                for (MethodBinding method : btb.methods()) {
                    if (method.isConstructor() && method.parameters.length > 0) {
                        char[] signature = method.signature();
                        for (IBinaryMethod methodInfo : binaryType.getMethods()) {
                            if (methodInfo.isConstructor()) {
                                char[] methodInfoSignature = methodInfo.getMethodDescriptor();
                                if (new String(signature).equals(new String(methodInfoSignature))) {
                                    IBinaryAnnotation[] binaryAnnotation = methodInfo.getParameterAnnotations(0);
                                    if (binaryAnnotation == null) {
                                        if (methodInfo.getAnnotatedParametersCount() == method.parameters.length + 1) {
                                            AnnotationBinding[][] newParameterAnnotations = new AnnotationBinding[method.parameters.length][];
                                            for (int i=0; i<method.parameters.length; i++) {
                                                IBinaryAnnotation[] goodAnnotations = null;
                                                try {
                                                     goodAnnotations = methodInfo.getParameterAnnotations(i + 1);
                                                }
                                                catch(IndexOutOfBoundsException e) {
                                                    break;
                                                }
                                                if (goodAnnotations != null) {
                                                    AnnotationBinding[] parameterAnnotations = BinaryTypeBinding.createAnnotations(goodAnnotations, lookupEnvironment, new char[][][] {});
                                                    newParameterAnnotations[i] = parameterAnnotations;
                                                }
                                            }
                                            method.setParameterAnnotations(newParameterAnnotations);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void accept(ICompilationUnit sourceUnit,
                AccessRestriction accessRestriction) {
            // Switch the current policy and compilation result for this unit to the requested one.
            CompilationResult unitResult = new CompilationResult(sourceUnit, 1, 1, compilerOptions.maxProblemsPerUnit);
            try {
                CompilationUnitDeclaration parsedUnit = basicParser().dietParse(sourceUnit, unitResult);
                lookupEnvironment.buildTypeBindings(parsedUnit, accessRestriction);
                lookupEnvironment.completeTypeBindings(parsedUnit, true);
            } catch (AbortCompilationUnit e) {
                // at this point, currentCompilationUnitResult may not be sourceUnit, but some other
                // one requested further along to resolve sourceUnit.
                if (unitResult.compilationUnit == sourceUnit) { // only report once
                    //requestor.acceptResult(unitResult.tagAsAccepted());
                } else {
                    throw e; // want to abort enclosing request to compile
                }
            }
            // Display unit error in debug mode
            if (BasicSearchEngine.VERBOSE) {
                if (unitResult.problemCount > 0) {
                    System.out.println(unitResult);
                }
            }
        }

        private Parser basicParser() {
            if (this.basicParser == null) {
                ProblemReporter problemReporter =
                    new ProblemReporter(
                        DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                        compilerOptions,
                        new DefaultProblemFactory());
                this.basicParser = new Parser(problemReporter, false);
                this.basicParser.reportOnlyOneSyntaxError = true;
            }
            return this.basicParser;
        }
    }

    public static class ModelLoaderNameEnvironment extends SearchableEnvironment {
        public ModelLoaderNameEnvironment(IJavaProject javaProject) throws JavaModelException {
            super((JavaProject)javaProject, (WorkingCopyOwner) null);
        }

        public IJavaProject getJavaProject() {
            return project;
        }
        
        public IType findTypeInNameLookup(char[][] compoundTypeName) {
            if (compoundTypeName == null) return null;

            int length = compoundTypeName.length;
            if (length <= 1) {
                if (length == 0) return null;
                return findTypeInNameLookup(new String(compoundTypeName[0]), IPackageFragment.DEFAULT_PACKAGE_NAME);
            }

            int lengthM1 = length - 1;
            char[][] packageName = new char[lengthM1][];
            System.arraycopy(compoundTypeName, 0, packageName, 0, lengthM1);

            return findTypeInNameLookup(
                new String(compoundTypeName[lengthM1]),
                CharOperation.toString(packageName));
        }
        
        private Method getProgressMonitorMethod = null;
        private IProgressMonitor getProgressMonitor(Job job) {
            if (job==null) {
                return new NullProgressMonitor();
            }
            try {
                if (getProgressMonitorMethod == null) {
                    for (Method m : InternalJob.class.getDeclaredMethods()) {
                        if ("getProgressMonitor".equals(m.getName())) {
                            getProgressMonitorMethod = m;
                            getProgressMonitorMethod.setAccessible(true);
                            break;
                        }
                    }
                }
                
                Object o = getProgressMonitorMethod.invoke(job);
                if (o instanceof IProgressMonitor) {
                    return (IProgressMonitor) o;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

        
        
        private boolean endsWith(char [] charArray, char[][] suffixes) {
            int arrayLength = charArray.length;
            for (char[] suffix : suffixes) {
                int suffixLength = suffix.length;
                if (arrayLength >= suffixLength) {
                    if (CharOperation.fragmentEquals(suffix, charArray, arrayLength - suffixLength, false)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        public IType findTypeInNameLookup(String typeName, String packageName) {
            JavaElementRequestor packageRequestor = new JavaElementRequestor();
            nameLookup.seekPackageFragments(packageName, false, packageRequestor);
            LinkedList<IPackageFragment> packagesToSearchIn = new LinkedList<>();
            
            for (IPackageFragment pf : packageRequestor.getPackageFragments()) {
                IPackageFragmentRoot packageRoot = (IPackageFragmentRoot) pf.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
                try {
                    IJavaProject packageProject = packageRoot.getJavaProject();
                    if (packageProject != null 
                            && !CeylonNature.isEnabled(packageProject.getProject())) {
                        continue;
                    }
                    
                    if (packageRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
                        packagesToSearchIn.addFirst(pf);
                        continue;
                    }
                    if (isInCeylonClassesOutputFolder(packageRoot.getPath())) {
                        continue;
                    }
                    packagesToSearchIn.addLast(pf);
                } catch (JavaModelException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            for (IPackageFragment pf : packagesToSearchIn) {

                // We use considerSecondTypes = false because we will do it explicitly afterwards, in order to use waitForIndexes=true
                IType type = nameLookup.findType(typeName, pf, false, NameLookup.ACCEPT_ALL);
                if (type != null) {
                    return type;
                }
            }
            
            char[] typeNameCharArray = typeName.toCharArray();
            if (CharOperation.equals(TypeConstants.PACKAGE_INFO_NAME, typeNameCharArray) ||
                    CharOperation.equals(packageDescriptorName, typeNameCharArray) ||
                    CharOperation.equals(moduleDescriptorName, typeNameCharArray) ||
                    CharOperation.equals(oldPackageDescriptorName, typeNameCharArray) ||
                    CharOperation.equals(oldModuleDescriptorName, typeNameCharArray) ||
                    endsWith(typeNameCharArray, descriptorClassNames)) {
                // Don't search for secondary types whose name ends with is a quoted of unquoted descriptors
                // or ends with a quoted descriptor (in case it would be searching for an inner class)
                return null;
            }
            
            if (isSettingInterfaceCompanionClass() && typeName.endsWith("$impl")) {
                // Don't search for Ceylon interface companion classes in Java Secondary types.
                return null;
            }
            
            Job currentJob = Job.getJobManager().currentJob();
            IProgressMonitor currentMonitor = getProgressMonitor(currentJob);
            for (IPackageFragment pf : packagesToSearchIn) {
                IType type = findSecondaryType(typeName, packageName, pf,
                        currentMonitor);
                if (type != null) {
                    return type;
                }
            }
            return null;
        }

        // This is a Copy / Paste from :
        // org.eclipse.jdt.internal.core.NameLookup.findSecondaryType(...), in order to be able to call it with waitForIndexes = true:
        // type = nameLookup.findSecondaryType(pf.getElementName(), typeName, pf.getJavaProject(), true, null);
        //
        // However the copied method has been changed to adapt it to the model loader needs.
        private IType findSecondaryType(String typeName, String packageName,
                IPackageFragment pf, IProgressMonitor currentMonitor) {
            JavaModelManager manager = JavaModelManager.getJavaModelManager();
            try {
                IJavaProject javaProject = pf.getJavaProject();
                @SuppressWarnings("rawtypes")
                Map secondaryTypePaths = manager.secondaryTypes(javaProject, true, currentMonitor);
                if (secondaryTypePaths.size() > 0) {
                    @SuppressWarnings("rawtypes")
                    Map types = (Map) secondaryTypePaths.get(packageName==null?"":packageName); //$NON-NLS-1$
                    if (types != null && types.size() > 0) {
                        boolean startsWithDollar = false;
                        if(typeName.startsWith("$")) {
                            startsWithDollar = true;
                            typeName = typeName.substring(1);
                        }
                        String[] parts = typeName.split("(\\.|\\$)");
                        if (startsWithDollar) {
                            parts[0] = "$" + parts[0];
                        }
                        int index = 0;
                        String topLevelClassName = parts[index++];
                        IType currentClass = (IType) types.get(topLevelClassName);
                        IType result = currentClass;
                        while (index < parts.length) {
                            result = null;
                            String nestedClassName = parts[index++];
                            if (currentClass != null && currentClass.exists()) {
                                currentClass = currentClass.getType(nestedClassName);
                                result = currentClass;
                            } else {
                                break;
                            }
                        }
                        return result;
                    }
                }
            }
            catch (JavaModelException jme) {
                // give up
            }
            return null;
        }
        
        @Override
        protected NameEnvironmentAnswer find(String typeName, String packageName) {
            if (packageName == null)
                packageName = IPackageFragment.DEFAULT_PACKAGE_NAME;
            if (this.owner != null) {
                String source = this.owner.findSource(typeName, packageName);
                if (source != null) {
                    ICompilationUnit cu = new BasicCompilationUnit(source.toCharArray(), 
                            CharOperation.splitOn('.', packageName.toCharArray()), 
                            typeName + org.eclipse.jdt.internal.core.util.Util.defaultJavaExtension());
                    return new NameEnvironmentAnswer(cu, null);
                }
            }

            IType type = findTypeInNameLookup(typeName, packageName);
            
            if (type != null) {
                // construct name env answer
                if (type instanceof BinaryType) { // BinaryType
                    try {
                        return new NameEnvironmentAnswer((IBinaryType) ((BinaryType) type).getElementInfo(), null);
                    } catch (JavaModelException npe) {
                        // fall back to using owner
                    }
                } else { //SourceType
                    try {
                        // retrieve the requested type
                        SourceTypeElementInfo sourceType = (SourceTypeElementInfo)((SourceType) type).getElementInfo();
                        ISourceType topLevelType = sourceType;
                        while (topLevelType.getEnclosingType() != null) {
                            topLevelType = topLevelType.getEnclosingType();
                        }
                        // find all siblings (other types declared in same unit, since may be used for name resolution)
                        IType[] types = sourceType.getHandle().getCompilationUnit().getTypes();
                        ISourceType[] sourceTypes = new ISourceType[types.length];

                        // in the resulting collection, ensure the requested type is the first one
                        sourceTypes[0] = sourceType;
                        int length = types.length;
                        for (int i = 0, index = 1; i < length; i++) {
                            ISourceType otherType =
                                (ISourceType) ((JavaElement) types[i]).getElementInfo();
                            if (!otherType.equals(topLevelType) && index < length) // check that the index is in bounds (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=62861)
                                sourceTypes[index++] = otherType;
                        }
                        return new NameEnvironmentAnswer(sourceTypes, null);
                    } catch (JavaModelException jme) {
                        if (jme.isDoesNotExist() && String.valueOf(TypeConstants.PACKAGE_INFO_NAME).equals(typeName)) {
                            // in case of package-info.java the type doesn't exist in the model,
                            // but the CU may still help in order to fetch package level annotations.
                            return new NameEnvironmentAnswer((ICompilationUnit)type.getParent(), null);
                        }
                        // no usable answer
                    }
                }
            }
            return null;
        }
    }
    
    private static final ThreadLocal<Object> isSettingInterfaceCompanionClassTL = new ThreadLocal<>();
    private static final Object isSettingInterfaceCompanionClassObj = new Object();
    
    private static boolean isSettingInterfaceCompanionClass() {
        return isSettingInterfaceCompanionClassTL.get() != null;
    }
    
    @Override
    protected void setInterfaceCompanionClass(Declaration d, ClassOrInterface container, LazyPackage pkg) {
        isSettingInterfaceCompanionClassTL.set(isSettingInterfaceCompanionClassObj);
        super.setInterfaceCompanionClass(d, container, pkg);
        isSettingInterfaceCompanionClassTL.set(null);
    }
    
    private INameEnvironment createSearchableEnvironment() throws JavaModelException {
        return new ModelLoaderNameEnvironment(javaProject);
    }
    
    synchronized private LookupEnvironment getLookupEnvironment() {
        if (mustResetLookupEnvironment) {
            synchronized (lookupEnvironment) {
                createLookupEnvironment();
            }
            mustResetLookupEnvironment = false;
        }
        return lookupEnvironment;
    }
    
    @Override
    public boolean searchAgain(ClassMirror cachedMirror, Module module, String name) {
        if (cachedMirror != null 
                && ( !(cachedMirror instanceof SourceClass) || 
                        !forceLoadFromBinaries(cachedMirror))) {
            return false;
        }
        if (module instanceof JDTModule) {
            JDTModule jdtModule = (JDTModule) module;
            if (jdtModule.isCeylonBinaryArchive() || jdtModule.isJavaBinaryArchive()) {
                String classRelativePath = name.replace('.', '/');
                return jdtModule.containsClass(classRelativePath + ".class") || jdtModule.containsClass(classRelativePath + "_.class");
            } else if (jdtModule.isProjectModule()) {
                int nameLength = name.length();
                int packageEnd = name.lastIndexOf('.');
                int classNameStart = packageEnd + 1;
                String packageName = packageEnd > 0 ? name.substring(0, packageEnd) : "";
                String className = classNameStart < nameLength ? name.substring(classNameStart) : "";
                boolean moduleContainsJava = false;
                for (IPackageFragmentRoot root : jdtModule.getPackageFragmentRoots()) {
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
        }
        return false;
    }
    
    @Override
    public boolean searchAgain(Declaration cachedDeclaration, LazyPackage lazyPackage, String name) {
        if (cachedDeclaration != null && 
                (cachedDeclaration instanceof LazyElement || 
                        !forceLoadFromBinaries(cachedDeclaration))) {
            return false;
        }
        return searchAgain(null, lazyPackage.getModule(), lazyPackage.getQualifiedName(lazyPackage.getQualifiedNameString(), name));
    }

    private boolean forceLoadFromBinaries(boolean isNativeDeclaration) {
        return getModuleManager().isLoadDependenciesFromModelLoaderFirst() 
                    && isNativeDeclaration;
    }

    private boolean forceLoadFromBinaries(Tree.Declaration declarationNode) {
        return forceLoadFromBinaries(getNative(declarationNode) != null);
    }

    private boolean forceLoadFromBinaries(Declaration declaration) {
        return forceLoadFromBinaries(declaration.isNative());
    }

    private boolean forceLoadFromBinaries(ClassMirror classMirror) {
        return forceLoadFromBinaries(getNative(classMirror) != null);
    }
    
    @Override
    public ClassMirror lookupNewClassMirror(Module module, String name) {
        synchronized(getLock()){
            String topLevelPartiallyQuotedName = getToplevelQualifiedName(name);
            SourceDeclarationHolder foundSourceDeclaration = sourceDeclarations.get(topLevelPartiallyQuotedName);
            if (foundSourceDeclaration != null
                    && !forceLoadFromBinaries(
                            foundSourceDeclaration.getAstDeclaration())) {
                return new SourceClass(foundSourceDeclaration);
            }
            
            ClassMirror classMirror = buildClassMirror(JVMModuleUtil.quoteJavaKeywords(name));
            if (classMirror == null 
                    && lastPartHasLowerInitial(name)
                    && !name.endsWith("_")) {
                // We have to try the unmunged name first, so that we find the symbol
                // from the source in preference to the symbol from any 
                // pre-existing .class file
                classMirror = buildClassMirror(JVMModuleUtil.quoteJavaKeywords(name + "_"));
            }
            
            if(classMirror == null) {
                if (foundSourceDeclaration != null) {
                    return new SourceClass(foundSourceDeclaration);
                }

                return null;
            }
            
            Module classMirrorModule = findModuleForClassMirror(classMirror);
            if(classMirrorModule == null){
                logVerbose("Found a class mirror with no module");
                return null;
            }
            // make sure it's imported
            if(isImported(module, classMirrorModule)){
                return classMirror;
            }
            logVerbose("Found a class mirror that is not imported: "+name);
            return null;
        }
    }

    public MissingTypeBinding getMissingTypeBinding() {
        synchronized (getLock()) {
            return missingTypeBinding;
        }
    }
    
    public static interface ActionOnResolvedType {
        void doWithBinding(ReferenceBinding referenceBinding);
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
                            JDTModuleManager moduleManager = loader.getModuleManager();
                            if (moduleManager == null) {
                                continue;
                            }
                            IJavaProject javaProject = moduleManager.getJavaProject();
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
    private static final char[] packageDescriptorName = Naming.PACKAGE_DESCRIPTOR_CLASS_NAME.toCharArray();
    private static final char[] moduleDescriptorName = Naming.MODULE_DESCRIPTOR_CLASS_NAME.toCharArray();
    private static final char[] oldPackageDescriptorName = OLD_PACKAGE_DESCRIPTOR_CLASS_NAME.toCharArray();
    private static final char[] oldModuleDescriptorName = Naming.OLD_MODULE_DESCRIPTOR_CLASS_NAME.toCharArray();
    private static final char[][] descriptorClassNames = new char[][] { packageDescriptorName, moduleDescriptorName };

    private JDTClass buildClassMirror(String name) {
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

    
    private ModelLoaderNameEnvironment getNameEnvironment() {
        return (ModelLoaderNameEnvironment)getLookupEnvironment().nameEnvironment;
    }
    
    @Override
    public Declaration convertToDeclaration(Module module, String typeName,
            DeclarationType declarationType) {
        synchronized (getLock()) {
            String fqn = getToplevelQualifiedName(typeName);

            SourceDeclarationHolder foundSourceDeclaration = sourceDeclarations.get(fqn);
            if (foundSourceDeclaration != null
                    && ! forceLoadFromBinaries(
                            foundSourceDeclaration.getAstDeclaration())) {
                return foundSourceDeclaration.getModelDeclaration();
            }

            Declaration result = null;
            try {
                result = super.convertToDeclaration(module, typeName, declarationType);
            } catch(RuntimeException e) {
                // FIXME: pretty sure this is plain wrong as it ignores problems and especially ModelResolutionException and just plain hides them
            }
            if (result == null && foundSourceDeclaration != null) {
                result = foundSourceDeclaration.getModelDeclaration();
            }
            return result;
        }
    }

    @Override
    public void addModuleToClassPath(Module module, ArtifactResult artifact) {
        if(artifact != null && module instanceof LazyModule)
            ((LazyModule)module).loadPackageList(artifact);
                    
        if (module instanceof JDTModule) {
            JDTModule jdtModule = (JDTModule) module;
            if (! jdtModule.equals(getLanguageModule()) && (jdtModule.isCeylonBinaryArchive() || jdtModule.isJavaBinaryArchive())) {
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
            }
        }
        modulesInClassPath.add(module);
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
    protected Unit getCompiledUnit(LazyPackage pkg, ClassMirror classMirror) {
        Unit unit = null;
        if (classMirror != null && classMirror instanceof JDTClass) {
            JDTClass jdtClass = (JDTClass) classMirror;
            String unitName = jdtClass.getFileName();
            
            if (!jdtClass.isBinary()) {
                // This search is for source Java classes since several classes might have the same file name 
                //  and live inside the same Java source file => into the same Unit
                for (Unit unitToTest : pkg.getUnits()) {
                    if (unitToTest.getFilename().equals(unitName)) {
                        return unitToTest;
                    }
                }
            }
    
            unit = newCompiledUnit(pkg, jdtClass);
        }
        
        if (unit == null) {
            String key = getPackageCacheKey(pkg);
            unit = unitsByPackage.get(key);
            if(unit == null){
                unit = new PackageTypeFactory(pkg);
                unit.setPackage(pkg);
                unitsByPackage.put(key, unit);
            }
        }
        return unit;
    }

    public void setModuleAndPackageUnits() {
        for (Module module : getModuleManager().getModules().getListOfModules()) {
            if (module instanceof JDTModule) {
                JDTModule jdtModule = (JDTModule) module;
                if (jdtModule.isCeylonBinaryArchive()) {
                    for (Package p : jdtModule.getPackages()) {
                        if (p.getUnit() == null) {
                            ClassMirror packageClassMirror = lookupClassMirror(jdtModule, p.getQualifiedNameString() + "." + Naming.PACKAGE_DESCRIPTOR_CLASS_NAME);
                            if (packageClassMirror ==  null) {
                                packageClassMirror = lookupClassMirror(jdtModule, p.getQualifiedNameString() + "." + Naming.PACKAGE_DESCRIPTOR_CLASS_NAME.substring(1));
                            }
                            // some modules do not declare their main package, because they don't have any declaration to share
                            // there, for example, so this can be null
                            if(packageClassMirror != null)
                                p.setUnit(newCompiledUnit((LazyPackage) p, packageClassMirror));
                        }
                        if (p.getNameAsString().equals(jdtModule.getNameAsString())) {
                            if (jdtModule.getUnit() == null) {
                                ClassMirror moduleClassMirror = lookupClassMirror(jdtModule, p.getQualifiedNameString() + "." + Naming.MODULE_DESCRIPTOR_CLASS_NAME);
                                if (moduleClassMirror ==  null) {
                                    moduleClassMirror = lookupClassMirror(jdtModule, p.getQualifiedNameString() + "." + Naming.OLD_MODULE_DESCRIPTOR_CLASS_NAME);
                                }
                                if (moduleClassMirror != null) {
                                    jdtModule.setUnit(newCompiledUnit((LazyPackage) p, moduleClassMirror));
                                }
                            }
                        }
                    }
                }
            }
        }
        
    }
    
    private Unit newCompiledUnit(LazyPackage pkg, ClassMirror classMirror) {
        Unit unit;
        JDTClass jdtClass = (JDTClass) classMirror;
        IType type = jdtClass.getType();
        if (type == null) {
            return null;
        }
        
        ITypeRoot typeRoot = type.getTypeRoot();
        StringBuilder sb = new StringBuilder();
        List<String> parts = pkg.getName();
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);
            if (! part.isEmpty()) {
                sb.append(Util.quoteIfJavaKeyword(part));
                sb.append('/');
            }
        }
        sb.append(jdtClass.getFileName());
        String relativePath = sb.toString();
        String fileName = jdtClass.getFileName();
        String fullPath = jdtClass.getFullPath();
        
        if (!jdtClass.isBinary()) {
            unit = new JavaCompilationUnit((org.eclipse.jdt.core.ICompilationUnit)typeRoot, fileName, relativePath, fullPath, pkg);
        }
        else {
            if (jdtClass.isCeylon()) {
                if (pkg.getModule() instanceof JDTModule) {
                    JDTModule module = (JDTModule) pkg.getModule();
                    IProject originalProject = module.getOriginalProject();
                    if (originalProject != null) {
                        unit = new CrossProjectBinaryUnit((IClassFile)typeRoot, fileName, relativePath, fullPath, pkg);
                    } else {
                        unit = new CeylonBinaryUnit((IClassFile)typeRoot, fileName, relativePath, fullPath, pkg);
                    }
                } else {
                    unit = new CeylonBinaryUnit((IClassFile)typeRoot, fileName, relativePath, fullPath, pkg);
                }
            }
            else {
                unit = new JavaClassFile((IClassFile)typeRoot, fileName, relativePath, fullPath, pkg);
            }
        }

        return unit;
    }

    @Override
    protected void logError(String message) {
        //System.err.println("ERROR: "+message);
    }

    @Override
    protected void logWarning(String message) {
        //System.err.println("WARNING: "+message);
    }

    @Override
    protected void logVerbose(String message) {
        //System.err.println("NOTE: "+message);
    }
    
    @Override
    public ClassMirror[] getClassMirrorsToRemove(com.redhat.ceylon.model.typechecker.model.Declaration declaration) {
        ClassMirror[] mirrors = super.getClassMirrorsToRemove(declaration);
        if (mirrors != null && mirrors.length == 0) {
            Unit unit = declaration.getUnit();
            if (unit instanceof SourceFile) {
                String fqn = getToplevelQualifiedName(unit.getPackage().getNameAsString(), declaration.getNameAsString());
                SourceDeclarationHolder holder = sourceDeclarations.get(fqn);
                if (holder != null) {
                    return new ClassMirror[] { new SourceClass(holder) };
                }
            }
        }
        return mirrors;
    }
    
    @Override
    public void removeDeclarations(List<Declaration> declarations) {
        synchronized(getLock()){
            List<Declaration> allDeclarations = new ArrayList<Declaration>(declarations.size());
            Set<Package> changedPackages = new HashSet<Package>();
            
            allDeclarations.addAll(declarations);

            for (Declaration declaration : declarations) {
                Unit unit = declaration.getUnit();
                if (unit != null) {
                    changedPackages.add(unit.getPackage());
                }
                retrieveInnerDeclarations(declaration, allDeclarations);
            }
            
            for (Declaration decl : allDeclarations) {
                String fqn = getToplevelQualifiedName(decl.getContainer().getQualifiedNameString(), decl.getName());
                sourceDeclarations.remove(fqn);
            }
            
            super.removeDeclarations(allDeclarations);
            for (Package changedPackage : changedPackages) {
                loadedPackages.remove(cacheKeyByModule(changedPackage.getModule(), changedPackage.getNameAsString()));
            }
            mustResetLookupEnvironment = true;
        }
    }

    private void retrieveInnerDeclarations(Declaration declaration,
            List<Declaration> allDeclarations) {
    	List<Declaration> members;
    	try {
            members = declaration.getMembers();
    	} catch(Exception e) {
    		members = Collections.emptyList();
    	}
        allDeclarations.addAll(members);
        for (Declaration member : members) {
            retrieveInnerDeclarations(member, allDeclarations);
        }
    }
    
    private final Map<String, SourceDeclarationHolder> sourceDeclarations = new TreeMap<String, SourceDeclarationHolder>();
    
    public synchronized Set<String> getSourceDeclarations() {
        Set<String> declarations  = new HashSet<String>();
        declarations.addAll(sourceDeclarations.keySet());
        return declarations;
    }
    
    public synchronized SourceDeclarationHolder getSourceDeclaration(String declarationName) {
        return sourceDeclarations.get(declarationName);
    }

    public class PackageTypeFactory extends TypeFactory {
        public PackageTypeFactory(Package pkg) {
            super(moduleSourceMapper.getContext());
            assert (pkg != null);
            setPackage(pkg);
        }
    }

    
    public class GlobalTypeFactory extends TypeFactory {
        public GlobalTypeFactory() {
            super(moduleSourceMapper.getContext());
        }

        @Override
        public Package getPackage() {
            synchronized (JDTModelLoader.this) {
                if(super.getPackage() == null){
                    super.setPackage(modules.getLanguageModule()
                            .getDirectPackage(Module.LANGUAGE_MODULE_NAME));
                }
                return super.getPackage();
            }
        }
    }

    public static interface SourceFileObjectManager {
        void setupSourceFileObjects(List<?> treeHolders);
    }
    
    public void setupSourceFileObjects(List<?> treeHolders) {
        synchronized (getLock()) {
            addSourcePhasedUnits(treeHolders, true);
        }
    }

    public void addSourcePhasedUnits(List<?> treeHolders, final boolean isSourceToCompile) {
        synchronized (getLock()) {
            for (Object treeHolder : treeHolders) {
                if (treeHolder instanceof PhasedUnit) {
                    final PhasedUnit unit = (PhasedUnit) treeHolder;
                    final String pkgName = unit.getPackage().getQualifiedNameString();
                    unit.getCompilationUnit().visit(new SourceDeclarationVisitor(){
                        @Override
                        public void loadFromSource(Tree.Declaration decl) {
                            if (decl.getIdentifier()!=null) {
                                String fqn = getToplevelQualifiedName(pkgName, decl.getIdentifier().getText());
                                if (! sourceDeclarations.containsKey(fqn)) {
                                    sourceDeclarations.put(fqn, new SourceDeclarationHolder(unit, decl, isSourceToCompile));
                                }
                            }
                        }
                        @Override
                        public void loadFromSource(ModuleDescriptor that) {
                        }
    
                        @Override
                        public void loadFromSource(PackageDescriptor that) {
                        }
                    });
                }
            }
        }
    }

    public void addSourceArchivePhasedUnits(List<PhasedUnit> sourceArchivePhasedUnits) {
        addSourcePhasedUnits(sourceArchivePhasedUnits, false);
    }
    
    public void clearCachesOnPackage(String packageName) {
        synchronized (getLock()) {
            List<String> keysToRemove = new ArrayList<String>(classMirrorCache.size());
            for (Entry<String, ClassMirror> element : classMirrorCache.entrySet()) {
                if (element.getValue() == null) {
                    String className = element.getKey();
                    if (className != null) {
                        String classPackageName =className.replaceAll("\\.[^\\.]+$", "");
                        if (classPackageName.equals(packageName)) {
                            keysToRemove.add(className);
                        }
                    }
                }
            }
            for (String keyToRemove : keysToRemove) {
                classMirrorCache.remove(keyToRemove);
            }
            Package pkg = findPackage(packageName);
            loadedPackages.remove(cacheKeyByModule(pkg.getModule(), packageName));
            mustResetLookupEnvironment = true;
        }
    }

    public void clearClassMirrorCacheForClass(JDTModule module, String classNameToRemove) {
        synchronized (getLock()) {
            classMirrorCache.remove(cacheKeyByModule(module, classNameToRemove));        
            mustResetLookupEnvironment = true;
        }
    }

    @Override
    protected LazyValue makeToplevelAttribute(ClassMirror classMirror, boolean isNativeHeader) {
        if (classMirror instanceof SourceClass) {
            return (LazyValue) (((SourceClass) classMirror).getModelDeclaration());
        }
        return super.makeToplevelAttribute(classMirror, isNativeHeader);
    }

    @Override
    protected LazyFunction makeToplevelMethod(ClassMirror classMirror, boolean isNativeHeader) {
        if (classMirror instanceof SourceClass) {
            return (LazyFunction) (((SourceClass) classMirror).getModelDeclaration());
        }
        return super.makeToplevelMethod(classMirror, isNativeHeader);
    }

    @Override
    protected LazyClass makeLazyClass(ClassMirror classMirror, Class superClass,
            MethodMirror constructor, boolean isNativeHeader) {
        if (classMirror instanceof SourceClass) {
            return (LazyClass) (((SourceClass) classMirror).getModelDeclaration());
        }
        return super.makeLazyClass(classMirror, superClass, constructor, isNativeHeader);
    }

    @Override
    protected LazyInterface makeLazyInterface(ClassMirror classMirror, boolean isNativeHeader) {
        if (classMirror instanceof SourceClass) {
            return (LazyInterface) ((SourceClass) classMirror).getModelDeclaration();
        }
        return super.makeLazyInterface(classMirror, isNativeHeader);
    }
    
    public TypeFactory getTypeFactory() {
        return (TypeFactory) typeFactory;
    }
    
    @Override
    public Module findModuleForClassMirror(ClassMirror classMirror) {
        String pkgName = getPackageNameForQualifiedClassName(classMirror);
        return lookupModuleByPackageName(pkgName);
    }
    
    public void loadJDKModules() {
        super.loadJDKModules();
    }

    @Override
    public LazyPackage findOrCreateModulelessPackage(String pkgName) {
        synchronized(getLock()){
            return (LazyPackage) findPackage(pkgName);
        }
    }

    @Override
    public boolean isModuleInClassPath(Module module) {
        return modulesInClassPath.contains(module)
                || ((module instanceof JDTModule) && 
                        ((JDTModule) module).isProjectModule())
                || ((module instanceof JDTModule) && 
                        ((JDTModule) module).getOriginalModule() != null && 
                                ((JDTModule) module).getOriginalModule().isProjectModule()) ;
    }
    
    @Override
    protected boolean needsLocalDeclarations() {
        return false;
    }

    void addJDKModuleToClassPath(Module module) {
        modulesInClassPath.add(module);
    }

    @Override
    protected boolean isAutoExportMavenDependencies() {
        if (javaProject != null) {
            CeylonProject<IProject> ceylonProject = modelJ2C.ceylonModel().getProject(javaProject.getProject());
            if (ceylonProject != null) {
                return ceylonProject.getConfiguration().getAutoExportMavenDependencies();
            }
        }
        
        return false;
    }

    @Override
    protected boolean isFlatClasspath() {
        if (javaProject != null) {
            CeylonProject<IProject> ceylonProject = modelJ2C.ceylonModel().getProject(javaProject.getProject());
            if (ceylonProject != null) {
                return ceylonProject.getConfiguration().getFlatClasspath();
            }
        }
        return false;
    }

    @Override
    protected void makeInteropAnnotationConstructorInvocation(AnnotationProxyMethod arg0, AnnotationProxyClass arg1, List<Parameter> arg2) {
        annotationLoader.makeInterorAnnotationConstructorInvocation(arg0, arg1, arg2);
    }

    @Override
    protected ErrorReporter makeModelErrorReporter(Module arg0, String arg1) {
        return new ModuleErrorAttacherRunnable(moduleSourceMapper, arg0, arg1);
    }

    public static class ModuleErrorAttacherRunnable extends UnknownType.ErrorReporter {

        private Module module;
        private ModuleSourceMapper moduleSourceMapper;

        public ModuleErrorAttacherRunnable(ModuleSourceMapper moduleSourceMapper, Module module, String message) {
            super(message);
            this.moduleSourceMapper = moduleSourceMapper;
            this.module = module;
        }

        @Override
        public void reportError() {
            moduleSourceMapper.attachErrorToOriginalModuleImport(module, getMessage());
        }
    }

    @Override
    protected void setAnnotationConstructor(LazyFunction arg0, MethodMirror arg1) {
        annotationLoader.setAnnotationConstructor(arg0, arg1);
    }

    protected String getNative(ClassMirror classMirror) {
        if (classMirror instanceof SourceClass) {
            return getNative(((SourceClass)classMirror).getAstDeclaration());
        }
        
        AnnotationMirror annotation = classMirror.getAnnotation("ceylon.language.NativeAnnotation$annotation$");
        if (annotation == null) {
            return null;
        }
        Object backend = annotation.getValue("backend");
        if (backend == null) {
            return "";
        }
        if (backend instanceof String) {
            return (String) backend;
        }
        return null;
    }
    
    protected String getNative(Tree.Declaration decl) {
        for (Tree.Annotation annotation : decl.getAnnotationList().getAnnotations()) {
            String text = annotation.getPrimary().getToken().getText();
            if (text != null && text.equals("native")) {
                String backend = "";
                Tree.PositionalArgumentList pal = annotation.getPositionalArgumentList();
                if (pal != null) {
                    List<PositionalArgument> pas = pal.getPositionalArguments();
                    if (pas != null && !pas.isEmpty()) {
                        PositionalArgument backendArg = pas.get(0);
                        String argText = backendArg.getEndToken().getText();
                        if (argText.startsWith("\"")) {
                            argText = argText.substring(1);
                        }
                        if (argText.endsWith("\"")) {
                            argText = argText.substring(0, argText.length()-1);
                        }
                        backend = argText;
                    }
                }
                return backend;
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
    
}
