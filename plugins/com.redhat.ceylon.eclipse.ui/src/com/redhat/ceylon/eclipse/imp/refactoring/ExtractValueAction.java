package com.redhat.ceylon.eclipse.imp.refactoring;


public class ExtractValueAction extends RefactoringAction {
    public void run() {
        new ExtractValueRefactoringAction(getEditor()).run();
    }
}