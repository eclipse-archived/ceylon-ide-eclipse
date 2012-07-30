package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;

import org.eclipse.jface.text.ITextSelection;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class Util {
    
    public static int getLevenshteinDistance(String x, String y) {
        
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

	//TODO: copy/pasted from AbstractFindAction
	public static Node getSelectedNode(CeylonEditor editor) {
	    CeylonParseController cpc = editor.getParseController();
	    return cpc==null || cpc.getRootNode()==null ? null : 
	        findNode(cpc.getRootNode(), 
	            (ITextSelection) editor.getSelectionProvider().getSelection());
	}
    
}
