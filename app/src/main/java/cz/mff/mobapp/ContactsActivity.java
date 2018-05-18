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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.gui.ServiceLocator;

public class ContactsActivity extends Activity implements AuthenticatedActivity {

    private ServiceLocator serviceLocator;

    class ContactEntry {
        final long id;
        final String name;

        ContactEntry (long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 7;

    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            };

    private boolean authenticated = false;

    private ListView contactList;
    private ListAdapter adapter;

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

        contactList.setEmptyView(findViewById(R.id.contact_list_empty));

        ServiceLocator.create(this);
    }

    private void loadContacts() {
        final String accountName = serviceLocator.getAccountSession().getAccountName();
        final Cursor rawContacts = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI.buildUpon()
                        .appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE)
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

        if (entries.size() == 0) {
            contactList.setEmptyView(findViewById(R.id.contact_list_empty));
            return;
        }

        adapter = new ArrayAdapter<ContactEntry>(this,
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
        };

        contactList.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (authenticated) loadContacts();
                }
                else {
                    contactList.setEmptyView(findViewById(R.id.contact_list_not_accessible));
                }
        }
    }

    private void showContactDetail(long contactId) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra("id", contactId);
        startActivity(intent);
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    @Override
    public void onAuthenticated() {
        authenticated = true;

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

    @Override
    public Activity getActivity() {
        return this;
    }
}
