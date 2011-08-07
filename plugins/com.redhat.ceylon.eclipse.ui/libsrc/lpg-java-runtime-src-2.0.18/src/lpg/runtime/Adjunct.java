package lpg.runtime;

public class Adjunct extends AbstractToken
{
    public Adjunct() {}
    public Adjunct(IPrsStream prsStream, int startOffset, int endOffset, int kind)
    {
        super(prsStream, startOffset, endOffset, kind);
    }
    public IToken[] getFollowingAdjuncts() { return null; }
    public IToken[] getPrecedingAdjuncts() { return null; }
}

