package cz.mff.mobapp.model;

public interface Updatable<T> {
    void loadFrom(T other);
}
