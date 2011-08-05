package lpg.runtime;

import java.util.ArrayList;

public interface IPrsStream extends TokenStream
{
    IMessageHandler getMessageHandler();
    void setMessageHandler(IMessageHandler errMsg);

    ILexStream getILexStream();

    /**
     * @deprecated replaced by {@link #getILexStream()}
     */
    ILexStream getLexStream();
    
    void setLexStream(ILexStream lexStream);
    
    /**
     * @deprecated replaced by {@link #getFirstRealToken()}
     *
     */
    int getFirstErrorToken(int i);

    /**
     * @deprecated replaced by {@link #getLastRealToken()}
     *
     */
    int getLastErrorToken(int i);

    void makeToken(int startLoc, int endLoc, int kind);

    void makeAdjunct(int startLoc, int endLoc, int kind);
    
    void removeLastToken();

    int getLineCount();

    int getSize();
    
    void remapTerminalSymbols(String[] ordered_parser_symbols, int eof_symbol)
         throws UndefinedEofSymbolException,
                NullExportedSymbolsException,
                NullTerminalSymbolsException,
                UnimplementedTerminalsException;
    String[] orderedTerminalSymbols();
    
    int mapKind(int kind);
    
    void resetTokenStream();

    int getStreamIndex();
    
    void setStreamIndex(int index);

    void setStreamLength();

    void setStreamLength(int len);

    void addToken(IToken token);
    
    void addAdjunct(IToken adjunct);
    
    String[] orderedExportedSymbols();

    ArrayList getTokens();
    
    ArrayList getAdjuncts();

    IToken[] getFollowingAdjuncts(int i);

    IToken[] getPrecedingAdjuncts(int i);

    IToken getIToken(int i);

    String getTokenText(int i);
    
    int getStartOffset(int i);
    
    int getEndOffset(int i);
    
    int getLineOffset(int i);
    
    int getLineNumberOfCharAt(int i);

    int getColumnOfCharAt(int i);
    
    int getTokenLength(int i);

    int getLineNumberOfTokenAt(int i);

    int getEndLineNumberOfTokenAt(int i);

    int getColumnOfTokenAt(int i);

    int getEndColumnOfTokenAt(int i);

    char [] getInputChars();

    byte [] getInputBytes();
    
    String toString(int first_token, int last_token);

    String toString(IToken t1, IToken t2);

    int getTokenIndexAtCharacter(int offset);
    
    IToken getTokenAtCharacter(int offset);
    
    IToken getTokenAt(int i);
    
    void dumpTokens();
    
    void dumpToken(int i);
    
    int makeErrorToken(int first, int last, int error, int kind);
}
