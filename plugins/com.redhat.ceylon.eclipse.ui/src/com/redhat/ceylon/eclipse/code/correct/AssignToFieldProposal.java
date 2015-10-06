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
import com.redhat.ceylon.model.typechecker.model.Constructor;
import com.redhat.ceylon.model.typechecker.model.Declaration;

class AssignToFieldProposal {

    static void addAssignToFieldProposal(IFile file, 
                Tree.Statement statement, 
                Tree.Declaration declaration, 
                Collection<ICompletionProposal> proposals) {
            if (declaration instanceof Tree.TypedDeclaration && 
                    statement instanceof Tree.Constructor) {
                Tree.Constructor constructor = 
                        (Tree.Constructor) statement;
                Declaration model = 
                        declaration.getDeclarationModel();
                String name = model.getName();
                Constructor cmodel = constructor.getConstructor();
                if (!model.getContainer().equals(cmodel)) {
                    return;
                }
                if (cmodel.getExtendedType()
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
                    int start = declaration.getStartIndex();
                    int end;
                    Tree.SpecifierOrInitializerExpression sie;
                    if (declaration instanceof Tree.AttributeDeclaration) {
                        Tree.AttributeDeclaration ad = 
                                (Tree.AttributeDeclaration) 
                                    declaration;
                        sie = ad.getSpecifierOrInitializerExpression();
                    }
                    else if (declaration instanceof Tree.MethodDeclaration) {
                        Tree.MethodDeclaration ad = 
                                (Tree.MethodDeclaration) 
                                    declaration;
                        sie = ad.getSpecifierExpression();
                    }
                    else {
                        sie = null;
                    }
                    end = sie==null ? 
                            declaration.getEndIndex() : 
                            sie.getStartIndex();
                    String def;
                    try {
                        def = document.get(start, end-start)
                                .trim();
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
