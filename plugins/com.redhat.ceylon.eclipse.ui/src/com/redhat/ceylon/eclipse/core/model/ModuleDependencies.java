package com.redhat.ceylon.eclipse.core.model;

import java.io.StringWriter;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;

class CleaningRunnable implements Runnable {
    WeakReference<ModuleDependencies> dependenciesRef;
    
    CleaningRunnable(ModuleDependencies dependencies) {
        dependenciesRef = new WeakReference<ModuleDependencies>(dependencies);
    }

    @Override
    public void run() {
        while (true) {
            ModuleDependencies toClean = dependenciesRef.get();
            if (toClean == null) {
                break;
            }
            Reference<? extends Module> moduleReference = toClean.removedModules.poll();
            if (moduleReference != null) {
                assert(moduleReference instanceof ModuleDependencies.ModuleWeakReference);
                synchronized (toClean) {
                    toClean.dependencies.removeVertex((ModuleDependencies.ModuleReference) moduleReference);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}

public class ModuleDependencies {
    public interface ModuleReference {
        String getIdentifier();
    }

    private class ModuleStringReference implements ModuleReference {
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
            super(module, removedModules);
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
    ReferenceQueue<Module> removedModules = new ReferenceQueue<>();

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
    
    DirectedGraph<ModuleReference, Dependency> dependencies = new ListenableDirectedGraph<ModuleReference, Dependency>(new DefaultDirectedGraph<ModuleReference, Dependency>(Dependency.class));
    private EdgeReversedGraph<ModuleReference, Dependency> reversedDependencies = new EdgeReversedGraph<>(dependencies);
    
    public ModuleDependencies() {
        Thread cleaningThread = new Thread(new CleaningRunnable(this));
        cleaningThread.setDaemon(true);
        cleaningThread.start();
    }
    
    public ModuleDependencies(ErrorListener listener) {
        errorListener = listener;
        Thread cleaningThread = new Thread(new CleaningRunnable(this));
        cleaningThread.setDaemon(true);
        cleaningThread.start();
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
    
    private ModuleWeakReference getExistingVertex(ModuleReference moduleString) {
        for (ModuleReference ref : dependencies.vertexSet()) {
            if (ref.equals(moduleString) && ref instanceof ModuleWeakReference) {
                return (ModuleWeakReference) ref;
            }
        }
        return null;
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

    public static interface TraversalAction {
        void applyOn(Module module);
    }
    
    public synchronized void doWithTransitiveDependencies(Module rootModule, TraversalAction action) {
        ModuleStringReference rootModuleString = new ModuleStringReference(rootModule);
        doWithTransitiveDependencies(rootModuleString, action);
    }

    public synchronized void doWithTransitiveDependencies(ModuleReference rootModuleReference, TraversalAction action) {
        ModuleWeakReference rootModuleRef = getExistingVertex(rootModuleReference);
        if (rootModuleRef != null) {
            BreadthFirstIterator<ModuleReference, Dependency> iterator = new BreadthFirstIterator<>(dependencies, rootModuleRef);
            while(iterator.hasNext()) {
                ModuleReference moduleReference = iterator.next();
                assert (moduleReference instanceof ModuleWeakReference);
                Module module = ((ModuleWeakReference) moduleReference).get();
                if (module != null) {
                    action.applyOn(module);
                }
            }
        }
    }
    
    public Iterable<Module> getTransitiveDependencies(Module rootModule) {
        final LinkedList<Module> result = new LinkedList<>();
        doWithTransitiveDependencies(rootModule, new TraversalAction() {
            public void applyOn(Module module) {
                result.add(module);
            }
        });
        return result;
    }

    public Iterable<Module> getTransitiveDependencies(ModuleReference rootModuleReference) {
        final LinkedList<Module> result = new LinkedList<>();
        doWithTransitiveDependencies(rootModuleReference, new TraversalAction() {
            public void applyOn(Module module) {
                result.add(module);
            }
        });
        return result;
    }

    public synchronized void doWithReferencingModules(Module rootModule, TraversalAction action) {
        ModuleStringReference rootModuleString = new ModuleStringReference(rootModule);
        doWithReferencingModules(rootModuleString, action);
    }

    private void doWithReferencingModules(ModuleReference rootModuleReference, TraversalAction action) {
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
                            if (isRootVisibleFrom((ModuleReference)dep.getTarget()) && dep.exported) {
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
            
            for (ModuleReference moduleReference : dependencyAnalyzer.modulesToAnalyze.keySet()) {
                if (dependencyAnalyzer.isRootVisibleFrom(moduleReference)) {
                    assert (moduleReference instanceof ModuleWeakReference);
                    Module module = ((ModuleWeakReference) moduleReference).get();
                    if (module != null) {
                        action.applyOn(module);
                    }
                }
            }
        }
    }

    public Iterable<Module> getReferencingModules(Module rootModule) {
        final LinkedList<Module> result = new LinkedList<>();
        doWithReferencingModules(rootModule, new TraversalAction() {
            public void applyOn(Module module) {
                result.add(module);
            }
        });
        return result;
    }
    
    public Iterable<Module> getReferencingModules(ModuleReference rootModuleReference) {
        final LinkedList<Module> result = new LinkedList<>();
        doWithReferencingModules(rootModuleReference, new TraversalAction() {
            public void applyOn(Module module) {
                result.add(module);
            }
        });
        return result;
    }
    
    public synchronized void reset() {
        LinkedList<ModuleReference> verticesToRemove = new LinkedList<>(dependencies.vertexSet()); 
        dependencies.removeAllVertices(verticesToRemove);
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
