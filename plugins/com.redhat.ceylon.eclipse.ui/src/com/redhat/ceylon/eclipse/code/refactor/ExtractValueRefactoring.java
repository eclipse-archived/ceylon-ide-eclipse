package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.Nodes.findStatement;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class ExtractValueRefactoring extends AbstractRefactoring implements ExtractLinkedModeEnabled {
    
    private final class FindAnonFunctionVisitor extends Visitor {
        private final Statement statement;
        Tree.FunctionArgument result;

        private FindAnonFunctionVisitor(Statement statement) {
            this.statement = statement;
        }

        @Override
        public void visit(Tree.FunctionArgument that) {
            if (that!=node &&
                    that.getStartIndex()<=node.getStartIndex() &&
                    that.getEndIndex()>=node.getEndIndex() &&
                    that.getStartIndex()>statement.getStartIndex()) {
                result = that;
            }
            super.visit(that);
        }
    }

    private String newName;
    private boolean explicitType;
    private boolean getter;
    private Type type;
    private boolean canBeInferred;
    
    public ExtractValueRefactoring(IEditorPart editor) {
        super(editor);
        newName = Nodes.nameProposals(node)[0];
    }
    
    @Override
    public boolean getEnabled() {
        return sourceFile!=null &&
                getEditable() &&
                !sourceFile.getName()
                    .equals("module.ceylon") &&
                !sourceFile.getName()
                    .equals("package.ceylon") &&
                node instanceof Tree.Term;
    }

    public String getName() {
        return "Extract Value";
    }

    public boolean forceWizardMode() {
        Declaration existing = 
                node.getScope()
                    .getMemberOrParameter(node.getUnit(), 
                            newName, null, false);
        return existing!=null;
    }
    
    public RefactoringStatus checkInitialConditions
            (IProgressMonitor pm)
                    throws CoreException, 
                           OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions
            (IProgressMonitor pm)
                    throws CoreException, 
                           OperationCanceledException {
        Declaration existing = 
                node.getScope()
                    .getMemberOrParameter(node.getUnit(), 
                            newName, null, false);
        if (null!=existing) {
            return createWarningStatus(
                    "An existing declaration named '" +
                    newName + 
                    "' already exists in the same scope");
        }
        return new RefactoringStatus();
    }

    public TextChange createChange(IProgressMonitor pm) 
    		throws CoreException, OperationCanceledException {
        TextChange tfc = newLocalChange();
        extractInFile(tfc);
        return tfc;
    }
    
    private IRegion typeRegion;
    public IRegion getTypeRegion() {
        return typeRegion;
    }
    public void setTypeRegion(IRegion typeRegion) {
        this.typeRegion = typeRegion;
    }

    private IRegion decRegion;
    public IRegion getDecRegion() {
        return decRegion;
    }
    public void setDecRegion(IRegion decRegion) {
        this.decRegion = decRegion;
    }

    private IRegion refRegion;
    public IRegion getRefRegion() {
        return refRegion;
    }
    public void setRefRegion(IRegion refRegion) {
        this.refRegion = refRegion;
    }


    public void extractInFile(TextChange tfc) {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(tfc);
        Unit unit = node.getUnit();
        Tree.Term term = (Tree.Term) node;
        final Tree.Statement statement = 
                findStatement(rootNode, node);
        int start = statement.getStartIndex();
        int il = 0;
        String newLineOrReturn = 
                getDefaultLineDelimiter(doc) + 
                getIndent(statement, doc);
        FindAnonFunctionVisitor visitor = 
                new FindAnonFunctionVisitor(statement);
        visitor.visit(statement);
        Tree.FunctionArgument anon = visitor.result;
        boolean toplevel;
        if (anon!=null && anon.getBlock()==null) {
            Tree.Expression ex = anon.getExpression();
            if (ex!=null) {
                List<Tree.ParameterList> pls = 
                        anon.getParameterLists();
                Node pl = 
                        pls.get(pls.size()-1);
                if (anon.getTypeConstraintList()!=null) {
                    pl = anon.getTypeConstraintList();
                }
                start = ex.getStartIndex();
                int loc = pl.getEndIndex();
                int len = ex.getStartIndex() - loc;
                int end = ex.getEndIndex();
                tfc.addEdit(new ReplaceEdit(loc, len, " { "));
                tfc.addEdit(new InsertEdit(end, "; }"));
                il-=len-3;
                if (anon.getDeclarationModel().isDeclaredVoid()) {
                    newLineOrReturn = " ";
                }
                else {
                    newLineOrReturn = " return ";
                }
                toplevel = false;
            }
            else {
                return;
            }
        }
        else if (statement instanceof Tree.Declaration) {
            Tree.Declaration dec = 
                    (Tree.Declaration) statement;
            if (dec instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration md = 
                        (Tree.MethodDeclaration) dec;
                Tree.SpecifierExpression se = 
                        md.getSpecifierExpression();
                if (se!=null) {
                    Tree.Expression ex = se.getExpression();
                    if (ex!=null) {
                        List<Tree.ParameterList> pls = 
                                md.getParameterLists();
                        Node pl = pls.get(pls.size()-1);
                        if (md.getTypeConstraintList()!=null) {
                            pl = md.getTypeConstraintList();
                        }
                        start = ex.getStartIndex();
                        int loc = pl.getEndIndex();
                        int len = ex.getStartIndex() - loc;
                        int end = ex.getEndIndex();
                        int semi = dec.getEndIndex()-1;
                        String indent = getDefaultIndent();
                        String starting = " {" + newLineOrReturn + indent;
                        String ending = ";" + newLineOrReturn + "}";
                        tfc.addEdit(new ReplaceEdit(loc, len, starting));
                        tfc.addEdit(new InsertEdit(end, ending));
                        tfc.addEdit(new DeleteEdit(semi, 1));
                        il-=len-starting.length();
                        newLineOrReturn = newLineOrReturn + indent;
                        if (!md.getDeclarationModel().isDeclaredVoid()) {
                            newLineOrReturn += "return ";
                        }
                    }
                }
                toplevel = false;
            }
            else {
                toplevel = dec.getDeclarationModel().isToplevel();
            }
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
                    (Tree.FunctionArgument) 
                        unparened;
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
        String typeDec;
        if (type==null || type.isUnknown()) {
            typeDec = "dynamic";
        }
        else if (explicitType || toplevel) {
            typeDec = type.asSourceCodeString(unit);
            HashSet<Declaration> decs = 
                    new HashSet<Declaration>();
            importType(decs, type, rootNode);
            il += applyImports(tfc, decs, rootNode, doc);
        }
        else {
            canBeInferred = true;
            typeDec = mod;
        }
        String dec = 
        		typeDec + " " +  newName + 
        		(anonFunction ? "" : (getter ? " => "  : " = ")) + 
        		exp;
        
        String text = dec + newLineOrReturn;
        int tlength = typeDec.length();
        int nstart = node.getStartIndex();
        int nlength = node.getDistance();
        tfc.addEdit(new InsertEdit(start, text));
        tfc.addEdit(new ReplaceEdit(nstart, nlength, newName));
        typeRegion = new Region(start+il, tlength);
        int len = newName.length();
        decRegion = new Region(start+il+tlength+1, len);
        refRegion = new Region(nstart+il+text.length(), len);
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
    
    Type getType() {
        return type;
    }
    
	public String[] getNameProposals() {
		return Nodes.nameProposals(node);
	}
	
	public boolean isFunction() {
		return node instanceof Tree.FunctionArgument;
	}
    
}
