package cz.mff.mobapp.sync;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
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
import cz.mff.mobapp.model.ContactInfo;
import cz.mff.mobapp.model.Manager;

import static android.provider.ContactsContract.RawContacts;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

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

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "Does not have read permission, cannot ask for it, bailing out!");
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS)
                != PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "Does not have write permission, cannot ask for it, bailing out!");
        }

        try {
            String token = accountManager.blockingGetAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, true);
            performSyncAuthenticated(account, token);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Authentication failure.");
        }
    }

    private void performSyncAuthenticated(Account account, String authToken) {
        final Requester requester = new Requester(new TokenAuthProvider(authToken));
        requester.initializeQueue(getContext());
        Manager<Contact, UUID> contactManager = new Manager<>(
                new APIStorage<>("contacts", requester, Contact.handler), Contact.handler);

        contactManager.listAll(new TryCatch<>(
                data -> processContacts(account, data),
                e -> {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Unable to fetch contact data from server.");
                }
        ));
    }

    private static String[] existingContactsProjection = {
            RawContacts._ID,
            RawContacts.SOURCE_ID,
            Contracts.LAST_MODIFIED,
            RawContacts.DELETED,
    };

    private void processContacts(Account account, ArrayList<Contact> contacts) throws RemoteException, OperationApplicationException {
        final ContentResolver contentResolver = getContext().getContentResolver();

        Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon()
                .appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name)
                .appendQueryParameter(RawContacts.ACCOUNT_TYPE, account.type)
                .build();

        final Cursor cursor = contentResolver.query(rawContactUri, existingContactsProjection, null, null, null);

        if (cursor == null) {
            Log.w(LOG_TAG, "No Cursor was obtained.");
        }

        HashMap<UUID, Integer> cursorMapping = new HashMap<>();

        while (cursor.moveToNext()) {
            String sourceId = cursor.getString(1);
            if (sourceId != null && !sourceId.equals("")) {
                cursorMapping.put(UUID.fromString(sourceId), cursor.getPosition());
            }
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        for (Contact contact : contacts) {
            try {
                if (!cursorMapping.containsKey(contact.getId())) {
                    insertContact(account, ops, contact);
                    continue;
                }

                cursor.moveToPosition(cursorMapping.get(contact.getId()));
                cursorMapping.remove(contact.getId());
                long rowId = cursor.getLong(0);
                String lastModifiedStr = cursor.getString(2);

                if (!contact.getLastModified().toString().equals(lastModifiedStr)) {
                    Log.v(LOG_TAG, contact.getLabel() + " up-to-date...");
                    continue;
                }

                Log.v(LOG_TAG, "Updating " + contact.getLabel());
                updateContact(account, rowId, ops, contact);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int pos : cursorMapping.values()) {
            cursor.moveToPosition(pos);
            long rowId = cursor.getLong(0);
            deleteContact(cursor, cursorMapping, ops, rowId);
        }

        cursor.close();
        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
    }

    private void deleteContact(Cursor cursor, HashMap<UUID, Integer> cursorMapping, ArrayList<ContentProviderOperation> ops, long rowId) {
        String[] rowIdSelection = new String[]{String.valueOf(rowId)};

        Builder contactBuilder = ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
                .withSelection(RawContacts._ID + " = ?", rowIdSelection);
        ops.add(contactBuilder.build());

        Builder deleteBuilder = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " = ?", rowIdSelection);
        ops.add(deleteBuilder.build());
    }

    private void updateContact(Account account, long rowId, ArrayList<ContentProviderOperation> ops, Contact contact) throws Exception {
        String[] rowIdSelection = new String[]{String.valueOf(rowId)};

        Builder contactBuilder = ContentProviderOperation.newUpdate(RawContacts.CONTENT_URI)
                .withSelection(RawContacts._ID + " = ?", rowIdSelection)
                .withValue(RawContacts.ACCOUNT_TYPE, account.type)
                .withValue(RawContacts.ACCOUNT_NAME, account.name);
        contactBuilder = Contact.handler.storeToBuilder(contact, contactBuilder);
        ops.add(contactBuilder.build());

        Builder deleteBuilder = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " = ?", rowIdSelection);
        ops.add(deleteBuilder.build());

        for (ContactInfo ci : contact.getContactInfos()) {
            Builder builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, rowId);
            builder = ci.getHandler().storeToBuilder(ci, builder);
            ops.add(builder.build());
        }
    }

    private void insertContact(Account account, ArrayList<ContentProviderOperation> ops, Contact contact) throws Exception {
        Log.v(LOG_TAG, "Inserting " + contact.getLabel());
        int backReferenceIndex = ops.size();
        Builder contactBuilder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, account.type)
                .withValue(RawContacts.ACCOUNT_NAME, account.name);
        contactBuilder = Contact.handler.storeToBuilder(contact, contactBuilder);
        ops.add(contactBuilder.build());

        for (ContactInfo ci : contact.getContactInfos()) {
            Builder builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backReferenceIndex);
            builder = ci.getHandler().storeToBuilder(ci, builder);
            ops.add(builder.build());
        }
    }


}
