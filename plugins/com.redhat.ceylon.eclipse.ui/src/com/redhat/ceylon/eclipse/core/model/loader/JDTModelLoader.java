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

package com.redhat.ceylon.eclipse.core.model.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.SourceTypeElementInfo;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;

import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.compiler.java.loader.TypeFactory;
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
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ExternalUnit;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.model.CeylonDeclaration;
import com.redhat.ceylon.eclipse.core.model.loader.mirror.JDTClass;
import com.redhat.ceylon.eclipse.core.model.loader.mirror.JDTMethod;
import com.redhat.ceylon.eclipse.core.model.loader.mirror.SourceClass;
import com.redhat.ceylon.eclipse.core.model.loader.model.JDTModule;
import com.redhat.ceylon.eclipse.core.model.loader.model.JDTModuleManager;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;

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
    
    private Map<String, Declaration> languageModuledeclarations;
    
    public JDTModelLoader(final ModuleManager moduleManager, final Modules modules){
        this.moduleManager = moduleManager;
        this.modules = modules;
        javaProject = ((JDTModuleManager)moduleManager).getJavaProject();
        compilerOptions = new CompilerOptions(javaProject.getOptions(true));
        compilerOptions.ignoreMethodBodies = true;
        compilerOptions.storeAnnotations = true;
        problemReporter = new ProblemReporter(
                DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                compilerOptions,
                new DefaultProblemFactory());
        
        internalCreate();
    }

    private void internalCreate() {
        this.languageModuledeclarations = new HashMap<String, Declaration>();
        this.typeFactory = new TypeFactory(moduleManager.getContext()) {
            @Override
            public Package getPackage() {
                if(super.getPackage() == null){
                    super.setPackage(modules.getLanguageModule().getDirectPackage("ceylon.language"));
                }
                return super.getPackage();
            }
            /**
             * Search for a declaration in the language module. 
             */
            public Declaration getLanguageModuleDeclaration(String name) {
                if (languageModuledeclarations.containsKey(name)) {
                    return languageModuledeclarations.get(name);
                }
                
                languageModuledeclarations.put(name, null);
                Declaration decl = super.getLanguageModuleDeclaration(name);
                languageModuledeclarations.put(name, decl);
                return decl;
            }
        };
        this.typeParser = new TypeParser(this, typeFactory);
        createLookupEnvironment();
    }

    public void createLookupEnvironment() {
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
                    lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);
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
            }, compilerOptions, problemReporter, ((JavaProject)javaProject).newSearchableNameEnvironment((WorkingCopyOwner)null));
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    // TODO : remove when the bug in the AbstractModelLoader is corrected
    @Override
    public LazyPackage findOrCreatePackage(Module module, String pkgName) {
        LazyPackage pkg = super.findOrCreatePackage(module, pkgName);
        if ("".equals(pkgName)) {
            pkg.setName(Collections.<String>emptyList());
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
        /*
         * We start by loading java.lang and ceylon.language because we will need them no matter what.
         */
        
        Module javaModule = findOrCreateModule("java.lang");
        Package javaLangPackage = findOrCreatePackage(javaModule, "java.lang");
        javaLangPackage.setShared(true);
        
        loadPackage("java.lang", false);
    }
    
    private String getQualifiedName(final String pkgName, String name) {
        // FIXME: some refactoring needed
        name = Util.quoteIfJavaKeyword(name);
        String className = pkgName.isEmpty() ? name : Util.quoteJavaKeywords(pkgName) + "." + name;
        return className;
    }
    
    @Override
    public void loadPackage(String packageName, boolean loadDeclarations) {
        packageName = Util.quoteJavaKeywords(packageName);
        if(loadDeclarations && !loadedPackages.add(packageName)){
            return;
        }
        if(!loadDeclarations)
            return;
        Module module = lookupModule(packageName);
        
        if (module instanceof JDTModule) {
            JDTModule jdtModule = (JDTModule) module;
            List<IPackageFragmentRoot> roots = jdtModule.getPackageFragmentRoots();
            IPackageFragment packageFragment = null;
            for (IPackageFragmentRoot root : roots) {
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
                    if(packageFragment.exists() && loadDeclarations) {
                        try {
                            for (IClassFile classFile : packageFragment.getClassFiles()) {
                                IType type = classFile.getType();
                                if (! type.isMember() && !sourceDeclarations.containsKey(getQualifiedName(type.getPackageFragment().getElementName(), type.getTypeQualifiedName()))) {
                                    convertToDeclaration(type.getFullyQualifiedName(), DeclarationType.VALUE);
                                }
                            }
                            for (org.eclipse.jdt.core.ICompilationUnit compilationUnit : packageFragment.getCompilationUnits()) {
                                for (IType type : compilationUnit.getTypes()) {
                                    if (! type.isMember() && !sourceDeclarations.containsKey(getQualifiedName(type.getPackageFragment().getElementName(), type.getTypeQualifiedName()))) {
                                        convertToDeclaration(type.getFullyQualifiedName(), DeclarationType.VALUE);
                                    }
                                }
                            }
                        } catch (JavaModelException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Module lookupModule(String packageName) {
        Module module = lookupModuleInternal(packageName);
        if (module != null) {
            return module;
        }
        return modules.getDefaultModule();
    }

    public Module lookupModuleInternal(String packageName) {
        for(Module module : modules.getListOfModules()){
            if(module instanceof LazyModule){
                if(((LazyModule)module).containsPackage(packageName))
                    return module;
            }else if(isSubPackage(module.getNameAsString(), packageName))
                return module;
        }
        return null;
    }

    private boolean isSubPackage(String moduleName, String pkgName) {
        return pkgName.equals(moduleName)
                || pkgName.startsWith(moduleName+".");
    }

    synchronized private LookupEnvironment getLookupEnvironment() {
        if (mustResetLookupEnvironment) {
            lookupEnvironment.reset();
            mustResetLookupEnvironment = false;
        }
        return lookupEnvironment;
    }
    
    @Override
    public synchronized ClassMirror lookupNewClassMirror(String name) {
        if (sourceDeclarations.containsKey(name)) {
            return new SourceClass(sourceDeclarations.get(name));
        }
        
        try {
            IType type = javaProject.findType(name);
            if (type == null) {
                return null;
            }
            
            
            LookupEnvironment theLookupEnvironment = getLookupEnvironment();
            if (type.isBinary()) {
                ClassFile classFile = (ClassFile) type.getClassFile();
                
                if (classFile != null) {
                    IBinaryType binaryType = classFile.getBinaryTypeInfo((IFile) classFile.getCorrespondingResource(), true);
                    BinaryTypeBinding binaryTypeBinding = theLookupEnvironment.cacheBinaryType(binaryType, null);
                    if (binaryTypeBinding == null) {
                        char[][] compoundName = CharOperation.splitOn('/', binaryType.getName());
                        ReferenceBinding existingType = theLookupEnvironment.getCachedType(compoundName);
                        if (existingType == null || ! (existingType instanceof BinaryTypeBinding)) {
                            return null;
                        }
                        binaryTypeBinding = (BinaryTypeBinding) existingType;
                    }
                    return new JDTClass(binaryTypeBinding, theLookupEnvironment);
                }
            } else {
                char[][] compoundName = CharOperation.splitOn('.', type.getFullyQualifiedName().toCharArray());
                ReferenceBinding referenceBinding = theLookupEnvironment.getType(compoundName);
                if (referenceBinding != null) {
                    if (referenceBinding instanceof ProblemReferenceBinding) {
                        ProblemReferenceBinding problemReferenceBinding = (ProblemReferenceBinding) referenceBinding;
                        if (problemReferenceBinding.problemId() == ProblemReasons.InternalNameProvided) {
                            referenceBinding = problemReferenceBinding.closestReferenceMatch();
                        } else {
                            System.out.println(ProblemReferenceBinding.problemReasonString(problemReferenceBinding.problemId()));
                            return null;
                        }
                    }
                    return new JDTClass(referenceBinding, theLookupEnvironment);
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public Declaration convertToDeclaration(String typeName,
            DeclarationType declarationType) {
        if (sourceDeclarations.containsKey(typeName)) {
            return sourceDeclarations.get(typeName).getModelDeclaration();
        }
        try {
            return super.convertToDeclaration(typeName, declarationType);
        } catch(RuntimeException e) {
            return null;
        }
    }

    @Override
    public void addModuleToClassPath(Module module, ArtifactResult artifact) {}
    
    @Override
    protected boolean isOverridingMethod(MethodMirror methodSymbol) {
        return ((JDTMethod)methodSymbol).isOverridingMethod();
    }

    
    @Override
    public Module findOrCreateModule(String pkgName) {
        java.util.List<String> moduleName;
        boolean isJava = false;
        boolean defaultModule = false;

        Module module = lookupModuleInternal(pkgName);
        if (module != null) {
            return module;
        }
        
        // FIXME: this is a rather simplistic view of the world
        if(pkgName == null){
            moduleName = Arrays.asList(Module.DEFAULT_MODULE_NAME);
            defaultModule = true;
        }else if(pkgName.startsWith("java.")){
            moduleName = Arrays.asList("java");
            isJava = true;
        } else if(pkgName.startsWith("sun.")){
            moduleName = Arrays.asList("sun");
            isJava = true;
        } else if(pkgName.startsWith("ceylon.language."))
            moduleName = Arrays.asList("ceylon","language");
        else{
            moduleName = Arrays.asList(Module.DEFAULT_MODULE_NAME);
            defaultModule = true;
        }
        
        module = moduleManager.getOrCreateModule(moduleName, null);
        // make sure that when we load the ceylon language module we set it to where
        // the typechecker will look for it
        if(pkgName != null
                 && pkgName.startsWith("ceylon.language.")
                 && modules.getLanguageModule() == null){
             modules.setLanguageModule(module);
         }
         
         if (module instanceof LazyModule) {
             ((LazyModule)module).setJava(isJava);
         }
         // FIXME: this can't be that easy.
         module.setAvailable(true);
         module.setDefault(defaultModule);
         return module;
    }

    @Override
    protected Unit getCompiledUnit(LazyPackage pkg, ClassMirror classMirror) {
        Unit unit = null;
        JDTClass jdtClass = ((JDTClass)classMirror);
        String unitName = jdtClass.getFileName();
        if (!jdtClass.isBinary()) {
            for (Unit unitToTest : pkg.getUnits()) {
                if (unitToTest.getFilename().equals(unitName)) {
                    return unitToTest;
                }
            }
        }
        unit = new ExternalUnit();
        unit.setFilename(jdtClass.getFileName());
        unit.setPackage(pkg);
        return unit;
    }

    @Override
    protected void logError(String message) {
        System.err.println("ERROR: "+message);
    }

    @Override
    protected void logWarning(String message) {
        System.err.println("WARNING: "+message);
    }

    @Override
    protected void logVerbose(String message) {
        System.err.println("NOTE: "+message);
    }
    
    @Override
    public void removeDeclarations(List<Declaration> declarations) {
        List<Declaration> allDeclarations = new ArrayList<Declaration>(declarations.size());
        allDeclarations.addAll(declarations);
        for (Declaration declaration : declarations) {
            retrieveInnerDeclarations(declaration, allDeclarations);
        }
        super.removeDeclarations(allDeclarations);
        mustResetLookupEnvironment = true;
    }

    private void retrieveInnerDeclarations(Declaration declaration,
            List<Declaration> allDeclarations) {
        List<Declaration> members = declaration.getMembers();
        allDeclarations.addAll(members);
        for (Declaration member : members) {
            retrieveInnerDeclarations(member, allDeclarations);
        }
    }
    
    private final Map<String, CeylonDeclaration> sourceDeclarations = new TreeMap<String, CeylonDeclaration>();
    
    public Set<String> getSourceDeclarations() {
        Set<String> declarations  = new HashSet<String>();
        synchronized (sourceDeclarations) {
            declarations.addAll(sourceDeclarations.keySet());
        }
        return declarations;
    }
    
    public static interface SourceFileObjectManager {
        void setupSourceFileObjects(List<?> treeHolders);
    }
    
    private SourceFileObjectManager additionalSourceFileObjectsManager = null;
    public void setSourceFileObjectManager(SourceFileObjectManager manager) {
        additionalSourceFileObjectsManager = manager;
    }
    
    public void setupSourceFileObjects(List<?> treeHolders) {
        addSourcePhasedUnits(treeHolders, true);
        if (additionalSourceFileObjectsManager != null) {
            additionalSourceFileObjectsManager.setupSourceFileObjects(treeHolders);
        }
    }

    public void addSourcePhasedUnits(List<?> treeHolders, final boolean isSourceToCompile) {
        for (Object treeHolder : treeHolders) {
            if (treeHolder instanceof PhasedUnit) {
                final PhasedUnit unit = (PhasedUnit) treeHolder;
                final String pkgName = unit.getPackage().getQualifiedNameString();
                unit.getCompilationUnit().visit(new SourceDeclarationVisitor(){
                    @Override
                    public void loadFromSource(Tree.Declaration decl) {
                        String name = Util.quoteIfJavaKeyword(decl.getIdentifier().getText());
                        String fqn = getQualifiedName(pkgName, name);
                        if (! sourceDeclarations.containsKey(fqn)) {
                            synchronized (sourceDeclarations) {
                                sourceDeclarations.put(fqn, new CeylonDeclaration(unit, decl, isSourceToCompile));
                            }
                        }
                    }
                });
            }
        }
    }

    public void addSourceArchivePhasedUnits(List<PhasedUnit> sourceArchivePhasedUnits) {
        addSourcePhasedUnits(sourceArchivePhasedUnits, false);
    }
    
    public void clearCachesOnPackage(String packageName) {
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
        loadedPackages.remove(packageName);
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
            MethodMirror constructor, boolean forTopLevelObject) {
        if (classMirror instanceof SourceClass) {
            return (LazyClass) (((SourceClass) classMirror).getModelDeclaration());
        }
        return super.makeLazyClass(classMirror, superClass, constructor,
                forTopLevelObject);
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
    
    public void reset() {
        internalCreate();
        declarationsByName.clear();
        unitsByPackage.clear();
        loadedPackages.clear();
        packageDescriptorsNeedLoading = false;
        classMirrorCache.clear();
    }
}
