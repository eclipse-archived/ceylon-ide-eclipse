package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;

public class AddSpreadToVariadicParameterProposal extends CorrectionProposal {
    
    public static void addEllipsisToSequenceParameterProposal(CompilationUnit cu, Node node, Collection<ICompletionProposal> proposals, IFile file) {
        if( !(node instanceof Tree.Term) ) {
            return;
        }
        
        Tree.Term term = (Tree.Term) node;
        ProducedType type = term.getTypeModel();
        Interface id = type.getDeclaration().getUnit().getIterableDeclaration();
        if( type.getSupertype(id) == null ) {
            return;
        }
        
        FindInvocationVisitor fiv = new FindInvocationVisitor(term);
        fiv.visit(cu);
        if (fiv.parameter == null || 
            !(fiv.parameter.isParameter()) ||
            !((MethodOrValue) fiv.parameter).getInitializerParameter().isSequenced()) {
            return;
        }
        
        TextFileChange change = new TextFileChange("Spread iterable argument of variadic parameter", file);
        change.setEdit(new InsertEdit(term.getStartIndex(), "*"));
        AddSpreadToVariadicParameterProposal p = new AddSpreadToVariadicParameterProposal(fiv.parameter, term.getStopIndex() + 4, file, change);
        if ( !proposals.contains(p)) {
            proposals.add(p);
        }                               
    }

    private int offset; 
    private IFile file;
    private TypedDeclaration parameter;
    
    private AddSpreadToVariadicParameterProposal(TypedDeclaration parameter, int offset, IFile file, TextFileChange change) {
        super("Spread iterable argument of variadic parameter", change);
        this.file=file;
        this.offset=offset;
        this.parameter = parameter;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        EditorUtil.gotoLocation(file, offset);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddSpreadToVariadicParameterProposal) {
            AddSpreadToVariadicParameterProposal that = (AddSpreadToVariadicParameterProposal) obj;
            return that.parameter.equals(parameter);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return parameter.hashCode();
    }

}
