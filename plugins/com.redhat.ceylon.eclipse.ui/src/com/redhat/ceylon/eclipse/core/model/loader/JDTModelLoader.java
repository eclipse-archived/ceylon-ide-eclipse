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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.Region;

import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.TypeParser;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.mirror.MethodMirror;
import com.redhat.ceylon.compiler.loader.model.LazyModule;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.core.model.loader.mirror.JDTClass;
import com.redhat.ceylon.eclipse.core.model.loader.mirror.JDTMethod;
import com.redhat.ceylon.eclipse.core.model.loader.model.JDTModule;
import com.redhat.ceylon.eclipse.core.model.loader.model.JDTModuleManager;

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
            public Declaration getLanguageModuleDeclaration(String name) {
                //all elements in ceylon.language are auto-imported
                //traverse all default module packages provided they have not been traversed yet
                Module languageModule = moduleManager.getContext().getModules().getLanguageModule();
                if ( languageModule != null && languageModule.isAvailable() ) {
                    for (Package languageScope : languageModule.getPackages() ) {
                        String packageName = languageScope.getQualifiedNameString() + ".";
                        if (name.startsWith(packageName)) {
                            name = name.substring(packageName.length());
                            break;
                        }
                    }
                    if ("Bottom".equals(name)) {
                        return getBottomDeclaration();
                    }
                    for (Package languageScope : languageModule.getPackages() ) {
                        Declaration d = languageScope.getMember(name, null);
                        if (d != null) {
                            return d;
                        }
                    }
                }
                return null;
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
                
                @Override
                public void accept(ISourceType[] sourceType, PackageBinding packageBinding,
                        AccessRestriction accessRestriction) {
                }
                
                @Override
                public void accept(IBinaryType binaryType, PackageBinding packageBinding,
                        AccessRestriction accessRestriction) {
                    lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);
                }
                
                @Override
                public void accept(ICompilationUnit unit,
                        AccessRestriction accessRestriction) {
                }
            }, compilerOptions, problemReporter, ((JavaProject)javaProject).newSearchableNameEnvironment((WorkingCopyOwner)null));
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            IPackageFragmentRoot root = jdtModule.getPackageFragmentRoot();
            IPackageFragment packageFragment = null;
            if (root != null) {
                packageFragment = root.getPackageFragment(packageName);
                if(packageFragment.exists() && loadDeclarations) {
                    try {
                        for (IClassFile classFile : packageFragment.getClassFiles()) {
                            IType type = classFile.getType();
                            if (! type.isMember()) {
                                convertToDeclaration(lookupClassMirror(type.getFullyQualifiedName()), DeclarationType.VALUE);
                            }
                        }
                    } catch (JavaModelException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Module lookupModule(String packageName) {
        for(Module module : modules.getListOfModules()){
            if(module instanceof LazyModule){
                if(((LazyModule)module).containsPackage(packageName))
                    return module;
            }else if(isSubPackage(module.getNameAsString(), packageName))
                return module;
        }
        return modules.getDefaultModule();
    }

    private boolean isSubPackage(String moduleName, String pkgName) {
        return pkgName.equals(moduleName)
                || pkgName.startsWith(moduleName+".");
    }

    @Override
    public ClassMirror lookupNewClassMirror(String name) {
        try {
            IType type = javaProject.findType(name);
            if (type == null) {
                return null;
            }
            ClassFile classFile = (ClassFile) type.getClassFile();
            
            IFile ifile = null; 
            if (classFile == null) {
                String packageName = type.getPackageFragment().getElementName();
                
                Module module = lookupModule(packageName);
                
                if (module instanceof JDTModule) {
                    JDTModule jdtModule = (JDTModule) module;
                    IPackageFragmentRoot fragmentRoot = jdtModule.getPackageFragmentRoot();
                    if (fragmentRoot != null) {
                        IPackageFragment packageFragment = fragmentRoot.getPackageFragment(packageName);
                        if (packageFragment != null) {
                            String shortClassName = name.substring(packageName.length() + 1);
                            classFile = (ClassFile) packageFragment.getClassFile(shortClassName + ".class");
                            if(classFile == null || !classFile.exists()){
                                org.eclipse.jdt.internal.core.CompilationUnit cu = (CompilationUnit) packageFragment.getCompilationUnit(shortClassName + ".java");
                                IRegion region = new Region();
                                region.add(cu.getPrimaryElement());
                                IResource[] generatedResources = JavaCore.getGeneratedResources(region, false);
                                if(generatedResources.length > 0){
                                    ifile = (IFile) generatedResources[0];
                                }
                            }else
                                ifile = (IFile) classFile.getUnderlyingResource();
                        }
                    }
                }
            }else
                ifile = (IFile) classFile.getUnderlyingResource();
            
            if (classFile != null) {
                IBinaryType binaryType = classFile.getBinaryTypeInfo(ifile, true);
                BinaryTypeBinding binaryTypeBinding = lookupEnvironment.cacheBinaryType(binaryType, null);
                if (binaryTypeBinding == null) {
                    char[][] compoundName = CharOperation.splitOn('/', binaryType.getName());
                    ReferenceBinding existingType = lookupEnvironment.getCachedType(compoundName);
                    if (existingType == null || ! (existingType instanceof BinaryTypeBinding)) {
                        return null;
                    }
                    binaryTypeBinding = (BinaryTypeBinding) existingType;
                }
                return new JDTClass(binaryTypeBinding, lookupEnvironment);
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addModuleToClassPath(Module module, VirtualFile artifact) {
    }

    @Override
    protected boolean isOverridingMethod(MethodMirror methodSymbol) {
        return ((JDTMethod)methodSymbol).isOverridingMethod();
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
}
