package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_ENTRIES_KEY;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;

public class CeylonTestLaunchConfigTab extends AbstractLaunchConfigurationTab {

    private List<CeylonTestLaunchConfigEntry> entries = new ArrayList<CeylonTestLaunchConfigEntry>();
    private Group testGroup;
    private TableViewer testViewer;
    private Button buttonAdd;
    private Button buttonRemove;
    private Button buttonUp;
    private Button buttonDown;

    @Override
    public String getName() {
        return CeylonTestMessages.configTabName;
    }

    @Override
    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));
        setControl(comp);

        createTestGroup(comp);
        createTestViewer();
        createButtonAdd();
        createButtonRemove();
        createButtonUp();
        createButtonDown();

        Dialog.applyDialogFont(comp);
        validate();
    }

    private void createTestGroup(Composite comp) {
        testGroup = new Group(comp, SWT.SHADOW_ETCHED_IN);
        testGroup.setText(CeylonTestMessages.configTabTestGroupLabel);
        testGroup.setLayout(new GridLayout(2, false));
        testGroup.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
    }

    private void createTestViewer() {
        testViewer = new TableViewer(testGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        final TableViewerColumn tableViewerColumn = new TableViewerColumn(testViewer, SWT.NONE);
        tableViewerColumn.getColumn().setText(CeylonTestMessages.configTabTestColumnLabel);
        tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                CeylonTestLaunchConfigEntry entry = (CeylonTestLaunchConfigEntry) cell.getElement();

                Image image = null;
                if (entry.isValid()) {
                    switch(entry.getType()) {
                    case PROJECT: image = CeylonLabelProvider.PROJECT; break;
                    case MODULE: image = CeylonLabelProvider.MODULE; break;
                    case PACKAGE: image = CeylonLabelProvider.PACKAGE; break;
                    case CLASS: image = CeylonLabelProvider.CLASS; break;
                    case CLASS_LOCAL: image = CeylonLabelProvider.LOCAL_CLASS; break;
                    case METHOD: image = CeylonLabelProvider.METHOD; break;
                    case METHOD_LOCAL: image = CeylonLabelProvider.LOCAL_METHOD; break;
                    }
                } else {
                    image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
                }

                StyledString styledText = new StyledString();
                if (entry.getType() == CeylonTestLaunchConfigEntry.Type.PROJECT) {
                    styledText.append(entry.getProjectName());
                } else {
                    styledText.append(entry.getModPkgDeclName());
                    styledText.append(CeylonTestMessages.inProjectPrefix, StyledString.COUNTER_STYLER);
                    styledText.append(entry.getProjectName(), StyledString.COUNTER_STYLER);
                }
                if (!entry.isValid()) {
                    styledText.setStyle(0, styledText.length(), StyledString.QUALIFIER_STYLER);
                }

                cell.setText(styledText.toString());
                cell.setStyleRanges(styledText.getStyleRanges());
                cell.setImage(image);

                super.update(cell);
            }

            @Override
            public String getToolTipText(Object element) {
                CeylonTestLaunchConfigEntry entry = (CeylonTestLaunchConfigEntry) element;
                if (!entry.isValid()) {
                    return entry.getErrorMessage();
                }
                return null;
            }

        });

        testViewer.getTable().setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(1, 5).create());
        testViewer.setContentProvider(ArrayContentProvider.getInstance());
        testViewer.setInput(entries);
        testViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateButtonState();
            }
        });
        testViewer.getTable().addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                tableViewerColumn.getColumn().setWidth(testViewer.getTable().getClientArea().width);
            }
        });
    }

    private void createButtonAdd() {
        buttonAdd = new Button(testGroup, SWT.PUSH);
        buttonAdd.setText(CeylonTestMessages.add);
        buttonAdd.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(100, SWT.DEFAULT).create());
        buttonAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CeylonTestSelectionDialog dlg = new CeylonTestSelectionDialog(getShell());
                if (dlg.open() == Dialog.OK) {
                    Object[] results = dlg.getResult();
                    for (Object result : results) {
                        entries.add((CeylonTestLaunchConfigEntry) result);
                        validate();
                    }
                }
            }
        });
    }

    private void createButtonRemove() {
        buttonRemove = new Button(testGroup, SWT.PUSH);
        buttonRemove.setText(CeylonTestMessages.remove);
        buttonRemove.setEnabled(false);
        buttonRemove.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        buttonRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) testViewer.getSelection();
                for (Object entry : selection.toArray()) {
                    entries.remove(entry);
                }
                validate();
            }
        });
    }

    private void createButtonUp() {
        buttonUp = new Button(testGroup, SWT.PUSH);
        buttonUp.setText(CeylonTestMessages.moveUp);
        buttonUp.setEnabled(false);
        buttonUp.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 10).create());
        buttonUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = testViewer.getTable().getSelectionIndex();
                if (selectionIndex > 0) {
                    Collections.swap(entries, selectionIndex, selectionIndex - 1);
                    testViewer.setSelection(new StructuredSelection(entries.get(selectionIndex - 1)));
                    validate();
                }
            }
        });
    }

    private void createButtonDown() {
        buttonDown = new Button(testGroup, SWT.PUSH);
        buttonDown.setText(CeylonTestMessages.moveDown);
        buttonDown.setEnabled(false);
        buttonDown.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        buttonDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = testViewer.getTable().getSelectionIndex();
                if (selectionIndex != -1 && selectionIndex < entries.size() - 1) {
                    Collections.swap(entries, selectionIndex, selectionIndex + 1);
                    testViewer.setSelection(new StructuredSelection(entries.get(selectionIndex + 1)));
                    validate();
                }
            }
        });
    }

    private void update() {
        testViewer.refresh();
        updateButtonState();
        getLaunchConfigurationDialog().updateMessage();
        getLaunchConfigurationDialog().updateButtons();
    }

    private void updateButtonState() {
        int selectionIndex = testViewer.getTable().getSelectionIndex();
        buttonRemove.setEnabled(selectionIndex != -1);
        buttonUp.setEnabled(selectionIndex > 0);
        buttonDown.setEnabled(selectionIndex != -1 && selectionIndex < entries.size() - 1);
    }

    private void validate() {
        String errorMessage = validateNoTests();
        if (errorMessage == null) {
            errorMessage = validateEntries();
        }
        setErrorMessage(errorMessage);
        update();
    }

    private String validateNoTests() {
        if (entries.isEmpty()) {
            return CeylonTestMessages.errorNoTests;
        }
        return null;
    }

    private String validateEntries() {
        for (CeylonTestLaunchConfigEntry entry : entries) {
            entry.validate();
            if (!entry.isValid()) {
                return entry.getErrorMessage();
            }
        }
        return null;
    }
    
    @Override
    public boolean isValid(ILaunchConfiguration config) {
        return getErrorMessage() == null;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy config) {
    }

    @Override
    public void initializeFrom(ILaunchConfiguration config) {
        try {
            entries.clear();
            entries.addAll(CeylonTestLaunchConfigEntry.buildFromLaunchConfig(config));
            validate();
        } catch (CoreException e) {
            CeylonTestPlugin.logError("", e);
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy config) {
        if( !entries.isEmpty() ) {
            config.setAttribute(ATTR_PROJECT_NAME, entries.get(0).getProjectName());
            config.setAttribute(LAUNCH_CONFIG_ENTRIES_KEY, CeylonTestLaunchConfigEntry.buildLaunchConfigAttributes(entries));
        }
    }

}