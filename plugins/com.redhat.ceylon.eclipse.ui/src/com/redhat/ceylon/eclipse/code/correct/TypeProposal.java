package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importProposals;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.platform.platformJ2C;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.ide.common.platform.ReplaceEdit;
import com.redhat.ceylon.ide.common.platform.TextChange;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

import ceylon.interop.java.CeylonMutableSet;

public class TypeProposal 
        implements ICompletionProposal, 
                   ICompletionProposalExtension2, 
                   ICompletionProposalExtension6 {
    
    private final Type type;
    private final int offset;
    private final String text;
    private final Tree.CompilationUnit rootNode;
    private Point selection;
    private String description;

    public TypeProposal(int offset, Type type,
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
        TextChange change = new platformJ2C()
                .newChange("Specify Type", 
                        new correctJ2C().newDocument(document));
        change.initMultiEdit();
        HashSet<Declaration> decs =
                new HashSet<Declaration>();
        if (type!=null) {
            importProposals()
                .importType(
                        new CeylonMutableSet<>(null, decs), 
                        type, rootNode);
        }
        int il = (int) importProposals()
                .applyImports(change, 
                        new CeylonMutableSet<>(null, decs), 
                        rootNode, change.getDocument());
        change.addEdit(new ReplaceEdit(offset,
                getCurrentLength(document), text));
        change.apply();
        selection = new Point(offset+il, text.length());
    }

    private int getCurrentLength(IDocument document) {
        int length = 0;
        for (int i=offset;
                i<document.getLength(); 
                i++) {
            char ch;
            try {
                ch = document.getChar(i);
            }
            catch (BadLocationException e) {
                break;
            }
            if (Character.isWhitespace(ch)) {
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
            String prefix =
                    document.get(this.offset,
                            offset-this.offset);
            String filter = prefix.trim().toLowerCase();
            return ModelUtil.isNameMatching(prefix, text) ||
                    text.toLowerCase().startsWith(filter);
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
    public StyledString getStyledDisplayString() {
        StyledString result = new StyledString();
        Highlights.styleFragment(result, 
                getDisplayString(), false, null, 
                CeylonPlugin.getCompletionFont());
        return result;
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

    public static ProposalPosition getTypeProposals(IDocument document, 
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
                infType.isTypeParameter() ||
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
                infType.isTypeParameter() ||
                infType.isUnion() || 
                infType.isIntersection()) {
            proposals[i++] = 
                    new TypeProposal(offset, infType, 
                            infType.asSourceCodeString(unit), 
                            infType.asString(unit), 
                            rootNode);
        }
        Collections.sort(supertypes, 
                new Comparator<TypeDeclaration>() {
            @Override
            public int compare(TypeDeclaration x, 
                    TypeDeclaration y) {
                if (x.inherits(y)) {
                    return 1;
                }
                if (y.inherits(x)) {
                    return -1;
                }
                return y.getName().compareTo(x.getName());
            }
        });
        for (int j=supertypes.size()-1; j>=0; j--) {
            Type type = 
                    infType.getSupertype(supertypes.get(j));
            proposals[i++] = 
                    new TypeProposal(offset, type, 
                            type.asSourceCodeString(unit), 
                            type.asString(unit), 
                            rootNode);
        }
        return new ProposalPosition(document, offset, length, 
                0, proposals);
    }
    
}