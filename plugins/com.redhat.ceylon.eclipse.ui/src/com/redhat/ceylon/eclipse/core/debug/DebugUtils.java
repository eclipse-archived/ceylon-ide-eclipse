package com.redhat.ceylon.eclipse.core.debug;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.util.JavaSearch;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;

public class DebugUtils {

    static IProject getProject(IDebugElement debugElement) {
        IDebugTarget target = debugElement.getDebugTarget();
        if (target instanceof CeylonJDIDebugTarget) {
            return ((CeylonJDIDebugTarget) target).getProject();
        }
        return null;
    }

    public static IMethod getStackFrameMethod(IJavaStackFrame frame,
            IProject project) {
        IJavaProject javaProject = JavaCore.create(project);
        try {
            IType declaringType = javaProject.findType(frame.getReferenceType()
                    .getName());
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

    public static IMethod getStackFrameMethod(IJavaStackFrame frame) {
        IProject project = getProject(frame);
        return project == null ? null : getStackFrameMethod(frame, project);
    }

    public static PhasedUnit getStackFramePhasedUnit(IJavaStackFrame frame,
            IProject project) {
        try {
            PhasedUnits projectPhasedUnits = CeylonBuilder
                    .getProjectPhasedUnits(project);
            if (projectPhasedUnits != null) {
                PhasedUnit phasedUnit = null;
                phasedUnit = projectPhasedUnits
                        .getPhasedUnitFromRelativePath(frame.getSourcePath());
                if (phasedUnit != null) {
                    return phasedUnit;
                }
            }
            for (Module module : CeylonBuilder
                    .getProjectExternalModules(project)) {
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

    public static PhasedUnit getStackFramePhasedUnit(IJavaStackFrame frame) {
        IProject project = getProject(frame);
        return project == null ? null : getStackFramePhasedUnit(frame, project);
    }

    public static Declaration getStackFrameCeylonDeclaration(
            IJavaStackFrame frame) {
        IProject project = getProject(frame);
        if (project == null) {
            // Not a Ceylon debug target
            return null;
        }
        IMethod method = getStackFrameMethod(frame, project);
        PhasedUnit unit = getStackFramePhasedUnit(frame, project);
        if (method != null && unit != null) {
            return JavaSearch.toCeylonDeclaration(method, Arrays.asList(unit));
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
        String declaringTypeName = location.declaringType().name();
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

        if (declaringTypeName.endsWith("$impl") && method.isConstructor()) {
            return true;
        }

        if (method.name().equals("$getType$")) {
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
}
