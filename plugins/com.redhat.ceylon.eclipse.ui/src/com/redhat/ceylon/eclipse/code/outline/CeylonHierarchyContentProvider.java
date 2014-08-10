package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.model.Util.getInterveningRefinements;
import static com.redhat.ceylon.compiler.typechecker.model.Util.getSignature;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isAbstraction;
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
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
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

            Declaration root = declaration;
            if (declaration instanceof TypeDeclaration) {
                TypeDeclaration dec = (TypeDeclaration) declaration;
                while (dec!=null) {
                    depthInHierarchy++;
                    root = dec;
                    ClassOrInterface superDec = dec.getExtendedTypeDeclaration();
                    if (superDec!=null) {
                        if (!dec.getSatisfiedTypeDeclarations().isEmpty()) {
                            getSubtypePathNode(superDec).setNonUnique(true);
                        }
                        getSubtypePathNode(superDec).addChild(getSubtypePathNode(dec));
                    }
                    dec = superDec;
                }
            }
            else if (declaration instanceof TypedDeclaration) {
                Declaration memberDec = declaration;
                Declaration refinedDeclaration = declaration.getRefinedDeclaration();
                Scope container = declaration.getContainer();
                if (container instanceof TypeDeclaration) {
                    TypeDeclaration dec = (TypeDeclaration) container;
                    List<ProducedType> signature = getSignature(declaration);
                    depthInHierarchy++;
                    root = memberDec;
                    //first walk up the superclass hierarchy
                    while (dec!=null) {
                        ClassOrInterface superDec = dec.getExtendedTypeDeclaration();
                        if (superDec!=null) {
                            Declaration superMemberDec = 
                                    superDec.getDirectMember(declaration.getName(), signature, false);
                            if (superMemberDec!=null && 
                                    !isAbstraction(superMemberDec) &&
                                    superMemberDec.getRefinedDeclaration()
                                    .equals(refinedDeclaration)) {
                                List<Declaration> directlyInheritedMembers = 
                                        getInterveningRefinements(declaration.getName(), signature, 
                                                refinedDeclaration,
                                                (TypeDeclaration) memberDec.getContainer(), superDec);
                                List<Declaration> all = 
                                        getInterveningRefinements(declaration.getName(), signature, 
                                                refinedDeclaration,
                                                (TypeDeclaration) memberDec.getContainer(), 
                                                (TypeDeclaration) refinedDeclaration.getContainer());
                                for (Declaration d: all) {
                                    TypeDeclaration dtd = (TypeDeclaration) d.getContainer();
                                    if (!superDec.inherits(dtd)) {
                                        getSubtypePathNode(superMemberDec).setNonUnique(true);
                                    }
                                }
                                directlyInheritedMembers.remove(superMemberDec);
                                if (directlyInheritedMembers.size()>0) {
                                    getSubtypePathNode(superMemberDec).setNonUnique(true);
                                }
                                getSubtypePathNode(superMemberDec).addChild(getSubtypePathNode(memberDec));
                                depthInHierarchy++;
                                root = superMemberDec;
                                memberDec = superMemberDec;
                            }
                            //TODO else add an "empty" node to the hierarchy like in JDT
                        }
                        dec = superDec;
                    }
                    //now look at the very top of the hierarchy, even if it is an interface
                    if (!memberDec.equals(refinedDeclaration)) {
                        List<Declaration> directlyInheritedMembers = 
                                getInterveningRefinements(declaration.getName(), signature, 
                                        declaration.getRefinedDeclaration(),
                                        (TypeDeclaration) memberDec.getContainer(), 
                                        (TypeDeclaration) refinedDeclaration.getContainer());
                        directlyInheritedMembers.remove(refinedDeclaration);
                        if (directlyInheritedMembers.size()>1) {
                            //multiple intervening interfaces
                            CeylonHierarchyNode n = 
                                    new CeylonHierarchyNode(memberDec);
                            n.setMultiple(true);
                            n.addChild(getSubtypePathNode(memberDec));
                            getSubtypePathNode(refinedDeclaration).addChild(n);
                        }
                        else if (directlyInheritedMembers.size()==1) {
                            //exactly one intervening interface
                            Declaration idec = directlyInheritedMembers.get(0);
                            getSubtypePathNode(idec).addChild(getSubtypePathNode(memberDec));
                            getSubtypePathNode(refinedDeclaration).addChild(getSubtypePathNode(idec));
                        }
                        else {
                            //no intervening interfaces
                            getSubtypePathNode(refinedDeclaration).addChild(getSubtypePathNode(memberDec));
                        }
                        root = refinedDeclaration;
                    }
                }
            }

            hierarchyRoot = getSubtypePathNode(root);
            subtypesRoot = getSubtypeHierarchyNode(declaration);
            supertypesRoot = getSupertypeHierarchyNode(declaration);
            
            if (monitor.isCanceled()) return;
            
            IEditorPart part = site.getPage().getActiveEditor();
            
            List<ProducedType> signature = getSignature(declaration);
            int ps = packages.size();
            for (Package p: packages) { //workaround CME
                int ms = p.getMembers().size();
                monitor.subTask("Building hierarchy - scanning " + p.getNameAsString());
                for (Unit u: p.getUnits()) {
                    try {
                        //TODO: unshared inner types get 
                        //      missed for binary modules
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
                                        Declaration refinedDeclaration = declaration.getRefinedDeclaration();
                                        TypeDeclaration td = (TypeDeclaration) d;
                                        Declaration dec = td.getDirectMember(declaration.getName(), 
                                                signature, false);
                                        if (dec!=null && dec.getRefinedDeclaration()!=null &&
                                                dec.getRefinedDeclaration().equals(refinedDeclaration)) {
                                            List<Declaration> refinements = 
                                                    getInterveningRefinements(declaration.getName(), signature,
                                                            refinedDeclaration, td,
                                                            (TypeDeclaration) refinedDeclaration.getContainer());
                                            //TODO: keep the directly refined declarations in the model
                                            //      (get the typechecker to set this up)
                                            for (Declaration candidate: refinements) {
                                                if (getInterveningRefinements(declaration.getName(), signature,
                                                        refinedDeclaration, td,
                                                        (TypeDeclaration) candidate.getContainer())
                                                                .size()==1) {
                                                    add(dec, candidate);
                                                }
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