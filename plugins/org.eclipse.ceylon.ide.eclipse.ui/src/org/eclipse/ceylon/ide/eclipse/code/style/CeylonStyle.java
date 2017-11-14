/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.style;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;

import ceylon.formatter.options.LineBreak;
import ceylon.formatter.options.Spaces;
import ceylon.formatter.options.SparseFormattingOptions;
import ceylon.formatter.options.Tabs;
import ceylon.formatter.options.VariableOptions;
import ceylon.formatter.options.crlf_;
import ceylon.formatter.options.lf_;
import ceylon.formatter.options.os_;
import ceylon.formatter.options.saveProfile_;

import org.eclipse.ceylon.common.config.CeylonConfig;
import org.eclipse.ceylon.common.config.ConfigWriter;
import org.eclipse.ceylon.ide.eclipse.code.style.FormatterProfileManager.Profile;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;

import static org.eclipse.ceylon.ide.eclipse.code.style.CeylonFormatterConstants.*;

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

    /**
     * Creates {@link SparseFormattingOptions} that respect whitespace-relevant
     * settings:
     * <ul>
     * <li>{@link SparseFormattingOptions#getIndentMode() indentMode} from
     * spaces-for-tabs and editor-tab-width</li>
     * <li>{@link SparseFormattingOptions#getLineBreak() lineBreak} from
     * document newline character</li>
     * </ul>
     */
    public static SparseFormattingOptions getEclipseWsOptions(IDocument document) {
        LineBreak lb;
        if (document instanceof IDocumentExtension4) {
            switch (((IDocumentExtension4) document).getDefaultLineDelimiter()) {
            case "\n":
                lb = lf_.get_();
                break;
            case "\r\n":
                lb = crlf_.get_();
                break;
            default:
                lb = os_.get_();
                break;
            }
        } else {
            lb = os_.get_();
        }
        VariableOptions ret = new VariableOptions(new SparseFormattingOptions());
        ret.setIndentMode(utilJ2C().indents().getIndentWithSpaces() ?
                new Spaces(utilJ2C().indents().getIndentSpaces()) :
                new Tabs(utilJ2C().indents().getIndentSpaces()));
        ret.setLineBreak(lb);
        return ret;
    }

    private static boolean writeProjectConfig(IProject project,
            CeylonConfig options) {
        if (project != null) {
            try {
                ConfigWriter.instance().write(options, new File(project.getLocation()
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
