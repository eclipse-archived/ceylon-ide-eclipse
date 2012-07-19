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

import static com.redhat.ceylon.eclipse.code.hover.CeylonDocumentationProvider.sanitize;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getLabel;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getModuleLabel;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static org.eclipse.jdt.internal.ui.JavaPluginImages.setLocalImageDescriptors;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;
import static org.eclipse.jface.internal.text.html.BrowserInformationControl.isAvailable;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK_DISABLED;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD_DISABLED;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.SimpleSelectionProvider;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.jface.internal.text.html.BrowserInput;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension4;
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
import org.eclipse.ui.IWorkbenchSite;
import org.osgi.framework.Bundle;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;


/**
 * Provides Javadoc as hover info for Java elements.
 *
 * @since 2.1
 */
public class DocHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {
	
	private CeylonEditor editor;
	
	public DocHover(CeylonEditor editor) {
		this.editor = editor;
	}

	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return JavaWordFinder.findWord(textViewer.getDocument(), offset);
	}
	/**
	 * Action to go back to the previous input in the hover control.
	 *
	 * @since 3.4
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
				setToolTipText(Messages.format("Back to {0}", 
						previous.getInputName()));
				setEnabled(true);
			} else {
				setToolTipText("Back");
				setEnabled(false);
			}
		}
	}

	/**
	 * Action to go forward to the next input in the hover control.
	 *
	 * @since 3.4
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
				setToolTipText(Messages.format("Forward to {0}", 
						current.getNext().getInputName()));
				setEnabled(true);
			} else {
				setToolTipText("Forward");
				setEnabled(false);
			}
		}
	}

	/**
	 * Action that shows the current hover contents in the Javadoc view.
	 *
	 * @since 3.4
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
	 *
	 * @since 3.4
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
			gotoDeclaration(fInfoControl);
		}
	}
	
	private void gotoDeclaration(BrowserInformationControl control) {
		DocBrowserInformationControlInput infoInput= (DocBrowserInformationControlInput) control.getInput();
		control.notifyDelayedInputChange(null);
		control.dispose(); //FIXME: should have protocol to hide, rather than dispose

		CeylonParseController parseController = editor.getParseController();
		gotoNode(getReferencedNode((Declaration) infoInput.getInputElement(), parseController), 
				parseController.getProject(), parseController.getTypeChecker());
	}


	/**
	 * Presenter control creator.
	 *
	 * @since 3.3
	 */
	public final class PresenterControlCreator extends AbstractReusableInformationControlCreator {

		private IWorkbenchSite fSite;

		/**
		 * Creates a new PresenterControlCreator.
		 * 
		 * @param site the site or <code>null</code> if none
		 * @since 3.6
		 */
		public PresenterControlCreator(IWorkbenchSite site) {
			fSite= site;
		}

		@Override
		public IInformationControl doCreateInformationControl(Shell parent) {
			if (isAvailable(parent)) {
				ToolBarManager tbm= new ToolBarManager(SWT.FLAT);
				BrowserInformationControl iControl= new BrowserInformationControl(parent, 
						APPEARANCE_JAVADOC_FONT, tbm);

				final BackAction backAction= new BackAction(iControl);
				backAction.setEnabled(false);
				tbm.add(backAction);
				final ForwardAction forwardAction= new ForwardAction(iControl);
				tbm.add(forwardAction);
				forwardAction.setEnabled(false);

				//final ShowInJavadocViewAction showInJavadocViewAction= new ShowInJavadocViewAction(iControl);
				//tbm.add(showInJavadocViewAction);
				final OpenDeclarationAction openDeclarationAction= new OpenDeclarationAction(iControl);
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
				iControl.addInputChangeListener(inputChangeListener);

				tbm.update(true);

				addLinkListener(iControl);
				return iControl;

			} 
			else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}


	/**
	 * Hover control creator.
	 *
	 * @since 3.3
	 */
	public final class HoverControlCreator extends AbstractReusableInformationControlCreator {
		private String statusLineMessage;
		/**
		 * The information presenter control creator.
		 * @since 3.4
		 */
		private final IInformationControlCreator fInformationPresenterControlCreator;
		/**
		 * <code>true</code> to use the additional info affordance, 
		 * <code>false</code> to use the hover affordance.
		 */
		private final boolean fAdditionalInfoAffordance;

		/**
		 * @param informationPresenterControlCreator control creator for enriched hover
		 * @since 3.4
		 */
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator,
				String statusLineMessage) {
			this(informationPresenterControlCreator, false);
			this.statusLineMessage = statusLineMessage;
		}

		/**
		 * @param informationPresenterControlCreator control creator for enriched hover
		 * @param additionalInfoAffordance <code>true</code> to use the additional info affordance,
		 *                                 <code>false</code> to use the hover affordance
		 * @since 3.4
		 */
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator, 
				boolean additionalInfoAffordance) {
			fInformationPresenterControlCreator= informationPresenterControlCreator;
			fAdditionalInfoAffordance= additionalInfoAffordance;
		}

		@Override
		public IInformationControl doCreateInformationControl(Shell parent) {
			if (isAvailable(parent)) {
				BrowserInformationControl iControl= new BrowserInformationControl(parent, 
						APPEARANCE_JAVADOC_FONT, statusLineMessage) {
					@Override
					public IInformationControlCreator getInformationPresenterControlCreator() {
						return fInformationPresenterControlCreator;
					}
					@Override
					public Point computeSizeHint() {
						Point sh = super.computeSizeHint();
						return new Point(Math.max(300,sh.x/4*3), sh.y+100);
					}
				};
				addLinkListener(iControl);
				return iControl;
			} 
			else {
				return new DefaultInformationControl(parent, statusLineMessage);
			}
		}

		@Override
		public boolean canReuse(IInformationControl control) {
			if (!super.canReuse(control))
				return false;

			if (control instanceof IInformationControlExtension4) {
				//((IInformationControlExtension4)control).setStatusText(tooltipAffordanceString);
			}

			return true;
		}
	}

	/**
	 * The style sheet (css).
	 * @since 3.4
	 */
	private static String fgStyleSheet;

	/**
	 * The hover control creator.
	 *
	 * @since 3.2
	 */
	private IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 *
	 * @since 3.2
	 */
	private IInformationControlCreator fPresenterControlCreator;

	private  IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null)
			fPresenterControlCreator= new PresenterControlCreator(editor.getEditorSite());
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
				if ("declaration:".equals(location)) {
					gotoDeclaration((BrowserInformationControl) control);
				}
				else if (location.startsWith("doc:")) {
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
						return;
					}
					Object target = module.getPackage(bits[1]);
					for (int i=2; i<bits.length; i++) {
						target = ((Scope) target).getDirectMemberOrParameter(bits[i], null);
					}
					control.setInput(getHoverInfo(target, control.getInput()));
				}
			}
			@Override
			public void changed(LocationEvent event) {}
		});
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
			return getHoverInfo(((Tree.ImportPath) node).getPackageModel(), null);
		}
		else {
			return getHoverInfo(getReferencedDeclaration(node), null);
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
	 * @param elements the resolved elements
	 * @param editorInputElement the editor input, or <code>null</code>
	 *
	 * @return the HTML hover info for the given element(s) or <code>null</code> 
	 *         if no information is available
	 * @since 3.4
	 */
	private DocBrowserInformationControlInput getHoverInfo(Object model, 
			BrowserInformationControlInput previousInput) {
		if (model instanceof Declaration) {
			Declaration dec = (Declaration) model;
			return new DocBrowserInformationControlInput(previousInput, dec, 
					getDocumentationFor(editor.getParseController(), dec), 20);
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

	public static String getDocumentationFor(CeylonParseController cpc, Package pack) {
		StringBuffer buffer= new StringBuffer();
		
		addImageAndLabel(buffer, null, fileUrl(getIcon(pack)).toExternalForm(), 
				16, 16, "<b><tt>" + getLabel(pack) +"</tt></b>", 20, 2);
		
		addImageAndLabel(buffer, null, fileUrl(getIcon(pack.getModule())).toExternalForm(), 
				16, 16, "in module&nbsp;&nbsp;<tt>" + getLabel(pack.getModule()) +"</tt>", 20, 2);

		//TODO: add package doc string
		
		buffer.append("<br/>Contains:&nbsp;&nbsp;");
		boolean first = true;
		for (Declaration dec: pack.getMembers()) {
			if (first) {
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
		buffer.append(".<br/>");
		
		HTMLPrinter.insertPageProlog(buffer, 0, DocHover.getStyleSheet());
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
		
	}
	public static String getDocumentationFor(CeylonParseController cpc, Declaration dec) {
		StringBuffer buffer= new StringBuffer();
		
		Package pack = dec.getUnit().getPackage();
		
		addImageAndLabel(buffer, null, fileUrl(getIcon(dec)).toExternalForm(), 
				16, 16, "<b><tt>" + sanitize(getDescriptionFor(dec)) + "</tt></b>", 20, 2);
		buffer.append("<br/>");
		
		if (dec.isClassOrInterfaceMember()) {
			ClassOrInterface outer = (ClassOrInterface) dec.getContainer();
			addImageAndLabel(buffer, null, fileUrl(getIcon(dec.getContainer())).toExternalForm(), 16, 16, 
					"member of&nbsp;&nbsp;<tt><a " + link(outer) + ">" + 
			        sanitize(outer.getType().getProducedTypeName()) + "</a></tt>", 20, 2);
		}

		if (dec.isShared()) {
			addImageAndLabel(buffer, null, fileUrl(getIcon(pack)).toExternalForm(), 
					16, 16, "in package&nbsp;&nbsp;<tt><a " + link(pack) + ">" + 
			        getPackageLabel(dec) +"</a></tt>", 20, 2);
			addImageAndLabel(buffer, null, fileUrl(getIcon(pack.getModule())).toExternalForm(), 
					16, 16, "in module&nbsp;&nbsp;<tt>" + getModuleLabel(dec) +"</tt>", 20, 2);
		}

		Tree.Declaration refnode = getReferencedNode(dec, cpc);
		if (refnode!=null) {
			appendDocAnnotationContent(refnode, buffer);
			appendSeeAnnotationContent(refnode, buffer);
		}
		
		boolean extraBreak = false;
		if (dec instanceof Class) {
			ProducedType sup = ((Class) dec).getExtendedType();
			if (sup!=null) {
				addImageAndLabel(buffer, null, fileUrl("super_co.gif").toExternalForm(), 
						16, 16, "<tt>extends <a " + link(sup.getDeclaration()) + ">" + 
				        sanitize(sup.getProducedTypeName()) +"</a></tt>", 20, 2);
				extraBreak = true;
			}
		}
		if (dec instanceof TypeDeclaration) {
			for (ProducedType td: ((TypeDeclaration) dec).getSatisfiedTypes()) {
				addImageAndLabel(buffer, null, fileUrl("super_co.gif").toExternalForm(), 
						16, 16, "<tt>satisfies <a " + link(td.getDeclaration()) + ">" + 
				        sanitize(td.getProducedTypeName()) +"</a></tt>", 20, 2);
				extraBreak = true;
			}
		}
		
		if (dec instanceof ClassOrInterface) {
			buffer.append("<br/>Members:&nbsp;&nbsp;");
			boolean first = true;
			for (Declaration mem: dec.getMembers()) {
				if (first) {
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
			buffer.append(".<br/>");
			extraBreak = true;
		}
		
		if (dec.getUnit().getFilename().endsWith(".ceylon")) {
			if (extraBreak) buffer.append("<br/>");
			addImageAndLabel(buffer, null, fileUrl("template_obj.gif").toExternalForm(), 
					16, 16, "<a href='declaration:'>declared</a> in unit&nbsp;&nbsp;<tt>"+ 
							dec.getUnit().getFilename() + "</tt>", 20, 2);
		}
		
		HTMLPrinter.insertPageProlog(buffer, 0, DocHover.getStyleSheet());
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
                            	//TODO: properly process the markdown!!
                                String docLine = sanitize(args.get(0).getExpression().getTerm().getText());
                                documentation.append("<p>")
                                    .append(docLine.subSequence(1, docLine.length()-1).toString()
                                		.replaceAll("`([^`]+)`", "<tt>$1</tt>"))
                                	.append("</p>");
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
								if (term instanceof BaseMemberOrTypeExpression) {
									ProducedReference target = ((BaseMemberOrTypeExpression) term).getTarget();
									if (target!=null) {
										Declaration dec = target.getDeclaration();
										addImageAndLabel(documentation, null, fileUrl(getIcon(dec)).toExternalForm(), 16, 16, 
												"<tt>see <a "+link(dec)+">"+dec.getName()+"</a></tt>", 20, 2);
									}
								}
								if (term instanceof QualifiedMemberOrTypeExpression) {
	                            	documentation.append("<p><tt>see ");
									ProducedReference target = ((QualifiedMemberOrTypeExpression) term).getTarget();
									if (target!=null) {
										Declaration dec = target.getDeclaration();
										documentation.append(dec.getQualifiedNameString());
									}
									documentation.append("</tt></p>");
								}
                            }
                            if (!args.isEmpty()) {
                            	documentation.append("<br/>");
                            }
                        }
                    }
                }
            }
        }
    }
    
	private static URL fileUrl(String icon) {
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
	private static String getStyleSheet() {
		if (fgStyleSheet == null)
			fgStyleSheet= loadStyleSheet();
		String css= fgStyleSheet;
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
	private static String loadStyleSheet() {
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

	public static void addImageAndLabel(StringBuffer buf, IJavaElement element, String imageSrcPath, 
			int imageWidth, int imageHeight, String label, int labelLeft, int labelTop) {
		buf.append("<div style='word-wrap: break-word; position: relative; "); 
		
		if (imageSrcPath != null) {
			buf.append("margin-left: ").append(labelLeft).append("px; ");  
			buf.append("padding-top: ").append(labelTop).append("px; ");  
		}

		buf.append("'>"); 
		if (imageSrcPath != null) {
			if (element != null) {
				try {
					String uri= JavaElementLinks.createURI(JavaElementLinks.OPEN_LINK_SCHEME, element);
					buf.append("<a href='").append(uri).append("'>");  
				} catch (URISyntaxException e) {
					element= null; // no link
				}
			}
			addImage(buf, imageSrcPath, imageWidth, imageHeight,
					labelLeft);
			if (element != null) {
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
	
}

