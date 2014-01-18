package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModule;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class RenameVersionRefactoring extends AbstractRefactoring {
    
    private static class FindReferencesVisitor extends Visitor {
    	
    	private Module module;
    	private final Set<Node> nodes = new HashSet<Node>();
    	
    	public Set<Node> getNodes() {
	        return nodes;
        }
    	
        private FindReferencesVisitor(Module module) {
            this.module = module;
        }
        @Override
        public void visit(ImportModule that) {
            super.visit(that);
            if (that.getImportPath().getModel().getNameAsString()
            		.equals(module.getNameAsString())) {
            	nodes.add(that.getVersion());
            }
        }
    }

	private String newName;
	private final Module module;
	
	public Node getNode() {
		return node;
	}

	public RenameVersionRefactoring(ITextEditor editor) {
	    super(editor);
	    if (rootNode!=null) {
	    	module = rootNode.getUnit().getPackage().getModule();
	    	newName = module.getVersion();
	    }
	    else {
    		module = null;
	    }
	}
	
	@Override
	public boolean isEnabled() {
	    return module!=null &&
                project != null;
	}

	public int getCount() {
	    return module==null ? 0 : countDeclarationOccurrences();
	}
	
	@Override
	int countReferences(Tree.CompilationUnit cu) {
        FindReferencesVisitor frv = new FindReferencesVisitor(module);
        cu.visit(frv);
        return frv.getNodes().size();
	}

	public String getName() {
		return "Rename Version";
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		// Check parameters retrieved from editor context
		return new RefactoringStatus();
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	public CompositeChange createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
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

    private void renameInFile(TextChange tfc, CompositeChange cc, Tree.CompilationUnit root) {
        tfc.setEdit(new MultiTextEdit());
        if (module!=null) {
        	for (Node node: getNodesToRename(root)) {
                renameNode(tfc, node, root);
        	}
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }
    
    public List<Node> getNodesToRename(Tree.CompilationUnit root) {
    	ArrayList<Node> list = new ArrayList<Node>();
    	FindReferencesVisitor frv = new FindReferencesVisitor(module);
    	root.visit(frv);
    	list.addAll(frv.getNodes());
    	return list;
    }
    
	protected void renameNode(TextChange tfc, Node node, Tree.CompilationUnit root) {
	    Node identifyingNode = getIdentifyingNode(node);
		tfc.addEdit(new ReplaceEdit(identifyingNode.getStartIndex(), 
		        identifyingNode.getText().length(), newName));
	}

	public void setNewName(String text) {
		newName = text;
	}

    public String getNewName() {
        return newName;
    }
}
