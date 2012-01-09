package com.redhat.ceylon.eclipse.imp.preferences;

import static org.eclipse.jface.preference.PreferenceConverter.setDefault;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonPreferenceInitializer extends AbstractPreferenceInitializer {

    public static final RGB BRIGHT_BLUE = new RGB(0,120,255);
    public static final RGB PURPLE = new RGB(63,31,191);
    public static final RGB BLACK = new RGB(0,0,0);
    public static final RGB DARK_BLUE = new RGB(0,0,128);
    public static final RGB BLUE = new RGB(0,0,255);
    public static final RGB GREY = new RGB(192, 192, 192);
    public static final RGB DARK_GREY = new RGB(128, 128, 128);
    public static final RGB DARK_MAGENTA = new RGB(128, 0, 128);
    public static final RGB DARK_CYAN = new RGB(0, 128, 128);
    public static final RGB DARK_GREEN = new RGB(0, 128, 0);
    
    @Override
    public void initializeDefaultPreferences() {
        //IEclipsePreferences node = DefaultScope.INSTANCE.getNode(CeylonPlugin.PLUGIN_ID);
        IPreferenceStore store = CeylonPlugin.getInstance().getPreferenceStore();
        setDefault(store, "color.keywords", DARK_MAGENTA);
        setDefault(store, "color.annotations", DARK_CYAN);
        setDefault(store, "color.strings", BLUE);
        setDefault(store, "color.annotationstrings", DARK_GREY);
        setDefault(store, "color.numbers", BLUE);
        setDefault(store, "color.types", DARK_BLUE);
        setDefault(store, "color.identifiers", BLACK);
        setDefault(store, "color.comments", DARK_GREY);
        setDefault(store, "color.todos", BRIGHT_BLUE);
    }

}
