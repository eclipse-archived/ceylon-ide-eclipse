package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.styleProposal;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.correct.SpecifyTypeArgumentsProposal.addSpecifyTypeArgumentsProposal;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.installLinkedMode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.MINOR_CHANGE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class SpecifyTypeProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private class TypeProposal implements ICompletionProposal, 
            ICompletionProposalExtension2 {
        
        private final ProducedType type;
        private final int offset;
        private final String text;

        private TypeProposal(int offset, ProducedType type) {
            this.type = type;
            this.offset = offset;
            this.text = type.getProducedTypeName(rootNode.getUnit());
        }

        @Override
        public void apply(IDocument document) {
            try {
                final DocumentChange change = 
                        new DocumentChange("Specify Type", document);
                change.setEdit(new MultiTextEdit());
                HashSet<Declaration> decs = 
                        new HashSet<Declaration>();
                importType(decs, type, rootNode);
                int il = applyImports(change, decs, rootNode, document);
                change.addEdit(new ReplaceEdit(offset,
                        getCurrentLength(document), text));
                change.perform(new NullProgressMonitor());
                selection = new Point(offset+il, text.length());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int getCurrentLength(IDocument document) 
                throws BadLocationException {
            int length = 0;
            for (int i=offset;
                    i<document.getLength(); 
                    i++) {
                if (Character.isWhitespace(document.getChar(i))) {
                    break;
                }
                length++;
            }
            return length;
        }
        
        @Override
        public Point getSelection(IDocument document) {
            return selection;
        }

        @Override
        public void apply(ITextViewer viewer, char trigger, int stateMask,
                int offset) {
            apply(viewer.getDocument());
        }

        @Override
        public void selected(ITextViewer viewer, boolean smartToggle) {}

        @Override
        public void unselected(ITextViewer viewer) {}

        @Override
        public boolean validate(IDocument document, int offset,
                DocumentEvent event) {
            try {
                String prefix = document.get(this.offset, 
                        offset-this.offset);
                return text.startsWith(prefix);
            }
            catch (BadLocationException e) {
                return false;
            }
        }

        @Override
        public String getAdditionalProposalInfo() {
            return null;
        }

        @Override
        public String getDisplayString() {
            return text;
        }

        @Override
        public Image getImage() {
            return getImageForDeclaration(type.getDeclaration());
        }

        @Override
        public IContextInformation getContextInformation() {
            return null;
        }
    }

    private final ProducedType infType;
    private final String desc;
    private final Tree.Type typeNode;
    private final CeylonEditor editor;
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
        final int offset = typeNode.getStartIndex();
        final int length = typeNode.getStopIndex()-offset+1;
        if (editor==null) {
//            final TextChange change = 
//                    new TextFileChange("Specify Type", file); 
            DocumentChange change = 
                    new DocumentChange("Specify Type", document);
            change.setEdit(new MultiTextEdit());
            try {
                HashSet<Declaration> decs = new HashSet<Declaration>();
                importType(decs, infType, rootNode);
                int il = applyImports(change, decs, rootNode, document);
                String typeName = infType.getProducedTypeName(rootNode.getUnit());
                change.addEdit(new ReplaceEdit(offset, length, typeName));
                change.perform(new NullProgressMonitor());
                selection = new Point(offset+il, typeName.length());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            LinkedModeModel linkedModeModel = new LinkedModeModel();
            List<ProducedType> supertypes = infType.getSupertypes();
            TypeDeclaration td = infType.getDeclaration();
            if (td instanceof UnionType || 
                    td instanceof IntersectionType) {
                supertypes.add(0, infType);
            }
            int size = supertypes.size();
            ICompletionProposal[] proposals = 
                    new ICompletionProposal[size];
            for (int i=0; i<size; i++) {
                ProducedType type = supertypes.get(i);
                proposals[i] = new TypeProposal(offset, type);
            }
            ProposalPosition linkedPosition = 
                    new ProposalPosition(document, offset, 
                            length, 0, proposals);
            try {
                addLinkedPosition(linkedModeModel, linkedPosition);
                installLinkedMode(editor, document, linkedModeModel, 
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
                    union(that.getType().getTypeModel());
                }
                super.visit(that);
            }            
        };
        itv.visit(cu);
        return itv.inferredType;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return styleProposal(getDisplayString());
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
        return MINOR_CHANGE;
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
