package cz.mff.mobapp.event;

@FunctionalInterface
public interface Listener<T> {
    void fire(T data) throws Exception;
}
