package com.redhat.ceylon.eclipse.platform;

import com.redhat.ceylon.eclipse.java2ceylon.PlatformJ2C;
import com.redhat.ceylon.ide.common.platform.PlatformServices;
import com.redhat.ceylon.ide.common.platform.platformServices_;

public class platformJ2C implements PlatformJ2C {

    @Override
    public PlatformServices platformServices() {
        return eclipsePlatformServices_.get_();
    }
}
