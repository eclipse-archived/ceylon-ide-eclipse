package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.model.Util.getInterveningRefinements;
import static com.redhat.ceylon.compiler.typechecker.model.Util.getSignature;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isAbstraction;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static com.redhat.ceylon.eclipse.util.ModelProxy.getDeclarationInUnit;

import java.lang.reflect.InvocationTargetException;
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

import com.redhat.ceylon.cmr.api.JDKUtils;
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
import com.redhat.ceylon.eclipse.core.model.IProjectAware;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.util.ModelProxy;

public final class CeylonHierarchyContentProvider
        implements ITreeContentProvider {
//        implements ILazyTreeContentProvider {
    
    private final IWorkbenchPartSite site;
    
    private HierarchyMode mode = HIERARCHY;
    
    private CeylonHierarchyNode hierarchyRoot;
    private CeylonHierarchyNode supertypesRoot;
    private CeylonHierarchyNode subtypesRoot;
    
    private boolean showingRefinements;
    private boolean empty;
    private boolean excludeJDK;
    private boolean excludeOracleJDK = true;
    
    private int depthInHierarchy;
    private boolean veryAbstractType;
    
    private String description;

    private String label;
    
    CeylonHierarchyContentProvider(IWorkbenchPartSite site, String label) {
        this.site = site;
        this.label = label;
    }
    
    Declaration getDeclaration() {
        return subtypesRoot==null ? null : 
            subtypesRoot.getDeclaration();
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput!=null && newInput!=oldInput) {
            Declaration declaration = ((ModelProxy) newInput).getDeclaration();
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
                if (//isShowingRefinements() && 
                        declaration.isClassOrInterfaceMember()) {
                    ClassOrInterface classOrInterface = 
                            (ClassOrInterface) declaration.getContainer();
                    description = classOrInterface.getName() + '.' + description;
                }
                rebuild(declaration);
            }
        }
    }

    private void rebuild(Declaration declaration) {
        if (declaration!=null) {
            try {
                site.getWorkbenchWindow()
                .run(true, true, new Runnable(declaration));
            } 
            catch (Exception e) {
                e.printStackTrace();
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
    public Object getParent(Object element) {
        if (element instanceof CeylonHierarchyNode) {
            return ((CeylonHierarchyNode) element).getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length>0;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public CeylonHierarchyNode[] getChildren(Object parentElement) {
        if (parentElement instanceof ModelProxy) {
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
            return ((CeylonHierarchyNode) parentElement).getChildren();
        }
        else {
            return null;
        }
    }

//    @Override
//    public void updateElement(Object parent, int index) {
//        if (recursive) return;
//        CeylonHierarchyNode child;
//        if (parent instanceof ModelProxy) {
//            if (index>0) return;
//            switch (mode) {
//            case HIERARCHY:
//                child = hierarchyRoot;
//                break;
//            case SUPERTYPES:
//                child = supertypesRoot;
//                break;
//            case SUBTYPES:
//                child = subtypesRoot;
//                break;
//            default:
//                throw new RuntimeException();
//            }
//        }
//        else if (parent instanceof CeylonHierarchyNode) {
//            List<CeylonHierarchyNode> children = 
//                    ((CeylonHierarchyNode) parent).getChildren();
//            //TODO: wow, this is really expensive!
//            CeylonHierarchyNode[] array = 
//                    children.toArray(new CeylonHierarchyNode[children.size()]);
//            Arrays.sort(array);
//            if (index>=array.length) return;
//            child = array[index];
//        }
//        else {
//            return;
//        }
//        recursive = true;
//        treeViewer.replace(parent, index, child);
//        treeViewer.setChildCount(child, child.getChildren().size());
//        recursive = false;
//    }
//
//    @Override
//    public void updateChildCount(Object element, int currentChildCount) {
//        if (recursive) return;
//        int count = 0;
//        if (element instanceof ModelProxy) {
//            count = 1;
//        }
//        else if (element instanceof CeylonHierarchyNode) {
//            count = ((CeylonHierarchyNode) element).getChildren().size();
//        }
//        recursive = true;
//        treeViewer.setChildCount(element, count);
//        recursive = false;
//    }
    
    HierarchyMode getMode() {
        return mode;
    }

    void setMode(HierarchyMode mode) {
        this.mode = mode;
    }
    
    boolean isExcludeJDK() {
        return excludeJDK;
    }
    
    void setExcludeJDK(boolean excludeJDK) {
        this.excludeJDK = excludeJDK;
        rebuild(getDeclaration());
    }
    
    boolean isExcludeOracleJDK() {
        return excludeOracleJDK;
    }
    
    void setExcludeOracleJDK(boolean excludeOracleJDK) {
        this.excludeOracleJDK = excludeOracleJDK;
        rebuild(getDeclaration());
    }
    
    int getDepthInHierarchy() {
        return depthInHierarchy;
    }
    
    private class Runnable implements IRunnableWithProgress {
        
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
        
        public Runnable(Declaration declaration) {
            this.declaration = declaration;
        }
        
        @Override
        public void run(IProgressMonitor monitor) 
                throws InvocationTargetException,
                       InterruptedException {
            
            monitor.beginTask("Building hierarchy", 100000);
            
            Unit unit = declaration.getUnit();
            Module currentModule = unit.getPackage().getModule();
            IProject project = null;
            if (unit instanceof IResourceAware) {
                project = ((IResourceAware) unit).getProjectResource();
            }
            else if (unit instanceof IProjectAware) {
                project = ((IProjectAware) unit).getProject();
            }
            List<TypeChecker> tcs = 
                    ModelProxy.getTypeChecker(currentModule, project);
            Set<Module> allModules = new HashSet<Module>();
            for (TypeChecker tc: tcs) {
                ModuleManager moduleManager = 
                        tc.getPhasedUnits().getModuleManager();
                allModules.addAll(moduleManager.getCompiledModules());
                allModules.add(currentModule);
            }
            
            monitor.worked(10000);
            
            Set<Package> packages = new HashSet<Package>();
            int ams = allModules.size();
            for (Module m: allModules) {
                for (Package p: m.getAllPackages()) {
                    String pmn = p.getModule().getNameAsString();
                    if ((!excludeJDK || !JDKUtils.isJDKModule(pmn)) && 
                            (!excludeOracleJDK || !JDKUtils.isOracleJDKModule(pmn))) {
                        packages.add(p);
                    }
                }
                monitor.worked(10000/ams);
                if (monitor.isCanceled()) return;
            }
    
            subtypesOfAllTypes.put(declaration, 
                    getSubtypePathNode(declaration));

            Declaration root = declaration;
            depthInHierarchy = 0;
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
                monitor.subTask("scanning " + p.getNameAsString());
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
                                        if (!(td instanceof TypeParameter)) {
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
                CompilationUnit rootNode = 
                        ((CeylonEditor) part).getParseController()
                                .getRootNode();
                if (rootNode!=null) {
                    Unit unit = rootNode.getUnit();
                    if (unit!=null && unit.getPackage().equals(p)) {
                        Declaration result = 
                                getDeclarationInUnit(d.getQualifiedNameString(), unit);
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
                return label + " - refinement hierarchy of " + description;
            case SUPERTYPES:
                return label + " - generalizations of " + description;
            case SUBTYPES:
                return label + " - refinements of " + description;
            default:
                throw new RuntimeException();
            }
        }
        else {
            switch (getMode()) {
            case HIERARCHY:
                return label + " - type hierarchy of " + description;
            case SUPERTYPES:
                return label + " - supertypes of " + description;
            case SUBTYPES:
                return label + " - subtypes of " + description;
            default:
                throw new RuntimeException();
            }
        }
    }
    
}