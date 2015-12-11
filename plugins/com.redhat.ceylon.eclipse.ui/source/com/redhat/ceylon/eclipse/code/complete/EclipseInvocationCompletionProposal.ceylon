import ceylon.interop.java {
    CeylonIterable
}

import com.redhat.ceylon.eclipse.code.complete {
    EInvocationCompletionProposal=InvocationCompletionProposal
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges,
    eclipseImportProposals
}
import com.redhat.ceylon.eclipse.code.hover {
    DocumentationHover
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.code.preferences {
    CeylonPreferenceInitializer
}
import com.redhat.ceylon.eclipse.ui {
    CeylonPlugin,
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.completion {
    InvocationCompletionProposal,
    getProposedName,
    appendPositionalArgs
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Reference,
    Scope,
    ModelUtil,
    Functional,
    Unit
}

import org.eclipse.core.resources {
    IFile
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
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    DocumentChange
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

class EclipseInvocationCompletionProposal(Integer _offset, String prefix, 
            String description, String text, Declaration dec,
            Reference? producedReference, Scope scope, 
            CeylonParseController cpc, Boolean includeDefaulted,
            Boolean positionalInvocation, Boolean namedInvocation, 
            Boolean inheritance, Boolean qualified, Declaration? qualifyingValue,
            EclipseCompletionManager completionManager)
        extends InvocationCompletionProposal<CeylonParseController, ICompletionProposal, IFile,
                IDocument, InsertEdit, TextEdit, TextChange, Point, LinkedModeModel>
                (_offset, prefix, description, text, dec, producedReference, scope, cpc.lastCompilationUnit,
    includeDefaulted, positionalInvocation, namedInvocation, inheritance, qualified, qualifyingValue, completionManager)
        satisfies EclipseDocumentChanges & EclipseCompletionProposal {
    
    shared actual variable String? currentPrefix = prefix;
    shared actual variable Boolean toggleOverwriteInternal = false;

    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;

    shared actual void apply(IDocument doc) {
        value change = DocumentChange("Complete Invocation", doc);
        createChange(change, doc).perform(NullProgressMonitor());
        
        if (CeylonPlugin.preferences.getBoolean(CeylonPreferenceInitializer.\iLINKED_MODE_ARGUMENTS)) {
            activeLinkedMode(doc, cpc);
        }
    }
    
    shared actual Image image => CeylonLabelProvider.getImageForDeclaration(dec);
    
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
    
    shared actual Boolean isProposalMatching(String currentPrefix, String text) {
        if (super.isProposalMatching(currentPrefix, text)) {
            return true;
        }
        
        for (al in CeylonIterable(dec.aliases)) {
            if (ModelUtil.isNameMatching(currentPrefix, al.string)) {
                return true;
            }
        }
        return false;
    }
    
    shared actual String? additionalProposalInfo
            => DocumentationHover.getDocumentationFor(cpc, dec, 
                producedReference, NullProgressMonitor());

    shared actual IContextInformation? contextInformation {
        if (namedInvocation || positionalInvocation) {
            if (is Functional fd = dec) {
                value pls = fd.parameterLists;
                if (!pls.empty) {
                    variable Integer argListOffset = 
                            if (parameterInfo)
                            then this.offset
                            else offset - prefix.size + (text.firstOccurrence(if (namedInvocation) then '{' else '(') else -1);
                    
                    value unit = cpc.lastCompilationUnit.unit;
                    return EInvocationCompletionProposal.ParameterContextInformation(dec, producedReference,
                        unit, pls.get(0), argListOffset, includeDefaulted, namedInvocation);
                }
            }
        }
        
        return null;
    }

    shared default Boolean parameterInfo => false;

    shared actual ICompletionProposal newNestedCompletionProposal(Declaration dec, Declaration? qualifier,
        Integer loc, Integer index, Boolean basic, String op)
            => NestedCompletionProposal(dec, qualifier, loc, index, basic, op);
    
    shared actual ICompletionProposal newNestedLiteralCompletionProposal(String val, Integer loc, Integer index)
            => NestedLiteralCompletionProposal(val, loc, index);
    
    class NestedCompletionProposal(Declaration dec, Declaration? qualifier, Integer loc, Integer index, Boolean basic, String op) 
            satisfies IEclipseCompletionProposal2And6 {
        
        shared actual String? additionalProposalInfo => null;
        
        shared actual void apply(IDocument document) {
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
            Unit unit = cpc.lastCompilationUnit.unit;
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
        
        shared actual Image image {
            return CeylonLabelProvider.getImageForDeclaration(dec);
        }
        
        shared actual IContextInformation? contextInformation => null;
        
        shared actual void apply(ITextViewer viewer, Character trigger, Integer stateMask, Integer offset) {
            apply(viewer.document);
        }
        
        shared actual void selected(ITextViewer viewer, Boolean smartToggle) {
        }
        
        shared actual void unselected(ITextViewer viewer) {
        }
        
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
            
            String filter = content.trimmed.lowercased;
            value unit = cpc.lastCompilationUnit.unit;
            
            return ModelUtil.isNameMatching(content, dec)
                    || getProposedName(qualifier, dec, unit)
                        .lowercased.startsWith(filter);
        }
    }

    class NestedLiteralCompletionProposal(String val, Integer loc, Integer index)
            satisfies IEclipseCompletionProposal2And6 {
        
        shared actual String? additionalProposalInfo => null;
        
        shared actual void apply(IDocument document) {
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
        
        shared actual Point? getSelection(IDocument document) {
            return null;
        }
        
        shared actual String displayString {
            return val;
        }
        
        shared actual StyledString styledDisplayString {
            StyledString result = StyledString();
            Highlights.styleFragment(result, displayString, false, null, CeylonPlugin.completionFont);
            return result;
        }
        
        shared actual Image image {
            return CeylonLabelProvider.getDecoratedImage(
                CeylonResources.\iCEYLON_LITERAL, 0, false);
        }
        
        shared actual IContextInformation? contextInformation => null;
        
        shared actual void apply(ITextViewer viewer, Character trigger, Integer stateMask, Integer offset) {
            apply(viewer.document);
        }
        
        shared actual void selected(ITextViewer viewer, Boolean smartToggle) {
        }
        
        shared actual void unselected(ITextViewer viewer) {
        }
        
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
                    
                    variable String filter = content.trimmed.lowercased;
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