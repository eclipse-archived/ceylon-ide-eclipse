package org.eclipse.ceylon.ide.eclipse.util;

public class InteropUtils {
    public static String toJavaString(ceylon.language.String string) {
        return string==null ? null : string.toString();
    }
    public static Boolean toJavaBoolean(ceylon.language.Boolean bool) {
        return bool==null ? null : bool.booleanValue();
    }
    public static ceylon.language.String toCeylonString(String string) {
        return string==null ? null : ceylon.language.String.instance(string);
    }
    public static ceylon.language.Boolean toCeylonBoolean(Boolean bool) {
        return bool==null ? null : ceylon.language.Boolean.instance(bool);
    }
}
