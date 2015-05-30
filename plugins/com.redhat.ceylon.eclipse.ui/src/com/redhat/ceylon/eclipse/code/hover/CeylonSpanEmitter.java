package com.redhat.ceylon.eclipse.code.hover;

import com.github.rjeschke.txtmark.SpanEmitter;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Unit;
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
        String linkDescription;
        String linkTarget; 
        
        int indexOf = content.indexOf("|");
        boolean hasNoPlainText = indexOf == -1;
        if (hasNoPlainText) {
            int sep = content.indexOf("::");
            linkDescription = sep<0 ? content : content.substring(sep+2);
            //to match behavior of ceylon doc:
            /*if (linkDescription.startsWith("package ")) {
                linkDescription = linkDescription.substring(8);
            }
            if (linkDescription.startsWith("module ")) {
                linkDescription = linkDescription.substring(7);
            }*/
            linkTarget = content;
        }
        else {
            linkDescription = content.substring(0, indexOf);
            linkTarget = content.substring(indexOf+1, content.length()); 
        }
        
        Referenceable decl = resolveLink(linkTarget, linkScope, unit);
        String href = decl != null ? HTML.link(decl) : null;
        if (href != null) {
            out.append("<a ").append(href).append(">");
        }
        if (hasNoPlainText) {
            out.append("<code>");
        }
        out.append(linkDescription);
        if (hasNoPlainText) {
            if (decl instanceof Function) {
                out.append("()");
            }
            out.append("</code>");
        }
        if (href != null) {
            out.append("</a>");
        }
    }

    static Referenceable resolveLink(String linkTarget, Scope linkScope, Unit unit) {
        if (linkTarget.startsWith("package ")) {
            Module module = DocumentationHover.resolveModule(linkScope);
            return module.getPackage(linkTarget.substring(8).trim());
        }
        if (linkTarget.startsWith("module ")) {
            Module module = DocumentationHover.resolveModule(linkScope);
            Package p = module.getPackage(linkTarget.substring(7).trim());
            return p==null ? null : p.getModule();
        }
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
    
        return decl;
    }
}