package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;
import static com.redhat.ceylon.model.typechecker.model.Util.isTypeUnknown;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.IntersectionType;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.UnionType;
import com.redhat.ceylon.model.typechecker.model.Unit;

class TypeProposal implements ICompletionProposal, 
        ICompletionProposalExtension2 {
    
    private final Type type;
    private final int offset;
    private final String text;
    private final Tree.CompilationUnit rootNode;
    private Point selection;
    private String description;

    private TypeProposal(int offset, Type type,
            String text, String desc, 
            Tree.CompilationUnit rootNode) {
        this.type = type;
        this.offset = offset;
        this.description = desc;
        this.rootNode = rootNode;
        this.text = text;
    }

    @Override
    public void apply(IDocument document) {
        try {
            final DocumentChange change = 
                    new DocumentChange("Specify Type", document);
            change.setEdit(new MultiTextEdit());
            HashSet<Declaration> decs = 
                    new HashSet<Declaration>();
            if (type!=null) {
                importType(decs, type, rootNode);
            }
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
        return description;
    }

    @Override
    public Image getImage() {
        if (type==null) {
            return getDecoratedImage(CEYLON_LITERAL, 0, false);
        }
        else {
            return getImageForDeclaration(type.getDeclaration());
        }
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    static ProposalPosition getTypeProposals(IDocument document, 
            int offset, int length, Type infType,
            Tree.CompilationUnit rootNode, String kind) {
        
        TypeDeclaration td = infType.getDeclaration();
        List<TypeDeclaration> supertypes = 
                isTypeUnknown(infType) || 
                infType.isTypeConstructor() ?
                    Collections.<TypeDeclaration>emptyList() :
                    td.getSupertypeDeclarations();
        
        int size = supertypes.size();
        if (kind!=null) size++;
        if (infType.isTypeConstructor() ||
                infType.isUnion() || 
                infType.isIntersection()) {
            size++;
        }
        
        ICompletionProposal[] proposals = 
                new ICompletionProposal[size];
        int i=0;
        if (kind!=null) {
            proposals[i++] =
                    new TypeProposal(offset, null, kind, kind, rootNode);
        }
        Unit unit = rootNode.getUnit();
        if (infType.isTypeConstructor() ||
                infType.isUnion() || 
                infType.isIntersection()) {
            proposals[i++] = 
                    new TypeProposal(offset, infType, 
                            infType.getProducedTypeNameInSource(unit), 
                            infType.getProducedTypeName(unit), 
                            rootNode);
        }
        for (int j=supertypes.size()-1; j>=0; j--) {
            Type type = 
                    infType.getSupertype(supertypes.get(j));
            proposals[i++] = 
                    new TypeProposal(offset, type, 
                            type.getProducedTypeNameInSource(unit), 
                            type.getProducedTypeName(unit), 
                            rootNode);
        }
        return new ProposalPosition(document, offset, length, 
                0, proposals);
    }
    
}