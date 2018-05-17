package cz.mff.mobapp.event;

public interface Updater<F, T> {
    void update(F from, T to) throws Exception;
}