package lpg.runtime;

public class UnavailableParserInformationException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public UnavailableParserInformationException()
    {
        str = "Unavailable parser Information Exception";
    }
    public UnavailableParserInformationException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
