package cz.mff.mobapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Entity;
import android.util.Log;
import android.widget.TextView;
import android.provider.ContactsContract.Data;

import cz.mff.mobapp.model.Contact;

public class ContactDetailActivity extends Activity {

    private Contact contact;
    private Loader<Cursor> cursorLoader;
    private Cursor contactCursor;

    private long contactId;

    private static final String[] PROJECTION =
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        contactId = getIntent().getLongExtra("id", -1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Cursor rawContact = getContentResolver().query(RawContacts.CONTENT_URI.buildUpon()
                        .appendQueryParameter(RawContacts.ACCOUNT_TYPE, ContactsActivity.MOBAPP_ACCOUNT_TYPE)
                        .appendQueryParameter(RawContacts.ACCOUNT_NAME, ContactsActivity.accountName)
                        .build(),
                new String[] {RawContacts._ID, RawContacts.CONTACT_ID, RawContacts.SOURCE_ID},
                RawContacts.CONTACT_ID + " = ?", new String[] { String.valueOf(contactId)},
                null);

        if(!rawContact.moveToNext()) {
            Log.w("N", "Notihng found");
        }
        long id = rawContact.getLong(0);

        Cursor c = getContentResolver().query(
                Uri.withAppendedPath(ContentUris.withAppendedId(RawContacts.CONTENT_URI, id),
                        Entity.CONTENT_DIRECTORY),
                new String[]{ContactsContract.RawContacts.SOURCE_ID,
                        Entity.DATA_ID, Entity.MIMETYPE, Entity.DATA1},
                null, null, null);

        showContact(contactId, c);
    }

    private void showContact(long contactId, Cursor cursor) {
        ((TextView) findViewById(R.id.contact_detail_id_value)).setText(String.valueOf(contactId));
        String info = "";
        while (cursor.moveToNext()) {
            if (!cursor.isNull(1)) {
                String mimeType = cursor.getString(2);
                String data = cursor.getString(3);
                info += mimeType + ", " + data;
            }
            info += "\n";
        }

        ((TextView) findViewById(R.id.contact_detail_label)).setText(info);
    }
}
