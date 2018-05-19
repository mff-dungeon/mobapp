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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.gui.ServiceLocator;

public class ContactsFragment extends Fragment {

    private class ContactEntry {
        final long id;
        final String name;

        ContactEntry (long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private ServiceLocator serviceLocator = null;
    private ContentResolver contentResolver = null;
    private Context context;

    private ViewGroup rootView;

    private ListView contactList;
    private ListAdapter adapter;

    private HashSet<Long> contactIds;
    private ArrayList<ContactEntry> entries;

    void initialize(Context context, ServiceLocator serviceLocator, ContentResolver contentResolver) {
        this.context = context;
        this.serviceLocator = serviceLocator;
        this.contentResolver = contentResolver;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_contacts, container, false);


        contactList = rootView.findViewById(R.id.fragment_contact_list);
        contactList.setOnItemClickListener((adapterView, view, position, row) -> {
            long contactId = entries.get(position).id;
            showContactDetail(contactId);
        });

        contactList.setEmptyView(rootView.findViewById(R.id.fragment_contact_list_empty));

        loadContacts();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void showContactDetail(long contactId) {
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra("id", contactId);
        startActivity(intent);
    }

    private void loadContacts() {
        final String accountName = serviceLocator.getAccountSession().getAccountName();
        final Cursor rawContacts = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI.buildUpon()
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

        final Cursor contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY},
                null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC");

        while (contacts.moveToNext()) {
            long id = contacts.getLong(0);
            if (!contactIds.contains(id)) {
                Log.v("ContactsFragment", "Filtering contact " + contacts.getString(1));
                continue;
            }
            ContactEntry entry = new ContactEntry(id, contacts.getString(1));
            entries.add(entry);
        }

        if (entries.isEmpty()) return;

        adapter = new ArrayAdapter<ContactEntry>(context,
                android.R.layout.simple_list_item_1, entries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context)
                            .inflate(R.layout.contact_list_item, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.contact_list_text))
                        .setText(entries.get(position).name);
                return convertView;
            }
        };

        contactList.setAdapter(adapter);
    }

}
