package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.ReplaceEdit;

import ceylon.file.Writer;
import ceylon.file.Writer$impl;
import ceylon.formatter.format_;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

final class FormatAction extends Action {
    private final CeylonEditor editor;

    FormatAction(CeylonEditor editor) {
        super(null);
        this.editor = editor;
    }
    
    @Override
    public void run() {
        IDocument document = editor.getCeylonSourceViewer().getDocument();
        final ITextSelection ts = getSelection(editor);
        CeylonParseController pc = editor.getParseController();
        Tree.CompilationUnit rootNode = pc.getRootNode();
        if (rootNode==null) return;
        
        final StringBuilder builder = new StringBuilder(document.getLength());
        format_.format(rootNode, format_.format$options(rootNode), new Writer() {

            @Override
            public Object write(String string) {
                builder.append(string);
                return null; // void
            }
            
            // the rest is boring
            
            @Override
            public Writer$impl $ceylon$file$Writer$impl() {
                return new Writer$impl(this);
            }
            @Override
            public Object close() {
                return null; // void
            }
            @Override
            public Object destroy(Throwable arg0) {
                return null; // void
            }
            @Override
            public Object flush() {
                return null; // void
            }
            @Override
            public Object writeLine() {
                // unused; ceylon.formatter has its own newline handling
                throw new UnsupportedOperationException();
            }
            @Override
            public Object writeLine(String line) {
                // unused; ceylon.formatter has its own newline handling
                throw new UnsupportedOperationException();
            }
            @Override
            public String writeLine$line() {
                return ""; // default value for "line" parameter
            }
        });
        
        String text = builder.toString();
        try {
            if (!document.get().equals(text)) {
                DocumentChange change = 
                        new DocumentChange("Format", document);
                change.setEdit(new ReplaceEdit(0, document.getLength(), text));
                change.perform(new NullProgressMonitor());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}