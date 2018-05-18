package cz.mff.mobapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.OperationCanceledException;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import cz.mff.mobapp.api.Requester;
import cz.mff.mobapp.auth.AccountUtils;
import cz.mff.mobapp.auth.AuthPreferences;
import cz.mff.mobapp.event.ExceptionListener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceFactory;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Manager;

public class MainActivity extends Activity implements ExceptionListener {

    public static final String UPDATE_DONE = "cz.mff.mobapp.UPDATE_DONE";

    private static final String TAG = "MainActivity";
    private static final int REQ_SIGNUP = 1;

    private Requester requester;
    private Manager<Contact, UUID> manager;
    private final UUID testBundleId = UUID.fromString("41795d9e-3cc9-4771-b88a-b0099516a753");

    private AccountManager mAccountManager;
    private AuthPreferences mAuthPreferences;
    private String authToken;

    private void sendRequest() {
        requester.getRequest("bundles/", new TryCatch<>(
                response -> {
                    JSONArray data = response.getArrayData();
                    ((TextView) findViewById(R.id.responseText)).setText(data.toString());
                }, this));
    }

    private void showBundlesActivity() {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authToken = null;
        mAuthPreferences = new AuthPreferences(this);
        mAccountManager = AccountManager.get(this);

        requester = new Requester("test", "test");
        requester.initializeQueue(this);

        ServiceFactory sf = new ServiceFactory(this);
        manager = sf.createContactManager();

        setContentView(R.layout.activity_main);

        findViewById(R.id.requestButton).setOnClickListener(view -> sendRequest());
        findViewById(R.id.retrieveButton).setOnClickListener(view -> retrieveBundle());
        findViewById(R.id.updateButton).setOnClickListener(view -> updateBundle());
        findViewById(R.id.bundleButton).setOnClickListener(view -> showBundlesActivity());
        findViewById(R.id.createDeleteButton).setOnClickListener(view -> createDeleteBundle());
        findViewById(R.id.shareTicketButton).setOnClickListener(view -> shareTicket("33319b2f-f891-40b0-a23f-bcdaa9b71857"));

        boolean handled = tryHandleIntent(getIntent());
        if (!handled) {
            askUserForTicketId();
        }

        mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE,
                null, this, null, null, new GetAuthTokenCallback(), null);
    }

    private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;

            try {
                bundle = result.getResult();

                final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (null != intent) {
                    startActivityForResult(intent, REQ_SIGNUP);
                } else {
                    authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                    // Save session username & auth token
                    mAuthPreferences.setAuthToken(authToken);
                    mAuthPreferences.setUsername(accountName);

                    System.out.println("[auth] Retrieved auth token: " + authToken);
                    System.out.println("[auth] Saved account name: " + mAuthPreferences.getAccountName());
                    System.out.println("[auth] Saved auth token: " + mAuthPreferences.getAuthToken());

                    // If the logged account didn't exist, we need to create it on the device
                    Account account = AccountUtils.getAccount(MainActivity.this, accountName);
                    if (null == account) {
                        account = new Account(accountName, AccountUtils.ACCOUNT_TYPE);
                        mAccountManager.addAccountExplicitly(account, bundle.getString(LoginActivity.PARAM_USER_PASSWORD), null);
                        mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
                    }
                }
            } catch (OperationCanceledException e) {
                // If signup was cancelled, force activity termination
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateBundle() {
        manager.retrieve(testBundleId, new TryCatch<>(bundle -> {
            manager.save(bundle, new TryCatch<>(bundle1 -> {
                ((TextView) findViewById(R.id.responseText)).setText("Updated.");
            }, this));
        }, this));
    }

    private void askUserForTicketId() {
        // TODO: show some kind of UI to retrieve ticket ID, then call subscribeToTicket()
    }

    private void subscribeToTicket(String ticketId) {
        System.out.printf("cloning ticket %s\n", ticketId);
        requester.putRequest(String.format("clone/%s/", ticketId), new JSONObject(),
                new TryCatch<>(response -> {
                    JSONObject data = response.getObjectData();
                    System.out.printf("ticket clone succeeded, clone has id: %s\n", data.get("id"));

                    // TODO: sync data store from the backend
                }, err -> {
                    System.out.println("ticket clone failed");

                    // TODO: show UI to indicate failure
                }));
    }

    private boolean tryHandleIntent(Intent intent) {
        if (intent == null) {
            // no intent provided
            return false;
        }

        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();

        if (appLinkData == null || !Intent.ACTION_VIEW.equals(appLinkAction)) {
            // not app link intent
            return false;
        }

        String ticketId = appLinkData.getLastPathSegment();
        subscribeToTicket(ticketId);
        return true;
    }

    private void createDeleteBundle() {
        Contact c = new Contact();
        c.setContact(true);

        manager.save(c, new TryCatch<>(
                contact -> {
                    ((TextView) findViewById(R.id.responseText)).setText("Created ID " + contact.getId().toString());
                    manager.delete(contact.getId(), new TryCatch<>(foo -> {
                        ((TextView) findViewById(R.id.responseText)).setText("Aaand gone. ID " + contact.getId().toString());
                    }, this));
                }, this
        ));
    }

    private void retrieveBundle() {
        manager.retrieve(testBundleId, new TryCatch<>(bundle -> {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(bundle.getLastModified());
            ((TextView) findViewById(R.id.responseText)).setText("Last modified at " + currentDateTimeString);
        }, this));
    }

    @Override
    public void doCatch(Exception e) {
        e.printStackTrace();
        Log.e(TAG, e.getMessage());
        ((TextView) findViewById(R.id.errorText)).setText(e.getMessage());
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        boolean handled = tryHandleIntent(intent);
        if (!handled) {
            askUserForTicketId();
        }
    }

    private void shareTicket(String ticketId) {
        Intent intent = new Intent(this, ShareTicketActivity.class);
        intent.putExtra(ShareTicketActivity.TICKET_ID, ticketId);
        startActivity(intent);
    }

}
