package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.core.refactoring.TextChange;

public interface ExtractLinkedModeEnabled {
    IRegion getTypeRegion();
    void setTypeRegion(IRegion region);

    IRegion getDecRegion();
    void setDecRegion(IRegion region);

    IRegion getRefRegion();
    void setRefRegion(IRegion region);
    
    void extractInFile(TextChange tfc) throws CoreException;
    
    String[] getNameProposals();
}
