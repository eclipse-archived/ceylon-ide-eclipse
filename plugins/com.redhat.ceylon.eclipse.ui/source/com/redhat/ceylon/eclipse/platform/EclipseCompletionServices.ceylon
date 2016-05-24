import ceylon.collection {
    ArrayList
}

import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.eclipse.code.complete {
    EclipseInvocationCompletionProposal,
    RefinementCompletionProposal,
    BasicCompletionProposal,
    ParametersCompletionProposal,
    KeywordCompletionProposal,
    CompletionProposal,
    EclipseControlStructureProposal,
    ModuleCompletions,
    EclipseImportedModulePackageProposal,
    InvocationCompletionProposal,
    PackageCompletions,
    EclipseFunctionCompletionProposal,
    EclipseCompletionContext,
    IEclipseCompletionProposal2And6
}
import com.redhat.ceylon.eclipse.code.correct {
    TypeProposal
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}
import com.redhat.ceylon.ide.common.completion {
    isModuleDescriptor,
    CompletionContext,
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.platform {
    CompletionServices,
    TextChange
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Unit,
    Type,
    Scope,
    Reference,
    Package
}

import java.util {
    JList=List
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
import org.eclipse.swt.graphics {
    Point,
    Image
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.eclipse.util {
    eclipseIcons,
    Highlights
}
import org.eclipse.jface.viewers {
    StyledString
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

object eclipseCompletionServices satisfies CompletionServices {
    
    shared actual void newParametersCompletionProposal(CompletionContext ctx,
        Integer offset, String prefix, String desc, String text, 
        JList<Type> argTypes, Node node, Unit unit) {
        
        if (is EclipseCompletionContext ctx) {
            value proposal = ParametersCompletionProposal(offset,
                desc.string, text.string,
                argTypes, node.scope, unit);
            ctx.proposals.add(proposal);
        }
    }
    
    shared actual void newInvocationCompletion(CompletionContext ctx, Integer offset, String prefix,
        String desc, String text, Declaration dec, 
        Reference? pr, Scope scope,
        Boolean includeDefaulted, Boolean positionalInvocation, 
        Boolean namedInvocation, Boolean inheritance, 
        Boolean qualified, Declaration? qualifyingDec) {
        
        if (is EclipseCompletionContext ctx) {
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
    }
    
    // TODO replace with EclipseRefinementCompletionProposal (and finish rewriting it)
    shared actual void newRefinementCompletionProposal(Integer offset, 
        String prefix, Reference? pr, String desc, String text, 
        CompletionContext ctx, Declaration dec, Scope scope, 
        Boolean fullType, Boolean explicitReturnType) {

        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(RefinementCompletionProposal(offset, prefix, 
                pr, desc, text, ctx.cpc, dec, scope, fullType, 
                explicitReturnType));
        }
    }
    
    shared actual void newMemberNameCompletionProposal(CompletionContext ctx, Integer offset, 
        String prefix, String name, String unquotedName) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(CompletionProposal(offset, prefix, 
                CeylonResources.\iLOCAL_NAME, 
                unquotedName, name));
        }
    }
    
    shared actual void newKeywordCompletionProposal(CompletionContext ctx, Integer offset, 
        String prefix, String keyword, String text) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(KeywordCompletionProposal(offset, prefix, keyword, text));
        }
    }
    
    shared actual ICompletionProposal newAnonFunctionProposal(CompletionContext ctx,
        Integer _offset, Type? requiredType, Unit unit, 
        String _text, String header, Boolean isVoid, 
        Integer selectionStart, Integer selectionLength) {
        
        value largeCorrectionImage 
                = CeylonLabelProvider.getDecoratedImage(
            CeylonResources.\iCEYLON_CORRECTION, 
            0, false);
        return object 
                extends CompletionProposal(_offset, "", 
            largeCorrectionImage, _text, _text) {
            getSelection(IDocument document) 
                    => Point(selectionStart, selectionLength);
        };
    }
    
    shared actual void newBasicCompletionProposal(CompletionContext ctx, Integer offset, String prefix,
        String text, String escapedText, Declaration decl) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(BasicCompletionProposal(offset, prefix, text, 
                escapedText, decl, ctx));
        }
    }

    shared actual void newPackageDescriptorProposal(CompletionContext ctx, Integer offset, String prefix, 
        String desc, String text) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(PackageCompletions.PackageDescriptorProposal(
                offset, prefix, desc, text));
        }
    }
    
    shared actual void newCurrentPackageProposal(Integer offset, String prefix, 
        String packageName, CompletionContext ctx) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(CompletionProposal(offset, prefix, 
                if (isModuleDescriptor(ctx.lastCompilationUnit)) 
                then CeylonResources.\iMODULE 
                else CeylonResources.\iPACKAGE,
                packageName, packageName));
        }
    }
    
    shared actual void newImportedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CompletionContext ctx,
        Package candidate) {
        
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(
                EclipseImportedModulePackageProposal {                        
                    offset = offset;
                    prefix = prefix;
                    memberPackageSubname = memberPackageSubname;
                    withBody = withBody;
                    fullPackageName = fullPackageName;
                    ctx = ctx;
                    candidate = candidate;
                }
            );
        }
    }
    
    shared actual void newQueriedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CompletionContext ctx,
        ModuleVersionDetails version, Unit unit, ModuleSearchResult.ModuleDetails md) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(
                PackageCompletions.QueriedModulePackageProposal(offset, prefix,
                memberPackageSubname, withBody, fullPackageName, 
                ctx, version, unit, md)
            );
        }
    }
    
    shared actual void newModuleProposal(Integer offset, String prefix, Integer len, 
        String versioned, ModuleSearchResult.ModuleDetails mod, Boolean withBody,
        ModuleVersionDetails version, String name, Node node, CompletionContext ctx) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(
                ModuleCompletions.ModuleProposal(
                offset, prefix, len, versioned, mod, 
                withBody, version, name, node)
            );
        }
    }
    
    shared actual void newModuleDescriptorProposal(CompletionContext ctx, Integer offset, String prefix, String desc,
        String text, Integer selectionStart, Integer selectionEnd) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(
                ModuleCompletions.ModuleDescriptorProposal(
                offset, prefix, desc, text, 
                selectionStart, selectionEnd)
            );
        }
    }
    
    shared actual void newJDKModuleProposal(CompletionContext ctx, Integer offset, String prefix, 
        Integer len, String versioned, String name) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(
                ModuleCompletions.JDKModuleProposal(offset, 
                    prefix, len, versioned, name)
            );
        }
    }
    
    shared actual void newParameterInfo(CompletionContext ctx, Integer offset, Declaration dec, 
        Reference producedReference, Scope scope, 
        Boolean namedInvocation) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(InvocationCompletionProposal.ParameterInfo(
                offset, dec, producedReference, scope, ctx, namedInvocation)
            );
        }
    }
    
    shared actual void newFunctionCompletionProposal(Integer offset, String prefix,
        String desc, String text, Declaration dec, Unit unit, 
        CompletionContext ctx) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(
                EclipseFunctionCompletionProposal {            
                    offset = offset;
                    prefix = prefix;
                    desc = desc;
                    text = text;
                    declaration = dec;
                    rootNode = ctx.lastCompilationUnit;
                }
            );
        }
    }
    
    shared actual void newControlStructureCompletionProposal(Integer offset, String prefix,
        String desc, String text, Declaration dec, 
        CompletionContext ctx, Node? node) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(
                EclipseControlStructureProposal {
                    offset = offset;
                    prefix = prefix;
                    desc = desc;
                    text = text;
                    declaration = dec;
                    cpc = ctx;
                    node = node;
                }
            );
        }
    }
    
    shared actual void newTypeProposal(ProposalsHolder ctx, Integer offset, Type? type, String text, 
        String desc, Tree.CompilationUnit rootNode) {
        
        if (is EclipseCompletionContext ctx) {
            ctx.proposals.add(TypeProposal(offset, type, text, desc, rootNode));
        }
    }
    
    createProposalsHolder() => EclipseProposalsHolder();
    
    shared actual void addProposal(ProposalsHolder proposals, Icons|Declaration icon, 
        String description, DefaultRegion region, String text, TextChange? change) {
        
        if (is EclipseProposalsHolder proposals,
            is EclipseTextChange? change) {
            
            value image = switch (icon)
            case (is Declaration) CeylonLabelProvider.getImageForDeclaration(icon)
            else eclipseIcons.fromIcons(icon);
            
            proposals.add(GenericProposal(description, text, region, change, image));
        }
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
    EclipseTextChange? change, shared actual Image? image)
        satisfies IEclipseCompletionProposal2And6 {
    
    shared actual String? additionalProposalInfo => null;
    
    shared actual void apply(IDocument iDocument) {
        iDocument.replace(region.start, region.length, text);
        
        if (exists change) {
            change.apply();
        }
    }
    
    shared actual void apply(ITextViewer viewer, Character char,
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
