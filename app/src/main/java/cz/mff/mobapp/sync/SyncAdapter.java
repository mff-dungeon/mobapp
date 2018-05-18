package cz.mff.mobapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import cz.mff.mobapp.api.APIStorage;
import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.api.TokenAuthProvider;
import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.event.Listener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.EntityHandler;
import cz.mff.mobapp.model.Manager;

import static android.provider.ContactsContract.RawContacts;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static String[] existingContactsProjection = {
            RawContacts.SOURCE_ID,
            RawContacts._ID,
            RawContacts.DIRTY,
            RawContacts.DELETED,
            Contracts.LAST_MODIFIED,
    };

    private static final String LOG_TAG = "SyncAdapter";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        AccountManager accountManager = AccountManager.get(getContext());

        accountManager.getAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, null, false, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                Bundle resultBundle;
                try {
                    resultBundle = accountManagerFuture.getResult();
                }
                catch (AuthenticatorException | IOException | OperationCanceledException e) {
                    Log.e(LOG_TAG, "Authentication failure.");
                    return;
                }

                final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (null != intent) {
                    return;
                }
                String authToken = resultBundle.getString(AccountManager.KEY_AUTHTOKEN);
                performSyncAuthenticated(account, authToken, bundle, s, contentProviderClient, syncResult);
            }
        }, null);
    }

    private void performSyncAuthenticated(Account account, String authToken, Bundle bundle, String s,
                                          ContentProviderClient contentProviderClient,
                                          SyncResult syncResult) {

        final Requester requester = new Requester(new TokenAuthProvider(authToken));
        Manager<Contact, UUID> contactManager = new Manager<>(
                new APIStorage<>("contacts", requester, Contact.handler), Contact.handler);

        contactManager.listAll(new Listener<ArrayList<Contact>>() {
            @Override
            public void doCatch(Exception data) {
                Log.e(LOG_TAG, "Unable to fetch contact data from server.");
            }

            @Override
            public void doTry(ArrayList<Contact> data) throws Exception {
                processContacts(account, data);
            }
        });
    }

    private void processContacts(Account account, ArrayList<Contact> contacts) {
        final ContentResolver contentResolver = getContext().getContentResolver();

        Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon()
                .appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name)
                .appendQueryParameter(RawContacts.ACCOUNT_TYPE, account.type)
                .build();

        final Cursor cursor = contentResolver.query(rawContactUri, existingContactsProjection, null, null, null);

        if (cursor == null) {
            Log.w(LOG_TAG, "No Cursor was obtained.");
        }

        HashMap<String, Integer> cursorMapping = new HashMap<>();

        while(cursor.moveToNext()) {
            String sourceId = cursor.getString(0);
            if (sourceId != null && !sourceId.equals("")) {
                cursorMapping.put(sourceId, cursor.getPosition());
            }
        }

        for(Contact contact : contacts) {
            String uuid = contact.getId().toString();
            if(cursorMapping.containsKey(uuid)) {
                cursor.moveToPosition(cursorMapping.get(uuid));
                String lastModifiedStr = cursor.getString(4);
                // FIXME: create date properly
                Date lastModified = Date.valueOf(lastModifiedStr);
                if (contact.getLastModified().after(lastModified)) {
                    // TODO: contentResolver.update() on all required RawContact fields/Entities
                }
            }
            else {
                // TODO: contentResolver.insert() on all required RawContact fields/Entities
            }
        }

        cursor.close();
    }
}
