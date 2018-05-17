package cz.mff.mobapp.database;

import java.util.UUID;
import java.util.concurrent.Executor;

import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Identifiable;
import cz.mff.mobapp.model.Storage;

public class DatabaseStorage <T extends Identifiable<UUID>, E extends Identifiable<UUID>>
        implements Storage<T, UUID> {

    private final GenericDao<E> dao;
    private final Executor executor;
    private final DaoMapper<T, E> daoMapper;

    public DatabaseStorage(GenericDao<E> dao, Executor executor, DaoMapper<T, E> daoMapper) {
        this.dao = dao;
        this.executor = executor;
        this.daoMapper = daoMapper;
    }

    @Override
    public void retrieve(UUID id, Listener<? super T> listener) {
        TryCatch<? super T> tryCatch = new TryCatch<>(listener);

        executor.execute(() -> {
            E retrieved = dao.findById(id);
            T result = daoMapper.createObject();

            if(retrieved != null && retrieved.getId() != null) {
                daoMapper.convertFromDao(retrieved, result);
                tryCatch.doTry(result);
            } else {
                tryCatch.doTry(null);
            }
        });
    }

    @Override
    public void update(T object, Listener<? super T> listener) {
        TryCatch<? super T> tryCatch = new TryCatch<>(listener);

        executor.execute(() -> {
            E daoObject = daoMapper.createDao();
            daoMapper.convertToDao(object, daoObject);
            dao.update(daoObject);
            tryCatch.doTry(object);
        });
    }

    @Override
    public void create(T object, Listener<? super T> listener) {
        assert object.getId() == null;
        TryCatch<? super T> tryCatch = new TryCatch<>(listener);

        executor.execute(() -> {
            E daoObject = daoMapper.createDao();
            daoMapper.convertToDao(object, daoObject);
            dao.insert(daoObject);
            tryCatch.doTry(object);
        });
    }

    @Override
    public void delete(UUID id, Listener<Void> listener) {
        TryCatch<Void> tryCatch = new TryCatch<>(listener);

        executor.execute(() -> {
            dao.delete(id);
            tryCatch.doTry(null);
        });
    }
}