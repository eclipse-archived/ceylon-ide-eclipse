package com.redhat.ceylon.eclipse.core.debug.hover;

import org.eclipse.debug.core.model.IVariable;

class DebugHoverInput {

    private String text;
    private IVariable variable;

    DebugHoverInput(IVariable variable, String text) {
        this.text = text;
        this.variable = variable;
    }
    
    String getText() {
        return text;
    }
    
    IVariable getVariable() {
        return variable;
    }

}
