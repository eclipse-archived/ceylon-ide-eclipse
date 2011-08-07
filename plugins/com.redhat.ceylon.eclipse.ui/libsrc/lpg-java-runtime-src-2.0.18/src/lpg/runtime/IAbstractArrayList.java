package lpg.runtime;

public interface IAbstractArrayList<T extends IAst> {
    public int size();
    public T getElementAt(int i);
    public java.util.List<T> getList();
    public void add(T elt);
    public java.util.List<T> getAllChildren();
}
