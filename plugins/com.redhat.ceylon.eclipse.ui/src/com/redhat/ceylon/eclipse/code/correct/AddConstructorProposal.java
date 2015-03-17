package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class AddConstructorProposal {

    public static void addConstructorProposal(IFile file,
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.ClassDefinition) {
            TextFileChange change = 
                    new TextFileChange("Add Default Constructor", file);
            Tree.ClassDefinition cd = (Tree.ClassDefinition) node;
            Tree.ClassBody body = cd.getClassBody();
            if (body!=null && cd.getIdentifier()!=null) {
                IDocument doc = getDocument(change);
                Tree.Statement les = null;
                List<TypedDeclaration> uninitialized = 
                        new ArrayList<TypedDeclaration>();
                if (body!=null) {
                    List<Tree.Statement> statements = 
                            body.getStatements();
                    for (int i=0; i<statements.size(); i++) {
                        Tree.Statement st = statements.get(i);
                        if (isExecutableStatement(st) || 
                                st instanceof Tree.Constructor) {
                            les = st;
                        }
                        if (st instanceof Tree.AttributeDeclaration) {
                            Tree.AttributeDeclaration ad = 
                                    (Tree.AttributeDeclaration) st;
                            if (ad.getSpecifierOrInitializerExpression()==null) {
                                uninitialized.add(ad.getDeclarationModel());
                            }
                        }
                        else if (st instanceof Tree.MethodDeclaration) {
                            Tree.MethodDeclaration ad = 
                                    (Tree.MethodDeclaration) st;
                            if (ad.getSpecifierExpression()==null) {
                                uninitialized.add(ad.getDeclarationModel());
                            }
                        }
                        else if (st instanceof Tree.SpecifierStatement) {
                            Tree.SpecifierStatement ss = 
                                    (Tree.SpecifierStatement) st;
                            Tree.Term bme = ss.getBaseMemberExpression();
                            if (bme instanceof Tree.BaseMemberExpression) {
                                uninitialized.remove(((Tree.BaseMemberExpression) bme).getDeclaration());
                            }
                        }
                    }
                }
                
                String defaultIndent = getDefaultIndent();
                String delim = getDefaultLineDelimiter(doc);
                String indent;
                indent = les==null ? 
                        getIndent(cd, doc) + defaultIndent : 
                        getIndent(les, doc);                
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
                    String type = 
                            dec.getProducedReference(null, Collections.<ProducedType>emptyList())
                               .getFullType().getProducedTypeName(unit);
                    params.append(type)
                          .append(" ")
                          .append(dec.getName());
                    initializers.append(indent)
                                .append(defaultIndent)
                                .append("this.")
                                .append(dec.getName())
                                .append(" = ")
                                .append(dec.getName())
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
                    if (body.getStopIndex()==start) {
                        text += delim + getIndent(cd, doc);
                    }
                }
                else {
                    start = les.getStopIndex()+1;
                }
                
                InsertEdit edit = new InsertEdit(start, text);
                change.setEdit(edit);
                
                int loc = start + text.indexOf('(') + 1;
                String name = cd.getDeclarationModel().getName();
                proposals.add(new CorrectionProposal(
                        "Add default constructor 'new (" + params + ")' of '" + name + "'", 
                        change, 
                        new Region(loc, 0)));
            }
        }
    }
    
    static boolean isExecutableStatement(Tree.Statement s) {
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
                    ProducedType et = o.getExtendedType().getType().getTypeModel();
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
    
}
