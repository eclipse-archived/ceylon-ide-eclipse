package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;
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
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.ProducedType;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Nodes;

public class ExtractValueRefactoring extends AbstractRefactoring {
    
    private String newName;
    private boolean explicitType;
    private boolean getter;
    private ProducedType type;
    private boolean canBeInferred;
    
    public ExtractValueRefactoring(IEditorPart editor) {
        super(editor);
        newName = Nodes.nameProposals(node)[0];
    }
    
    @Override
    public boolean isEnabled() {
        return sourceFile!=null &&
                isEditable() &&
                !sourceFile.getName().equals("module.ceylon") &&
                !sourceFile.getName().equals("package.ceylon") &&
                node instanceof Tree.Term;
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

    public TextChange createChange(IProgressMonitor pm) 
    		throws CoreException, OperationCanceledException {
        TextChange tfc = newLocalChange();
        extractInFile(tfc);
        return tfc;
    }
    
    IRegion typeRegion;
    IRegion decRegion;
    IRegion refRegion;

    void extractInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = tfc.getCurrentDocument(null);
        Unit unit = node.getUnit();
        Tree.Term term = (Tree.Term) node;
        Tree.Statement statement = findStatement(rootNode, node);
        boolean toplevel;
        if (statement instanceof Tree.Declaration) {
            Tree.Declaration d = (Tree.Declaration) statement;
            toplevel = d.getDeclarationModel().isToplevel();
        }
        else {
            toplevel = false;
        }
        type = unit.denotableType(term.getTypeModel());
        Tree.Term unparened = unparenthesize(term);
        String exp;
        boolean anonFunction = 
                unparened instanceof Tree.FunctionArgument;
        String mod;
        if (anonFunction) {
            type = unit.getCallableReturnType(type);
            Tree.FunctionArgument fa = 
                    (Tree.FunctionArgument) unparened;
            StringBuilder sb = new StringBuilder();
            if (fa.getType() instanceof Tree.VoidModifier) {
                mod = "void ";
            }
            else {
                mod = "function";
            }
            Nodes.appendParameters(sb, fa, unit, tokens);
            if (fa.getBlock()!=null) {
                sb.append(" ")
                  .append(toString(fa.getBlock()));
            }
            else if (fa.getExpression()!=null) {
                sb.append(" => ")
                  .append(toString(fa.getExpression()))
                  .append(";");
            }
            else {
                sb.append(" => ");
            }
            exp = sb.toString();
        }
        else {
            mod = "value";
        	exp = toString(unparened) + ";";
        }
        int il;
        String typeDec;
        if (type==null || type.isUnknown()) {
            typeDec = "dynamic";
            il = 0;
        }
        else if (explicitType||toplevel) {
            typeDec = type.getProducedTypeNameInSource(unit);
            HashSet<Declaration> decs = new HashSet<Declaration>();
            importType(decs, type, rootNode);
            il = applyImports(tfc, decs, rootNode, doc);
        }
        else {
            canBeInferred = true;
            typeDec = mod;
            il = 0;
        }
        String dec = 
        		typeDec + " " +  newName + 
        		(anonFunction ? "" : (getter ? " => "  : " = ")) + 
        		exp;
        
        String text = dec + getDefaultLineDelimiter(doc) + 
        		getIndent(statement, doc);
        Integer start = statement.getStartIndex();
        tfc.addEdit(new InsertEdit(start, text));
        tfc.addEdit(new ReplaceEdit(Nodes.getNodeStartOffset(node), 
        		Nodes.getNodeLength(node), newName));
        typeRegion = new Region(start+il, typeDec.length());
        decRegion = new Region(start+il+typeDec.length()+1, newName.length());
        refRegion = new Region(Nodes.getNodeStartOffset(node)+il+text.length(), 
        		newName.length());
    }
    
    public boolean canBeInferred() {
        return canBeInferred;
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

    ProducedType getType() {
        return type;
    }
    
	public String[] getNameProposals() {
		return Nodes.nameProposals(node);
	}
	
	public boolean isFunction() {
		return node instanceof Tree.FunctionArgument;
	}
    
}
