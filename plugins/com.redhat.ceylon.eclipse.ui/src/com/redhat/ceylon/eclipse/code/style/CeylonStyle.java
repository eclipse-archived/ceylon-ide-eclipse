package com.redhat.ceylon.eclipse.code.style;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import ceylon.formatter.options.saveProfile_;

import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.ConfigFinder;
import com.redhat.ceylon.common.config.ConfigWriter;
import com.redhat.ceylon.eclipse.code.style.FormatterProfileManager.Profile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * @author Akber Choudhry
 * Utility query and update for style options
 * 
 */
public class CeylonStyle {

    private CeylonStyle() {

    }

    private static final String PREF_STYLE_FORMATTER_PROFILE = "style.formatterProfile";
    private static final String PREF_STYLE_NEW_MODULE_VERSION = "style.newModuleVersion";
    private static final String PREF_STYLE_NEW_MODULE_AUTHOR = "style.newModuleAuthor";

    public static String getFormatterProfile(IProject project) {
        CeylonConfig config = readProjectStyle(project);
        if (config != null && config.isOptionDefined(PREF_STYLE_FORMATTER_PROFILE)) {
            return config.getOption(PREF_STYLE_FORMATTER_PROFILE);
        } else {
            return "default";
        }
    }
    
    public static String getnewModuleVersion(IProject project) {
        CeylonConfig config = readProjectStyle(project);
        if (config != null && config.isOptionDefined(PREF_STYLE_NEW_MODULE_VERSION)) {
            return config.getOption(PREF_STYLE_NEW_MODULE_VERSION);
        } else {
            return "1.0.0";
        }       
    }
    public static String getnewModuleAuthor(IProject project) {
        CeylonConfig config = readProjectStyle(project);
        if (config != null && config.isOptionDefined(PREF_STYLE_NEW_MODULE_AUTHOR)) {
            return config.getOption(PREF_STYLE_NEW_MODULE_AUTHOR);
        } else {
            return System.getProperty("user.name");
        }       
    }
    
    public static boolean setnewModuleVersion(IProject project, String newModuleVersion) {
        CeylonConfig options = readProjectStyle(project);
        options.setOption(PREF_STYLE_NEW_MODULE_VERSION, newModuleVersion);
        return writeProjectStyle(project, options);
    }

    public static boolean setnewModuleAuthor(IProject project, String newModuleAuthor) {
        CeylonConfig options = readProjectStyle(project);
        options.setOption(PREF_STYLE_NEW_MODULE_AUTHOR, newModuleAuthor);
        return writeProjectStyle(project, options);   
    }
    
    public static boolean setFormatterProfile(IProject project, String name) {
        CeylonConfig options = readProjectStyle(project);
        options.setOption(PREF_STYLE_FORMATTER_PROFILE, name);
        return writeProjectStyle(project, options);        
    }
    
    public static void writeProfileToFile(Profile profile, File file)
            throws CoreException {
        try {
            saveProfile_.saveProfile(profile.getSettings(), profile.getName(),
                    file.isDirectory() ? file.getAbsolutePath() : file.getParent());
        } catch (Exception e) {
            throw new CoreException(new StatusInfo(IStatus.ERROR, e.getMessage()));
        }
    }
    
// FIXME very inefficient to read one by one
    private static CeylonConfig readProjectStyle(IProject project) {
        try {
            return new ConfigFinder("style", null).loadLocalConfig(project
                    .getLocation().toFile());
        } catch (IOException e) {
            CeylonPlugin.getInstance().getLog().log(
                    new StatusInfo(IStatus.ERROR, e.getMessage()));
            return null;
        }
    }
    
 // FIXME very inefficient to read one by one
    private static boolean writeProjectStyle(IProject project, CeylonConfig options) {
        if (project != null) {
            try {
                ConfigWriter.write(options, new File(project.getLocation()
                        .toFile(), ".ceylon/style"));
                return true;
            } catch (IOException e) {
                CeylonPlugin.getInstance().getLog().log(
                        new StatusInfo(IStatus.ERROR, e.getMessage()));
                return false;
            }
        } else {
            return false;
        }
    }  
}
