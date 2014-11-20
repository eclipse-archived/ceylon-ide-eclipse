package com.redhat.ceylon.eclipse.core.debug;

import java.util.regex.Matcher;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public interface CeylonDebugLabelUpdater {
    Matcher matches(String existingLabel);
    String updateLabel(Matcher matcher, Declaration declaration);
}