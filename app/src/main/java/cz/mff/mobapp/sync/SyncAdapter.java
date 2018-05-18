package cz.mff.mobapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import static android.provider.ContactsContract.RawContacts;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static String[] existingContactsProjection = {
            RawContacts.SOURCE_ID,
            RawContacts._ID,
            RawContacts.DIRTY,
            RawContacts.DELETED,
            Contracts.LAST_MODIFIED,
    };

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        final ContentResolver contentResolver = getContext().getContentResolver();

        Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon()
                .appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name)
                .appendQueryParameter(RawContacts.ACCOUNT_TYPE, account.type)
                .build();

        Cursor cursor = contentResolver.query(rawContactUri, existingContactsProjection, null, null, null);

        /*
            TODO: read cursor and look for:
                - known, updated contacts (source id filled, dirty = 1)
                - new contacts (source id not filled)
                - deleted contacts (deleted = 1)

            TODO: fetch updated bundles from server
               - find contacts added on server (no such field with source id == uuid)
               - find contacts updated on server (last modified differs)

            TODO: resolve conflicts :)

            Documentation for columns + examples on:
                https://developer.android.com/reference/android/provider/ContactsContract.RawContacts

          */

    }
}
