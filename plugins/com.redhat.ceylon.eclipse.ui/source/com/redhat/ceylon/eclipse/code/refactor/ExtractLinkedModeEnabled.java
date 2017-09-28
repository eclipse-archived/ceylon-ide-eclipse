package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.jface.text.IRegion;

public interface ExtractLinkedModeEnabled {
    IRegion getTypeRegion();
    IRegion getDecRegion();
    IRegion getRefRegion();
    
    String[] getNameProposals();
}
