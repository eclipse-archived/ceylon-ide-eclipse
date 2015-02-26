package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectDeclaredSourceModules;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_COMPLETE_DESCRIPTION;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getAllMethods;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getProjects;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getWorkspaceRoot;
import static com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil.isTestable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.internal.corext.util.Strings;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jdt.internal.ui.viewsupport.FilteredElementTreeSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.complete.CodeCompletions;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestMessages;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.util.MethodWithContainer;

@SuppressWarnings("restriction")
public class CeylonTestSelectionDialog extends FilteredElementTreeSelectionDialog {

    private static final java.lang.Class<?>[] ACCEPTED_TYPES = { IProject.class, Module.class, Package.class, Declaration.class, MethodWithContainer.class };

    private final CeylonTestSelectionDialogLabelProvider labelProvider;
    private Composite buttonPanel;
    private Button buttonShowCompleteDescription;

    public CeylonTestSelectionDialog(Shell parent) {
        this(parent, 
                new CeylonTestSelectionDialogLabelProvider(true), 
                new CeylonTestSelectionDialogContentProvider());
    }

    public CeylonTestSelectionDialog(Shell parent, CeylonTestSelectionDialogLabelProvider labelProvider, CeylonTestSelectionDialogContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);

        this.labelProvider = labelProvider;

        setTitle(CeylonTestMessages.testSelectDialogTitle);
        setMessage(CeylonTestMessages.testSelectDialogMessage);
        setHelpAvailable(false);
        setInput(getWorkspaceRoot());
        setComparator(new ViewerComparator(Collator.getInstance()));
        setValidator(new TypedElementSelectionValidator(ACCEPTED_TYPES, true) {
            @Override
            protected boolean isSelectedValid(Object element) {
                return isTestable(element);
            }
        });
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        
        createButtonPanel(composite);
        createButtonShowCompleteDescription();
        loadSettings();
        getTreeViewer().addFilter(new CeylonTestSelectionDialogViewerFilter());
        update();
        
        return composite;
    }

    private void createButtonPanel(Composite composite) {
        buttonPanel = new Composite(composite, SWT.NONE);
        buttonPanel.setLayout(new GridLayout(1, false));
        buttonPanel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
    }

    private void createButtonShowCompleteDescription() {
        buttonShowCompleteDescription = new Button(buttonPanel, SWT.CHECK);
        buttonShowCompleteDescription.setText(CeylonTestMessages.testSelectDialogShowComplateDescription);
        buttonShowCompleteDescription.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                update();
            }
        });
    }

    @Override
    protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
        // copy from FilteredElementTreeSelectionDialog, but without FilteredTreeWithFilter, which disabled auto-expansion
        FilteredTree tree = new FilteredTree(parent, style, new PatternFilter(), true);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(tree);
        SWTUtil.setAccessibilityText(tree.getViewer().getControl(), Strings.removeMnemonicIndicator(getMessage()));
        return tree.getViewer();
    }

    @Override
    public boolean close() {
        saveSettings();
        return super.close();
    }

    @Override
    protected void computeResult() {
        ITreeSelection treeSelection = (ITreeSelection) getTreeViewer().getSelection();
        TreePath[] treePaths = treeSelection.getPaths();

        List<CeylonTestLaunchConfigEntry> result = new ArrayList<CeylonTestLaunchConfigEntry>();
        for(TreePath treePath : treePaths) {
            result.add(CeylonTestLaunchConfigEntry.buildFromTreePath(treePath));
        }

        setResult(result);
    }

    private void loadSettings() {
        IDialogSettings dialogSettings = CeylonTestPlugin.getDefault().getDialogSettings();
        buttonShowCompleteDescription.setSelection(dialogSettings.getBoolean(PREF_SHOW_COMPLETE_DESCRIPTION));
    }

    private void saveSettings() {
        IDialogSettings dialogSettings = CeylonTestPlugin.getDefault().getDialogSettings();
        dialogSettings.put(PREF_SHOW_COMPLETE_DESCRIPTION, buttonShowCompleteDescription.getSelection());
    }

    private void update() {
        labelProvider.setShowCompleteDescription(buttonShowCompleteDescription.getSelection());
        getTreeViewer().refresh();
    }

    public static class CeylonTestSelectionDialogContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getChildren(Object parent) {
            List<?> children = Collections.emptyList();
            if (parent instanceof IWorkspaceRoot) {
                children = getProjects();
            } else if( parent instanceof IProject ) {
                Collection<Module> modules = getProjectDeclaredSourceModules((IProject)parent);
                List<Module> childrenModules = new ArrayList<Module>(modules.size());
                childrenModules.addAll(modules);
                children = childrenModules;
            } else if( parent instanceof Module) {
                children = ((Module) parent).getPackages();
            } else if( parent instanceof Package ) {
                children = ((Package) parent).getMembers();
            } else if( parent instanceof ClassOrInterface ) {
                children = getAllMethods((ClassOrInterface) parent);
            }
            return children.toArray();
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }

        @Override
        public Object[] getElements(Object element) {
            return getChildren(element);
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // noop
        }

        @Override
        public void dispose() {
            // noop
        }

    }

    public static class CeylonTestSelectionDialogLabelProvider extends CeylonLabelProvider {

        public CeylonTestSelectionDialogLabelProvider(boolean includePackage) {
            super(includePackage);
        }

        private boolean showCompleteDescription;

        public boolean isShowCompleteDescription() {
            return showCompleteDescription;
        }

        public void setShowCompleteDescription(boolean showCompleteDescription) {
            this.showCompleteDescription = showCompleteDescription;
        }

        @Override
        public Image getImage(Object element) {
            if (element instanceof Declaration) {
                return getImageForDeclaration((Declaration) element);
            }
            else if (element instanceof MethodWithContainer) {
                return getImageForDeclaration(((MethodWithContainer) element).getMethod());
            }
            return super.getImage(element);
        }

        @Override
        public StyledString getStyledText(Object element) {
            StyledString styledText;

            if (element instanceof Declaration) {
                Declaration declaration = (Declaration) element;
                if (showCompleteDescription) {
                    styledText = CodeCompletions.getStyledDescriptionFor(declaration);
                } else {
                    styledText = new StyledString(declaration.getName());
                }
            } else if (element instanceof MethodWithContainer) {
                Declaration declaration = ((MethodWithContainer) element).getMethod();
                if (showCompleteDescription) {
                    styledText = CodeCompletions.getStyledDescriptionFor(declaration);
                } else {
                    styledText = new StyledString(declaration.getName());
                }
            } else if (element instanceof Package) {
                // default package color from CeylonLabelProvider is gray, we want black
                styledText = new StyledString(getLabel((Package) element));
            } else if (element instanceof Module) {
                // default package color from CeylonLabelProvider is gray, we want black
                styledText = new StyledString(getLabel((Module) element));
            } else {
                styledText = super.getStyledText(element); 
            }

            if( !isTestable(element) ) {
                styledText = new StyledString(styledText.getString(), StyledString.QUALIFIER_STYLER);
            }

            return styledText;
        }

    }

    public static class CeylonTestSelectionDialogViewerFilter extends TypedViewerFilter {

        public CeylonTestSelectionDialogViewerFilter() {
            super(ACCEPTED_TYPES);
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            return isTestable(element);
        }

    }    

}