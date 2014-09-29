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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
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
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.internal.core.SourceTypeElementInfo;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;

import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.loader.TypeFactory;
import com.redhat.ceylon.compiler.java.util.Timer;
import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.SourceDeclarationVisitor;
import com.redhat.ceylon.compiler.loader.TypeParser;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.mirror.MethodMirror;
import com.redhat.ceylon.compiler.loader.model.LazyClass;
import com.redhat.ceylon.compiler.loader.model.LazyInterface;
import com.redhat.ceylon.compiler.loader.model.LazyMethod;
import com.redhat.ceylon.compiler.loader.model.LazyModule;
import com.redhat.ceylon.compiler.loader.model.LazyPackage;
import com.redhat.ceylon.compiler.loader.model.LazyValue;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ModuleDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PackageDescriptor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;
import com.redhat.ceylon.eclipse.core.model.mirror.JDTClass;
import com.redhat.ceylon.eclipse.core.model.mirror.JDTMethod;
import com.redhat.ceylon.eclipse.core.model.mirror.SourceClass;
import com.redhat.ceylon.eclipse.core.model.mirror.SourceDeclarationHolder;

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
    private boolean mustResetLookupEnvironment = false;
    private Set<Module> modulesInClassPath = new HashSet<Module>();
    
    public JDTModelLoader(final JDTModuleManager moduleManager, final Modules modules){
        this.moduleManager = moduleManager;
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
    }

    public JDTModuleManager getModuleManager() {
        return (JDTModuleManager) moduleManager;
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
            lookupEnvironment = new LookupEnvironment(new ITypeRequestor() {
                
                private Parser basicParser;

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
            }, compilerOptions, problemReporter, createSearchableEnvironment());
            lookupEnvironment.mayTolerateMissingType = true;
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
    public void loadStandardModules() {
        // Now create JDK and Oracle modules (cf. https://github.com/ceylon/ceylon-ide-eclipse/issues/733 )
        loadJDKModules();
        /*
         * We start by loading java.lang because we will need it no matter what.
         */
        Module jdkModule = findOrCreateModule(JAVA_BASE_MODULE_NAME, JDKUtils.jdk.version);
        Module languageModule = getLanguageModule();
        if (getModuleManager().isLoadDependenciesFromModelLoaderFirst() && !isBootstrap) {
            findOrCreatePackage(languageModule, CEYLON_LANGUAGE);
        }        
        
        loadPackage(jdkModule, "java.lang", false);
        loadPackage(languageModule, "com.redhat.ceylon.compiler.java.metadata", false);
        loadPackage(languageModule, "com.redhat.ceylon.compiler.java.language", false);
        
    }
    
    private String getToplevelQualifiedName(final String pkgName, String name) {
        if (! Util.isInitialLowerCase(name)) {
            name = Util.quoteIfJavaKeyword(name);
        }

        String className = pkgName.isEmpty() ? name : Util.quoteJavaKeywords(pkgName) + "." + name;
        return className;
    }
    
    @Override
    public synchronized boolean loadPackage(Module module, String packageName, boolean loadDeclarations) {
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
                    // only top-levels are added in source declarations
                    if (typeFullyQualifiedName.indexOf('$') > 0) {
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

    synchronized public void refreshNameEnvironment() {
        try {
            lookupEnvironment.nameEnvironment = createSearchableEnvironment();
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }            
    }
    

    private class ModelLoaderNameEnvironment extends SearchableEnvironment {
        public ModelLoaderNameEnvironment() throws JavaModelException {
            super((JavaProject)javaProject, (WorkingCopyOwner) null);
        }

        public IType findTypeInNameLookup(String typeName, String packageName) {
            JavaElementRequestor packageRequestor = new JavaElementRequestor();
            nameLookup.seekPackageFragments(packageName, false, packageRequestor);
            LinkedList<IPackageFragment> packagesToSearchIn = new LinkedList<>();
            
            for (IPackageFragment pf : packageRequestor.getPackageFragments()) {
                IPackageFragmentRoot packageRoot = (IPackageFragmentRoot) pf.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
                try {
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
            
            IType type = null;
            for (IPackageFragment pf : packagesToSearchIn) {

                // We use considerSecondTypes = false because we will do it explicitly afterwards, in order to use waitForIndexes=true
            	// TODO : when migrating to Luna only (removing Kepler support), we will be able to simply call :
            	//        nameLookup.findType(typeName, pf, false, NameLookup.ACCEPT_ALL, 
            	//                            true /* waitForIndices */,
            	//                            true /* considerSecondaryTypes */)
            	// But unfortunately, Kepler doesn't provide the ability to set the 'waitForIndexes' parameter to true.
                type = nameLookup.findType(typeName, pf, false, NameLookup.ACCEPT_ALL);
                if (type == null) {
                    JavaModelManager manager = JavaModelManager.getJavaModelManager();
                    try {
                        // This is a Copy / Paste from :
                        // org.eclipse.jdt.internal.core.NameLookup.findSecondaryType(...), in order to be able to call it with waitForIndexes = true:
                        // type = nameLookup.findSecondaryType(pf.getElementName(), typeName, pf.getJavaProject(), true, null);
                        IJavaProject javaProject = pf.getJavaProject();
                        @SuppressWarnings("rawtypes")
                        Map secondaryTypePaths = manager.secondaryTypes(javaProject, true, null);
                        if (secondaryTypePaths.size() > 0) {
                            @SuppressWarnings("rawtypes")
                            Map types = (Map) secondaryTypePaths.get(packageName==null?"":packageName); //$NON-NLS-1$
                            if (types != null && types.size() > 0) {
                                type = (IType) types.get(typeName);
                            }
                        }
                    }
                    catch (JavaModelException jme) {
                        // give up
                    }
                    
                }
                if (type != null) {
                    break;
                }
            }
            return type;
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
    
    private INameEnvironment createSearchableEnvironment() throws JavaModelException {
        return new ModelLoaderNameEnvironment();
    }
    
    synchronized private LookupEnvironment getLookupEnvironment() {
        if (mustResetLookupEnvironment) {
            refreshNameEnvironment();
            lookupEnvironment.reset();
            mustResetLookupEnvironment = false;
        }
        return lookupEnvironment;
    }
    
    @Override
    public boolean searchAgain(Module module, String name) {
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
    public boolean searchAgain(LazyPackage lazyPackage, String name) {
        return searchAgain(lazyPackage.getModule(), lazyPackage.getQualifiedName(lazyPackage.getQualifiedNameString(), name));
    }

    
    @Override
    public synchronized ClassMirror lookupNewClassMirror(Module module, String name) {
        if (sourceDeclarations.containsKey(name)) {
            return new SourceClass(sourceDeclarations.get(name));
        }
        
        ClassMirror mirror = buildClassMirror(name);
        if (mirror == null && lastPartHasLowerInitial(name)) {
            // We have to try the unmunged name first, so that we find the symbol
            // from the source in preference to the symbol from any 
            // pre-existing .class file
            mirror = buildClassMirror(name+"_");
        }
        return mirror;
    }

    private ClassMirror buildClassMirror(String name) {
        if (javaProject == null) {
            return null;
        }
        
        try {
            LookupEnvironment theLookupEnvironment = getLookupEnvironment();
            char[][] uncertainCompoundName = CharOperation.splitOn('.', name.toCharArray());
            int numberOfParts = uncertainCompoundName.length;
            char[][] compoundName = null;
            IType type = null;
            
            for (int i=numberOfParts-1; i>0; i--) {
                char[][] triedPackageName = new char[0][];
                for (int j=0; j<i; j++) {
                    triedPackageName = CharOperation.arrayConcat(triedPackageName, uncertainCompoundName[j]);
                }
                char[] triedClassName = new char[0];
                for (int k=i; k<numberOfParts; k++) {
                    triedClassName = CharOperation.concat(triedClassName, uncertainCompoundName[k], '$');
                }
                
                ModelLoaderNameEnvironment nameEnvironment = getNameEnvironment();
                type = nameEnvironment.findTypeInNameLookup(CharOperation.charToString(triedClassName), 
                        CharOperation.toString(triedPackageName));
                if (type != null) {
                    compoundName = CharOperation.arrayConcat(triedPackageName, triedClassName);
                    break;
                }
            }

            if (type == null) {
                return null;
            }

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
                return new JDTClass(binaryTypeBinding, theLookupEnvironment, type);
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
                    return new JDTClass(referenceBinding, theLookupEnvironment, type);
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ModelLoaderNameEnvironment getNameEnvironment() {
        ModelLoaderNameEnvironment searchableEnvironment = (ModelLoaderNameEnvironment)getLookupEnvironment().nameEnvironment;
        return searchableEnvironment;
    }

    
    @Override
    public synchronized Declaration convertToDeclaration(Module module, String typeName,
            DeclarationType declarationType) {
        if (sourceDeclarations.containsKey(typeName)) {
            return sourceDeclarations.get(typeName).getModelDeclaration();
        }
        try {
            return super.convertToDeclaration(module, typeName, declarationType);
        } catch(RuntimeException e) {
            // FIXME: pretty sure this is plain wrong as it ignores problems and especially ModelResolutionException and just plain hides them
            return null;
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
            unit = unitsByPackage.get(pkg);
            if(unit == null){
                unit = new PackageTypeFactory(pkg);
                unit.setPackage(pkg);
                unitsByPackage.put(pkg, unit);
            }
        }
        return unit;
    }

    public void setModuleAndPackageUnits() {
        Context context = getModuleManager().getContext();
        for (Module module : context.getModules().getListOfModules()) {
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
        IType type = jdtClass.useCachedType();
        if (type == null && javaProject != null) {
            String packageName = jdtClass.getPackage().getQualifiedName();
            String className = jdtClass.getQualifiedName().substring(packageName.length() +1 );
            type = getNameEnvironment().findTypeInNameLookup(className, packageName);
        }

        if (type == null) {
            return null;
        }
        
        ITypeRoot typeRoot = type.getTypeRoot();
        StringBuilder sb = new StringBuilder();
        List<String> parts = pkg.getName();
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);
            if (! part.isEmpty()) {
                sb.append(part);
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
    public synchronized void removeDeclarations(List<Declaration> declarations) {
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
            super(moduleManager.getContext());
            assert (pkg != null);
            setPackage(pkg);
        }
    }

    
    public class GlobalTypeFactory extends TypeFactory {
        public GlobalTypeFactory() {
            super(moduleManager.getContext());
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
    
    public synchronized void setupSourceFileObjects(List<?> treeHolders) {
        addSourcePhasedUnits(treeHolders, true);
    }

    public synchronized void addSourcePhasedUnits(List<?> treeHolders, final boolean isSourceToCompile) {
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

    public void addSourceArchivePhasedUnits(List<PhasedUnit> sourceArchivePhasedUnits) {
        addSourcePhasedUnits(sourceArchivePhasedUnits, false);
    }
    
    public synchronized void clearCachesOnPackage(String packageName) {
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

    public synchronized void clearClassMirrorCacheForClass(JDTModule module, String classNameToRemove) {
        classMirrorCache.remove(cacheKeyByModule(module, classNameToRemove));        
        mustResetLookupEnvironment = true;
    }

    @Override
    protected LazyValue makeToplevelAttribute(ClassMirror classMirror) {
        if (classMirror instanceof SourceClass) {
            return (LazyValue) (((SourceClass) classMirror).getModelDeclaration());
        }
        return super.makeToplevelAttribute(classMirror);
    }

    @Override
    protected LazyMethod makeToplevelMethod(ClassMirror classMirror) {
        if (classMirror instanceof SourceClass) {
            return (LazyMethod) (((SourceClass) classMirror).getModelDeclaration());
        }
        return super.makeToplevelMethod(classMirror);
    }

    @Override
    protected LazyClass makeLazyClass(ClassMirror classMirror, Class superClass,
            MethodMirror constructor) {
        if (classMirror instanceof SourceClass) {
            return (LazyClass) (((SourceClass) classMirror).getModelDeclaration());
        }
        return super.makeLazyClass(classMirror, superClass, constructor);
    }

    @Override
    protected LazyInterface makeLazyInterface(ClassMirror classMirror) {
        if (classMirror instanceof SourceClass) {
            return (LazyInterface) ((SourceClass) classMirror).getModelDeclaration();
        }
        return super.makeLazyInterface(classMirror);
    }
    
    public TypeFactory getTypeFactory() {
        return (TypeFactory) typeFactory;
    }
    
    public synchronized Package findPackage(String quotedPkgName) {
        String pkgName = quotedPkgName.replace("$", "");
        // in theory we only have one package with the same name per module in eclipse
        for(Package pkg : packagesByName.values()){
            if(pkg.getNameAsString().equals(pkgName))
                return pkg;
        }
        return null;
    }

    @Override
    protected Module findModuleForClassMirror(ClassMirror classMirror) {
        String pkgName = getPackageNameForQualifiedClassName(classMirror);
        return lookupModuleByPackageName(pkgName);
    }
    
    public void loadJDKModules() {
        for(String jdkModule : JDKUtils.getJDKModuleNames())
            findOrCreateModule(jdkModule, JDKUtils.jdk.version);
        for(String jdkOracleModule : JDKUtils.getOracleJDKModuleNames())
            findOrCreateModule(jdkOracleModule, JDKUtils.jdk.version);
    }

    @Override
    public synchronized LazyPackage findOrCreateModulelessPackage(String pkgName) {
        return (LazyPackage) findPackage(pkgName);
    }

    @Override
    public boolean isModuleInClassPath(Module module) {
        return modulesInClassPath.contains(module) || 
                ( (module instanceof JDTModule) && 
                        ((JDTModule) module).isProjectModule());
    }
    
    @Override
    protected boolean needsLocalDeclarations() {
        return false;
    }

    void addJDKModuleToClassPath(Module module) {
        modulesInClassPath.add(module);
    }
}
