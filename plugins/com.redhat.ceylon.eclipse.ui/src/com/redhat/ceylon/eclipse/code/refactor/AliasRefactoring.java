package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeStartOffset;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createErrorStatus;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class AliasRefactoring extends AbstractRefactoring {
    
    private static class FindAliasedTypeVisitor 
            extends Visitor {
        private Type type;
        private List<Node> nodes = new ArrayList<Node>();

        private FindAliasedTypeVisitor(Type type) {
            this.type = type;
        }
        
        public List<Node> getNodes() {
            return nodes;
        }
        
        @Override
        public void visit(Tree.Type that) {
            super.visit(that);
            Type t = that.getTypeModel();
            if (t!=null && type.isExactly(t)) {
                nodes.add(that);
            }
        }
        @Override
        public void visit(Tree.BaseTypeExpression that) {
            super.visit(that);
            Type t = that.getTarget().getType();
            if (t!=null && type.isExactly(t)) {
                nodes.add(that);
            }
        }
    }

    private String newName;
    private String typeString;
    private final Type type;
//    private boolean renameValuesAndFunctions;
    
    public Node getNode() {
        return node;
    }

    public AliasRefactoring(IEditorPart editor) {
        super(editor);
        if (rootNode!=null) {
            if (node instanceof Tree.Type) {
                Tree.Type t = (Tree.Type) node;
                type = t.getTypeModel();
                newName = null;
                typeString = Nodes.toString(t, tokens);
            }
            else {
                type = null;
            }
        }
        else {
            type = null;
        }
    }
    
    @Override
    public boolean isEnabled() {
        return type!=null &&
                project != null;
    }

    public int getCount() {
        return type==null ? 
                0 : countDeclarationOccurrences();
    }
    
    @Override
    int countReferences(Tree.CompilationUnit cu) {
        FindAliasedTypeVisitor frv = 
                new FindAliasedTypeVisitor(type);
        cu.visit(frv);
        return frv.getNodes().size();
    }

    public String getName() {
        return "Introduce Type Alias";
    }

    public RefactoringStatus checkInitialConditions(
            IProgressMonitor pm)
                    throws CoreException, 
                           OperationCanceledException {
        // Check parameters retrieved from editor context
        return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(
            IProgressMonitor pm)
                    throws CoreException, 
                           OperationCanceledException {
        if (newName==null || !newName.matches("^[a-zA-Z_]\\w*$")) {
            return createErrorStatus("Not a legal Ceylon identifier");
        }
        else if (Escaping.KEYWORDS.contains(newName)) {
            return createErrorStatus("'" + newName + "' is a Ceylon keyword");
        }
        else {
            int ch = newName.codePointAt(0);
            if (!Character.isUpperCase(ch)) {
                return createErrorStatus("Not an initial uppercase identifier");
            }
        }
        /*Declaration existing = declaration.getContainer()
                        .getMemberOrParameter(declaration.getUnit(), 
                                newName, null, false);
        if (null!=existing && !existing.equals(declaration)) {
            return createWarningStatus("An existing declaration named '" +
                newName + "' already exists in the same scope");
        }*/
        return new RefactoringStatus();
    }

    public CompositeChange createChange(
            IProgressMonitor pm) 
                    throws CoreException, 
                           OperationCanceledException {
        List<PhasedUnit> units = getAllUnits();
        pm.beginTask(getName(), units.size());
        CompositeChange cc = new CompositeChange(getName());
        int i=0;
        for (PhasedUnit pu: units) {
            Package editorPackage = 
                    editor.getParseController()
                        .getRootNode()
                        .getUnit()
                        .getPackage();
            boolean inSamePackage = 
                    pu.getPackage()
                        .equals(editorPackage);
            if (inSamePackage && searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange(pu);
                renameInFile(tfc, cc, 
                        pu.getCompilationUnit());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            renameInFile(dc, cc, 
                    editor.getParseController()
                        .getRootNode());
            pm.worked(i++);
        }
        pm.done();
        return cc;
    }
    
    private int aliasOffset;
    
    int getAliasOffset() {
        return aliasOffset;
    }
    
    int getAliasLength() {
        return typeString.length();
    }
    
    void renameInFile(TextChange tfc, 
            CompositeChange cc, 
            Tree.CompilationUnit root) {
        tfc.setEdit(new MultiTextEdit());
        if (type!=null) {
            Unit editorUnit = 
                    editor.getParseController()
                        .getRootNode()
                        .getUnit();
            Unit unit = root.getUnit();
            if (editorUnit.getPackage()
                    .equals(unit.getPackage())) {
                IDocument doc = getDocument(tfc);
                String delim = 
                        getDefaultLineDelimiter(document);
                if (newName!=null) {
                    for (Node node: getNodesToRename(root)) {
                        renameNode(tfc, node, root);
                    }
                }
                if (unit.getFilename()
                        .equals(editorUnit.getFilename())) {
                    TypeDeclaration td = 
                            getType()
                                .getDeclaration();
                    StringBuffer header = 
                            new StringBuffer("shared ");
                    StringBuffer args = new StringBuffer();
                    String initialName = getInitialName();
                    if (td instanceof Class) {
                        aliasOffset = 
                                doc.getLength() + 
                                delim.length()*2 +
                                6+7;
                        header.append("class ")
                            .append(initialName)
                            .append("(");
                        Class c = (Class) td;
                        ParameterList pl = 
                                c.getParameterList();
                        if (pl!=null) {
                            args.append("(");
                            boolean first = true;
                            for (Parameter p: pl.getParameters()) {
                                if (first) {
                                    first = false;
                                }
                                else {
                                    header.append(", ");
                                    args.append(", ");
                                }
                                Type ptype = 
                                        getType()
                                            .getTypedParameter(p)
                                            .getFullType();
                                String pname = p.getName();
                                header.append(ptype.asString(unit)) 
                                    .append(" ")
                                    .append(pname);
                                args.append(pname);
                            }
                            header.append(")");
                            args.append(")");
                        }
                    }
                    else if (td instanceof Interface) {
                        aliasOffset = 
                                doc.getLength() + 
                                delim.length()*2 +
                                10+7;
                        header.append("interface ")
                            .append(initialName);
                    }
                    else {
                        aliasOffset = 
                                doc.getLength() + 
                                delim.length()*2 +
                                6+7;
                        header.append("alias ")
                            .append(initialName);
                    }
                    tfc.addEdit(new InsertEdit(
                            doc.getLength(),
                            delim + delim +
                            header + " => " + 
                            getType().asString(unit) + 
                            args + ";"));
                }
            }
//            if (renameValuesAndFunctions) { 
//                for (Tree.Identifier id: getIdentifiersToRename(root)) {
//                    renameIdentifier(tfc, id, root);
//                }
//            }
        }
        if (cc!=null && tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

    String getInitialName() {
        return newName==null ? 
                typeString : newName;
    }
    
    public List<Node> getNodesToRename(
            Tree.CompilationUnit root) {
        FindAliasedTypeVisitor frv = 
                new FindAliasedTypeVisitor(type);
        root.visit(frv);
        return frv.getNodes();
    }
    
    protected void renameNode(TextChange tfc, Node node, 
            Tree.CompilationUnit root) {
        tfc.addEdit(new ReplaceEdit(
                getNodeStartOffset(node), 
                getNodeLength(node), 
                newName));
    }
    
    /*public boolean isRenameValuesAndFunctions() {
        return renameValuesAndFunctions;
    }
    
    public void setRenameValuesAndFunctions(boolean renameLocals) {
        this.renameValuesAndFunctions = renameLocals;
    }*/
    
    public void setNewName(String text) {
        newName = text;
    }
    
    public Type getType() {
        return type;
    }

    public String getNewName() {
        return newName;
    }

}
