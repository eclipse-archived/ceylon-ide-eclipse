package com.redhat.ceylon.test.eclipse.plugin.testview;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STACK_TRACE;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STACK_TRACE_FILTER;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.STACK_TRACE_LINE;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.stackTraceCopyLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.stackTraceFilterLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages.stackTraceLabel;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_STACK_TRACE_FILTER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement.State;

public class TestStackTracePanel extends Composite {
    
    private static final String[] STACK_TRACE_FILTER_PATTERNS = new String[] {
            "com.redhat.ceylon.test.eclipse.plugin.*",
            "java.lang.reflect.Method.invoke",
            "sun.reflect.*",
    };
    
    private TestElement selectedTestElement;
    private Label panelIcon;
    private Label panelLabel;
    private ToolBar toolBar;
    private ToolBarManager toolBarManager;
    private Table stackTraceTable;
    private StackTraceFilterAction stackTraceFilterAction;
    private StackTraceCopyAction stackTraceCopyAction;

    public TestStackTracePanel(Composite parent) {
        super(parent, SWT.NONE);
        
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginTop = 10;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        setLayout(gridLayout);
        
        createHeader();
        createToolBar();
        createStackTraceTable();
    }

    public void setSelectedTestElement(TestElement selectedTestElement) {
        this.selectedTestElement = selectedTestElement;
        updateStackTrace();
    }

    private void createHeader() {
        panelIcon = new Label(this, SWT.NONE);
        panelIcon.setImage(getImage(STACK_TRACE));
        panelLabel = new Label(this, SWT.NONE);
        panelLabel.setText(stackTraceLabel);
    }

    private void createToolBar() {
        stackTraceFilterAction = new StackTraceFilterAction();
        stackTraceCopyAction = new StackTraceCopyAction();
        
        toolBar = new ToolBar(this, SWT.FLAT | SWT.WRAP);
        toolBar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).create());
        toolBarManager = new ToolBarManager(toolBar);
        toolBarManager.add(stackTraceFilterAction);
        toolBarManager.add(stackTraceCopyAction);
        toolBarManager.update(true);
    }

    private void createStackTraceTable() {
        stackTraceTable = new Table(this, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
        stackTraceTable.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).align(SWT.FILL, SWT.FILL).grab(true, true).create());
    }

    private void createStackTraceLines(String stackTrace) {
        StringReader stringReader = new StringReader(stackTrace);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        try {
            String line;
            boolean isFirstLine = true;
            while ((line = bufferedReader.readLine()) != null) {
                if( !isStackTraceLineFiltred(isFirstLine, line) ) {
                    createStackTraceLine(isFirstLine, line);
                }
                if( isFirstLine ) {
                    isFirstLine = false;
                }
            }
        } catch (IOException e) {
            CeylonTestPlugin.logError("", e);
        }
    }

    private void createStackTraceLine(boolean isFirstLine, String line) {
        String text = line.replace("\t", "  ");
    
        Image image = null;
        if (isFirstLine || text.startsWith("Caused by: ")) {
            if (selectedTestElement.getState() == State.FAILURE) {
                image = getImage(TEST_FAILED);
            } else {
                image = getImage(TEST_ERROR);
            }
        } else if (text.startsWith("  at ")) {
            image = getImage(STACK_TRACE_LINE);
        }
    
        TableItem tableItem = new TableItem(stackTraceTable, SWT.NONE);
        tableItem.setText(text);
        tableItem.setImage(image);
    }

    private void updateStackTrace() {
        stackTraceTable.setRedraw(false);
        stackTraceTable.removeAll();
        boolean stackTraceAvailable = isStackTraceAvailable();
        if (stackTraceAvailable) {
            createStackTraceLines(selectedTestElement.getException());
        }
        stackTraceCopyAction.setEnabled(stackTraceAvailable);
        stackTraceTable.setRedraw(true);
    }

    private boolean isStackTraceAvailable() {
        if (selectedTestElement != null &&
                selectedTestElement.getException() != null &&
                (selectedTestElement.getState() == State.FAILURE || selectedTestElement.getState() == State.ERROR)) {
            return true;
        }        
        return false;
    }

    private boolean isStackTraceLineFiltred(boolean isFirstLine, String line) {
        if( !isFirstLine && stackTraceFilterAction.isChecked() ) {
            for (String pattern : STACK_TRACE_FILTER_PATTERNS) {
                if (pattern.charAt(pattern.length() - 1) == '*') {
                    // strip trailing * from a package filter
                    pattern = pattern.substring(0, pattern.length() - 1);
                } else if (Character.isUpperCase(pattern.charAt(0))) {
                    // class in the default package
                    pattern = "at " + pattern + '.';
                } else {
                    // class names start w/ an uppercase letter after the .
                    int lastDotIndex = pattern.lastIndexOf('.');
                    if ((lastDotIndex != -1)
                            && (lastDotIndex != pattern.length() - 1)
                            && Character.isUpperCase(pattern.charAt(lastDotIndex + 1))) {
                        pattern += '.'; // append . to a class filter
                    }
                }
                if (line.indexOf(pattern) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private class StackTraceFilterAction extends Action {
    
        public StackTraceFilterAction() {
            super(stackTraceFilterLabel, AS_CHECK_BOX);
            setDescription(stackTraceFilterLabel);
            setToolTipText(stackTraceFilterLabel);
            setImageDescriptor(CeylonTestImageRegistry.getImageDescriptor(STACK_TRACE_FILTER));
    
            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            setChecked(preferenceStore.getBoolean(PREF_STACK_TRACE_FILTER));
        }
    
        @Override
        public void run() {
            IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();
            preferenceStore.setValue(PREF_STACK_TRACE_FILTER, isChecked());
    
            updateStackTrace();
        }
    
    }

    private class StackTraceCopyAction extends Action {
        
        public StackTraceCopyAction() {
            super(stackTraceCopyLabel);
            setDescription(stackTraceCopyLabel);
            setToolTipText(stackTraceCopyLabel);
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
            setEnabled(false);
        }
        
        @Override
        public void run() {
            if( isStackTraceAvailable() ) {
                String stackTrace = selectedTestElement.getException();
                
                Clipboard clipboard = new Clipboard(getDisplay());
                clipboard.setContents(new String[] { stackTrace }, new Transfer[] { TextTransfer.getInstance() });
                clipboard.dispose();                
            }
        }
        
    }

}