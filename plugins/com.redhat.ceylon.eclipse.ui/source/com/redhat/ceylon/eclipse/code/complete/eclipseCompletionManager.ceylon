import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.code.preferences {
    CeylonPreferenceInitializer
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil,
    Indents
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import com.redhat.ceylon.ide.common.util {
    escaping,
    OccurrenceLocation
}
import com.redhat.ceylon.model.typechecker.model {
    Type,
    Declaration,
    Reference,
    Scope,
    Unit,
    ClassOrInterface
}

import java.util {
    JList=List
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}

object eclipseCompletionManager extends IdeCompletionManager<CeylonParseController, ICompletionProposal, IDocument>() {
    
    shared actual ICompletionProposal newParametersCompletionProposal(Integer offset, 
            Type type, JList<Type> argTypes, 
            Node node, CeylonParseController cpc) {

        value unit = node.unit;
    
        value cd = unit.callableDeclaration;
        value paramTypes = showParameterTypes;
        value desc = StringBuilder();
        value text = StringBuilder();

        desc.append("(");
        text.append("(");
        
        for (i in 0..argTypes.size()) {
            variable Type argType = argTypes.get(i);
            if (desc.size > 1) { desc.append(", "); }
            if (text.size > 1) { text.append(", "); }
            
            if (argType.classOrInterface, argType.declaration.equals(cd)) {
                value anon = anonFunctionHeader(argType, unit);
                text.append(anon).append(" => ");
                desc.append(anon).append(" => ");
                argType = unit.getCallableReturnType(argType);
                argTypes.set(i, argType);
            } else if (paramTypes) {
                desc.append(argType.asString(unit))
                        .append(" ");
            }
            
            value name = if (argType.classOrInterface || argType.typeParameter)
            then escaping.toInitialLowercase(argType.declaration.getName(unit))
            else "arg";
            text.append(name);
            desc.append(name);
        }
        text.append(")");
        desc.append(")");

        return ParametersCompletionProposal(offset, 
                            desc.string, text.string, 
                            argTypes, node.scope, cpc);
    }
    
    shared actual Boolean showParameterTypes => EditorUtil.preferences.getBoolean(CeylonPreferenceInitializer.\iPARAMETER_TYPES_IN_COMPLETIONS);
    
    shared actual Tree.CompilationUnit getCompilationUnit(CeylonParseController cpc) => cpc.rootNode;
    
    shared actual String inexactMatches => EditorUtil.preferences.getString(CeylonPreferenceInitializer.\iINEXACT_MATCHES);
    
    shared actual String getDocumentSubstring(IDocument doc, Integer start, Integer length) => doc.get(start, length);
    
    shared actual ICompletionProposal newPositionalInvocationCompletion(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, CeylonParseController cpc, Boolean isMember,
        OccurrenceLocation? ol, String? typeArgs, Boolean includeDefaulted) {
        
        value desc = CodeCompletions.getPositionalInvocationDescriptionFor(dec, ol, pr, cpc.rootNode.unit, includeDefaulted, typeArgs);
        value text = CodeCompletions.getPositionalInvocationTextFor(dec, ol, pr, cpc.rootNode.unit, includeDefaulted, typeArgs);
        return InvocationCompletionProposal(offset, prefix, desc, text, dec, pr, scope, cpc, includeDefaulted, true, false, isMember, null);
    }
    
    shared actual ICompletionProposal newNamedInvocationCompletion(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, CeylonParseController cpc, Boolean isMember,
        OccurrenceLocation? ol, String? typeArgs, Boolean includeDefaulted) {
        
        value desc = CodeCompletions.getNamedInvocationDescriptionFor(dec, pr, cpc.rootNode.unit, includeDefaulted, typeArgs);
        value text = CodeCompletions.getNamedInvocationTextFor(dec, pr, cpc.rootNode.unit, includeDefaulted, typeArgs);
        return InvocationCompletionProposal(offset, prefix, desc, text, dec, pr, scope, cpc, includeDefaulted, false, true, isMember, null);
    }
    
    shared actual ICompletionProposal newReferenceCompletion(Integer offset, String prefix,
        Declaration dec, Unit u, Reference? pr, Scope scope, CeylonParseController cpc, Boolean isMember, Boolean includeTypeArgs) {
        
        value desc = CodeCompletions.getDescriptionFor(dec, cpc.rootNode.unit);
        value text = CodeCompletions.getTextFor(dec, cpc.rootNode.unit);
        return InvocationCompletionProposal(offset, prefix, desc, text, dec, pr, scope, cpc, true, false, false, isMember, null);
    }
    
    shared actual ICompletionProposal newRefinementCompletionProposal(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, CeylonParseController cmp, Boolean isInterface,
        ClassOrInterface ci, Node node, Unit unit, IDocument doc, Boolean preamble) {
        
        value lineDeliniter = Indents.getDefaultLineDelimiter(doc);
        value indent = Indents.getIndent(node, doc);
        value desc = CodeCompletions.getRefinementDescriptionFor(dec, pr, unit);
        value text = CodeCompletions.getRefinementTextFor(dec, pr, unit, isInterface, ci, lineDeliniter + indent, true, preamble);
        return RefinementCompletionProposal(offset, prefix, pr, desc, text, cmp, dec, scope, false, true);
    }

}