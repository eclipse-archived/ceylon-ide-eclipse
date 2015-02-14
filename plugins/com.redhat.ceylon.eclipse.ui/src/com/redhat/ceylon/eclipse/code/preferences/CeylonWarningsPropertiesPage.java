package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getSuppressedWarnings;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.showWarnings;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.PropertyPage;

import com.redhat.ceylon.compiler.typechecker.analyzer.Warning;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonWarningsPropertiesPage extends PropertyPage {
    
    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.warnings";
    
    private boolean showCompilerWarnings = true;
    private EnumSet<Warning> suppressedWarnings = null;
    private List<Button> warningTypeButtons = new ArrayList<Button>();
    private Button showWarnings;
    
    private IResourceChangeListener encodingListener;

    private Composite warningOptions;
    
    @Override
    public boolean performOk() {
        IProject project = getSelectedProject();
        if (CeylonNature.isEnabled(project)) {
            CeylonProjectConfig config = CeylonProjectConfig.get(project);
            if (suppressedWarnings.isEmpty()) {
                config.setProjectSuppressWarnings(null);
            }
            else {
                config.setProjectSuppressWarningsEnum(suppressedWarnings);
            }
            config.save();
        }
        return true;
    }
    
    @Override
    protected void performDefaults() {
        suppressedWarnings = EnumSet.noneOf(Warning.class);
        showWarnings.setSelection(true);
        warningOptions.setEnabled(true);;
        for (Button b: warningTypeButtons) {
            b.setEnabled(true);
            b.setSelection(true);
        }
        super.performDefaults();
    }
    
    private IProject getSelectedProject() {
        return (IProject) getElement().getAdapter(IProject.class);
    }
    
    final class OptionListener implements SelectionListener {
        private Warning[] types;
        private Button b;
        OptionListener(Button b, Warning... type) {
            this.b = b;
            this.types = type;
        }
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (b.getSelection()) {
                for (Warning t: types) {
                    suppressedWarnings.remove(t);
                }
            }
            else {
                for (Warning t: types) {
                    suppressedWarnings.add(t);
                }
            }
        }
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {}
    }
    
    void createOption(Composite comp, String description, Warning... type) {
        final Button b = new Button(comp, SWT.CHECK);
        b.setText(description);
        b.addSelectionListener(new OptionListener(b, type));
        b.setSelection(!suppressedWarnings.containsAll(Arrays.asList(type)));
        b.setEnabled(showCompilerWarnings && 
                CeylonNature.isEnabled(getSelectedProject()));
        warningTypeButtons.add(b);
    }
    
    void addControls(final Composite parent) {
        
        boolean builderEnabled = CeylonNature.isEnabled(getSelectedProject());
        
        showWarnings = new Button(parent, SWT.CHECK);
        showWarnings.setText("Display compilation warnings");
        showWarnings.setSelection(showCompilerWarnings);
        showWarnings.setEnabled(builderEnabled);
        
        final Group warningsGroup = new Group(parent, SWT.NONE);
        warningsGroup.setText("Enabled compilation warning types");
        warningsGroup.setLayout(GridLayoutFactory.swtDefaults().create());
        warningsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        
        showWarnings.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
        
        suppressedWarnings = getSuppressedWarnings(getSelectedProject());
        
        warningOptions = new Composite(warningsGroup, SWT.NONE);
        warningOptions.setLayout(GridLayoutFactory.swtDefaults().equalWidth(true).numColumns(2).create());
        warningOptions.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        
        createOption(warningOptions, 
                "Unused declarations",
                Warning.unusedDeclaration);
        createOption(warningOptions, 
                "Unused imports", 
                Warning.unusedImport);
        createOption(warningOptions, 
                "Deprecation", 
                Warning.deprecation);
        createOption(warningOptions, 
                "Expressions of type 'Nothing'", 
                Warning.expressionTypeNothing);
        createOption(warningOptions, 
                "Broken documentation links", 
                Warning.doclink);
        createOption(warningOptions, 
                "Discouraged namespaces", 
                Warning.ceylonNamespace, 
                Warning.javaNamespace);
        createOption(warningOptions, 
                "Source file names", 
                Warning.filenameCaselessCollision, 
                Warning.filenameNonAscii);
        createOption(warningOptions, 
                "Warning suppression", 
                Warning.suppressedAlready, 
                Warning.suppressesNothing, 
                Warning.unknownWarning);
        createOption(warningOptions, 
                "Ambiguous annotations", 
                Warning.ambiguousAnnotation);
        createOption(warningOptions, 
                "Compiler annotations", 
                Warning.compilerAnnotation);
        warningOptions.setEnabled(showCompilerWarnings && builderEnabled);
        
        showWarnings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean showCompilerWarnings = showWarnings.getSelection();
                suppressedWarnings = showCompilerWarnings ? 
                        EnumSet.noneOf(Warning.class) : 
                        EnumSet.allOf(Warning.class);
                warningOptions.setEnabled(showCompilerWarnings);
                for (Button b: warningTypeButtons) {
                    b.setEnabled(showCompilerWarnings);
                    b.setSelection(showCompilerWarnings);
                }
            }
        });

    }
    
    @Override
    protected Control createContents(Composite composite) {
        IProject project = getSelectedProject();
        if (project.isOpen()) {
            boolean builderEnabled = CeylonNature.isEnabled(project);
            if (builderEnabled) {
                showCompilerWarnings = showWarnings(project);
                suppressedWarnings = getSuppressedWarnings(project);
            }
        }

        addControls(composite);
        return composite;
    }
        
    @Override
    public void dispose() {
        if (encodingListener!=null) {
            getWorkspace().removeResourceChangeListener(encodingListener);
            encodingListener = null;
        }
        super.dispose();
    }
    
}