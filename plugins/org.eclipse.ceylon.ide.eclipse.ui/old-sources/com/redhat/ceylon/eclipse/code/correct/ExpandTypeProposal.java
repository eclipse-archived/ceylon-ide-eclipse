/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.model.typechecker.util.TypePrinter;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;

public class ExpandTypeProposal extends CorrectionProposal {

    private static final class FindTypeVisitor extends Visitor {
        private final IRegion region;
        Tree.Type result;

        private FindTypeVisitor(IRegion region) {
            this.region = region;
        }

        @Override
        public void visit(Tree.Type that) {
            super.visit(that);
            Integer start = that.getStartIndex();
            Integer stop = that.getEndIndex();
            if (start!=null && stop!=null &&
                    region.getOffset()<=start &&
                    region.getOffset()+region.getLength()>=stop) {
                result = that;
            }
        }
    }

    public ExpandTypeProposal(String name, Change change, Region selection) {
        super(name, change, selection);
    }
    
    public static void addExpandTypeProposal(CeylonEditor editor, 
            Node node, IFile file, IDocument doc,
            Collection<ICompletionProposal> proposals) {
        if (node==null) return;
        FindTypeVisitor ftv = new FindTypeVisitor(editor.getSelection());
        node.visit(ftv);
        Tree.Type result = ftv.result;
        if (result!=null) {
            Type type = result.getTypeModel();
            int start = result.getStartIndex();
            int len = result.getDistance();
            String text;
            try {
                text = doc.get(start, len);
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
            String unabbreviated = 
                    new TypePrinter(false)
                        .print(type, node.getUnit());
            if (!unabbreviated.equals(text)) {
                TextChange change = new TextFileChange("Expand Type", file);
                change.setEdit(new ReplaceEdit(start, len, unabbreviated));
                proposals.add(new ExpandTypeProposal("Expand type abbreviation", 
                        change, new Region(start, unabbreviated.length())));
            }
        }
    }

}
