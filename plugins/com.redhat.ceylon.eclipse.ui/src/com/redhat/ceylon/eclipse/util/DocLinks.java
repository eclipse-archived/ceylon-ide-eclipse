package org.eclipse.ceylon.ide.eclipse.util;

import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Region;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree.DocLink;

public class DocLinks {
    
    private static final Pattern packagePattern = compile("^([^|]*\\|)?([^:]*)::");
    private static final Pattern namePattern = compile("^([^|]*\\|)?([^:]*::)?(.*)");
    
    public static boolean hasPackage(DocLink docLink) {
        return docLink.getText().contains("::");
    }
    
    public static Region packageRegion(DocLink docLink) {
        Matcher matcher = packagePattern.matcher(docLink.getText());
        if (matcher.find()) {
            int offset = docLink.getStartIndex();
            return new Region(offset+matcher.start(2), matcher.end(2));
        }
        else {
            return null;
        }
    }

    public static String packageName(DocLink docLink) {
        Matcher matcher = packagePattern.matcher(docLink.getText());
        if (matcher.find()) {
            return matcher.group(2);
        }
        else {
            return null;
        }
    }

    public static Region nameRegion(DocLink docLink, int i) {
        Matcher matcher = namePattern.matcher(docLink.getText());
        if (matcher.find()) {
            String path = matcher.group(3);
            int start = matcher.start(3);
            int startIndex = 0;
            for (int j=0; j<i; j++) {
                startIndex = path.indexOf('.', startIndex)+1;
                if (startIndex==0) {
                    return null;
                }
            }
            int stopIndex = path.indexOf('.', startIndex);
            if (stopIndex<0) stopIndex = path.length();
            int offset = docLink.getStartIndex();
            return new Region(offset+start+startIndex, stopIndex-startIndex);
        }
        else {
            return null;
        }
    }
    
    public static String name(DocLink docLink, int i) {
        Matcher matcher = namePattern.matcher(docLink.getText());
        if (matcher.find()) {
            String path = matcher.group(3);
            int startIndex = 0;
            for (int j=0; j<i; j++) {
                startIndex = path.indexOf('.', startIndex)+1;
                if (startIndex==0) {
                    return null;
                }
            }
            int stopIndex = path.indexOf('.', startIndex);
            if (stopIndex<0) stopIndex = path.length();
            return path.substring(startIndex, stopIndex);
        }
        else {
            return null;
        }
    }
}
