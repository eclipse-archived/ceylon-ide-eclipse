package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.compiler.typechecker.model.Util.unionType;
import static com.redhat.ceylon.eclipse.code.editor.EditorAnnotationService.getRefinedDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ATTRIBUTE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CLASS;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.INTERFACE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.METHOD;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.PARAMETER;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getProposals;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.quickfix.Util.getLevenshteinDistance;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static org.eclipse.imp.parser.IMessageHandler.ERROR_CODE_KEY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.model.ValueParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrTypeList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Primary;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifiedArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypedArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer;
import com.redhat.ceylon.eclipse.util.FindContainerVisitor;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;
import com.redhat.ceylon.eclipse.util.FindStatementVisitor;

/**
 * Popup quick fixes for problem annotations displayed in editor
 * @author gavin
 */
public class CeylonQuickFixAssistant {

    public boolean canFix(Annotation annotation) {
        int code;
        if (annotation instanceof CeylonAnnotation) {
            code = ((CeylonAnnotation) annotation).getId();
        }
        else if (annotation instanceof MarkerAnnotation) {
            code = ((MarkerAnnotation) annotation).getMarker()
                   .getAttribute(ERROR_CODE_KEY, 0);
        }
        else {
            return false;
        }
        return code>0;
    }

    public boolean canAssist(IQuickFixInvocationContext context) {
        //oops, all this is totally useless, because
        //this method never gets called by IMP
        /*Tree.CompilationUnit cu = (CompilationUnit) context.getModel()
                .getAST(new NullMessageHandler(), new NullProgressMonitor());
        return CeylonSourcePositionLocator.findNode(cu, context.getOffset(), 
                context.getOffset()+context.getLength()) instanceof Tree.Term;*/
        return true;
    }

    public String[] getSupportedMarkerTypes() {
        return new String[] { PROBLEM_MARKER_ID };
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
    
    public void addProposals(IQuickAssistInvocationContext context, 
    		CeylonEditor editor, Collection<ICompletionProposal> proposals) {
        
        RenameRefactoringProposal.add(proposals, editor);
        InlineRefactoringProposal.add(proposals, editor);
        ExtractValueProposal.add(proposals, editor);
        ExtractFunctionProposal.add(proposals, editor);
        ConvertToClassProposal.add(proposals, editor);
        ConvertToNamedArgumentsProposal.add(proposals, editor);
        
        IDocument doc = context.getSourceViewer().getDocument();
        IProject project = Util.getProject(editor.getEditorInput());
        IFile file = Util.getFile(editor.getEditorInput());
        Tree.CompilationUnit cu = (Tree.CompilationUnit) editor.getParseController().getCurrentAst();
        if (cu!=null) {
            Node node = findNode(cu, context.getOffset(), 
                    context.getOffset() + context.getLength());
            AssignToLocalProposal.addAssignToLocalProposal(context.getSourceViewer().getDocument(),
                    file, cu, proposals, node);
            if (node instanceof Tree.Declaration) {
                Declaration d = ((Tree.Declaration) node).getDeclarationModel();
                if ((d.isClassOrInterfaceMember()||d.isToplevel()) && 
                        !d.isShared() && 
                        !(d instanceof Parameter)) {
                    addMakeSharedDecProposal(proposals, project, node);
                }
                if (d.isClassOrInterfaceMember() && 
                        d.isShared() &&
                        !d.isDefault() && !d.isFormal() &&
                        !(d instanceof Interface) && 
                        !(d instanceof Parameter)) {
                    addMakeDefaultDecProposal(proposals, project, node);
                }
            }
            if (node instanceof Tree.TypedDeclaration && 
                    !(node instanceof Tree.ObjectDefinition) &&
                    !(node instanceof Tree.Variable) &&
                    !(node instanceof Tree.Parameter)) {
                Type type = ((Tree.TypedDeclaration) node).getType();
                if (type instanceof Tree.LocalModifier) {
                    SpecifyTypeProposal.addSpecifyTypeProposal(cu, type, proposals, file);
                }
            }
            if (node instanceof Tree.LocalModifier) {
                SpecifyTypeProposal.addSpecifyTypeProposal(cu, node, proposals, file);
            }
            if (node instanceof Tree.AttributeDeclaration) {
                Tree.AttributeDeclaration dec = (Tree.AttributeDeclaration) node;
                if (dec.getSpecifierOrInitializerExpression()!=null) {
                    SplitDeclarationProposal.addSplitDeclarationProposal(doc, cu, proposals, 
                            file, dec);
                    ConvertToGetterProposal.addConvertToGetterProposal(doc, proposals, 
                            file, dec);
                }
                AddParameterProposal.addParameterProposal(doc, cu, proposals, 
                        file, dec, editor);
            }
            CreateObjectProposal.addCreateObjectProposal(doc, cu, proposals, file, node);
            CreateLocalSubtypeProposal.addCreateLocalSubtypeProposal(doc, cu, proposals, file, node);

            Statement statement = findStatement(cu, node);
            ConvertThenElseToIfElse.addConvertToGetterProposal(doc, proposals, file, statement);
        }

        CreateSubtypeProposal.add(proposals, editor);
        MoveDeclarationProposal.add(proposals, editor);
        
        RefineFormalMembersProposal.add(proposals, editor);
    }
    
    public void addProposals(IQuickFixInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals) {
        if (context.getModel()==null) return;
        IProject project = context.getModel().getProject().getRawProject();
        IFile file = context.getModel().getFile();
        TypeChecker tc = getProjectTypeChecker(project);
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
            case 102:
                if (tc!=null) {
                    addImportProposals(cu, node, proposals, file);
                }
                addCreateEnumProposal(cu, node, problem, proposals, 
                        project, tc, file);
                addCreateProposals(cu, node, problem, proposals, 
                        project, tc, file);
                if (tc!=null) {
                    addRenameProposals(cu, node, problem, proposals, file);
                }
                break;
            case 101:
                addCreateParameterProposals(cu, node, problem, proposals, 
                        project, tc, file);
                if (tc!=null) {
                    addRenameProposals(cu, node, problem, proposals, file);
                }
                break;
            case 200:
                SpecifyTypeProposal.addSpecifyTypeProposal(cu, node, proposals, file);
                break;
            case 300:
                if (context.getSourceViewer()!=null) { //TODO: figure out some other way to get the Document!
                    ImplementFormalMembersProposal.addImplementFormalMembersProposal(cu, node, proposals, file,
                            context.getSourceViewer().getDocument());
                }
                break;
            case 400:
                addMakeSharedProposal(proposals, project, node);
                break;
            case 500:
                addMakeDefaultProposal(proposals, project, node);
                break;
            case 600:
                addMakeActualProposal(proposals, project, node);
                break;
            case 701:
                addMakeSharedDecProposal(proposals, project, node);
                addRemoveAnnotationDecProposal(proposals, "actual", project, node);
                break;
            case 702:
                addMakeSharedDecProposal(proposals, project, node);
                addRemoveAnnotationDecProposal(proposals, "formal", project, node);
                break;
            case 703:
                addMakeSharedDecProposal(proposals, project, node);
                addRemoveAnnotationDecProposal(proposals, "default", project, node);
                break;
            case 800:
            case 804:
                addMakeVariableProposal(proposals, project, node);
                break;
            case 803:
                addMakeVariableProposal(proposals, project, node);
                addFixSpecificationProposal(problem, proposals, project, file, node);
                break;
            case 801:
                addMakeVariableDecProposal(cu, proposals, project, node);
                addFixSpecificationProposal(problem, proposals, project, file, node);
                break;
            case 802:
                addFixAssignmentProposal(problem, proposals, project, file, node);
                break;
            case 900:
                addMakeAbstractProposal(proposals, project, node);
                break;
            case 1000:
                AddParenthesesProposal.addAddParenthesesProposal(problem, file, proposals, node);
                ChangeDeclarationProposal.addChangeDeclarationProposal(problem, file, proposals, node);
                break;
            case 1100:
                addRemoveAnnotationDecProposal(proposals, "formal", project, node);
                break;
            case 1200:
            case 1201:
                addRemoveAnnotationDecProposal(proposals, "shared", project, node);
                break;
            case 1300:
            case 1301:
                addRemoveAnnotationDecProposal(proposals, "actual", project, node);
                break;
            case 1302:
            case 1312:
                addRemoveAnnotationDecProposal(proposals, "formal", project, node);
                break;
            case 1303:
            case 1313:
                addRemoveAnnotationDecProposal(proposals, "default", project, node);
                break;
            case 1400:
                addMakeFormalProposal(proposals, project, node);
                break;
            case 1500:
                addRemoveAnnotationDecProposal(proposals, "variable", project, node);
                break;
            case 1600:
                addRemoveAnnotationDecProposal(proposals, "abstract", project, node);
                break;
            case 2000:
                addCreateParameterProposals(cu, node, problem, proposals, 
                        project, tc, file);
                break;
            case 2100:
                addChangeTypeProposals(cu, node, problem, proposals, project);
                break;
            case 3000:
                if (context.getSourceViewer()!=null) {
                    AssignToLocalProposal.addAssignToLocalProposal(context.getSourceViewer().getDocument(),
                            file, cu, proposals, node);
                }
                break;
            case 3100:
                if (context.getSourceViewer()!=null) {
                    ShadowReferenceProposal.addShadowReferenceProposal(context.getSourceViewer().getDocument(),
                            file, cu, proposals, node);
                }
                break;
            }
        }
    }

    private void addMakeActualProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        boolean shared = decNode.getDeclarationModel().isShared();
        addAddAnnotationProposal(node, shared ? "actual " : "shared actual ", 
                shared ? "Make Actual" : "Make Shared Actual",
                decNode.getDeclarationModel(), proposals, project);
    }

    private void addMakeDefaultProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        Declaration d = getRefinedDeclaration(decNode.getDeclarationModel()); //TODO: this is wrong!
        if (d==null) d = decNode.getDeclarationModel();
        addAddAnnotationProposal(node, "default ", "Make Default", d, 
                proposals, project);
    }

    private void addMakeDefaultDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        Declaration d = decNode.getDeclarationModel();
        addAddAnnotationProposal(node, "default ", "Make Default", d, 
                proposals, project);
    }

    private void addMakeFormalProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        boolean shared = decNode.getDeclarationModel().isShared();
        addAddAnnotationProposal(node, shared ? "formal " : "shared formal ", 
                shared ? "Make Formal" : "Make Shared Formal",
                decNode.getDeclarationModel(), proposals, project);
    }

    private void addMakeAbstractProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            dec = (Declaration) ((Tree.Declaration) node).getDeclarationModel().getContainer();
        }
        else {
            dec = (Declaration) node.getScope();
        }
        addAddAnnotationProposal(node, "abstract ", "Make Abstract", dec, 
                proposals, project);
    }

    private void addMakeVariableProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Term term;
        if (node instanceof Tree.AssignmentOp) {
            term = ((Tree.AssignOp) node).getLeftTerm();
        }
        else if (node instanceof Tree.UnaryOperatorExpression) {
            term = ((Tree.PrefixOperatorExpression) node).getTerm();
        }
        else if (node instanceof Tree.MemberOrTypeExpression) {
            term = (Tree.MemberOrTypeExpression) node;
        }
        else {
            return;
        }
        Declaration dec = ((Tree.MemberOrTypeExpression) term).getDeclaration();
        addAddAnnotationProposal(node, "variable ", "Make Variable", 
                dec, proposals, project);
    }
    
    private void addMakeVariableDecProposal(Tree.CompilationUnit cu,
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
        addAddAnnotationProposal(node, "variable ", "Make Variable", v.dec, 
                proposals, project);
    }
    
    private void addFixSpecificationProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, IFile file, Node node) {
        addFixAssignmentProposal(proposals, "=", ((CommonToken) node.getMainToken()).getStartIndex(), file);
    }
    
    private void addFixAssignmentProposal(ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project, IFile file, Node node) {
        addFixAssignmentProposal(proposals, ":=", ((CommonToken) node.getMainToken()).getStartIndex(), file);
    }
    
    private void addFixAssignmentProposal(Collection<ICompletionProposal> proposals, 
            String op, int offset, IFile file) {
        String desc = "Change to '" + op + "'";
        TextFileChange change = new TextFileChange(desc, file);
        change.setEdit(new ReplaceEdit(offset, op.length()==1 ? 2 : 1, op));
        proposals.add(new ChangeCorrectionProposal(desc, change, 10, CORRECTION));
    }

    private void addMakeSharedProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec = null;
        if (node instanceof Tree.StaticMemberOrTypeExpression) {
            Tree.StaticMemberOrTypeExpression qmte = (Tree.StaticMemberOrTypeExpression) node;
            dec = qmte.getDeclaration();
        }
        else if (node instanceof Tree.SimpleType) {
            Tree.SimpleType qmte = (Tree.SimpleType) node;
            dec = qmte.getDeclarationModel();
        }
        else if (node instanceof Tree.ImportMemberOrType) {
            Tree.ImportMemberOrType imt = (Tree.ImportMemberOrType) node;
            dec = imt.getDeclarationModel();
        }
        if (dec!=null) {
            addAddAnnotationProposal(node, "shared ", "Make Shared", dec, 
                    proposals, project);
        }
    }
    
    private void addMakeSharedDecProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        addAddAnnotationProposal(node, "shared ", "Make Shared",  
                decNode.getDeclarationModel(), proposals, project);
    }
    
    private void addRemoveAnnotationDecProposal(Collection<ICompletionProposal> proposals, 
            String annotation, IProject project, Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        addRemoveAnnotationProposal(node, annotation, "Make Non" + annotation,  
                decNode.getDeclarationModel(), proposals, project);
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
    
    private void addCreateEnumProposal(Tree.CompilationUnit cu, Node node, 
        ProblemLocation problem, Collection<ICompletionProposal> proposals, 
        IProject project, TypeChecker tc, IFile file) {
        String brokenName = getIdentifyingNode(node).getText();
        if (brokenName.isEmpty()) return;
        //Tree.BaseType bt = (Tree.BaseType) node;
        FindContainerVisitor fdv = new FindContainerVisitor(node);
        fdv.visit(cu);
        Tree.Declaration dec = fdv.getDeclaration();
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
                    result.append(p.getType().getTypeModel().getProducedTypeName()) 
                            .append(" ")
                            .append(p.getIdentifier().getText());
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
                    result.append(p.getIdentifier().getText());
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
    
    private void addCreateProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
        if (node instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression smte = (Tree.MemberOrTypeExpression) node;

            String brokenName = getIdentifyingNode(node).getText();
            if (brokenName.isEmpty()) return;
            boolean isUpperCase = Character.isUpperCase(brokenName.charAt(0));
            String def;
            String desc;
            Image image;
            FindArgumentsVisitor fav = new FindArgumentsVisitor(smte);
            cu.visit(fav);
            ProducedType t = fav.expectedType;
            final boolean isVoid = t==null;
            String stn = isVoid ? null : node.getUnit().denotableType(t).getProducedTypeName();
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
                        if (!stn.equals("unknown")) {
                            if (t.getDeclaration() instanceof Class) {
                                supertype = " extends " + stn + "()"; //TODO: arguments!
                            }
                            else {
                                supertype = " satisfies " + stn;
                            }
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
                    String type = isVoid ? "void" : 
                        stn.equals("unknown") ? "function" : stn;
                    String impl = isVoid ? " {}" : " { return bottom; }";
                    def = type + " " + brokenName + params + impl;
                    desc = "function '" + brokenName + params + "'";
                    image = METHOD;
                }
            }
            else if (!isUpperCase) {
                String type = isVoid ? "Void" : 
                    stn.equals("unknown") ? "value" : stn;
                def = type + " " + brokenName + " = bottom;";
                desc = "value '" + brokenName + "'";
                image = ATTRIBUTE;
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
                    do {
                        addCreateMemberProposals(proposals, project, def, desc, image, container);
                        if(container.getContainer() instanceof Declaration)
                            container = findClassContainer((Declaration) container.getContainer());
                        else 
                            break;
                    }
                    while(container != null);
                }
                addCreateToplevelProposals(proposals, project, def, desc, image, cu, smte);
                CreateInNewUnitProposal.addCreateToplevelProposal(proposals, def.replace("$indent", ""), 
                        desc, image, file, brokenName);
            }
            
        }
        else if (node instanceof Tree.BaseType) {
            Tree.BaseType bt = (Tree.BaseType) node;
            String brokenName = bt.getIdentifier().getText();
            String idef = "interface " + brokenName + " {}";
            String idesc = "interface '" + brokenName + "'";
            String cdef = "class " + brokenName + "() {}";
            String cdesc = "class '" + brokenName + "()'";
            //addCreateLocalProposals(proposals, project, idef, idesc, INTERFACE, cu, bt);
            addCreateLocalProposals(proposals, project, cdef, cdesc, CLASS, cu, bt);
            addCreateToplevelProposals(proposals, project, idef, idesc, INTERFACE, cu, bt);
            addCreateToplevelProposals(proposals, project, cdef, cdesc, CLASS, cu, bt);
            CreateInNewUnitProposal.addCreateToplevelProposal(proposals, idef, idesc, 
                    INTERFACE, file, brokenName);
            CreateInNewUnitProposal.addCreateToplevelProposal(proposals, cdef, cdesc, 
                    CLASS, file, brokenName);
        }
    }

    private ClassOrInterface findClassContainer(Tree.CompilationUnit cu, Node n){
        FindContainerVisitor visitor = new FindContainerVisitor(n);
        visitor.visit(cu);
        Tree.Declaration declaration = visitor.getDeclaration();
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

    private void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Tree.QualifiedMemberOrTypeExpression qmte) {
        Declaration typeDec = ((Tree.QualifiedMemberOrTypeExpression) qmte).getPrimary()
                .getTypeModel().getDeclaration();
        addCreateMemberProposals(proposals, project, def, desc, image, typeDec);
    }
    
    private void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, Declaration typeDec) {
        if (typeDec!=null && typeDec instanceof ClassOrInterface) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    //TODO: "object" declarations?
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(typeDec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    Tree.Body body = getBody(decNode);
                    if (body!=null) {
                        CreateProposal.addCreateMemberProposal(proposals, def, desc, 
                                image, typeDec, unit, decNode, body);
                        break;
                    }
                }
            }
        }
    }

    private void addChangeTypeProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project) {
        if (node instanceof Tree.SpecifierExpression) {
            Expression e = ((Tree.SpecifierExpression) node).getExpression();
            if (e!=null) {
                node = e.getTerm();
            }
        }
        if (node instanceof Tree.Expression) {
            node = ((Tree.Expression) node).getTerm();
        }
        if (node instanceof Tree.Term) {
            ProducedType type = node.getUnit().denotableType(((Tree.Term) node).getTypeModel());
            FindInvocationVisitor fav = new FindInvocationVisitor(node);
            fav.visit(cu);
            TypedDeclaration td = fav.parameter;
            if (td instanceof ValueParameter) {
                ValueParameter vp = (ValueParameter)td;
                if (vp.isHidden()) {
                    td = (TypedDeclaration) vp.getDeclaration()
                            .getMember(td.getName(), null);
                }
            }
            addChangeTypeProposals(proposals, problem, project, type, td);
        }
    }
    
    private void addChangeTypeProposals(Collection<ICompletionProposal> proposals,
            ProblemLocation problem, IProject project, ProducedType type, 
            TypedDeclaration typedDec) {
        if (typedDec!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typedDec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(typedDec);
                    getRootNode(unit).visit(fdv);
                    Tree.TypedDeclaration decNode = (Tree.TypedDeclaration) fdv.getDeclarationNode();
                    if (decNode!=null) {
                        Tree.Type typeNode = decNode.getType();
                        ProducedType newType = unionType(typeNode.getTypeModel(), type, unit.getUnit());
                        ChangeTypeProposal.addChangeTypeProposal(typeNode, problem, proposals, typedDec, 
                                newType, getFile(unit));
                    }
                }
            }
        }
    }

    private void addCreateParameterProposals(Tree.CompilationUnit cu, Node node, 
            ProblemLocation problem, Collection<ICompletionProposal> proposals, 
            IProject project, TypeChecker tc, IFile file) {
        FindInvocationVisitor fav = new FindInvocationVisitor(node);
        fav.visit(cu);
        Primary prim = fav.result.getPrimary();
        if (prim instanceof Tree.BaseMemberOrTypeExpression) {
            ProducedReference pr = ((Tree.BaseMemberOrTypeExpression) prim).getTarget();
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
                    SpecifiedArgument sa = (Tree.SpecifiedArgument) node;
                    SpecifierExpression se = sa.getSpecifierExpression();
                    if (se!=null && se.getExpression()!=null) {
                        t = se.getExpression().getTypeModel();
                    }
                    n = sa.getIdentifier().getText();
                }
                else if (node instanceof Tree.TypedArgument) {
                    TypedArgument ta = (Tree.TypedArgument) node;
                    t = ta.getType().getTypeModel();
                    n = ta.getIdentifier().getText();
                }
                if (t!=null && n!=null) {
                    t = node.getUnit().denotableType(t);
                    String dv = defaultValue(prim.getUnit(), t);
                    String tn = t.getProducedTypeName();
                    String def = tn + " " + n + " = " + dv;
                    String desc = "parameter '" + n +"'";
                    addCreateParameterProposals(proposals, project, def, desc, d);
                    String pdef = n + " = " + dv;
                    String adef = tn + " " + n + ";";
                    String padesc = "attribute '" + n +"'";
                    addCreateParameterAndAttributeProposals(proposals, project, 
                            pdef, adef, padesc, d);
                }
            }
        }
    }

    private static String defaultValue(Unit unit, ProducedType t) {
        String tn = t.getProducedTypeQualifiedName();
        if (tn.equals("ceylon.language.Boolean")) {
            return "false";
        }
        else if (tn.equals("ceylon.language.Integer")) {
            return "0";
        }
        else if (tn.equals("ceylon.language.Float")) {
            return "0.0";
        }
        else if (unit.isOptionalType(t)) {
            return "null";
        }
        else if (tn.equals("ceylon.language.String")) {
            return "\"\"";
        }
        else {
            return "bottom";
        }
    }
    
    private void addCreateParameterProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Declaration typeDec) {
        if (typeDec!=null && typeDec instanceof ClassOrInterface) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(typeDec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    Tree.ParameterList paramList = getParameters(decNode);
                    if (paramList!=null) {
                        if (!paramList.getParameters().isEmpty()) {
                            def = ", " + def;
                        }
                        CreateProposal.addCreateParameterProposal(proposals, def, desc, 
                                PARAMETER, typeDec, unit, decNode, paramList);
                        break;
                    }
                }
            }
        }
    }

    private void addCreateParameterAndAttributeProposals(Collection<ICompletionProposal> proposals,
            IProject project, String pdef, String adef, String desc, Declaration typeDec) {
        if (typeDec!=null && typeDec instanceof ClassOrInterface) {
            for (PhasedUnit unit: getUnits(project)) {
                if (typeDec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(typeDec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    Tree.ParameterList paramList = getParameters(decNode);
                    Tree.Body body = getBody(decNode);
                    if (body!=null && paramList!=null) {
                        if (!paramList.getParameters().isEmpty()) {
                            pdef = ", " + pdef;
                        }
                        CreateProposal.addCreateParameterAndAttributeProposal(proposals, pdef, 
                                adef, desc, ATTRIBUTE, typeDec, unit, decNode, 
                                paramList, body);
                    }
                }
            }
        }
    }

    private Tree.CompilationUnit getRootNode(PhasedUnit unit) {
        IEditorPart ce = Util.getCurrentEditor();
        if (ce instanceof CeylonEditor) {
            CeylonParseController cpc = ((CeylonEditor) ce).getParseController();
            if (cpc!=null) {
                CompilationUnit rn = cpc.getRootNode();
                if (rn!=null) {
                    Unit u = rn.getUnit();
                    if (u.equals(unit.getUnit())) {
                        return rn;
                    }
                }
            }
        }       
        return unit.getCompilationUnit();
    }

    private static Tree.Body getBody(Tree.Declaration decNode) {
        if (decNode instanceof Tree.ClassDefinition) {
            return ((Tree.ClassDefinition) decNode).getClassBody();
        }
        else if (decNode instanceof Tree.InterfaceDefinition){
            return ((Tree.InterfaceDefinition) decNode).getInterfaceBody();
        }
        else if (decNode instanceof Tree.ObjectDefinition){
            return ((Tree.ObjectDefinition) decNode).getClassBody();
        }
        return null;
    }

    private static Tree.ParameterList getParameters(Tree.Declaration decNode) {
        if (decNode instanceof Tree.AnyClass) {
            return ((Tree.AnyClass) decNode).getParameterList();
        }
        else if (decNode instanceof Tree.AnyMethod){
            List<ParameterList> pls = ((Tree.AnyMethod) decNode).getParameterLists();
            return pls.isEmpty() ? null : pls.get(0);
        }
        return null;
    }

    private void addCreateEnumProposal(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Tree.CompilationUnit cu, Tree.TypeDeclaration cd) {
        for (PhasedUnit unit: getUnits(project)) {
            if (unit.getUnit().equals(cu.getUnit())) {
                CreateProposal.addCreateEnumProposal(proposals, def, desc, image, unit, cd);
                break;
            }
        }
    }

    private void addCreateLocalProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Tree.CompilationUnit cu, Node node) {
        FindStatementVisitor fsv = new FindStatementVisitor(node, false);
        cu.visit(fsv);
        if (!fsv.isToplevel()) {
            Tree.Statement statement = fsv.getStatement();
            for (PhasedUnit unit: getUnits(project)) {
                if (unit.getUnit().equals(cu.getUnit())) {
                    CreateProposal.addCreateProposal(proposals, def, true, desc, image, unit, statement);
                    break;
                }
            }
        }
    }

    private void addCreateToplevelProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, 
            Tree.CompilationUnit cu, Node node) {
        FindStatementVisitor fsv = new FindStatementVisitor(node, true);
        cu.visit(fsv);
        Tree.Statement statement = fsv.getStatement();
        for (PhasedUnit unit: getUnits(project)) {
            if (unit.getUnit().equals(cu.getUnit())) {
                CreateProposal.addCreateProposal(proposals, def+"\n", false, desc, image, unit, statement);
                break;
            }
        }
    }

    private void appendNamedArgs(FindArgumentsVisitor fav, StringBuilder params) {
        for (Tree.NamedArgument a: fav.namedArgs.getNamedArguments()) {
            if (a instanceof Tree.SpecifiedArgument) {
                Tree.SpecifiedArgument na = (Tree.SpecifiedArgument) a;
                params.append( a.getUnit().denotableType(na.getSpecifierExpression().getExpression().getTypeModel())
                            .getProducedTypeName() )
                    .append(" ")
                    .append(na.getIdentifier().getText());
                params.append(", ");
            }
        }
    }

    private void appendPositionalArgs(FindArgumentsVisitor fav, StringBuilder params) {
        for (Tree.PositionalArgument pa: fav.positionalArgs.getPositionalArguments()) {
            params.append( pa.getUnit().denotableType(pa.getExpression().getTypeModel()).getProducedTypeName() )
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

    private void addRenameProposals(Tree.CompilationUnit cu, Node node, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file) {
          String brokenName = getIdentifyingNode(node).getText();
          if (brokenName.isEmpty()) return;
          for (DeclarationWithProximity dwp: getProposals(node, cu).values()) {
              int dist = getLevenshteinDistance(brokenName, dwp.getName()); //+dwp.getProximity()/3;
              //TODO: would it be better to just sort by dist, and
              //      then select the 3 closest possibilities?
              if (dist<=brokenName.length()/3+1) {
                  RenameProposal.addRenameProposal(problem, proposals, file, brokenName, dwp,
                        dist);
              }
          }
    }

    private void addImportProposals(Tree.CompilationUnit cu, Node node,
            Collection<ICompletionProposal> proposals, IFile file) {
        if (node instanceof Tree.BaseMemberOrTypeExpression ||
                node instanceof Tree.SimpleType) {
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
            String text;
            ImportMemberOrTypeList imtl = importNode.getImportMemberOrTypeList();
            if (imtl.getImportWildcard()!=null) {
                text = declaration + ", ";
            }
            else if (imtl.getImportMemberOrTypes().isEmpty()) {
                text = declaration;
            }
            else {
                text = ", " + declaration;
            }
            edit = new InsertEdit(insertPosition, text);
        } 
        else {
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
    
    private int getBestImportInsertPosition(Tree.CompilationUnit cu) {
        Integer stopIndex = cu.getImportList().getStopIndex();
        if (stopIndex == null) return 0;
        return stopIndex+1;
    }

    private Tree.Import findImportNode(Tree.CompilationUnit cu, String packageName) {
        FindImportNodeVisitor visitor = new FindImportNodeVisitor(packageName);
        cu.visit(visitor);
        return visitor.getResult();
    }

    private int getBestImportMemberInsertPosition(Tree.Import importNode,
            String declaration) {
        ImportMemberOrTypeList imtl = importNode.getImportMemberOrTypeList();
        if (imtl.getImportWildcard()!=null) {
            return imtl.getImportWildcard().getStartIndex();
        }
        else {
            List<ImportMemberOrType> imts = imtl.getImportMemberOrTypes();
            if (imts.isEmpty()) {
                return imtl.getStartIndex()+1;
            }
            else {
                return imts.get(imts.size()-1).getStopIndex()+1;
            }
        }
    }

    private void addAddAnnotationProposal(Node node, String annotation, String desc,
            Declaration dec, Collection<ICompletionProposal> proposals, IProject project) {
        if (dec!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(dec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    if (decNode!=null) {
                        AddAnnotionProposal.addAddAnnotationProposal(annotation, desc, dec,
                                proposals, unit, decNode);
                    }
                    break;
                }
            }
        }
    }

    private void addRemoveAnnotationProposal(Node node, String annotation, String desc,
            Declaration dec, Collection<ICompletionProposal> proposals, IProject project) {
        if (dec!=null) {
            for (PhasedUnit unit: getUnits(project)) {
                if (dec.getUnit().equals(unit.getUnit())) {
                    //TODO: "object" declarations?
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(dec);
                    getRootNode(unit).visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    if (decNode!=null) {
                        RemoveAnnotionProposal.addRemoveAnnotationProposal(annotation, desc, dec,
                                proposals, unit, decNode);
                    }
                    break;
                }
            }
        }
    }

}
