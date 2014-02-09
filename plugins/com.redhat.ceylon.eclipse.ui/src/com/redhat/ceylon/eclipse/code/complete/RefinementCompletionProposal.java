package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getInlineFunctionDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getInlineFunctionTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public final class RefinementCompletionProposal extends CompletionProposal {
	
    public static Image DEFAULT_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(CeylonResources.CEYLON_DEFAULT_REFINEMENT);
    public static Image FORMAL_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(CeylonResources.CEYLON_FORMAL_REFINEMENT);
    
    static void addRefinementProposal(int offset, final Declaration dec, 
            ClassOrInterface ci, Node node, Scope scope, String prefix, 
            CeylonParseController cpc, IDocument doc, 
            List<ICompletionProposal> result, boolean preamble) {
        boolean isInterface = scope instanceof Interface;
        ProducedReference pr = getRefinedProducedReference(scope, dec);
        Unit unit = node.getUnit();
        result.add(new RefinementCompletionProposal(offset, prefix,  
                getRefinementDescriptionFor(dec, pr, unit), 
                getRefinementTextFor(dec, pr, unit, isInterface, ci, 
                        getDefaultLineDelimiter(doc) + getIndent(node, doc), 
                        true, preamble), 
                cpc, dec));
    }
    
    static void addInlineFunctionProposal(int offset, Declaration dec, 
            Node node, String prefix, CeylonParseController cpc, 
            IDocument doc, List<ICompletionProposal> result) {
        //TODO: type argument substitution using the ProducedReference of the primary node
        if (dec.isParameter()) {
            Parameter p = ((MethodOrValue) dec).getInitializerParameter();
            Unit unit = node.getUnit();
            result.add(new RefinementCompletionProposal(offset, prefix,
                    getInlineFunctionDescriptionFor(p, null, unit),
                    getInlineFunctionTextFor(p, null, unit, 
                            getDefaultLineDelimiter(doc) + getIndent(node, doc)),
                    cpc, dec));
        }
    }

    public static ProducedReference getRefinedProducedReference(Scope scope, 
            Declaration d) {
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
            ProducedType declaringType = 
                    superType.getDeclaration().getDeclaringType(d);
            if (declaringType==null) return null;
            ProducedType outerType = 
                    superType.getSupertype(declaringType.getDeclaration());
            return refinedProducedReference(outerType, d);
        }
    }
    
    private static ProducedReference refinedProducedReference(ProducedType outerType, 
            Declaration d) {
        List<ProducedType> params = new ArrayList<ProducedType>();
        if (d instanceof Generic) {
            for (TypeParameter tp: ((Generic) d).getTypeParameters()) {
                params.add(tp.getType());
            }
        }
        return d.getProducedReference(outerType, params);
    }
    
	private final CeylonParseController cpc;
	private final Declaration declaration;

	private RefinementCompletionProposal(int offset, String prefix, 
			String desc, String text,
			CeylonParseController cpc, Declaration d) {
		super(offset, prefix, d.isFormal() ? 
					FORMAL_REFINEMENT : DEFAULT_REFINEMENT, 
				desc, text, false);
		this.cpc = cpc;
		this.declaration = d;
	}

	@Override
	public void apply(IDocument document) {
		int originalLength = document.getLength();
		try {
			imports(document).perform(new NullProgressMonitor());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		offset += document.getLength() - originalLength;
		super.apply(document);
	}

	private DocumentChange imports(IDocument document)
			throws BadLocationException {
		DocumentChange tc = new DocumentChange("imports", document);
		tc.setEdit(new MultiTextEdit());
		HashSet<Declaration> decs = new HashSet<Declaration>();
		CompilationUnit cu = cpc.getRootNode();
		//TODO for an inline function completion, we don't
		//     need to import the return type
		importSignatureTypes(declaration, cu, decs);
		applyImports(tc, decs, cu, document);
		return tc;
	}

	public String getAdditionalProposalInfo() {
		return getDocumentationFor(cpc, declaration);	
	}
	
}