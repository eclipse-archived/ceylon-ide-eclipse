package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static org.eclipse.jdt.core.IJavaModelMarker.BUILDPATH_PROBLEM_MARKER;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;

/**
 * This class provides a message handler that creates markers in
 * response to received messages.
 * 
 * MarkerCreators are instantiated with a file (IFile) and a parse
 * controller (IParseController).  The parse controller should
 * be parsing the file and generating the messages that are
 * received by the MarkerCreator.  The MarkerCreator, in turn,
 * creates a problem marker for each error message received,
 * uses the parse controller to compute a line number for the
 * token provided with each message, and attaches the marker to
 * the given file at the computed line.
 */
public class MarkerCreator extends ErrorVisitor {
	
    protected IFile file;
	public static final String ERROR_CODE_KEY= "errorCode";

    public MarkerCreator(IFile file) {
        this.file = file;
    }

    @Override
    public void handleMessage(int startOffset, int endOffset,
			int startCol, int startLine, Message message) {
    	
    	String[] attributeNames= new String[] {
		        IMarker.LINE_NUMBER, 
		        IMarker.CHAR_START, IMarker.CHAR_END, 
		        IMarker.MESSAGE, 
		        IMarker.PRIORITY, 
		        IMarker.SEVERITY,
		        ERROR_CODE_KEY,
		        IMarker.SOURCE_ID
			};
		Object[] values= new Object[] {
		        startLine, 
		        startOffset, endOffset+1, 
		        message.getMessage(), 
		        IMarker.PRIORITY_HIGH, 
		        getSeverity(message, warnForErrors),
		        message.getCode(),
		        CeylonBuilder.SOURCE
			};
		try {
		    file.createMarker(isCompilerError(message.getMessage())?
		    		BUILDPATH_PROBLEM_MARKER:PROBLEM_MARKER_ID)
		        .setAttributes(attributeNames, values);
		} 
		catch (Exception e) {
		    e.printStackTrace();
		}
	}

	private static boolean isCompilerError(String msg) {
        //TODO: we need a MUCH better way to distinguish 
        //      compiler errors from typechecker errors
		return msg.startsWith("cannot find module") || 
				msg.startsWith("unable to read source artifact for");
	}
	
    public int getSeverity(Message error, boolean expected) {
        return expected || error instanceof UsageWarning ? 
        		IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR;
    }
    
}
