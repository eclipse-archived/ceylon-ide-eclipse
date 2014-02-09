package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.NO_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendParameterContextInfo;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.appendPositionalArgs;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getNamedInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getNamedInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getPositionalInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getPositionalInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getParameters;
import static com.redhat.ceylon.eclipse.code.complete.OccurrenceLocation.CLASS_ALIAS;
import static com.redhat.ceylon.eclipse.code.complete.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.eclipse.code.complete.ParameterContextValidator.findCharCount;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importDeclaration;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
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
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedTypedReference;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

class InvocationCompletionProposal extends CompletionProposal {
	
    static void addReferenceProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration dec, Scope scope,
            boolean isMember) {
        result.add(new InvocationCompletionProposal(offset, prefix,
                getDescriptionFor(dwp), getTextFor(dwp), 
                dec, dec.getReference(), scope, cpc, 
                true, false, false, isMember));
    }
    
    static void addInvocationProposals(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, ProducedReference pr, Scope scope,
            OccurrenceLocation ol, String typeArgs, boolean isMember) {
        Declaration dec = pr.getDeclaration();
        if (dec instanceof Functional) {
            Unit unit = cpc.getRootNode().getUnit();
            boolean isAbstractClass = 
                    dec instanceof Class && ((Class) dec).isAbstract();
            Functional fd = (Functional) dec;
            List<ParameterList> pls = fd.getParameterLists();
            if (!pls.isEmpty()) {
                ParameterList parameterList = pls.get(0);
                List<Parameter> ps = parameterList.getParameters();
                if (!isAbstractClass ||
                        ol==EXTENDS || ol==CLASS_ALIAS) {
                    if (ps.size()!=getParameters(parameterList, false, false).size()) {
                        result.add(new InvocationCompletionProposal(offset, prefix, 
                                getPositionalInvocationDescriptionFor(dwp, ol, pr, unit, false, null), 
                                getPositionalInvocationTextFor(dwp, ol, pr, unit, false, null), dec,
                                pr, scope, cpc, false, true, false, isMember));
                    }
                    result.add(new InvocationCompletionProposal(offset, prefix, 
                            getPositionalInvocationDescriptionFor(dwp, ol, pr, unit, true, typeArgs), 
                            getPositionalInvocationTextFor(dwp, ol, pr, unit, true, typeArgs), dec,
                            pr, scope, cpc, true, true, false, isMember));
                }
                if (!isAbstractClass &&
                        ol!=EXTENDS && ol!=CLASS_ALIAS &&
                        !fd.isOverloaded() && typeArgs==null) {
                    //if there is at least one parameter, 
                    //suggest a named argument invocation
                    if (ps.size()!=getParameters(parameterList, false, true).size()) {
                        result.add(new InvocationCompletionProposal(offset, prefix, 
                                getNamedInvocationDescriptionFor(dwp, pr, unit, false), 
                                getNamedInvocationTextFor(dwp, pr, unit, false), dec,
                                pr, scope, cpc, false, false, true, isMember));
                    }
                    if (!ps.isEmpty()) {
                        result.add(new InvocationCompletionProposal(offset, prefix, 
                                getNamedInvocationDescriptionFor(dwp, pr, unit, true), 
                                getNamedInvocationTextFor(dwp, pr, unit, true), dec,
                                pr, scope, cpc, true, false, true, isMember));
                    }
                }
            }
        }
    }
    
	final class NestedCompletionProposal implements ICompletionProposal, 
	        ICompletionProposalExtension2 {
	    private final String op;
	    private final int loc;
	    private final int index;
	    private final boolean basic;
	    private final Declaration d;

	    NestedCompletionProposal(Declaration dec, int loc, int index, boolean basic,
	            String op) {
		    this.op = op;
		    this.loc = loc;
		    this.index = index;
		    this.basic = basic;
		    this.d = dec;
	    }

	    public String getAdditionalProposalInfo() {
	    	return null;
	    }

	    @Override
	    public void apply(IDocument document) {
	    	try {
	    		IRegion li = document.getLineInformationOfOffset(loc);
	    		int endOfLine = li.getOffset() + li.getLength();
	    		int startOfArgs = getFirstPosition();
	    		int offset = findCharCount(index, document, 
	    				loc+startOfArgs, endOfLine, 
	    				",;", "", true)+1;
	    		if (offset>0&&document.getChar(offset)==' ') {
	    		    offset++;
	    		}
	    		int nextOffset = findCharCount(index+1, document, 
	    				loc+startOfArgs, endOfLine, 
	    				",;", "", true);
	    		int middleOffset = findCharCount(1, document, 
	    		        offset, nextOffset, 
	    				"=", "", true)+1;
	    		if (middleOffset>0 &&
	    		        document.getChar(middleOffset)=='>') {
	    		    middleOffset++;
	    		}
	    		while (middleOffset>0 &&
	    		        document.getChar(middleOffset)==' ') {
	    		    middleOffset++;
	    		}
	    		if (middleOffset>offset &&
	    		        middleOffset<nextOffset) {
	    		    offset = middleOffset;
	    		}
	    		String str = getText();
	    		if (nextOffset==-1) {
	    		    nextOffset = offset;
	    		}
	    		if (document.getChar(nextOffset)=='}') {
	    		    str += " ";
	    		}
	    		document.replace(offset, nextOffset-offset, str);
	    	} 
	    	catch (BadLocationException e) {
	    		e.printStackTrace();
	    	}
	    	//adding imports drops us out of linked mode :(
            /*try {
                DocumentChange tc = new DocumentChange("imports", document);
                tc.setEdit(new MultiTextEdit());
                HashSet<Declaration> decs = new HashSet<Declaration>();
                CompilationUnit cu = cpc.getRootNode();
                importDeclaration(decs, d, cu);
                if (d instanceof Functional) {
                    List<ParameterList> pls = ((Functional) d).getParameterLists();
                    if (!pls.isEmpty()) {
                        for (Parameter p: pls.get(0).getParameters()) {
                            MethodOrValue pm = p.getModel();
                            if (pm instanceof Method) {
                                for (ParameterList ppl: ((Method) pm).getParameterLists()) {
                                    for (Parameter pp: ppl.getParameters()) {
                                        importSignatureTypes(pp.getModel(), cu, decs);
                                    }
                                }
                            }
                        }
                    }
                    
                }
                applyImports(tc, decs, cu, document);
                tc.perform(new NullProgressMonitor());
            }
            catch (Exception e) {
                e.printStackTrace();
            }*/
	    }

        private String getText() {
            String str = op+d.getName();
            if (d instanceof Class && !basic) {
                StringBuilder sb = new StringBuilder();
                appendPositionalArgs(d, d.getReference(), 
                        cpc.getRootNode().getUnit(), sb, false);
                str += sb;
            }
            return str;
        }

	    @Override
	    public Point getSelection(IDocument document) {
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
        public boolean validate(IDocument document, int currentOffset,
                DocumentEvent event) {
	        if (event==null) {
	        	return true;
	        }
	        else {
	    		try {
	    			IRegion li = document.getLineInformationOfOffset(loc);
	    			int endOfLine = li.getOffset() + li.getLength();
	    			int startOfArgs = getFirstPosition();
	    			int offset = findCharCount(index, document, 
	    					loc+startOfArgs, endOfLine, 
	    					",;", "", true)+1;
	    			String content = document.get(offset, currentOffset - offset);
	    			if ((op+d.getName()).startsWith(content.trim())) {
	    				return true;
	    			}
	    		} catch (BadLocationException e) {
	    			// ignore concurrently modified document
	    		}
	        	return false;
	        }
        }
    }

	private final CeylonParseController cpc;
	private final Declaration declaration;
	private final ProducedReference producedReference;
	private final Scope scope;
	private final boolean includeDefaulted;
	private final boolean namedInvocation;
    private final boolean positionalInvocation;
    private final boolean qualified;
	
	private InvocationCompletionProposal(int offset, String prefix, 
			String desc, String text, Declaration dec,
			ProducedReference producedReference, Scope scope, 
			CeylonParseController cpc, boolean includeDefaulted,
			boolean positionalInvocation, boolean namedInvocation, 
			boolean qualified) {
		super(offset, prefix, getImageForDeclaration(dec), 
				desc, text, true);
		this.cpc = cpc;
		this.declaration = dec;
		this.producedReference = producedReference;
		this.scope = scope;
		this.includeDefaulted = includeDefaulted;
		this.namedInvocation = namedInvocation;
		this.positionalInvocation = positionalInvocation;
		this.qualified = qualified;
	}

    private DocumentChange imports(IDocument document)
            throws BadLocationException {
        DocumentChange tc = new DocumentChange("imports", document);
        tc.setEdit(new MultiTextEdit());
        HashSet<Declaration> decs = new HashSet<Declaration>();
        CompilationUnit cu = cpc.getRootNode();
        if (!qualified) {
            importDeclaration(decs, declaration, cu);
        }
        if (declaration instanceof Functional) {
            List<ParameterList> pls = ((Functional) declaration).getParameterLists();
            if (!pls.isEmpty()) {
                for (Parameter p: pls.get(0).getParameters()) {
                    MethodOrValue pm = p.getModel();
                    if (pm instanceof Method) {
                        for (ParameterList ppl: ((Method) pm).getParameterLists()) {
                            for (Parameter pp: ppl.getParameters()) {
                                importSignatureTypes(pp.getModel(), cu, decs);
                            }
                        }
                    }
                }
            }
            
        }
        applyImports(tc, decs, cu, document);
        return tc;
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
		
		if (EditorsUI.getPreferenceStore()
				.getBoolean(LINKED_MODE)) {
			if (declaration instanceof Generic) {
                Generic generic = (Generic) declaration;
				ParameterList paramList = null;
				if (declaration instanceof Functional && 
						(positionalInvocation||namedInvocation)) {
					List<ParameterList> pls = 
							((Functional) declaration).getParameterLists();
					if (!pls.isEmpty() && 
					        !pls.get(0).getParameters().isEmpty()) {
						paramList = pls.get(0);
					}
				}
				if (paramList!=null) {
					List<Parameter> params = getParameters(paramList, 
					        includeDefaulted, namedInvocation);
					if (!params.isEmpty()) {
						enterLinkedMode(document, params, null);
						return; //NOTE: early exit!
					}
                }
				List<TypeParameter> typeParams = generic.getTypeParameters();
				if (!typeParams.isEmpty()) {
					enterLinkedMode(document, null, typeParams);
				}
			}
		}
		
	}
	
	@Override
	public Point getSelection(IDocument document) {
		if (declaration instanceof Generic) {
        	int first = getFirstPosition();
        	if (first<=0) {
        	    //no arg list
        		return super.getSelection(document);
        	}
        	int next = getNextPosition(document, first);
            if (next<=0) {
                //an empty arg list
                return super.getSelection(document);
            }
        	int middle = getCompletionPosition(first, next);
			return new Point(offset-prefix.length()+first+middle, 
			        next-middle);
		}
		return super.getSelection(document);
	}

	public int getNextPosition(IDocument document, 
	        int lastOffset) {
		int loc = offset-prefix.length();
		int comma = -1;
		try {
			int start = loc+lastOffset;
			int end = loc+text.length()-1;
			if (text.endsWith(";")) {
			    end--;
			}
			comma = findCharCount(1, document, start, end, 
			        ",;", "", true) - start;
		} 
		catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (comma<0) {
		    int index;
		    if (namedInvocation) {
		        index = text.lastIndexOf('}');
		    }
		    else if (positionalInvocation) {
		        index = text.lastIndexOf(')');
		    }
		    else {
		        index = text.lastIndexOf('>');
		    }
            return index - lastOffset;
		}
		return comma;
	}
	
	public String getAdditionalProposalInfo() {
		return getDocumentationFor(cpc, declaration);	
	}
	
    private IEditingSupport editingSupport;
    
	public void enterLinkedMode(IDocument document, 
	        List<Parameter> params, 
			List<TypeParameter> typeParams) {
        boolean proposeTypeArguments = params==null;
        int paramCount = proposeTypeArguments ? 
                typeParams.size() : params.size();
        if (paramCount==0) return;
	    try {
	        final int loc = offset-prefix.length();
	        int first = getFirstPosition();
	        if (first<=0) return; //no arg list
	        int next = getNextPosition(document, first);
	        if (next<=0) return; //empty arg list
            final LinkedModeModel linkedModeModel = 
                    new LinkedModeModel();
	        int i=0;
	        while (next>0 && i<paramCount) {
	        	List<ICompletionProposal> props = 
	        	        new ArrayList<ICompletionProposal>();
	        	if (proposeTypeArguments) {
	        		addTypeArgumentProposals(typeParams, loc, first, props, i);
	        	}
	        	else {
	        		addValueArgumentProposals(params, loc, first, props, i);
	        	}
		        LinkedPositionGroup linkedPositionGroup = 
		                new LinkedPositionGroup();
		        int middle = getCompletionPosition(first, next);
		        ProposalPosition linkedPosition = 
		                new ProposalPosition(document, 
		                        loc+first+middle, next-middle, i, 
		                        props.toArray(NO_COMPLETIONS));
		        linkedPositionGroup.addPosition(linkedPosition);
		        first = first+next+1;
		        next = getNextPosition(document, first);
	            linkedModeModel.addGroup(linkedPositionGroup);
	            i++;
	        }
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
                    editor.setLinkedMode(model, InvocationCompletionProposal.this);
                }
            });
            editor.setLinkedMode(linkedModeModel, this);
            CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
			EditorLinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
            ui.setExitPosition(viewer, loc+text.length(), 0, i);
            ui.setExitPolicy(new DeleteBlockingExitPolicy(document));
            ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
            ui.setDoContextInfo(true);
            ui.enter();
            
            registerEditingSupport(editor, viewer);

	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}

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
	
	protected int getCompletionPosition(int first, int next) {
		return text.substring(first, first+next-1).lastIndexOf(' ')+1;
	}

	protected int getFirstPosition() {
	    int index;
	    if (namedInvocation) {
	        index = text.indexOf('{');
	    }
	    else if (positionalInvocation) {
	        index = text.indexOf('(');
	    }
	    else {
	        index = text.indexOf('<');
	    }
        return index+1;
	}
	
//	protected boolean isNamedArgs() {
//        int parenPos = text.indexOf('(');
//        int bracePos = text.indexOf('{');
//        return bracePos>0&&(bracePos<parenPos||parenPos<0);
//	}
//
//    private boolean isPosArgs() {
//        int parenPos = text.indexOf('(');
//        int bracePos = text.indexOf('{');
//        return parenPos>0&&(bracePos>parenPos||bracePos<0);
//    }

	private void addValueArgumentProposals(List<Parameter> params, final int loc,
			int first, List<ICompletionProposal> props, final int index) {
		Parameter p = params.get(index);
		if (p.getModel().isDynamicallyTyped()) {
			return;
		}
		ProducedType type = producedReference.getTypedParameter(p)
				.getType();
		if (type==null) return;
		Unit unit = p.getDeclaration().getUnit();
		TypeDeclaration td = type.getDeclaration();
		for (DeclarationWithProximity dwp: getSortedProposedValues()) {
		    if (dwp.isUnimported()) {
		        //don't propose unimported stuff b/c adding
		        //imports drops us out of linked mode and
		        //because it results in a pause
		        continue;
		    }
			Declaration d = dwp.getDeclaration();
			if (d instanceof Value) {
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
					boolean isIterArg = namedInvocation &&
							index==params.size()-1 && 
							unit.isIterableParameterType(type);
					boolean isVarArg = p.isSequenced() && positionalInvocation;
					props.add(new NestedCompletionProposal(d, 
                    loc, index, false, isIterArg || isVarArg ? "*" : ""));
				}
			}
			if (d instanceof Class && 
			        !((Class) d).isAbstract() && !d.isAnnotation()) {
                if (d.getUnit().getPackage().getNameAsString()
                        .equals(Module.LANGUAGE_MODULE_NAME)) {
                    if (d.getName().equals("String") ||
                            d.getName().equals("Integer") ||
                            d.getName().equals("Float") ||
                            d.getName().equals("Character")) {
                        continue;
                    }
                }
                ProducedType ct = ((Class) d).getType();
                if (ct!=null && !ct.isNothing() &&
                    ((td instanceof TypeParameter) && 
                        isInBounds(((TypeParameter)td).getSatisfiedTypes(), ct) || 
                            ct.isSubtypeOf(type))) {
                    boolean isIterArg = namedInvocation &&
                            index==params.size()-1 && 
                            unit.isIterableParameterType(type);
                    boolean isVarArg = p.isSequenced() && positionalInvocation;
                    props.add(new NestedCompletionProposal(d, loc, index, false, 
                            isIterArg || isVarArg ? "*" : ""));
                }
			}
		}
	}

	private void addTypeArgumentProposals(List<TypeParameter> typeParams, 
			final int loc, int first, List<ICompletionProposal> props, 
			final int index) {
		TypeParameter p = typeParams.get(index);
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
						props.add(new NestedCompletionProposal(d, loc, index, 
						        true, ""));
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

	@Override
	public IContextInformation getContextInformation() {
	    if (namedInvocation||positionalInvocation) { //TODO: context info for type arg lists!
	        if (declaration instanceof Functional) {
	            List<ParameterList> pls = ((Functional) declaration).getParameterLists();
	            if (!pls.isEmpty()) {
	                int argListOffset = isParameterInfo() ? 
	                        this.offset : 
	                            offset-prefix.length() + text.indexOf(namedInvocation?'{':'(');
	                return new ParameterContextInformation(declaration, 
	                        producedReference, cpc.getRootNode().getUnit(), 
	                        pls.get(0), argListOffset, includeDefaulted, 
	                        namedInvocation, !isParameterInfo());
	            }
	        }
	    }
		return null;
	}
	
	boolean isParameterInfo() {
	    return false;
	}
	
    static final class ParameterInfo 
            extends InvocationCompletionProposal {
        private ParameterInfo(int offset, Declaration dec, 
                ProducedReference producedReference,
                Scope scope, CeylonParseController cpc, 
                boolean namedInvocation) {
            super(offset, "", "show parameters", "", dec, 
                    producedReference, scope, cpc, true, 
                    true, namedInvocation, false);
        }
        @Override
        boolean isParameterInfo() {
            return true;
        }
        @Override
        public Point getSelection(IDocument document) {
            return null;
        }
        @Override
        public void apply(IDocument document) {}
    }

    static List<IContextInformation> computeParameterContextInformation(final int offset,
            final Tree.CompilationUnit rootNode, final ITextViewer viewer) {
        final List<IContextInformation> infos = 
                new ArrayList<IContextInformation>();
        rootNode.visit(new Visitor() {
            @Override
            public void visit(Tree.InvocationExpression that) {
                Tree.ArgumentList al = that.getPositionalArgumentList();
                if (al==null) {
                    al = that.getNamedArgumentList();
                }
                if (al!=null) {
                    //TODO: should reuse logic for adjusting tokens
                    //      from CeylonContentProposer!!
                    Integer start = al.getStartIndex();
                    Integer stop = al.getStopIndex();
                    if (start!=null && stop!=null && offset>start) { 
                        String string = "";
                        if (offset>stop) {
                            try {
                                string = viewer.getDocument()
                                        .get(stop+1, offset-stop-1);
                            } 
                            catch (BadLocationException e) {}
                        }
                        if (string.trim().isEmpty()) {
                            Tree.MemberOrTypeExpression mte = 
                                    (Tree.MemberOrTypeExpression) that.getPrimary();
                            Declaration declaration = mte.getDeclaration();
                            if (declaration instanceof Functional) {
                                List<ParameterList> pls = 
                                        ((Functional) declaration).getParameterLists();
                                if (!pls.isEmpty()) {
                                    //Note: This line suppresses the little menu 
                                    //      that gives me a choice of context infos.
                                    //      Delete it to get a choice of all surrounding
                                    //      argument lists.
                                    infos.clear();
                                    infos.add(new ParameterContextInformation(declaration, 
                                            mte.getTarget(), rootNode.getUnit(), 
                                            pls.get(0), al.getStartIndex(), 
                                            true, al instanceof Tree.NamedArgumentList, false));
                                }
                            }
                        }
                    }
                }
                super.visit(that);
            }
        });
        return infos;
    }
    
    static void addFakeShowParametersCompletion(final Node node, 
            final CeylonParseController cpc, 
            final List<ICompletionProposal> result) {
        new Visitor() {
            @Override
            public void visit(Tree.InvocationExpression that) {
                Tree.ArgumentList al = that.getPositionalArgumentList();
                if (al==null) {
                    al = that.getNamedArgumentList();
                }
                if (al!=null) {
                    Integer startIndex = al.getStartIndex();
                    Integer startIndex2 = node.getStartIndex();
                    if (startIndex!=null && startIndex2!=null &&
                            startIndex.intValue()==startIndex2.intValue()) {
                        Tree.Primary primary = that.getPrimary();
                        if (primary instanceof Tree.MemberOrTypeExpression) {
                            Tree.MemberOrTypeExpression mte = 
                                    (Tree.MemberOrTypeExpression) primary;
                            if (mte.getDeclaration()!=null && mte.getTarget()!=null) {
                                result.add(new ParameterInfo(al.getStartIndex(),
                                        mte.getDeclaration(), mte.getTarget(), 
                                        node.getScope(), cpc, 
                                        al instanceof Tree.NamedArgumentList));
                            }
                        }
                    }
                }
                super.visit(that);
            }
        }.visit(cpc.getRootNode());
    }
    
    static final class ParameterContextInformation 
            implements IContextInformation {
        
        private final Declaration declaration;
        private final ProducedReference producedReference;
        private final ParameterList parameterList;
        private final int argumentListOffset;
        private final Unit unit;
        private final boolean includeDefaulted;
        private final boolean inLinkedMode;
        private final boolean namedInvocation;
        
        private ParameterContextInformation(Declaration declaration,
                ProducedReference producedReference, Unit unit,
                ParameterList parameterList, int argumentListOffset, 
                boolean includeDefaulted, boolean namedInvocation, 
                boolean inLinkedMode) {
            this.declaration = declaration;
            this.producedReference = producedReference;
            this.unit = unit;
            this.parameterList = parameterList;
            this.argumentListOffset = argumentListOffset;
            this.includeDefaulted = includeDefaulted;
            this.inLinkedMode = inLinkedMode;
            this.namedInvocation = namedInvocation;
        }
        
        @Override
        public String getContextDisplayString() {
            return "Parameters of '" + declaration.getName() + "'";
        }
        
        @Override
        public Image getImage() {
            return getImageForDeclaration(declaration);
        }
        
        @Override
        public String getInformationDisplayString() {
            List<Parameter> ps = getParameters(parameterList, 
                    includeDefaulted, namedInvocation);
            if (ps.isEmpty()) {
                return "no parameters";
            }
            StringBuilder result = new StringBuilder();
            for (Parameter p: ps) {
                boolean isListedValues = namedInvocation && 
                        p==ps.get(ps.size()-1) &&
                        p.getModel() instanceof Value && 
                        p.getType()!=null &&
                        unit.isIterableParameterType(p.getType());
                if (includeDefaulted || !p.isDefaulted() ||
                        isListedValues) {
                    if (producedReference==null) {
                        result.append(p.getName());
                    }
                    else {
                        ProducedTypedReference pr = 
                                producedReference.getTypedParameter(p);
                        appendParameterContextInfo(result, pr, p, unit, 
                                namedInvocation, isListedValues);
                    }
                    if (!isListedValues) {
                        result.append(namedInvocation ? "; " : ", ");
                    }
                }
            }
            if (!namedInvocation && result.length()>0) {
                result.setLength(result.length()-2);
            }
            return result.toString();
        }
        
        @Override
        public boolean equals(Object that) {
            if (that instanceof ParameterContextInformation) {
                return ((ParameterContextInformation) that).declaration
                        .equals(declaration);
            }
            else {
                return false;
            }
        }
        
        int getArgumentListOffset() {
            return argumentListOffset;
        }
        
    }
    
}