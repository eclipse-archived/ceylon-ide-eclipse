/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import ceylon.collection {
    MutableList,
    ArrayList
}

import org.eclipse.ceylon.ide.eclipse.code.parse {
    CeylonParseController
}
import org.eclipse.ceylon.ide.eclipse.code.preferences {
    CeylonPreferenceInitializer {
        parameterTypesInCompletions,
        inexactMatches,
        completion,
        linkedModeArguments,
        chainLinkedModeArguments,
        enableCompletionFilters
    }
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseProposalsHolder
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin
}
import org.eclipse.ceylon.ide.common.completion {
    CompletionContext
}
import org.eclipse.ceylon.ide.common.settings {
    CompletionOptions
}

import java.util.regex {
    Pattern
}

shared class EclipseCompletionContext(shared CeylonParseController cpc) satisfies CompletionContext {
    
    ceylonProject => cpc.ceylonProject;
    commonDocument = cpc.commonDocument;
    lastCompilationUnit => cpc.lastCompilationUnit;
    lastPhasedUnit => cpc.lastPhasedUnit;
    parsedRootNode => cpc.parsedRootNode;
    tokens => cpc.tokens;
    typeChecker => cpc.typeChecker;
    typecheckedPhasedUnit => cpc.typecheckedPhasedUnit;
    phasedUnitWhenTypechecked => cpc.phasedUnitWhenTypechecked;

    
    function createOptions() {
        value options = CompletionOptions();
        
        value prefs = CeylonPlugin.preferences;
        
        options.parameterTypesInCompletion = prefs.getBoolean(parameterTypesInCompletions);
        options.inexactMatches = prefs.getString(inexactMatches);
        options.completionMode = prefs.getString(completion);
        options.linkedModeArguments = prefs.getBoolean(linkedModeArguments);
        options.chainLinkedModeArguments = prefs.getBoolean(chainLinkedModeArguments);
        options.enableCompletionFilters = prefs.getBoolean(enableCompletionFilters);
        
        return options;
    }
    
    options = createOptions();
    
    // see CeylonCompletionProcessor.parseFilters()
    void parseFilters(MutableList<Pattern> filters, String filtersString) {
        if (!filtersString.trimmed.empty) {
            value regexes 
                    = filtersString
                    .replace("""\(\w+\)""", "")
                    .replace(".", """\.""")
                    .replace("*", ".*")
                    .split(','.equals);
            for (String regex in regexes) {
                value trimmedRegex = regex.trimmed;
                if (!trimmedRegex.empty) {
                    filters.add(Pattern.compile(trimmedRegex));
                }
            }
        }
    }
    
    shared actual List<Pattern> proposalFilters {
        value filters = ArrayList<Pattern>();
        value preferences = CeylonPlugin.preferences;
        parseFilters(filters, preferences.getString(CeylonPreferenceInitializer.filters));
        if (preferences.getBoolean(CeylonPreferenceInitializer.enableCompletionFilters)) {
            parseFilters(filters, preferences.getString(CeylonPreferenceInitializer.completionFilters));
        }
        return filters;
    }
    
    shared actual EclipseProposalsHolder proposals = EclipseProposalsHolder();
}
