import org.eclipse.swt.graphics {
    Image
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}

shared object eclipseIcons {
    
    shared Image? fromIcons(Icons? icon) {
         return switch(icon)
         case(Icons.classes) CeylonResources.\iclass
         case(Icons.interfaces) CeylonResources.\iinterface
         case(Icons.attributes) CeylonResources.attribute
         case(Icons.localClass) CeylonResources.localClass
         case(Icons.localMethod) CeylonResources.localMethod
         else null;
    }
}