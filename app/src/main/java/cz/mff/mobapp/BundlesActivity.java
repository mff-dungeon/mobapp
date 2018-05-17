package cz.mff.mobapp;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cz.mff.mobapp.api.SerializerFactory;
import cz.mff.mobapp.database.AppDatabase;
import cz.mff.mobapp.database.ContactData;
import cz.mff.mobapp.database.DatabaseStorage;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.model.Contact;

public class BundlesActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();


        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "test-database2").build();

        DatabaseStorage<Contact, ContactData> storage =
                new DatabaseStorage<>(db.contactDao(), Executors.newSingleThreadExecutor(),
                (from, to) -> {
                    to.setId(from.getId());
                    to.setLastModified(from.getLastModified());
                    to.setContact(true);
                },
                SerializerFactory.getContactSerializer(), Contact::new);

        final ListView bundleList = findViewById(R.id.bundle_list);

        storage.retrieve(UUID.fromString("74832b01-7643-4d62-965b-b94a8d7cbda9"), new Listener<Contact>() {
            @Override
            public void doCatch(Exception data) {

            }

            @Override
            public void doTry(Contact data) throws Exception {
                ArrayList<String> listItems = new ArrayList<>();
                if (data == null || data.getId() == null) {
                    listItems.add("NULL");
                }
                else {
                    listItems.add(data.getId() + " --- " + data.getLastModified().toString());
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(BundlesActivity.this,
                        android.R.layout.simple_list_item_1, listItems);

                runOnUiThread(() -> bundleList.setAdapter(adapter));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundles);
    }
}
