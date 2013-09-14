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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration;
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
		
		if (EditorsPlugin.getDefault().getPreferenceStore()
				.getBoolean(CeylonSourceViewerConfiguration.LINKED_MODE)) {
			if (declaration instanceof Generic) {
				ParameterList paramList = null;
				if (declaration instanceof Functional && getFirstPosition(false)>0) {
					List<ParameterList> pls = ((Functional) declaration).getParameterLists();
					if (!pls.isEmpty() && !pls.get(0).getParameters().isEmpty()) {
						paramList = pls.get(0);
					}
				}
				enterLinkedMode(document, paramList, (Generic) declaration);
			}
		}
		
	}
	
	@Override
	public Point getSelection(IDocument document) {
		if (declaration instanceof Generic) {
			ParameterList pl = null;
			if (declaration instanceof Functional) {
				List<ParameterList> pls = ((Functional) declaration).getParameterLists();
				if (!pls.isEmpty() && !pls.get(0).getParameters().isEmpty()) {
					pl = pls.get(0);
				}
			}
        	int first = getFirstPosition(pl==null);
        	if (first<=0) {
        		return super.getSelection(document);
        	}
        	int next = getNextPosition(document, first, pl==null);
        	int middle = getCompletionPosition(first, next);
			return new Point(offset-prefix.length()+first+middle, next-middle);
		}
		return super.getSelection(document);
	}

	public int getNextPosition(IDocument document, int lastOffset, 
			boolean typeArgList) {
		int loc = offset-prefix.length();
		int comma = -1;
		try {
			int start = loc+lastOffset;
			int end = loc+text.length()-1;
			if (text.endsWith(";")) end--;
			comma = findCharCount(1, document, start, end, ",;", "", true) - start;
		} 
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (comma<0) {
			int angleIndex = text.lastIndexOf('>');
			int parenIndex = text.lastIndexOf(')');
			int braceIndex = text.lastIndexOf('}');
			comma = (typeArgList ? angleIndex : (braceIndex>parenIndex?braceIndex:parenIndex))-lastOffset;
		}
		return comma;
	}
	
	public String getAdditionalProposalInfo() {
		return getDocumentationFor(cpc, declaration);	
	}
	
    private IEditingSupport editingSupport;
    
	public void enterLinkedMode(IDocument document, ParameterList parameterList, 
			Generic generic) {
        boolean basicProposal = parameterList==null;
        int paramCount = basicProposal ? 
        		generic.getTypeParameters().size() :
        		parameterList.getParameters().size();
        if (paramCount==0) return;
	    try {
	        final LinkedModeModel linkedModeModel = new LinkedModeModel();
	        final int loc = offset-prefix.length();
	        int first = getFirstPosition(basicProposal);
	        if (first<=0) return;
	        int next = getNextPosition(document, first, basicProposal);
	        int i=0;
	        while (next>1 && i<paramCount) {
	        	List<ICompletionProposal> props = new ArrayList<ICompletionProposal>();
	        	if (basicProposal) {
	        		addBasicProposals(generic, loc, first, props, i);
	        	}
	        	else {
	        		addProposals(parameterList, loc, first, props, i);
	        	}
		        LinkedPositionGroup linkedPositionGroup = new LinkedPositionGroup();
		        int middle = getCompletionPosition(first, next);
		        ProposalPosition linkedPosition = new ProposalPosition(document, 
		        		loc+first+middle, next-middle, i, 
		        		props.toArray(NO_COMPLETIONS));
		        linkedPositionGroup.addPosition(linkedPosition);
		        first = first+next+1;
		        next = getNextPosition(document, first, basicProposal);
	            linkedModeModel.addGroup(linkedPositionGroup);
	            i++;
	        }
            linkedModeModel.forceInstall();
            final CeylonEditor editor = (CeylonEditor) Util.getCurrentEditor();
            linkedModeModel.addLinkingListener(new ILinkedModeListener() {
                @Override
                public void left(LinkedModeModel model, int flags) {
                    editor.setLinkedMode(null);
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
                    editor.setLinkedMode(null);
                }
                @Override
                public void resume(LinkedModeModel model, int flags) {
                    editor.setLinkedMode(model);
                }
            });
            editor.setLinkedMode(linkedModeModel);
            CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
			EditorLinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
            ui.setExitPosition(viewer, loc+text.length(), 0, i);
            ui.setExitPolicy(new DeleteBlockingExitPolicy(document));
            ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
            ui.setDoContextInfo(true);
            ui.enter();
            
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
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	protected int getCompletionPosition(int first, int next) {
		return text.substring(first, first+next-1).lastIndexOf(' ')+1;
	}

	protected int getFirstPosition(boolean basicProposal) {
		int anglePos = text.indexOf('<');
		int parenPos = text.indexOf('(');
		int bracePos = text.indexOf('{');
		return (basicProposal ? anglePos : (bracePos>0&&(bracePos<parenPos||parenPos<0) ? bracePos : parenPos))+1;
	}
	
	private boolean isNamedArgs() {
        int parenPos = text.indexOf('(');
        int bracePos = text.indexOf('{');
        return bracePos>0&&(bracePos<parenPos||parenPos<0);
	}

    private boolean isPosArgs() {
        int parenPos = text.indexOf('(');
        int bracePos = text.indexOf('{');
        return parenPos>0&&(bracePos>parenPos||bracePos<0);
    }

	private void addProposals(ParameterList parameterList, final int loc,
			int first, List<ICompletionProposal> props, final int index) {
		Parameter p = parameterList.getParameters().get(index);
		if (p.getModel().isDynamicallyTyped()) {
			return;
		}
		ProducedType type = producedReference.getTypedParameter(p)
				.getType();
		if (type==null) return;
		TypeDeclaration td = type.getDeclaration();
		for (DeclarationWithProximity dwp: getSortedProposedValues()) {
			Declaration d = dwp.getDeclaration();
			if (d instanceof Value && !dwp.isUnimported()) {
				if (d.getUnit().getPackage().getNameAsString()
						.equals(Module.LANGUAGE_MODULE_NAME)) {
					if (d.getName().equals("process") ||
							d.getName().equals("language") ||
							d.getName().equals("emptyIterator") ||
							d.getName().equals("infinity") ||
							d.getName().endsWith("IntegerValue") ||
							d.getName().equals("finished")) {
						continue;
					}
				}
				ProducedType vt = ((Value) d).getType();
				if (vt!=null && !vt.isNothing() &&
				    ((td instanceof TypeParameter) && 
						isInBounds(((TypeParameter)td).getSatisfiedTypes(), vt) || 
						    vt.isSubtypeOf(type))) {
					addProposal(loc, first, props, index, d, false, p.isSequenced()&&isPosArgs());
				}
			}
		}
	}

	private void addBasicProposals(Generic generic, final int loc,
			int first, List<ICompletionProposal> props, final int index) {
		TypeParameter p = generic.getTypeParameters().get(index);
		for (DeclarationWithProximity dwp: getSortedProposedValues()) {
			Declaration d = dwp.getDeclaration();
			if (d instanceof TypeDeclaration && !dwp.isUnimported()) {
				TypeDeclaration td = (TypeDeclaration) d;
				ProducedType t = td.getType();
				if (td.getTypeParameters().isEmpty() && 
						!td.isAnnotation() &&
						!(td instanceof NothingType) &&
						!td.inherits(td.getUnit().getExceptionDeclaration())) {
					if (td.getUnit().getPackage().getNameAsString()
							.equals(Module.LANGUAGE_MODULE_NAME)) {
						if (!td.getName().equals("Object") && 
								!td.getName().equals("Anything") &&
								!td.getName().equals("String") &&
								!td.getName().equals("Integer") &&
								!td.getName().equals("Character") &&
								!td.getName().equals("Float") &&
								!td.getName().equals("Boolean")) {
							continue;
						}
					}
					if (isInBounds(p.getSatisfiedTypes(), t)) {
						addProposal(loc, first, props, index, d, true, false);
					}
				}
			}
		}
	}

	public boolean isInBounds(List<ProducedType> upperBounds, ProducedType t) {
		boolean ok = true;
		for (ProducedType ub: upperBounds) {
			if (!t.isSubtypeOf(ub) &&
					!(ub.containsTypeParameters() &&
					    t.getDeclaration().inherits(ub.getDeclaration()))) {
				ok = false;
				break;
			}
		}
		return ok;
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
			final Declaration d, final boolean basic, 
			final boolean spread) {
	    final String op = spread?"*":"";
		props.add(new ICompletionProposal() {
			public String getAdditionalProposalInfo() {
				return null;
			}
			@Override
			public void apply(IDocument document) {
				try {
					IRegion li = document.getLineInformationOfOffset(loc);
					int endOfLine = li.getOffset() + li.getLength();
					int startOfArgs = getFirstPosition(basic);
					int offset = findCharCount(index, document, 
								loc+startOfArgs, endOfLine, 
								",;", "", true)+1;
					while (offset>0&&document.getChar(offset)==' ') offset++;
					int nextOffset = findCharCount(index+1, document, 
							loc+startOfArgs, endOfLine, 
							",;", "", true);
					int middleOffset = findCharCount(1, document, 
					        offset, nextOffset, 
							"=", "", true)+1;
					if (middleOffset>0&&document.getChar(middleOffset)=='>') middleOffset++;
					while (middleOffset>0&&document.getChar(middleOffset)==' ') middleOffset++;
					if (middleOffset>offset&&middleOffset<nextOffset) offset = middleOffset;
					document.replace(offset, nextOffset-offset, op+d.getName());
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
				return op+d.getName();
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
            if (!pls.isEmpty() && 
            		//TODO: for now there is no context info for type args lists - fix that!
            		!(pls.get(0).getParameters().isEmpty()&&!((Generic)declaration).getTypeParameters().isEmpty())) {
            	int paren = text.indexOf('(');
            	if (paren<0 && !getDisplayString().equals("show parameters")) { //ugh, horrible, todo!
            		return super.getContextInformation();
            	}
            	return new ParameterContextInformation(declaration, 
            			producedReference, pls.get(0), offset-prefix.length());
            }
		}
		return null;
	}
}