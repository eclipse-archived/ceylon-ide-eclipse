/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.code.correct {
    LinkedModeCompletionProposal {
        NullProposal
    }
}
import org.eclipse.ceylon.ide.eclipse.code.outline {
    CeylonLabelProvider
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonResources
}
import org.eclipse.ceylon.ide.common.correct {
    AssignToLocalProposal
}
import org.eclipse.ceylon.model.typechecker.model {
    Unit,
    Type
}

import java.util {
    Collections
}

import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ceylon.ide.common.completion {
    ProposalsHolder
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseProposalsHolder
}

class EclipseAssignToLocalProposal(EclipseQuickFixData data, String desc)
        extends EclipseLocalProposal(data, desc)
        satisfies AssignToLocalProposal {

    shared actual void toNameProposals(String[] names, ProposalsHolder proposals, 
        Integer offset, Unit unit, Integer seq) {
        
        if (is EclipseProposalsHolder proposals) {
            proposals.add(NullProposal(Collections.emptyList<ICompletionProposal>()));
            names.each((n) => proposals.add(LinkedModeCompletionProposal(n, offset, seq)));
        }
    }
    
    shared actual void toProposals(<String|Type>[] types, ProposalsHolder proposals,
        Integer offset, Unit unit) {
        
        assert(is EclipseProposalsHolder proposals);
        
        types.each((t) {
            if (is String t) {
                proposals.add(LinkedModeCompletionProposal(t, offset, t, 0,
                    CeylonLabelProvider.getDecoratedImage(CeylonResources.\iCEYLON_LITERAL, 0, false)));
            } else {
                proposals.add(LinkedModeCompletionProposal(t, unit, offset, 0));
            }
        });
    }
    
    styledDisplayString => 
        let(hint = CorrectionUtil.shortcut("org.eclipse.ceylon.ide.eclipse.ui.action.assignToLocal"))
        StyledString(displayString).append(hint, StyledString.\iQUALIFIER_STYLER);
    
}
