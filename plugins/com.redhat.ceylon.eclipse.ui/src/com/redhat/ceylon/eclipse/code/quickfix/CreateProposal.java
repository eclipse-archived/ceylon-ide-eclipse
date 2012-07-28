package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.Util;

class CreateProposal extends ChangeCorrectionProposal {
    
    final int offset;
    final IFile file;
    final int length;
    
    CreateProposal(String def, String desc, Image image, int indentLength, 
            int offset, IFile file, TextFileChange change) {
        super(desc, change, 50, image);
        int loc = def.indexOf("= bottom");
        if (loc<0) {
            loc = def.indexOf("= ");
            if (loc<0) {
                loc = def.indexOf("{}")+1;
                length=0;
            }
            else {
                loc += 2;
                length = def.length()-loc;
            }
        }
        else {
            loc += 2;
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
            Tree.Declaration decNode, Tree.Body body, ProducedType t) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Create Member", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        String indent;
        String indentAfter;
        int offset;
        List<Tree.Statement> statements = body.getStatements();
        if (statements.isEmpty()) {
            indentAfter = "\n" + getIndent(decNode, doc);
            indent = indentAfter + getDefaultIndent();
            offset = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = statements.get(statements.size()-1);
            indent = "";
            offset = statement.getStartIndex();
            indentAfter = "\n" + getIndent(statement, doc);
        }
        int il = importType(change, t, unit.getCompilationUnit(), new HashSet<Declaration>());
        change.addEdit(new InsertEdit(offset, indent+def+indentAfter));
        proposals.add(new CreateProposal(def, 
                "Create " + desc + " in '" + typeDec.getName() + "'", 
                image, indent.length(), offset+il, file, change));
    }

    static void addCreateProposal(Collection<ICompletionProposal> proposals, String def,
            boolean local, String desc, Image image, PhasedUnit unit, Tree.Statement statement,
            ProducedType t) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange(local ? "Create Local" : "Create Toplevel", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        String indent = getIndent(statement, doc);
        int offset = statement.getStartIndex();
        def = def.replace("$indent", indent);
        int il = importType(change, t, unit.getCompilationUnit(), new HashSet<Declaration>());
        change.addEdit(new InsertEdit(offset, def+"\n"+indent));
        proposals.add(new CreateProposal(def, 
                (local ? "Create local " : "Create toplevel ") + desc, 
                image, 0, offset+il, file, change));
    }

    static void addCreateEnumProposal(Collection<ICompletionProposal> proposals, String def,
            String desc, Image image, PhasedUnit unit, Tree.Statement statement) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Create Enumerated", file);
        IDocument doc = getDocument(change);
        String indent = getIndent(statement, doc);
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

    static void addCreateParameterProposal(Collection<ICompletionProposal> proposals, 
            String def, String desc, Image image, Declaration dec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.ParameterList paramList, ProducedType t) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Add Parameter", file);
        change.setEdit(new MultiTextEdit());
        int offset = paramList.getStopIndex();
        int il = importType(change, t, unit.getCompilationUnit(), new HashSet<Declaration>());
        change.addEdit(new InsertEdit(offset, def));
        proposals.add(new CreateProposal(def, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                image, 0, offset+il, file, change));
    }

    static void addCreateParameterAndAttributeProposal(Collection<ICompletionProposal> proposals, 
            String pdef, String adef, String desc, Image image, Declaration dec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.ParameterList paramList, Tree.Body body, ProducedType t) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Add Attribute", file);
        change.setEdit(new MultiTextEdit());
        int offset = paramList.getStopIndex();
        IDocument doc = getDocument(change);
        String indent;
        String indentAfter;
        int offset2;
        List<Tree.Statement> statements = body.getStatements();
        if (statements.isEmpty()) {
            indentAfter = "\n" + getIndent(decNode, doc);
            indent = indentAfter + getDefaultIndent();
            offset2 = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = statements.get(statements.size()-1);
            indent = "\n" + getIndent(statement, doc);
            offset2 = statement.getStopIndex()+1;
            indentAfter = "";
        }
        int il = importType(change, t, unit.getCompilationUnit(), new HashSet<Declaration>());
        change.addEdit(new InsertEdit(offset, pdef));
        change.addEdit(new InsertEdit(offset2, indent+adef+indentAfter));
        proposals.add(new CreateProposal(pdef, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                image, 0, offset+il, file, change));
    }

}