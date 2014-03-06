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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class SpecifyTypeProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final ProducedType infType;
    private final String desc;
    private final Tree.Type typeNode;
    private final CeylonEditor editor;
    private final Tree.CompilationUnit rootNode;
    private final IFile file;
    private Point selection;
    
    private SpecifyTypeProposal(String desc, Tree.Type type,
            Tree.CompilationUnit cu, ProducedType infType, 
            CeylonEditor editor, IFile file) {
        this.desc = desc;
        this.typeNode = type;
        this.rootNode = cu;
        this.infType = infType;
        this.editor = editor;
        this.file = file;
    }
    
    @Override
    public void apply(IDocument document) {
        final int offset = typeNode.getStartIndex();
        final int length = typeNode.getStopIndex()-offset+1;
        if (editor==null) {
//            final TextChange change = 
//                    new TextFileChange("Specify Type", file); 
            final DocumentChange change = 
                    new DocumentChange("Specify Type", document);
            change.setEdit(new MultiTextEdit());
            try {
                HashSet<Declaration> decs = new HashSet<Declaration>();
                importType(decs, infType, rootNode);
                int il = applyImports(change, decs, rootNode, document);
                String typeName = infType.getProducedTypeName(rootNode.getUnit());
                change.addEdit(new ReplaceEdit(offset, length, typeName));
                change.perform(new NullProgressMonitor());
                selection = new Point(offset+il, offset+il+typeName.length());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            final DocumentChange change = 
                    new DocumentChange("Specify Type", document);
            change.setEdit(new MultiTextEdit());
            final LinkedModeModel linkedModeModel = new LinkedModeModel();
            CeylonParseController cpc = editor.getParseController();
            final Tree.CompilationUnit rootNode = cpc.getRootNode();
            Unit unit = rootNode.getUnit();
            List<ProducedType> supertypes = infType.getSupertypes();
            ICompletionProposal[] proposals = 
                    new ICompletionProposal[supertypes.size()];
            for (int i=0; i<supertypes.size(); i++) {
                final ProducedType type = supertypes.get(i);
                final String typeName = type.getProducedTypeName(unit);
                Image image = getImageForDeclaration(type.getDeclaration());
                proposals[i] = new LinkedModeCompletionProposal(offset, 
                        typeName, 0, image) {
                    @Override
                    public void apply(IDocument document) {
                        try {
                            IRegion region = getCurrentRegion(document);
                            HashSet<Declaration> decs = 
                                    new HashSet<Declaration>();
                            importType(decs, type, rootNode);
                            int il = applyImports(change, decs, rootNode, document);
                            change.addEdit(new ReplaceEdit(region.getOffset(), 
                                    region.getLength(), typeName));
                            change.perform(new NullProgressMonitor());
                            selection = new Point(offset+il, offset+il+typeName.length());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public Point getSelection(IDocument document) {
                        return selection;
                    }
                };
            }
            ProposalPosition linkedPosition = 
                    new ProposalPosition(document, offset, length, 
                            0, proposals);
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
            Collection<ICompletionProposal> proposals, IFile file, 
            CeylonEditor editor) {
        SpecifyTypeProposal proposal = 
                createProposal(cu, node, editor, file);
        if (proposal!=null) {
            proposals.add(proposal);
        }
    }

    public static SpecifyTypeProposal createProposal(Tree.CompilationUnit cu, 
            Node node, CeylonEditor editor, IFile file) {
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
                    infType, editor, file);
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
                addSpecifyTypeProposal(cu, type, proposals, file, editor);
            }
        }
        else if (node instanceof Tree.LocalModifier || 
                node instanceof Tree.StaticType) {
            addSpecifyTypeProposal(cu, node, proposals, file, editor);
        }
        if (node instanceof Tree.MemberOrTypeExpression) {
            addSpecifyTypeArgumentsProposal(cu, node, proposals, file);
        }
    }
    
}
