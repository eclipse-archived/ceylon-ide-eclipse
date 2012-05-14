package com.redhat.ceylon.eclipse.imp.imports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Import;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class DetectUnusedImportsVisitor extends Visitor {
    //private List<ImportMemberOrType> result = new ArrayList<ImportMemberOrType>();
    private List<Declaration> result = new ArrayList<Declaration>();
    
    /*@Override
    public void visit(ImportMemberOrType that) {
        result.add(that.getDeclarationModel());
    }*/
    
    @Override
    public void visit(Tree.Import that) {
        super.visit(that);
        for (Import i: that.getImportMemberOrTypeList()
                .getImportList().getImports()) {
            result.add(i.getDeclaration());
        }
    }
    
    
    @Override
    public void visit(Tree.BaseMemberOrTypeExpression that) {
        super.visit(that);
        for (Iterator<Declaration> it = result.iterator();
                it.hasNext();) {
            if ( it.next().equals(that.getDeclaration()) ) {
                it.remove();
            }
        }
    }
    
    @Override
    public void visit(Tree.BaseType that) {
        super.visit(that);
        for (Iterator<Declaration> it = result.iterator();
                it.hasNext();) {
            if ( it.next().equals(that.getDeclarationModel()) ) {
                it.remove();
            }
        }
    }    
    
    public List<Declaration> getResult() {
        return result;
    }
    
}
