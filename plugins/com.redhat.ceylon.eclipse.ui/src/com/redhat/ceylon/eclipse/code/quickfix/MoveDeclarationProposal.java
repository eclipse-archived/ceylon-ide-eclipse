package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.editor.Util.getFile;
import static com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler.imports;
import static com.redhat.ceylon.eclipse.code.quickfix.Util.getSelectedNode;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.wizard.NewUnitWizard;
import com.redhat.ceylon.eclipse.util.Indents;

public class MoveDeclarationProposal implements ICompletionProposal {

    private CeylonEditor editor;
    
    public MoveDeclarationProposal(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return CeylonLabelProvider.MOVE;
    }

    @Override
    public String getDisplayString() {
    	return "Move declaration to new unit";
    }

    @Override
    public IContextInformation getContextInformation() {
    	return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
    	return null;
    }

    @Override
    public void apply(IDocument doc) {
        try {
            moveDeclaration(editor);
        } 
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean canMoveDeclaration(CeylonEditor editor) {
	    Node node = getSelectedNode(editor);
	    if (node instanceof Tree.Declaration) {
	    	Declaration d = ((Tree.Declaration) node).getDeclarationModel();
	    	return d!=null && d.isToplevel();
	    }
	    else {
	    	return false;
	    }
	}

	public static void moveDeclaration(CeylonEditor editor) throws ExecutionException {
	    Tree.CompilationUnit cu = editor.getParseController().getRootNode();
	    if (cu==null) return;
	    Node node = getSelectedNode(editor);
	    if (node instanceof Tree.Declaration) {
	        try {
	            IDocument document = editor.getDocumentProvider()
	                    .getDocument(editor.getEditorInput());
	            int start = node.getStartIndex();
	            int length = node.getStopIndex()-start+1;
	            String contents = document.get(start, length);
	            /*final Set<Declaration> decs = new HashSet<Declaration>();
	            node.visit(new Visitor() {
	            	@Override
	            	public void visit(BaseMemberExpression that) {
	            		super.visit(that);
	            		if (that.getDeclaration()!=null) {
	            			decs.add(that.getDeclaration());
	            		}
	            	}
	            	@Override
	            	public void visit(BaseTypeExpression that) {
	            		super.visit(that);
	            		if (that.getDeclaration()!=null) {
	            			decs.add(that.getDeclaration());
	            		}
	            	}
	            	@Override
	            	public void visit(BaseType that) {
	            		super.visit(that);
	            		if (that.getDeclarationModel()!=null) {
	            			decs.add(that.getDeclarationModel());
	            		}
	            	}
	            });*/
	            String imports = imports(node, cu.getImportList(), document);
	            boolean success = NewUnitWizard.open(imports==null ? 
	                        contents : imports + Indents.getDefaultLineDelimiter(document) + contents, 
	                    getFile(editor.getEditorInput()), 
	                    ((Tree.Declaration) node).getIdentifier().getText(), "Move to New Unit", 
	                    "Create a new Ceylon compilation unit containing the selected declaration.");
	            if (success) {
	                final TextChange tc;
	                if (editor.isDirty()) {
	                    tc = new DocumentChange("Move to New Unit", document);
	                }
	                else {
	                    tc = new TextFileChange("Move to New Unit", 
	                            getFile(editor.getEditorInput()));
	                }
	                tc.setEdit(new DeleteEdit(start, length));
	                tc.initializeValidationData(null);
	                AbstractOperation op = new TextChangeOperation(tc);
					IWorkbenchOperationSupport os = getWorkbench().getOperationSupport();
					op.addContext(os.getUndoContext());
		            os.getOperationHistory().execute(op, new NullProgressMonitor(), 
	                        		getUIInfoAdapter(editor.getSite().getShell()));
	            }
	        } 
	        catch (BadLocationException e) {
	            e.printStackTrace();
	        }
	    }
	}

	public static void add(Collection<ICompletionProposal> proposals, CeylonEditor editor) {
    	if (canMoveDeclaration(editor)) {
    		proposals.add(new MoveDeclarationProposal(editor));
    	}
    }

}