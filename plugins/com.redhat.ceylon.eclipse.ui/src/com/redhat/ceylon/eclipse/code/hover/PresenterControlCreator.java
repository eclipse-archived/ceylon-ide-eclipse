package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.code.hover.BrowserInformationControl.isAvailable;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.hover.CeylonHover.BackAction;
import com.redhat.ceylon.eclipse.code.hover.CeylonHover.ForwardAction;
import com.redhat.ceylon.eclipse.code.hover.CeylonHover.OpenDeclarationAction;

/**
 * Presenter control creator that creates the "enriched" control.
 */
public final class PresenterControlCreator extends AbstractReusableInformationControlCreator {
	
	private final CeylonHover docHover;
	
	public PresenterControlCreator(CeylonHover docHover) {
		this.docHover = docHover;
	}
	
	@Override
	public IInformationControl doCreateInformationControl(Shell parent) {
		if (isAvailable(parent)) {
			ToolBarManager tbm= new ToolBarManager(SWT.FLAT);
			BrowserInformationControl control= new BrowserInformationControl(parent, 
					APPEARANCE_JAVADOC_FONT, tbm);

			final BackAction backAction= new CeylonHover.BackAction(control);
			backAction.setEnabled(false);
			tbm.add(backAction);
			final ForwardAction forwardAction= new CeylonHover.ForwardAction(control);
			tbm.add(forwardAction);
			forwardAction.setEnabled(false);

			//final ShowInJavadocViewAction showInJavadocViewAction= new ShowInJavadocViewAction(iControl);
			//tbm.add(showInJavadocViewAction);
			final OpenDeclarationAction openDeclarationAction = docHover.new OpenDeclarationAction(control);
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

			docHover.addLinkListener(control);
			return control;

		} 
		else {
			return new DefaultInformationControl(parent, true);
		}
	}
	
}