package com.redhat.ceylon.eclipse.code.imports;

import java.util.Iterator;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class DetectUnusedImportsVisitor extends Visitor {
    
    private final List<Declaration> result;
    
    DetectUnusedImportsVisitor(List<Declaration> result) {
    	this.result = result;
    }
    
    @Override
    public void visit(Tree.Import that) {
        super.visit(that);
        for (Tree.ImportMemberOrType i: that.getImportMemberOrTypeList()
                .getImportMemberOrTypes()) {
            if (i.getDeclarationModel()!=null)
                result.add(i.getDeclarationModel());
            if (i.getImportMemberOrTypeList()!=null) {
                for (Tree.ImportMemberOrType j: i.getImportMemberOrTypeList()
                        .getImportMemberOrTypes()) {
                    if (j.getDeclarationModel()!=null)
                        result.add(j.getDeclarationModel());
                }
            }
        }
    }
    
    
    @Override
    public void visit(Tree.BaseMemberOrTypeExpression that) {
        super.visit(that);
        Declaration d = that.getDeclaration();
        if (d!=null) {
            for (Iterator<Declaration> it = result.iterator();
                    it.hasNext();) {
                if ( it.next().equals(d) ) {
                    it.remove();
                }
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
    
    @Override
    public void visit(Tree.MemberLiteral that) {
        super.visit(that);
        Declaration d = that.getDeclaration();
        if (d!=null && that.getType()==null) {
            for (Iterator<Declaration> it = result.iterator();
                    it.hasNext();) {
                if ( it.next().equals(d) ) {
                    it.remove();
                }
            }
        }
    }

}
