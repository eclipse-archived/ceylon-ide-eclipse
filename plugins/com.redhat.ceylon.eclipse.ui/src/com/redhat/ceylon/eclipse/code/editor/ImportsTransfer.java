package com.redhat.ceylon.eclipse.code.editor;

import java.util.List;

import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class ImportsTransfer extends Transfer {
    
    public static final ImportsTransfer INSTANCE = new ImportsTransfer();

    private static final String TYPE_NAME = "ceylon-source-with-imports-transfer-format" + 
            System.currentTimeMillis();
    
    private static final int TYPEID = registerType(TYPE_NAME);
    
    public static List<Declaration> imports;
    
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
        //TODO: serialize qualified names to NSStrings
        imports = (List<Declaration>) object;
    }

    @Override
    protected List<Declaration> nativeToJava(TransferData transferData) {
        //TODO: unserialize qualified names from NSStrings
        return imports;
    }

}
