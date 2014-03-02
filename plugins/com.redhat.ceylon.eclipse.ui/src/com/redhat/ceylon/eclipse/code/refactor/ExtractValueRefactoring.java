package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;
import static com.redhat.ceylon.eclipse.util.FindUtils.findStatement;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class ExtractValueRefactoring extends AbstractRefactoring {
    
    private String newName;
    private boolean explicitType;
    private boolean getter;

    public ExtractValueRefactoring(ITextEditor editor) {
        super(editor);
        newName = guessName(node);
    }
    
    @Override
    public boolean isEnabled() {
        return node instanceof Tree.Term;
    }

    public String getName() {
        return "Extract Value";
    }

    public boolean forceWizardMode() {
        Declaration existing = node.getScope()
                .getMemberOrParameter(node.getUnit(), newName, null, false);
        return existing!=null;
    }
    
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        Declaration existing = node.getScope()
                .getMemberOrParameter(node.getUnit(), newName, null, false);
        if (null!=existing) {
            return createWarningStatus("An existing declaration named '" +
                    newName + "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    public TextChange createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        TextChange tfc = newLocalChange();
        extractInFile(tfc);
        return tfc;
    }
    
    IRegion decRegion;
    IRegion refRegion;

    void extractInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = tfc.getCurrentDocument(null);
        Tree.Term term = (Tree.Term) node;
        Tree.Statement statement = findStatement(rootNode, node);
        String exp = toString(unparenthesize(term));
        String typeDec;
        ProducedType tm = term.getTypeModel();
        int il;
        if (tm==null || tm.isUnknown()) {
            typeDec = "dynamic";
            il = 0;
        }
        else if (explicitType) {
            ProducedType type = node.getUnit().denotableType(tm);
            typeDec = type.getProducedTypeName();
            HashSet<Declaration> decs = new HashSet<Declaration>();
            importType(decs, type, rootNode);
            il = applyImports(tfc, decs, rootNode, doc);
        }
        else {
             typeDec = "value";
             il = 0;
        }
        String dec = typeDec + " " +  newName + 
                (getter ? " { return " + exp  + "; } " : " = " + exp + ";");
        String text = dec + getDefaultLineDelimiter(doc) + getIndent(statement, doc);
        Integer start = statement.getStartIndex();
        tfc.addEdit(new InsertEdit(start, text));
        tfc.addEdit(new ReplaceEdit(getStartOffset(node), getLength(node), newName));
        decRegion = new Region(start+typeDec.length()+1, newName.length());
        refRegion = new Region(getStartOffset(node)+text.length()+il, newName.length());
    }

    public void setNewName(String text) {
        newName = text;
    }
    
    public String getNewName() {
        return newName;
    }
    
    public void setExplicitType() {
        this.explicitType = !explicitType;
    }
    
    public void setGetter() {
        this.getter = !getter;
    }

}
