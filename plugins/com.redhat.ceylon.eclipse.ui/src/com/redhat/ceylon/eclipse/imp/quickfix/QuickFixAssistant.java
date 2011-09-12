package com.redhat.ceylon.eclipse.imp.quickfix;

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
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.contentProposer.CeylonContentProposer;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;
import com.redhat.ceylon.eclipse.util.Util;

/**
 * Popup quick fixes for problem annotations displayed in editor
 * @author gavin
 */
public class QuickFixAssistant implements IQuickFixAssistant {

    @Override
    public boolean canFix(Annotation annotation) {
        return annotation instanceof IAnnotation 
                && ((IAnnotation) annotation).getId()==100 ||
               annotation instanceof MarkerAnnotation && 
               ((MarkerAnnotation) annotation).getMarker()
                   .getAttribute(IMessageHandler.ERROR_CODE_KEY, -666)==100;
    }

    @Override
    public boolean canAssist(IQuickFixInvocationContext invocationContext) {
        return false;
    }

    @Override
    public String[] getSupportedMarkerTypes() {
        return new String[] { CeylonBuilder.PROBLEM_MARKER_ID };
    }

    @Override
    public void addProposals(IQuickFixInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals) {
        switch ( problem.getProblemId() ) {
        case 100:
            IProject project = context.getModel().getProject().getRawProject();
            IFile file = context.getModel().getFile();
            TypeChecker tc = CeylonBuilder.getProjectTypeChecker(project);
            if (tc!=null) {
                Tree.CompilationUnit cu = (Tree.CompilationUnit) context.getModel()
                        .getAST(new NullMessageHandler(), new NullProgressMonitor());
                addCreateMemberProposals(cu, problem, proposals, project);
                addRenameProposals(cu, problem, proposals, file, tc);
            }
            break;
        }
    }
    
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

    private void addCreateMemberProposals(Tree.CompilationUnit cu, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IProject project) {
        Node node = CeylonSourcePositionLocator.findNode(cu, problem.getOffset(), 
                problem.getOffset() + problem.getLength());
        String brokenName = CeylonSourcePositionLocator.getIdentifyingNode(node).getText();
        if (node instanceof Tree.QualifiedMemberOrTypeExpression) {
            Tree.QualifiedMemberOrTypeExpression qmte = (Tree.QualifiedMemberOrTypeExpression) node;
            FindArgumentsVisitor fav = new FindArgumentsVisitor(qmte);
            cu.visit(fav);
            String def;
            String desc;
            Image image;
            if (fav.positionalArgs!=null || fav.namedArgs!=null) {
                StringBuilder params = new StringBuilder();
                params.append("(");
                if (fav.positionalArgs!=null)
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
                if (fav.namedArgs!=null)
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
                    def = "\nshared class " + brokenName + params + supertype + " {}";
                    desc = "class '" + brokenName + params + supertype + "'";
                    image = CeylonLabelProvider.CLASS;
                }
                else {
                    String type = fav.expectedType==null ? "Nothing" : 
                        fav.expectedType.getProducedTypeName();
                    def = "\nshared " + type + " " + brokenName + params + " { return null; }";
                    desc = "function '" + brokenName + params + "'";
                    image = CeylonLabelProvider.METHOD;
                }
            }
            else {
                String type = fav.expectedType==null ? "Nothing" : 
                    fav.expectedType.getProducedTypeName();
                def = "\nshared " + type + " " + brokenName + " = null;";
                desc = "value '" + brokenName + "'";
                image = CeylonLabelProvider.ATTRIBUTE;
            }
            Declaration typeDec = qmte.getPrimary().getTypeModel().getDeclaration();
            if (typeDec!=null && typeDec instanceof ClassOrInterface) {
                for (PhasedUnit unit: CeylonBuilder.getUnits(project)) {
                    FindDeclarationVisitor fdv = new FindDeclarationVisitor(typeDec);
                    unit.getCompilationUnit().visit(fdv);
                    Tree.Declaration decNode = fdv.getDeclarationNode();
                    if (decNode!=null) {
                        TextFileChange change = new TextFileChange("Add Member", 
                                CeylonBuilder.getFile(unit));
                        change.setEdit(new InsertEdit(decNode.getStopIndex()-1, def));
                        proposals.add(new ChangeCorrectionProposal("Create " + 
                                desc + " in '" + typeDec.getName() + "'", 
                                change, 50, image));
                        break;
                    }
                }
            }
        }
    }

    private void addRenameProposals(Tree.CompilationUnit cu, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file, TypeChecker tc) {
          Node node = CeylonSourcePositionLocator.findNode(cu, problem.getOffset(), 
                  problem.getOffset() + problem.getLength());
          String brokenName = CeylonSourcePositionLocator.getIdentifyingNode(node).getText();
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
                proposals.add(new ChangeCorrectionProposal("Rename to '" + name + "'", 
                        change, dist+10, CeylonLabelProvider.getImage(dwp.getDeclaration())));
            }
          }
    }
    
}
