package com.redhat.ceylon.eclipse.core.debug;

import java.lang.annotation.Annotation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IEvaluationRunnable;
import org.eclipse.jdt.debug.core.IJavaClassObject;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDINullValue;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;

import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class CeylonJDIDebugTarget extends JDIDebugTarget {
    private IProject project = null;
    
    public CeylonJDIDebugTarget(ILaunch launch, VirtualMachine jvm, String name,
            boolean supportTerminate, boolean supportDisconnect,
            IProcess process, boolean resume) {
        super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
        try {
            ILaunchConfiguration config = launch.getLaunchConfiguration();
            String projectName;
            projectName = config.getAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", "");
            if (projectName != null) {
                IProject theProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
                if (theProject.exists()) {
                    project = theProject;
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JDIThread newThread(ThreadReference reference) {
        try {
            return new CeylonJDIThread(this, reference);
        } catch (ObjectCollectedException exception) {
            // ObjectCollectionException can be thrown if the thread has already
            // completed (exited) in the VM.
        }
        return null;
    }

    public IProject getProject() {
        return project;
    }
    

    
    private static final String getAnnotationMethodSignature = "(Ljava/lang/Class;)Ljava/lang/annotation/Annotation;";
    private static final String getAnnotationMethodName = "getAnnotation";
    public IJavaValue getAnnotation(IJavaStackFrame frame, IJavaReferenceType valueType, final Class<? extends Annotation> annotationClass, final String parameter) {
        
        IJavaClassObject annotationClassObject = null;
        
        try {
            // TODO : The searched annotationClassObject should be cached inside the debugTarget
            // but the cache should be cleaned in case of hot code replace
            IJavaType[] jts = getJavaTypes(annotationClass.getName());
            if (jts.length > 0) {
                if (jts[0] instanceof IJavaReferenceType) {
                    annotationClassObject = ((IJavaReferenceType) jts[0])
                            .getClassObject();
                }
            }

            if (annotationClassObject != null) {
                final IJavaClassObject ceylonAnnotationClass = annotationClassObject;
                final IJavaClassObject theClassObject = valueType.getClassObject();
    
                class Listener {
                    boolean finished = false;
                    IJavaValue annotation = null;
    
                    synchronized public void finished(
                            IJavaValue annotation) {
                        this.annotation = annotation;
                        finished = true;
                        notifyAll();
                    }
                    
                    synchronized IJavaValue waitForAnnotation() {
                        if (!finished) {
                            try {
                                wait(5000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                                // Fall through
                            }
                        }
                        return annotation;
                    }
                };
                final Listener listener = new Listener();
                final IJavaThread evaluationThread = JDIModelPresentation
                        .getEvaluationThread((IJavaDebugTarget) theClassObject
                                .getDebugTarget());
                if (evaluationThread == null) {
                    listener.finished(null);
                } else {
                    evaluationThread
                            .queueRunnable(new Runnable() {
                                public void run() {
                                    if (evaluationThread == null || !evaluationThread.isSuspended()) {
                                        listener.finished(null);
                                        System.err.println("Annotation retrieval cancelled : thread is not suspended");
                                        return;
                                    }
                                    IEvaluationRunnable eval = new IEvaluationRunnable() {
                                        public void run(
                                                IJavaThread innerThread,
                                                IProgressMonitor monitor)
                                                throws DebugException {
                                            try {
                                                IJavaValue result = theClassObject.sendMessage(
                                                        getAnnotationMethodName,
                                                        getAnnotationMethodSignature,
                                                        new IJavaValue[] { ceylonAnnotationClass },
                                                        evaluationThread,
                                                        (String) null);
                                                if (parameter == null) {
                                                    listener.finished(result);
                                                } else {
                                                    if (result instanceof JDINullValue) {
                                                        listener.finished(null);
                                                    } else {
                                                        IJavaObject annotationObject = (IJavaObject)result;
                                                        result = null;
                                                        String annotationParameterMethodSignature = null;
                                                        //TODO : the signatures for the annotation parameters should
                                                        // be cached definitively inside the debug target.
                                                        try {
                                                            IJavaProject javaProject = JavaCore
                                                                    .create(getProject());
                                                            IType annotationType = javaProject
                                                                    .findType(annotationClass.getName());
                                                            IMethod method = annotationType.getMethod(parameter, new String[0]);
                                                            if (method != null) {
                                                                annotationParameterMethodSignature = method.getSignature();
                                                            }
                                                        } catch (JavaModelException e) {
                                                            e.printStackTrace();
                                                        }
                                                        if (annotationParameterMethodSignature != null) {
                                                            result = ((IJavaObject) annotationObject).sendMessage(
                                                                    parameter,
                                                                    annotationParameterMethodSignature,
                                                                    new IJavaValue[] { },
                                                                    evaluationThread,
                                                                    (String) null);
                                                        }
                                                        listener.finished(result);
                                                    }
                                                }
                                            } catch(Throwable t) {
                                                t.printStackTrace();
                                                listener.finished(null);
                                            }
                                        }
                                    };
                                    try {
                                        evaluationThread.runEvaluation(
                                                        eval,
                                                        null,
                                                        DebugEvent.EVALUATION_IMPLICIT,
                                                        false);
                                    } catch (DebugException e) {
                                        e.printStackTrace();
                                        listener.finished(null);
                                    }
                                }
                            });
                }
    
                return listener.waitForAnnotation();
            }
        } catch (DebugException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean isAnnotationPresent(IJavaStackFrame frame, IJavaReferenceType valueType, final Class<? extends Annotation> annotationClass) {
        IJavaValue annotation = getAnnotation(frame, valueType, annotationClass, null);
        return annotation instanceof JDIObjectValue && 
                !(annotation instanceof JDINullValue);
    }
}