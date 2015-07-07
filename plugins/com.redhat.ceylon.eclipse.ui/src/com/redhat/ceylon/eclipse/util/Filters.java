package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.ENABLE_OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IJavaElement;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;

public class Filters {
    
    private List<Pattern> filters;
    private List<Pattern> packageFilters;
    private List<Pattern> moduleFilters;
    
    public Filters() {
        initFilters();
    }
    
    protected String getFilterListAsString(String preference) {
        return getPreferences().getString(preference);
    }
    
    public void initFilters() {
        filters = new ArrayList<Pattern>();
        packageFilters = new ArrayList<Pattern>();
        moduleFilters = new ArrayList<Pattern>();
        parseFilters(getFilterListAsString(FILTERS));
        if (getPreferences().getBoolean(enableExtraFiltersPref())) {
            parseFilters(getFilterListAsString(extraFiltersPref())); 
        }
    }
    
    protected String extraFiltersPref() {
        return OPEN_FILTERS;
    }

    protected String enableExtraFiltersPref() {
        return ENABLE_OPEN_FILTERS;
    }

    private void parseFilters(String filtersString) {
        if (!filtersString.trim().isEmpty()) {
            String[] regexes = 
                    filtersString
                        .replaceAll("\\(\\w+\\)", "")
                        .replace(".", "\\.")
                        .replace("*", ".*")
                        .split(",");
            for (String regex: regexes) {
                regex = regex.trim();
                if (!regex.isEmpty()) {
                    filters.add(Pattern.compile(regex));
                    if (!regex.contains("::") && regex.endsWith(".*")) {
                        moduleFilters.add(Pattern.compile(regex));
                    }
                    if (regex.endsWith("::.*")) {
                        regex = regex.substring(0, regex.length()-4);
                    }
                    if (!regex.contains("::")) {
                        packageFilters.add(Pattern.compile(regex));
                    }
                }
            }
        }
    }
    
    public boolean isFiltered(Declaration declaration) {
        if (declaration.getName().contains("__") &&
                declaration.isAnnotation()) {
            //actually what we should really do is filter
            //out all constructors for Java annotations
            return true;
        }
        if (!filters.isEmpty()) {
            String name = 
                    declaration.getQualifiedNameString();
            for (Pattern filter: filters) {
                if (filter.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFiltered(Module module) {
        if (!moduleFilters.isEmpty()) {
            String name = module.getNameAsString();
            for (Pattern filter: moduleFilters) {
                if (filter.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFiltered(Package pack) {
        if (!packageFilters.isEmpty()) {
            String name = pack.getNameAsString();
            for (Pattern filter: packageFilters) {
                if (filter.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFiltered(IJavaElement enclosingElement) {
        if (!packageFilters.isEmpty()) {
            IJavaElement pf = 
                    enclosingElement.getAncestor(
                            IJavaElement.PACKAGE_FRAGMENT);
            if (pf!=null) {
                for (Pattern filter: packageFilters) {
                    String name = pf.getElementName();
                    if (filter.matcher(name).matches()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
