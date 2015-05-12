package com.redhat.ceylon.eclipse.code.editor;

import java.util.Map;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import com.redhat.ceylon.model.typechecker.model.Declaration;

public class ImportsTransfer extends ByteArrayTransfer {
    
    public static final ImportsTransfer INSTANCE = new ImportsTransfer();

    private static final String TYPE_NAME = "ceylon-source-with-imports-transfer-format" + 
            System.currentTimeMillis();
    
    private static final int TYPEID = registerType(TYPE_NAME);
    
    public static Map<Declaration,String> imports;
    
    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        if (isSupportedType(transferData)) {
            //TODO: serialize qualified names to NSStrings
            imports = (Map) object;
            super.javaToNative(new byte[1], transferData);
        }
    }

    @Override
    protected Map<Declaration,String> nativeToJava(TransferData transferData) {
        if (isSupportedType(transferData)) {
            //TODO: unserialize qualified names from NSStrings
            return imports;
        }
        else {
            return null;
        }
    }

}
