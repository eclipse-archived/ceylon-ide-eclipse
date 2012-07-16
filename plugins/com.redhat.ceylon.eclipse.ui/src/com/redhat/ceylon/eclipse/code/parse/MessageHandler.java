package com.redhat.ceylon.eclipse.code.parse;

import java.util.Map;

import org.eclipse.core.resources.IMarker;

/**
 * This interface describes something that can process messages 
 * emitted by a parser or other language processing front-end.
 */
public interface MessageHandler 
        extends org.eclipse.imp.parser.IMessageHandler {
    /**
     * Attribute key for the severity of a message
     */
    public static final String SEVERITY_KEY= IMarker.SEVERITY;

    /**
     * Attribute key for the error code associated with a given message.
     * Used to correlate messages with quick fixes.
     */
    public static final String ERROR_CODE_KEY= "errorCode";

    /**
     * Clear all previously-issued messages. Typically called at the
     * beginning of a parsing "session".
     */
    void clearMessages();

    /**
     * Marks the end of a session of messages. Permits batching of message
     * handling operations for greater efficiency.
     */
    void endMessages();

    /**
     * Issue a single message with the given text and source position.
     * @param msg the message text
     * @param startOffset 0-based, inclusive
     * @param endOffset 0-based, inclusive
     * @param startCol 1-based, inclusive
     * @param endCol 1-based, inclusive
     * @param startLine 1-based, inclusive
     * @param endLine 1-based, inclusive
     */
    void handleSimpleMessage(String msg, int startOffset, int endOffset,
                             int startCol, int endCol, int startLine, int endLine);

    /**
     * Issue a single message with the given text and source position.
     * @param msg the message text
     * @param startOffset 0-based, inclusive
     * @param endOffset 0-based, inclusive
     * @param startCol 1-based, inclusive
     * @param endCol 1-based, inclusive
     * @param startLine 1-based, inclusive
     * @param endLine 1-based, inclusive
     * @param attributes map of additional attributes
     */
    void handleSimpleMessage(String msg, int startOffset, int endOffset,
                             int startCol, int endCol, int startLine, int endLine,
                             Map<String, Object> attributes);

    /**
     * Begins a group of related messages (e.g. the first describing an error
     * relating multiple entities, identifying the position of the first entity,
     * followed by individual messages, one per additional entity).
     * @param groupName
     */
    void startMessageGroup(String groupName);

    /**
     * Ends a group of related messages
     */
    void endMessageGroup();
}
