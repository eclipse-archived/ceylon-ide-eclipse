/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    JavaList
}

import org.eclipse.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import org.eclipse.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import org.eclipse.ceylon.ide.eclipse.code.complete {
    EclipseInvocationCompletionProposal,
    RefinementCompletionProposal,
    ParametersCompletionProposal,
    EclipseControlStructureProposal,
    ModuleCompletions,
    EclipseImportedModulePackageProposal,
    PackageCompletions,
    EclipseFunctionCompletionProposal,
    EclipseCompletionContext,
    IEclipseCompletionProposal2And6,
    proposalFactory,
    ParameterInfo
}
import org.eclipse.ceylon.ide.eclipse.code.correct {
    TypeProposal
}
import org.eclipse.ceylon.ide.eclipse.code.outline {
    CeylonLabelProvider
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin
}
import org.eclipse.ceylon.ide.eclipse.util {
    eclipseIcons,
    Highlights
}
import org.eclipse.ceylon.ide.common.completion {
    CompletionContext,
    ProposalsHolder,
    ProposalKind
}
import org.eclipse.ceylon.ide.common.doc {
    Icons
}
import org.eclipse.ceylon.ide.common.platform {
    CompletionServices,
    TextChange
}
import org.eclipse.ceylon.ide.common.refactoring {
    DefaultRegion
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration,
    Unit,
    Type,
    Scope,
    Reference,
    Package
}

import org.eclipse.jface.text {
    IDocument,
    DocumentEvent,
    ITextViewer
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import java.lang {
	overloaded
}

object eclipseCompletionServices satisfies CompletionServices {
    
    shared actual void newParametersCompletionProposal(CompletionContext ctx,
        Integer offset, String prefix, String desc, String text, 
        List<Type> argTypes, Node node, Unit unit) {
        
        assert (is EclipseCompletionContext ctx);
        
        value proposal = ParametersCompletionProposal(offset,
            desc.string, text.string,
            JavaList(argTypes), node.scope, unit);
        ctx.proposals.add(proposal);
    }
    
    shared actual void newInvocationCompletion(CompletionContext ctx, Integer offset, String prefix,
        String desc, String text, Declaration dec, 
        Reference()? pr, Scope scope,
        Boolean includeDefaulted, Boolean positionalInvocation, 
        Boolean namedInvocation, Boolean inheritance, 
        Boolean qualified, Declaration? qualifyingDec) {
        
        assert (is EclipseCompletionContext ctx);
        
        value proposal = EclipseInvocationCompletionProposal {
            _offset = offset;
            prefix = prefix;
            description = desc;
            text = text;
            dec = dec;
            producedReference = pr;
            scope = scope;
            ctx = ctx;
            includeDefaulted = includeDefaulted;
            positionalInvocation = positionalInvocation;
            namedInvocation = namedInvocation;
            inheritance = inheritance;
            qualified = qualified;
            qualifyingValue = qualifyingDec;
        };
        ctx.proposals.add(proposal);
    }
    
    // TODO replace with EclipseRefinementCompletionProposal (and finish rewriting it)
    shared actual void newRefinementCompletionProposal(Integer offset, 
        String prefix, Reference? pr, String desc, String text, 
        CompletionContext ctx, Declaration dec, Scope scope, 
        Boolean fullType, Boolean explicitReturnType) {

        assert (is EclipseCompletionContext ctx);
        
        ctx.proposals.add(RefinementCompletionProposal(offset, prefix, 
            pr, desc, text, ctx.cpc, dec, scope, fullType, 
            explicitReturnType));
    }
    
    shared actual void newPackageDescriptorProposal(CompletionContext ctx, Integer offset, String prefix, 
        String desc, String text) {
        
        assert (is EclipseCompletionContext ctx);
        
        ctx.proposals.add(PackageCompletions.PackageDescriptorProposal(
            offset, prefix, desc, text));
    }
    
    shared actual void newImportedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CompletionContext ctx,
        Package candidate) {
        
        assert (is EclipseCompletionContext ctx);
        
        ctx.proposals.add(
            EclipseImportedModulePackageProposal {                        
                offset = offset;
                prefix = prefix;
                memberPackageSubname = memberPackageSubname;
                withBody = withBody;
                fullPackageName = fullPackageName;
                ctx = ctx;
                candidate = candidate;
            });
    }
    
    shared actual void newQueriedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CompletionContext ctx,
        ModuleVersionDetails version, Unit unit, ModuleSearchResult.ModuleDetails md) {
        
        assert (is EclipseCompletionContext ctx);
        
        ctx.proposals.add(
            PackageCompletions.QueriedModulePackageProposal(offset, prefix,
            memberPackageSubname, withBody, fullPackageName, 
            ctx, version, unit, md));
    }
    
    shared actual void newModuleProposal(Integer offset, String prefix, Integer len, 
        String versioned, ModuleSearchResult.ModuleDetails mod, Boolean withBody,
        ModuleVersionDetails version, String name, Node node, CompletionContext ctx) {
        
        assert (is EclipseCompletionContext ctx);
    
        ctx.proposals.add(
            ModuleCompletions.ModuleProposal(
            offset, prefix, len, versioned, mod, 
            withBody, version, name, node));
    }
    
    shared actual void newModuleDescriptorProposal(CompletionContext ctx, Integer offset, String prefix, String desc,
        String text, Integer selectionStart, Integer selectionEnd) {
        
        assert (is EclipseCompletionContext ctx);
        
        ctx.proposals.add(
            ModuleCompletions.ModuleDescriptorProposal(
            offset, prefix, desc, text, 
            selectionStart, selectionEnd));
    }
    
    shared actual void newJDKModuleProposal(CompletionContext ctx, Integer offset, String prefix, 
        Integer len, String versioned, String name) {
        
        assert (is EclipseCompletionContext ctx);
        
        ctx.proposals.add(
            ModuleCompletions.JDKModuleProposal(offset, 
                prefix, len, versioned, name));
    }
    
    shared actual void newParameterInfo(CompletionContext ctx, Integer offset, Declaration dec, 
        Reference producedReference, Scope scope, 
        Boolean namedInvocation) {
        
        assert (is EclipseCompletionContext ctx);
        ctx.proposals.add(ParameterInfo(
            offset, dec, producedReference, scope, ctx, namedInvocation));
    }
    
    shared actual void newFunctionCompletionProposal(Integer offset, String prefix,
        String desc, String text, Declaration dec, Unit unit, 
        CompletionContext ctx) {
        
        assert (is EclipseCompletionContext ctx);
        
        ctx.proposals.add(
            EclipseFunctionCompletionProposal {            
                offset = offset;
                prefix = prefix;
                desc = desc;
                text = text;
                declaration = dec;
                rootNode = ctx.lastCompilationUnit;
            });
    }
    
    shared actual void newControlStructureCompletionProposal(Integer offset, String prefix,
        String desc, String text, Declaration dec, 
        CompletionContext ctx, Node? node) {
        
        assert (is EclipseCompletionContext ctx);
        
        ctx.proposals.add(
            EclipseControlStructureProposal {
                offset = offset;
                prefix = prefix;
                desc = desc;
                text = text;
                declaration = dec;
                cpc = ctx;
                node = node;
            });
    }
    
    shared actual void newTypeProposal(ProposalsHolder proposals, 
        Integer offset, Type? type, String text, 
        String desc, Tree.CompilationUnit rootNode) {
        
        assert (is EclipseProposalsHolder proposals);
        
        proposals.add(TypeProposal(offset, type, text, desc, rootNode));
    }
    
    createProposalsHolder() => EclipseProposalsHolder();
    
    shared actual void addNestedProposal(ProposalsHolder proposals, Icons|Declaration icon,
        String description, DefaultRegion region, String text) {
        
        assert (is EclipseProposalsHolder proposals);
        
        value image = switch (icon)
        case (is Declaration) CeylonLabelProvider.getImageForDeclaration(icon)
        else eclipseIcons.fromIcons(icon);
        
        proposals.add(GenericProposal(description, text, region, image));
    }
    
    shared actual void addProposal(CompletionContext ctx, Integer offset, 
        String prefix, Icons|Declaration icon, String description, String text,
        ProposalKind kind, TextChange? additionalChange, DefaultRegion? selection) {
        
        assert (is EclipseCompletionContext ctx);
        
        value point = if (exists selection)
        then Point(selection.start, selection.length)
        else null;
        
        ctx.proposals.add(proposalFactory.create(ctx, offset, prefix, icon,
            description, text, kind, additionalChange, point));
    }
}

shared class EclipseProposalsHolder() satisfies ProposalsHolder {
    value _proposals = ArrayList<ICompletionProposal>();

    shared List<ICompletionProposal> proposals => _proposals;
    
    shared void add(ICompletionProposal proposal) {
        _proposals.add(proposal);
    }

    size => proposals.size;
}

class GenericProposal(String description, String text, DefaultRegion region,
    shared actual Image? image)
        satisfies IEclipseCompletionProposal2And6 {
    
    shared actual String? additionalProposalInfo => null;
    
    shared actual overloaded void apply(IDocument iDocument) {
        iDocument.replace(region.start, region.length, text);
    }
    
    shared actual overloaded void apply(ITextViewer viewer, Character char,
        Integer int, Integer int1) {
        
        apply(viewer.document);
    }
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => description;
    
    shared actual Point? getSelection(IDocument? iDocument) => null;
    
    shared actual void selected(ITextViewer? iTextViewer, Boolean boolean) {}
    
    shared actual StyledString styledDisplayString {
        value result = StyledString();
        Highlights.styleFragment(result, displayString, false, null,
            CeylonPlugin.completionFont);
        return result;
    }
    
    shared actual void unselected(ITextViewer? iTextViewer) {}
    
    shared actual Boolean validate(IDocument? iDocument, Integer int,
        DocumentEvent? documentEvent) => true; // TODO
}
