package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;

public class ChangeVersionRefactoring extends AbstractRefactoring {
    
    private static class FindVersionReferenceVisitor extends Visitor {
        private Module module;
        private final Set<Node> nodes = new HashSet<Node>();
        public Set<Node> getNodes() {
            return nodes;
        }
        private FindVersionReferenceVisitor(Module module) {
            this.module = module;
        }
        @Override
        public void visit(Tree.ImportModule that) {
            super.visit(that);
            Tree.ImportPath ip = that.getImportPath();
            String name = module.getNameAsString();
            Tree.QuotedLiteral version = that.getVersion();
            if (version!=null) {
                if (ip!=null && ip.getModel()!=null &&
                        ip.getModel().getNameAsString()
                        .equals(name)) {
                    nodes.add(version);
                }
                Tree.QuotedLiteral ql = that.getQuotedLiteral();
                if (ql!=null && 
                        ql.getText().equals('"' + name + '"')) {
                    nodes.add(version);
                }
            }
        }
        @Override
        public void visit(Tree.ModuleDescriptor that) {
            super.visit(that);
            Tree.QuotedLiteral version = that.getVersion();
            if (version!=null) {
                Tree.ImportPath ip = that.getImportPath();
                if (ip!=null && ip.getModel()!=null &&
                        ip.getModel().getNameAsString()
                          .equals(module.getNameAsString())) {
                    nodes.add(version);
                }
            }
        }
    }

    private String newVersion;
    private final Module module;
    
    public Node getNode() {
        return node;
    }

    public ChangeVersionRefactoring(IEditorPart editor) {
        super(editor);
        if (rootNode!=null) {
            module = rootNode.getUnit().getPackage().getModule();
            newVersion = module.getVersion();
        }
        else {
            module = null;
        }
    }
    
    @Override
    public boolean getEnabled() {
        return module!=null &&
                project != null;
    }

    public int getCount() {
        return module==null ? 
                0 : countDeclarationOccurrences();
    }
    
    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindVersionReferenceVisitor frv = 
                new FindVersionReferenceVisitor(module);
        cu.visit(frv);
        return frv.getNodes().size();
    }

    public String getName() {
        return "Change Module Version";
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

    public CompositeChange createChange(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        CompositeChange cc = new CompositeChange(getName());
        
        List<PhasedUnit> units = getAllUnits();
        pm.beginTask(getName(), units.size());
        int i=0;
        for (PhasedUnit pu: units) {
            if (searchInFile(pu)) {
                TextFileChange tfc = 
                        newTextFileChange((ProjectPhasedUnit<IProject,IResource,IFolder,IFile>)pu);
                renameInFile(tfc, cc, 
                        pu.getCompilationUnit());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            Tree.CompilationUnit rn = 
                    editor.getParseController().getLastCompilationUnit();
            renameInFile(dc, cc, rn);
            pm.worked(i++);
        }
        pm.done();
        return cc;
    }

    private void renameInFile(TextChange tfc, CompositeChange cc, 
            Tree.CompilationUnit root) {
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
        FindVersionReferenceVisitor frv = 
                new FindVersionReferenceVisitor(module);
        root.visit(frv);
        list.addAll(frv.getNodes());
        return list;
    }
    
    protected void renameNode(TextChange tfc, Node node, 
            Tree.CompilationUnit root) {
        Node identifyingNode = getIdentifyingNode(node);
        int start = identifyingNode.getStartIndex() + 1;
        int length = identifyingNode.getText().length() - 2;
        tfc.addEdit(new ReplaceEdit(start, length, 
                newVersion));
    }

    public void setNewVersion(String text) {
        newVersion = text;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public Module getModule() {
        return module;
    }
    
    @Override
    protected boolean visibleOutsideUnit() {
        return true;
    }
}
