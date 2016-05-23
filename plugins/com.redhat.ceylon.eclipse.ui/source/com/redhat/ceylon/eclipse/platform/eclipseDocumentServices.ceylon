import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.ide.common.platform {
    DocumentServices,
    CommonDocument
}

import org.eclipse.jface.preference {
    IPreferenceStore
}
import org.eclipse.ui.editors.text {
    EditorsUI
}
import org.eclipse.ui.texteditor {
    AbstractDecoratedTextEditorPreferenceConstants {
        editorTabWidth,
        editorSpacesForTabs
    }
}

object eclipseDocumentServices satisfies DocumentServices {
    createTextChange(String desc, CommonDocument|PhasedUnit input)
            => EclipseTextChange(desc, input);
    
    createCompositeChange(String desc) => EclipseCompositeChange(desc);

    indentSpaces 
            =>let(IPreferenceStore? store = EditorsUI.preferenceStore)
              (store?.getInt(editorTabWidth) else 4);
    
    indentWithSpaces
            => let(IPreferenceStore? store = EditorsUI.preferenceStore)
               (store?.getBoolean(editorSpacesForTabs) else false);
    
}