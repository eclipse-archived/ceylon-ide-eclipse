import com.redhat.ceylon.compiler.typechecker.context {
    TypecheckerUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges
}
import com.redhat.ceylon.eclipse.code.editor {
    Navigation
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents,
    EditorUtil
}
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import com.redhat.ceylon.ide.common.model {
    ProjectSourceFile
}
import com.redhat.ceylon.ide.common.typechecker {
    ProjectPhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import org.eclipse.core.resources {
    IFile,
    IProject,
    IResource,
    IFolder
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    TextFileChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

shared object eclipseModuleImportUtils
        extends AbstractModuleImportUtil<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange>()
        satisfies EclipseDocumentChanges {
    
    getChar(IDocument doc, Integer offset)
            => doc.getChar(offset);
    
    shared actual Integer getEditOffset(TextChange change)
            => change.edit.offset;
    
    suppressWarnings("expressionTypeNothing")
    shared actual [IFile, Tree.CompilationUnit, TypecheckerUnit]
    getUnit(IProject project, Module mod) {
        
        if (exists ppu = getDescriptorPhasedUnit(project, mod)) {
            return [ppu.resourceFile, ppu.compilationUnit, ppu.unit]; 
        }
        
        // should never be called
        return [nothing, nothing, nothing];
    }
    
    shared actual Indents<IDocument> indents => eclipseIndents;
    
    shared actual TextChange newTextChange(String desc, IFile file)
            => TextFileChange(desc, file);
    
    shared actual void performChange(TextChange change) {
        EditorUtil.performChange(change);
    }
    
    ProjectPhasedUnit<IProject, IResource, IFolder, IFile>? getDescriptorPhasedUnit(IProject project, Module mod) {
        value unit = mod.unit;
        if (is ProjectSourceFile<IProject, IResource, IFolder, IFile> unit) {
            value ceylonUnit = unit;
            return ceylonUnit.phasedUnit;
        }
        
        return null;
    }
    
    shared actual void gotoLocation(TypecheckerUnit unit, Integer offset,
        Integer length) {
        
        Navigation.gotoLocation(unit, offset, length);
    }
}
