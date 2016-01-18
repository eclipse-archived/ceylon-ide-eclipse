package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.ENABLE_HIERARCHY_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.HIERARCHY_FILTERS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getTypeCheckers;
import static com.redhat.ceylon.eclipse.util.ModelProxy.getDeclarationInUnit;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getInterveningRefinements;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.getSignature;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isAbstraction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.util.Filters;
import com.redhat.ceylon.eclipse.util.ModelProxy;
import com.redhat.ceylon.model.cmr.JDKUtils;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

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
    
    CeylonHierarchyContentProvider(IWorkbenchPartSite site, 
            String label,
            boolean excludeJDK, boolean excludeOracleJDK) {
        this.site = site;
        this.label = label;
        this.excludeJDK = excludeJDK;
        this.excludeOracleJDK = excludeOracleJDK;
    }
    
    Declaration getDeclaration() {
        return subtypesRoot==null ? null : 
            subtypesRoot.getDeclaration();
    }

    @Override
    public void inputChanged(Viewer viewer, 
            Object oldInput, Object newInput) {
        if (newInput!=null && newInput!=oldInput) {
            filters.initFilters();
            ModelProxy proxy = (ModelProxy) newInput;
            Declaration declaration = proxy.get();
            if (declaration instanceof TypedDeclaration) { 
                TypedDeclaration td = 
                        (TypedDeclaration) declaration;
                if (td.getTypeDeclaration().isAnonymous()) {
                    TypedDeclaration atd = 
                            (TypedDeclaration) declaration;
                    declaration = atd.getTypeDeclaration();
                }
            }
            showingRefinements = 
                    !(declaration instanceof TypeDeclaration);
            empty = declaration==null;
            if (!empty) {
                String name = 
                        declaration.getQualifiedNameString();
                veryAbstractType = 
                        name.equals("ceylon.language::Object") ||
                        name.equals("ceylon.language::Anything") ||
                        name.equals("ceylon.language::Basic") ||
                        name.equals("ceylon.language::Identifiable");
                description = declaration.getName();//getDescriptionFor(declaration);
                if (//isShowingRefinements() && 
                        declaration.isClassOrInterfaceMember()) {
                    ClassOrInterface classOrInterface = 
                            (ClassOrInterface) 
                                declaration.getContainer();
                    description = 
                            classOrInterface.getName() + 
                            '.' + description;
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
            CeylonHierarchyNode chn = 
                    (CeylonHierarchyNode) element;
            return chn.getParent();
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
    public CeylonHierarchyNode[] getChildren(Object parent) {
        if (parent instanceof ModelProxy) {
            switch (mode) {
            case HIERARCHY:
                return new CeylonHierarchyNode[] 
                        { hierarchyRoot };                    
            case SUPERTYPES:
                return new CeylonHierarchyNode[] 
                        { supertypesRoot };
            case SUBTYPES:
                return new CeylonHierarchyNode[] 
                        { subtypesRoot };
            default:
                throw new RuntimeException();
            }
        }
        else if (parent instanceof CeylonHierarchyNode) {
            CeylonHierarchyNode chn = 
                    (CeylonHierarchyNode) parent;
            return chn.getChildren();
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
                
        private final Map<Declaration, CeylonHierarchyNode> 
            subtypesOfSupertypes = 
                new HashMap<Declaration,CeylonHierarchyNode>();
        private final Map<Declaration,CeylonHierarchyNode> 
            subtypesOfAllTypes = 
                new HashMap<Declaration,CeylonHierarchyNode>();
        private final Map<Declaration,CeylonHierarchyNode> 
            supertypesOfAllTypes = 
                new HashMap<Declaration,CeylonHierarchyNode>();

        private void add(Declaration td, Declaration etd) {
            if (inheritedBy(td)) {
                getSubtypeHierarchyNode(etd)
                    .addChild(getSubtypeHierarchyNode(td));
            }
            if (inherits(td)) {
                getSupertypeHierarchyNode(td)
                    .addChild(getSupertypeHierarchyNode(etd));
            }
        }
        
        private boolean inherits(Declaration td) {
            if (declaration instanceof TypeDeclaration &&
                    td instanceof TypeDeclaration) {
                return ((TypeDeclaration) declaration)
                        .inherits((TypeDeclaration) td);
            }
            else if (declaration instanceof TypedDeclaration &&
                    td instanceof TypedDeclaration) {
                return declaration.getRefinedDeclaration()
                        .equals(td.getRefinedDeclaration());
            }
            else {
                return false;
            }
        }
        
        private boolean inheritedBy(Declaration td) {
            if (declaration instanceof TypeDeclaration &&
                    td instanceof TypeDeclaration) {
                return ((TypeDeclaration) td)
                        .inherits((TypeDeclaration) declaration);
            }
            else if (declaration instanceof TypedDeclaration &&
                    td instanceof TypedDeclaration) {
                return declaration.getRefinedDeclaration()
                        .equals(td.getRefinedDeclaration());
            }
            else {
                return false;
            }
        }
        
        private CeylonHierarchyNode getSubtypePathNode(
                Declaration declaration) {
            CeylonHierarchyNode n = 
                    subtypesOfSupertypes.get(declaration);
            if (n==null) {
                n = new CeylonHierarchyNode(declaration);
                subtypesOfSupertypes.put(declaration, n);
            }
            return n;
        }

        private CeylonHierarchyNode getSubtypeHierarchyNode(
                Declaration declaration) {
            CeylonHierarchyNode n = 
                    subtypesOfAllTypes.get(declaration);
            if (n==null) {
                n = new CeylonHierarchyNode(declaration);
                subtypesOfAllTypes.put(declaration, n);
            }
            return n;
        }

        private CeylonHierarchyNode getSupertypeHierarchyNode(
                Declaration declaration) {
            CeylonHierarchyNode n = 
                    supertypesOfAllTypes.get(declaration);
            if (n==null) {
                n = new CeylonHierarchyNode(declaration);
                supertypesOfAllTypes.put(declaration, n);
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
            
            Set<Module> allModules = collectModules();
            
            monitor.worked(10000);
            
            Set<Package> packages = new HashSet<Package>();
            int ams = allModules.size();
            for (Module module: allModules) {
                collectPackages(packages, module);
                monitor.worked(10000/ams);
                if (monitor.isCanceled()) return;
            }
    
            CeylonHierarchyNode node = 
                    getSubtypePathNode(declaration);
            node.setFocus(true);
            subtypesOfAllTypes.put(declaration, node);

            Declaration root = declaration;
            depthInHierarchy = 0;
            if (declaration instanceof TypeDeclaration) {
                root = collectSuperclasses(root);
            }
            else if (declaration instanceof TypedDeclaration) {
                root = collectOverriddenMembers(root);
            }

            hierarchyRoot = getSubtypePathNode(root);
            subtypesRoot = getSubtypeHierarchyNode(declaration);
            subtypesRoot.setFocus(true);
            supertypesRoot = getSupertypeHierarchyNode(declaration);
            supertypesRoot.setFocus(true);
            
            if (monitor.isCanceled()) return;
            
            IEditorPart part = 
                    site.getPage().getActiveEditor();
            
            List<Type> signature = getSignature(declaration);
            int ps = packages.size();
            for (Package p: packages) { //workaround CME
                int ms = p.getMembers().size();
                monitor.subTask("scanning " + p.getNameAsString());
                for (Unit u: p.getUnits()) {
                    try {
                        //TODO: unshared inner types get 
                        //      missed for binary modules
                        for (Declaration d: u.getDeclarations()) {
                            if (!isFiltered(d)) {
                                d = replaceWithCurrentEditorDeclaration(
                                        part, p, d); //TODO: not enough to catch *new* subtypes in the dirty editor
                                if (d instanceof ClassOrInterface || 
                                        d instanceof TypeParameter) {
                                    try {
                                        if (declaration instanceof TypeDeclaration) {
                                            addTypeToHierarchy(d);
                                        }
                                        else if (declaration instanceof TypedDeclaration) {
                                            addMemberToHierarchy(signature, d);
                                        }
                                    }
                                    catch (Exception e) {
                                        String qn = d.getQualifiedNameString();
                                        System.err.println(qn);
                                        throw e;
                                    }
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

        private Declaration collectOverriddenMembers(Declaration root) {
            Declaration memberDec = declaration;
            Declaration refinedDeclaration = 
                    declaration.getRefinedDeclaration();
            Scope container = declaration.getContainer();
            if (container instanceof TypeDeclaration) {
                TypeDeclaration dec = 
                        (TypeDeclaration) container;
                List<Type> signature = 
                        getSignature(declaration);
                depthInHierarchy++;
                root = memberDec;
                //first walk up the superclass hierarchy
                String decname = declaration.getName();
                while (dec!=null) {
                    TypeDeclaration mc = 
                            (TypeDeclaration) 
                                memberDec.getContainer();
                    TypeDeclaration rc = 
                            (TypeDeclaration) 
                                refinedDeclaration.getContainer();
                    Type extended = 
                            dec.getExtendedType();
                    if (extended==null) {
                        dec = null;
                    }
                    else {
                        TypeDeclaration superDec = 
                                extended.getDeclaration();
                        Declaration superMemberDec = 
                                superDec.getDirectMember(
                                        decname, 
                                        signature, false);
                        if (superMemberDec!=null && 
                                !isAbstraction(superMemberDec) &&
                                superMemberDec.getRefinedDeclaration()
                                    .equals(refinedDeclaration)) {
                            List<Declaration> directlyInheritedMembers = 
                                    getInterveningRefinements(
                                            decname, 
                                            signature, 
                                            refinedDeclaration,
                                            mc, superDec);
                            List<Declaration> all = 
                                    getInterveningRefinements(
                                            decname, 
                                            signature, 
                                            refinedDeclaration,
                                            mc, rc);
                            for (Declaration d: all) {
                                TypeDeclaration dtd = 
                                        (TypeDeclaration) 
                                            d.getContainer();
                                if (!superDec.inherits(dtd)) {
                                    getSubtypePathNode(superMemberDec)
                                        .setNonUnique(true);
                                }
                            }
                            directlyInheritedMembers.remove(superMemberDec);
                            if (directlyInheritedMembers.size()>0) {
                                getSubtypePathNode(superMemberDec)
                                    .setNonUnique(true);
                            }
                            getSubtypePathNode(superMemberDec)
                                .addChild(getSubtypePathNode(memberDec));
                            depthInHierarchy++;
                            root = superMemberDec;
                            memberDec = superMemberDec;
                        }
                        //TODO else add an "empty" node to the hierarchy like in JDT
                        dec = superDec;
                    }
                }
                //now look at the very top of the hierarchy, even if it is an interface
                if (!memberDec.equals(refinedDeclaration)) {
                    TypeDeclaration mc = 
                            (TypeDeclaration) 
                                memberDec.getContainer();
                    TypeDeclaration rc = 
                            (TypeDeclaration) 
                                refinedDeclaration.getContainer();
                    List<Declaration> directlyInheritedMembers = 
                            getInterveningRefinements(
                                    decname, 
                                    signature, 
                                    declaration.getRefinedDeclaration(),
                                    mc, rc);
                    directlyInheritedMembers.remove(refinedDeclaration);
                    if (directlyInheritedMembers.size()>1) {
                        //multiple intervening interfaces
                        CeylonHierarchyNode n = 
                                new CeylonHierarchyNode(memberDec);
                        n.setMultiple(true);
                        n.addChild(getSubtypePathNode(memberDec));
                        getSubtypePathNode(refinedDeclaration)
                            .addChild(n);
                    }
                    else if (directlyInheritedMembers.size()==1) {
                        //exactly one intervening interface
                        Declaration idec = 
                                directlyInheritedMembers.get(0);
                        getSubtypePathNode(idec)
                            .addChild(getSubtypePathNode(memberDec));
                        getSubtypePathNode(refinedDeclaration)
                            .addChild(getSubtypePathNode(idec));
                    }
                    else {
                        //no intervening interfaces
                        getSubtypePathNode(refinedDeclaration)
                            .addChild(getSubtypePathNode(memberDec));
                    }
                    root = refinedDeclaration;
                }
            }
            return root;
        }

        private Declaration collectSuperclasses(Declaration root) {
            TypeDeclaration dec = 
                    (TypeDeclaration) declaration;
            while (dec!=null) {
                depthInHierarchy++;
                root = dec;
                Type extended = dec.getExtendedType();
                if (extended!=null) {
                    TypeDeclaration superDec = 
                            extended.getDeclaration();
                    if (!dec.getSatisfiedTypes()
                            .isEmpty()) {
                        getSubtypePathNode(superDec)
                            .setNonUnique(true);
                    }
                    getSubtypePathNode(superDec)
                        .addChild(getSubtypePathNode(dec));
                    dec = superDec;
                }
                else {
                    dec = null;
                }
            }
            return root;
        }

        private void collectPackages(Set<Package> packages, 
                Module module) {
            if (!filters.isFiltered(module)) {
                for (Package pack: 
                        module.getPackages()) {
                    if (!filters.isFiltered(pack)) {
                        String packageModuleName = 
                                pack.getModule()
                                    .getNameAsString();
                        if ((!excludeJDK || 
                                !JDKUtils.isJDKModule(
                                        packageModuleName)) && 
                            (!excludeOracleJDK || 
                                !JDKUtils.isOracleJDKModule(
                                        packageModuleName))) {
                            packages.add(pack);
                        }
                    }
                }
            }
        }

        private Set<Module> collectModules() {
            Unit unit = declaration.getUnit();
            Module currentModule = 
                    unit.getPackage().getModule();
            Set<Module> allModules = new HashSet<Module>();
            if (currentModule instanceof JDTModule) {
                JDTModule jdtCurrentModule = (JDTModule) currentModule;
                List<JDTModule> moduleInAllProjects = new ArrayList<JDTModule>(); 
                moduleInAllProjects.add(jdtCurrentModule);
                moduleInAllProjects.addAll(jdtCurrentModule.getModuleInReferencingProjects());
                for (JDTModule jdtModule : moduleInAllProjects) {
                    allModules.add(jdtModule);
                    for (Module relatedModule : jdtModule.getReferencingModules()) {
                        allModules.add(relatedModule);
                    }
                    for (Module relatedModule : jdtModule.getTransitiveDependencies()) {
                        allModules.add(relatedModule);
                    }
                }
            }
            return allModules;
        }

        private void addMemberToHierarchy(
                List<Type> signature, Declaration dec) {
            Declaration refinedDeclaration = 
                    declaration.getRefinedDeclaration();
            TypeDeclaration td = 
                    (TypeDeclaration) dec;
            String name = declaration.getName();
            Declaration member = 
                    td.getDirectMember(name, 
                            signature, false);
            if (member!=null) {
                Declaration rd = 
                        member.getRefinedDeclaration();
                if (rd!=null && 
                        rd.equals(refinedDeclaration)) {
                    TypeDeclaration rdc = 
                            (TypeDeclaration) 
                                refinedDeclaration.getContainer();
                    List<Declaration> refinements = 
                            getInterveningRefinements(
                                    name, 
                                    signature, 
                                    refinedDeclaration, 
                                    td, rdc);
                    //TODO: keep the directly refined declarations in the model
                    //      (get the typechecker to set this up)
                    for (Declaration candidate: refinements) {
                        TypeDeclaration cc = 
                                (TypeDeclaration) 
                                    candidate.getContainer();
                        List<Declaration> refs = 
                                getInterveningRefinements(
                                        name, 
                                        signature, 
                                        refinedDeclaration, 
                                        td, cc);
                        if (refs.size()==1) {
                            add(member, candidate);
                        }
                    }
                }
            }
        }

        private void addTypeToHierarchy(Declaration dec) {
            TypeDeclaration td = (TypeDeclaration) dec;
            if (!(td instanceof TypeParameter)) {
                Type et = td.getExtendedType();
                if (et!=null) {
                    add(td, et.getDeclaration());
                }
                for (Type st: td.getSatisfiedTypes()) {
                    if (st!=null) {
                        add(td, st.getDeclaration());
                    }
                }
            }
        }

        private Declaration replaceWithCurrentEditorDeclaration(
                IEditorPart part, Package p, Declaration d) {
            if (part instanceof CeylonEditor && part.isDirty()) {
                CeylonEditor editor = (CeylonEditor) part;
                CompilationUnit rootNode = 
                        editor.getParseController()
                                .getLastCompilationUnit();
                if (rootNode!=null) {
                    Unit unit = rootNode.getUnit();
                    if (unit!=null && 
                            unit.getPackage().equals(p)) {
                        String name = 
                                d.getQualifiedNameString();
                        Declaration result = 
                                getDeclarationInUnit(name, 
                                        unit);
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
                return label + 
                        " \u2014 refinement hierarchy of " + 
                        description;
            case SUPERTYPES:
                return label + 
                        " \u2014 generalizations of " + 
                        description;
            case SUBTYPES:
                return label + 
                        " \u2014 refinements of " + 
                        description;
            default:
                throw new RuntimeException();
            }
        }
        else {
            switch (getMode()) {
            case HIERARCHY:
                return label + 
                        " \u2014 type hierarchy of " + 
                        description;
            case SUPERTYPES:
                return label + 
                        " \u2014 supertypes of " + 
                        description;
            case SUBTYPES:
                return label + 
                        " \u2014 subtypes of " + 
                        description;
            default:
                throw new RuntimeException();
            }
        }
    }
    
    private Filters filters = new Filters() {
        @Override
        protected String enableExtraFiltersPref() {
            return ENABLE_HIERARCHY_FILTERS;
        }
        @Override
        protected String extraFiltersPref() {
            return HIERARCHY_FILTERS;
        }
    };
    
    private boolean isFiltered(Declaration declaration) {
//        if (excludeDeprecated && 
//                declaration.isDeprecated()) {
//            return true;
//        }
        if (declaration.isAnnotation() &&
                declaration.getName().contains("__")) {
            //actually what we should really do is filter
            //out all constructors for Java annotations
            return true;
        }
        return filters.isFiltered(declaration);
    }

}