package org.eclipse.ceylon.ide.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import org.eclipse.ceylon.model.typechecker.model.Interface;
import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

public class AddSpreadToVariadicParameterProposal extends CorrectionProposal {
    
    @Deprecated
    public static void addSpreadToSequenceParameterProposal(CompilationUnit cu, 
            Node node, Collection<ICompletionProposal> proposals, IFile file) {
        if( !(node instanceof Tree.Term) ) {
            return;
        }
        
        Tree.Term term = (Tree.Term) node;
        Type type = term.getTypeModel();
        Interface id = type.getDeclaration().getUnit().getIterableDeclaration();
        if( type.getSupertype(id) == null ) {
            return;
        }
        
        FindInvocationVisitor fiv = new FindInvocationVisitor(term);
        fiv.visit(cu);
        if (fiv.parameter == null || 
            !(fiv.parameter.isParameter()) ||
            !((FunctionOrValue) fiv.parameter).getInitializerParameter().isSequenced()) {
            return;
        }
        
        TextFileChange change = 
                new TextFileChange("Spread iterable argument of variadic parameter", file);
        change.setEdit(new InsertEdit(term.getStartIndex(), "*"));
        AddSpreadToVariadicParameterProposal p = 
                new AddSpreadToVariadicParameterProposal(fiv.parameter, 
                        term.getEndIndex() + 3, change);
        if ( !proposals.contains(p)) {
            proposals.add(p);
        }                               
    }

    private final TypedDeclaration parameter;
    
    @Deprecated
    private AddSpreadToVariadicParameterProposal(TypedDeclaration parameter, 
            int offset, TextFileChange change) {
        super("Spread iterable argument of variadic parameter", change,
                new Region(offset, 0));
        this.parameter = parameter;
    }

    AddSpreadToVariadicParameterProposal(TypedDeclaration parameter, 
            String desc, int offset, TextChange change) {
        super(desc, change, new Region(offset, 0));
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddSpreadToVariadicParameterProposal) {
            AddSpreadToVariadicParameterProposal that = 
                    (AddSpreadToVariadicParameterProposal) obj;
            return that.parameter.equals(parameter);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return parameter.hashCode();
    }

}
