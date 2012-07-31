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

import static com.redhat.ceylon.eclipse.code.hover.BrowserInformationControl.isAvailable;
import static com.redhat.ceylon.eclipse.code.hover.CeylonWordFinder.findWord;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getLabel;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getModuleLabel;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.getJavaElement;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.javadoc.JavaDocLocations;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInput;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.osgi.framework.Bundle;

import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Processor;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Getter;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Primary;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.search.FindAssignmentsAction;
import com.redhat.ceylon.eclipse.code.search.FindReferencesAction;
import com.redhat.ceylon.eclipse.code.search.FindRefinementsAction;
import com.redhat.ceylon.eclipse.code.search.FindSubtypesAction;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;


/**
 * Provides Javadoc as hover info for Java elements.
 *
 * @since 2.1
 */
public class DocHover 
        implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {
	
	private CeylonEditor editor;
	
	public DocHover(CeylonEditor editor) {
		this.editor = editor;
	}

	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return findWord(textViewer.getDocument(), offset);
	}
	/**
	 * Action to go back to the previous input in the hover control.
	 */
	private static final class BackAction extends Action {
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
			BrowserInformationControlInput previous= (BrowserInformationControlInput) fInfoControl.getInput().getPrevious();
			if (previous != null) {
				fInfoControl.setInput(previous);
			}
		}

		public void update() {
			BrowserInformationControlInput current= fInfoControl.getInput();
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
	private static final class ForwardAction extends Action {
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
			BrowserInformationControlInput next= (BrowserInformationControlInput) fInfoControl.getInput().getNext();
			if (next != null) {
				fInfoControl.setInput(next);
			}
		}

		public void update() {
			BrowserInformationControlInput current= fInfoControl.getInput();
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
	private final class OpenDeclarationAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public OpenDeclarationAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			setText("Open Declaration");
			setLocalImageDescriptors(this, "goto_input.gif");
		}

		@Override
		public void run() {
			gotoDeclaration(fInfoControl, fInfoControl.getInput().getInputElement());
		}
	}
	
	private void gotoDeclaration(BrowserInformationControl control, Object model) {
		close(control); //FIXME: should have protocol to hide, rather than dispose
		CeylonParseController cpc = editor.getParseController();
		Declaration dec = (Declaration)model;
		Node refNode = getReferencedNode(dec, cpc);
		if (refNode!=null) {
			gotoNode(refNode, cpc.getProject(), cpc.getTypeChecker());
		}
		else {
			gotoJavaNode(dec, cpc);
		}
	}

	private void close(BrowserInformationControl control) {
		control.notifyDelayedInputChange(null);
		control.dispose();
	}


	/**
	 * Presenter control creator that creates the "enriched" control.
	 */
	public final class PresenterControlCreator extends AbstractReusableInformationControlCreator {
		@Override
		public IInformationControl doCreateInformationControl(Shell parent) {
			if (isAvailable(parent)) {
				ToolBarManager tbm= new ToolBarManager(SWT.FLAT);
				BrowserInformationControl control= new BrowserInformationControl(parent, 
						APPEARANCE_JAVADOC_FONT, tbm, null
						/*CeylonTokenColorer.getCurrentThemeColor("docHover")*/);

				final BackAction backAction= new BackAction(control);
				backAction.setEnabled(false);
				tbm.add(backAction);
				final ForwardAction forwardAction= new ForwardAction(control);
				tbm.add(forwardAction);
				forwardAction.setEnabled(false);

				//final ShowInJavadocViewAction showInJavadocViewAction= new ShowInJavadocViewAction(iControl);
				//tbm.add(showInJavadocViewAction);
				final OpenDeclarationAction openDeclarationAction= new OpenDeclarationAction(control);
				tbm.add(openDeclarationAction);

				final SimpleSelectionProvider selectionProvider= new SimpleSelectionProvider();
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

				IInputChangedListener inputChangeListener= new IInputChangedListener() {
					public void inputChanged(Object newInput) {
						backAction.update();
						forwardAction.update();
						if (newInput == null) {
							selectionProvider.setSelection(new StructuredSelection());
						} else if (newInput instanceof BrowserInformationControlInput) {
							BrowserInformationControlInput input= (BrowserInformationControlInput) newInput;
							Object inputElement= input.getInputElement();
							selectionProvider.setSelection(new StructuredSelection(inputElement));
							boolean isDeclarationElementInput= inputElement instanceof Declaration;
							//showInJavadocViewAction.setEnabled(isJavaElementInput);
							openDeclarationAction.setEnabled(isDeclarationElementInput);
						}
					}
				};
				control.addInputChangeListener(inputChangeListener);

				tbm.update(true);

				addLinkListener(control);
				return control;

			} 
			else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}


	/**
	 * Hover control creator.
	 */
	public final class HoverControlCreator extends AbstractReusableInformationControlCreator {
		private String statusLineMessage;
		private final IInformationControlCreator fInformationPresenterControlCreator;

		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator,
				String statusLineMessage) {
			fInformationPresenterControlCreator= informationPresenterControlCreator;
			this.statusLineMessage = statusLineMessage;
		}

		@Override
		public IInformationControl doCreateInformationControl(Shell parent) {
			if (isAvailable(parent)) {
				BrowserInformationControl control= new BrowserInformationControl(parent, 
						APPEARANCE_JAVADOC_FONT, statusLineMessage, null
						/*CeylonTokenColorer.getCurrentThemeColor("docHover")*/) {
					@Override
					public IInformationControlCreator getInformationPresenterControlCreator() {
						return fInformationPresenterControlCreator;
					}
					@Override
					public Point computeSizeHint() {
						Point sh = super.computeSizeHint();
						return new Point(sh.x, sh.y*4);
					}
				};
				addLinkListener(control);
				return control;
			} 
			else {
				return new DefaultInformationControl(parent, statusLineMessage);
			}
		}

		/*@Override
		public boolean canReuse(IInformationControl control) {
			if (!super.canReuse(control))
				return false;

			if (control instanceof IInformationControlExtension4) {
				//((IInformationControlExtension4)control).setStatusText(tooltipAffordanceString);
			}

			return true;
		}*/
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
			fPresenterControlCreator= new PresenterControlCreator();
		return fPresenterControlCreator;
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return getHoverControlCreator("F2 for focus");
	}

	public IInformationControlCreator getHoverControlCreator(
			String statusLineMessage) {
		if (fHoverControlCreator == null) {
			fHoverControlCreator= new HoverControlCreator(getInformationPresenterControlCreator(), 
					statusLineMessage);
		}
		return fHoverControlCreator;
	}

	private void addLinkListener(final BrowserInformationControl control) {
		control.addLocationListener(new LocationListener() {
			@Override
			public void changing(LocationEvent event) {
				String location = event.location;
				if (location.startsWith("dec:")) {
					Object target = getModel(control, location);
					if (target!=null) {
						gotoDeclaration(control, target);
					}
				}
				else if (location.startsWith("doc:")) {
					Object target = getModel(control, location);
					if (target!=null) {
						control.setInput(getHoverInfo(target, control.getInput(), null));
					}
				}
				else if (location.startsWith("ref:")) {
					Object target = getModel(control, location);
					close(control);
					new FindReferencesAction(editor, (Declaration) target).run();
				}
				else if (location.startsWith("sub:")) {
					Object target = getModel(control, location);
					close(control);
					new FindSubtypesAction(editor, (Declaration) target).run();
				}
				else if (location.startsWith("act:")) {
					Object target = getModel(control, location);
					close(control);
					new FindRefinementsAction(editor, (Declaration) target).run();
				}
				else if (location.startsWith("ass:")) {
					Object target = getModel(control, location);
					close(control);
					new FindAssignmentsAction(editor, (Declaration) target).run();
				}
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
											StringBuffer sb = new StringBuffer();
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
			@Override
			public void changed(LocationEvent event) {}
		});
	}

	public Object getModel(final BrowserInformationControl control,
			String location) {
		String[] bits = location.split(":");
		Object model = control.getInput().getInputElement();
		Module module;
		if (model instanceof String) {
			module = editor.getParseController().getRootNode().getUnit().getPackage().getModule();
		}
		else if (model instanceof Declaration) {
			Declaration dec = (Declaration) model;
			module = dec.getUnit().getPackage().getModule();
		}
		else if (model instanceof Package){
			Package pack = (Package) model;
			module = pack.getModule();
		}
		else {
			return null;
		}
		Object target = module.getPackage(bits[1]);
		for (int i=2; i<bits.length; i++) {
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
			target = scope.getDirectMemberOrParameter(bits[i], null);
		}
		return target;
	}

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		DocBrowserInformationControlInput info= (DocBrowserInformationControlInput) getHoverInfo2(textViewer, hoverRegion);
		return info != null ? info.getHtml() : null;
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		return internalGetHoverInfo(textViewer, hoverRegion);
	}

	private DocBrowserInformationControlInput internalGetHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		Node node = findNode(editor.getParseController().getRootNode(), 
				hoverRegion.getOffset());
		if (node instanceof Tree.ImportPath) {
			return getHoverInfo(((Tree.ImportPath) node).getPackageModel(), null, node);
		}
		else {
			return getHoverInfo(getReferencedDeclaration(node), null, node);
		}
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
						"innerinterface_private_obj.gif";
			}
			else if (dec instanceof Interface) {
				return dec.isShared() ? 
						"int_obj.gif" : 
						"innerclass_private_obj.gif";
			}
			else if (dec instanceof Parameter) {
				return "methpro_obj.gif";
			}
			else if (dec instanceof MethodOrValue) {
				return dec.isShared() ?
						"public_co.gif" : 
						"private_co.gif";
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
	private DocBrowserInformationControlInput getHoverInfo(Object model, 
			BrowserInformationControlInput previousInput, Node node) {
		if (model instanceof Declaration) {
			Declaration dec = (Declaration) model;
			return new DocBrowserInformationControlInput(previousInput, dec, 
					getDocumentationFor(editor.getParseController(), dec, node), 20);
		}
		if (model instanceof Package) {
			Package dec = (Package) model;
			return new DocBrowserInformationControlInput(previousInput, dec, 
					getDocumentationFor(editor.getParseController(), dec), 20);
		}
		else {
			return null;
		}
	}

	private static void appendJavadoc(IJavaElement elem, StringBuffer sb) {
		if (elem instanceof IMember) {
			try {
            	//TODO: Javadoc @ icon?
				sb.append("<br/>").append(JavadocContentAccess2.getHTMLContent((IMember) elem, true));
				String base = JavaDocLocations.getBaseURL((IMember) elem);
				int endHeadIdx= sb.indexOf("</head>");
				sb.insert(endHeadIdx, "\n<base href='" + base + "'>\n");
			} 
			catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getDocumentationFor(CeylonParseController cpc, Package pack) {
		StringBuffer buffer= new StringBuffer();
		
		addImageAndLabel(buffer, pack, fileUrl(getIcon(pack)).toExternalForm(), 
				16, 16, "<b><tt>" + getLabel(pack) +"</tt></b>", 20, 4);
		buffer.append("<hr/>");
		addImageAndLabel(buffer, null, fileUrl(getIcon(pack.getModule())).toExternalForm(), 
				16, 16, "in module&nbsp;&nbsp;<tt>" + getLabel(pack.getModule()) +"</tt>", 20, 2);

		//TODO: add package doc string
		
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
		
		HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
		
	}

	public static String getDocumentationFor(CeylonParseController cpc, Declaration dec) {
		return getDocumentationFor(cpc, dec, null);
	}
	
	public static String getDocumentationFor(CeylonParseController cpc, Declaration dec, Node node) {
		if (dec==null) return null;
		StringBuffer buffer= new StringBuffer();
		HTMLPrinter.insertPageProlog(buffer, 0, DocHover.getStyleSheet());
		
		Package pack = dec.getUnit().getPackage();
		
		addImageAndLabel(buffer, dec, fileUrl(getIcon(dec)).toExternalForm(), 
				16, 16, "<b><tt>" + HTMLPrinter.convertToHTMLContent(getDescriptionFor(dec)) + "</tt></b>", 20, 4);
		buffer.append("<hr/>");
		
		if (dec instanceof Parameter) {
			Declaration pd = ((Parameter) dec).getDeclaration();
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
								HTMLPrinter.convertToHTMLContent(outer.getType().getProducedTypeName()) + "</a></tt>", 20, 2);
			}

			if (dec.isShared() || dec.isToplevel()) {
				addImageAndLabel(buffer, pack, fileUrl(getIcon(pack)).toExternalForm(), 
						16, 16, "in package&nbsp;&nbsp;<tt><a " + link(pack) + ">" + 
								getPackageLabel(dec) +"</a></tt>", 20, 2);
				addImageAndLabel(buffer, null, fileUrl(getIcon(pack.getModule())).toExternalForm(), 
						16, 16, "in module&nbsp;&nbsp;<tt>" + getModuleLabel(dec) +"</tt>", 20, 2);
			}
		}
		
		Tree.Declaration refnode = getReferencedNode(dec, cpc);
		if (refnode!=null) {
			appendDocAnnotationContent(refnode, buffer);
			appendSeeAnnotationContent(refnode, buffer);
		}
		
		appendJavadoc(dec, cpc.getProject(), buffer, node);
		
		//boolean extraBreak = false;
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
		if (dec instanceof TypeDeclaration) {
			List<ProducedType> sts = ((TypeDeclaration) dec).getSatisfiedTypes();
			if (!sts.isEmpty()) {
				buffer.append("<p>");
				for (ProducedType td: sts) {
					addImageAndLabel(buffer, td.getDeclaration(), fileUrl("super_co.gif").toExternalForm(), 
							16, 16, "satisfies <tt><a " + link(td.getDeclaration()) + ">" + 
									HTMLPrinter.convertToHTMLContent(td.getProducedTypeName()) +"</a></tt>", 20, 2);
					//extraBreak = true;
				}
				buffer.append("</p>");
			}
			List<ProducedType> cts = ((TypeDeclaration) dec).getCaseTypes();
			if (cts!=null) {
				buffer.append("<p>");
				for (ProducedType td: cts) {
					addImageAndLabel(buffer, td.getDeclaration(), fileUrl("sub_co.gif").toExternalForm(), 
							16, 16, "has case <tt><a " + link(td.getDeclaration()) + ">" + 
									HTMLPrinter.convertToHTMLContent(td.getProducedTypeName()) +"</a></tt>", 20, 2);
					//extraBreak = true;
				}
				buffer.append("</p>");
			}
		}
		if (dec!=dec.getRefinedDeclaration()) {
			buffer.append("<p>");
			Declaration rd = dec.getRefinedDeclaration();
			addImageAndLabel(buffer, rd, fileUrl(rd.isFormal() ? "implm_co.gif" : "over_co.gif").toExternalForm(),
					16, 16, "refines&nbsp;&nbsp;<tt><a " + link(rd) + ">" + rd.getName() +"</a></tt>&nbsp;&nbsp;declared by&nbsp;&nbsp;<tt>" +
					HTMLPrinter.convertToHTMLContent(((TypeDeclaration) rd.getContainer()).getType().getProducedTypeName()) + 
					"</tt>", 20, 2);
			buffer.append("</p>");
		}
		
		if (dec instanceof TypedDeclaration) {
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
				StringBuffer buf = new StringBuffer("returns&nbsp;&nbsp;<tt>");
				for (ProducedType pt: list) {
					if (pt.getDeclaration() instanceof ClassOrInterface || 
							pt.getDeclaration() instanceof TypeParameter) {
						buf.append("<a " + link(pt.getDeclaration()) + ">" + 
								HTMLPrinter.convertToHTMLContent(pt.getProducedTypeName()) +"</a>");
					}
					else {
						buf.append(HTMLPrinter.convertToHTMLContent(pt.getProducedTypeName()));
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
						StringBuffer doc = new StringBuffer();
						Tree.Declaration refNode = getReferencedNode(p, cpc);
						if (refNode!=null) {
							appendDocAnnotationContent(refNode, doc);
						}
						if (doc.length()!=0) {
							doc.insert(0, ":");
						}
						addImageAndLabel(buffer, p, fileUrl("methpro_obj.gif"/*"stepinto_co.gif"*/).toExternalForm(),
								16, 16, "accepts&nbsp;&nbsp;<tt><a " + link(p) + ">" + getDescriptionFor(p) + 
								"</a></tt>" + doc, 20, 2);
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
					16, 16, "<tt><a " + link(dec) + ">" + 
			        dec.getName() + "</a></tt>", 20, 2);*/
						buffer.append("<tt><a " + link(mem) + ">" + mem.getName() + "</a></tt>");
					}
				}
				if (!first) {
					buffer.append(".<br/>");
					//extraBreak = true;
				}
			}
		}
		
		//if (dec.getUnit().getFilename().endsWith(".ceylon")) {
			//if (extraBreak) 
			buffer.append("<hr/>");
			addImageAndLabel(buffer, null, fileUrl("template_obj.gif").toExternalForm(), 
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
		if (dec instanceof Value
				|| dec instanceof Parameter
				|| dec instanceof Getter && ((Getter)dec).isVariable()) {
			addImageAndLabel(buffer, null, fileUrl("search_ref_obj.png").toExternalForm(), 
					16, 16, "<a href='ass:" + declink(dec) + "'>find assignments</a> to&nbsp;&nbsp;<tt>" +
							dec.getName() + "</tt>", 20, 2);
		}
		if (dec.isFormal()||dec.isDefault()) {
			addImageAndLabel(buffer, null, fileUrl("search_decl_obj.png").toExternalForm(), 
					16, 16, "<a href='act:" + declink(dec) + "'>find refinements</a> of&nbsp;&nbsp;<tt>" +
							dec.getName() + "</tt>", 20, 2);
		}
		
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}
	
	private static String link(Object model) {
		return "href='doc:" + declink(model) + "'";
	}
	
	private static String declink(Object model) {
		if (model instanceof Package) {
			return ((Package)model).getQualifiedNameString();
		}
		else if (model instanceof Declaration) {
			return declink(((Declaration) model).getContainer())
					+ ":" + ((Declaration) model).getName();
		}
		else {
		   return "";
		}
	}

    private static void appendJavadoc(Declaration model, IProject project,
            StringBuffer buffer, Node node) {
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

    private static void appendDocAnnotationContent(Tree.Declaration decl,
            StringBuffer documentation) {
        Tree.AnnotationList annotationList = decl.getAnnotationList();
        if (annotationList != null)
        {
            for (Tree.Annotation annotation : annotationList.getAnnotations())
            {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof BaseMemberExpression)
                {
                    String name = ((BaseMemberExpression) annotPrim).getIdentifier().getText();
                    if ("doc".equals(name))
                    {
                        Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                            if (!args.isEmpty()) {
                                String text = args.get(0).getExpression().getTerm().getText();
                                if (text!=null) {
                                    documentation.append(markdown(text));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void appendSeeAnnotationContent(Tree.Declaration decl,
            StringBuffer documentation) {
        Tree.AnnotationList annotationList = decl.getAnnotationList();
        if (annotationList != null)
        {
            for (Tree.Annotation annotation : annotationList.getAnnotations())
            {
                Tree.Primary annotPrim = annotation.getPrimary();
                if (annotPrim instanceof BaseMemberExpression)
                {
                    String name = ((BaseMemberExpression) annotPrim).getIdentifier().getText();
                    if ("see".equals(name))
                    {
                        Tree.PositionalArgumentList argList = annotation.getPositionalArgumentList();
                        if (argList!=null) {
                            List<Tree.PositionalArgument> args = argList.getPositionalArguments();
                            for (Tree.PositionalArgument arg: args) {
                                Term term = arg.getExpression().getTerm();
								if (term instanceof MemberOrTypeExpression) {
									Declaration dec = ((MemberOrTypeExpression) term).getDeclaration();
									if (dec!=null) {
										String dn = dec.getName();
										if (term instanceof QualifiedMemberOrTypeExpression) {
											Primary p = ((QualifiedMemberOrTypeExpression) term).getPrimary();
											if (p instanceof MemberOrTypeExpression) {
												dn = ((MemberOrTypeExpression) p).getDeclaration().getName()
														+ "." + dn;
											}
										}
										addImageAndLabel(documentation, dec, fileUrl("link_obj.gif"/*getIcon(dec)*/).toExternalForm(), 16, 16, 
												"<tt>see <a "+link(dec)+">"+dn+"</a></tt>", 20, 2);
									}
								}
								/*if (term instanceof QualifiedMemberOrTypeExpression) {
	                            	documentation.append("<p><tt>see ");
									ProducedReference target = ((QualifiedMemberOrTypeExpression) term).getTarget();
									if (target!=null) {
										Declaration dec = target.getDeclaration();
										documentation.append(dec.getQualifiedNameString());
									}
									documentation.append("</tt></p>");
								}*/
                            }
                            /*if (!args.isEmpty()) {
                            	documentation.append("<br/>");
                            }*/
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
			fgStyleSheet= loadStyleSheet();
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
				StringBuffer buffer= new StringBuffer(1500);
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

	public static void addImageAndLabel(StringBuffer buf, Object model, String imageSrcPath, 
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

	public static void addImage(StringBuffer buf, String imageSrcPath, 
			int imageWidth, int imageHeight, int labelLeft) {
		StringBuffer imageStyle= new StringBuffer("border:none; position: absolute; "); 
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
	
	private static String markdown(String text) {
	    // TODO after compiler release use com.redhat.ceylon.ceylondoc.Util.wikiToHTML
	    // TODO after txtmark release use txtmark.SpanEmitter for links inside doc annotation
	    if( text == null || text.length() == 0 ) {
	        return text;
	    }

	    String unquotedText = text.substring(1, text.length()-1);

	    Configuration config = Configuration.builder()
	            .forceExtentedProfile()
	            .build();

	    return Processor.process(unquotedText, config);
	}

}