package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importProposals;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.swt.widgets.Display;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.platform.platformJ2C;
import com.redhat.ceylon.ide.common.platform.TextChange;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Type;

public class LinkedModeImporter implements ILinkedModeListener {
    
    public static final int CANCEL = 1 << 10;
    
    private Type type;
    private IDocument document;
    private CeylonEditor editor;
    
    public LinkedModeImporter(IDocument document, 
            CeylonEditor editor) {
        this.document = document;
        this.editor = editor;
    }

    @Override
    public void left(LinkedModeModel model, int flags) {
        if (type!=null && (flags&CANCEL)==0) {
            Display.getCurrent()
                    .syncExec(new Runnable() {
                @Override
                public void run() {
                    Set<Declaration> imports = 
                            new HashSet<Declaration>();
                    //note: we want the very latest tree here, so 
                    //get it direct from the editor!
                    Tree.CompilationUnit rootNode = 
                            editor.getParseController()
                                .getLastCompilationUnit();
                    importProposals()
                        .importType(imports, type, rootNode);
                    if (!imports.isEmpty()) {
                        TextChange change = new platformJ2C().newChange("Import Type", 
                                        document);
                        change.initMultiEdit();
                        importProposals()
                            .applyImports(change, imports, 
                                    rootNode, change.getDocument());
                        change.apply();
                    }
                }
            });
        }
    }
    
    @Override
    public void suspend(LinkedModeModel model) {}

    @Override
    public void resume(LinkedModeModel model, int flags) {}

    public void selected(Type type) {
        this.type = type;
    }

}