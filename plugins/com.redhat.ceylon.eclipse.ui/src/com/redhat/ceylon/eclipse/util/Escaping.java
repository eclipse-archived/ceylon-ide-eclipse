package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.keywords;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public class Escaping {

    public static String escape(String suggestedName) {
        if (keywords.contains(suggestedName)) {
            return "\\i" + suggestedName;
        }
        else {
            return suggestedName;
        }
    }

    public static String escapePackageName(Package p) {
        List<String> path = p.getName();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<path.size(); i++) {
            String pathPart = path.get(i);
            if (!pathPart.isEmpty()) {
                if (CeylonTokenColorer.keywords.contains(pathPart)) {
                    pathPart = "\\i" + pathPart;
                }
                sb.append(pathPart);
                if (i<path.size()-1) sb.append('.');
            }
        }
        return sb.toString();
    }

    public static String escapeName(DeclarationWithProximity d) {
        return escapeAliasedName(d.getDeclaration(), d.getName());
    }

    public static String escapeName(Declaration d) {
        return escapeAliasedName(d, d.getName());
    }

    public static String escapeAliasedName(Declaration d, String alias) {
        if (alias==null) {
            return "";
        }
        char c = alias.charAt(0);
        if (d instanceof TypedDeclaration &&
                (isUpperCase(c) || 
                        keywords.contains(alias))) {
            return "\\i" + alias;
        }
        else if (d instanceof TypeDeclaration &&
                isLowerCase(c) && !d.isAnonymous()) {
            return "\\I" + alias;
        }
        else {
            return alias;
        }
    }

}
