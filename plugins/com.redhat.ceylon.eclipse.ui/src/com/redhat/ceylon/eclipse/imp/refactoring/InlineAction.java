package com.redhat.ceylon.eclipse.imp.refactoring;


public class InlineAction extends RefactoringAction {
    public void run() {
        new InlineRefactoringAction(getEditor()).run();
    }
}