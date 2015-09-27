package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isNameMatching;

import java.util.Comparator;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;

final class ArgumentProposalComparator 
        implements Comparator<DeclarationWithProximity> {
    private final String exactName;

    ArgumentProposalComparator(String exactName) {
        this.exactName = exactName;
    }
    
    @Override
    public int compare(
            DeclarationWithProximity x, 
            DeclarationWithProximity y) {
        String xname = x.getName();
        String yname = y.getName();
        if (exactName!=null) {
            boolean xhit = xname.equals(exactName);
            boolean yhit = yname.equals(exactName);
            if (xhit && !yhit) {
                return -1;
            }
            if (yhit && !xhit) {
                return 1;
            }
            xhit = isNameMatching(xname, exactName);
            yhit = isNameMatching(yname, exactName);
            if (xhit && !yhit) {
                return -1;
            }
            if (yhit && !xhit) {
                return 1;
            }
        }
        int xp = x.getProximity();
        int yp = y.getProximity();
        int p = xp-yp;
        if (p!=0) {
            return p;
        }
        int c = xname.compareTo(yname);
        if (c!=0) {
            return c;  
        }
        Declaration xd = x.getDeclaration();
        Declaration yd = y.getDeclaration();
        return xd.getQualifiedNameString()
                .compareTo(yd.getQualifiedNameString());
    }
}