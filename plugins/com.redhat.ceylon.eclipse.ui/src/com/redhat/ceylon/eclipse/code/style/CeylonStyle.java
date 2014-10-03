package com.redhat.ceylon.eclipse.code.style;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ceylon.formatter.options.saveProfile_;

import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.ConfigWriter;
import com.redhat.ceylon.eclipse.code.style.FormatterProfileManager.Profile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.*;


/**
 * Utility query and update for style options
 * 
 */
public class CeylonStyle {

    private CeylonStyle() {
        // only static methods
    }

    private static final String PREF_STYLE_FORMATTER_PROFILE = "formattool.profile";

    public static String getFormatterProfile(IProject project) {
        CeylonConfig config = CeylonConfig.createFromLocalDir(project
                .getLocation().toFile());
        if (config != null
                && config.isOptionDefined(PREF_STYLE_FORMATTER_PROFILE)) {
            return config.getOption(PREF_STYLE_FORMATTER_PROFILE);
        } else {
            return DEFAULT_PROFILE_NAME;
        }
    }

    public static boolean setFormatterProfile(IProject project, String name) {
        CeylonConfig options = CeylonConfig.createFromLocalDir(project
                .getLocation().toFile());
        options.setOption(PREF_STYLE_FORMATTER_PROFILE, name);
        return writeProjectConfig(project, options);
    }

    public static void writeProfileToFile(Profile profile, File file)
            throws CoreException {
        try {
            saveProfile_.saveProfile(
                    profile.getSettings(),
                    profile.getName(),
                    file.isDirectory() ? file.getAbsolutePath() : file
                            .getParent());
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR,
                    CeylonPlugin.PLUGIN_ID, e.getMessage()));
        }
    }

    private static boolean writeProjectConfig(IProject project,
            CeylonConfig options) {
        if (project != null) {
            try {
                ConfigWriter.write(options, new File(project.getLocation()
                        .toFile(), ".ceylon/config"));
                return true;
            } catch (IOException e) {
                CeylonPlugin.getInstance().getLog()
                        .log(new Status(IStatus.ERROR, 
                                CeylonPlugin.PLUGIN_ID, e.getMessage()));
                return false;
            }
        } else {
            return false;
        }
    }
}
