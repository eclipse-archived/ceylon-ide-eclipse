package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Declaration;

class AssignToFieldProposal {

    static void addAssignToFieldProposal(IFile file, 
                Tree.Statement statement, 
                Tree.Declaration declaration, 
                Collection<ICompletionProposal> proposals) {
            if (declaration instanceof Tree.TypedDeclaration && 
                    statement instanceof Tree.Constructor) {
    //            Tree.TypedDeclaration td = 
    //                    (Tree.TypedDeclaration) declaration;
                Tree.Constructor constructor = 
                        (Tree.Constructor) statement;
                Declaration model = 
                        declaration.getDeclarationModel();
                String name = model.getName();
                if (constructor.getConstructor()
                        .getExtendedType()
                        .getDeclaration()
                        .getMember(name, null, false)
                            == null) {
                    TextFileChange change = 
                            new TextFileChange("Assign to Field", 
                                    file);
                    change.setEdit(new MultiTextEdit());
                    IDocument document = getDocument(change);
                    String indent = 
                            getDefaultLineDelimiter(document) +
                            getIndent(constructor, document);
                    String def;
                    try {
                        def = document.get(
                                declaration.getStartIndex(), 
                                declaration.getDistance());
                    }
                    catch (BadLocationException e) {
                        return;
                    }
                    //TODO: strip off the default argument!!!
                    def += ";" + indent;
                    int loc = statement.getStartIndex();
                    int offset = 
                            constructor.getBlock()
                                .getStartIndex() + 1;
                    String text = 
                            indent +
                            getDefaultIndent() +
                            "this." + name + 
                            " = " + name + ";";
                    change.addEdit(new InsertEdit(loc, def));
                    change.addEdit(new InsertEdit(offset, text));
                    String desc = 
                            "Assign parameter '" + name + 
                            "' to new field";
                    proposals.add(new CorrectionProposal(
                            desc, change, null));
                }
            }
        }

}
