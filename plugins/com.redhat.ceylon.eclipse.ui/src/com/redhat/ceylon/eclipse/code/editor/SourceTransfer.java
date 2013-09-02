package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.internal.cocoa.NSString;

public class SourceTransfer extends Transfer {
    
    public static final SourceTransfer INSTANCE = new SourceTransfer();

    private static final String TYPE_NAME = "ceylon-source-transfer-format" + 
            System.currentTimeMillis();
    
    private static final int TYPEID = registerType(TYPE_NAME);
    
    public static String text;
    
    @Override
    public TransferData[] getSupportedTypes() {
        return null;
    }

    public boolean isSupportedType(TransferData transferData){
        if (transferData == null) return false;
        int[] types = getTypeIds();
        for (int i = 0; i < types.length; i++) {
            if (transferData.type == types[i]) return true;
        }
        return false;
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        text = (String) object;
        transferData.data = NSString.stringWith("");
    }

    @Override
    protected String nativeToJava(TransferData transferData) {
        return text;
    }

}
