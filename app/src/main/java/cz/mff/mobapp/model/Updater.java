package cz.mff.mobapp.model;

@FunctionalInterface
public interface Updater<F, T> {
    void update(F from, T to) throws Exception;
}