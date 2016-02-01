package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static com.redhat.ceylon.eclipse.util.Nodes.getContainer;
import static com.redhat.ceylon.eclipse.util.Nodes.text;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
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
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Value;

public class MakeReceiverRefactoring extends AbstractRefactoring {
    
    private boolean leaveDelegate = false;
    
    private final class MoveVisitor extends Visitor {
        private final TypeDeclaration newOwner;
        private final IDocument doc;
        private final Tree.Term defaultArg;
        private final Tree.Declaration fun;
        private final Declaration parameter;
        private final TextChange tfc;
        private List<CommonToken> localTokens;

        private MoveVisitor(TypeDeclaration newOwner,
                IDocument doc, Declaration parameter,
                Tree.Declaration fun, Tree.Term defaultArg,
                TextChange tfc, List<CommonToken> tokens) {
            this.newOwner = newOwner;
            this.doc = doc;
            this.parameter = parameter;
            this.fun = fun;
            this.defaultArg = defaultArg;
            this.tfc = tfc;
            localTokens = tokens;
        }

        private String getDefinition() {
            final StringBuilder def = 
                    new StringBuilder(text(fun, tokens));
            new Visitor() {
                int offset=0;
                public void visit(Tree.Declaration that) {
                    if (that.getDeclarationModel()
                            .equals(parameter)) {
                        int len = node.getDistance();
                        int start =
                                node.getStartIndex()
                                - fun.getStartIndex()
                                + offset;
                        def.replace(start, start+len, "");
                        offset-=len;
                        boolean deleted=false;
                        for (int i=start-1; i>=0; i--) {
                            if (!Character.isWhitespace(def.charAt(i))) {
                                if (def.charAt(i)==',') {
                                    def.delete(i, start);
                                    deleted = true;
                                    offset-=start-i;
                                }
                                break;
                            }
                        }
                        if (!deleted) {
                            boolean found=false;
                            for (int i=start; i<def.length(); i++) {
                                if (!Character.isWhitespace(def.charAt(i))) {
                                    if (!found && def.charAt(i)==',') {
                                        found = true;
                                    }
                                    else {
                                        def.delete(start, i);
                                        deleted = true;
                                        offset-=i-start;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    super.visit(that);
                }
                public void visit(
                        Tree.BaseMemberOrTypeExpression that) {
                    if (that.getDeclaration()
                            .equals(parameter)) {
                        int len = that.getDistance();
                        int start =
                                that.getStartIndex()
                                - fun.getStartIndex()
                                + offset;
                        String outerRef =
                                fun.getDeclarationModel()
                                    instanceof Class ?
                                        "outer" : "this";
                        def.replace(start, start+len, outerRef);
                        offset += outerRef.length()-len;
                    }
                    super.visit(that);
                }
            }.visit(fun);
            if (!fun.getDeclarationModel().isShared()) {
                def.insert(0, "shared ");
            }
            return def.toString();
        }

        private void insert(Tree.Body body, Tree.Declaration that) {
            String delim = 
                    utilJ2C().indents().getDefaultLineDelimiter(document);
            String originalIndent =
                    delim+utilJ2C().indents().getIndent(fun, document);
            String text;
            List<Tree.Statement> sts = body.getStatements();
            int loc;
            if (sts.isEmpty()) {
                String outerIndent =
                        delim + utilJ2C().indents().getIndent(that, doc);
                String newIndent =
                        outerIndent + utilJ2C().indents().getDefaultIndent();
                String def =
                        getDefinition()
                            .replaceAll(originalIndent,
                                    newIndent);
                text = newIndent + def + outerIndent;
                loc = body.getEndIndex()-1;
            }
            else {
                Tree.Statement st = sts.get(sts.size()-1);
                String newIndent =
                        delim + utilJ2C().indents().getIndent(st, doc);
                String def =
                        getDefinition()
                            .replaceAll(originalIndent,
                                    newIndent);
                text = newIndent + def;
                loc = st.getEndIndex();
            }
            tfc.addEdit(new InsertEdit(loc, text));
        }

        @Override
        public void visit(Tree.ClassDefinition that) {
            super.visit(that);
            if (that.getDeclarationModel()
                    .equals(newOwner)) {
                insert(that.getClassBody(), that);
            }
        }

        @Override
        public void visit(Tree.InterfaceDefinition that) {
            super.visit(that);
            if (that.getDeclarationModel()
                    .equals(newOwner)) {
                insert(that.getInterfaceBody(), that);
            }
        }
        
        @Override
        public void visit(Tree.SimpleType that) {
            super.visit(that);
            TypeDeclaration d = that.getDeclarationModel();
            if (d!=null &&
                    d.equals(fun.getDeclarationModel())) {
                tfc.addEdit(new InsertEdit(
                        that.getIdentifier().getStartIndex(),
                        newOwner.getName(that.getUnit()) + "."));
            }
        }

        @Override
        public void visit(Tree.InvocationExpression that) {
            super.visit(that);
            if (leaveDelegate) return;
            Tree.Primary p = that.getPrimary();
            if (p instanceof Tree.BaseMemberOrTypeExpression) {
                Tree.BaseMemberOrTypeExpression bmte =
                        (Tree.BaseMemberOrTypeExpression) p;
                Declaration d = bmte.getDeclaration();
                if (d!=null &&
                        d.equals(fun.getDeclarationModel())) {
                    Tree.PositionalArgumentList pal =
                            that.getPositionalArgumentList();
                    Tree.NamedArgumentList nal =
                            that.getNamedArgumentList();
                    if (pal!=null) {
                        List<Tree.PositionalArgument> pas =
                                pal.getPositionalArguments();
                        for (int i=0; i<pas.size(); i++) {
                            Tree.PositionalArgument arg =
                                    pas.get(i);
                            if (arg.getParameter()
                                    .getModel()
                                    .equals(parameter)) {
                                tfc.addEdit(new InsertEdit(
                                        p.getStartIndex(),
                                        text(arg, localTokens) +
                                        "."));
                                int start, stop;
                                if (i>0) {
                                    start = pas.get(i-1).getEndIndex();
                                    stop = arg.getEndIndex();
                                }
                                else if (i<pas.size()-1) {
                                    start = arg.getStartIndex();
                                    stop = pas.get(i+1).getStartIndex();
                                }
                                else {
                                    start = arg.getStartIndex();
                                    stop = arg.getEndIndex();
                                }
                                tfc.addEdit(new DeleteEdit(start, stop-start));
                                return; //NOTE: early exit!!!
                            }
                        }
                        if (defaultArg!=null) {
                            tfc.addEdit(new InsertEdit(
                                    p.getStartIndex(),
                                    text(defaultArg, tokens) + "."));
                        }
                    }
                    if (nal!=null) {
                        List<Tree.NamedArgument> nas =
                                nal.getNamedArguments();
                        for (int i=0; i<nas.size(); i++) {
                            Tree.NamedArgument arg = nas.get(i);
                            Parameter param = arg.getParameter();
                            if (param!=null &&
                                    param.getModel()
                                        .equals(parameter)) {
                                if (arg instanceof Tree.SpecifiedArgument) {
                                    Tree.SpecifiedArgument sa =
                                            (Tree.SpecifiedArgument) arg;
                                    Tree.Expression e =
                                            sa.getSpecifierExpression()
                                                .getExpression();
                                    tfc.addEdit(new InsertEdit(
                                            p.getStartIndex(),
                                            text(e, localTokens) + "."));
                                }
                                else {
                                    String name =
                                            arg.getIdentifier()
                                                .getText();
                                    tfc.addEdit(new InsertEdit(
                                            p.getStartIndex(),
                                            text(arg, localTokens) +
                                            utilJ2C().indents().getDefaultLineDelimiter(doc) +
                                            utilJ2C().indents().getIndent(that, doc) +
                                            name + "."));
                                }
                                int start, stop;
                                if (i>0) {
                                    start = nas.get(i-1).getEndIndex();
                                    stop = arg.getEndIndex();
                                }
                                else if (i<nas.size()-1) {
                                    start = arg.getStartIndex();
                                    stop = nas.get(i+1).getStartIndex();
                                }
                                else {
                                    start = arg.getStartIndex();
                                    stop = arg.getEndIndex();
                                }
                                tfc.addEdit(new DeleteEdit(start, stop-start));
                                return; //NOTE: early exit!!
                            }
                        }
                        if (defaultArg!=null) {
                            tfc.addEdit(new InsertEdit(
                                    p.getStartIndex(),
                                    text(defaultArg, tokens) + "."));
                        }
                    }
                }
            }
        }
        
    }

    public MakeReceiverRefactoring(IEditorPart editor) {
        super(editor);
    }

    @Override
    public boolean getEnabled() {
        if (node instanceof Tree.AttributeDeclaration && 
                project != null) {
            Tree.AttributeDeclaration ad =
                    (Tree.AttributeDeclaration) node;
            Value param = ad.getDeclarationModel();
            if (param!=null && 
                    param.isParameter() && 
                    param.getInitializerParameter()
                        .getDeclaration()
                        .isToplevel()) {
                TypeDeclaration target =
                        param.getTypeDeclaration();
                return target!=null &&
                        inSameProject(target) && 
                        (target instanceof Class || 
                         target instanceof Interface);
            }
        }
        return false;
    }
    
    public String getName() {
        return "Make Receiver";
    }

    public RefactoringStatus checkInitialConditions
            (IProgressMonitor pm)
                    throws CoreException,
                           OperationCanceledException {
        RefactoringStatus result = new RefactoringStatus();
        return result;
    }

    public RefactoringStatus checkFinalConditions
            (IProgressMonitor pm)
                    throws CoreException,
                           OperationCanceledException {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm)
            throws CoreException,
                   OperationCanceledException {
        CompositeChange cc = new CompositeChange(getName());
        Tree.AttributeDeclaration decNode =
                (Tree.AttributeDeclaration) node;
        Value param = decNode.getDeclarationModel();
        TypeDeclaration target = param.getTypeDeclaration();
        Tree.Declaration fun = getContainer(rootNode, param);
        Tree.SpecifierOrInitializerExpression sie = 
                decNode.getSpecifierOrInitializerExpression();
        Tree.Term defaultArg = null;
        if (sie!=null && sie.getExpression()!=null) {
            defaultArg = sie.getExpression().getTerm();
        }
        
        for (PhasedUnit pu: getAllUnits()) {
            if (searchInFile(pu)) {
                ProjectPhasedUnit<IProject, IResource, IFolder, IFile> ppu = 
                        (ProjectPhasedUnit<IProject, IResource, IFolder, IFile>) pu;
                TextFileChange pufc = newTextFileChange(ppu);
                IDocument doc = pufc.getCurrentDocument(null);
                pufc.setEdit(new MultiTextEdit());
                if (fun.getUnit().equals(pu.getUnit())) {
                    if (leaveDelegate) {
                        leaveOriginal(pufc, fun, param);
                    }
                    else {
                        deleteOld(pufc, fun);
                    }
                }
                new MoveVisitor(target, doc, param, fun,
                                defaultArg, pufc,
                                pu.getTokens())
                        .visit(pu.getCompilationUnit());
                if (pufc.getEdit().hasChildren()) {
                    cc.add(pufc);
                }
            }
        }
        if (searchInEditor()) {
            final TextChange tfc = newLocalChange();
            tfc.setEdit(new MultiTextEdit());
            if (leaveDelegate) {
                leaveOriginal(tfc, fun, param);
            }
            else {
                deleteOld(tfc, fun);
            }
            new MoveVisitor(target, document, param, fun,
                            defaultArg, tfc, tokens)
                    .visit(rootNode);
            cc.add(tfc);
        }
        
        
        return cc;
    }

    private void leaveOriginal(TextChange tfc, 
            Tree.Declaration declaration, Value param) {
        StringBuilder params = new StringBuilder();
        Declaration dec = declaration.getDeclarationModel();
        String outer = param.getName() + ".";
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
        for (Tree.Parameter parameter:
                parameterList.getParameters()) {
            Parameter p = parameter.getParameterModel();
            FunctionOrValue model = p.getModel();
            if (model==null || !model.equals(param)) {
                if (params.length()>0) {
                    params.append(", ");
                }
                params.append(p.getName());
            }
        }
        tfc.addEdit(new ReplaceEdit(
                body.getStartIndex(), 
                body.getDistance(), 
                "=> " + outer + 
                dec.getName() + 
                "(" + params + ")" + semi));
    }
    
    private void deleteOld(TextChange tfc, Tree.Declaration fun) {
        tfc.addEdit(new DeleteEdit(fun.getStartIndex(), 
                fun.getDistance()));
    }

    public void setLeaveDelegate() {
        leaveDelegate = !leaveDelegate;
    }

    public boolean isMethod() {
        Tree.AttributeDeclaration ad =
                (Tree.AttributeDeclaration) node;
        Value dec = ad.getDeclarationModel();
        return dec.getContainer() instanceof Function;
    }
    
}
