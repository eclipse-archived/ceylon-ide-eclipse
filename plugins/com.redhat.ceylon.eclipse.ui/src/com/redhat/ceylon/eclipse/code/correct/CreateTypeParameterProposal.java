package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.asIntersectionTypeString;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_CORR;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Generic;
import com.redhat.ceylon.model.typechecker.model.Method;
import com.redhat.ceylon.model.typechecker.model.ProducedType;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;

class CreateTypeParameterProposal extends CorrectionProposal {
    
    CreateTypeParameterProposal(String desc, Image image, 
            int offset, int length, TextFileChange change) {
        super(desc, change, new Region(offset, length), 
                image);
    }
    
    private static void addProposal(
            Collection<ICompletionProposal> proposals, 
            boolean wasNotGeneric, String def, String name, 
            Image image, Declaration dec, PhasedUnit unit, 
            Tree.Declaration decNode, int offset, 
            String constraints) {
        IFile file = getFile(unit);
        TextFileChange change = 
                new TextFileChange("Add Parameter", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        HashSet<Declaration> decs = 
                new HashSet<Declaration>();
        CompilationUnit cu = unit.getCompilationUnit();
        int il = applyImports(change, decs, cu, doc);
        change.addEdit(new InsertEdit(offset, def));
        if (constraints!=null) {
            int loc = getConstraintLoc(decNode);
            if (loc>=0) {
                change.addEdit(new InsertEdit(loc, constraints));
            }
        }
        String desc = "Add type parameter '" + name + "'" + 
                " to '" + dec.getName() + "'";
        int off = wasNotGeneric?1:2;
        proposals.add(new CreateTypeParameterProposal(desc, 
                image, offset+il+off, name.length(), change));
    }

    private static int getConstraintLoc(Tree.Declaration decNode) {
        if( decNode instanceof Tree.ClassDefinition ) {
            Tree.ClassDefinition classDefinition = 
                    (Tree.ClassDefinition) decNode;
            return classDefinition.getClassBody()
                    .getStartIndex();
        }
        else if( decNode instanceof Tree.InterfaceDefinition ) {
            Tree.InterfaceDefinition interfaceDefinition = 
                    (Tree.InterfaceDefinition) decNode;
            return interfaceDefinition.getInterfaceBody()
                    .getStartIndex();
        }
        else if( decNode instanceof Tree.MethodDefinition ) {
            Tree.MethodDefinition methodDefinition = 
                    (Tree.MethodDefinition) decNode;
            return methodDefinition.getBlock()
                    .getStartIndex();
        }
        else if( decNode instanceof Tree.ClassDeclaration ) {
            Tree.ClassDeclaration classDefinition = 
                    (Tree.ClassDeclaration) decNode;
            return classDefinition.getClassSpecifier()
                    .getStartIndex();
        }
        else if( decNode instanceof Tree.InterfaceDefinition ) {
            Tree.InterfaceDeclaration interfaceDefinition = 
                    (Tree.InterfaceDeclaration) decNode;
            return interfaceDefinition.getTypeSpecifier()
                    .getStartIndex();
        }
        else if( decNode instanceof Tree.MethodDeclaration ) {
            Tree.MethodDeclaration methodDefinition = 
                    (Tree.MethodDeclaration) decNode;
            return methodDefinition.getSpecifierExpression()
                    .getStartIndex();
        }
        else {
            return -1;
        }
    }
    
    static void addCreateTypeParameterProposal(
            Collection<ICompletionProposal> proposals, 
            IProject project, Tree.CompilationUnit cu, 
            final Tree.BaseType node, String brokenName) {
        
        class FilterExtendsSatisfiesVisitor 
                extends Visitor {
            boolean filter = false;
            @Override
            public void visit(Tree.ExtendedType that) {
                super.visit(that);
                if (that.getType()==node) {
                    filter = true;
                }
            }
            @Override
            public void visit(Tree.SatisfiedTypes that) {
                super.visit(that);
                for (Tree.Type t: that.getTypes()) {
                    if (t==node) {
                        filter = true;
                    }
                }
            }
            @Override
            public void visit(Tree.CaseTypes that) {
                super.visit(that);
                for (Tree.Type t: that.getTypes()) {
                    if (t==node) {
                        filter = true;
                    }
                }
            }
        }
        
        FilterExtendsSatisfiesVisitor v = 
                new FilterExtendsSatisfiesVisitor();
        v.visit(cu);
        if (v.filter) {
            return;
        }
        
        Tree.Declaration decl = 
                findDeclarationWithBody(cu, node);
        Declaration d = decl==null ? null : 
            decl.getDeclarationModel();
        if (d == null || d.isActual() ||
                !(d instanceof Method || 
                  d instanceof ClassOrInterface)) {
            return;
        }
        
        Tree.TypeParameterList paramList = 
                getTypeParameters(decl);
        String paramDef;
        int offset;
        //TODO: add bounds as default type arg?
        if (paramList != null) {
            paramDef = ", " + brokenName;
            offset = paramList.getStopIndex();
        }
        else {
            paramDef = "<" + brokenName + ">";
            offset = getIdentifyingNode(decl).getStopIndex()+1;
        }
        
        class FindTypeParameterConstraintVisitor 
                extends Visitor {
            List<ProducedType> result;
            @Override
            public void visit(Tree.SimpleType that) {
                super.visit(that);
                TypeDeclaration dm = 
                        that.getDeclarationModel();
                if (dm!=null) {
                    List<TypeParameter> tps = 
                            dm.getTypeParameters();
                    Tree.TypeArgumentList tal = 
                            that.getTypeArgumentList();
                    if (tal!=null) {
                        List<Tree.Type> tas = tal.getTypes();
                        for (int i=0; i<tas.size(); i++) {
                            if (tas.get(i)==node) {
                                result = tps.get(i).getSatisfiedTypes();
                            }
                        }
                    }
                }
            }
            @Override
            public void visit(Tree.StaticMemberOrTypeExpression that) {
                super.visit(that);
                Declaration d = that.getDeclaration();
                if (d instanceof Generic) {
                    Generic g = (Generic) d;
                    List<TypeParameter> tps = 
                            g.getTypeParameters();
                    Tree.TypeArguments tas = 
                            that.getTypeArguments();
                    if (tas instanceof Tree.TypeArgumentList) {
                        Tree.TypeArgumentList tal = 
                                (Tree.TypeArgumentList) tas;
                        List<Tree.Type> ts = 
                                tal.getTypes();
                        for (int i=0; i<ts.size(); i++) {
                            if (ts.get(i)==node) {
                                result = tps.get(i)
                                        .getSatisfiedTypes();
                            }
                        }
                    }
                }
            }
        }
        FindTypeParameterConstraintVisitor ftpcv = 
                new FindTypeParameterConstraintVisitor();
        ftpcv.visit(cu);
        String constraints;
        if (ftpcv.result==null) {
            constraints = null;
        }
        else {
            String bounds = 
                    asIntersectionTypeString(ftpcv.result);
            if (bounds.isEmpty()) {
                constraints = null;
            }
            else {
                constraints = "given " + brokenName + 
                        " satisfies " + bounds + " ";
            }
        }
        
        for (PhasedUnit unit : getUnits(project)) {
            if (unit.getUnit().equals(cu.getUnit())) {
                addProposal(proposals, paramList==null,
                        paramDef, brokenName, ADD_CORR, 
                        d, unit, decl, offset, constraints);
                break;
            }
        }

    }
    
    private static Tree.TypeParameterList getTypeParameters(
            Tree.Declaration decl) {
        if (decl instanceof Tree.ClassOrInterface) {
            Tree.ClassOrInterface ci = 
                    (Tree.ClassOrInterface) decl;
            return ci.getTypeParameterList();
        }
        else if (decl instanceof Tree.AnyMethod) {
            Tree.AnyMethod am = (Tree.AnyMethod) decl;
            return am.getTypeParameterList();
        }
        return null;
    }
    
        
}