package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextForDocLink;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importDeclaration;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecorationAttributes;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_FUN;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LOCAL_FUN;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

class BasicCompletionProposal extends CompletionProposal {
    
    static void addImportProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration dec, Scope scope) {
        result.add(new BasicCompletionProposal(offset, prefix,
                dec.getName(), escapeName(dec), dec, cpc));
    }

    static void addDocLinkProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration dec, Scope scope) {
        result.add(new BasicCompletionProposal(offset, prefix,
                dec.getName(), getTextForDocLink(cpc, dwp), dec, cpc));
    }

    static void addForProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (d instanceof Value) {
            TypedDeclaration td = (TypedDeclaration) d;
            if (td.getType()!=null && 
                    d.getUnit().isIterableType(td.getType())) {
                String elemName;
                String name = d.getName();
                if (name.length()==1) {
                    elemName = "element";
                }
                else if (name.endsWith("s")) {
                    elemName = name.substring(0, name.length()-1);
                }
                else {
                    elemName = name.substring(0, 1);
                }
                result.add(new BasicCompletionProposal(offset, prefix, 
                        "for (" + elemName + " in " + getDescriptionFor(dwp) + ")", 
                        "for (" + elemName + " in " + getTextFor(dwp) + ") {}",
                        d, cpc));
            }
        }
    }

    static void addIfExistsProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        d.getUnit().isOptionalType(v.getType()) && 
                        !v.isVariable()) {
                    result.add(new BasicCompletionProposal(offset, prefix, 
                            "if (exists " + getDescriptionFor(dwp) + ")", 
                            "if (exists " + getTextFor(dwp) + ") {}", 
                            d, cpc));
                }
            }
        }
    }

    static void addSwitchProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Node node, 
            IDocument doc) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        v.getType().getCaseTypes()!=null && 
                        !v.isVariable()) {
                    StringBuilder body = new StringBuilder();
                    String indent = getIndent(node, doc);
                    for (ProducedType pt: v.getType().getCaseTypes()) {
                        body.append(indent).append("case (");
                        if (!pt.getDeclaration().isAnonymous()) {
                            body.append("is ");
                        }
                        body.append(pt.getProducedTypeName(node.getUnit()))
                            .append(") {}")
                            .append(getDefaultLineDelimiter(doc));
                    }
                    body.append(indent);
                    result.add(new BasicCompletionProposal(offset, prefix, 
                            "switch (" + getDescriptionFor(dwp) + ")", 
                            "switch (" + getTextFor(dwp) + ")" + 
                                    getDefaultLineDelimiter(doc) + body, 
                            d, cpc));
                }
            }
        }
    }
    
    static void addFunctionProposal(int offset,
            final CeylonParseController cpc, 
            Tree.Primary primary,
            List<ICompletionProposal> result, 
            DeclarationWithProximity dwp,
            IDocument doc) {
        Tree.Term arg = primary;
        while (arg instanceof Tree.Expression) {
            arg = ((Tree.Expression) arg).getTerm(); 
        }
        final int start = arg.getStartIndex();
        final int stop = arg.getStopIndex();
        int origin = primary.getStartIndex();
        String argText;
        String prefix;
        try {
            //the argument
            argText = doc.get(start, stop-start+1);
            //the text to replace
            prefix = doc.get(origin, offset-origin);
        }
        catch (BadLocationException e) {
            return;
        }
        final Declaration dec = dwp.getDeclaration();
        String text = dec.getName(arg.getUnit())
                + "(" + argText + ")";
        //TODO: imports!!!
        result.add(new BasicCompletionProposal(offset, prefix, 
                getDescriptionFor(dwp) + "(...)",
                text, dec, cpc) {
            private DocumentChange createChange(IDocument document)
                    throws BadLocationException {
                DocumentChange change = 
                        new DocumentChange("Complete Invocation", document);
                change.setEdit(new MultiTextEdit());
                HashSet<Declaration> decs = new HashSet<Declaration>();
                Tree.CompilationUnit cu = cpc.getRootNode();
                importDeclaration(decs, dec, cu);
                int il=applyImports(change, decs, cu, document);
                String str;
                if (text.endsWith(";") && document.getChar(offset)==';') {
                    str = text.substring(0,text.length()-1);
                }
                else {
                    str = text;
                }
                change.addEdit(new ReplaceEdit(offset-prefix.length(), 
                            prefix.length(), str));
                offset+=il;
                return change;
            }
            @Override
            public boolean isAutoInsertable() {
                return false;
            }
            @Override
            public void apply(IDocument document) {
                try {
                    createChange(document).perform(new NullProgressMonitor());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public Image getImage() {
                return getDecoratedImage(dec.isShared() ? 
                                CEYLON_FUN : CEYLON_LOCAL_FUN,
                        getDecorationAttributes(dec),
                        false);
            }
        });
    }
    
    private final CeylonParseController cpc;
    private final Declaration declaration;
    
    private BasicCompletionProposal(int offset, String prefix, 
            String desc, String text, Declaration dec, 
            CeylonParseController cpc) {
        super(offset, prefix, getImageForDeclaration(dec), 
                desc, text);
        this.cpc = cpc;
        this.declaration = dec;
    }
    
    public String getAdditionalProposalInfo() {
        return getDocumentationFor(cpc, declaration);    
    }

}