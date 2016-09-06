package com.redhat.ceylon.eclipse.util;

public class InteropUtils {
    public static String toJavaString(ceylon.language.String string) {
        return string==null ? null : string.toString();
    }
    public static Boolean toJavaBoolean(ceylon.language.Boolean bool) {
        return bool==null ? null : bool.booleanValue();
    }
}
