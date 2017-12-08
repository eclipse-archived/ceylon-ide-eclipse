/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.imports;

import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getAbstraction;

import java.util.Iterator;
import java.util.List;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

@Deprecated
class DetectUnusedImportsVisitor extends Visitor {
    
    private final List<Declaration> result;
    
    DetectUnusedImportsVisitor(List<Declaration> result) {
        this.result = result;
    }
    
    @Override
    public void visit(Tree.Import that) {
        super.visit(that);
        for (Tree.ImportMemberOrType i: 
                that.getImportMemberOrTypeList()
                    .getImportMemberOrTypes()) {
            if (i.getDeclarationModel()!=null) {
                result.add(i.getDeclarationModel());
            }
            if (i.getImportMemberOrTypeList()!=null) {
                for (Tree.ImportMemberOrType j: 
                        i.getImportMemberOrTypeList()
                            .getImportMemberOrTypes()) {
                    if (j.getDeclarationModel()!=null) {
                        result.add(j.getDeclarationModel());
                    }
                }
            }
        }
    }
    
    private void remove(Declaration d) {
        if (d!=null) {
            for (Iterator<Declaration> it = result.iterator();
                    it.hasNext();) {
                if ( it.next().equals(d) ) {
                    it.remove();
                }
            }
        }
    }
        
    private boolean isAliased(final Declaration d, Tree.Identifier id) {
        return id==null || d!=null && !d.getName().equals(id.getText());
    }

    @Override
    public void visit(Tree.QualifiedMemberOrTypeExpression that) {
        super.visit(that);
        final Declaration d = that.getDeclaration();
        if (isAliased(d, that.getIdentifier())) {
            remove(getAbstraction(d));
        }
    }

    @Override
    public void visit(Tree.BaseMemberOrTypeExpression that) {
        super.visit(that);
        remove(getAbstraction(that.getDeclaration()));
    }

    @Override
    public void visit(Tree.QualifiedType that) {
        super.visit(that);
        TypeDeclaration d = that.getDeclarationModel();
        if (isAliased(d, that.getIdentifier())) {
            remove(getAbstraction(d));
        }
    } 
    
    @Override
    public void visit(Tree.BaseType that) {
        super.visit(that);
        remove(getAbstraction(that.getDeclarationModel()));
    } 
    
    @Override
    public void visit(Tree.MemberLiteral that) {
        super.visit(that);
        if (that.getType()==null) {
            remove(getAbstraction(that.getDeclaration()));
        }
    }

}
