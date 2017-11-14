/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.cmr.api {
    ModuleVersionDetails
}
import org.eclipse.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonResources
}
import org.eclipse.ceylon.ide.eclipse.util {
    eclipseIcons
}
import org.eclipse.ceylon.ide.common.correct {
    QuickFixData,
    QuickFixKind
}
import org.eclipse.ceylon.ide.common.doc {
    Icons
}
import org.eclipse.ceylon.ide.common.model {
    BaseCeylonProject
}
import org.eclipse.ceylon.ide.common.platform {
    CommonTextChange=TextChange
}
import org.eclipse.ceylon.ide.common.refactoring {
    DefaultRegion
}
import org.eclipse.ceylon.model.typechecker.model {
    Referenceable
}

import java.util {
    Collection
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}

shared class EclipseQuickFixData(ProblemLocation location,
    shared actual Tree.CompilationUnit rootNode,
    shared actual Node node,
    shared IProject project,
    shared Collection<ICompletionProposal> proposals,
    shared CeylonEditor editor,
    shared actual BaseCeylonProject ceylonProject,
    IDocument doc)
        satisfies QuickFixData {
    
    errorCode => location.problemId;
    problemOffset => location.offset;
    problemLength => location.length;
    
    phasedUnit => editor.parseController.lastPhasedUnit;
    document = EclipseDocument(doc);
    editorSelection => DefaultRegion {
        start = editor.selection.offset;
        length = editor.selection.length;
    };
    
    shared actual void addQuickFix(String desc, 
        CommonTextChange|Anything() change,
        DefaultRegion? selection, 
        Boolean qualifiedNameIsPath, 
        Icons? icon,
        QuickFixKind kind, 
        String? hint, 
        Boolean asynchronous, 
        Referenceable|ModuleVersionDetails? declaration,
        Boolean affectsOtherUnits) {
        
        value myImage 
                = eclipseIcons.fromIcons(icon) 
                else CeylonResources.minorChange;

        if (is CommonTextChange change) {
            proposals.add(
                proposalsFactory.createProposal {
                    description = desc;
                    change = change;
                    selection = selection;
                    qualifiedNameIsPath = qualifiedNameIsPath;
                    myImage = myImage;
                    kind = kind;
                }
            );
        } else {
            value callback = change;
            proposals.add(object extends CorrectionProposal
                (desc, null, null, myImage, qualifiedNameIsPath) {
                apply(IDocument? doc) => callback();
            });
        }
    }
    
    addConvertToClassProposal(String description, 
        Tree.ObjectDefinition declaration) 
            => proposals.add(EclipseConvertToClassProposal {
                desc = description;
                editor = editor;
                declaration = declaration;
            });
        
    addAssignToLocalProposal(String description) 
            => proposals.add(EclipseAssignToLocalProposal(this, description));
}
