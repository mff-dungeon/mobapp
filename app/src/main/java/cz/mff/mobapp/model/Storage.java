package cz.mff.mobapp.model;

import java.util.Collection;

import cz.mff.mobapp.event.Listener;

public interface Storage<T, I> {
    default void retrieve(I id, Listener<? super T> listener) {
        listener.doCatch(new UnsupportedOperationException("No."));
    }

    default void delete(I id, Listener<Void> listener) {
        listener.doCatch(new UnsupportedOperationException("No."));
    }

    default void create(T object, Listener<? super T> listener) {
        listener.doCatch(new UnsupportedOperationException("No."));
    }

    default void update(T object, Listener<? super T> listener) {
        listener.doCatch(new UnsupportedOperationException("No."));
    }

    default void listAll(Listener<T[]> listener) {
        listener.doCatch(new UnsupportedOperationException("No."));
    }

    default void listByIDs(I[] ids, Listener<T[]> listener) {
        listener.doCatch(new UnsupportedOperationException("No."));
    }


}
