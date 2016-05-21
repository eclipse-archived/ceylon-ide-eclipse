import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.code.refactor {
    AbstractLinkedMode
}
import com.redhat.ceylon.eclipse.platform {
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