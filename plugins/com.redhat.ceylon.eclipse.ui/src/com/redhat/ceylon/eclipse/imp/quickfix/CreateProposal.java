package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.editor.CeylonAutoEditStrategy.getDefaultIndent;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.editor.Util;

class CreateProposal extends ChangeCorrectionProposal {
    
    final int offset;
    final IFile file;
    final int length;
    
    CreateProposal(String def, String desc, Image image, int indentLength, 
            int offset, IFile file, TextFileChange change) {
        super(desc, change, 50, image);
        int loc = def.indexOf("bottom;");
        if (loc<0) {
            loc = def.indexOf("{}")+1;
            length=0;
        }
        else {
            length=6;
        }
        this.offset=offset+indentLength + loc;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }

    static IDocument getDocument(TextFileChange change) {
        IDocument doc;
        try {
            doc = change.getCurrentDocument(null);
        }
        catch (CoreException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    static void addCreateMemberProposal(Collection<ICompletionProposal> proposals, String def,
            String desc, Image image, Declaration typeDec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.Body body) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange("Create Member", file);
        IDocument doc = CreateProposal.getDocument(change);
        String indent;
        String indentAfter;
        int offset;
        List<Tree.Statement> statements = body.getStatements();
        if (statements.isEmpty()) {
            indentAfter = "\n" + CeylonQuickFixAssistant.getIndent(decNode, doc);
            indent = indentAfter + getDefaultIndent();
            offset = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = statements.get(statements.size()-1);
            indent = "\n" + CeylonQuickFixAssistant.getIndent(statement, doc);
            offset = statement.getStopIndex()+1;
            indentAfter = "";
        }
        change.setEdit(new InsertEdit(offset, indent+def+indentAfter));
        proposals.add(new CreateProposal(def, 
                "Create " + desc + " in '" + typeDec.getName() + "'", 
                image, indent.length(), offset, file, change));
    }

    static void addCreateProposal(Collection<ICompletionProposal> proposals, String def,
            boolean local, String desc, Image image, PhasedUnit unit, Tree.Statement statement) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange(local ? "Create Local" : "Create Toplevel", file);
        IDocument doc = CreateProposal.getDocument(change);
        String indent = CeylonQuickFixAssistant.getIndent(statement, doc);
        int offset = statement.getStartIndex();
        def = def.replace("$indent", indent);
        change.setEdit(new InsertEdit(offset, def+"\n"+indent));
        proposals.add(new CreateProposal(def, 
                (local ? "Create local " : "Create toplevel ") + desc, 
                image, 0, offset, file, change));
    }

    static void addCreateEnumProposal(Collection<ICompletionProposal> proposals, String def,
            String desc, Image image, PhasedUnit unit, Tree.Statement statement) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange("Create Enumerated", file);
        IDocument doc = CreateProposal.getDocument(change);
        String indent = CeylonQuickFixAssistant.getIndent(statement, doc);
        String s = indent + def + "\n";
        int offset = statement.getStopIndex()+2;
        if (offset>doc.getLength()) {
            offset = doc.getLength();
            s = "\n" + s;
        }
        //def = def.replace("$indent", indent);
        change.setEdit(new InsertEdit(offset, s));
        proposals.add(new CreateProposal(def, "Create enumerated " + desc, 
                image, 0, offset, file, change));
    }

    static void addCreateParameterProposal(Collection<ICompletionProposal> proposals, String def,
            String desc, Image image, Declaration typeDec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.ParameterList paramList) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange("Add Parameter", file);
        int offset = paramList.getStopIndex();
        change.setEdit(new InsertEdit(offset, def));
        proposals.add(new CreateProposal(def, 
                "Create " + desc + " in '" + typeDec.getName() + "'", 
                image, 0, offset, file, change));
    }

}