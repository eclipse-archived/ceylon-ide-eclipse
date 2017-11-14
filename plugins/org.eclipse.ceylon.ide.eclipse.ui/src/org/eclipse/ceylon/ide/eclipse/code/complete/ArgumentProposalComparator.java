/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isNameMatching;

import java.util.Comparator;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;

@Deprecated
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
        Declaration xd = x.getDeclaration();
        Declaration yd = y.getDeclaration();
        boolean xdepr = xd.isDeprecated();
        boolean ydepr = yd.isDeprecated();
        if (xdepr && !ydepr) {
            return 1;
        }
        if (!xdepr && ydepr) {
            return -1;
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
        return xd.getQualifiedNameString()
                .compareTo(yd.getQualifiedNameString());
    }
}