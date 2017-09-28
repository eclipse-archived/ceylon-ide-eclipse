import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.code.refactor {
    AbstractLinkedMode
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseLinkedMode
}

class AbstractLinkedModeAdapter(hintTemplate, ceylonEditor, document)
        extends AbstractLinkedMode(ceylonEditor) {
    
    CeylonEditor ceylonEditor;
    EclipseDocument document;
    shared actual String hintTemplate;
    
    variable EclipseLinkedMode? lm = null;

    shared EclipseLinkedMode linkedMode
        => lm else (lm = EclipseLinkedMode(document, linkedModeModel));
    
    // this is only to make the function `shared`
    shared actual void openPopup() => super.openPopup();
}