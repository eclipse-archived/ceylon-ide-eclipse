package com.redhat.ceylon.eclipse.code.style;

import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.*;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class FormatterTabSpace extends FormatterTabPage {

    private final String PREVIEW =
            "import ceylon.collection { MutableList, freq=frequencies }\n\n\n"
            + "shared class Example(str) {"
            + "shared String str; \n"
            + "value test => 0;\n"
            + "}";

    private CeylonPreview ceylonPreview;

    public FormatterTabSpace(FormatterModifyProfileDialog modifyDialog,
            FormatterPreferences workingValues) {
        super(modifyDialog, workingValues);
    }

    @Override
    protected void doCreatePreferences(Composite composite, int numColumns) {

        final Group generalGroup = createGroup(numColumns, composite,
                "Spacing Options");
        createCheckboxPref(generalGroup, numColumns, "Space before method or class positional argument list",
                FORMATTER_space_BeforeMethodOrClassPositionalArgumentList, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space before resource list",
                FORMATTER_space_BeforeResourceList, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space before catch variable",
                FORMATTER_space_BeforeCatchVariable, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space before 'satisfies' and 'of'",
                FORMATTER_space_AroundSatisfiesOf, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space around '=' for import alias",
                FORMATTER_space_AroundImportAliasEqualsSign, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space after type argument or parameter list comma",
                FORMATTER_space_AfterTypeArgOrParamListComma, FALSE_TRUE);

        createLabel(2, generalGroup, "Parameter List Opening");
        createCheckboxPref(generalGroup, 1, "Before (",
                FORMATTER_space_BeforeParamListOpeningParen, FALSE_TRUE);
        createCheckboxPref(generalGroup, 1, "After (",
                FORMATTER_space_AfterParamListOpeningParen, FALSE_TRUE);
            
        createLabel(2, generalGroup, "Parameter List Closing");
        createCheckboxPref(generalGroup, 1, "Before )",
                FORMATTER_space_BeforeParamListClosingParen, FALSE_TRUE);  
        CheckboxPreference saplcp = createCheckboxPref(generalGroup, 1, "After )",
                FORMATTER_space_AfterParamListClosingParen, FALSE_TRUE);

        final NumberPreference saplcpn = createNumberPref(generalGroup, numColumns, "Optionally, -80 to 80, 0 resets",
                FORMATTER_space_AfterParamListClosingParen_Number, -80, 80); //TODO magic numbers
        updatePreferences(this.workingValues.get(FORMATTER_space_AfterParamListClosingParen), saplcpn, saplcp);

        saplcp.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                updatePreferences((String) arg, saplcpn, (CheckboxPreference)o);
            }
        });
        
        createLabel(2, generalGroup, "Value Iterator");
        createCheckboxPref(generalGroup, 1, "After (",
                FORMATTER_space_AfterValueIteratorOpeningParenthesis, FALSE_TRUE);  
        createCheckboxPref(generalGroup, 1, "Before )",
                FORMATTER_space_BeforeValueIteratorClosingParenthesis, FALSE_TRUE);
        
        createLabel(2, generalGroup, "Sequence Enumeration");
        createCheckboxPref(generalGroup, 1, "After {",
                FORMATTER_space_AfterSequenceEnumerationOpeningBrace, FALSE_TRUE);         
        createCheckboxPref(generalGroup, 1, "Before }",
                FORMATTER_space_BeforeSequenceEnumerationClosingBrace, FALSE_TRUE);
 
        createCheckboxPref(generalGroup, numColumns, "Space before 'if' opening (",
                FORMATTER_space_BeforeIfOpeningParenthesis, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space before 'for' opening (",
                FORMATTER_space_BeforeForOpeningParenthesis, FALSE_TRUE);  
        createCheckboxPref(generalGroup, numColumns, "Space before 'while' opening (",
                FORMATTER_space_BeforeWhileOpeningParenthesis, FALSE_TRUE);  
        createCheckboxPref(generalGroup, numColumns, "Space before annotation positional argument list",
                FORMATTER_space_BeforeAnnotationPositionalArgumentList, FALSE_TRUE);
  
    }

    private void updatePreferences(String v, NumberPreference np, CheckboxPreference cbp) {
        if (cbp.getChecked()) {
            np.setEnabled(true);
        } else {
            np.setEnabled(false);
        }
    }

    @Override
    public void initializePage() {
        ceylonPreview.setPreviewText(PREVIEW);
    }

    @Override
    protected CeylonPreview doCreateCeylonPreview(Composite parent) {
        ceylonPreview = new CeylonPreview(this.workingValues, parent);
        return ceylonPreview;
    }

    @Override
    protected void doUpdatePreview() {
        super.doUpdatePreview();
        ceylonPreview.update();
    }
}
