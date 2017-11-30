/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findReferencedNode;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.ParameterList;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Unit;

@Deprecated
public class ConvertToDefaultConstructorProposal {

    static void addConvertToDefaultConstructorProposal(
            Collection<ICompletionProposal> proposals, 
            IDocument doc, IFile file, 
            Tree.CompilationUnit rootNode,
            Tree.Statement statement) {
        if (statement instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition cd = 
                    (Tree.ClassDefinition) 
                        statement;
            Tree.ParameterList pl = cd.getParameterList();
            if (pl!=null) {
                TextChange change = 
                        new TextFileChange(
                                "Convert to Class with Default Constructor", 
                                file);
                change.setEdit(new MultiTextEdit());
                String indent = utilJ2C().indents().getIndent(statement, doc);
                String delim = utilJ2C().indents().getDefaultLineDelimiter(doc);
                String defIndent = utilJ2C().indents().getDefaultIndent();
                int insertLoc = cd.getClassBody().getStartIndex()+1;
                StringBuilder declarations = new StringBuilder();
                StringBuilder assignments = new StringBuilder();
                StringBuilder params = new StringBuilder();
                String extend = "";
                Tree.ExtendedType et = cd.getExtendedType();
                if (et!=null) {
                    try {
                        String text =
                                doc.get(et.getStartIndex(),
                                        et.getDistance());
                        extend =
                                new StringBuilder()
                                    .append(delim)
                                    .append(indent)
                                    .append(defIndent)
                                    .append(defIndent)
                                    .append(defIndent)
                                    .append(text)
                                    .toString();
                    }
                    catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    Tree.PositionalArgumentList pal =
                            et.getInvocationExpression()
                                .getPositionalArgumentList();
                    if (pal!=null) {
                        change.addEdit(new DeleteEdit(
                                pal.getStartIndex(),
                                pal.getDistance()));
                    }
                }
                for (Tree.Parameter p: pl.getParameters()) {
                    if (p instanceof Tree.InitializerParameter) {
                        Node pdn =
                                findReferencedNode(rootNode,
                                        p.getParameterModel()
                                            .getModel());
                        if (pdn!=null) {
                            //the constructor has to come
                            //after the declarations of the
                            //parameters
                            int index = pdn.getEndIndex();
                            if (index>insertLoc) {
                                insertLoc = index;
                            }
                        }
                    }
                    Parameter model = p.getParameterModel();
                    String attDef = "";
                    StringBuilder paramDef = new StringBuilder();
                    String pname = model.getName();
                    Unit unit = cd.getUnit();
                    int end = p.getEndIndex();
                    int start = p.getStartIndex();
                    if (p instanceof Tree.ParameterDeclaration) {
                        Tree.ParameterDeclaration pd =
                                (Tree.ParameterDeclaration) p;
                        Tree.TypedDeclaration td =
                                pd.getTypedDeclaration();
                        Tree.Type t = td.getType();
                        try {
                            String text =
                                    doc.get(t.getStartIndex(),
                                            p.getEndIndex()
                                            - t.getStartIndex());
                            paramDef.append(text);
                        }
                        catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                        Tree.TypedDeclaration tdn =
                                pd.getTypedDeclaration();
                        Tree.SpecifierOrInitializerExpression se;
                        if (tdn instanceof Tree.AttributeDeclaration) {
                            Tree.AttributeDeclaration ad =
                                    (Tree.AttributeDeclaration) tdn;
                            se = ad.getSpecifierOrInitializerExpression();
                        }
                        else if (tdn instanceof Tree.MethodDeclaration) {
                            Tree.MethodDeclaration md =
                                    (Tree.MethodDeclaration) tdn;
                            se = md.getSpecifierExpression();
                        }
                        else {
                            se = null;
                        }
                        if (se!=null) {
                            end = se.getStartIndex();
                        }
                    }
                    else if (p instanceof Tree.InitializerParameter) {
                        Tree.InitializerParameter ip =
                                (Tree.InitializerParameter) p;
                        Type pt = model.getType();
                        paramDef.append(pt.asString(unit))
                                .append(" ")
                                .append(pname);
                        FunctionOrValue dec = model.getModel();
                        if (dec instanceof Function) {
                            Function run = (Function) dec;
                            for (ParameterList npl:
                                    run.getParameterLists()) {
                                paramDef.append("(");
                                boolean first = true;
                                for (Parameter np:
                                        npl.getParameters()) {
                                    if (first) {
                                        first = false;
                                    }
                                    else {
                                        paramDef.append(", ");
                                    }
                                    Type npt = np.getType();
                                    paramDef.append(npt.asString(unit) )
                                            .append(" ")
                                            .append(np.getName());
                                }
                                paramDef.append(")");
                            }
                        }
                        Tree.SpecifierExpression se =
                                ip.getSpecifierExpression();
                        if (se!=null) {
                            try {
                            String text =
                                    doc.get(se.getStartIndex(),
                                            se.getDistance());
                                paramDef.append(text);
                            }
                            catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                            end = se.getStartIndex();
                        }
                    }
                    else {
                        //impossible
                        return;
                    }
                    try {
                        attDef = doc.get(start, end-start).trim();
                    }
                    catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    if (p instanceof Tree.ParameterDeclaration) {
                        declarations
                            .append(indent)
                            .append(defIndent)
                            .append(attDef)
                            .append(";")
                            .append(delim);
                    }
                    assignments
                        .append(indent)
                        .append(defIndent)
                        .append(defIndent)
                        .append("this.")
                        .append(pname)
                        .append(" = ")
                        .append(pname)
                        .append(";")
                        .append(delim);
                    if (params.length()>0) {
                        params.append(", ");
                    }
                    params.append(paramDef);
                }
                String text = 
                        new StringBuilder()
                            .append(delim)
                            .append(declarations)
                            .append(indent)
                            .append(defIndent)
                            .append("shared new (")
                            .append(params)
                            .append(")")
                            .append(extend)
                            .append(" {")
                            .append(delim)
                            .append(assignments)
                            .append(indent)
                            .append(defIndent)
                            .append("}")
                            .append(delim)
                            .toString();
                change.addEdit(new DeleteEdit(
                        pl.getStartIndex(),
                        pl.getDistance()));
                change.addEdit(new InsertEdit(insertLoc, text));
                String name = 
                        cd.getDeclarationModel().getName();
                proposals.add(new CorrectionProposal(
                        "Convert '" + name + 
                        "' to class with default constructor", 
                        change, 
                        new Region(statement.getStartIndex(), 0)));
            }
        }
    }

}
