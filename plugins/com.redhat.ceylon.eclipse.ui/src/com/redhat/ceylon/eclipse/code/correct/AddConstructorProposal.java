package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CorrectionUtil.collectUninitializedMembers;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Indents.indents;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class AddConstructorProposal extends CorrectionProposal {

    public AddConstructorProposal(String name, Change change, Region selection) {
        super(name, change, selection);
    }

    public static void addConstructorProposal(IFile file,
            Collection<ICompletionProposal> proposals, Node node,
            Tree.CompilationUnit rootNode) {
        if (node instanceof Tree.TypedDeclaration) {
            node = findDeclarationWithBody(rootNode, node);
        }
        if (node instanceof Tree.ClassDefinition) {
            TextFileChange change = 
                    new TextFileChange("Add Default Constructor", file);
            Tree.ClassDefinition cd = (Tree.ClassDefinition) node;
            if (cd.getParameterList()!=null) return;
            Tree.ClassBody body = cd.getClassBody();
            if (body!=null && cd.getIdentifier()!=null) {
                IDocument doc = getDocument(change);
                List<TypedDeclaration> uninitialized = 
                        collectUninitializedMembers(body);
                Tree.Statement les = findLastExecutable(body);
                String defaultIndent = indents().getDefaultIndent();
                String delim = indents().getDefaultLineDelimiter(doc);
                String indent;
                indent = les==null ? 
                        indents().getIndent(cd, doc) + defaultIndent :
                            indents().getIndent(les, doc);
                Unit unit = node.getUnit();
                StringBuilder params = new StringBuilder();
                StringBuilder initializers = new StringBuilder();
                if (!uninitialized.isEmpty()) {
                    initializers.append(delim);
                }
                for (TypedDeclaration dec: uninitialized) {
                    if (params.length()!=0) {
                        params.append(", ");
                    }
                    Reference pr = 
                            dec.appliedReference(null, 
                                    Collections.<Type>emptyList());
                    String type = 
                            pr.getFullType().asString(unit);
                    String name = dec.getName();
                    params.append(type)
                          .append(" ")
                          .append(name);
                    initializers.append(indent)
                                .append(defaultIndent)
                                .append("this.")
                                .append(name)
                                .append(" = ")
                                .append(name)
                                .append(";")
                                .append(delim);
                }
                if (!uninitialized.isEmpty()) {
                    initializers.append(indent);
                }
                
                String text = delim + indent + 
                        "shared new (" + params + ") {" + initializers + "}";
                
                int start;
                if (les==null) {
                    start = body.getStartIndex()+1;
                    if (body.getEndIndex()-1==start) {
                        text += delim + getIndent(cd, doc);
                    }
                }
                else {
                    start = les.getEndIndex();
                }
                
                InsertEdit edit = new InsertEdit(start, text);
                change.setEdit(edit);
                
                int loc = start + text.indexOf('(') + 1;
                String name = cd.getDeclarationModel().getName();
                proposals.add(new AddConstructorProposal(
                        "Add constructor 'new (" + params + ")' of '" + name + "'", 
                        change, 
                        new Region(loc, 0)));
            }
        }
    }

    private static Tree.Statement findLastExecutable(Tree.ClassBody body) {
        Tree.Statement les = null;
        if (body!=null) {
            List<Tree.Statement> statements = 
                    body.getStatements();
            for (Tree.Statement st: statements) {
                if (isExecutableStatement(st) || 
                        st instanceof Tree.Constructor) {
                    les = st;
                }
            }
        }
        return les;
    }

    private static boolean isExecutableStatement(Tree.Statement s) {
        Unit unit = s.getUnit();
        if (s instanceof Tree.SpecifierStatement) {
            //shortcut refinement statements with => aren't really "executable"
            Tree.SpecifierStatement ss = 
                    (Tree.SpecifierStatement) s;
            return !(ss.getSpecifierExpression() 
                        instanceof Tree.LazySpecifierExpression && 
                    !ss.getRefinement());
        }
        else if (s instanceof Tree.ExecutableStatement) {
            return true;
        }
        else {
            if (s instanceof Tree.AttributeDeclaration) {
                Tree.AttributeDeclaration ad = 
                        (Tree.AttributeDeclaration) s;
                Tree.SpecifierOrInitializerExpression sie = 
                        ad.getSpecifierOrInitializerExpression();
                return !(sie instanceof Tree.LazySpecifierExpression) &&
                        !ad.getDeclarationModel().isFormal();
            }
            else if (s instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration ad = 
                        (Tree.MethodDeclaration) s;
                Tree.SpecifierExpression sie = 
                        ad.getSpecifierExpression();
                return !(sie instanceof Tree.LazySpecifierExpression) &&
                        !ad.getDeclarationModel().isFormal();
            }
            else if (s instanceof Tree.ObjectDefinition) {
                Tree.ObjectDefinition o = (Tree.ObjectDefinition) s;
                if (o.getExtendedType()!=null) {
                    Type et = o.getExtendedType().getType().getTypeModel();
                    if (et!=null 
                            && !et.getDeclaration().equals(unit.getObjectDeclaration())
                            && !et.getDeclaration().equals(unit.getBasicDeclaration())) {
                        return true;
                    }
                }
                Tree.ClassBody ocb = o.getClassBody();
                if (ocb!=null) {
                    List<Tree.Statement> statements = 
                            ocb.getStatements();
                    for (int i=statements.size()-1; i>=0; i--) {
                        Tree.Statement st = statements.get(i);
                        if (isExecutableStatement(st) || 
                                st instanceof Tree.Constructor) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            else {
                return false;
            }
        }
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        String hint =
                CorrectionUtil.shortcut(
                        "com.redhat.ceylon.eclipse.ui.action.addConstructor");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }
    
}
