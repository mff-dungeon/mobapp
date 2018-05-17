package cz.mff.mobapp.database;

import java.util.List;
import java.util.UUID;

public interface GenericDao<T> {
    List<T> getAllContacts();

    List<T> loadAllByIds(UUID[] ids);

    T findById(UUID uuid);

    void insert(T object);

    void insertAll(T... object);

    void update(T object);

    void delete(UUID id);
}
