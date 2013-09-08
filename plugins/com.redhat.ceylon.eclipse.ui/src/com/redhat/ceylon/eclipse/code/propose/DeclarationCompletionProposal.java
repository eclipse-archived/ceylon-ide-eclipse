package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.hover.CeylonHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.propose.CompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.propose.ParameterContextValidator.findCharCount;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importEdit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

class DeclarationCompletionProposal extends CompletionProposal {
	
	private final CeylonParseController cpc;
	private final Declaration declaration;
	private final boolean addimport;
	private final ProducedReference producedReference;
	private Scope scope;

	DeclarationCompletionProposal(int offset, String prefix, 
			String desc, String text, boolean selectParams,
			CeylonParseController cpc, Declaration d) {
		this(offset, prefix, desc, text, selectParams,
				cpc, d, false, null, null);
	}
	
	DeclarationCompletionProposal(int offset, String prefix, 
			String desc, String text, boolean selectParams,
			CeylonParseController cpc, Declaration d, 
			boolean addimport, ProducedReference producedReference,
			Scope scope) {
		super(offset, prefix, CeylonLabelProvider.getImage(d), 
				desc, text, selectParams);
		this.cpc = cpc;
		this.declaration = d;
		this.addimport = addimport;
		this.producedReference = producedReference;
		this.scope = scope;
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
                enterLinkedMode(document, pls.get(0));
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
    
	public void enterLinkedMode(IDocument document, ParameterList parameterList) {
		//Big TODO: handle named arguments!
	    try {
	        final LinkedModeModel linkedModeModel = new LinkedModeModel();
	        final int loc = offset-prefix.length();
	        int first = text.indexOf('(');
	        if (first<0) return;
	        int next = text.substring(first).indexOf(',');
	        if (next<0) next = text.substring(first).indexOf(')');
	        int i=0;
	        while (next>0) {
	        	List<ICompletionProposal> props = new ArrayList<ICompletionProposal>();
	        	addProposals(parameterList, loc, first, props, i);
		        LinkedPositionGroup linkedPositionGroup = new LinkedPositionGroup();
		        ProposalPosition linkedPosition = new ProposalPosition(document, 
		        		loc+first+1, next-1, i, 
		        		props.toArray(NO_COMPLETIONS));
		        linkedPositionGroup.addPosition(linkedPosition);
		        first = first+next+1;
		        next = text.substring(first).indexOf(',');
		        if (next<0) next = text.substring(first).indexOf(')');
	            linkedModeModel.addGroup(linkedPositionGroup);	          
	            i++;
	        }
            linkedModeModel.forceInstall();
            final CeylonEditor editor = (CeylonEditor) Util.getCurrentEditor();
            linkedModeModel.addLinkingListener(new ILinkedModeListener() {
                @Override
                public void left(LinkedModeModel model, int flags) {
                    editor.setInLinkedMode(false);
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
                }
                @Override
                public void resume(LinkedModeModel model, int flags) {
                    editor.setInLinkedMode(true);
                }
            });
            editor.setInLinkedMode(true);
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

	private void addProposals(ParameterList parameterList, final int loc,
			int first, List<ICompletionProposal> props, final int index) {
		Parameter p = parameterList.getParameters().get(index);
		ProducedType type = producedReference.getTypedParameter(p)
				.getFullType();
		for (DeclarationWithProximity dwp: getSortedProposedValues()) {
			Declaration d = dwp.getDeclaration();
			if (d instanceof Value && !dwp.isUnimported()) {
				ProducedType vt = ((Value) d).getType();
				if (vt.isSubtypeOf(type) && !vt.isNothing()) {
					addProposal(loc, first, props, index, p, d);
				}
			}
		}
	}

	public List<DeclarationWithProximity> getSortedProposedValues() {
		List<DeclarationWithProximity> results = new ArrayList<DeclarationWithProximity>(
				scope.getMatchingDeclarations(cpc.getRootNode().getUnit(), "", 0).values());
		Collections.sort(results, new Comparator<DeclarationWithProximity>() {
			public int compare(DeclarationWithProximity x, DeclarationWithProximity y) {
				if (x.getProximity()<y.getProximity()) return -1;
				if (x.getProximity()>y.getProximity()) return 1;
				int c = x.getDeclaration().getName().compareTo(y.getDeclaration().getName());
				if (c!=0) return c;  
				return x.getDeclaration().getQualifiedNameString()
						.compareTo(y.getDeclaration().getQualifiedNameString());
			}
		});
		return results;
	}

	private void addProposal(final int loc, int first,
			List<ICompletionProposal> props, final int index, 
			Parameter p, final Declaration d) {
		props.add(new ICompletionProposal() {
			public String getAdditionalProposalInfo() {
				return null;
			}
			@Override
			public void apply(IDocument document) {
				try {
					IRegion li = document.getLineInformationOfOffset(loc);
					int len = li.getOffset() + li.getLength() - loc;
					String rest = document.get(loc, len);
					int paren = rest.indexOf('(');
					int offset = findCharCount(index, document, 
								loc+paren+1, loc+len, 
								",", "", true);
					while (document.getChar(offset)==' ') offset++;
					int nextOffset = findCharCount(index+1, document, 
							loc+paren+1, loc+len, 
							",", "", true);
					document.replace(offset, nextOffset-offset-1, d.getName());
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
			public String getDisplayString() {
				return d.getName();
			}
			@Override
			public Image getImage() {
				return CeylonLabelProvider.getImage(d);
			}
			@Override
			public IContextInformation getContextInformation() {
				return null;
			}
		});
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