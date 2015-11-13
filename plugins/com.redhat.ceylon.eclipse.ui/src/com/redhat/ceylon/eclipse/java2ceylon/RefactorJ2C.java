package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.ide.common.refactoring.ExtractLinkedModeEnabled;
import com.redhat.ceylon.ide.common.refactoring.ExtractValueRefactoring;

public interface RefactorJ2C {

    ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion> newExtractValueRefactoring(
            IEditorPart editorPart);

    ExtractLinkedModeEnabled<IRegion> toExtractLinkedModeEnabled(
            ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion> refactoring);

}