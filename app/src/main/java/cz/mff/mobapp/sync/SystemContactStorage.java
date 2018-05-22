package cz.mff.mobapp.sync;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Entity;
import android.provider.ContactsContract.Data;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Storage;

public class SystemContactStorage implements Storage<SystemContactStorage.ContactEntry, UUID> {

    private final String accountName;
    private final ContentResolver contentResolver;

    private static final String[] RAW_PROJECTION = {
            RawContacts._ID,
            RawContacts.CONTACT_ID,
            RawContacts.SOURCE_ID,
    };

    private static final String[] CONTACTS_PROJECTION = {
            Contacts._ID,
            Contacts.DISPLAY_NAME_PRIMARY
    };

    private static final String[] DATA_PROJECTION =
            {
                    Data._ID,
                    Data.MIMETYPE,
                    Data.DATA1,
                    Data.DATA2,
                    Data.DATA3,
                    Data.DATA4,
                    Data.DATA5,
                    Data.DATA6,
                    Data.DATA7,
                    Data.DATA8,
                    Data.DATA9,
                    Data.DATA10,
                    Data.DATA11,
                    Data.DATA12,
                    Data.DATA13,
                    Data.DATA14,
                    Data.DATA15
            };


    public SystemContactStorage(String accountName, ContentResolver contentResolver) {
        this.accountName = accountName;
        this.contentResolver = contentResolver;
    }

    @Override
    public void listAll(Listener<ArrayList<ContactEntry>> listener) {
        final Cursor rawContacts = contentResolver.query(RawContacts.CONTENT_URI.buildUpon()
                        .appendQueryParameter(RawContacts.ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE)
                        .appendQueryParameter(RawContacts.ACCOUNT_NAME, accountName)
                        .build(),
                RAW_PROJECTION,
                null, null, null);

        HashMap<Long, String> contactIds = new HashMap<>();
        ArrayList<ContactEntry> entries = new ArrayList<>();
        while (rawContacts.moveToNext()) {
            contactIds.put(rawContacts.getLong(1), rawContacts.getString(2));
        }

        rawContacts.close();

        final Cursor contacts = contentResolver.query(Contacts.CONTENT_URI,
                CONTACTS_PROJECTION,
                null, null, Contacts.DISPLAY_NAME_PRIMARY + " ASC");

        while (contacts.moveToNext()) {
            long id = contacts.getLong(0);
            String uuid = contactIds.get(id);
            if (uuid != null) {
                ContactEntry entry = new ContactEntry(id, UUID.fromString(uuid));
                entry.name = contacts.getString(1);
                entries.add(entry);
            }
        }

        contacts.close();

        new TryCatch<>(listener).doTry(entries);
    }

    @Override
    public void retrieve(UUID uuid, Listener<? super ContactEntry> listener) {
        TryCatch<? super ContactEntry> tryCatch = new TryCatch<>(listener);

        Cursor rawContact = contentResolver.query(RawContacts.CONTENT_URI.buildUpon()
                        .appendQueryParameter(RawContacts.ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE)
                        .appendQueryParameter(RawContacts.ACCOUNT_NAME, accountName)
                        .build(),
                RAW_PROJECTION,
                RawContacts.CONTACT_ID + " = ?", new String[]{String.valueOf(uuid)},
                null);

        if (rawContact == null || !rawContact.moveToNext()) {
            tryCatch.doCatch(new FileNotFoundException());
            return;
        }

        long id = rawContact.getLong(0);
        rawContact.close();

        Cursor c = contentResolver.query(
                Uri.withAppendedPath(ContentUris.withAppendedId(RawContacts.CONTENT_URI, id),
                        Entity.CONTENT_DIRECTORY),
                new String[]{RawContacts.SOURCE_ID,
                        Entity.DATA_ID, Entity.MIMETYPE, Entity.DATA1},
                null, null, null);

        if (c == null) {
            tryCatch.doCatch(new FileNotFoundException());
            return;
        }

        ContactEntry entry = new ContactEntry(id, uuid);

        while (c.moveToNext()) {
            if (!c.isNull(1)) {
                String mimeType = c.getString(2);
                // TODO: parse CIs
            }
        }

        c.close();

        tryCatch.doTry(entry);
    }

    public class ContactEntry {
        long id;
        String name;
        UUID uuid;

        ContactEntry(long id, UUID uuid) {
            this.id = id;
            this.uuid = uuid;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
