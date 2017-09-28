package org.eclipse.ceylon.ide.eclipse.code.outline;

import static org.eclipse.ceylon.ide.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.ENABLE_HIERARCHY_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.HIERARCHY_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.util.ModelProxy.getDeclarationInUnit;
import static org.eclipse.ceylon.model.cmr.JDKUtils.isJDKModule;
import static org.eclipse.ceylon.model.cmr.JDKUtils.isOracleJDKModule;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.getInterveningRefinements;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.getSignature;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isAbstraction;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isVariadic;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.util.Filters;
import org.eclipse.ceylon.ide.eclipse.util.ModelProxy;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.model.typechecker.model.ClassOrInterface;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypeParameter;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

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
                    site.getPage()
                        .getActiveEditor();
            
            List<Type> signature = getSignature(declaration);
            boolean isVariadic = isVariadic(declaration);
            int ps = packages.size();
            for (Package pack: packages) { //workaround CME
                List<Declaration> members =
                        //IMPORTANT: call getMembers() to 
                        //force lazy loading of all Units!
                        pack.getMembers();
                int ms = members.size();
                monitor.subTask("scanning " 
                        + pack.getNameAsString());
                //iterate over units and then declarations
                //in order to pick up nested declarations,
                //unlike in OpenDeclarationDialog where we
                //iterate over Package.getMembers() because
                //we only want toplevels
                for (Unit unit: pack.getUnits()) {
                    try {
                        //TODO: unshared inner types get 
                        //      missed for binary modules
                        for (Declaration d: unit.getDeclarations()) {
                            if (!isFiltered(d)) {
                                d = replaceWithCurrentEditorDeclaration(
                                        part, pack, d); //TODO: not enough to catch *new* subtypes in the dirty editor
                                if (d instanceof ClassOrInterface || 
                                    d instanceof TypeParameter) {
                                    try {
                                        if (declaration instanceof TypeDeclaration) {
                                            addTypeToHierarchy(d);
                                        }
                                        else if (declaration instanceof TypedDeclaration) {
                                            addMemberToHierarchy(signature, isVariadic, d);
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
                boolean isVariadic = 
                        isVariadic(declaration);
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
                                            isVariadic,
                                            refinedDeclaration,
                                            mc, superDec);
                            List<Declaration> all = 
                                    getInterveningRefinements(
                                            decname, 
                                            signature,
                                            isVariadic,
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
                                    isVariadic,
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
            if (includeModule(module)) {
                //TODO: I guess we don't need to clone the 
                //      package list before iterating, like 
                //      what we do in OpenDeclarationDialog, 
                //      because all we're doing is adding
                //      the packages to a Set
                for (Package pack: module.getPackages()) {
                    if (!filters.isFiltered(pack)) {
                        packages.add(pack);
                    }
                }
            }
        }
        
        private boolean includeModule(Module module) {
            return !excluded(module)
                    && !filters.isFiltered(module) 
                    && module.isAvailable();
        }
        
        private boolean excluded(Module module) {
            String moduleName = module.getNameAsString();
            return 
                excludeJDK &&
                    isJDKModule(moduleName) || 
                excludeOracleJDK && 
                    isOracleJDKModule(moduleName);
        }

        private Set<Module> collectModules() {
            Unit unit = declaration.getUnit();
            Module currentModule = 
                    unit.getPackage()
                        .getModule();
            Set<Module> allModules = new HashSet<Module>();
            if (currentModule instanceof BaseIdeModule) {
                BaseIdeModule jdtCurrentModule = 
                        (BaseIdeModule) currentModule;
                List<BaseIdeModule> moduleInAllProjects = 
                        new ArrayList<BaseIdeModule>();
                moduleInAllProjects.add(jdtCurrentModule);
                moduleInAllProjects.addAll(jdtCurrentModule.getModuleInReferencingProjectsAsJavaList());
                for (BaseIdeModule ideModule: moduleInAllProjects) {
                    allModules.add(ideModule);
                    allModules.addAll(ideModule.getReferencingModulesAsJavaList());
                    allModules.addAll(ideModule.getTransitiveDependenciesAsJavaList());
                }
            }
            return allModules;
        }

        private void addMemberToHierarchy(
                List<Type> signature, boolean isVariadic, Declaration dec) {
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
                                    isVariadic,
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
                                        isVariadic,
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