package com.redhat.ceylon.eclipse.core.model;

import java.io.StringWriter;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.jgrapht.alg.CycleDetector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;

class CleaningRunnable implements Runnable {
    final ReferenceQueue<Module> removedModules = new ReferenceQueue<>();
    WeakReference<ModuleDependencies> dependenciesRef;
    
    CleaningRunnable(ModuleDependencies dependencies) {
        dependenciesRef = new WeakReference<ModuleDependencies>(dependencies);
    }

    @Override
    public void run() {
        while (true) {
            if (dependenciesRef.get() == null) {
                break;
            }
            
            try {
                Reference<? extends Module> moduleReference = removedModules.remove(10000);
                if (moduleReference != null) {
                    ModuleDependencies toClean = dependenciesRef.get();
                    if (toClean != null) {
                        synchronized (toClean) {
                            assert(moduleReference instanceof ModuleDependencies.ModuleWeakReference);
                            if (moduleReference instanceof ModuleDependencies.ModuleWeakReference) {
                                if (toClean.getExistingVertex((ModuleDependencies.ModuleWeakReference) moduleReference) == moduleReference) {
                                    toClean.dependencies.removeVertex((ModuleDependencies.ModuleReference) moduleReference);
                                } else {
                                    // A new ModuleReference has been added to the same module signature in the meantime. 
                                }
                            }
                        }
                    }
                }
            } catch (InterruptedException ignored) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored2) {}
            }
        }
    }
}

public class ModuleDependencies {
    public interface ModuleReference {
        String getIdentifier();
    }

    private static class ModuleStringReference implements ModuleReference {
        private String moduleIdentifier;
        
        ModuleStringReference(Module module) {
            assert(module != null);
            moduleIdentifier = module.getSignature();
        }
        
        public String getIdentifier() {
            return moduleIdentifier;
        }
        
        @Override
        public int hashCode() {
            return getIdentifier().hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj == null || obj instanceof ModuleReference == false)
                return false;
            ModuleReference b = (ModuleReference) obj;
            return getIdentifier().equals(b.getIdentifier());
        }
        
        @Override
        public String toString() {
            return getIdentifier();
        }
    }
    
    public class ModuleWeakReference extends WeakReference<Module> implements ModuleReference {
        private String moduleIdentifier;
        
        ModuleWeakReference(Module module) {
            super(module, ModuleDependencies.this.cleaningRunnable.removedModules);
            assert(module != null);
            moduleIdentifier = module.getSignature();
        }
        
        public String getIdentifier() {
            return moduleIdentifier;
        }
        
        @Override
        public int hashCode() {
            return getIdentifier().hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj == null || obj instanceof ModuleReference == false)
                return false;
            ModuleReference b = (ModuleReference) obj;
            return getIdentifier().equals(b.getIdentifier());
        }

        @Override
        public String toString() {
            return getIdentifier();
        }
    }

    public static class Dependency extends DefaultEdge {
        private static final long serialVersionUID = 5791860543581708398L;
        public boolean exported = true; 
        public boolean optional = false;
        
        @Override
        public Object getSource() {
            return super.getSource();
        }
        
        @Override
        public Object getTarget() {
            return super.getTarget();
        }
    }
    
    public static class ErrorListener {
        public void moduleNotAvailable(Module module) {}
    }
    
    private ErrorListener errorListener = new ErrorListener();
    
    final ListenableDirectedGraph<ModuleReference, Dependency> dependencies = new ListenableDirectedGraph<ModuleReference, Dependency>(new DefaultDirectedGraph<ModuleReference, Dependency>(Dependency.class));
    final private EdgeReversedGraph<ModuleReference, Dependency> reversedDependencies = new EdgeReversedGraph<>(dependencies);
    final private Map<ModuleReference, Iterable<ModuleReference>> referencingModulesMap = new WeakHashMap<ModuleReference, Iterable<ModuleReference>>();
    final private Map<ModuleReference, Iterable<ModuleReference>> moduleDependenciesMap = new WeakHashMap<ModuleReference, Iterable<ModuleReference>>();
    private boolean mustCleanCaches = false;
    final private List<GraphListener<ModuleReference, Dependency>> listeners = new ArrayList<>();
    final private CleaningRunnable cleaningRunnable = new CleaningRunnable(this);
    
    public ModuleDependencies(GraphListener<ModuleReference, Dependency>[] graphListeners, ErrorListener errorListener) {
        Thread cleaningThread = new Thread(cleaningRunnable);
        cleaningThread.setDaemon(true);
        cleaningThread.start();
        listeners.add(new GraphListener<ModuleReference, Dependency>() {
            private void cleanInternalCaches() {
                mustCleanCaches = true;
            }
            @Override
            public void vertexRemoved(GraphVertexChangeEvent<ModuleReference> e) {
                cleanInternalCaches();
            }
            @Override
            public void vertexAdded(GraphVertexChangeEvent<ModuleReference> e) {
                cleanInternalCaches();
            }
            @Override
            public void edgeRemoved(GraphEdgeChangeEvent<ModuleReference, Dependency> e) {
                cleanInternalCaches();
            }
            @Override
            public void edgeAdded(GraphEdgeChangeEvent<ModuleReference, Dependency> e) {
                cleanInternalCaches();
            }
        });
        if (errorListener != null) {
            this.errorListener = errorListener;
        }
        if (graphListeners != null) {
            for (GraphListener<ModuleReference, Dependency> graphListener : graphListeners) {
                listeners.add(graphListener);
            }
        }
        enableGraphListeners();
    }
    
    public ModuleDependencies() {
        this(null, null);
    }

    public ModuleDependencies(ErrorListener listener) {
        this(null, listener);
    }

    public ModuleDependencies(GraphListener<ModuleReference, Dependency>[] graphListeners) {
        this(graphListeners, null);
    }

    private void enableGraphListeners() {
        for (GraphListener<ModuleReference, Dependency> listener : listeners) {
            dependencies.addGraphListener(listener);
        }
    }

    private void disableGraphListeners() {
        for (GraphListener<ModuleReference, Dependency> listener : listeners) {
            dependencies.removeGraphListener(listener);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
    
    public static void main(String args[]) throws InterruptedException {
        // Test that ModuleDependencies can be garbaged collected
        @SuppressWarnings("unused")
        ModuleDependencies deps = new ModuleDependencies();
        Thread.sleep(3000);
        deps = null;
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        Thread.sleep(20000);
    }

    private boolean checkModuleIsComplete(Module module) {
        if (module == null || ! module.isAvailable()) {
            errorListener.moduleNotAvailable(module);
            return false;
        }
        return true;
    }
    
    synchronized ModuleWeakReference getExistingVertex(ModuleReference moduleString) {
        for (ModuleReference ref : dependencies.vertexSet()) {
            if (ref.equals(moduleString) && ref instanceof ModuleWeakReference) {
                return (ModuleWeakReference) ref;
            }
        }
        return null;
    }

    public synchronized void clearCaches() {
        if (! moduleDependenciesMap.isEmpty()) {
            moduleDependenciesMap.clear();
        }
        if (! referencingModulesMap.isEmpty()) {
            referencingModulesMap.clear();
        }
        mustCleanCaches = false;        
    }
    
    public synchronized void addModuleWithDependencies(Module module) {
        if (module.isDefault()) {
            return;
        }
        if (module.equals(module.getLanguageModule())) {
            return;
        }
        checkModuleIsComplete(module);
        
        ModuleStringReference moduleString = new ModuleStringReference(module);
        ModuleWeakReference moduleReference = null;
        if (! dependencies.containsVertex(moduleString)) {
            moduleReference = new ModuleWeakReference(module);
            dependencies.addVertex(moduleReference);
        } else {
            moduleReference = getExistingVertex(moduleString);
        }
        assert(moduleReference != null);
        
        for (ModuleImport imp : module.getImports()) {
            Module importedModule = imp.getModule();
            if (importedModule.equals(module.getLanguageModule())) {
                return;
            }

            checkModuleIsComplete(importedModule);
            ModuleStringReference importedModuleString = new ModuleStringReference(importedModule);
            ModuleWeakReference importedReference = null;
            if (! dependencies.containsVertex(importedModuleString)) {
                importedReference = new ModuleWeakReference(importedModule);
                dependencies.addVertex(new ModuleWeakReference(importedModule));
            } else {
                importedReference = getExistingVertex(importedModuleString);
            }
            if (importedReference != null) {
                Dependency dependency = dependencies.addEdge(moduleReference, importedReference);
                if (dependency != null) {
                    dependency.exported = imp.isExport();
                    dependency.optional = imp.isOptional();
                }
            }
        }
    }

    public synchronized void addModulesWithDependencies(Iterable<? extends Module> modules) {
        for (Module module : modules) {
            addModuleWithDependencies(module);
        }
    }

    public synchronized void removeModule(Module module) {
        ModuleStringReference moduleString = new ModuleStringReference(module);
        dependencies.removeVertex(moduleString);
    }

    public static interface TraversalAction<T> {
        void applyOn(T module);
    }
    
    public void doWithTransitiveDependencies(Module rootModule, TraversalAction<Module> action) {
        ModuleStringReference rootModuleString = new ModuleStringReference(rootModule);
        doWithTransitiveDependencies(rootModuleString, action);
    }

    public void doWithTransitiveDependencies(ModuleReference rootModuleReference, final TraversalAction<Module> action) {
        doWithTransitiveDependenciesInternal(rootModuleReference, new TraversalAction<ModuleReference>() {
            @Override
            public void applyOn(ModuleReference moduleReference) {
                assert (moduleReference instanceof ModuleWeakReference);
                Module module = ((ModuleWeakReference) moduleReference).get();
                if (module != null) {
                    action.applyOn(module);
                }
            }
        });
    }

    private synchronized void doWithTransitiveDependenciesInternal(ModuleReference rootModuleReference, TraversalAction<ModuleReference> action) {
        Iterable<ModuleReference> moduleDependencies = moduleDependenciesMap.get(rootModuleReference);
        if (moduleDependencies == null) {
            ModuleWeakReference rootModuleRef = getExistingVertex(rootModuleReference);
            if (rootModuleRef != null) {
                DepthFirstIterator<ModuleReference, Dependency> iterator = new DepthFirstIterator<>(dependencies, rootModuleRef);
                iterator.setCrossComponentTraversal(false);
                List<ModuleReference> deps = new ArrayList<ModuleReference>();
                while(iterator.hasNext()) {
                    ModuleReference ref = iterator.next();
                    if (ref != null && ! rootModuleReference.equals(ref)) {
                        deps.add(ref);
                    }
                }
                moduleDependencies = deps;
                moduleDependenciesMap.put(rootModuleReference, moduleDependencies);
            }
        }
        if (moduleDependencies != null) {
            for (ModuleReference dependency : moduleDependencies) {
                action.applyOn(dependency);
            }
        }
    }
    
    public Iterable<Module> getTransitiveDependencies(Module rootModule) {
        final LinkedList<Module> result = new LinkedList<>();
        doWithTransitiveDependencies(rootModule, new TraversalAction<Module>() {
            public void applyOn(Module module) {
                result.add(module);
            }
        });
        return result;
    }

    public Iterable<Module> getTransitiveDependencies(ModuleReference rootModuleReference) {
        final LinkedList<Module> result = new LinkedList<>();
        doWithTransitiveDependencies(rootModuleReference, new TraversalAction<Module>() {
            public void applyOn(Module module) {
                result.add(module);
            }
        });
        return result;
    }

    public void doWithReferencingModules(Module rootModule, TraversalAction<Module> action) {
        ModuleStringReference rootModuleString = new ModuleStringReference(rootModule);
        doWithReferencingModules(rootModuleString, action);
    }

    public void doWithReferencingModules(ModuleReference rootModuleReference, final TraversalAction<Module> action) {
        doWithReferencingModulesInternal(rootModuleReference, new TraversalAction<ModuleReference>() {
            @Override
            public void applyOn(ModuleReference moduleReference) {
                assert (moduleReference instanceof ModuleWeakReference);
                Module module = ((ModuleWeakReference) moduleReference).get();
                if (module != null) {
                    action.applyOn(module);
                }
            }
        });
    }

    private void doWithReferencingModulesInternal(ModuleReference rootModuleReference, TraversalAction<ModuleReference> action) {
        if (mustCleanCaches) {
            clearCaches();
        }
        Iterable<ModuleReference> referencingModules = referencingModulesMap.get(rootModuleReference);
        if (referencingModules == null) {
            final ModuleWeakReference rootModuleRef = getExistingVertex(rootModuleReference);
            if (rootModuleRef != null) {
                class DependencyAnalyzer {
                    class ModuleAnalysis {
                        Set<Dependency> dependenciesTowardsRoot = new HashSet<>();
                        Boolean isRootExportedBy = null;
                        Boolean isRootVisibleFrom = null;
                    }
                    private Map<ModuleReference, ModuleAnalysis> modulesToAnalyze = new HashMap<>();

                    public void addDependency(ModuleReference vertex,
                            Dependency edge) {
                        referencingModulesMap.clear();
                        moduleDependenciesMap.clear();

                        if (! vertex.equals(rootModuleRef)) {
                            ModuleAnalysis moduleAnalysis = modulesToAnalyze.get(vertex);
                            if (moduleAnalysis == null) {
                                moduleAnalysis = new ModuleAnalysis();
                                modulesToAnalyze.put(vertex, moduleAnalysis);
                            }
                            moduleAnalysis.dependenciesTowardsRoot.add(edge);
                        }
                    }
                    
                    public boolean isRootVisibleFrom(ModuleReference moduleReference) {
                        if (moduleReference.equals(rootModuleRef)) {
                            return true;
                        }
                        ModuleAnalysis moduleAnalysis = modulesToAnalyze.get(moduleReference);
                        if (moduleAnalysis == null) {
                            return false;
                        }
                        if (moduleAnalysis.isRootVisibleFrom != null) {
                            return moduleAnalysis.isRootVisibleFrom.booleanValue();
                        }
                        
                        boolean rootIsVisible = false;
                        Set<Dependency> dependencies = moduleAnalysis.dependenciesTowardsRoot;
                        for (Dependency dep : dependencies) {
                            if (dep.getTarget().equals(rootModuleRef)) {
                                rootIsVisible = true;
                                break;
                            }
                        }
                        if (! rootIsVisible) {
                            for (Dependency dep : dependencies) {
                                if (isRootExportedBy((ModuleReference)dep.getTarget())) {
                                    rootIsVisible = true;
                                    break;
                                }
                            }
                        }
                        
                        moduleAnalysis.isRootVisibleFrom = new Boolean(rootIsVisible);
                        return rootIsVisible; 
                    }

                    public boolean isRootExportedBy(ModuleReference moduleReference) {
                        ModuleAnalysis moduleAnalysis = modulesToAnalyze.get(moduleReference);
                        if (moduleAnalysis == null) {
                            return false;
                        }
                        if (moduleAnalysis.isRootExportedBy != null) {
                            return moduleAnalysis.isRootExportedBy.booleanValue();
                        }
                        
                        boolean rootIsExported = false;
                        Set<Dependency> dependencies = moduleAnalysis.dependenciesTowardsRoot;
                        for (Dependency dep : dependencies) {
                            if (dep.getTarget().equals(rootModuleRef) && dep.exported) {
                                rootIsExported = true;
                                break;
                            }
                        }
                        if (! rootIsExported) {
                            for (Dependency dep : dependencies) {
                                ModuleReference target = (ModuleReference)dep.getTarget();
                                if (dep.exported && isRootExportedBy(target)) {
                                    rootIsExported = true;
                                }
                            }
                        }
                        moduleAnalysis.isRootExportedBy = new Boolean(rootIsExported);
                        return rootIsExported;
                    }                
                }

                final DependencyAnalyzer dependencyAnalyzer = new DependencyAnalyzer();
                
                DepthFirstIterator<ModuleReference, Dependency> iterator = new DepthFirstIterator<ModuleReference, Dependency>(reversedDependencies, rootModuleRef) {
                    @Override
                    protected void encounterVertex(ModuleReference vertex,
                            Dependency edge) {
                        dependencyAnalyzer.addDependency(vertex, edge);
                        super.encounterVertex(vertex, edge);
                    }

                    @Override
                    protected void encounterVertexAgain(ModuleReference vertex,
                            Dependency edge) {
                        dependencyAnalyzer.addDependency(vertex, edge);
                        super.encounterVertexAgain(vertex, edge);
                    }
                };
                iterator.setCrossComponentTraversal(false);
                while(iterator.hasNext()) {
                    iterator.next();
                }
                
                List<ModuleReference> modulesToAdd = new ArrayList<ModuleReference>();
                for (ModuleReference moduleReference : dependencyAnalyzer.modulesToAnalyze.keySet()) {
                    if (dependencyAnalyzer.isRootVisibleFrom(moduleReference)) {
                        modulesToAdd.add(moduleReference);
                    }
                }
                referencingModules = modulesToAdd;
                referencingModulesMap.put(rootModuleReference, referencingModules);
            }
        }
        if (referencingModules != null) {
            for (ModuleReference dependency : referencingModules) {
                action.applyOn(dependency);
            }
        }
    }

    public Iterable<Module> getReferencingModules(Module rootModule) {
        final LinkedList<Module> result = new LinkedList<>();
        doWithReferencingModules(rootModule, new TraversalAction<Module>() {
            public void applyOn(Module module) {
                result.add(module);
            }
        });
        return result;
    }
    
    public Iterable<Module> getReferencingModules(ModuleReference rootModuleReference) {
        final LinkedList<Module> result = new LinkedList<>();
        doWithReferencingModules(rootModuleReference, new TraversalAction<Module>() {
            public void applyOn(Module module) {
                result.add(module);
            }
        });
        return result;
    }
    
    public synchronized void reset() {
        disableGraphListeners();
        clearCaches();
        LinkedList<ModuleReference> verticesToRemove = new LinkedList<>(dependencies.vertexSet());
        dependencies.removeAllVertices(verticesToRemove);
        enableGraphListeners();
    }
    
    public synchronized String dependenciesToDot() {
        DOTExporter<ModuleReference, Dependency> exporter = new DOTExporter<>(
                new IntegerNameProvider<ModuleReference>(),
                new StringNameProvider<ModuleReference>(),
                null,
                null,
                new ComponentAttributeProvider<Dependency>() {
                    @Override
                    public Map<String, String> getComponentAttributes(
                            Dependency dep) {
                        Map<String, String> attributes = new HashMap<String, String>();
                        attributes.put("Exported", Boolean.toString(dep.exported));
                        attributes.put("Optional", Boolean.toString(dep.optional));
                        return attributes;
                    }
                });
        
        StringWriter writer = new StringWriter();
        exporter.export(writer, dependencies);
        return writer.toString();
    }

    public synchronized Collection<Dependency> getAllDependencies() {
        return new LinkedList<>(dependencies.edgeSet());
    }
    
    public synchronized Collection<Dependency> getDirectDependencies(ModuleReference moduleReference) {
        if (moduleReference instanceof ModuleStringReference) {
            moduleReference = getExistingVertex((ModuleStringReference) moduleReference);
        }
        return new LinkedList<>(dependencies.outgoingEdgesOf(moduleReference));
    }

    public synchronized Collection<Dependency> getDirectReverseDependencies(ModuleReference moduleReference) {
        if (moduleReference instanceof ModuleStringReference) {
            moduleReference = getExistingVertex((ModuleStringReference) moduleReference);
        }
        return new LinkedList<>(dependencies.incomingEdgesOf(moduleReference));
    }
    
    public boolean hasCycles() {
        CycleDetector<ModuleReference, Dependency> detector = new CycleDetector<>(dependencies);
        return detector.detectCycles();
    }
}
