import org.eclipse.swt.graphics {
    Image
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}

shared object eclipseIcons {
    
    shared Image? fromIcons(Icons? icon) {
         return switch(icon)
         case(Icons.classes) CeylonResources.\iclass
         case(Icons.interfaces) CeylonResources.\iinterface
         case(Icons.attributes) CeylonResources.attribute
         case(Icons.localClass) CeylonResources.localClass
         case(Icons.localMethod) CeylonResources.localMethod
         case(Icons.imports) CeylonResources.\iimport
         case(Icons.reveal) CeylonResources.reveal
         case(Icons.refinement) CeylonPlugin.imageRegistry()
                 .get(CeylonResources.ceylonDefaultRefinement)
         case(Icons.formalRefinement) CeylonPlugin.imageRegistry()
                 .get(CeylonResources.ceylonFormalRefinement)
         else null;
    }
}