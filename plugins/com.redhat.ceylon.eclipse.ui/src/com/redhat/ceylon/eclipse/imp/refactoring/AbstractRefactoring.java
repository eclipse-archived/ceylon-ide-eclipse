package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.imageRegistry;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.getTokenIterator;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_MOVE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_RENAME;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_DELETE_IMPORT;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public abstract class AbstractRefactoring extends Refactoring {
    
    public static ImageDescriptor MOVE = imageRegistry.getDescriptor(CEYLON_MOVE);
    public static ImageDescriptor RENAME = imageRegistry.getDescriptor(CEYLON_RENAME);
    public static ImageDescriptor DELETE_IMPORT = imageRegistry.getDescriptor(CEYLON_DELETE_IMPORT);
    
    IProject project;
    IFile sourceFile;
    Node node;
    Tree.CompilationUnit rootNode;
    List<CommonToken> tokens;
    IDocument document;
    CeylonEditor editor;
   
    /*public AbstractRefactoring(IQuickFixInvocationContext context) {
        sourceFile = context.getModel().getFile();
        project = sourceFile.getProject();
        PhasedUnit pu = CeylonBuilder.getPhasedUnit(sourceFile);
        rootNode = pu.getCompilationUnit();
        tokenStream = pu.getTokenStream();
        node = CeylonSourcePositionLocator.findNode(rootNode, context.getOffset(),
                context.getOffset()+context.getLength());
    }*/
    
    public AbstractRefactoring(ITextEditor editor) {
        if (editor instanceof CeylonEditor) {
            project = Util.getProject(editor);
            CeylonParseController cpc = ((CeylonEditor) editor).getParseController();
            tokens = cpc.getTokens();
            rootNode = cpc.getRootNode();
            IEditorInput input = editor.getEditorInput();
            if (rootNode!=null && input instanceof IFileEditorInput) {
                sourceFile = Util.getFile(input);
                node = findNode(rootNode, 
                    (ITextSelection) editor.getSelectionProvider().getSelection());
            }
        }
    }
    
    abstract boolean isEnabled();
    
    String guessName() {
        Node identifyingNode = node;
        if (identifyingNode instanceof Tree.Expression) {
            identifyingNode = ((Tree.Expression) identifyingNode).getTerm();
        }
        if (identifyingNode instanceof Tree.InvocationExpression) {
            identifyingNode = ((Tree.InvocationExpression) identifyingNode).getPrimary();
        }
        if (identifyingNode instanceof Tree.StaticMemberOrTypeExpression) {
            String id = ((Tree.StaticMemberOrTypeExpression) identifyingNode).getIdentifier().getText();
            if (!id.isEmpty()) {
                return Character.toLowerCase(id.charAt(0)) + 
                        id.substring(1);
            }
        }
        return "temp";
    }

    String toString(Node term) {
        return toString(term, tokens);
    }
    
    public static String toString(Node term, List<CommonToken> theTokens) {
        Integer start = term.getStartIndex();
        int length = term.getStopIndex()-start+1;
        Region region = new Region(start, length);
        StringBuilder exp = new StringBuilder();
        for (Iterator<CommonToken> ti = getTokenIterator(theTokens, region); 
                ti.hasNext();) {
            exp.append(ti.next().getText());
        }
        return exp.toString();
    }
    
    DocumentChange newDocumentChange() {
        return new DocumentChange(editor.getEditorInput().getName() + 
                " - current editor", document);
    }
    
    TextFileChange newTextFileChange(PhasedUnit pu) {
        return new TextFileChange(getName(), 
                CeylonBuilder.getFile(pu));
    }

    boolean searchInEditor() {
        return editor!=null && editor.isDirty();
    }

    boolean searchInFile(PhasedUnit pu) {
        return editor==null || !editor.isDirty() || 
                !pu.getUnit().equals(editor.getParseController().getRootNode().getUnit());
    }
    
    TextChange newLocalChange() {
        return searchInEditor() ?
                new DocumentChange(getName(), document) :
                new TextFileChange(getName(), sourceFile);
    }

}
