package cz.mff.mobapp.sync;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Storage;

public class SystemContactStorage implements Storage<SystemContactStorage.ContactEntry, UUID> {

    private final String accountName;
    private final ContentResolver contentResolver;

    private static final String[] RAW_PROJECTION = {
            ContactsContract.RawContacts.CONTACT_ID,
            ContactsContract.RawContacts.SOURCE_ID,
    };

    private static final String[] CONTACTS_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };

    public SystemContactStorage(String accountName, ContentResolver contentResolver) {
        this.accountName = accountName;
        this.contentResolver = contentResolver;
    }

    @Override
    public void listAll(Listener<ArrayList<ContactEntry>> listener) {
        final Cursor rawContacts = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI.buildUpon()
                        .appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE)
                        .appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_NAME, accountName)
                        .build(),
                RAW_PROJECTION,
                null, null, null);

        HashMap<Long, String> contactIds = new HashMap<>();
        ArrayList<ContactEntry> entries = new ArrayList<>();
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
            if (uuid != null) {
                ContactEntry entry = new ContactEntry(id, contacts.getString(1), UUID.fromString(uuid));
                entries.add(entry);
            }
        }

        contacts.close();
    }

    public class ContactEntry {
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

}
