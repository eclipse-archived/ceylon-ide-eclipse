/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.code.correct {
    EclipseDocument
}
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseCompositeChange
}
import org.eclipse.ceylon.ide.eclipse.util {
    EditorUtil
}
import org.eclipse.ceylon.ide.common.platform {
    CommonDocument,
    CompositeChange
}
import org.eclipse.ceylon.ide.common.refactoring {
    InlineRefactoring,
    isInlineRefactoringAvailable
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.ltk.core.refactoring {
    RefactoringStatus,
    Change
}
import org.eclipse.ui {
    IEditorPart
}

EclipseInlineRefactoring? newEclipseInlineRefactoring(IEditorPart editor) {
    if (is CeylonEditor editor,
        exists rootNode 
            = editor.parseController.typecheckedRootNode) {
        value selection = EditorUtil.getSelection(editor);
        value node = nodes.findNode {
            node = rootNode;
            tokens = editor.parseController.tokens;
            startOffset = selection.offset;
            endOffset = selection.offset+selection.length;
        };
        if (is Declaration decl = nodes.getReferencedDeclaration(node), 
            isInlineRefactoringAvailable(decl, rootNode, inSameProject(decl, editor))) {
            return EclipseInlineRefactoring(editor, decl).init();
        }
    }
    return null;
}

class EclipseInlineRefactoring(CeylonEditor editorPart, shared Declaration declaration)
        extends EclipseAbstractRefactoring<CompositeChange>(editorPart)
        satisfies InlineRefactoring {

    variable Boolean delete = true;
    variable Boolean justOne = false;
    
    shared void toggleDelete() => delete = !delete;
    shared void toggleJustOne() => justOne = !justOne;

    shared class EclipseInlineData(CeylonEditor editor)
            extends EclipseEditorData(editor) satisfies InlineData {
        
        shared actual Declaration declaration => outer.declaration;
        
        shared actual Boolean delete => outer.delete;
        
        shared actual Boolean justOne => outer.justOne;
        
        shared actual CommonDocument doc {
            if (exists _ = super.document) {
                return EclipseDocument(_);
            }
            throw Exception("Can't find document");
        }
    }
    
    shared actual late EclipseInlineData editorData;
    
    shared EclipseInlineRefactoring init() {
        editorData = EclipseInlineData(editorPart);
        return this;
    }

    shared actual RefactoringStatus checkFinalConditions(IProgressMonitor mon)
            => RefactoringStatus();
    
    shared actual RefactoringStatus checkInitialConditions(IProgressMonitor mon) {
        value res = checkAvailability();
        
        if (is String res) {
            return RefactoringStatus.createFatalErrorStatus(res);
        } else {
            value status = RefactoringStatus();
            for (warning in res) {
                status.merge(RefactoringStatus.createWarningStatus(warning));
            }
            return status;
        }
    }

    shared actual Change createChange(IProgressMonitor? iProgressMonitor) {
        value change = EclipseCompositeChange(name);
        build(change);
        return change.nativeChange;
    }

    shared actual String name => (super of InlineRefactoring).name;
}