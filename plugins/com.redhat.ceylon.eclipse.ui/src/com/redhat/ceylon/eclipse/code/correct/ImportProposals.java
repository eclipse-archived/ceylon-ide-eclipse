package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.correctJ2C;


public class ImportProposals {
    private final static com.redhat.ceylon.ide.common.correct.importProposals_ importProposals
        = correctJ2C().importProposals();
    
    public static com.redhat.ceylon.ide.common.correct.importProposals_ importProposals() {
        return importProposals;
    }

}
