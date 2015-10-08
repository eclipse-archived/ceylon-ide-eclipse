package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.Indents.indents;

import java.util.Collection;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ObjectDefinition;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.AbstractLinkedMode;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.util.Escaping;
import com.redhat.ceylon.model.typechecker.model.Value;

class ConvertToClassProposal extends AbstractLinkedMode implements ICompletionProposal {

    private final Tree.ObjectDefinition node;
    
    public ConvertToClassProposal(Tree.ObjectDefinition node, 
            CeylonEditor editor) {
        super(editor);
        this.node = node;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return node.getDeclarationModel().isShared() ? 
                CeylonResources.CLASS : 
                CeylonResources.LOCAL_CLASS;
    }

    @Override
    public String getDisplayString() {
        return "Convert " + node.getDeclarationModel().getName() + " to class";
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public void apply(IDocument doc) {
        Value declaration = node.getDeclarationModel();
        String name = declaration.getName();
        String initialName = Escaping.toInitialUppercase(name);
        TextChange change = new DocumentChange("Convert to Class", doc);
        change.setEdit(new MultiTextEdit());
        Tree.ObjectDefinition od = (Tree.ObjectDefinition) node;
        int dstart = ((CommonToken) od.getMainToken()).getStartIndex();
        change.addEdit(new ReplaceEdit(dstart, 6, "class"));
        int start = od.getIdentifier().getStartIndex();
        int length = od.getIdentifier().getDistance();
        change.addEdit(new ReplaceEdit(start, length, initialName + "()"));
        int offset = od.getEndIndex();
        //TODO: handle actual object declarations
        String mods = declaration.isShared() ? "shared " : "";
        String ws = indents().getDefaultLineDelimiter(doc) + indents().getIndent(od, doc);
        String impl = " = " + initialName + "();";
        String dec = ws + mods + initialName + " " + name;
        change.addEdit(new InsertEdit(offset, dec + impl));
        try {
            EditorUtil.performChange(change);
            
            LinkedPositionGroup group = new LinkedPositionGroup();
            group.addPosition(new LinkedPosition(doc, start-1, length, 0));
            group.addPosition(new LinkedPosition(doc, offset+ws.length()+mods.length()+1, length, 1));
            group.addPosition(new LinkedPosition(doc, offset+dec.length()+4, length, 2));
            linkedModeModel.addGroup(group);
            enterLinkedMode(doc, -1, start-1);
            openPopup();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void addConvertToClassProposal(Collection<ICompletionProposal> proposals, 
            Tree.Declaration declaration, CeylonEditor editor) {
        if (declaration instanceof Tree.ObjectDefinition) {
            ConvertToClassProposal prop = 
                    new ConvertToClassProposal((ObjectDefinition) declaration, editor);
            proposals.add(prop);
        }
    }
    
    @Override
    protected String getHintTemplate() {
        return "Enter name for new class {0}";
    }

}