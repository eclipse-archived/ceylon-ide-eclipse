package com.redhat.ceylon.eclipse.imp.hierarchy;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getCompilationUnit;
import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.imp.editor.EditorAnnotationService.getRefinedDeclaration;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.getDescriptionFor;
import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;

public class HierarchyPopup extends PopupDialog {

    private Declaration declaration;
    private final CeylonEditor editor;
    final boolean isMember;
    final private Map<Declaration, Declaration> subtypesOfSupertypes = new HashMap<Declaration, Declaration>();
    final private Map<Declaration, Set<Declaration>> subtypesOfAllTypes = new HashMap<Declaration, Set<Declaration>>();
    
    public HierarchyPopup(Declaration declaration, CeylonEditor editor, Shell parent) {
        super(parent, INFOPOPUPRESIZE_SHELLSTYLE, true, true, true, true, false,
                "Hierarchy of '" + getDescriptionFor(declaration) + "'", null);
        this.editor = editor;
        isMember = !(declaration instanceof TypeDeclaration);
        while (declaration!=null) {
            this.declaration = declaration;
            Declaration sd;
            if (declaration instanceof TypeDeclaration) {
                sd = ((TypeDeclaration) declaration).getExtendedTypeDeclaration();
            }
            else if (declaration instanceof TypedDeclaration){
                sd = getRefinedDeclaration(declaration);
            }
            else {
                sd = null;
            }
            subtypesOfSupertypes.put(sd, declaration);
            declaration = sd;
        }
        Modules modules = editor.getParseController().getTypeChecker()
                .getContext().getModules();
        for (Module m: modules.getListOfModules()) {
            for (Package p: new ArrayList<Package>(m.getPackages())) { //workaround CME
                for (Unit u: p.getUnits()) {
                    for (Declaration d: u.getDeclarations()) {
                        if (d instanceof ClassOrInterface) {
                            TypeDeclaration td = (TypeDeclaration) d;
                            ClassOrInterface etd = td.getExtendedTypeDeclaration();
                            if (etd!=null) {
                                Set<Declaration> list = subtypesOfAllTypes.get(etd);
                                if (list==null) {
                                    list = new HashSet<Declaration>();
                                    subtypesOfAllTypes.put(etd, list);
                                }
                                list.add(td);
                            }
                            for (TypeDeclaration std: td.getSatisfiedTypeDeclarations()) {
                                Set<Declaration> list = subtypesOfAllTypes.get(std);
                                if (list==null) {
                                    list = new HashSet<Declaration>();
                                    subtypesOfAllTypes.put(std, list);
                                }
                                list.add(td);
                            }
                        }
                        else if (d instanceof TypedDeclaration) {
                            Declaration rd = getRefinedDeclaration(d);
                            if (rd!=null) {
                                Set<Declaration> list = subtypesOfAllTypes.get(rd);
                                if (list==null) {
                                    list = new HashSet<Declaration>();
                                    subtypesOfAllTypes.put(rd, list);
                                }
                                list.add(d);
                            }
                        }
                    }
                }
            }
        }

    }
    
    @Override
    protected void adjustBounds() {
        Rectangle bounds = getShell().getBounds();
        int h = bounds.height;
        if (h>400) {
            bounds.height=400;
            bounds.y = bounds.y + (h-400)/3;
            getShell().setBounds(bounds);
        }
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        final Tree tree = new Tree(composite, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gd= new GridData(GridData.FILL_BOTH);
        //gd.heightHint= tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        final TreeViewer treeViewer = new TreeViewer(tree);
        final Object root = new Object();
        treeViewer.setContentProvider(new ITreeContentProvider() {
            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
            @Override
            public void dispose() {}
            @Override
            public boolean hasChildren(Object element) {
                return getChildren(element).length>0;
            }
            @Override
            public Object getParent(Object element) {
                return null;
            }
            @Override
            public Object[] getElements(Object inputElement) {
                //return new Object[] { inputElement };
                return getChildren(inputElement);
            }
            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement==root) {
                    return new Object[] { declaration };
                }
                Declaration sd = subtypesOfSupertypes.get(parentElement);
                if (sd!=null) {
                    return new Object[] { sd };
                }
                else {
                    Set<Declaration> sdl = subtypesOfAllTypes.get(parentElement);
                    if (sdl==null) {
                        return new Object[0];
                    }
                    else {
                        return sdl.toArray();
                    }
                }
            }
        });
        treeViewer.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void removeListener(ILabelProviderListener listener) {}
            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }
            @Override
            public void dispose() {}
            @Override
            public void addListener(ILabelProviderListener listener) {}
            
            StyledString getStyledText(Object element) {
                Declaration d = getDisplayedDeclaration(element);
                StyledString result = new StyledString(getDescriptionFor(d));
                if (d.getContainer() instanceof Declaration) {
                    result.append(" in ")
                            .append(getDescriptionFor((Declaration) d.getContainer()));
                }
                result.append(" - ", QUALIFIER_STYLER)
                        .append(CeylonLabelProvider.getPackageLabel(d), QUALIFIER_STYLER);
                return result;
            }
            
            Declaration getDisplayedDeclaration(Object element) {
                Declaration d = (Declaration) element;
                if (isMember && d.isClassOrInterfaceMember()) {
                    d = (ClassOrInterface) d.getContainer();
                }
                return d;
            }
            
            @Override
            public void update(ViewerCell cell) {
            	Object element = cell.getElement();
            	
            	StyledString styledText = getStyledText(element);
            	
            	cell.setText(styledText.toString());
            	cell.setStyleRanges(styledText.getStyleRanges());
            	cell.setImage(CeylonLabelProvider.getImage(getDisplayedDeclaration(element)));
            	super.update(cell);
            }
            
        });
        treeViewer.setInput(root);
        treeViewer.expandToLevel(6);
        
        tree.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {}
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                gotoSelectedElement((IStructuredSelection) treeViewer.getSelection());
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {

                if (tree.getSelectionCount() < 1)
                    return;

                if (e.button != 1)
                    return;

                if (tree.equals(e.getSource())) {
                    Object o= tree.getItem(new Point(e.x, e.y));
                    TreeItem selection= tree.getSelection()[0];
                    if (selection.equals(o)) {
                        gotoSelectedElement((IStructuredSelection) treeViewer.getSelection());
                    }
                }
            }
        });

        tree.addMouseMoveListener(new MouseMoveListener()    {
            TreeItem fLastItem= null;
            public void mouseMove(MouseEvent e) {
                if (tree.equals(e.getSource())) {
                    Object o= tree.getItem(new Point(e.x, e.y));
                    if (fLastItem == null ^ o == null) {
                        tree.setCursor(o == null ? null : tree.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
                    }
                    if (o instanceof TreeItem) {
                        Rectangle clientArea = tree.getClientArea();
                        if (!o.equals(fLastItem)) {
                            fLastItem= (TreeItem)o;
                            tree.setSelection(new TreeItem[] { fLastItem });
                        } else if (e.y - clientArea.y < tree.getItemHeight() / 4) {
                            // Scroll up
                            Point p= tree.toDisplay(e.x, e.y);
                            Item item= treeViewer.scrollUp(p.x, p.y);
                            if (item instanceof TreeItem) {
                                fLastItem= (TreeItem)item;
                                tree.setSelection(new TreeItem[] { fLastItem });
                            }
                        } else if (clientArea.y + clientArea.height - e.y < tree.getItemHeight() / 4) {
                            // Scroll down
                            Point p= tree.toDisplay(e.x, e.y);
                            Item item= treeViewer.scrollDown(p.x, p.y);
                            if (item instanceof TreeItem) {
                                fLastItem= (TreeItem)item;
                                tree.setSelection(new TreeItem[] { fLastItem });
                            }
                        }
                    } else if (o == null) {
                        fLastItem= null;
                    }
                }
            }
        });
        
        return composite;
    }

    protected void gotoSelectedElement(IStructuredSelection selection) {
    	if (editor.getParseController()!=null) {
	        Object object = selection.getFirstElement();
	        Declaration dec = (Declaration) object;
	        gotoNode(getReferencedNode(dec, getCompilationUnit(editor.getParseController(), dec)), 
	                editor.getParseController().getTypeChecker());
    	}
        close();
    }
    
}
