package com.redhat.ceylon.eclipse.imp.open;

import static com.redhat.ceylon.eclipse.util.Util.getCurrentEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenCeylonDeclarationHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        new OpenDeclarationAction(getCurrentEditor()).run();
        return null;
    }
        
}