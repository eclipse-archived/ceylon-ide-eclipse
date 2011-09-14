package com.redhat.ceylon.eclipse.imp.refactoring;


public class RenameAction extends RefactoringAction {        
    public void run() {
        new RenameRefactoringAction(getEditor()).run();
    }
}