package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CreateInNewUnitProposal.addCreateToplevelProposal;
import static com.redhat.ceylon.eclipse.code.correct.CreateParameterProposal.addCreateParameterProposal;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importTypes;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getClassOrInterfaceBody;
import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.getRootNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findToplevelStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.eclipse.util.Indents;

class CreateProposal extends ChangeCorrectionProposal {
    
    final int offset;
    final IFile file;
    final int length;
    
    private CreateProposal(String def, String desc, Image image, 
    		int offset, IFile file, TextFileChange change) {
        super(desc, change, image);
        int loc = def.indexOf("= nothing");
        if (loc<0) {
            loc = def.indexOf("= ");
            if (loc<0) {
                loc = def.indexOf("{}")+1;
                length=0;
            }
            else {
                loc += 2;
                length = def.length()-loc;
            }
        }
        else {
            loc += 2;
            length=7;
        }
        this.offset=offset + loc;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        EditorUtil.gotoLocation(file, offset, length);
    }

    static IDocument getDocument(TextFileChange change) {
        try {
            return change.getCurrentDocument(null);
        }
        catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

	static void addCreateMemberProposal(Collection<ICompletionProposal> proposals, 
			DefinitionGenerator dg, Declaration typeDec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.Body body) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Create Member", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        String indent;
        String indentAfter;
        int offset;
        List<Tree.Statement> statements = body.getStatements();
        String delim = Indents.getDefaultLineDelimiter(doc);
		if (statements.isEmpty()) {
            indentAfter = delim + getIndent(decNode, doc);
            indent = indentAfter + getDefaultIndent();
            offset = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = statements.get(statements.size()-1);
            indent = "";
            offset = statement.getStartIndex();
            indentAfter = delim + getIndent(statement, doc);
        }
		HashSet<Declaration> alreadyImported = new HashSet<Declaration>();
		CompilationUnit cu = unit.getCompilationUnit();
		importType(alreadyImported, dg.returnType, cu);
		if (dg.parameters!=null) {
			importTypes(alreadyImported, dg.parameters.values(), cu);
		}
		int il = applyImports(change, alreadyImported, cu, doc);
		String def = indent + dg.generateShared(indentAfter, delim) + indentAfter;
		change.addEdit(new InsertEdit(offset, def));
		String desc = "Create " + dg.desc + " in '" + typeDec.getName() + "'";
		proposals.add(new CreateProposal(def, desc, dg.image, 
				offset+il, file, change));
    }

    private static void addCreateProposal(Collection<ICompletionProposal> proposals, 
    		boolean local, DefinitionGenerator dg, PhasedUnit unit, 
    		Tree.Statement statement) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange(local ? 
        		"Create Local" : "Create Toplevel", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        String indent = getIndent(statement, doc);
        int offset = statement.getStartIndex();
        String delim = Indents.getDefaultLineDelimiter(doc);
    	HashSet<Declaration> alreadyImported = new HashSet<Declaration>();
    	CompilationUnit cu = unit.getCompilationUnit();
    	importType(alreadyImported, dg.returnType, cu);
		if (dg.parameters!=null) {
			importTypes(alreadyImported, dg.parameters.values(), cu);
		}
    	int il = applyImports(change, alreadyImported, cu, doc);
    	String def = dg.generate(indent, delim) + delim + indent;
		change.addEdit(new InsertEdit(offset, def));
    	String desc = (local ? "Create local " : "Create toplevel ") + dg.desc;
		proposals.add(new CreateProposal(def, desc, dg.image, 
				offset+il, file, change));
    }
    
    static void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, DefinitionGenerator dg,
            Tree.QualifiedMemberOrTypeExpression qmte) {
        Tree.Primary p = ((Tree.QualifiedMemberOrTypeExpression) qmte).getPrimary();
        if (p.getTypeModel()!=null) {
            Declaration typeDec = p.getTypeModel().getDeclaration();
            addCreateMemberProposals(proposals, project, dg, typeDec);
        }
    }

	static void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, DefinitionGenerator dg, Declaration typeDec) {
	    if (typeDec!=null && typeDec instanceof ClassOrInterface) {
	        for (PhasedUnit unit: getUnits(project)) {
	            if (typeDec.getUnit().equals(unit.getUnit())) {
	                //TODO: "object" declarations?
	                FindDeclarationNodeVisitor fdv = new FindDeclarationNodeVisitor(typeDec);
	                getRootNode(unit).visit(fdv);
	                Tree.Declaration decNode = fdv.getDeclarationNode();
	                Tree.Body body = getClassOrInterfaceBody(decNode);
	                if (body!=null) {
	                    addCreateMemberProposal(proposals, dg, 
	                    		typeDec, unit, decNode, body);
	                    break;
	                }
	            }
	        }
	    }
    }

    static void addCreateLocalProposals(Collection<ICompletionProposal> proposals,
            IProject project, DefinitionGenerator dg) {
        //if (!fsv.isToplevel()) {
    	Tree.Statement statement = findStatement(dg.rootNode, dg.node);
    	if (statement!=null) {
    		for (PhasedUnit unit: getUnits(project)) {
    			if (unit.getUnit().equals(dg.rootNode.getUnit())) {
    				addCreateProposal(proposals, true, dg, 
    						unit, statement);
    				break;
    			}
    		}
    	}
        //}
    }

    static void addCreateToplevelProposals(Collection<ICompletionProposal> proposals,
            IProject project, DefinitionGenerator dg) {
    	Tree.Statement statement = findToplevelStatement(dg.rootNode, dg.node);
    	if (statement!=null) {
    		for (PhasedUnit unit: getUnits(project)) {
    			if (unit.getUnit().equals(dg.rootNode.getUnit())) {
    				addCreateProposal(proposals, 
    						false, dg, unit, statement);
    				break;
    			}
    		}
    	}
    }
        
	static void addCreateProposals(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IProject project,
            IFile file) {
	    Tree.MemberOrTypeExpression smte = (Tree.MemberOrTypeExpression) node;
	    String brokenName = getIdentifyingNode(node).getText();
	    if (!brokenName.isEmpty()) {
	    	DefinitionGenerator dg = DefinitionGenerator.create(brokenName, smte, cu);
	    	if (dg!=null) {
	    		if (smte instanceof Tree.QualifiedMemberOrTypeExpression) {
	    			addCreateMemberProposals(proposals, project, dg, 
	    					(Tree.QualifiedMemberOrTypeExpression) smte);
	    		}
	    		else {
	    			addCreateLocalProposals(proposals, project, dg);
	    			ClassOrInterface container = findClassContainer(cu, smte);
	    			if (container!=null && 
	    					container!=smte.getScope()) { //if the statement appears directly in an initializer, propose a local, not a member 
	    				do {
	    					addCreateMemberProposals(proposals, project, 
	    							dg, container);
	    					if (container.getContainer() instanceof Declaration) {
	    						container = findClassContainer((Declaration) container.getContainer());
	    					}
	    					else { 
	    						break;
	    					}
	    				}
	    				while (container!=null);
	    			}
	    			addCreateToplevelProposals(proposals, project, dg);
	    			addCreateToplevelProposal(proposals, dg, file);

	    			addCreateParameterProposal(proposals, project, dg);
	    		}
	    	}
	    }
    }


    private static ClassOrInterface findClassContainer(Tree.CompilationUnit cu, Node node){
		FindContainerVisitor fcv = new FindContainerVisitor(node);
		fcv.visit(cu);
    	Tree.Declaration declaration = fcv.getDeclaration();
        if(declaration == null || declaration == node)
            return null;
        if(declaration instanceof Tree.ClassOrInterface)
            return (ClassOrInterface) declaration.getDeclarationModel();
        if(declaration instanceof Tree.MethodDefinition)
            return findClassContainer(declaration.getDeclarationModel());
        if(declaration instanceof Tree.ObjectDefinition)
            return findClassContainer(declaration.getDeclarationModel());
        return null;
    }
    
    private static ClassOrInterface findClassContainer(Declaration declarationModel) {
        do {
            if(declarationModel == null)
                return null;
            if(declarationModel instanceof ClassOrInterface)
                return (ClassOrInterface) declarationModel;
            if(declarationModel.getContainer() instanceof Declaration)
                declarationModel = (Declaration)declarationModel.getContainer();
            else
                return null;
        }
        while(true);
    }
    
}