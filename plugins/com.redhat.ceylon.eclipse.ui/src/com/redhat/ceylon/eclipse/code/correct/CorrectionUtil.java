package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.model.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.ProducedType;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

class CorrectionUtil {
    
    static int getLevenshteinDistance(String x, String y) {
        
        int n = x.length(); // length of s
        int m = y.length(); // length of t
        
        if (n == 0) return m;
        if (m == 0) return n;
        
        int p[] = new int[n+1]; //'previous' cost array, horizontally
        int d[] = new int[n+1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d
        
        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t
        
        char t_j; // jth character of t
        
        int cost; // cost
        
        for (i = 0; i<=n; i++) {
           p[i] = i;
        }
        
        for (j = 1; j<=m; j++) {
           t_j = y.charAt(j-1);
           d[0] = j;
           
           for (i=1; i<=n; i++) {
              cost = x.charAt(i-1)==t_j ? 0 : 1;
              // minimum of cell to the left+1, to the top+1, diagonally left and up +cost                
              d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);  
           }
           
           // copy current distance counts to 'previous row' distance counts
           _d = p;
           p = d;
           d = _d;
        } 
        
        // our last action in the above loop was to switch d and p, so p now 
        // actually has the most recent cost counts
        return p[n];
    }
    
    static Tree.Body getClassOrInterfaceBody(Tree.Declaration decNode) {
        if (decNode instanceof Tree.ClassDefinition) {
            return ((Tree.ClassDefinition) decNode).getClassBody();
        }
        else if (decNode instanceof Tree.InterfaceDefinition){
            return ((Tree.InterfaceDefinition) decNode).getInterfaceBody();
        }
        else if (decNode instanceof Tree.ObjectDefinition){
            return ((Tree.ObjectDefinition) decNode).getClassBody();
        }
        else {
            return null;
        }
    }

    static Tree.CompilationUnit getRootNode(PhasedUnit unit) {
        IEditorPart ce = getCurrentEditor();
        if (ce instanceof CeylonEditor) {
            CeylonParseController cpc = ((CeylonEditor) ce).getParseController();
            if (cpc!=null) {
                Tree.CompilationUnit rn = cpc.getRootNode();
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
    
    static String asIntersectionTypeString(List<ProducedType> types) {
        StringBuffer missingSatisfiedTypesText = new StringBuffer();
        for( ProducedType missingSatisfiedType: types ) {
            if( missingSatisfiedTypesText.length() != 0 ) {
                missingSatisfiedTypesText.append(" & ");
            }
            missingSatisfiedTypesText.append(missingSatisfiedType.getProducedTypeName());   
        }
        return missingSatisfiedTypesText.toString();
    }
    
    static String defaultValue(Unit unit, ProducedType t) {
        if (isTypeUnknown(t)) {
            return "nothing";
        }
        TypeDeclaration tn = t.getDeclaration();
        if(tn.isAlias()){
            return defaultValue(unit, tn.getExtendedType());
        }
        boolean isClass = tn instanceof Class;
        if (unit.isOptionalType(t)) {
            return "null";
        }
        else if (isClass &&
                tn.equals(unit.getBooleanDeclaration())) {
            return "false";
        }
        else if (isClass &&
                tn.equals(unit.getIntegerDeclaration())) {
            return "0";
        }
        else if (isClass &&
                tn.equals(unit.getFloatDeclaration())) {
            return "0.0";
        }
        else if (isClass &&
                tn.equals(unit.getStringDeclaration())) {
            return "\"\"";
        }
        else if (isClass &&
                tn.equals(unit.getByteDeclaration())) {
            return "0.byte";
        }
        else if (isClass &&
                tn.equals(unit.getTupleDeclaration())) {
            final int minimumLength = unit.getTupleMinimumLength(t);
            final List<ProducedType> tupleTypes = unit.getTupleElementTypes(t);
            final StringBuilder sb = new StringBuilder();
            for(int i = 0 ; i < minimumLength ; i++){
                sb.append(sb.length() == 0 ? "[" : ", ");
                ProducedType currentType = tupleTypes.get(i);
                if(unit.isSequentialType(currentType)){
                    currentType = unit.getSequentialElementType(currentType);
                }
                sb.append(defaultValue(unit, currentType));
            }
            sb.append(']');
            return sb.toString();
        } 
        else if (unit.isSequentialType(t)) {
            final StringBuilder sb = new StringBuilder();
            sb.append('[');
            if (!unit.getEmptyDeclaration().getType().isSubtypeOf(t)) {
                sb.append(defaultValue(unit, unit.getSequentialElementType(t)));
            }
            sb.append(']');
            return sb.toString();
        }
        else if (unit.isIterableType(t)) {
            final StringBuilder sb = new StringBuilder();
            sb.append('{');
            if (!unit.getEmptyDeclaration().getType().isSubtypeOf(t)) {
                sb.append(defaultValue(unit, unit.getIteratedType(t)));
            }
            sb.append('}');
            return sb.toString();
        }
        else {
            return "nothing";
        }
    }
    
    static Region computeSelection(int offset, String def) {
        int length;
        int loc = def.indexOf("= nothing");
        if (loc<0) loc = def.indexOf("=> nothing");
        if (loc<0) {
            loc = def.indexOf("= ");
            if (loc<0) loc = def.indexOf("=> ");
            if (loc<0) {
                loc = def.indexOf("{")+1;
                length=0;
            }
            else {
                loc = def.indexOf(" ", loc)+1;
                int semi = def.indexOf(";", loc);
                length = semi<0 ? def.length()-loc:semi-loc;
            }
        }
        else {
            loc = def.indexOf(" ", loc)+1;
            length = 7;
        }
        return new Region(offset + loc, length);
    }

    static String getDescription(Declaration dec) {
        String desc = "'" + dec.getName() + "'";
        Scope container = dec.getContainer();
        if (container instanceof TypeDeclaration) {
            desc += " in '" + ((TypeDeclaration) container).getName() + "'";
        }
        return desc;
    }

    static Node getBeforeParenthesisNode(Tree.Declaration decNode) {
        Node n = decNode.getIdentifier();
        if (decNode instanceof Tree.TypeDeclaration) {
            Tree.TypeParameterList tpl = 
                    ((Tree.TypeDeclaration) decNode)
                            .getTypeParameterList();
            if (tpl!=null) {
                n = tpl;
            }
        }
        if (decNode instanceof Tree.AnyMethod) {
            Tree.TypeParameterList tpl = 
                    ((Tree.AnyMethod) decNode)
                            .getTypeParameterList();
            if (tpl!=null) {
                n = tpl;
            }
        }
        return n;
    }

    static List<TypedDeclaration> collectUninitializedMembers(
            Tree.Body body) {
        List<TypedDeclaration> uninitialized = 
                new ArrayList<TypedDeclaration>();
        if (body!=null) {
            List<Tree.Statement> statements = 
                    body.getStatements();
            for (Tree.Statement st: statements) {
                if (st instanceof Tree.AttributeDeclaration) {
                    Tree.AttributeDeclaration ad = 
                            (Tree.AttributeDeclaration) st;
                    if (ad.getSpecifierOrInitializerExpression()==null &&
                            !ad.getDeclarationModel().isFormal()) {
                        uninitialized.add(ad.getDeclarationModel());
                    }
                }
                else if (st instanceof Tree.MethodDeclaration) {
                    Tree.MethodDeclaration md = 
                            (Tree.MethodDeclaration) st;
                    if (md.getSpecifierExpression()==null &&
                            !md.getDeclarationModel().isFormal()) {
                        uninitialized.add(md.getDeclarationModel());
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
        return uninitialized;
    }
    
}
