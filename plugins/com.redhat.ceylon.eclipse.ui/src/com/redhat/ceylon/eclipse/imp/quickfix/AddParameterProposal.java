package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
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
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.refactoring.AbstractRefactoring;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;

class AddParameterProposal extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    AddParameterProposal(Declaration dec, int offset, IFile file, TextChange change) {
        super("Add to parameter list of '" + dec.getName() + "'", change, 10, CORRECTION);
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
            Tree.AttributeDeclaration decNode, UniversalEditor editor) {
        Value dec = decNode.getDeclarationModel();
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
                        ((CeylonEditor) editor).getParseController().getTokens());
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
            if (type instanceof Tree.LocalModifier) {
                Integer typeOffset = type.getStartIndex();
                String explicitType = SpecifyTypeProposal.inferType(cu, type);
                change.addEdit(new ReplaceEdit(typeOffset, type.getText().length(), explicitType));
            }
            proposals.add(new AddParameterProposal(container.getDeclarationModel(), 
                    offset+param.length(), file, change));
        }
    }
    
}