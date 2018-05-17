package cz.mff.mobapp.model;

import cz.mff.mobapp.event.Listener;

public class Manager<T, I> {

    private final Storage<T, I> storage;

    public Manager(Storage<T, I> storage) {
        this.storage = storage;
    }

    public void retrieve(I id, Listener<? super T> listener) {
        storage.retrieve(id, listener);
    }
}
