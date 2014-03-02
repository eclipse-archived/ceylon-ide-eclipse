package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.correct.CreateProposal.getDocument;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.gotoLocation;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.installLinkedMode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.complete.CompletionProposal;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;

public class SpecifyTypeProposal extends CorrectionProposal {

    private final int offset;
    private final IFile file;
    private final ProducedType infType;
    private String explicitType;
    
    SpecifyTypeProposal(int offset, IFile file, String explicitType, 
            ProducedType infType, TextFileChange change) {
        super("Specify explicit type '" + explicitType + "'", change);
        this.offset = offset;
        this.explicitType = explicitType;
        this.infType = infType;
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        gotoLocation(file, offset, explicitType.length());
        if (!isTypeUnknown(infType)) {
            final LinkedModeModel linkedModeModel = new LinkedModeModel();
            CeylonEditor editor = (CeylonEditor) EditorUtil.getCurrentEditor();
            Unit unit = editor.getParseController().getRootNode().getUnit();
            List<ProducedType> supertypes = infType.getSupertypes();
            ICompletionProposal[] proposals = 
                    new ICompletionProposal[supertypes.size()];
            for (int i=0; i<supertypes.size(); i++) {
                ProducedType type = supertypes.get(i);
                String typeName = type.getProducedTypeName(unit);
                proposals[i] = new CompletionProposal(offset+explicitType.length(), 
                        explicitType,
                        getImageForDeclaration(type.getDeclaration()),
                        typeName, typeName) {
                    @Override
                    public void apply(IDocument document) {
                        super.apply(document);
                        linkedModeModel.exit(ILinkedModeListener.SELECT);
                    }
                };
            }
            ProposalPosition linkedPosition = 
                    new ProposalPosition(document, offset, explicitType.length(), 0, proposals);
            try {
                addLinkedPosition(linkedModeModel, linkedPosition);
                installLinkedMode(editor, document, linkedModeModel, 
                        this, NO_STOP, offset + explicitType.length());
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
    
    static void addSpecifyTypeProposal(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IFile file) {
        proposals.add(create(cu, node, file));
    }

    public static SpecifyTypeProposal create(Tree.CompilationUnit cu,
            Node node, IFile file) {
        final Tree.Type type = (Tree.Type) node;
        TextFileChange change = new TextFileChange("Specify Explicit Type", file);
        change.setEdit(new MultiTextEdit());
        Integer offset = node.getStartIndex();
        ProducedType infType = inferType(cu, type);
        String explicitType;
        int il;
        if (infType==null || infType.containsUnknowns()) {
            explicitType = "Object";
            il = 0;
        } 
        else {
            explicitType = infType.getProducedTypeName();
            HashSet<Declaration> decs = new HashSet<Declaration>();
            importType(decs, infType, cu);
            il = applyImports(change, decs, cu, getDocument(change));
        }
        change.addEdit(new ReplaceEdit(offset, type.getText().length(), explicitType)); 
            //Note: don't use problem.getLength() because it's wrong from the problem list
        return new SpecifyTypeProposal(offset+il, file, explicitType, infType, change);
    }

    static ProducedType inferType(Tree.CompilationUnit cu,
            final Tree.Type type) {
        InferTypeVisitor itv = new InferTypeVisitor() {
            { unit = type.getUnit(); }
            @Override public void visit(Tree.TypedDeclaration that) {
                if (that.getType()==type) {
                    dec = that.getDeclarationModel();
                    union(that.getType().getTypeModel());
                }
                super.visit(that);
            }            
        };
        itv.visit(cu);
        return itv.inferredType;
    }
    
}
