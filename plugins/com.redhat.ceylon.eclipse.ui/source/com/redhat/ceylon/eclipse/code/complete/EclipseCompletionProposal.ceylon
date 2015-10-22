import ceylon.interop.java {
    javaString
}

import com.redhat.ceylon.eclipse.code.preferences {
    CeylonPreferenceInitializer
}
import com.redhat.ceylon.eclipse.ui {
    CeylonPlugin
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.completion {
    CommonCompletionProposal
}
import com.redhat.ceylon.model.typechecker.model {
    ModelUtil
}

import java.lang {
    CharSequence,
    JCharacter=Character
}

import org.eclipse.jface.text {
    ITextViewer,
    IDocument,
    DocumentEvent,
    BadLocationException,
    IInformationControlCreator
}
import org.eclipse.jface.text.contentassist {
    IContextInformation
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt {
    SWT
}
import org.eclipse.swt.graphics {
    Point
}

// see CompletionProposal
interface EclipseCompletionProposal
        satisfies IEclipseCompletionProposal
                & EclipseLinkedModeSupport
                & CommonCompletionProposal<IDocument,Point> {
    
    shared variable formal Boolean toggleOverwriteInternal;
    shared formal variable String? currentPrefix;
    
    shared actual String displayString => description;
    shared default actual String? additionalProposalInfo => null;
    shared default actual Boolean autoInsertable => true;
    shared default Boolean qualifiedNameIsPath => false;
    
    shared default actual StyledString styledDisplayString {
        value result = StyledString();
        Highlights.styleFragment(result, 
            displayString, 
            qualifiedNameIsPath, 
            currentPrefix,
            CeylonPlugin.completionFont);
        return result;
    }
    
    shared default actual IContextInformation? contextInformation => null;
        
    shared default actual void apply(IDocument doc) {
    }
    
    shared default actual void apply(ITextViewer viewer, Character trigger, Integer stateMask, Integer offset) {
        toggleOverwriteInternal = stateMask.and(SWT.\iCTRL) != 0;
        length = prefix.size + offset - this.offset;
        apply(viewer.document);
    }
    
    shared actual void selected(ITextViewer? iTextViewer, Boolean boolean) {}
    
    shared actual void unselected(ITextViewer? iTextViewer) {}
    
    shared actual Boolean validate(IDocument document, Integer offset, DocumentEvent? event) {
        if (offset < this.offset) {
            return false;
        }
        currentPrefix = getCurrentPrefix(document, offset);
        return if (exists pr = currentPrefix) then isProposalMatching(pr, text) else false;

    }
    
    shared default Boolean isProposalMatching(variable String currentPrefix, variable String text) {
        return ModelUtil.isNameMatching(currentPrefix, text);
    }

    String? getCurrentPrefix(IDocument document, Integer offset) {
        try {
            variable Integer start = this.offset - prefix.size;
            return document.get(start, offset - start);
        } catch (BadLocationException e) {
            return null;
        }
    }

    shared actual CharSequence getPrefixCompletionText(IDocument document, Integer completionOffset) {
        return javaString(withoutDupeSemi(document));
    }
    
    shared actual Integer getPrefixCompletionStart(IDocument document, Integer completionOffset) {
        return start();
    }
    
    shared actual IInformationControlCreator? informationControlCreator => null;
    
    shared actual Point getSelection(IDocument document) => getSelectionInternal(document);
    
    shared actual String completionMode => CeylonPlugin.preferences.getString(CeylonPreferenceInitializer.\iCOMPLETION);
    
    shared actual JCharacter getDocChar(IDocument doc, Integer offset) => JCharacter(doc.getChar(offset));
    
    shared actual Integer getDocLength(IDocument doc) => doc.length;
    
    shared actual String getDocSpan(IDocument doc, Integer start, Integer length)
            => doc.get(start, length);
    
    shared actual Point newRegion(Integer start, Integer length) => Point(start, length);
    shared actual Integer getRegionStart(Point region) => region.x;
    shared actual Integer getRegionLength(Point region) => region.y;
    
    shared actual void replaceInDoc(IDocument doc, Integer start, Integer length, String newText) {
        doc.replace(start, length, newText);
    }
}
