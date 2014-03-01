package com.redhat.ceylon.eclipse.code.refactor;


import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.search.CeylonElement;
import com.redhat.ceylon.eclipse.code.search.CeylonSearchMatch;
import com.redhat.ceylon.eclipse.code.search.CeylonViewerComparator;

public class DeleteInputPage extends UserInputWizardPage {
    public DeleteInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        int count = getDeleteRefactoring().getCount();
        
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        result.setLayout(layout);
        
        Label title = new Label(result, SWT.LEFT);  
        Declaration dec = getDeleteRefactoring().getDeclaration();
        title.setText("Delete '" + dec.getName() + 
                "' which is referenced in " + count + 
                " places.");
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=2;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
        
        Composite composite = new Composite(result, SWT.NONE);
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        cgd.grabExcessHorizontalSpace = true;
        cgd.grabExcessVerticalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout tableLayout = new GridLayout(3, true);
        tableLayout.marginWidth=0;
        composite.setLayout(tableLayout);
        
        TableViewer table = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        table.setLabelProvider(new CeylonLabelProvider(true) {
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
        table.setInput(getDeleteRefactoring().getReferences());
        
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
                    IPath path = element.getFile().getFullPath();
                    gotoLocation(path, match.getOffset(), match.getLength());
                }
            }
        });
        
        Declaration declaration = getDeleteRefactoring().getDeclaration();
        Declaration refinedDeclaration = getDeleteRefactoring().getRefinedDeclaration();
        if (count>0) {
            addWarning(result, "There are " + count + " references to '" + 
                    declaration.getName() + "'");
        }
        if (declaration.isActual() && refinedDeclaration!=null) {
            addWarning(result, "This declaration refines '" + 
                    refinedDeclaration.getName() + "' declared by '" +
                    ((Declaration) refinedDeclaration.getContainer()).getName() + "'");
        }
    }

    private void addWarning(Composite result, String text) {
        Composite warn = new Composite(result, SWT.NONE);
        GridLayout l = new GridLayout();
        l.numColumns = 2;
        warn.setLayout(l);
        Label icon = new Label(warn, SWT.LEFT);
        icon.setImage(CeylonLabelProvider.WARNING);
        Label label = new Label(warn, SWT.LEFT);
        label.setText(text);
    }
    
    private DeleteRefactoring getDeleteRefactoring() {
        return (DeleteRefactoring) getRefactoring();
    }

}
