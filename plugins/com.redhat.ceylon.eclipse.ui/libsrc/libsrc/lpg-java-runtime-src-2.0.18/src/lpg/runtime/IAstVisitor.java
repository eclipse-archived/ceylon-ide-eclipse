package lpg.runtime;

public interface IAstVisitor
{
    boolean preVisit(IAst element);
    void postVisit(IAst element);
}
