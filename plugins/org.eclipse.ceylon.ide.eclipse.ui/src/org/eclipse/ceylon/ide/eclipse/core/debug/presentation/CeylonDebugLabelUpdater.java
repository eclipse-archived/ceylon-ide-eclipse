package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import java.util.regex.Matcher;

import org.eclipse.ceylon.model.typechecker.model.Declaration;

public interface CeylonDebugLabelUpdater {
    Matcher matches(String existingLabel);
    String updateLabel(Matcher matcher, Declaration declaration);
}