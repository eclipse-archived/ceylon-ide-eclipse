package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.AdditionalAnnotationCreator.getRefinedDeclaration;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

final class CeylonHierarchyBuilder implements IRunnableWithProgress {
	
    private final CeylonHierarchyContentProvider ceylonHierarchyContentProvider;
    private final Declaration declaration;
    
    private CeylonHierarchyNode hierarchyRoot;
    private CeylonHierarchyNode supertypesRoot;
    private CeylonHierarchyNode subtypesRoot;
    
    private int depthInHierarchy;
    
    private final Map<Declaration, CeylonHierarchyNode> subtypesOfSupertypes = new HashMap<Declaration, CeylonHierarchyNode>();
    private final Map<Declaration, CeylonHierarchyNode> subtypesOfAllTypes = new HashMap<Declaration, CeylonHierarchyNode>();
    private final Map<Declaration, CeylonHierarchyNode> supertypesOfAllTypes = new HashMap<Declaration, CeylonHierarchyNode>();

	CeylonHierarchyBuilder(CeylonHierarchyContentProvider ceylonHierarchyContentProvider, Declaration declaration) {
		this.ceylonHierarchyContentProvider = ceylonHierarchyContentProvider;
        this.declaration = declaration;
	}

	private void add(Declaration td, Declaration etd) {
		getSubtypeHierarchyNode(etd).addChild(getSubtypeHierarchyNode(td));
		if (!(td instanceof Interface)||!(etd instanceof Class)||td==declaration) {
			getSupertypeHierarchyNode(td).addChild(getSupertypeHierarchyNode(etd));
		}
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
	
	CeylonHierarchyNode getHierarchyRoot() {
        return hierarchyRoot;
    }
	
	CeylonHierarchyNode getSubtypesRoot() {
        return subtypesRoot;
    }
	
	CeylonHierarchyNode getSupertypesRoot() {
        return supertypesRoot;
    }
	
	int getDepthInHierarchy() {
        return depthInHierarchy;
    }
	
	public Declaration getDeclaration() {
        return declaration;
    }

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		
		monitor.beginTask("Building hierarchy", 100000);
		
		Modules modules = ceylonHierarchyContentProvider.editor
		        .getParseController().getTypeChecker()
	            .getContext().getModules();

		boolean isFromUnversionedModule = declaration.getUnit().getPackage()
				.getModule().getVersion()==null;

		Set<Module> allModules = modules.getListOfModules();
		monitor.worked(10000);
		
		Set<Package> packages = new HashSet<Package>();
		int ams = allModules.size();
		for (Module m: allModules) {
			if (m.getVersion()!=null || isFromUnversionedModule) {
				packages.addAll(m.getPackages());
				monitor.worked(20000/ams);
				if (monitor.isCanceled()) return;
			}
		}

		subtypesOfAllTypes.put(declaration, getSubtypePathNode(declaration));
		
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
	        else if (declaration instanceof TypedDeclaration){
				superDec = getRefinedDeclaration(dec);
				if (superDec!=null) {
					List<Declaration> directlyInheritedMembers = ((TypeDeclaration)dec.getContainer())
							.getInheritedMembers(dec.getName());
					if (!directlyInheritedMembers.contains(superDec)) {
						CeylonHierarchyNode n = new CeylonHierarchyNode(null);
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
		
        int ps = packages.size();
        for (Package p: packages) { //workaround CME
            int ms = p.getMembers().size();
            monitor.subTask("Building hierarchy - scanning " + p.getNameAsString());
            for (Unit u: p.getUnits()) {
                try {
                    for (Declaration d: u.getDeclarations()) {
                        if (d instanceof ClassOrInterface) {
                            try {
                                if (declaration instanceof TypeDeclaration) {
                                    TypeDeclaration td = (TypeDeclaration) d;
                                    ClassOrInterface etd = td.getExtendedTypeDeclaration();
                                    if (etd!=null) {
                                        add(td, etd);
                                    }
                                    for (TypeDeclaration std: td.getSatisfiedTypeDeclarations()) {
                                        add(td, std);
                                    }
                                }
                                else if (declaration instanceof TypedDeclaration) {
                                    TypeDeclaration td = (TypeDeclaration) d;
                                    //TODO: keep the directly refined declarations in the model
                                    //      (get the typechecker to set this up)
                                    Declaration mem = td.getDirectMember(declaration.getName(), null, false);
                                    if (mem!=null) {
                                        for (Declaration id: td.getInheritedMembers(declaration.getName())) {
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
                        monitor.worked(70000/ps/ms);
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
}