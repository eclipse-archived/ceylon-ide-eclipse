package com.redhat.ceylon.eclipse.code.hover;

/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Genady Beryozkin <eclipse@genady.org> - [hovering] tooltip for constant string does not show constant value - https://bugs.eclipse.org/bugs/show_bug.cgi?id=85382
 *******************************************************************************/

import static com.redhat.ceylon.eclipse.code.browser.BrowserInformationControl.isAvailable;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.addPageEpilog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.convertToHTMLContent;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.insertPageProlog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.toHex;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getLabel;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getModuleLabel;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.CHARS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.COMMENTS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.IDENTIFIERS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.KEYWORDS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.NUMBERS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.PACKAGES;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.STRINGS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.TYPES;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.getCurrentThemeColor;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.appendParameters;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.getJavaElement;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModelLoader;
import static java.lang.Character.codePointCount;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static org.eclipse.jdt.internal.ui.JavaPluginImages.setLocalImageDescriptors;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK_DISABLED;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD_DISABLED;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import com.github.rjeschke.txtmark.BlockEmitter;
import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Configuration.Builder;
import com.github.rjeschke.txtmark.Processor;
import com.github.rjeschke.txtmark.SpanEmitter;
import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.UnknownType;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnonymousAnnotation;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.browser.BrowserInformationControl;
import com.redhat.ceylon.eclipse.code.browser.BrowserInput;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.html.HTMLPrinter;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;
import com.redhat.ceylon.eclipse.code.quickfix.ExtractFunctionProposal;
import com.redhat.ceylon.eclipse.code.quickfix.ExtractValueProposal;
import com.redhat.ceylon.eclipse.code.quickfix.SpecifyTypeProposal;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring;
import com.redhat.ceylon.eclipse.code.search.FindAssignmentsAction;
import com.redhat.ceylon.eclipse.code.search.FindReferencesAction;
import com.redhat.ceylon.eclipse.code.search.FindRefinementsAction;
import com.redhat.ceylon.eclipse.code.search.FindSubtypesAction;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;


/**
 * Provides Javadoc as hover info for Java elements.
 *
 * @since 2.1
 */
public class DocumentationHover 
        implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {
	
	private CeylonEditor editor;
	
	public DocumentationHover(CeylonEditor editor) {
		this.editor = editor;
	}

	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		IDocument document = textViewer.getDocument();
        int start= -2;
        int end= -1;
        
        try {
        	int pos= offset;
        	char c;
        
        	while (pos >= 0) {
        		c= document.getChar(pos);
        		if (!Character.isJavaIdentifierPart(c)) {
        			break;
        		}
        		--pos;
        	}
        	start= pos;
        
        	pos= offset;
        	int length= document.getLength();
        
        	while (pos < length) {
        		c= document.getChar(pos);
        		if (!Character.isJavaIdentifierPart(c)) {
        			break;
        
        		}
        		++pos;
        	}
        	end= pos;
        
        } catch (BadLocationException x) {
        }
        
        if (start >= -1 && end > -1) {
        	if (start == offset && end == offset)
        		return new Region(offset, 0);
        	else if (start == offset)
        		return new Region(start, end - start);
        	else
        		return new Region(start + 1, end - start - 1);
        }
        
        return null;
	}
	
	final class CeylonLocationListener implements LocationListener {
        private final BrowserInformationControl control;
        
        CeylonLocationListener(BrowserInformationControl control) {
            this.control = control;
        }
        
        @Override
        public void changing(LocationEvent event) {
        	String location = event.location;
        	
        	//necessary for windows environment (fix for blank page)
        	//somehow related to this: https://bugs.eclipse.org/bugs/show_bug.cgi?id=129236
        	if (!"about:blank".equals(location)) {
        		event.doit= false;
        	}
        	
        	handleLink(location);
        	
        	/*else if (location.startsWith("javadoc:")) {
        		final DocBrowserInformationControlInput input = (DocBrowserInformationControlInput) control.getInput();
        		int beginIndex = input.getHtml().indexOf("javadoc:")+8;
        		final String handle = input.getHtml().substring(beginIndex, input.getHtml().indexOf("\"",beginIndex));
        		new Job("Fetching Javadoc") {
        			@Override
        			protected IStatus run(IProgressMonitor monitor) {
        				final IJavaElement elem = JavaCore.create(handle);
        				try {
        					final String javadoc = JavadocContentAccess2.getHTMLContent((IMember) elem, true);
        					if (javadoc!=null) {
        						PlatformUI.getWorkbench().getProgressService()
        						        .runInUI(editor.getSite().getWorkbenchWindow(), new IRunnableWithProgress() {
        							@Override
        							public void run(IProgressMonitor monitor) 
        									throws InvocationTargetException, InterruptedException {
        								StringBuilder sb = new StringBuilder();
        								HTMLPrinter.insertPageProlog(sb, 0, getStyleSheet());
        								appendJavadoc(elem, javadoc, sb);
        								HTMLPrinter.addPageEpilog(sb);
        								control.setInput(new DocBrowserInformationControlInput(input, null, sb.toString(), 0));
        							}
        						}, null);
        					}
        				} 
        				catch (Exception e) {
        					e.printStackTrace();
        				}
        				return Status.OK_STATUS;
        			}
        		}.schedule();
        	}*/
        }
        
        private void handleLink(String location) {
            if (location.startsWith("dec:")) {
        		Referenceable target = getLinkedModel(editor, location);
        		if (target!=null) {
                    close(control); //FIXME: should have protocol to hide, rather than dispose
        			gotoDeclaration(editor, target);
        		}
        	}
        	else if (location.startsWith("doc:")) {
        	    Referenceable target = getLinkedModel(editor, location);
        		if (target!=null) {
        			control.setInput(getHoverInfo(target, control.getInput(), editor, null));
        		}
        	}
        	else if (location.startsWith("ref:")) {
        	    Referenceable target = getLinkedModel(editor, location);
        		close(control);
        		new FindReferencesAction(editor, (Declaration) target).run();
        	}
        	else if (location.startsWith("sub:")) {
        	    Referenceable target = getLinkedModel(editor, location);
        		close(control);
        		new FindSubtypesAction(editor, (Declaration) target).run();
        	}
        	else if (location.startsWith("act:")) {
        	    Referenceable target = getLinkedModel(editor, location);
        		close(control);
        		new FindRefinementsAction(editor, (Declaration) target).run();
        	}
        	else if (location.startsWith("ass:")) {
        	    Referenceable target = getLinkedModel(editor, location);
        		close(control);
        		new FindAssignmentsAction(editor, (Declaration) target).run();
        	}
        	else if (location.startsWith("stp:")) {
        		close(control);
        		CompilationUnit rn = editor.getParseController().getRootNode();
        		Node node = findNode(rn, Integer.parseInt(location.substring(4)));
        		SpecifyTypeProposal.create(rn, node, Util.getFile(editor.getEditorInput()))
        		        .apply(editor.getParseController().getDocument());
        	}
        	else if (location.startsWith("exv:")) {
        		close(control);
        		new ExtractValueProposal(editor).apply(editor.getParseController().getDocument());
        	}
        	else if (location.startsWith("exf:")) {
        		close(control);
        		new ExtractFunctionProposal(editor).apply(editor.getParseController().getDocument());
        	}
        }
        
        @Override
        public void changed(LocationEvent event) {}
    }
	
    /**
	 * Action to go back to the previous input in the hover control.
	 */
	static final class BackAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public BackAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			setText("Back");
			ISharedImages images= getWorkbench().getSharedImages();
			setImageDescriptor(images.getImageDescriptor(IMG_TOOL_BACK));
			setDisabledImageDescriptor(images.getImageDescriptor(IMG_TOOL_BACK_DISABLED));

			update();
		}

		@Override
		public void run() {
			BrowserInput previous= (BrowserInput) fInfoControl.getInput().getPrevious();
			if (previous != null) {
				fInfoControl.setInput(previous);
			}
		}

        public void update() {
			BrowserInput current= fInfoControl.getInput();
			if (current != null && current.getPrevious() != null) {
				BrowserInput previous= current.getPrevious();
				setToolTipText("Back to " + previous.getInputName());
				setEnabled(true);
			} else {
				setToolTipText("Back");
				setEnabled(false);
			}
		}
	}

	/**
	 * Action to go forward to the next input in the hover control.
	 */
	static final class ForwardAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public ForwardAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			setText("Forward");
			ISharedImages images= getWorkbench().getSharedImages();
			setImageDescriptor(images.getImageDescriptor(IMG_TOOL_FORWARD));
			setDisabledImageDescriptor(images.getImageDescriptor(IMG_TOOL_FORWARD_DISABLED));

			update();
		}

		@Override
		public void run() {
			BrowserInput next= (BrowserInput) fInfoControl.getInput().getNext();
			if (next != null) {
				fInfoControl.setInput(next);
			}
		}

		public void update() {
			BrowserInput current= fInfoControl.getInput();
			if (current != null && current.getNext() != null) {
				setToolTipText("Forward to " + current.getNext().getInputName());
				setEnabled(true);
			} else {
				setToolTipText("Forward");
				setEnabled(false);
			}
		}
	}
	
	/**
	 * Action that shows the current hover contents in the Javadoc view.
	 */
	/*private static final class ShowInDocViewAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public ShowInJavadocViewAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			setText("Show in Ceylondoc View");
			setImageDescriptor(JavaPluginImages.DESC_OBJS_JAVADOCTAG); //TODO: better image
		}

		@Override
		public void run() {
			DocBrowserInformationControlInput infoInput= (DocBrowserInformationControlInput) fInfoControl.getInput(); //TODO: check cast
			fInfoControl.notifyDelayedInputChange(null);
			fInfoControl.dispose(); //FIXME: should have protocol to hide, rather than dispose
			try {
				JavadocView view= (JavadocView) JavaPlugin.getActivePage().showView(JavaUI.ID_JAVADOC_VIEW);
				view.setInput(infoInput);
			} catch (PartInitException e) {
				JavaPlugin.log(e);
			}
		}
	}*/
	
	/**
	 * Action that opens the current hover input element.
	 */
	final class OpenDeclarationAction extends Action {
		private final BrowserInformationControl fInfoControl;
		public OpenDeclarationAction(BrowserInformationControl infoControl) {
			fInfoControl = infoControl;
			setText("Open Declaration");
			setLocalImageDescriptors(this, "goto_input.gif");
		}
		@Override
		public void run() {
	        close(fInfoControl); //FIXME: should have protocol to hide, rather than dispose
	        CeylonBrowserInput input = (CeylonBrowserInput) fInfoControl.getInput();
			gotoDeclaration(editor, getLinkedModel(editor, input.getAddress()));
		}
	}
	
	static void gotoDeclaration(CeylonEditor editor, Referenceable model) {
		CeylonParseController cpc = editor.getParseController();
		Node refNode = getReferencedNode(model, cpc);
		if (refNode!=null) {
			gotoNode(refNode, cpc.getProject(), cpc.getTypeChecker());
		}
		else if (model instanceof Declaration) {
	        gotoJavaNode((Declaration) model, cpc);
		}
	}
	
	private static void close(BrowserInformationControl control) {
		control.notifyDelayedInputChange(null);
		control.dispose();
	}
	
	/**
	 * The style sheet (css).
	 */
	private static String fgStyleSheet;

	/**
	 * The hover control creator.
	 */
	private IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 */
	private IInformationControlCreator fPresenterControlCreator;

	private  IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null)
			fPresenterControlCreator= new PresenterControlCreator(this);
		return fPresenterControlCreator;
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return getHoverControlCreator("F2 for focus");
	}

	public IInformationControlCreator getHoverControlCreator(
			String statusLineMessage) {
		if (fHoverControlCreator == null) {
			fHoverControlCreator= new HoverControlCreator(this, 
					getInformationPresenterControlCreator(), 
					statusLineMessage);
		}
		return fHoverControlCreator;
	}

	void addLinkListener(final BrowserInformationControl control) {
		control.addLocationListener(new CeylonLocationListener(control));
	}

	public static Referenceable getLinkedModel(CeylonEditor editor, String location) {
        TypeChecker tc = editor.getParseController().getTypeChecker();
        String[] bits = location.split(":");
		JDTModelLoader modelLoader = getModelLoader(tc);
        String moduleName = bits[1];
	    Module module = modelLoader.getLoadedModule(moduleName);
	    if (module==null || bits.length==2) {
	        return module;
	    }
	    Referenceable target = module.getPackage(bits[2]);
	    for (int i=3; i<bits.length; i++) {
	        Scope scope;
	        if (target instanceof Scope) {
	            scope = (Scope) target;
	        }
	        else if (target instanceof TypedDeclaration) {
	            scope = ((TypedDeclaration) target).getType().getDeclaration();
	        }
	        else {
	            return null;
	        }
	        target = scope.getDirectMember(bits[i], null, false);
	    }
	    return target;
	}
	
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		CeylonBrowserInput info = (CeylonBrowserInput) getHoverInfo2(textViewer, hoverRegion);
		return info!=null ? info.getHtml() : null;
	}

	@Override
	public CeylonBrowserInput getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		return internalGetHoverInfo(editor, hoverRegion);
	}

	static CeylonBrowserInput internalGetHoverInfo(final CeylonEditor editor, 
	        IRegion hoverRegion) {
		if (editor==null || editor.getSelectionProvider()==null) return null;
	    CeylonParseController parseController = editor.getParseController();
	    if (parseController==null) return null;
        Tree.CompilationUnit rn = parseController.getRootNode();
		if (rn!=null) {
			int hoffset = hoverRegion.getOffset();
		    ITextSelection selection = getSelection(editor);
			if (selection!=null && 
				selection.getOffset()<=hoffset &&
				selection.getOffset()+selection.getLength()>=hoffset) {
				Node node = findNode(rn, selection.getOffset(),
						selection.getOffset()+selection.getLength()-1);
				
				if (node instanceof Tree.Expression) {
					node = ((Tree.Expression) node).getTerm();
				}
				if (node instanceof Tree.Term) {
					return getTermTypeHoverInfo(node, selection.getText(), 
					        editor.getCeylonSourceViewer().getDocument(),
		                    editor.getParseController().getProject());
				}
			}
			Node node = findNode(rn, hoffset);
			if (node instanceof Tree.ImportPath) {
				Referenceable r = ((Tree.ImportPath) node).getModel();
				if (r!=null) {
					return getHoverInfo(r, null, editor, node);
				}
			}
			else if (node instanceof Tree.LocalModifier) {
				return getInferredTypeHoverInfo(node,
	                    editor.getParseController().getProject());
			}
			else if (node instanceof Tree.Literal) {
				return getTermTypeHoverInfo(node, null, 
				        editor.getCeylonSourceViewer().getDocument(),
	                    editor.getParseController().getProject());
			}
			else {
				return getHoverInfo(getReferencedDeclaration(node), 
				        null, editor, node);
			}
		}
		return null;
	}

    private static ITextSelection getSelection(final CeylonEditor editor) {
        final class GetSelection implements Runnable {
            ITextSelection selection;
            @Override
            public void run() {
                selection = (ITextSelection) editor.getSelectionProvider().getSelection();
            }
            ITextSelection getSelection() {
                Display.getDefault().syncExec(this);
                return selection;
            }
        }
        return new GetSelection().getSelection();
    }

	private static CeylonBrowserInput getInferredTypeHoverInfo(Node node, IProject project) {
		ProducedType t = ((Tree.LocalModifier) node).getTypeModel();
		if (t==null) return null;
		StringBuilder buffer = new StringBuilder();
		HTMLPrinter.insertPageProlog(buffer, 0, DocumentationHover.getStyleSheet());
		addImageAndLabel(buffer, null, fileUrl("types.gif").toExternalForm(), 
				16, 16, "<b><tt>" + highlightLine(t.getProducedTypeName()) + "</tt></b>", 
				20, 4);
		buffer.append("<hr/>");
		if (!t.containsUnknowns()) {
			buffer.append("One quick assist available:<br/>");
			addImageAndLabel(buffer, null, fileUrl("correction_change.gif").toExternalForm(), 
					16, 16, "<a href=\"stp:" + node.getStartIndex() + "\">Specify explicit type</a>", 
					20, 4);
		}
		//buffer.append(getDocumentationFor(editor.getParseController(), t.getDeclaration()));
		HTMLPrinter.addPageEpilog(buffer);
		return new CeylonBrowserInput(null, null, buffer.toString());
	}
	
	private static CeylonBrowserInput getTermTypeHoverInfo(Node node, String selectedText, 
	        IDocument doc, IProject project) {
		ProducedType t = ((Tree.Term) node).getTypeModel();
		if (t==null) return null;
//		String expr = "";
//		try {
//			expr = doc.get(node.getStartIndex(), node.getStopIndex()-node.getStartIndex()+1);
//		} 
//		catch (BadLocationException e) {
//			e.printStackTrace();
//		}
		StringBuilder buffer = new StringBuilder();
		HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
		String desc = node instanceof Tree.Literal ? "literal" : "expression";
		addImageAndLabel(buffer, null, fileUrl("types.gif").toExternalForm(), 
				16, 16, "<b><tt>" + highlightLine(t.getProducedTypeName()) + 
				"</tt> "+desc+"</b>", 
				20, 4);
		buffer.append( "<hr/>");
		if (node instanceof Tree.StringLiteral) {
			buffer.append("<code style='color:")
			    .append(toHex(getCurrentThemeColor(STRINGS)))
			    .append("'><pre>")
			    .append('\"')
			    .append(convertToHTMLContent(node.getText()))
			    .append('\"')
			    .append("</pre></code>")
			    .append("<hr/>");
			// If a single char selection, then append info on that character too
			if (selectedText != null
			        && codePointCount(selectedText, 0, selectedText.length()) == 1) {
			    appendCharacterHoverInfo(buffer, selectedText);
			}
		}
		else if (node instanceof Tree.CharLiteral) {
		    String character = node.getText();
		    if (character.length()>2) {
		        appendCharacterHoverInfo(buffer, 
		                character.substring(1, character.length()-1));
		    }
		}
		else if (node instanceof Tree.NaturalLiteral) {
		    buffer.append("<code style='color:")
                .append(toHex(getCurrentThemeColor(NUMBERS)))
                .append("'>");
			String text = node.getText().replace("_", "");
		    switch (text.charAt(0)) {
		    case '#':
				buffer.append(parseInt(text.substring(1),16));
				break;
		    case '$':
				buffer.append(parseInt(text.substring(1),2));
				break;
			default:
				buffer.append(parseInt(text));
		    }
			buffer.append("</code>").append("<hr/>");
		}
		else if (node instanceof Tree.FloatLiteral) {
            buffer.append("<code style='color:")
            .append(toHex(getCurrentThemeColor(NUMBERS)))
            .append("'>");
			buffer.append(parseFloat(node.getText().replace("_", "")));
            buffer.append("</code>").append("<hr/>");
		}
		buffer.append("Two quick assists available:<br/>");
		addImageAndLabel(buffer, null, fileUrl("change.png").toExternalForm(), 
				16, 16, "<a href=\"exv:\">Extract value</a>", 
				20, 4);
		addImageAndLabel(buffer, null, fileUrl("change.png").toExternalForm(), 
				16, 16, "<a href=\"exf:\">Extract function</a>", 
				20, 4);
		HTMLPrinter.addPageEpilog(buffer);
		return new CeylonBrowserInput(null, null, buffer.toString());
	}

    private static void appendCharacterHoverInfo(StringBuilder buffer, String character) {
        buffer.append("<code style='color:")
            .append(toHex(getCurrentThemeColor(CHARS)))
            .append("'>")
            .append('\'')
            .append(convertToHTMLContent(character))
            .append('\'')
            .append("</code>");
        int codepoint = Character.codePointAt(character, 0);
        String name = Character.getName(codepoint);
        buffer.append("<hr/>Unicode Name: <code>").append(name).append("</code>");
        String hex = Integer.toHexString(codepoint).toUpperCase();
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        buffer.append("<br/>Codepoint: <code>").append("U+").append(hex).append("</code>");
        buffer.append("<br/>General Category: <code>").append(getCodepointGeneralCategoryName(codepoint)).append("</code>");
        Character.UnicodeScript script = Character.UnicodeScript.of(codepoint);
        buffer.append("<br/>Script: <code>").append(script.name()).append("</code>");
        Character.UnicodeBlock block = Character.UnicodeBlock.of(codepoint);
        buffer.append("<br/>Block: <code>").append(block).append("<hr/>").append("</code>");
    }

    private static String getCodepointGeneralCategoryName(int codepoint) {
        String gc;
        switch (Character.getType(codepoint)) {
        case Character.COMBINING_SPACING_MARK:
            gc = "Mark, combining spacing"; break;
        case Character.CONNECTOR_PUNCTUATION:
            gc = "Punctuation, connector"; break;
        case Character.CONTROL:
            gc = "Other, control"; break;
        case Character.CURRENCY_SYMBOL:
            gc = "Symbol, currency"; break;
        case Character.DASH_PUNCTUATION:
            gc = "Punctuation, dash"; break;
        case Character.DECIMAL_DIGIT_NUMBER:
            gc = "Number, decimal digit"; break;
        case Character.ENCLOSING_MARK:
            gc = "Mark, enclosing"; break;
        case Character.END_PUNCTUATION:
            gc = "Punctuation, close"; break;
        case Character.FINAL_QUOTE_PUNCTUATION:
            gc = "Punctuation, final quote"; break;
        case Character.FORMAT:
            gc = "Other, format"; break;
        case Character.INITIAL_QUOTE_PUNCTUATION:
            gc = "Punctuation, initial quote"; break;
        case Character.LETTER_NUMBER:
            gc = "Number, letter"; break;
        case Character.LINE_SEPARATOR:
            gc = "Separator, line"; break;
        case Character.LOWERCASE_LETTER:
            gc = "Letter, lowercase"; break;
        case Character.MATH_SYMBOL:
            gc = "Symbol, math"; break;
        case Character.MODIFIER_LETTER:
            gc = "Letter, modifier"; break;
        case Character.MODIFIER_SYMBOL:
            gc = "Symbol, modifier"; break;
        case Character.NON_SPACING_MARK:
            gc = "Mark, nonspacing"; break;
        case Character.OTHER_LETTER:
            gc = "Letter, other"; break;
        case Character.OTHER_NUMBER:
            gc = "Number, other"; break;
        case Character.OTHER_PUNCTUATION:
            gc = "Punctuation, other"; break;
        case Character.OTHER_SYMBOL:
            gc = "Symbol, other"; break;
        case Character.PARAGRAPH_SEPARATOR:
            gc = "Separator, paragraph"; break;
        case Character.PRIVATE_USE:
            gc = "Other, private use"; break;
        case Character.SPACE_SEPARATOR:
            gc = "Separator, space"; break;
        case Character.START_PUNCTUATION:
            gc = "Punctuation, open"; break;
        case Character.SURROGATE:
            gc = "Other, surrogate"; break;
        case Character.TITLECASE_LETTER:
            gc = "Letter, titlecase"; break;
        case Character.UNASSIGNED:
            gc = "Other, unassigned"; break;
        case Character.UPPERCASE_LETTER:
            gc = "Letter, uppercase"; break;
        default:
            gc = "&lt;Unknown&gt;";
        }
        return gc;
    }
	
	private static String getIcon(Object obj) {
		if (obj instanceof Module) {
			return "jar_l_obj.gif";
		}
		else if (obj instanceof Package) {
			return "package_obj.gif";
		}
		else if (obj instanceof Declaration) {
			Declaration dec = (Declaration) obj;
			if (dec instanceof Class) {
				return dec.isShared() ? 
						"class_obj.gif" : 
						"innerclass_private_obj.gif";
			}
			else if (dec instanceof Interface) {
				return dec.isShared() ? 
						"int_obj.gif" : 
	                    "innerinterface_private_obj.gif";
			}
			else if (dec instanceof TypeAlias||
			        dec instanceof NothingType) {
				return "types.gif";
			}
			else if (dec.isParameter()) {
				if (dec instanceof Method) {
					return "methpro_obj.gif";
				}
				else {
					return "field_protected_obj.gif";
				}
			}
			else if (dec instanceof Method) {
				return dec.isShared() ?
						"public_co.gif" : 
						"private_co.gif";
			}
			else if (dec instanceof MethodOrValue) {
				return dec.isShared() ?
						"field_public_obj.gif" : 
						"field_private_obj.gif";
			}
			else if (dec instanceof TypeParameter) {
				return "typevariable_obj.gif";
			}
		}
		return null;
	}

	/**
	 * Computes the hover info.
	 * @param previousInput the previous input, or <code>null</code>
	 * @param node 
	 * @param elements the resolved elements
	 * @param editorInputElement the editor input, or <code>null</code>
	 *
	 * @return the HTML hover info for the given element(s) or <code>null</code> 
	 *         if no information is available
	 * @since 3.4
	 */
	static CeylonBrowserInput getHoverInfo(Referenceable model, 
			BrowserInput previousInput, CeylonEditor editor, Node node) {
		if (model instanceof Declaration) {
			Declaration dec = (Declaration) model;
			return new CeylonBrowserInput(previousInput, dec, 
					getDocumentationFor(editor.getParseController(), dec, node));
		}
		else if (model instanceof Package) {
			Package dec = (Package) model;
			return new CeylonBrowserInput(previousInput, dec, 
					getDocumentationFor(editor.getParseController(), dec));
		}
		else if (model instanceof Module) {
			Module dec = (Module) model;
			return new CeylonBrowserInput(previousInput, dec, 
					getDocumentationFor(editor.getParseController(), dec));
		}
		else {
			return null;
		}
	}

	private static void appendJavadoc(IJavaElement elem, StringBuilder sb) {
		if (elem instanceof IMember) {
			try {
            	//TODO: Javadoc @ icon?
				IMember mem = (IMember) elem;
				String jd = JavadocContentAccess2.getHTMLContent(mem, true);
				if (jd!=null) {
					sb.append("<br/>").append(jd);
					String base = getBaseURL(mem, mem.isBinary());
					int endHeadIdx= sb.indexOf("</head>");
					sb.insert(endHeadIdx, "\n<base href='" + base + "'>\n");
				}
			} 
			catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getBaseURL(IJavaElement element, boolean isBinary) throws JavaModelException {
		if (isBinary) {
			// Source attachment usually does not include Javadoc resources
			// => Always use the Javadoc location as base:
			URL baseURL= JavaUI.getJavadocLocation(element, false);
			if (baseURL != null) {
				if (baseURL.getProtocol().equals("jar")) {
					// It's a JarURLConnection, which is not known to the browser widget.
					// Let's start the help web server:
					URL baseURL2= PlatformUI.getWorkbench().getHelpSystem().resolve(baseURL.toExternalForm(), true);
					if (baseURL2 != null) { // can be null if org.eclipse.help.ui is not available
						baseURL= baseURL2;
					}
				}
				return baseURL.toExternalForm();
			}
		} else {
			IResource resource= element.getResource();
			if (resource != null) {
				/*
				 * Too bad: Browser widget knows nothing about EFS and custom URL handlers,
				 * so IResource#getLocationURI() does not work in all cases.
				 * We only support the local file system for now.
				 * A solution could be https://bugs.eclipse.org/bugs/show_bug.cgi?id=149022 .
				 */
				IPath location= resource.getLocation();
				if (location != null)
					return location.toFile().toURI().toString();
			}
		}
		return null;
	}

	public static String getDocumentationFor(CeylonParseController cpc, Package pack) {
	    StringBuilder buffer= new StringBuilder();
		
		addImageAndLabel(buffer, pack, fileUrl(getIcon(pack)).toExternalForm(), 
				16, 16, "<b><tt>" + highlightLine(description(pack)) +"</tt></b>", 20, 4);
		buffer.append("<hr/>");
		Module mod = pack.getModule();
		addImageAndLabel(buffer, mod, fileUrl(getIcon(mod)).toExternalForm(), 
				16, 16, "in module&nbsp;&nbsp;<tt><a " + link(mod) + ">" + 
					getLabel(mod) +"</a></tt>", 20, 2);

		PhasedUnit pu = cpc.getTypeChecker()
				.getPhasedUnitFromRelativePath(pack.getNameAsString().replace('.', '/') + "/package.ceylon");
		if (pu!=null) {
			List<Tree.PackageDescriptor> packageDescriptors = pu.getCompilationUnit().getPackageDescriptors();
			if (!packageDescriptors.isEmpty()) {
			    Tree.PackageDescriptor refnode = packageDescriptors.get(0);
			    if (refnode!=null) {
			        appendDocAnnotationContent(refnode.getAnnotationList(), buffer, pack);
			        appendThrowAnnotationContent(refnode.getAnnotationList(), buffer, pack);
			        appendSeeAnnotationContent(refnode.getAnnotationList(), buffer);
			    }
			}
		}
		
		if (mod.isJava()) {
			buffer.append("<p>This package is implemented in Java.</p>");
		}
		if (JDKUtils.isJDKModule(mod.getNameAsString())) {
			buffer.append("<p>This package forms part of the Java SDK.</p>");			
		}
		
		boolean first = true;
		for (Declaration dec: pack.getMembers()) {
			if (dec instanceof Class && ((Class)dec).isOverloaded()) {
				continue;
			}
			if (dec.isShared() && !dec.isAnonymous()) {
				if (first) {
					buffer.append("<hr/>Contains:&nbsp;&nbsp;");
					first = false;
				}
				else {
					buffer.append(", ");
				}

				/*addImageAndLabel(buffer, null, fileUrl(getIcon(dec)).toExternalForm(), 
					16, 16, "<tt><a " + link(dec) + ">" + 
			        dec.getName() + "</a></tt>", 20, 2);*/
				buffer.append("<tt><a " + link(dec) + ">" + dec.getName() + "</a></tt>");
			}
		}
		if (!first) {
			buffer.append(".<br/>");
		}
		
		insertPageProlog(buffer, 0, getStyleSheet());
		addPageEpilog(buffer);
		return buffer.toString();
		
	}

    private static String description(Package pack) {
        return "package " + getLabel(pack);
    }
    
	public static String getDocumentationFor(ModuleDetails mod, String version) {
	    return getDocumentationForModule(mod.getName(), version, mod.getDoc());
    }
	
	public static String getDocumentationForModule(String name, String version, String doc) {
		StringBuilder buffer= new StringBuilder();
		
		addImageAndLabel(buffer, null, fileUrl("jar_l_obj.gif").toExternalForm(), 
				16, 16, "<b><tt>" + highlightLine(description(name, version)) + "</tt></b>", 20, 4);
		buffer.append("<hr/>");
		
		if (doc!=null) {
		    buffer.append(markdown(doc, null, null));
		}
				
		insertPageProlog(buffer, 0, getStyleSheet());
		addPageEpilog(buffer);
		return buffer.toString();
		
	}

	private static String description(String name, String version) {
        return "module " + name + " \"" + version + "\"";
    }

	public static String getDocumentationFor(CeylonParseController cpc, Module mod) {
		StringBuilder buffer= new StringBuilder();
		
		addImageAndLabel(buffer, mod, fileUrl(getIcon(mod)).toExternalForm(), 
				16, 16, "<b><tt>" + highlightLine(description(mod)) + "</tt></b>", 20, 4);
		buffer.append("<hr/>");

		if (mod.isJava()) {
			buffer.append("<p>This module is implemented in Java.</p>");
		}
		if (mod.isDefault()) {
			buffer.append("<p>The default module for packages which do not belong to explicit module.</p>");
		}
		if (JDKUtils.isJDKModule(mod.getNameAsString())) {
			buffer.append("<p>This module forms part of the Java SDK.</p>");			
		}

		PhasedUnit pu = cpc.getTypeChecker()
				.getPhasedUnitFromRelativePath(mod.getNameAsString().replace('.', '/') + "/module.ceylon");
		if (pu!=null) {
            List<Tree.ModuleDescriptor> moduleDescriptors = pu.getCompilationUnit().getModuleDescriptors();
            if (!moduleDescriptors.isEmpty()) {
                Tree.ModuleDescriptor refnode = moduleDescriptors.get(0);
                if (refnode!=null) {
                    Scope linkScope = mod.getPackage(mod.getNameAsString());
                    appendDocAnnotationContent(refnode.getAnnotationList(), buffer, linkScope);
                    appendThrowAnnotationContent(refnode.getAnnotationList(), buffer, linkScope);
                    appendSeeAnnotationContent(refnode.getAnnotationList(), buffer);
                }
            }
		}
				
		boolean first = true;
		for (Package pack: mod.getPackages()) {
			if (pack.isShared()) {
				if (first) {
					buffer.append("<hr/>Contains:&nbsp;&nbsp;");
					first = false;
				}
				else {
					buffer.append(", ");
				}

				/*addImageAndLabel(buffer, null, fileUrl(getIcon(dec)).toExternalForm(), 
					16, 16, "<tt><a " + link(dec) + ">" + 
			        dec.getName() + "</a></tt>", 20, 2);*/
				buffer.append("<tt><a " + link(pack) + ">" + pack.getNameAsString() + "</a></tt>");
			}
		}
		if (!first) {
			buffer.append(".<br/>");
		}
		
		insertPageProlog(buffer, 0, getStyleSheet());
		addPageEpilog(buffer);
		return buffer.toString();
		
	}

	private static String description(Module mod) {
        return "module " + getLabel(mod) + " \"" + mod.getVersion() + "\"";
    }

	public static String getDocumentationFor(CeylonParseController cpc, Declaration dec) {
		return getDocumentationFor(cpc, dec, null);
	}
	
	public static String getDocumentationFor(CeylonParseController cpc, Declaration dec, Node node) {
		if (dec==null) return null;
		StringBuilder buffer = new StringBuilder();
		insertPageProlog(buffer, 0, getStyleSheet());
		
		Package pack = dec.getUnit().getPackage();
		
		addImageAndLabel(buffer, dec, fileUrl(getIcon(dec)).toExternalForm(), 
				16, 16, "<b><tt>" + (dec.isDeprecated() ? "<s>":"") + 
				highlightLine(description(dec, cpc)) + 
				(dec.isDeprecated() ? "</s>":"") + "</tt></b>", 20, 4);
		buffer.append("<hr/>");
		
		if (dec.isParameter()) {
			Declaration pd = ((MethodOrValue) dec).getInitializerParameter().getDeclaration();
			addImageAndLabel(buffer, pd, fileUrl(getIcon(pd)).toExternalForm(),
					16, 16, "parameter of&nbsp;&nbsp;<tt><a " + link(pd) + ">" + pd.getName() +"</a></tt>", 20, 2);
		}
		else if (dec instanceof TypeParameter) {
			Declaration pd = ((TypeParameter) dec).getDeclaration();
			addImageAndLabel(buffer, pd, fileUrl(getIcon(pd)).toExternalForm(),
					16, 16, "type parameter of&nbsp;&nbsp;<tt><a " + link(pd) + ">" + pd.getName() +"</a></tt>", 20, 2);
		}
		else {
			if (dec.isClassOrInterfaceMember()) {
				ClassOrInterface outer = (ClassOrInterface) dec.getContainer();
				addImageAndLabel(buffer, outer, fileUrl(getIcon(outer)).toExternalForm(), 16, 16, 
						"member of&nbsp;&nbsp;<tt><a " + link(outer) + ">" + 
						convertToHTMLContent(outer.getType().getProducedTypeName()) + "</a></tt>", 20, 2);
			}

			if ((dec.isShared() || dec.isToplevel()) &&
			        !(dec instanceof NothingType)) {
				String label;
				if (pack.getNameAsString().isEmpty()) {
					label = "in default package";
				}
				else {
					label = "in package&nbsp;&nbsp;<tt><a " + link(pack) + ">" + 
							getPackageLabel(dec) +"</a></tt>";
				}
				addImageAndLabel(buffer, pack, fileUrl(getIcon(pack)).toExternalForm(), 
						16, 16, label, 20, 2);
				Module mod = pack.getModule();
				addImageAndLabel(buffer, mod, fileUrl(getIcon(mod)).toExternalForm(), 
						16, 16, "in module&nbsp;&nbsp;<tt><a " + link(mod) + ">" + 
							getModuleLabel(dec) +"</a></tt>", 20, 2);
			}
		}
		
		boolean hasDoc = false;
		Tree.Declaration refnode = (Tree.Declaration) getReferencedNode(dec, cpc);
		if (refnode!=null) {
			appendDeprecatedAnnotationContent(refnode.getAnnotationList(), buffer, resolveScope(dec));
			int len = buffer.length();
			appendDocAnnotationContent(refnode.getAnnotationList(), buffer, resolveScope(dec));
			hasDoc = buffer.length()!=len;
			appendThrowAnnotationContent(refnode.getAnnotationList(), buffer, resolveScope(dec));
			appendSeeAnnotationContent(refnode.getAnnotationList(), buffer);
		}
		
		appendJavadoc(dec, cpc.getProject(), buffer, node);
		
		//boolean extraBreak = false;
		boolean obj=false;
		if (dec instanceof TypedDeclaration) {
			TypeDeclaration td = ((TypedDeclaration) dec).getTypeDeclaration();
			if (td!=null && td.isAnonymous()) {
				obj=true;
				documentInheritance(td, buffer);	
			}
		}
		else if (dec instanceof TypeDeclaration) {
			documentInheritance((TypeDeclaration) dec, buffer);	
		}
		
        Declaration rd = dec.getRefinedDeclaration();
		if (dec!=rd) {
			buffer.append("<p>");
			addImageAndLabel(buffer, rd, fileUrl(rd.isFormal() ? "implm_co.gif" : "over_co.gif").toExternalForm(),
					16, 16, "refines&nbsp;&nbsp;<tt><a " + link(rd) + ">" + rd.getName() +"</a></tt>&nbsp;&nbsp;declared by&nbsp;&nbsp;<tt>" +
					convertToHTMLContent(((TypeDeclaration) rd.getContainer()).getType().getProducedTypeName()) + 
					"</tt>", 20, 2);
			buffer.append("</p>");
			if (!hasDoc) {
		        Tree.Declaration refnode2 = (Tree.Declaration) getReferencedNode(rd, cpc);
		        if (refnode2!=null) {
		            appendDocAnnotationContent(refnode2.getAnnotationList(), buffer, resolveScope(rd));
		        }
			}
		}
		
		if (dec instanceof TypedDeclaration && !obj) {
			ProducedType ret = ((TypedDeclaration) dec).getType();
			if (ret!=null) {
				buffer.append("<p>");
				List<ProducedType> list;
				if (ret.getDeclaration() instanceof UnionType) {
					list = ret.getDeclaration().getCaseTypes();
				}
				else {
					list = Arrays.asList(ret);
				}
				StringBuilder buf = new StringBuilder("returns&nbsp;&nbsp;<tt>");
				for (ProducedType pt: list) {
					if (pt.getDeclaration() instanceof ClassOrInterface || 
							pt.getDeclaration() instanceof TypeParameter) {
						buf.append("<a " + link(pt.getDeclaration()) + ">" + 
								convertToHTMLContent(pt.getProducedTypeName()) +"</a>");
					}
					else {
						buf.append(convertToHTMLContent(pt.getProducedTypeName()));
					}
					buf.append("|");
				}
				buf.setLength(buf.length()-1);
				buf.append("</tt>");
				addImageAndLabel(buffer, ret.getDeclaration(), fileUrl("stepreturn_co.gif").toExternalForm(), 
						16, 16, buf.toString(), 20, 2);
				buffer.append("</p>");
			}
		}
		if (dec instanceof Functional) {
			for (ParameterList pl: ((Functional) dec).getParameterLists()) {
				if (!pl.getParameters().isEmpty()) {
					buffer.append("<p>");
					for (Parameter p: pl.getParameters()) {
					    StringBuilder params = new StringBuilder();
						appendParameters(p.getModel(), params, cpc);
						String def = getDefaultValue(p, cpc);
						StringBuilder doc = new StringBuilder();
						Tree.Declaration refNode = (Tree.Declaration) getReferencedNode(p.getModel(), cpc);
						if (refNode!=null) {
							appendDocAnnotationContent(refNode.getAnnotationList(), doc, resolveScope(dec));
						}
						ProducedType type = p.getType();
						if (type==null) type = new UnknownType(dec.getUnit()).getType();
                        addImageAndLabel(buffer, p.getModel(), fileUrl("methpro_obj.gif"/*"stepinto_co.gif"*/).toExternalForm(),
								16, 16, "accepts&nbsp;&nbsp;<tt><a " + link(type.getDeclaration()) + ">" + 
								convertToHTMLContent(type.getProducedTypeName()) + 
								"</a>&nbsp;<a " + link(p.getModel()) + ">"+ p.getName() +
								convertToHTMLContent(params.toString()) + "</a>" + convertToHTMLContent(def) + "</tt>" + doc, 20, 2);
					}
					buffer.append("</p>");
				}
			}
		}
		
		if (dec instanceof ClassOrInterface) {
			if (!dec.getMembers().isEmpty()) {
				boolean first = true;
				for (Declaration mem: dec.getMembers()) {
					if (mem instanceof Method && ((Method)mem).isOverloaded()) {
						continue;
					}
					if (mem.isShared() && !dec.isAnonymous()) {
						if (first) {
							buffer.append("<hr/>Members:&nbsp;&nbsp;");
							first = false;
						}
						else {
							buffer.append(", ");
						}

						/*addImageAndLabel(buffer, null, fileUrl(getIcon(dec)).toExternalForm(), 
					          16, 16, "<tt><a " + link(dec) + ">" + dec.getName() + "</a></tt>", 20, 2);*/
						buffer.append("<tt><a " + link(mem) + ">" + mem.getName() + "</a></tt>");
					}
				}
				if (!first) {
					buffer.append(".<br/>");
					//extraBreak = true;
				}
			}
		}
		
		if (dec instanceof NothingType) {
		    buffer.append("Special bottom type defined by the language. "
		            + "<code>Nothing</code> is assignable to all types, but has no value. "
		            + "A function or value of type <code>Nothing</code> either throws "
		            + "an exception, or never returns.");
		}
		else {

		    //if (dec.getUnit().getFilename().endsWith(".ceylon")) {
		    //if (extraBreak) 
		    appendExtraActions(dec, buffer);

		}
		
		addPageEpilog(buffer);
		return buffer.toString();
	}

    public static String getDefaultValue(Parameter p, CeylonParseController cpc) {
        if (p.isDefaulted()) {
            if (p.getModel() instanceof Functional) {
                return " => ...";
            }
            else {
                return getInitalValue(p.getModel(), cpc);
            }
        }
        else {
            return "";
        }
    }

    public static void appendExtraActions(Declaration dec, StringBuilder buffer) {
        buffer.append("<hr/>");
        addImageAndLabel(buffer, null, fileUrl("unit.gif").toExternalForm(), 
                16, 16, "<a href='dec:" + declink(dec) + "'>declared</a> in unit&nbsp;&nbsp;<tt>"+ 
                        dec.getUnit().getFilename() + "</tt>", 20, 2);
        //}
        buffer.append("<hr/>");
        addImageAndLabel(buffer, null, fileUrl("search_ref_obj.png").toExternalForm(), 
                16, 16, "<a href='ref:" + declink(dec) + "'>find references</a> to&nbsp;&nbsp;<tt>" +
                        dec.getName() + "</tt>", 20, 2);
        if (dec instanceof ClassOrInterface) {
            addImageAndLabel(buffer, null, fileUrl("search_decl_obj.png").toExternalForm(), 
                    16, 16, "<a href='sub:" + declink(dec) + "'>find subtypes</a> of&nbsp;&nbsp;<tt>" +
                            dec.getName() + "</tt>", 20, 2);
        }
        if (dec instanceof Value) {
            addImageAndLabel(buffer, null, fileUrl("search_ref_obj.png").toExternalForm(), 
                    16, 16, "<a href='ass:" + declink(dec) + "'>find assignments</a> to&nbsp;&nbsp;<tt>" +
                            dec.getName() + "</tt>", 20, 2);
        }
        if (dec.isFormal()||dec.isDefault()) {
            addImageAndLabel(buffer, null, fileUrl("search_decl_obj.png").toExternalForm(), 
                    16, 16, "<a href='act:" + declink(dec) + "'>find refinements</a> of&nbsp;&nbsp;<tt>" +
                            dec.getName() + "</tt>", 20, 2);
        }
    }

	private static void documentInheritance(TypeDeclaration dec, StringBuilder buffer) {
		if (dec instanceof Class) {
			ProducedType sup = ((Class) dec).getExtendedType();
			if (sup!=null) {
				buffer.append("<p>");
				addImageAndLabel(buffer, sup.getDeclaration(), fileUrl("super_co.gif").toExternalForm(), 
						16, 16, "extends <tt><a " + link(sup.getDeclaration()) + ">" + 
				        HTMLPrinter.convertToHTMLContent(sup.getProducedTypeName()) +"</a></tt>", 20, 2);
				buffer.append("</p>");
				//extraBreak = true;
			}
		}
//		if (dec instanceof TypeDeclaration) {
			List<ProducedType> sts = ((TypeDeclaration) dec).getSatisfiedTypes();
			if (!sts.isEmpty()) {
				buffer.append("<p>");
				for (ProducedType td: sts) {
				    addImageAndLabel(buffer, td.getDeclaration(), fileUrl("super_co.gif").toExternalForm(), 
				    16, 16, "satisfies <tt><a " + link(td.getDeclaration()) + ">" + 
				    convertToHTMLContent(td.getProducedTypeName()) +"</a></tt>", 20, 2);
				    //extraBreak = true;
				}
				buffer.append("</p>");
			}
			List<ProducedType> cts = ((TypeDeclaration) dec).getCaseTypes();
			if (cts!=null) {
				buffer.append("<p>");
				for (ProducedType td: cts) {
					addImageAndLabel(buffer, td.getDeclaration(), fileUrl("sub_co.gif").toExternalForm(), 
							16, 16, (td.getDeclaration().isSelfType() ? "has self type" : "has case") + 
							" <tt><a " + link(td.getDeclaration()) + ">" + 
							convertToHTMLContent(td.getProducedTypeName()) +"</a></tt>", 20, 2);
					//extraBreak = true;
				}
				buffer.append("</p>");
			}
//		}
	}

	private static String description(Declaration dec, CeylonParseController cpc) {
		String result = getDescriptionFor(dec, cpc);
		if (dec instanceof TypeDeclaration) {
			TypeDeclaration td = (TypeDeclaration) dec;
			if (td.isAlias() && td.getExtendedType()!=null) {
				result += " => ";
				result += td.getExtendedType().getProducedTypeName();
			}
		}
		else if (dec instanceof Value) {
			if (!((Value) dec).isVariable()) {
				result += getInitalValue(dec, cpc);
			}
		}
		/*else if (dec instanceof ValueParameter) {
			Tree.Declaration refnode = (Tree.Declaration) getReferencedNode(dec, cpc);
			if (refnode instanceof Tree.ValueParameterDeclaration) {
				Tree.DefaultArgument da = ((Tree.ValueParameterDeclaration) refnode).getDefaultArgument();
				if (da!=null) {
					Tree.Expression e = da.getSpecifierExpression().getExpression();
					if (e!=null) {
						Tree.Term term = e.getTerm();
						if (term instanceof Tree.Literal) {
							result += " = ";
							result += term.getText();
						}
						else {
							result += " =";
						}
					}
				}
			}
		}*/
		return result;
	}
	
    public static String getInitalValue(Declaration dec, CeylonParseController cpc) {
        Tree.Declaration refnode = (Tree.Declaration) getReferencedNode(dec, cpc);
        if (refnode instanceof Tree.AttributeDeclaration) {
        	Tree.SpecifierOrInitializerExpression sie = ((Tree.AttributeDeclaration) refnode).getSpecifierOrInitializerExpression();
        	if (sie!=null) {
        		if (sie.getExpression()!=null) {
        			Tree.Term term = sie.getExpression().getTerm();
        			if (term instanceof Tree.Literal) {
        				return " = " + term.getToken().getText();
        			}
        			else if (term instanceof Tree.BaseMemberOrTypeExpression) {
        			    Tree.BaseMemberOrTypeExpression bme = (Tree.BaseMemberOrTypeExpression) term;
        			    if (bme.getIdentifier()!=null) {
        			        return " = " + bme.getIdentifier().getText();
        			    }
        			}
        			else if (term.getUnit().equals(cpc.getRootNode().getUnit())) {
        			    return " = " + AbstractRefactoring.toString(term, cpc.getTokens());
        			}
        			return " = ...";
        		}
        	}
        }
        return "";
    }
	
	static String getAddress(Referenceable model) {
	    if (model==null) return null;
	    return "dec:" + declink(model);
	}
	
	private static String link(Referenceable model) {
		return "href='doc:" + declink(model) + "'";
	}
	
	private static String declink(Referenceable model) {
		if (model instanceof Package) {
			Package p = (Package) model;
            return declink(p.getModule()) + ":" + p.getNameAsString();
		}
		if (model instanceof Module) {
			return  ((Module) model).getNameAsString();
		}
		else if (model instanceof Declaration) {
		    String result = ":" + ((Declaration) model).getName();
			Scope container = ((Declaration) model).getContainer();
			if (container instanceof Referenceable) {
			    return declink((Referenceable) container)
			            + result;
			}
			else {
			    return result;
			}
		}
		else {
		   return "";
		}
	}

    private static void appendJavadoc(Declaration model, IProject project,
            StringBuilder buffer, Node node) {
        IJavaProject jp = JavaCore.create(project);
        if (jp!=null) {
            try {
            	appendJavadoc(getJavaElement(model, jp, node), buffer);
            }
            catch (JavaModelException jme) {
                jme.printStackTrace();
            }
        }
    }

    private static void appendDocAnnotationContent(Tree.AnnotationList annotationList,
            StringBuilder documentation, Scope linkScope) {
        if (annotationList!=null) {
            AnonymousAnnotation aa = annotationList.getAnonymousAnnotation();
            if (aa!=null) {
                documentation.append(markdown(aa.getStringLiteral().getText(), linkScope,
                        annotationList.getUnit()));
            }
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    String name = ((Tree.BaseMemberExpression) annotPrim).getIdentifier().getText();
                    if ("doc".equals(name)) {
                        Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                            if (!args.isEmpty()) {
                                Tree.PositionalArgument a = args.get(0);
                                if (a instanceof Tree.ListedArgument) {
                                	String text = ((Tree.ListedArgument) a).getExpression()
                                			.getTerm().getText();
                                	if (text!=null) {
                                		documentation.append(markdown(text, linkScope,
                                		        annotationList.getUnit()));
                                	}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void appendDeprecatedAnnotationContent(Tree.AnnotationList annotationList,
            StringBuilder documentation, Scope linkScope) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    String name = ((Tree.BaseMemberExpression) annotPrim).getIdentifier().getText();
                    if ("deprecated".equals(name)) {
                        Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                            if (!args.isEmpty()) {
                                Tree.PositionalArgument a = args.get(0);
                                if (a instanceof Tree.ListedArgument) {
                                	String text = ((Tree.ListedArgument) a).getExpression()
                                			    .getTerm().getText();
                                	if (text!=null) {
                                		documentation.append(markdown("_(This is a deprecated program element.)_\n\n" + text, 
                                				linkScope, annotationList.getUnit()));
                                	}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void appendSeeAnnotationContent(Tree.AnnotationList annotationList,
            StringBuilder documentation) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    String name = ((Tree.BaseMemberExpression) annotPrim).getIdentifier().getText();
                    if ("see".equals(name)) {
                        Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                            for (Tree.PositionalArgument arg: args) {
                            	if (arg instanceof Tree.ListedArgument) {
                            		Tree.Term term = ((Tree.ListedArgument) arg).getExpression().getTerm();
                            		if (term instanceof Tree.MetaLiteral) {
                            			Declaration dec = ((Tree.MetaLiteral) term).getDeclaration();
                            			if (dec!=null) {
                            				String dn = dec.getName();
                            				if (dec.isClassOrInterfaceMember()) {
                            				    dn = ((ClassOrInterface) dec.getContainer()).getName() + "." + dn;
                            				}
                            				addImageAndLabel(documentation, dec, fileUrl("link_obj.gif"/*getIcon(dec)*/).toExternalForm(), 16, 16, 
                            						"see <tt><a "+link(dec)+">"+dn+"</a></tt>", 20, 2);
                            			}
                            		}
                            	}
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void appendThrowAnnotationContent(Tree.AnnotationList annotationList,
            StringBuilder documentation, Scope linkScope) {
        if (annotationList!=null) {
            for (Tree.Annotation annotation : annotationList.getAnnotations()) {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof Tree.BaseMemberExpression) {
                    String name = ((Tree.BaseMemberExpression) annotPrim).getIdentifier().getText();
                    if ("throws".equals(name)) {
                        Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                            if (args.isEmpty()) continue;
                            Tree.PositionalArgument typeArg = args.get(0);
                        	Tree.PositionalArgument textArg = args.size()>1 ? args.get(1) : null;
                            if (typeArg instanceof Tree.ListedArgument && 
                            		(textArg==null || textArg instanceof Tree.ListedArgument)) {
                            	Tree.Term typeArgTerm = ((Tree.ListedArgument) typeArg).getExpression().getTerm();
                            	Tree.Term textArgTerm = textArg==null ? null : ((Tree.ListedArgument) textArg).getExpression().getTerm();
                            	String text = textArgTerm instanceof Tree.StringLiteral ?
                            			textArgTerm.getText() : "";
                    			if (typeArgTerm instanceof Tree.MetaLiteral) {
                    				Declaration dec = ((Tree.MetaLiteral) typeArgTerm).getDeclaration();
                    				if (dec!=null) {
                    					String dn = dec.getName();
                    					if (typeArgTerm instanceof Tree.QualifiedMemberOrTypeExpression) {
                    						Tree.Primary p = ((Tree.QualifiedMemberOrTypeExpression) typeArgTerm).getPrimary();
                    						if (p instanceof Tree.MemberOrTypeExpression) {
                    							dn = ((Tree.MemberOrTypeExpression) p).getDeclaration().getName()
                    									+ "." + dn;
                    						}
                    					}
                    					addImageAndLabel(documentation, dec, fileUrl("ihigh_obj.gif"/*getIcon(dec)*/).toExternalForm(), 16, 16, 
                    							"throws <tt><a "+link(dec)+">"+dn+"</a></tt>" + 
                    							        markdown(text, linkScope, annotationList.getUnit()), 20, 2);
                    				}
                    			}
                            }
                        }
                    }
                }
            }
        }
    }
    
	public static URL fileUrl(String icon) {
		try {
			return FileLocator.toFileURL(FileLocator.find(CeylonPlugin.getInstance().getBundle(), 
					new Path("icons/").append(icon), null));
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the Javadoc hover style sheet with the current Javadoc font from the preferences.
	 * @return the updated style sheet
	 * @since 3.4
	 */
	public static String getStyleSheet() {
		if (fgStyleSheet == null)
			fgStyleSheet = loadStyleSheet();
		//Color c = CeylonTokenColorer.getCurrentThemeColor("docHover");
		//String color = toHexString(c.getRed()) + toHexString(c.getGreen()) + toHexString(c.getBlue());
		String css= fgStyleSheet;// + "body { background-color: #" + color+ " }";
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry()
					.getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			css= HTMLPrinter.convertTopLevelFont(css, fontData);
		}
		return css;
	}

	/**
	 * Loads and returns the Javadoc hover style sheet.
	 * @return the style sheet, or <code>null</code> if unable to load
	 * @since 3.4
	 */
	public static String loadStyleSheet() {
		Bundle bundle= Platform.getBundle(JavaPlugin.getPluginId());
		URL styleSheetURL= bundle.getEntry("/JavadocHoverStyleSheet.css"); 
		if (styleSheetURL != null) {
			BufferedReader reader= null;
			try {
				reader= new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
				StringBuilder buffer= new StringBuilder(1500);
				String line= reader.readLine();
				while (line != null) {
					buffer.append(line);
					buffer.append('\n');
					line= reader.readLine();
				}
				return buffer.toString();
			} catch (IOException ex) {
				JavaPlugin.log(ex);
				return ""; 
			} finally {
				try {
					if (reader != null)
						reader.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static void addImageAndLabel(StringBuilder buf, Referenceable model, String imageSrcPath, 
			int imageWidth, int imageHeight, String label, int labelLeft, int labelTop) {
		buf.append("<div style='word-wrap: break-word; position: relative; "); 
		
		if (imageSrcPath != null) {
			buf.append("margin-left: ").append(labelLeft).append("px; ");  
			buf.append("padding-top: ").append(labelTop).append("px; ");  
		}

		buf.append("'>"); 
		if (imageSrcPath != null) {
			if (model!=null) {
				buf.append("<a ").append(link(model)).append(">");  
			}
			addImage(buf, imageSrcPath, imageWidth, imageHeight,
					labelLeft);
			if (model!=null) {
				buf.append("</a>"); 
			}
		}
		
		buf.append(label);
		
		buf.append("</div>"); 
	}

	public static void addImage(StringBuilder buf, String imageSrcPath, 
			int imageWidth, int imageHeight, int labelLeft) {
		StringBuilder imageStyle= new StringBuilder("border:none; position: absolute; "); 
		imageStyle.append("width: ").append(imageWidth).append("px; ");  
		imageStyle.append("height: ").append(imageHeight).append("px; ");  
		imageStyle.append("left: ").append(- labelLeft - 1).append("px; ");  

		// hack for broken transparent PNG support in IE 6, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=223900 :
		buf.append("<!--[if lte IE 6]><![if gte IE 5.5]>\n"); 
		//String tooltip= element == null ? "" : "alt='" + "Open Declaration" + "' ";   
		buf.append("<span ").append("style=\"").append(imageStyle)  
				.append("filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='")
				.append(imageSrcPath).append("')\"></span>\n");  
		buf.append("<![endif]><![endif]-->\n"); 

		buf.append("<!--[if !IE]>-->\n"); 
		buf.append("<img ").append("style='").append(imageStyle).append("' src='")
		        .append(imageSrcPath).append("'/>\n");    
		buf.append("<!--<![endif]-->\n"); 
		buf.append("<!--[if gte IE 7]>\n"); 
		buf.append("<img ").append("style='").append(imageStyle).append("' src='")
		        .append(imageSrcPath).append("'/>\n");    
		buf.append("<![endif]-->\n"); 
	}
	
	private static String markdown(String text, final Scope linkScope, final Unit unit) {
	    if( text == null || text.length() == 0 ) {
	        return text;
	    }

//	    String unquotedText = text.substring(1, text.length()-1);

	    Builder builder = Configuration.builder().forceExtentedProfile();
	    builder.setCodeBlockEmitter(new CeylonBlockEmitter());
	    if (linkScope!=null) {
	    	builder.setSpecialLinkEmitter(new SpanEmitter() {
                @Override
                public void emitSpan(StringBuilder out, String content) {
                    String linkName;
                    String linkTarget; 
                    
                    int indexOf = content.indexOf("|");
                    if( indexOf == -1 ) {
                        linkName = content;
                        linkTarget = content;
                    } else {
                        linkName = content.substring(0, indexOf);
                        linkTarget = content.substring(indexOf+1, content.length()); 
                    }
                    
                    String href = resolveLink(linkTarget, linkScope, unit);
                    if (href != null) {
                        out.append("<a ").append(href).append(">");
                    }
                    out.append("<code>");
                    int sep = linkName.indexOf("::");
                    out.append(sep<0?linkName:linkName.substring(sep+2));
                    out.append("</code>");
                    if (href != null) {
                        out.append("</a>");
                    }
                }
            });
	    }
	    return Processor.process(text, builder.build());
	}
	
    private static String resolveLink(String linkTarget, Scope linkScope, Unit unit) {
        String declName;
        Scope scope = null;
        int pkgSeparatorIndex = linkTarget.indexOf("::");
        if( pkgSeparatorIndex == -1 ) {
            declName = linkTarget;
            scope = linkScope;
        } 
        else {
            String pkgName = linkTarget.substring(0, pkgSeparatorIndex);
            declName = linkTarget.substring(pkgSeparatorIndex+2, linkTarget.length());
            Module module = resolveModule(linkScope);
            if( module != null ) {
                scope = module.getPackage(pkgName);
            }
        }
        
        if (scope==null || declName == null || "".equals(declName)) {
            return null; // no point in continuing. Required for non-token auto-complete.
        }
        
        String[] declNames = declName.split("\\.");
        Declaration decl = scope.getMemberOrParameter(unit, declNames[0], null, false);
        for (int i=1; i<declNames.length; i++) {
            if (decl instanceof Scope) {
                scope = (Scope) decl;
                decl = scope.getMember(declNames[i], null, false);
            }
            else {
                decl = null;
                break;
            }
        }
    
        if (decl != null) {
            String href = link(decl);
            return href;
        }
        else {
            return null;
        }
    }
    
    private static Scope resolveScope(Declaration decl) {
        if (decl == null) {
            return null;
        } else if (decl instanceof Scope) {
            return (Scope) decl;
        } else {
            return decl.getContainer();
        }
    }
    
    private static Module resolveModule(Scope scope) {
        if (scope == null) {
            return null;
        } else if (scope instanceof Package) {
            return ((Package) scope).getModule();
        } else {
            return resolveModule(scope.getContainer());
        }
    }
    
    public static final class CeylonBlockEmitter implements BlockEmitter {
        
        @Override
        public void emitBlock(StringBuilder out, List<String> lines, String meta) {
            if (!lines.isEmpty()) {
                out.append("<pre>");
                /*if (meta == null || meta.length() == 0) {
                    out.append("<pre>");
                } else {
                    out.append("<pre class=\"brush: ").append(meta).append("\">");
                }*/
                StringBuilder code = new StringBuilder();
                for (String s: lines) {
                    code.append(s).append('\n');
                }
                String highlighted;
                if (meta == null || meta.length() == 0 || "ceylon".equals(meta)) {
                    highlighted = highlightLine(code.toString());
                }
                else {
                    highlighted = code.toString();
                }
                out.append(highlighted);
                out.append("</pre>\n");
            }
        }

    }

    public static String highlightLine(String line) {
        String kwc = toHex(getCurrentThemeColor(KEYWORDS));
        String tc = toHex(getCurrentThemeColor(TYPES));
        String ic = toHex(getCurrentThemeColor(IDENTIFIERS));
        String sc = toHex(getCurrentThemeColor(STRINGS));
        String nc = toHex(getCurrentThemeColor(NUMBERS));
        String cc = toHex(getCurrentThemeColor(CHARS));
        String pc = toHex(getCurrentThemeColor(PACKAGES));
        String lcc = toHex(getCurrentThemeColor(COMMENTS));
        CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(line));
        Token token;
        boolean inPackageName = false;
        StringBuilder result = new StringBuilder();
        while ((token=lexer.nextToken()).getType()!=CeylonLexer.EOF) {
            String s = convertToHTMLContent(token.getText());
            int type = token.getType();
            if (type!=CeylonLexer.LIDENTIFIER &&
                type!=CeylonLexer.MEMBER_OP) {
                inPackageName = false;
            }
            else if (inPackageName) {
                result.append("<span style='color:"+pc+"'>").append(s).append("</span>");
                continue;
            }
            switch (type) {
            case CeylonLexer.FLOAT_LITERAL:
            case CeylonLexer.NATURAL_LITERAL:
                result.append("<span style='color:"+nc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.CHAR_LITERAL:
                result.append("<span style='color:"+cc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.STRING_LITERAL:
            case CeylonLexer.STRING_START:
            case CeylonLexer.STRING_MID:
            case CeylonLexer.VERBATIM_STRING:
                result.append("<span style='color:"+sc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.UIDENTIFIER:
                result.append("<span style='color:"+tc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.LIDENTIFIER:
                result.append("<span style='color:"+ic+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.MULTI_COMMENT:
            case CeylonLexer.LINE_COMMENT:
                result.append("<span style='color:"+lcc+"'>").append(s).append("</span>");
                break;
            case CeylonLexer.IMPORT:
            case CeylonLexer.PACKAGE:
            case CeylonLexer.MODULE:
                inPackageName = true; //then fall through!
            default:
                if (CeylonTokenColorer.keywords.contains(s)) {
                    result.append("<span style='color:"+kwc+"'>").append(s).append("</span>");
                }
                else {
                    result.append(s);
                }
            }
        }
        return result.toString();
    }
    
    /**
     * Creates the "enriched" control.
     */
    private final class PresenterControlCreator extends AbstractReusableInformationControlCreator {
        
        private final DocumentationHover docHover;
        
        PresenterControlCreator(DocumentationHover docHover) {
            this.docHover = docHover;
        }
        
        @Override
        public IInformationControl doCreateInformationControl(Shell parent) {
            if (isAvailable(parent)) {
                ToolBarManager tbm = new ToolBarManager(SWT.FLAT);
                BrowserInformationControl control = new BrowserInformationControl(parent, 
                        APPEARANCE_JAVADOC_FONT, tbm);

                final BackAction backAction = new BackAction(control);
                backAction.setEnabled(false);
                tbm.add(backAction);
                final ForwardAction forwardAction = new ForwardAction(control);
                tbm.add(forwardAction);
                forwardAction.setEnabled(false);

                //final ShowInJavadocViewAction showInJavadocViewAction= new ShowInJavadocViewAction(iControl);
                //tbm.add(showInJavadocViewAction);
                final OpenDeclarationAction openDeclarationAction = new OpenDeclarationAction(control);
                tbm.add(openDeclarationAction);

//                final SimpleSelectionProvider selectionProvider = new SimpleSelectionProvider();
                //TODO: an action to open the generated ceylondoc  
                //      from the doc archive, in a browser window
                /*if (fSite != null) {
                    OpenAttachedJavadocAction openAttachedJavadocAction= new OpenAttachedJavadocAction(fSite);
                    openAttachedJavadocAction.setSpecialSelectionProvider(selectionProvider);
                    openAttachedJavadocAction.setImageDescriptor(DESC_ELCL_OPEN_BROWSER);
                    openAttachedJavadocAction.setDisabledImageDescriptor(DESC_DLCL_OPEN_BROWSER);
                    selectionProvider.addSelectionChangedListener(openAttachedJavadocAction);
                    selectionProvider.setSelection(new StructuredSelection());
                    tbm.add(openAttachedJavadocAction);
                }*/

                IInputChangedListener inputChangeListener = new IInputChangedListener() {
                    public void inputChanged(Object newInput) {
                        backAction.update();
                        forwardAction.update();
//                        if (newInput == null) {
//                            selectionProvider.setSelection(new StructuredSelection());
//                        }
//                        else 
                        boolean isDeclaration = false;
                        if (newInput instanceof CeylonBrowserInput) {
//                            Object inputElement = ((CeylonBrowserInput) newInput).getInputElement();
//                            selectionProvider.setSelection(new StructuredSelection(inputElement));
                            //showInJavadocViewAction.setEnabled(isJavaElementInput);
                            isDeclaration = ((CeylonBrowserInput) newInput).getAddress()!=null;
                        }
                        openDeclarationAction.setEnabled(isDeclaration);
                    }
                };
                control.addInputChangeListener(inputChangeListener);

                tbm.update(true);

                docHover.addLinkListener(control);
                return control;

            } 
            else {
                return new DefaultInformationControl(parent, true);
            }
        }
        
    }
    
    private final class HoverControlCreator extends AbstractReusableInformationControlCreator {
        
        private final DocumentationHover docHover;
        private String statusLineMessage;
        private final IInformationControlCreator enrichedControlCreator;

        HoverControlCreator(DocumentationHover docHover, 
                IInformationControlCreator enrichedControlCreator,
                String statusLineMessage) {
            this.docHover = docHover;
            this.enrichedControlCreator = enrichedControlCreator;
            this.statusLineMessage = statusLineMessage;
        }
        
        @Override
        public IInformationControl doCreateInformationControl(Shell parent) {
            if (enrichedControlCreator!=null && isAvailable(parent)) {
                BrowserInformationControl control = new BrowserInformationControl(parent, 
                        APPEARANCE_JAVADOC_FONT, statusLineMessage) {
                    @Override
                    public IInformationControlCreator getInformationPresenterControlCreator() {
                        return enrichedControlCreator;
                    }
                };
                if (docHover!=null) {
                    docHover.addLinkListener(control);
                }
                return control;
            } 
            else {
                return new DefaultInformationControl(parent, statusLineMessage);
            }
        }
        
    }
    
}