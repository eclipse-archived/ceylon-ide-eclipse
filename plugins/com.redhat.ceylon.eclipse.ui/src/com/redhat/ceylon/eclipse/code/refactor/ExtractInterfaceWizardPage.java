package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

public class ExtractInterfaceWizardPage extends UserInputWizardPage {

    private ExtractInterfaceRefactoring refactoring;
    private Tree.Declaration[] extractableMembers;
    private Label interfaceNameLabel;
    private Text interfaceNameText;
    private Button btnSelectAll;
    private Button btnDeselectAll;
    private CheckboxTableViewer tableViewer;

    public ExtractInterfaceWizardPage(ExtractInterfaceRefactoring refactoring) {
        super(refactoring.getName());
        this.refactoring = refactoring;
        this.extractableMembers = refactoring.extractableMembers;
    }

    @Override
    public void createControl(Composite parent) {
        setPageComplete(false);
        initializeDialogUnits(parent);

        Composite page = new Composite(parent, SWT.NONE);
        page.setLayout(new GridLayout(2, false));
        setControl(page);

        interfaceNameLabel = new Label(page, SWT.RIGHT);
        interfaceNameLabel.setText("Interface name: ");

        interfaceNameText = new Text(page, SWT.SINGLE | SWT.BORDER);
        interfaceNameText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        interfaceNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                update();
            }
        });

        Label separator = new Label(page, SWT.NONE);
        separator.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());

        Label tableLabel = new Label(page, SWT.NONE);
        tableLabel.setText("Members to declare in the interface:");
        tableLabel.setEnabled(extractableMembers.length > 0);
        tableLabel.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());

        createExtracableMembersTable(page);
    }

    private void createExtracableMembersTable(Composite page) {
        Composite composite = new Composite(page, SWT.NONE);
        composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).create());
        composite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).span(2, 1).hint(SWT.DEFAULT, convertHeightInCharsToPixels(12)).create());

        tableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        tableViewer.setLabelProvider(new CeylonLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(extractableMembers);
        tableViewer.getControl().setEnabled(extractableMembers.length > 0);
        tableViewer.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                Declaration d1 = ((Tree.Declaration) e1).getDeclarationModel();
                Declaration d2 = ((Tree.Declaration) e2).getDeclarationModel();
                return d1.getName().compareTo(d2.getName());
            }
        });
        tableViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                update();
            }
        });

        createButtons(composite);
    }

    private void createButtons(Composite composite) {
        Composite buttonComposite = new Composite(composite, SWT.NONE);
        buttonComposite.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).create());
        buttonComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).grab(false, true).create());

        btnSelectAll = new Button(buttonComposite, SWT.PUSH);
        btnSelectAll.setText("Select All");
        btnSelectAll.setEnabled(extractableMembers.length > 0);
        btnSelectAll.setLayoutData(new GridData());
        btnSelectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tableViewer.setAllChecked(true);
                update();
            }
        });

        btnDeselectAll = new Button(buttonComposite, SWT.PUSH);
        btnDeselectAll.setText("Deselect All");
        btnDeselectAll.setEnabled(extractableMembers.length > 0);
        btnDeselectAll.setLayoutData(new GridData());
        btnDeselectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tableViewer.setAllChecked(false);
                update();
            }
        });

        SWTUtil.setButtonDimensionHint(btnSelectAll);
        SWTUtil.setButtonDimensionHint(btnDeselectAll);
    }

    private void update() {
        String interfaceName = interfaceNameText.getText();

        Tree.TypedDeclaration[] checkedMembers = new Tree.TypedDeclaration[tableViewer.getCheckedElements().length];
        for (int i = 0; i < tableViewer.getCheckedElements().length; i++) {
            checkedMembers[i] = (Tree.TypedDeclaration) tableViewer.getCheckedElements()[i];
        }

        if (interfaceName.isEmpty()) {
            setPageComplete(false);
            return;
        }
        if (checkedMembers.length == 0) {
            setPageComplete(false);
            return;
        }
        setPageComplete(true);

        refactoring.newInterfaceName = interfaceName;
        refactoring.extractedMembers = checkedMembers;
    }

}
