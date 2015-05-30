package com.redhat.ceylon.eclipse.code.refactor;


import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.appendTypeName;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedNode;
import static org.eclipse.jface.viewers.ArrayContentProvider.getInstance;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_FILL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.common.BackendSupport;
import com.redhat.ceylon.compiler.typechecker.analyzer.ExpressionVisitor;
import com.redhat.ceylon.compiler.typechecker.analyzer.TypeVisitor;
import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Constructor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Value;

public class ChangeParametersInputPage extends UserInputWizardPage {
    
    private StyledText signatureText;
    private final BackendSupport backendSupport;

    public ChangeParametersInputPage(String name) {
        super(name);
        // FIXME this should really come from the ModuleManager
        // or at least depend on the backends that are enabled
        // in the project configuration
        backendSupport = new BackendSupport() {
            @Override
            public boolean supportsBackend(Backend backend) {
                return true;
            }
        };
    }
    
    private boolean isDefaulted(List<Parameter> parameterModels, 
            Parameter parameter) {
        return getChangeParametersRefactoring().getDefaulted()
                .get(parameterModels.indexOf(parameter));
    }
    
    public void createControl(Composite parent) {
        final ChangeParametersRefactoring refactoring = 
                getChangeParametersRefactoring();
        
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Label title = new Label(result, SWT.LEFT);  
        Declaration dec = refactoring.getDeclaration();
        String name = dec.getName();
        if (name == null && dec instanceof Constructor) {
            Scope container = dec.getContainer();
            if (container instanceof Declaration) {
                name = ((Declaration) container).getName();
            }
        }
        title.setText("Change parameters in " + 
                refactoring.getCount() + 
                " occurrences of '" + name + "'.");
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        title.setLayoutData(gd);
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=2;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);

        Composite composite = new Composite(result, SWT.NONE);
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        cgd.grabExcessHorizontalSpace = true;
        cgd.grabExcessVerticalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout tableLayout = new GridLayout(4, true);
        tableLayout.marginWidth=0;
        composite.setLayout(tableLayout);
        
        final List<Parameter> parameterModels = 
                new ArrayList<Parameter>(refactoring.getParameters());
        
        final TableViewer viewer = new TableViewer(composite, 
                SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        viewer.setContentProvider(getInstance());
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
        GridData tgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        tgd.horizontalSpan = 3;
        tgd.verticalSpan = 6;
        tgd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        tgd.heightHint = 100;
        tgd.widthHint = 425;
        viewer.getTable().setLayoutData(tgd);
        TableViewerColumn orderCol = new TableViewerColumn(viewer, SWT.LEFT);
        orderCol.getColumn().setText("");
        orderCol.getColumn().setWidth(10);
        orderCol.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                final int originalIndex = refactoring.getParameters().indexOf(element);
                final int currentIndex = parameterModels.indexOf(element);
                if (originalIndex==currentIndex) return "";
                return originalIndex>currentIndex ? "\u2191" : "\u2193"; 
            }
            @Override
            public Image getImage(Object element) {
                return null;
            }
        });
        TableViewerColumn sigCol = new TableViewerColumn(viewer, SWT.LEFT);
        sigCol.getColumn().setText("Type");
        sigCol.getColumn().setWidth(140);
        sigCol.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                Parameter p = (Parameter) cell.getElement();
                FunctionOrValue model = p.getModel();
                StyledString styledString = new StyledString();
                Type fullType = model.getProducedReference(null, 
                        Collections.<Type>emptyList()).getFullType();
                appendTypeName(styledString, fullType);
                cell.setImage(getImageForDeclaration(fullType.getDeclaration()));
                cell.setText(styledString.toString());
                cell.setStyleRanges(styledString.getStyleRanges());
                super.update(cell);
            }
        });
        TableViewerColumn nameCol = new TableViewerColumn(viewer, SWT.LEFT);
        nameCol.getColumn().setText("Name");
        nameCol.getColumn().setWidth(100);
        nameCol.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                String name = getChangeParametersRefactoring().getNames()
                        .get(parameterModels.indexOf((Parameter) cell.getElement()));
                StyledString styledString = new StyledString();
                styledString.append(name, Highlights.ID_STYLER);
                cell.setText(styledString.toString());
                cell.setStyleRanges(styledString.getStyleRanges());
                super.update(cell);
            }
        });
        nameCol.setEditingSupport(new EditingSupport(viewer) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(viewer.getTable(), SWT.FLAT);
            }
            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
            @Override
            protected Object getValue(Object element) {
                return getChangeParametersRefactoring().getNames()
                        .get(parameterModels.indexOf((Parameter) element));
            }
            @Override
            protected void setValue(Object element, Object value) {
                getChangeParametersRefactoring().getNames()
                        .set(parameterModels.indexOf((Parameter) element), 
                                value.toString());
                viewer.update(element, null);
                drawSignature();
            } 
        });
        TableViewerColumn optCol = new TableViewerColumn(viewer, SWT.LEFT);
        optCol.getColumn().setText("Optionality");
        optCol.getColumn().setWidth(70);
        optCol.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return isDefaulted(parameterModels, (Parameter) element) ?
                        "defaulted" : "required"; 
            }
            @Override
            public Image getImage(Object element) {
                return null;
            }
        });
        final String[] options = new String[] {"required", "defaulted"};
        optCol.setEditingSupport(new EditingSupport(viewer) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new ComboBoxCellEditor(viewer.getTable(), 
                        options, SWT.FLAT);
            }
            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
            @Override
            protected Object getValue(Object element) {
                return isDefaulted(parameterModels, (Parameter) element) ? 1 : 0;
            }
            @Override
            protected void setValue(Object element, Object value) {
                refactoring.getDefaulted()
                        .set(parameterModels.indexOf(element), 
                                value.equals(1));
                viewer.update(element, null);
                drawSignature();
            } 
        });
        TableViewerColumn diffCol = new TableViewerColumn(viewer, SWT.LEFT);
        diffCol.getColumn().setText("");
        diffCol.getColumn().setWidth(15);
        diffCol.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Parameter p = (Parameter) element;
                boolean def = isDefaulted(parameterModels, p);
                if (!def && p.isDefaulted()) {
                    return "\u2296";
                }
                if (def && !p.isDefaulted()) {
                    return "\u2295";
                }
                if (def && 
                        refactoring.defaultHasChanged(p)) {
                    return ">";
                }
                return "";
            }
            @Override
            public Image getImage(Object element) {
                /*Parameter p = (Parameter) element;
                boolean def = isDefaulted(parameterModels, p);
                if (p.isDefaulted()) {
                    return  def ? null : REMOVE_CORR;
                }
                else {
                    return def ? ADD_CORR : null;
                }*/
                return null;
            }
        });
        TableViewerColumn argCol = new TableViewerColumn(viewer, SWT.LEFT);
        argCol.getColumn().setText("Default Argument");
        argCol.getColumn().setWidth(100);
        argCol.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                StyledString styledString = new StyledString();
                String text = getText(cell.getElement());
                if (text!=null) {
                    Highlights.styleProposal(styledString, text, false);
                }
                cell.setText(styledString.toString());
                cell.setStyleRanges(styledString.getStyleRanges());
                super.update(cell);
            }
            public String getText(Object element) {
                FunctionOrValue model = ((Parameter) element).getModel();
                if (isDefaulted(parameterModels, (Parameter) element)) {
                    return refactoring.getDefaultArgs().get(model);
                }
                else {
                    return null;
                }
            }
        });
        argCol.setEditingSupport(new EditingSupport(viewer) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(viewer.getTable(), SWT.FLAT);
            }
            @Override
            protected boolean canEdit(Object element) {
                return isDefaulted(parameterModels, (Parameter) element);
            }
            @Override
            protected Object getValue(Object element) {
                FunctionOrValue model = ((Parameter) element).getModel();
                if (isDefaulted(parameterModels, (Parameter) element)) {
                    String arg = refactoring.getDefaultArgs().get(model);
                    return arg==null ? "" : arg;
                }
                else {
                    return "";
                }
            }
            @Override
            protected void setValue(Object element, Object value) {
                updateArgument((Parameter) element, (String) value,
                        refactoring);
                FunctionOrValue model = ((Parameter) element).getModel();
                refactoring.getDefaultArgs().put(model, 
                        (String) value);
                viewer.update(element, null);
                drawSignature();
            } 
        });
        
        viewer.setInput(parameterModels);
        
        final Button upButton = new Button(composite, SWT.PUSH);
        upButton.setText("Up");
        GridData bgd = new GridData(VERTICAL_ALIGN_BEGINNING|HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        upButton.setLayoutData(bgd);
        upButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = viewer.getTable().getSelectionIndices();
                if (indices.length>0 && indices[0]>0) {
                    int index = indices[0];
                    parameterModels.add(index-1, parameterModels.remove(index));
                    List<Integer> order = refactoring.getOrder();
                    order.add(index-1, order.remove(index));
                    List<Boolean> defaulted = refactoring.getDefaulted();
                    List<String> names = refactoring.getNames();
                    defaulted.add(index-1, defaulted.remove(index));
                    names.add(index-1, names.remove(index));
                    viewer.refresh();
                    viewer.getTable().select(index-1);
                }
                drawSignature();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        final Button downButton = new Button(composite, SWT.PUSH);
        downButton.setText("Down");
        downButton.setLayoutData(bgd);
        downButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = viewer.getTable().getSelectionIndices();
                int lastIndex = viewer.getTable().getItemCount()-1;
                if (indices.length>0 && indices[0]<lastIndex) {
                    int index = indices[0];
                    parameterModels.add(index+1, parameterModels.remove(index));
                    List<Integer> order = refactoring.getOrder();
                    order.add(index+1, order.remove(index));
                    List<Boolean> defaulted = refactoring.getDefaulted();
                    List<String> names = refactoring.getNames();
                    defaulted.add(index+1, defaulted.remove(index));
                    names.add(index+1, names.remove(index));
                    viewer.refresh();
                    viewer.getTable().select(index+1);
                }
                drawSignature();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        final Button addButton = new Button(composite, SWT.PUSH);
        addButton.setText("Add...");
        addButton.setLayoutData(bgd);
        
        final Button removeButton = new Button(composite, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.setLayoutData(bgd);
        
        new Label(composite, SWT.NONE);

        final Button toggleButton = new Button(composite, SWT.PUSH);
        toggleButton.setText("Toggle Optionality");
        toggleButton.setLayoutData(bgd);
        toggleButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = viewer.getTable().getSelectionIndices();
                if (indices.length>0) {
                    int index = indices[0];
                    List<Boolean> defaulted = refactoring.getDefaulted();
                    defaulted.set(index, !defaulted.get(index));
                    viewer.refresh();
                }
                drawSignature();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = viewer.getTable().getSelectionIndices();
                int lastIndex = viewer.getTable().getItemCount()-1;
                if (indices.length>0 && indices[0]<=lastIndex) {
                    int index = indices[0];
                    Parameter p = parameterModels.remove(index);
                    refactoring.getDefaulted().remove(index);
                    refactoring.getOrder().remove(index);
                    refactoring.getNames().remove(index);
                    viewer.remove(p);
                    viewer.refresh();
                    if (parameterModels.isEmpty()) {
                        toggleButton.setEnabled(false);
                        removeButton.setEnabled(false);
                    }
                    if (parameterModels.size()<2) {
                        upButton.setEnabled(false);
                        downButton.setEnabled(false);
                    }
                }
                drawSignature();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Set<String> names = new HashSet<String>();
                for (Parameter p: parameterModels) {
                    names.add(p.getName());
                }
                AddParameterDialog dialog = new AddParameterDialog(getShell(), 
                        refactoring.node, refactoring.project, names);
                if (dialog.open()==Window.OK) {
                    String name = dialog.getName();
                    Type type = dialog.getType();
                    String arg = dialog.getArgument();
                    Value model = new Value();
                    model.setType(type);
                    model.setName(name);
                    Scope scope = refactoring.node.getScope();
                    model.setContainer(scope);
                    model.setScope(scope);
                    Parameter p = new Parameter();
                    p.setModel(model);
                    p.setName(name);
                    p.setDefaulted(false);
                    p.setDeclaration((Declaration) scope);
                    model.setInitializerParameter(p);
                    int index = parameterModels.size();
                    int order = refactoring.getParameters().size();
                    parameterModels.add(p);
                    refactoring.getParameters().add(p);
                    refactoring.getDefaulted().add(false);
                    refactoring.getNames().add(name);
                    refactoring.getArguments().put(p.getModel(), arg);
                    refactoring.getDefaultArgs().put(model, arg);
                    refactoring.getOrder().add(index, order);
                    viewer.add(p);
                    viewer.refresh();
                    viewer.getTable().select(index);
                    if (index==1) {
                        upButton.setEnabled(true);
                        downButton.setEnabled(true);
                    }
                    if (index==0) {
                        toggleButton.setEnabled(true);
                        removeButton.setEnabled(true);
                    }
                }
                drawSignature();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        if (parameterModels.isEmpty()) {
            toggleButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
        else {
            viewer.getTable().setSelection(0);
        }
        if (parameterModels.size()<2) {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }
        
        new Label(composite, SWT.SEPARATOR|SWT.HORIZONTAL)
            .setLayoutData(GridDataFactory.fillDefaults().span(4, 1).create());
        
        Label l = new Label(composite, SWT.NONE);
        l.setLayoutData(GridDataFactory.fillDefaults().span(4, 1).create());
        l.setText("Refactored signature preview:");
        
        signatureText = new StyledText(composite, SWT.FLAT|SWT.READ_ONLY|SWT.WRAP);
        signatureText.setLayoutData(GridDataFactory.fillDefaults().hint(300, 50).grab(true, true).span(4, 3).create());
        signatureText.setBackground(composite.getBackground());
        drawSignature();
    }
    
    private void drawSignature() {
        ChangeParametersRefactoring ref = 
                getChangeParametersRefactoring();
        Declaration declaration = 
                ref.getDeclaration();
        Node decNode = getReferencedNode(declaration);
        Tree.ParameterList pl;
        if (decNode instanceof Tree.AnyMethod) {
            pl = ((Tree.AnyMethod) decNode).getParameterLists().get(0);
        }
        else if (decNode instanceof Tree.AnyClass) {
            pl = ((Tree.AnyClass) decNode).getParameterList();
        }
        else if (decNode instanceof Tree.Constructor) {
            pl = ((Tree.Constructor) decNode).getParameterList();
        }
        else {
            return;
        }
        List<CommonToken> tokens = 
                ((CeylonUnit) declaration.getUnit()).getPhasedUnit().getTokens();
        Tree.Parameter[] reorderedParameters = 
                ref.reorderedParameters(pl.getParameters());
        ReplaceEdit edit = 
                ref.reorderParamsEdit(pl, reorderedParameters, false, tokens);
        int start = ((CommonToken) decNode.getMainToken()).getStartIndex() - decNode.getStartIndex();
        int end = pl.getStartIndex() - decNode.getStartIndex();
        String text = Nodes.toString(decNode, tokens).substring(start,end) + edit.getText();
        StyledString styledString = new StyledString();
        Highlights.styleProposal(styledString, text, false);
        signatureText.setText(styledString.getString());
        signatureText.setStyleRanges(styledString.getStyleRanges());
        signatureText.setFont(CeylonEditor.getEditorFont());
    }
    
    private ChangeParametersRefactoring getChangeParametersRefactoring() {
        return (ChangeParametersRefactoring) getRefactoring();
    }
    
    private boolean updateArgument(final Parameter parameter, 
            final String text, 
            final ChangeParametersRefactoring refactoring) {
        try {
            if (text.isEmpty()) {
                return true;
//                setErrorMessage("Missing argument expression");
//                return false;
            }
            String typeExpression = parameter.getType().getProducedTypeName();
            String parameterName = parameter.getName();
            String paramDeclaration = "(" + typeExpression + " " + parameterName + " = " + text + ")";
            
            CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(paramDeclaration));
            CommonTokenStream ts = new CommonTokenStream(lexer);
            ts.fill();
            List<LexError> lexErrors = lexer.getErrors();
            if (!lexErrors.isEmpty()) {
                setErrorMessage(lexErrors.get(0).getMessage());
                return false;
            }
            
            CeylonParser parser = new CeylonParser(ts);
            Tree.ParameterList parameters = parser.parameters();
            if (ts.index()<ts.size()-1) {
                setErrorMessage("extra tokens in argument expression");
                return false;
            }
            List<ParseError> parseErrors = parser.getErrors();
            if (!parseErrors.isEmpty()) {
                setErrorMessage(parseErrors.get(0).getMessage());
                return false;
            }
            
            final TypecheckerUnit unit = refactoring.node.getUnit();
            final Scope scope = refactoring.node.getScope();
            final Value value = new Value();
            value.setName(parameterName);
            final Parameter param = new Parameter();
            param.setName(parameterName);
            param.setModel(value);
            parameters.visit(new Visitor() {
                @Override
                public void visitAny(Node that) {
                    that.setUnit(unit);
                    that.setScope(scope);
                    super.visitAny(that);
                }
                @Override
                public void visit(Tree.AttributeDeclaration that) {
                    that.setDeclarationModel(value);
                    super.visit(that);
                }
                @Override public void visit(Tree.ParameterDeclaration that) {
                    that.setParameterModel(param);
                    super.visit(that);
                }
            });
            parameters.visit(new TypeVisitor(unit, backendSupport));
            parameters.visit(new ExpressionVisitor(unit, backendSupport));
            
            setErrorMessage(null);
            
            new ErrorVisitor() {
                @Override
                protected void handleMessage(int startOffset, int endOffset, 
                        int startCol, int startLine, Message error) {
                    setErrorMessage(error.getMessage());
                }
            }.visit(parameters);
            
            if (getErrorMessage()!=null) { 
                return false;
            }
            else {
                return true;
            }
            
        }
        catch (Exception e) {
            setErrorMessage("Could not parse argument expression");
            return false;
        }
    }

}
