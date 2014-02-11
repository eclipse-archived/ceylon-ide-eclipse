package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.correct.SpecifyTypeProposal.inferType;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ADD;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring;
import com.redhat.ceylon.eclipse.util.FindBodyContainerVisitor;

class AddParameterProposal extends CorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    AddParameterProposal(Declaration dec, int offset, IFile file, 
            TextChange change) {
        super("Add to parameter list of '" + dec.getName() + "'", 
                change, ADD);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        EditorUtil.gotoLocation(file, offset);
    }

    static void addParameterProposal(IDocument doc, Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Tree.TypedDeclaration decNode, Tree.SpecifierOrInitializerExpression sie,
            CeylonEditor editor) {
        MethodOrValue dec = (MethodOrValue) decNode.getDeclarationModel();
        if (dec==null) return;
        if (dec.getInitializerParameter()==null && !dec.isFormal()) {
            //TODO: copy/pasted from SplitDeclarationProposal 
            String params = null;
            if (decNode instanceof Tree.MethodDeclaration) {
                List<ParameterList> pls = 
                        ((Tree.MethodDeclaration) decNode).getParameterLists();
                if (pls.isEmpty()) {
                    return;
                } 
                else {
                    Integer start = pls.get(0).getStartIndex();
                    Integer end = pls.get(pls.size()-1).getStopIndex();
                    try {
                        params = doc.get(start, end-start+1);
                    } 
                    catch (BadLocationException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            TextChange change = new TextFileChange("Add Parameter", file);
//            TextChange change = new DocumentChange("Add Parameter", doc);
            change.setEdit(new MultiTextEdit());
            FindBodyContainerVisitor fcv = new FindBodyContainerVisitor(decNode);
            fcv.visit(cu);
            Tree.Declaration container = fcv.getDeclaration();
            if (container instanceof Tree.ClassDefinition) {
                ParameterList pl = 
                        ((Tree.ClassDefinition) container).getParameterList();
                String def;
                if (sie==null) {
                    def = " = nothing";
                }
                else {
                    def = AbstractRefactoring.toString(sie, 
                              editor.getParseController().getTokens());
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
                    if (params!=null) def = " = " + params + def;
                    change.addEdit(new DeleteEdit(start, sie.getStopIndex()-start+1));
                }
                String param = (pl.getParameters().isEmpty() ? "" : ", ") + 
                        dec.getName() + def;
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
                        HashSet<Declaration> decs = new HashSet<Declaration>();
                        importType(decs, infType, cu);
                        shift = applyImports(change, decs, cu, doc);
                    }
                    change.addEdit(new ReplaceEdit(typeOffset, type.getText().length(), 
                            explicitType));
                }
                proposals.add(new AddParameterProposal(container.getDeclarationModel(), 
                        offset+param.length()+shift, file, change));
            }
        }
    }
    
}