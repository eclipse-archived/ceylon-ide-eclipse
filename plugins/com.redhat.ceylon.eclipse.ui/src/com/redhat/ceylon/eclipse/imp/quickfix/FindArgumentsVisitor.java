package com.redhat.ceylon.eclipse.imp.quickfix;

import java.util.Arrays;
import java.util.Collections;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class FindArgumentsVisitor extends Visitor 
        implements NaturalVisitor {
    Tree.StaticMemberOrTypeExpression smte;
    Tree.NamedArgumentList namedArgs;
    Tree.PositionalArgumentList positionalArgs;
    ProducedType currentType;
    ProducedType expectedType;
    boolean found = false;
    TypeChecker typeChecker;
    
    FindArgumentsVisitor(Tree.StaticMemberOrTypeExpression smte,
            TypeChecker tc) {
        this.smte = smte;
        typeChecker = tc;
    }
    
    private Declaration getLanguageModuleDeclaration(String name) {
        Module languageModule = typeChecker.getContext().getModules().getLanguageModule();
        for (Package languageScope : languageModule.getPackages() ) {
            Declaration d = languageScope.getMember(name);
            if (d!=null) return d;
        }
        return null;
    }
    
    @Override
    public void visit(Tree.StaticMemberOrTypeExpression that) {
        super.visit(that);
        if (that==smte) {
            expectedType = currentType;
            found = true;
        }
    }
    
    @Override
    public void visit(Tree.InvocationExpression that) {
        super.visit(that);
        if (that.getPrimary()==smte) {
            namedArgs = that.getNamedArgumentList();
            positionalArgs = that.getPositionalArgumentList();
        }
    }
    @Override
    public void visit(Tree.NamedArgument that) {
        ProducedType ct = currentType;
        currentType = that.getParameter().getType();
        super.visit(that);
        currentType = ct;
    }
    @Override
    public void visit(Tree.PositionalArgument that) {
        ProducedType ct = currentType;
        currentType = that.getParameter().getType();
        super.visit(that);
        currentType = ct;
    }
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        currentType = that.getType().getTypeModel();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.Variable that) {
        currentType = that.getType().getTypeModel();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.ValueIterator that) {
        Interface iterable = (Interface) getLanguageModuleDeclaration("Iterable");
        ProducedType varType = that.getVariable().getType().getTypeModel();
        ProducedType iterableType = iterable.getProducedType(null, Collections.singletonList(varType));
        currentType = iterableType;
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.KeyValueIterator that) {
        Interface iterable = (Interface) getLanguageModuleDeclaration("Iterable");
        Class entry = (Class) getLanguageModuleDeclaration("Entry");
        ProducedType keyType = that.getKeyVariable().getType().getTypeModel();
        ProducedType valueType = that.getValueVariable().getType().getTypeModel();
        ProducedType entryType = entry.getProducedType(null, Arrays.asList(keyType, valueType));
        ProducedType iterableType = iterable.getProducedType(null, Collections.singletonList(entryType));
        currentType = iterableType;
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.SpecifierStatement that) {
        currentType = that.getBaseMemberExpression().getTypeModel();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.AssignmentOp that) {
        ProducedType ct = currentType;
        currentType = that.getLeftTerm().getTypeModel();
        super.visit(that);
        currentType = ct;
    }
    @Override
    public void visit(Tree.Return that) {
        if (that.getDeclaration() instanceof TypedDeclaration) {
            currentType = ((TypedDeclaration) that.getDeclaration()).getType();
        }
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.Throw that) {
        super.visit(that);
        //set expected type to Exception
    }
    @Override
    public void visitAny(Node that) {
        if (!found) super.visitAny(that);
    }
}