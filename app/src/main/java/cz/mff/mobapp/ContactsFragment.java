package cz.mff.mobapp;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.gui.ServiceLocator;

public class ContactsFragment extends Fragment {


    private class ContactEntry {
        final long id;
        final String name;
        final UUID uuid;

        ContactEntry(long id, String name, UUID uuid) {
            this.id = id;
            this.name = name;
            this.uuid = uuid;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private ServiceLocator serviceLocator = null;
    private ContentResolver contentResolver = null;
    private Context context;

    private ViewGroup rootView;

    private ListView contactList;

    private ArrayList<ContactEntry> entries;

    void initialize(Context context, ServiceLocator serviceLocator, ContentResolver contentResolver) {
        this.context = context;
        this.serviceLocator = serviceLocator;
        this.contentResolver = contentResolver;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_contacts, container, false);

        contactList = rootView.findViewById(R.id.fragment_contact_list);
        contactList.setEmptyView(rootView.findViewById(R.id.fragment_contact_list_empty));

        loadContacts();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void startContactActivity(ContactEntry entry, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra("id", entry.id);
        intent.putExtra("uuid", entry.uuid);
        startActivity(intent);
    }

    public static final String[] RAW_PROJECTION = {
            ContactsContract.RawContacts.CONTACT_ID,
            ContactsContract.RawContacts.SOURCE_ID,
    };

    public static final String[] CONTACTS_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };

    private void loadContacts() {
        final String accountName = serviceLocator.getAccountSession().getAccountName();
        final Cursor rawContacts = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI.buildUpon()
                        .appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE)
                        .appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_NAME, accountName)
                        .build(),
                RAW_PROJECTION,
                null, null, null);

        HashMap<Long, String> contactIds = new HashMap<>();
        entries = new ArrayList<>();
        while (rawContacts.moveToNext()) {
            contactIds.put(rawContacts.getLong(0), rawContacts.getString(1));
        }

        rawContacts.close();

        final Cursor contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                CONTACTS_PROJECTION,
                null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC");

        while (contacts.moveToNext()) {
            long id = contacts.getLong(0);
            String uuid = contactIds.get(id);
            if (uuid == null) {
                Log.v("ContactsFragment", "Filtering contact " + contacts.getString(1));
                continue;
            }
            ContactEntry entry = new ContactEntry(id, contacts.getString(1), UUID.fromString(uuid));
            entries.add(entry);
        }

        contacts.close();

        if (entries.isEmpty()) return;

        final ArrayAdapter<ContactEntry> adapter = new ArrayAdapter<ContactEntry>(context, R.layout.list_item_share_edit, R.id.item_text, entries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ContactEntry entry = entries.get(position);

                view.findViewById(R.id.item_text)
                        .setOnClickListener(l -> startContactActivity(entry, ContactDetailActivity.class));
                view.findViewById(R.id.btn_edit)
                        .setOnClickListener(l -> startContactActivity(entry, ContactEditActivity.class));
                view.findViewById(R.id.btn_share)
                        .setOnClickListener(l -> startContactActivity(entry, ShareBundleActivity.class));

                return view;
            }
        };

        contactList.setAdapter(adapter);
    }

}
