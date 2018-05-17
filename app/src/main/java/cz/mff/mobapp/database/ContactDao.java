package cz.mff.mobapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.UUID;

@Dao
public interface ContactDao extends GenericDao<ContactData> {
    @Query("SELECT * FROM contactdata")
    List<ContactData> getAllContacts();

    @Query("SELECT * FROM contactdata WHERE id IN (:ids)")
    List<ContactData> loadAllByIds(UUID[] ids);

    @Query("SELECT * FROM contactdata WHERE id = (:id)")
    ContactData findById(UUID id);

    @Insert
    void insertAll(ContactData... contact);

    @Insert
    void insert(ContactData contact);

    @Update
    void update(ContactData contact);

    @Query("DELETE FROM contactdata WHERE id = (:id)")
    void delete(ContactData contact);
}
