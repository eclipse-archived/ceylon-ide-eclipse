package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ADD;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;
import static com.redhat.ceylon.eclipse.code.quickfix.SpecifyTypeProposal.inferType;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;

class AddParameterProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    AddParameterProposal(Declaration dec, int offset, IFile file, TextChange change) {
        super("Add to parameter list of '" + dec.getName() + "'", change, 10, ADD);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }

    static void addParameterProposal(IDocument doc, Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.AttributeDeclaration decNode, CeylonEditor editor) {
        Value dec = decNode.getDeclarationModel();
        if (dec.getInitializerParameter()==null && !dec.isFormal()) {
            TextChange change = new DocumentChange("Add Parameter", doc);
            change.setEdit(new MultiTextEdit());
            FindContainerVisitor fcv = new FindContainerVisitor(decNode);
            fcv.visit(cu);
            Tree.Declaration container = fcv.getDeclaration();
            if (container instanceof Tree.ClassDefinition) {
                ParameterList pl = ((Tree.ClassDefinition) container).getParameterList();
                SpecifierOrInitializerExpression sie = decNode.getSpecifierOrInitializerExpression();
                String def;
                if (sie==null) {
                    def = " = bottom";
                }
                else {
                    def = AbstractRefactoring.toString(sie, 
                              editor.getParseController().getTokens())
                                  .replace(":=", "=");
                    int start = sie.getStartIndex();
                    try {
                        if (doc.get(start-1,1).equals(" ")) {
                            start--;
                            def = " " + def;
                        }
                    } 
                    catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    change.addEdit(new DeleteEdit(start, sie.getStopIndex()-start+1));
                }
                String param = (pl.getParameters().isEmpty() ? "" : ", ") + dec.getName() + def;
                Integer offset = pl.getStopIndex();
                change.addEdit(new InsertEdit(offset, param));
                Type type = decNode.getType();
                int shift=0;
                if (type instanceof Tree.LocalModifier) {
                    Integer typeOffset = type.getStartIndex();
                    ProducedType infType = inferType(cu, type);
					String explicitType;
					if (infType==null) {
						explicitType = "Object";
					}
					else {
						explicitType = infType.getProducedTypeName();
						shift=importType(change, infType, cu, new HashSet<Declaration>());
					}
                    change.addEdit(new ReplaceEdit(typeOffset, type.getText().length(), explicitType));
                }
                proposals.add(new AddParameterProposal(container.getDeclarationModel(), 
                        offset+param.length()+shift, file, change));
            }
        }
    }
    
}