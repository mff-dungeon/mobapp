package cz.mff.mobapp.database;

import java.util.UUID;
import java.util.concurrent.Executor;

import cz.mff.mobapp.event.Listener;
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
        executor.execute(() -> {
            E retrieved = dao.findById(id);
            T result = daoMapper.createObject();

            if(retrieved != null && retrieved.getId() != null) {
                try {
                    daoMapper.convertFromDao(retrieved, result);
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

    @Override
    public void update(T object, Listener<? super T> listener) {
        executor.execute(() -> {
            E daoObject = daoMapper.createDao();
            daoMapper.convertToDao(object, daoObject);
            dao.update(daoObject);

            if(listener != null) {
                try {
                    listener.doTry(object);
                }
                catch (Exception e) {
                    listener.doCatch(e);
                }
            }
        });
    }

    @Override
    public void create(T object, Listener<? super T> listener) {
        assert object.getId() == null;

        executor.execute(() -> {
            E daoObject = daoMapper.createDao();
            daoMapper.convertToDao(object, daoObject);
            dao.insert(daoObject);

            if(listener != null) {
                try {
                    listener.doTry(object);
                } catch (Exception e) {
                    listener.doCatch(e);
                }
            }
        });
    }

    @Override
    public void delete(UUID id, Listener<Void> listener) {
        executor.execute(() -> {
            dao.delete(id);
            if(listener != null) {
                try {
                    listener.doTry(null);
                } catch (Exception e) {
                    listener.doCatch(e);
                }
            }
        });
    }
}