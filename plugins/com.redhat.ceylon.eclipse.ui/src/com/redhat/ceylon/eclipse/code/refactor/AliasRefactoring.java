package com.redhat.ceylon.eclipse.code.refactor;

import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createErrorStatus;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.model.typechecker.model.Type;

public class AliasRefactoring extends AbstractRefactoring {
    
    private static class FindAliasedTypeVisitor 
            extends Visitor {
        private Type type;
        private List<Tree.Type> nodes = 
                new ArrayList<Tree.Type>();

        private FindAliasedTypeVisitor(Type type) {
            this.type = type;
        }
        
        public List<Tree.Type> getNodes() {
            return nodes;
        }
        @Override
        public void visit(Tree.Type that) {
            super.visit(that);
            Type t = that.getTypeModel();
            if (t!=null && type.isExactly(t)) {
                nodes.add(that);
            }
        }
    }

    private String newName;
    private final Type type;
//    private boolean renameValuesAndFunctions;
    
    public Node getNode() {
        return node;
    }

    public AliasRefactoring(IEditorPart editor) {
        super(editor);
        if (rootNode!=null) {
            if (node instanceof Tree.Type) {
                type = ((Tree.Type) node).getTypeModel();
                newName = "Alias"; //TODO but what?
            }
            else {
                type = null;
            }
        }
        else {
            type = null;
        }
    }
    
    @Override
    public boolean isEnabled() {
        return type!=null &&
                project != null;
    }

    public int getCount() {
        return type==null ? 
                0 : countDeclarationOccurrences();
    }
    
    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindAliasedTypeVisitor frv = 
                new FindAliasedTypeVisitor(type);
        cu.visit(frv);
        return frv.getNodes().size();
    }

    public String getName() {
        return "Rename";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        if (!newName.matches("^[a-zA-Z_]\\w*$")) {
            return createErrorStatus("Not a legal Ceylon identifier");
        }
        else if (Escaping.KEYWORDS.contains(newName)) {
            return createErrorStatus("'" + newName + "' is a Ceylon keyword");
        }
        else {
            int ch = newName.codePointAt(0);
            if (!Character.isUpperCase(ch)) {
                return createErrorStatus("Not an initial uppercase identifier");
            }
        }
        /*Declaration existing = declaration.getContainer()
                        .getMemberOrParameter(declaration.getUnit(), 
                                newName, null, false);
        if (null!=existing && !existing.equals(declaration)) {
            return createWarningStatus("An existing declaration named '" +
                newName + "' already exists in the same scope");
        }*/
        return new RefactoringStatus();
    }

    public CompositeChange createChange(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        List<PhasedUnit> units = getAllUnits();
        pm.beginTask(getName(), units.size());
        CompositeChange cc = new CompositeChange(getName());
        int i=0;
        for (PhasedUnit pu: units) {
            if (searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange(pu);
                renameInFile(tfc, cc, pu.getCompilationUnit());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            renameInFile(dc, cc, editor.getParseController().getRootNode());
            pm.worked(i++);
        }
        
        pm.done();
        return cc;
    }
    
    private void renameInFile(TextChange tfc, CompositeChange cc, 
            Tree.CompilationUnit root) {
        tfc.setEdit(new MultiTextEdit());
        if (type!=null) {
            for (Tree.Type node: getNodesToRename(root)) {
                renameNode(tfc, node, root);
            }
//            if (renameValuesAndFunctions) { 
//                for (Tree.Identifier id: getIdentifiersToRename(root)) {
//                    renameIdentifier(tfc, id, root);
//                }
//            }
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }
    
    public List<Tree.Type> getNodesToRename(Tree.CompilationUnit root) {
        FindAliasedTypeVisitor frv = 
                new FindAliasedTypeVisitor(type);
        root.visit(frv);
        return frv.getNodes();
    }
    
    protected void renameNode(TextChange tfc, Tree.Type node, 
            Tree.CompilationUnit root) {
        tfc.addEdit(new ReplaceEdit(node.getStartIndex(), 
                node.getStopIndex()-node.getStartIndex()+1, 
                newName));
    }
    
    /*public boolean isRenameValuesAndFunctions() {
        return renameValuesAndFunctions;
    }
    
    public void setRenameValuesAndFunctions(boolean renameLocals) {
        this.renameValuesAndFunctions = renameLocals;
    }*/
    
    public void setNewName(String text) {
        newName = text;
    }
    
    public Type getType() {
        return type;
    }

    public String getNewName() {
        return newName;
    }

}
