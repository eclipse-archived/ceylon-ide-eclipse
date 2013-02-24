package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.cmr.api.JDKUtils.isJDKModule;
import static com.redhat.ceylon.cmr.api.JDKUtils.isOracleJDKModule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CrossProjectSourceFile;
import com.redhat.ceylon.eclipse.core.model.EditedSourceFile;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.JavaClassFile;
import com.redhat.ceylon.eclipse.core.model.JavaCompilationUnit;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;

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
            storeDependency(declarationUnit, phasedUnit);
            storeDependency(declarationUnit.getPackage().getUnit(), phasedUnit);
            storeDependency(declarationUnit.getPackage().getModule().getUnit(), phasedUnit);
        }
    }

    static void storeDependency(Unit declarationUnit, PhasedUnit phasedUnit) {
        if (declarationUnit != null) {
        	String moduleName = declarationUnit.getPackage().getModule().getNameAsString();
        	if (!moduleName.equals("ceylon.language") && 
        			!isJDKModule(moduleName) && !isOracleJDKModule(moduleName)) { 
        		Unit currentUnit = phasedUnit.getUnit();
        		String currentUnitPath = phasedUnit.getUnitFile().getPath();
        		String currentUnitName = currentUnit.getFilename();
        		String dependedOnUnitName = declarationUnit.getFilename();
        		String currentUnitPackage = currentUnit.getPackage().getNameAsString();
        		String dependedOnPackage = declarationUnit.getPackage().getNameAsString();
        		if (!dependedOnUnitName.equals(currentUnitName) ||
        				!dependedOnPackage.equals(currentUnitPackage)) {
        		    
        		    // WOW : Ceylon Abstract Data types and swith case would be cool here ;) 
        		    if (declarationUnit instanceof ProjectSourceFile) {
                        declarationUnit.getDependentsOf().add(currentUnitPath);
        		    }
        		    else if (declarationUnit instanceof EditedSourceFile) {
        		        ((EditedSourceFile) declarationUnit).getDependentsOf().add(currentUnitPath);
                        //((EditedSourceFile) declarationUnit).getOriginalSourceFile().getDependentsOf().add(currentUnitPath);
                    }
        		    else if (declarationUnit instanceof CrossProjectSourceFile) {
        		        ProjectSourceFile originalProjectSourceFile = ((CrossProjectSourceFile) declarationUnit).getOriginalSourceFile();
        		        if (originalProjectSourceFile != null) {
        		            originalProjectSourceFile.getDependentsOf().add(currentUnitPath);
        		        }
        		    }
        		    else if (declarationUnit instanceof ExternalSourceFile) {
        		        // Don't manage them : they cannot change ... Well they might if we were using these dependencies to manage module 
        		        // removal. But since module removal triggers a classpath container update and so a full build, it's not necessary.
        		        // Might change in the future 
        		    }
        		    else if (declarationUnit instanceof CeylonBinaryUnit) {
                        //TODO: When we can typecheck from binary modules, we'll have to manage the case when a binary module 
        		        // corresponding PhasedUnit is in fact a CrossProjectPhasedUnit. 
        		        // And then take the corresponding ProjectSourceFile in the referenced project 
                        declarationUnit.getDependentsOf().add(currentUnitPath);
                    } 
                    else if (declarationUnit instanceof JavaCompilationUnit) {
                        //TODO: this does not seem to work for cross-project deps
                        // We should introduce a CrossProjectJavaUnit that can return 
                        // the original JavaCompilationUnit from the original project 
                        declarationUnit.getDependentsOf().add(currentUnitPath);
                    } 
                    else if (declarationUnit instanceof JavaClassFile) {
                        //TODO: All the dependencies to class files are also added... It is really useful ?
                        // I assume in the case of the classes in the classes or exploded dirs, it might be,
                        // but not sure it is also used not in the case of jar-located classes
                        declarationUnit.getDependentsOf().add(currentUnitPath);
                    } 
        		    else {
                        assert(false);
        		    }
        		}
        	}
        }
    }
    
    @Override
    public void visit(Tree.CompilationUnit that) {
        storeDependency(that.getUnit().getPackage().getUnit(), phasedUnit);
        storeDependency(that.getUnit().getPackage().getModule().getUnit(), phasedUnit);
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.Import that) {
        Referenceable model = that.getImportPath().getModel();
        if (model instanceof Package) {
            storeDependency(((Package) model).getUnit(), phasedUnit);
            storeDependency(((Package) model).getModule().getUnit(), phasedUnit);
        }
        super.visit(that);
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
