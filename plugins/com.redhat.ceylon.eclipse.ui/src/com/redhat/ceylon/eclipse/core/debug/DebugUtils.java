package com.redhat.ceylon.eclipse.core.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.internal.debug.core.model.JDIClassType;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDINullValue;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.codegen.Naming.Suffix;
import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import com.redhat.ceylon.compiler.java.language.LazyIterable;
import com.redhat.ceylon.compiler.java.runtime.metamodel.Metamodel;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.loader.ModelLoader.DeclarationType;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.LazyProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget.EvaluationListener;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget.EvaluationRunner;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget.EvaluationWaiter;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.JavaSearch;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

public class DebugUtils {

    public static IJavaProject getProject(IDebugElement debugElement) {
        IDebugTarget target = debugElement.getDebugTarget();
        if (target instanceof CeylonJDIDebugTarget) {
            IProject project = ((CeylonJDIDebugTarget) target).getProject();
            return project == null ? null : JavaCore.create(project);
        }
        return null;
    }

    public static IMethod getJavaMethod(IJavaStackFrame frame,
            IJavaProject project) {
        try {
            IType declaringType = getTypeForJDIReferenceType(frame, project);
            if (declaringType != null) {
                for (IMethod method : declaringType.getMethods()) {
                    if (method.getElementName().equals(frame.getMethodName())
                            || frame.isConstructor() && method.isConstructor()) {
                        String[] methodParameterTypes = new String[method
                                .getParameterTypes().length];
                        int i = 0;
                        for (String signature : method.getParameterTypes()) {
                            signature = signature
                                    .replace("$", "####dollar####");
                            signature = Signature.toString(signature);
                            signature = signature
                                    .replace("####dollar####", "$");
                            methodParameterTypes[i++] = signature;
                        }
                        if (Arrays.equals(methodParameterTypes, frame
                                .getArgumentTypeNames().toArray())) {
                            return method;
                        }
                    }
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IType getTypeForJDIReferenceType(IJavaStackFrame frame,
            IJavaProject javaProject) {
        try {
            IJavaReferenceType referenceType = frame.getReferenceType();
            IType declaringType = javaProject.findType(referenceType.getName());
            return declaringType;
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IType getJavaType(IJavaObject object) {
        IJavaProject project = getProject(object);
        return project == null ? null : getJavaType(object, project);
    }

    public static IType getJavaType(IJavaObject object,
            IJavaProject javaProject) {
        try {
            IJavaReferenceType referenceType = (IJavaReferenceType) object.getJavaType();
            IType declaringType = javaProject.findType(referenceType.getName());
            return declaringType;
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IType getJavaType(IJavaClassType jdiType) {
        IJavaProject project = getProject(jdiType);
        return project == null ? null : getJavaType(jdiType, project);
    }

    public static IType getJavaType(IJavaClassType jdiType,
            IJavaProject javaProject) {
        try {
            IType declaringType = javaProject.findType(jdiType.getName());
            return declaringType;
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IMethod getJavaMethod(IJavaStackFrame frame) {
        IJavaProject project = getProject(frame);
        return project == null ? null : getJavaMethod(frame, project);
    }

    public static PhasedUnit getPhasedUnit(IJavaObject object,
            IJavaProject project) {
        try {
            IJavaType javaType = object.getJavaType();
            if (javaType instanceof IJavaClassType) {
                IJavaClassType javaClassType = (IJavaClassType) javaType;
                String[] sourcePaths = javaClassType.getSourcePaths(null);
                if (sourcePaths != null) {
                    for (String sourcePath : sourcePaths) {
                        PhasedUnit pu = getPhasedUnit(sourcePath, project);
                        if (pu != null) {
                            return pu;
                        }
                    }
                }
            }
            
        } catch (DebugException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PhasedUnit getPhasedUnit(IJavaStackFrame frame,
            IJavaProject project) {
        try {
            return getPhasedUnit(frame.getSourcePath(), project);
        } catch (DebugException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PhasedUnit getPhasedUnit(String sourcePath,
            IJavaProject project) {
        PhasedUnits projectPhasedUnits = CeylonBuilder
                .getProjectPhasedUnits(project.getProject());
        if (projectPhasedUnits != null) {
            PhasedUnit phasedUnit = null;
            phasedUnit = projectPhasedUnits
                    .getPhasedUnitFromRelativePath(sourcePath);
            if (phasedUnit != null) {
                return phasedUnit;
            }
        }
        for (Module module : CeylonBuilder
                .getProjectExternalModules(project.getProject())) {
            if (module instanceof JDTModule) {
                JDTModule jdtModule = (JDTModule) module;
                if (jdtModule.isCeylonArchive()) {
                    PhasedUnit phasedUnit = jdtModule
                            .getPhasedUnitFromRelativePath(sourcePath);
                    if (phasedUnit != null) {
                        if (phasedUnit instanceof CrossProjectPhasedUnit) {
                            phasedUnit = ((CrossProjectPhasedUnit) phasedUnit)
                                    .getOriginalProjectPhasedUnit();
                        }
                        return phasedUnit;
                    }
                }
            }
        }
        return null;
    }

    public static PhasedUnit getPhasedUnit(IJavaStackFrame frame) {
        IJavaProject project = getProject(frame);
        return project == null ? null : getPhasedUnit(frame, project);
    }

    public static Declaration getSourceDeclaration(
            IJavaStackFrame frame) {
        IJavaProject project = getProject(frame);
        if (project == null) {
            // Not a Ceylon debug target
            return null;
        }
        IMethod method = getJavaMethod(frame, project);
        PhasedUnit unit = getPhasedUnit(frame, project);
        if (method != null && unit != null) {
            return JavaSearch.toCeylonDeclaration(method, Arrays.asList(unit));
        }
        return null;
    }

    public static Declaration getSourceDeclaration(
            IJavaObject object) {
        IJavaProject project = getProject(object);
        if (project == null) {
            // Not a Ceylon debug target
            return null;
        }

        IType type = getJavaType(object, project);
        PhasedUnit unit = getPhasedUnit(object, project);
        if (type != null) {
            if (unit != null) {
                Declaration result = JavaSearch.toCeylonDeclaration(type, Arrays.asList(unit));
                return result;
            }
        }
        return null;
    }

    public static Declaration getDeclaration(
            IJavaObject object) {
        Declaration declaration = getSourceDeclaration(object);
        if (declaration == null) {
            try {
                declaration = getModelDeclaration(object);
            } catch (DebugException e) {
                e.printStackTrace();
            }
        }
        return declaration;
    }
    
    private interface ProducedTypeAction<ReturnType extends IJavaValue> {
        ReturnType doOnProducedType(IJavaObject producedType, 
                IJavaThread innerThread, 
                IProgressMonitor monitor) throws DebugException;
    }
    
    public static JDIClassType getMetaModelClass(JDIDebugTarget debugTarget) {
        IJavaType[] types = null;
        try {
            types = debugTarget.getJavaTypes(Metamodel.class.getName());
        } catch (DebugException e) {
            e.printStackTrace();
        }
        if (types != null && types.length > 0) {
            return (JDIClassType) types[0];
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static <ReturnType extends IJavaValue> ReturnType doOnJdiProducedType(IValue value, final ProducedTypeAction<ReturnType> postAction) {
        if (value instanceof JDINullValue) {
            return null;
        }
        if (value instanceof JDIObjectValue) {
            JDIObjectValue objectValue = (JDIObjectValue) value;
            try {
                final IJavaReferenceType type = (IJavaReferenceType) objectValue.getJavaType();
                final String typeName = type.getName();
                if (typeName.endsWith("$impl")) {
                    IJavaFieldVariable thisField = objectValue.getField("$this", 0);
                    value = null;
                    if (thisField != null) {
                        IValue fieldValue = thisField.getValue();
                        if (fieldValue instanceof JDIObjectValue && 
                                !(fieldValue instanceof JDINullValue)) {
                            value = fieldValue;
                            objectValue = (JDIObjectValue) value;
                        }
                    }
                }
                
                if (value != null) {
                    final JDIObjectValue javaValue = objectValue;
                    final JDIDebugTarget debugTarget = objectValue.getJavaDebugTarget();
                    if (debugTarget instanceof CeylonJDIDebugTarget) {
                        CeylonJDIDebugTarget ceylonDebugTarget = (CeylonJDIDebugTarget) debugTarget;
                        final boolean isMethod = ceylonDebugTarget.isAnnotationPresent(type, com.redhat.ceylon.compiler.java.metadata.Method.class, 5000);
                        
                        IJavaValue reifiedTypeInfo = ceylonDebugTarget.getEvaluationResult(new EvaluationRunner() {
                            @Override
                            public void run(IJavaThread innerThread, IProgressMonitor monitor,
                                    EvaluationListener listener) throws DebugException {

                                IJavaValue producedType = null;
                                IJavaValue typeDescriptor = null;
                                if (typeName.contains("ProducedType")) {
                                    try {
                                        Class<?> producedTypeClass = ProducedType.class.getClassLoader().loadClass(javaValue.getReferenceTypeName());
                                        if (producedTypeClass != null &&
                                                ProducedType.class.isAssignableFrom(producedTypeClass)) {
                                            producedType = javaValue;
                                        }
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                } else if (typeName.contains("TypeDescriptor")) {
                                    try {
                                        Class<?> typeDescriptorClass = TypeDescriptor.class.getClassLoader().loadClass(javaValue.getReferenceTypeName());
                                        if (typeDescriptorClass != null &&
                                                TypeDescriptor.class.isAssignableFrom(typeDescriptorClass)) {
                                            typeDescriptor = javaValue;
                                        }
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (producedType == null) {
                                    JDIClassType metamodelType = getMetaModelClass(javaValue.getJavaDebugTarget());
                                    if (metamodelType != null) {
                                        if (typeDescriptor == null) {
                                            boolean cancelTypeDescriptorRetrieval = false;
                                            for(IVariable v : javaValue.getVariables()) {
                                                if (v.getName().startsWith(Naming.Prefix.$reified$.name())) {
                                                    IValue reifiedTypeValue = v.getValue();
                                                    if (reifiedTypeValue == null || reifiedTypeValue instanceof JDINullValue) {
                                                        // The reified type arguments are not fully set,
                                                        // we are probably at the first line of the constructor.
                                                        // => Don't try to retrieve the produced type.
                                                        cancelTypeDescriptorRetrieval = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (! cancelTypeDescriptorRetrieval) {
                                                try {
                                                    typeDescriptor = javaValue.sendMessage("$getType$", "()Lcom/redhat/ceylon/compiler/java/runtime/model/TypeDescriptor;", new IJavaValue[0], innerThread, null);
                                                } catch(DebugException de) {
                                                    // the value surely doesn't implement ReifiedType
                                                    if (! isMethod) {
                                                        // Don't call getTypeDescriptor for objects that are in fact local Ceylon methods. It would trigger an exception 
                                                        typeDescriptor = metamodelType.sendMessage("getTypeDescriptor", "(Ljava/lang/Object;)Lcom/redhat/ceylon/compiler/java/runtime/model/TypeDescriptor;", new IJavaValue[] {javaValue}, innerThread);
                                                    }
                                                }
                                            }
                                        }
                                        if (typeDescriptor instanceof IJavaObject && ! (typeDescriptor instanceof JDINullValue)) {
                                            producedType = metamodelType.sendMessage("getProducedType", "(Lcom/redhat/ceylon/compiler/java/runtime/model/TypeDescriptor;)Lcom/redhat/ceylon/compiler/typechecker/model/ProducedType;", new IJavaValue[] {typeDescriptor}, innerThread);
                                        }
                                    }
                                }

                                if (producedType instanceof IJavaObject) {
                                    listener.finished(postAction.doOnProducedType((IJavaObject)producedType, innerThread, monitor));
                                } else {
                                    listener.finished(null);
                                }
                            }
                        }, 5000);
                        return (ReturnType)reifiedTypeInfo;
                    }
                }
            } catch (DebugException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static String getProducedTypeName(IValue value) throws DebugException {
        IJavaValue reifiedTypeNameValue = doOnJdiProducedType(value, new ProducedTypeAction<IJavaValue>() {
            @Override
            public IJavaValue doOnProducedType(IJavaObject producedType,
                    IJavaThread innerThread, IProgressMonitor monitor)
            throws DebugException {
                if (producedType instanceof IJavaObject && ! (producedType instanceof JDINullValue)) {
                    IJavaValue producedTypeName = ((IJavaObject) producedType).sendMessage("getProducedTypeName", "()Ljava/lang/String;", new IJavaValue[] {}, innerThread, "Lcom/redhat/ceylon/compiler/typechecker/model/ProducedType;");
                    return producedTypeName;
                }
                return null;
            }
        });
        
        if (reifiedTypeNameValue instanceof JDIObjectValue  && !(reifiedTypeNameValue instanceof JDINullValue)) {
            String reifiedTypeName;
            reifiedTypeName = reifiedTypeNameValue.getValueString();
            return reifiedTypeName;
        }
        
        return null;
    }
    
    public static IJavaObject getJdiProducedType(IValue value) throws DebugException {
        return doOnJdiProducedType(value, new ProducedTypeAction<IJavaObject>() {
            @Override
            public IJavaObject doOnProducedType(IJavaObject producedType,
                    IJavaThread innerThread, IProgressMonitor monitor)
            throws DebugException {
                return producedType;
            }
        });
    }
    

    public static Declaration getModelDeclaration(IJavaObject jdiObject) throws DebugException {
        return getModelDeclaration(jdiObject, null);
    }   
    
    private static Declaration getModelDeclaration(IJavaObject jdiObject, IJavaThread evaluationThread) throws DebugException {
        IJavaObject jdiDeclaration = getJdiDeclaration(jdiObject);

        if (! (jdiDeclaration instanceof JDIObjectValue)) {
            return null;
        }

        final CeylonJDIDebugTarget debugTarget = (CeylonJDIDebugTarget)((JDIObjectValue) jdiDeclaration).getJavaDebugTarget();
        return toModelDeclaration(evaluationThread, debugTarget, jdiDeclaration);
    }
    
    public static ProducedType getModelProducedType(IJavaObject jdiObject) throws DebugException {
        return getModelProducedType(jdiObject, null);
    }   
    
    private static ProducedType getModelProducedType(IJavaObject jdiObject, IJavaThread evaluationThread) throws DebugException {
        IJavaObject jdiProducedType = getJdiProducedType(jdiObject);

        if (! (jdiProducedType instanceof JDIObjectValue)) {
            return null;
        }

        return toModelProducedType(jdiProducedType, evaluationThread);
    }

    
    public static ProducedType toModelProducedType(IJavaObject jdiProducedType) throws DebugException {
        return toModelProducedType(jdiProducedType, null);
    }   
    
    private static ProducedType toModelProducedType(IJavaObject jdiProducedType, IJavaThread evaluationThread) throws DebugException {
        if (! (jdiProducedType instanceof JDIObjectValue)) {
            return null;
        }

        final CeylonJDIDebugTarget debugTarget = (CeylonJDIDebugTarget)((JDIObjectValue) jdiProducedType).getJavaDebugTarget();

        final IJavaObject jdiDeclaration = toJdiDeclaration(jdiProducedType);
        
        Declaration declaration = toModelDeclaration(evaluationThread,
                debugTarget, jdiDeclaration);
        if (declaration instanceof TypeDeclaration) {
            Unit unit = declaration.getUnit();
            final TypeDeclaration typeDeclaration = (TypeDeclaration) declaration;
            final List<TypeParameter> typeParameters = typeDeclaration.getTypeParameters();
            final List<ProducedType> typeArguments = new ArrayList<>(typeParameters.size());
            ProducedTypeAction<IJavaValue> produceTypeAction = new ProducedTypeAction<IJavaValue>() {
                @Override
                public IJavaValue doOnProducedType(IJavaObject producedType,
                        IJavaThread innerThread, IProgressMonitor monitor)
                        throws DebugException {
                    IJavaObject producedTypeList = (IJavaObject) producedType.sendMessage("getTypeArgumentList", "()Ljava/util/List;", new IJavaValue[] {}, innerThread, null);
                    if (producedTypeList instanceof IJavaObject) {
                        IJavaValue size = ((IJavaObject)producedTypeList).sendMessage("size", "()I", new IJavaValue[] {}, innerThread, null);
                        if (size instanceof IJavaPrimitiveValue) {
                            final int intSize = ((IJavaPrimitiveValue)size).getIntValue();
                            if (intSize != typeParameters.size()) {
                                return debugTarget.newValue(false);
                            }
                            int i;
                            for (i = 0; i<intSize; i++) {
                                IJavaValue childTypeValue = ((IJavaObject)producedTypeList).sendMessage("get", "(I)Ljava/lang/Object;", new IJavaValue[] {debugTarget.newValue(i)}, innerThread, null);
                                if (childTypeValue instanceof IJavaObject) {
                                    ProducedType childType = toModelProducedType((IJavaObject)childTypeValue, innerThread);
                                    if (childType == null) {
                                        break;
                                    }
                                    typeArguments.add(childType);
                                }
                            }
                            if (i == intSize) {
                                return debugTarget.newValue(true);
                            }
                        }
                    }
                    return debugTarget.newValue(false);
                }
            };
            IJavaValue result = null;
            if (evaluationThread == null) {
                result = doOnJdiProducedType(jdiProducedType, produceTypeAction);
            } else {
                result = produceTypeAction.doOnProducedType(jdiProducedType, evaluationThread, null);
            }
            if (result instanceof IJavaPrimitiveValue &&
                    ((IJavaPrimitiveValue) result).getBooleanValue()) {
                final Map<TypeParameter, ProducedType> typeArgumentMap = new HashMap<>();
                for (int i = 0; i< typeParameters.size(); i++) {
                    typeArgumentMap.put(typeParameters.get(i), typeArguments.get(i));
                }
                return new LazyProducedType(unit) {

                    @Override
                    public Map<TypeParameter, ProducedType> initTypeArguments() {
                        return typeArgumentMap;
                    }

                    @Override
                    public TypeDeclaration initDeclaration() {
                        return typeDeclaration;
                    }
                    
                };
            }
        }
        return null;
    }

    public static Declaration toModelDeclaration(IJavaThread evaluationThread,
            final CeylonJDIDebugTarget debugTarget,
            final IJavaObject jdiDeclaration) throws DebugException {
        if (jdiDeclaration == null) {
            return null;
        }
        
        EvaluationRunner runner = new EvaluationRunner() {
            @Override
            public void run(IJavaThread innerThread, IProgressMonitor monitor,
                    EvaluationListener listener) throws DebugException {
                IJavaValue qualifiedStringValue = jdiDeclaration.sendMessage("getQualifiedNameString", "()Ljava/lang/String;", new IJavaValue[0], innerThread, "Lcom/redhat/ceylon/compiler/typechecker/model/Declaration;");
                listener.finished(qualifiedStringValue);
            }
        };
        
        IJavaValue nameFieldValue = null;
        if (evaluationThread == null) {
            nameFieldValue = debugTarget.getEvaluationResult(runner, 5000);
        } else {
            EvaluationWaiter waiter = new EvaluationWaiter();
            runner.run(evaluationThread, null, waiter);
            nameFieldValue = waiter.waitForResult(5000);
        }
        

        IJavaProject javaProject = DebugUtils.getProject(jdiDeclaration);
        JDTModelLoader modelLoader = CeylonBuilder.getProjectModelLoader(javaProject.getProject());
        String qualifiedName = nameFieldValue.getValueString();
        String[] qualifiedNameParts = qualifiedName.split("::");
        Declaration declaration = null;
        if (qualifiedNameParts.length > 1) {
            String packageName = modelLoader.getPackageNameForQualifiedClassName(qualifiedNameParts[0], qualifiedName);
            Module module = modelLoader.lookupModuleByPackageName(packageName);
            declaration = modelLoader.convertToDeclaration(module, qualifiedName.replace("::",  "."), DeclarationType.TYPE);
        }
        return declaration;
    }
    
    
    public static IJavaObject toJdiDeclaration(IJavaObject jdiProducedType) throws DebugException {
        if (jdiProducedType != null) {
            IJavaFieldVariable fieldVariable = jdiProducedType.getField("declaration", true);
            if (fieldVariable != null) {
                IValue declValue = fieldVariable.getValue();
                if (declValue instanceof IJavaObject) {
                    return (IJavaObject) declValue;
                }
            }
        }
        return null;
    }

    public static IJavaObject getJdiDeclaration(IValue value) throws DebugException {
        IJavaObject jdiProducedType = getJdiProducedType(value);
        return toJdiDeclaration(jdiProducedType);
    }
    
    
    public static boolean isCeylonFrame(IJavaStackFrame frame) {
        try {
            if (frame.getSourceName() != null
                    && frame.getSourceName().endsWith(".ceylon")) {
                return true;
            }
        } catch (DebugException e) {
            e.printStackTrace();
        }
        return false;
    }

    private final static String ABSTRACT_CALLABLE = AbstractCallable.class.getName();
    private final static String LAZY_ITERABLE = LazyIterable.class.getName();
    private final static String CEYLON_BOOLEAN = ceylon.language.Boolean.class.getName();
    private final static String CEYLON_FALSE = ceylon.language.true_.class.getName();
    private final static String CEYLON_TRUE = ceylon.language.false_.class.getName();
    private final static String CEYLON_FLOAT = ceylon.language.Float.class.getName();
    private final static String CEYLON_INTEGER = ceylon.language.Integer.class.getName();
    private final static String CEYLON_BYTE = ceylon.language.Byte.class.getName();
    private final static String CEYLON_STRING = ceylon.language.String.class.getName();
    private final static String CEYLON_BASE_PACKAGE = "ceylon.language.impl.Base";
    private final static String TYPE_DESCRIPTOR = TypeDescriptor.class.getName();
    
    public static boolean isInternalCeylonMethod(Method method) {
        return isJavaGeneratedMethodToStepThrough(method) || 
                isCeylonGeneratedMethodToStepThrough(method) || 
                isCeylonGeneratedMethodToSkipCompletely(method);
    }

    public static boolean isCeylonGeneratedMethodToStepThrough(Method method) {
        Location location = method.location();
        ReferenceType declaringType = location.declaringType();
        final String methodName = method.name();

        if (declaringType.name().startsWith(CEYLON_BASE_PACKAGE) && ! method.isConstructor()) {
            return true;
        }

        if (methodName.equals(Naming.Unfix.$evaluate$.name())) {
            if (declaringType instanceof ClassType) {
                ClassType classType = (ClassType) declaringType;
                String superClassName = classType.superclass().name();
                if (LAZY_ITERABLE.equals(superClassName)) {
                    return true;
                }
            }
        }

        if (methodName.equals(Naming.Unfix.$call$.name())
                || methodName.equals(Naming.Unfix.$callvariadic$.name()) 
                || methodName.equals(Naming.Unfix.$calltyped$.name())) {
            if (declaringType instanceof ClassType) {
                ClassType classType = (ClassType) declaringType;
                String superClassName = classType.superclass().name();
                if (ABSTRACT_CALLABLE.equals(superClassName)) {
                    if (method.isSynthetic()) {
                        // some synthetic methods are generated by Javac apparently
                        return true;
                    }
                    List<Method> methods = classType.methodsByName(Naming.Unfix.$calltyped$.name());
                    if (methods != null && ! methods.isEmpty()) {
                        if (methodName.equals(Naming.Unfix.$call$.name())
                                || methodName.equals(Naming.Unfix.$callvariadic$.name())) {
                            // they only delegate to $callTyped$
                            return true;
                        }
                    }
                }
            }
        }

        if (methodName.startsWith(Naming.Prefix.$default$.name())) {
            CeylonJDIDebugTarget debugTarget = getDebugTarget();
            return debugTarget != null 
                    && debugTarget.isStepFiltersEnabled() 
                    && debugTarget.isFiltersDefaultArgumentsCode();
        }

        boolean isDefaultArgumentMethod = false;
        String[] parts = methodName.split("\\$");
        if (parts.length == 2) {
            CeylonJDIDebugTarget debugTarget = getDebugTarget();
            if(debugTarget != null 
                    && debugTarget.isStepFiltersEnabled() 
                    && debugTarget.isFiltersDefaultArgumentsCode()) {
                List<Method> methodsWithTheSameName = method.declaringType().methodsByName(parts[0]);
                if (methodsWithTheSameName != null) {
                    label:
                    for (Method m : methodsWithTheSameName) {
                        try {
                            for (LocalVariable arg : m.arguments()) {
                                if (parts[1].equals(arg.name())) {
                                    isDefaultArgumentMethod = true;
                                    break label;
                                }
                            }
                        } catch (AbsentInformationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            List<Method> defaultArgumentMethods = new ArrayList<>();
            for (Method m : method.declaringType().methods()) {
                if (m.name().startsWith(parts[0] + "$")) {
                    defaultArgumentMethods.add(m);
                }
            }
            if (! defaultArgumentMethods.isEmpty()) {
                ArrayList<String> defaultArguments = new ArrayList<>(defaultArgumentMethods.size());
                for (Method defaultArgumentMethod : defaultArgumentMethods) {
                    String argumentName = defaultArgumentMethod.name().substring(parts[0].length() + 1);
                    if (! argumentName.isEmpty()) {
                        defaultArguments.add(argumentName);
                    }
                }
                
                List<Method> overloadedMethods = method.declaringType().methodsByName(parts[0]);
                Method methodWithAllArguments = null;
                if (overloadedMethods.size() > 1) {
                    for (Method overloadedMethod : overloadedMethods) {
                        if (overloadedMethod.equals(method)) {
                            continue;
                        }
                        
                        try {
                            List<LocalVariable> arguments = overloadedMethod.arguments();
                            if (arguments.size() < defaultArguments.size()) {
                                continue;
                            }
                            List<String> argumentNames = new ArrayList<>(arguments.size());
                            for (LocalVariable arg : arguments) {
                                argumentNames.add(arg.name());
                            }
                            
                            if (! argumentNames.containsAll(defaultArguments)) {
                                continue;
                            }
                            methodWithAllArguments = overloadedMethod;
                            break;
                        } catch (AbsentInformationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (methodWithAllArguments != null) {
                    isDefaultArgumentMethod = true;
                }
            }
        }
        if (isDefaultArgumentMethod) {
            return true;
        }

        return false;
    }

    public static boolean isJavaGeneratedMethodToStepThrough(Method method) {
        if (method.isSynthetic()) {
            if (method.name().startsWith("access$")) {
                return true;
            }
        }
        
        if (method.isBridge()) {
            return true;
        }

        return false;
    }

    public static boolean isMethodToStepThrough(Method method) {
        return isJavaGeneratedMethodToStepThrough(method) || 
                isCeylonGeneratedMethodToStepThrough(method);
    }
    
    public static boolean isCeylonGeneratedMethodToSkipCompletely(Method method) {
        Location location = method.location();
        ReferenceType declaringType = location.declaringType();
        String declaringTypeName = declaringType.name();
        final String methodName = method.name();

        if (method.isStaticInitializer()) {
            try {
                if (location.sourceName() != null
                        && ! location.sourceName().endsWith(".ceylon")) {
                    return false;
                }
            } catch (AbsentInformationException e1) {
                e1.printStackTrace();
                return false;
            }

            if (! declaringTypeName.endsWith("_")) {
                return true;
            }
            if (! (declaringType instanceof ClassType)) {
                return true;
            }

            try {
                List<Location> locations = method.allLineLocations();
                return locations.size() <= 1;
            } catch (AbsentInformationException e) {
            }
            return false;
        }
        
        if (declaringTypeName.equals(CEYLON_BOOLEAN) ||
                declaringTypeName.equals(CEYLON_FALSE) ||
                declaringTypeName.equals(CEYLON_TRUE)) {
            return true;
        }
        if (declaringTypeName.equals(CEYLON_INTEGER)
                && (methodName.equals("instance")
                        || methodName.equals("longValue") || method
                            .isConstructor())) {
            return true;
        }
        if (declaringTypeName.equals(CEYLON_FLOAT)
                && (methodName.equals("instance")
                        || methodName.equals("doubleValue") || method
                            .isConstructor())) {
            return true;
        }
        if (declaringTypeName.equals(CEYLON_BYTE)
                && (methodName.equals("instance")
                        || methodName.equals("byteValue") || method
                            .isConstructor())) {
            return true;
        }
        if (declaringTypeName.equals(CEYLON_STRING)
                && (methodName.equals("instance")
                        || methodName.equals("toString") || (method
                        .isConstructor()
                        && method.argumentTypeNames().size() == 1 && "java.lang.String"
                            .equals(method.argumentTypeNames().get(0))))) {
            return true;
        }

        if (declaringTypeName.equals(ABSTRACT_CALLABLE)) {
            return true;
        }

        if (declaringType.name().startsWith(CEYLON_BASE_PACKAGE) && method.isConstructor()) {
            return true;
        }

        if (declaringTypeName
                .startsWith(TYPE_DESCRIPTOR)) {
            return true;
        }

        if (method.isConstructor()) {
            if (declaringTypeName.endsWith("$impl")) {
                return true;
            } else if (declaringType instanceof ClassType) {
                ClassType superClassType = ((ClassType) declaringType).superclass();
                if (superClassType != null) {
                    if (ABSTRACT_CALLABLE.equals(superClassType.name())) {
                        return true;
                    } else if (LAZY_ITERABLE.equals(superClassType.name())) {
                        return true;
                    }
                }
            }
         }

        if ((methodName.startsWith("get") ||
                methodName.startsWith("set"))
                && methodName.endsWith(Suffix.$priv$.name())) {
            if (declaringType instanceof ClassType) {
                ClassType classType = (ClassType) declaringType;
                String fieldName = methodName.substring(3, methodName.length() - Suffix.$priv$.name().length());
                fieldName = Escaping.toInitialLowercase(fieldName);

                Field field = classType.fieldByName(fieldName);
                if (field != null) {
                    // we are stepping in a getter that simply returns the raw field value.
                    // Don't step then (or don't stop on breakpoints)
                    return true;
                }
                // if no such field is found, it means that it is a Ceylon getter or a lazy specifier, then take 
                // the location in account in step and breakpoint requests.
            }
        }

        if (methodName.equals("$getType$")) {
            return true;
        }

        if (methodName.equals("$getReifiedElement$")) {
            return true;
        }

        try {
            if (method.isStatic()
                    && methodName.equals("get_")
                    && method.argumentTypeNames().isEmpty()
                    && method.returnType().name()
                            .equals(method.declaringType().name())) {
                return true;
            }
        } catch (ClassNotLoadedException e) {}
        

        return false;
    }

    public static boolean isJavaSystemMethodToSkip(Method method) {
        Location location = method.location();
        ReferenceType declaringType = location.declaringType();
        String declaringTypeName = declaringType.name();

        final String methodName = method.name();
        if (declaringTypeName.equals("java.lang.ClassLoader")) {
            return true;
        }
        
        if (declaringTypeName.equals("java.lang.System") &&
                methodName.equals("getSecurityManager")) {
            return true;
        }
        
        if (declaringTypeName.equals("java.lang.Object") 
                && method.isConstructor()) {
            return true;
        }
        return false;
    }
    
    public static boolean isMethodFiltered(Method method) {
        return isCeylonGeneratedMethodToSkipCompletely(method) ||
                isCeylonGeneratedMethodToStepThrough(method) ||
                isJavaGeneratedMethodToStepThrough(method) ||
                isJavaSystemMethodToSkip(method);
    }

    /**
     * Returns the stack frame in which to search for variables, or <code>null</code>
     * if none.
     * 
     * @return the stack frame in which to search for variables, or <code>null</code>
     * if none
     */
    public static JDIStackFrame getFrame() {
        IAdaptable adaptable = DebugUITools.getDebugContext();
        if (adaptable != null) {
            IJavaStackFrame stackFrame = (IJavaStackFrame)adaptable.getAdapter(IJavaStackFrame.class);
            if (stackFrame instanceof JDIStackFrame) {
                return (JDIStackFrame) stackFrame;
            }
        }
        return null;
    }

    public static CeylonJDIDebugTarget getDebugTarget() {
        JDIStackFrame stackFrame = getFrame();
        if (stackFrame != null) {
            JDIDebugTarget debugTarget = stackFrame.getJavaDebugTarget();
            if (debugTarget instanceof CeylonJDIDebugTarget) {
                return (CeylonJDIDebugTarget) debugTarget;
            }
        }
        return null;
    }
}
