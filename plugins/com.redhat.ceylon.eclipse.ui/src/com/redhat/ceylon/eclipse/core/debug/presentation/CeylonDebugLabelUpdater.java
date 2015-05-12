package com.redhat.ceylon.eclipse.core.debug.presentation;

import java.util.regex.Matcher;

import com.redhat.ceylon.model.typechecker.model.Declaration;

public interface CeylonDebugLabelUpdater {
    Matcher matches(String existingLabel);
    String updateLabel(Matcher matcher, Declaration declaration);
}