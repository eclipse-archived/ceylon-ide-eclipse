package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.redhat.ceylon.model.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class MakeReceiverInputPage extends UserInputWizardPage {
    public MakeReceiverInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Value dm = ((Tree.AttributeDeclaration) getMakeReceiverRefactoring().node)
                 .getDeclarationModel();
        new Label(result, SWT.RIGHT).setText("Make '" + 
                 dm.getInitializerParameter().getDeclaration().getName() + 
                 "()' a member of the type '" + 
                 dm.getType().getDeclaration().getName() + 
                "', and remove parameter '" + dm.getName() + "'.");
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=2;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
    }

    private MakeReceiverRefactoring getMakeReceiverRefactoring() {
        return (MakeReceiverRefactoring) getRefactoring();
    }
    
}
