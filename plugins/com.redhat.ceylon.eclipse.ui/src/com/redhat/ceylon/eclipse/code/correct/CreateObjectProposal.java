package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CreateSubtypeInNewUnitProposal.subtypeDeclaration;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.correct.CreateSubtypeInNewUnitProposal.CreateSubtype;
import com.redhat.ceylon.eclipse.util.FindUtils;
import com.redhat.ceylon.eclipse.util.Indents;

class CreateObjectProposal extends CorrectionProposal {
    
    private final int offset;
    private final int length;
    
    CreateObjectProposal(ProducedType type, int offset, int length, 
            IFile file, TextChange change) {
        super("Create instance of '" + type.getProducedTypeName() + "'", 
                change);
        this.offset=offset;
        this.length=length;
    }
    
    @Override
    public Point getSelection(IDocument document) {
        return new Point(offset, length);
    }
    
    static void addCreateObjectProposal(IDocument doc, Tree.CompilationUnit cu,
            Collection<ICompletionProposal> proposals, IFile file,
            Node node) {
        Tree.Statement statement = FindUtils.findStatement(cu, node);
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
                String delim = Indents.getDefaultLineDelimiter(doc);
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
        return CorrectionUtil.styleProposal(getDisplayString());
    }
    
}