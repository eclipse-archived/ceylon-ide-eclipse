package com.redhat.ceylon.eclipse.code.style;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public abstract class FormatterTabPage {

    public interface ModificationListener {

        void valuesModified(FormatterPreferences workingValues);

        void updateStatus(IStatus status);

    }

    private final static String SHOW_INVISIBLE_PREFERENCE_KEY = CeylonPlugin.PLUGIN_ID
            + ".style.formatter_page.show_invisible_characters";

    protected static String[] FALSE_TRUE = { "false", "true" };

    protected static String[] TRUE_FALSE = { "true", "false" };

    private CeylonPreview fPreview;
    private final IDialogSettings fDialogSettings;
    private Button fShowInvisibleButton;

    protected Composite doCreatePreviewPane(Composite composite, int numColumns) {

        createLabel(numColumns - 1, composite, "Preview");

        fShowInvisibleButton = new Button(composite, SWT.CHECK);
        fShowInvisibleButton.setText("Show Invisible Characters");
        fShowInvisibleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP,
                true, false));
        fShowInvisibleButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fPreview.showInvisibleCharacters(fShowInvisibleButton
                        .getSelection());
                fDialogSettings.put(SHOW_INVISIBLE_PREFERENCE_KEY,
                        fShowInvisibleButton.getSelection());
                doUpdatePreview();
            }
        });
        fShowInvisibleButton.setSelection(isShowInvisible());

        fPreview = doCreateCeylonPreview(composite);

        fPreview.showInvisibleCharacters(fShowInvisibleButton.getSelection());

        final GridData gd = createGridData(numColumns, GridData.FILL_BOTH, 0);
        gd.widthHint = 0;
        gd.heightHint = 0;
        fPreview.getControl().setLayoutData(gd);

        return composite;
    }

    private boolean isShowInvisible() {
        return fDialogSettings.getBoolean(SHOW_INVISIBLE_PREFERENCE_KEY);
    }

    protected void doUpdatePreview() {
        boolean showInvisible = isShowInvisible();
        fPreview.showInvisibleCharacters(showInvisible);
        fShowInvisibleButton.setSelection(showInvisible);
    }

    protected final Observer fUpdater = new Observer() {
        public void update(Observable o, Object arg) {
            doUpdatePreview();
            notifyValuesModified();
        }
    };

    protected abstract class Preference extends Observable {
        private final FormatterPreferences fPreferences;
        private boolean fEnabled;
        private String fKey;

        public Preference(FormatterPreferences workingValues, String key) {
            fPreferences = workingValues;
            fEnabled = true;
            fKey = key;
        }

        protected final FormatterPreferences getPreferences() {
            return fPreferences;
        }

        public final void setEnabled(boolean enabled) {
            fEnabled = enabled;
            updateWidget();
        }

        public final boolean getEnabled() {
            return fEnabled;
        }

        public final void setKey(String key) {
            if (key == null || !fKey.equals(key)) {
                fKey = key;
                updateWidget();
            }
        }

        public final String getKey() {
            return fKey;
        }

        public abstract Control getControl();

        protected abstract void updateWidget();
    }

    protected class ButtonPreference extends Preference {
        private final String[] fValues;
        private final Button fCheckbox;

        public ButtonPreference(Composite composite, int numColumns,
                FormatterPreferences workingValues, String key,
                String[] values, String text, int style) {
            super(workingValues, key);
            if (values == null || text == null)
                throw new IllegalArgumentException("Error : text unassigned");
            fValues = values;

            fCheckbox = new Button(composite, style);
            fCheckbox.setText(text);
            fCheckbox.setLayoutData(createGridData(numColumns,
                    GridData.FILL_HORIZONTAL, SWT.DEFAULT));
            fCheckbox.setFont(composite.getFont());

            updateWidget();

            fCheckbox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    checkboxChecked(((Button) e.widget).getSelection());
                }
            });
        }

        protected void checkboxChecked(boolean state) {
            getPreferences().put(getKey(), state ? fValues[1] : fValues[0]);
            setChanged();
            notifyObservers();
        }

        @Override
        protected void updateWidget() {
            if (getKey() != null) {
                fCheckbox.setEnabled(getEnabled());
                fCheckbox.setSelection(getChecked());
            } else {
                fCheckbox.setSelection(false);
                fCheckbox.setEnabled(false);
            }
        }

        public boolean getChecked() {
            return fValues[1].equals(getPreferences().get(getKey()));
        }

        public void setChecked(boolean checked) {
            getPreferences().put(getKey(), checked ? fValues[1] : fValues[0]);
            updateWidget();
            checkboxChecked(checked);
        }

        @Override
        public Control getControl() {
            return fCheckbox;
        }
    }

    protected final class CheckboxPreference extends ButtonPreference {
        public CheckboxPreference(Composite composite, int numColumns,
                FormatterPreferences workingValues, String key,
                String[] values, String text) {
            super(composite, numColumns, workingValues, key, values, text,
                    SWT.CHECK);
        }
    }

    protected final class RadioPreference extends ButtonPreference {
        public RadioPreference(Composite composite, int numColumns,
                FormatterPreferences workingValues, String key,
                String[] values, String text) {
            super(composite, numColumns, workingValues, key, values, text,
                    SWT.RADIO);
        }
    }

    protected final class ComboPreference extends Preference {
        private final String[] fItems;
        private final String[] fValues;
        private final Combo fCombo;

        public ComboPreference(Composite composite, int numColumns,
                FormatterPreferences workingValues, String key,
                String[] values, String text, String[] items) {
            super(workingValues, key);
            if (values == null || items == null || text == null)
                throw new IllegalArgumentException(
                        "Error values text items unassigned");
            fValues = values;
            fItems = items;
            createLabel(numColumns - 1, composite, text);
            fCombo = new Combo(composite, SWT.SINGLE | SWT.READ_ONLY);
            fCombo.setFont(composite.getFont());
            fCombo.setItems(items);

            int max = 0;
            for (int i = 0; i < items.length; i++)
                if (items[i].length() > max)
                    max = items[i].length();

            fCombo.setLayoutData(createGridData(1,
                    GridData.HORIZONTAL_ALIGN_FILL,
                    fCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x));

            updateWidget();

            fCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    comboSelected(((Combo) e.widget).getSelectionIndex());
                }
            });
        }

        protected void comboSelected(int index) {
            getPreferences().put(getKey(), fValues[index]);
            setChanged();
            notifyObservers(fValues[index]);
        }

        @Override
        protected void updateWidget() {
            if (getKey() != null) {
                fCombo.setEnabled(getEnabled());
                fCombo.setText(getSelectedItem());
            } else {
                fCombo.setText("");
                fCombo.setEnabled(false);
            }
        }

        public String getSelectedItem() {
            final String selected = getPreferences().get(getKey());
            for (int i = 0; i < fValues.length; i++) {
                if (fValues[i].equals(selected)) {
                    return fItems[i];
                }
            }
            return "";
        }

        public boolean hasValue(String value) {
            return value.equals(getPreferences().get(getKey()));
        }

        @Override
        public Control getControl() {
            return fCombo;
        }
    }

    protected class MinMaxValidator implements IInputValidator {
        int fMinValue;
        int fMaxValue;

        protected MinMaxValidator() {
            super();
        }

        public MinMaxValidator(int minValue, int maxValue) {
            this.fMinValue = minValue;
            this.fMaxValue = maxValue;
        }

        public String isValid(String trimInput) {
            int number;

            try {
                number = Integer.parseInt(trimInput);
            } catch (NumberFormatException x) {
                return Boolean.toString(false);
            }

            if (number < fMinValue)
                return Boolean.toString(false);
            if (number > fMaxValue)
                return Boolean.toString(false);
            return Boolean.toString(true);
        }
    }

    protected class NumberPreference extends Preference {

        private MinMaxValidator fValidator;
        private final Label fNumberLabel;
        private final Text fNumberText;

        protected int fSelected;
        protected int fOldSelected;

        public NumberPreference(Composite composite, int numColumns,
                FormatterPreferences workingValues, String key,
                MinMaxValidator validator, String text, boolean compact) {
            super(workingValues, key);

            if (compact) {
                fNumberLabel = createCompactLabel(numColumns - 1, composite,
                        text, SWT.RIGHT, 40);
            } else {
                fNumberLabel = createLabel(numColumns - 1, composite, text,
                        GridData.FILL_HORIZONTAL);
            }
            fNumberText = new Text(composite, SWT.SINGLE | SWT.BORDER
                    | SWT.RIGHT);
            fNumberText.setFont(composite.getFont());

            final int length = Integer.toString(validator.fMaxValue).length() + 3;
            fNumberText.setLayoutData(createGridData(1,
                    (compact ? GridData.HORIZONTAL_ALIGN_BEGINNING
                            : GridData.HORIZONTAL_ALIGN_END), fPixelConverter
                            .convertWidthInCharsToPixels(length)));

            fValidator = validator;

            updateWidget();

            fNumberText.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    NumberPreference.this.focusGained();
                }

                public void focusLost(FocusEvent e) {
                    NumberPreference.this.focusLost();
                }
            });

            fNumberText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    fieldModified();
                }
            });
        }

        private IStatus createErrorStatus(MinMaxValidator validator) {
            int min, max;
            String rangeText = "";
            if (validator != null) {
                min = validator.fMinValue;
                max = validator.fMaxValue;
                rangeText = Integer.toString(min) + " and "
                        + Integer.toString(max);
            }
            return new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, 0,
                    "Inavlid number value. Should be between - " + rangeText,
                    null);
        }

        protected void focusGained() {
            fOldSelected = fSelected;
            fNumberText.setSelection(0, fNumberText.getCharCount());
        }

        protected void focusLost() {
            updateStatus(null);
            final String input = fNumberText.getText();
            if (!Boolean.parseBoolean(fValidator.isValid(input)))
                fSelected = fOldSelected;
            else
                fSelected = Integer.parseInt(input);
            if (fSelected != fOldSelected) {
                saveSelected();
                fNumberText.setText(Integer.toString(fSelected));
            }
        }

        protected void fieldModified() {
            final String trimInput = fNumberText.getText().trim();
            final boolean valid = Boolean.parseBoolean(fValidator
                    .isValid(trimInput));

            updateStatus(valid ? null : createErrorStatus(fValidator));

            if (valid) {
                final int number = Integer.parseInt(trimInput);
                if (fSelected != number) {
                    fSelected = number;
                    saveSelected();
                }
            }
        }

        private void saveSelected() {
            getPreferences().put(getKey(), Integer.toString(fSelected));
            setChanged();
            notifyObservers();
        }

        @Override
        protected void updateWidget() {
            final boolean hasKey = getKey() != null;

            fNumberLabel.setEnabled(hasKey && getEnabled());
            fNumberText.setEnabled(hasKey && getEnabled());

            if (hasKey) {
                String s = getPreferences().get(getKey());
                try {
                    fSelected = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    final String message = "Invalid key - " + getKey();
                    CeylonPlugin
                            .getInstance()
                            .getLog()
                            .log(new Status(IStatus.ERROR,
                                    CeylonPlugin.PLUGIN_ID, IStatus.OK,
                                    message, e));
                    s = "";
                }
                fNumberText.setText(s);
            } else {
                fNumberText.setText("");
            }
        }

        @Override
        public Control getControl() {
            return fNumberText;
        }
    }

    protected final class StringPreference extends Preference {

        protected class Validator {
            boolean isValid(String input) {
                return input != null;
            }
        }

        private final Label fLabel;

        private final Text fText;

        private IInputValidator fInputValidator;

        protected String fSelected;

        protected String fOldSelected;

        public StringPreference(Composite composite, int numColumns,
                FormatterPreferences workingValues, String key, String text,
                IInputValidator inputValidator) {
            super(workingValues, key);

            fInputValidator = inputValidator;

            fLabel = new Label(composite, SWT.NONE);
            fLabel.setFont(composite.getFont());
            fLabel.setText(text);

            fLabel.setLayoutData(createGridData(numColumns - 1,
                    GridData.HORIZONTAL_ALIGN_BEGINNING, SWT.DEFAULT));

            fText = new Text(composite, SWT.SINGLE | SWT.BORDER);
            fText.setFont(composite.getFont());

            final int length = 30;
            fText.setLayoutData(createGridData(1,
                    GridData.HORIZONTAL_ALIGN_BEGINNING,
                    fPixelConverter.convertWidthInCharsToPixels(length)));

            updateWidget();

            fText.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    StringPreference.this.focusGained();
                }

                public void focusLost(FocusEvent e) {
                    StringPreference.this.focusLost();
                }
            });

            fText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    fieldModified();
                }
            });
        }

        private IStatus createErrorStatus(String errorText) {
            return new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, 0,
                    errorText, null);

        }

        protected void focusGained() {
            fOldSelected = fSelected;
            fText.setSelection(0, fText.getCharCount());
        }

        protected void focusLost() {
            updateStatus(null);
            final String input = fText.getText();
            if (fInputValidator != null
                    && fInputValidator.isValid(input) != null)
                fSelected = fOldSelected;
            else
                fSelected = input;
            if (fSelected != fOldSelected) {
                saveSelected();
                fText.setText(fSelected);
            }
        }

        protected void fieldModified() {
            final String text = fText.getText();
            final String errorText = fInputValidator != null ? fInputValidator
                    .isValid(text) : null;
            if (errorText == null) {
                updateStatus(null);
                if (fSelected != text) {
                    fSelected = text;
                    saveSelected();
                }
            } else
                updateStatus(createErrorStatus(errorText));
        }

        private void saveSelected() {
            getPreferences().put(getKey(), fSelected);
            setChanged();
            notifyObservers();
        }

        @Override
        protected void updateWidget() {
            final boolean hasKey = getKey() != null;

            fLabel.setEnabled(hasKey && getEnabled());
            fText.setEnabled(hasKey && getEnabled());

            if (hasKey) {
                fSelected = getPreferences().get(getKey());
                fText.setText(fSelected);
            } else {
                fText.setText("");
            }
        }

        @Override
        public Control getControl() {
            return fText;
        }
    }
    
    private static class PageLayout extends Layout {

        private final ScrolledComposite fContainer;
        private final int fMinimalWidth;
        private final int fMinimalHight;

        private PageLayout(ScrolledComposite container, int minimalWidth,
                int minimalHight) {
            fContainer = container;
            fMinimalWidth = minimalWidth;
            fMinimalHight = minimalHight;
        }

        @Override
        public Point computeSize(Composite composite, int wHint, int hHint,
                boolean force) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
                return new Point(wHint, hHint);
            }

            int x = fMinimalWidth;
            int y = fMinimalHight;
            Control[] children = composite.getChildren();
            for (int i = 0; i < children.length; i++) {
                Point size = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT,
                        force);
                x = Math.max(x, size.x);
                y = Math.max(y, size.y);
            }

            Rectangle area = fContainer.getClientArea();
            if (area.width > x) {
                fContainer.setExpandHorizontal(true);
            } else {
                fContainer.setExpandHorizontal(false);
            }

            if (area.height > y) {
                fContainer.setExpandVertical(true);
            } else {
                fContainer.setExpandVertical(false);
            }

            if (wHint != SWT.DEFAULT) {
                x = wHint;
            }
            if (hHint != SWT.DEFAULT) {
                y = hHint;
            }

            return new Point(x, y);
        }

        @Override
        public void layout(Composite composite, boolean force) {
            Rectangle rect = composite.getClientArea();
            Control[] children = composite.getChildren();
            for (int i = 0; i < children.length; i++) {
                children[i].setSize(rect.width, rect.height);
            }
        }
    }

    protected PixelConverter fPixelConverter;

    protected FormatterPreferences workingValues;

    private FormatterTabPage.ModificationListener fModifyListener;

    protected boolean ideMode;

    public FormatterTabPage(
            FormatterTabPage.ModificationListener modifyListener,
            FormatterPreferences workingValues) {
        this();
        this.workingValues = workingValues;
        fModifyListener = modifyListener;
        if (fModifyListener instanceof FormatterModifyProfileDialog) {
            ((FormatterModifyProfileDialog)fModifyListener)
                .setHelpAvailable(false);
            FormatterModifyProfileDialog
                .setDialogHelpAvailable(false);
            this.ideMode = ((FormatterModifyProfileDialog)fModifyListener)
                .isProjectSpecific();
        }
    }

    public FormatterTabPage() {
        fDialogSettings = CeylonPlugin.getInstance().getDialogSettings();
    }

    public void setWorkingValues(FormatterPreferences workingValues) {
        this.workingValues = workingValues;
    }

    public void setModifyListener(ModificationListener modifyListener) {
        fModifyListener = modifyListener;
    }

    public Composite createContents(Composite parent) {
        final int numColumns = 4;

        if (fPixelConverter == null) {
            fPixelConverter = new PixelConverter(parent);
        }

        final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        sashForm.setFont(parent.getFont());

        Composite scrollContainer = new Composite(sashForm, SWT.NONE);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        scrollContainer.setLayoutData(gridData);

        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        scrollContainer.setLayout(layout);

        ScrolledComposite scroll = new ScrolledComposite(scrollContainer,
                SWT.V_SCROLL | SWT.H_SCROLL);
        scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);

        final Composite settingsContainer = new Composite(scroll, SWT.NONE);
        settingsContainer.setFont(sashForm.getFont());

        scroll.setContent(settingsContainer);

        settingsContainer.setLayout(new PageLayout(scroll, 0, 0));
        settingsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                true));

        Composite settingsPane = new Composite(settingsContainer, SWT.NONE);
        settingsPane
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        layout = new GridLayout(numColumns, false);
        layout.verticalSpacing = (int) (1.5 * fPixelConverter
                .convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING));
        layout.horizontalSpacing = fPixelConverter
                .convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.marginHeight = fPixelConverter
                .convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = fPixelConverter
                .convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        settingsPane.setLayout(layout);
        doCreatePreferences(settingsPane, numColumns);

        settingsContainer.setSize(settingsContainer.computeSize(SWT.DEFAULT,
                SWT.DEFAULT));

        scroll.addControlListener(new ControlListener() {

            public void controlMoved(ControlEvent e) {
            }

            public void controlResized(ControlEvent e) {
                settingsContainer.setSize(settingsContainer.computeSize(
                        SWT.DEFAULT, SWT.DEFAULT));
            }
        });

        Label sashHandle = new Label(scrollContainer, SWT.SEPARATOR
                | SWT.VERTICAL);
        gridData = new GridData(SWT.RIGHT, SWT.FILL, false, true);
        sashHandle.setLayoutData(gridData);

        final Composite previewPane = new Composite(sashForm, SWT.NONE);
        previewPane.setLayout(createGridLayout(numColumns, true));
        previewPane.setFont(sashForm.getFont());
        doCreatePreviewPane(previewPane, numColumns);

        initializePage();

        sashForm.setWeights(new int[] { 3, 3 });
        return sashForm;
    }

    protected abstract void initializePage();

    protected abstract void doCreatePreferences(Composite composite,
            int numColumns);

    public final void makeVisible() {
        doUpdatePreview();
    }

    protected void notifyValuesModified() {
        fModifyListener.valuesModified(workingValues);
    }

    protected void updateStatus(IStatus status) {
        fModifyListener.updateStatus(status);
    }

    protected GridLayout createGridLayout(int numColumns, boolean margins) {
        final GridLayout layout = new GridLayout(numColumns, false);
        layout.verticalSpacing = fPixelConverter
                .convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = fPixelConverter
                .convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        if (margins) {
            layout.marginHeight = fPixelConverter
                    .convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = fPixelConverter
                    .convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        } else {
            layout.marginHeight = 0;
            layout.marginWidth = 0;
        }
        return layout;
    }

    protected static GridData createGridData(int numColumns, int style,
            int widthHint) {
        final GridData gd = new GridData(style);
        gd.horizontalSpan = numColumns;
        gd.widthHint = widthHint;
        return gd;
    }

    protected static Label createLabel(int numColumns, Composite parent,
            String text) {
        return createLabel(numColumns, parent, text, GridData.FILL_HORIZONTAL);
    }

    protected static Label createLabel(int numColumns, Composite parent,
            String text, int gridDataStyle) {
        final Label label = new Label(parent, SWT.WRAP);
        label.setFont(parent.getFont());
        label.setText(text);

        PixelConverter pixelConverter = new PixelConverter(parent);
        label.setLayoutData(createGridData(numColumns, gridDataStyle,
                pixelConverter.convertHorizontalDLUsToPixels(150)));
        return label;
    }

    protected static Label createCompactLabel(int numColumns, Composite parent,
            String text, int align, int width) {
        final Label label = new Label(parent, SWT.WRAP | align);
        label.setFont(parent.getFont());
        label.setText(text);

        PixelConverter pixelConverter = new PixelConverter(parent);
        label.setLayoutData(createGridData(numColumns,
                GridData.FILL_HORIZONTAL,
                pixelConverter.convertHorizontalDLUsToPixels(width)));
        return label;
    }

    protected Group createGroup(int numColumns, Composite parent, String text) {
        final Group group = new Group(parent, SWT.NONE);
        group.setFont(parent.getFont());
        group.setLayoutData(createGridData(numColumns,
                GridData.FILL_HORIZONTAL, SWT.DEFAULT));

        final GridLayout layout = new GridLayout(numColumns, false);
        layout.verticalSpacing = fPixelConverter
                .convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = fPixelConverter
                .convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.marginHeight = fPixelConverter
                .convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);

        group.setLayout(layout);
        group.setText(text);
        return group;
    }

    protected NumberPreference createNumberPref(Composite composite,
            int numColumns, String name, String key, int minValue, int maxValue) {
        final NumberPreference pref = new NumberPreference(composite,
                numColumns, workingValues, key, new MinMaxValidator(minValue,
                        maxValue), name, false);
        pref.addObserver(fUpdater);
        return pref;
    }

    protected NumberPreference createCompactNumberPref(Composite composite,
            int numColumns, String name, String key, MinMaxValidator validator) {
        final NumberPreference pref = new NumberPreference(composite,
                numColumns, workingValues, key, validator, name, true);
        pref.addObserver(fUpdater);
        return pref;
    }

    protected StringPreference createStringPref(Composite composite,
            int numColumns, String name, String key,
            IInputValidator inputValidator) {
        return createStringPref(composite, numColumns, name, key, inputValidator, true);
    }

    protected StringPreference createStringPref(Composite composite,
            int numColumns, String name, String key,
            IInputValidator inputValidator, boolean enabled) {
        StringPreference pref = new StringPreference(composite, numColumns,
                workingValues, key, name, inputValidator);
        if (!enabled)
            pref.setEnabled(false);
        pref.addObserver(fUpdater);
        return pref;
    }

    protected ComboPreference createComboPref(Composite composite,
            int numColumns, String name, String key, String[] values,
            String[] items) {
        final ComboPreference pref = new ComboPreference(composite, numColumns,
                workingValues, key, values, name, items);
        pref.addObserver(fUpdater);
        return pref;
    }

    protected CheckboxPreference createCheckboxPref(Composite composite,
            int numColumns, String name, String key, String[] values) {
        final CheckboxPreference pref = new CheckboxPreference(composite,
                numColumns, workingValues, key, values, name);
        pref.addObserver(fUpdater);
        return pref;
    }

    protected RadioPreference createRadioPref(Composite composite,
            int numColumns, String name, String key, String[] values) {
        final RadioPreference pref = new RadioPreference(composite, numColumns,
                workingValues, key, values, name);
        pref.addObserver(fUpdater);
        return pref;
    }

    protected abstract CeylonPreview doCreateCeylonPreview(Composite parent);
}
