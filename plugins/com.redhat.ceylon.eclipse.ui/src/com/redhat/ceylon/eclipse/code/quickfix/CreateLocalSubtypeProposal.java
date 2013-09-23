package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateSubtypeInNewUnitProposal.subtypeDeclaration;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.quickfix.CreateSubtypeInNewUnitProposal.CreateSubtype;
import com.redhat.ceylon.eclipse.util.FindStatementVisitor;

class CreateLocalSubtypeProposal extends ChangeCorrectionProposal {
    
    final int offset;
    final int length;
    final IFile file;
    
    CreateLocalSubtypeProposal(ProducedType type, int offset, int length, 
            IFile file, TextChange change) {
        super("Create subtype of '" + type.getProducedTypeName() + "'", 
                change);
        this.offset=offset;
        this.length=length;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }

    static void addCreateLocalSubtypeProposal(IDocument doc, Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file, Node node) {
        FindStatementVisitor fsv = new FindStatementVisitor(node, true);
        fsv.visit(cu);
        Statement s = fsv.getStatement();
        if (s!=null) {
            ProducedType type = CreateSubtypeInNewUnitProposal.getType(cu, node);
            if (type!=null && CreateSubtypeInNewUnitProposal.proposeSubtype(type)) {
                TextChange change = new TextFileChange("Create Subtype", file);
//                TextChange change = new DocumentChange("Create Subtype", doc);
                change.setEdit(new MultiTextEdit());
                Integer offset = s.getStartIndex();
                String name = type.getDeclaration().getName()
                		.replace("&", "").replace("<", "").replace(">", "");
                CreateSubtype cs = subtypeDeclaration(type, 
                		cu.getUnit().getPackage(), cu.getUnit(), false);
            	HashSet<Declaration> already = new HashSet<Declaration>();
                for (ProducedType pt: cs.getImportedTypes()) {
                	importType(already, pt, cu);
                }
                int il = applyImports(change, already, cu);
				String dec = cs.getDefinition().replace("$className", "My" + name) + "\n\n";
                change.addEdit(new InsertEdit(offset,dec));
                proposals.add(new CreateLocalSubtypeProposal(type, 
                        offset+6+il, name.length()+2, file, change));
            }
        }
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        return style(getDisplayString());
    }
    
}