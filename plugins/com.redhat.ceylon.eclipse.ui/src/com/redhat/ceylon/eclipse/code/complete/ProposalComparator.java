package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.util.Types.getResultType;
import static java.lang.Character.isUpperCase;

import java.util.Comparator;

import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;

final class ProposalComparator 
        implements Comparator<DeclarationWithProximity> {
    private final String prefix;
    private final ProducedType type;

    ProposalComparator(String prefix, ProducedType type) {
        this.prefix = prefix;
        this.type = type;
    }

    public int compare(DeclarationWithProximity x, DeclarationWithProximity y) {
        try {
            boolean xbt = x.getDeclaration() instanceof NothingType;
            boolean ybt = y.getDeclaration() instanceof NothingType;
            if (xbt&&ybt) {
                return 0;
            }
            if (xbt&&!ybt) {
                return 1;
            }
            if (ybt&&!xbt) {
                return -1;
            }
            ProducedType xtype = getResultType(x.getDeclaration());
            ProducedType ytype = getResultType(y.getDeclaration());
            boolean xbottom = xtype!=null && xtype.isNothing();
            boolean ybottom = ytype!=null && ytype.isNothing();
            if (xbottom && !ybottom) {
                return 1;
            }
            if (ybottom && !xbottom) {
                return -1;
            }
            String xName = x.getName();
            String yName = y.getName();
            boolean yUpperCase = isUpperCase(yName.codePointAt(0));
            boolean xUpperCase = isUpperCase(xName.codePointAt(0));
            if (!prefix.isEmpty()) {
                boolean upperCasePrefix = isUpperCase(prefix.codePointAt(0));
                if (!xUpperCase && yUpperCase) {
                    return upperCasePrefix ? 1 : -1;
                }
                else if (xUpperCase && !yUpperCase) {
                    return upperCasePrefix ? -1 : 1;
                }
            }
            if (type!=null) {
                boolean xassigns = xtype!=null && xtype.isSubtypeOf(type);
                boolean yassigns = ytype!=null && ytype.isSubtypeOf(type);
                if (xassigns && !yassigns) {
                    return -1;
                }
                if (yassigns && !xassigns) {
                    return 1;
                }
                if (xassigns && yassigns) {
                    boolean xtd = x.getDeclaration() instanceof TypedDeclaration;
                    boolean ytd = y.getDeclaration() instanceof TypedDeclaration;
                    if (xtd && !ytd) {
                        return -1;
                    }
                    if (ytd && !xtd) {
                        return 1;
                    }
                }
            }
            if (x.getProximity()!=y.getProximity()) {
                return new Integer(x.getProximity()).compareTo(y.getProximity());
            }
            //if (!prefix.isEmpty() && isLowerCase(prefix.charAt(0))) {
            if (!xUpperCase && yUpperCase) {
                return -1;
            }
            else if (xUpperCase && !yUpperCase) {
                return 1;
            }
            int nc = xName.compareTo(yName);
            if (nc==0) {
                String xqn = x.getDeclaration().getQualifiedNameString();
                String yqn = y.getDeclaration().getQualifiedNameString();
                return xqn.compareTo(yqn);
            }
            else {
                return nc;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}