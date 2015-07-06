package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeEndOffset;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeStartOffset;

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

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Parameter;

public class ConvertToDefaultConstructorProposal {

    static void addConvertToDefaultConstructorProposal(
            Collection<ICompletionProposal> proposals, 
            IDocument doc, IFile file, 
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
                StringBuilder declarations = new StringBuilder();
                StringBuilder assignments = new StringBuilder();
                StringBuilder params = new StringBuilder();
                for (Tree.Parameter p: pl.getParameters()) {
                    Parameter model = p.getParameterModel();
                    String attDef = "";
                    String paramDef = "";
                    String pname = model.getName();
                    try {
                        attDef = 
                                doc.get(getNodeStartOffset(p), 
                                        getNodeLength(p));
                        if (p instanceof Tree.ParameterDeclaration) {
                            Tree.ParameterDeclaration pd = 
                                    (Tree.ParameterDeclaration) p;
                            Tree.TypedDeclaration td = 
                                    pd.getTypedDeclaration();
                            Tree.Type t = td.getType();
                            paramDef = 
                                    doc.get(getNodeStartOffset(t), 
                                            getNodeEndOffset(p)
                                            - getNodeStartOffset(t));
                        }
                        else {
                            paramDef = 
                                    model.getType()
                                    .asString(cd.getUnit()) 
                                    + " " + pname;
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
                        getNodeStartOffset(pl),
                        getNodeLength(pl)));
                change.addEdit(new InsertEdit(
                        cd.getClassBody().getStartIndex()+1, 
                        text));
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
