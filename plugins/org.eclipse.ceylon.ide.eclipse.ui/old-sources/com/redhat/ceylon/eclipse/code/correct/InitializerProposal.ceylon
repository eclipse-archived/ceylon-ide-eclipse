/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.code.complete {
    IEclipseCompletionProposal2And6,
    CompletionUtil
}
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor,
    Navigation
}
import org.eclipse.ceylon.ide.eclipse.code.outline {
    CeylonLabelProvider
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin,
    CeylonResources
}
import org.eclipse.ceylon.ide.eclipse.util {
    Highlights
}
import org.eclipse.ceylon.ide.common.completion {
    getProposedName,
    appendPositionalArgs,
    ProposalsHolder
}
import org.eclipse.ceylon.ide.common.correct {
    AbstractInitializerQuickFix
}
import org.eclipse.ceylon.ide.common.model {
    ModifiableSourceFile
}
import org.eclipse.ceylon.ide.common.refactoring {
    DefaultRegion
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration,
    Scope,
    Unit,
    Type,
    Functional,
    ModelUtil
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}
import org.eclipse.jface.text {
    IDocument,
    Region,
    DocumentEvent,
    ITextViewer
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedPositionGroup
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    Change
}
import org.eclipse.swt.graphics {
    Image,
    Point
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseProposalsHolder
}

class EclipseInitializerProposal(
    String name, 
    Change change,
    Unit unit,
    Scope scope,
    Type? type,
    Region selection,
    Image image,
    variable Integer exitPos) 
        extends CorrectionProposal(name, change, selection, image)
        satisfies AbstractInitializerQuickFix {

    //shared actual void newNestedCompletionProposal(ProposalsHolder proposals,
    //    Declaration dec, Integer offset) {
    //    
    //    if (is EclipseProposalsHolder proposals) {
    //        proposals.add(NestedCompletionProposal(dec, offset));
    //    }
    //}
    //
    //shared actual void newNestedLiteralCompletionProposal(ProposalsHolder proposals,
    //    String val, Integer offset) {
    //    
    //    if (is EclipseProposalsHolder proposals) {
    //        proposals.add(NestedLiteralCompletionProposal(val, offset));
    //    }
    //}
    
    //we don't apply a selection because:
    //1. we're using linked mode anyway, and
    //2. the change might have been applied to
    //   a different editor to the one from
    //   which the quick fix was invoked.
    shared actual Point? getSelection(IDocument? iDocument) => null;
    
    shared actual void apply(variable IDocument document) {
        variable CeylonEditor? editor = null;
        
        if (is ModifiableSourceFile<IProject, IResource, IFolder, IFile> unit) {
            value file = unit.resourceFile;
            if (exists file) {
                assert(is CeylonEditor e = Navigation.gotoFile(file, 0, 0));
                editor = e;
                value ed = e.parseController.document;
                if (ed != document) {
                    document = ed;
                    exitPos = -1;
                }
            }
        }
        
        value lenBefore = document.length;
        super.apply(document);
        value lenAfter = document.length;
        Point? point = super.getSelection(document);
        if (!exists point) {
            return;
        }

        if (exists e = editor) {
            e.selectAndReveal(point.x, point.y);
        }
        if (lenAfter > lenBefore,
            exists e = editor,
            point.y > 0) {

            variable Integer adjustedExitPos = exitPos;
            if (exitPos>=0 && exitPos>point.x) {
                adjustedExitPos += lenAfter-lenBefore;
            }
            Integer exitSeq = exitPos>=0 then 1 else LinkedPositionGroup.\iNO_STOP;

            addInitializer(EclipseDocument(document), DefaultRegion(point.x, point.y), type, 
                unit, scope, exitSeq, adjustedExitPos);
        }
    }
    
    class NestedCompletionProposal(Declaration dec, Integer offset)
            satisfies ICompletionProposal 
                    & IEclipseCompletionProposal2And6 {
        
        shared actual String? additionalProposalInfo => null;

        shared actual void apply(IDocument doc) {
            value region = CompletionUtil.getCurrentSpecifierRegion(doc, offset);
            doc.replace(region.offset, region.length, getText(false));
        }
        
        shared actual void apply(ITextViewer viewer, Character char, 
            Integer int, Integer int1) {
            apply(viewer.document);
        }
        
        shared actual IContextInformation? contextInformation => null;
        
        shared actual String displayString => getText(true);
        
        shared actual Point? getSelection(IDocument? iDocument) => null;
        
        shared actual Image image => CeylonLabelProvider.getImageForDeclaration(dec);
        
        shared actual void selected(ITextViewer? iTextViewer, Boolean boolean) {}
        
        shared actual StyledString styledDisplayString {
            value result = StyledString();
            
            Highlights.styleFragment(result, 
                displayString, false, null, 
                CeylonPlugin.completionFont);

            return result;
        }
        
        shared actual void unselected(ITextViewer? iTextViewer) {}
        
        shared actual Boolean validate(IDocument document, Integer currentOffset,
            DocumentEvent? event) {
            
            if (!exists event) {
                return true;
            }

            value region = CompletionUtil.getCurrentSpecifierRegion(document, offset);
            value content = document.get(region.offset, currentOffset - region.offset);
            return isContentValid(content);
        }
        
        Boolean isContentValid(String content) {
            value filter = content.trimmed.lowercased;
            return ModelUtil.isNameMatching(content, dec)
                    || getProposedName(null, dec, unit).lowercased.startsWith(filter);
        }
        
        String getText(Boolean description) {
            StringBuilder sb = StringBuilder();
            value unit = dec.unit;
            sb.append(getProposedName(null, dec, unit));
            if (is Functional dec) {
                appendPositionalArgs(dec, null, unit, sb, false, description, false);
            }
            
            return sb.string;
        }
    }

    class NestedLiteralCompletionProposal(String val, Integer offset)
            satisfies ICompletionProposal 
            & IEclipseCompletionProposal2And6 {
        
        shared actual String? additionalProposalInfo => null;
        
        shared actual void apply(IDocument doc) {
            value region = CompletionUtil.getCurrentSpecifierRegion(doc, offset);
            doc.replace(region.offset, region.length, val);
        }
        
        shared actual void apply(ITextViewer viewer, Character char, 
            Integer int, Integer int1) {
            apply(viewer.document);
        }
        
        shared actual IContextInformation? contextInformation => null;
        
        shared actual String displayString => val;
        
        shared actual Point? getSelection(IDocument? iDocument) => null;
        
        shared actual Image image 
                => CeylonLabelProvider.getDecoratedImage(CeylonResources.\iCEYLON_LITERAL, 0, false);
        
        shared actual void selected(ITextViewer? iTextViewer, Boolean boolean) {}
        
        shared actual StyledString styledDisplayString {
            value result = StyledString();
            
            Highlights.styleFragment(result, 
                displayString, false, null, 
                CeylonPlugin.completionFont);
            
            return result;
        }
        
        shared actual void unselected(ITextViewer? iTextViewer) {}
        
        shared actual Boolean validate(IDocument document, Integer currentOffset,
            DocumentEvent? event) {
            
            if (!exists event) {
                return true;
            }
            
            value region = CompletionUtil.getCurrentSpecifierRegion(document, offset);
            value content = document.get(region.offset, currentOffset - region.offset);
            value filter = content.trimmed.lowercased;
            return val.lowercased.startsWith(filter);
        }
    }
}