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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IClassFile;
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
import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.TypeParser;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.mirror.MethodMirror;
import com.redhat.ceylon.compiler.loader.model.LazyModule;
import com.redhat.ceylon.compiler.loader.model.LazyPackage;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ExternalUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
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
    
    public JDTModelLoader(final ModuleManager moduleManager, final Modules modules){
        this.moduleManager = moduleManager;
        this.modules = modules;
        this.typeFactory = new Unit() {
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
            private Map<String, Declaration> languageModuledeclarations = new HashMap<String, Declaration>();
            
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
        javaProject = ((JDTModuleManager)moduleManager).getJavaProject();

        compilerOptions = new CompilerOptions(javaProject.getOptions(true));
        compilerOptions.ignoreMethodBodies = true;
        compilerOptions.storeAnnotations = true;
        problemReporter = new ProblemReporter(
                DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                compilerOptions,
                new DefaultProblemFactory());
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
            module.getPackages().add(pkg);
            pkg.setModule(module);
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
        loadPackage("com.redhat.ceylon.compiler.java.metadata", false);
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
                    if (CeylonBuilder.isCeylonSourceEntry(root.getRawClasspathEntry())) {
                        packageFragment = root.getPackageFragment(packageName);
                        if(packageFragment.exists() && loadDeclarations) {
                            try {
                                for (IClassFile classFile : packageFragment.getClassFiles()) {
                                    IType type = classFile.getType();
                                    if (! type.isMember() && !sourceDeclarations.contains(type.getFullyQualifiedName())) {
                                        convertToDeclaration(type.getFullyQualifiedName(), DeclarationType.VALUE);
                                    }
                                }
                                for (org.eclipse.jdt.core.ICompilationUnit compilationUnit : packageFragment.getCompilationUnits()) {
                                    for (IType type : compilationUnit.getTypes()) {
                                        if (! type.isMember() && !sourceDeclarations.contains(type.getFullyQualifiedName())) {
                                            convertToDeclaration(type.getFullyQualifiedName(), DeclarationType.VALUE);
                                        }
                                    }
                                }
                            } catch (JavaModelException e) {
                                e.printStackTrace();
                            }
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
        if (sourceDeclarations.contains(name)) {
            return new SourceClass(name);
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
        if (typeName.startsWith("ceylon.language")) {
            return typeFactory.getLanguageModuleDeclaration(typeName.substring(typeName.lastIndexOf('.') + 1));
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
    
    private Set<String> sourceDeclarations = new TreeSet<String>();
    public void setupSourceFileObjects(List<PhasedUnit> phasedUnits) {
        for (PhasedUnit unit : phasedUnits) {
            final String pkgName = unit.getPackage().getQualifiedNameString();
            unit.getCompilationUnit().visit(new SourceDeclarationVisitor(){
                @Override
                public void loadFromSource(Tree.Declaration decl) {
                    String name = Util.quoteIfJavaKeyword(decl.getIdentifier().getText());
                    String fqn = pkgName.isEmpty() ? name : pkgName+"."+name;
                        sourceDeclarations.add(fqn);
                }
            });
        }
    }
}
