/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.core.builder {
    CeylonBuilder
}
import org.eclipse.ceylon.ide.eclipse.core.vfs {
    IFileVirtualFile
}
import org.eclipse.ceylon.ide.eclipse.util {
    EditorUtil
}
import org.eclipse.ceylon.ide.common.model {
    CrossProjectSourceFile,
    CrossProjectBinaryUnit,
    IResourceAware
}
import org.eclipse.ceylon.ide.common.refactoring {
    AbstractRefactoring
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import java.util {
    List,
    ArrayList
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.ltk.core.refactoring {
    DocumentChange,
    TextFileChange,
    TextChange
}
import org.eclipse.ui {
    IFileEditorInput
}

Boolean inSameProject(Declaration declaration, CeylonEditor editor) {
    value unit = declaration.unit;
    if (unit is CrossProjectSourceFile<IProject,IResource,IFolder,IFile> || 
        unit is CrossProjectBinaryUnit<IProject,IResource,IFolder,IFile, out Anything, out Anything>) {
        return false;
    }
    else if (is IResourceAware<out Anything, out Anything, out Anything> unit, 
        exists p = unit.resourceProject,
        exists editorProject = EditorUtil.getProject(editor)) {
        return p==editorProject;
    }
    else {
        return false;
    }
}

abstract class EclipseAbstractRefactoring<RefactoringData>
        (CeylonEditor editorPart)
        extends Refactoring()
        satisfies AbstractRefactoring<RefactoringData> {
    
    shared class EclipseEditorData(shared CeylonEditor editor) 
            satisfies EditorData {
        
        shared default IDocument? document
                = editor.documentProvider
                    .getDocument(editor.editorInput);
        
        shared IProject? project = EditorUtil.getProject(editor);
        
        tokens = editor.parseController.tokens;
        
        "Can't refactor while typechecking"
        assert (exists typecheckedRootNode
            = editor.parseController.typecheckedRootNode);
        rootNode = typecheckedRootNode;
        
        value selection = EditorUtil.getSelection(editor);
        "Can't refactor if selected node is null"
        assert (exists foundNode = nodes.findNode {
            node = rootNode;
            tokens = tokens;
            startOffset = selection.offset;
            endOffset = selection.offset+selection.length;
        });
        node = foundNode;
        
        shared actual IFileVirtualFile? sourceVirtualFile = 
                if (is IFileEditorInput input = editor.editorInput, 
                    exists file=  EditorUtil.getFile(input)) 
                    then IFileVirtualFile(file) else null;
    }

    shared actual default EclipseEditorData editorData 
            = EclipseEditorData(editorPart);

    inSameProject(Declaration declaration)
            => package.inSameProject(declaration, editorPart);
    
    shared DocumentChange newDocumentChange() {
        value dc = DocumentChange(
            editorPart.editorInput.name 
                    + " \{#2014} current editor", 
            editorData.document);
        dc.textType = "ceylon";
        return dc;
    }

    shared TextFileChange newTextFileChange(PhasedUnit pu) {
        assert (is IResourceAware<IProject,IFolder,IFile> pu);
        value tfc = TextFileChange(name, pu.resourceFile);
        tfc.textType = "ceylon";
        return tfc;
    }
    
    shared actual PhasedUnit editorPhasedUnit 
            => editorData.editor.parseController.lastPhasedUnit;

    searchInEditor() => editorData.editor.dirty;

    searchInFile(PhasedUnit pu)
            => !editorData.editor.dirty 
            || pu.unit != rootNode.unit;

    shared TextChange newLocalChange() {
        TextChange tc;
        if (searchInEditor()) {
            assert (exists doc = editorData.document);
            tc = DocumentChange(name, editorData.document);
        }
        else {
            assert (exists file = editorData.sourceVirtualFile?.nativeResource);
            tc = TextFileChange(name, file);
        }
        tc.textType = "ceylon";
        return tc;
    }

    shared actual List<PhasedUnit> getAllUnits() {
        assert (exists project = editorData.project);
        value units = ArrayList<PhasedUnit>();
        units.addAll(CeylonBuilder.getUnits(project));
        for (p in project.referencingProjects.iterable) {
            units.addAll(CeylonBuilder.getUnits(p));
        }
        return units;
    }
    
    shared default small Integer saveMode 
            => affectsOtherFiles 
            then RefactoringSaveHelper.\iSAVE_CEYLON_REFACTORING 
            else RefactoringSaveHelper.\iSAVE_NOTHING;
}