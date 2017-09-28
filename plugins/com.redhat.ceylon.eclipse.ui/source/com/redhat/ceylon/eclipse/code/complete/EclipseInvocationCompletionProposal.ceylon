import org.eclipse.ceylon.ide.eclipse.code.correct {
    EclipseDocument
}
import org.eclipse.ceylon.ide.eclipse.code.hover {
    DocumentationHover
}
import org.eclipse.ceylon.ide.eclipse.code.outline {
    CeylonLabelProvider
}
import org.eclipse.ceylon.ide.eclipse.code.preferences {
    CeylonPreferenceInitializer
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseProposalsHolder
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin,
    CeylonResources
}
import org.eclipse.ceylon.ide.eclipse.util {
    Highlights
}
import org.eclipse.ceylon.ide.common.completion {
    InvocationCompletionProposal,
    getProposedName,
    appendPositionalArgs,
    ProposalsHolder
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration,
    Reference,
    Scope,
    ModelUtil,
    Functional,
    Unit
}

import org.eclipse.core.runtime {
    NullProgressMonitor
}
import org.eclipse.jface.text {
    IDocument,
    IRegion,
    BadLocationException,
    ITextViewer,
    DocumentEvent
}
import org.eclipse.jface.text.contentassist {
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

shared class EclipseInvocationCompletionProposal(Integer _offset, String prefix, 
            String description, String text, Declaration dec,
            Reference()? producedReference, Scope scope, 
            EclipseCompletionContext ctx, Boolean includeDefaulted,
            Boolean positionalInvocation, Boolean namedInvocation, 
            Boolean inheritance, Boolean qualified, Declaration? qualifyingValue)
        extends InvocationCompletionProposal
                (_offset, prefix, description, text, dec, producedReference, scope, ctx.lastCompilationUnit,
    includeDefaulted, positionalInvocation, namedInvocation, inheritance, qualified, qualifyingValue)
        satisfies EclipseCompletionProposal {
    
    shared actual variable String? currentPrefix = prefix;
    shared actual variable Boolean toggleOverwriteInternal = false;

    shared actual overloaded void apply(IDocument doc) {
        value commonDocument = EclipseDocument(doc);
        createChange(commonDocument).apply();
        
        if (CeylonPlugin.preferences.getBoolean(CeylonPreferenceInitializer.linkedModeArguments)) {
            activeLinkedMode(commonDocument, ctx);
        }
    }
    
    shared actual Image image => CeylonLabelProvider.getImageForDeclaration(dec);
    
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
    
    shared actual Boolean isProposalMatching(String currentPrefix, String text) {
        if (super.isProposalMatching(currentPrefix, text)) {
            return true;
        }
        
        for (al in dec.aliases) {
            if (ModelUtil.isNameMatching(currentPrefix, al.string)) {
                return true;
            }
        }
        return false;
    }
    
    shared actual String? additionalProposalInfo {
        value ref = if (exists producedReference)
            then producedReference()
            else null;
        
        return DocumentationHover.getDocumentationFor(ctx.cpc, dec, 
            ref, NullProgressMonitor());        
    }

    shared actual IContextInformation? contextInformation {
        if (namedInvocation || positionalInvocation) {
            if (is Functional fd = dec) {
                value pls = fd.parameterLists;
                if (!pls.empty) {
                    value argListOffset = 
                            if (parameterInfo)
                            then this.offset
                            else offset - prefix.size + (text.firstOccurrence(if (namedInvocation) then '{' else '(') else -1);
                    value ref = if (exists producedReference)
                        then producedReference()
                        else null;
                    
                    value unit = ctx.lastCompilationUnit.unit;
                    return ParameterContextInformation(dec, ref,
                        unit, pls.get(0), argListOffset, includeDefaulted, namedInvocation);
                }
            }
        }
        
        return null;
    }

    shared default Boolean parameterInfo => false;

    shared actual void newNestedCompletionProposal(ProposalsHolder proposals, Declaration dec, Declaration? qualifier,
        Integer loc, Integer index, Boolean basic, String op) {
        
        if (is EclipseProposalsHolder proposals) {
            proposals.add(NestedCompletionProposal(dec, qualifier, loc, index, basic, op));
        }
    }
    
    shared actual void newNestedLiteralCompletionProposal(ProposalsHolder proposals, String val, Integer loc, Integer index) {
        if (is EclipseProposalsHolder proposals) {
            proposals.add(NestedLiteralCompletionProposal(val, loc, index));
        }
    }
    
    class NestedCompletionProposal(Declaration dec, Declaration? qualifier, Integer loc, Integer index, Boolean basic, String op) 
            satisfies IEclipseCompletionProposal2And6 {
        
        shared actual String? additionalProposalInfo => null;
        
        shared actual overloaded void apply(IDocument document) {
            //the following awfulness is necessary because the
            //insertion point may have changed (and even its
            //text may have changed, since the proposal was
            //instantiated).
            try {
                IRegion region = 
                        CompletionUtil.getCurrentArgumentRegion(
                            document, loc, index, getFirstPosition());
                variable String str = getText(false);
                Integer start = region.offset;
                Integer len = region.length;
                Integer end = start + len;
                if (document.getChar(end) == '}') {
                    str += " ";
                }
                
                document.replace(start, len, str);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
        String getText(Boolean description) {
            StringBuilder sb = StringBuilder().append(op);
            Unit unit = ctx.lastCompilationUnit.unit;
            sb.append(getProposedName(qualifier, dec, unit));
            if (dec is Functional, !basic) {
                appendPositionalArgs(dec, dec.reference, unit, sb, false, description, false);
            }
            
            return sb.string;
        }
        
        shared actual Point? getSelection(IDocument document) => null;
        
        shared actual String displayString => getText(true);
        
        shared actual StyledString styledDisplayString {
            value result = StyledString();
            Highlights.styleFragment(result, displayString, false, null, CeylonPlugin.completionFont);
            return result;
        }
        
        shared actual Image image => CeylonLabelProvider.getImageForDeclaration(dec);
        
        shared actual IContextInformation? contextInformation => null;
        
        shared actual overloaded void apply(ITextViewer viewer, Character trigger, Integer stateMask, Integer offset) {
            apply(viewer.document);
        }
        
        shared actual void selected(ITextViewer viewer, Boolean smartToggle) {}
        
        shared actual void unselected(ITextViewer viewer) {}
        
        shared actual Boolean validate(IDocument document, Integer currentOffset, DocumentEvent? event) {
            if (!exists event) {
                return true;
            } else {
                try {
                    IRegion region = 
                            CompletionUtil.getCurrentArgumentRegion(
                                document, loc, index, getFirstPosition());
                    String content = document.get(region.offset, currentOffset - region.offset);
                    return isContentValid(content);
                } catch (BadLocationException e) {
                    return false;
                }
            }
        }
        
        Boolean isContentValid(variable String content) {
            if (exists fat = content.firstInclusion("=>")) {
                content = content[fat + 2...];
            }
            
            if (exists eq = content.firstInclusion("=")) {
                content = content[eq + 1...];
            }
            
            if (content.startsWith(op)) {
                content = content.spanFrom(op.size);
            }
            
            value filter = content.trimmed.lowercased;
            value unit = ctx.lastCompilationUnit.unit;
            
            return ModelUtil.isNameMatching(content, dec)
                || getProposedName(qualifier, dec, unit)
                    	.lowercased.startsWith(filter);
        }
    }

    class NestedLiteralCompletionProposal(String val, Integer loc, Integer index)
            satisfies IEclipseCompletionProposal2And6 {
        
        shared actual String? additionalProposalInfo => null;
        
        shared actual overloaded void apply(IDocument document) {
            //the following awfulness is necessary because the
            //insertion point may have changed (and even its
            //text may have changed, since the proposal was
            //instantiated).
            try {
                IRegion region = 
                        CompletionUtil.getCurrentArgumentRegion(
                            document, loc, index, getFirstPosition());
                variable String str = val;
                Integer start = region.offset;
                Integer len = region.length;
                Integer end = start + len;
                if (document.getChar(end) == '}') {
                    str += " ";
                }
                
                document.replace(start, len, str);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
        shared actual Point? getSelection(IDocument document) => null;
        
        shared actual String displayString => val;
        
        shared actual StyledString styledDisplayString {
            StyledString result = StyledString();
            Highlights.styleFragment(result, displayString, false, null, CeylonPlugin.completionFont);
            return result;
        }
        
        shared actual Image image 
                => CeylonLabelProvider.getDecoratedImage(CeylonResources.ceylonLiteral, 0, false);
        
        shared actual IContextInformation? contextInformation => null;
        
        shared actual overloaded void apply(ITextViewer viewer, Character trigger, Integer stateMask, Integer offset) {
            apply(viewer.document);
        }
        
        shared actual void selected(ITextViewer viewer, Boolean smartToggle) {}
        
        shared actual void unselected(ITextViewer viewer) {}
        
        shared actual Boolean validate(IDocument document, Integer currentOffset, DocumentEvent? event) {
            if (!exists event) {
                return true;
            } else {
                try {
                    IRegion region = 
                            CompletionUtil.getCurrentArgumentRegion(
                                document, loc, index, getFirstPosition());
                    variable String content = document.get(region.offset, currentOffset - region.offset);
                    
                    if (exists eq = content.firstOccurrence('=')) {
                        content = content.spanFrom(eq + 1);
                    }
                    
                    value filter = content.trimmed.lowercased;
                    if (val.lowercased.startsWith(filter)) {
                        return true;
                    }
                } catch (BadLocationException e) {
                    // ignore concurrently modified document
                }
                
                return false;
            }
        }
    }

}