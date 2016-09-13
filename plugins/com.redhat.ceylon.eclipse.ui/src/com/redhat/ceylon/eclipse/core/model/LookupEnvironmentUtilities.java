package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.ClassFile;

import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.model.loader.ModelResolutionException;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

public class LookupEnvironmentUtilities {
    public static ThreadLocal<Object> isSettingInterfaceCompanionClassTL = new ThreadLocal<Object>();
    public static Object isSettingInterfaceCompanionClassObj = new Object();

    static boolean isSettingInterfaceCompanionClass() {
        return isSettingInterfaceCompanionClassTL.get() != null;
    }

    public static interface Provider {
        LookupEnvironment getUpToDateLookupEnvironment();
        LookupEnvironment getCurrentLookupEnvironment();
        LookupEnvironment createLookupEnvironmentForGeneratedCode();
        Object getLookupEnvironmentMutex();
        void refreshNameEnvironment();
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
        
        Provider provider = modelJ2C().getLookupEnvironmentProvider(typeModel);
        if (provider == null) {
            throw new ModelResolutionException("The Model Loader corresponding to the type '" + typeModel.getFullyQualifiedName() + "' was not available");
        }
        
        synchronized (provider.getLookupEnvironmentMutex()) {
            if (provider.getCurrentLookupEnvironment() != lookupEnvironment) {
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
        
        Provider modelLoader = modelJ2C().getLookupEnvironmentProvider(declaringClassModel);
        if (modelLoader == null) {
            throw new ModelResolutionException("The Model Loader corresponding the type '" + declaringClassModel.getFullyQualifiedName() + "' doesn't exist");
        }
        
        synchronized (modelLoader.getLookupEnvironmentMutex()) {
            if (modelLoader.getCurrentLookupEnvironment() != lookupEnvironment) {
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
        
        Provider modelLoader = modelJ2C().getLookupEnvironmentProvider(typeModel);
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
        
        Provider modelLoader = modelJ2C().getLookupEnvironmentProvider(typeModel);
        if (modelLoader == null) {
            throw new ModelResolutionException("The Model Loader is not available to resolve type '" + typeModel.getFullyQualifiedName() + "'");
        }
        char[][] compoundName = CharOperation.splitOn('.', typeModel.getFullyQualifiedName().toCharArray());
        LookupEnvironment lookupEnvironment = modelLoader.getUpToDateLookupEnvironment();
        synchronized (modelLoader.getLookupEnvironmentMutex()) {
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

    static ReferenceBinding toBinding(IType type, LookupEnvironment theLookupEnvironment, char[][] compoundName) throws JavaModelException {
        return toBinding(type, theLookupEnvironment, compoundName, null);
    }

    static ReferenceBinding toBinding(IType type, LookupEnvironment theLookupEnvironment, char[][] compoundName, ClassFileReader[] readerHolder) throws JavaModelException {
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
                IBinaryType binaryType;
                if (type instanceof BinaryType) {
                    binaryType = (IBinaryType) ((BinaryType) type).getElementInfo();                    
                } else {
                    binaryType = classFile.getBinaryTypeInfo(classFileRsrc, true);                    
                }
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
