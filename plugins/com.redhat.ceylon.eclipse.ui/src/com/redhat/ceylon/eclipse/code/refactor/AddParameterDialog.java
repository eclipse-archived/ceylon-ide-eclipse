package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModelLoader;

import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.compiler.loader.TypeParser;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class AddParameterDialog extends Dialog /*TitleAreaDialog*/ {
    
    private String name; 
    private ProducedType type;
    private String argument;
    private TypeParser parser;
    private Node node;
    
    public AddParameterDialog(Shell parentShell, 
            Node node, IProject project) {
        super(parentShell);
        parser = new TypeParser(getProjectModelLoader(project));
        this.node = node;
        name = "something";
        type = node.getUnit().getAnythingDeclaration().getType();
        argument = "nothing";
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    public String getName() {
        return name;
    }
    
    public ProducedType getType() {
        return type;
    }
    
    public String getArgument() {
        return argument;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText("Add Parameter");
        parent = (Composite) super.createDialogArea(parent);
        
        new Label(parent, SWT.NONE).setText("Enter the name and type of the new parameter.");
        new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        
        Label nameLabel = new Label(composite, SWT.NONE);
        nameLabel.setText("Name:");
        final Text nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        nameText.setText("something");
        nameText.selectAll();
        nameText.setLayoutData(GridDataFactory.fillDefaults().hint(80, SWT.DEFAULT).create());
        
        Label typeLabel = new Label(composite, SWT.NONE);
        typeLabel.setText("Type:");
        final Text typeText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        typeText.setText("Anything");

        Label valLabel = new Label(composite, SWT.NONE);
        valLabel.setText("Argument:");
        final Text argumentText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        argumentText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        argumentText.setText("nothing");

        final CLabel errorLabel = new CLabel(composite, SWT.NONE);
        errorLabel.setLayoutData(GridDataFactory.fillDefaults().span(4, 1).grab(true, false).create());
        errorLabel.setVisible(false);
        errorLabel.setImage(CeylonResources.ERROR);
        
        nameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                name = nameText.getText();
            }
        });
        argumentText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                argument = argumentText.getText();
            }
        });
        typeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                try {
                    String text = typeText.getText();
                    StringBuffer buffer = new StringBuffer();
                    StringTokenizer tokens = 
                            new StringTokenizer(text, 
                                    " <>()[]{}*+|&,?", true);
                    while (tokens.hasMoreTokens()) {
                        String token = tokens.nextToken();
                        if (" <>()[]{}*+|&,?".contains(token)) {
                            buffer.append(token);
                        }
                        else {
                            Declaration dec = 
                                    node.getScope()
                                        .getMemberOrParameter(node.getUnit(), 
                                                token, null, false);
                            if (dec==null) {
                                errorLabel.setText("Type not found: " + token);
                                errorLabel.setVisible(true);
                                return;
                            }
                            else if (!(dec instanceof TypeDeclaration)) {
                                errorLabel.setText("Not a type: " + token);
                                errorLabel.setVisible(true);
                                return;
                            }
                            else {
                                buffer.append(dec.getQualifiedNameString());
                            }
                        }
                    }
                    type = parser.decodeType(buffer.toString(), 
                            node.getScope(), 
                            node.getUnit().getPackage().getModule(), 
                            node.getUnit());
                    errorLabel.setVisible(false);
                }
                catch (Exception e) {
                    errorLabel.setText("Illegal type expression");
                    errorLabel.setVisible(true);
                }
            }
        });
        typeText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).hint(300, SWT.DEFAULT).create());
        return parent;
    }

}
