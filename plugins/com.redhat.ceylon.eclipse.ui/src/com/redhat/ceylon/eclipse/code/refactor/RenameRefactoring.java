package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedExplicitDeclaration;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.StringLiteral;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class RenameRefactoring extends AbstractRefactoring {
    
	private static class FindReferencesVisitor extends FindReferenceVisitor {
        private FindReferencesVisitor(Declaration declaration) {
            super(declaration);
        }
        @Override
        protected boolean isReference(Declaration ref) {
            return super.isReference(ref) ||
                    ref!=null && ref.refines(getDeclaration());
        }
        @Override
        protected boolean isReference(Declaration ref, String id) {
            return isReference(ref) && id!=null &&
                    getDeclaration().getName().equals(id); //TODO: really lame way to tell if its an alias!
        }
    }

	private String newName;
	private final Declaration declaration;
	
	public Node getNode() {
		return node;
	}

	public RenameRefactoring(ITextEditor editor) {
	    super(editor);
	    if (rootNode!=null) {
	    	Declaration refDec = getReferencedExplicitDeclaration(node, rootNode);
	    	if (refDec!=null) {
	    		declaration = refDec.getRefinedDeclaration();
	    		newName = declaration.getName();
	    	}
	    	else {
	    		declaration = null;
	    	}
	    }
	    else {
    		declaration = null;
	    }
	}
	
	@Override
	public boolean isEnabled() {
	    return declaration!=null &&
                project != null &&
                inSameProject(declaration);
	}

	public int getCount() {
	    return declaration==null ? 0 : countDeclarationOccurrences();
	}

	@Override
	int countReferences(Tree.CompilationUnit cu) {
        FindReferencesVisitor frv = new FindReferencesVisitor(declaration);
        FindRefinementsVisitor fdv = new FindRefinementsVisitor(frv.getDeclaration());
        cu.visit(frv);
        cu.visit(fdv);
        return frv.getNodes().size() + fdv.getDeclarationNodes().size();
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
	    Declaration existing = declaration.getContainer()
                        .getMemberOrParameter(declaration.getUnit(), newName, null, false);
        if (null!=existing && !existing.equals(declaration)) {
	        return createWarningStatus("An existing declaration named '" +
	            newName + "' already exists in the same scope");
	    }
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
        if (declaration!=null) {
        	for (Node node: getNodesToRename(root)) {
                renameNode(tfc, node, root);
        	}
        	for (Region region: getStringsToReplace(root)) {
        	    renameRegion(tfc, region, root);
        	}
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }
    
    public List<Node> getNodesToRename(Tree.CompilationUnit root) {
    	ArrayList<Node> list = new ArrayList<Node>();
    	FindReferencesVisitor frv = new FindReferencesVisitor(declaration);
    	root.visit(frv);
    	list.addAll(frv.getNodes());
    	FindRefinementsVisitor fdv = new FindRefinementsVisitor(frv.getDeclaration());
    	root.visit(fdv);
    	list.addAll(fdv.getDeclarationNodes());
    	return list;
    }
    
    public List<Region> getStringsToReplace(Tree.CompilationUnit root) {
        final Pattern wikiRef = Pattern.compile("\\[\\[(\\w+)(\\.(\\w+))?\\]\\]");
        final List<Region> result = new ArrayList<Region>();
        new Visitor() {
            @Override
            public void visit(StringLiteral that) {
                super.visit(that);
                Matcher m = wikiRef.matcher(that.getToken().getText());
                while (m.find()) {
                    String group1 = m.group(1);
                    Declaration base = that.getScope().getMemberOrParameter(that.getUnit(), 
                            group1, null, false);
                    if (base!=null && base.equals(declaration)) {
                        result.add(new Region(that.getStartIndex()+m.start(1), group1.length()));
                    }
                    String group3 = m.group(3);
                    if (base instanceof TypeDeclaration && group3!=null) {
                        Declaration qualified = ((TypeDeclaration) base).getMember(group3, null, false);
                        if (qualified!=null && qualified.equals(declaration)) {
                            result.add(new Region(that.getStartIndex()+m.start(3), group3.length()));
                        }
                    }
                }
            }
        }.visit(root);
        return result;
    }

    protected void renameRegion(TextChange tfc, Region region, Tree.CompilationUnit root) {
        tfc.addEdit(new ReplaceEdit(region.getOffset(), 
                region.getLength(), newName));
    }

	protected void renameNode(TextChange tfc, Node node, Tree.CompilationUnit root) {
	    Node identifyingNode = getIdentifyingNode(node);
		tfc.addEdit(new ReplaceEdit(identifyingNode.getStartIndex(), 
		        identifyingNode.getText().length(), newName));
	}

	public void setNewName(String text) {
		newName = text;
	}
	
	public Declaration getDeclaration() {
		return declaration;
	}
}
