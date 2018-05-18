package cz.mff.mobapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;

public class Manager<T extends Identifiable<I>, I> {

    private final Storage<T, I> storage;
    public final EntityHandler<T> handler;

    public Manager(Storage<T, I> storage, EntityHandler<T> handler) {
        this.storage = storage;
        this.handler = handler;
    }

    public void retrieve(I id, Listener<? super T> listener) {
        storage.retrieve(id, listener);
    }

    public void save(T object, Listener<? super T> listener) {
        if (object.getId() != null)
            storage.update(object, new TryCatch<>(updated -> {
                handler.update(updated, object);
                listener.doTry(object);
            }, listener));
        else
            storage.create(object, new TryCatch<>(updated -> {
                handler.update(updated, object);
                listener.doTry(object);
            }, listener));
    }

    public void delete(I id, Listener<Void> listener) {
        storage.delete(id, listener);
    }

    public void listAll(Listener<ArrayList<T>> listener) {
        storage.listAll(listener);
    }

    public void listByIDs(I[] ids, Listener<ArrayList<T>> listener) {
        storage.listByIDs(ids, listener);
    }
}
