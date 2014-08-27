package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.Nodes;

public class ExtractParameterRefactoring extends AbstractRefactoring {
    
    private String newName;
    private Tree.Declaration methodOrClass;
    private ProducedType type;

    private static class FindFunctionVisitor 
            extends Visitor 
            implements NaturalVisitor {
        
        private final Node term;
        private Tree.Declaration declaration;
        private Tree.Declaration current;
        
        public FindFunctionVisitor(Node term) {
            this.term = term;
        }
        
        public Tree.Declaration getDefinitionNode() {
            return declaration;
        }
        
        @Override
        public void visit(Tree.MethodDefinition that) {
            Tree.Declaration outer = current;
            current = that;
            super.visit(that);
            current = outer;
        }
        
        @Override
        public void visit(Tree.ClassDefinition that) {
            Tree.Declaration outer = current;
            current = that;
            super.visit(that);
            current = outer;
        }
        
        @Override
        public void visitAny(Node node) {
            if (node == term) {
                declaration = current;
            }
            if (declaration == null) {
                super.visitAny(node);
            }
        }
        
    }
        
    public ExtractParameterRefactoring(IEditorPart editor) {
        super(editor);
        newName = Nodes.nameProposals(node)[0];
        FindFunctionVisitor ffv = new FindFunctionVisitor(node);
        ffv.visit(rootNode);
        methodOrClass = ffv.getDefinitionNode();
    }
    
    
    @Override
    public boolean isEnabled() {
        return node instanceof Tree.Term && 
                methodOrClass!=null &&
                methodOrClass.getDeclarationModel()!=null &&
                !methodOrClass.getDeclarationModel().isActual() &&
                !isWithinParameterList();
    }

    private boolean isWithinParameterList() {
        Tree.ParameterList pl1, pl2;
        if (methodOrClass instanceof Tree.AnyClass) {
            pl1 = pl2 = ((Tree.AnyClass) methodOrClass).getParameterList();
        }
        else if (methodOrClass instanceof Tree.AnyMethod) {
            List<Tree.ParameterList> pls = 
                    ((Tree.AnyMethod) methodOrClass).getParameterLists();
            pl1 = pls.get(0);
            pl2 = pls.get(pls.size()-1);
        }
        else {
            return false;
        }
        return node.getStartIndex()>=pl1.getStartIndex() && 
                node.getStopIndex()<=pl2.getStopIndex();
    }
    
    public String getName() {
        return "Extract Parameter";
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

    public Change createChange(IProgressMonitor pm) 
            throws CoreException,
                   OperationCanceledException {
        TextChange tfc = newLocalChange();
        extractInFile(tfc);
        return tfc;
    }

    IRegion decRegion;
    IRegion refRegion;
    IRegion typeRegion;

    private boolean isParameterOfMethodOrClass(Declaration d) {
        return d.isParameter()&&d.getContainer().equals(methodOrClass.getDeclarationModel());
    }
    void extractInFile(TextChange tfc) 
            throws CoreException {
        tfc.setEdit(new MultiTextEdit());
        IDocument doc = tfc.getCurrentDocument(null);
        
        Tree.ParameterList pl;
        if (methodOrClass instanceof Tree.MethodDefinition) {
            List<Tree.ParameterList> pls = 
                    ((Tree.MethodDefinition) methodOrClass).getParameterLists();
            if (pls.isEmpty()) {
                return; //TODO
            }
            pl = pls.get(0);
        }
        else if (methodOrClass instanceof Tree.ClassDefinition) {
            pl = ((Tree.ClassDefinition) methodOrClass).getParameterList();
            if (pl==null) {
                return; //TODO
            }
        }
        else {
            return;
        }
        
        Tree.Term term = (Tree.Term) node;
        boolean anonfun = node instanceof Tree.FunctionArgument &&
                ((Tree.FunctionArgument) node).getExpression()!=null;
        String text;
        int il = 0;
        if (anonfun) {
            Tree.FunctionArgument fa = (Tree.FunctionArgument) node;
            type = fa.getType().getTypeModel();
            text = Nodes.toString(fa.getExpression(), tokens);
        }
        else {
            type = node.getUnit()
                    .denotableType(term.getTypeModel());
            text = Nodes.toString(node, tokens);
        }
        
        String typeDec;
        if (type==null || type.isUnknown()) {
            typeDec = "dynamic";
        }
        else {
            StringBuilder builder = new StringBuilder(); 
            il+=addType(tfc, doc, type, builder);
            typeDec = builder.toString();
        }
        
        final List<Tree.BaseMemberExpression> localRefs = 
                new ArrayList<Tree.BaseMemberExpression>();
        node.visit(new Visitor() {
            private Set<Declaration> decs = new HashSet<Declaration>();
            @Override
            public void visit(Tree.BaseMemberExpression that) {
                super.visit(that);
                Declaration d = that.getDeclaration();
                if (d!=null && !isParameterOfMethodOrClass(d) && 
                        !decs.contains(d) &&
                        d.isDefinedInScope(node.getScope()) && 
                        !d.isDefinedInScope(methodOrClass.getScope().getContainer())) {
                    localRefs.add(that);
                    decs.add(d);
                }
            }
        });
        
        String decl;
        String call;
        if (localRefs.isEmpty()) {
            decl = typeDec + " " + newName + " = " + text;
            call = newName;
        }
        else {
            StringBuilder params = new StringBuilder();
            StringBuilder args = new StringBuilder();
            for (Tree.BaseMemberExpression bme: localRefs) {
                if (params.length()!=0) {
                    params.append(", ");
                    args.append(", ");
                }
                String n = bme.getIdentifier().getText();
                il+=addType(tfc, doc, bme.getTypeModel(), params);
                params.append(" ").append(n);
                args.append(n);
            }
            decl = typeDec + " " + newName + "(" + params + ") => " + text;
            call = anonfun ? newName : newName + "(" + args + ")";
        }
        
        Integer start = pl.getStopIndex();
        String dectext = (pl.getParameters().isEmpty()?"":", ") + decl;
        tfc.addEdit(new InsertEdit(start, dectext));
        tfc.addEdit(new ReplaceEdit(Nodes.getNodeStartOffset(node), 
                Nodes.getNodeLength(node), call));
        int buffer = pl.getParameters().isEmpty()?0:2;
        decRegion = new Region(start+typeDec.length()+buffer+1, newName.length());
        refRegion = new Region(Nodes.getNodeStartOffset(node)+dectext.length()+il, call.length());
        typeRegion = new Region(start+buffer, typeDec.length());
    }

    private int addType(TextChange tfc, IDocument doc, 
            ProducedType tm, StringBuilder builder) {
        ProducedType type = node.getUnit().denotableType(tm);
        HashSet<Declaration> decs = new HashSet<Declaration>();
        importType(decs, type, rootNode);
        int il = applyImports(tfc, decs, rootNode, doc);
        builder.append(type.getProducedTypeName(rootNode.getUnit()));
        return il;
    }

    public void setNewName(String text) {
        newName = text;
    }
    
    public String getNewName() {
        return newName;
    }

    ProducedType getType() {
        return type;
    }

	public String[] getNameProposals() {
		return Nodes.nameProposals(node);
	}
    
}
