package cz.mff.mobapp;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import cz.mff.mobapp.api.APIEndpoints;
import cz.mff.mobapp.event.ExceptionListener;
import cz.mff.mobapp.event.TryCatch;
import cz.mff.mobapp.gui.ServiceLocator;
import cz.mff.mobapp.model.Contact;
import cz.mff.mobapp.model.Manager;

public class MainActivity extends Activity implements ExceptionListener, AuthenticatedActivity {

    public static final String UPDATE_DONE = "cz.mff.mobapp.UPDATE_DONE";

    private static final String TAG = "MainActivity";

    private ServiceLocator serviceLocator;
    private Manager<Contact, UUID> manager;
    private final UUID testBundleId = UUID.fromString("41795d9e-3cc9-4771-b88a-b0099516a753");

    private void sendRequest() {
        serviceLocator.getRequester().getRequest(APIEndpoints.BUNDLES_ENDPOINT, new TryCatch<>(
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

        setContentView(R.layout.activity_main);

        findViewById(R.id.requestButton).setOnClickListener(view -> sendRequest());
        findViewById(R.id.retrieveButton).setOnClickListener(view -> retrieveBundle());
        findViewById(R.id.updateButton).setOnClickListener(view -> updateBundle());
        findViewById(R.id.bundleButton).setOnClickListener(view -> showBundlesActivity());
        findViewById(R.id.createDeleteButton).setOnClickListener(view -> createDeleteBundle());
        findViewById(R.id.shareTicketButton).setOnClickListener(view -> shareTicket("33319b2f-f891-40b0-a23f-bcdaa9b71857"));

        ServiceLocator.create(this);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onAuthenticated() {
        manager = serviceLocator.createContactManager();

        boolean handled = tryHandleIntent(getIntent());
        if (!handled) {
            askUserForTicketId();
        }
    }

    @Override
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
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
        serviceLocator.getRequester().putRequest(String.format(APIEndpoints.CLONE_ENDPOINT, ticketId), new JSONObject(),
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
