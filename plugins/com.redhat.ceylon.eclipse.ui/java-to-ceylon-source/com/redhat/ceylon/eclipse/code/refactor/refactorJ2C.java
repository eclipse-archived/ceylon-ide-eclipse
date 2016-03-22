package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.java2ceylon.RefactorJ2C;
import com.redhat.ceylon.ide.common.refactoring.ExtractFunctionRefactoring;
import com.redhat.ceylon.ide.common.refactoring.ExtractParameterRefactoring;
import com.redhat.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import com.redhat.ceylon.ide.common.refactoring.InlineRefactoring;

public class refactorJ2C implements RefactorJ2C {
    @Override
    public ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion> 
    newExtractValueRefactoring(IEditorPart editorPart) {
        return new EclipseExtractValueRefactoring((CeylonEditor) editorPart);
    }
    @Override
    public ExtractParameterRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion> 
    newExtractParameterRefactoring(IEditorPart editorPart) {
        return new EclipseExtractParameterRefactoring((CeylonEditor) editorPart);
    }
    @Override
    public ExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, CompositeChange, IRegion> 
    newExtractFunctionRefactoring(IEditorPart editorPart) {
        return new EclipseExtractFunctionRefactoring((CeylonEditor) editorPart);
    }
    @Override
    public ExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, CompositeChange, IRegion> 
    newExtractFunctionRefactoring(IEditorPart editorPart, Tree.Declaration target) {
        return new EclipseExtractFunctionRefactoring((CeylonEditor) editorPart, target);
    }
    @Override
    public InlineRefactoring<ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, CompositeChange> 
    newInlineRefactoring(IEditorPart editorPart) {
        return new EclipseInlineRefactoring((CeylonEditor) editorPart);
    }
}
