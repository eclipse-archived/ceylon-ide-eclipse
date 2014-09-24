package com.redhat.ceylon.eclipse.code.refactor;


import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoFile;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.search.CeylonElement;
import com.redhat.ceylon.eclipse.code.search.CeylonSearchMatch;
import com.redhat.ceylon.eclipse.code.search.CeylonViewerComparator;
import com.redhat.ceylon.eclipse.code.search.SearchResultsLabelProvider;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class DeleteInputPage extends UserInputWizardPage {
    public DeleteInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        final DeleteRefactoring refactoring = getDeleteRefactoring();
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        result.setLayout(layout);
        
        Declaration dec = refactoring.getDeclaration();
        setDescription("Safely delete '" + dec.getName() + 
                "' which is referenced in " + refactoring.getCount() + 
                " places.");
//        Label title = new Label(result, SWT.LEFT);  
//        title.setText("Delete '" + dec.getName() + 
//                "' which is referenced in " + refactoring.getCount() + 
//                " places.");
//        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
//        gd2.horizontalSpan=2;
//        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
        
        final Button et = new Button(result, SWT.CHECK);
        et.setEnabled(dec.isShared() && dec.isClassOrInterfaceMember() || dec.isParameter());
        et.setText("Also delete arguments/refinements");
        
        Composite composite = new Composite(result, SWT.NONE);
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        cgd.grabExcessHorizontalSpace = true;
        cgd.grabExcessVerticalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout tableLayout = new GridLayout(3, true);
        tableLayout.marginWidth=0;
        composite.setLayout(tableLayout);
        
        final TableViewer table = new TableViewer(composite, 
                SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        table.setLabelProvider(new SearchResultsLabelProvider() {
            @Override
            public StyledString getStyledText(Object element) {
                return super.getStyledText(((CeylonSearchMatch) element).getElement());
            }
            @Override
            public Image getImage(Object element) {
                return super.getImage(((CeylonSearchMatch) element).getElement());
            }
        });
        table.setContentProvider(ArrayContentProvider.getInstance());
        table.setComparator(new CeylonViewerComparator());
        table.setInput(refactoring.getReferences());
        
        GridData tgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        tgd.horizontalSpan=3;
        tgd.verticalSpan=4;
        tgd.grabExcessHorizontalSpace = true;
        tgd.heightHint = 100;
        tgd.widthHint = 250;
        table.getTable().setLayoutData(tgd);
        
        table.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object obj = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (obj instanceof CeylonSearchMatch) {
                    CeylonSearchMatch match = (CeylonSearchMatch) obj;
                    CeylonElement element = (CeylonElement) match.getElement();
                    gotoFile(element.getFile(), match.getOffset(), match.getLength());
                }
            }
        });
        
        final Table warnings = new Table(result, SWT.NO_SCROLL|SWT.NO_FOCUS|SWT.NO_BACKGROUND);
        warnings.setBackground(result.getBackground());
        showWarnings(warnings);

        et.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                refactoring.setDeleteRefinements();
                table.setInput(refactoring.getReferences());
                showWarnings(warnings);
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        
    }

    private void showWarnings(Table table) {
        table.removeAll();
        DeleteRefactoring refactoring = getDeleteRefactoring();
        Declaration dec = refactoring.getDeclaration();
        Declaration rdec = refactoring.getRefinedDeclaration();
        String name = dec.getName();
        int usages = refactoring.countUsages();
        if (usages>0) {
            addWarning(table, "There are " + usages + 
                    " usages of '" + name + "'");
        }
        int refinements = refactoring.countRefinements();
        if (/*dec.isActual() &&*/ rdec!=null && !rdec.equals(dec)) {
            if (rdec.isFormal() && !dec.isFormal()) {
                addWarning(table, "This declaration refines formal member '" + 
                        rdec.getName() + "' declared by '" +
                        ((Declaration) rdec.getContainer()).getName() + "'");
                refinements--;
            }
            else {
                addWarning(table, "This declaration refines member '" + 
                        rdec.getName() + "' declared by '" +
                        ((Declaration) rdec.getContainer()).getName() + "'");
            }
        }
        if (refinements>0) {
            addWarning(table, "There are " + refinements + 
                    " refinements of member '" + name + "'");
        }
        if (isPublic(dec)) {
        	addWarning(table, "This declaration is visible outside of the module '" +
        			dec.getUnit().getPackage().getModule().getNameAsString() + "'");
        }
    }

	private static boolean isPublic(Declaration dec) {
		return dec.isShared() && dec.getUnit().getPackage().isShared() && 
        		(dec.isToplevel() || 
        				dec.isClassOrInterfaceMember() && 
        				isPublic((Declaration)dec.getContainer()));
	}

    private void addWarning(Table table, String text) {
        TableItem item = new TableItem(table, SWT.NONE);
        item.setImage(CeylonResources.WARNING);
        item.setText(text);
    }
    
    private DeleteRefactoring getDeleteRefactoring() {
        return (DeleteRefactoring) getRefactoring();
    }

}
