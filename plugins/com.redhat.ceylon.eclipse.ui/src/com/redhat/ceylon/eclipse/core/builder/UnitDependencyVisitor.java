package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.model.typechecker.model.Module.LANGUAGE_MODULE_NAME;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.redhat.ceylon.compiler.java.loader.TypeFactory;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.ICrossProjectReference;
import com.redhat.ceylon.eclipse.core.model.JavaClassFile;
import com.redhat.ceylon.eclipse.core.model.JavaCompilationUnit;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.model.cmr.JDKUtils;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class UnitDependencyVisitor extends Visitor {
    
    private final PhasedUnit phasedUnit;
    private Set<Declaration> alreadyDone;
    
    public UnitDependencyVisitor(PhasedUnit phasedUnit) {
        this.phasedUnit = phasedUnit;
        alreadyDone = new HashSet<Declaration>();
    }
    
    private void storeDependency(Type type) {
        if (type!=null) {
            if (type.isClassOrInterface() || type.isTypeAlias()) {
                if (!createDependency(type.getDeclaration())) {
                    return;
                }
            }
            Type et = type.getExtendedType();
            storeDependency(et);
            List<Type> satisfiedTypes = 
                    type.getSatisfiedTypes();
            for (Type st: satisfiedTypes) {
                storeDependency(st);
            }
            List<Type> caseTypes = 
                    type.getCaseTypes();
            if (caseTypes!=null) {
                for (Type ct: caseTypes) {
                    storeDependency(ct);
                }
            }
        }
    }
    
    private void storeDependency(Declaration dec) {
        if (dec instanceof TypeDeclaration) {
            storeDependency((TypeDeclaration) dec); 
        }
        else if (dec instanceof TypedDeclaration) {
            storeDependency((TypedDeclaration) dec); 
        }
    }

    private void storeDependency(TypedDeclaration dec) {
        storeDependency(dec.getType());
        //TODO: parameters!
        Declaration rd = dec.getRefinedDeclaration();
        if (rd!=dec && rd instanceof TypedDeclaration) {
            storeDependency((TypedDeclaration) rd); //this one is needed for default arguments, I think
        }
        createDependency(dec);
    }

    private void storeDependency(TypeDeclaration dec) {
        TypeDeclaration typeDeclaration = 
                (TypeDeclaration) dec;
        storeDependency(typeDeclaration.getType());
        Declaration rd = dec.getRefinedDeclaration();
        if (rd!=dec && rd instanceof TypeDeclaration) {
            storeDependency((TypeDeclaration) rd); //this one is needed for default arguments, I think
        }
        createDependency(dec);
    }

    boolean createDependency(Declaration dec) {
        if (dec!=null && !alreadyDone.contains(dec)) {
            alreadyDone.add(dec);
            Unit declarationUnit = dec.getUnit();
            if (declarationUnit != null && 
                    !(declarationUnit instanceof TypeFactory)) {
                String moduleName = 
                        declarationUnit.getPackage()
                            .getModule()
                            .getNameAsString();
                if (!moduleName.equals(LANGUAGE_MODULE_NAME) && 
                        !JDKUtils.isJDKModule(moduleName)
                        && !JDKUtils.isOracleJDKModule(moduleName)) { 
                    Unit currentUnit = phasedUnit.getUnit();
                    String currentUnitPath = 
                            phasedUnit.getUnitFile()
                                .getPath();
                    String currentUnitName = 
                            currentUnit.getFilename();
                    String dependedOnUnitName = 
                            declarationUnit.getFilename();
                    String currentUnitPackage = 
                            currentUnit.getPackage()
                                .getNameAsString();
                    String dependedOnPackage = 
                            declarationUnit.getPackage()
                                .getNameAsString();
                    if (!dependedOnUnitName.equals(currentUnitName) ||
                            !dependedOnPackage.equals(currentUnitPackage)) {
                        
                        // WOW : Ceylon Abstract Data types and swith case would be cool here ;) 
                        if (declarationUnit instanceof ProjectSourceFile) {
                            declarationUnit.getDependentsOf()
                                .add(currentUnitPath);
                        }
                        else if (declarationUnit instanceof ICrossProjectReference) {
                            ICrossProjectReference crossProjectReference = 
                                    (ICrossProjectReference) declarationUnit;
                            ProjectSourceFile originalProjectSourceFile = 
                                    crossProjectReference.getOriginalSourceFile();
                            if (originalProjectSourceFile != null) {
                                originalProjectSourceFile.getDependentsOf()
                                    .add(currentUnitPath);
                            }
                        }
                        else if (declarationUnit 
                                instanceof ExternalSourceFile) {
                            // Don't manage them : they cannot change ... Well they might if we were using these dependencies to manage module 
                            // removal. But since module removal triggers a classpath container update and so a full build, it's not necessary.
                            // Might change in the future 
                        }
                        else if (declarationUnit instanceof CeylonBinaryUnit) {
                            declarationUnit.getDependentsOf()
                                .add(currentUnitPath);
                        } 
                        else if (declarationUnit instanceof JavaCompilationUnit) {
                            //TODO: this does not seem to work for cross-project deps
                            // We should introduce a CrossProjectJavaUnit that can return 
                            // the original JavaCompilationUnit from the original project 
                            declarationUnit.getDependentsOf()
                                .add(currentUnitPath);
                        } 
                        else if (declarationUnit instanceof JavaClassFile) {
                            //TODO: All the dependencies to class files are also added... It is really useful ?
                            // I assume in the case of the classes in the classes or exploded dirs, it might be,
                            // but not sure it is also used not in the case of jar-located classes
                            declarationUnit.getDependentsOf()
                                .add(currentUnitPath);
                        } 
                        else {
                            assert(false);
                        }
                    }
                }
            }
            return true;
        }
        else {
            return false;
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
    
    void storeDependency(Parameter parameter) {
        if (parameter!=null) {
            storeDependency(parameter.getModel());
        }
    }
        
    @Override
    public void visit(Tree.Type that) {
        storeDependency(that.getTypeModel());
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
        List<Type> types = that.getTypeModels();
        if (types!=null) {
            for (Type type: types) {
                storeDependency(type);
            }
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.Term that) {
        //TODO: is this really necessary?
        storeDependency(that.getTypeModel());
        super.visit(that);
    }
    
    public void visit(Tree.Declaration that) {
        Declaration decl = that.getDeclarationModel();
        if (decl.isNative()) {
            List<Declaration> overloads = decl.getOverloads();
            if (overloads != null) {
                for (Declaration overload : overloads) {
                    if (overload == decl) {
                        continue;
                    }
                    createDependency(overload);
                }
            }
        }
        super.visit(that);
    }
}
