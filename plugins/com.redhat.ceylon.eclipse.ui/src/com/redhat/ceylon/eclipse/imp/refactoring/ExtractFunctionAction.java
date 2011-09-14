package com.redhat.ceylon.eclipse.imp.refactoring;


public class ExtractFunctionAction extends RefactoringAction {
    public void run() {
        new ExtractFunctionRefactoringAction(getEditor()).run();
    }
}