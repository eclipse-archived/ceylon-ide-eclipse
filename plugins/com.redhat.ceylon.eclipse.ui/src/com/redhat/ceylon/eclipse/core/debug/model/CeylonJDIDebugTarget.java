package com.redhat.ceylon.eclipse.core.debug.model;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.core.IEvaluationRunnable;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaClassObject;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaMethodBreakpoint;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaThreadGroup;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDINullValue;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.redhat.ceylon.eclipse.core.launch.LaunchHelper;
import com.redhat.ceylon.launcher.CeylonDebugEvaluationThread;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class CeylonJDIDebugTarget extends JDIDebugTarget {
    
    private IProject project = null;
    private String[] ceylonStepFilters;
    private boolean stepFiltersEnabled;
    private boolean filterDefaultArgumentsCode;
    
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
            startLocation = LaunchHelper.getStartLocation(config);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean isStepThruFilters() {
        return false;
    }
    
    private static IJavaBreakpoint ceylonDebugEvaluationBreakpoint = null;
            
    @Override
    protected synchronized void initialize() {
        super.initialize();

        if (ceylonDebugEvaluationBreakpoint == null) {
            try {
                Map<String, Object> map = new HashMap<String, Object>();
                IJavaMethodBreakpoint bp = JDIDebugModel
                        .createMethodBreakpoint(
                                ResourcesPlugin
                                        .getWorkspace()
                                        .getRoot(),
                                CeylonDebugEvaluationThread.name, 
                                CeylonDebugEvaluationThread.methodForBreakpoint,
                                "()V",
                                true, false, false, -1, -1,
                                -1, 0, false, map);
                bp.setPersisted(false);
                ceylonDebugEvaluationBreakpoint = bp;
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        breakpointAdded(ceylonDebugEvaluationBreakpoint);
    }

    private IJavaObject mainThread = null;
    private String startLocation = null;
    
    public String getStartLocation() {
        return startLocation;
    }

    public boolean isMainThread(IThread thread) {
        if (! (thread instanceof JDIThread)) {
            return false;
        }
        if (ceylonEvaluationThread == null) {
            return false;
        }
        if (mainThread == null) {
            return false;
        }
        JDIThread javaThread = (JDIThread) thread;
        try {
            return mainThread != null && mainThread.equals(javaThread.getThreadObject());
        } catch (DebugException e) {
            e.printStackTrace();
            return false;
        }
    }

    private IJavaThread ceylonEvaluationThread = null;
    private boolean hasEvaluationThreadGroup = false;
    
    public boolean hasCeylonEvaluationThread() {
        return ceylonEvaluationThread != null;
    }
    
    public boolean hasCeylonEvaluationThreadGroup() {
        return hasEvaluationThreadGroup;
    }
    
    @Override
    protected JDIThread newThread(ThreadReference reference) {
        try {
            JDIThread newThread = new CeylonJDIThread(this, reference);
            try {
                if (CeylonDebugEvaluationThread.name.equals(reference.name())) {
                    ceylonEvaluationThread = newThread;
                    for (IJavaThreadGroup threadGroup : getRootThreadGroups()) {
                        if (CeylonDebugEvaluationThread.name.equals(threadGroup.getName())) {
                            hasEvaluationThreadGroup = true;
                            break;
                        }
                    }
                    IJavaObject evalThreadObject = ceylonEvaluationThread.getThreadObject();
                    IJavaFieldVariable mainThreadRefVariable = evalThreadObject.getField(CeylonDebugEvaluationThread.mainThreadRefFieldName, false);
                    IJavaObject mainThreadRefValue = (IJavaObject)mainThreadRefVariable.getValue();
                    if (mainThreadRefValue != null) {
                        IJavaFieldVariable mainThreadVariable = mainThreadRefValue.getField("referent", true);
                        IJavaObject mainThreadValue = (IJavaObject) mainThreadVariable.getValue();
                        if (mainThreadValue != null) {
                            mainThread = mainThreadValue;
                        }
                    }
                }
            } catch(com.sun.jdi.VMDisconnectedException | DebugException e) {
                e.printStackTrace();
            }
            return newThread;
        } catch (ObjectCollectedException exception) {
            // ObjectCollectionException can be thrown if the thread has already
            // completed (exited) in the VM.
        }
        return null;
    }

    public IProject getProject() {
        return project;
    }
    
    private Cache jdiAnnotationParameterSignatureCache = new Cache(10);
    private static final Object NULL_ENTRY = new Object();
    
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

    
    private static final String getAnnotationsMethodSignature = "()[Ljava/lang/annotation/Annotation;";
    private static final String getAnnotationsMethodName = "getAnnotations";
    
    public static class EvaluationWaiter implements EvaluationListener { 
        boolean finished = false;
        IJavaValue result = null;

        synchronized public void finished(
                IJavaValue annotation) {
            this.result = annotation;
            finished = true;
            notifyAll();
        }
        
        synchronized public IJavaValue waitForResult(long timeout) {
            if (!finished) {
                try {
                    wait(timeout);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    // Fall through
                }
            }
            return result;
        }
    };

    public static interface EvaluationListener {
        void finished(IJavaValue annotation);
    };
    
    public interface EvaluationRunner {
        void run(
                IJavaThread innerThread,
                IProgressMonitor monitor,
                EvaluationListener listener) throws DebugException;
    }
    
    public IJavaValue getEvaluationResult(final EvaluationRunner runner, long timeout) {
        EvaluationWaiter listener = new EvaluationWaiter();
        evaluate(runner, listener);
        return jdiNullValueToNull(listener.waitForResult(timeout));
    }
    
    public void evaluate(final EvaluationRunner runner, final EvaluationListener listener) {
        if (listener == null) {
            return;
        }
        final IJavaThread evaluationThread = ceylonEvaluationThread;
        if (evaluationThread == null) {
            listener.finished(null);
        } else {
            Runnable runnable = new Runnable() {
                public void run() {
                    if (evaluationThread == null || !evaluationThread.isSuspended()) {
                        System.err.println("Evaluation cancelled : thread is not suspended");
                        listener.finished(null);
                        return;
                    }
                    
                    IEvaluationRunnable eval = new IEvaluationRunnable() {
                        
                        public void run(
                                IJavaThread innerThread,
                                IProgressMonitor monitor)
                                throws DebugException {
                            try {
                                runner.run(innerThread, monitor, listener);
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
            };
            if (listener != null) {
                evaluationThread.queueRunnable(runnable);
            }
        }
    }

    private IJavaValue jdiNullValueToNull(IJavaValue value) {
        if (value instanceof JDINullValue) {
            return null;
        }
        return value;
    }
    
    public IJavaValue getAnnotation(IJavaReferenceType valueType, Class<? extends Annotation> annotationClass, String parameter, long timeout) {
        EvaluationWaiter listener = new EvaluationWaiter();
        evaluateAnnotation(valueType, annotationClass, parameter, listener);
        return jdiNullValueToNull(listener.waitForResult(timeout));
    }
    
    public void evaluateAnnotation(IJavaReferenceType valueType, final Class<? extends Annotation> annotationClass, final String parameter, final EvaluationListener listener) {
        
        try {
            final IJavaClassObject theClassObject = valueType.getClassObject();

            EvaluationRunner runner = new EvaluationRunner() {
                public void run(
                        IJavaThread innerThread,
                        IProgressMonitor monitor,
                        EvaluationListener listener) throws DebugException {
                    IJavaValue result = null;
                    IJavaValue annotations = theClassObject.sendMessage(
                            getAnnotationsMethodName,
                            getAnnotationsMethodSignature,
                            new IJavaValue[0],
                            innerThread,
                            (String) null);
                    if (annotations instanceof IJavaArray) {
                        IJavaValue[] annotationValues = ((IJavaArray)annotations).getValues();
                        for (IJavaValue annotationValue : annotationValues) {
                            IJavaValue annotationClassValue = ((IJavaObject)annotationValue).sendMessage("annotationType", "()Ljava/lang/Class;", new IJavaValue[0], innerThread, null);
                            if (annotationClassValue instanceof IJavaObject && ! (annotationClassValue instanceof JDINullValue)) {
                                IJavaValue annotationClassName = ((IJavaObject)annotationClassValue).sendMessage("getName", "()Ljava/lang/String;", new IJavaValue[0], innerThread, null);
                                if (annotationClassName != null && annotationClassName.getValueString().equals(annotationClass.getName())) {
                                    result = annotationValue;
                                    break;
                                }
                            }
                        }
                    }
                    if (parameter == null) {
                        listener.finished(result);
                    } else {
                        if (result instanceof JDINullValue) {
                            listener.finished(null);
                        } else {
                            IJavaObject annotationObject = (IJavaObject)result;
                            result = null;
                            String annotationParameterMethodSignature = getAnnotationParameterSignature(annotationClass, parameter);
                            if (annotationParameterMethodSignature != null) {
                                result = ((IJavaObject) annotationObject).sendMessage(
                                        parameter,
                                        annotationParameterMethodSignature,
                                        new IJavaValue[] { },
                                        innerThread,
                                        (String) null);
                            }
                            listener.finished(result);
                        }
                    }
                }
            };
            
            evaluate(runner, listener);
        } catch (DebugException e) {
            e.printStackTrace();
        }
    }

    private boolean isAnnotationPresent(IJavaValue annotation) {
        return annotation instanceof JDIObjectValue && 
                !(annotation instanceof JDINullValue);
    }
    
    public boolean isAnnotationPresent(IJavaReferenceType valueType, Class<? extends Annotation> annotationClass, long timeout) {
        EvaluationWaiter listener = new EvaluationWaiter();
        evaluateAnnotation(valueType, annotationClass, null, listener);
        return isAnnotationPresent(listener.waitForResult(timeout));
    }
    
    public void evaluateAnnotationPresent(IJavaReferenceType valueType, final Class<? extends Annotation> annotationClass, final EvaluationListener listener) {
        EvaluationListener internalListener = null;
        internalListener = new EvaluationListener() {
            @Override
            public void finished(IJavaValue annotation) {
                if (isAnnotationPresent(annotation)) {
                    IDebugTarget debugTarget = annotation.getDebugTarget();
                    if (! (debugTarget instanceof JDIDebugTarget)) {
                        System.err.println("This should be a JDIDebugTarget !!!");
                        listener.finished(null);
                        return;
                    }
                    listener.finished(((JDIDebugTarget) debugTarget).newValue(true));
                } else {
                    listener.finished(null);
                }
            }
            
        };
        evaluateAnnotation(valueType, annotationClass, null, internalListener);
    }

    public void setCeylonStepFilters(String[] list) {
        ceylonStepFilters = list;
    }
    
    @Override
    public String[] getStepFilters() {
        return ceylonStepFilters;
    }
    
    public void setFiltersDefaultArgumentsCode(boolean enabled) {
        filterDefaultArgumentsCode = enabled;
    }
    
    public boolean isFiltersDefaultArgumentsCode() {
        return filterDefaultArgumentsCode;
    }
    
    public void setCeylonStepFiltersEnabled(boolean enabled) {
        stepFiltersEnabled = enabled;
    }

    @Override
    public boolean isStepFiltersEnabled() {
        return stepFiltersEnabled;
    }
}