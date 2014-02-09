package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getInlineFunctionDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getInlineFunctionTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getSortedProposedValues;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isInBounds;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public final class RefinementCompletionProposal extends CompletionProposal {
	
    final class ReturnValueContextInfo implements IContextInformation {
        @Override
        public String getInformationDisplayString() {
            if (declaration instanceof TypedDeclaration) {
                final Unit unit = cpc.getRootNode().getUnit();
                return getType().getProducedTypeName(unit);
            }
            else {
                return null;
            }
        }

        @Override
        public Image getImage() {
            return getImageForDeclaration(declaration);
        }

        @Override
        public String getContextDisplayString() {
            return "Return value of '" + declaration.getName() + "'";
        }
    }

    public static Image DEFAULT_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(CeylonResources.CEYLON_DEFAULT_REFINEMENT);
    public static Image FORMAL_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(CeylonResources.CEYLON_FORMAL_REFINEMENT);
    
    static void addRefinementProposal(int offset, final Declaration dec, 
            ClassOrInterface ci, Node node, Scope scope, String prefix, 
            CeylonParseController cpc, IDocument doc, 
            List<ICompletionProposal> result, boolean preamble) {
        boolean isInterface = scope instanceof Interface;
        ProducedReference pr = getRefinedProducedReference(scope, dec);
        Unit unit = node.getUnit();
        result.add(new RefinementCompletionProposal(offset, prefix, pr,
                getRefinementDescriptionFor(dec, pr, unit), 
                getRefinementTextFor(dec, pr, unit, isInterface, ci, 
                        getDefaultLineDelimiter(doc) + getIndent(node, doc), 
                        true, preamble), 
                cpc, dec, scope, false));
    }
    
    static void addNamedArgumentProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration dec, Scope scope) {
        //TODO: type argument substitution using the ProducedReference of the primary node
        result.add(new RefinementCompletionProposal(offset, prefix, 
                dec.getReference(), //TODO: this needs to do type arg substitution
                getDescriptionFor(dwp), 
                getTextFor(dwp) + " = nothing;", 
                cpc, dec, scope, true));
    }

    static void addInlineFunctionProposal(int offset, Declaration dec, 
            Scope scope, Node node, String prefix, CeylonParseController cpc, 
            IDocument doc, List<ICompletionProposal> result) {
        //TODO: type argument substitution using the ProducedReference of the primary node
        if (dec.isParameter()) {
            Parameter p = ((MethodOrValue) dec).getInitializerParameter();
            Unit unit = node.getUnit();
            result.add(new RefinementCompletionProposal(offset, prefix, 
                    dec.getReference(), //TODO: this needs to do type arg substitution
                    getInlineFunctionDescriptionFor(p, null, unit),
                    getInlineFunctionTextFor(p, null, unit, 
                            getDefaultLineDelimiter(doc) + getIndent(node, doc)),
                    cpc, dec, scope, false));
        }
    }

    public static ProducedReference getRefinedProducedReference(Scope scope, 
            Declaration d) {
        return refinedProducedReference(scope.getDeclaringType(d), d);
    }

    public static ProducedReference getRefinedProducedReference(ProducedType superType, 
            Declaration d) {
        if (superType.getDeclaration() instanceof IntersectionType) {
            for (ProducedType pt: superType.getDeclaration().getSatisfiedTypes()) {
                ProducedReference result = getRefinedProducedReference(pt, d);
                if (result!=null) return result;
            }
            return null; //never happens?
        }
        else {
            ProducedType declaringType = 
                    superType.getDeclaration().getDeclaringType(d);
            if (declaringType==null) return null;
            ProducedType outerType = 
                    superType.getSupertype(declaringType.getDeclaration());
            return refinedProducedReference(outerType, d);
        }
    }
    
    private static ProducedReference refinedProducedReference(ProducedType outerType, 
            Declaration d) {
        List<ProducedType> params = new ArrayList<ProducedType>();
        if (d instanceof Generic) {
            for (TypeParameter tp: ((Generic) d).getTypeParameters()) {
                params.add(tp.getType());
            }
        }
        return d.getProducedReference(outerType, params);
    }
    
	private final CeylonParseController cpc;
	private final Declaration declaration;
	private final ProducedReference pr;
	private final boolean fullType;
	private final Scope scope;

	private RefinementCompletionProposal(int offset, String prefix, 
			ProducedReference pr, String desc, String text, 
			CeylonParseController cpc, Declaration dec, Scope scope,
			boolean fullType) {
		super(offset, prefix, dec.isFormal() ? 
					FORMAL_REFINEMENT : DEFAULT_REFINEMENT, 
				desc, text, false);
		this.cpc = cpc;
		this.declaration = dec;
		this.pr = pr;
		this.fullType = fullType;
		this.scope = scope;
	}

    private ProducedType getType() {
        return fullType ?
                pr.getFullType() :
                pr.getType();
    }

	@Override
	public void apply(IDocument document) {
		int originalLength = document.getLength();
		try {
			imports(document).perform(new NullProgressMonitor());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		offset += document.getLength() - originalLength;
		super.apply(document);
		enterLinkedMode(document);
	}

	private DocumentChange imports(IDocument document)
			throws BadLocationException {
		DocumentChange tc = new DocumentChange("imports", document);
		tc.setEdit(new MultiTextEdit());
		HashSet<Declaration> decs = new HashSet<Declaration>();
		CompilationUnit cu = cpc.getRootNode();
		//TODO for an inline function completion, we don't
		//     need to import the return type
		importSignatureTypes(declaration, cu, decs);
		applyImports(tc, decs, cu, document);
		return tc;
	}

	public String getAdditionalProposalInfo() {
		return getDocumentationFor(cpc, declaration);	
	}
	
    public void enterLinkedMode(IDocument document) {
        try {
            final int loc = offset-prefix.length();
            int pos = text.indexOf("nothing");
            if (pos>0) {
                final LinkedModeModel linkedModeModel = 
                        new LinkedModeModel();
                List<ICompletionProposal> props = 
                        new ArrayList<ICompletionProposal>();
                addProposals(loc+pos, 7, props, prefix);
                LinkedPositionGroup linkedPositionGroup = 
                        new LinkedPositionGroup();
                ProposalPosition linkedPosition = 
                        new ProposalPosition(document, 
                                loc+pos, 7, 0, 
                                props.toArray(NO_COMPLETIONS));
                linkedPositionGroup.addPosition(linkedPosition);
                linkedModeModel.addGroup(linkedPositionGroup);
                linkedModeModel.forceInstall();
                final CeylonEditor editor = 
                        (CeylonEditor) EditorUtil.getCurrentEditor();
                linkedModeModel.addLinkingListener(new ILinkedModeListener() {
                    @Override
                    public void left(LinkedModeModel model, int flags) {
                        editor.clearLinkedMode();
                        //                    linkedModeModel.exit(ILinkedModeListener.NONE);
                        CeylonSourceViewer viewer= editor.getCeylonSourceViewer();
                        if (viewer instanceof IEditingSupportRegistry) {
                            ((IEditingSupportRegistry) viewer).unregister(editingSupport);
                        }
                        editor.getSite().getPage().activate(editor);
                        if ((flags&EXTERNAL_MODIFICATION)==0 && viewer!=null) {
                            viewer.invalidateTextPresentation();
                        }
                    }
                    @Override
                    public void suspend(LinkedModeModel model) {
                        editor.clearLinkedMode();
                    }
                    @Override
                    public void resume(LinkedModeModel model, int flags) {
                        editor.setLinkedMode(model, RefinementCompletionProposal.this);
                    }
                });
                editor.setLinkedMode(linkedModeModel, this);
                CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
                EditorLinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
                ui.setExitPosition(viewer, loc+text.length(), 0, 1);
                ui.setExitPolicy(new DeleteBlockingExitPolicy(document));
                ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
                ui.setDoContextInfo(true);
                ui.enter();

                registerEditingSupport(editor, viewer);
                
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IEditingSupport editingSupport;
    
    private void registerEditingSupport(final CeylonEditor editor,
            CeylonSourceViewer viewer) {
        if (viewer instanceof IEditingSupportRegistry) {
            editingSupport = new IEditingSupport() {
                public boolean ownsFocusShell() {
                    Shell editorShell= editor.getSite().getShell();
                    Shell activeShell= editorShell.getDisplay().getActiveShell();
                    if (editorShell == activeShell)
                        return true;
                    return false;
                }
                public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
                    return false; //leave on external modification outside positions
                }
            };
            ((IEditingSupportRegistry) viewer).register(editingSupport);
        }
    }
    
    @Override
    public IContextInformation getContextInformation() {
        return new ReturnValueContextInfo();
    }
    
    private void addProposals(final int loc, int len, 
            List<ICompletionProposal> props, String prefix) {
        Unit unit = cpc.getRootNode().getUnit();
        ProducedType type = getType();
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
            final String name = d.getName();
            String[] split = prefix.split("\\s+");
            if (split.length>0 && 
                    name.equals(split[split.length-1])) {
                continue;
            }
            if (d instanceof Value) {
                if (d.getUnit().getPackage().getNameAsString()
                        .equals(Module.LANGUAGE_MODULE_NAME)) {
                    if (name.equals("process") ||
                            name.equals("language") ||
                            name.equals("emptyIterator") ||
                            name.equals("infinity") ||
                            name.endsWith("IntegerValue") ||
                            name.equals("finished")) {
                        continue;
                    }
                }
                ProducedType vt = ((Value) d).getType();
                if (vt!=null && !vt.isNothing() &&
                    ((td instanceof TypeParameter) && 
                        isInBounds(((TypeParameter)td).getSatisfiedTypes(), vt) || 
                            vt.isSubtypeOf(type))) {
                    props.add(new NestedCompletionProposal(d, loc, len));
                }
            }
            if (d instanceof Class && 
                    !((Class) d).isAbstract() && !d.isAnnotation()) {
                if (d.getUnit().getPackage().getNameAsString()
                        .equals(Module.LANGUAGE_MODULE_NAME)) {
                    if (name.equals("String") ||
                            name.equals("Integer") ||
                            name.equals("Float") ||
                            name.equals("Character")) {
                        continue;
                    }
                }
                ProducedType ct = ((Class) d).getType();
                if (ct!=null && !ct.isNothing() &&
                    ((td instanceof TypeParameter) && 
                        isInBounds(((TypeParameter)td).getSatisfiedTypes(), ct) || 
                            ct.getDeclaration().equals(type.getDeclaration()) ||
                            ct.isSubtypeOf(type))) {
                    props.add(new NestedCompletionProposal(d, loc, len));
                }
            }
        }
    }
    
    final class NestedCompletionProposal implements ICompletionProposal {
        
        private final Declaration d;
        private final int offset;
        private final int len;
        
        public NestedCompletionProposal(Declaration d, int offset, int len) {
            super();
            this.d = d;
            this.offset = offset;
            this.len = len;
        }

        @Override
        public void apply(IDocument document) {
            try {
                document.replace(offset, len, getText());
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Point getSelection(IDocument document) {
            return null;
        }

        @Override
        public String getAdditionalProposalInfo() {
            return null;
        }

        @Override
        public String getDisplayString() {
            return getText();
        }

        @Override
        public Image getImage() {
            return getImageForDeclaration(d);
        }

        @Override
        public IContextInformation getContextInformation() {
           return null;
        }
        
        private String getText() {
            StringBuilder sb = new StringBuilder()
                    .append(d.getName());
            if (d instanceof Class) {
                appendPositionalArgs(d, d.getReference(), 
                        cpc.getRootNode().getUnit(), sb, false);
            }
            return sb.toString();
        }

    }
    
}