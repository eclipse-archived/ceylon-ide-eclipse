package com.redhat.ceylon.eclipse.code.refactor;

import java.util.List;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
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

import com.redhat.ceylon.compiler.typechecker.analyzer.TypeVisitor;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.StaticType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;

public class AddParameterDialog extends Dialog /*TitleAreaDialog*/ {
    
    private String name; 
    private ProducedType type;
    private String argument;
    private Node node;
    private Set<String> parameterNames;
    private Unit unit;
    
    public AddParameterDialog(Shell parentShell, 
            Node node, IProject project, 
            Set<String> parameterNames) {
        super(parentShell);
        this.parameterNames = parameterNames;
        this.node = node;
        name = "something";
        unit = node.getUnit();
        type = unit.getAnythingDeclaration().getType();
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
        composite.setLayoutData(GridDataFactory.fillDefaults().hint(500, SWT.DEFAULT).grab(true, true).create());
        
        Label typeLabel = new Label(composite, SWT.NONE);
        typeLabel.setText("Type:");
        final Text typeText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        typeText.setText("Anything");

        Label nameLabel = new Label(composite, SWT.NONE);
        nameLabel.setText("Name:");
        final Text nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        nameText.setText("something");
        nameText.selectAll();
        nameText.setLayoutData(GridDataFactory.fillDefaults().create());
        
        Label valLabel = new Label(composite, SWT.NONE);
        valLabel.setText("Argument:");
        final Text argumentText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        argumentText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        argumentText.setText("nothing");

        final CLabel errorLabel = new CLabel(composite, SWT.NONE);
        errorLabel.setLayoutData(GridDataFactory.fillDefaults().span(4, 1).grab(true, true).create());
        errorLabel.setVisible(false);
        errorLabel.setImage(CeylonResources.ERROR);
        
        nameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (updateName(nameText, errorLabel)) {
                    updateType(typeText, errorLabel);
                }
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
                if (updateType(typeText, errorLabel)) {
                    updateName(nameText, errorLabel);
                }
            }
        });
        typeText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).hint(300, SWT.DEFAULT).create());
        return parent;
    }

    private boolean updateType(final Text typeText, 
            final CLabel errorLabel) {
        try {
            String text = typeText.getText();
            
            CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(text));
            CommonTokenStream ts = new CommonTokenStream(lexer);
            ts.fill();
            List<LexError> lexErrors = lexer.getErrors();
            if (!lexErrors.isEmpty()) {
                errorLabel.setText(lexErrors.get(0).getMessage());
                errorLabel.setVisible(true);
                return false;
            }
            
            CeylonParser parser = new CeylonParser(ts);
            StaticType staticType = parser.type();
            if (ts.index()<ts.size()-1) {
                errorLabel.setText("extra tokens in type expression");
                errorLabel.setVisible(true);
                return false;
            }
            List<ParseError> parseErrors = parser.getErrors();
            if (!parseErrors.isEmpty()) {
                errorLabel.setText(parseErrors.get(0).getMessage());
                errorLabel.setVisible(true);
                return false;
            }
            
            staticType.visit(new Visitor() {
                @Override
                public void visitAny(Node that) {
                    that.setUnit(unit);
                    that.setScope(node.getScope());
                    super.visitAny(that);
                }
            });
            staticType.visit(new TypeVisitor(unit));
            //TODO: error if unparameterized type has type args!
            type = staticType.getTypeModel();
            
            errorLabel.setVisible(false);
            
            new ErrorVisitor() {
                @Override
                protected void handleMessage(int startOffset, int endOffset, 
                        int startCol, int startLine, Message error) {
                    errorLabel.setText(error.getMessage());
                    errorLabel.setVisible(true);
                }
            }.visit(staticType);
            
            return !errorLabel.isVisible();
            
        }
        catch (Exception e) {
            errorLabel.setText("Could not parse type expression");
            errorLabel.setVisible(true);
            return false;
        }
    }

    private boolean updateName(final Text nameText, 
            final CLabel errorLabel) {
        String text = nameText.getText();
        if (text.isEmpty()) {
            errorLabel.setText("Missing name");
            errorLabel.setVisible(true);
            return false;
        }
        else if (!text.matches("^[a-z_]\\w*$")) {
            errorLabel.setText("Illegal parameter name");
            errorLabel.setVisible(true);
            return false;
        }
        else if (parameterNames.contains(text)) {
            errorLabel.setText("Duplicate parameter name");
            errorLabel.setVisible(true);
            return false;
        }
        else {
            name = text;
            errorLabel.setVisible(false);
            return true;
        }
    }
    
}
