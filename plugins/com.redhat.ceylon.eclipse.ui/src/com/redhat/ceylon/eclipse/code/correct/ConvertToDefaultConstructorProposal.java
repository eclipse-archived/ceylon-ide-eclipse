package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.findReferencedNode;

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

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;

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
                String indent = getIndent(statement, doc);
                String delim = getDefaultLineDelimiter(doc);
                String defIndent = getDefaultIndent();
                int insertLoc = cd.getClassBody().getStartIndex()+1;
                StringBuilder declarations = new StringBuilder();
                StringBuilder assignments = new StringBuilder();
                StringBuilder params = new StringBuilder();
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
                            insertLoc = pdn.getEndIndex();
                        }
                    }
                    Parameter model = p.getParameterModel();
                    String attDef = "";
                    StringBuilder paramDef = new StringBuilder();
                    String pname = model.getName();
                    Unit unit = cd.getUnit();
                    try {
                        attDef = 
                                doc.get(p.getStartIndex(), 
                                        p.getDistance());
                        if (p instanceof Tree.ParameterDeclaration) {
                            Tree.ParameterDeclaration pd = 
                                    (Tree.ParameterDeclaration) p;
                            Tree.TypedDeclaration td = 
                                    pd.getTypedDeclaration();
                            Tree.Type t = td.getType();
                            String text = 
                                    doc.get(t.getStartIndex(), 
                                            p.getEndIndex()
                                            - t.getStartIndex());
                            paramDef.append(text);
;
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
                                String text = 
                                        doc.get(se.getStartIndex(), 
                                                se.getDistance());
                                paramDef.append(text);
                            }
                        }
                        else {
                            //impossible
                            return;
                        }
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
                        delim + 
                        declarations + 
                        indent + defIndent + 
                        "shared new (" + params + ") {" + delim + 
                        assignments + 
                        indent + defIndent + "}" + delim;
                change.setEdit(new MultiTextEdit());
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
