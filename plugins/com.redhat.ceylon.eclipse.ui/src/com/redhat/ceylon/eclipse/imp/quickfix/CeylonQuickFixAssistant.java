package com.redhat.ceylon.eclipse.imp.quickfix;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.getIndent;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.imp.editor.quickfix.IAnnotation;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.services.IQuickFixAssistant;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;
import com.redhat.ceylon.eclipse.util.Util;

/**
 * Popup quick fixes for problem annotations displayed in editor
 * @author gavin
 */
public class CeylonQuickFixAssistant implements IQuickFixAssistant {

    @Override
    public boolean canFix(Annotation annotation) {
        return annotation instanceof IAnnotation 
                && ((IAnnotation) annotation).getId()==100 ||
               annotation instanceof MarkerAnnotation && 
               ((MarkerAnnotation) annotation).getMarker()
                   .getAttribute(IMessageHandler.ERROR_CODE_KEY, -666)==100;
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

    @Override
    public void addProposals(IQuickFixInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals) {
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
                addCreateMemberProposals(cu, node, problem, proposals, project);
                if (tc!=null) {
                    addRenameProposals(cu, node, problem, proposals, file, tc);
                }
                break;
            }
        }
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
    
    static class FindArgumentsVisitor extends Visitor 
            implements NaturalVisitor {
        Tree.StaticMemberOrTypeExpression smte;
        Tree.NamedArgumentList namedArgs;
        Tree.PositionalArgumentList positionalArgs;
        ProducedType currentType;
        ProducedType expectedType;
        boolean found = false;
        FindArgumentsVisitor(Tree.StaticMemberOrTypeExpression smte) {
            this.smte = smte;
        }
        
        @Override
        public void visit(Tree.StaticMemberOrTypeExpression that) {
            super.visit(that);
            if (that==smte) {
                expectedType = currentType;
                found = true;
            }
        }
        
        @Override
        public void visit(Tree.InvocationExpression that) {
            super.visit(that);
            if (that.getPrimary()==smte) {
                namedArgs = that.getNamedArgumentList();
                positionalArgs = that.getPositionalArgumentList();
            }
        }
        @Override
        public void visit(Tree.NamedArgument that) {
            currentType = that.getParameter().getType();
            super.visit(that);
            currentType = null;
        }
        @Override
        public void visit(Tree.PositionalArgument that) {
            currentType = that.getParameter().getType();
            super.visit(that);
            currentType = null;
        }
        @Override
        public void visit(Tree.AttributeDeclaration that) {
            currentType = that.getType().getTypeModel();
            super.visit(that);
            currentType = null;
        }
        @Override
        public void visit(Tree.Variable that) {
            currentType = that.getType().getTypeModel();
            super.visit(that);
            currentType = null;
        }
        @Override
        public void visit(Tree.ValueIterator that) {
            currentType = that.getVariable().getType().getTypeModel();
            super.visit(that);
            currentType = null;
        }
        @Override
        public void visit(Tree.SpecifierStatement that) {
            currentType = that.getBaseMemberExpression().getTypeModel();
            super.visit(that);
            currentType = null;
        }
        @Override
        public void visit(Tree.AssignmentOp that) {
            currentType = that.getLeftTerm().getTypeModel();
            super.visit(that);
            currentType = null;
        }
        @Override
        public void visit(Tree.Return that) {
            if (that.getDeclaration() instanceof TypedDeclaration) {
                currentType = ((TypedDeclaration) that.getDeclaration()).getType();
            }
            super.visit(that);
            currentType = null;
        }
        @Override
        public void visit(Tree.Throw that) {
            super.visit(that);
            //set expected type to Exception
        }
        @Override
        public void visitAny(Node that) {
            if (!found) super.visitAny(that);
        }
    }

    private void addCreateMemberProposals(Tree.CompilationUnit cu, Node node, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project) {
        if (node instanceof Tree.QualifiedMemberOrTypeExpression) {
            Tree.QualifiedMemberOrTypeExpression qmte = (Tree.QualifiedMemberOrTypeExpression) node;
            Declaration typeDec = qmte.getPrimary().getTypeModel().getDeclaration();
            if (typeDec!=null && typeDec instanceof ClassOrInterface) {
                String brokenName = getIdentifyingNode(node).getText();
                String def;
                String desc;
                Image image;
                FindArgumentsVisitor fav = new FindArgumentsVisitor(qmte);
                cu.visit(fav);
                if (fav.positionalArgs!=null || fav.namedArgs!=null) {
                    StringBuilder params = new StringBuilder();
                    params.append("(");
                    if (fav.positionalArgs!=null) appendPositionalArgs(fav, params);
                    if (fav.namedArgs!=null) appendNamedArgs(fav, params);
                    if (params.length()>1) {
                        params.setLength(params.length()-2);
                    }
                    params.append(")");
                    if (Character.isUpperCase(brokenName.charAt(0))) {
                        String supertype = "";
                        if (fav.expectedType!=null) {
                            if (fav.expectedType.getDeclaration() instanceof Class) {
                                supertype = " extends " + fav.expectedType.getProducedTypeName() + "()";
                            }
                            else {
                                supertype = " satisfies " + fav.expectedType.getProducedTypeName();
                            }
                        }
                        def = "shared class " + brokenName + params + supertype + " {}";
                        desc = "class '" + brokenName + params + supertype + "'";
                        image = CeylonLabelProvider.CLASS;
                    }
                    else {
                        String type = fav.expectedType==null ? "Nothing" : 
                            fav.expectedType.getProducedTypeName();
                        def = "shared " + type + " " + brokenName + params + " { return null; }";
                        desc = "function '" + brokenName + params + "'";
                        image = CeylonLabelProvider.METHOD;
                    }
                }
                else {
                    String type = fav.expectedType==null ? "Nothing" : 
                        fav.expectedType.getProducedTypeName();
                    def = "shared " + type + " " + brokenName + " = null;";
                    desc = "value '" + brokenName + "'";
                    image = CeylonLabelProvider.ATTRIBUTE;
                }
                addCreateMemberProposals(proposals, project, def, desc, image, typeDec);
            }
        }
    }

    private void addCreateMemberProposals(Collection<ICompletionProposal> proposals,
            IProject project, String def, String desc, Image image, Declaration typeDec) {
        for (PhasedUnit unit: CeylonBuilder.getUnits(project)) {
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
            if (body!=null) {
                addProposal(proposals, def, desc, image, typeDec, unit, decNode, body);
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

    private void addProposal(Collection<ICompletionProposal> proposals, String def,
            String desc, Image image, Declaration typeDec, PhasedUnit unit,
            Tree.Declaration decNode, Tree.Body body) {
        String indent;
        String indentAfter;
        int offset;
        if (!body.getStatements().isEmpty()) {
            Statement statement = body.getStatements()
                    .get(body.getStatements().size()-1);
            indent = getIndent(unit.getTokenStream(), statement);
            offset = statement.getStopIndex()+1;
            indentAfter = "";
        }
        else {
            indentAfter = getIndent(unit.getTokenStream(), decNode);
            indent = indentAfter + CeylonAutoEditStrategy.getDefaultIndent();
            offset = body.getStartIndex()+1;
        }
        IFile file = CeylonBuilder.getFile(unit);
        TextFileChange change = new TextFileChange("Add Member", file);
        change.setEdit(new InsertEdit(offset, indent+def+indentAfter));
        proposals.add(createCreateMemberProposal(def, desc, image, typeDec, indent, offset, file, change));
    }

    private ChangeCorrectionProposal createCreateMemberProposal(final String def, String desc, Image image,
            Declaration typeDec, final String indent, final int offset, final IFile file,
            TextFileChange change) {
        return new ChangeCorrectionProposal("Create " + 
                desc + " in '" + typeDec.getName() + "'", 
                change, 50, image) {
            @Override
            public void apply(IDocument document) {
                super.apply(document);
                int loc = def.indexOf("null;");
                int len;
                if (loc<0) {
                    loc = def.indexOf("{}")+1;
                    len=0;
                }
                else {
                    len=4;
                }
                Util.gotoLocation(file, offset + loc + indent.length(), len);
            }
        };
    }

    private void addRenameProposals(Tree.CompilationUnit cu, Node node, final ProblemLocation problem,
            Collection<ICompletionProposal> proposals, final IFile file, TypeChecker tc) {
          String brokenName = getIdentifyingNode(node).getText();
          for (Map.Entry<String,DeclarationWithProximity> entry: 
              CeylonContentProposer.getProposals(node, "", tc.getContext()).entrySet()) {
            String name = entry.getKey();
            DeclarationWithProximity dwp = entry.getValue();
            int dist = Util.getLevenshteinDistance(brokenName, name); //+dwp.getProximity()/3;
            //TODO: would it be better to just sort by dist, and
            //      then select the 3 closest possibilities?
            if (dist<=brokenName.length()/3+1) {
                TextFileChange change = new TextFileChange("Rename", file);
                change.setEdit(new ReplaceEdit(problem.getOffset(), 
                        brokenName.length(), name)); //TODO: don't use problem.getLength() because it's wrong from the problem list
                proposals.add(createRenameProposal(problem, file, name, dwp, dist, change));
            }
          }
    }

    private ChangeCorrectionProposal createRenameProposal(final ProblemLocation problem,
            final IFile file, final String name, DeclarationWithProximity dwp, int dist,
            TextFileChange change) {
        return new ChangeCorrectionProposal("Rename to '" + name + "'", 
                change, dist+10, CeylonLabelProvider.getImage(dwp.getDeclaration())) {
            @Override
            public void apply(IDocument document) {
                // TODO Auto-generated method stub
                super.apply(document);
                Util.gotoLocation(file, problem.getOffset(), name.length());
            }
        };
    }
    
}
