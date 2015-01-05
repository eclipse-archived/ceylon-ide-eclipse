package com.redhat.ceylon.eclipse.core.debug;

import java.util.Arrays;

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
import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import com.redhat.ceylon.compiler.java.language.LazyIterable;
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

    public static boolean isInternalCeylonMethod(Method method) {
        Location location = method.location();
        ReferenceType declaringType = location.declaringType();
        String declaringTypeName = declaringType.name();
        if (declaringTypeName.equals("ceylon.language.Boolean")) {
            return true;
        }
        if (declaringTypeName.equals("ceylon.language.Integer")
                && (method.name().equals("instance")
                        || method.name().equals("longValue") || method
                            .isConstructor())) {
            return true;
        }
        if (declaringTypeName.equals("ceylon.language.Float")
                && (method.name().equals("instance")
                        || method.name().equals("doubleValue") || method
                            .isConstructor())) {
            return true;
        }
        if (declaringTypeName.equals("ceylon.language.Byte")
                && (method.name().equals("instance")
                        || method.name().equals("byteValue") || method
                            .isConstructor())) {
            return true;
        }
        if (declaringTypeName.equals("ceylon.language.String")
                && (method.name().equals("instance")
                        || method.name().equals("toString") || (method
                        .isConstructor()
                        && method.argumentTypeNames().size() == 1 && "java.lang.String"
                            .equals(method.argumentTypeNames().get(0))))) {
            return true;
        }

        if (declaringTypeName.startsWith("ceylon.language.impl.Base")) {
            return true;
        }

        if (declaringTypeName
                .startsWith("com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor")) {
            return true;
        }

        if (method.isConstructor()) {
            if (declaringTypeName.endsWith("$impl")) {
                return true;
            } else if (declaringType instanceof ClassType) {
                ClassType classType = (ClassType) declaringType;
                String superClassName = classType.superclass().name();
                if (AbstractCallable.class.getName().equals(superClassName)) {
                    return true;
                }
            }
         }

        if (method.name().equals(Naming.Unfix.$evaluate$.toString())) {
            if (declaringType instanceof ClassType) {
                ClassType classType = (ClassType) declaringType;
                String superClassName = classType.superclass().name();
                if (LazyIterable.class.getName().equals(superClassName)) {
                    return true;
                }
            }
        }

        if (method.name().equals("$getType$")) {
            return true;
        }

        if (method.name().equals("$getReifiedElement$")) {
            return true;
        }

        if (method.name().equals("$call$") || method.name().equals("$callvariadic$")) {
            return true;
        }

        try {
            if (method.isStatic()
                    && method.name().equals("get_")
                    && method.argumentTypeNames().isEmpty()
                    && method.returnType().name()
                            .equals(method.declaringType().name())) {
                return true;
            }
        } catch (ClassNotLoadedException e) {}

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
