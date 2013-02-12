package com.redhat.ceylon.eclipse.core.builder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ExternalUnit;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.model.CrossProjectSourceFile;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.eclipse.core.model.SourceFile;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;

public class UnitDependencyVisitor extends Visitor {
    
    private final PhasedUnit phasedUnit;
    private Set<Declaration> alreadyDone;
    
    public UnitDependencyVisitor(PhasedUnit phasedUnit) {
        this.phasedUnit = phasedUnit;
        alreadyDone = new HashSet<Declaration>();
    }
    
    private void storeDependency(Declaration d) {
        if (d!=null && (d instanceof UnionType || 
                        d instanceof IntersectionType || 
                        !alreadyDone.contains(d))) {
            if (!(d instanceof UnionType || 
                        d instanceof IntersectionType)) {
                alreadyDone.add(d);
            }
            if (d instanceof TypeDeclaration) {
                TypeDeclaration td = (TypeDeclaration) d;
                storeDependency(td.getExtendedTypeDeclaration());
                for (TypeDeclaration st: td.getSatisfiedTypeDeclarations()) {
                    storeDependency(st);
                }
                List<TypeDeclaration> caseTypes = td.getCaseTypeDeclarations();
                if (caseTypes!=null) {
                    for (TypeDeclaration ct: caseTypes) {
                        storeDependency(ct);
                    }
                }
            }
            if (d instanceof TypedDeclaration) {
                //TODO: is this really necessary?
                storeDependency(((TypedDeclaration) d).getTypeDeclaration());
            }
            Declaration rd = d.getRefinedDeclaration();
            if (rd!=d) {
                storeDependency(rd); //this one is needed for default arguments, I think
            }
            Unit declarationUnit = d.getUnit();
            if (declarationUnit != null) {
            	String moduleName = declarationUnit.getPackage().getModule().getNameAsString();
            	if (!moduleName.equals("ceylon.language") && 
            			!JDKUtils.isJDKModule(moduleName)
            			&& !JDKUtils.isOracleJDKModule(moduleName)) { 
            	    //TODO: also filter out src archives from external repos
            	    //      Now with specialized units we could do : 
            	    //         if (unit instanceOf ProjectSourceFile 
            	    //             || unit instanceOf JavaCompilationUnit)
            	    //      Might be necesary though to manage a specific case 
            	    //      for cross-project dependencies when they will be managed 
            	    //      and not from source archives anymore
            		Unit currentUnit = phasedUnit.getUnit();
            		String currentUnitPath = phasedUnit.getUnitFile().getPath();
            		String currentUnitName = currentUnit.getFilename();
            		String dependedOnUnitName = declarationUnit.getFilename();
            		String currentUnitPackage = currentUnit.getPackage().getNameAsString();
            		String dependedOnPackage = declarationUnit.getPackage().getNameAsString();
            		if (!dependedOnUnitName.equals(currentUnitName) ||
            				!dependedOnPackage.equals(currentUnitPackage)) {
            			if (! (declarationUnit instanceof SourceFile)) {
            				//TODO: this does not seem to work for cross-project deps
                            //TODO: All the dependencies to class files are also added... It is really useful ?
                            // I assume in the case of the classes in the classes or exploded dirs, it might be,
                            // but not sure it is also used not in the case of jar-located classes 
            				declarationUnit.getDependentsOf().add(currentUnitPath);
            			} 
            			else {
            			    if (declarationUnit instanceof ProjectSourceFile) {
            			        ProjectSourceFile dependedOnSourceFile = (ProjectSourceFile) declarationUnit;
            			        ProjectPhasedUnit dependedOnPhasedUnit = dependedOnSourceFile.getPhasedUnit();
                                if (dependedOnPhasedUnit != null && dependedOnPhasedUnit.getUnit() != null) {
                                    dependedOnPhasedUnit.getUnit().getDependentsOf().add(currentUnitPath);
                                }
            			    }
            			    else if (declarationUnit instanceof CrossProjectSourceFile) {
            			        CrossProjectPhasedUnit crossProjectPhasedUnits = ((CrossProjectSourceFile) declarationUnit).getPhasedUnit();
            			        if (crossProjectPhasedUnits != null) {
            			            ProjectPhasedUnit dependedOnPhasedUnit = crossProjectPhasedUnits.getOriginalProjectPhasedUnit();
                                    if (dependedOnPhasedUnit != null && dependedOnPhasedUnit.getUnit() != null) {
                                        dependedOnPhasedUnit.getUnit().getDependentsOf().add(currentUnitPath);
                                    }
            			        }
            			    }
            			    else if (declarationUnit instanceof ExternalSourceFile) {
            			        // Don't manage them : they cannot change ... Well they might if we were using these dependencies to manage module 
            			        // removal. But since module removal triggers a classpath container update and so a full build, it's not necessary.
            			        // Might change in the future 
            			    }
            			    else {
                                assert(false);
            			    }
            			}
            		}
            	}
            }
        }
    }
    
    @Override
    public void visit(Tree.MemberOrTypeExpression that) {
        storeDependency(that.getDeclaration());
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.NamedArgument that) {
        //TODO: is this really necessary?
        storeDependency(that.getParameter());
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.SequencedArgument that) {
        //TODO: is this really necessary?
        storeDependency(that.getParameter());
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.PositionalArgument that) {
        //TODO: is this really necessary?
        storeDependency(that.getParameter());
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.Type that) {
        ProducedType tm = that.getTypeModel();
        if (tm!=null) {
            storeDependency(tm.getDeclaration());
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.ImportMemberOrType that) {
        storeDependency(that.getDeclarationModel());
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.TypeArguments that) {
        //TODO: is this really necessary?
        List<ProducedType> tms = that.getTypeModels();
        if (tms!=null) {
            for (ProducedType pt: tms) {
                storeDependency(pt.getDeclaration());
            }
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.Term that) {
        //TODO: is this really necessary?
        ProducedType tm = that.getTypeModel();
        if (tm!=null) {
            storeDependency(tm.getDeclaration());
        }
        super.visit(that);
    }
    
}
