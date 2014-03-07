package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.imageRegistry;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIterator;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_CHANGE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_COMPOSITE_CHANGE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_MOVE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.EditedSourceFile;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;

public abstract class AbstractRefactoring extends Refactoring {
    
    public static ImageDescriptor CHANGE = imageRegistry.getDescriptor(CEYLON_CHANGE);
    public static ImageDescriptor COMP_CHANGE = imageRegistry.getDescriptor(CEYLON_COMPOSITE_CHANGE);
    public static ImageDescriptor MOVE = imageRegistry.getDescriptor(CEYLON_MOVE);
    
    final IProject project;
    final IFile sourceFile;
    final List<CommonToken> tokens;
    final IDocument document;
    final CeylonEditor editor;
    final Tree.CompilationUnit rootNode;
    Node node;
   
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
            CeylonEditor ce = (CeylonEditor) editor;
            this.editor = ce;
            document = ce.getDocumentProvider().getDocument(editor.getEditorInput());
            project = EditorUtil.getProject(editor);
            CeylonParseController cpc = ce.getParseController();
            tokens = cpc.getTokens();
            rootNode = cpc.getRootNode();
            IEditorInput input = editor.getEditorInput();
            if (rootNode!=null && input instanceof IFileEditorInput) {
                sourceFile = EditorUtil.getFile(input);
                node = findNode(rootNode, getSelection(editor));
            }
            else {
                sourceFile = null;
                node = null;
            }
        }
        else {
            this.editor = null;
            document = null;
            tokens = null;
            rootNode = null;
            sourceFile = null;
            node = null;
            project = null;
        }
    }
    
    abstract boolean isEnabled();
    
    boolean inSameProject(Declaration declaration) {
        return declaration.getUnit() instanceof EditedSourceFile &&
        project.equals(((EditedSourceFile)declaration.getUnit()).getProjectResource()) ||
        declaration.getUnit() instanceof ProjectSourceFile &&
                project.equals(((ProjectSourceFile)declaration.getUnit()).getProjectResource());
    }
    
    public static String guessName(Node node) {
        Node identifyingNode = node;
        if (identifyingNode instanceof Tree.Expression) {
            identifyingNode = ((Tree.Expression) identifyingNode).getTerm();
        }
        if (identifyingNode instanceof Tree.InvocationExpression) {
            identifyingNode = ((Tree.InvocationExpression) identifyingNode)
                    .getPrimary();
        }
        
        if (node instanceof Tree.MemberOrTypeExpression) {
            Declaration d = ((Tree.MemberOrTypeExpression) node).getDeclaration();
            if (d!=null) {
                return guessName(identifyingNode, d);
            }
        }
        else if (node instanceof Tree.Term) {
            ProducedType type = ((Tree.Term) node).getTypeModel();
            if (type!=null) {
                TypeDeclaration d = type.getDeclaration();
                if (d instanceof ClassOrInterface || 
                    d instanceof TypeParameter) {
                    return guessName(identifyingNode, d);
                }
            }
        }
        
        return "it";
    }

    private static String guessName(Node identifyingNode, Declaration d) {
        String tn = d.getName();
        String name = Character.toLowerCase(tn.charAt(0)) + tn.substring(1);
        if (identifyingNode instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = (Tree.BaseMemberExpression) identifyingNode;
            String id = bme.getIdentifier().getText();
            if (name.equals(id)) {
                return name + "2";
            }
        }
        if (!CeylonTokenColorer.keywords.contains(name)) {
            return name;
        }
        else {
            return "it";
        }
    }

    String toString(Node term) {
        return toString(term, tokens);
    }
    
    Tree.Term unparenthesize(Tree.Term term) {
        if (term instanceof Tree.Expression) {
            Expression e = (Tree.Expression) term;
            if (!(e.getTerm() instanceof Tree.Tuple)) {
                return unparenthesize(e.getTerm());
            }
        }
        return term;
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
        DocumentChange dc = new DocumentChange(editor.getEditorInput().getName() + 
                " - current editor", document);
        dc.setTextType("ceylon");
        return dc;
    }
    
    TextFileChange newTextFileChange(PhasedUnit pu) {
        TextFileChange tfc = new TextFileChange(getName(), 
                CeylonBuilder.getFile(pu));
        tfc.setTextType("ceylon");
        return tfc;
    }

    protected boolean searchInEditor() {
        return editor!=null && editor.isDirty();
    }

    boolean searchInFile(PhasedUnit pu) {
        return editor==null || !editor.isDirty() || 
                !pu.getUnit().equals(editor.getParseController().getRootNode().getUnit());
    }
    
    TextChange newLocalChange() {
        TextChange tc = searchInEditor() ?
                new DocumentChange(getName(), document) :
                new TextFileChange(getName(), sourceFile);
        tc.setTextType("ceylon");
        return tc;
    }

    protected List<PhasedUnit> getAllUnits() {
        List<PhasedUnit> units = new ArrayList<PhasedUnit>();
        units.addAll(getUnits(project));
        for (IProject p: project.getReferencingProjects()) {
            units.addAll(getUnits(p));
        }
        return units;
    }

    protected int countDeclarationOccurrences() {
        int count = 0;
        for (PhasedUnit pu: getAllUnits()) {
            if (searchInFile(pu)) {
                count += countReferences(pu.getCompilationUnit());
            }
        }
        if (searchInEditor()) {
            count += countReferences(rootNode);
        }
        return count;
    }
    
    int countReferences(Tree.CompilationUnit cu) {
        return 0;
    }

}
