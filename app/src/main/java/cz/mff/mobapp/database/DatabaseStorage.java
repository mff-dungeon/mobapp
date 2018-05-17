package cz.mff.mobapp.database;

import java.util.UUID;
import java.util.concurrent.Executor;

import cz.mff.mobapp.api.Serializer;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.model.Updater;
import cz.mff.mobapp.model.Factory;
import cz.mff.mobapp.model.Identifiable;
import cz.mff.mobapp.model.Storage;

public class DatabaseStorage <T, E extends Identifiable<UUID>> implements Storage<T, UUID> {

    private final GenericDao<E> dao;
    private final Executor executor;
    private final Updater<E, T> updater;
    private final Serializer<T> serializer;
    private final Factory<T> factory;

    public DatabaseStorage(GenericDao<E> dao, Executor executor,
                           Updater<E, T> updater, Serializer<T> serializer, Factory<T> factory) {
        this.dao = dao;
        this.executor = executor;
        this.updater = updater;
        this.serializer = serializer;
        this.factory = factory;
    }

    @Override
    public void retrieve(UUID id, Listener<? super T> listener) {
        executor.execute(() -> {
            E retrieved = dao.findById(id);
            T result = factory.create();

            if(retrieved != null && retrieved.getId() != null) {
                try {
                    updater.update(retrieved, result);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                listener.doTry(result);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}