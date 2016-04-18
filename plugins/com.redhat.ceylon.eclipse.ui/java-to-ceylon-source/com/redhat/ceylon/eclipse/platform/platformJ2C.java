package com.redhat.ceylon.eclipse.platform;

import org.eclipse.ltk.core.refactoring.Change;

import com.redhat.ceylon.eclipse.java2ceylon.PlatformJ2C;
import com.redhat.ceylon.ide.common.platform.PlatformServices;

public class platformJ2C implements PlatformJ2C {

    @Override
    public PlatformServices platformServices() {
        return eclipsePlatformServices_.get_();
    }
    

    @Override
    public Change getNativeChange(Object commonChange) {
        if (commonChange instanceof EclipseCompositeChange) {
            return ((EclipseCompositeChange) commonChange).getNativeChange();
        } else if (commonChange instanceof EclipseTextChange) {
            return ((EclipseTextChange) commonChange).getNativeChange();
        }
        return null;
    }
}
