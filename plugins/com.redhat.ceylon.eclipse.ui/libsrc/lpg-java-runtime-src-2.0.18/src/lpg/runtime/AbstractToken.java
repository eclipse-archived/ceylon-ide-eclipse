package lpg.runtime;

public abstract class AbstractToken implements IToken
{
    private int kind = 0,
                startOffset = 0,
                endOffset = 0,
                tokenIndex = 0,
                adjunctIndex;
    private IPrsStream iPrsStream;

    public AbstractToken() {}
    public AbstractToken(IPrsStream iPrsStream, int startOffset, int endOffset, int kind)
    {
        this.iPrsStream = iPrsStream;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.kind = kind;
    }

    public int getKind() { return kind; }
    public void setKind(int kind) { this.kind = kind; }

    public int getStartOffset() { return startOffset; }
    public void setStartOffset(int startOffset)
    {
        this.startOffset = startOffset;
    }

    public int getEndOffset() { return endOffset; }
    public void setEndOffset(int endOffset)
    {
        this.endOffset = endOffset;
    }

    public int getTokenIndex() { return tokenIndex; }
    public void setTokenIndex(int tokenIndex) { this.tokenIndex = tokenIndex; }

    public void setAdjunctIndex(int adjunctIndex) { this.adjunctIndex = adjunctIndex; }
    public int getAdjunctIndex() { return adjunctIndex; }
    
    public IPrsStream getIPrsStream() { return iPrsStream; }
    public ILexStream getILexStream() { return iPrsStream == null ? null : iPrsStream.getILexStream(); }
    public int getLine() { return (iPrsStream == null ? 0 : iPrsStream.getILexStream().getLineNumberOfCharAt(startOffset)); }
    public int getColumn() { return (iPrsStream == null ? 0 : iPrsStream.getILexStream().getColumnOfCharAt(startOffset)); }
    public int getEndLine() { return (iPrsStream == null ? 0 : iPrsStream.getILexStream().getLineNumberOfCharAt(endOffset)); }
    public int getEndColumn() { return (iPrsStream == null ? 0 : iPrsStream.getILexStream().getColumnOfCharAt(endOffset)); }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     */
    public IPrsStream getPrsStream() { return iPrsStream; }


    /**
     * @deprecated replaced by {@link #getILexStream()}
     */
    public ILexStream getLexStream() { return iPrsStream == null ? null : iPrsStream.getILexStream(); }

    /**
     * @deprecated replaced by {@link #toString()}
     */
    public String getValue(char[] inputChars)
    {
        if (iPrsStream != null)
            return toString();
        if (iPrsStream.getLexStream() instanceof LexStream)
        {
            LexStream lex_stream = (LexStream) iPrsStream.getLexStream();
            if (inputChars != lex_stream.getInputChars())
                throw new MismatchedInputCharsException();
            return toString();
        }
        throw new UnknownStreamType("Unknown stream type " +
                                    iPrsStream.getLexStream().getClass().toString());
    }

    public String toString()
    {
        return (iPrsStream == null
                           ? "<toString>"
                           : iPrsStream.toString(this, this));
    }
}