import com.redhat.ceylon.model.typechecker.model {
    Referenceable
}

import org.eclipse.jface.text {
    Region
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}

class AddRemoveAnnotionProposal(dec, annotation, desc, change, region)
        extends CorrectionProposal(desc, change, region) {
    
    Referenceable dec;
    String annotation;
    String desc;
    TextChange change;
    Region? region;
    
    shared actual Boolean equals(Object that) {
        if (is AddRemoveAnnotionProposal that) {
            return that.dec==dec && 
                    that.annotation==annotation;
        } else {
            return super.equals(that);
        }
    }
    
    shared actual Integer hash => dec.hash;
}