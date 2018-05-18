package cz.mff.mobapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import cz.mff.mobapp.gui.ServiceLocator;

public class ContactsActivity extends Activity {

    private ServiceLocator serviceLocator;

    class ContactEntry {
        final long id;
        final String name;

        ContactEntry (long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            };
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 7;

    // FIXME: This field is user specific - have globally accessible?
    public static final String accountName = "???";
    // FIXME: this field is application specific
    public static final String MOBAPP_ACCOUNT_TYPE = "com.google";

    private ListView contactList;
    private SimpleCursorAdapter cursorAdapter;

    private HashSet<Long> contactIds;
    private ArrayList<ContactEntry> entries;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactList = findViewById(R.id.contact_list);
        contactList.setOnItemClickListener((adapterView, view, position, row) -> {
            long contactId = entries.get(position).id;
            showContactDetail(contactId);
        });

        cursorAdapter = new SimpleCursorAdapter(
                this, android.R.layout.simple_list_item_1,
                null, new String[]{ContactsContract.Contacts.DISPLAY_NAME_PRIMARY},
                new int[] {android.R.id.text1}, 0);
        contactList.setAdapter(cursorAdapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_READ_CONTACTS);
        }
        else {
            loadContacts();
        }
    }

    public void loadContacts() {
        final Cursor rawContacts = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI.buildUpon()
                        .appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_TYPE, MOBAPP_ACCOUNT_TYPE)
                        .appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_NAME, accountName)
                        .build(),
                new String[] {ContactsContract.RawContacts.CONTACT_ID},
                null, null, null);

        contactIds = new HashSet<>();
        entries = new ArrayList<>();
        while (rawContacts.moveToNext()) {
            contactIds.add(rawContacts.getLong(0));
        }

        final Cursor contacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                PROJECTION, null, null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC");

        while (contacts.moveToNext()) {
            long id = contacts.getLong(0);
            if (!contactIds.contains(id)) {
                Log.w("f", "Filtering contact " + contacts.getString(1));
                continue;
            }
            ContactEntry entry = new ContactEntry(id, contacts.getString(1));
            entries.add(entry);
        }

        contactList.setAdapter(new ArrayAdapter<ContactEntry>(this,
                android.R.layout.simple_list_item_1, entries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(ContactsActivity.this)
                            .inflate(R.layout.contact_list_item, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.contact_list_text))
                        .setText(entries.get(position).name);
                return convertView;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts();
                }
                else {
                    TextView view = new TextView(this);
                    view.setText("Cannot display your contacts as you didn't give us the permission");
                    contactList.setEmptyView(view);
                }
        }
    }

    private void showContactDetail(long contactId) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra("id", contactId);
        startActivity(intent);
    }
}
