package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getSortedProposedValues;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isInBounds;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.installLinkedMode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.code.complete.CompletionUtil;
import com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;

class ParameterProposal extends CorrectionProposal {

    private final class DefaultArgProposal extends LinkedModeCompletionProposal {
        private final Point point;

        private DefaultArgProposal(int offset, String text, Image image, Point point) {
            super(offset, text, 0, image);
            this.point = point;
        }

        @Override
        protected IRegion getCurrentRegion(IDocument document)
                throws BadLocationException {
            return new Region(point.x, point.y);
        }
    }

    private CeylonEditor editor;
    
    private final ProducedType type;
    private final Declaration declaration;
    
    ParameterProposal(String name, Change change,
            Declaration declaration, ProducedType type, 
            Point selection, Image image, CeylonEditor editor) {
        super(name, change, selection, image);
        this.editor = editor;
        this.declaration = declaration;
        this.type = type;
    }

    @Override
    public void apply(IDocument document) {
        super.apply(document);
        if (editor==null) {
            IEditorPart ed = EditorUtil.getCurrentEditor();
            if (ed instanceof CeylonEditor) {
                editor = (CeylonEditor) ed;
            }
        }
        if (editor!=null) {
            Point point = getSelection(document);
            if (point.y>0) {
                LinkedModeModel linkedModeModel = new LinkedModeModel();
                ICompletionProposal[] proposals = getProposals(document, point);
                if (proposals.length>1) {
                    ProposalPosition linkedPosition = 
                            new ProposalPosition(document, point.x, point.y, 0, 
                                    proposals);
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
        }
    }

    private ICompletionProposal[] getProposals(IDocument document,
            Point point) {
        List<ICompletionProposal> proposals = 
                new ArrayList<ICompletionProposal>();
        try {
            proposals.add(new DefaultArgProposal(point.x, 
                    document.get(point.x, point.y), null, 
                    point));
        }
        catch (BadLocationException e1) {
            e1.printStackTrace();
        }
        addValueArgumentProposals(point.x, proposals, point);
        return proposals.toArray(new ICompletionProposal[0]);
    }
    
    private void addValueArgumentProposals(int loc,
            List<ICompletionProposal> props, final Point point) {
        TypeDeclaration td = type.getDeclaration();
        for (DeclarationWithProximity dwp: 
                getSortedProposedValues(declaration.getScope(), 
                        declaration.getUnit())) {
            if (dwp.isUnimported()) {
                //don't propose unimported stuff b/c adding
                //imports drops us out of linked mode and
                //because it results in a pause
                continue;
            }
            Declaration d = dwp.getDeclaration();
            final String name = d.getName();
            if (d instanceof Value) {
                if (d.getUnit().getPackage().getNameAsString()
                        .equals(Module.LANGUAGE_MODULE_NAME)) {
                    if (CompletionUtil.isIgnoredLanguageModuleValue(name)) {
                        continue;
                    }
                }
                ProducedType vt = ((Value) d).getType();
                if (vt!=null && !vt.isNothing() &&
                    ((td instanceof TypeParameter) && 
                        isInBounds(((TypeParameter)td).getSatisfiedTypes(), vt) || 
                            vt.isSubtypeOf(type))) {
                    props.add(new DefaultArgProposal(loc, d.getName(),
                            getImageForDeclaration(d), point));
                }
            }
            if (d instanceof Class &&
                    !((Class) d).isAbstract() && !d.isAnnotation()) {
                if (d.getUnit().getPackage().getNameAsString()
                        .equals(Module.LANGUAGE_MODULE_NAME)) {
                    if (CompletionUtil.isIgnoredLanguageModuleClass(name)) {
                        continue;
                    }
                }
                ProducedType ct = ((Class) d).getType();
                if (ct!=null && !ct.isNothing() &&
                    ((td instanceof TypeParameter) && 
                        isInBounds(((TypeParameter)td).getSatisfiedTypes(), ct) || 
                            ct.getDeclaration().equals(type.getDeclaration()) ||
                            ct.isSubtypeOf(type))) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(d.getName());
                    appendPositionalArgs(d, declaration.getUnit(), sb, false, false);
                    props.add(new DefaultArgProposal(loc, sb.toString(),
                            getImageForDeclaration(d), point));
                }
            }
        }
    }
}
