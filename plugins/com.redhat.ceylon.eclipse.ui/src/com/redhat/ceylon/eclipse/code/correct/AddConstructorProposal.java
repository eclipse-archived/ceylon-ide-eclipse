package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.analyzer.Util.getLastExecutableStatement;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class AddConstructorProposal {

    public static void addConstructorProposal(IFile file,
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.ClassDefinition) {
            TextFileChange change = 
                    new TextFileChange("Add Default Constructor", file);
            Tree.ClassDefinition cd = (Tree.ClassDefinition) node;
            Tree.ClassBody body = cd.getClassBody();
            if (body!=null && cd.getIdentifier()!=null) {
                String name = cd.getDeclarationModel().getName();
                String text = "shared new " + name + "() {}";
                IDocument doc = EditorUtil.getDocument(change);
                Tree.Statement les = getLastExecutableStatement(body);
                InsertEdit edit;
                int loc;
                String delim = getDefaultLineDelimiter(doc);
                if (les==null) {
                    String indent = getIndent(cd, doc);
                    String lws = delim + indent + getDefaultIndent();
                    int start = body.getStartIndex()+1;
                    if (body.getStopIndex()==start) {
                        text += delim + indent;
                    }
                    edit = new InsertEdit(start, lws + text);
                    loc = start + lws.length() + text.indexOf('(') + 1;
                    //TODO: add additional ws if necessary
                }
                else {
                    int start = les.getStopIndex()+1;
                    String indent = getIndent(les, doc);
                    String lws = delim + indent;
                    edit = new InsertEdit(start, lws + text);
                    loc = start + lws.length() + text.indexOf('(') + 1;
                }
                change.setEdit(edit);
                proposals.add(new CorrectionProposal(
                        "Add default constructor to '" + name + "'", 
                        change, new Region(loc, 0)));
            }
        }
    }

}
