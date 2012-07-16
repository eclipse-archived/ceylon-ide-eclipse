package com.redhat.ceylon.eclipse.code.parse;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.IRegion;

public class CeylonLanguageSyntaxProperties {
    
    public static final CeylonLanguageSyntaxProperties INSTANCE = new CeylonLanguageSyntaxProperties();
    
	public boolean isWhitespace(char ch) {
		return ch==' '||ch=='\r'||ch=='\n'||ch=='\t'||ch=='\u000C';
	}
    
	public boolean isIdentifierStart(char ch) {
		return Character.isJavaIdentifierStart(ch) && ch!='$';
	}
    
	public boolean isIdentifierPart(char ch) {
		return Character.isJavaIdentifierPart(ch) && ch!='$';
	}
    
	public String getSingleLineCommentPrefix() {
		return "//";
	}
    
	public String getIdentifierConstituentChars() {
		return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
	}
    
	public int[] getIdentifierComponents(String ident) {
		//this doesn't seem to actually work...
        List<Integer> listResult= new LinkedList<Integer>();
        for (int i=1; i<ident.length(); i++) {
            if ((Character.isLowerCase(ident.charAt(i-1)) && 
            		Character.isUpperCase(ident.charAt(i)) 
            		    || ident.charAt(i) == '_')) {
                listResult.add(i);
            }
        }
        int[] result = new int[listResult.size()];
        for (int i=0; i<listResult.size(); i++) {
            result[i++] = listResult.get(i);
        }
        return result;
	}
    
	public String[][] getFences() {
		return new String[][] { { "(", ")" }, { "[", "]" }, { "{", "}" } };
	}
    
	public IRegion getDoubleClickRegion(int offset, CeylonParseController pc) {
		//this seems to be unnecessary ... default behavior is fine
	    /*CommonTokenStream stream = (CommonTokenStream) parser.getTokenStream();
	    if (stream!=null) {
	      List<Token> tokens = stream.getTokens();
	      int firstTokIdx= getTokenIndexAtCharacter(tokens, offset);
	      CommonToken token = (CommonToken) tokens.get(firstTokIdx);
	      return new Region(token.getStartIndex(), token.getText().length());
	    }
	    else {
	      return null;
	    }*/
		return null;
	}
    
	public String getBlockCommentStart() {
		return "/*";
	}
    
	public String getBlockCommentEnd() {
		return "*/";
	}
    
	public String getBlockCommentContinuation() {
		return null;
	}
}