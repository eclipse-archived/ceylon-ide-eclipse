package com.redhat.ceylon.eclipse.core.debug;

import java.lang.annotation.Annotation;

import org.eclipse.core.internal.utils.Cache;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
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
    

    private Cache jdiAnnotationClassCache = new Cache(10);
    private Cache jdiAnnotationParameterSignatureCache = new Cache(10);
    private static final Object NULL_ENTRY = new Object();
    
    private IJavaClassObject getAnnotationClassJdiObject(
            final Class<? extends Annotation> annotationClass) throws DebugException {
        IJavaClassObject annotationClassObject = null;
        synchronized (jdiAnnotationClassCache) {
            Cache.Entry entry = jdiAnnotationClassCache.getEntry(annotationClass);
            if (entry == null) {
                IJavaType[] jts = getJavaTypes(annotationClass.getName());
                if (jts.length > 0) {
                    if (jts[0] instanceof IJavaReferenceType) {
                        annotationClassObject = ((IJavaReferenceType) jts[0])
                                .getClassObject();
                    }
                }
                if (annotationClassObject != null) {
                    jdiAnnotationClassCache.addEntry(annotationClass, annotationClassObject);
                } else {
                    jdiAnnotationClassCache.addEntry(annotationClass, NULL_ENTRY);
                }
            } else {
                Object cached = entry.getCached();
                annotationClassObject = cached ==  NULL_ENTRY ? null : (IJavaClassObject) cached;
            }
        }
        return annotationClassObject;
    }
    
    private String getAnnotationParameterSignature(
            final Class<? extends Annotation> annotationClass, String parameter) throws DebugException {
        String key = annotationClass.getName() + "/" + parameter;
        String annotationSignature = null;
        synchronized (jdiAnnotationParameterSignatureCache) {
            Cache.Entry entry = jdiAnnotationParameterSignatureCache.getEntry(key);
            if (entry == null) {
                try {
                    IJavaProject javaProject = JavaCore
                            .create(getProject());
                    IType annotationType = javaProject
                            .findType(annotationClass.getName());
                    IMethod method = annotationType.getMethod(parameter, new String[0]);
                    if (method != null) {
                        annotationSignature = method.getSignature();
                    }
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
                if (annotationSignature != null) {
                    jdiAnnotationParameterSignatureCache.addEntry(key, annotationSignature);
                } else {
                    jdiAnnotationParameterSignatureCache.addEntry(key, NULL_ENTRY);
                }
            } else {
                Object cached = entry.getCached();
                annotationSignature = cached ==  NULL_ENTRY ? null : (String) cached;
            }
        }
        return annotationSignature;
    }

    
    private static final String getAnnotationMethodSignature = "(Ljava/lang/Class;)Ljava/lang/annotation/Annotation;";
    private static final String getAnnotationMethodName = "getAnnotation";
    
    public static class EvaluationWaiter implements EvaluationListener { 
        boolean finished = false;
        IJavaValue annotation = null;

        synchronized public void finished(
                IJavaValue annotation) {
            this.annotation = annotation;
            finished = true;
            notifyAll();
        }
        
        synchronized IJavaValue waitForAnnotation(long timeout) {
            if (!finished) {
                try {
                    wait(timeout);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    // Fall through
                }
            }
            return annotation;
        }
    };

    public static interface EvaluationListener {
        void finished(IJavaValue annotation);
    };
    
    
    public IJavaValue getAnnotation(IJavaStackFrame frame, IJavaReferenceType valueType, final Class<? extends Annotation> annotationClass, final String parameter, final EvaluationListener listener) {
        
        IJavaClassObject annotationClassObject = null;
        
        try {
            annotationClassObject = getAnnotationClassJdiObject(
                    annotationClass);

            if (annotationClassObject != null) {
                final IJavaClassObject ceylonAnnotationClass = annotationClassObject;
                final IJavaClassObject theClassObject = valueType.getClassObject();
    
                final IJavaThread evaluationThread = JDIModelPresentation
                        .getEvaluationThread((IJavaDebugTarget) theClassObject
                                .getDebugTarget());
                if (evaluationThread == null) {
                    if (listener != null) {
                        listener.finished(null);
                    }
                } else {
                    final IJavaValue[] immediateResult = new IJavaValue[1];
                    Runnable runnable = new Runnable() {
                        private void finished(IJavaValue result) {
                            if (listener != null) {
                                listener.finished(result);
                            } else {
                                immediateResult[0] = result;
                            }
                        }
                        
                        public void run() {
                            if (evaluationThread == null || !evaluationThread.isSuspended()) {
                                System.err.println("Annotation retrieval cancelled : thread is not suspended");
                                finished(null);
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
                                            finished(result);
                                        } else {
                                            if (result instanceof JDINullValue) {
                                                finished(null);
                                            } else {
                                                IJavaObject annotationObject = (IJavaObject)result;
                                                result = null;
                                                String annotationParameterMethodSignature = getAnnotationParameterSignature(annotationClass, parameter);
                                                if (annotationParameterMethodSignature != null) {
                                                    result = ((IJavaObject) annotationObject).sendMessage(
                                                            parameter,
                                                            annotationParameterMethodSignature,
                                                            new IJavaValue[] { },
                                                            evaluationThread,
                                                            (String) null);
                                                }
                                                finished(result);
                                            }
                                        }
                                    } catch(Throwable t) {
                                        t.printStackTrace();
                                        finished(null);
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
                                finished(null);
                            }
                        }
                    };
                    if (listener != null) {
                        evaluationThread.queueRunnable(runnable);
                        return null;
                    } else {
                        runnable.run();
                        return immediateResult[0];
                    }
                }
                
            }
        } catch (DebugException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isAnnotationPresent(IJavaValue annotation) {
        return annotation instanceof JDIObjectValue && 
                !(annotation instanceof JDINullValue);
    }
    
    public boolean isAnnotationPresent(IJavaStackFrame frame, IJavaReferenceType valueType, final Class<? extends Annotation> annotationClass, final EvaluationListener listener) {
        EvaluationListener internalListener = null;
        if (listener != null) {
            internalListener = new EvaluationListener() {
                @Override
                public void finished(IJavaValue annotation) {
                    IDebugTarget debugTarget = annotation.getDebugTarget();
                    if (! (debugTarget instanceof JDIDebugTarget)) {
                        System.err.println("This should be a JDIDebugTarget !!!");
                        listener.finished(null);
                        return;
                    }
                    
                    if (isAnnotationPresent(annotation)) {
                        listener.finished(((JDIDebugTarget) debugTarget).newValue(true));
                    } else {
                        listener.finished(null);
                    }
                }
                
            };
        }
        IJavaValue annotation = getAnnotation(frame, valueType, annotationClass, null, internalListener);
        return isAnnotationPresent(annotation);
    }
}