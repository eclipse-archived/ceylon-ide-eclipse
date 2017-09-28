import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseTextChange
}
import org.eclipse.ceylon.ide.common.platform {
    TextChange
}
import org.eclipse.ceylon.ide.common.refactoring {
    ExtractValueRefactoring
}
import org.eclipse.ceylon.model.typechecker.model {
    Type
}

import java.util {
    List,
    ArrayList
}

import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.jface.text {
    IRegion,
    Region
}
import org.eclipse.ltk.core.refactoring {
    RefactoringStatus,
    ETextChange=TextChange,
    Change
}

class EclipseExtractValueRefactoring(CeylonEditor editorPart) 
        extends EclipseAbstractRefactoring<TextChange>(editorPart)
        satisfies ExtractValueRefactoring<IRegion>
        & EclipseExtractLinkedModeEnabled {
    
    shared actual variable String? internalNewName=null;
    shared actual variable Boolean canBeInferred=false;
    shared actual variable Boolean explicitType=false;
    shared actual variable Type? type=null;
    shared actual variable IRegion? typeRegion=null;
    shared actual variable IRegion? decRegion=null;
    shared actual variable IRegion? refRegion=null;
    shared actual variable Boolean getter=false;
    shared actual List<IRegion> dupeRegions = ArrayList<IRegion>();
    
    checkFinalConditions(IProgressMonitor? monitor)
            => let(node=editorData.node) 
            if (exists mop=node.scope.getMemberOrParameter(node.unit, newName, null, false))
            then RefactoringStatus.createWarningStatus(
                    "An existing declaration named '``newName``' is already visible this scope")
            else RefactoringStatus();
    
    checkInitialConditions(IProgressMonitor? monitor)
            => RefactoringStatus();
    
    shared actual Change createChange(IProgressMonitor? monitor) {
        value tc = newLocalChange();
        extractInFile(tc);
        return tc;
    }
    
    newRegion(Integer start, Integer length) => Region(start, length);
    
    extractInFile(ETextChange tfc) => build(EclipseTextChange("", tfc));
    
    shared actual String name => (super of ExtractValueRefactoring<IRegion>).name;
    
}
