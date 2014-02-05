package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
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
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.quickfix.CreateSubtypeInNewUnitProposal.CreateSubtype;

class CreateObjectProposal extends ChangeCorrectionProposal {
    
    final int offset;
    final int length;
    final IFile file;
    
    CreateObjectProposal(ProducedType type, int offset, int length, 
            IFile file, TextChange change) {
        super("Create instance of '" + type.getProducedTypeName() + "'", 
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

    static void addCreateObjectProposal(IDocument doc, Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Node node) {
    	Tree.Statement statement = findStatement(cu, node);
        if (statement!=null) {
            ProducedType type = CreateSubtypeInNewUnitProposal.getType(cu, node);
            if (type!=null && CreateSubtypeInNewUnitProposal.proposeSubtype(type)) {
                TextChange change = new TextFileChange("Create Object", file);
                change.setEdit(new MultiTextEdit());
                Integer offset = statement.getStartIndex();
                String name = type.getDeclaration().getName().replace("&", "")
                		.replace("<", "").replace(">", "");
                CreateSubtype cs = subtypeDeclaration(type, 
                		cu.getUnit().getPackage(), cu.getUnit(), 
                		true, doc);
            	HashSet<Declaration> already = new HashSet<Declaration>();
                for (ProducedType pt: cs.getImportedTypes()) {
                	importType(already, pt, cu);
                }
                int il = applyImports(change, already, cu, doc);
                String delim = getDefaultLineDelimiter(doc);
				String dec = cs.getDefinition().replace("$className", "my" + name) + 
						delim;
                dec = dec.replaceAll(delim, delim + getIndent(node, doc));
                change.addEdit(new InsertEdit(offset,dec));
                proposals.add(new CreateObjectProposal(type, 
                        offset+7+il, name.length()+2, file, change));
            }
        }
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        return style(getDisplayString());
    }
    
}