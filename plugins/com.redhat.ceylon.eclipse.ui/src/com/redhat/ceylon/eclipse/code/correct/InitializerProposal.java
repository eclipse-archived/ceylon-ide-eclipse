package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getSortedProposedValues;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleClass;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleMethod;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleValue;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isInBounds;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.LinkedMode;

class InitializerProposal extends CorrectionProposal {

    private final class InitializerValueProposal 
            implements ICompletionProposal, ICompletionProposalExtension2 {
        
        private final String text;
        private final Image image;
        private final int offset;

        private InitializerValueProposal(int offset, String text, Image image) {
            this.offset = offset;
            this.text = text;
            this.image = image;
        }

        protected IRegion getCurrentRegion(IDocument document) 
                throws BadLocationException {
            int start = offset;
            int length = 0;
            for (int i=offset;
                    i<document.getLength(); 
                    i++) {
                char ch = document.getChar(i);
                if (Character.isWhitespace(ch) ||
                        ch==';'||ch==','||ch==')') {
                    break;
                }
                length++;
            }
            return new Region(start, length);
        }
        
        @Override
        public Image getImage() {
            return image;
        }
        
        @Override
        public Point getSelection(IDocument document) {
            return new Point(offset + text.length(), 0);
        }
        
        public void apply(IDocument document) {
            try {
                IRegion region = getCurrentRegion(document);
                document.replace(region.getOffset(), 
                        region.getLength(), text);
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
        public String getDisplayString() {
            return text;
        }
        
        public String getAdditionalProposalInfo() {
            return null;
        }
        
        @Override
        public IContextInformation getContextInformation() {
            return null;
        }
        
        @Override
        public void apply(ITextViewer viewer, char trigger, 
                int stateMask, int offset) {
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
                IRegion region = getCurrentRegion(document);
                String prefix = document.get(region.getOffset(), 
                        offset-region.getOffset());
                return text.startsWith(prefix);
            }
            catch (BadLocationException e) {
                return false;
            }
        }
        
    }

    private CeylonEditor editor;
    
    private final Type type;
    private final Scope scope;
    private final Unit unit;
    private final int exitPos;
    
    InitializerProposal(String name, Change change,
            Declaration declaration, Type type, 
            Region selection, Image image, int exitPos, 
            CeylonEditor editor) {
        super(name, change, selection, image);
        this.exitPos = exitPos;
        this.editor = editor;
        this.scope = declaration.getScope();
        this.unit = declaration.getUnit();
        this.type = type;
    }

    InitializerProposal(String name, Change change,
            Scope scope, Unit unit, Type type, 
            Region selection, Image image, int exitPos, 
            CeylonEditor editor) {
        super(name, change, selection, image);
        this.exitPos = exitPos;
        this.editor = editor;
        this.scope = scope;
        this.unit = unit;
        this.type = type;
    }

    @Override
    public void apply(IDocument document) {
        int lenBefore = document.getLength();
        super.apply(document);
        int lenAfter = document.getLength();
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
                        LinkedMode.addLinkedPosition(linkedModeModel, linkedPosition);
                        int adjustedExitPos = exitPos;
                        if (exitPos>=0 && exitPos>point.x) {
                            adjustedExitPos += lenAfter-lenBefore;
                        }
                        int exitSeq = exitPos>=0 ? 1 : NO_STOP;
                        LinkedMode.installLinkedMode(editor, document, linkedModeModel, 
                                this, new DeleteBlockingExitPolicy(document), 
                                exitSeq, adjustedExitPos);
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
            proposals.add(new InitializerValueProposal(point.x, 
                    document.get(point.x, point.y), null));
        }
        catch (BadLocationException e1) {
            e1.printStackTrace();
        }
        addValueArgumentProposals(point.x, proposals);
        return proposals.toArray(new ICompletionProposal[0]);
    }
    
    private void addValueArgumentProposals(int loc,
            List<ICompletionProposal> props) {
        if (type==null) return;
        TypeDeclaration td = type.getDeclaration();
        for (DeclarationWithProximity dwp: 
                getSortedProposedValues(scope, unit)) {
            if (dwp.isUnimported()) {
                //don't propose unimported stuff b/c adding
                //imports drops us out of linked mode and
                //because it results in a pause
                continue;
            }
            Declaration d = dwp.getDeclaration();
            if (d instanceof Value) {
                Value value = (Value) d;
                if (d.getUnit().getPackage().getNameAsString()
                        .equals(Module.LANGUAGE_MODULE_NAME)) {
                    if (isIgnoredLanguageModuleValue(value)) {
                        continue;
                    }
                }
                Type vt = value.getType();
                if (vt!=null && !vt.isNothing() &&
                    ((td instanceof TypeParameter) && 
                        isInBounds(((TypeParameter)td).getSatisfiedTypes(), vt) || 
                            vt.isSubtypeOf(type))) {
                    props.add(new InitializerValueProposal(loc, d.getName(),
                            getImageForDeclaration(d)));
                }
            }
            if (d instanceof Function) {
                if (!d.isAnnotation()) {
                    Function method = (Function) d;
                    if (d.getUnit().getPackage().getNameAsString()
                            .equals(Module.LANGUAGE_MODULE_NAME)) {
                        if (isIgnoredLanguageModuleMethod(method)) {
                            continue;
                        }
                    }
                    Type mt = method.getType();
                    if (mt!=null && !mt.isNothing() &&
                            ((td instanceof TypeParameter) && 
                                    isInBounds(((TypeParameter)td).getSatisfiedTypes(), mt) || 
                                    mt.isSubtypeOf(type))) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(d.getName());
                        appendPositionalArgs(d, unit, sb, false, false);
                        props.add(new InitializerValueProposal(loc, sb.toString(),
                                getImageForDeclaration(d)));
                    }
                }
            }
            if (d instanceof Class) {
                Class clazz = (Class) d;
                if (!clazz.isAbstract() && !d.isAnnotation()) {
                    if (d.getUnit().getPackage().getNameAsString()
                            .equals(Module.LANGUAGE_MODULE_NAME)) {
                        if (isIgnoredLanguageModuleClass(clazz)) {
                            continue;
                        }
                    }
                    Type ct = clazz.getType();
                    if (ct!=null && !ct.isNothing() &&
                            ((td instanceof TypeParameter) && 
                                    isInBounds(((TypeParameter)td).getSatisfiedTypes(), ct) || 
                                    ct.getDeclaration().equals(type.getDeclaration()) ||
                                    ct.isSubtypeOf(type))) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(d.getName());
                        appendPositionalArgs(d, unit, sb, false, false);
                        props.add(new InitializerValueProposal(loc, sb.toString(),
                                getImageForDeclaration(d)));
                    }
                }
            }
        }
    }
}
