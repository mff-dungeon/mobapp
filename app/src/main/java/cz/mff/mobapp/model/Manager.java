package cz.mff.mobapp.model;

import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;

public class Manager<T extends Identifiable<I> & Updatable<T>, I> {

    private final Storage<T, I> storage;

    public Manager(Storage<T, I> storage) {
        this.storage = storage;
    }

    public void retrieve(I id, Listener<? super T> listener) {
        storage.retrieve(id, listener);
    }

    public void save(T object, Listener<? super T> listener) {
        if (object.getId() != null)
            storage.update(object, new TryCatch<>(updated -> {
                object.loadFrom(updated);
                listener.doTry(object);
            }, listener));
        else
            storage.create(object, new TryCatch<>(updated -> {
                object.loadFrom(updated);
                listener.doTry(object);
            }, listener));
    }
}
