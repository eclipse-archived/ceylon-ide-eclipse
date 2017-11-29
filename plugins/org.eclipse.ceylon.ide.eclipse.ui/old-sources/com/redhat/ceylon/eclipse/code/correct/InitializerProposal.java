/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.getAssignableLiterals;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.getCurrentSpecifierRegion;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.getProposedName;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.getSortedProposedValues;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleClass;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleMethod;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.isIgnoredLanguageModuleValue;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.isInBounds;
import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoFile;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.getCompletionFont;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_LITERAL;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.eclipse.util.LinkedMode;
import org.eclipse.ceylon.ide.common.model.ModifiableSourceFile;
import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.Constructor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.Functional;
import org.eclipse.ceylon.model.typechecker.model.ModelUtil;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.NothingType;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypeParameter;
import org.eclipse.ceylon.model.typechecker.model.Unit;
import org.eclipse.ceylon.model.typechecker.model.Value;

class InitializerProposal extends CorrectionProposal {
    
    final class NestedCompletionProposal 
            implements ICompletionProposal, 
                       ICompletionProposalExtension2,
                       ICompletionProposalExtension6 {
        
        private Unit getUnit() {
            return unit;
        }

        private final Declaration dec;
        private final int offset;

        NestedCompletionProposal(Declaration dec, int offset) {
            this.offset = offset;
            this.dec = dec;
        }

        @Override
        public Point getSelection(IDocument document) {
            return null;
        }
        
        public void apply(IDocument document) {
            try {
                IRegion region = 
                        getCurrentSpecifierRegion(document, 
                                offset);
                document.replace(region.getOffset(), 
                        region.getLength(), getText(false));
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
        public String getDisplayString() {
            return getText(true);
        }
        
        @Override
        public StyledString getStyledDisplayString() {
            StyledString result = new StyledString();
            Highlights.styleFragment(result, 
                    getDisplayString(), false, null, 
                    getCompletionFont());
            return result;
        }

        @Override
        public Image getImage() {
            return getImageForDeclaration(dec);
        }

        public String getAdditionalProposalInfo() {
            return null;
        }
        
        @Override
        public IContextInformation getContextInformation() {
            return null;
        }
        
        private String getText(boolean description) {
            StringBuilder sb = new StringBuilder();
            Unit unit = getUnit();
            sb.append(getProposedName(null, dec, unit));
            if (dec instanceof Functional) {
                appendPositionalArgs(dec, unit, sb, false, 
                        description);
            }
            return sb.toString();
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
        public boolean validate(IDocument document, 
                int currentOffset, DocumentEvent event) {
            if (event==null) {
                return true;
            }
            else {
                try {
                    IRegion region = 
                            getCurrentSpecifierRegion(document, 
                                    offset);
                    String content = 
                            document.get(region.getOffset(), 
                                    currentOffset-region.getOffset());
                    return isContentValid(content);
                }
                catch (BadLocationException e) {
                    // ignore concurrently modified document
                }
                return false;
            }
        }

        private boolean isContentValid(String content) {
            String filter = content.trim().toLowerCase();
            return ModelUtil.isNameMatching(content, dec) ||
                    getProposedName(null, dec, getUnit())
                        .toLowerCase()
                        .startsWith(filter);
        }
        
    }

    final class NestedLiteralCompletionProposal 
            implements ICompletionProposal, 
                       ICompletionProposalExtension2,
                       ICompletionProposalExtension6 {
        
        private final String value;
        private final int offset;
        
        NestedLiteralCompletionProposal(String value, int offset) {
            this.offset = offset;
            this.value = value;
        }
        
        @Override
        public Point getSelection(IDocument document) {
            return null;
        }
        
        public void apply(IDocument document) {
            try {
                IRegion region = 
                        getCurrentSpecifierRegion(document, 
                                offset);
                document.replace(region.getOffset(), 
                        region.getLength(), value);
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
        public String getDisplayString() {
            return value;
        }
        
        @Override
        public StyledString getStyledDisplayString() {
            StyledString result = new StyledString();
            Highlights.styleFragment(result, 
                    getDisplayString(), false, null, 
                    getCompletionFont());
            return result;
        }
        
        @Override
        public Image getImage() {
            return getDecoratedImage(CEYLON_LITERAL, 0, false);
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
        public boolean validate(IDocument document, 
                int currentOffset, DocumentEvent event) {
            if (event==null) {
                return true;
            }
            else {
                try {
                    IRegion region = 
                            getCurrentSpecifierRegion(document, 
                                    offset);
                    String content = 
                            document.get(region.getOffset(), 
                                    currentOffset-region.getOffset());
                    String filter = content.trim().toLowerCase();
                    if (value.toLowerCase().startsWith(filter)) {
                        return true;
                    }
                }
                catch (BadLocationException e) {
                    // ignore concurrently modified document
                }
                return false;
            }
        }
        
    }
        
    private final Type type;
    private final Scope scope;
    private final Unit unit;
    private int exitPos;
    
    InitializerProposal(String name, Change change,
            Declaration declaration, Type type, 
            Region selection, Image image, int exitPos) {
        this(name, change, 
                declaration.getScope(),
                declaration.getUnit(), 
                type, selection, image, exitPos);
    }

    InitializerProposal(String name, Change change,
            Scope scope, Unit unit, Type type, 
            Region selection, Image image, int exitPos) {
        super(name, change, selection, image);
        this.exitPos = exitPos;
        this.scope = scope;
        this.unit = unit;
        this.type = type;
    }
    
    @Override
    public Point getSelection(IDocument document) {
        //we don't apply a selection because:
        //1. we're using linked mode anyway, and
        //2. the change might have been applied to
        //   a different editor to the one from
        //   which the quick fix was invoked.
        return null;
    }

    @Override
    public void apply(IDocument document) {
        CeylonEditor editor = null;
        if (unit instanceof ModifiableSourceFile) {
            ModifiableSourceFile<IProject,IResource,IFolder,IFile> msf = 
                    (ModifiableSourceFile<IProject,IResource,IFolder,IFile>) unit;
            IFile file = msf.getResourceFile();
            if (file!=null) {
                editor = (CeylonEditor) gotoFile(file, 0, 0);
                //NOTE: the document we're given is the one
                //for the editor from which the quick fix was
                //invoked, not the one to which the fix applies
                IDocument ed = 
                        editor.getParseController()
                            .getDocument();
                if (ed!=document) {
                    document = ed;
                    exitPos = -1;
                }
            }
        }
        int lenBefore = document.getLength();
        super.apply(document);
        int lenAfter = document.getLength();
        
        Point point = super.getSelection(document);
        if (point==null) return;
        
        editor.selectAndReveal(point.x, point.y);
        
        //TODO: preference to disable linked mode?
        if (lenAfter>lenBefore && editor!=null) {
            if (point.y>0) {
                LinkedModeModel linkedModeModel = 
                        new LinkedModeModel();
                ICompletionProposal[] proposals = 
                        getProposals(document, point);
                if (proposals.length>1) {
                    ProposalPosition linkedPosition = 
                            new ProposalPosition(document, 
                                    point.x, point.y, 0, 
                                    proposals);
                    try {
                        LinkedMode.addLinkedPosition(
                                linkedModeModel, linkedPosition);
                        int adjustedExitPos = exitPos;
                        if (exitPos>=0 && exitPos>point.x) {
                            adjustedExitPos += lenAfter-lenBefore;
                        }
                        int exitSeq = exitPos>=0 ? 1 : NO_STOP;
                        LinkedMode.installLinkedMode(editor, 
                                document, linkedModeModel, this, 
                                new DeleteBlockingExitPolicy(document), 
                                exitSeq, adjustedExitPos);
                    } 
                    catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private ICompletionProposal[] getProposals(
            IDocument document, Point point) {
        List<ICompletionProposal> proposals = 
                new ArrayList<ICompletionProposal>();
//        try {
//            //this is totally lame
//            //TODO: see InvocationCompletionProcessor
//            proposals.add(new NestedLiteralCompletionProposal(
//                    document.get(point.x, point.y), point.x));
//        }
//        catch (BadLocationException e1) {
//            e1.printStackTrace();
//        }
        addValueArgumentProposals(point.x, proposals);
        return proposals.toArray(new ICompletionProposal[0]);
    }
    
    private void addValueArgumentProposals(int loc,
            List<ICompletionProposal> props) {
        if (type==null) return;
        for (String value: 
                getAssignableLiterals(type, unit)) {
            props.add(new NestedLiteralCompletionProposal(
                        value, loc));
        }
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
            if (d instanceof NothingType) {
                return;
            }
            String pname = 
                    d.getUnit()
                        .getPackage()
                        .getNameAsString();
            boolean inLangModule = 
                    pname.equals(Module.LANGUAGE_MODULE_NAME);
            if (d instanceof Value) {
                Value value = (Value) d;
                if (inLangModule) {
                    if (isIgnoredLanguageModuleValue(value)) {
                        continue;
                    }
                }
                Type vt = value.getType();
                if (vt!=null && !vt.isNothing() &&
                    (isTypeParamInBounds(td, vt) || 
                            vt.isSubtypeOf(type))) {
                    props.add(new NestedCompletionProposal(d, loc));
                }
            }
            if (d instanceof Function) {
                if (!d.isAnnotation()) {
                    Function method = (Function) d;
                    if (inLangModule) {
                        if (isIgnoredLanguageModuleMethod(method)) {
                            continue;
                        }
                    }
                    Type mt = method.getType();
                    if (mt!=null && !mt.isNothing() &&
                            (isTypeParamInBounds(td, mt) || 
                                    mt.isSubtypeOf(type))) {
                        props.add(new NestedCompletionProposal(d, loc));
                    }
                }
            }
            if (d instanceof Class) {
                Class clazz = (Class) d;
                if (!clazz.isAbstract() && !d.isAnnotation()) {
                    if (inLangModule) {
                        if (isIgnoredLanguageModuleClass(clazz)) {
                            continue;
                        }
                    }
                    Type ct = clazz.getType();
                    if (ct!=null && !ct.isNothing() &&
                            (isTypeParamInBounds(td, ct) || 
                                    ct.getDeclaration()
                                        .equals(type.getDeclaration()) ||
                                    ct.isSubtypeOf(type))) {
                        if (clazz.getParameterList()!=null) {
                            props.add(new NestedCompletionProposal(d, loc));
                        }
                        for (Declaration m: clazz.getMembers()) {
                            if (m instanceof Constructor && 
                                    m.isShared() &&
                                    m.getName()!=null) {
                                props.add(new NestedCompletionProposal(m, loc));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isTypeParamInBounds(TypeDeclaration td, Type t) {
        return (td instanceof TypeParameter) && 
                isInBounds(((TypeParameter)td).getSatisfiedTypes(), t);
    }
}
