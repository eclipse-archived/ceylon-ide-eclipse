package com.redhat.ceylon.eclipse.core.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.runtime.AdapterManager;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementMementoProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxyFactory;
import org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapter;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.ui.display.JavaInspectExpression;
import org.eclipse.jdt.internal.debug.ui.monitors.MonitorsAdapterFactory;
import org.eclipse.jdt.internal.debug.ui.variables.JavaDebugElementAdapterFactory;

public class CeylonDebugElementAdapterFactory implements IAdapterFactory {
    private static final CeylonDebugElementAdapterFactory ceylonDebugAdapterFactory = new CeylonDebugElementAdapterFactory();
    private static final TargetAdapterFactory ceylonDebugTargetAdapterFactory = new TargetAdapterFactory();
    private static final JavaDebugElementAdapterFactory javaDebugElementAdapterFactory = new JavaDebugElementAdapterFactory();

    private static final IElementLabelProvider fgLPVariable = new CeylonVariableLabelProvider();
    private static final IElementContentProvider fgCPVariable = new CeylonVariableContentProvider();
    private static final IElementLabelProvider fgLPExpression = new CeylonExpressionLabelProvider();
    private static final IElementContentProvider fgCPExpression = new CeylonExpressionContentProvider();
    private static final IWatchExpressionFactoryAdapter fgWEVariable = new CeylonWatchExpressionFilter();
    private static final IElementLabelProvider fgLPFrame = new CeylonStackFrameLabelProvider();

    private static final IElementContentProvider fgCPTarget = new CeylonDebugTargetContentProvider();
    private static final IModelProxyFactory fgCeylonModelProxyFactory = new CeylonModelProxyFactory();
    
    private static final IElementContentProvider ceylonStackFrameContentProvider = new CeylonStackFrameContentProvider();
    
    public static class TargetAdapterFactory implements IAdapterFactory {
        @Override
        public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
            if (adapterType.equals(IModelProxyFactory.class)) {
                if (adaptableObject instanceof CeylonJDIDebugTarget) {
                    return fgCeylonModelProxyFactory;
                }
            }
            if (adapterType.equals(IElementContentProvider.class)) {
                if (adaptableObject instanceof CeylonJDIDebugTarget) {
                    return fgCPTarget;
                }
            }
            return null;
        }
        
        @SuppressWarnings("rawtypes")
        @Override
        public Class[] getAdapterList() {
            return new Class[]{
                    IModelProxyFactory.class,
                    IElementContentProvider.class};
        }
    }

    @Override
    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
        if (IElementLabelProvider.class.equals(adapterType)) {
            if (adaptableObject instanceof IJavaVariable) {
                return fgLPVariable; 
            }
            if (adaptableObject instanceof IJavaStackFrame) {
                return fgLPFrame;
            }
            if (adaptableObject instanceof JavaInspectExpression) {
                return fgLPExpression;
            }
        }
        if (IElementContentProvider.class.equals(adapterType)) {
            if (adaptableObject instanceof IJavaVariable) {
                return fgCPVariable;
            }
            if (adaptableObject instanceof JavaInspectExpression) {
                return fgCPExpression;
            }
            if (adaptableObject instanceof IJavaValue) {
                return fgCPExpression;
            }
            if (adaptableObject instanceof IJavaStackFrame) {
                return ceylonStackFrameContentProvider;
            }
        }
        if (IWatchExpressionFactoryAdapter.class.equals(adapterType)) {
            if (adaptableObject instanceof IJavaVariable) {
                return fgWEVariable;
            }
            if (adaptableObject instanceof JavaInspectExpression) {
                return fgWEVariable;
            }
        }
        
        return javaDebugElementAdapterFactory.getAdapter(adaptableObject, adapterType);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList() {
        return new Class[] {IElementLabelProvider.class, IElementContentProvider.class, IWatchExpressionFactoryAdapter.class, IElementMementoProvider.class};
    }
    
    private static final Map<Class<? extends IAdaptable>, List<IAdapterFactory>> replacedAdapters = new HashMap<Class<? extends IAdaptable>, List<IAdapterFactory>>();
    
    private static final Class<? extends IAdaptable> stackFrameAdaptableClass = IJavaStackFrame.class;
    private static final Class<? extends IAdaptable> ceylonDebugTargetAdaptableClass = CeylonJDIDebugTarget.class;
    private static final Class<? extends IAdaptable> variableAdaptableClass = IJavaVariable.class;
    private static final Class<? extends IAdaptable> valueAdaptableClass = IJavaValue.class;
    private static final Class<? extends IAdaptable> inspectExpressionAdaptableClass = JavaInspectExpression.class;
    private static final List<Class<? extends IAdaptable>> adaptablesToOverride = Arrays.asList(stackFrameAdaptableClass, variableAdaptableClass, valueAdaptableClass, inspectExpressionAdaptableClass);
            
    public static synchronized void installCeylonDebugElementAdapters() {
        try {
            AdapterManager adapterManager = (AdapterManager) Platform.getAdapterManager();
            
            if (! replacedAdapters.isEmpty()) {
                System.err.println("WARNING : Ceylon Debug Adapters already installed. Cancelling.");
                return;
            }

            for (Class<? extends IAdaptable> adaptableClass : adaptablesToOverride) {
                String adaptableName = adaptableClass.getName();
                @SuppressWarnings("unchecked")
                List<IAdapterFactory> factories = (List<IAdapterFactory>) adapterManager.getFactories()
                                                        .get(adaptableName);
                ArrayList<IAdapterFactory> removedFactories = new ArrayList<>();
                replacedAdapters.put(adaptableClass, removedFactories);
                for (Iterator<IAdapterFactory> iterator = factories.iterator(); iterator.hasNext();) {
                    IAdapterFactory factory = iterator.next();

                    boolean remove = false;
                    if (adaptableClass == stackFrameAdaptableClass) {
                        // Remove the IStackFrame JDT adapters added in the JDT Debug UI plugin.xml (
                        //   => for IElementLabelProvider and IElementMementoProvider )
                        if (factory.getClass().getName().equals("org.eclipse.core.internal.adapter.AdapterFactoryProxy")) {
                            remove = true;
                        }

                        // Remove the other IStackFrame JDT adapters
                        if (factory instanceof MonitorsAdapterFactory) {
                            remove = true;
                        }
                    } else {
                        if (factory instanceof JavaDebugElementAdapterFactory) {
                            remove = true;
                        }
                    }
                    
                    if (remove) {
                        iterator.remove();
                        removedFactories.add(factory);
                    }
                }
            }
            
            // Add the provided adapters
            for (Class<? extends IAdaptable> adaptableClass : adaptablesToOverride) {
                adapterManager.registerAdapters(ceylonDebugAdapterFactory, adaptableClass);
            }
            adapterManager.registerAdapters(ceylonDebugTargetAdapterFactory, ceylonDebugTargetAdaptableClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized void restoreJDTDebugElementAdapters() {
        try {
            AdapterManager adapterManager = (AdapterManager) Platform.getAdapterManager();

            for (Class<? extends IAdaptable> adaptableClass : adaptablesToOverride) {
                adapterManager.unregisterAdapters(ceylonDebugAdapterFactory, adaptableClass);
            }
            adapterManager.unregisterAdapters(ceylonDebugTargetAdapterFactory, ceylonDebugTargetAdaptableClass);

            for (Class<? extends IAdaptable> adaptableClass : adaptablesToOverride) {
                List<IAdapterFactory> factoriesToRestore = replacedAdapters.get(adaptableClass);
                if (factoriesToRestore != null) {
                    for (IAdapterFactory factoryToRestore : factoriesToRestore) {
                        adapterManager.registerAdapters(factoryToRestore, adaptableClass);
                    }
                }
            }
            replacedAdapters.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized boolean areCeylonDebugElementAdaptersInstalled() {
        return ! replacedAdapters.isEmpty();
    }
}
