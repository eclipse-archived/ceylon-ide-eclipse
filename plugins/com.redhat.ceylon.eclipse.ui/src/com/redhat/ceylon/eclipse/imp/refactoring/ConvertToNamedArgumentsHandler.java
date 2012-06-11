package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class ConvertToNamedArgumentsHandler extends AbstractHandler {

    private CeylonEditor editor;
    
    public ConvertToNamedArgumentsHandler() {
        editor = (CeylonEditor) getCurrentEditor();
    }
    
    public ConvertToNamedArgumentsHandler(CeylonEditor editor) {
        this.editor = editor;
    }
    
    //TODO: copy/pasted from AbstractFindAction
    private static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        return cpc.getRootNode()==null ? null : 
            findNode(cpc.getRootNode(), 
                (ITextSelection) editor.getSelectionProvider().getSelection());
    }

    //TODO: copy/pasted from RefineFormalMembersHandler
    @Override
    public boolean isEnabled() {
        if (super.isEnabled() && 
                editor.getEditorInput() instanceof IFileEditorInput) {
            Node node = getSelectedNode((CeylonEditor) editor);
            return node instanceof Tree.PositionalArgumentList;
        }
        else {
            return false;
        }
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        CeylonParseController cpc = editor.getParseController();
        Tree.CompilationUnit cu = cpc.getRootNode();
        if (cu==null) return null;
        Node node = getSelectedNode(editor);
        if (node instanceof Tree.PositionalArgumentList) {
            IDocument document = editor.getDocumentProvider()
                    .getDocument(editor.getEditorInput());
            final TextChange tc;
            if (editor.isDirty()) {
                tc = new DocumentChange("Convert To Named Arguments", document);
            }
            else {
                tc = new TextFileChange("Convert To Named Arguments", 
                        Util.getFile(editor.getEditorInput()));
            }
            tc.setEdit(new MultiTextEdit());
    		Tree.PositionalArgumentList argList = (Tree.PositionalArgumentList) node;
    		Integer start = node.getStartIndex();
    		int length = node.getStopIndex()-start+1;
    		StringBuilder result = new StringBuilder().append(" {");
    		boolean sequencedArgs = false;
    		for (Tree.PositionalArgument arg: argList.getPositionalArguments()) {
    			if (arg.getParameter().isSequenced() && argList.getEllipsis()==null) {
    				if (sequencedArgs) result.append(",");
    				sequencedArgs=true;
    				result.append(" " + AbstractRefactoring.toString(arg.getExpression().getTerm(), cpc.getTokens()));
    			}
    			else {
    				result.append(" " + arg.getParameter().getName() + "=" + 
    				        AbstractRefactoring.toString(arg.getExpression().getTerm(), cpc.getTokens()) + ";");
    			}
    		}
    		result.append(" }");
    		tc.addEdit(new ReplaceEdit(start, length, result.toString()));
            tc.initializeValidationData(null);
            try {
                ResourcesPlugin.getWorkspace().run(new PerformChangeOperation(tc), 
                        new NullProgressMonitor());
            }
            catch (CoreException ce) {
                throw new ExecutionException("Error cleaning imports", ce);
            }
        }
        return null;
    }
	
}
