package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.compiler.typechecker.io.impl.Helper.computeRelativePath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.java.tools.LanguageCompiler.PhasedUnitsManager;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.ModelState;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.loader.SourceClass;

final class JdtPhasedUnitsManager implements PhasedUnitsManager {
	private final JDTModelLoader modelLoader;
	private final IProject project;
	private final TypeChecker typeChecker;

	JdtPhasedUnitsManager(JDTModelLoader modelLoader,
			IProject project, TypeChecker typeChecker) {
		this.modelLoader = modelLoader;
		this.project = project;
		this.typeChecker = typeChecker;
	}

	@Override
	public ModuleManager getModuleManager() {
	    return typeChecker.getPhasedUnits().getModuleManager();
	}

	@Override
	public void resolveDependencies() {
	}

	@Override
	public PhasedUnit getExternalSourcePhasedUnit(
	        VirtualFile srcDir, VirtualFile file) {
	    return typeChecker.getPhasedUnits()
	    		.getPhasedUnitFromRelativePath(computeRelativePath(file, srcDir));
	}

	@Override
	public Iterable<PhasedUnit> getPhasedUnitsForExtraPhase(
	        List<PhasedUnit> sourceUnits) {
	    if (CeylonBuilder.getModelState(project).equals(ModelState.Compiled) || 
	            CeylonBuilder.compileWithJDTModelLoader(project)) {
	        return sourceUnits;
	    }

	    List<PhasedUnit> dependencies = new ArrayList<PhasedUnit>();
	    for (PhasedUnits phasedUnits : typeChecker.getPhasedUnitsOfDependencies()) {
	        for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
	            dependencies.add(phasedUnit);
	        }
	    }
	    
	    for (PhasedUnit dependency : dependencies) {
	        dependency.analyseTypes();
	    }
	    for (PhasedUnit dependency : dependencies) {
	        dependency.analyseFlow();
	    }
	    List<PhasedUnit> allPhasedUnits = new ArrayList<PhasedUnit>();
	    allPhasedUnits.addAll(dependencies);
	    allPhasedUnits.addAll(sourceUnits);
	    
	    ClassMirror objectMirror = modelLoader.lookupClassMirror("ceylon.language.Object");
	    if (objectMirror instanceof SourceClass) {
	        Declaration objectClass = ((SourceClass) objectMirror).getModelDeclaration();
	        if (objectClass != null) {
	            Declaration hashMethod = objectClass.getDirectMember("hash", 
	            		Collections.<ProducedType>emptyList());
	            if (hashMethod instanceof TypedDeclaration) {
	                ((TypedDeclaration)hashMethod).getType().setUnderlyingType("int");
	            }
	        }
	        
	    }
	    return allPhasedUnits;
	}

	@Override
	public void extraPhasesApplied() {
	    CeylonBuilder.modelStates.put(project, ModelState.Compiled);
	}
}