package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ADD;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ATTRIBUTE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.INTERFACE;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findToplevelStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.quickfix.AddConstraintSatisfiesProposal.createMissingBoundsText;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.applyImports;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.findDeclaration;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getBody;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getRootNode;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importTypes;
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

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.util.FindBodyContainerVisitor;
import com.redhat.ceylon.eclipse.util.FindDeclarationNodeVisitor;

class CreateProposal extends ChangeCorrectionProposal {
    
    final int offset;
    final IFile file;
    final int length;
    
    CreateProposal(String def, String desc, Image image, int indentLength, 
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
        this.offset=offset+indentLength + loc;
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
        dg.generateShared(indent, delim);
		if (dg.generated) {
			HashSet<Declaration> alreadyImported = new HashSet<Declaration>();
			CompilationUnit cu = unit.getCompilationUnit();
			importType(alreadyImported, dg.returnType, cu);
			importTypes(alreadyImported, dg.paramTypes, cu);
			int il = applyImports(change, alreadyImported, cu, doc);
			change.addEdit(new InsertEdit(offset, indent+dg.def+indentAfter));
			proposals.add(new CreateProposal(dg.def, 
					"Create " + dg.desc + " in '" + typeDec.getName() + "'", 
					dg.image, indent.length(), offset+il, file, change));
		}
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
        dg.generate(indent, delim);
        if (dg.generated) {
        	HashSet<Declaration> alreadyImported = new HashSet<Declaration>();
        	CompilationUnit cu = unit.getCompilationUnit();
        	importType(alreadyImported, dg.returnType, cu);
        	importTypes(alreadyImported, dg.paramTypes, cu);
        	int il = applyImports(change, alreadyImported, cu, doc);
        	change.addEdit(new InsertEdit(offset, 
        			dg.def+delim+indent));
        	proposals.add(new CreateProposal(dg.def, 
        			(local ? "Create local " : "Create toplevel ") + dg.desc, 
        			dg.image, 0, offset+il, file, change));
        }
    }

    private static void addCreateEnumProposal(Collection<ICompletionProposal> proposals, 
    		String def, String desc, Image image, PhasedUnit unit, 
    		Tree.Statement statement) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Create Enumerated", file);
        IDocument doc = getDocument(change);
        String indent = getIndent(statement, doc);
        String s = indent + def + getDefaultLineDelimiter(doc);
        int offset = statement.getStopIndex()+2;
        if (offset>doc.getLength()) {
            offset = doc.getLength();
            s = getDefaultLineDelimiter(doc) + s;
        }
        //def = def.replace("$indent", indent);
        change.setEdit(new InsertEdit(offset, s));
        proposals.add(new CreateProposal(def, "Create enumerated " + desc, 
                image, 0, offset, file, change));
    }

    private static void addCreateParameterProposal(Collection<ICompletionProposal> proposals, 
            String def, String desc, Image image, Declaration dec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.ParameterList paramList, 
            ProducedType returnType) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Add Parameter", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        int offset = paramList.getStopIndex();
        HashSet<Declaration> decs = new HashSet<Declaration>();
		CompilationUnit cu = unit.getCompilationUnit();
		importType(decs, returnType, cu);
		int il = applyImports(change, decs, cu, doc);
        change.addEdit(new InsertEdit(offset, def));
        proposals.add(new CreateProposal(def, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                image, 0, offset+il, file, change));
    }

    private static void addCreateTypeParameterProposal(Collection<ICompletionProposal> proposals, 
            String def, String desc, Image image, Declaration dec, PhasedUnit unit,
            Tree.Declaration decNode, int offset, String constraints) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Add Parameter", file);
        change.setEdit(new MultiTextEdit());
        IDocument doc = getDocument(change);
        HashSet<Declaration> decs = new HashSet<Declaration>();
		CompilationUnit cu = unit.getCompilationUnit();
		int il = applyImports(change, decs, cu, doc);
        change.addEdit(new InsertEdit(offset, def));
        if (constraints!=null) {
        	int loc = getConstraintLoc(decNode);
        	if (loc>=0) {
        		change.addEdit(new InsertEdit(loc, constraints));
        	}
        }
        proposals.add(new CreateProposal(def, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                image, 0, offset+il, file, change));
    }

    private static void addCreateParameterAndAttributeProposal(Collection<ICompletionProposal> proposals, 
            String pdef, String adef, String desc, Image image, Declaration dec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.ParameterList paramList, Tree.Body body, 
            ProducedType returnType) {
        IFile file = getFile(unit);
        TextFileChange change = new TextFileChange("Add Attribute", file);
        change.setEdit(new MultiTextEdit());
        int offset = paramList.getStopIndex();
        IDocument doc = getDocument(change);
        String indent;
        String indentAfter;
        int offset2;
        List<Tree.Statement> statements = body.getStatements();
        if (statements.isEmpty()) {
            indentAfter = getDefaultLineDelimiter(doc) + getIndent(decNode, doc);
            indent = indentAfter + getDefaultIndent();
            offset2 = body.getStartIndex()+1;
        }
        else {
            Tree.Statement statement = statements.get(statements.size()-1);
            indent = getDefaultLineDelimiter(doc) + getIndent(statement, doc);
            offset2 = statement.getStopIndex()+1;
            indentAfter = "";
        }
        HashSet<Declaration> decs = new HashSet<Declaration>();
		Tree.CompilationUnit cu = unit.getCompilationUnit();
		importType(decs, returnType, cu);
		int il = applyImports(change, decs, cu, doc);
        change.addEdit(new InsertEdit(offset, pdef));
        change.addEdit(new InsertEdit(offset2, indent+adef+indentAfter));
        proposals.add(new CreateProposal(pdef, 
                "Add " + desc + " to '" + dec.getName() + "'", 
                image, 0, offset+il, file, change));
    }
    
    private static int getConstraintLoc(Tree.Declaration decNode) {
        if( decNode instanceof Tree.ClassDefinition ) {
            Tree.ClassDefinition classDefinition = (Tree.ClassDefinition) decNode;
            return classDefinition.getClassBody().getStartIndex();
        }
        else if( decNode instanceof Tree.InterfaceDefinition ) {
            Tree.InterfaceDefinition interfaceDefinition = (Tree.InterfaceDefinition) decNode;
            return interfaceDefinition.getInterfaceBody().getStartIndex();
        }
        else if( decNode instanceof Tree.MethodDefinition ) {
            Tree.MethodDefinition methodDefinition = (Tree.MethodDefinition) decNode;
            return methodDefinition.getBlock().getStartIndex();
        }
        else if( decNode instanceof Tree.ClassDeclaration ) {
            Tree.ClassDeclaration classDefinition = (Tree.ClassDeclaration) decNode;
            return classDefinition.getClassSpecifier().getStartIndex();
        }
        else if( decNode instanceof Tree.InterfaceDefinition ) {
            Tree.InterfaceDeclaration interfaceDefinition = (Tree.InterfaceDeclaration) decNode;
            return interfaceDefinition.getTypeSpecifier().getStartIndex();
        }
        else if( decNode instanceof Tree.MethodDeclaration ) {
            Tree.MethodDeclaration methodDefinition = (Tree.MethodDeclaration) decNode;
            return methodDefinition.getSpecifierExpression().getStartIndex();
        }
        else {
        	return -1;
        }
    }

    static void addCreateParameterProposal(Collection<ICompletionProposal> proposals, 
    		IProject project, DefinitionGenerator dg) {
    	FindBodyContainerVisitor fcv = new FindBodyContainerVisitor(dg.node);
        fcv.visit(dg.cu);
        Tree.Declaration decl = fcv.getDeclaration();
        if (decl == null || 
                decl.getDeclarationModel() == null || 
                decl.getDeclarationModel().isActual()) {
            return;
        }
        
        Tree.ParameterList paramList = getParameters(decl);
        if (paramList != null) {
        	dg.generate("", ""); //TODO: is this right?
        	if (dg.generated) {
        		String paramDef = (paramList.getParameters().isEmpty() ? "" : ", ") + 
        				dg.def.substring(0, dg.def.length() - 1);
        		String paramDesc = "parameter '" + dg.brokenName + "'";

        		for (PhasedUnit unit : getUnits(project)) {
        			if (unit.getUnit().equals(dg.cu.getUnit())) {
        				CreateProposal.addCreateParameterProposal(proposals, paramDef, paramDesc, ADD, 
        						decl.getDeclarationModel(), unit, decl, paramList, dg.returnType);
        				break;
        			}
        		}
        	}
        }
    }

    static void addCreateParameterProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
        FindInvocationVisitor fav = new FindInvocationVisitor(node);
        fav.visit(cu);
        if (fav.result==null) return;
        Tree.Primary prim = fav.result.getPrimary();
        if (prim instanceof Tree.MemberOrTypeExpression) {
            ProducedReference pr = ((Tree.MemberOrTypeExpression) prim).getTarget();
            if (pr!=null) {
                Declaration d = pr.getDeclaration();
                ProducedType t=null;
                String n=null;
                if (node instanceof Tree.Term) {
                    t = ((Tree.Term) node).getTypeModel();
                    n = t.getDeclaration().getName();
                    if (n!=null) {
                        n = Character.toLowerCase(n.charAt(0)) + n.substring(1)
                                .replace("?", "").replace("[]", "");
                        if ("string".equals(n)) {
                            n = "text";
                        }
                    }
                }
                else if (node instanceof Tree.SpecifiedArgument) {
                	Tree.SpecifiedArgument sa = (Tree.SpecifiedArgument) node;
                	Tree.SpecifierExpression se = sa.getSpecifierExpression();
                    if (se!=null && se.getExpression()!=null) {
                        t = se.getExpression().getTypeModel();
                    }
                    n = sa.getIdentifier().getText();
                }
                else if (node instanceof Tree.TypedArgument) {
                	Tree.TypedArgument ta = (Tree.TypedArgument) node;
                    t = ta.getType().getTypeModel();
                    n = ta.getIdentifier().getText();
                }
                if (t!=null && n!=null) {
                    t = node.getUnit().denotableType(t);
                    String dv = defaultValue(prim.getUnit(), t);
                    String tn = t.getProducedTypeName();
                    String def = tn + " " + n + " = " + dv;
                    String desc = "parameter '" + n +"'";
                    addCreateParameterProposals(proposals, project, def, desc, d, t);
                    String pdef = n + " = " + dv;
                    String adef = tn + " " + n + ";";
                    String padesc = "attribute '" + n +"'";
                    addCreateParameterAndAttributeProposals(proposals, project, 
                            pdef, adef, padesc, d, t);
                }
            }
        }
    }

    static String defaultValue(Unit unit, ProducedType t) {
        if (t==null) {
            return "nothing";
        }
        String tn = t.getProducedTypeQualifiedName();
        if (tn.equals("ceylon.language::Boolean")) {
            return "false";
        }
        else if (tn.equals("ceylon.language::Integer")) {
            return "0";
        }
        else if (tn.equals("ceylon.language::Float")) {
            return "0.0";
        }
        else if (unit.isOptionalType(t)) {
            return "null";
        }
        else if (tn.equals("ceylon.language::String")) {
            return "\"\"";
        }
        else {
            return "nothing";
        }
    }
    
    private static Tree.ParameterList getParameters(Tree.Declaration decNode) {
        if (decNode instanceof Tree.AnyClass) {
            return ((Tree.AnyClass) decNode).getParameterList();
        }
        else if (decNode instanceof Tree.AnyMethod){
            List<Tree.ParameterList> pls = ((Tree.AnyMethod) decNode).getParameterLists();
            return pls.isEmpty() ? null : pls.get(0);
        }
        return null;
    }

    private static void addCreateParameterProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Declaration typeDec, ProducedType t) {
        if (typeDec!=null && typeDec instanceof Functional) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationNodeVisitor fdv = new FindDeclarationNodeVisitor(typeDec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    Tree.ParameterList paramList = getParameters(decNode);
                    if (paramList!=null) {
                        if (!paramList.getParameters().isEmpty()) {
                            def = ", " + def;
                        }
                        addCreateParameterProposal(proposals, def, desc, 
                        		ADD, typeDec, unit, decNode, paramList, t);
                        break;
                    }
                }
            }
        }
    }

    private static void addCreateParameterAndAttributeProposals(Collection<ICompletionProposal> proposals,
            IProject project, String pdef, String adef, String desc, Declaration typeDec, ProducedType t) {
        if (typeDec!=null && typeDec instanceof ClassOrInterface) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationNodeVisitor fdv = new FindDeclarationNodeVisitor(typeDec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    Tree.ParameterList paramList = getParameters(decNode);
                    Tree.Body body = getBody(decNode);
                    if (body!=null && paramList!=null) {
                        if (!paramList.getParameters().isEmpty()) {
                            pdef = ", " + pdef;
                        }
                        CreateProposal.addCreateParameterAndAttributeProposal(proposals, pdef, 
                                adef, desc, ADD, typeDec, unit, decNode, 
                                paramList, body, t);
                    }
                }
            }
        }
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
    	Tree.Statement statement = findStatement(dg.cu, dg.node);
    	if (statement!=null) {
    		for (PhasedUnit unit: getUnits(project)) {
    			if (unit.getUnit().equals(dg.cu.getUnit())) {
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
    	Tree.Statement statement = findToplevelStatement(dg.cu, dg.node);
    	if (statement!=null) {
    		for (PhasedUnit unit: getUnits(project)) {
    			if (unit.getUnit().equals(dg.cu.getUnit())) {
    				addCreateProposal(proposals, 
    						false, dg, unit, statement);
    				break;
    			}
    		}
    	}
    }

    static void addCreateTypeParameterProposal(Collection<ICompletionProposal> proposals, 
    		IProject project, Tree.CompilationUnit cu, final Tree.BaseType node, 
    		String brokenName) {
    	FindBodyContainerVisitor fcv = new FindBodyContainerVisitor(node);
        fcv.visit(cu);
        Tree.Declaration decl = fcv.getDeclaration();
        Declaration d = decl==null ? null : decl.getDeclarationModel();
		if (d == null || d.isActual() ||
                !(d instanceof Method || d instanceof ClassOrInterface)) {
            return;
        }
        
        Tree.TypeParameterList paramList = getTypeParameters(decl);
        String paramDef;
        int offset;
        String paramDesc = "type parameter '" + brokenName + "'";
        if (paramList != null) {
            paramDef = ", " + brokenName;
            offset = paramList.getStopIndex();
        }
        else {
        	paramDef = "<" + brokenName + ">";
        	offset = getIdentifyingNode(decl).getStopIndex()+1;
        }
        
        //TODO: generate type constraints when it appears 
        //      as a type argument!
        
        class FindTypeParameterConstraintVisitor extends Visitor {
        	List<ProducedType> result;
        	@Override
        	public void visit(Tree.SimpleType that) {
        	    super.visit(that);
        	    List<TypeParameter> tps = that.getDeclarationModel().getTypeParameters();
        	    Tree.TypeArgumentList tal = that.getTypeArgumentList();
        	    if (tal!=null) {
        	    	List<Tree.Type> tas = tal.getTypes();
        	    	for (int i=0; i<tas.size(); i++) {
        	    		if (tas.get(i)==node) {
        	    			result = tps.get(i).getSatisfiedTypes();
        	    		}
        	    	}
        	    }
        	}
        	@Override
        	public void visit(Tree.StaticMemberOrTypeExpression that) {
        	    super.visit(that);
        	    Declaration d = that.getDeclaration();
        	    if (d instanceof Generic) {
        	    	List<TypeParameter> tps = ((Generic) d).getTypeParameters();
        	    	Tree.TypeArguments tal = that.getTypeArguments();
        	    	if (tal instanceof Tree.TypeArgumentList) {
        	    		List<Tree.Type> tas = ((Tree.TypeArgumentList) tal).getTypes();
        	    		for (int i=0; i<tas.size(); i++) {
        	    			if (tas.get(i)==node) {
        	    				result = tps.get(i).getSatisfiedTypes();
        	    			}
        	    		}
        	    	}
        	    }
        	}
        }
        FindTypeParameterConstraintVisitor ftpcv = 
        		new FindTypeParameterConstraintVisitor();
        ftpcv.visit(cu);
        String constraints;
        if (ftpcv.result==null) {
        	constraints = null;
        }
        else {
        	String bounds = createMissingBoundsText(ftpcv.result);
        	if (bounds.isEmpty()) {
        		constraints = null;
        	}
        	else {
        		constraints = "given " + brokenName + 
        				" satisfies " + bounds + " ";
        	}
        }
        
        for (PhasedUnit unit : getUnits(project)) {
        	if (unit.getUnit().equals(cu.getUnit())) {
        		CreateProposal.addCreateTypeParameterProposal(proposals, 
        				paramDef, paramDesc, ADD, d, unit, decl, offset,
        				constraints);
        		break;
        	}
        }

    }
    
    private static Tree.TypeParameterList getTypeParameters(Tree.Declaration decl) {
    	if (decl instanceof Tree.ClassOrInterface) {
    		return ((Tree.ClassOrInterface) decl).getTypeParameterList();
    	}
    	else if (decl instanceof Tree.AnyMethod) {
    		return ((Tree.AnyMethod) decl).getTypeParameterList();
    	}
    	return null;
    }

    static void addCreateEnumProposal(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
            Node idn = getIdentifyingNode(node);
            if (idn==null) return;
    		String brokenName = idn.getText();
            if (brokenName.isEmpty()) return;
            Tree.Declaration dec = findDeclaration(cu, node);
            if (dec instanceof Tree.ClassDefinition) {
                Tree.ClassDefinition cd = (Tree.ClassDefinition) dec;
                if (cd.getCaseTypes()!=null) {
                    if (cd.getCaseTypes().getTypes().contains(node)) {
                        addCreateEnumProposal(proposals, project, 
                                "class " + brokenName + parameters(cd.getTypeParameterList()) +
                                    parameters(cd.getParameterList()) +
                                    " extends " + cd.getDeclarationModel().getName() + 
                                    parameters(cd.getTypeParameterList()) + 
                                    arguments(cd.getParameterList()) + " {}", 
                                "class '"+ brokenName + parameters(cd.getTypeParameterList()) +
                                parameters(cd.getParameterList()) + "'", 
                                CeylonLabelProvider.CLASS, cu, cd);
                    }
                    if (cd.getCaseTypes().getBaseMemberExpressions().contains(node)) {
                        addCreateEnumProposal(proposals, project, 
                                "object " + brokenName + 
                                    " extends " + cd.getDeclarationModel().getName() + 
                                    parameters(cd.getTypeParameterList()) + 
                                    arguments(cd.getParameterList()) + " {}", 
                                "object '"+ brokenName + "'", 
                                ATTRIBUTE, cu, cd);
                    }
                }
            }
            if (dec instanceof Tree.InterfaceDefinition) {
                Tree.InterfaceDefinition cd = (Tree.InterfaceDefinition) dec;
                if (cd.getCaseTypes()!=null) {
                    if (cd.getCaseTypes().getTypes().contains(node)) {
                        addCreateEnumProposal(proposals, project, 
                                "interface " + brokenName + parameters(cd.getTypeParameterList()) +
                                    " satisfies " + cd.getDeclarationModel().getName() + 
                                    parameters(cd.getTypeParameterList()) + " {}", 
                                "interface '"+ brokenName + parameters(cd.getTypeParameterList()) +  "'", 
                                INTERFACE, cu, cd);
                    }
                    if (cd.getCaseTypes().getBaseMemberExpressions().contains(node)) {
                        addCreateEnumProposal(proposals, project, 
                                "object " + brokenName + 
                                    " satisfies " + cd.getDeclarationModel().getName() + 
                                    parameters(cd.getTypeParameterList()) + " {}", 
                                "object '"+ brokenName + "'", 
                                ATTRIBUTE, cu, cd);
                    }
                }
            }
        }
        
        private static void addCreateEnumProposal(Collection<ICompletionProposal> proposals,
                IProject project, String def, String desc, Image image, 
                Tree.CompilationUnit cu, Tree.TypeDeclaration cd) {
            for (PhasedUnit unit: getUnits(project)) {
                if (unit.getUnit().equals(cu.getUnit())) {
                    CreateProposal.addCreateEnumProposal(proposals, def, desc, image, unit, cd);
                    break;
                }
            }
        }

        private static String parameters(Tree.ParameterList pl) {
            StringBuilder result = new StringBuilder();
            if (pl==null ||
                    pl.getParameters().isEmpty()) {
                result.append("()");
            }
            else {
                result.append("(");
                int len = pl.getParameters().size(), i=0;
                for (Tree.Parameter p: pl.getParameters()) {
                    if (p!=null) {
                        if (p instanceof Tree.ParameterDeclaration) {
                            Tree.TypedDeclaration td = ((Tree.ParameterDeclaration) p).getTypedDeclaration();
                            result.append(td.getType().getTypeModel().getProducedTypeName()) 
                                    .append(" ")
                                    .append(td.getIdentifier().getText());
                        }
                        else if (p instanceof Tree.InitializerParameter) {
                            result.append(p.getParameterModel().getType().getProducedTypeName()) 
                                .append(" ")
                                .append(((Tree.InitializerParameter) p).getIdentifier().getText());
                        }
                        //TODO: easy to add back in:
                        /*if (p instanceof Tree.FunctionalParameterDeclaration) {
                            Tree.FunctionalParameterDeclaration fp = (Tree.FunctionalParameterDeclaration) p;
                            for (Tree.ParameterList ipl: fp.getParameterLists()) {
                                parameters(ipl, label);
                            }
                        }*/
                    }
                    if (++i<len) result.append(", ");
                }
                result.append(")");
            }
            return result.toString();
        }
        
        private static String parameters(Tree.TypeParameterList tpl) {
            StringBuilder result = new StringBuilder();
            if (tpl!=null &&
                    !tpl.getTypeParameterDeclarations().isEmpty()) {
                result.append("<");
                int len = tpl.getTypeParameterDeclarations().size(), i=0;
                for (Tree.TypeParameterDeclaration p: tpl.getTypeParameterDeclarations()) {
                    result.append(p.getIdentifier().getText());
                    if (++i<len) result.append(", ");
                }
                result.append(">");
            }
            return result.toString();
        }
        
        private static String arguments(Tree.ParameterList pl) {
            StringBuilder result = new StringBuilder();
            if (pl==null ||
                    pl.getParameters().isEmpty()) {
                result.append("()");
            }
            else {
                result.append("(");
                int len = pl.getParameters().size(), i=0;
                for (Tree.Parameter p: pl.getParameters()) {
                    if (p!=null) {
                        Tree.Identifier id;
                        if (p instanceof Tree.InitializerParameter) {
                            id = ((Tree.InitializerParameter) p).getIdentifier();
                        }
                        else if (p instanceof Tree.ParameterDeclaration) {
                            id = ((Tree.ParameterDeclaration) p).getTypedDeclaration().getIdentifier();
                        }
                        else {
                            continue;
                        }
                        result.append(id.getText());
                        //TODO: easy to add back in:
                        /*if (p instanceof Tree.FunctionalParameterDeclaration) {
                            Tree.FunctionalParameterDeclaration fp = (Tree.FunctionalParameterDeclaration) p;
                            for (Tree.ParameterList ipl: fp.getParameterLists()) {
                                parameters(ipl, label);
                            }
                        }*/
                    }
                    if (++i<len) result.append(", ");
                }
                result.append(")");
            }
            return result.toString();
        }
        
}