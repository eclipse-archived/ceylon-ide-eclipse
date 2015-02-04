package com.redhat.ceylon.eclipse.core.debug;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

import com.redhat.ceylon.compiler.java.codegen.Naming;
import com.redhat.ceylon.compiler.java.codegen.Naming.Suffix;
import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import com.redhat.ceylon.compiler.java.language.LazyIterable;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.util.JavaSearch;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
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

    public static PhasedUnit getPhasedUnit(IJavaStackFrame frame,
            IJavaProject project) {
        try {
            PhasedUnits projectPhasedUnits = CeylonBuilder
                    .getProjectPhasedUnits(project.getProject());
            if (projectPhasedUnits != null) {
                PhasedUnit phasedUnit = null;
                phasedUnit = projectPhasedUnits
                        .getPhasedUnitFromRelativePath(frame.getSourcePath());
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
                                .getPhasedUnitFromRelativePath(frame
                                        .getSourcePath());
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
        } catch (DebugException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PhasedUnit getPhasedUnit(IJavaStackFrame frame) {
        IJavaProject project = getProject(frame);
        return project == null ? null : getPhasedUnit(frame, project);
    }

    public static Declaration getCeylonDeclaration(
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

    public static Declaration getCeylonDeclaration(
            IJavaObject object) {
        IJavaProject project = getProject(object);
        if (project == null) {
            // Not a Ceylon debug target
            return null;
        }
        IJavaStackFrame frame = getFrame();
        if (frame != null) {
            IType type = getJavaType(object, project);
            PhasedUnit unit = getPhasedUnit(frame, project);
            if (type != null && unit != null) {
                return JavaSearch.toCeylonDeclaration(type, Arrays.asList(unit));
            }
        }
        return null;
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
    private final static String CEYLON_FLOAT = ceylon.language.Float.class.getName();
    private final static String CEYLON_INTEGER = ceylon.language.Integer.class.getName();
    private final static String CEYLON_BYTE = ceylon.language.Byte.class.getName();
    private final static String CEYLON_STRING = ceylon.language.String.class.getName();
    private final static String CEYLON_BASE_PACKAGE = "ceylon.language.impl.Base";
    private final static String TYPE_DESCRIPTOR = TypeDescriptor.class.getName();
    
    public static boolean isInternalCeylonMethod(Method method) {
        Location location = method.location();
        ReferenceType declaringType = location.declaringType();
        String declaringTypeName = declaringType.name();
        if (declaringTypeName.equals(CEYLON_BOOLEAN)) {
            return true;
        }
        
        final String methodName = method.name();
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

        if (declaringTypeName.startsWith(CEYLON_BASE_PACKAGE)) {
            return true;
        }

        if (declaringTypeName.equals(ABSTRACT_CALLABLE)) {
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
                if (superClassType != null && ABSTRACT_CALLABLE.equals(superClassType.name())) {
                    return true;
                }
            }
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

        if ((methodName.startsWith("get") ||
                methodName.startsWith("set"))
                && methodName.endsWith(Suffix.$priv$.name())) {
            if (declaringType instanceof ClassType) {
                ClassType classType = (ClassType) declaringType;
                String fieldName = methodName.substring(3, methodName.length() - Suffix.$priv$.name().length());
                fieldName = Character.toLowerCase(fieldName.charAt(0)) + 
                        fieldName.substring(1);

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
    
    public static boolean isMethodFilteredForCeylon(Method method,
            CeylonJDIDebugTarget debugTarget) {
        return isInternalCeylonMethod(method);
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
