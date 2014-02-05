package com.redhat.ceylon.eclipse.code.propose;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DocLink {

	String declName;
	int declStart;
	
	static DocLink getDocLink(String text, int offsetInToken, final int offset) {     
		
		Matcher docLinkMatcher = Pattern.compile("\\[\\[(.*?)\\]\\]").matcher(text);
		if (text==null || offset==0 || !docLinkMatcher.find()) {
			return null;
		}

		DocLink docLink = null;
		docLinkMatcher.reset();
		while (docLinkMatcher.find()) {
			for (int i = 1; i <= docLinkMatcher.groupCount(); i++) { // loop for safety
				if (offsetInToken >= docLinkMatcher.start(i) && 
						offsetInToken <= docLinkMatcher.end(i)) {
					docLink = new DocLink();
					String docLinkText = docLinkMatcher.group(i);
					int separatorIndex = docLinkText.indexOf("|");
					if (separatorIndex > -1) {
						//docLink.linkName = docLinkText.substring(0, separatorIndex);
						docLink.declName = docLinkText.substring(separatorIndex+1);
						docLink.declStart = docLinkMatcher.start(i)+separatorIndex+1;
					}
					else {
						docLink.declName = docLinkText;
						docLink.declStart = docLinkMatcher.start(i);
					}
					break;
				}
			}
		}

		if (docLink == null) { // it will be empty string if we are in a wiki ref
			return null;
		}

		return docLink;
	}

}