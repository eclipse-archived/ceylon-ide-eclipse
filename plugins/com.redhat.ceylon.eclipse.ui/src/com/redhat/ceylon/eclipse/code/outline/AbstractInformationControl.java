/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

 *******************************************************************************/

package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.runtime.PluginImages;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.ide.IDE;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;

/**
 * Abstract class for Show hierarchy in light-weight controls.
 */
public abstract class AbstractInformationControl 
    implements IInformationControl, IInformationControlExtension, 
               IInformationControlExtension2, IInformationControlExtension3, 
               DisposeListener {

	/**
	 * The NamePatternFilter selects the elements which
	 * match the given string patterns.
	 */
	public class NamePatternFilter extends ViewerFilter {
		public NamePatternFilter() {}

		public boolean select(Viewer viewer, Object parentElement, Object element) {
			//TODO: enable filtering!!!!!!
			return true;
			/*StringMatcher matcher= getMatcher();
	    if (matcher == null || !(viewer instanceof TreeViewer))
		return true;
	    TreeViewer treeViewer= (TreeViewer) viewer;
	    String matchName= ((ILabelProvider) treeViewer.getLabelProvider()).getText(element);
	    if (matchName != null && matcher.match(matchName))
		return true;
	    return hasUnfilteredChild(treeViewer, element);
			 */
		}

		private boolean hasUnfilteredChild(TreeViewer viewer, Object element) {
			Object[] children= ((ITreeContentProvider) viewer.getContentProvider()).getChildren(element);
			for(int i= 0; i < children.length; i++)
				if (select(viewer, element, children[i]))
					return true;
			return false;
		}
	}

	private static class BorderFillLayout extends Layout {
		/** The border widths. */
		final int fBorderSize;

		/**
		 * Creates a fill layout with a border.
		 *
		 * @param borderSize the border size
		 */
		public BorderFillLayout(int borderSize) {
			if (borderSize < 0)
				throw new IllegalArgumentException();
			fBorderSize= borderSize;
		}

		/**
		 * Returns the border size.
		 *
		 * @return the border size
		 */
		public int getBorderSize() {
			return fBorderSize;
		}

		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			Control[] children= composite.getChildren();
			Point minSize= new Point(0, 0);
			if (children != null) {
				for(int i= 0; i < children.length; i++) {
					Point size= children[i].computeSize(wHint, hHint, flushCache);
					minSize.x= Math.max(minSize.x, size.x);
					minSize.y= Math.max(minSize.y, size.y);
				}
			}
			minSize.x+= fBorderSize * 2 + RIGHT_MARGIN;
			minSize.y+= fBorderSize * 2;
			return minSize;
		}

		protected void layout(Composite composite, boolean flushCache) {
			Control[] children= composite.getChildren();
			Point minSize= new Point(composite.getClientArea().width, composite.getClientArea().height);
			if (children != null) {
				for(int i= 0; i < children.length; i++) {
					Control child= children[i];
					child.setSize(minSize.x - fBorderSize * 2, minSize.y - fBorderSize * 2);
					child.setLocation(fBorderSize, fBorderSize);
				}
			}
		}
	}

	/**
	 * The view menu's Remember Size and Location action.
	 */
	private class RememberBoundsAction extends Action {
		RememberBoundsAction() {
			super("Remember Size and Location", IAction.AS_CHECK_BOX);
			setChecked(!getDialogSettings().getBoolean(STORE_DISABLE_RESTORE_LOCATION));
		}

		public void run() {
			IDialogSettings settings= getDialogSettings();
			boolean newValue= !isChecked();
			// store new value
			settings.put(STORE_DISABLE_RESTORE_LOCATION, newValue);
			settings.put(STORE_DISABLE_RESTORE_SIZE, newValue);
			fIsDecativateListenerActive= true;
		}
	}

	/**
	 * The view menu's Resize action.
	 */
	private class ResizeAction extends Action {
		ResizeAction() {
			super("Resize", IAction.AS_PUSH_BUTTON);
		}

		public void run() {
			Tracker tracker= new Tracker(fShell.getDisplay(), SWT.RESIZE);
			tracker.setStippled(true);
			Rectangle[] r= new Rectangle[] { getFilterText().getShell().getBounds() };
			tracker.setRectangles(r);
			if (tracker.open())
				fShell.setBounds(tracker.getRectangles()[0]);
		}
	}

	/**
	 * The view menu's Move action.
	 */
	private class MoveAction extends Action {
		MoveAction() {
			super("Move", IAction.AS_PUSH_BUTTON);
		}

		/*
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			Tracker tracker= new Tracker(fShell.getDisplay(), SWT.NONE);
			tracker.setStippled(true);
			Rectangle[] r= new Rectangle[] { getFilterText().getShell().getBounds() };
			tracker.setRectangles(r);
			if (tracker.open())
				fShell.setBounds(tracker.getRectangles()[0]);
		}
	}

	/** Border thickness in pixels. */
	private static final int BORDER= 1;
	/** Right margin in pixels. */
	private static final int RIGHT_MARGIN= 3;
	/**
	 * Dialog constants telling whether this control can be resized or move.
	 */
	private static final String STORE_DISABLE_RESTORE_SIZE= "DISABLE_RESTORE_SIZE"; //$NON-NLS-1$
	private static final String STORE_DISABLE_RESTORE_LOCATION= "DISABLE_RESTORE_LOCATION"; //$NON-NLS-1$
	/** The control's shell */
	private Shell fShell;
	/** The composite */
	Composite fComposite;
	/** The control's text widget */
	private Text fFilterText;
	/** The control's tree widget */
	private TreeViewer fTreeViewer;
	/** The current string matcher */
	//private StringMatcher fStringMatcher;
	private Command fInvokingCommand;
	private Label fStatusField;
	private Font fStatusTextFont;
	private KeySequence[] fInvokingCommandKeySequences;
	/**
	 * Remembers the bounds for this information control.
	 * @since 3.0
	 */
	private Rectangle fBounds;
	private Rectangle fTrim;
	/**
	 * Fields for view menu support.
	 * @since 3.0
	 */
	private Button fViewMenuButton;
	private ToolBar fToolBar;
	private Composite fViewMenuButtonComposite;
	private MenuManager fViewMenuManager;
	private Listener fDeactivateListener;
	private boolean fIsDecativateListenerActive= false;
	//  private CustomFiltersActionGroup fCustomFiltersActionGroup;
	private IKeyBindingService fKeyBindingService;
	private String[] fKeyBindingScopes;
	private IAction fShowViewMenuAction;
	private IExecutionListener fShowViewMenuHandlerSubmission;

	/**
	 * Creates a tree information control with the given shell as parent. The given
	 * styles are applied to the shell and the tree widget.
	 *
	 * @param parent the parent shell
	 * @param shellStyle the additional styles for the shell
	 * @param treeStyle the additional styles for the tree widget
	 * @param invokingCommandId the id of the command that invoked this control or <code>null</code>
	 * @param showStatusField <code>true</code> iff the control has a status field at the bottom
	 */
	public AbstractInformationControl(Shell parent, int shellStyle, int treeStyle, String invokingCommandId, boolean showStatusField) {
		if (invokingCommandId != null) {
			ICommandService commandManager= (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
			fInvokingCommand= commandManager.getCommand(invokingCommandId);
			if (fInvokingCommand != null && !fInvokingCommand.isDefined())
				fInvokingCommand= null;
			else {
				// Pre-fetch key sequence - do not change because scope will change later.
				//		getInvokingCommandKeySequences();
			}
		}
		fShell= new Shell(parent, shellStyle);
		Display display= fShell.getDisplay();
		fShell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		// Composite for filter text and tree
		fComposite= new Composite(fShell, SWT.RESIZE);
		GridLayout layout= new GridLayout(1, false);
		fComposite.setLayout(layout);
		fComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fViewMenuButtonComposite= new Composite(fComposite, SWT.NONE);
		layout= new GridLayout(2, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		fViewMenuButtonComposite.setLayout(layout);
		fViewMenuButtonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (hasHeader()) {
			createHeader(fViewMenuButtonComposite);
			fFilterText= createFilterText(fComposite);
		} else {
			fFilterText= createFilterText(fViewMenuButtonComposite);
		}
		createViewMenu(fViewMenuButtonComposite);
		createHorizontalSeparator(fComposite);
		fTreeViewer= createTreeViewer(fComposite, treeStyle);
		//	fCustomFiltersActionGroup= new CustomFiltersActionGroup(getId(), fTreeViewer);
		if (showStatusField)
			createStatusField(fComposite);
		final Tree tree= fTreeViewer.getTree();
		tree.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == 0x1B) // ESC
					dispose();
			}
			public void keyReleased(KeyEvent e) {}
		});
		tree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {}
			public void widgetDefaultSelected(SelectionEvent e) {
				gotoSelectedElement();
			}
		});
		tree.addMouseMoveListener(new MouseMoveListener() {
			TreeItem fLastItem= null;

			public void mouseMove(MouseEvent e) {
				if (tree.equals(e.getSource())) {
					Object o= tree.getItem(new Point(e.x, e.y));
					if (o instanceof TreeItem) {
						if (!o.equals(fLastItem)) {
							fLastItem= (TreeItem) o;
							tree.setSelection(new TreeItem[] { fLastItem });
						} 
						else if (e.y < tree.getItemHeight() / 4) {
							// Scroll up
							Point p= tree.toDisplay(e.x, e.y);
							Item item= fTreeViewer.scrollUp(p.x, p.y);
							if (item instanceof TreeItem) {
								fLastItem= (TreeItem) item;
								tree.setSelection(new TreeItem[] { fLastItem });
							}
						} 
						else if (e.y > tree.getBounds().height - tree.getItemHeight() / 4) {
							// Scroll down
							Point p= tree.toDisplay(e.x, e.y);
							Item item= fTreeViewer.scrollDown(p.x, p.y);
							if (item instanceof TreeItem) {
								fLastItem= (TreeItem) item;
								tree.setSelection(new TreeItem[] { fLastItem });
							}
						}
					}
				}
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if (tree.getSelectionCount() < 1)
					return;
				if (e.button != 1)
					return;
				if (tree.equals(e.getSource())) {
					Object o= tree.getItem(new Point(e.x, e.y));
					TreeItem selection= tree.getSelection()[0];
					if (selection.equals(o))
						gotoSelectedElement();
				}
			}
		});
		int border= ((shellStyle & SWT.NO_TRIM) == 0) ? 0 : BORDER;
		fShell.setLayout(new BorderFillLayout(border));
		if (hasHeader()) {
			fComposite.setTabList(new Control[] { fFilterText, fTreeViewer.getTree() });
		} else {
			fViewMenuButtonComposite.setTabList(new Control[] { fFilterText });
			fComposite.setTabList(new Control[] { fViewMenuButtonComposite, fTreeViewer.getTree() });
		}
		setInfoSystemColor();
		installFilter();
		addDisposeListener(this);
		fDeactivateListener= new Listener() {
			public void handleEvent(Event event) {
				if (fIsDecativateListenerActive)
					dispose();
			}
		};
		fShell.addListener(SWT.Deactivate, fDeactivateListener);
		fIsDecativateListenerActive= true;
		fShell.addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent e) {
				if (e.widget == fShell && fShell.getShells().length == 0)
					fIsDecativateListenerActive= true;
			}
		});
		fShell.addControlListener(new ControlAdapter() {
			/**
			 * {@inheritDoc}
			 */
			public void controlMoved(ControlEvent e) {
				fBounds= fShell.getBounds();
				if (fTrim != null) {
					Point location= fComposite.getLocation();
					fBounds.x= fBounds.x - fTrim.x + location.x;
					fBounds.y= fBounds.y - fTrim.y + location.y;
				}
			}

			/**
			 * {@inheritDoc}
			 */
			public void controlResized(ControlEvent e) {
				fBounds= fShell.getBounds();
				if (fTrim != null) {
					Point location= fComposite.getLocation();
					fBounds.x= fBounds.x - fTrim.x + location.x;
					fBounds.y= fBounds.y - fTrim.y + location.y;
				}
			}
		});
	}

	/**
	 * Creates a tree information control with the given shell as parent. The given
	 * styles are applied to the shell and the tree widget.
	 *
	 * @param parent the parent shell
	 * @param shellStyle the additional styles for the shell
	 * @param treeStyle the additional styles for the tree widget
	 */
	public AbstractInformationControl(Shell parent, int shellStyle, int treeStyle) {
		this(parent, shellStyle, treeStyle, null, false);
	}

	protected abstract TreeViewer createTreeViewer(Composite parent, int style);

	/**
	 * Returns the name of the dialog settings section.
	 *
	 * @return the name of the dialog settings section
	 */
	protected abstract String getId();

	protected TreeViewer getTreeViewer() {
		return fTreeViewer;
	}

	protected boolean hasHeader() {
		// default is to have no header
		return false;
	}

	/**
	 * Creates a header for this information control.
	 * <p>
	 * Note: The header is only be created if {@link #hasHeader()} returns <code>true</code>.
	 * </p>
	 *
	 * @param parent
	 */
	protected void createHeader(Composite parent) {
		// default is to have no header
	}

	protected Text getFilterText() {
		return fFilterText;
	}

	protected Text createFilterText(Composite parent) {
		fFilterText= new Text(parent, SWT.NONE);
		GridData data= new GridData(GridData.FILL_HORIZONTAL);
		GC gc= new GC(parent);
		gc.setFont(parent.getFont());
		FontMetrics fontMetrics= gc.getFontMetrics();
		gc.dispose();
		data.heightHint= Dialog.convertHeightInCharsToPixels(fontMetrics, 1);
		data.horizontalAlignment= GridData.FILL;
		data.verticalAlignment= GridData.CENTER;
		fFilterText.setLayoutData(data);
		fFilterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 0x0D) // return
					gotoSelectedElement();
				if (e.keyCode == SWT.ARROW_DOWN)
					fTreeViewer.getTree().setFocus();
				if (e.keyCode == SWT.ARROW_UP)
					fTreeViewer.getTree().setFocus();
				if (e.character == 0x1B) // ESC
					dispose();
			}

			public void keyReleased(KeyEvent e) {
				// do nothing
			}
		});
		return fFilterText;
	}

	protected void createHorizontalSeparator(Composite parent) {
		Label separator= new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DOT);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createViewMenu(Composite toolbar) {
		fToolBar= new ToolBar(toolbar, SWT.FLAT);
		ToolItem viewMenuButton= new ToolItem(fToolBar, SWT.PUSH, 0);
		GridData data= new GridData();
		data.horizontalAlignment= GridData.END;
		data.verticalAlignment= GridData.BEGINNING;
		fToolBar.setLayoutData(data);
		viewMenuButton.setImage(PluginImages.get(PluginImages.VIEW_MENU_IMAGE));
		viewMenuButton.setDisabledImage(PluginImages.get(PluginImages.VIEW_MENU_IMAGE));
		viewMenuButton.setToolTipText("View Menu");
		viewMenuButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showViewMenu();
			}
		});
		// Key binding service
		IWorkbenchPart part= RuntimePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IWorkbenchPartSite site= part.getSite();
		fKeyBindingService= site.getKeyBindingService();
		// Remember current scope and then set window context.
		fKeyBindingScopes= fKeyBindingService.getScopes();
		fKeyBindingService.setScopes(new String[] { IContextService.CONTEXT_ID_WINDOW });
		// Create show view menu action
		fShowViewMenuAction= new Action("showViewMenu") { //$NON-NLS-1$
			/*
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				showViewMenu();
			}
		};
		fShowViewMenuAction.setEnabled(true);
		fShowViewMenuAction.setActionDefinitionId("org.eclipse.ui.window.showViewMenu"); //$NON-NLS-1$
		// Register action with command support
		fShowViewMenuHandlerSubmission= new ExecutionListener(fShowViewMenuAction.getActionDefinitionId(), fShowViewMenuAction);
		((ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class)).addExecutionListener(fShowViewMenuHandlerSubmission);
	}

	private class ExecutionListener implements IExecutionListener {
		private final String fCommandDefID;
		private final IAction fAction;
		public ExecutionListener(String cmdDefID, IAction action) {
			fCommandDefID= cmdDefID;
			fAction= action;
		}
		public void notHandled(String commandId, NotHandledException exception) { }
		public void postExecuteFailure(String commandId, ExecutionException exception) { }
		public void postExecuteSuccess(String commandId, Object returnValue) { }
		public void preExecute(String commandId, ExecutionEvent event) {
			fAction.run();
		}
	}

	private MenuManager getViewMenuManager() {
		if (fViewMenuManager == null) {
			fViewMenuManager= new MenuManager();
			fillViewMenu(fViewMenuManager);
		}
		return fViewMenuManager;
	}

	private void showViewMenu() {
		fIsDecativateListenerActive= false;
		Menu aMenu= getViewMenuManager().createContextMenu(fShell);
		Rectangle bounds= fToolBar.getBounds();
		Point topLeft= new Point(bounds.x, bounds.y + bounds.height);
		topLeft= fShell.toDisplay(topLeft);
		aMenu.setLocation(topLeft.x, topLeft.y);
		aMenu.setVisible(true);
	}

	private void createStatusField(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout(1, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Horizontal separator line
		Label separator= new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DOT);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Status field label
		fStatusField= new Label(parent, SWT.RIGHT);
		fStatusField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fStatusField.setText(getStatusFieldText());
		Font font= fStatusField.getFont();
		Display display= parent.getDisplay();
		FontData[] fontDatas= font.getFontData();
		for(int i= 0; i < fontDatas.length; i++)
			fontDatas[i].setHeight(fontDatas[i].getHeight() * 9 / 10);
		fStatusTextFont= new Font(display, fontDatas);
		fStatusField.setFont(fStatusTextFont);
		fStatusField.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
	}

	protected void updateStatusFieldText() {
		if (fStatusField != null)
			fStatusField.setText(getStatusFieldText());
	}

	/**
	 * Handles click in status field.
	 * <p>
	 * Default does nothing.
	 * </p>
	 */
	protected void handleStatusFieldClicked() {}

	protected String getStatusFieldText() {
		return ""; //$NON-NLS-1$
	}

	private void setInfoSystemColor() {
		Display display= fShell.getDisplay();
		setForegroundColor(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		setBackgroundColor(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	}

	private void installFilter() {
		fFilterText.setText(""); //$NON-NLS-1$
		fFilterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text= ((Text) e.widget).getText();
				int length= text.length();
				if (length > 0 && text.charAt(length - 1) != '*') {
					text= text + '*';
				}
				setMatcherString(text);
			}
		});
	}

	/**
	 * The string matcher has been modified. The default implementation
	 * refreshes the view and selects the first matched element
	 */
	protected void stringMatcherUpdated() {
		// refresh viewer to re-filter
		fTreeViewer.getControl().setRedraw(false);
		fTreeViewer.refresh();
		fTreeViewer.expandAll();
		selectFirstMatch();
		fTreeViewer.getControl().setRedraw(true);
	}

	/**
	 * Sets the patterns to filter out for the receiver.
	 * <p>
	 * The following characters have special meaning:
	 *   ? => any character
	 *   * => any string
	 * </p>
	 *
	 * @param pattern the pattern
	 */
	protected void setMatcherString(String pattern) {
		//TODO: rreenable fitering!!!!
		/*if (pattern.length() == 0) {
	    fStringMatcher= null;
	} else {
	    boolean ignoreCase= pattern.toLowerCase().equals(pattern);
	    fStringMatcher= new StringMatcher(pattern, ignoreCase, false);
	}*/
		stringMatcherUpdated();
	}

	/*protected StringMatcher getMatcher() {
	return fStringMatcher;
    }*/

	protected Object getSelectedElement() {
		if (fTreeViewer == null)
			return null;
		return ((IStructuredSelection) fTreeViewer.getSelection()).getFirstElement();
	}

	private void gotoSelectedElement() {
		Object selectedElement= getSelectedElement();
		if (selectedElement != null) {
			try {
				dispose();
				if (selectedElement instanceof IFile) {
					openInEditor((IFile) selectedElement, true);
				}
				else {
					IWorkbenchPage p= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IEditorPart editor= p.getActiveEditor();
					CeylonEditor ue= (CeylonEditor) editor;
					CeylonParseController parseController= ue.getParseController();
					CeylonSourcePositionLocator locator= parseController.getSourcePositionLocator();
					Object element= selectedElement;

					if (element instanceof CeylonOutlineNode) {
						element= ((CeylonOutlineNode) selectedElement).getASTNode();
					}
					ue.selectAndReveal(locator.getStartOffset(element), 0 /*pos.length*/);
				}
			} 
			catch (CoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
		if (file != null) {
			IWorkbenchPage p= RuntimePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (p != null) {
				IEditorPart editorPart= IDE.openEditor(p, file, activate);
				//		initializeHighlightRange(editorPart);
				return editorPart;
			}
		}
		return null;
	}

	/**
	 * Selects the first element in the tree which
	 * matches the current filter pattern.
	 */
	protected void selectFirstMatch() {
		Tree tree= fTreeViewer.getTree();
		Object element= findElement(tree.getItems());
		if (element != null)
			fTreeViewer.setSelection(new StructuredSelection(element), true);
		else
			fTreeViewer.setSelection(StructuredSelection.EMPTY);
	}

	private Object findElement(TreeItem[] items) {
		ILabelProvider labelProvider= (ILabelProvider) fTreeViewer.getLabelProvider();
		for(int i= 0; i < items.length; i++) {
			Object element= items[i].getData();
			//TODO: reenable filtering!
			/*if (fStringMatcher == null)
		return element;
	    if (element != null) {
		String label= labelProvider.getText(element);
		if (fStringMatcher.match(label))
		    return element;
	    }
	    element= findElement(items[i].getItems());
	    if (element != null)*/
			return element;
		}
		return null;
	}

	public void setInformation(String information) {
		// this method is ignored, see IInformationControlExtension2
	}

	public abstract void setInput(Object information);

	/**
	 * Fills the view menu.
	 * Clients can extend or override.
	 *
	 * @param viewMenu the menu manager that manages the menu
	 * @since 3.0
	 */
	protected void fillViewMenu(IMenuManager viewMenu) {
		viewMenu.add(new GroupMarker("SystemMenuStart")); //$NON-NLS-1$
		viewMenu.add(new MoveAction());
		viewMenu.add(new ResizeAction());
		viewMenu.add(new RememberBoundsAction());
		viewMenu.add(new Separator("SystemMenuEnd")); //$NON-NLS-1$
		//	if (fCustomFiltersActionGroup != null)
		//	    fCustomFiltersActionGroup.fillViewMenu(viewMenu);
	}

	protected void inputChanged(Object newInput, Object newSelection) {
		fFilterText.setText(""); //$NON-NLS-1$
		fTreeViewer.setInput(newInput);
		if (newSelection != null) {
			fTreeViewer.setSelection(new StructuredSelection(newSelection));
		}
	}

	public void setVisible(boolean visible) {
		if (visible || fIsDecativateListenerActive)
			fShell.setVisible(visible);
	}

	public final void dispose() {
		if (fShell != null && !fShell.isDisposed())
			fShell.dispose();
		else
			widgetDisposed(null);
	}

	public void widgetDisposed(DisposeEvent event) {
		if (fStatusTextFont != null && !fStatusTextFont.isDisposed())
			fStatusTextFont.dispose();
		fShell= null;
		fTreeViewer= null;
		fComposite= null;
		fFilterText= null;
		fStatusTextFont= null;
		// Remove handler submission
		((ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class))
		        .removeExecutionListener(fShowViewMenuHandlerSubmission);
		// Restore editor's key binding scope
		if (fKeyBindingScopes != null && fKeyBindingService != null) {
			fKeyBindingService.setScopes(fKeyBindingScopes);
			fKeyBindingScopes= null;
			fKeyBindingService= null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasContents() {
		return fTreeViewer != null && fTreeViewer.getInput() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		if (maxWidth > -1 && maxHeight > -1) {
			GridData gd= new GridData(GridData.FILL_BOTH);
			if (maxWidth > -1)
				gd.widthHint= maxWidth;
			if (maxHeight > -1)
				gd.heightHint= maxHeight;
			fShell.setLayoutData(gd);
		}
	}

	public Point computeSizeHint() {
		return fShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}

	public Rectangle getBounds() {
		return fBounds;
	}

	public boolean restoresLocation() {
		return !getDialogSettings().getBoolean(STORE_DISABLE_RESTORE_LOCATION);
	}

	public boolean restoresSize() {
		return !getDialogSettings().getBoolean(STORE_DISABLE_RESTORE_SIZE);
	}

	public Rectangle computeTrim() {
		if (fTrim != null)
			return fTrim;
		return new Rectangle(0, 0, 0, 0);
	}

	public void setLocation(Point location) {
		fTrim= fShell.computeTrim(0, 0, 0, 0);
		Point compositeLocation= fComposite.getLocation();
		location.x+= fTrim.x - compositeLocation.x;
		location.y+= fTrim.y - compositeLocation.y;
		fShell.setLocation(location);
	}

	public void setSize(int width, int height) {
		fShell.setSize(width, height);
	}

	public void addDisposeListener(DisposeListener listener) {
		fShell.addDisposeListener(listener);
	}

	public void removeDisposeListener(DisposeListener listener) {
		fShell.removeDisposeListener(listener);
	}

	public void setForegroundColor(Color foreground) {
		fTreeViewer.getTree().setForeground(foreground);
		fFilterText.setForeground(foreground);
		fComposite.setForeground(foreground);
		fViewMenuButtonComposite.setForeground(foreground);
		if (fStatusField != null)
			fStatusField.getParent().setForeground(foreground);
	}

	public void setBackgroundColor(Color background) {
		fTreeViewer.getTree().setBackground(background);
		fFilterText.setBackground(background);
		fComposite.setBackground(background);
		fViewMenuButtonComposite.setBackground(background);
		if (fStatusField != null) {
			fStatusField.setBackground(background);
			fStatusField.getParent().setBackground(background);
		}
		if (fViewMenuButton != null)
			fViewMenuButton.setBackground(background);
		if (fToolBar != null)
			fToolBar.setBackground(background);
	}

	public boolean isFocusControl() {
		return fTreeViewer.getControl().isFocusControl() || fFilterText.isFocusControl();
	}

	public void setFocus() {
		fShell.forceFocus();
		fFilterText.setFocus();
	}

	public void addFocusListener(FocusListener listener) {
		fShell.addFocusListener(listener);
	}

	public void removeFocusListener(FocusListener listener) {
		fShell.removeFocusListener(listener);
	}

	final protected Command getInvokingCommand() {
		return fInvokingCommand;
	}

	// RMF 6/1/2006 - don't know how to do this without using deprecated API, so disabled for now...
	//    final protected KeySequence[] getInvokingCommandKeySequences() {
	//	if (fInvokingCommandKeySequences == null) {
	//	    if (getInvokingCommand() != null) {
	//		List list= getInvokingCommand().getKeySequenceBindings();
	//		if (!list.isEmpty()) {
	//		    fInvokingCommandKeySequences= new KeySequence[list.size()];
	//		    for(int i= 0; i < fInvokingCommandKeySequences.length; i++) {
	//			fInvokingCommandKeySequences[i]= ((IKeySequenceBinding) list.get(i)).getKeySequence();
	//		    }
	//		    return fInvokingCommandKeySequences;
	//		}
	//	    }
	//	}
	//	return fInvokingCommandKeySequences;
	//    }

	protected IDialogSettings getDialogSettings() {
		String sectionName= getId();
		IDialogSettings settings= RuntimePlugin.getInstance().getDialogSettings().getSection(sectionName);
		if (settings == null)
			settings= RuntimePlugin.getInstance().getDialogSettings().addNewSection(sectionName);
		return settings;
	}
}
