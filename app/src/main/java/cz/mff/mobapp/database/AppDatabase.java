package cz.mff.mobapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {ContactData.class}, version = 1, exportSchema = false)
@TypeConverters({DataConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
}
