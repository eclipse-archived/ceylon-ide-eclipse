package com.redhat.ceylon.eclipse.imp.refactoring;


public class ConvertToNamedArgumentsAction extends RefactoringAction {
    public void run() {
        new ConvertToNamedArgumentsRefactoringAction(getEditor()).run();
    }
}