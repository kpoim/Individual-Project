package dao;

public interface Dao <E>{
    boolean create(E e);
    boolean checkIfExists(E e);
    E findById(int id);
}
