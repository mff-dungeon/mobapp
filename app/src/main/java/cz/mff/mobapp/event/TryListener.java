package cz.mff.mobapp.event;

@FunctionalInterface
public interface TryListener<T> {
    void doTry(T data) throws Exception;
}
