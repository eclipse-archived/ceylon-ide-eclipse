package lpg.runtime;

public class TokenStreamNotIPrsStreamException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public TokenStreamNotIPrsStreamException()
    {
        str = "TokenStreamNotIPrsStreamException";
    }
    public TokenStreamNotIPrsStreamException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
