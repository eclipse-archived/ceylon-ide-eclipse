package com.redhat.ceylon.eclipse.code.editor;

import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class ImportsTransfer extends ByteArrayTransfer {
    
    public static final ImportsTransfer INSTANCE = new ImportsTransfer();

    private static final String TYPE_NAME = "ceylon-source-with-imports-transfer-format" + 
            System.currentTimeMillis();
    
    private static final int TYPEID = registerType(TYPE_NAME);
    
    public static List<Declaration> imports;
    
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
        if (isSupportedType(transferData)) {
            //TODO: serialize qualified names to NSStrings
            imports = (List<Declaration>) object;
            super.javaToNative(new byte[0], transferData);
        }
    }

    @Override
    protected List<Declaration> nativeToJava(TransferData transferData) {
        if (isSupportedType(transferData)) {
            //TODO: unserialize qualified names from NSStrings
            return imports;
        }
        else {
            return null;
        }
    }

}
