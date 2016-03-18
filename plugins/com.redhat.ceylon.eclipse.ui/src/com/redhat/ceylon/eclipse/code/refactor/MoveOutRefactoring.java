package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.util.Nodes.getContainer;
import static com.redhat.ceylon.eclipse.util.Nodes.text;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createErrorStatus;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.ide.common.util.escaping_;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class MoveOutRefactoring extends AbstractRefactoring {
    
    private Tree.Declaration declaration;
    private boolean makeShared=true;
    private boolean leaveDelegate=false;
    private String newName;

    public MoveOutRefactoring(IEditorPart editor) {
        super(editor);
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            if (ce.getSelectionProvider()!=null) {
                init(getSelection(ce));
            }
        }
    }

    private void init(ITextSelection selection) {
        if (node instanceof Tree.Declaration) {
            declaration = (Tree.Declaration) node;
            Declaration dec = declaration.getDeclarationModel();
            if (dec instanceof Functional) {
                newName = 
                        defaultName((Functional) dec, 
                                getContainer(rootNode, dec));
            }
        }
    }

    @Override
    public boolean getEnabled() {
        if (node instanceof Tree.AnyMethod || 
            node instanceof Tree.ClassDefinition) {
            Tree.Declaration decNode = (Tree.Declaration) node;
            Declaration dec = decNode.getDeclarationModel();
            if (dec==null || !dec.isClassOrInterfaceMember()) {
                return false;
            }
            if (decNode instanceof Tree.ClassDefinition) {
                Tree.ClassDefinition cd = (Tree.ClassDefinition) decNode;
                if (cd.getParameterList()==null) {
                    return false;
                }
            }
            else if (decNode instanceof Tree.AnyMethod) {
                Tree.AnyMethod am = (Tree.AnyMethod) decNode;
                if (am.getParameterLists().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    public String getName() {
        return "Move Out";
    }

    public boolean isMethod() {
        return declaration instanceof Tree.AnyMethod;
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        RefactoringStatus result = new RefactoringStatus();
        Tree.Declaration decNode = (Tree.Declaration) node;
        Declaration dec = decNode.getDeclarationModel();
        if (!(dec instanceof Functional) || 
                ((Functional) dec).getParameterLists().isEmpty()) {
            result.merge(createErrorStatus("Selected declaration has no parameter list"));
        }
        if (!dec.isClassOrInterfaceMember()) {
            result.merge(createErrorStatus("Selected declaration is not a member of a class or interface"));
        }
        if (dec.isFormal()) {
            result.merge(createWarningStatus("Selected declaration is annotated formal")); 
        }
        if (dec.isDefault()) {
            result.merge(createWarningStatus("Selected declaration is annotated default")); 
        }
        if (dec.isActual()) {
            result.merge(createWarningStatus("Selected declaration is annotated actual")); 
        }
        //TODO: check if there are method references to this declaration
        return result;
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, 
                   OperationCanceledException {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) 
            throws CoreException,
                   OperationCanceledException {
        CompositeChange cc = new CompositeChange(getName());
        
        Declaration dec = declaration.getDeclarationModel();
        Tree.TypeDeclaration owner = 
                (Tree.TypeDeclaration) 
                    getContainer(rootNode, dec);

        for (PhasedUnit pu: getAllUnits()) {
            if (searchInFile(pu)) {
                ProjectPhasedUnit<IProject, IResource, IFolder, IFile> ppu = 
                        (ProjectPhasedUnit<IProject, IResource, IFolder, IFile>) pu;
                TextFileChange pufc = 
                        newTextFileChange(ppu);
                pufc.setEdit(new MultiTextEdit());
                if (declaration.getUnit().equals(pu.getUnit())) {
                    move(owner, pufc);
                    if (makeShared) {
                        addSharedAnnotations(pufc, owner);
                    }
                }
                if (!leaveDelegate) {
                    fixInvocations(dec, 
                            pu.getCompilationUnit(), 
                            pu.getTokens(), 
                            pufc);
                }
                if (pufc.getEdit().hasChildren()) {
                    cc.add(pufc);
                }
            }
        }
        
        if (searchInEditor()) {
            TextChange tfc = newLocalChange();
            tfc.setEdit(new MultiTextEdit());
            move(owner, tfc);
            if (!leaveDelegate) {
                fixInvocations(dec, rootNode, tokens, tfc);
            }
            cc.add(tfc);
            if (makeShared) {
                addSharedAnnotations(tfc, owner);
            }
        }
        
        return cc;
    }

    private String renderText(Tree.TypeDeclaration owner,
            String indent, String originalIndent, String delim) {
        Unit unit = declaration.getUnit();
        String qtype =
                owner.getDeclarationModel().getType()
                    .asSourceCodeString(unit);
        StringBuilder sb = new StringBuilder();
        if (declaration instanceof Tree.AnyMethod) {
            Tree.AnyMethod md = (Tree.AnyMethod) declaration;
            appendAnnotations(sb, md, owner.getDeclarationModel());
            String typeDec;
            Tree.Type mdt = md.getType();
            if (mdt instanceof Tree.FunctionModifier &&
                    !ModelUtil.isTypeUnknown(mdt.getTypeModel())) {
                typeDec = 
                        mdt.getTypeModel()
                            .asSourceCodeString(unit);
            }
            else {
                typeDec = text(mdt, tokens);
            }
            sb.append(typeDec).append(" ")
                .append(text(md.getIdentifier(), tokens));
            if (md.getTypeParameterList()!=null)
            	sb.append(text(md.getTypeParameterList(), tokens));
            List<Tree.ParameterList> parameterLists = 
                    md.getParameterLists();
            Tree.ParameterList first = parameterLists.get(0);
            sb.append(text(first, tokens));
            if (!first.getParameters().isEmpty()) {
                sb.insert(sb.length()-1, ", ");
            }
            sb.insert(sb.length()-1, qtype+ " " + newName);
            for (int i=1; i<parameterLists.size(); i++) {
                sb.append(text(parameterLists.get(i), tokens));
            }
            if (md.getTypeConstraintList()!=null) {
                appendConstraints(indent, delim, sb, 
                        md.getTypeConstraintList());
            }
            sb.append(" ");
            if (md instanceof Tree.MethodDefinition &&
                    ((Tree.MethodDefinition) md).getBlock()!=null) {
                appendBody(owner.getDeclarationModel(), 
                        indent, originalIndent, 
                        delim, sb, 
                        ((Tree.MethodDefinition) md).getBlock());
            }
            if (md instanceof Tree.MethodDeclaration &&
                    ((Tree.MethodDeclaration) md).getSpecifierExpression()!=null) {
                appendBody(owner.getDeclarationModel(), 
                        indent, originalIndent, 
                        delim, sb, 
                        ((Tree.MethodDeclaration) md).getSpecifierExpression());
                sb.append(";");
            }
        }
        else if (declaration instanceof Tree.ClassDefinition) {
            Tree.ClassDefinition cd = (Tree.ClassDefinition) declaration;
            appendAnnotations(sb, cd, owner.getDeclarationModel());
            sb.append("class ")
                .append(text(cd.getIdentifier(), tokens));
            if (cd.getTypeParameterList()!=null)
            	sb.append(text(cd.getTypeParameterList(), tokens));
            Tree.ParameterList first = cd.getParameterList();
            sb.append(text(first, tokens));
            if (!first.getParameters().isEmpty()) {
                sb.insert(sb.length()-1, ", ");
            }
            sb.insert(sb.length()-1, qtype+ " " + newName);
            if (cd.getCaseTypes()!=null) {
                appendClause(indent, delim, sb, cd.getCaseTypes());
            }
            if (cd.getExtendedType()!=null) {
                appendClause(indent, delim, sb, cd.getExtendedType());
            }
            if (cd.getSatisfiedTypes()!=null) {
                appendClause(indent, delim, sb, cd.getSatisfiedTypes());
            }
            if (cd.getTypeConstraintList()!=null) {
                appendConstraints(indent, delim, sb, 
                        cd.getTypeConstraintList());
            }
            sb.append(" ");
            if (cd.getClassBody()!=null) {
                appendBody(owner.getDeclarationModel(), 
                        indent, originalIndent, 
                        delim, sb, 
                        cd.getClassBody());
            }
        }
        return sb.toString();
    }

    private void move(Tree.TypeDeclaration owner, TextChange tfc) {
        String indent = utilJ2C().indents().getIndent(owner, document);
        String originalIndent = utilJ2C().indents().getIndent(declaration, document);
        String delim = utilJ2C().indents().getDefaultLineDelimiter(document);
        String text = renderText(owner, indent, originalIndent, delim);
        tfc.addEdit(new InsertEdit(owner.getEndIndex(), 
                delim+indent+delim+indent+text));
        if (leaveDelegate) {
            leaveOriginal(tfc);
        }
        else {
            tfc.addEdit(new DeleteEdit(
                    declaration.getStartIndex(),
                    declaration.getDistance()));
        }
    }

    private void leaveOriginal(TextChange tfc) {
        StringBuilder params = new StringBuilder();
        String outer;
        Declaration dec = declaration.getDeclarationModel();
        ClassOrInterface container =
                (ClassOrInterface) dec.getContainer();
        if (container.isToplevel()) {
            outer = "package.";
        }
        else if (container.isClassOrInterfaceMember()) {
            outer = "outer.";
        }
        else {
            outer = ""; //let the user deal with it!
        }
        String semi = ";";
        Node body;
        Tree.ParameterList parameterList;
        if (declaration instanceof Tree.AnyMethod) {
            Tree.AnyMethod m =
                    (Tree.AnyMethod) declaration;
            parameterList = m.getParameterLists().get(0);
            if (declaration instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration md =
                        (Tree.MethodDeclaration) declaration;
                body = md.getSpecifierExpression();
                semi = "";
            }
            else if (declaration instanceof Tree.MethodDefinition) {
                Tree.MethodDefinition md =
                        (Tree.MethodDefinition) declaration;
                body = md.getBlock();
            }
            else {
                return; //impossible!
            }
        }
        /*else if (declaration instanceof Tree.AnyClass) {
            Tree.AnyClass m =
                    (Tree.AnyClass) declaration;
            parameterList = m.getParameterList();
            if (declaration instanceof Tree.ClassDefinition) {
                Tree.ClassDefinition md =
                        (Tree.ClassDefinition) declaration;
                body = md.getClassBody();
            }
            else {
                return; //impossible!
            }
        }*/
        else {
            return; //impossible!
        }
        for (Tree.Parameter parameter: parameterList.getParameters()) {
            params.append(parameter.getParameterModel().getName())
                  .append(", ");
        }
        params.append("this");
        tfc.addEdit(new ReplaceEdit(
                body.getStartIndex(),
                body.getDistance(),
                "=> " + outer +
                dec.getName() +
                "(" + params + ")" + semi));
    }

    private void addSharedAnnotations(final TextChange tfc,
            final Tree.TypeDeclaration owner) {
        final Set<Declaration> decs = new HashSet<Declaration>();
        new Visitor() {
            private void add(Declaration d) {
                if (d!=null && !d.isShared() && 
                        d.getContainer().equals(
                                owner.getDeclarationModel())) {
                    decs.add(d);
                }
            }
            public void visit(Tree.BaseMemberOrTypeExpression that) {
                super.visit(that);
                add(that.getDeclaration());
            }
            public void visit(Tree.QualifiedMemberOrTypeExpression that) {
                super.visit(that);
                if (that.getPrimary() instanceof Tree.This) {
                    add(that.getDeclaration());
                }
            }
        }.visit(declaration);
        new Visitor() {
            public void visit(Tree.Declaration that) {
                if (decs.contains(that.getDeclarationModel())) {
                    tfc.addEdit(new InsertEdit(that.getStartIndex(), "shared "));
                }
                super.visit(that);
            }
        }.visit(owner);
    }

    private static String defaultName(Functional dec, Tree.Declaration owner) {
        if (owner==null || owner.getIdentifier()==null) {
            return "it";
        }
        String name = owner.getIdentifier().getText();
        String paramName = escaping_.get_().toInitialLowercase(name);
        if (escaping_.get_().isKeyword(paramName)) {
            return "it";
        }
        ParameterList firstParameterList = 
                dec.getFirstParameterList();
        if (firstParameterList!=null) {
            for (Parameter p: 
                    firstParameterList.getParameters()) {
                if (p!=null) {
                    if (paramName.equals(p.getName())) {
                        return "it";
                    }
                }
            }
        }
        return paramName;
    }

    private void fixInvocations(final Declaration dec,
            Tree.CompilationUnit cu, 
            final List<CommonToken> tokens, 
            final TextChange tc) {
        new Visitor() {
            
            public void visit(Tree.QualifiedType that) {
                TypeDeclaration d = that.getDeclarationModel();
                if (d!=null && d.equals(dec)) {
                    Tree.StaticType qt = that.getOuterType();
                    tc.addEdit(new DeleteEdit(qt.getStartIndex(), 
                            that.getIdentifier().getStartIndex()-qt.getStartIndex()));
                }
            }
            
            public void visit(Tree.InvocationExpression that) {
                super.visit(that);
                Tree.PositionalArgumentList pal =
                        that.getPositionalArgumentList();
                Tree.NamedArgumentList nal =
                        that.getNamedArgumentList();
                Tree.Primary primary = that.getPrimary();
                if (primary instanceof Tree.BaseMemberOrTypeExpression) {
                    Tree.BaseMemberOrTypeExpression bmte = 
                            (Tree.BaseMemberOrTypeExpression)
                                primary;
                    if (bmte.getDeclaration().equals(dec)) {
                        if (pal!=null) {
                            String arg =
                                    pal.getPositionalArguments()
                                        .isEmpty() ?
                                            "this" : ", this";
                            tc.addEdit(new InsertEdit(pal.getEndIndex()-1, arg));
                        }
                        if (nal!=null) {
                            try {
                                IDocument doc =
                                        tc.getCurrentDocument(null);
                                String arg =
                                        namedArgIndent(nal, doc) +
                                            newName + " = this;";
                                List<Tree.NamedArgument> args =
                                        nal.getNamedArguments();
                                int offset =
                                        args.isEmpty() ?
                                            nal.getStartIndex()+1 :
                                            args.get(args.size()-1).getEndIndex();
                                tc.addEdit(new InsertEdit(offset, arg));
                            }
                            catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (primary instanceof Tree.QualifiedMemberOrTypeExpression) {
                    Tree.QualifiedMemberOrTypeExpression qmte = 
                            (Tree.QualifiedMemberOrTypeExpression)
                                primary;
                    if (qmte.getDeclaration().equals(dec)) {
                        Tree.Primary p = qmte.getPrimary();
                        String pt = text(p, tokens);
                        tc.addEdit(new DeleteEdit(p.getStartIndex(), 
                                qmte.getIdentifier().getStartIndex()-p.getStartIndex()));
                        if (pal!=null) {
                            String arg =
                                    pal.getPositionalArguments()
                                        .isEmpty() ?
                                            pt : ", " + pt;
                            tc.addEdit(new InsertEdit(pal.getEndIndex()-1, arg));
                        }
                        if (nal!=null) {
                            try {
                                IDocument doc =
                                        tc.getCurrentDocument(null);
                                String arg =
                                        namedArgIndent(nal, doc) +
                                            newName + " = " + pt + ";";
                                List<Tree.NamedArgument> args =
                                        nal.getNamedArguments();
                                int offset = args.isEmpty() ? 
                                        nal.getStartIndex()+1 : 
                                        args.get(args.size()-1).getEndIndex();
                                tc.addEdit(new InsertEdit(offset, arg));
                            }
                            catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            private String namedArgIndent(
                    Tree.NamedArgumentList nal,
                    IDocument doc) {
                return utilJ2C().indents().getDefaultLineDelimiter(doc) +
                        utilJ2C().indents().getIndent(nal, doc) +
                        utilJ2C().indents().getDefaultIndent();
            }
            
        }.visit(cu);
    }

    private void appendAnnotations(StringBuilder sb,
            Tree.Declaration d, TypeDeclaration od) {
        if (!d.getAnnotationList().getAnnotations().isEmpty()) {
            String annotations = text(d.getAnnotationList(), tokens);
            if (!od.isShared()) {
                annotations = annotations.replaceAll("shared", "");
            }
            annotations = annotations.replaceAll("default|formal|actual", "");
            sb.append(annotations.trim());
            if (sb.length()!=0) {
                sb.append(" ");
            }
        }
    }

    private void appendConstraints(String indent, String delim,
            StringBuilder sb, Tree.TypeConstraintList tcl) {
        for (Tree.TypeConstraint tc: tcl.getTypeConstraints()) {
            appendClause(indent, delim, sb, tc);
        }
    }

    private void appendClause(String indent, String delim,
            StringBuilder sb, Node clause) {
        sb.append(delim).append(indent)
            .append(utilJ2C().indents().getDefaultIndent())
            .append(utilJ2C().indents().getDefaultIndent())
            .append(text(clause, tokens));
    }

    private void appendBody(final Scope container,
            String indent, String originalIndent,
            String delim, StringBuilder sb, final Node body) {
        final StringBuilder stb =
                new StringBuilder(text(body, tokens));
        body.visit(new Visitor() {
            int offset = 0;
            private int startIndex(Node that) {
                return that.getStartIndex()
                        - body.getStartIndex()
                        + offset;
            }
            @Override
            public void visit(Tree.BaseMemberOrTypeExpression that) {
                super.visit(that);
                if (that.getDeclaration().getContainer()
                        .equals(container)) {
                    int start = startIndex(that);
                    stb.insert(start, newName + ".");
                    offset+=newName.length()+1;
                }
            }
            @Override
            public void visit(Tree.This that) {
                super.visit(that);
                int start = startIndex(that);
                boolean isClass =
                        declaration.getDeclarationModel()
                        instanceof Class;
                if (!isClass) {
                    stb.replace(start, start+4, newName);
                    offset+=newName.length()-4;
                }
            }
            @Override
            public void visit(Tree.Outer that) {
                super.visit(that);
                int start = startIndex(that);
                boolean isClass =
                        declaration.getDeclarationModel()
                        instanceof Class;
                if (isClass) {
                    stb.replace(start, start+5, newName);
                    offset+=newName.length()-5;
                }
                else {
                    stb.replace(start, start+5, "this");
                    offset+=4-5;
                }
            }
        });
        sb.append(stb.toString().replaceAll(delim+originalIndent, delim+indent));
    }

    public void setMakeShared() {
        makeShared=!makeShared;
    }

    public void setLeaveDelegate() {
        leaveDelegate = !leaveDelegate;
    }

    void setNewName(String name) {
        newName = name;
    }

    String getNewName() {
        return newName;
    }
    
}
