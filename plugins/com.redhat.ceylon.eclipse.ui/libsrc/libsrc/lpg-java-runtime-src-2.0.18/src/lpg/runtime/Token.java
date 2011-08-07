package lpg.runtime;

public class Token extends AbstractToken
{
    public Token() {}
    public Token(int startOffset, int endOffset, int kind)
    {
        super(null, startOffset, endOffset, kind);
    }
    public Token(IPrsStream iPrsStream, int startOffset, int endOffset, int kind)
    {
        super(iPrsStream, startOffset, endOffset, kind);
    }

    //
    // Return an iterator for the adjuncts that follow token i.
    //
    public IToken[] getFollowingAdjuncts()
    {
        return getIPrsStream().getFollowingAdjuncts(getTokenIndex());
    }

    public IToken[] getPrecedingAdjuncts()
    {
        return getIPrsStream().getPrecedingAdjuncts(getTokenIndex());
    }

}
