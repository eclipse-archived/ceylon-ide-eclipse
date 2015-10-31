import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges,
    eclipseImportProposals
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}
import com.redhat.ceylon.ide.common.completion {
    ImportedModulePackageProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration,
    ModelUtil,
    TypeDeclaration
}

import org.eclipse.core.resources {
    IFile,
    IProject
}
import org.eclipse.jface.text {
    IDocument,
    DocumentEvent,
    ITextViewer,
    BadLocationException
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedModeModel,
    ILinkedModeListener
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.jface.viewers {
    StyledString
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import java.lang {
    JCharacter=Character
}

class EclipseImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
                String fullPackageName, CeylonParseController controller, Package candidate)
                extends ImportedModulePackageProposal<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange,Point,LinkedModeModel,CeylonParseController,IProject>
                (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate, controller)
                satisfies EclipseDocumentChanges & EclipseCompletionProposal{

    shared actual variable String? currentPrefix = prefix;
    shared actual variable Boolean toggleOverwriteInternal = false;
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;

    shared actual Boolean qualifiedNameIsPath => true;
    
    shared actual Image image => CeylonResources.\iPACKAGE;
    
    shared actual void apply(IDocument doc) => applyInternal(doc);
    
    shared actual ICompletionProposal newPackageMemberCompletionProposal(Declaration d, Point selection, LinkedModeModel lm) 
            => object satisfies IEclipseCompletionProposal2And6 {
        function length(IDocument document) {
            variable value length = 0;
            variable value i = selection.x;
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
                document.replace(selection.x,
                    length(document),
                    d.name);
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
            lm.exit(ILinkedModeListener.\iUPDATE_CARET);
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
            value start = selection.x;
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
}