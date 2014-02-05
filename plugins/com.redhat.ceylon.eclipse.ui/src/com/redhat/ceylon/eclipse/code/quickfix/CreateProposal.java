package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findToplevelStatement;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getBody;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getRootNode;
import static com.redhat.ceylon.eclipse.code.quickfix.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.quickfix.ImportProposals.importTypes;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;

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
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

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
        Util.gotoLocation(file, offset, length);
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
        String delim = getDefaultLineDelimiter(doc);
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
        String delim = getDefaultLineDelimiter(doc);
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
	                Tree.Body body = getBody(decNode);
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
        
}