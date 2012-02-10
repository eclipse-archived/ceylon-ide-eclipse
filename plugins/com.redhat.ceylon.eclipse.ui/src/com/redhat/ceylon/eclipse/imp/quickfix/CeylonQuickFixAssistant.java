package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.imp.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.imp.editor.EditorAnnotationService.getRefinedDeclaration;
import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.FORMAL_REFINEMENT;
import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.getProposals;
import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.imp.quickfix.Util.getLevenshteinDistance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.imp.editor.quickfix.IAnnotation;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.services.IQuickFixAssistant;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AttributeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Import;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PrefixOperatorExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SimpleType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;
import com.redhat.ceylon.eclipse.imp.wizard.NewUnitWizard;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;
import com.redhat.ceylon.eclipse.util.FindStatementVisitor;

/**
 * Popup quick fixes for problem annotations displayed in editor
 * @author gavin
 */
public class CeylonQuickFixAssistant implements IQuickFixAssistant {

    @Override
    public boolean canFix(Annotation annotation) {
        int code;
        if (annotation instanceof IAnnotation) {
            code = ((IAnnotation) annotation).getId();
        }
        else if (annotation instanceof MarkerAnnotation) {
            code = ((MarkerAnnotation) annotation).getMarker()
                   .getAttribute(IMessageHandler.ERROR_CODE_KEY, 0);
        }
        else {
            return false;
        }
        return code>0;
    }

    @Override
    public boolean canAssist(IQuickFixInvocationContext context) {
        //oops, all this is totally useless, because
        //this method never gets called by IMP
        /*Tree.CompilationUnit cu = (CompilationUnit) context.getModel()
                .getAST(new NullMessageHandler(), new NullProgressMonitor());
        return CeylonSourcePositionLocator.findNode(cu, context.getOffset(), 
                context.getOffset()+context.getLength()) instanceof Tree.Term;*/
        return false;
    }

    @Override
    public String[] getSupportedMarkerTypes() {
        return new String[] { CeylonBuilder.PROBLEM_MARKER_ID };
    }

    public static String getIndent(Node node, IDocument doc) {
        try {
            IRegion region = doc.getLineInformation(node.getEndToken().getLine()-1);
            String line = doc.get(region.getOffset(), region.getLength());
            char[] chars = line.toCharArray();
            for (int i=0; i<chars.length; i++) {
                if (chars[i]!='\t' && chars[i]!=' ') {
                    return line.substring(0,i);
                }
            }
            return line;
        }
        catch (BadLocationException ble) {
            return "";
        }
    }
    
    @Override
    public void addProposals(IQuickFixInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals) {
        if (context.getModel()==null) return;
        IProject project = context.getModel().getProject().getRawProject();
        IFile file = context.getModel().getFile();
        TypeChecker tc = CeylonBuilder.getProjectTypeChecker(project);
        Tree.CompilationUnit cu = (Tree.CompilationUnit) context.getModel()
                .getAST(new NullMessageHandler(), new NullProgressMonitor());
        if (problem==null) {
            //oops, all this is totally useless, because
            //this method never gets called except when
            //there is a Problem
            /*Node node = findNode(cu, context.getOffset(), 
                    context.getOffset() + context.getLength());
            addRefactoringProposals(context, proposals, node);*/
        }
        else {
            Node node = findNode(cu, problem.getOffset(), 
                    problem.getOffset() + problem.getLength());
            switch ( problem.getProblemId() ) {
            case 100:
                addCreateProposals(cu, node, problem, proposals, 
                        project, tc, file);
                if (tc!=null) {
                    addRenameProposals(cu, node, problem, proposals, file);
                    addImportProposals(cu, node, proposals, file);
                }
                break;
            case 101:
                if (tc!=null) {
                    addRenameProposals(cu, node, problem, proposals, file);
                }
                break;
            case 200:
                addSpecifyTypeProposal(cu, node, problem, proposals, file);
                break;
            case 300:
                if (context.getSourceViewer()!=null) { //TODO: figure out some other way to get the Document!
                    addImplementFormalMembersProposal(cu, node, proposals, file,
                            context.getSourceViewer().getDocument());
                }
                break;
            case 400:
                addMakeSharedProposal(problem, proposals, project, node);
                break;
            case 500:
                addMakeDefaultProposal(problem, proposals, project, node);
                break;
            case 600:
                addMakeActualProposal(problem, proposals, project, node);
                break;
            case 700:
                addMakeSharedDecProposal(problem, proposals, project, node);
                break;
            case 800:
                addMakeVariableProposal(problem, proposals, project, node);
                break;
            case 803:
                addMakeVariableProposal(problem, proposals, project, node);
                addFixSpecificationProposal(problem, proposals, project, file, node);
                break;
            case 801:
                addMakeVariableDecProposal(cu, problem, proposals, project, node);
                addFixSpecificationProposal(problem, proposals, project, file, node);
                break;
            case 802:
                addFixAssignmentProposal(problem, proposals, project, file, node);
                break;
            case 900:
                addMakeAbstractProposal(problem, proposals, project, node);
                break;
            case 1000:
                addAddParenthesesProposal(problem, file, proposals, node);
                addChangeDeclarationProposal(problem, file, proposals, node);
                break;
            }
        }
    }

    private void addAddParenthesesProposal(ProblemLocation problem, IFile file,
            Collection<ICompletionProposal> proposals, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        TextFileChange change = new TextFileChange("Add Empty Parameter List", file);
        int offset = decNode.getIdentifier().getStopIndex();
        change.setEdit(new InsertEdit(offset+1, "()"));
        proposals.add(createAddParenthesesProposal(decNode.getDeclarationModel(), 
                offset+2, file, change));
    }

    private ChangeCorrectionProposal createAddParenthesesProposal(Declaration dec,
            final int offset, final IFile file, TextFileChange change) {
        String desc = "Add empty parameter list to '" + dec.getName() + "'";
        if (dec.getContainer() instanceof TypeDeclaration) {
            desc += "in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'";
        }
        return new ChangeCorrectionProposal(desc, change, 10, CORRECTION) {
            @Override
            public void apply(IDocument document) {
                super.apply(document);
                Util.gotoLocation(file, offset, 0);
            }
        };
    }

    private void addChangeDeclarationProposal(ProblemLocation problem, IFile file,
            Collection<ICompletionProposal> proposals, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        CommonToken token = (CommonToken) decNode.getMainToken();
        String keyword;
        if (decNode instanceof Tree.AnyClass){
            keyword = "interface";
        }
        else if (decNode instanceof Tree.AnyMethod) {
            if (token.getText().equals("void")) return;
            keyword = "value";
        }
        else {
            return;
        }
        TextFileChange change = new TextFileChange("Change Declaration", file);
        change.setEdit(new ReplaceEdit(token.getStartIndex(), token.getText().length(), 
                keyword));
        proposals.add(createChangeDeclarationProposal(decNode.getDeclarationModel(), 
                keyword, token, file, change));
    }

    private ChangeCorrectionProposal createChangeDeclarationProposal(Declaration dec,
            final String kw, final CommonToken token, final IFile file, TextFileChange change) {
        String desc = "Change declaration to '" + kw + "'";
        return new ChangeCorrectionProposal(desc, change, 10, CORRECTION) {
            @Override
            public void apply(IDocument document) {
                super.apply(document);
                Util.gotoLocation(file, token.getStartIndex(), 
                        kw.length());
            }
        };
    }

    private void addMakeActualProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        boolean shared = decNode.getDeclarationModel().isShared();
        addAddAnnotationProposal(node, shared ? "actual " : "shared actual ", 
                shared ? "Make Actual" : "Make Shared Actual", problem, 
                decNode.getDeclarationModel(), proposals, project);
    }

    private void addMakeDefaultProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        addAddAnnotationProposal(node, "default ", "Make Default", problem, 
                getRefinedDeclaration(decNode.getDeclarationModel()), //TODO: this is wrong!
                proposals, project);
    }

    private void addMakeAbstractProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            dec = (Declaration) ((Tree.Declaration) node).getDeclarationModel().getContainer();
        }
        else {
            dec = (Declaration) node.getScope();
        }
        addAddAnnotationProposal(node, "abstract ", "Make Abstract", problem, dec, 
                proposals, project);
    }

    private void addMakeVariableProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, Node node) {
        Term term;
        if (node instanceof Tree.AssignmentOp) {
            term = ((Tree.AssignOp) node).getLeftTerm();
        }
        else if (node instanceof Tree.UnaryOperatorExpression) {
            term = ((Tree.PrefixOperatorExpression) node).getTerm();
        }
        else {
            return;
        }
        Declaration dec = ((Tree.BaseMemberExpression) term).getDeclaration();
        addAddAnnotationProposal(node, "variable ", "Make Variable", problem, 
                dec, proposals, project);
    }
    
    private void addMakeVariableDecProposal(Tree.CompilationUnit cu, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, Node node) {
        final Tree.SpecifierOrInitializerExpression sie = (Tree.SpecifierOrInitializerExpression) node;
        class GetInitializedVisitor extends Visitor {
            Value dec;
            @Override
            public void visit(Tree.AttributeDeclaration that) {
                super.visit(that);
                if (that.getSpecifierOrInitializerExpression()==sie) {
                    dec = that.getDeclarationModel();
                }
            }
        }
        GetInitializedVisitor v = new GetInitializedVisitor();
        v.visit(cu);
        addAddAnnotationProposal(node, "variable ", "Make Variable", problem, v.dec, 
                proposals, project);
    }
    
    private void addFixSpecificationProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, IFile file, Node node) {
        proposals.add(createFixAssignmentProposal("=", ((CommonToken) node.getMainToken()).getStartIndex(), file));
    }
    
    private void addFixAssignmentProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, IFile file, Node node) {
        proposals.add(createFixAssignmentProposal(":=", ((CommonToken) node.getMainToken()).getStartIndex(), file));
    }
    
    private ChangeCorrectionProposal createFixAssignmentProposal(String op, int offset, IFile file) {
        String desc = "Change to '" + op + "'";
        TextFileChange change = new TextFileChange(desc, file);
        change.setEdit(new ReplaceEdit(offset, op.length()==1 ? 2 : 1, op));
        return new ChangeCorrectionProposal(desc, change, 10, CORRECTION);
    }

    private void addMakeSharedProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, Node node) {
        Declaration dec = null;
        if (node instanceof Tree.QualifiedMemberOrTypeExpression) {
            Tree.QualifiedMemberOrTypeExpression qmte = (Tree.QualifiedMemberOrTypeExpression) node;
            dec = qmte.getDeclaration();
        }
        else if (node instanceof Tree.QualifiedType) {
            Tree.QualifiedType qmte = (Tree.QualifiedType) node;
            dec = qmte.getDeclarationModel();
        }
        else if (node instanceof Tree.ImportMemberOrType) {
            Tree.ImportMemberOrType imt = (Tree.ImportMemberOrType) node;
            dec = imt.getDeclarationModel();
        }
        if (dec!=null) {
            addAddAnnotationProposal(node, "shared ", "Make Shared", problem, dec, 
                    proposals, project);
        }
    }
    
    private void addMakeSharedDecProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        addAddAnnotationProposal(node, "shared ", "Make Shared", problem, 
                decNode.getDeclarationModel(), proposals, project);
    }
    
    private void addAddAnnotationProposal(Node node, String annotation, String desc, ProblemLocation problem, 
            Declaration dec, Collection<ICompletionProposal> proposals, IProject project) {
        if (dec!=null) {
            for (PhasedUnit unit: CeylonBuilder.getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    //TODO: "object" declarations?
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(dec);
                    unit.getCompilationUnit().visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    if (decNode!=null) {
                        IFile file = CeylonBuilder.getFile(unit);
                        TextFileChange change = new TextFileChange(desc, file);
                        change.setEdit(new MultiTextEdit());
                        Integer offset = decNode.getStartIndex();
                        change.addEdit(new InsertEdit(offset, annotation));
                        if (decNode instanceof Tree.TypedDeclaration) {
                            Type type = ((Tree.TypedDeclaration) decNode).getType();
                            if (type instanceof Tree.FunctionModifier 
                                    || type instanceof Tree.ValueModifier) {
                                String explicitType = type.getTypeModel().getProducedTypeName();
                                change.addEdit(new ReplaceEdit(type.getStartIndex(), type.getText().length(), 
                                        explicitType));
                            }
                        }
                        proposals.add(createAddAnnotionProposal(dec, annotation, offset, file, change));
                    }
                    break;
                }
            }
        }
    }

    private ChangeCorrectionProposal createAddAnnotionProposal(Declaration dec, String annotation,
            final int offset, final IFile file, TextFileChange change) {
        String desc = "Make '" + dec.getName() + "' " + annotation;
        if (dec.getContainer() instanceof TypeDeclaration) {
            desc += "in '" + ((TypeDeclaration) dec.getContainer()).getName() + "'";
        }
        return new ChangeCorrectionProposal(desc, change, 10, CORRECTION) {
            @Override
            public void apply(IDocument document) {
                super.apply(document);
                Util.gotoLocation(file, offset, 0);
            }
        };
    }

    private void addImplementFormalMembersProposal(Tree.CompilationUnit cu, Node node, 
            Collection<ICompletionProposal> proposals, IFile file, IDocument doc) {
        Tree.ClassBody body;
        if (node instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition def = (Tree.ClassDefinition) node;
            body = def.getClassBody();
        }
        else if (node instanceof Tree.ObjectDefinition) {
            Tree.ObjectDefinition def = (Tree.ObjectDefinition) node;
            body = def.getClassBody();
        }
        else {
            return;
        }
        List<Statement> statements = body.getStatements();
        int offset;
        String indent;
        String indentAfter;
        if (statements.isEmpty()) {
            indentAfter = "\n" + getIndent(body, doc);
            indent = indentAfter + getDefaultIndent();
            offset = body.getStartIndex()+1;
        }
        else {
            Statement statement = statements.get(statements.size()-1);
            indent = "\n" + getIndent(statement, doc);
            indentAfter = "";
            offset = statement.getStopIndex()+1;
        }
        StringBuilder result = new StringBuilder();
        for (DeclarationWithProximity dwp: getProposals(node, "", cu).values()) {
            Declaration d = dwp.getDeclaration();
            if (d.isFormal() && 
                    ((ClassOrInterface) node.getScope()).isInheritedFromSupertype(d)) {
            	ProducedReference pr = CeylonContentProposer.getRefinedProducedReference(node, d);
                result.append(indent).append(getRefinementTextFor(d, pr, indent)).append(indentAfter);
            }
        }
        TextFileChange change = new TextFileChange("Refine Formal Members", file);
        change.setEdit(new InsertEdit(offset, result.toString()));
        proposals.add(createImplementFormalMembersProposal(offset, file, change));
    }
    
    private void addSpecifyTypeProposal(Tree.CompilationUnit cu, Node node, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file) {
        final Type type = (Tree.Type) node;
        class InferTypeVisitor extends Visitor {
            Declaration dec;
            ProducedType inferredType;
            @Override public void visit(Tree.TypedDeclaration that) {
                super.visit(that);
                if (that.getType()==type) {
                    dec = that.getDeclarationModel();
                    inferredType = type.getTypeModel();
                }
            }
            @Override public void visit(Tree.SpecifierStatement that) {
                super.visit(that);
                Term bme = that.getBaseMemberExpression();
                if (bme instanceof BaseMemberExpression) {
                	if (((BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                		inferredType = that.getSpecifierExpression().getExpression().getTypeModel();
                	}
                }
            }
            @Override public void visit(Tree.AssignmentOp that) {
                super.visit(that);
                if (that.getLeftTerm() instanceof Tree.BaseMemberExpression) {
                    Tree.BaseMemberExpression bme = (Tree.BaseMemberExpression) that.getLeftTerm();
                    if (bme.getDeclaration().equals(dec)) {
                        //TODO: take a union if there are multiple assignments
                        inferredType = that.getRightTerm().getTypeModel();
                    }
                }
            }
        }
        InferTypeVisitor itv = new InferTypeVisitor();
        itv.visit(cu);
        String explicitType = itv.inferredType.getProducedTypeName();
        TextFileChange change =  new TextFileChange("Specify Type", file);
        change.setEdit(new ReplaceEdit(problem.getOffset(), type.getText().length(), 
                explicitType)); //Note: don't use problem.getLength() because it's wrong from the problem list
        proposals.add(createSpecifyTypeProposal(problem, file, explicitType, change));
    }

    /*public void addRefactoringProposals(IQuickFixInvocationContext context,
            Collection<ICompletionProposal> proposals, Node node) {
        try {
            if (node instanceof Tree.Term) {
                ExtractFunctionRefactoring efr = new ExtractFunctionRefactoring(context);
                proposals.add( new ChangeCorrectionProposal("Extract function '" + efr.getNewName() + "'", 
                        efr.createChange(new NullProgressMonitor()), 20, null));
                ExtractValueRefactoring evr = new ExtractValueRefactoring(context);
                proposals.add( new ChangeCorrectionProposal("Extract value '" + evr.getNewName() + "'", 
                        evr.createChange(new NullProgressMonitor()), 20, null));
            }
        }
        catch (CoreException ce) {}
    }*/
    
    private void addCreateProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
        if (node instanceof Tree.StaticMemberOrTypeExpression) {
            Tree.StaticMemberOrTypeExpression smte = (Tree.StaticMemberOrTypeExpression) node;

            String brokenName = getIdentifyingNode(node).getText();
            if (brokenName.isEmpty()) return;
            boolean isUpperCase = Character.isUpperCase(brokenName.charAt(0));
            String def;
            String desc;
            Image image;
            FindArgumentsVisitor fav = new FindArgumentsVisitor(smte);
            cu.visit(fav);
            ProducedType t = fav.expectedType;
            boolean isVoid = t==null;
            if (fav.positionalArgs!=null || fav.namedArgs!=null) {
                StringBuilder params = new StringBuilder();
                params.append("(");
                if (fav.positionalArgs!=null) appendPositionalArgs(fav, params);
                if (fav.namedArgs!=null) appendNamedArgs(fav, params);
                if (params.length()>1) {
                    params.setLength(params.length()-2);
                }
                params.append(")");
                if (isUpperCase) {
                    String supertype = "";
                    if (!isVoid) {
                        if (t.getDeclaration() instanceof Class) {
                            supertype = " extends " + t.getProducedTypeName() + "()"; //TODO: arguments!
                        }
                        else {
                            supertype = " satisfies " + t.getProducedTypeName();
                        }
                    }
                    def = "class " + brokenName + params + supertype + " {\n";
                    if (!isVoid) {
                        for (DeclarationWithProximity dwp: t.getDeclaration().getMatchingMemberDeclarations("", 0).values()) {
                            Declaration d = dwp.getDeclaration();
                            if (d.isFormal() /*&& td.isInheritedFromSupertype(d)*/) {
                                ProducedReference pr = CeylonContentProposer.getRefinedProducedReference(t, d);
                                def+= "$indent    " + getRefinementTextFor(d, pr, "") + "\n";
                            }
                        }
                    }
                    def+="$indent}";
                    desc = "class '" + brokenName + params + supertype + "'";
                    image = CeylonLabelProvider.CLASS;
                }
                else {
                    String type = isVoid ? "void" : t.getProducedTypeName();
                    String impl = isVoid ? " {}" : " { return bottom; }";
                    def = type + " " + brokenName + params + impl;
                    desc = "function '" + brokenName + params + "'";
                    image = CeylonLabelProvider.METHOD;
                }
            }
            else if (!isUpperCase) {
                String type = isVoid ? "Void" : 
                    t.getProducedTypeName();
                def = type + " " + brokenName + " = bottom;";
                desc = "value '" + brokenName + "'";
                image = CeylonLabelProvider.ATTRIBUTE;
            }
            else {
                return;
            }

            if (smte instanceof Tree.QualifiedMemberOrTypeExpression) {
                    addCreateMemberProposals(proposals, project, "shared " + def, desc, image, 
                            (Tree.QualifiedMemberOrTypeExpression) smte);
            }
            else {
                addCreateLocalProposals(proposals, project, def, desc, image, cu, smte);
                ClassOrInterface container = findClassContainer(cu, smte);
                if(container != null){
                    do{
                        addCreateMemberProposals(proposals, project, def, desc, image, container);
                        if(container.getContainer() instanceof Declaration)
                            container = findClassContainer((Declaration) container.getContainer());
                        else 
                            break;
                    }while(container != null);
                }
                addCreateToplevelProposals(proposals, project, def, desc, image, cu, smte);
                addCreateToplevelProposal(proposals, def.replace("$indent", ""), 
                        desc, image, file, brokenName);
            }
            
        }
    }

    private ClassOrInterface findClassContainer(CompilationUnit cu, Node n){
        FindContainerVisitor visitor = new FindContainerVisitor(n);
        visitor.visit(cu);
        com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration declaration = visitor.getDeclaration();
        if(declaration == null || declaration == n)
            return null;
        if(declaration instanceof Tree.ClassOrInterface)
            return (ClassOrInterface) declaration.getDeclarationModel();
        if(declaration instanceof Tree.MethodDefinition)
            return findClassContainer(declaration.getDeclarationModel());
        if(declaration instanceof Tree.ObjectDefinition)
            return findClassContainer(declaration.getDeclarationModel());
        return null;
    }
    
    private ClassOrInterface findClassContainer(Declaration declarationModel) {
        do{
            if(declarationModel == null)
                return null;
            if(declarationModel instanceof ClassOrInterface)
                return (ClassOrInterface) declarationModel;
            if(declarationModel.getContainer() instanceof Declaration)
                declarationModel = (Declaration)declarationModel.getContainer();
            else
                return null;
        }while(true);
    }

    private void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Tree.QualifiedMemberOrTypeExpression qmte) {
        Declaration typeDec = ((Tree.QualifiedMemberOrTypeExpression) qmte).getPrimary()
                .getTypeModel().getDeclaration();
        addCreateMemberProposals(proposals, project, def, desc, image, typeDec);
    }
    
    private void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Declaration typeDec) {
        if (typeDec!=null && typeDec instanceof ClassOrInterface) {
            for (PhasedUnit unit: CeylonBuilder.getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    //TODO: "object" declarations?
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(typeDec);
                    unit.getCompilationUnit().visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    Tree.Body body=null;
                    if (decNode instanceof Tree.ClassDefinition) {
                        body = ((Tree.ClassDefinition) decNode).getClassBody();
                    }
                    else if (decNode instanceof Tree.InterfaceDefinition){
                        body = ((Tree.InterfaceDefinition) decNode).getInterfaceBody();
                    }
                    else if (decNode instanceof Tree.ObjectDefinition){
                        body = ((Tree.ObjectDefinition) decNode).getClassBody();
                    }
                    if (body!=null) {
                        addCreateMemberProposal(proposals, def, desc, image, typeDec, unit, 
                                decNode, body);
                        break;
                    }
                }
            }
        }
    }

    private void addCreateLocalProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Tree.CompilationUnit cu, Tree.StaticMemberOrTypeExpression smte) {
        for (PhasedUnit unit: CeylonBuilder.getUnits(project)) {
            if (unit.getUnit().equals(cu.getUnit())) {
                FindStatementVisitor fsv = new FindStatementVisitor(smte, false);
                cu.visit(fsv);
                Tree.Statement statement = fsv.getStatement();
                addCreateProposal(proposals, def, true, desc, image, unit, statement);
                break;
            }
        }
    }

    private void addCreateToplevelProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Tree.CompilationUnit cu, Tree.StaticMemberOrTypeExpression smte) {
        for (PhasedUnit unit: CeylonBuilder.getUnits(project)) {
            if (unit.getUnit().equals(cu.getUnit())) {
                FindStatementVisitor fsv = new FindStatementVisitor(smte, true);
                cu.visit(fsv);
                Tree.Statement statement = fsv.getStatement();
                addCreateProposal(proposals, def+"\n", false, desc, image, unit, statement);
                break;
            }
        }
    }

    private void appendNamedArgs(FindArgumentsVisitor fav, StringBuilder params) {
        for (Tree.NamedArgument a: fav.namedArgs.getNamedArguments()) {
            if (a instanceof Tree.SpecifiedArgument) {
                Tree.SpecifiedArgument na = (Tree.SpecifiedArgument) a;
                params.append( na.getSpecifierExpression().getExpression().getTypeModel()
                            .getProducedTypeName() )
                    .append(" ")
                    .append(na.getIdentifier().getText());
                params.append(", ");
            }
        }
    }

    private void appendPositionalArgs(FindArgumentsVisitor fav, StringBuilder params) {
        for (Tree.PositionalArgument pa: fav.positionalArgs.getPositionalArguments()) {
            params.append( pa.getExpression().getTypeModel().getProducedTypeName() )
                .append(" ");
            if ( pa.getExpression().getTerm() instanceof Tree.StaticMemberOrTypeExpression ) {
                params.append( ((Tree.StaticMemberOrTypeExpression) pa.getExpression().getTerm())
                        .getIdentifier().getText() );
            }
            else {
                int loc = params.length();
                params.append( pa.getExpression().getTypeModel().getDeclaration().getName() );
                params.setCharAt(loc, Character.toLowerCase(params.charAt(loc)));
            }
            params.append(", ");
        }
    }

    private void addCreateToplevelProposal(Collection<ICompletionProposal> proposals, final String def,
            final String desc, final Image image, final IFile file, final String unitName) {
        proposals.add(new ICompletionProposal() {
			@Override
			public Point getSelection(IDocument doc) {
				return null;
			}
			@Override
			public Image getImage() {
				return image;
			}
			@Override
			public String getDisplayString() {
				return "Create toplevel " + desc + " in new unit";
			}
			@Override
			public IContextInformation getContextInformation() {
				return null;
			}
			@Override
			public String getAdditionalProposalInfo() {
				return null;
			}
			@Override
			public void apply(IDocument doc) {
				NewUnitWizard.open(def, file, unitName, 
				        "Create Missing Declaration in New Unit",
				        "Create a new Ceylon compilation unit with the missing declaration.");
			}
		});
    }

    private void addCreateMemberProposal(Collection<ICompletionProposal> proposals, String def,
            String desc, Image image, Declaration typeDec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.Body body) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange("Create Member", file);
        IDocument doc;
        try {
            doc = change.getCurrentDocument(null);
        }
        catch (CoreException e) {
            throw new RuntimeException(e);
        }
        String indent;
        String indentAfter;
        int offset;
        List<Statement> statements = body.getStatements();
        if (statements.isEmpty()) {
            indentAfter = "\n" + getIndent(decNode, doc);
            indent = indentAfter + getDefaultIndent();
            offset = body.getStartIndex()+1;
        }
        else {
            Statement statement = statements.get(statements.size()-1);
            indent = "\n" + getIndent(statement, doc);
            offset = statement.getStopIndex()+1;
            indentAfter = "";
        }
        change.setEdit(new InsertEdit(offset, indent+def+indentAfter));
        proposals.add(createCreateProposal(def, 
                "Create " + desc + " in '" + typeDec.getName() + "'", 
                image, indent.length(), offset, file, change));
    }

    private void addCreateProposal(Collection<ICompletionProposal> proposals, String def,
            boolean local, String desc, Image image, PhasedUnit unit, Tree.Statement statement) {
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange(local ? "Create Local" : "Create Toplevel", file);
        IDocument doc;
        try {
            doc = change.getCurrentDocument(null);
        }
        catch (CoreException e) {
            throw new RuntimeException(e);
        }
        String indent = getIndent(statement, doc);
        int offset = statement.getStartIndex();
        def = def.replace("$indent", indent);
        change.setEdit(new InsertEdit(offset, def+"\n"+indent));
        proposals.add(createCreateProposal(def, (local ? "Create local " : "Create toplevel ") + desc, 
                image, 0, offset, file, change));
    }

    private ChangeCorrectionProposal createCreateProposal(final String def, 
            String desc, Image image, final int indentLength, final int offset, 
            final IFile file, TextFileChange change) {
        return new ChangeCorrectionProposal(desc, 
                change, 50, image) {
            @Override
            public void apply(IDocument document) {
                super.apply(document);
                int loc = def.indexOf("bottom;");
                int len;
                if (loc<0) {
                    loc = def.indexOf("{}")+1;
                    len=0;
                }
                else {
                    len=6;
                }
                Util.gotoLocation(file, offset + loc + indentLength, len);
            }
        };
    }

    private void addRenameProposals(Tree.CompilationUnit cu, Node node, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file) {
          String brokenName = getIdentifyingNode(node).getText();
          if (brokenName.isEmpty()) return;
          for (DeclarationWithProximity dwp: getProposals(node, "", cu).values()) {
            int dist = getLevenshteinDistance(brokenName, dwp.getName()); //+dwp.getProximity()/3;
            //TODO: would it be better to just sort by dist, and
            //      then select the 3 closest possibilities?
            if (dist<=brokenName.length()/3+1) {
                TextFileChange change = new TextFileChange("Change Reference", file);
                change.setEdit(new ReplaceEdit(problem.getOffset(), 
                        brokenName.length(), dwp.getName())); //Note: don't use problem.getLength() because it's wrong from the problem list
                proposals.add(createRenameProposal(problem, file, dwp.getName(), 
                        dwp.getDeclaration(), dist, change));
            }
          }
    }

    private ChangeCorrectionProposal createRenameProposal(final ProblemLocation problem,
            final IFile file, final String name, Declaration dec, int dist,
            TextFileChange change) {
        return new ChangeCorrectionProposal("Change reference to '" + name + "'", 
                change, dist+10, CORRECTION/*CeylonLabelProvider.getImage(dec)*/) {
            @Override
            public void apply(IDocument document) {
                super.apply(document);
                Util.gotoLocation(file, problem.getOffset(), name.length());
            }
        };
    }
    
    private ChangeCorrectionProposal createSpecifyTypeProposal(final ProblemLocation problem,
            final IFile file, final String type, TextFileChange change) {
        return new ChangeCorrectionProposal("Specify type '" + type + "'", 
                change, 10, CORRECTION) {
            @Override
            public void apply(IDocument document) {
                super.apply(document);
                Util.gotoLocation(file, problem.getOffset(), type.length());
            }
        };
    }
    
    private ChangeCorrectionProposal createImplementFormalMembersProposal(final int loc,
            final IFile file, TextFileChange change) {
        return new ChangeCorrectionProposal("Refine formal members", 
                change, 10, FORMAL_REFINEMENT) {
            @Override
            public void apply(IDocument document) {
                super.apply(document);
                Util.gotoLocation(file, loc, 0);
            }
        };
    }
    
    private void addImportProposals(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IFile file) {
        if (node instanceof Tree.BaseMemberOrTypeExpression ||
                node instanceof SimpleType) {
            String brokenName = getIdentifyingNode(node).getText();
            Collection<Declaration> candidates = findImportCandidates(cu, brokenName);
            for (Declaration decl: candidates) {
                proposals.add(createImportProposal(cu, file, 
                        decl.getContainer().getQualifiedNameString(), 
                        decl.getName()));
            }
        }
    }

    private static Collection<Declaration> findImportCandidates(Tree.CompilationUnit cu, 
            String name) {
        List<Declaration> result = new ArrayList<Declaration>();
        Module currentModule = cu.getUnit().getPackage().getModule();
        addImportProposalsForModule(result, currentModule, name);
        return result;
    }

    private static void addImportProposalsForModule(List<Declaration> output,
            Module module, String name) {
        for (Package pkg: module.getAllPackages()) {
            Declaration member = pkg.getMember(name, null);
            if (member != null) {
                output.add(member);
            }
        }
    }

    private ICompletionProposal createImportProposal(Tree.CompilationUnit cu, IFile file,
            String packageName, String declaration) {
        
        Tree.Import importNode = findImportNode(cu, packageName);
        
        TextFileChange change = new TextFileChange("Add Import", file);
        TextEdit edit;

        if (importNode != null) {
            int insertPosition = getBestImportMemberInsertPosition(importNode, declaration);
            edit = new InsertEdit(insertPosition, ", " + declaration);
        } else {
            int insertPosition = getBestImportInsertPosition(cu);
            String text = "import " + packageName + " { " + declaration + " }";
            if (insertPosition==0) {
                text = text + "\n";
            }
            else {
                text = "\n" + text;
            }
            edit = new InsertEdit(insertPosition, text);
        }
        
        change.setEdit(edit);
        return new ChangeCorrectionProposal("Add import of '" + declaration + "'" + 
                " in package " + packageName, change, 50, CeylonLabelProvider.IMPORT);
    }
    
    private int getBestImportInsertPosition(CompilationUnit cu) {
        Integer stopIndex = cu.getImportList().getStopIndex();
        if (stopIndex == null) return 0;
        return stopIndex+1;
    }

    private static class FindImportNodeVisitor extends Visitor {
        private final String[] packageNameComponents;
        private Tree.Import result;
        
        public FindImportNodeVisitor(String packageName) {
            super();
            this.packageNameComponents = packageName.split("\\.");
        }
        
        public Tree.Import getResult() {
            return result;
        }

        public void visit(Tree.Import that) {
            if (result != null) {
                return;
            }

            List<Identifier> identifiers = that.getImportPath().getIdentifiers();
            if (identifiersEqual(identifiers, packageNameComponents)) {
                result = that;
            }
        }

        private static boolean identifiersEqual(List<Identifier> identifiers,
                String[] components) {
            if (identifiers.size() != components.length) {
                return false;
            }
            
            for (int i = 0; i < components.length; i++) {
                if (!identifiers.get(i).getText().equals(components[i])) {
                    return false;
                }
            }
            
            return true;
        }
    }

    private Import findImportNode(CompilationUnit cu, String packageName) {
        FindImportNodeVisitor visitor = new FindImportNodeVisitor(packageName);
        cu.visit(visitor);
        return visitor.getResult();
    }

    private int getBestImportMemberInsertPosition(Import importNode,
            String declaration) {
        return importNode.getImportMemberOrTypeList().getStopIndex() - 1;
    }

}
