package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.correctJ2C;


public class ImportProposals {
    private final static org.eclipse.ceylon.ide.common.correct.importProposals_ importProposals
        = correctJ2C().importProposals();
    
    public static org.eclipse.ceylon.ide.common.correct.importProposals_ importProposals() {
        return importProposals;
    }

}
