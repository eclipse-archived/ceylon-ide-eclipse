import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocument
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.platform {
    EclipseLinkedMode,
    EclipseProposalsHolder
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.completion {
    ImportedModulePackageProposal,
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.platform {
    LinkedMode
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration,
    ModelUtil,
    TypeDeclaration
}

import java.lang {
    JCharacter=Character
}

import org.eclipse.jface.text {
    IDocument,
    DocumentEvent,
    ITextViewer,
    BadLocationException
}
import org.eclipse.jface.text.contentassist {
    IContextInformation
}
import org.eclipse.jface.text.link {
    ILinkedModeListener
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point,
    Image
}

shared class EclipseImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
                String fullPackageName, EclipseCompletionContext ctx, Package candidate)
                extends ImportedModulePackageProposal
                (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate, ctx)
                satisfies EclipseCompletionProposal{

    shared actual variable String? currentPrefix = prefix;
    shared actual variable Boolean toggleOverwriteInternal = false;
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;

    shared actual Boolean qualifiedNameIsPath => true;
    
    shared actual Image image => CeylonResources.\iPACKAGE;
    
    shared actual void apply(IDocument doc) => applyInternal(EclipseDocument(doc));
    
    shared actual void newPackageMemberCompletionProposal(ProposalsHolder proposals, Declaration d, DefaultRegion selection, LinkedMode lm) {
        if (is EclipseProposalsHolder proposals) {
            value proposal = object satisfies IEclipseCompletionProposal2And6 {
                function length(IDocument document) {
                    variable value length = 0;
                    variable value i = selection.start;
                    try {
                        while (i<document.length &&
                            (JCharacter.isJavaIdentifierPart(document.getChar(i)) ||
                            document.getChar(i)=='.')) {
                            length++;
                            i++;
                        }
                    }
                    catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    return length;
                }
                
                shared actual void apply(IDocument document) {
                    try {
                        document.replace(selection.start,
                            length(document),
                            d.name);
                    }
                    catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    
                    assert(is EclipseLinkedMode lm);
                    lm.model.exit(ILinkedModeListener.\iUPDATE_CARET);
                }
                
                shared actual void apply(ITextViewer viewer, Character trigger, Integer stateMask, Integer offset) {
                    apply(viewer.document);
                }
                
                shared actual Point? getSelection(IDocument document) => null;
                
                shared actual Image image => CeylonLabelProvider.getImageForDeclaration(d);
                
                shared actual String displayString => d.name;
                
                shared actual IContextInformation? contextInformation => null;
                
                shared actual String? additionalProposalInfo => null;
                
                shared actual void selected(ITextViewer iTextViewer, Boolean boolean) {}
                
                shared actual void unselected(ITextViewer iTextViewer) {}
                
                shared actual StyledString styledDisplayString {
                    StyledString result = StyledString();
                    Highlights.styleIdentifier(result, prefix,
                        displayString,
                        d is TypeDeclaration 
                        then Highlights.\iTYPE_STYLER 
                        else Highlights.\iMEMBER_STYLER,
                        CeylonPlugin.completionFont);
                    return result;
                }
                
                shared actual Boolean validate(IDocument document, Integer currentOffset, DocumentEvent? documentEvent) {
                    value start = selection.start;
                    if (currentOffset<start) {
                        return false;
                    }
                    String prefix;
                    try {
                        prefix = document.get(start, currentOffset-start);
                    }
                    catch (BadLocationException e) {
                        return false;
                    }
                    return ModelUtil.isNameMatching(prefix, d);
                }
                
            };
            
            proposals.add(proposal);
        }
    }
}