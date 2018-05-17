package cz.mff.mobapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import cz.mff.mobapp.api.APIStorage;
import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.SerializerFactory;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Manager;
import cz.mff.mobapp.model.Storage;

public class ContactsActivity extends Activity {

    private ListView contactList;

    @Override
    protected void onStart() {
        super.onStart();

        /*AppDatabase db = Room.databaseBuilder(getApplicationContext(),
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

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(ContactsActivity.this,
                        android.R.layout.simple_list_item_1, listItems);

                runOnUiThread(() -> bundleList.setAdapter(adapter));
            }
        });
        */

        Requester requester = new Requester("test", "test");
        requester.initializeQueue(this);
        Storage<Contact, UUID> storage = new APIStorage<>("contacts", requester, SerializerFactory.getContactSerializer(), Contact::new, Contact::copy);
        Manager<Contact, UUID> manager = new Manager<>(storage, Contact::copy);

        loadContactData(manager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactList = findViewById(R.id.contact_list);
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact contact = (Contact) adapterView.getItemAtPosition(i);
                UUID id = contact.getId();
                showContactDetail(id);
            }
        });
    }

    private void showContactDetail(UUID id) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra("uuid", id);
        startActivity(intent);
    }

    private void loadContactData(Manager<Contact, UUID> manager) {
        manager.listAll(new TryCatch<>(this::showContactData, e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()));
    }

    // Should be run on UI Thread
    private void showContactData(ArrayList<Contact> contacts) {
        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(this,
                android.R.layout.simple_list_item_1, contacts) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                UUID id = getItem(position).getId();
                if (id != null) {
                    view.setText(id.toString());
                }
                else {
                    view.setText("NULL");
                }
                return view;
            }
        };

        contactList.setAdapter(adapter);
    }
}
