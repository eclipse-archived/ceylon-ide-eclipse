package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.hover.CeylonHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importEdit;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
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
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

final class DeclarationCompletionProposal extends CompletionProposal {
	
	private final CeylonParseController cpc;
	private final Declaration declaration;
	private final boolean addimport;
	private final ProducedReference producedReference;

	DeclarationCompletionProposal(int offset, String prefix, 
			String desc, String text, boolean selectParams,
			CeylonParseController cpc, Declaration d) {
		this(offset, prefix, desc, text, selectParams,
				cpc, d, false, null);
	}
	
	DeclarationCompletionProposal(int offset, String prefix, 
			String desc, String text, boolean selectParams,
			CeylonParseController cpc, Declaration d, 
			boolean addimport, ProducedReference producedReference) {
		super(offset, prefix, CeylonLabelProvider.getImage(d), 
				desc, text, selectParams);
		this.cpc = cpc;
		this.declaration = d;
		this.addimport = addimport;
		this.producedReference = producedReference;
	}

	@Override
	public void apply(IDocument document) {
		
		if (addimport) {
			try {
				List<InsertEdit> ies = importEdit(cpc.getRootNode(), 
					Collections.singleton(declaration), null, null);
				for (InsertEdit ie: ies) {
					ie.apply(document);
					offset+=ie.getText().length();
				}
						
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		super.apply(document);
		
		if (declaration instanceof Functional) {
		    List<ParameterList> pls = ((Functional) declaration).getParameterLists();
            if (!pls.isEmpty() && !pls.get(0).getParameters().isEmpty()) {
                enterLinkedMode(document);
            }
		}
		
	}
	
	@Override
	public Point getSelection(IDocument document) {
		if (declaration instanceof Functional) {
		    List<ParameterList> pls = ((Functional) declaration).getParameterLists();
            if (!pls.isEmpty()) {
            	int loc = offset-prefix.length();
            	int paren = text.indexOf('(');
            	int comma = text.substring(paren).indexOf(',');
            	if (comma<0) comma = text.substring(paren).indexOf(')');
				return new Point(loc+paren+1, comma-1);
            }
		}
		return super.getSelection(document);
	}
	
	public String getAdditionalProposalInfo() {
		return getDocumentationFor(cpc, declaration);	
	}
	
    private IEditingSupport editingSupport;
    
	public void enterLinkedMode(IDocument document) {
		//Big TODO: handle named arguments!
	    try {
	        final LinkedModeModel linkedModeModel = new LinkedModeModel();
	        int loc = offset-prefix.length();
	        int first = text.indexOf('(');
	        if (first<0) return;
	        int next = text.substring(first).indexOf(',');
	        if (next<0) next = text.substring(first).indexOf(')');
	        int i=0;
	        while (next>0) {
		        LinkedPositionGroup linkedPositionGroup = new LinkedPositionGroup();
				LinkedPosition linkedPosition = new ProposalPosition(document, 
		        		loc+first+1, next-1, i++, 
		        		new ICompletionProposal[0]);
		        linkedPositionGroup.addPosition(linkedPosition);
		        first = first+next+1;
		        next = text.substring(first).indexOf(',');
		        if (next<0) next = text.substring(first).indexOf(')');
	            linkedModeModel.addGroup(linkedPositionGroup);
	        }
            linkedModeModel.forceInstall();
            final CeylonEditor editor = (CeylonEditor) Util.getCurrentEditor();
            linkedModeModel.addLinkingListener(new ILinkedModeListener() {
                @Override
                public void left(LinkedModeModel model, int flags) {
                    editor.setInLinkedMode(false);
                    editor.unpauseBackgroundParsing();
                    linkedModeModel.exit(ILinkedModeListener.NONE);
                    ISourceViewer viewer= editor.getCeylonSourceViewer();
                    if (viewer instanceof IEditingSupportRegistry) {
                        IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
                        registry.unregister(editingSupport);
                    }
                    editor.getSite().getPage().activate(editor);
                }
                @Override
                public void suspend(LinkedModeModel model) {
                    editor.setInLinkedMode(false);
                    editor.unpauseBackgroundParsing();
                }
                @Override
                public void resume(LinkedModeModel model, int flags) {
                    editor.setInLinkedMode(true);
                    editor.pauseBackgroundParsing();
                }
            });
            editor.setInLinkedMode(true);
            editor.pauseBackgroundParsing();
            CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
			EditorLinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
            ui.setExitPosition(viewer, loc+first+next+1, 0, i);
            ui.setExitPolicy(new DeleteBlockingExitPolicy(document));
            ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
            ui.setDoContextInfo(true);
            ui.enter();
            
            if (viewer instanceof IEditingSupportRegistry) {
                IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
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
				registry.register(editingSupport);
            }

	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	public IContextInformation getContextInformation() {
		if (declaration instanceof Functional) {
		    List<ParameterList> pls = ((Functional) declaration).getParameterLists();
            if (!pls.isEmpty()) {
            	return new ParameterContextInformation(declaration, 
            			producedReference, pls.get(0), offset-prefix.length());
            }
		}
		return null;
	}
}