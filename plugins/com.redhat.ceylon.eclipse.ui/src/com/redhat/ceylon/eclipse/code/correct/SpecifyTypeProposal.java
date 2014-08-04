package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.correct.SpecifyTypeArgumentsProposal.addSpecifyTypeArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.correct.TypeProposal.getTypeProposals;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.REVEAL;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.LinkedMode;

public class SpecifyTypeProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final ProducedType infType;
    private final String desc;
    private final Tree.Type typeNode;
    private CeylonEditor editor;
    private final Tree.CompilationUnit rootNode;
    private Point selection;
    
    private SpecifyTypeProposal(String desc, Tree.Type type,
            Tree.CompilationUnit cu, ProducedType infType, 
            CeylonEditor editor) {
        this.desc = desc;
        this.typeNode = type;
        this.rootNode = cu;
        this.infType = infType;
        this.editor = editor;
    }
    
    @Override
    public void apply(IDocument document) {
        int offset = typeNode.getStartIndex();
        int length = typeNode.getStopIndex()-offset+1;
        if (editor==null) {
            IEditorPart ed = EditorUtil.getCurrentEditor();
            if (ed instanceof CeylonEditor) {
                editor = (CeylonEditor) ed;
            }
        }
        if (editor==null) {
            if (typeNode instanceof Tree.LocalModifier) {
                DocumentChange change = 
                        new DocumentChange("Specify Type", document);
                change.setEdit(new MultiTextEdit());
                try {
                    HashSet<Declaration> decs = new HashSet<Declaration>();
                    importType(decs, infType, rootNode);
                    int il = applyImports(change, decs, rootNode, document);
                    String typeName = 
                            infType.getProducedTypeName(rootNode.getUnit());
                    change.addEdit(new ReplaceEdit(offset, length, typeName));
                    change.perform(new NullProgressMonitor());
                    offset += il;
                    length = typeName.length();
                    selection = new Point(offset, length);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            LinkedModeModel linkedModeModel = new LinkedModeModel();
            ProposalPosition linkedPosition = 
                    getTypeProposals(document, offset, length, 
                            infType, rootNode, null);
            try {
                LinkedMode.addLinkedPosition(linkedModeModel, linkedPosition);
                LinkedMode.installLinkedMode(editor, document, linkedModeModel, 
                        this, NO_STOP, -1);
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    static void addSpecifyTypeProposal(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, CeylonEditor editor) {
        SpecifyTypeProposal proposal = 
                createProposal(cu, node, editor);
        if (proposal!=null) {
            proposals.add(proposal);
        }
    }

    public static SpecifyTypeProposal createProposal(Tree.CompilationUnit cu, 
            Node node, CeylonEditor editor) {
        final Tree.Type type = (Tree.Type) node;
        String desc;
        if (type instanceof Tree.LocalModifier) {
            desc = "Specify explicit type";
        }
        else {
            desc = "Generalize type";
        }
        ProducedType infType = inferType(cu, type);
        if (isTypeUnknown(infType)) {
            return null;
        }
        else {
            return new SpecifyTypeProposal(desc, type, cu,
                    infType, editor);
        }
    }

    static ProducedType inferType(Tree.CompilationUnit cu,
            final Tree.Type type) {
        InferTypeVisitor itv = new InferTypeVisitor() {
            { unit = type.getUnit(); }
            @Override 
            public void visit(Tree.TypedDeclaration that) {
                if (that.getType()==type) {
                    dec = that.getDeclarationModel();
//                    union(that.getType().getTypeModel());
                }
                super.visit(that);
            }            
        };
        itv.visit(cu);
        return itv.inferredType;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), false);
    }

    @Override
    public Point getSelection(IDocument document) {
        return selection;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return desc + " '" + infType.getProducedTypeName(rootNode.getUnit()) + "'";
    }

    @Override
    public Image getImage() {
        return REVEAL;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    static void addTypingProposals(Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, Node node,
            Tree.Declaration decNode, CeylonEditor editor) {
        if (decNode instanceof Tree.TypedDeclaration && 
                !(decNode instanceof Tree.ObjectDefinition) &&
                !(decNode instanceof Tree.Variable)) {
            Tree.Type type = ((Tree.TypedDeclaration) decNode).getType();
            if (type instanceof Tree.LocalModifier || 
                    type instanceof Tree.StaticType) {
                addSpecifyTypeProposal(cu, type, proposals, editor);
            }
        }
        else if (node instanceof Tree.LocalModifier || 
                node instanceof Tree.StaticType) {
            addSpecifyTypeProposal(cu, node, proposals, editor);
        }
        if (node instanceof Tree.MemberOrTypeExpression) {
            addSpecifyTypeArgumentsProposal(cu, node, proposals, file);
        }
    }
    
}
