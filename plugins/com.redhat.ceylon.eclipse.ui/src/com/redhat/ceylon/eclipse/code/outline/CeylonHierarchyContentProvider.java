package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.EditorAnnotationService.getRefinedDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

final class CeylonHierarchyContentProvider 
        implements ITreeContentProvider {
	
	public final Object root = new Object();
	
	boolean reverse = false;
	
	private Declaration declaration;
	private Declaration voidDeclaration;
    final private Map<Declaration, Declaration> subtypesOfSupertypes = new HashMap<Declaration, Declaration>();
    final private Map<Declaration, Set<Declaration>> subtypesOfAllTypes = new HashMap<Declaration, Set<Declaration>>();
    final private Map<Declaration, Set<Declaration>> supertypesOfAllTypes = new HashMap<Declaration, Set<Declaration>>();

	Object init(Declaration declaration, CeylonEditor editor) {
		Modules modules = editor.getParseController().getTypeChecker()
                .getContext().getModules();
        this.declaration = declaration;
        while (declaration!=null) {
            this.voidDeclaration = declaration;
            Declaration sd;
            if (declaration instanceof TypeDeclaration) {
                sd = ((TypeDeclaration) declaration).getExtendedTypeDeclaration();
            }
            else if (declaration instanceof TypedDeclaration){
                sd = getRefinedDeclaration(declaration);
            }
            else {
                sd = null;
            }
            subtypesOfSupertypes.put(sd, declaration);
            declaration = sd;
        }
        for (Module m: modules.getListOfModules()) {
            for (Package p: new ArrayList<Package>(m.getPackages())) { //workaround CME
                for (Unit u: p.getUnits()) {
                    for (Declaration d: u.getDeclarations()) {
                        if (d instanceof ClassOrInterface) {
                            TypeDeclaration td = (TypeDeclaration) d;
                            ClassOrInterface etd = td.getExtendedTypeDeclaration();
                            if (etd!=null) {
                                add(td, etd);
                            }
                            for (TypeDeclaration std: td.getSatisfiedTypeDeclarations()) {
                                add(td, std);
                            }
                        }
                        else if (d instanceof TypedDeclaration) {
                            Declaration rd = getRefinedDeclaration(d);
                            if (rd!=null) {
                                add(rd, d);
                            }
                        }
                    }
                }
            }
        }
        return root;
	}

	public void add(Declaration td, Declaration etd) {
		Set<Declaration> list = subtypesOfAllTypes.get(etd);
		if (list==null) {
		    list = new HashSet<Declaration>();
		    subtypesOfAllTypes.put(etd, list);
		}
		list.add(td);
		if (!(td instanceof Interface)||!(etd instanceof Class)||td==declaration) {
			Set<Declaration> list2 = supertypesOfAllTypes.get(td);
			if (list2==null) {
				list2 = new HashSet<Declaration>();
				supertypesOfAllTypes.put(td, list2);
			}
			list2.add(etd);
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

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
	    //return new Object[] { inputElement };
	    return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
	    if (reverse) {
		    if (parentElement==root) {
		        return new Object[] { declaration };
		    }
	        Set<Declaration> sdl = supertypesOfAllTypes.get(parentElement);
	        if (sdl==null) {
	            return new Object[0];
	        }
	        else {
	            return sdl.toArray();
	        }
	    }
	    else {
		    if (parentElement==root) {
		        return new Object[] { voidDeclaration };
		    }
	    	Declaration sd = subtypesOfSupertypes.get(parentElement);
	    	if (sd!=null) {
	    		return new Object[] { sd };
	    	}
	    	else {
	    		Set<Declaration> sdl = subtypesOfAllTypes.get(parentElement);
	    		if (sdl==null) {
	    			return new Object[0];
	    		}
	    		else {
	    			return sdl.toArray();
	    		}
	    	}
	    }
	}
}