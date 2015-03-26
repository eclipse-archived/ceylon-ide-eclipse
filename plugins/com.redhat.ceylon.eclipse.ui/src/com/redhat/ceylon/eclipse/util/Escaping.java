package com.redhat.ceylon.eclipse.util;

import static java.lang.Character.charCount;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toChars;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static java.util.Arrays.asList;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class Escaping {

    public static String escape(String suggestedName) {
        if (KEYWORDS.contains(suggestedName)) {
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
                if (KEYWORDS.contains(pathPart)) {
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

    public static String escapeName(Declaration d, Unit unit) {
        return escapeAliasedName(d, d.getName(unit));
    }

    public static String escapeAliasedName(Declaration d, String alias) {
        if (alias==null) {
            return "";
        }
        int c = alias.codePointAt(0);
        if (d instanceof TypedDeclaration &&
                (isUpperCase(c) || KEYWORDS.contains(alias))) {
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

    public static final Set<String> KEYWORDS = new LinkedHashSet<String>(asList("import", "assert",
        "alias", "class", "interface", "object", "given", "value", "assign", "void", "function", 
        "assembly", "module", "package", "of", "extends", "satisfies", "abstracts", "in", "out", 
        "return", "break", "continue", "throw", "if", "else", "switch", "case", "for", "while", 
        "try", "catch", "finally", "this", "outer", "super", "is", "exists", "nonempty", "then",
        "dynamic", "new", "let"));

    public static String toInitialLowercase(String name) {
        int first = name.codePointAt(0);
        return new String(toChars(toLowerCase(first))) + 
                name.substring(charCount(first));
    }

    public static String toInitialUppercase(String name) {
        int first = name.codePointAt(0);
        return new String(toChars(toUpperCase(first))) + 
                name.substring(charCount(first));
    }

}
