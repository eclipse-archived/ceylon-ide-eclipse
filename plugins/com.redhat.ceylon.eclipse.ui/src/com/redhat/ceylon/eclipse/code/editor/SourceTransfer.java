package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class SourceTransfer extends ByteArrayTransfer {
    
    public static final SourceTransfer INSTANCE = new SourceTransfer();

    private static final String TYPE_NAME = "ceylon-source-transfer-format" + 
            System.currentTimeMillis();
    
    private static final int TYPEID = registerType(TYPE_NAME);
    
    public static String text;
    
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
        super.javaToNative(new byte[1], transferData);
    }

    @Override
    protected String nativeToJava(TransferData transferData) {
        return text;
    }

}
