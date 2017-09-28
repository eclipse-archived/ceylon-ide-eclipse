package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import org.eclipse.ltk.core.refactoring.Change;

import org.eclipse.ceylon.ide.common.platform.PlatformServices;
import org.eclipse.ceylon.ide.common.platform.TextChange;

public interface PlatformJ2C {
    PlatformServices platformServices();
    
    Change getNativeChange(Object cc);
    
    TextChange newChange(String desc, Object doc);
}