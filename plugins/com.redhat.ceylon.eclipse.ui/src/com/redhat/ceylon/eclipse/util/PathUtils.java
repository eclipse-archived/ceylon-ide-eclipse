package com.redhat.ceylon.eclipse.util;

import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.ide.common.util.Path;

public class PathUtils {
    public static Path toCommonPath(IPath path) {
        return new Path(path.toString());
    }

    public static IPath toIPath(Path path) {
        return new org.eclipse.core.runtime.Path(path.toString());
    }

}
