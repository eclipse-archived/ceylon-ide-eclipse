package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.AdditionalAnnotationCreator.getRefinedDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonHierarchyNode.getDeclarationInUnit;
import static com.redhat.ceylon.eclipse.code.outline.CeylonHierarchyNode.getTypeChecker;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public final class CeylonHierarchyContentProvider 
        implements ITreeContentProvider {
    
    private final IWorkbenchPartSite site;
    
    private HierarchyMode mode = HIERARCHY;
    
    private CeylonHierarchyNode hierarchyRoot;
    private CeylonHierarchyNode supertypesRoot;
    private CeylonHierarchyNode subtypesRoot;
    
    private boolean showingRefinements;
    private boolean empty;
    
    private int depthInHierarchy;
    private boolean veryAbstractType;
    
    private String description;
    
    CeylonHierarchyContentProvider(IWorkbenchPartSite site) {
        this.site = site;
    }
    
    Declaration getDeclaration(IProject project) {
        return subtypesRoot.getDeclaration(project);
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput!=null && newInput!=oldInput) {
            HierarchyInput rootNode = (HierarchyInput) newInput;
            Declaration declaration = rootNode.declaration;
            if (declaration instanceof TypedDeclaration) { 
                TypedDeclaration td = (TypedDeclaration) declaration;
                if (td.getTypeDeclaration().isAnonymous()) {
                    declaration = ((TypedDeclaration) declaration).getTypeDeclaration();
                }
            }
            showingRefinements = !(declaration instanceof TypeDeclaration);
            empty = declaration==null;
            if (!empty) {
                String name = declaration.getQualifiedNameString();
                veryAbstractType = 
                        name.equals("ceylon.language::Object") ||
                        name.equals("ceylon.language::Anything") ||
                        name.equals("ceylon.language::Basic") ||
                        name.equals("ceylon.language::Identifiable");
                description = declaration.getName();//getDescriptionFor(declaration);
                if (isShowingRefinements() && 
                        declaration.isClassOrInterfaceMember()) {
                    ClassOrInterface classOrInterface = 
                            (ClassOrInterface) declaration.getContainer();
                    description = classOrInterface.getName() + '.' + description;
                }
                try {
                    site.getWorkbenchWindow().run(true, true, 
                            new Runnable(rootNode.project, declaration));
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
                rootNode.declaration=null;//don't hang onto hard ref
            }
        }
    }
    
    boolean isVeryAbstractType() {
        return veryAbstractType;
    }

    boolean isShowingRefinements() {
        return showingRefinements;
    }
    
    public boolean isEmpty() {
        return empty;
    }
    
    @Override
    public void dispose() {}

    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length>0;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public CeylonHierarchyNode[] getChildren(Object parentElement) {
        if (parentElement instanceof HierarchyInput) {
            switch (mode) {
            case HIERARCHY:
                return new CeylonHierarchyNode[] { hierarchyRoot };                    
            case SUPERTYPES:
                return new CeylonHierarchyNode[] { supertypesRoot };
            case SUBTYPES:
                return new CeylonHierarchyNode[] { subtypesRoot };
            default:
                throw new RuntimeException();
            }
        }
        else if (parentElement instanceof CeylonHierarchyNode) {
            List<CeylonHierarchyNode> children = 
                    ((CeylonHierarchyNode) parentElement).getChildren();
            CeylonHierarchyNode[] array = 
                    children.toArray(new CeylonHierarchyNode[children.size()]);
            Arrays.sort(array);
            return array;
        }
        else {
            return null;
        }
    }

    HierarchyMode getMode() {
        return mode;
    }

    void setMode(HierarchyMode mode) {
        this.mode = mode;
    }
    
    int getDepthInHierarchy() {
        return depthInHierarchy;
    }
    
    private class Runnable implements IRunnableWithProgress {
        
        private final IProject project;
        private final Declaration declaration;
                
        private final Map<Declaration, CeylonHierarchyNode> subtypesOfSupertypes = 
                new HashMap<Declaration, CeylonHierarchyNode>();
        private final Map<Declaration, CeylonHierarchyNode> subtypesOfAllTypes = 
                new HashMap<Declaration, CeylonHierarchyNode>();
        private final Map<Declaration, CeylonHierarchyNode> supertypesOfAllTypes = 
                new HashMap<Declaration, CeylonHierarchyNode>();

        private void add(Declaration td, Declaration etd) {
            getSubtypeHierarchyNode(etd).addChild(getSubtypeHierarchyNode(td));
            getSupertypeHierarchyNode(td).addChild(getSupertypeHierarchyNode(etd));
        }

        private CeylonHierarchyNode getSubtypePathNode(Declaration d) {
            CeylonHierarchyNode n = subtypesOfSupertypes.get(d);
            if (n==null) {
                n = new CeylonHierarchyNode(d);
                subtypesOfSupertypes.put(d, n);
            }
            return n;
        }

        private CeylonHierarchyNode getSubtypeHierarchyNode(Declaration d) {
            CeylonHierarchyNode n = subtypesOfAllTypes.get(d);
            if (n==null) {
                n = new CeylonHierarchyNode(d);
                subtypesOfAllTypes.put(d, n);
            }
            return n;
        }

        private CeylonHierarchyNode getSupertypeHierarchyNode(Declaration d) {
            CeylonHierarchyNode n = supertypesOfAllTypes.get(d);
            if (n==null) {
                n = new CeylonHierarchyNode(d);
                supertypesOfAllTypes.put(d, n);
            }
            return n;
        }
        
        public Runnable(IProject project, Declaration declaration) {
            this.project = project;
            this.declaration = declaration;
        }
        
        @Override
        public void run(IProgressMonitor monitor) 
                throws InvocationTargetException,
                       InterruptedException {
            
            monitor.beginTask("Building hierarchy", 100000);
            
            Unit unit = declaration.getUnit();
            Module currentModule = unit.getPackage().getModule();
            List<TypeChecker> tcs = getTypeChecker(project, 
                    currentModule.getNameAsString());
            Set<Module> allModules = new HashSet<Module>();
            for (TypeChecker tc: tcs) {
                ModuleManager moduleManager = 
                        tc.getPhasedUnits().getModuleManager();
                allModules.addAll(moduleManager.getCompiledModules());
                allModules.add(currentModule);
            }
            
//            boolean isFromUnversionedModule = declaration.getUnit().getPackage()
//                    .getModule().getVersion()==null;
            
            monitor.worked(10000);
            
            Set<Package> packages = new HashSet<Package>();
            int ams = allModules.size();
            for (Module m: allModules) {
//                if (m.getVersion()!=null || isFromUnversionedModule) {
                    packages.addAll(m.getAllPackages());
                    monitor.worked(10000/ams);
                    if (monitor.isCanceled()) return;
//                }
            }
    
            subtypesOfAllTypes.put(declaration, 
                    getSubtypePathNode(declaration));
            
            Declaration dec = declaration;
            Declaration superDec;
            do {
                depthInHierarchy++;
                if (declaration instanceof TypeDeclaration) {
                    TypeDeclaration td = (TypeDeclaration) dec;
                    superDec = td.getExtendedTypeDeclaration();
                    if (!td.getSatisfiedTypeDeclarations().isEmpty()) {
                        getSubtypePathNode(superDec).setNonUnique(true);
                    }
                }
                else if (declaration instanceof TypedDeclaration) {
                    superDec = getRefinedDeclaration(dec);
                    if (superDec!=null) {
                        List<Declaration> directlyInheritedMembers = 
                                ((TypeDeclaration)dec.getContainer())
                                        .getInheritedMembers(dec.getName());
                        if (!directlyInheritedMembers.contains(superDec)) {
                            CeylonHierarchyNode n = 
                                    new CeylonHierarchyNode(superDec);
                            n.setMultiple(true);
                            n.addChild(getSubtypePathNode(dec));
                            getSubtypePathNode(superDec).addChild(n);
                            dec = superDec;
                            continue;
                        }
                        else if (directlyInheritedMembers.size()>1) {
                            getSubtypePathNode(superDec).setNonUnique(true);
                        }
                    }
                }
                else {
                    superDec = null;
                }
                if (superDec!=null) {
                    getSubtypePathNode(superDec).addChild(getSubtypePathNode(dec));
                    dec = superDec;
                }
            } 
            while (superDec!=null);
            
            hierarchyRoot = getSubtypePathNode(dec);
            subtypesRoot = getSubtypeHierarchyNode(declaration);
            supertypesRoot = getSupertypeHierarchyNode(declaration);
            
            if (monitor.isCanceled()) return;
            
            IEditorPart part = site.getPage().getActiveEditor();
            
            int ps = packages.size();
            for (Package p: packages) { //workaround CME
                int ms = p.getMembers().size();
                monitor.subTask("Building hierarchy - scanning " + p.getNameAsString());
                for (Unit u: p.getUnits()) {
                    try {
                        for (Declaration d: u.getDeclarations()) {
                            d = replaceWithCurrentEditorDeclaration(part, p, d); //TODO: not enough to catch *new* subtypes in the dirty editor
                            if (d instanceof ClassOrInterface || 
                                    d instanceof TypeParameter) {
                                try {
                                    if (declaration instanceof TypeDeclaration) {
                                        TypeDeclaration td = (TypeDeclaration) d;
                                        ClassOrInterface etd = 
                                                td.getExtendedTypeDeclaration();
                                        if (etd!=null) {
                                            add(td, etd);
                                        }
                                        for (TypeDeclaration std: 
                                            td.getSatisfiedTypeDeclarations()) {
                                            add(td, std);
                                        }
                                    }
                                    else if (declaration instanceof TypedDeclaration) {
                                        TypeDeclaration td = (TypeDeclaration) d;
                                        //TODO: keep the directly refined declarations in the model
                                        //      (get the typechecker to set this up)
                                        Declaration mem = 
                                                td.getDirectMember(declaration.getName(), 
                                                        null, false);
                                        if (mem!=null) {
                                            for (Declaration id: 
                                                td.getInheritedMembers(declaration.getName())) {
                                                add(mem, id);
                                            }
                                        }                               
                                    }
                                }
                                catch (Exception e) {
                                    System.err.println(d.getQualifiedNameString());
                                    throw e;
                                }
                            }
                            monitor.worked(15000/ps/ms);
                            if (monitor.isCanceled()) return;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (monitor.isCanceled()) return;
            }
            monitor.done();
        }

        private Declaration replaceWithCurrentEditorDeclaration(IEditorPart part, 
                Package p, Declaration d) {
            if (part instanceof CeylonEditor && part.isDirty()) {
                CompilationUnit rootNode = ((CeylonEditor) part).getParseController()
                        .getRootNode();
                if (rootNode!=null && 
                        rootNode.getUnit()!=null) {
                    Unit un = rootNode.getUnit();
                    if (un.getPackage().equals(p)) {
                        Declaration result = 
                                getDeclarationInUnit(d.getQualifiedNameString(), un);
                        if (result!=null) {
                            return result;
                        }
                    }
                }
            }
            return d;
        }
        
    }
    
    String getDescription() {
        if (isShowingRefinements()) {
            switch (getMode()) {
            case HIERARCHY:
                return "Quick Hierarchy - refinement hierarchy of '" + description + "'";
            case SUPERTYPES:
                return "Quick Hierarchy - generalizations of '" + description + "'";
            case SUBTYPES:
                return "Quick Hierarchy - refinements '" + description + "'";
            default:
                throw new RuntimeException();
            }
        }
        else {
            switch (getMode()) {
            case HIERARCHY:
                return "Quick Hierarchy - type hierarchy of '" + description + "'";
            case SUPERTYPES:
                return "Quick Hierarchy - supertypes of '" + description + "'";
            case SUBTYPES:
                return "Quick Hierarchy - subtypes of '" + description + "'";
            default:
                throw new RuntimeException();
            }
        }
    }
    
}