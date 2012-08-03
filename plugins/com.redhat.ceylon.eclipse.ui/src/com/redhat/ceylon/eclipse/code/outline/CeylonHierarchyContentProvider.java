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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

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
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public final class CeylonHierarchyContentProvider 
        implements ITreeContentProvider {
	
	private final CeylonEditor editor;
	
	private final class CeylonHierarchyBuilder implements IRunnableWithProgress {
		
		private final Declaration declaration;
		Map<Declaration, CeylonHierarchyNode> subtypesOfSupertypes = new HashMap<Declaration, CeylonHierarchyNode>();
		Map<Declaration, CeylonHierarchyNode> subtypesOfAllTypes = new HashMap<Declaration, CeylonHierarchyNode>();
		Map<Declaration, CeylonHierarchyNode> supertypesOfAllTypes = new HashMap<Declaration, CeylonHierarchyNode>();

		private CeylonHierarchyBuilder(Declaration declaration) {
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

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException,
				InterruptedException {
			
			monitor.beginTask("Building type hierarchy", 100000);
			
			Modules modules = editor.getParseController().getTypeChecker()
		            .getContext().getModules();

			boolean isFromUnversionedModule = declaration.getUnit().getPackage()
					.getModule().getVersion()==null;

			Set<Module> allModules = modules.getListOfModules();
			monitor.worked(10000);
			
			Set<Package> packages = new HashSet<Package>();
			for (Module m: allModules) {
				if (m.getVersion()!=null || isFromUnversionedModule) {
					packages.addAll(m.getPackages());
					monitor.worked(30000/allModules.size());
					if (monitor.isCanceled()) return;
				}
			}

			subtypesOfAllTypes.put(declaration, getSubtypePathNode(declaration));
			
		    Declaration dec = declaration;
		    Declaration superDec;
		    do {
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
			
		    for (Module m: allModules) {
		    	if (m.getVersion()!=null || isFromUnversionedModule) {
		        for (Package p: packages) { //workaround CME
		            for (Unit u: p.getUnits()) {
		            	try {
		            		for (Declaration d: u.getDeclarations()) {
		            			if (d instanceof ClassOrInterface) {
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
		            					Declaration mem = td.getDirectMember(declaration.getName(), null);
		            					if (mem!=null) {
		            						for (Declaration id: td.getInheritedMembers(declaration.getName())) {
		            							add(mem, id);
		            						}
		            					}                        		
		            				}
		            			}
		            		}
		            	}
		            	catch (Exception e) {
		            		e.printStackTrace();
		            	}
		            }
			        monitor.worked(60000/packages.size());
					if (monitor.isCanceled()) return;
		        }
		    	}
		    }
		    monitor.done();
		}
	}

	public static final class RootNode {
		Declaration declaration;
		public RootNode(Declaration declaration) {
			this.declaration = declaration;
		}
	}
	
	HierarachyMode mode = HierarachyMode.HIERARCHY;
	
	Declaration declaration;
    private CeylonHierarchyNode hierarchyRoot;
    private CeylonHierarchyNode supertypesRoot;
    private CeylonHierarchyNode subtypesRoot;
    
	CeylonHierarchyContentProvider(CeylonEditor editor) {
		this.editor = editor;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput!=null) {
			declaration = ((RootNode) newInput).declaration;
			try {
				editor.getSite().getWorkbenchWindow().run(true, true, 
						new CeylonHierarchyBuilder(declaration));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	boolean isShowingRefinements() {
		return !(declaration instanceof TypeDeclaration);
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
	    if (parentElement instanceof RootNode) {
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
	    	List<CeylonHierarchyNode> children = ((CeylonHierarchyNode) parentElement).getChildren();
			return children.toArray(new CeylonHierarchyNode[children.size()]);
	    }
	    else {
	    	return null;
	    }
	}
}