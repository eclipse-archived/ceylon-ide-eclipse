package lpg.runtime;

public class UnknownStreamType extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public UnknownStreamType()
    {
        str = "UnknownStreamType";
    }
    public UnknownStreamType(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
