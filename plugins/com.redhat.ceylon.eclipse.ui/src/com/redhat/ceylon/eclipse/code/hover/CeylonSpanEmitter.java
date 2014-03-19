package com.redhat.ceylon.eclipse.code.hover;

import com.github.rjeschke.txtmark.SpanEmitter;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.code.html.HTML;

final class CeylonSpanEmitter implements SpanEmitter {
    private final Scope linkScope;
    private final Unit unit;

    CeylonSpanEmitter(Scope linkScope, Unit unit) {
        this.linkScope = linkScope;
        this.unit = unit;
    }

    @Override
    public void emitSpan(StringBuilder out, String content) {
        String linkName;
        String linkTarget; 
        
        int indexOf = content.indexOf("|");
        if (indexOf == -1) {
            linkName = content;
            linkTarget = content;
        }
        else {
            linkName = content.substring(0, indexOf);
            linkTarget = content.substring(indexOf+1, content.length()); 
        }
        
        String href = resolveLink(linkTarget, linkScope, unit);
        if (href != null) {
            out.append("<a ").append(href).append(">");
        }
        out.append("<code>");
        int sep = linkName.indexOf("::");
        out.append(sep<0?linkName:linkName.substring(sep+2));
        out.append("</code>");
        if (href != null) {
            out.append("</a>");
        }
    }

    static String resolveLink(String linkTarget, Scope linkScope, Unit unit) {
        String declName;
        Scope scope = null;
        int pkgSeparatorIndex = linkTarget.indexOf("::");
        if (pkgSeparatorIndex == -1) {
            declName = linkTarget;
            scope = linkScope;
        } 
        else {
            String pkgName = linkTarget.substring(0, pkgSeparatorIndex);
            declName = linkTarget.substring(pkgSeparatorIndex+2, linkTarget.length());
            Module module = DocumentationHover.resolveModule(linkScope);
            if (module != null) {
                scope = module.getPackage(pkgName);
            }
        }
        
        if (scope==null || declName == null || "".equals(declName)) {
            return null; // no point in continuing. Required for non-token auto-complete.
        }
        
        String[] declNames = declName.split("\\.");
        Declaration decl = scope.getMemberOrParameter(unit, declNames[0], null, false);
        for (int i=1; i<declNames.length; i++) {
            if (decl instanceof Scope) {
                scope = (Scope) decl;
                decl = scope.getMember(declNames[i], null, false);
            }
            else {
                decl = null;
                break;
            }
        }
    
        if (decl != null) {
            String href = HTML.link(decl);
            return href;
        }
        else {
            return null;
        }
    }
}