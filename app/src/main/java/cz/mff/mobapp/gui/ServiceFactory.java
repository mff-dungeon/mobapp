package cz.mff.mobapp.gui;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.UUID;
import java.util.concurrent.Executors;

import cz.mff.mobapp.api.APIStorage;
import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.SerializerFactory;
import cz.mff.mobapp.database.AppDatabase;
import cz.mff.mobapp.database.ContactData;
import cz.mff.mobapp.database.DaoMapperFactory;
import cz.mff.mobapp.database.DatabaseStorage;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Manager;

public class ServiceFactory {

    private final Activity activity;
    private static final String DB_NAME = "test-database2";

    public ServiceFactory(Activity activity) {
        this.activity = activity;
    }

    public Requester createRequester()
    {
        Requester r = new Requester("test", "test");
        r.initializeQueue(activity);
        return r;
    }

    public APIStorage<Contact, UUID> createContactAPIStorage()
    {
        return new APIStorage<>("contacts", createRequester(), SerializerFactory.getContactSerializer(), Contact::new, Contact::copy);
    }

    public Manager<Contact, UUID> createContactAPIManager()
    {
        return new Manager<>(createContactAPIStorage(), Contact::copy);
    }

    // TODO: This will change return type - Interface?
    public Manager<Contact, UUID> createContactManager()
    {
        // TODO: return ApplicationManager once ready
        return createContactAPIManager();
    }

    public AppDatabase createAppDatabase() {
        return Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, DB_NAME).build();
    }

    public DatabaseStorage<Contact, ContactData> createDatabaseStorage() {
        return new DatabaseStorage<>(createAppDatabase().contactDao(), Executors.newSingleThreadExecutor(),
                DaoMapperFactory.getContactDaoMapper());
    }

}
