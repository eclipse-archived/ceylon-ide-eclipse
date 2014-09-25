package com.redhat.ceylon.eclipse.code.style;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/*
 * The page to configure new module and other high-level conventions
 */
public class StyleModuleConventionBlock extends StyleBlock {

    public StyleModuleConventionBlock(IProject project) {
        if (project != null) {
            super.project = project;
            enableProjectSettings();
        }
        initialize();
    }

    private String newModuleVersion;
    private String newModuleAuthor;
    private int selectedVersionComboIndex = 0;

    @Override
    protected Control createContents(Composite parent) {
        setShell(parent.getShell());

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;

        block = new Composite(parent, SWT.NONE);
        block.setFont(parent.getFont());
        block.setLayout(layout);

        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 3;

        Label labelVersion = new Label(block, SWT.NONE);
        labelVersion.setText("New Module Version:");
        labelVersion.setLayoutData(gd);

        Combo versionCombo = new Combo(block, SWT.SINGLE | SWT.BORDER
                | SWT.DROP_DOWN);
        versionCombo.add("1.0.0");
        versionCombo.add("1.0");
        versionCombo.add("0.1");
        if (newModuleVersion == null) {
            versionCombo.select(selectedVersionComboIndex);
        } else {
            versionCombo.setText(newModuleVersion);
        }

        versionCombo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent me) {
                newModuleVersion = ((Combo) me.widget).getText();
            }
        });

        Label labelAuthor = new Label(block, SWT.NONE);
        labelAuthor.setText("New Module Author Name:");
        labelAuthor.setLayoutData(gd);

        Text authorText = new Text(block, SWT.SINGLE | SWT.BORDER);
        authorText.setLayoutData(gd);
        authorText.setTextLimit(40); // TODO arbitrary
        if (newModuleAuthor == null) {
            authorText.setText("");
        } else {
            authorText.setText(newModuleAuthor);
        }

        authorText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent me) {
                newModuleAuthor = ((Text) me.widget).getText();
            }
        });

        return block;
    }

    @Override
    public void initialize() {
        performDefaults();
    }

    @Override
    public boolean performApply() {
        if (CeylonStyle.setnewModuleAuthor(project, newModuleAuthor)
                & CeylonStyle.setnewModuleVersion(project, newModuleVersion)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void dispose() {
        if (block != null) {
            block.dispose();
            block = null;
        }
    }

    @Override
    protected void performDefaults() {
        newModuleVersion = CeylonStyle.getnewModuleVersion(project);
        newModuleAuthor = CeylonStyle.getnewModuleAuthor(project);
    }
}
