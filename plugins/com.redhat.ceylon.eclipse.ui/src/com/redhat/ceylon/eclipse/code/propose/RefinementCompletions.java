package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getRefinementDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.Indents;

public class RefinementCompletions {
	
    public static Image DEFAULT_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(CeylonResources.CEYLON_DEFAULT_REFINEMENT);
    public static Image FORMAL_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(CeylonResources.CEYLON_FORMAL_REFINEMENT);
    
    static void addRefinementProposal(int offset, String prefix, final CeylonParseController cpc,
            Scope scope, Node node, List<ICompletionProposal> result, final Declaration d, IDocument doc,
            boolean preamble) {
        boolean isInterface = scope instanceof Interface;
        ProducedReference pr = getRefinedProducedReference(scope, d);
        //TODO: if it is equals() or hash, fill in the implementation
        String delim = Indents.getDefaultLineDelimiter(doc);
		result.add(new RefinementCompletionProposal(offset, prefix,  
                getRefinementDescriptionFor(d, pr, node.getUnit()), 
                getRefinementTextFor(d, pr, node.getUnit(), isInterface, 
                		delim + getIndent(node, doc), true, preamble), 
                cpc, d));
    }
    
    public static ProducedReference getRefinedProducedReference(Scope scope, Declaration d) {
        return refinedProducedReference(scope.getDeclaringType(d), d);
    }

    public static ProducedReference getRefinedProducedReference(ProducedType superType, 
            Declaration d) {
        if (superType.getDeclaration() instanceof IntersectionType) {
            for (ProducedType pt: superType.getDeclaration().getSatisfiedTypes()) {
                ProducedReference result = getRefinedProducedReference(pt, d);
                if (result!=null) return result;
            }
            return null; //never happens?
        }
        else {
            ProducedType declaringType = superType.getDeclaration().getDeclaringType(d);
            if (declaringType==null) return null;
            ProducedType outerType = superType.getSupertype(declaringType.getDeclaration());
            return refinedProducedReference(outerType, d);
        }
    }
    
    private static ProducedReference refinedProducedReference(ProducedType outerType, 
            Declaration d) {
        List<ProducedType> params = new ArrayList<ProducedType>();
        if (d instanceof Generic) {
            for (TypeParameter tp: ((Generic)d).getTypeParameters()) {
                params.add(tp.getType());
            }
        }
        return d.getProducedReference(outerType, params);
    }
    

}
